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
public class CompanyAdmin extends User implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade={CascadeType.ALL})
    private CompanyAdminAccount companyAdminAccount;
    
    public CompanyAdmin(){
    }
    
//    public CompanyAdmin(CompanyAdminAccount companyAdminAccount){
//        this.setCompanyAdminAccount(companyAdminAccount);
//        this.setDeleteOrNot(Boolean.FALSE);
//        this.setApprovedOrNot(Boolean.FALSE);
//    }
   
    public CompanyAdmin(String firstName, String lastName, String email, CompanyAdminAccount companyAdminAccount){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setCompanyAdminAccount(companyAdminAccount);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public CompanyAdmin(String firstName, String lastName, String email, CompanyAdminAccount companyAdminAccount, Boolean register){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setCompanyAdminAccount(companyAdminAccount);
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
     * @return the companyAdminAccount
     */
    public CompanyAdminAccount getCompanyAdminAccount() {
        return companyAdminAccount;
    }

    /**
     * @param companyAdminAccount the companyAdminAccount to set
     */
    public void setCompanyAdminAccount(CompanyAdminAccount companyAdminAccount) {
        this.companyAdminAccount = companyAdminAccount;
    }
}
