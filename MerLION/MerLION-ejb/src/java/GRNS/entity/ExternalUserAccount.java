/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.entity;

import Common.entity.Account;
import Common.entity.ExternalCompany;
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
public class ExternalUserAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private String contact;
    private boolean activated;
    private String verificationKey;
    @OneToOne
    private ExternalCompany externalCompany;
    
    public ExternalUserAccount() {}
    
    public ExternalUserAccount(String username, String password, String salt, ExternalCompany company, String firstName, String lastName, 
            String email, String contact, String verificationKey) {
        Date dateOfIssue = new Date();
        this.setDateOfIssue(dateOfIssue);
        this.setUsername(username);
        this.setPassword(password);
        this.setSalt(salt);
        this.setExternalCompany(company);
        this.setCompany(company);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setContact(contact);
        this.setActivated(false);
        this.setVerificationKey(verificationKey);
        this.setApprovedOrNot(Boolean.TRUE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setLockedOrNot(Boolean.FALSE);
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
        if (!(object instanceof ExternalUserAccount)) {
            return false;
        }
        ExternalUserAccount other = (ExternalUserAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.ExternalUserAccount[ id=" + id + " ]";
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the profilePhoto
     */
    public String getProfilePhoto() {
        return profilePhoto;
    }

    /**
     * @param profilePhoto the profilePhoto to set
     */
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    /**
     * @return the activated
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return the verificationKey
     */
    public String getVerificationKey() {
        return verificationKey;
    }

    /**
     * @param verificationKey the verificationKey to set
     */
    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    /**
     * @return the externalCompany
     */
    public ExternalCompany getExternalCompany() {
        return externalCompany;
    }

    /**
     * @param externalCompany the externalCompany to set
     */
    public void setExternalCompany(ExternalCompany externalCompany) {
        this.externalCompany = externalCompany;
    }
    
}
