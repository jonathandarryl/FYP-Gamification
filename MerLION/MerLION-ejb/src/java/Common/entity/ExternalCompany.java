/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import GRNS.entity.ExternalUserAccount;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author chongyangsun
 */
@Entity
public class ExternalCompany extends Company implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy = "externalCompany")
    private ExternalUserAccount externalUserAccount;

    public ExternalCompany(String companyName, String contactNo) {
        this.setCompanyName(companyName);
        this.setCompanyType("External");
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public ExternalCompany(String customerName) {
        this.setCompanyName(customerName);
    }
    
    public ExternalCompany(){}

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
        if (!(object instanceof ExternalCompany)) {
            return false;
        }
        ExternalCompany other = (ExternalCompany) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.ExternalCompany[ id=" + id + " ]";
    }

    /**
     * @return the externalUserAccount
     */
    public ExternalUserAccount getExternalUserAccount() {
        return externalUserAccount;
    }

    /**
     * @param externalUserAccount the externalUserAccount to set
     */
    public void setExternalUserAccount(ExternalUserAccount externalUserAccount) {
        this.externalUserAccount = externalUserAccount;
    }
    
}
