/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.Account;
import Common.entity.Announcement;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.AccountTypeNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class AnnouncementSessionBeanTest {

    AnnouncementSessionBeanRemote asbr = this.lookupAnnouncementSessionBeanRemote();
    AccountManagementSessionBeanRemote amsbr = this.lookupAccountManagementSessionBeanRemote();

    public AnnouncementSessionBeanTest() {
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
    public void testCreateAnnouncement() throws AccountTypeNotExistException {
        System.out.println("Test create announcement");
        String title = "test";
        String content = "test";
//        Account a = amsbr.retrieveAccount(Long.valueOf("10"), "Admin");
//        System.out.println("user account type is " + a.getAccountType());
        Long departmentId = Long.valueOf("5");
        List<Announcement> result = asbr.getAllAnnouncements(departmentId);
        assertNotNull(result);
    }

    @Test
    public void testGetAllAnnouncements() throws Exception {
        System.out.println("Test get all announcements");
        Long accountId = Long.valueOf("5");
        List<Announcement> result = asbr.getAllAnnouncements(accountId);
        assertNotNull(result);
    }

    @Test
    public void testGetDepartmentAnnouncements() throws Exception {
        System.out.println("Test get all department announcements");
        Long departmentId = Long.valueOf("2");
        List<Announcement> result = null;
        assertNull(result);
    }

    @Test
    public void testDeleteAnnouncement() throws Exception {
        System.out.println("Test delete announcement");
        Long announcementId = Long.valueOf("999");
        boolean result = asbr.deleteAnnouncement(announcementId);
        assertFalse(result);
    }

    private AnnouncementSessionBeanRemote lookupAnnouncementSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (AnnouncementSessionBeanRemote) c.lookup("java:global/MerLION/MerLION-ejb/AnnouncementSessionBean!Common.session.AnnouncementSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    private AccountManagementSessionBeanRemote lookupAccountManagementSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (AccountManagementSessionBeanRemote) c.lookup("java:global/MerLION/MerLION-ejb/AccountManagementSessionBean!Common.session.AccountManagementSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }

    }
}
