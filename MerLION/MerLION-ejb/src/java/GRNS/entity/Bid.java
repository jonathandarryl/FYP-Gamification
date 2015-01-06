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

/**
 *
 * @author chongyangsun
 */
@Entity
public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long postId;
    private String postType;
    private Double currentBid;
    private Double winningPrice; // only for vickrey auction
    private Long bidderId;
    private String bidderType;
    private String bidderUsername;
    private Long bidderCompanyId;
    private String bidderCompanyName;
    private Timestamp placeDateTime;
//    
//    @ManyToOne
//    private AggregationPost aggregationPost;
//    @ManyToOne
//    private StoragePost storagePost;
//    @ManyToOne
//    private TransportPost transportPost;
    
    public Bid() {}
    
    public Bid(Long postId, String postType, Long bidderId, String bidderUsername, Long bidderCompanyId, String bidderCompanyName, Double currentBid) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        this.setPlaceDateTime(ts);
        this.setPostId(postId);
        this.setPostType(postType);
        this.setBidderId(bidderId);
        this.setBidderUsername(bidderUsername);
        this.setBidderCompanyId(bidderCompanyId);
        this.setBidderCompanyName(bidderCompanyName);
        this.setCurrentBid(currentBid);
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
        if (!(object instanceof Bid)) {
            return false;
        }
        Bid other = (Bid) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.Bid[ id=" + id + " ]";
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
     * @return the bidderId
     */
    public Long getBidderId() {
        return bidderId;
    }

    /**
     * @param bidderId the bidderId to set
     */
    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
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
    
//    /**
//     * @return the aggregationPost
//     */
//    public AggregationPost getAggregationPost() {
//        return aggregationPost;
//    }
//
//    /**
//     * @param aggregationPost the aggregationPost to set
//     */
//    public void setAggregationPost(AggregationPost aggregationPost) {
//        this.aggregationPost = aggregationPost;
//    }
//
//    /**
//     * @return the storagePost
//     */
//    public StoragePost getStoragePost() {
//        return storagePost;
//    }
//
//    /**
//     * @param storagePost the storagePost to set
//     */
//    public void setStoragePost(StoragePost storagePost) {
//        this.storagePost = storagePost;
//    }
//
//    /**
//     * @return the transportPost
//     */
//    public TransportPost getTransportPost() {
//        return transportPost;
//    }
//
//    /**
//     * @param transportPost the transportPost to set
//     */
//    public void setTransportPost(TransportPost transportPost) {
//        this.transportPost = transportPost;
//    }

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
     * @return the placeDateTime
     */
    public Timestamp getPlaceDateTime() {
        return placeDateTime;
    }

    /**
     * @param placeDateTime the placeDateTime to set
     */
    public void setPlaceDateTime(Timestamp placeDateTime) {
        this.placeDateTime = placeDateTime;
    }

    /**
     * @return the bidderCompanyName
     */
    public String getBidderCompanyName() {
        return bidderCompanyName;
    }

    /**
     * @param bidderCompanyName the bidderCompanyName to set
     */
    public void setBidderCompanyName(String bidderCompanyName) {
        this.bidderCompanyName = bidderCompanyName;
    }

    /**
     * @return the bidderUsername
     */
    public String getBidderUsername() {
        return bidderUsername;
    }

    /**
     * @param bidderUsername the bidderUsername to set
     */
    public void setBidderUsername(String bidderUsername) {
        this.bidderUsername = bidderUsername;
    }

    /**
     * @return the winningPrice
     */
    public Double getWinningPrice() {
        return winningPrice;
    }

    /**
     * @param winningPrice the winningPrice to set
     */
    public void setWinningPrice(Double winningPrice) {
        this.winningPrice = winningPrice;
    }

    /**
     * @return the bidderType
     */
    public String getBidderType() {
        return bidderType;
    }

    /**
     * @param bidderType the bidderType to set
     */
    public void setBidderType(String bidderType) {
        this.bidderType = bidderType;
    }

}
