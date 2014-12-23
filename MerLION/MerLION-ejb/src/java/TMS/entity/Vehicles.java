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
public class Vehicles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long vehiclesId;
    private String type;
    private Integer useYear;
    private Boolean avail;
    private Long capacity;
    private Long maxDistance;
    private Double transCost;
    private Double velocity;
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    @ManyToOne
    private Company ownerCompany;
    
    @ManyToOne
    private Routes route;
    
    private ArrayList<Timestamp> timetable = new ArrayList<>();
    
    private Long transOrderId;

    public String viewTimeTable(){
        if(this.timetable.isEmpty()){
            return "no timetable!";
        }else{
            String table = "";
            for(int i=0; i<this.timetable.size(); ){
                table += "\nFrom: " + this.timetable.get(i).toString() + " To: " + this.timetable.get(i+1).toString();
                i+=2;
            }
            return table;
        }
    }
    
    /**
     * @return the vehiclesId
     */
    public Long getVehiclesId() {
        return vehiclesId;
    }
 
    /**
     * @param vehiclesId the vehiclesId to set
     */
    public void setVehiclesId(Long vehiclesId) {
        this.vehiclesId = vehiclesId;
    }

    /**
     * @return the capacity
     */
    public Long getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the maxDistance
     */
    public Long getMaxDistance() {
        return maxDistance;
    }

    /**
     * @param maxDistance the maxDistance to set
     */
    public void setMaxDistance(Long maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * @return the transCost
     */
    public Double getTransCost() {
        return transCost;
    }

    /**
     * @param transCost the transCost to set
     */
    public void setTransCost(Double transCost) {
        this.transCost = transCost;
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

    /**
     * @return the avail
     */
    public Boolean getAvail() {
        return avail;
    }

    /**
     * @param avail the avail to set
     */
    public void setAvail(Boolean avail) {
        this.avail = avail;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public Integer getUseYear() {
        return useYear;
    }

    public void setUseYear(Integer useYear) {
        this.useYear = useYear;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }
    
    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public ArrayList<Timestamp> getTimetable() {
        return timetable;
    }

    public void setTimetable(ArrayList<Timestamp> timetable) {
        this.timetable = timetable;
    }

    public Long getTransOrderId() {
        return transOrderId;
    }

    public void setTransOrderId(Long transOrderId) {
        this.transOrderId = transOrderId;
    }

    public Routes getRoute() {
        return route;
    }

    public void setRoute(Routes route) {
        this.route = route;
    }

}
