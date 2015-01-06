/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Company;
import Common.entity.Location;
import TMS.entity.Drivers;
import TMS.entity.Routes;
import TMS.entity.TransOrder;
import TMS.entity.Trucks;
import TMS.entity.Vehicles;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNoDriversException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class TransOperationMgtSessionBean implements TransOperationMgtSessionBeanLocal {
    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;
    
    @Override
    public ArrayList<TransOrder> viewAllTransOrders(Long ownerId) throws CompanyNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
               throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query=em.createQuery("select t from TransOrder t WHERE t.serviceOrder.serviceContract.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            ArrayList<TransOrder> transOrdersList = new ArrayList<>(query.getResultList());
            if(transOrdersList.isEmpty()) {
               throw new TransOrderNotExistException("Transportation order not exist!");
            }else{
                return transOrdersList;
            }
        }
    }

    //D.2.1
    @Override
    public String viewDeliverTime(Long ownerId, Long transOrderId) throws CompanyNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
               throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);
            if (transOrder == null || !transOrder.getServiceOrder().getServiceContract().getProvider().getId().equals(ownerId)) {
                throw new TransOrderNotExistException("Transportation order not exist!");
            }else{               
                return "From: "+transOrder.getStartDeliverTime().toString() + " to: " + transOrder.getEndDeliverTime().toString();
            }
        }
    }
    
    //D.2.2
    @Override
    public List<Timestamp> scheduleDeliverTime(Long ownerId, Long transOrderId, Long vehicleId, Timestamp startTime, Double velocity) throws CompanyNotExistException, TransOrderNotExistException, VehicleNotExistException, VehicleNoDriversException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
               throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);
            if (transOrder == null) {
               throw new TransOrderNotExistException("Transportation order not exist!");
            }else {
                Vehicles v = em.find(Vehicles.class, vehicleId);
                if (v == null) {
                    throw new VehicleNotExistException("vehicle not exist!");
                }else{
                    //TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                    v.setTransOrderId(transOrderId); 
                    if(v.getType().toLowerCase().equals("truck")){
                        for(Drivers d:((Trucks)v).getDrivers()){
                            d.setTransOrderId(transOrderId);
                            em.merge(d);
                        }
                    }
                    em.merge(v);
                    System.out.println("timetable"+v.getRoute().getMode());
                    Integer duration = ((Double)(v.getRoute().getDistance()/velocity)).intValue();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startTime);
                    Timestamp endTime = addDeliverEndTime(cal, duration);
                    if(v.getTimetable().isEmpty() || checkTime(v.getTimetable(), startTime, endTime)){
                        System.out.println("add timestamp");
                        v.getTimetable().add(startTime);
                        v.getTimetable().add(endTime);
                        em.merge(v);                       
                        System.out.println("timestamp: " + v.getTimetable().size());
                        em.merge(transOrder);
                        em.flush();
                        em.refresh(v);
                        em.flush();
                        return v.getTimetable();
                    }else{
                        return null;
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
    
    private Timestamp addDeliverEndTime(Calendar cal, Integer hour){
        Timestamp exTime;
        cal.add(Calendar.HOUR, hour);
        exTime = new Timestamp(cal.getTimeInMillis());
        return exTime;
    }
    
    @Override
    public List<Timestamp> viewTimetable(Vehicles v){
        em.flush();
        return v.getTimetable();
    }
    
    //D.2.3
    @Override
    public List<Routes> viewRoutes(Long ownerId, Long transOrderId) throws CompanyNotExistException, TransOrderNotExistException, RoutesNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);
            if(transOrder == null){
                throw new TransOrderNotExistException("Order not exist!");
            }else{
                if(transOrder.getRoutes() == null){
                    throw new RoutesNotExistException("Routes not exist!");
                }else{
                    return transOrder.getRoutes();
                }
            }
        }
    }
    
    //D.2.4
    @Override
    public ArrayList<Routes> setTransRoutes(Long ownerId, Location start, Location dest, ArrayList<ArrayList<String>> stopover) throws CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            ArrayList<Routes> routesList = new ArrayList<>();
            ArrayList<Location> stopoverList = new ArrayList<>();
            for (ArrayList<String> l : stopover) {
            Location location = new Location(l.get(0), l.get(1), l.get(2), 
                    l.get(3), l.get(4), l.get(5));
            stopoverList.add(location);
            }
            Routes startRoute = new Routes(start, stopoverList.get(0));
            routesList.add(startRoute);   
            for(int i=1; i<stopoverList.size()-1; i++){
                Routes r = new Routes(stopoverList.get(i), stopoverList.get(i+1));
                //end of N = start of N+1
                routesList.add(r);
            }
            Routes endRoute = new Routes(stopoverList.get(stopoverList.size()-1), dest); 
            routesList.add(endRoute);
            return routesList;
        }
    }
    
    @Override
    public List<Routes> checkGoThrough(Long ownerId, Long orderId, List<Routes> routes) throws CompanyNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, orderId);  
            if (transOrder == null) {
               throw new TransOrderNotExistException("Transportation order not exist!");
            }else {
                Location start = transOrder.getStartPoint();
                Location dest = transOrder.getEndPoint();
                Boolean can = true;
                for(int i=0; i<routes.size() && can; i++){
                    if(routes.get(i).getVehicles().isEmpty()) {
                        System.out.println("in routes: "+ i);
                        return null;
                    }
                    can = false;
                    for(int j=i; j<routes.size(); j++){
                        if(locationEqual(routes.get(j).getStartOfRoute(), start)){            
                            Routes temp = routes.get(i);
                            routes.set(i, routes.get(j));
                            routes.set(j, temp);
                            start = routes.get(i).getDestOfRoute();
                            can = true;
                            break;
                        }
                    }
                }
                if(can && locationEqual(routes.get(routes.size()-1).getDestOfRoute(), dest)){                   
                    return routes;
                }else{
                    return null;
                }
            }
        }
    }
    
    private Boolean locationEqual(Location l1, Location l2){
        return l1.getCountry().equals(l2.getCountry()) && l1.getState().equals(l2.getState()) && l1.getCity().equals(l2.getCity())
             && l1.getStreet().equals(l2.getStreet()) && l1.getBlockNo().equals(l2.getBlockNo()) && l1.getPostalCode().equals(l2.getPostalCode());                
    }
    
    @Override
    public void assignRoutesToOrder(Long ownerId, Long transOrderId, ArrayList<Routes> routes) throws CompanyNotExistException, RoutesNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);  
            if (transOrder == null) {
               throw new TransOrderNotExistException("Transportation order not exist!");
            }else {
                transOrder.setRoutes(routes);
                em.merge(transOrder);
            }
        }
    }
    
    //D.2.5
    @Override
    public String viewMode(Long ownerId, Long transOrderId, Long routesId)throws CompanyNotExistException,RoutesNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
        TransOrder transOrder = em.find(TransOrder.class, transOrderId);
        if (transOrder == null) {
               throw new TransOrderNotExistException("Transportation order not exist!");
        }else {
            String result = "\nSorry, no such route record is found!";
            ArrayList<Routes> routeList = (ArrayList<Routes>) this.viewRoutes(ownerId, transOrderId);
            for (Routes R:routeList){
               if(Objects.equals(R.getRoutesId(), routesId)){
                   result = R.getMode();
                   break;
               }
            }
             return result;
            }
        }
    }
    
    //D.2.6
    @Override
    public String setRouteMode(Long ownerId, Long transOrderId, Long routesId, String mode) throws CompanyNotExistException, RoutesNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);
            if (transOrder == null) {
               throw new TransOrderNotExistException("Transportation order not exist!");
            }else {
                String result = "\nSorry, no such route record is found!";
                ArrayList<Routes> routeList = (ArrayList<Routes>) this.viewRoutes(ownerId, transOrderId);
                for (Routes R:routeList){
                   if(Objects.equals(R.getRoutesId(), routesId)){
                       R.setMode(mode);
                       em.persist(transOrder);
                       result = "\nRoute mode set successfully!";
                       break;
                   }
                }
                return result;
            }
        }
    }
    
    @Override
    public Boolean assignVehicleToRoute(Long ownerId, Long routesId, ArrayList<Vehicles> vList) throws CompanyNotExistException, RoutesNotExistException{
        Company pc = em.find(Company.class, ownerId);
        Routes r = em.find(Routes.class, routesId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else if (r == null){
            throw new RoutesNotExistException("Routes " + routesId + " not exist!");
        }else{
            if(!r.getVehicles().isEmpty()){
                System.out.println("route get vehicle not empty!");
                for(Vehicles v: r.getVehicles()){
                    v.setRoute(null);
                    em.merge(v);
                }
                r.setVehicles(new ArrayList<>());
            }
            for(Vehicles v: vList){
                if(v.getType().toLowerCase().equals("truck")){
                    Trucks t = (Trucks) v;
                    if(t.getDrivers().isEmpty()){
                        return false;
                    }
                }
                if(v.getRoute()!=null){
                    return false;
                }
                v.setRoute(r);
                em.merge(v);
            }
            if(!r.getVehicles().isEmpty()){
                r.getVehicles().addAll(vList);
            }else{
                r.setVehicles(vList);
            }
            em.merge(r);
            return true;
        }
    }
    
    @Override
    public void reassignVehiclesToRoute(Long ownerId, Long routesId) throws CompanyNotExistException, RoutesNotExistException{
        Company pc = em.find(Company.class, ownerId);
        Routes r = em.find(Routes.class, routesId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else if (r == null){
            throw new RoutesNotExistException("Routes " + routesId + " not exist!");
        }else{
            if(!r.getVehicles().isEmpty()){
                for(Vehicles v: r.getVehicles()){
                    v.setRoute(null);
                }
            }
            r.setVehicles(null);
        }
    }
    
    @Override
    public void assignDriversToTruck(Long ownerId, Long truckId, List<Drivers> dList) throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        Trucks t = em.find(Trucks.class, truckId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else if (t == null){
            throw new VehicleNotExistException("Routes " + truckId + " not exist!");
        }else{
            for(Drivers d: dList){
                d.setTrucks(t);
                em.merge(d);
            }
            t.setDrivers(dList);
            em.merge(t);       
        }
    }
    
    @Override
    public List<Drivers> viewAssignedDrivers(Long ownerId, Long truckId) throws CompanyNotExistException, VehicleNotExistException{
        Company pc = em.find(Company.class, ownerId);
        Trucks t = em.find(Trucks.class, truckId);
        if (pc == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else if (t == null){
            throw new VehicleNotExistException("Routes " + truckId + " not exist!");
        }else{
            return t.getDrivers();
        }
    }
}