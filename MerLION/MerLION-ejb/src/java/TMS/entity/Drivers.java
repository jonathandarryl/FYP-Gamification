/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.entity;

import Common.entity.Company;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class Drivers implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long driverId;
    
    private String name;
    private String gender;
    private Integer age;
    private String licenseNo;
    private Integer drivingAge;
    private Long transOrderId;
    @ManyToOne
    private Trucks trucks;

    @ManyToOne
    private Company ownerCompany;
    
    private ArrayList<Timestamp> timetable = new ArrayList<>();

    public Drivers() {
        //this.name = name;
    }

    public Boolean addTimeSlot(Timestamp start, Timestamp end){
        if (start.before(end)){
            this.timetable.add(start);
            this.timetable.add(end);
            return true;
        }else {
            return false;
        }
    }
    /**
     * @return the driverId
     */
    public Long getDriverId() {
        return driverId;
    }

    /**
     * @param driverId the driverId to set
     */
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public Trucks getTrucks() {
        return trucks;
    }

    public void setTrucks(Trucks trucks) {
        this.trucks = trucks;
    }

    public ArrayList<Timestamp> getTimetable() {
        return timetable;
    }

    public void setTimetable(ArrayList<Timestamp> timetable) {
        this.timetable = timetable;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public Integer getDrivingAge() {
        return drivingAge;
    }

    public void setDrivingAge(Integer drivingAge) {
        this.drivingAge = drivingAge;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public Long getTransOrderId() {
        return transOrderId;
    }

    public void setTransOrderId(Long transOrderId) {
        this.transOrderId = transOrderId;
    }
      
}
