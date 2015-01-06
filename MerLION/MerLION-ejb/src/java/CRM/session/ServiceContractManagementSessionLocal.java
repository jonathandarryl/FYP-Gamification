/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceContractRenewNotification;
import CRM.entity.ServiceQuotation;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceContractAlreadySignedException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
import util.exception.ServiceQuotationAlreadyEstablishedContractException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ServiceQuotationNotApprovedException;
import util.exception.ServiceQuotationStartAfterCatalogExpireException;
import util.exception.ServiceQuotationStartBeforeCatalogCommmenceException;

/**
 *
 * @author songhan
 */
@Local
public interface ServiceContractManagementSessionLocal {
    //B.3.b.1
    public Long createServiceContract(Long sqId) 
            throws ServiceQuotationNotExistException, ServiceQuotationNotApprovedException, ServiceQuotationAlreadyEstablishedContractException, CompanyNotExistException, MembershipSchemaNotExistException;
    
    //B.3.b.2
    public void updateServiceContract(ServiceContract sc, String description, Date start, Date end)
            throws NoVehiclesAssignedException, NoPossibleRoutesException, CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException;
    
    //B.3.b.3
    public ServiceContract retrieveServiceContract(Long scId) 
            throws ServiceContractNotExistException;
    
    public ArrayList<ServiceContract> retrieveServiceContractList(Long ownerCompanyId) 
             throws ServiceContractNotExistException;

    public ArrayList<ServiceContract> retrieveServiceContractWithSpecificCompanyList(Long ownerCompanyId, String clientCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException;
    
     public ArrayList<ServiceContract> retrieveServiceContractWithSpecificCompanyList(String ownerCompanyName, String clientCompanyName)
             throws ServiceContractNotExistException, CompanyNotExistException;
    //B.3.b.4
    public void sendContractRenewNotification(Long scId) 
            throws ServiceContractNotExistException, ServiceContractNotSignedException;

//    public Integer getOutsourcingTruckNumber(Long outsourcerCompanyId) 
//            throws CompanyNotExistException;
//
//    public Integer getOutsourcingPlaneNumber(Long outsourcerCompanyId) 
//            throws CompanyNotExistException;
//
//    public Integer getOutsourcingVesselNumber(Long outsourcerCompanyId) 
//            throws CompanyNotExistException;
//
//    public Integer getOutsourcingWarehouseCapacity(Long outsourcerCompanyId) 
//            throws CompanyNotExistException;
//    

    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationList(Long ownerCompanyId) 
            throws ServiceQuotationNotExistException;

    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationWithSpecificCompanyList(Long ownerCompanyId, Long clientCompanyId) 
            throws ServiceQuotationNotExistException;

    public ArrayList<ServiceContract> retrieveServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException;   
        
    public ArrayList<ServiceContract> retrieveOutsourcingServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException;
    
    public ArrayList<ServiceContract> retrieveCurrentOutsourcingServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException;

    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationWithSpecificCompanyList(String ownerCompanyName, String clientCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException;

    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationList(String ownerCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException;

    public ArrayList<ServiceContractRenewNotification> getIncomingReminderList(String ownerCompanyName) 
            throws CompanyNotExistException;

    public ArrayList<ServiceContractRenewNotification> getOutgoingReminderList(String ownerCompanyName) 
            throws CompanyNotExistException;

    public void signOutsourcingContract(Long scId) throws ServiceContractNotExistException, ServiceContractAlreadySignedException;    
 
   
}
