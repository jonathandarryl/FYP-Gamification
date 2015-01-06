/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import Common.entity.SystemAdmin;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class SubscriptionSchema implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Integer numOfYears;
    private Double boundaryPrice;
    
    @ManyToOne
    private SystemAdmin owner;
    
    @OneToMany(mappedBy="subscriptionSchema")
    private List<Company> company;
    
    @OneToMany(mappedBy="schema")
    private List<SubscriptionPayment> payment;

    public SubscriptionSchema() {
    }

    public SubscriptionSchema(SystemAdmin owner, Integer numOfYears, Double boundaryPrice) {
        this.owner = owner;
        this.numOfYears = numOfYears;
        this.boundaryPrice = boundaryPrice;
    }
    
//    public Double assignYearToPrice(Integer year){
//        for(int i=0; i<this.numOfYears.size(); i++){
//            if(year <= this.numOfYears.get(i)){
//                return this.boundaryPrice.get(i);
//            }
//        }
//        return this.boundaryPrice.get(this.boundaryPrice.size()-1);
//    }

    public Integer getNumOfYears() {
        return numOfYears;
    }

    public void setNumOfYears(Integer numOfYears) {
        this.numOfYears = numOfYears;
    }

    public Double getBoundaryPrice() {
        return boundaryPrice;
    }

    public void setBoundaryPrice(Double boundaryPrice) {
        this.boundaryPrice = boundaryPrice;
    }
    
    public List<SubscriptionPayment> getPayment() {
        return payment;
    }

    public void setPayment(List<SubscriptionPayment> payment) {
        this.payment = payment;
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
        if (!(object instanceof SubscriptionSchema)) {
            return false;
        }
        SubscriptionSchema other = (SubscriptionSchema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.SubscriptionSchema[ id=" + id + " ]";
    }

    /**
     * @return the company
     */
    public List<Company> getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(List<Company> company) {
        this.company = company;
    }
    
    public SystemAdmin getOwner() {
        return owner;
    }

    public void setOwner(SystemAdmin owner) {
        this.owner = owner;
    }

}
