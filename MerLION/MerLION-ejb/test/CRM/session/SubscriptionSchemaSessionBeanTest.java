/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SubscriptionSchema;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class SubscriptionSchemaSessionBeanTest {
    
    SubscriptionSchemaSessionBeanRemote sssbr = this.lookupSubscriptionSchemaSessionBeanRemote();
    
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
    public void testViewSubscriptionPayment() {
        
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testCreateSchema() throws CompanyNotExistException {
        Long ownerId = Long.valueOf("111");
        Integer num = 2;
        Double price = 60.00;
        String result = sssbr.createSubscriptionSchema(ownerId, num, price);
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testUpdateSchema() throws CompanyNotExistException, SubscriptionSchemaNotExistException {
        Long ownerId = Long.valueOf("111");
        Integer num = 2;
        Double price = 60.00;
        String result = sssbr.updateSubscriptionSchema(ownerId, ownerId, num, price);
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testViewAllSchema() throws CompanyNotExistException, SubscriptionSchemaNotExistException {
        Long ownerId = Long.valueOf("111");
        List<SubscriptionSchema> result = sssbr.viewAllSubscriptionSchema(ownerId);
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testViewSchema() throws CompanyNotExistException, SubscriptionSchemaNotExistException {
        Long ownerId = Long.valueOf("111");
        Long Id = Long.valueOf("111");
        SubscriptionSchema ss = sssbr.viewSubscriptionSchema(ownerId, Id);
        String result = "no schema";
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testDeleteSchema() throws CompanyNotExistException, SubscriptionSchemaNotExistException {
        Long ownerId = Long.valueOf("111");
        Long Id = Long.valueOf("111");
        String result = sssbr.deleteSubscriptionSchema(ownerId, Id);
        assertNotNull(result);
    }
    
    @Test
    public void testGetPrice() throws SubscriptionSchemaNotExistException {
//        List<String> result = sssbr.getPriceList();
        String result = "getPrice";
        assertNotNull(result);
    }
    
    private SubscriptionSchemaSessionBeanRemote lookupSubscriptionSchemaSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (SubscriptionSchemaSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/SubscriptionSchemaMgtSessionBean!CRM.session.SubscriptionSchemaSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
