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
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class ServiceInvoice implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp creationTime;
    @OneToOne
    private ServiceOrder serviceOrder;
    @OneToOne(mappedBy="serviceInvoice")
    private ServicePayment servicePayment;
    private Boolean paidOrNot;

    public ServiceInvoice() {
    }

    public ServiceInvoice(ServiceOrder serviceOrder) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.serviceOrder = serviceOrder;
        this.paidOrNot = Boolean.FALSE;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public ServicePayment getServicePayment() {
        return servicePayment;
    }

    public void setServicePayment(ServicePayment servicePayment) {
        this.servicePayment = servicePayment;
    }

    public Boolean isPaidOrNot() {
        return paidOrNot;
    }

    public void setPaidOrNot(Boolean paidOrNot) {
        this.paidOrNot = paidOrNot;
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
        if (!(object instanceof ServiceInvoice)) {
            return false;
        }
        ServiceInvoice other = (ServiceInvoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceInvoice[ id=" + id + " ]";
    }
    
}
