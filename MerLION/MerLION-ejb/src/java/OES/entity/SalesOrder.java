/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.entity;

import MRPII.entity.Production;
import WMS.entity.ExtractionRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author songhan
 */
@Entity
public class SalesOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy="salesOrder")
    private SalesQuotation salesQuotation;
    private Boolean isBackOrder;
    private Boolean isFulfilled;
    private Boolean archivedOrNot;
    private Boolean approvedOrNot;
    private Boolean sendOrNot;
    
    private Timestamp salesOrderDate;
    private String ownerCompanyName;
    
    @OneToMany(mappedBy="salesOrder")
    List<Production> productionList;
    
    @OneToMany
    List<Product> productList;
    HashMap<Product, Double> productQuantityMap;
    HashMap<Product, Boolean> productReadyOrNotMap = new HashMap<>();
    @OneToMany(mappedBy="salesOrder")
    List <ExtractionRequest> productExtractionRequestList;
    private Boolean productReadyOrNot;

    public SalesOrder() {
    }

    public SalesOrder(SalesQuotation salesQuotation) {
        this.salesQuotation = salesQuotation;
        this.isBackOrder = Boolean.FALSE;
        this.isFulfilled = Boolean.FALSE;
        this.archivedOrNot = Boolean.FALSE;
        this.approvedOrNot = Boolean.FALSE;
        this.sendOrNot=Boolean.FALSE;
        this.productReadyOrNot = Boolean.FALSE;
        this.salesOrderDate = new Timestamp(System.currentTimeMillis());
        this.ownerCompanyName = salesQuotation.getOwnerCompanyName();
        this.productList = salesQuotation.getProductList();
        this.productQuantityMap = salesQuotation.getProductListMap();
        for(Product curPro: productList){
            productReadyOrNotMap.put(curPro, Boolean.FALSE);
        }
    }

    public SalesQuotation getSalesQuotation() {
        return salesQuotation;
    }

    public void setSalesQuotation(SalesQuotation salesQuotation) {
        this.salesQuotation = salesQuotation;
    }

    public Boolean isIsBackOrder() {
        return isBackOrder;
    }

    public Boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    public void setIsBackOrder(Boolean isBackOrder) {
        this.isBackOrder = isBackOrder;
    }

    public Boolean isIsFulfilled() {
        return isFulfilled;
    }

    public void setIsFulfilled(Boolean isFulfilled) {
        this.isFulfilled = isFulfilled;
    }

    public Boolean isArchivedOrNot() {
        return archivedOrNot;
    }

    public void setArchivedOrNot(Boolean archivedOrNot) {
        this.archivedOrNot = archivedOrNot;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getSalesOrderDate() {
        return salesOrderDate;
    }

    public void setSalesOrderDate(Timestamp salesOrderDate) {
        this.salesOrderDate = salesOrderDate;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public Boolean isSendOrNot() {
        return sendOrNot;
    }

    public void setSendOrNot(Boolean sendOrNot) {
        this.sendOrNot = sendOrNot;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public List<Production> getProductionList() {
        return productionList;
    }

    public void setProductionList(List<Production> productionList) {
        this.productionList = productionList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public HashMap<Product, Double> getProductQuantityMap() {
        return productQuantityMap;
    }

    public void setProductQuantityMap(HashMap<Product, Double> productQuantityMap) {
        this.productQuantityMap = productQuantityMap;
    }

    public HashMap<Product, Boolean> getProductReadyOrNotMap() {
        return productReadyOrNotMap;
    }

    public void setProductReadyOrNotMap(HashMap<Product, Boolean> productReadyOrNotMap) {
        this.productReadyOrNotMap = productReadyOrNotMap;
    }

    public List<ExtractionRequest> getProductExtractionRequestList() {
        return productExtractionRequestList;
    }

    public void setProductExtractionRequestList(List<ExtractionRequest> productExtractionRequestList) {
        this.productExtractionRequestList = productExtractionRequestList;
    }

    public Boolean getProductReadyOrNot() {
        return productReadyOrNot;
    }

    public void setProductReadyOrNot(Boolean productReadyOrNot) {
        this.productReadyOrNot = productReadyOrNot;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof SalesOrder)) {
            return false;
        }
        SalesOrder other = (SalesOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OES.SalesOrder[ id=" + id + " ]";
    }
    
}
