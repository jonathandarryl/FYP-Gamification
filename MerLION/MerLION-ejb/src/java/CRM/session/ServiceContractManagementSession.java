/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceContractRenewNotification;
import CRM.entity.ServiceQuotation;
import Common.entity.Company;
import Common.entity.PartnerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
@Stateless
public class ServiceContractManagementSession implements ServiceContractManagementSessionLocal, ServiceContractSessionBeanRemote {
    @PersistenceContext
    private EntityManager em;
    @EJB
    private ServiceQuotationManagementSessionLocal sqm;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private ClientInfoMgtSessionBeanLocal cim;
    
    @Override
    public Long createServiceContract(Long sqId)
            throws ServiceQuotationNotExistException, ServiceQuotationNotApprovedException, ServiceQuotationAlreadyEstablishedContractException, CompanyNotExistException, MembershipSchemaNotExistException{
        ServiceQuotation sq = em.find(ServiceQuotation.class, sqId);
        if(!sq.isApprovedOrNot() || !sq.getClientApprovedOrNot())
            throw new ServiceQuotationNotApprovedException("Contract creation failure. ServiceQuotation "+sqId+" has not been approved by both sides yet!");
        if(sq.getEstablishedContractOrNot())
            throw new ServiceQuotationAlreadyEstablishedContractException("Contract creation failure. A contract has already established based on ServiceQuotation "+sqId+"!");
        ServiceContract sc = new ServiceContract(sq);
        em.persist(sc);
        sq.setServiceContract(sc);
        sq.setEstablishedContractOrNot(Boolean.TRUE);
        em.persist(sc);
        Company com = sq.getClientCompany();
        com.getOutsourcingServiceContract().add(sc);
        
        em.merge(sq);
        em.merge(com);
        if(sq.getpCompany().getCompanyName().equals("merlion")) {
            Company proCom = sq.getpCompany();
            System.out.println("get providing svc contract list " + proCom.getCompanyName());
            ((PartnerCompany) proCom).getProvidingServiceContractList().add(sc);
            cim.addClientRecord(proCom.getId(), com.getCompanyName(), com.getContactNo()); 
            em.merge(proCom);
        }else{
            PartnerCompany proCom = (PartnerCompany) sq.getpCompany();
            System.out.println("get providing svc contract list " + proCom.getCompanyName());
            proCom.getProvidingServiceContractList().add(sc);
            cim.addClientRecord(proCom.getId(), com.getCompanyName(), com.getContactNo());    
            em.merge(proCom);
        }   
        System.out.println("Contract creation successful! "+sc.getId());
        return sc.getId();
    }
        
    @Override
    public void updateServiceContract(ServiceContract sc, String description, Date start, Date end)
            throws NoVehiclesAssignedException, NoPossibleRoutesException, CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException{
        try {
            ServiceQuotation sq = sc.getServiceQuotation();
            Double quantity = sq.getEstimatedQuantity();
            Integer duration = sq.getStorageDurationDays();
            Integer orderNumber = sq.getOrderNumber();
            sqm.updateServiceQuotation(sq, description, start, end, quantity, duration, orderNumber);
        } catch (NoVehiclesAssignedException ex) {
            throw new NoVehiclesAssignedException("updata contact: no vehicles assigned");
        } catch (NoPossibleRoutesException ex) {
            throw new NoPossibleRoutesException("update contract: Routes cannot go through");
        }
    }
    
    @Override
    public ServiceContract retrieveServiceContract(Long scId)
            throws ServiceContractNotExistException{
        ServiceContract sc = em.find(ServiceContract.class, scId);
        if(sc==null)
            throw new ServiceContractNotExistException("Service Contract "+scId+" not found!");
        System.out.println("Service Contract "+scId+" found!");
        return sc;
    }
    
    @Override
    public ArrayList<ServiceContract> retrieveServiceContractList(Long ownerCompanyId) 
            throws ServiceContractNotExistException{
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.pCompany.id=?1");
        query.setParameter(1, ownerCompanyId);
        ArrayList<ServiceContract> serviceContractList = new ArrayList(query.getResultList());
        if(serviceContractList.isEmpty())
            throw new ServiceContractNotExistException("Company "+ownerCompanyId+" does not have any service contract!");

        return serviceContractList;
    }
    
    @Override
    public ArrayList<ServiceContract> retrieveServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.pCompany=?1");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceContract> serviceContractList = new ArrayList(query.getResultList());
        if(serviceContractList.isEmpty())
            throw new ServiceContractNotExistException("Company "+ownerCompanyName+" does not have any service contract!");

