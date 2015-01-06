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
public class Announcement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Lob
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date announcementTime;
    @ManyToOne
    private Account account;
    private Long departmentId; //receiver department

    public Announcement() {
    }
    
    public Announcement(String title, String content, Account account, Long departmentId){
        Date thisTime = new Date();
        this.setTitle(title);
        this.setContent(content);
        this.setAnnouncementTime(thisTime);
        this.setAccount(account);
        this.setDepartmentId(departmentId);
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
        if (!(object instanceof Announcement)) {
            return false;
        }
        Announcement other = (Announcement) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.Announcement[ id=" + getId() + " ]";
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
     * @return the announcementTime
     */
    public Date getAnnouncementTime() {
        return announcementTime;
    }

    /**
     * @param announcementTime the announcementTime to set
     */
    public void setAnnouncementTime(Date announcementTime) {
        this.announcementTime = announcementTime;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
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
