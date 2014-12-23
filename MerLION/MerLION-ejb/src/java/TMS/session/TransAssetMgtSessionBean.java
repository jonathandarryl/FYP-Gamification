/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Company;
import TMS.entity.Drivers;
import TMS.entity.Planes;
import TMS.entity.Trucks;
import TMS.entity.Vehicles;
import TMS.entity.Vessel;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.VehicleNotExistException;


/**
 *
 * @author HanXiangyu
 */
@Stateless
public class TransAssetMgtSessionBean implements TransAssetMgtSessionBeanLocal {
    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;
    private String ownerId;
    
    @Override
    public String createTrucks(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue,  Boolean avail, Drivers driver) 
            throws CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Vehicles trucks = new Trucks("truck", useYear, capacity, maxDistance, transCost, perishable, flammable, pharmaceutical, highvalue, avail);
            trucks.setVelocity(velocity);
            trucks.setOwnerCompany(pc);            
            if(pc.getVehicleList().isEmpty()){
                List<Vehicles> vList = new ArrayList<>();
                vList.add(trucks);
                pc.setVehicleList(vList);
            }else{
                pc.getVehicleList().add(trucks);
            }
            em.persist(pc);
            em.persist(trucks);
            return "New truck added sucessfully!";
        }
    }
    
    @Override
    public String createPlanes(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail) throws CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Vehicles planes = new Planes("plane", useYear, capacity, maxDistance, transCost, perishable, flammable, pharmaceutical, highvalue, avail);
            planes.setVelocity(velocity);
            planes.setOwnerCompany(pc);
            if(pc.getVehicleList().isEmpty()){
                List<Vehicles> vList = new ArrayList<>();
                vList.add(planes);
                pc.setVehicleList(vList);
            }else{
                pc.getVehicleList().add(planes);
            }
            em.persist(pc);
            em.persist(planes);
            return "New Plane added successfully!";
        }
    }
    
    @Override
    public String createVessel(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue,  Boolean avail)throws CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Vehicles vessel = new Vessel("vessel", useYear, capacity, maxDistance, transCost, perishable, flammable, pharmaceutical, highvalue, avail);
            vessel.setVelocity(velocity);
            vessel.setOwnerCompany(pc);
            if(pc.getVehicleList().isEmpty()){
                List<Vehicles> vList = new ArrayList<>();
                vList.add(vessel);
                pc.setVehicleList(vList);
            }else{
                pc.getVehicleList().add(vessel);
            }
            em.persist(vessel);
            em.persist(pc);
            return "New Vessel added successfully!";
        }
    }
    
    @Override
    public List<Vehicles> viewAllTrucks(Long companyId)throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, companyId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + companyId + " not exist!");
        }else{
            Query query=em.createQuery("SELECT t FROM Trucks t where t.ownerCompany.id=?1");
            query.setParameter(1, companyId);
            @SuppressWarnings("unchecked")
            List<Vehicles>trucksList = new ArrayList<>(query.getResultList());
            if(trucksList == null){
              throw new VehicleNotExistException("Trucks not exist!");
            }else{
                List<Vehicles> result = new ArrayList<>();
                for(Vehicles v: trucksList){
                    if(v.getTransOrderId()==null && v.getRoute()==null){
                        result.add(v);
                    }
                }
                return result;
            }
        }
    }
    
    @Override
    public List<Vehicles> viewAllPlanes(Long ownerId)throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query=em.createQuery("SELECT p FROM Planes p where p.ownerCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<Vehicles>planesList = new ArrayList<>(query.getResultList());
            if(planesList == null){
              throw new VehicleNotExistException("Planes not exist!");
            }else{
                List<Vehicles> result = new ArrayList<>();
                for(Vehicles v: planesList){
                    if(v.getTransOrderId()==null && v.getRoute()==null){
                        result.add(v);
                    }
                }
                return result;
            }
        }
    }
    
    @Override
    public List<Vehicles> viewAllVessel(Long ownerId)throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT v FROM Vessel v where v.ownerCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<Vehicles>cargoList = new ArrayList<>(query.getResultList());
            if(cargoList == null){
              throw new VehicleNotExistException("cargo not exist!");
            }else{
                List<Vehicles> result = new ArrayList<>();
                for(Vehicles v: cargoList){
                    if(v.getTransOrderId()==null && v.getRoute()==null){
                        result.add(v);
                    }
                }
                return result;
            }
        }
    }
    
    @Override
    public List<Vehicles> viewAllVehicles(Long ownerId)throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT v FROM Vehicles v where v.ownerCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<Vehicles> vList = query.getResultList();
            if(vList == null){
              throw new VehicleNotExistException("vehicle not exist!");
            }else{
                return vList;
            }
        }   
    } 
    
    @Override
    public Vehicles viewVehicles(Long vId) throws VehicleNotExistException{
        Query query;
            query = em.createQuery("SELECT v FROM Vehicles v where v.vehiclesId=?1");
            query.setParameter(1, vId);
            @SuppressWarnings("unchecked")
            Vehicles vList = (Vehicles) query.getSingleResult();
            if(vList == null){
              throw new VehicleNotExistException("vehicle not exist!");
            }else{
                return vList;
            }
    }

    @Override
    public String updateTrucks(Long ownerId, Long vehiclesId, Integer useYear, Long capacity, 
                          Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail) throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Vehicles vehicle = em.find(Trucks.class, vehiclesId);
            Trucks truck = (Trucks) vehicle;
            if (truck == null || !truck.getOwnerCompany().getId().equals(ownerId)) {
              throw new VehicleNotExistException("truck not exist!");
            } else {
                System.out.println("in update");
                truck.setUseYear(useYear);
                truck.setAvail(avail);
                truck.setCapacity(capacity);
                truck.setFlammable(flammable);
                truck.setHighValue(highvalue);
                truck.setPerishable(perishable);
                truck.setPharmaceutical(pharmaceutical);
                truck.setMaxDistance(maxDistance);
                truck.setTransCost(transCost);
                truck.setVelocity(velocity);
                //truck.setDrivers(driver);
                em.refresh(truck);
                return "\nTruck information updated successfully!";
            }
        }
    }
    
    @Override
     public String updatePlaneOrVessel(Long ownerId, Long vehiclesId, Integer useYear, Long capacity,
             Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail) throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{ 
            Vehicles plane;    
            plane = em.find(Vehicles.class, vehiclesId);     
            if (plane == null || !plane.getOwnerCompany().getId().equals(ownerId)) {
              throw new VehicleNotExistException("plane not exist!");
            } else {
                System.out.println("in update");
                plane.setUseYear(useYear);
                plane.setAvail(avail);
                plane.setCapacity(capacity);
                plane.setFlammable(flammable);
                plane.setHighValue(highvalue);
                plane.setPerishable(perishable);
                plane.setPharmaceutical(pharmaceutical);
                plane.setMaxDistance(maxDistance);
                plane.setTransCost(transCost);
                plane.setVelocity(velocity);
                em.persist(plane);
                return "\nAsset information updated successfully!";
            }
        }
    }
     
    @Override
    public String deleteTransAsset(Long ownerId, String type, Long vehiclesId) throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            switch (type.trim()){
                case "truck":
                    Vehicles truck = em.find(Trucks.class, vehiclesId);
                    if (truck == null || !truck.getOwnerCompany().getId().equals(ownerId)) {
                         throw new VehicleNotExistException("trucks not exist!");
                } else {
                    for(Vehicles v: pc.getVehicleList()){
                        if(v.getVehiclesId().equals(vehiclesId)){
                            pc.getVehicleList().remove(v);
                            break;
                        }
                    }
                    em.persist(pc);
                    em.remove(truck);
                }
                break;

                case "plane":
                    Vehicles plane = em.find(Planes.class, vehiclesId);
                    if (plane == null || !plane.getOwnerCompany().getId().equals(ownerId)) {
                         throw new VehicleNotExistException("plane not exist!");
            } else {
                        for(Vehicles v: pc.getVehicleList()){
                            if(v.getVehiclesId().equals(vehiclesId)){
                                pc.getVehicleList().remove(v);
                                break;
                            }
                        }
                        em.persist(pc);
                        em.remove(plane);
            }
                    break;

                case "vessel":
                    Vehicles vessel = em.find(Vessel.class, vehiclesId);
                    if (vessel == null || !vessel.getOwnerCompany().getId().equals(ownerId)) {
                          throw new VehicleNotExistException("vessel not exist!");
            } else {
                        for(Vehicles v: pc.getVehicleList()){
                            if(v.getVehiclesId().equals(vehiclesId)){
                                pc.getVehicleList().remove(v);
                                break;
                            }
                        }
                        em.persist(pc);
                        em.remove(vessel);
            }
                    break;

                default:
                    return "\nSorry, no such type found!";
            }
            return "";
        }
    }
    
    public void persist(Object object) {
        em.persist(object);
    }
}
