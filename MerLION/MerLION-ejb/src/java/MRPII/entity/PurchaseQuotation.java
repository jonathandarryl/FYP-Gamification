/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

import Common.entity.Company;
import OES.entity.Product;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author songhan
 */
@Entity
public class PurchaseQuotation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double totalPrice;
    private Timestamp purchaseQuotationDate;
    
    private Timestamp scheduledSentDate;
    private Integer scheduledSentYear;
    private Integer scheduledSentMonth;
    private Integer scheduledSentWeek;
    
    private Timestamp scheduledArrivalDate;
    private Integer scheduledArrivalYear;
    private Integer scheduledArrivalMonth;
    private Integer scheduledArrivalWeek;
    
    @OneToOne
    private PurchaseOrder purchaseOrder;
    private String purchaseType; //"RawMaterial" or "FinishedGood"
    
    @ManyToOne
    private Company buyerCompany;
    
    @ManyToOne
    private Supplier supplier;
    
    @ManyToOne
    private Product product;
    private Double quantity;
    private Integer batchNumber;
    private Boolean approvedOrNot;
        
    public PurchaseQuotation() {
    }

    public PurchaseQuotation(Timestamp scheduledSentDate, Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek, 
            Timestamp scheduledArrivalDate, Integer scheduledArrivalYear, Integer scheduledArrivalMonth, Integer scheduledArrivalWeek, 
            String purchaseType, Company buyerCompany, Supplier supplier, Product product, Integer batchNumber) {
        
        this.purchaseQuotationDate = new Timestamp(System.currentTimeMillis());
        this.scheduledSentDate = scheduledSentDate;
        this.scheduledSentYear = scheduledSentYear;
        this.scheduledSentMonth = scheduledSentMonth;
        this.scheduledSentWeek = scheduledSentWeek;
        this.scheduledArrivalDate = scheduledArrivalDate;
        this.scheduledArrivalYear = scheduledArrivalYear;
        this.scheduledArrivalMonth = scheduledArrivalMonth;
        this.scheduledArrivalWeek = scheduledArrivalWeek;
        this.purchaseType = purchaseType;
        this.buyerCompany = buyerCompany;
        this.supplier = supplier;
        this.product = product;
        this.quantity = product.getQuantityInOneBatch()*batchNumber;
        this.batchNumber = batchNumber;
        this.totalPrice = product.getProductPrice()*quantity;
        this.approvedOrNot=Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getPurchaseQuotationDate() {
        return purchaseQuotationDate;
    }

    public void setPurchaseQuotationDate(Timestamp purchaseQuotationDate) {
        this.purchaseQuotationDate = purchaseQuotationDate;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
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

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getScheduledSentYear() {
        return scheduledSentYear;
    }

    public void setScheduledSentYear(Integer scheduledSentYear) {
        this.scheduledSentYear = scheduledSentYear;
    }

    public Integer getScheduledSentMonth() {
        return scheduledSentMonth;
    }

    public void setScheduledSentMonth(Integer scheduledSentMonth) {
        this.scheduledSentMonth = scheduledSentMonth;
    }

    public Boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    public Integer getScheduledSentWeek() {
        return scheduledSentWeek;
    }

    public void setScheduledSentWeek(Integer scheduledSentWeek) {
        this.scheduledSentWeek = scheduledSentWeek;
    }

    public Integer getScheduledArrivalYear() {
        return scheduledArrivalYear;
    }

    public void setScheduledArrivalYear(Integer scheduledArrivalYear) {
        this.scheduledArrivalYear = scheduledArrivalYear;
    }

    public Integer getScheduledArrivalMonth() {
        return scheduledArrivalMonth;
    }

    public void setScheduledArrivalMonth(Integer scheduledArrivalMonth) {
        this.scheduledArrivalMonth = scheduledArrivalMonth;
    }

    public Integer getScheduledArrivalWeek() {
        return scheduledArrivalWeek;
    }

    public void setScheduledArrivalWeek(Integer scheduledArrivalWeek) {
        this.scheduledArrivalWeek = scheduledArrivalWeek;
    }

    public Company getBuyerCompany() {
        return buyerCompany;
    }

    public void setBuyerCompany(Company buyerCompany) {
        this.buyerCompany = buyerCompany;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Timestamp getScheduledSentDate() {
        return scheduledSentDate;
    }

    public void setScheduledSentDate(Timestamp scheduledSentDate) {
        this.scheduledSentDate = scheduledSentDate;
    }

    public Timestamp getScheduledArrivalDate() {
        return scheduledArrivalDate;
    }

    public void setScheduledArrivalDate(Timestamp scheduledArrivalDate) {
        this.scheduledArrivalDate = scheduledArrivalDate;
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
        if (!(object instanceof PurchaseQuotation)) {
            return false;
        }
        PurchaseQuotation other = (PurchaseQuotation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OES.PurchaseQuotation[ id=" + id + " ]";
    }
    
}
