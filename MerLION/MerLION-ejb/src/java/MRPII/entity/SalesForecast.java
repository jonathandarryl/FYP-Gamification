/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

import OES.entity.FinishedGood;
import java.io.Serializable;
import java.sql.Timestamp;
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
public class SalesForecast implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double amount;    
    private Timestamp salesForecastDate;
    @OneToOne
    private FinishedGood finishedGood;
    private Boolean archivedOrNot;

    public SalesForecast() {
    }

    public SalesForecast(Double amount, Timestamp salesForecastDate, FinishedGood finishedGood) {
        this.amount = amount;
        this.salesForecastDate = salesForecastDate;
        this.finishedGood = finishedGood;
        this.archivedOrNot = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getSalesForecastDate() {
        return salesForecastDate;
    }

    public void setSalesForecastDate(Timestamp salesForecastDate) {
        this.salesForecastDate = salesForecastDate;
    }

    public FinishedGood getFinishedGood() {
        return finishedGood;
    }

    public void setFinishedGood(FinishedGood finishedGood) {
        this.finishedGood = finishedGood;
    }

    public Boolean getArchivedOrNot() {
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
        if (!(object instanceof SalesForecast)) {
            return false;
        }
        SalesForecast other = (SalesForecast) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mrp2.SalesForecast[ id=" + id + " ]";
    }
    
}
