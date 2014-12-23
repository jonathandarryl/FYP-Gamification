/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class KeyClient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer cumulativeMembershipPoints;
    private String membershipTier;
    private String companyName;
    private String contactNo;
    private Integer creditPoints;

    @ManyToOne
    private Company company;

    @ManyToOne
    private MembershipSchema membershipSchema;

    public KeyClient() {
        this.creditPoints = 1000;
    }

    public Integer getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(Integer creditPoints) {
        this.creditPoints = creditPoints;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
        
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }
    
    public MembershipSchema getMembershipSchema() {
        return membershipSchema;
    }

    public void setMembershipSchema(MembershipSchema membershipSchema) {
        this.membershipSchema = membershipSchema;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getCumulativeMembershipPoints() {
        return cumulativeMembershipPoints;
    }

    public void setCumulativeMembershipPoints(Integer cumulativeMembershipPoints) {
        this.cumulativeMembershipPoints = cumulativeMembershipPoints;
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
        if (!(object instanceof KeyClient)) {
            return false;
        }
        KeyClient other = (KeyClient) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.keyClient[ id=" + id + " ]";
    }
    
}
