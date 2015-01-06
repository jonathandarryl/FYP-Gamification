/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.entity;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class Planes extends Vehicles implements Serializable {
    private static final long serialVersionUID = 1L;

    public Planes() {
    }
    
    public Planes(String type, Integer useYear, Long capacity, Long maxDistance, Double transCost, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail) {
        this.setType(type);
        this.setUseYear(useYear);
        this.setCapacity(capacity);
        this.setMaxDistance(maxDistance);
        this.setTransCost(transCost);
        this.setPerishable(perishable);
        this.setPharmaceutical(pharmaceutical);
        this.setFlammable(flammable);
        this.setHighValue(highvalue);
        this.setAvail(avail);
    }
}
