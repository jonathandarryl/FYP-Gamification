/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author songhan
 */
@Entity
public class ServiceContractRenewNotification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ServiceContract serviceContract;
    private Timestamp creationTime;

    public ServiceContractRenewNotification() {
    }

    public ServiceContractRenewNotification(ServiceContract serviceContract) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.serviceContract = serviceContract;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
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
        if (!(object instanceof ServiceContractRenewNotification)) {
            return false;
        }
        ServiceContractRenewNotification other = (ServiceContractRenewNotification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceContractRenewNotification[ id=" + id + " ]";
    }
    
}
