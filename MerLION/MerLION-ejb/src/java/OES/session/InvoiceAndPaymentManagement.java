/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.Location;
import OES.entity.Invoice;
import OES.entity.Receipt;
import OES.entity.SalesOrder;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvoiceNotExistException;
import util.exception.ReceiptNotExistException;
import util.exception.SalesOrderNotBeenFulfilledException;
import util.exception.SalesOrderNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class InvoiceAndPaymentManagement implements InvoiceAndPaymentManagementLocal,InvoiceAndPaymentSessionBeanRemote {

    @PersistenceContext
    EntityManager em;
    @EJB
    private SalesOrderManagementLocal som;

    @Override
    public Invoice createInvoice(Long soId, Location loc) throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException {
        SalesOrder so = som.retrieveSalesOrder(soId);

        if (!so.isIsFulfilled()) {
            throw new SalesOrderNotBeenFulfilledException("Invoice creation failed. Sales Order " + soId + " has not been fulfilled yet.");
        }
        Invoice invoice = new Invoice(so, loc);
        em.persist(invoice);
        System.out.println("Invoice " + invoice.getId() + " has been successfully created for Sales Order " + soId);
        return invoice;
    }

//    @Override
//    public String updateInvoiceSalesOrder(Long inId, Long soId) {
//        SalesOrder so = retrieveSalesOrder(soId);
//        Invoice invoice = retrieveInvoice(inId);
//        if(so==null)
//            return "Invoice creation failed. Sales Order "+soId+" not found";
//        else if(invoice==null)
//            return "Update failed. Invoice "+inId+"not found";
//        else{
//            if(so.isArchivedOrNot())
//                return "Invoice update failed. Sales Order "+soId+" has been archived.";
//            else if(!so.isIsFulfilled())
//                return "Invoice update failed. Sales Order "+soId+" has not been fulfilled yet.";
//            else{   
//                invoice.setSalesOrder(so);
//                em.persist(invoice);
//                return "Invoice "+invoice.getId()+" has been successfully updated to bond with the new Sales Order "+soId;
//            }
//        }
//            
//    }
//    @Override
//    public String updateInvoiceDest(Long inId, ArrayList<String> locInfo) {
//        String country = locInfo.get(0);
//        String state = locInfo.get(1);
//        String city = locInfo.get(2);
//        String street = locInfo.get(3);
//        String blockNo = locInfo.get(4);
//        String postalCode = locInfo.get(5);
//        Location loc = new Location(country, state, city, street, blockNo, postalCode);
//        Invoice invoice = retrieveInvoice(inId);
//        if(invoice!=null){
//            invoice.setWarehouseLoc(loc);
//            em.persist(invoice);
//            return "New destination location in Invoice "+invoice.getId()+" has been successfully updated";
//        }
//        else
//            return "Update failed. Invoice "+inId+"not found";
//    }
    @Override
    public Invoice retrieveInvoice(Long inId) throws InvoiceNotExistException {
        Invoice invoice = em.find(Invoice.class, inId);
        if (invoice == null) {
            throw new InvoiceNotExistException("Invoice " + inId + "does not exist!");
        }
        return invoice;
    }

    @Override
    public ArrayList<Invoice> retrieveInvoiceList(Timestamp startDate, Timestamp endDate, String companyName) throws InvoiceNotExistException {
        System.out.println("enter retrieveInvoiceList session bean: companyName " + companyName);

        Query query = em.createQuery("select i from Invoice i where i.invoiceDate between ?1 and ?2 and i.ownerCompanyName=?3");
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, companyName);
        ArrayList<Invoice> invoiceList = new ArrayList(query.getResultList());
        if (invoiceList.isEmpty()) {
            throw new InvoiceNotExistException("Invoice under Company " + companyName + " does not exist!");
        }
        return invoiceList;
    }
    
    @Override
    public ArrayList<Invoice> retrieveAllInvoiceList(String companyName) throws InvoiceNotExistException {
        Query query = em.createQuery("select i from Invoice i where i.ownerCompanyName=?3");
        query.setParameter(3, companyName);
        ArrayList<Invoice> invoiceList = new ArrayList(query.getResultList());
        if (invoiceList.isEmpty()) {
            throw new InvoiceNotExistException("Invoice under Company " + companyName + " does not exist!");
        }
        return invoiceList;
    }
    
    @Override
    public ArrayList<Receipt> retrieveAllReceiptList(String companyName) throws ReceiptNotExistException {
        Query query = em.createQuery("select i from Receipt i where i.ownerComapanyName=?3");
        query.setParameter(3, companyName);
        ArrayList<Receipt> receiptList = new ArrayList(query.getResultList());
        if (receiptList.isEmpty()) {
            throw new ReceiptNotExistException("Receipt under Company " + companyName + " does not exist!");
        }
        return receiptList;
    } 

    @Override
    public void createReceipt(Long inId) throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException, InvoiceNotExistException {
        Invoice in=retrieveInvoice(inId);

        if (!in.getSalesOrder().isIsFulfilled()) {
            throw new SalesOrderNotBeenFulfilledException("Receipt creation failed. Sales Order " + in.getSalesOrder().getId() + " has not been fulfilled yet.");
        }
        Receipt receipt = new Receipt(in);
        em.persist(receipt);
        System.out.println("Receipt " + receipt.getId() + " has been successfully created for Invoice " + inId);
    }

