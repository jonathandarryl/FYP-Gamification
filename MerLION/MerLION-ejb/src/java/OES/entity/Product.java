/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.entity;

import Common.entity.CustomerCompany;
import MRPII.entity.Supplier;
import WMS.entity.Inventory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * @author songhan
 */
@Inheritance(strategy=InheritanceType.JOINED)
@Entity
public abstract class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String productName;
    private Double productPrice;
    private String productDescription;
    @ManyToOne
    private CustomerCompany ownerCompany;
    private String ownerCompanyName;
    private Boolean archivedOrNot;
    private Boolean publicOrNot;
    private HashMap<Date, Double> monthlySalesRecord = new HashMap<>();
    @OneToMany(mappedBy="product")
    private Set<Inventory> inventoryList = new HashSet<>();
    private String productType; // only "FinishedGood" and "RawMaterial" are allowed in this module
    private String unit;
    private Double quantityInOneUnitCapacity;
    private Double inventoryLevel;
    private Double quantityInOneBatch;
    
    @ManyToMany
    private List<Supplier> supplierList = new ArrayList<>();

    
    public Product (){}
    
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Boolean isPublicOrNot() {
        return publicOrNot;
    }

    public void setPublicOrNot(Boolean publicOrNot) {
        this.publicOrNot = publicOrNot;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public CustomerCompany getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(CustomerCompany ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }

    public HashMap<Date, Double> getMonthlySalesRecord() {
        return monthlySalesRecord;
    }

    public void setMonthlySalesRecord(HashMap<Date, Double> monthlySalesRecord) {
        this.monthlySalesRecord = monthlySalesRecord;
    }

    public Set<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(Set<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantityInOneUnitCapacity() {
        return quantityInOneUnitCapacity;
    }

    public void setQuantityInOneUnitCapacity(Double quantityInOneUnitCapacity) {
        this.quantityInOneUnitCapacity = quantityInOneUnitCapacity;
    }

    public Double getInventoryLevel() {
        return inventoryLevel;
    }

    public void setInventoryLevel(Double inventoryLevel) {
        this.inventoryLevel = inventoryLevel;
    }

    public Double getQuantityInOneBatch() {
        return quantityInOneBatch;
    }

    public void setQuantityInOneBatch(Double quantityInOneBatch) {
        this.quantityInOneBatch = quantityInOneBatch;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
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
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OES.Product[ id=" + id + " ]";
    }
    
}
