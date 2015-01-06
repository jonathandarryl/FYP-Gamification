/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.entity;

import OES.entity.FinishedGood;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author songhan
 */
@Entity
public class BillOfMaterial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy="billOfMaterial")
    private FinishedGood finishedGood;
    @OneToMany
    private List<RawMaterial> rawMaterialList = new ArrayList<>();
    private HashMap<RawMaterial, Double> materialQuantityMap = new HashMap<>();
    private Boolean archivedOrNot;

    public BillOfMaterial() {
    }

    public BillOfMaterial(FinishedGood finishedGood, List<RawMaterial> rawMaterialList, HashMap<RawMaterial, Double> materialQuantityMap) {
        this.finishedGood = finishedGood;
        this.rawMaterialList = rawMaterialList;
        this.materialQuantityMap = materialQuantityMap;
        this.archivedOrNot = Boolean.FALSE;
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

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public void setRawMaterialList(List<RawMaterial> rawMaterialList) {
        this.rawMaterialList = rawMaterialList;
    }

    public HashMap<RawMaterial, Double> getMaterialQuantityMap() {
        return materialQuantityMap;
    }

    public void setMaterialQuantityMap(HashMap<RawMaterial, Double> materialQuantityMap) {
        this.materialQuantityMap = materialQuantityMap;
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
        if (!(object instanceof BillOfMaterial)) {
            return false;
        }
        BillOfMaterial other = (BillOfMaterial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MRPII.entity.BillOfMaterial[ id=" + id + " ]";
    }
    
}
