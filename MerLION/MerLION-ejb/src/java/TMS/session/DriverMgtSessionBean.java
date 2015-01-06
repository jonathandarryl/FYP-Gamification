/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Company;
import TMS.entity.Drivers;
import TMS.entity.TimeSlot;
import TMS.entity.TransOrder;
import TMS.entity.Trucks;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.DriverNotExistException;
import util.exception.StartNotBeforeEndException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNoTimetableException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class DriverMgtSessionBean implements DriverMgtSessionBeanLocal {
    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;
    
    @Override
    public ArrayList<String> viewTimeTable(Long ownerId, Long driverId) throws CompanyNotExistException, DriverNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Drivers driver = em.find(Drivers.class, driverId); 
            if (driver == null || !Objects.equals(driver.getTrucks().getOwnerCompany().getId(), ownerId)) {
              throw new DriverNotExistException("Driver " + driverId + " not exist!");
            } else {
                ArrayList<String> sList = new ArrayList<>();
                if(driver.getTimetable() == null){
                    sList.add("This driver has not schedule a timetable yet");
                    return sList;
                }else{
                    ArrayList<Timestamp> tList = driver.getTimetable();
                    for(int i=0; i<tList.size(); ){
                        sList.add("From: " + tList.get(i).toString() + " To: " + tList.get(i+1).toString());
                        i+=2;
                    }
                    return sList;
                }            
            }
        }
    }
    
    @Override
    public ArrayList<Drivers> viewAllDriversInfo(Long ownerId) throws CompanyNotExistException, DriverNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            //System.out.println("get all drivers session" + ownerId);
            Query query=em.createQuery("select d from Drivers d WHERE d.ownerCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            ArrayList<Drivers> driversList = new ArrayList<>(query.getResultList());
            if(driversList==null) {
                throw new DriverNotExistException("Driver not exist!");
            }else{
                return driversList;
            }
        }
    }
    
    @Override
    public ArrayList<Drivers> viewAllDrivers(Long ownerId) throws CompanyNotExistException, DriverNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            //System.out.println("get all drivers session" + ownerId);
            Query query=em.createQuery("select d from Drivers d WHERE d.ownerCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            ArrayList<Drivers> driversList = new ArrayList<>(query.getResultList());
            if(driversList==null) {
                throw new DriverNotExistException("Driver not exist!");
            }else{
                ArrayList<Drivers> result = new ArrayList<>();
                for(Drivers d: driversList){
                    if(d.getTrucks()==null){
                        result.add(d);
                    }
                }
                return result;
            }
        }
    }
    
    @Override
    public String arrangeTimetable(Long ownerId, Long orderId, Long driverId) throws TransOrderNotExistException, CompanyNotExistException, DriverNotExistException, StartNotBeforeEndException, VehicleNoTimetableException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder order = em.find(TransOrder.class, orderId);
            if (order == null){
              throw new TransOrderNotExistException("transorder " + orderId + " not exist!");
            }else{
                Drivers driver = em.find(Drivers.class, driverId);
                driver.setTransOrderId(orderId);
                if (driver == null) {
                    throw new DriverNotExistException("Driver " + driverId + " not exist!");
                } else {
                    if(driver.getTrucks().getTimetable().isEmpty()){
                        throw new VehicleNoTimetableException("vehicle no timetable");
                    }else{
                        ArrayList<Timestamp> timetable = driver.getTrucks().getTimetable();
                        Timestamp start = timetable.get(timetable.size()-2);
                        Timestamp end = timetable.get(timetable.size()-1);
//                        System.out.println(start);
//                        System.out.println(end);
                        if(driver.getTimetable()==null || checkTime(driver.getTimetable(), start, end)){
                            if(driver.addTimeSlot(start, end)) {
                                return "\nNew time slot added successfully!";
                            }else {
                                throw new StartNotBeforeEndException("Company " + ownerId + " not exist!");
                            }
                        }else{
                            return "time conflict";
                        }
                    }
                }
            }
        }
    }
    
    private Boolean checkTime(ArrayList<Timestamp> timetable, Timestamp start, Timestamp end){
            Boolean check = false;
            int size = timetable.size();
            if(end.before(timetable.get(0)) || start.after(timetable.get(size-1))){
                return true;
            }
            for (int i=1; i<=(size-1)/2; ){
                if(start.after(timetable.get(i)) && end.before(timetable.get(i+1))){
                    check = true;
                    break;
                }
                i+=2;
            }
            return check;
    }

    
    @Override
    public Drivers viewSelectedDriver(Long orderId, Long truckId) throws TransOrderNotExistException{
        TransOrder order = em.find(TransOrder.class, orderId);
            if (order == null){
                throw new TransOrderNotExistException("trans order " + orderId + " not exist!");
            }else{
                Trucks t = em.find(Trucks.class, truckId);
                List<Drivers> dList = t.getDrivers();
                for(Drivers d: dList){
                    if(orderId.equals(d.getTransOrderId())){
                        return d;
                    }
                }
                return null;
            }
   }
    
    @Override
    public String addNewDriver(Long ownerId, String name, String gender, Integer age, String licenseNo, Integer drivingAge) throws CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Drivers driver = new Drivers();
            driver.setName(name);
            driver.setAge(age);
            driver.setDrivingAge(drivingAge);
            driver.setLicenseNo(licenseNo);
            driver.setGender(gender);
            driver.setOwnerCompany(pc);
            
            em.persist(driver);
            return "\nNew driver added successfully!";
        }
    }
    
    @Override
    public String deleteDriver(Long ownerId, Long driverId) throws CompanyNotExistException, DriverNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Drivers driver = em.find(Drivers.class, driverId);
            if (driver == null) {
              throw new DriverNotExistException("Driver " + driverId + " not exist!");
            } else {
                em.remove(driver);
                return "\nDriver with ID: " + driverId +" deleted successfully!";
            }
        }
    }
    
    
    @Override
    public String updateDriver(Long ownerId, Long driverId, String name, String gender, Integer age, String licenseNo, Integer drivingAge) throws CompanyNotExistException, DriverNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Drivers driver = em.find(Drivers.class, driverId);
            if (driver == null) {
              throw new DriverNotExistException("Driver " + driverId + " not exist!");
            } else {
                driver.setName(name);
                driver.setAge(age);
                driver.setDrivingAge(drivingAge);
                driver.setLicenseNo(licenseNo);
                driver.setGender(gender);
                em.persist(driver);
                return "\nNew driver added successfully!";
            }
        }
    }
    
    @Override
    public Drivers retrieveDrivers(Long driverId){
        Query q = em.createQuery("SELECT d FROM Drivers d WHERE d.driverId=?1");
        q.setParameter(1, driverId);
        return (Drivers) q.getSingleResult();
    }

}
