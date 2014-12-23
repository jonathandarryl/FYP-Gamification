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
import java.util.List;
import javax.ejb.Local;
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
@Local
public interface ServiceInvoiceAndPaymentManagementSessionLocal {
    //B.5.b.1
    public void createServiceInvoice(Long soId) 
            throws ServiceOrderNotExistException;
    //B.5.b.2
    public ServiceInvoice retrieveServiceInvoice(Long siId) 
            throws ServiceInvoiceNotExistException;
    
    public List<ServiceInvoice> retrieveOutgoingServiceInvoiceList(String ownerCompanyName)
            throws CompanyNotExistException, ServiceInvoiceNotExistException;

    public List<ServiceInvoice> retrieveIncomingServiceInvoiceList(String ownerCompanyName) 
            throws CompanyNotExistException, ServiceInvoiceNotExistException;
    
    //B.5.b.3
    public void createServicePayment(Long serviceInvoiceId) 
            throws ServiceInvoiceNotExistException, ServiceInvoiceAlreadyPaidException;
    //B.5.b.4
    public ServicePayment retrieveServicePayment(Long spId) 
            throws ServicePaymentNotExistException;
    
    public List<ServicePayment> retrieveOutgoingServicePaymentList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentNotExistException;

    public List<ServicePayment> retrieveIncomingServicePaymentList(String ownerCompanyName)
            throws CompanyNotExistException, ServicePaymentNotExistException;

    //B.5.b.5
    public Boolean verifyServicePayment(Long siId) 
            throws ServiceInvoiceNotExistException;
    //B.5.b.6
    public void createServicePaymentReceipt(Long spId) 
            throws ServicePaymentNotExistException, ServiceOrderNotPaidException, ServiceOrderNotExistException, ServicePaymentAlreadySentException;
    //B.5.b.7
    public ServicePaymentReceipt retrieveServicePaymentReceipt(Long sprId) 
            throws ServicePaymentReceiptNotExistException;

    public List<ServicePaymentReceipt> retrieveOutgoingServicePaymentReceiptList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentReceiptNotExistException;

    public List<ServicePaymentReceipt> retrieveIncomingServicePaymentReceiptList(String ownerCompanyName) 
            throws CompanyNotExistException, ServicePaymentReceiptNotExistException;

    public void createServiceInvoice(ServiceOrder so) throws ServiceOrderNotExistException;

    
}
