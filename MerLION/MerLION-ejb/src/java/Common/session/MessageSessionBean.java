/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Account;
import Common.entity.Department;
import Common.entity.Message;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class MessageSessionBean implements MessageSessionBeanLocal, MessageSessionBeanRemote {
    
    @PersistenceContext
    private EntityManager em;
    Message message;
    Account account;
    Department department;
    
    @Override
    public Message createMessage(Long senderId, Long receiverId, String title, String content) {
        try {
            message = new Message();
            Date currentTime = new Date();
            message = new Message(title, content, receiverId);
            account = em.find(Account.class, senderId);
            message.setSender(account);
            message.setMessageTime(currentTime);
            em.persist(message);
            System.out.println("after send msg");
            return message;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<Message> getAllMessages(Long receiverId) {
        try {
            Query q = em.createQuery("SELECT m FROM Message m WHERE m.receiverId=:receiver");
            q.setParameter("receiver", receiverId);
            return q.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public List<Message> getUnreadMessages(Long receiverId) {
        try {
            boolean notRead = false;
            Query q = em.createQuery("SELECT m FROM Message m WHERE m.receiverId=:receiver AND m.readOrNot=:notRead");
            q.setParameter("notRead", notRead);
            q.setParameter("receiver", receiverId);
            return q.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Boolean deleteMessage(Long id) {
        try {
            Message message = em.find(Message.class, id);
            System.out.println("Message " + id + " is found, ready to delete.");
            em.remove(message);
            System.out.println("Message is removed.");
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public boolean readMessage(Long id) {
        try {
            message = em.find(Message.class, id);
            message.setReadOrNot(true);
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int unreadCount(Long receiverId) {
        int count = 0;
        Query q = em.createQuery("SELECT m FROM Message m WHERE m.receiverId=:receiver");
        q.setParameter("receiver", receiverId);
        List<Message> messageList = q.getResultList();

        for (Message m : messageList) {
            if (!m.isReadOrNot()) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public boolean markAsUnread(Long id) {
        try {
            message = em.find(Message.class, id);
            message.setReadOrNot(false);
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
