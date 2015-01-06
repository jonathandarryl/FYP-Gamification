/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author chongyangsun
 */
@Embeddable
public class Location implements Serializable {
    private String country;
    private String state;
    private String city;
    private String street;
    private String blockNo;
    private String postalCode;

    public Location() {
    }

    public Location(String country, String state, String city, String street, String blockNo, String postalCode) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.blockNo = blockNo;
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        if(blockNo == null)
            return street + ", " + city + ", " + state + ", " + country + " " + postalCode;
        else
            return street + ", " + blockNo + ", " + city + ", " + state + ", " + country + " " + postalCode;
    }

}