//    @Override
//    public String updateReceipt(Long reId, Long soId) {
//        SalesOrder so = retrieveSalesOrder(soId);
//        Receipt receipt = retrieveReceipt(reId);
//        if(so==null)
//            return "Receipt update failed. Sales Order "+soId+" not found";
//        else if(receipt==null)
//            return "Update failed. Receipt "+soId+"not found";
//        else{
//            if(so.isArchivedOrNot())
//                return "Receipt update failed. Sales Order "+soId+" has been archived.";
//            else if(!so.isIsFulfilled())
//                return "Receipt update failed. Sales Order "+soId+" has not been fulfilled yet.";
//            else{  
//                receipt.setSalesOrder(so);
//                em.persist(receipt);
//                return "Receipt "+receipt.getId()+" has been successfully updated to bond with the new Sales Order "+soId;
//            }
//        }
//         
//    }
    @Override
    public Receipt retrieveReceipt(Long reId) throws ReceiptNotExistException {
        Receipt receipt = em.find(Receipt.class, reId);
        if (receipt == null) {
            throw new ReceiptNotExistException("Receipt " + reId + " does not exist");
        }
        return receipt;

    }

    @Override
    public ArrayList<Receipt> retrieveReceiptList(Timestamp startDate, Timestamp endDate, String companyName) throws ReceiptNotExistException {
        Query query = em.createQuery("select i from Receipt i where i.receiptDate between ?1 and ?2 and i.ownerComapanyName=?3");
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, companyName);
        ArrayList<Receipt> receiptList = new ArrayList(query.getResultList());
        if (receiptList.isEmpty()) {
            throw new ReceiptNotExistException("Receipt under Company" + companyName + " does not exist");
        }
        return receiptList;
    }

//    public SalesOrder retrieveSalesOrder(Long soId) {
//        Query query=em.createQuery("select s from SalesOrder s where s.id=?1");
//        query.setParameter(1, soId);
//        ArrayList <SalesOrder> salesOrderList=new ArrayList(query.getResultList());
//       
//        if(!salesOrderList.isEmpty()){
//            SalesOrder salesOrder = salesOrderList.get(0);         
//            return salesOrder;
//        }        
//        else{
//            return null;
//        }
//    }
    @Override
    public void confirmPayment(Long inId, String bNo) throws InvoiceNotExistException {
        Query query = em.createQuery("select i from Invoice i where i.id=?1");
        query.setParameter(1, inId);
        ArrayList<Invoice> invoiceList = new ArrayList(query.getResultList());
        if (invoiceList.isEmpty()) {
            throw new InvoiceNotExistException("Invoice " + inId + "does not exist!");
        }
        Invoice invoice = invoiceList.get(0);
        invoice.setPaidOrNot(Boolean.TRUE);
        invoice.setBillingNo(bNo);
        em.merge(invoice);
    }

    @Override
    public void sendInvoice(Long inId) throws InvoiceNotExistException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendReceipt(Long reId) throws ReceiptNotExistException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
