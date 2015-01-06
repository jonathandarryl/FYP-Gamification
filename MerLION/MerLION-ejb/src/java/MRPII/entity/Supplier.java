/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

import Common.entity.CustomerCompany;
import OES.entity.Product;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author songhan
 */
@Entity
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;  //Unique
    @ManyToMany(mappedBy="supplierList")
    private List<Product> supplyList = new ArrayList<>();
    private HashMap<Product, Integer> lagTimeMap = new HashMap<>();
    private List<String> allSupplyList = new ArrayList<>();
    private Boolean archivedOrNot;
    private String email;
    @ManyToOne
    private CustomerCompany supplyToCompany;

    public Supplier() {
    }

    public Supplier(String name, String email, CustomerCompany supplyToCompany, List<Product> supplyList, HashMap<Product, Integer> lagTimeMap, List<String> allSupplyList) {
        this.name = name;
        this.email=email;
        this.supplyToCompany = supplyToCompany;
        this.supplyList = supplyList;
        this.lagTimeMap = lagTimeMap;
        this.allSupplyList = allSupplyList;
        this.archivedOrNot = Boolean.FALSE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getSupplyList() {
        return supplyList;
    }

    public void setSupplyList(List<Product> supplyList) {
        this.supplyList = supplyList;
    }

    public HashMap<Product, Integer> getLagTimeMap() {
        return lagTimeMap;
    }

    public void setLagTimeMap(HashMap<Product, Integer> lagTimeMap) {
        this.lagTimeMap = lagTimeMap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }

    public CustomerCompany getSupplyToCompany() {
        return supplyToCompany;
    }

    public void setSupplyToCompany(CustomerCompany supplyToCompany) {
        this.supplyToCompany = supplyToCompany;
    }

    public List<String> getAllSupplyList() {
        return allSupplyList;
    }

    public void setAllSupplyList(List<String> allSupplyList) {
        this.allSupplyList = allSupplyList;
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
        if (!(object instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MRPII.Supplier[ id=" + id + " ]";
    }
}
