/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Account;
import Common.entity.Announcement;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author chongyangsun
 */
@Local
public interface AnnouncementSessionBeanLocal {
    public Announcement createAnnouncement(String title, String content, Account account, Long departmentId);
    public List<Announcement> getAllAnnouncements(Long accountId);//retrieve announcement send by account
    public List<Announcement> getDepartmentAnnouncements(Long departmentId);//retrieve received announcement of department
    public Boolean deleteAnnouncement(Long announcementId);
}
