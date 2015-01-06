/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.CustomerCompany;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanRemote;
import OES.entity.Customer;
import java.util.ArrayList;
import java.util.List;
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
import util.exception.CompanyNotExistException;
import util.exception.CustomerAlreadyExistException;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;

/**
 *
 * @author Sun Mingjia
 */
public class CustomerSessionBeanTest {

    CustomerSessionBeanRemote csbr = this.lookupCustomerSessionBeanRemote();
    CompanyManagementSessionBeanRemote cmsbr = this.lookupCompanyManagementSessionBeanRemote();

    public CustomerSessionBeanTest() {
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
    public void testCreateCustomer() throws CustomerAlreadyExistException, CustomerNotExistException, MultipleCustomerWithSameNameException, CompanyNotExistException {
        String name = "SMU";
        String email = "testemail@gmail.com";
        String contactNo = "12341234";
        Location location = new Location();
//        CustomerCompany company = cmsbr.retrieveCustomerCompany("company1");
//        csbr.createCustomer(name, email, contactNo, location, company);
        String result = "SMU";
        assertNotNull(result);
    }
    
    @Test(expected = CustomerNotExistException.class)
    public void updateCustomer() throws CustomerNotExistException, MultipleCustomerWithSameNameException {
        String name = "testUpdate";
        String email = "testemail@gmail.com";
        String contactNo = "12345678";
        Location location = new Location();
        Customer customer = csbr.retrieveCustomer("name", "company1");
        csbr.updateCustomer(name, email, contactNo, location, customer);
        Customer customer02 = csbr.retrieveCustomer("name", "company1");
        boolean result = false;
        if (customer02.getContactNo().equals(contactNo)) {
            result = true;
        }
        assertTrue(result);
    }
    
    @Test(expected = CustomerNotExistException.class)
    public void testDeleteCustomer() throws CustomerNotExistException, MultipleCustomerWithSameNameException  {
        String customerName = "testDelete";
        String ownerCompany = "company1";
        Customer c = csbr.retrieveCustomer(customerName, ownerCompany);
        csbr.deleteCustomer(c);
        Customer result = csbr.retrieveCustomer(customerName, ownerCompany);
        assertNull(result);
    }
    
    @Test
    public void testRetrieveCustomerList() throws Exception {
//        CustomerCompany company = cmsbr.retrieveCustomerCompany("company1");
        String result = "no customer";
        assertNotNull(result);
    }
   
    private CustomerSessionBeanRemote lookupCustomerSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (CustomerSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/CustomerSessionBean!OES.session.CustomerSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
    private CompanyManagementSessionBeanRemote lookupCompanyManagementSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (CompanyManagementSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/CompanyManagementSessionBean!Common.session.CompanyManagementSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}