/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import java.io.Serializable;
import java.util.Date;
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
public class CompanyUserAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy="companyUserAccount")
    private CompanyUser companyUser;
    
    public CompanyUserAccount(){}
    
    public CompanyUserAccount (String username, String password, String salt, Department department, Company company, String accountType){
        this.setUsername(username);
        this.setPassword(password);
        this.setSalt(salt);
        this.setLockedOrNot(Boolean.FALSE);
        Date dateOfIssue = new Date();
        this.setDateOfIssue(dateOfIssue);
        this.setDepartment(department);
        this.setCompany(company);
        this.setApprovedOrNot(Boolean.TRUE);
        this.setAccountType(accountType);
//        this.setCompanyUser(companyUser);
    }
    
    public CompanyUserAccount (String username, String password, String salt, Department department, Company company, String accountType, Boolean register){
        this.setUsername(username);
        this.setPassword(password);
        this.setSalt(salt);
        this.setLockedOrNot(Boolean.FALSE);
        Date dateOfIssue = new Date();
        this.setDateOfIssue(dateOfIssue);
        this.setDepartment(department);
        this.setCompany(company);
        this.setAccountType(accountType);
        this.setApprovedOrNot(register);
//        this.setCompanyUser(companyUser);
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
        if (!(object instanceof CompanyUserAccount)) {
            return false;
        }
        CompanyUserAccount other = (CompanyUserAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.CompanyUserAccount[ id=" + id + " ]";
    }

    /**
     * @return the companyUser
     */
    public CompanyUser getCompanyUser() {
        return companyUser;
    }

    /**
     * @param companyUser the companyUser to set
     */
    public void setCompanyUser(CompanyUser companyUser) {
        this.companyUser = companyUser;
    }
    
}
