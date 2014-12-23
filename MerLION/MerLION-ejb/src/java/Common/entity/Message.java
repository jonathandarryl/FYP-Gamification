/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author chongyangsun
 */
@Entity
public class Message implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Lob
    private String content;
    @ManyToOne
    private Account sender;
    private Long receiverId;
    private boolean readOrNot;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date messageTime;
    
    public Message(){
       
    }
    
    public Message(String title,String content,Long receiverId){    
        this.setTitle(title);
        this.setContent(content);
        this.setReceiverId(receiverId);
        this.setReadOrNot(false); 
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Message)) {
            return false;
        }
        Message other = (Message) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.Message[ id=" + getId() + " ]";
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the sender
     */
    public Account getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(Account sender) {
        this.sender = sender;
    }

    /**
     * @return the receiverId
     */
    public Long getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId the receiverId to set
     */
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return the readOrNot
     */
    public boolean isReadOrNot() {
        return readOrNot;
    }

    /**
     * @param readOrNot the readOrNot to set
     */
    public void setReadOrNot(boolean readOrNot) {
        this.readOrNot = readOrNot;
    }

    /**
     * @return the messageTime
     */
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * @param messageTime the messageTime to set
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }
}
