/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class SubscriptionPaymentReceipt implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double price;
    private Timestamp receiveTime;
    @ManyToOne
    private Company company;
    @OneToOne
    private SubscriptionPayment subscriptionPayment;

    public SubscriptionPaymentReceipt() {
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public SubscriptionPayment getSubscriptionPayment() {
        return subscriptionPayment;
    }

    public void setSubscriptionPayment(SubscriptionPayment subscriptionPayment) {
        this.subscriptionPayment = subscriptionPayment;
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
        if (!(object instanceof SubscriptionPaymentReceipt)) {
            return false;
        }
        SubscriptionPaymentReceipt other = (SubscriptionPaymentReceipt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.SubscriptionPaymentReceipt[ id=" + id + " ]";
    }

    public Timestamp getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Timestamp receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
}
