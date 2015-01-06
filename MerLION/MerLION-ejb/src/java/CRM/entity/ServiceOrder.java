/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Location;
import GRNS.entity.AggregatedOrder;
import GRNS.entity.CommissionPayment;
import OES.entity.Product;
import TMS.entity.TransOrder;
import WMS.entity.Warehouse;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
 * @author HanXiangyu
 */
@Entity
public class ServiceOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp creationTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double price;
    private String description;
    // Boolean 
    @ManyToOne
    private Product product;
    private Double quantity;//this quantitty is for product in product's unit
    private Double capacity;// this capacity is for total order quantity in unit
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
//    private String serviceType; //Only "Transportation" and "Warehouse" are allowed
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Boolean approvedOrNot;
    private Boolean paidOrNot;
    private Boolean fulfilledOrNot;
    
    private Boolean isToEstablishTransOrder;
    private Boolean isToEstablishWarehouseOrder;
    
    private Boolean establishedTransOrderOrNot;
    private Boolean establishedWarehouseOrderOrNot;
    
    @ManyToOne
    private ServiceContract serviceContract;
    @OneToOne(mappedBy="serviceOrder")
    private ServiceInvoice serviceInvoice;
    @OneToOne(mappedBy="serviceOrder")
    private TransOrder transOrder;
    @OneToOne(mappedBy="serviceOrder")
    private WarehouseOrder warehouseOrder;

    @ManyToOne
    private AggregatedOrder aggregatedOrder;
    private Boolean isToAggregate;
    private Boolean aggregated;
    private Boolean addedToCart;
    private Boolean fromGRNS;
    private String postType;
    private Long postId;
    
    @ManyToOne
    private Warehouse sourceWarehouse;
    
    private Boolean isToOutsource;
    private Long toServiceOrderId;
    
    private Boolean isFromOutsourcing;
    private Long fromServiceOrderId;

    public ServiceOrder() {
    }

    public ServiceOrder(Timestamp startTime, Timestamp endTime, Double price, String description, Product product, Double quantity, Double capacity, Location sourceLoc, 
            Location destLoc, Long warehouseId, Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, ServiceContract serviceContract) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.description = description;
        this.product = product;
        this.quantity = quantity;
        this.capacity = capacity;
        this.sourceLoc = sourceLoc;
        this.destLoc = destLoc;
        this.warehouseId = warehouseId;
        
        this.isToEstablishTransOrder = isToEstablishTransOrder;
        this.isToEstablishWarehouseOrder = isToEstablishWarehouseOrder;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.serviceContract = serviceContract;
        
        this.approvedOrNot = Boolean.FALSE;
        this.fulfilledOrNot = Boolean.FALSE;
        this.paidOrNot = Boolean.FALSE;
        this.establishedTransOrderOrNot = Boolean.FALSE;
        this.establishedWarehouseOrderOrNot = Boolean.FALSE;
        this.isToAggregate = Boolean.TRUE;
        this.aggregated = Boolean.FALSE;
        this.setAddedToCart(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
        this.setFromGRNS(Boolean.FALSE);
        this.isToOutsource = Boolean.FALSE;
        this.isFromOutsourcing = Boolean.FALSE;
    }
    
    public ServiceOrder(Timestamp startTime, Timestamp endTime, Double price, String description, Product product, Double quantity, Double capacity, Location sourceLoc, 
            Location destLoc, Long warehouseId, Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, ServiceContract serviceContract, Boolean isToAggregate) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.description = description;
        this.product = product;
        this.quantity = quantity;
        this.capacity = capacity;
        this.sourceLoc = sourceLoc;
        this.destLoc = destLoc;
        this.warehouseId = warehouseId;
        
        this.isToEstablishTransOrder = isToEstablishTransOrder;
        this.isToEstablishWarehouseOrder = isToEstablishWarehouseOrder;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.serviceContract = serviceContract;
        
        this.approvedOrNot = Boolean.FALSE;
        this.fulfilledOrNot = Boolean.FALSE;
        this.paidOrNot = Boolean.FALSE;
        this.establishedTransOrderOrNot = Boolean.FALSE;
        this.establishedWarehouseOrderOrNot = Boolean.FALSE;
        this.isToAggregate = isToAggregate;
        this.aggregated = Boolean.FALSE;
        this.setAddedToCart(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
        this.setFromGRNS(Boolean.FALSE);
        this.isToOutsource = Boolean.FALSE;
        this.isFromOutsourcing = Boolean.FALSE;
    }
    
    // for GRNS order creation
    public ServiceOrder(Timestamp startTime, Timestamp endTime, Double price, String description, Product product, Double productQuantity,
            Double capacity, Location sourceLoc, Location destLoc, Long warehouseId, 
            Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, 
            ServiceContract serviceContract, Long postId, String postType) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setPrice(price);
        this.setDescription(description);
        this.setProduct(product);
        this.setQuantity(productQuantity);
        this.setCapacity(capacity);
        this.setSourceLoc(sourceLoc);
        this.setDestLoc(destLoc);
        this.setWarehouseId(warehouseId);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setApprovedOrNot(Boolean.FALSE);
        this.setPaidOrNot(Boolean.FALSE);
        this.setFulfilledOrNot(Boolean.FALSE);
        this.setIsToEstablishTransOrder(isToEstablishTransOrder);
        this.setIsToEstablishWarehouseOrder(isToEstablishWarehouseOrder);
        this.setEstablishedTransOrderOrNot(Boolean.FALSE);
        this.setEstablishedWarehouseOrderOrNot(Boolean.FALSE);
        this.setServiceContract(serviceContract);
        this.setIsToAggregate(Boolean.FALSE);
        this.setPostId(postId);
        this.setPostType(postType);
        this.setFromGRNS(Boolean.TRUE);
        this.isToOutsource = Boolean.FALSE;
        this.isFromOutsourcing = Boolean.FALSE;
    }
    
    public ServiceOrder(Timestamp startTime, Timestamp endTime, Double price, String description, Product product, Double quantity, Double capacity, Location sourceLoc, 
            Location destLoc, Long warehouseId, Boolean isToEstablishTransOrder, Boolean isToEstablishWarehouseOrder, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, 
            ServiceContract serviceContract, Boolean isToAggregate, Boolean isFromOutsourcing, Long fromServiceOrderId, Warehouse sourceWarehouse) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.description = description;
        this.product = product;
        this.quantity = quantity;
        this.capacity = capacity;
        this.sourceLoc = sourceLoc;
        this.destLoc = destLoc;
        this.warehouseId = warehouseId;
        
        this.isToEstablishTransOrder = isToEstablishTransOrder;
        this.isToEstablishWarehouseOrder = isToEstablishWarehouseOrder;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.serviceContract = serviceContract;
        
        this.approvedOrNot = Boolean.FALSE;
        this.fulfilledOrNot = Boolean.FALSE;
        this.paidOrNot = Boolean.FALSE;
        this.establishedTransOrderOrNot = Boolean.FALSE;
        this.establishedWarehouseOrderOrNot = Boolean.FALSE;
        this.isToAggregate = isToAggregate;
        this.aggregated = Boolean.FALSE;
        this.setAddedToCart(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
        this.setFromGRNS(Boolean.FALSE);
        this.isToOutsource = Boolean.FALSE;
        this.isFromOutsourcing = isFromOutsourcing;
        this.fromServiceOrderId = fromServiceOrderId;
        this.sourceWarehouse = sourceWarehouse;
    }

    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public ServiceInvoice getServiceInvoice() {
        return serviceInvoice;
    }

    public void setServiceInvoice(ServiceInvoice serviceInvoice) {
        this.serviceInvoice = serviceInvoice;
    }

    public TransOrder getTransOrder() {
        return transOrder;
    }

    public void setTransOrder(TransOrder transOrder) {
        this.transOrder = transOrder;
    }

    public WarehouseOrder getWarehouseOrder() {
        return warehouseOrder;
    }

    public void setWarehouseOrder(WarehouseOrder warehouseOrder) {
        this.warehouseOrder = warehouseOrder;
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

    public Boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
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

    public Boolean getEstablishedTransOrderOrNot() {
        return establishedTransOrderOrNot;
    }

    public void setEstablishedTransOrderOrNot(Boolean establishedTransOrderOrNot) {
        this.establishedTransOrderOrNot = establishedTransOrderOrNot;
    }

    public Boolean getEstablishedWarehouseOrderOrNot() {
        return establishedWarehouseOrderOrNot;
    }

    public void setEstablishedWarehouseOrderOrNot(Boolean establishedWarehouseOrderOrNot) {
        this.establishedWarehouseOrderOrNot = establishedWarehouseOrderOrNot;
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
        if (!(object instanceof ServiceOrder)) {
            return false;
        }
        ServiceOrder other = (ServiceOrder) object;
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
     * @return the aggregated
     */
    public Boolean getAggregated() {
        return aggregated;
    }

    /**
     * @param aggregated the aggregated to set
     */
    public void setAggregated(Boolean aggregated) {
        this.aggregated = aggregated;
    }

    /**
     * @return the aggregatedOrder
     */
    public AggregatedOrder getAggregatedOrder() {
        return aggregatedOrder;
    }

    /**
     * @param aggregatedOrder the aggregatedOrder to set
     */
    public void setAggregatedOrder(AggregatedOrder aggregatedOrder) {
        this.aggregatedOrder = aggregatedOrder;
    }

    public Boolean getIsToEstablishTransOrder() {
        return isToEstablishTransOrder;
    }

    public void setIsToEstablishTransOrder(Boolean isToEstablishTransOrder) {
        this.isToEstablishTransOrder = isToEstablishTransOrder;
    }

    public Boolean getIsToEstablishWarehouseOrder() {
        return isToEstablishWarehouseOrder;
    }

    public void setIsToEstablishWarehouseOrder(Boolean isToEstablishWarehouseOrder) {
        this.isToEstablishWarehouseOrder = isToEstablishWarehouseOrder;
    }

    public Boolean getIsToAggregate() {
        return isToAggregate;
    }

    public void setIsToAggregate(Boolean isToAggregate) {
        this.isToAggregate = isToAggregate;
    }

    /**
     * @return the addedToCart
     */
    public Boolean getAddedToCart() {
        return addedToCart;
    }

    /**
     * @param addedToCart the addedToCart to set
     */
    public void setAddedToCart(Boolean addedToCart) {
        this.addedToCart = addedToCart;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the postType
     */
    public String getPostType() {
        return postType;
    }

    /**
     * @param postType the postType to set
     */
    public void setPostType(String postType) {
        this.postType = postType;
    }

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /**
     * @return the fromGRNS
     */
    public Boolean getFromGRNS() {
        return fromGRNS;
    }

    /**
     * @param fromGRNS the fromGRNS to set
     */
    public void setFromGRNS(Boolean fromGRNS) {
        this.fromGRNS = fromGRNS;
    }

    public Warehouse getSourceWarehouse() {
        return sourceWarehouse;
    }

    public void setSourceWarehouse(Warehouse sourceWarehouse) {
        this.sourceWarehouse = sourceWarehouse;
    }

    public Boolean getIsToOutsource() {
        return isToOutsource;
    }

    public void setIsToOutsource(Boolean isToOutsource) {
        this.isToOutsource = isToOutsource;
    }

    public Long getToServiceOrderId() {
        return toServiceOrderId;
    }

    public void setToServiceOrderId(Long toServiceOrderId) {
        this.toServiceOrderId = toServiceOrderId;
    }

    public Boolean getIsFromOutsourcing() {
        return isFromOutsourcing;
    }

    public void setIsFromOutsourcing(Boolean isFromOutsourcing) {
        this.isFromOutsourcing = isFromOutsourcing;
    }

    public Long getFromServiceOrderId() {
        return fromServiceOrderId;
    }

    public void setFromServiceOrderId(Long fromServiceOrderId) {
        this.fromServiceOrderId = fromServiceOrderId;
    }
    
    
}
