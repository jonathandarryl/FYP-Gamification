/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class SubscriptionPayment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double price;
    private Timestamp paymentTime;
    private Timestamp expectedPaymentTime;
    @OneToOne
    private SubscriptionPaymentReceipt recipt;
    @ManyToOne
    private Company company;
    @ManyToOne
    private SubscriptionSchema schema;

    public SubscriptionPayment() {
    }
    
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getExpectedPaymentTime() {
        return this.expectedPaymentTime;
    }

    public void setExpectedPaymentTime(Timestamp expectedPaymentTime) {
        this.expectedPaymentTime = expectedPaymentTime;
    }

    public Timestamp computeExpectedPaymentTime(Calendar cal, Integer year){
//        String st = "";
//        String newst = "";
//        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
//        st = sdf.format(this.paymentTime);  
//        String[] stArray = st.split("/");
//        //add the year of subscription
//        newst += (Integer.parseInt(stArray[0]) + year);
//        newst  =  newst + "/" + stArray[1]+ "/" + stArray[2] + ".0"; 
//        System.out.println(st + " " + newst);
//        return Timestamp.valueOf(newst);
        Timestamp exTime;
//        if(){
            //Calendar cur = Calendar.getInstance();
            cal.add(Calendar.YEAR, year);
            exTime = new Timestamp(cal.getTimeInMillis());
//        }else{
//            
//        }
        return exTime;
    }
    
    public SubscriptionSchema getSchema() {
        return schema;
    }

    public void setSchema(SubscriptionSchema schema) {
        this.schema = schema;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public SubscriptionPaymentReceipt getRecipt() {
        return recipt;
    }

    public void setRecipt(SubscriptionPaymentReceipt recipt) {
        this.recipt = recipt;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
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
        if (!(object instanceof SubscriptionPayment)) {
            return false;
        }
        SubscriptionPayment other = (SubscriptionPayment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.SubscriptionPayment[ id=" + id + " ]";
    }
    
}
