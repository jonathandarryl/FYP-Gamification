/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Message;
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

/**
 *
 * @author Sun Mingjia
 */
public class MessageSessionBeanTest {
    
    MessageSessionBeanRemote msbr = this.lookupMessageSessionBeanRemote();
    
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
    public void testCreateMessage() throws Exception {
        System.out.println("inside create msg");
        Long senderId = Long.valueOf("5");
        Long receiverId = Long.valueOf("24");
        String title = "msg title";
        String content = "msg content";
        Message result = msbr.createMessage(senderId, receiverId, title, content);
        assertNotNull(result);
    }
    
    @Test
    public void testGetAllMessages() throws Exception {
        Long receiverId = Long.valueOf("5");
        List<Message> result = msbr.getAllMessages(receiverId);
        assertNotNull(result);
    }
    
    @Test
    public void testGetUnreadMessages() throws Exception {
        Long receiverId = Long.valueOf("27");
        List<Message> result = msbr.getUnreadMessages(receiverId);
        assertNotNull(result);
    }
    
    @Test
    public void testDeleteMessage() throws Exception {
        Long messageId = Long.valueOf("999");
        boolean result = msbr.deleteMessage(messageId);
        assertFalse(result);
    }
    
    @Test
    public void testReadMessage() throws Exception {
        Long messageId = Long.valueOf("327");
        boolean result = msbr.readMessage(messageId);
        assertTrue(result);
    }
    
    @Test
    public void testUnreadCount() throws Exception {
        Long receiverId = Long.valueOf("24");
        int count = msbr.unreadCount(receiverId);
        boolean result = false;
        if (count==0) {
            result = true;
        }
        assertFalse(result);
    }
    
     private MessageSessionBeanRemote lookupMessageSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (MessageSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/MessageSessionBean!Common.session.MessageSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}
