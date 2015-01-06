/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
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
public class CompanyUser extends User implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne(cascade={CascadeType.ALL})
    private CompanyUserAccount companyUserAccount;
    
    
    public CompanyUser(){
    }
    
//    public CompanyUser(CompanyUserAccount companyUserAccount) {
//        this.setCompanyUserAccount(companyUserAccount);
//        this.setDeleteOrNot(Boolean.FALSE);
//        this.setApprovedOrNot(Boolean.FALSE);
//    }
    
    public CompanyUser(String firstName, String lastName, String email, CompanyUserAccount companyUserAccount){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setCompanyUserAccount(companyUserAccount);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public CompanyUser(String firstName, String lastName, String email, CompanyUserAccount companyUserAccount, Boolean register){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setCompanyUserAccount(companyUserAccount);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(register);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the companyUserAccount
     */
    public CompanyUserAccount getCompanyUserAccount() {
        return companyUserAccount;
    }

    /**
     * @param companyUserAccount the companyUserAccount to set
     */
    public void setCompanyUserAccount(CompanyUserAccount companyUserAccount) {
        this.companyUserAccount = companyUserAccount;
    }
}
