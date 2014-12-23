/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.entity.SalesQuotation;
import WMS.entity.ExtractionRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import util.exception.SalesOrderNotExistException;
import util.exception.SalesQuotationAlreadyEstablishedOrderException;
import util.exception.SalesQuotationNotApprovedException;
import util.exception.SalesQuotationNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class SalesOrderSessionBeanTest {

    SalesOrderSessionBeanRemote sosbr = this.lookupSalesOrderSessionBeanRemote();
    ProductSessionBeanRemote psbr = this.lookupProductSessionBeanRemote();
    
    public SalesOrderSessionBeanTest() {
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
    public void testCreateSalesQuotation() throws Exception {
        Product p = psbr.retrieveProduct("company1", "iPhone cases");
        List<Product> pl = new ArrayList();
        pl.add(p);
        HashMap<Product, Double> plm = new HashMap();
        plm.put(p, 20.0);
        String customerName = "SMU";
        String ownerCompanyName = "company1";
        SalesQuotation result = sosbr.createSalesQuotation(pl, plm, customerName, ownerCompanyName);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveSalesQuotation() throws Exception {
        ArrayList<SalesQuotation> sql = sosbr.retrieveSalesQuotationList("company1");
        SalesQuotation sq = sql.get(0);
        Long sqId = sq.getId();
        SalesQuotation result = sosbr.retrieveSalesQuotation(sqId);
        assertNotNull(result);
    }
    
    @Test(expected = SalesQuotationAlreadyEstablishedOrderException.class)
    public void testCreateSalesOrder() throws SalesQuotationNotExistException, SalesQuotationNotApprovedException, SalesQuotationAlreadyEstablishedOrderException{
        ArrayList<SalesQuotation> sql = sosbr.retrieveSalesQuotationList("company1");
        SalesQuotation sq = sql.get(0);
        Long sqId = sq.getId();
        SalesOrder so = sosbr.createSalesOrder(sqId);
        assertNotNull(so);
    }
    
    @Test
    public void testRetrieveSalesOrderList() throws SalesOrderNotExistException {
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> result = sosbr.retrieveSalesOrderList(start, end, companyName);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveSalesOrder() throws Exception {
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> sol = sosbr.retrieveSalesOrderList(start, end, companyName);
        Long soId = sol.get(0).getId();
        SalesOrder result = sosbr.retrieveSalesOrder(soId);
        assertNotNull(result);
    }
    
    @Test
    public void testMarkBackOrder() throws Exception{
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> sol = sosbr.retrieveSalesOrderList(start, end, companyName);
        Long Id = null;
        for (int i=0;i<sol.size();i++) {
            if (sol.get(i).isIsBackOrder()==false) {
                Id = sol.get(i).getId();
                break;
            }
        }
        sosbr.markBackOrder(Id);
        boolean result = sosbr.retrieveSalesOrder(Id).isIsBackOrder();
        assertTrue(result);
    }
    
    @Test(expected = SalesOrderNotExistException.class)
    public void testFulfilSalesOrder() throws SalesOrderNotExistException {
        SalesOrder result = sosbr.retrieveSalesOrder(Long.valueOf("1"));
        assertNull(result);
    }
    
    @Test
    public void testRetrieveExtrationRequest() throws Exception {
        Product p = psbr.retrieveProduct("company1", "iPhone cases");
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> sol = sosbr.retrieveSalesOrderList(start, end, companyName);
        SalesOrder so = sol.get(0);
        List<ExtractionRequest> result = null;
        assertNull(result);
    }
    
    @Test
    public void testFulfilledExtractionRequest() throws Exception{
        Product p = psbr.retrieveProduct("company1", "iPhone cases");
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        String companyName = "company1";
        ArrayList<SalesOrder> sol = sosbr.retrieveSalesOrderList(start, end, companyName);
        SalesOrder so = sol.get(0);
        List<ExtractionRequest> result = null;
        assertNull(result);
    }
    
    private SalesOrderSessionBeanRemote lookupSalesOrderSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (SalesOrderSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/SalesOrderManagement!OES.session.SalesOrderSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
    private ProductSessionBeanRemote lookupProductSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ProductSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/ProductInfoManagement!OES.session.ProductSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}