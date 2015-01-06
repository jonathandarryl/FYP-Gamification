/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import CRM.entity.WarehouseOrder;
import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Product;
import TMS.entity.TransOrder;
import TMS.session.TransOrderMgtSessionBeanLocal;
import WMS.entity.Warehouse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.ExceedMaxCapacityException;
import util.exception.InsufficientInventoryException;
import util.exception.KeyClientNotExistException;
import util.exception.LocationNotMatchException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
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
@Stateless
public class ServiceOrderManagement implements ServiceOrderManagementLocal {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private ServiceContractManagementSessionLocal scm;
    @EJB
    private TransOrderMgtSessionBeanLocal tom;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private ServiceInvoiceAndPaymentManagementSessionLocal sipm;
    @EJB
    private ClientInfoMgtSessionBeanLocal cimsb;

    private Timestamp getTimestamp(Integer year, Integer month, Integer day) {
        Calendar time = Calendar.getInstance();
        time.set(year, month - 1, day);
        Timestamp timestamp = new Timestamp(time.getTimeInMillis());
        return timestamp;
    }

    @Override
    public Long createServiceOrder(String partnerCompanyName, String customerCompanyName, Timestamp startTime, Timestamp endTime, Double price,
            String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId,
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Long serviceContractId)
            throws ServiceContractNotExistException, ServiceOrderStartAfterContractExpireException, ServiceOrderStartBeforeContractCommmenceException {

        ServiceContract sc = em.find(ServiceContract.class, serviceContractId);
        Company partnerCompany = sc.getProvider();
        Company customerCompany = sc.getClient();

        if (!partnerCompany.getCompanyName().equals(partnerCompanyName)
                || !customerCompany.getCompanyName().equals(customerCompanyName)) {
            throw new ServiceContractNotExistException("Service Contract " + serviceContractId + "is not between Company " + partnerCompanyName + " and " + customerCompanyName + "!");
        }

        if (startTime.after(sc.getServiceQuotation().getEndDate())) {
            throw new ServiceOrderStartAfterContractExpireException("Service Order Creation Failure. Service Contract " + serviceContractId + " will have been expired when the order starts. Please renew your contract or create a new contract.");
        }

        if (startTime.before(sc.getServiceQuotation().getStartDate())) {
            throw new ServiceOrderStartBeforeContractCommmenceException("Service Order Creation Failure. Service Contract " + serviceContractId + " has not commenced when the order starts. Please renew your contract or create a new contract.");
        }

        Double capacity = (double) sc.getServiceQuotation().getRequiredWarehouseCapacity();
        ServiceOrder so = new ServiceOrder(startTime, endTime, price, description, product, quantity, capacity, sourceLoc, destLoc, warehouseId,
                isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue, sc);
        em.persist(so);
        Long id = so.getId();   //?? id is null somehow
        System.out.println("Inside populate service order: service order Id " + id);
        sc.getServiceOrder().add(so);
        em.merge(sc);
        System.out.println("Service order creation successful!");
        try {
            cimsb.calculateMembershipPoint(partnerCompany.getId(), id); //id is null, exception
        } catch (CompanyNotExistException | KeyClientNotExistException | ServiceOrderNotExistException ex) {
            Logger.getLogger(ServiceOrderManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public Long createServiceOrder(String partnerCompanyName, String customerCompanyName, Timestamp startTime, Timestamp endTime, Double price,
            String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId,
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue,
            Long serviceContractId, Boolean aggregated)
            throws ServiceContractNotExistException, ServiceOrderStartAfterContractExpireException, ServiceOrderStartBeforeContractCommmenceException, InsufficientInventoryException {

        ServiceContract sc = scm.retrieveServiceContract(serviceContractId);
        Company partnerCompany = sc.getProvider();
        Company customerCompany = sc.getClient();

        if (!partnerCompany.getCompanyName().equals(partnerCompanyName)
                || !customerCompany.getCompanyName().equals(customerCompanyName)) {
            throw new ServiceContractNotExistException("Service Contract " + serviceContractId + "is not between Company " + partnerCompanyName + " and " + customerCompanyName + "!");
        }

        if (startTime.after(sc.getServiceQuotation().getEndDate())) {
            throw new ServiceOrderStartAfterContractExpireException("Service Order Creation Failure. Service Contract " + serviceContractId + " will have been expired when the order starts. Please renew your contract or create a new contract.");
        }

        if (startTime.before(sc.getServiceQuotation().getStartDate())) {
            throw new ServiceOrderStartBeforeContractCommmenceException("Service Order Creation Failure. Service Contract " + serviceContractId + " has not commenced when the order starts. Please renew your contract or create a new contract.");
        }
        
        Double capacity = (double) sc.getServiceQuotation().getRequiredWarehouseCapacity();
        ServiceOrder so = new ServiceOrder(startTime, endTime, price, description, product, quantity, capacity, sourceLoc, destLoc, warehouseId,
                isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue, sc, aggregated);
        sc.getServiceOrder().add(so);
        em.persist(so);
        Long id = so.getId();
        em.merge(sc);
        System.out.println("Service order creation successful!");
        try {
            System.out.println(partnerCompany.getId() + " " + id);
            cimsb.calculateMembershipPoint(partnerCompany.getId(), id);
        } catch (CompanyNotExistException | KeyClientNotExistException | ServiceOrderNotExistException ex) {
            Logger.getLogger(ServiceOrderManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public ServiceOrder setSourceWarehouse(ServiceOrder so, Warehouse wh) {
        so.setSourceWarehouse(wh);
        em.merge(so);
        return so;
    }

    @Override
    public void updateServiceOrder(Long soId, Timestamp startTime, Timestamp endTime, Double price,
            String description, Product product, Double quantity, Location sourceLoc, Location destLoc, Long warehouseId,
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws ServiceOrderNotExistException, ServiceOrderStartAfterContractExpireException {

        ServiceOrder so = retrieveServiceOrder(soId);
        Timestamp contractExpireTime = so.getServiceContract().getServiceQuotation().getEndDate();
        if (startTime.after(contractExpireTime)) {
            throw new ServiceOrderStartAfterContractExpireException("Service Order update failure!");
        }

        so.setStartTime(startTime);
        so.setEndTime(endTime);
        so.setPrice(price);
        so.setDescription(description);
        so.setProduct(product);
        so.setQuantity(quantity);
        so.setSourceLoc(sourceLoc);
        so.setDestLoc(destLoc);
        so.setWarehouseId(warehouseId);
        so.setIsToEstablishTransOrder(isToEstablishTransOrder);
        so.setIsToEstablishWarehouseOrder(isToEstablishWarehouseOrder);
        so.setPerishable(perishable);
        so.setFlammable(flammable);
        so.setPharmaceutical(pharmaceutical);
        so.setHighValue(highValue);

        em.merge(so);
        System.out.println("Service order " + soId + " update successful!");
    }

    @Override
    public ServiceOrder retrieveServiceOrder(Long soId)
            throws ServiceOrderNotExistException {
        ServiceOrder so = em.find(ServiceOrder.class, soId);
        if (so == null) {
            throw new ServiceOrderNotExistException("Service Order " + soId + " not exist!");
        }
        return so;
    }

    @Override
    public List<ServiceOrder> retrieveOwningServiceOrderList(String partnerCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Query query = em.createQuery("select s from ServiceOrder s where s.serviceContract.provider=?1");
        query.setParameter(1, ownerCompany);
        List<ServiceOrder> serviceOrderList = new ArrayList(query.getResultList());
        if (serviceOrderList.isEmpty()) {
            throw new ServiceOrderNotExistException("Company " + partnerCompanyName + " does not own any service order!");
        }
        return serviceOrderList;
    }

    @Override
    public List<ServiceOrder> retrieveOwningServiceOrderListWithSpecificClient(String partnerCompanyName, String clientCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select s from ServiceOrder s where s.serviceContract.provider=?1 and s.serviceContract.client=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        List<ServiceOrder> serviceOrderList = new ArrayList(query.getResultList());
        if (serviceOrderList.isEmpty()) {
            throw new ServiceOrderNotExistException("Company " + partnerCompanyName + " does not own any service order!");
        }
        return serviceOrderList;
    }

    @Override
    public List<ServiceOrder> retrieveOutsourcingServiceOrderList(String clientCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select s from ServiceOrder s where s.serviceContract.client=?1");
        query.setParameter(1, ownerCompany);
        List<ServiceOrder> serviceOrderList = new ArrayList(query.getResultList());
        if (serviceOrderList.isEmpty()) {
            throw new ServiceOrderNotExistException("Company " + clientCompanyName + " does not own any service order!");
        }
        return serviceOrderList;
    }

    @Override
    public void approveServiceOrder(Long soId)
            throws ServiceOrderNotExistException, ServiceOrderAlreadyApprovedException {
        ServiceOrder so = retrieveServiceOrder(soId);
        if (so.isApprovedOrNot()) {
            throw new ServiceOrderAlreadyApprovedException("Service Order " + soId + " has already been approved!");
        }
        so.setApprovedOrNot(Boolean.TRUE);
        sipm.createServiceInvoice(soId);
        em.merge(so);
    }

    @Override
    public void approveServiceOrder(ServiceOrder so)
            throws ServiceOrderNotExistException, ServiceOrderAlreadyApprovedException {
        if (so.isApprovedOrNot()) {
            throw new ServiceOrderAlreadyApprovedException("Service Order " + so.getId() + " has already been approved!");
        }
        so.setApprovedOrNot(Boolean.TRUE);
        sipm.createServiceInvoice(so);
        em.merge(so);
    }

    @Override
    public Long createTransportationOrder(Long soId)
            throws WarehouseNotExistException, ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedTransOrderException, ServiceOrderNotPaidException, TransOrderNotExistException {
        ServiceOrder so = retrieveServiceOrder(soId);
        if (!so.isApprovedOrNot()) {
            throw new ServiceOrderNotApprovedException("Creation failed. Service Order " + soId + " has not been approved yet!");
        }
        if (so.getEstablishedTransOrderOrNot()) {
            throw new ServiceOrderAlreadyEstablishedTransOrderException("Creation failed. Service Order " + soId + " has already been associate with a transportation order!");
        }
        if (!so.isPaidOrNot()) {
            throw new ServiceOrderNotPaidException("Creation failed. Service Order " + soId + " has not been paid yet!");
        }
        so.setEstablishedTransOrderOrNot(Boolean.TRUE);
        em.merge(so);
        return tom.createTransOrder(so);
    }

    @Override
    public void updateTransportationOrder(Long transOrderId, Integer startYear, Integer startMonth, Integer startDay,
            Integer endYear, Integer endMonth, Integer endDay, Double price,
            String description, Product product, Double quantity, Location sourceLoc, Location destLoc,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws ServiceOrderNotExistException, TransOrderNotExistException, CompanyNotExistException {
        TransOrder to = tom.retrieveTransOrder(transOrderId);
        ServiceOrder so = to.getServiceOrder();
        Long soId = so.getId();
        Long warehouseId = so.getWarehouseId();

//        updateServiceOrder(soId, startYear, startMonth, startDay, endYear, endMonth, endDay, price, description, product, quantity, sourceLoc, destLoc, 
//                warehouseId, perishable, flammable, pharmaceutical, highValue);
//        
        System.out.println("Transportation order " + transOrderId + " has been updated successfully. So does its related service order.");
    }

    @Override
    public void updateTransportationOrder(Long transOrderId, Timestamp startTime, Timestamp endTime,
            Double price, String description, Product product, Double quantity, Location sourceLoc, Location destLoc,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws ServiceOrderNotExistException, TransOrderNotExistException, CompanyNotExistException, ServiceOrderStartAfterContractExpireException {
        TransOrder to = retrieveTransOrder(transOrderId);
        ServiceOrder so = to.getServiceOrder();
        Long soId = so.getId();
        Long warehouseId = so.getWarehouseId();
        Boolean isToEstablishTransOrder = so.getIsToEstablishTransOrder();
        Boolean isToEstablishWarehouseOrder = so.getIsToEstablishWarehouseOrder();

        updateServiceOrder(soId, startTime, endTime, price, description, product, quantity, sourceLoc, destLoc,
                warehouseId, isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue);

        System.out.println("Transportation order " + transOrderId + " has been updated successfully. So does its related service order.");
    }

    @Override
    public void fulfillTransOrder(Long transOrderId)
            throws TransOrderNotExistException, ServiceOrderAlreadyFulfilledException {
        TransOrder to = tom.retrieveTransOrder(transOrderId);
        ServiceOrder so = to.getServiceOrder();
        if (so.isFulfilledOrNot()) {
            throw new ServiceOrderAlreadyFulfilledException("Transportation Order " + transOrderId + " has already been fulfilled!");
        }
        so.setFulfilledOrNot(Boolean.TRUE);
        to.setFulfilledOrNot(Boolean.TRUE);
        em.merge(so);
    }

    @Override
    public TransOrder retrieveTransOrder(Long toId)
            throws TransOrderNotExistException {
        TransOrder to = em.find(TransOrder.class, toId);
        if (to == null) {
            throw new TransOrderNotExistException("TransOrder " + toId + " not found!");
        }
        return to;
    }

    @Override
    public List<TransOrder> retrieveOwningTransOrderList(String partnerCompanyName)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Query query = em.createQuery("select t from TransOrder t where t.serviceOrder.serviceContract.provider=?1");
        query.setParameter(1, ownerCompany);
        List<TransOrder> transOrderList = new ArrayList(query.getResultList());
        if (transOrderList.isEmpty()) {
            throw new TransOrderNotExistException("Company " + partnerCompanyName + " does not own any transportation order!");
        }
        return transOrderList;
    }

    @Override
    public List<TransOrder> retrieveTransOrderListWithSpecificCompany(String partnerCompanyName, String clientCompanyName)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select t from TransOrder t where t.serviceOrder.serviceContract..provider=?1 and t.serviceOrder.serviceContract.client=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        List<TransOrder> transOrderList = new ArrayList(query.getResultList());
        if (transOrderList.isEmpty()) {
            throw new TransOrderNotExistException("Company " + partnerCompanyName + " does not own any transportation order!");
        }
        return transOrderList;
    }

    @Override
    public List<TransOrder> retrieveOutsourcingTransOrderList(String clientCompanyName)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select t from TransOrder t where t.serviceOrder.serviceContract.client=?1");
        query.setParameter(1, clientCompany);
        List<TransOrder> transOrderList = new ArrayList(query.getResultList());
        if (transOrderList.isEmpty()) {
            throw new TransOrderNotExistException("Company " + clientCompanyName + " does not outsource any transportation order!");
        }
        return transOrderList;
    }

    @Override
    public List<ServiceOrder> retrieveServiceOrderListForTransOrderCreation(String partnerCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Query query = em.createQuery("select s from ServiceOrder s where s.serviceContract.provider=?1 and s.approvedOrNot=TRUE and s.establishedTransOrderOrNot=FALSE and s.isToEstablishTransOrder=TRUE and s.isToOutsource=FALSE and s.paidOrNot=TRUE");
        query.setParameter(1, ownerCompany);
        @SuppressWarnings("unchecked")
        List<ServiceOrder> serviceOrderList = new ArrayList<>(query.getResultList());
        if (serviceOrderList.isEmpty()) {
            throw new ServiceOrderNotExistException("Company " + partnerCompanyName + " does not own any service order suitable for transOrder creation!");
        }
        return serviceOrderList;
    }

    @Override
    public void createWarehouseOrder(Long soId)
            throws ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedWarehouseOrderException, ServiceOrderNotPaidException {
        ServiceOrder so = retrieveServiceOrder(soId);
        WarehouseOrder wo = new WarehouseOrder(so);
        if (!so.isApprovedOrNot()) {
            throw new ServiceOrderNotApprovedException("Creation failed. Service Order " + soId + " has not been approved yet!");
        }
        if (so.getEstablishedWarehouseOrderOrNot()) {
            throw new ServiceOrderAlreadyEstablishedWarehouseOrderException("Creation failed. Service Order " + soId + " has already been associate with a warehouse order!");
        }
        if (!so.isPaidOrNot()) {
            throw new ServiceOrderNotPaidException("Creation failed. Service Order " + soId + " has not been paid yet!");
        }
        Location source = new Location("N.A.", "N.A.", "N.A.", "N.A.", "N.A.", "N.A.");
        so.setSourceLoc(source);
        so.setEstablishedTransOrderOrNot(Boolean.TRUE);
        em.persist(wo);
        so.setWarehouseOrder(wo);
        em.merge(so);
        System.out.println("Warehouse order creation successful for service order " + soId);
    }

    @Override
    public void updateWarehouseOrder(Long woId, Timestamp startTime, Timestamp endTime,
            Double price, String description, Product product, Double quantity, Long warehouseId,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws ServiceOrderNotExistException, WarehouseOrderNotExistException, ServiceOrderStartAfterContractExpireException {
        WarehouseOrder wo = retrieveWarehouseOrder(woId);
        Long soId = wo.getId();
        ServiceOrder so = retrieveServiceOrder(soId);
        Location sourceLoc = so.getSourceLoc();
        Location destLoc = so.getDestLoc();
        Boolean isToEstablishTransOrder = so.getIsToEstablishTransOrder();
        Boolean isToEstablishWarehouseOrder = so.getIsToEstablishWarehouseOrder();

        updateServiceOrder(soId, startTime, endTime, price, description, product, quantity, sourceLoc, destLoc,
                warehouseId, isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue);

        System.out.println("Warehouse order " + woId + " has been updated successfully. So does its related service order.");
    }

    @Override
    public void fulfillWarehouseOrder(Long woId)
            throws WarehouseOrderNotExistException, ServiceOrderAlreadyFulfilledException {
        WarehouseOrder wo = retrieveWarehouseOrder(woId);
        ServiceOrder so = wo.getServiceOrder();
        if (so.isFulfilledOrNot()) {
            throw new ServiceOrderAlreadyFulfilledException("Warehouse Order " + woId + " has already been fulfilled!");
        }
        so.setFulfilledOrNot(Boolean.TRUE);
        wo.setFulfilledOrNot(Boolean.TRUE);
        em.merge(so);
        em.merge(wo);
        System.out.println("Warehouse order " + woId + " has been fulfilled successfully.");
    }

    @Override
    public WarehouseOrder retrieveWarehouseOrder(Long woId)
            throws WarehouseOrderNotExistException {
        WarehouseOrder wo = em.find(WarehouseOrder.class, woId);
        if (wo == null) {
            throw new WarehouseOrderNotExistException("Warehouse order " + woId + " not found!");
        }
        return wo;
    }

    @Override
    public List<WarehouseOrder> retrieveOwningWarehouseOrderList(String partnerCompanyName)
            throws WarehouseOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Query query = em.createQuery("select t from WarehouseOrder t where t.serviceOrder.serviceContract.provider=?1");
        query.setParameter(1, ownerCompany);
        List<WarehouseOrder> warehouseOrderList = new ArrayList(query.getResultList());
        if (warehouseOrderList.isEmpty()) {
            throw new WarehouseOrderNotExistException("Company " + partnerCompanyName + " does not own any warehouse order!");
        }
        return warehouseOrderList;
    }

    @Override
    public List<WarehouseOrder> retrieveOutsourcingWarehouseOrderList(String clientCompanyName)
            throws WarehouseOrderNotExistException, CompanyNotExistException {
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select t from WarehouseOrder t where t.serviceOrder.serviceContract.client=?1");
        query.setParameter(1, clientCompany);
        List<WarehouseOrder> warehouseOrderList = new ArrayList(query.getResultList());
        if (warehouseOrderList.isEmpty()) {
            throw new WarehouseOrderNotExistException("Company " + clientCompanyName + " does not outsource any warehouse order!");
        }
        return warehouseOrderList;
    }

    @Override
    public List<WarehouseOrder> retrieveWarehouseOrderListWithSpecificCompany(String partnerCompanyName, String clientCompanyName)
            throws WarehouseOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select t from WarehouseOrder t where t.serviceOrder.serviceContract.provider=?1 and t.serviceOrder.serviceContract.client=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        List<WarehouseOrder> warehouseOrderList = new ArrayList(query.getResultList());
        if (warehouseOrderList.isEmpty()) {
            throw new WarehouseOrderNotExistException("Company " + clientCompanyName + " does not outsource any warehouse order!");
        }
        return warehouseOrderList;
    }

    @Override
    public List<ServiceOrder> retrieveServiceOrderListForWarehouseOrderCreation(String partnerCompanyName)
            throws ServiceOrderNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(partnerCompanyName);
        Query query = em.createQuery("select s from ServiceOrder s where s.serviceContract.provider=?1 and s.approvedOrNot=TRUE and s.establishedWarehouseOrderOrNot=FALSE and s.isToOutsource=FALSE and s.paidOrNot=TRUE");
        query.setParameter(1, ownerCompany);
        List<ServiceOrder> serviceOrderList = new ArrayList(query.getResultList());
        if (serviceOrderList.isEmpty()) {
            throw new ServiceOrderNotExistException("Company " + partnerCompanyName + " does not own any service order suitable for warehouseOrder creation!");
        }
        return serviceOrderList;
    }
    
    @Override
    public ServiceOrder createOutsourceServiceOrder(ServiceOrder so, ServiceContract sc, String description) 
            throws ServiceOrderNotApprovedException, ServiceOrderNotPaidException, ServiceOrderAlreadyOutsourcedException, LocationNotMatchException, ExceedMaxCapacityException{
        if(!so.isApprovedOrNot())
            throw new ServiceOrderNotApprovedException("Service Order "+so.getId()+" has not been approved yet! Cannot be outsourced!");
        if(!so.isPaidOrNot())
            throw new ServiceOrderNotPaidException("Service Order "+so.getId()+" has not been paid yet! Cannot be outsourced!");
        if(so.getIsToOutsource())
            throw new ServiceOrderAlreadyOutsourcedException("Service Order "+so.getId()+" has not already been outsourced! Cannot be outsourced!");
        if(!so.getSourceLoc().getCity().equals(sc.getServiceQuotation().getSource().getCity()))
            throw new LocationNotMatchException("Source location of Service Contract "+sc.getId()+" is not "+so.getSourceLoc().getCity()+"! Cannot be outsourced!");
        if(!so.getDestLoc().getCity().equals(sc.getServiceQuotation().getDest().getCity()))
            throw new LocationNotMatchException("Destination location of Service Contract "+sc.getId()+" is not "+so.getDestLoc().getCity()+"! Cannot be outsourced!");
        if(so.getCapacity() > (sc.getServiceQuotation().getRequiredWarehouseCapacity()*1.0))
            throw new ExceedMaxCapacityException("Maxinum logistic capacity of Service Contract "+sc.getId()+" cannot match the requirement of Service Order "+so.getId()+"! Cannot be outsourced!");
        
        Timestamp startTime = so.getStartTime();
        Timestamp endTime = so.getEndTime();
        Product product = so.getProduct();
        Double quantity = so.getQuantity();
        Double capacity = so.getCapacity();        
        Location sourceLoc = so.getSourceLoc();
        Location destLoc = so.getDestLoc();
        Long warehouseId = so.getWarehouseId();
        Boolean isToEstablishTransOrder = so.getIsToEstablishTransOrder();
        Boolean isToEstablishWarehouseOrder = so.getIsToEstablishWarehouseOrder();
        Boolean perishable = so.isPerishable();
        Boolean flammable = so.isFlammable();
        Boolean pharmaceutical = so.isPharmaceutical();
        Double price = sc.getServiceQuotation().getTotalPrice();
        Boolean highValue = so.isHighValue(); 
        Boolean aggregated = so.getAggregated();
        Boolean isFromOutsourcing = Boolean.TRUE;
        Long fromServiceOrderId = so.getId();
        Warehouse sourceWarehouse = so.getSourceWarehouse();
        
        ServiceOrder outSo = new ServiceOrder(startTime, endTime, price, description, product, quantity, capacity, sourceLoc, destLoc, warehouseId,
                isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue, sc, aggregated, 
                isFromOutsourcing, fromServiceOrderId, sourceWarehouse);
        em.persist(outSo);
        so.setToServiceOrderId(outSo.getId());
        so.setIsToOutsource(Boolean.TRUE);
        em.merge(so);
        sc.getServiceOrder().add(outSo);
        em.merge(sc);
        
        return outSo;
    }
    
    
}
