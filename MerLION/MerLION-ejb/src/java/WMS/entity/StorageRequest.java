/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.entity;

import Common.entity.Company;
import OES.entity.Product;
import java.io.Serializable;
import java.sql.Timestamp;
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
public class StorageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;   
    private Timestamp creationTime;
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    @ManyToOne
    private Product product;
    private Double quantity;
    @ManyToOne
    private Warehouse warehouse;
    @ManyToOne
    private Company requestInitiator;
    @ManyToOne
    private Company requestDealer;
    
    private String requestSource;
    
    private Boolean fulfilledOrNot;
    private Boolean rejectedOrNot;

    public StorageRequest() {
    }

    public StorageRequest(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer, String requestSource, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.product = product;
        this.quantity = quantity;
        this.warehouse = warehouse;
        this.requestInitiator = requestInitiator;
        this.requestDealer = requestDealer;
        this.requestSource = requestSource;
        this.fulfilledOrNot = Boolean.FALSE;
        this.rejectedOrNot = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPerishable() {
        return perishable;
    }

    public void setPerishable(Boolean perishable) {
        this.perishable = perishable;
    }

    public Boolean getFlammable() {
        return flammable;
    }

    public void setFlammable(Boolean flammable) {
        this.flammable = flammable;
    }

    public Boolean getPharmaceutical() {
        return pharmaceutical;
    }

    public void setPharmaceutical(Boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    public Boolean getHighValue() {
        return highValue;
    }

    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Company getRequestInitiator() {
        return requestInitiator;
    }

    public void setRequestInitiator(Company requestInitiator) {
        this.requestInitiator = requestInitiator;
    }

    public Company getRequestDealer() {
        return requestDealer;
    }

    public void setRequestDealer(Company requestDealer) {
        this.requestDealer = requestDealer;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

    public Boolean getFulfilledOrNot() {
        return fulfilledOrNot;
    }

    public void setFulfilledOrNot(Boolean fulfilledOrNot) {
        this.fulfilledOrNot = fulfilledOrNot;
    }

    public Boolean getRejectedOrNot() {
        return rejectedOrNot;
    }

    public void setRejectedOrNot(Boolean rejectedOrNot) {
        this.rejectedOrNot = rejectedOrNot;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
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
        if (!(object instanceof StorageRequest)) {
            return false;
        }
        StorageRequest other = (StorageRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WMS.entity.StorageRequest[ id=" + id + " ]";
    }
    
}
