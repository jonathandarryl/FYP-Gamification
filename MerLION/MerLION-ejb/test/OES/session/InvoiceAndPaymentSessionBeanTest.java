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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.InvoiceNotExistException;
import util.exception.ReceiptNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class InvoiceAndPaymentSessionBeanTest {

    InvoiceAndPaymentSessionBeanRemote ipsbr = this.lookupInvoiceAndPaymentSessionBeanRemote();
    SalesOrderSessionBeanRemote sosbr = this.lookupSalesOrderSessionBeanRemote();
    
    public InvoiceAndPaymentSessionBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testCreateInvoice() throws Exception{
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> sol = sosbr.retrieveSalesOrderList(start, end, companyName);
        Long soId = sol.get(9).getId();
        Location loc = new Location();
        Invoice result = ipsbr.createInvoice(soId, loc);
        assertNotNull(result);
    }
    
    @Test(expected = InvoiceNotExistException.class)
    public void testRetrieveInvoiceList() throws InvoiceNotExistException  {
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2013-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<Invoice> result = ipsbr.retrieveInvoiceList(start, end, companyName);
        assertNull(result);
    }

    @Test(expected = ReceiptNotExistException.class)
    public void testRetrieveReceiptList() throws ReceiptNotExistException {
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2013-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<Receipt> result = ipsbr.retrieveReceiptList(start, end, companyName);
        assertNull(result);
    }
    
    @Test
    public void testRetrieveAllInvoice() throws Exception {
        String companyName = "company1";
        ArrayList<Invoice> result = null;
        assertNull(result);
    }
    
    @Test
    public void testRetrieveAllReceipt() throws Exception {
        String companyName = "company1";
        ArrayList<Receipt> result = null;
        assertNull(result);
    }
    
    private InvoiceAndPaymentSessionBeanRemote lookupInvoiceAndPaymentSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (InvoiceAndPaymentSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/InvoiceAndPaymentManagement!OES.session.InvoiceAndPaymentSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
    private SalesOrderSessionBeanRemote lookupSalesOrderSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (SalesOrderSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/SalesOrderManagement!OES.session.SalesOrderSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}