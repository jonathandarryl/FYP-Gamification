/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

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
public class ResourcePlan implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Timestamp resourcePlanDate;
    private Double MPSweeklyDemand; //From weekly plan
    private Double grossRequirements;
    private Double scheduledReceipt; //From purchase orders scheduled to arrive on this week
    private Double plannedReceipt; //From previous week's resource plan's planned order, how many weeks depend on the lead time
    private Double onHand; //Calculate based on previous week's resource plan's onHand(or retrieve from WMS for the first resource plan)
    private Double plannedOrders; //From Purchase Orders scheduled to be sent on this week
    
    private Timestamp plannedDate;
    private Integer plannedYear;
    private Integer plannedMonth;
    private Integer plannedWeek;
    @ManyToOne
    private RawMaterial rawMaterial;
    @ManyToOne
    private Supplier supplier;
    
    @OneToOne
    private WeeklyPlan weeklyPlan;;

    public ResourcePlan() {
    }

    public ResourcePlan(Double grossRequirements, Double plannedOrders, Double scheduledReceipt, Double plannedReceipt, Double onHand, WeeklyPlan weeklyPlan, RawMaterial rawMaterial, Supplier supplier) {
        this.MPSweeklyDemand = weeklyPlan.getWeeklyDemand();
        this.resourcePlanDate = new Timestamp(System.currentTimeMillis());
        this.grossRequirements = grossRequirements;
        this.scheduledReceipt = scheduledReceipt;
        this.plannedReceipt = plannedReceipt;
        this.onHand = onHand;
        this.plannedOrders = plannedOrders;
        this.weeklyPlan = weeklyPlan;
        this.plannedDate = weeklyPlan.getWeeklyPlanDate();
        this.plannedYear = weeklyPlan.getPlannedYear();
        this.plannedMonth = weeklyPlan.getPlannedMonth();
        this.plannedWeek = weeklyPlan.getPlannedWeek();
        this.rawMaterial = rawMaterial;
        this.supplier = supplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getResourcePlanDate() {
        return resourcePlanDate;
    }

    public void setResourcePlanDate(Timestamp resourcePlanDate) {
        this.resourcePlanDate = resourcePlanDate;
    }
    
    public Double getGrossRequirements() {
        return grossRequirements;
    }

    public void setGrossRequirements(Double grossRequirements) {
        this.grossRequirements = grossRequirements;
    }

    public Double getPlannedOrders() {
        return plannedOrders;
    }

    public void setPlannedOrders(Double plannedOrders) {
        this.plannedOrders = plannedOrders;
    }
    
    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }
    
    public Double getMPSweeklyDemand() {
        return MPSweeklyDemand;
    }

    public void setMPSweeklyDemand(Double MPSweeklyDemand) {
        this.MPSweeklyDemand = MPSweeklyDemand;
    }

    public Double getScheduledReceipt() {
        return scheduledReceipt;
    }

    public void setScheduledReceipt(Double scheduledReceipt) {
        this.scheduledReceipt = scheduledReceipt;
    }

    public Double getPlannedReceipt() {
        return plannedReceipt;
    }

    public void setPlannedReceipt(Double plannedReceipt) {
        this.plannedReceipt = plannedReceipt;
    }

    public Double getOnHand() {
        return onHand;
    }

    public void setOnHand(Double onHand) {
        this.onHand = onHand;
    }

    public Integer getPlannedYear() {
        return plannedYear;
    }

    public void setPlannedYear(Integer plannedYear) {
        this.plannedYear = plannedYear;
    }

    public Integer getPlannedMonth() {
        return plannedMonth;
    }

    public void setPlannedMonth(Integer plannedMonth) {
        this.plannedMonth = plannedMonth;
    }

    public Integer getPlannedWeek() {
        return plannedWeek;
    }

    public void setPlannedWeek(Integer plannedWeek) {
        this.plannedWeek = plannedWeek;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Timestamp getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Timestamp plannedDate) {
        this.plannedDate = plannedDate;
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
        if (!(object instanceof ResourcePlan)) {
            return false;
        }
        ResourcePlan other = (ResourcePlan) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mrp2.ResourcePlan[ id=" + id + " ]";
    }
    
}
