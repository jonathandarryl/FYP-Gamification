/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author songhan
 */
@Entity
public class MonthlyPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  
    private Timestamp monthlyPlanDate; 
    private Integer planYear;
    private Integer planMonth;
    private Double productionPlan;
    private Double targetInventoryLevel;
    private Integer workingDay;
    @OneToOne
    private SalesForecast salesForecast;
    private Boolean archivedOrNot;

    public MonthlyPlan() {
    }

    public MonthlyPlan(Timestamp monthlyPlanDate, Double productionPlan, Double targetInventoryLevel, SalesForecast salesForecast, Integer planYear, Integer planMonth) {
        this.monthlyPlanDate = monthlyPlanDate;
        this.productionPlan = productionPlan;
        this.targetInventoryLevel = targetInventoryLevel;
        this.workingDay = calculateWorkingDays(monthlyPlanDate);
        this.salesForecast = salesForecast;
        this.planYear = planYear;
        this.planMonth = planMonth;
        this.archivedOrNot = Boolean.FALSE;
    }
    
    private Integer calculateWorkingDays(Timestamp targetTime){
        Calendar targetMonth = Calendar.getInstance();
        targetMonth.setTime(targetTime);
        Integer totalDays = targetMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        Integer workingday = 0;
        Calendar markDay = targetMonth;
        for(int i=0; i<totalDays; i++){            
            Integer weekday = markDay.get(Calendar.DAY_OF_WEEK);
            if(weekday!=1 && weekday!=7)
                workingday++;
            markDay.add(Calendar.DAY_OF_MONTH, 1);
        }
        return workingday;                   
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getMonthlyPlanDate() {
        return monthlyPlanDate;
    }

    public void setMonthlyPlanDate(Timestamp monthlyPlanDate) {
        this.monthlyPlanDate = monthlyPlanDate;
    }

    public Integer getPlanYear() {
        return planYear;
    }

    public void setPlanYear(Integer planYear) {
        this.planYear = planYear;
    }

    public Integer getPlanMonth() {
        return planMonth;
    }

    public void setPlanMonth(Integer planMonth) {
        this.planMonth = planMonth;
    }

    public Double getProductionPlan() {
        return productionPlan;
    }

    public void setProductionPlan(Double productionPlan) {
        this.productionPlan = productionPlan;
    }

    public Double getTargetInventoryLevel() {
        return targetInventoryLevel;
    }

    public void setTargetInventoryLevel(Double targetInventoryLevel) {
        this.targetInventoryLevel = targetInventoryLevel;
    }

    public Integer getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Integer workingDay) {
        this.workingDay = workingDay;
    }

    public SalesForecast getSalesForecast() {
        return salesForecast;
    }

    public void setSalesForecast(SalesForecast salesForecast) {
        this.salesForecast = salesForecast;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof MonthlyPlan)) {
            return false;
        }
        MonthlyPlan other = (MonthlyPlan) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mrp2.MonthlyPlan[ id=" + id + " ]";
    }
    
}
