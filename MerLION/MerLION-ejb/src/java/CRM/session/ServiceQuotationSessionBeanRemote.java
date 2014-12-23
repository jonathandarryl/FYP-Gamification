/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceQuotation;
import Common.entity.Location;
import OES.entity.Product;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.Remote;
import util.exception.CompanyNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceCatalogNotPublishedException;
import util.exception.ServiceQuotationAlreadyApprovedException;
import util.exception.ServiceQuotationCannotBeAchivedException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ServiceQuotationStartAfterCatalogExpireException;
import util.exception.ServiceQuotationStartBeforeCatalogCommmenceException;
import util.exception.SpecialRequirementNotMetException;
import util.exception.WarehouseNotExistException;
/**
 *
 * @author songhan
 */
@Remote
public interface ServiceQuotationSessionBeanRemote {
    
    //B.2.a.3
    public ServiceQuotation retrieveServiceQuotation(Long sqId) 
            throws ServiceQuotationNotExistException;
    
    public ArrayList<ServiceQuotation> retrieveServiceQuotationList(String ownerCompanyName, String clientCompanyName) 
            throws CompanyNotExistException, ServiceQuotationNotExistException;
    
    public ArrayList<ServiceQuotation> retrieveAllServiceQuotationList(String ownerCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException; 
    
    public ArrayList<ServiceQuotation> retrieveOutsourcingServiceQuotationList(String ownerCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException;
    
    public void removeServiceQuotation(Long sqId)             
            throws ServiceQuotationNotExistException, ServiceQuotationCannotBeAchivedException;
    //B.2.b
    public void approveServiceQuotation(Long sqId)
            throws ServiceQuotationNotExistException, ServiceQuotationAlreadyApprovedException;
    
    public Boolean verifyServiceQuotationWarehouse(Long sqId, Double warehouseBufferRatio)
            throws ServiceQuotationNotExistException;

    public Boolean verifyServiceQuotationTransportation(Long sqId) 
            throws ServiceQuotationNotExistException;

    public void approveOutsourcingServiceQuotation(Long sqId) 
            throws ServiceQuotationNotExistException, ServiceQuotationAlreadyApprovedException;

    public void rejectIncomingServiceQuotation(ServiceQuotation sq);

    public ServiceQuotation addServiceQuotation(Long partnerCompanyId, Long customerCompanyId, String description, Timestamp startTime, Timestamp endTime,
            Integer requiredWarehouseCapacity,String warehouseName, Double totalPrice, Location source, Location dest, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Product product, Double estimatedQuantity) throws CompanyNotExistException;

    public Integer calculateCapacity(Double quantity, Product product);

    public ServiceQuotation updateServiceQuotation(ServiceQuotation sq, String description, Date startTime, Date endTime, Double quantity, Integer duration, Integer orderNumber)
            throws NoVehiclesAssignedException, NoPossibleRoutesException, CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException;

    public ServiceQuotation initiateServiceQuotation(ServiceCatalog sc, String customerCompanyName, String description, Date start, Date end, Product product, Double estimatedQuantity, Integer duration, Integer orderNumber) 
            throws NoVehiclesAssignedException, NoPossibleRoutesException, WarehouseNotExistException, CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException;

    public ServiceQuotation requestForServiceQuotation(ServiceCatalog sc, String clientName, String description, Date start, Date end, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Product product, Double estimatedQuantity, Integer duration, Integer orderNumber) 
            throws NoVehiclesAssignedException, NoPossibleRoutesException, ServiceCatalogNotPublishedException, CompanyNotExistException, WarehouseNotExistException, SpecialRequirementNotMetException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException;

    public void choosePrice(Double chosenPrice, ServiceQuotation sq);

}
