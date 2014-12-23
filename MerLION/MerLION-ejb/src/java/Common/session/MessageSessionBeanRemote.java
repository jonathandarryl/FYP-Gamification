/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Message;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author chongyangsun
 */
@Remote
public interface MessageSessionBeanRemote {
    public Message createMessage(Long sender, Long receiver, String title, String content);
    public List<Message> getAllMessages(Long receiver);
    public List<Message> getUnreadMessages(Long receiver);
    public Boolean deleteMessage(Long id);
    public boolean readMessage(Long id);
    public int unreadCount(Long receiver);
    public boolean markAsUnread(Long id);
}
