/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Embeddable;

/**
 *
 * @author HanXiangyu
 */
@Embeddable
public class TimeSlot implements Serializable {

    private Timestamp start;
    private Timestamp end;

    public TimeSlot() {
    }

    public TimeSlot(Timestamp start, Timestamp end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return the start
     */
    public Timestamp getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Timestamp start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Timestamp getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Timestamp end) {
        this.end = end;
    }

    
}
