/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.entity;

import Common.entity.CustomerCompany;
import MRPII.entity.BillOfMaterial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author songhan
 */
@Entity
public class FinishedGood extends Product implements Serializable {
    
    @OneToOne
    private BillOfMaterial billOfMaterial;
    
    public FinishedGood() {
    }
    
    public FinishedGood(String name, Double price, String description, CustomerCompany ownerCompany, String productType, String unit,
            Double quantityInOneUnitCapacity, Double qunatityInOneBatch) {
        this.setProductName(name);
        this.setProductPrice(price);
        this.setProductDescription(description);
        this.setOwnerCompany(ownerCompany);
        this.setOwnerCompanyName(ownerCompany.getCompanyName());
        this.setArchivedOrNot(Boolean.FALSE);
        this.setPublicOrNot(Boolean.FALSE);
        this.setProductType(productType);
        this.setUnit(unit);
        this.setInventoryLevel(0.0);
        this.setQuantityInOneUnitCapacity(quantityInOneUnitCapacity);
        this.setQuantityInOneBatch(qunatityInOneBatch);
    }

    public BillOfMaterial getBillOfMaterial() {
        return billOfMaterial;
    }

    public void setBillOfMaterial(BillOfMaterial billOfMaterial) {
        this.billOfMaterial = billOfMaterial;
    }
    
}
