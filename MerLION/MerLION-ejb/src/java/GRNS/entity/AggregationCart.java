/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author sunmingjia
 */
@Entity
public class AggregationCart implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp addTime;
    private Long serviceOrderId;
    private Boolean finished;
    
    public AggregationCart() {}
    
    public AggregationCart(Long serviceOrderId) {
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        this.setAddTime(time);
        this.setServiceOrderId(serviceOrderId);
        this.setFinished(Boolean.FALSE);
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
        if (!(object instanceof AggregationCart)) {
            return false;
        }
        AggregationCart other = (AggregationCart) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.AggregationCart[ id=" + id + " ]";
    }

    /**
     * @return the addTime
     */
    public Timestamp getAddTime() {
        return addTime;
    }

    /**
     * @param addTime the addTime to set
     */
    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    /**
     * @return the serviceOrderId
     */
    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    /**
     * @param serviceOrderId the serviceOrderId to set
     */
    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    /**
     * @return the finished
     */
    public Boolean getFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
    
}
