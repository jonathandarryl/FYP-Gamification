/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.entity;

import Common.entity.Company;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author songhan
 */
@Entity
public class SalesQuotation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double totalPrice; 
    
    private Timestamp salesQuotationDate;
    @ManyToMany
    private List<Product> productList;
    private HashMap<Product, Double> productListMap = new HashMap<>();
    @ManyToOne
    private Customer customer;
    @OneToOne
    private SalesOrder salesOrder;
    private String ownerCompanyName;
    private Boolean archivedOrNot;
    private Boolean approvedOrNot;
    
    public SalesQuotation() {
    }

    public SalesQuotation(List<Product> productList, Double totalPrice, HashMap<Product, Double> productListMap, Customer customer, String ownerCompanyName) {
        this.totalPrice = totalPrice;
        this.productList = productList;
        this.productListMap = productListMap;
        this.customer = customer;
        this.ownerCompanyName = ownerCompanyName;
        this.salesQuotationDate = new Timestamp(System.currentTimeMillis());
        this.archivedOrNot = Boolean.FALSE;
        this.approvedOrNot = Boolean.FALSE;
    }

    public Boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getSalesQuotationDate() {
        return salesQuotationDate;
    }

    public void setSalesQuotationDate(Timestamp salesQuotationDate) {
        this.salesQuotationDate = salesQuotationDate;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public HashMap<Product, Double> getProductListMap() {
        return productListMap;
    }

    public void setProductListMap(HashMap<Product, Double> productListMap) {
        this.productListMap = productListMap;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
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
        if (!(object instanceof SalesQuotation)) {
            return false;
        }
        SalesQuotation other = (SalesQuotation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String str="Products ordered: ";
        for(int i=0;i<productList.size();i++){
            Product prod=productList.get(i);
            Double quant=productListMap.get(prod);
            str+=prod.getProductName();
            str+=" ";
            str+=quant;
            str+="<br/>";
        }
        return str;
    }
    
}