        return serviceContractList;
    }
    
    @Override
    public ArrayList<ServiceContract> retrieveOutsourcingServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.clientCompany=?1");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceContract> serviceContractList = new ArrayList(query.getResultList());
        if(serviceContractList.isEmpty())
            throw new ServiceContractNotExistException("Company "+ownerCompanyName+" does not have any service contract!");

        return serviceContractList;
    }
    
    @Override
    public ArrayList<ServiceContract> retrieveCurrentOutsourcingServiceContractList(String ownerCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.clientCompany=?1 and s.clientSignOrNot=TRUE");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceContract> serviceContractList = new ArrayList(query.getResultList());
        if(serviceContractList.isEmpty())
            throw new ServiceContractNotExistException("Company "+ownerCompanyName+" does not have any service contract!");

        return serviceContractList;
    }
   
    @Override
    public ArrayList<ServiceContract> retrieveServiceContractWithSpecificCompanyList(Long ownerCompanyId, String clientCompanyName) 
            throws ServiceContractNotExistException{
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.pCompany.id=?1 and s.serviceQuotation.clientCompany.companyName=?2");
        query.setParameter(1, ownerCompanyId);
        query.setParameter(2, clientCompanyName);
        ArrayList<ServiceContract> serviceContractList = new ArrayList(query.getResultList());
        if(serviceContractList.isEmpty())
            throw new ServiceContractNotExistException("No service contract with company "+clientCompanyName+"!");

        return serviceContractList;
    }
    
    @Override
    public ArrayList<ServiceContract> retrieveServiceContractWithSpecificCompanyList(String ownerCompanyName, String clientCompanyName) 
            throws ServiceContractNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query=em.createQuery("select s from ServiceContract s where s.serviceQuotation.pCompany=?1 and s.serviceQuotation.clientCompany=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        ArrayList<ServiceContract> serviceContractList = new ArrayList<>(query.getResultList());
        if(serviceContractList.isEmpty()) {
            throw new ServiceContractNotExistException("No service contract with company "+clientCompanyName+"!");
        }

        return serviceContractList;
    }
    
    @Override
    public void sendContractRenewNotification(Long scId) 
            throws ServiceContractNotExistException, ServiceContractNotSignedException{
        ServiceContract sc = retrieveServiceContract(scId);
        if(!sc.getClientSignOrNot())
            throw new ServiceContractNotSignedException("Reminder sent failed. Service Contract "+scId+" has not been signed by client yet!");
        ServiceContractRenewNotification sn = new ServiceContractRenewNotification(sc);
        em.persist(sn);
    }
    
    @Override
    public ArrayList<ServiceContractRenewNotification> getIncomingReminderList(String ownerCompanyName) 
            throws CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceContractRenewNotification s where s.serviceContract.serviceQuotation.clientCompany=?1");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceContractRenewNotification> serviceContractRenewNotificationList = new ArrayList(query.getResultList());
        return serviceContractRenewNotificationList;
    }
    
    @Override
    public ArrayList<ServiceContractRenewNotification> getOutgoingReminderList(String ownerCompanyName) 
            throws CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceContractRenewNotification s where s.serviceContract.serviceQuotation.pCompany=?1");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceContractRenewNotification> serviceContractRenewNotificationList = new ArrayList(query.getResultList());
        return serviceContractRenewNotificationList;
    }
    
    @Override
    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationList(Long ownerCompanyId) 
            throws ServiceQuotationNotExistException{
        Query query=em.createQuery("select s from ServiceQuotation s where s.pCompany.id=?1 and s.archivedOrNot=FALSE and s.approvedOrNot=TRUE and s.establishedContractOrNot=FALSE");
        query.setParameter(1, ownerCompanyId);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if(serviceQuotationList.isEmpty())
            throw new ServiceQuotationNotExistException("Company "+ownerCompanyId+" does not have any service quotation suitable for new contract establishment!");

        return serviceQuotationList;
    }
    
    @Override
    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationList(String ownerCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceQuotation s where s.pCompany=?1 and s.archivedOrNot=FALSE and s.approvedOrNot=TRUE and s.establishedContractOrNot=FALSE and s.clientApprovedOrNot=TRUE");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if(serviceQuotationList.isEmpty())
            throw new ServiceQuotationNotExistException("Company "+ownerCompanyName+" does not have any service quotation suitable for new contract establishment!");

        return serviceQuotationList;
    }
    
    @Override
    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationWithSpecificCompanyList(Long ownerCompanyId, Long clientCompanyId) 
            throws ServiceQuotationNotExistException{
        Query query=em.createQuery("select s from ServiceQuotation s where s.pCompany.id=?1 and s.clientCompany.id=?2 and s.archivedOrNot=FALSE and s.approvedOrNot=TRUE and s.establishedContractOrNot=FALSE");
        query.setParameter(1, ownerCompanyId);
        query.setParameter(2, clientCompanyId);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if(serviceQuotationList.isEmpty())
            throw new ServiceQuotationNotExistException("No service quotation suitable for new contract establishment with company "+clientCompanyId+"!");

        return serviceQuotationList;
    }
    
    @Override
    public ArrayList<ServiceQuotation> retrieveSuitableServiceQuotationWithSpecificCompanyList(String ownerCompanyName, String clientCompanyName) 
            throws ServiceQuotationNotExistException, CompanyNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query=em.createQuery("select s from ServiceQuotation s where s.pCompany=?1 and s.clientCompany=?2 and s.archivedOrNot=FALSE and s.approvedOrNot=TRUE and s.establishedContractOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        @SuppressWarnings("unchecked")
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList<>(query.getResultList());
        if(serviceQuotationList.isEmpty()) {
            throw new ServiceQuotationNotExistException("No service quotation suitable for new contract establishment with company "+clientCompanyName+"!");
        }

        return serviceQuotationList;
    }
    
    @Override
    public void signOutsourcingContract(Long scId) 
            throws ServiceContractNotExistException, ServiceContractAlreadySignedException{
        ServiceContract sc = retrieveServiceContract(scId);
        if(sc.getClientSignOrNot())
            throw new ServiceContractAlreadySignedException("Service Contract "+scId+" has already been signed!");
        sc.setClientSignOrNot(Boolean.TRUE);
    }
