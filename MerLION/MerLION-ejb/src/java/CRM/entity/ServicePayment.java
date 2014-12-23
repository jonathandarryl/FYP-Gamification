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
public class ServicePayment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp paymentTime;
    private Double discount;
    
    @OneToOne
    private ServiceInvoice serviceInvoice;
    @OneToOne(mappedBy="servicePayment")
    private ServicePaymentReceipt receipt;
    
    private Boolean receiptIssued;

    public ServicePayment() {
    }

    public ServicePayment(ServiceInvoice serviceInvoice) {
        this.paymentTime = new Timestamp(System.currentTimeMillis());
        this.serviceInvoice = serviceInvoice;
        this.setReceiptIssued(Boolean.FALSE);
    }

    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }

    public ServiceInvoice getServiceInvoice() {
        return serviceInvoice;
    }

    public void setServiceInvoice(ServiceInvoice serviceInvoice) {
        this.serviceInvoice = serviceInvoice;
    }

    public ServicePaymentReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(ServicePaymentReceipt receipt) {
        this.receipt = receipt;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
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
        if (!(object instanceof ServicePayment)) {
            return false;
        }
        ServicePayment other = (ServicePayment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServicePayment[ id=" + id + " ]";
    }

    /**
     * @return the receiptIssued
     */
    public Boolean getReceiptIssued() {
        return receiptIssued;
    }

    /**
     * @param receiptIssued the receiptIssued to set
     */
    public void setReceiptIssued(Boolean receiptIssued) {
        this.receiptIssued = receiptIssued;
    }
    
}
