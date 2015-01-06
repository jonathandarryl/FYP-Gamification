/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.Account;
import Common.entity.Department;
import Common.entity.Notification;
import Common.entity.NotificationRecord;
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
public class NotificationSessionBean implements NotificationSessionBeanLocal, NotificationSessionBeanRemote {

    @PersistenceContext
    EntityManager em;

    public NotificationSessionBean() {
    }

    @Override
    public Notification createNotification(Account account, Long departmentId, String content) {
        try {
            Date currentTime = new Date();
            Notification notification = new Notification(account, departmentId, content, currentTime);
            Department department = em.find(Department.class, departmentId);
            List<Account> accountList = department.getAccount();
            for (Account a : accountList) {
                NotificationRecord notificationRecord = new NotificationRecord(account.getId(), content, currentTime, departmentId);
                notificationRecord.setAccount(a);
                em.persist(notificationRecord);
            }
            em.persist(notification);
            return notification;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<NotificationRecord> getNotficationRecords(Long accountId) {
        try {
            Account account = em.find(Account.class, accountId);
            System.out.println("Account is "+account.getUsername());
            Query query = em.createQuery("SELECT n FROM NotificationRecord n WHERE n.account=:acc");
            query.setParameter("acc", account);
            List<NotificationRecord> receivedNotifications = query.getResultList();
            if (receivedNotifications.isEmpty()) {
                System.out.println("It is empty");
            }
            else {
                System.out.println("It is not empty!");
            }
            return receivedNotifications;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean deleteNotificationRecord(Long id) {
        try {
            NotificationRecord notificationRecord = em.find(NotificationRecord.class, id);
            em.remove(notificationRecord);
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
