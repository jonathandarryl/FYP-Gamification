/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import CRM.entity.ServiceOrder;
import CRM.entity.ServiceQuotation;
import Common.entity.Location;
import OES.entity.Product;
import TMS.entity.TransOrder;
import TMS.entity.Vehicles;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceOrderNotExistException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNotExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface TransOrderMgtSessionBeanLocal {
    public Long createTransOrder(ServiceOrder so) throws ServiceOrderNotExistException, WarehouseNotExistException;
    
    public List<TransOrder> viewAllTransOrders(Long ownerId) throws CompanyNotExistException, TransOrderNotExistException;
    
    public void updateTransOrder(Long ownerId, Long transOrderId, Location currentLocation)
            throws TransOrderNotExistException,  CompanyNotExistException, ServiceOrderNotExistException;
   
    public void fulfillTransOrder(Long ownerId, Long transOrderId)
            throws TransOrderNotExistException, CompanyNotExistException, ServiceOrderNotExistException;
   
    public void deleteTransOrder(Long ownerId, Long transOrderId)
            throws TransOrderNotExistException,  CompanyNotExistException;

    public TransOrder retrieveTransOrder(Long ownerId, Long toId) 
            throws TransOrderNotExistException, CompanyNotExistException;
    
    public TransOrder retrieveTransOrder(Long toId) 
            throws TransOrderNotExistException;

    public TransOrder retrieveTransOrderClient(Long clientId, Long toId) throws TransOrderNotExistException, CompanyNotExistException;

    public List<TransOrder> viewAllTransOrdersClient(Long clientId) throws CompanyNotExistException, TransOrderNotExistException;
            
    public Boolean verifyViability(ServiceQuotation sq);

    public List<Vehicles> retrieveCapableVehicles(Long ownerId, Long orderId, Long routeId) throws CompanyNotExistException, TransOrderNotExistException, RoutesNotExistException, VehicleNotExistException;

    public Boolean checkTotalCapacity(Long orderId, List<Vehicles> vList);

    public TransOrder retrieveTransOrder(Long ownerId, String trackingNumber) throws TransOrderNotExistException, CompanyNotExistException;
   
}
