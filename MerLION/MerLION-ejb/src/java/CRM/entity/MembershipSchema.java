/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class MembershipSchema implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;          
    private String membershipTier;           
    private Integer boundaryPoint;   
    private Double discountPerOrder;   
    @OneToMany(mappedBy="membershipSchema")
    private List<KeyClient> keyClient;
    
    @ManyToOne
    private Company ownerCompany;

    public MembershipSchema() {
    }

    public MembershipSchema(String membershipTier, Integer boundaryPoint, Double discountPerOrder) {
        this.membershipTier = membershipTier;
        this.boundaryPoint = boundaryPoint;
        this.discountPerOrder = discountPerOrder;
    }

//    public String assignPointsToTier(Integer points){
//        for(int i=0; i<this.boundaryPoint.size(); i++){
//            if(points <= boundaryPoint.get(i)){
//                return this.membershipTier.get(i);
//            }
//        }
//        return this.membershipTier.get(this.membershipTier.size()-1);
//    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public Integer getBoundaryPoint() {
        return boundaryPoint;
    }

    public void setBoundaryPoint(Integer boundaryPoint) {
        this.boundaryPoint = boundaryPoint;
    }

    public Double getDiscountPerOrder() {
        return discountPerOrder;
    }

    public void setDiscountPerOrder(Double discountPerOrder) {
        this.discountPerOrder = discountPerOrder;
    }
    
    public List<KeyClient> getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(List<KeyClient> keyClient) {
        this.keyClient = keyClient;
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
        if (!(object instanceof MembershipSchema)) {
            return false;
        }
        MembershipSchema other = (MembershipSchema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.MembershipSchema[ id=" + id + " ]";
    }   
}
