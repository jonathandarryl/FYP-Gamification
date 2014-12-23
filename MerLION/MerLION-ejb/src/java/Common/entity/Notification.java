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
import javax.persistence.TemporalType;

/**
 *
 * @author chongyangsun
 */
@Entity
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date notifyTime;
    @Lob
    private String content;
    @ManyToOne
    private Account sender;
    private Long departmentId;

    
    public Notification(){
    }
    
    public Notification(Account sender, Long departmentId, String content, Date notifyTime){
        this.setSender(sender);
        this.setDepartmentId(departmentId);
        this.setContent(content);
        this.setNotifyTime(notifyTime);
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
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.Notification[ id=" + getId() + " ]";
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getContent() {
        return content;
    }

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
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
