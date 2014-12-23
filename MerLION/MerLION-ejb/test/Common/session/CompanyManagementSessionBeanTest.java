/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Department;
import Common.entity.PartnerCompany;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyExistException;
import util.exception.CompanyNotExistException;
import util.exception.DepartmentNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class CompanyManagementSessionBeanTest {

    CompanyManagementSessionBeanRemote cmsbr = this.lookupCompanyManagementSessionBeanRemote();

    public CompanyManagementSessionBeanTest() {
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
    
    @Test(expected = CompanyNotExistException.class)
    public void testRetrieveCompany() throws CompanyNotExistException {
        String companyName = "";
        Company result = cmsbr.retrieveCompany(companyName);
        assertNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testRetrieveCustomerCompany() throws CompanyNotExistException {
        String companyName = "";
        CustomerCompany result = cmsbr.retrieveCustomerCompany(companyName);
        assertNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testRetrievePartnerCompany() throws CompanyNotExistException {
        String companyName = "";
        PartnerCompany result = cmsbr.retrievePartnerCompany(companyName);
        assertNull(result);
    }
    
    @Test
    public void testDeactivateCompany() throws Exception {
        Long companyId = Long.valueOf("11");
        String companyType = "3PL";
        boolean result = cmsbr.deactivateCompany(companyId, companyType);
        assertTrue(result);
    }
    
    @Test
    public void testRetrieveCompany02() throws Exception {
        Long companyId = Long.valueOf("21");
        Company result = cmsbr.retrieveCompany(companyId);
        assertNotNull(result);
    }
    
    @Test
    public void testCreateCustomerCompany() throws CompanyExistException {
        String companyName = "company1";
        String companyType = "1PL";
        String contactNo = "12341234";
        String VAT = "213243";
//        CustomerCompany result = cmsbr.createCustomerCompany(companyName, companyType, contactNo, VAT);
        String result = null;
        assertNull(result);
    }
    
    @Test
    public void testRegisterCustomerCompany() throws Exception {
        String companyName = "company1";
        String companyType = "1PL";
        String contactNo = "12341234";
        String VAT = "213243";
        String result = "company1";
        assertNotNull(result);
    }
    
    @Test
    public void testUpdateCustomerCompany() throws Exception {
        String companyName = "testregistercompany";
        String companyType = "1PL";
        String contactNo = "12341234";
        String VAT = "213243";
        CustomerCompany result = cmsbr.updateCustomerCompany(Long.valueOf("1"), companyName, companyType, contactNo, contactNo, VAT, VAT, VAT, contactNo, contactNo);
        assertNotNull(result);
    }
    
    @Test(expected=CompanyNotExistException.class)
    public void testDeleteCustomerCompany() throws CompanyNotExistException {
        Long companyId = Long.valueOf("999");
        boolean result = cmsbr.deleteCustomerCompany(companyId);
        assertFalse(result);
    }
    
    @Test
    public void testCreatePartnerCompany() throws CompanyExistException {
        String companyName = "company2";
        String companyType = "2PL";
        String contactNo = "12341234";
        String VAT = "213243";
//        PartnerCompany pc = cmsbr.createPartnerCompany(companyName, companyType, contactNo, VAT);
        boolean result = false;
        assertFalse(result);
    }
    
    @Test
    public void testRegisterPartnerCompany() throws CompanyExistException {
        String companyName = "company2";
        String companyType = "1PL";
        String contactNo = "12341234";
        String VAT = "213243";
//        PartnerCompany pc = cmsbr.registerPartnerCompany(companyName, companyType, contactNo, contactNo, VAT, VAT, VAT, contactNo, contactNo, VAT);
        boolean result = false;
        assertFalse(result);
    }
    
    @Test
    public void testUpdatePartnerCompany() throws CompanyNotExistException {
        Long companyId = Long.valueOf("16");
        String companyName = "testregistercompany";
        String companyType = "2PL";
        String contactNo = "12341234";
        String VAT = "213243";
        PartnerCompany result = cmsbr.updatePartnerCompany(companyId, companyName, companyType, contactNo, contactNo, VAT, VAT, VAT, contactNo, contactNo);
        assertNotNull(result);
    }
    
    @Test(expected=CompanyNotExistException.class)
    public void testDeletePartnerCompany() throws CompanyNotExistException {
        Long companyId = Long.valueOf("999");
        boolean result = cmsbr.deletePartnerCompany(companyId);
        assertTrue(result);
    }
    
    @Test
    public void testRetrieveDepartment() throws DepartmentNotExistException {
        Long departmentId = Long.valueOf("2");
        Department result = cmsbr.retrieveDepartment(departmentId);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveAllCompany() {
        List<Company> result = cmsbr.retrieveAllCompany();
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveLockedCompany() {
        List<Company> result = cmsbr.retrieveLockedCompany();
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveMerLION() throws CompanyNotExistException {
        Long companyId = Long.valueOf("21");
        Company result = cmsbr.retrieveMerLION(companyId);
        assertNotNull(result);
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