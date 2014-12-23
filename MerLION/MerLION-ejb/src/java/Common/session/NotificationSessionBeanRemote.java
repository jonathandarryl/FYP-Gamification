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
import javax.ejb.Remote;

/**
 *
 * @author chongyangsun
 */
@Remote
public interface NotificationSessionBeanRemote {
    public Notification createNotification(Account account, Long receiverId, String content);
    public List<NotificationRecord> getNotficationRecords(Long accountId);
    public boolean deleteNotificationRecord(Long id);
}