//    @Override
//    public Integer getOutsourcingTruckNumber(Long outsourcerCompanyId)
//            throws CompanyNotExistException{
//        Company com = cmsb.retrieveCompany(outsourcerCompanyId);
//        List<ServiceContract> contractList = com.getOutsourcingServiceContract();
//        Integer total = 0;
//        if(contractList.isEmpty())
//            return total;
//        for(Object o: contractList){
//            ServiceContract curContract = (ServiceContract) o;
//            Integer curTruck = curContract.getServiceQuotation().getRequiredTruckNumber();
//            total = total + curTruck;
//        }
//        return total;
//    }
//    
//    @Override
//    public Integer getOutsourcingPlaneNumber(Long outsourcerCompanyId)
//            throws CompanyNotExistException{
//        Company com = cmsb.retrieveCompany(outsourcerCompanyId);
//        List<ServiceContract> contractList = com.getOutsourcingServiceContract();
//        Integer total = 0;
//        if(contractList.isEmpty())
//            return total;
//        for(Object o: contractList){
//            ServiceContract curContract = (ServiceContract) o;
//            Integer curPlane = curContract.getServiceQuotation().getRequiredPlaneNumber();
//            total = total + curPlane;
//        }
//        return total;
//    }
//    
//    @Override
//    public Integer getOutsourcingVesselNumber(Long outsourcerCompanyId)
//            throws CompanyNotExistException{
//        Company com = cmsb.retrieveCompany(outsourcerCompanyId);
//        List<ServiceContract> contractList = com.getOutsourcingServiceContract();
//        Integer total = 0;
//        if(contractList.isEmpty())
//            return total;
//        for(Object o: contractList){
//            ServiceContract curContract = (ServiceContract) o;
//            Integer curVessel = curContract.getServiceQuotation().getRequiredVesselNumber();
//            total = total + curVessel;
//        }
//        return total;
//    }
//    
//    @Override
//    public Integer getOutsourcingWarehouseCapacity(Long outsourcerCompanyId)
//            throws CompanyNotExistException{
//        Company com = cmsb.retrieveCompany(outsourcerCompanyId);
//        List<ServiceContract> contractList = com.getOutsourcingServiceContract();
//        Integer total = 0;
//        if(contractList.isEmpty())
//            return total;
//        for(Object o: contractList){
//            ServiceContract curContract = (ServiceContract) o;
//            Integer curCapacity = curContract.getServiceQuotation().getRequiredWarehouseCapacity();
//            total = total + curCapacity;
//        }
//        return total;
//    }
}
