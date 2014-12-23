/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WMS.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author songhan
 */
@Entity
public class Shelf implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String regionCode;
    private Integer number;
    private String serialCode;
    private Integer capacity;
    private Double curQuantity;
    private Boolean availability;
    @ManyToOne
    private Warehouse warehouse;
    @ManyToOne
    private Inventory inventory;
    private Boolean archivedOrNot;

    public Shelf() {
    }

    public Shelf(String regionCode, Integer number, String serialCode, Integer capacity, Warehouse warehouse) {
        this.regionCode = regionCode;
        this.number = number;
        this.serialCode = serialCode;
        this.capacity = capacity;
        this.curQuantity = 0.0;
        this.availability = Boolean.TRUE;
        this.warehouse = warehouse;
        this.archivedOrNot = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getSerialCode() {
        return serialCode;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getCurQuantity() {
        return curQuantity;
    }

    public void setCurQuantity(Double curQuantity) {
        this.curQuantity = curQuantity;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
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
        if (!(object instanceof Shelf)) {
            return false;
        }
        Shelf other = (Shelf) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WMS.entity.Shelf[ id=" + id + " ]";
    }
    
}
