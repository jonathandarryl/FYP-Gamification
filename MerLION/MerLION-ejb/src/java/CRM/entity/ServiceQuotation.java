/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import Common.entity.Location;
import OES.entity.Product;
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
public class ServiceQuotation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    @ManyToOne
    private Company pCompany;
    @OneToOne
    private Company clientCompany;
    @ManyToOne
    private ServiceCatalog serviceCatalog;
    private Timestamp createTime;
    private Timestamp startDate;
    private Timestamp endDate; 
    @OneToOne
    private ServiceContract serviceContract;
    private Boolean establishedContractOrNot;
    private Boolean clientApprovedOrNot;
    private Boolean approvedOrNot;
    private Boolean archivedOrNot;
    
    private Boolean rejectedByProivderOrNot;    
    @ManyToOne
    private Product product;
    private Double estimatedQuantity;
    
    @ManyToOne
    private Warehouse warehouse;
    private Integer requiredWarehouseCapacity;
    private Integer storageDurationDays;
    
    private Double totalPrice;
    private Double optiCheapestPrice;
    private Double optiPriceForShortestTime;
    private Double optiValuestPrice;
    
    private Integer orderNumber;
    
    @Embedded
    @AttributeOverrides({        
        @AttributeOverride(name="country", column=@Column(name="sourceCountry")),
        @AttributeOverride(name="state", column=@Column(name="sourceState")),
        @AttributeOverride(name="city", column=@Column(name="sourceCity")),
        @AttributeOverride(name="street", column=@Column(name="sourceStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="sourceBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="sourcepostalCode"))
    })
    private Location source;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="destCountry")),
        @AttributeOverride(name="state", column=@Column(name="destState")),
        @AttributeOverride(name="city", column=@Column(name="destCity")),
        @AttributeOverride(name="street", column=@Column(name="destStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="destBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="destpostalCode"))
    })
    private Location dest;
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    // this is for GRNS order
    private Boolean fromGRNS;
    private String postType;
    private Long postId;

    public ServiceQuotation() {
    }

    public ServiceQuotation(Company pCompany, Company clientCompany, String description, Timestamp startDate, Timestamp endDate, 
            Integer requiredWarehouseCapacity, Double totalPrice,
            Location source, Location dest,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue,
            Warehouse warehouse, Product product, Double estimatedQuantity) {
        this.description = description;
        this.pCompany = pCompany;
        this.clientCompany = clientCompany;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requiredWarehouseCapacity = requiredWarehouseCapacity;
        this.totalPrice = totalPrice;
        this.source = source;
        this.dest = dest;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.establishedContractOrNot = Boolean.FALSE;
        this.clientApprovedOrNot = Boolean.FALSE;
        this.approvedOrNot = Boolean.FALSE;
        this.archivedOrNot = Boolean.FALSE;
        this.rejectedByProivderOrNot = Boolean.FALSE;
        this.warehouse = warehouse;
        this.product = product;
        this.estimatedQuantity = estimatedQuantity;
        this.setFromGRNS(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
    }
    
    public ServiceQuotation(Company pCompany, Company clientCompany, String description, Timestamp startDate, Timestamp endDate,
            Integer requiredWarehouseCapacity, Integer storageDurationDays,
            Location source, Location dest,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue,
            Warehouse warehouse, Product product, Double estimatedQuantity, Integer orderNumber) {
        this.description = description;
        this.pCompany = pCompany;
        this.clientCompany = clientCompany;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requiredWarehouseCapacity = requiredWarehouseCapacity;
        this.storageDurationDays = storageDurationDays;
        this.source = source;
        this.dest = dest;
        this.perishable = perishable;
        this.flammable = flammable;
        this.pharmaceutical = pharmaceutical;
        this.highValue = highValue;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.establishedContractOrNot = Boolean.FALSE;
        this.clientApprovedOrNot = Boolean.FALSE;
        this.approvedOrNot = Boolean.FALSE;
        this.archivedOrNot = Boolean.FALSE;
        this.rejectedByProivderOrNot = Boolean.FALSE;
        this.warehouse = warehouse;
        this.product = product;
        this.estimatedQuantity = estimatedQuantity;
        this.orderNumber = orderNumber;
        this.setFromGRNS(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
    }
    
    // for GRNS order creation
    public ServiceQuotation(Company pCompany, Company clientCompany, String description, Timestamp startDate, Timestamp endDate,
            Integer requiredWarehouseCapacity, Integer storageDurationDays, Location source, Location dest,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue,
            Warehouse warehouse, Product product, Double estimatedQuantity, Long postId, String postType) {
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.setDescription(description);
        this.setpCompany(pCompany);
        this.setClientCompany(clientCompany);
        this.setServiceCatalog(null);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setClientApprovedOrNot(Boolean.TRUE);
        this.setEstablishedContractOrNot(Boolean.FALSE);
        this.setApprovedOrNot(Boolean.TRUE);
        this.setArchivedOrNot(Boolean.TRUE);
        this.setRejectedByProivderOrNot(Boolean.FALSE);
        this.setProduct(product);
        this.setEstimatedQuantity(estimatedQuantity);
        this.setWarehouse(warehouse);
        this.setRequiredWarehouseCapacity(requiredWarehouseCapacity);
        this.setStorageDurationDays(storageDurationDays);
        this.setTotalPrice(totalPrice);
        this.setOrderNumber(1);
        this.setSource(source);
        this.setDest(dest);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setFromGRNS(Boolean.TRUE);
        this.setPostId(postId);
        this.setPostType(postType);
    }
    
    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getpCompany() {
        return pCompany;
    }

    public void setpCompany(Company pCompany) {
        this.pCompany = pCompany;
    }

    public Company getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(Company clientCompany) {
        this.clientCompany = clientCompany;
    }

    public ServiceCatalog getServiceCatalog() {
        return serviceCatalog;
    }

    public void setServiceCatalog(ServiceCatalog serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    public Boolean getEstablishedContractOrNot() {
        return establishedContractOrNot;
    }

    public void setEstablishedContractOrNot(Boolean establishedContractOrNot) {
        this.establishedContractOrNot = establishedContractOrNot;
    }

    public Boolean getClientApprovedOrNot() {
        return clientApprovedOrNot;
    }

    public void setClientApprovedOrNot(Boolean clientApprovedOrNot) {
        this.clientApprovedOrNot = clientApprovedOrNot;
    }

    public Boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    public Boolean getRejectedByProivderOrNot() {
        return rejectedByProivderOrNot;
    }

    public void setRejectedByProivderOrNot(Boolean rejectedByProivderOrNot) {
        this.rejectedByProivderOrNot = rejectedByProivderOrNot;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Integer getRequiredWarehouseCapacity() {
        return requiredWarehouseCapacity;
    }

    public void setRequiredWarehouseCapacity(Integer requiredWarehouseCapacity) {
        this.requiredWarehouseCapacity = requiredWarehouseCapacity;
    }

    public Integer getStorageDurationDays() {
        return storageDurationDays;
    }

    public void setStorageDurationDays(Integer storageDurationDays) {
        this.storageDurationDays = storageDurationDays;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getOptiCheapestPrice() {
        return optiCheapestPrice;
    }

    public void setOptiCheapestPrice(Double optiCheapestPrice) {
        this.optiCheapestPrice = optiCheapestPrice;
    }

    public Double getOptiPriceForShortestTime() {
        return optiPriceForShortestTime;
    }

    public void setOptiPriceForShortestTime(Double optiPriceForShortestTime) {
        this.optiPriceForShortestTime = optiPriceForShortestTime;
    }

    public Double getOptiValuestPrice() {
        return optiValuestPrice;
    }

    public void setOptiValuestPrice(Double optiValuestPrice) {
        this.optiValuestPrice = optiValuestPrice;
    }

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
    }

    public Location getDest() {
        return dest;
    }

    public void setDest(Location dest) {
        this.dest = dest;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Double getEstimatedQuantity() {
        return estimatedQuantity;
    }

    public void setEstimatedQuantity(Double estimatedQuantity) {
        this.estimatedQuantity = estimatedQuantity;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
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
        if (!(object instanceof ServiceQuotation)) {
            return false;
        }
        ServiceQuotation other = (ServiceQuotation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceQuotation[ id=" + id + " ]";
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
    
}
