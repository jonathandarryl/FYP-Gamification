/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author chongyangsun
 */
@Entity
public class Scheduler implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long accountId;
    private String eventId;
    private String event;
    private Boolean allDay;
    private Timestamp startTime;
    private Timestamp endTime;
    
    public Scheduler() {}
    
    public Scheduler(Long accountId, String eventId, String event, Timestamp startTime, Timestamp endTime, Boolean allDay) {
        this.setAccountId(accountId);
        this.setEventId(eventId);
        this.setEvent(event);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setAllDay(allDay);
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Scheduler)) {
            return false;
        }
        Scheduler other = (Scheduler) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.Schedule[ id=" + id + " ]";
    }

    /**
     * @return the accountId
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the allDay
     */
    public Boolean getAllDay() {
        return allDay;
    }

    /**
     * @param allDay the allDay to set
     */
    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    /**
     * @return the startTime
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Timestamp getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * @return the eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
}
