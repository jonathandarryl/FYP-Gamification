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
public class WeeklyPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double monthlyDemand;
    private Integer workingDaysInWeek;
    private Integer workingDaysInMonth;
    private Double weeklyDemand; 
    private Timestamp weeklyPlanDate;
    private Integer plannedYear;
    private Integer plannedMonth;
    private Integer plannedWeek;
    @OneToOne
    private MonthlyPlan monthlyPlan;
    private String workingDayPeriodStr;
    
    private Boolean isAtEndOfMonth;
    private Double monthlyDemand1;
    private Integer workingDaysInWeek1;
    private Integer workingDaysInMonth1;
    private Double weeklyDemand1; 
    private String workingDayPeriodStr1;
    private Double monthlyDemand2;
    private Integer workingDaysInWeek2;
    private Integer workingDaysInMonth2;
    private Double weeklyDemand2; 
    private String workingDayPeriodStr2;
    
    private Boolean archivedOrNot;

    public WeeklyPlan() {
    }

    public WeeklyPlan(Double monthlyDemand, Integer workingDaysInWeek, Double weeklyDemand, Timestamp weeklyPlanDate, MonthlyPlan monthlyPlan, String workingDayPeriodStr) {
        this.monthlyDemand = monthlyDemand;
        this.workingDaysInWeek = workingDaysInWeek;
        this.weeklyDemand = weeklyDemand;
        this.weeklyPlanDate = weeklyPlanDate;
        this.monthlyPlan = monthlyPlan;
        this.workingDayPeriodStr = workingDayPeriodStr;
        this.isAtEndOfMonth = Boolean.FALSE;
        this.archivedOrNot = Boolean.FALSE;
        
        Calendar curTime = Calendar.getInstance();
        curTime.setTime(weeklyPlanDate);
        this.plannedYear = curTime.get(Calendar.YEAR);
        this.plannedMonth = curTime.get(Calendar.MONTH)+1;
        this.plannedWeek = setPlannedWeek(curTime);
    }
    
    private Integer setPlannedWeek(Calendar curTime){
        Calendar firstDayOfTheMonth = Calendar.getInstance();
        firstDayOfTheMonth.set(curTime.get(Calendar.YEAR), curTime.get(Calendar.MONTH), 1);
        firstDayOfTheMonth.setFirstDayOfWeek(Calendar.MONDAY);
        if(firstDayOfTheMonth.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
            return curTime.get(Calendar.WEEK_OF_MONTH);
        else
            return curTime.get(Calendar.WEEK_OF_MONTH)-1;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonthlyDemand() {
        return monthlyDemand;
    }

    public void setMonthlyDemand(Double monthlyDemand) {
        this.monthlyDemand = monthlyDemand;
    }

    public Integer getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(Integer workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Double getWeeklyDemand() {
        return weeklyDemand;
    }

    public void setWeeklyDemand(Double weeklyDemand) {
        this.weeklyDemand = weeklyDemand;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getWeeklyPlanDate() {
        return weeklyPlanDate;
    }

    public void setWeeklyPlanDate(Timestamp weeklyPlanDate) {
        this.weeklyPlanDate = weeklyPlanDate;
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

    public MonthlyPlan getMonthlyPlan() {
        return monthlyPlan;
    }

    public void setMonthlyPlan(MonthlyPlan monthlyPlan) {
        this.monthlyPlan = monthlyPlan;
    }

    public String getWorkingDayPeriodStr() {
        return workingDayPeriodStr;
    }

    public void setWorkingDayPeriodStr(String workingDayPeriodStr) {
        this.workingDayPeriodStr = workingDayPeriodStr;
    }

    public Boolean isIsAtEndOfMonth() {
        return isAtEndOfMonth;
    }

    public void setIsAtEndOfMonth(Boolean isAtEndOfMonth) {
        this.isAtEndOfMonth = isAtEndOfMonth;
    }

    public Double getMonthlyDemand1() {
        return monthlyDemand1;
    }

    public void setMonthlyDemand1(Double monthlyDemand1) {
        this.monthlyDemand1 = monthlyDemand1;
    }

    public Integer getWorkingDaysInWeek1() {
        return workingDaysInWeek1;
    }

    public void setWorkingDaysInWeek1(Integer workingDaysInWeek1) {
        this.workingDaysInWeek1 = workingDaysInWeek1;
    }
    
    public Double getWeeklyDemand1() {
        return weeklyDemand1;
    }

    public void setWeeklyDemand1(Double weeklyDemand1) {
        this.weeklyDemand1 = weeklyDemand1;
    }

    public Double getMonthlyDemand2() {
        return monthlyDemand2;
    }

    public void setMonthlyDemand2(Double monthlyDemand2) {
        this.monthlyDemand2 = monthlyDemand2;
    }

    public Integer getWorkingDaysInWeek2() {
        return workingDaysInWeek2;
    }

    public void setWorkingDaysInWeek2(Integer workingDaysInWeek2) {
        this.workingDaysInWeek2 = workingDaysInWeek2;
    }

    public Double getWeeklyDemand2() {
        return weeklyDemand2;
    }

    public void setWeeklyDemand2(Double weeklyDemand2) {
        this.weeklyDemand2 = weeklyDemand2;
    }

    public Integer getWorkingDaysInMonth() {
        return workingDaysInMonth;
    }

    public void setWorkingDaysInMonth(Integer workingDaysInMonth) {
        this.workingDaysInMonth = workingDaysInMonth;
    }

    public Integer getWorkingDaysInMonth1() {
        return workingDaysInMonth1;
    }

    public void setWorkingDaysInMonth1(Integer workingDaysInMonth1) {
        this.workingDaysInMonth1 = workingDaysInMonth1;
    }

    public Integer getWorkingDaysInMonth2() {
        return workingDaysInMonth2;
    }

    public void setWorkingDaysInMonth2(Integer workingDaysInMonth2) {
        this.workingDaysInMonth2 = workingDaysInMonth2;
    }

    public String getWorkingDayPeriodStr1() {
        return workingDayPeriodStr1;
    }

    public void setWorkingDayPeriodStr1(String workingDayPeriodStr1) {
        this.workingDayPeriodStr1 = workingDayPeriodStr1;
    }

    public String getWorkingDayPeriodStr2() {
        return workingDayPeriodStr2;
    }

    public void setWorkingDayPeriodStr2(String workingDayPeriodStr2) {
        this.workingDayPeriodStr2 = workingDayPeriodStr2;
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
        if (!(object instanceof WeeklyPlan)) {
            return false;
        }
        WeeklyPlan other = (WeeklyPlan) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mrp2.WeeklyPlan[ id=" + id + " ]";
    }
    
}
