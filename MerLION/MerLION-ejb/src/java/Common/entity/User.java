/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author chongyangsun
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private Boolean deleteOrNot;
    private boolean approvedOrNot;
    
    @ManyToOne
    private Title title = new Title();
    
    @OneToOne(cascade={CascadeType.ALL})
    private Account account;
    
    public User(){}

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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Common.entity.User[ id=" + id + " ]";
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
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return the deleteOrNot
     */
    public Boolean getDeleteOrNot() {
        return deleteOrNot;
    }

    /**
     * @param deleteOrNot the deleteOrNot to set
     */
    public void setDeleteOrNot(Boolean deleteOrNot) {
        this.deleteOrNot = deleteOrNot;
    }

    /**
     * @return the approvedOrNot
     */
    public boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    /**
     * @param approvedOrNot the approvedOrNot to set
     */
    public void setApprovedOrNot(boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(Title title) {
        this.title = title;
    }
    
}
