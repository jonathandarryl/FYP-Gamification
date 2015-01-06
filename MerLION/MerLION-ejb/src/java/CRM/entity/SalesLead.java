/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import Common.entity.PartnerCompany;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author songhan
 */
@Entity
public class SalesLead implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PartnerCompany  ownerCompany;
    @OneToOne
    private Company clientCompany;
    private String description;

    public SalesLead() {
    }

    public SalesLead(PartnerCompany ownerCompany, Company clientCompany) {
        this.ownerCompany = ownerCompany;
        this.clientCompany = clientCompany;
        this.description = "No description";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PartnerCompany getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(PartnerCompany ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public Company getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(Company clientCompany) {
        this.clientCompany = clientCompany;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SalesLead)) {
            return false;
        }
        SalesLead other = (SalesLead) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.SalesLead[ id=" + id + " ]";
    }
    
}
