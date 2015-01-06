/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.entity;

import OES.entity.FinishedGood;
import OES.entity.SalesOrder;
import WMS.entity.ExtractionRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author songhan
 */
@Entity
public class Production implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FinishedGood finishedGood;
    private Integer batchNumber;
    private Double quantity;
    private Boolean rawMaterialReadyOrNot;
    private Boolean commencedOrNot;
    private Boolean completeOrNot;
    
    private Timestamp creationTime;
    private Timestamp startTime;
    private Timestamp completeTime;
    
    @ManyToOne
    private SalesOrder salesOrder;
    
    @OneToMany
    List<RawMaterial> rawMaterialList;
    HashMap<RawMaterial, Double> rawMaterialQuantityMap;
    HashMap<RawMaterial, Boolean> rawMaterialReadyOrNotMap;
    @OneToMany(mappedBy="production")
    List <ExtractionRequest> rawMaterialExtractionRequestList;

    public Production() {
    }

    public Production(FinishedGood finishedGood, Integer batchNumber, Double quantity, SalesOrder salesOrder) {
        this.finishedGood = finishedGood;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.salesOrder = salesOrder;
        this.rawMaterialReadyOrNot = Boolean.FALSE;
        this.commencedOrNot = Boolean.FALSE;
        this.completeOrNot = Boolean.FALSE;
        this.creationTime = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FinishedGood getFinishedGood() {
        return finishedGood;
    }

    public void setFinishedGood(FinishedGood finishedGood) {
        this.finishedGood = finishedGood;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getCompleteOrNot() {
        return completeOrNot;
    }

    public void setCompleteOrNot(Boolean completeOrNot) {
        this.completeOrNot = completeOrNot;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime) {
        this.completeTime = completeTime;
    }

    public Boolean getRawMaterialReadyorNot() {
        return rawMaterialReadyOrNot;
    }

    public void setRawMaterialReadyorNot(Boolean rawMaterialReadyOrNot) {
        this.rawMaterialReadyOrNot = rawMaterialReadyOrNot;
    }

    public Boolean getCommencedOrNot() {
        return commencedOrNot;
    }

    public void setCommencedOrNot(Boolean commencedOrNot) {
        this.commencedOrNot = commencedOrNot;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public void setRawMaterialList(List<RawMaterial> rawMaterialList) {
        this.rawMaterialList = rawMaterialList;
    }

    public HashMap<RawMaterial, Double> getRawMaterialQuantityMap() {
        return rawMaterialQuantityMap;
    }

    public void setRawMaterialQuantityMap(HashMap<RawMaterial, Double> rawMaterialQuantityMap) {
        this.rawMaterialQuantityMap = rawMaterialQuantityMap;
    }

    public HashMap<RawMaterial, Boolean> getRawMaterialReadyOrNotMap() {
        return rawMaterialReadyOrNotMap;
    }

    public void setRawMaterialReadyOrNotMap(HashMap<RawMaterial, Boolean> rawMaterialReadyOrNotMap) {
        this.rawMaterialReadyOrNotMap = rawMaterialReadyOrNotMap;
    }

    public List<ExtractionRequest> getRawMaterialExtractionRequestList() {
        return rawMaterialExtractionRequestList;
    }

    public void setRawMaterialExtractionRequestList(List<ExtractionRequest> rawMaterialExtractionRequestList) {
        this.rawMaterialExtractionRequestList = rawMaterialExtractionRequestList;
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
        if (!(object instanceof Production)) {
            return false;
        }
        Production other = (Production) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MRPII.entity.Production[ id=" + id + " ]";
    }
    
}
