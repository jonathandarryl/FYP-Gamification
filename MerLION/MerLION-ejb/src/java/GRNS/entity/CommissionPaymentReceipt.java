/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author chongyangsun
 */
@Entity
public class CommissionPaymentReceipt implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double price;
    private String description;
    private Timestamp paymentTime;// corresponding payment time
    private Timestamp creationTime;
    @OneToOne(mappedBy = "receipt")
    private CommissionPayment commissionPayment;
    
    private Long receiverId; //company user id
    private String receiverType;
    private Long receiverCompanyId;
    private String receiverCompanyName;
    
    private Long senderId;
    private String senderType;
    private Long senderCompanyId;
    private String senderCompanyName;
    
    private String postType;
    private Long postId;

    public CommissionPaymentReceipt() {
    }

    public CommissionPaymentReceipt(CommissionPayment commissionPayment) {
        this.setCommissionPayment(commissionPayment);
        Date current = new Date();
        this.setPostId(commissionPayment.getPostId());
        this.setPostType(commissionPayment.getPostType());
        this.setCreationTime(new Timestamp(current.getTime()));
        this.setReceiverId(commissionPayment.getSenderId());
        this.setReceiverType(commissionPayment.getSenderType());
        this.setReceiverCompanyId(commissionPayment.getSenderCompanyId());
        this.setReceiverCompanyName(commissionPayment.getSenderCompanyName());
        this.setSenderId(commissionPayment.getReceiverId());
        this.setSenderType(commissionPayment.getReceiverType());
        this.setSenderCompanyId(commissionPayment.getReceiverCompanyId());
        this.setSenderCompanyName(commissionPayment.getReceiverCompanyName());
        this.setPaymentTime(commissionPayment.getPaymentTime());
        this.setPrice(commissionPayment.getPrice());
    }
    
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommissionPayment getCommissionPayment() {
        return commissionPayment;
    }

    public void setCommissionPayment(CommissionPayment commissionPayment) {
        this.commissionPayment = commissionPayment;
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
        if (!(object instanceof CommissionPaymentReceipt)) {
            return false;
        }
        CommissionPaymentReceipt other = (CommissionPaymentReceipt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.CommissionPaymentReceipt[ id=" + id + " ]";
    }

    /**
     * @return the paymentTime
     */
    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    /**
     * @param paymentTime the paymentTime to set
     */
    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }

    /**
     * @return the creationTime
     */
    public Timestamp getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @return the receiverId
     */
    public Long getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId the receiverId to set
     */
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return the receiverCompanyId
     */
    public Long getReceiverCompanyId() {
        return receiverCompanyId;
    }

    /**
     * @param receiverCompanyId the receiverCompanyId to set
     */
    public void setReceiverCompanyId(Long receiverCompanyId) {
        this.receiverCompanyId = receiverCompanyId;
    }
    
    /**
     * @return the senderId
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the senderCompanyId
     */
    public Long getSenderCompanyId() {
        return senderCompanyId;
    }

    /**
     * @param senderCompanyId the senderCompanyId to set
     */
    public void setSenderCompanyId(Long senderCompanyId) {
        this.senderCompanyId = senderCompanyId;
    }

    /**
     * @return the postType
     */
    public String getPostType() {
        return postType;
    }

    /**
     * @param postType the postType to set
     */
    public void setPostType(String postType) {
        this.postType = postType;
    }

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /**
     * @return the receiverCompanyName
     */
    public String getReceiverCompanyName() {
        return receiverCompanyName;
    }

    /**
     * @param receiverCompanyName the receiverCompanyName to set
     */
    public void setReceiverCompanyName(String receiverCompanyName) {
        this.receiverCompanyName = receiverCompanyName;
    }

    /**
     * @return the senderCompanyName
     */
    public String getSenderCompanyName() {
        return senderCompanyName;
    }

    /**
     * @param senderCompanyName the senderCompanyName to set
     */
    public void setSenderCompanyName(String senderCompanyName) {
        this.senderCompanyName = senderCompanyName;
    }

    /**
     * @return the receiverType
     */
    public String getReceiverType() {
        return receiverType;
    }

    /**
     * @param receiverType the receiverType to set
     */
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     * @return the senderType
     */
    public String getSenderType() {
        return senderType;
    }

    /**
     * @param senderType the senderType to set
     */
    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }
    
}
