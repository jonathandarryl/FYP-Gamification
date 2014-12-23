/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Location;
import TMS.entity.Drivers;
import TMS.entity.Routes;
import TMS.entity.TransOrder;
import TMS.entity.Vehicles;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNoDriversException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface TransOperationMgtSessionBeanLocal {
    public ArrayList<TransOrder> viewAllTransOrders (Long ownerId)throws CompanyNotExistException, TransOrderNotExistException;

    public String viewDeliverTime(Long ownerId, Long transOrderId)throws CompanyNotExistException, TransOrderNotExistException;

    public List<Timestamp> scheduleDeliverTime(Long ownerId, Long transOrderId, Long vehicleId, Timestamp startTime, Double velocity)throws CompanyNotExistException, TransOrderNotExistException, VehicleNotExistException, VehicleNoDriversException;

    public List<Routes> viewRoutes(Long ownerId, Long transOrderId)throws CompanyNotExistException, TransOrderNotExistException, RoutesNotExistException;

    public ArrayList<Routes> setTransRoutes(Long ownerId, Location start, Location dest, ArrayList<ArrayList<String>> stopover)throws CompanyNotExistException;

    public String viewMode(Long ownerId, Long transOrderId, Long routesId)throws CompanyNotExistException, TransOrderNotExistException, RoutesNotExistException;

    public String setRouteMode(Long ownerId, Long transOrderId, Long routesId, String mode)throws CompanyNotExistException, RoutesNotExistException, TransOrderNotExistException;   

    public Boolean assignVehicleToRoute(Long ownerId, Long routesId, ArrayList<Vehicles> vList) throws CompanyNotExistException, RoutesNotExistException;

    public void assignRoutesToOrder(Long ownerId, Long transOrderId, ArrayList<Routes> routes) throws CompanyNotExistException, RoutesNotExistException, TransOrderNotExistException;

    public List<Routes> checkGoThrough(Long ownerId, Long orderId, List<Routes> routes) throws CompanyNotExistException, TransOrderNotExistException;

    public void assignDriversToTruck(Long ownerId, Long truckId, List<Drivers> dList) throws CompanyNotExistException, VehicleNotExistException;

    public List<Drivers> viewAssignedDrivers(Long ownerId, Long truckId) throws CompanyNotExistException, VehicleNotExistException;

    public List<Timestamp> viewTimetable(Vehicles v);

    public void reassignVehiclesToRoute(Long ownerId, Long routesId) throws CompanyNotExistException, RoutesNotExistException;
}
