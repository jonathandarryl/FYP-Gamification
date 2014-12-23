/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WMS.entity;

import Common.entity.Company;
import Common.entity.Location;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author songhan
 */
@Entity
public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer capacity;
    private String contactNo;
    private Integer utilizedCapacity;
    private Integer spareCapacity;
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    @ManyToOne
    private Company ownerCompany;
    
    
    @OneToMany(mappedBy="warehouse")
    private List<Shelf> shelfList = new ArrayList<>();
    
    @Embedded
    private Location location;
    private Boolean archivedOrNot;
    
    @ManyToMany
    private List<FinishedGood> finishedGoodList = new ArrayList<>();
    private HashMap<FinishedGood, Double> finishedGoodMap = new HashMap<>();
    @ManyToMany
    private List<RawMaterial> rawMaterialList = new ArrayList<>();
    private HashMap<RawMaterial, Double> rawMaterialMap = new HashMap<>();    
    
    private List<String> regionList = new ArrayList<>();
    
    private Double pricePerCapacityPerDay;

    public Warehouse() {
    }
    
    //A new warehouse does not have its shelf list since those shelves have not been created yet. Hence the capacity is 0 at the beginning
    public Warehouse(String name, String contactNo, Company ownerCompany, Location location, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Double pricePerCapacityPerDay) {
        this.name = name;
        this.capacity = 0; 
        this.contactNo = contactNo;        
        this.utilizedCapacity = 0;
        this.spareCapacity = 0;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.ownerCompany = ownerCompany;
        this.location = location;
        this.pricePerCapacityPerDay = pricePerCapacityPerDay;
        this.archivedOrNot = Boolean.FALSE;
    }

  
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Integer getUtilizedCapacity() {
        return utilizedCapacity;
    }

    public void setUtilizedCapacity(Integer utilizedCapacity) {
        this.utilizedCapacity = utilizedCapacity;
    }

    public Integer getSpareCapacity() {
        return spareCapacity;
    }

    public void setSpareCapacity(Integer spareCapacity) {
        this.spareCapacity = spareCapacity;
    }

    public Boolean isPerishable() {
        return perishable;
    }

    public void setPerishable(Boolean perishable) {
        this.perishable = perishable;
    }

    public Boolean isFlammable() {
        return flammable;
    }

    public void setFlammable(Boolean flammable) {
        this.flammable = flammable;
    }

    public Boolean isPharmaceutical() {
        return pharmaceutical;
    }

    public void setPharmaceutical(Boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    public Boolean isHighValue() {
        return highValue;
    }

    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }    

    public List<Shelf> getShelfList() {
        return shelfList;
    }

    public void setShelfList(List<Shelf> shelfList) {
        this.shelfList = shelfList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }

    public List<FinishedGood> getFinishedGoodList() {
        return finishedGoodList;
    }

    public void setFinishedGoodList(List<FinishedGood> finishedGoodList) {
        this.finishedGoodList = finishedGoodList;
    }

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public void setRawMaterialList(List<RawMaterial> rawMaterialList) {
        this.rawMaterialList = rawMaterialList;
    }

    public HashMap<FinishedGood, Double> getFinishedGoodMap() {
        return finishedGoodMap;
    }

    public void setFinishedGoodMap(HashMap<FinishedGood, Double> finishedGoodMap) {
        this.finishedGoodMap = finishedGoodMap;
    }

    public HashMap<RawMaterial, Double> getRawMaterialMap() {
        return rawMaterialMap;
    }

    public void setRawMaterialMap(HashMap<RawMaterial, Double> rawMaterialMap) {
        this.rawMaterialMap = rawMaterialMap;
    }

    
    
    public List<String> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<String> regionList) {
        this.regionList = regionList;
    }

    public Double getPricePerCapacityPerDay() {
        return pricePerCapacityPerDay;
    }

    public void setPricePerCapacityPerDay(Double pricePerCapacityPerDay) {
        this.pricePerCapacityPerDay = pricePerCapacityPerDay;
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
        if (!(object instanceof Warehouse)) {
            return false;
        }
        Warehouse other = (Warehouse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WMS.entity.Warehouse[ id=" + id + " ]";
    }
    
}
