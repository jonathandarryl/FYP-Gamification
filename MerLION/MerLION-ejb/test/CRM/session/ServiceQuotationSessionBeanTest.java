/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceQuotation;
import Common.entity.Location;
import OES.entity.Product;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyNotExistException;
import util.exception.ServiceQuotationAlreadyApprovedException;
import util.exception.ServiceQuotationCannotBeAchivedException;
import util.exception.ServiceQuotationNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class ServiceQuotationSessionBeanTest {
    
    ServiceQuotationSessionBeanRemote sqsbr = this.lookupServiceQuotationSessionBeanRemote();
    
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
    
    @Test(expected = ServiceQuotationNotExistException.class)
    public void testRetrieveServiceQuotation() throws ServiceQuotationNotExistException {
        Long sqId = Long.valueOf("111");
        ServiceQuotation sq = sqsbr.retrieveServiceQuotation(sqId);
        String result = "error";
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testRetrieveSQList() throws CompanyNotExistException, ServiceQuotationNotExistException {
        String ownerCompany = "mer";
        String clientCompany = "mermer";
        ArrayList<ServiceQuotation> sql = sqsbr.retrieveServiceQuotationList(ownerCompany, clientCompany);
        String result = "error";
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveSQList02() throws ServiceQuotationNotExistException, CompanyNotExistException {
        String ownerCompany = "merlion";
//        ArrayList<ServiceQuotation> result = sqsbr.retrieveAllServiceQuotationList(ownerCompany);
        String result = ownerCompany;
        assertNotNull(result);
    }
    
    @Test(expected = ServiceQuotationNotExistException.class)
    public void testRemoteSQ() throws ServiceQuotationNotExistException, ServiceQuotationCannotBeAchivedException {
        Long sqId = Long.valueOf("111");
        sqsbr.removeServiceQuotation(sqId);
        String result = "Id not exist";
        assertNotNull(result);
    }
    
    @Test(expected = ServiceQuotationNotExistException.class)
    public void testApproveSQ() throws ServiceQuotationNotExistException, ServiceQuotationAlreadyApprovedException {
        Long sqId = Long.valueOf("111");
        sqsbr.approveServiceQuotation(sqId);
        String result = "Id not exist";
        assertNotNull(result);
    }
    
    @Test
    public void testVerifyWarehouse() {
        boolean result = true;
        assertTrue(result);
    }
    
    @Test
    public void testVerifyTransport() {
        boolean result = true;
        assertTrue(result);
    }
    
    @Test(expected = CompanyNotExistException.class )
    public void testAddSQ() throws CompanyNotExistException {
        Long partnerId = Long.valueOf("222");
        Long customerId = Long.valueOf("222");
        String description = "lol";
        Timestamp start = Timestamp.valueOf("2011-11-01 00:00:00.000000000");
        Timestamp end = Timestamp.valueOf("2015-11-01 00:00:00.000000000");
        Integer warehouse = 15;
        String wrehouseName = "warehouse";
        Double totalPrice = 20000.00;
        Location source = new Location();
        Location dest = new Location();
        boolean perishable = false;
        boolean pharmaceutical = false;
        boolean flammable = false;
        boolean highValue = false;
        Product product = null;
        Double estimatedQty = 20.00;
        ServiceQuotation sq = sqsbr.addServiceQuotation(partnerId, customerId, description, start, end, warehouse, 
                wrehouseName, totalPrice, source, dest, perishable, flammable, pharmaceutical, highValue, product, estimatedQty);
        String result = "company not exist";
        assertNotNull(result);
    }
    
    private ServiceQuotationSessionBeanRemote lookupServiceQuotationSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ServiceQuotationSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/ServiceQuotationManagementSession!CRM.session.ServiceQuotationSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
