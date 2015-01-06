/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.session;

import Common.entity.Location;
import OES.entity.Invoice;
import OES.entity.Receipt;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.ejb.Remote;
import util.exception.InvoiceNotExistException;
import util.exception.ReceiptNotExistException;
import util.exception.SalesOrderNotBeenFulfilledException;
import util.exception.SalesOrderNotExistException;

/**
 *
 * @author songhan
 */
@Remote
public interface InvoiceAndPaymentSessionBeanRemote {
    public Invoice createInvoice(Long soId, Location loc)
            throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException;
    
    //public String updateInvoiceSalesOrder(Long inId, Long soId);
    
    public void sendInvoice(Long inId)
            throws InvoiceNotExistException;

//    public String updateInvoiceDest(Long inId, ArrayList<String> locInfo);
    
    public Invoice retrieveInvoice(Long inId)
            throws InvoiceNotExistException;
    
    public ArrayList<Invoice> retrieveInvoiceList(Timestamp startDate, Timestamp endDate, String companyName)
            throws InvoiceNotExistException;
    
    public void createReceipt(Long soId)
            throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException, InvoiceNotExistException;
    
//public String updateReceipt(Long reId, Long soId);

    public void sendReceipt(Long reId)
            throws ReceiptNotExistException;
    
    public Receipt retrieveReceipt(Long reId)
            throws ReceiptNotExistException;
    
    public void confirmPayment(Long inId, String bNo) 
            throws InvoiceNotExistException;
    
    public ArrayList<Receipt> retrieveReceiptList(Timestamp startDate, Timestamp endDate, String companyName)
            throws ReceiptNotExistException;

    public ArrayList<Invoice> retrieveAllInvoiceList(String companyName) throws InvoiceNotExistException;
    
    public ArrayList<Receipt> retrieveAllReceiptList(String companyName) throws ReceiptNotExistException;
}
