/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceInvoice;
import CRM.entity.ServiceOrder;
import CRM.entity.ServicePayment;
import CRM.entity.ServicePaymentReceipt;
import Common.entity.Company;
import Common.entity.PartnerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
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
import util.exception.ServiceInvoiceAlreadyPaidException;
import util.exception.ServiceInvoiceNotExistException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.ServicePaymentAlreadySentException;
import util.exception.ServicePaymentNotExistException;
import util.exception.ServicePaymentReceiptNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class ServiceInvoiceAndPaymentManagementSession implements ServiceInvoiceAndPaymentManagementSessionLocal {
    @PersistenceContext
    private EntityManager em;
    @EJB
    private ServiceOrderManagementLocal som;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    
    @Override
    public void createServiceInvoice(Long soId)
        throws ServiceOrderNotExistException{
        ServiceOrder so = som.retrieveServiceOrder(soId);
        ServiceInvoice si = new ServiceInvoice(so);
        so.setServiceInvoice(si);
        em.merge(so);
        em.persist(si);
        System.out.println("Service invoice for Service order "+soId+" has been created.");
    }
    
    @Override
    public void createServiceInvoice(ServiceOrder so)
        throws ServiceOrderNotExistException{
        ServiceInvoice si = new ServiceInvoice(so);
        so.setServiceInvoice(si);
        em.merge(so);
        em.persist(si);
        System.out.println("Service invoice for Service order "+so.getId()+" has been created.");
    }
    
    @Override
    public ServiceInvoice retrieveServiceInvoice(Long siId)
        throws ServiceInvoiceNotExistException{
        ServiceInvoice si = em.find(ServiceInvoice.class, siId);
        if(si==null)
            throw new ServiceInvoiceNotExistException("Service Invoice "+siId+" not found!");
        return si;
    }
    
    @Override
    public List<ServiceInvoice> retrieveOutgoingServiceInvoiceList(String ownerCompanyName) 
            throws CompanyNotExistException, ServiceInvoiceNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceInvoice s where s.serviceOrder.serviceContract.serviceQuotation.pCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServiceInvoice> serviceInvoiceList = new ArrayList(query.getResultList());
        if(serviceInvoiceList.isEmpty())
            throw new ServiceInvoiceNotExistException("Company "+ownerCompanyName+" has not issued any invoice!");
        return serviceInvoiceList;
    }
    
    @Override
    public List<ServiceInvoice> retrieveIncomingServiceInvoiceList(String ownerCompanyName) 
            throws CompanyNotExistException, ServiceInvoiceNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServiceInvoice s where s.serviceOrder.serviceContract.serviceQuotation.clientCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServiceInvoice> serviceInvoiceList = new ArrayList(query.getResultList());
        if(serviceInvoiceList.isEmpty())
            throw new ServiceInvoiceNotExistException("Company "+ownerCompanyName+" has not received any incoming invoice!");
        return serviceInvoiceList;
    }
    
    @Override
    public void createServicePayment(Long serviceInvoiceId)
        throws ServiceInvoiceNotExistException, ServiceInvoiceAlreadyPaidException{
        ServiceInvoice si = retrieveServiceInvoice(serviceInvoiceId);
        ServicePayment sp = new ServicePayment(si);
        ServiceOrder so = si.getServiceOrder();
        if(si.isPaidOrNot())
            throw new ServiceInvoiceAlreadyPaidException("Payment failed. Invoice "+serviceInvoiceId+" has already been paid!");
        si.setPaidOrNot(Boolean.TRUE);
        so.setPaidOrNot(Boolean.TRUE);
        si.setServicePayment(sp);
        sp.setDiscount(calculateDiscount(si, sp));
        em.merge(si);
        em.merge(so);
        em.persist(sp);        
    }
    
    private Double calculateDiscount(ServiceInvoice si, ServicePayment sp){
        Double discount = 0.0;
        Double total = si.getServiceOrder().getPrice();
        Calendar orderCreationTime = Calendar.getInstance();
        orderCreationTime.setTime(si.getServiceOrder().getCreationTime());
        Calendar paymentTime = Calendar.getInstance();
        paymentTime.setTime(sp.getPaymentTime());
        Integer firstClassDiscountDay = -10;
        Integer secondClassDiscountDay = -20;
        
        paymentTime.add(Calendar.DAY_OF_YEAR, firstClassDiscountDay);
        if(orderCreationTime.after(paymentTime)){
            discount = total*0.03;
            return discount;
        }
        orderCreationTime.setTime(si.getServiceOrder().getCreationTime());
        paymentTime.add(Calendar.DAY_OF_YEAR, secondClassDiscountDay);
        if(orderCreationTime.after(paymentTime)){
            discount = total*0.01;
            return discount;
        }
        
        return discount;
    }
    
    @Override
    public ServicePayment retrieveServicePayment(Long spId)
        throws ServicePaymentNotExistException{
        ServicePayment sp = em.find(ServicePayment.class, spId);
        if(sp==null)
            throw new ServicePaymentNotExistException("Service Pament record "+spId+" not found!");
        return sp;
    }
    
    @Override
    public List<ServicePayment> retrieveOutgoingServicePaymentList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServicePayment s where s.serviceInvoice.serviceOrder.serviceContract.serviceQuotation.clientCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServicePayment> servicePaymentList = new ArrayList(query.getResultList());
        if(servicePaymentList.isEmpty())
            throw new ServicePaymentNotExistException("Company "+ownerCompanyName+" does not has any outgoing payment!");
        return servicePaymentList;
    }
    
    @Override
    public List<ServicePayment> retrieveIncomingServicePaymentList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServicePayment s where s.serviceInvoice.serviceOrder.serviceContract.serviceQuotation.pCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServicePayment> servicePaymentList = new ArrayList(query.getResultList());
        if(servicePaymentList.isEmpty())
            throw new ServicePaymentNotExistException("Company "+ownerCompanyName+" does not has any incoming service payment!");
        return servicePaymentList;
    }
    
    @Override
    public Boolean verifyServicePayment(Long siId)
        throws ServiceInvoiceNotExistException{
        ServiceInvoice si = retrieveServiceInvoice(siId);
        return si.isPaidOrNot();
    }
    
    @Override
    public void createServicePaymentReceipt(Long soId)
        throws ServicePaymentNotExistException, ServiceOrderNotPaidException, ServiceOrderNotExistException, ServicePaymentAlreadySentException{
        ServiceOrder so = som.retrieveServiceOrder(soId);
        Long spId = so.getServiceInvoice().getServicePayment().getId();
        ServicePayment sp = retrieveServicePayment(spId);
        if(!so.isPaidOrNot())
            throw new ServiceOrderNotPaidException("Receipt creation failed. Service Order"+soId+" has not been paid yet!");
        if(so.getServiceInvoice().getServicePayment().getReceipt()!=null)
            throw new ServicePaymentAlreadySentException("Receipt creation failed. Receipt for this service order payment has already been sent!");
        
        ServicePaymentReceipt spr = new ServicePaymentReceipt(sp);
        sp.setReceipt(spr);
        sp.setReceiptIssued(Boolean.TRUE);
        em.merge(sp);
        em.persist(spr);
        System.out.println("Receipt created for Service Payment "+spId);
    }
    
    @Override
    public ServicePaymentReceipt retrieveServicePaymentReceipt(Long sprId)
        throws ServicePaymentReceiptNotExistException{
        ServicePaymentReceipt spr = em.find(ServicePaymentReceipt.class, sprId);
        if(spr == null)
            throw new ServicePaymentReceiptNotExistException("ServicePaymentReceipt "+sprId+" not found!");
        return spr;
    }
    
    @Override
    public List<ServicePaymentReceipt> retrieveOutgoingServicePaymentReceiptList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentReceiptNotExistException{
        PartnerCompany ownerCompany = (PartnerCompany)cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServicePaymentReceipt s where s.servicePayment.serviceInvoice.serviceOrder.serviceContract.serviceQuotation.pCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServicePaymentReceipt> servicePaymentReceiptList = new ArrayList(query.getResultList());
        if(servicePaymentReceiptList.isEmpty())
            throw new ServicePaymentReceiptNotExistException("Company "+ownerCompanyName+" has not issued any service payment receipt!");
        return servicePaymentReceiptList;
    }
    
    @Override
    public List<ServicePaymentReceipt> retrieveIncomingServicePaymentReceiptList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentReceiptNotExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query=em.createQuery("select s from ServicePaymentReceipt s where s.servicePayment.serviceInvoice.serviceOrder.serviceContract.serviceQuotation.clientCompany=?1");
        query.setParameter(1, ownerCompany);
        List<ServicePaymentReceipt> servicePaymentReceiptList = new ArrayList(query.getResultList());
        if(servicePaymentReceiptList.isEmpty())
            throw new ServicePaymentReceiptNotExistException("Company "+ownerCompanyName+" has received any service payment receipt!");
        return servicePaymentReceiptList;
    }
}
