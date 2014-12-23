/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import CRM.entity.SubscriptionSchema;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author chongyangsun
 */
@Entity
public class SystemAdmin extends User implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne(cascade={CascadeType.ALL})
    private SystemAdminAccount systemAdminAccount;
    
    @OneToMany(mappedBy="owner")
    private List<SubscriptionSchema> subscriptionSchema;
    
    public SystemAdmin(){
    }
    
//    public SystemAdmin(SystemAdminAccount systemAdminAccount) {
//        this.setSystemAdminAccount(systemAdminAccount);
//        this.setDeleteOrNot(Boolean.FALSE);
//        this.setApprovedOrNot(Boolean.FALSE);
//    }
    
    public SystemAdmin(String firstName, String lastName, String email, SystemAdminAccount systemAdminAccount){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setSystemAdminAccount(systemAdminAccount);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public SystemAdmin(String firstName, String lastName, String email, SystemAdminAccount systemAdminAccount, Boolean register){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setSystemAdminAccount(systemAdminAccount);
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
     * @return the systemAdminAccount
     */
    public SystemAdminAccount getSystemAdminAccount() {
        return systemAdminAccount;
    }

    /**
     * @param systemAdminAccount the systemAdminAccount to set
     */
    public void setSystemAdminAccount(SystemAdminAccount systemAdminAccount) {
        this.systemAdminAccount = systemAdminAccount;
    }

    public List<SubscriptionSchema> getSubscriptionSchema() {
        return subscriptionSchema;
    }

    public void setSubscriptionSchema(List<SubscriptionSchema> subscriptionSchema) {
        this.subscriptionSchema = subscriptionSchema;
    }
}
