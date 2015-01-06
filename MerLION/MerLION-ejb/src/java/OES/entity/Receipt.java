/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.entity;

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
public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne
    private Invoice invoice;
    
    private Timestamp receiptDate;
    private String ownerComapanyName;

    public Receipt() {
    }

    public Receipt(Invoice invoice) {
        this.invoice = invoice;
        this.receiptDate = new Timestamp(System.currentTimeMillis());
        this.ownerComapanyName = invoice.getOwnerCompanyName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Timestamp receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getOwnerComapanyName() {
        return ownerComapanyName;
    }

    public void setOwnerComapanyName(String ownerComapanyName) {
        this.ownerComapanyName = ownerComapanyName;
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
        if (!(object instanceof Receipt)) {
            return false;
        }
        Receipt other = (Receipt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OES.Receipt[ id=" + id + " ]";
    }
    
}
