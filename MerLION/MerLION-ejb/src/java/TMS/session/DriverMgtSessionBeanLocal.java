/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import TMS.entity.Drivers;
import TMS.entity.TimeSlot;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.DriverNotExistException;
import util.exception.StartNotBeforeEndException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNoTimetableException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface DriverMgtSessionBeanLocal {
    
    public ArrayList<String> viewTimeTable(Long ownerId, Long driverId)throws CompanyNotExistException, DriverNotExistException;
   
    public ArrayList<Drivers> viewAllDriversInfo(Long ownerId) throws CompanyNotExistException, DriverNotExistException;

    public ArrayList<Drivers> viewAllDrivers(Long ownerId) throws CompanyNotExistException,DriverNotExistException;
    
    public String arrangeTimetable(Long ownerId, Long orderId, Long driverId) throws TransOrderNotExistException, CompanyNotExistException, DriverNotExistException, StartNotBeforeEndException ,VehicleNoTimetableException;
    
    public String addNewDriver(Long ownerId, String name, String gender, Integer age, String licenseNo, Integer drivingAge) throws CompanyNotExistException;
    
    public String deleteDriver(Long ownerId, Long driverId) throws CompanyNotExistException, DriverNotExistException;
    
    public String updateDriver(Long ownerId, Long driverId, String name, String gender, Integer age, String licenseNo, Integer drivingAge) throws CompanyNotExistException, DriverNotExistException;

    public Drivers viewSelectedDriver(Long orderId, Long truckId) throws TransOrderNotExistException;

    public Drivers retrieveDrivers(Long driverId);
}
