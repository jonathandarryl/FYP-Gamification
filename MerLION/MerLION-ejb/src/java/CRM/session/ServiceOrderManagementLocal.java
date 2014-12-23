/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import CRM.entity.WarehouseOrder;
import Common.entity.Location;
import OES.entity.Product;
import TMS.entity.TransOrder;
import WMS.entity.Warehouse;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.ExceedMaxCapacityException;
import util.exception.InsufficientInventoryException;
import util.exception.LocationNotMatchException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceOrderAlreadyApprovedException;
import util.exception.ServiceOrderAlreadyEstablishedTransOrderException;
import util.exception.ServiceOrderAlreadyEstablishedWarehouseOrderException;
import util.exception.ServiceOrderAlreadyFulfilledException;
import util.exception.ServiceOrderAlreadyOutsourcedException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.ServiceOrderStartAfterContractExpireException;
import util.exception.ServiceOrderStartBeforeContractCommmenceException;
import util.exception.TransOrderNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseOrderNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface ServiceOrderManagementLocal {
    //B.5.a.1
    
    public Long createServiceOrder(String partnerCompanyName, String customerCompanyName, Timestamp startTime, Timestamp endTime, 
            Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId, 
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, 
            Boolean highValue, Long serviceContractId) 
            throws ServiceContractNotExistException, ServiceOrderStartAfterContractExpireException, ServiceOrderStartBeforeContractCommmenceException;
    
    public Long createServiceOrder(String partnerCompanyName, String customerCompanyName, Timestamp startTime, Timestamp endTime, 
            Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId,
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical,
            Boolean highValue, Long serviceContractId, Boolean aggregated)
            throws ServiceContractNotExistException, ServiceOrderStartAfterContractExpireException, ServiceOrderStartBeforeContractCommmenceException, InsufficientInventoryException;

//B.5.a.2
    public void updateServiceOrder( Long soId,Timestamp startTime, Timestamp endTime, 
            Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId,
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) 
            throws ServiceOrderNotExistException, ServiceOrderStartAfterContractExpireException;
    //B.5.a.3
    public ServiceOrder retrieveServiceOrder(Long soId) 
            throws ServiceOrderNotExistException;
    
    public List<ServiceOrder> retrieveOwningServiceOrderList(String partnerCompanyName) 
            throws ServiceOrderNotExistException, CompanyNotExistException;
    
    public List<ServiceOrder> retrieveOwningServiceOrderListWithSpecificClient(String partnerCompanyName, String clientCompanyName) 
            throws ServiceOrderNotExistException, CompanyNotExistException;
    
    public List<ServiceOrder> retrieveOutsourcingServiceOrderList(String clientCompanyName) 
            throws ServiceOrderNotExistException, CompanyNotExistException;
  
    public void approveServiceOrder(Long soId)
            throws ServiceOrderNotExistException, ServiceOrderAlreadyApprovedException;

    //B.5.a.5
    public Long createTransportationOrder(Long soId) 
            throws WarehouseNotExistException, ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedTransOrderException, ServiceOrderNotPaidException, TransOrderNotExistException;
    
    //B.5.a.6
    public void updateTransportationOrder(Long transOrderId, Integer startYear, Integer startMonth, Integer startDay, Integer endYear, Integer endMonth, 
            Integer endDay, Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) 
            throws ServiceOrderNotExistException, TransOrderNotExistException, CompanyNotExistException;
    
    public void updateTransportationOrder(Long transOrderId, Timestamp startTime, Timestamp endTime, 
            Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) 
            throws ServiceOrderNotExistException, TransOrderNotExistException, CompanyNotExistException, ServiceOrderStartAfterContractExpireException;
    
    public void fulfillTransOrder(Long transOrderId)
            throws TransOrderNotExistException, CompanyNotExistException, ServiceOrderAlreadyFulfilledException;    
    
    //B.5.a.7
    public TransOrder retrieveTransOrder(Long transId) 
            throws TransOrderNotExistException;
    
    public List<TransOrder> retrieveOwningTransOrderList(String partnerCompanyName) 
            throws TransOrderNotExistException, CompanyNotExistException;

    public List<TransOrder> retrieveOutsourcingTransOrderList(String clientCompanyName) 
            throws TransOrderNotExistException, CompanyNotExistException;

    public List<TransOrder> retrieveTransOrderListWithSpecificCompany(String partnerCompanyName, String clientCompanyName) 
            throws TransOrderNotExistException, CompanyNotExistException;    
    
    public List<ServiceOrder> retrieveServiceOrderListForTransOrderCreation(String partnerCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException;
    
    //B.5.a.8
    public void createWarehouseOrder(Long soId) 
            throws ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedWarehouseOrderException, ServiceOrderNotPaidException;
    
    //B.5.a.9
    public void updateWarehouseOrder(Long woId, Timestamp startTime, Timestamp endTime, 
            Double price, String description, Product product, Double quantity, Long warehouseId,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws ServiceOrderNotExistException, WarehouseOrderNotExistException, ServiceOrderStartAfterContractExpireException;

    public void fulfillWarehouseOrder(Long woId) 
            throws WarehouseOrderNotExistException, ServiceOrderAlreadyFulfilledException;
    //B.5.a.10
    public WarehouseOrder retrieveWarehouseOrder(Long woId) 
            throws WarehouseOrderNotExistException;    

    public List<ServiceOrder> retrieveServiceOrderListForWarehouseOrderCreation(String partnerCompanyName) 
            throws ServiceOrderNotExistException, CompanyNotExistException;

    
    public List<WarehouseOrder> retrieveOwningWarehouseOrderList(String partnerCompanyName) 
            throws WarehouseOrderNotExistException, CompanyNotExistException;

    public List<WarehouseOrder> retrieveOutsourcingWarehouseOrderList(String clientCompanyName) 
            throws WarehouseOrderNotExistException, CompanyNotExistException;

    public List<WarehouseOrder> retrieveWarehouseOrderListWithSpecificCompany(String partnerCompanyName, String clientCompanyName) 
            throws WarehouseOrderNotExistException, CompanyNotExistException;

    public void approveServiceOrder(ServiceOrder so) 
            throws ServiceOrderNotExistException, ServiceOrderAlreadyApprovedException;

    public ServiceOrder setSourceWarehouse(ServiceOrder so, Warehouse wh);

    public ServiceOrder createOutsourceServiceOrder(ServiceOrder so, ServiceContract sc, String description) 
            throws ServiceOrderNotApprovedException, ServiceOrderNotPaidException, ServiceOrderAlreadyOutsourcedException, LocationNotMatchException, ExceedMaxCapacityException;

    
}
