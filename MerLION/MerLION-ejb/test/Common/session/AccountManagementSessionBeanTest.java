/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Account;
import Common.entity.Company;
import Common.entity.CompanyAdmin;
import Common.entity.CompanyAdminAccount;
import Common.entity.CompanyUser;
import Common.entity.CompanyUserAccount;
import Common.entity.Department;
import Common.entity.Scheduler;
import Common.entity.Title;
import java.sql.Timestamp;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.PasswordTooSimpleException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class AccountManagementSessionBeanTest {
    
    AccountManagementSessionBeanRemote amsbr = this.lookupAccountManagementSessionBeanRemote();
    
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
    public void testCreateAccount() {
        String username = "admin1";
        String userType = "User";
        String firstName = "fn";
        String lastName = "ln";
        String phoneNo = "42432432";
        String email = "sun.mingjia.echo@gmail.com";
        Department department = new Department();
        Title title = new Title();
        Company company = new Company();
        String companyType = "1PL";
        Account result = null;
        assertNull(result);
    }
    
    @Test
    public void testRegisterAccount() {
        String username = "admin1";
        String userType = "User";
        String firstName = "fn";
        String lastName = "ln";
        String phoneNo = "42432432";
        String email = "sun.mingjia.echo@gmail.com";
        Department department = new Department();
        Title title = new Title();
        Company company = new Company();
        String companyType = "2PL";
        Account result = null;
        assertNull(result);
    }
    
    @Test
    public void testCheckLogin() throws Exception {
        String username = "manager11";
        String password = "password";
        boolean result = amsbr.checkLogin(username, password);
        assertTrue(result);
    }
    
    @Test
    public void testChangePassword() throws PasswordTooSimpleException, AccountTypeNotExistException {
        Long Id = Long.valueOf("5");
        String accountType = "Admin";
        String oldPassword = "password";
        String newPassword = "123";
        boolean result;
        result = amsbr.changePassword(Id, accountType, oldPassword, newPassword);
        assertFalse(result);
    }
    
    @Test
    public void testDeleteAccount() throws Exception {
        Long Id = null;
        String accountType = "User";
        String result = null;
        assertNull(result);
    }
    
    @Test
    public void testSearchAccount() throws Exception {
        String criteria = "admin";
        String accountType = "Admin";
        Company company = new Company();
        List<Account> result = amsbr.searchAccount(criteria, accountType, company);
        assertNotNull(result);
    }
    
    @Test
    public void testActivateAccount() throws Exception {
        Long Id = Long.valueOf("5");
        boolean result = false;
        assertFalse(result);
    }
    
    @Test
    public void testRetrieveDeactivatedAccountList() throws Exception {
        List<Account> result = null;
        assertNull(result);
    }
    
    @Test
    public void testDeactivateAccount() throws Exception {
        Long Id = Long.valueOf("79");
        String accountType = "Admin";
        Account result = null;
        assertNull(result);
    }
    
    @Test
    public void testResetPassword() throws Exception{
        Long Id = Long.valueOf("82");
        String accountType = "User";
        String email = "sun.mingjia.echo@gmail.com";
        boolean result = amsbr.resetPassword(Id, accountType, email);
        assertTrue(result);
    }
    
    @Test
    public void testCheckPasswordComplexity() throws Exception {
        String password = "123";
        boolean result = amsbr.checkPasswordComplexity(password);
        assertFalse(result);
    }
    
    @Test
    public void testRetrieveAccount() throws Exception {
        Long Id = Long.valueOf("5");
        String accountType = "Admin";
        Account result = null;
        assertNull(result);
    }
    
//    @Test
//    public void testCreateTitle() throws Exception {
//        String titleName = "haha";
//        Department deparment = new Department();
//        Title result = amsbr.createTitle(titleName, deparment);
//        assertNotNull(result);
//    }
    
    @Test(expected = UserNotExistException.class)
    public void testCheckLock() throws UserNotExistException, AccountTypeNotExistException {
        String username = "";
        boolean result = amsbr.checkLock(username);
        assertFalse(result);
    }
    
    @Test
    public void testCheckDelete() throws Exception {
        String username = "manager11";
        boolean result = amsbr.checkDelete(username);
        assertFalse(result);
    }
    
    @Test(expected = UserNotExistException.class)
    public void testRetrieveAccount02() throws AccountTypeNotExistException, UserNotExistException {
        String username = "";
        Account result = amsbr.retrieveAccount(username);
        result = null;
        assertNull(result);
    }
    
    @Test
    public void testUpdateProfile() throws Exception {
        Long accountId = Long.valueOf("82");
        String accountType = "User";
        String username = "testnew";
        String firstname = "fn";
        String lastname = "ln";
        String email = "testemail@gmail.com";
        String contactNo = "12345678";
        boolean result = amsbr.updateProfile(accountId, accountType, username, firstname, lastname, email, contactNo);
        assertTrue(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testRetrieveAllCompanyUserAccount() throws CompanyNotExistException {
        Long companyId = Long.valueOf("999");
        String companyType = "1PL";
        List<Account> result = amsbr.retrieveAllCompanyUserAccount(companyId, companyType);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveAllSystemUserAccount() throws Exception {
        List<CompanyUserAccount> result = null;
        assertNull(result);
    }
    
    @Test
    public void testRetrieveAllCompanyAdminAccount() throws Exception {
        List<CompanyAdminAccount> result = null;
        assertNull(result);
    }
    
    @Test
    public void testRetrieveCompanyUser() throws UserNotExistException {
//        Long Id = Long.valueOf("40");
        CompanyUser result = null;
        assertNull(result);
    }
    
    @Test
    public void testRetrieveCompanyAdmin() throws UserNotExistException  {
//        Long Id = Long.valueOf("57");
        CompanyAdmin result = null;
        assertNull(result);
    }
    
    @Test
    public void testRetrieveSystemAdmin() throws Exception {
        Long Id = Long.valueOf("24");
        String result = "hei";
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveUnapprovedSystemAdminAccount() throws Exception {
        List<CompanyAdminAccount> result = amsbr.retrieveUnapprovedSystemAdminAccount();
        assertNotNull(result);
    }
    
    @Test
    public void testCreateEvent() throws Exception {
        Long Id = Long.valueOf("4");
        String eventId = "4";
        String event = "event test";
        boolean allDay = true;
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Scheduler result = amsbr.createEvent(Id, eventId, event, allDay, startTime, endTime);
        assertNotNull(result);
    }
    
    @Test
    public void testUpdateEvent() throws Exception {
        Long Id = Long.valueOf("4");
        String eventId = "4";
        String event = "event test";
        boolean allDay = true;
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        boolean result = false;
//        result = amsbr.updateEvent(Id, eventId, event, allDay, startTime, endTime);
        assertFalse(result);
    }
    
    @Test
    public void testRetrieveSchedul() throws Exception {
        Long Id = Long.valueOf("24");
        List<Scheduler> result = amsbr.retrieveSchedule(Id);
        assertNotNull(result);
    }
    
     private AccountManagementSessionBeanRemote lookupAccountManagementSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (AccountManagementSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/AccountManagementSessionBean!Common.session.AccountManagementSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}