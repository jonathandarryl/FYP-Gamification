/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

//import WMS.entity.Warehouse;
import CRM.entity.KeyClient;
import CRM.entity.MembershipSchema;
import CRM.entity.SalesLead;
import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceContract;
import CRM.entity.SubscriptionPayment;
import CRM.entity.SubscriptionPaymentReceipt;
import CRM.entity.SubscriptionSchema;
import TMS.entity.Drivers;
import TMS.entity.Routes;
import TMS.entity.Vehicles;
import WMS.entity.Warehouse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author chongyangsun
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String companyName;
    private String companyType;
    private String contactNo;
    private String VAT;
    private Boolean lockedOrNot;
    private Boolean deleteOrNot;
    private boolean approvedOrNot;
    @Embedded
    private Location location;
    
    @OneToMany(mappedBy="ownerCompany")
    private List<Warehouse> ownedWarehouseList = new ArrayList<>();
    @OneToMany (mappedBy="company")
    private List<Department> department;
    @OneToMany (mappedBy="company")
    private List<Account> account;
    @ManyToOne
    private SubscriptionSchema subscriptionSchema;
    @OneToMany(mappedBy="company")
    private List<SubscriptionPayment> subscriptionPayment;  
    @OneToMany(mappedBy="company")
    private List<SubscriptionPaymentReceipt> subscriptionPaymentReceipt;
    @ManyToMany
    private List<SalesLead> salesLead;
    @OneToMany(mappedBy="client")
    private List<ServiceContract> outsourcingServiceContract;
    @OneToMany(mappedBy="pCompany")
    private List<Routes> routes;
    @OneToMany(mappedBy="ownerCompany")
    private List<Vehicles> vehicleList;
    @OneToMany(mappedBy="ownerCompany")
    private List<Drivers> driverList;
    @OneToMany (mappedBy="company")
    private List<KeyClient> keyClient;  
    @OneToMany(mappedBy="ownerCompany")
    private List<MembershipSchema> membershipSchema;
    
    @OneToMany(mappedBy="company")
    private List<ServiceCatalog> svcCatalogList; 

    public Company() {
    }
    
    public Company(String companyName, String companyType, String contactNo) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public Company(String companyName, String companyType, String contactNo, Boolean register) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(register);
    }


    public List<KeyClient> getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(List<KeyClient> keyClient) {
        this.keyClient = keyClient;
    }

    public List<SubscriptionPayment> getSubscriptionPayment() {
        return subscriptionPayment;
    }

    public void setSubscriptionPayment(List<SubscriptionPayment> subscriptionPayment) {
        this.subscriptionPayment = subscriptionPayment;
    }

    public List<SubscriptionPaymentReceipt> getSubscriptionPaymentReceipt() {
        return subscriptionPaymentReceipt;
    }

    public void setSubscriptionPaymentReceipt(List<SubscriptionPaymentReceipt> subscriptionPaymentReceipt) {
        this.subscriptionPaymentReceipt = subscriptionPaymentReceipt;
    }

    public SubscriptionSchema getSubscriptionSchema() {
        return subscriptionSchema;
    }

    public void setSubscriptionSchema(SubscriptionSchema subscriptionSchema) {
        this.subscriptionSchema = subscriptionSchema;
    }

    public List<Department> getDepartment() {
        return department;
    }

    public void setDepartment(List<Department> department) {
        this.department = department;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public List<Warehouse> getOwnedWarehouseList() {
        return ownedWarehouseList;
    }

    public void setOwnedWarehouseList(List<Warehouse> ownedWarehouseList) {
        this.ownedWarehouseList = ownedWarehouseList;
    }

    public List<SalesLead> getSalesLead() {
        return salesLead;
    }

    public void setSalesLead(List<SalesLead> salesLead) {
        this.salesLead = salesLead;
    }

    public List<ServiceContract> getOutsourcingServiceContract() {
        return outsourcingServiceContract;
    }

    public void setOutsourcingServiceContract(List<ServiceContract> outsourcingServiceContract) {
        this.outsourcingServiceContract = outsourcingServiceContract;
    }

    public List<Vehicles> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicles> vehicleList) {
        this.vehicleList = vehicleList;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "common.entity.Company[ id=" + id + " ]";
    }

    /**
     * @return the lockedOrNot
     */
    public Boolean getLockedOrNot() {
        return lockedOrNot;
    }

    /**
     * @param lockedOrNot the lockedOrNot to set
     */
    public void setLockedOrNot(Boolean lockedOrNot) {
        this.lockedOrNot = lockedOrNot;
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
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the membershipSchema
     */
    public List<MembershipSchema> getMembershipSchema() {
        return membershipSchema;
    }

    /**
     * @param membershipSchema the membershipSchema to set
     */
    public void setMembershipSchema(List<MembershipSchema> membershipSchema) {
        this.membershipSchema = membershipSchema;
    }

    public List<Drivers> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Drivers> driverList) {
        this.driverList = driverList;
    }    

    public List<ServiceCatalog> getSvcCatalogList() {
        return svcCatalogList;
    }

    public void setSvcCatalogList(List<ServiceCatalog> svcCatalogList) {
        this.svcCatalogList = svcCatalogList;
    }

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }

    /**
     * @return the VAT
     */
    public String getVAT() {
        return VAT;
    }

    /**
     * @param VAT the VAT to set
     */
    public void setVAT(String VAT) {
        this.VAT = VAT;
    }
    
}