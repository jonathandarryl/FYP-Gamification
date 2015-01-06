/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Account;
import Common.entity.Announcement;
import Common.entity.Message;
import java.util.ArrayList;
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
public class AnnouncementSessionBean implements AnnouncementSessionBeanLocal, AnnouncementSessionBeanRemote{

    @PersistenceContext
    EntityManager em;

    public AnnouncementSessionBean() {
    }

    @Override
    public Announcement createAnnouncement(String title, String content, Account account, Long departmentId) {
        try {
            Announcement announcement = new Announcement(title, content, account, departmentId);
            Date currentTime = new Date();
            announcement.setAnnouncementTime(currentTime);
            em.persist(announcement);
            return announcement;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Announcement> getAllAnnouncements(Long accountId) {
        try {
            Account account = em.find(Account.class, accountId);
            Query query = em.createQuery("SELECT a FROM Announcement a WHERE a.account=:acc");
            System.out.println("haha");
            query.setParameter("acc", account);
            List<Announcement> announcements = new ArrayList(query.getResultList());
            System.out.println("lala");
            return announcements;
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public List<Announcement> getDepartmentAnnouncements(Long departmentId) {
        try {            
            Query q = em.createQuery("SELECT a FROM Announcement a WHERE a.departmentId=:receiver");
            q.setParameter("receiver", departmentId);
            return q.getResultList();         
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Boolean deleteAnnouncement(Long id) {
        try {
            Announcement announcement = em.find(Announcement.class, id);
            System.out.println("Announcement is found, ready to delete.");
            em.remove(announcement);
            System.out.println("Announcemnet is removed.");
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
