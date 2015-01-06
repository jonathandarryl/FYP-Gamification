/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import CRM.entity.SalesLead;
import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceContract;
import CRM.entity.ServiceQuotation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author chongyangsun
 */
@Entity
public class PartnerCompany extends Company implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToMany(mappedBy="pCompany")
    private List<ServiceCatalog> svcCatalogList; 
    
    @OneToMany(mappedBy="pCompany")
    private List<ServiceQuotation> serviceQuotationList;
    
    @OneToMany(mappedBy="ownerCompany")
    private List<SalesLead> salesLeadList = new ArrayList<>();

    @OneToMany(mappedBy = "provider")
    private List<ServiceContract> providingServiceContractList;

    public PartnerCompany() {
    }

    public PartnerCompany(String companyName, String companyType, String contactNo) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public PartnerCompany(String companyName, String companyType, String contactNo, Boolean register) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(register);
    }

    public List<ServiceCatalog> getSvcCatalogList() {
        return svcCatalogList;
    }

    public void setSvcCatalogList(List<ServiceCatalog> svcCatalogList) {
        this.svcCatalogList = svcCatalogList;
    }

    public List<SalesLead> getSalesLeadList() {
        return salesLeadList;
    }

    public void setSalesLeadList(List<SalesLead> salesLeadList) {
        this.salesLeadList = salesLeadList;
    }

    public List<ServiceQuotation> getServiceQuotationList() {
        return serviceQuotationList;
    }

    public void setServiceQuotationList(List<ServiceQuotation> serviceQuotationList) {
        this.serviceQuotationList = serviceQuotationList;
    }

    public List<ServiceContract> getProvidingServiceContractList() {
        return providingServiceContractList;
    }

    public void setProvidingServiceContractList(List<ServiceContract> providingServiceContractList) {
        this.providingServiceContractList = providingServiceContractList;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
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
        if (!(object instanceof PartnerCompany)) {
            return false;
        }
        PartnerCompany other = (PartnerCompany) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRMS.entity.PartnerCompany[ id=" + id + " ]";
    }
    
}
