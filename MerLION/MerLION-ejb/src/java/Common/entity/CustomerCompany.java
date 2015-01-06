/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import MRPII.entity.RawMaterial;
import MRPII.entity.Supplier;
import OES.entity.Customer;
import OES.entity.FinishedGood;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author chongyangsun
 */
@Entity
public class CustomerCompany extends Company implements Serializable {
    @OneToMany(mappedBy="ownerCompany")
    private List<FinishedGood> productList;
    @OneToMany(mappedBy="ownerCompany")
    private List<RawMaterial> rawMaterialList;
    @OneToMany(mappedBy="seller")
    private List<Customer> customerList;
    @OneToMany(mappedBy="supplyToCompany")
    private List<Supplier> supplierList;

    public CustomerCompany() {
    }

    public CustomerCompany(String companyName, String companyType, String contactNo) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public CustomerCompany(String companyName, String companyType, String contactNo, Boolean register) {
        this.setCompanyName(companyName);
        this.setCompanyType(companyType);
        this.setContactNo(contactNo);
        this.setLockedOrNot(Boolean.FALSE);
        this.setDeleteOrNot(Boolean.FALSE);
        this.setApprovedOrNot(register);
    }

    public List<FinishedGood> getProductList() {
        return productList;
    }

    public void setProductList(List<FinishedGood> productList) {
        this.productList = productList;
    }

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public void setRawMaterialList(List<RawMaterial> rawMaterialList) {
        this.rawMaterialList = rawMaterialList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

}
