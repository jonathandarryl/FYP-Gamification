/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.entity;

import Common.entity.Account;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author chongyangsun
 */
@Entity
public class BiddingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long postId;
    private Double currentBid;
    private Long bidderCompanyId;
    private int biddingPeriod;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startTime;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endTime;
    private boolean ended;
    private boolean success;
    @ManyToOne
    private Account account;
    
    public BiddingRecord() {}

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
        if (!(object instanceof BiddingRecord)) {
            return false;
        }
        BiddingRecord other = (BiddingRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.BiddingRecord[ id=" + id + " ]";
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
     * @return the currentBid
     */
    public Double getCurrentBid() {
        return currentBid;
    }

    /**
     * @param currentBid the currentBid to set
     */
    public void setCurrentBid(Double currentBid) {
        this.currentBid = currentBid;
    }

    /**
     * @return the bidderCompanyId
     */
    public Long getBidderCompanyId() {
        return bidderCompanyId;
    }

    /**
     * @param bidderCompanyId the bidderCompanyId to set
     */
    public void setBidderCompanyId(Long bidderCompanyId) {
        this.bidderCompanyId = bidderCompanyId;
    }

    /**
     * @return the biddingPeriod
     */
    public int getBiddingPeriod() {
        return biddingPeriod;
    }

    /**
     * @param biddingPeriod the biddingPeriod to set
     */
    public void setBiddingPeriod(int biddingPeriod) {
        this.biddingPeriod = biddingPeriod;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the ended
     */
    public boolean isEnded() {
        return ended;
    }

    /**
     * @param ended the ended to set
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }
    
}
