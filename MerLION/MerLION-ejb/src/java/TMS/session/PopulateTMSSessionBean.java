/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMS.session;

import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import TMS.entity.Drivers;
import TMS.entity.Routes;
import TMS.entity.Trucks;
import TMS.entity.Vehicles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
@LocalBean
public class PopulateTMSSessionBean {
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private TransAssetMgtSessionBeanLocal tam;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private DriverMgtSessionBeanLocal dms;
    @EJB
    private TransRouteOptiSessionBeanLocal tro;
    
    public Boolean isVehicleEmpty(){
        Query query = em.createQuery("SELECT v FROM Vehicles v");
        return query.getResultList().size() <= 0;
    }
    
    public Boolean isRouteEmpty(){
        Query query = em.createQuery("SELECT r FROM Routes r");
        return query.getResultList().size() <= 0;
    }
    
    public Boolean isDriverEmpty(){
        Query query = em.createQuery("SELECT d FROM Drivers d");
        return query.getResultList().size() <= 0;
    }
    
    public Boolean isMerlionEmpty(){
        Query query = em.createQuery("SELECT v FROM Vehicles v WHERE v.ownerCompany.companyName=?1");
        query.setParameter(1, "merlion");
        return query.getResultList().size() <= 0;
    }
    
    public void populateTMS(){
        try{
            this.populateTruck("company2", "1", "10", "1000", "0.5", "100", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("company2", "2", "20", "2000", "0.4", "200", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("company2", "2", "20", "2000", "0.5", "120", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("company2", "2", "20", "2000", "0.4", "230", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("company2", "2", "20", "2000", "0.25", "220", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("company2", "2", "20", "2000", "0.5", "241", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateDriver("company2", "Tom", "male", "30", "A123456", "5");
            this.populateDriver("company2", "James", "male", "35", "ABafdfad", "3");
            this.populateDriver("company2", "Jerry", "female", "25", "A654321", "3");
            this.populateDriver("company2", "Jolin", "female", "31", "A12daff", "2");
            this.populateRoute("company2", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "byTruck", "100");
            this.populateRoute("company2", "Singapore", "Singapore", "2", "2", "2", "2", "UK", "England", "3", "3", "3", "3", "byTruck", "200");
            this.populateRoute("company2", "UK", "England", "3", "3", "3", "3", "UK", "Wales", "4", "4", "4", "4", "byTruck", "60");
            this.populateRoute("company2", "UK", "Wales", "4", "4", "4", "4", "China", "Jiangsu", "5", "5", "5", "5", "byTruck", "200");
            this.populateRoute("company2", "China", "Jiangsu", "5", "5", "5", "5", "UK", "England", "5", "5", "5", "5", "byTruck", "200");
            this.populateRoute("company2", "China", "Fujian", "7", "7", "7", "7", "USA", "Texas", "8", "8", "8", "8", "byTruck", "200");
            this.populateRoute("company2", "USA", "Texas", "8", "8", "8", "8", "UK", "England", "3", "3", "3", "3", "byTruck", "200");
        
        //for 2nd system release
            this.populateTruck("Yunda", "1", "100", "1000", "0.5", "100", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("Yunda", "2", "120", "1000", "0.4", "120", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("Yunda", "3", "105", "1000", "0.5", "130", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("Yunda", "2", "90", "1000", "0.8", "100", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            
            this.populatePlane("Yunda", "1", "107", "1000", "0.1", "700", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populatePlane("Yunda", "2", "115", "1000", "0.2", "750", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populatePlane("Yunda", "3", "125", "1000", "0.4", "720", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populatePlane("Yunda", "2", "95", "1000", "0.1", "800", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
                    
            this.populateTruck("Yunda", "1", "107", "1000", "0.4", "100", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("Yunda", "2", "115", "1000", "0.5", "120", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("Yunda", "3", "125", "1000", "0.4", "130", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("Yunda", "2", "95", "1000", "0.5", "100", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
        
            this.populateDriver("Yunda", "Li Lei", "male", "30", "A7852341", "5");
            this.populateDriver("Yunda", "James Wallance", "male", "34", "A8067535", "8");
            this.populateDriver("Yunda", "Eason Choo", "male", "42", "A0985636", "13");
            this.populateDriver("Yunda", "Johnny John", "male", "28", "A5642310", "5");
            this.populateDriver("Yunda", "Lillian Lee", "female", "30", "A0769531", "7");
            this.populateDriver("Yunda", "Joyce Mary", "female", "47", "A7095742", "15");
  
            this.populateRoute("Yunda", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "China", "Guang Dong", "Gunagzhou", "Jichang East Road", "888", "510470", "byTruck", "130");
            this.populateRoute("Yunda", "China", "Guang Dong", "Gunagzhou", "Jichang East Road", "888", "510470", "Singapore", "Singapore", "Singapore", "1 Changi South Street 3", "1", "486361", "byPlane", "2700");
            this.populateRoute("Yunda", "Singapore", "Singapore", "Singapore", "1 Changi South Street 3", "1", "486361", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "byTruck", "50");

            Drivers d = dms.retrieveDrivers(3478L);
            Trucks t = (Trucks) tam.viewVehicles(3466L);
            Routes r = tro.viewRoutes(3484L);
            d.setTrucks(t);
            List<Drivers> dList = new ArrayList<>();
            Collection<Vehicles> tList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            tList.add(t);
            t.setRoute(r);
            
            d = dms.retrieveDrivers(3479L);
            t = (Trucks) tam.viewVehicles(3467L);
            d.setTrucks(t);
            dList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            tList.add(t);
            t.setRoute(r);
            
            r.setVehicles(tList);
            
            r = tro.viewRoutes(3486L);
            tList = new ArrayList<>();
            d = dms.retrieveDrivers(3480L);
            t = (Trucks) tam.viewVehicles(3468L);
            d.setTrucks(t);
            dList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            tList.add(t);
            t.setRoute(r);
            
            d = dms.retrieveDrivers(3481L);
            t = (Trucks) tam.viewVehicles(3469L);
            d.setTrucks(t);
            dList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            tList.add(t);
            t.setRoute(r);
            r.setVehicles(tList);
            
            d = dms.retrieveDrivers(3482L);
            t = (Trucks) tam.viewVehicles(3474L);
            d.setTrucks(t);
            dList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            
            d = dms.retrieveDrivers(3483L);
            t = (Trucks) tam.viewVehicles(3475L);
            d.setTrucks(t);
            dList = new ArrayList<>();
            dList.add(d);
            t.setDrivers(dList);
            
            
        }catch(Exception ex){
            System.out.println("popuate TMS");
        }
    }
    
    public void populateForMerlion(){
        try {
            this.populateTruck("merlion", "1", "100", "1000", "0.5", "100", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("merlion", "2", "120", "1000", "0.4", "120", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("merlion", "3", "105", "1000", "0.2", "130", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("merlion", "2", "90", "1000", "0.4", "100", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            
            this.populatePlane("merlion", "1", "107", "1000", "0.1", "700", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populatePlane("merlion", "2", "115", "1000", "0.2", "750", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populatePlane("merlion", "3", "125", "1000", "0.05", "720", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populatePlane("merlion", "2", "95", "1000", "0.2", "800", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
                    
            this.populateTruck("merlion", "1", "107", "1000", "0.5", "100", "TRUE", "FALSE", "FALSE", "FALSE", "TRUE");
            this.populateTruck("merlion", "2", "115", "1000", "0.2", "120", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("merlion", "3", "125", "1000", "0.4", "130", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
            this.populateTruck("merlion", "2", "95", "1000", "0.2", "100", "TRUE", "TRUE", "TRUE", "TRUE", "TRUE");
       
            this.populateRoute("merlion", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "China", "Guang Dong", "Gunagzhou", "Jichang East Road", "888", "510470", "byTruck", "130");
            this.populateRoute("merlion", "China", "Guang Dong", "Gunagzhou", "Jichang East Road", "888", "510470", "Singapore", "Singapore", "Singapore", "1 Changi South Street 3", "1", "486361", "byPlane", "2700");
            this.populateRoute("merlion", "Singapore", "Singapore", "Singapore", "1 Changi South Street 3", "1", "486361", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "byTruck", "50");
            
            List<Vehicles> vList = new ArrayList<>(); 
            vList.add(tam.viewVehicles(3487L));
            vList.add(tam.viewVehicles(3488L));
            vList.add(tam.viewVehicles(3489L));
            vList.add(tam.viewVehicles(3490L));
            Routes r = tro.viewRoutes(3499L);
            r.setVehicles(vList);
            em.merge(r);
            
            vList = new ArrayList<>();
            vList.add(tam.viewVehicles(3491L));
            vList.add(tam.viewVehicles(3492L));
            vList.add(tam.viewVehicles(3493L));
            vList.add(tam.viewVehicles(3494L));
            r = tro.viewRoutes(3500L);
            r.setVehicles(vList);
            em.merge(r);
            
            vList = new ArrayList<>();
            vList.add(tam.viewVehicles(3495L));
            vList.add(tam.viewVehicles(3496L));
            vList.add(tam.viewVehicles(3497L));
            vList.add(tam.viewVehicles(3498L));
            r = tro.viewRoutes(3501L);
            r.setVehicles(vList);
            em.merge(r);
            System.out.println("pop for merlion!");
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateTMSSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VehicleNotExistException ex) {
            Logger.getLogger(PopulateTMSSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RoutesNotExistException ex) {
            Logger.getLogger(PopulateTMSSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void populateTruck(String companyName, String useYear, String capa, String max, String cost, String velocity,
            String peri, String fla, String pha, String high, String ava) throws CompanyNotExistException{
        Company c = cmsbl.retrieveCompany(companyName);
        tam.createTrucks(c.getId(), Integer.parseInt(useYear), Long.parseLong(capa), Long.parseLong(max), Double.parseDouble(cost), Double.parseDouble(velocity), 
                Boolean.parseBoolean(peri), Boolean.parseBoolean(fla), Boolean.parseBoolean(pha), Boolean.parseBoolean(high), Boolean.parseBoolean(ava), null);
    }
    
    public void populatePlane(String companyName, String useYear, String capa, String max, String cost, String velocity,
            String peri, String fla, String pha, String high, String ava) throws CompanyNotExistException{
        Company c = cmsbl.retrieveCompany(companyName);
        tam.createPlanes(c.getId(), Integer.parseInt(useYear), Long.parseLong(capa), Long.parseLong(max), Double.parseDouble(cost), Double.parseDouble(velocity), 
                Boolean.parseBoolean(peri), Boolean.parseBoolean(fla), Boolean.parseBoolean(pha), Boolean.parseBoolean(high), Boolean.parseBoolean(ava));
    }

    public void populateDriver(String companyName, String driverName, String gender, String age, String license, String drivingAge) throws CompanyNotExistException{
        Company c = cmsbl.retrieveCompany(companyName);
        dms.addNewDriver(c.getId(), driverName, gender, Integer.parseInt(age), license, Integer.parseInt(drivingAge));
    }
    
    public void populateRoute(String companyName, String sCountry, String sState, String sCity, String sStreet, String sBlockNo, String sPostal,
            String dCountry, String dState, String dCity, String dStreet, String dBlockNo, String dPostal, String mode, String distance) throws CompanyNotExistException{
        Company c = cmsbl.retrieveCompany(companyName);
        Location source = new Location(sCountry, sState, sCity, sStreet, sBlockNo, sPostal);
        Location dest = new Location(dCountry, dState, dCity, dStreet, dBlockNo, dPostal);
        tro.addNewRoutes(c.getId(), source, dest, mode, Double.parseDouble(distance));
    }
}
