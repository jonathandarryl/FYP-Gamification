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
public class CompanyAdminAccount extends Account implements Serializable {
    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy="companyAdminAccount")
    private CompanyAdmin companyAdmin;
    
    public CompanyAdminAccount(){
    }
    
    public CompanyAdminAccount (String username, String password, String salt, Department department, Company company, String accountType){
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
//        this.setCompanyAdmin(companyAdmin);
    }
    
    public CompanyAdminAccount (String username, String password, String salt, Department department, Company company, String accountType, Boolean register){
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
//        this.setCompanyAdmin(companyAdmin);
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
        if (!(object instanceof CompanyAdminAccount)) {
            return false;
        }
        CompanyAdminAccount other = (CompanyAdminAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.CompanyAdminAccount[ id=" + id + " ]";
    }

    /**
     * @return the companyAdmin
     */
    
    
    public CompanyAdmin getCompanyAdmin() {
        return companyAdmin;
    }

    /**
     * @param companyAdmin the companyAdmin to set
     */
    public void setCompanyAdmin(CompanyAdmin companyAdmin) {
        this.companyAdmin = companyAdmin;
    }
    
}
