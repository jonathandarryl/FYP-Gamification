/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.entity;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import Common.entity.Location;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author chongyangsun
 */
@Entity
public class AggregatedOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp creationTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double price;
    private String description;
    
    // following three are mapped by corresponding position in the list
    @OneToMany(mappedBy = "aggregatedOrder")
    private List<ServiceOrder> serviceOrders;
    @OneToMany(mappedBy = "aggregatedOrder")
    private List<ServiceContract> serviceContracts;
    private List<Long> companyIdList;
    
    private List<Long> productIdList;
    private List<Double> quantityList;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="sourceCountry")),
        @AttributeOverride(name="state", column=@Column(name="sourceState")),
        @AttributeOverride(name="city", column=@Column(name="sourceCity")),
        @AttributeOverride(name="street", column=@Column(name="sourceStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="sourceBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="sourcepostalCode"))
    })
    private Location sourceLoc;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="destCountry")),
        @AttributeOverride(name="state", column=@Column(name="destState")),
        @AttributeOverride(name="city", column=@Column(name="destCity")),
        @AttributeOverride(name="street", column=@Column(name="destStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="destBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="destpostalCode"))
    })
    private Location destLoc;
    private Long warehouseId;
    private String warehouseName;
    
    private Double totalQuantity;
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Boolean paidOrNot;
    private Boolean fulfilledOrNot;
    private Boolean published;
    @OneToOne(mappedBy = "aggregatedOrder")
    private AggregationPost aggregationPost;

    public AggregatedOrder() {
    }

    public AggregatedOrder(Timestamp startTime, Timestamp endTime, Double price, Location sourceLoc, Location destLoc, 
            Long warehouseId, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) {
        Date currentDate = new Date();
        Timestamp currentTime = new Timestamp(currentDate.getTime());
        this.setCreationTime(currentTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.sourceLoc = sourceLoc;
        this.destLoc = destLoc;
        this.warehouseId = warehouseId;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        
        this.fulfilledOrNot = Boolean.FALSE;
        this.paidOrNot = Boolean.FALSE;
        this.published = Boolean.FALSE;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getSourceLoc() {
        return sourceLoc;
    }

    public void setSourceLoc(Location sourceLoc) {
        this.sourceLoc = sourceLoc;
    }

    public Location getDestLoc() {
        return destLoc;
    }

    public void setDestLoc(Location destLoc) {
        this.destLoc = destLoc;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public Boolean isPaidOrNot() {
        return paidOrNot;
    }

    public void setPaidOrNot(Boolean paidOrNot) {
        this.paidOrNot = paidOrNot;
    }

    public Boolean isFulfilledOrNot() {
        return fulfilledOrNot;
    }

    public void setFulfilledOrNot(Boolean fulfilledOrNot) {
        this.fulfilledOrNot = fulfilledOrNot;
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
        if (!(object instanceof AggregatedOrder)) {
            return false;
        }
        AggregatedOrder other = (AggregatedOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceOrder[ id=" + id + " ]";
    }


    /**
     * @return the serviceOrders
     */
    public List<ServiceOrder> getServiceOrders() {
        return serviceOrders;
    }

    /**
     * @param serviceOrders the serviceOrders to set
     */
    public void setServiceOrders(List<ServiceOrder> serviceOrders) {
        this.serviceOrders = serviceOrders;
    }

    /**
     * @return the companyIdList
     */
    public List<Long> getCompanyIdList() {
        return companyIdList;
    }

    /**
     * @param companyIdList the companyIdList to set
     */
    public void setCompanyIdList(List<Long> companyIdList) {
        this.companyIdList = companyIdList;
    }

    /**
     * @return the productIdList
     */
    public List<Long> getProductIdList() {
        return productIdList;
    }

    /**
     * @param productIdList the productIdList to set
     */
    public void setProductIdList(List<Long> productIdList) {
        this.productIdList = productIdList;
    }

    /**
     * @return the quantityList
     */
    public List<Double> getQuantityList() {
        return quantityList;
    }

    /**
     * @param quantityList the quantityList to set
     */
    public void setQuantityList(List<Double> quantityList) {
        this.quantityList = quantityList;
    }

    /**
     * @return the creationTime
     */
    public Timestamp getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @return the startTime
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Timestamp getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the serviceContracts
     */
    public List<ServiceContract> getServiceContracts() {
        return serviceContracts;
    }

    /**
     * @param serviceContracts the serviceContracts to set
     */
    public void setServiceContracts(List<ServiceContract> serviceContracts) {
        this.serviceContracts = serviceContracts;
    }

    /**
     * @return the published
     */
    public Boolean getPublished() {
        return published;
    }

    /**
     * @param published the published to set
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }

    /**
     * @return the warehouseName
     */
    public String getWarehouseName() {
        return warehouseName;
    }

    /**
     * @param warehouseName the warehouseName to set
     */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    /**
     * @return the totalQuantity
     */
    public Double getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * @param totalQuantity the totalQuantity to set
     */
    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    /**
     * @return the aggregationPost
     */
    public AggregationPost getAggregationPost() {
        return aggregationPost;
    }

    /**
     * @param aggregationPost the aggregationPost to set
     */
    public void setAggregationPost(AggregationPost aggregationPost) {
        this.aggregationPost = aggregationPost;
    }
    
}
