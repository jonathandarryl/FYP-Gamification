/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.entity;

import Common.entity.Company;
import MRPII.entity.Production;
import OES.entity.Product;
import OES.entity.SalesOrder;
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
public class ExtractionRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp creationTime;
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
    @ManyToOne
    private Production production;
    @ManyToOne
    private SalesOrder salesOrder;
    
    private Boolean fulfilledOrNot;
    private Boolean rejectedOrNot;

    public ExtractionRequest() {
    }

    public ExtractionRequest(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer, String requestSource) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
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

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
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

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExtractionRequest)) {
            return false;
        }
        ExtractionRequest other = (ExtractionRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WMS.entity.ExtractionRequest[ id=" + id + " ]";
    }
    
}
