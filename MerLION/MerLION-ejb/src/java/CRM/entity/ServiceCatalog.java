/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import Common.entity.Location;
import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.persistence.OneToOne;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class ServiceCatalog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
        
    @ManyToOne(cascade={CascadeType.PERSIST})
    private Company company;

    private String serviceName;    
    private String description;
    private Timestamp createTime;
    private Timestamp startTime;   
    private Timestamp endTime;  
    private Double price;
    
    private Long warehouseId;
    
    private Integer availTruckNumber;
    private Integer availPlaneNumber;
    private Integer availVesselNumber;
    private Integer availWarehouseCapacity;
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Boolean publicOrNot;
   
    
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
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="warehCountry")),
        @AttributeOverride(name="state", column=@Column(name="warehState")),
        @AttributeOverride(name="city", column=@Column(name="warehCity")),
        @AttributeOverride(name="street", column=@Column(name="warehStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="warehBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="warehpostalCode"))
    })
    private Location warehLoca;
    

   // private string warehouseCountry;
    private Boolean transport;
    private Boolean storage;
    private Boolean archivedOrNot;
    
    
    public ServiceCatalog() {
    }

    public ServiceCatalog(String serviceName, String description, Double price, Timestamp startTime, Timestamp endTime,
             Location source, Location dest,  Long warehouseId, Location warehLoca,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Boolean publicOrNot) {
        //this.pCompany = pCompany;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.dest = dest;
        this.source = source;
        this.perishable = perishable;
        this.warehouseId = warehouseId;
        this.warehLoca = warehLoca;
        
        this.flammable = flammable;
        this.highValue = highValue;
        this.pharmaceutical = pharmaceutical;
        this.archivedOrNot = Boolean.FALSE;
        this.publicOrNot = publicOrNot;
        
        if((source == null) || (dest == null) || (source != null && dest != null && (source.getCountry() == null || dest.getCountry() == null))) {
            this.setTransport(Boolean.FALSE);
        } else {
            this.setTransport(Boolean.TRUE);
        }
        if(warehLoca == null || (warehLoca != null && (warehLoca.getCountry() == null))) {
            this.setStorage(Boolean.FALSE);
        } else {
            this.setStorage(Boolean.TRUE);
        }
    }

    public Boolean getPublicOrNot() {
        return publicOrNot;
    }

    public void setPublicOrNot(Boolean publicOrNot) {
        this.publicOrNot = publicOrNot;
    }

    

    
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
        if (!(object instanceof ServiceCatalog)) {
            return false;
        }
        ServiceCatalog other = (ServiceCatalog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceCatalog[ id=" + id + " ]";
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getAvailTruckNumber() {
        return availTruckNumber;
    }

    public void setAvailTruckNumber(Integer availTruckNumber) {
        this.availTruckNumber = availTruckNumber;
    }

    public Integer getAvailPlaneNumber() {
        return availPlaneNumber;
    }

    public void setAvailPlaneNumber(Integer availPlaneNumber) {
        this.availPlaneNumber = availPlaneNumber;
    }

    public Integer getAvailVesselNumber() {
        return availVesselNumber;
    }

    public void setAvailVesselNumber(Integer availVesselNumber) {
        this.availVesselNumber = availVesselNumber;
    }

    public Integer getAvailWarehouseCapacity() {
        return availWarehouseCapacity;
    }

    public void setAvailWarehouseCapacity(Integer availWarehouseCapacity) {
        this.availWarehouseCapacity = availWarehouseCapacity;
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

    public Location getWarehLoca() {
        return warehLoca;
    }

    public void setWarehLoca(Location warehLoca) {
        this.warehLoca = warehLoca;
    }

 
    
    public Boolean getArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }



    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * @return the transport
     */
    public Boolean getTransport() {
        return transport;
    }

    /**
     * @param transport the transport to set
     */
    public void setTransport(Boolean transport) {
        this.transport = transport;
    }

    /**
     * @return the storage
     */
    public Boolean getStorage() {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(Boolean storage) {
        this.storage = storage;
    }


   
    

}
