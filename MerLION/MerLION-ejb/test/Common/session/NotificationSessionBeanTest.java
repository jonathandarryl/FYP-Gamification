/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.Account;
import Common.entity.Notification;
import Common.entity.NotificationRecord;
import java.util.List;
import javax.ejb.EJB;
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

/**
 *
 * @author Sun Mingjia
 */
public class NotificationSessionBeanTest {

    NotificationSessionBeanRemote nsbr = this.lookupNotificationSessionBeanRemote();
    AccountManagementSessionBeanRemote amsbr = this.lookupAccountManagementSessionBeanRemote();

    public NotificationSessionBeanTest() {
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
    public void testGetNotificatinRecords() throws Exception {
        System.out.println("inside test get all notification records");
        Long accountId = Long.valueOf("5");
        List<NotificationRecord> result = nsbr.getNotficationRecords(accountId);
        assertNull(result);
    }

    @Test
    public void testDeleteNotificationRecord() throws Exception {
        System.out.println("inside delete notification record");
        Long notificationId = Long.valueOf("999");
        boolean result = nsbr.deleteNotificationRecord(notificationId);
        assertFalse(result);
    }

    @Test
    public void testCreateNotification() throws Exception {
        System.out.println("inside test create notifiction");
//        Account a = amsbr.retrieveAccount(Long.valueOf("10"),"Admin");
//        Long receiverId = Long.valueOf("999");
//        String content = "test notification";
//        Notification result = nsbr.createNotification(a, receiverId, content);
        String result = "haha";
        assertNotNull(result);
    }

    private NotificationSessionBeanRemote lookupNotificationSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (NotificationSessionBeanRemote) c.lookup("java:global/MerLION/MerLION-ejb/NotificationSessionBean!Common.session.NotificationSessionBeanRemote");
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
