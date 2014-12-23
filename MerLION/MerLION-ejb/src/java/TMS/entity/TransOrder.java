/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.entity;

import CRM.entity.ServiceOrder;
import Common.entity.Location;
import OES.entity.Product;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class TransOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transOrderId;    
    private Timestamp recvTime;
    
    @Embedded
    @AttributeOverrides({        
        @AttributeOverride(name="country", column=@Column(name="sourceCountry")),
        @AttributeOverride(name="state", column=@Column(name="sourceState")),
        @AttributeOverride(name="city", column=@Column(name="sourceCity")),
        @AttributeOverride(name="street", column=@Column(name="sourceStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="sourceBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="sourcepostalCode"))
    })
    private Location startPoint;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="destCountry")),
        @AttributeOverride(name="state", column=@Column(name="destState")),
        @AttributeOverride(name="city", column=@Column(name="destCity")),
        @AttributeOverride(name="street", column=@Column(name="destStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="destBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="destpostalCode"))
    })   
    private Location endPoint;  
    
    private Timestamp startDeliverTime;  
    private Timestamp endDeliverTime;
    
    private String trackingNumber;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="currCountry")),
        @AttributeOverride(name="state", column=@Column(name="currState")),
        @AttributeOverride(name="city", column=@Column(name="currCity")),
        @AttributeOverride(name="street", column=@Column(name="currStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="currBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="currpostalCode"))
    })
    private Location currentLocation;
    
    @OneToMany(cascade={CascadeType.PERSIST})
    private List<Routes> routes;
    @OneToOne
    private ServiceOrder serviceOrder;
    private Boolean fulfilledOrNot;
    
    @ManyToOne
    private Product product;
    private Double quantity;
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    public TransOrder() {
    }

    public TransOrder(ServiceOrder serviceOrder) {
        this.recvTime = new Timestamp(System.currentTimeMillis());
        this.startDeliverTime = serviceOrder.getStartTime();
        this.endDeliverTime = serviceOrder.getEndTime();
        this.startPoint = serviceOrder.getSourceLoc();
        this.endPoint = serviceOrder.getDestLoc();
        this.serviceOrder = serviceOrder;
        this.fulfilledOrNot = Boolean.FALSE;
    }
    
    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /**
     * @return the routes
     */
    public List<Routes> getRoutes() {
        return routes;
    }

    /**
     * @param routes the routes to set
     */
    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Location endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @return the transOrderId
     */
    public Long getTransOrderId() {
        return transOrderId;
    }

    /**
     * @param transOrderId the transOrderId to set
     */
    public void setTransOrderId(Long transOrderId) {
        this.transOrderId = transOrderId;
    }

    /**
     * @return the recvTime
     */
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getRecvTime() {
        return recvTime;
    }

    /**
     * @param recvTime the recvTime to set
     */
    public void setRecvTime(Timestamp recvTime) {
        this.recvTime = recvTime;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public Boolean getFulfilledOrNot() {
        return fulfilledOrNot;
    }

    public void setFulfilledOrNot(Boolean fulfilledOrNot) {
        this.fulfilledOrNot = fulfilledOrNot;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.transOrderId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransOrder other = (TransOrder) obj;
        if (!Objects.equals(this.transOrderId, other.transOrderId)) {
            return false;
        }
        return true;
    }

    public Timestamp getStartDeliverTime() {
        return startDeliverTime;
    }

    public void setStartDeliverTime(Timestamp startDeliverTime) {
        this.startDeliverTime = startDeliverTime;
    }

    public Timestamp getEndDeliverTime() {
        return endDeliverTime;
    }

    public void setEndDeliverTime(Timestamp endDeliverTime) {
        this.endDeliverTime = endDeliverTime;
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
}
    
