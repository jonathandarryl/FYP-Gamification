/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.entity;

import Common.entity.Account;
import Common.entity.Location;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.ejb.TimerHandle;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author chongyangsun
 */
@Entity
public class AggregationPost implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String postType; // aggregation, storage, or transport
    @ManyToOne
    private Account account;
    private Long companyId;
    private Timestamp postDateTime;
    private Timestamp endDateTime;
    
    private Boolean reservePriceSpecified;
    private Double reservePrice;
    
    private Boolean vickreyAuctionSpecified;
    private Boolean blindAuctionSpecified;
    private Boolean englishAuctionSpecified;
    private String auctionType;
    
    @Lob
    private TimerHandle timerHandle;
    
    private String image;
    private String title;
    @Lob
    private String content;
    private Double initBid;
    private Double lowestBid;
    private Double winningPrice;
    private Long winnerId;
    private String winnerType;
    private String winnerUsername;
    private Long winnerCompanyId;
    private String winnerCompanyName;
    private Boolean ended;
    private Boolean deleted;
    
    private List<Long> companyIdList;// customer company id list
    private List<Long> serviceOrderIdList;// corresponding order id list
    private List<Long> serviceContractIdList;
    private Timestamp requestStartTime;
    private Timestamp requestEndTime;
    
    private List<Long> productIdList;// included product list
    private List<Double> quantityList;// included quantity for each of the product. index match.
    
    private Double quantity;// aggregation order quantity
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="sourceCountry")),
        @AttributeOverride(name="state", column=@Column(name="sourceState")),
        @AttributeOverride(name="city", column=@Column(name="sourceCity")),
        @AttributeOverride(name="street", column=@Column(name="sourceStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="sourceBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="sourcepostalCode"))
    })
    private Location sourceLoc;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="country", column=@Column(name="destCountry")),
        @AttributeOverride(name="state", column=@Column(name="destState")),
        @AttributeOverride(name="city", column=@Column(name="destCity")),
        @AttributeOverride(name="street", column=@Column(name="destStreet")),
        @AttributeOverride(name="blockNo", column=@Column(name="destBlockNo")),
        @AttributeOverride(name="postalCode", column=@Column(name="destpostalCode"))
    })
    private Location destLoc;
        
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Boolean paidOrNot;
    private Boolean orderCreated;
    private Long serviceOrderId;
    
    @OneToOne
    private AggregatedOrder aggregatedOrder;
    
//    @OneToMany(mappedBy = "aggregationPost")
//    private List<Bid> bidList;
        
    public AggregationPost() {}
    
    public AggregationPost(Account account, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, Boolean vickreyAuctionSpecified, Boolean blindAuctionSpecified, 
            Boolean englishAuctionSpecified, Location sourceLoc, Location destLoc, Double quantity, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime) {
        this.setPostType("aggregation");
        Date postDate = new Date();
        this.setPostDateTime(new Timestamp(postDate.getTime()));
        this.setEndDateTime(endDateTime);
        this.setAccount(account);
        this.setCompanyId(companyId);
        this.setTitle(title);
        this.setContent(content);
        this.setInitBid(initBid);
        this.setLowestBid(0.0);
        this.setDeleted(false);
        this.setEnded(false);
        this.setReservePriceSpecified(reservePriceSpecified);
        if(reservePriceSpecified) {
            this.setReservePrice(reservePrice);
        } else {
            this.setReservePrice(999999999999.9);
        }
        this.setVickreyAuctionSpecified(vickreyAuctionSpecified);
        this.setBlindAuctionSpecified(blindAuctionSpecified);
        this.setEnglishAuctionSpecified(englishAuctionSpecified);
        if(vickreyAuctionSpecified) {
            this.setAuctionType("vickreyAuction");
        } else if(blindAuctionSpecified) {
            this.setAuctionType("blindAuction");
        } else {
            this.setAuctionType("englishAuction");
        }
        this.setQuantity(quantity);
        this.setSourceLoc(sourceLoc);
        this.setDestLoc(destLoc);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setRequestStartTime(requestStartTime);
        this.setRequestEndTime(requestEndTime);
        this.setPaidOrNot(Boolean.FALSE);
        this.setOrderCreated(Boolean.FALSE);
    }
    
    public AggregationPost(Account account, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, Boolean vickreyAuctionSpecified, Boolean blindAuctionSpecified, 
            Boolean englishAuctionSpecified, Location sourceLoc, Location destLoc, Double quantity, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime, 
            List<Long> companyIdList, List<Long> serviceOrderIdList, List<Long> productIdList, 
            List<Double> quantityList) {
        this.setPostType("aggregation");
        Date postDate = new Date();
        this.setPostDateTime(new Timestamp(postDate.getTime()));
        this.setEndDateTime(endDateTime);
        this.setAccount(account);
        this.setCompanyId(companyId);
        this.setTitle(title);
        this.setContent(content);
        this.setInitBid(initBid);
        this.setLowestBid(0.0);
        this.setDeleted(false);
        this.setEnded(false);
        this.setReservePriceSpecified(reservePriceSpecified);
        if(reservePriceSpecified) {
            this.setReservePrice(reservePrice);
        } else {
            this.setReservePrice(0.0);
        }
        this.setVickreyAuctionSpecified(vickreyAuctionSpecified);
        this.setBlindAuctionSpecified(blindAuctionSpecified);
        this.setEnglishAuctionSpecified(englishAuctionSpecified);
        if(vickreyAuctionSpecified) {
            this.setAuctionType("vickreyAuction");
        } else if(blindAuctionSpecified) {
            this.setAuctionType("blindAuction");
        } else {
            this.setAuctionType("englishAuction");
        }
        this.setQuantity(quantity);
        this.setSourceLoc(sourceLoc);
        this.setDestLoc(destLoc);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setCompanyIdList(companyIdList);
        this.setServiceOrderIdList(serviceOrderIdList);
        this.setProductIdList(productIdList);
        this.setQuantityList(quantityList);
        this.setRequestStartTime(requestStartTime);
        this.setRequestEndTime(requestEndTime);
        this.setPaidOrNot(Boolean.FALSE);
        this.setOrderCreated(Boolean.FALSE);
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
        if (!(object instanceof AggregationPost)) {
            return false;
        }
        AggregationPost other = (AggregationPost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.AggregationPost[ id=" + id + " ]";
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

    /**
     * @return the companyId
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the initBid
     */
    public Double getInitBid() {
        return initBid;
    }

    /**
     * @param initBid the initBid to set
     */
    public void setInitBid(Double initBid) {
        this.initBid = initBid;
    }

    /**
     * @return the companyIdList
     */
    public List<Long> getCompanyIdList() {
        return companyIdList;
    }

    /**
     * @param companyIdList the companyIdList to set
     */
    public void setCompanyIdList(List<Long> companyIdList) {
        this.companyIdList = companyIdList;
    }

    /**
     * @return the serviceOrderIdList
     */
    public List<Long> getServiceOrderIdList() {
        return serviceOrderIdList;
    }

    /**
     * @param serviceOrderIdList the serviceOrderIdList to set
     */
    public void setServiceOrderIdList(List<Long> serviceOrderIdList) {
        this.serviceOrderIdList = serviceOrderIdList;
    }

    /**
     * @return the productIdList
     */
    public List<Long> getProductIdList() {
        return productIdList;
    }

    /**
     * @param productIdList the productIdList to set
     */
    public void setProductIdList(List<Long> productIdList) {
        this.productIdList = productIdList;
    }

    /**
     * @return the quantityList
     */
    public List<Double> getQuantityList() {
        return quantityList;
    }

    /**
     * @param quantityList the quantityList to set
     */
    public void setQuantityList(List<Double> quantityList) {
        this.quantityList = quantityList;
    }

    /**
     * @return the sourceLoc
     */
    public Location getSourceLoc() {
        return sourceLoc;
    }

    /**
     * @param sourceLoc the sourceLoc to set
     */
    public void setSourceLoc(Location sourceLoc) {
        this.sourceLoc = sourceLoc;
    }

    /**
     * @return the destLoc
     */
    public Location getDestLoc() {
        return destLoc;
    }

    /**
     * @param destLoc the destLoc to set
     */
    public void setDestLoc(Location destLoc) {
        this.destLoc = destLoc;
    }

    /**
     * @return the perishable
     */
    public Boolean getPerishable() {
        return perishable;
    }

    /**
     * @param perishable the perishable to set
     */
    public void setPerishable(Boolean perishable) {
        this.perishable = perishable;
    }

    /**
     * @return the flammable
     */
    public Boolean getFlammable() {
        return flammable;
    }

    /**
     * @param flammable the flammable to set
     */
    public void setFlammable(Boolean flammable) {
        this.flammable = flammable;
    }

    /**
     * @return the pharmaceutical
     */
    public Boolean getPharmaceutical() {
        return pharmaceutical;
    }

    /**
     * @param pharmaceutical the pharmaceutical to set
     */
    public void setPharmaceutical(Boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    /**
     * @return the highValue
     */
    public Boolean getHighValue() {
        return highValue;
    }

    /**
     * @param highValue the highValue to set
     */
    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

    /**
     * @return the postDateTime
     */
    public Timestamp getPostDateTime() {
        return postDateTime;
    }

    /**
     * @param postDateTime the postDateTime to set
     */
    public void setPostDateTime(Timestamp postDateTime) {
        this.postDateTime = postDateTime;
    }

    /**
     * @return the endDateTime
     */
    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    /**
     * @param endDateTime the endDateTime to set
     */
    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * @return the lowestBid
     */
    public Double getLowestBid() {
        return lowestBid;
    }

    /**
     * @param lowestBid the lowestBid to set
     */
    public void setLowestBid(Double lowestBid) {
        this.lowestBid = lowestBid;
    }

//    /**
//     * @return the bidList
//     */
//    public List<Bid> getBidList() {
//        return bidList;
//    }
//
//    /**
//     * @param bidList the bidList to set
//     */
//    public void setBidList(List<Bid> bidList) {
//        this.bidList = bidList;
//    }

    /**
     * @return the reservePriceSpecified
     */
    public Boolean getReservePriceSpecified() {
        return reservePriceSpecified;
    }

    /**
     * @param reservePriceSpecified the reservePriceSpecified to set
     */
    public void setReservePriceSpecified(Boolean reservePriceSpecified) {
        this.reservePriceSpecified = reservePriceSpecified;
    }

    /**
     * @return the reservePrice
     */
    public Double getReservePrice() {
        return reservePrice;
    }

    /**
     * @param reservePrice the reservePrice to set
     */
    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

    /**
     * @return the vickreyAuctionSpecified
     */
    public Boolean getVickreyAuctionSpecified() {
        return vickreyAuctionSpecified;
    }

    /**
     * @param vickreyAuctionSpecified the vickreyAuctionSpecified to set
     */
    public void setVickreyAuctionSpecified(Boolean vickreyAuctionSpecified) {
        this.vickreyAuctionSpecified = vickreyAuctionSpecified;
    }

    /**
     * @return the blindAuctionSpecified
     */
    public Boolean getBlindAuctionSpecified() {
        return blindAuctionSpecified;
    }

    /**
     * @param blindAuctionSpecified the blindAuctionSpecified to set
     */
    public void setBlindAuctionSpecified(Boolean blindAuctionSpecified) {
        this.blindAuctionSpecified = blindAuctionSpecified;
    }

    /**
     * @return the englishAuctionSpecified
     */
    public Boolean getEnglishAuctionSpecified() {
        return englishAuctionSpecified;
    }

    /**
     * @param englishAuctionSpecified the englishAuctionSpecified to set
     */
    public void setEnglishAuctionSpecified(Boolean englishAuctionSpecified) {
        this.englishAuctionSpecified = englishAuctionSpecified;
    }

    /**
     * @return the ended
     */
    public Boolean getEnded() {
        return ended;
    }

    /**
     * @param ended the ended to set
     */
    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    /**
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the winnerId
     */
    public Long getWinnerId() {
        return winnerId;
    }

    /**
     * @param winnerId the winnerId to set
     */
    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    /**
     * @return the winnerCompanyId
     */
    public Long getWinnerCompanyId() {
        return winnerCompanyId;
    }

    /**
     * @param winnerCompanyId the winnerCompanyId to set
     */
    public void setWinnerCompanyId(Long winnerCompanyId) {
        this.winnerCompanyId = winnerCompanyId;
    }

    /**
     * @return the winnerUsername
     */
    public String getWinnerUsername() {
        return winnerUsername;
    }

    /**
     * @param winnerUsername the winnerUsername to set
     */
    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    /**
     * @return the winnerCompanyName
     */
    public String getWinnerCompanyName() {
        return winnerCompanyName;
    }

    /**
     * @param winnerCompanyName the winnerCompanyName to set
     */
    public void setWinnerCompanyName(String winnerCompanyName) {
        this.winnerCompanyName = winnerCompanyName;
    }

    /**
     * @return the auctionType
     */
    public String getAuctionType() {
        return auctionType;
    }

    /**
     * @param auctionType the auctionType to set
     */
    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    /**
     * @return the requestStartTime
     */
    public Timestamp getRequestStartTime() {
        return requestStartTime;
    }

    /**
     * @param requestStartTime the requestStartTime to set
     */
    public void setRequestStartTime(Timestamp requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    /**
     * @return the requestEndTime
     */
    public Timestamp getRequestEndTime() {
        return requestEndTime;
    }

    /**
     * @param requestEndTime the requestEndTime to set
     */
    public void setRequestEndTime(Timestamp requestEndTime) {
        this.requestEndTime = requestEndTime;
    }

    /**
     * @return the timerHandle
     */
    public TimerHandle getTimerHandle() {
        return timerHandle;
    }

    /**
     * @param timerHandle the timerHandle to set
     */
    public void setTimerHandle(TimerHandle timerHandle) {
        this.timerHandle = timerHandle;
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
     * @return the serviceContractIdList
     */
    public List<Long> getServiceContractIdList() {
        return serviceContractIdList;
    }

    /**
     * @param serviceContractIdList the serviceContractIdList to set
     */
    public void setServiceContractIdList(List<Long> serviceContractIdList) {
        this.serviceContractIdList = serviceContractIdList;
    }

    /**
     * @return the quantity
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the winnerType
     */
    public String getWinnerType() {
        return winnerType;
    }

    /**
     * @param winnerType the winnerType to set
     */
    public void setWinnerType(String winnerType) {
        this.winnerType = winnerType;
    }

    /**
     * @return the paidOrNot
     */
    public Boolean getPaidOrNot() {
        return paidOrNot;
    }

    /**
     * @param paidOrNot the paidOrNot to set
     */
    public void setPaidOrNot(Boolean paidOrNot) {
        this.paidOrNot = paidOrNot;
    }

    /**
     * @return the orderCreated
     */
    public Boolean getOrderCreated() {
        return orderCreated;
    }

    /**
     * @param orderCreated the orderCreated to set
     */
    public void setOrderCreated(Boolean orderCreated) {
        this.orderCreated = orderCreated;
    }

    /**
     * @return the serviceOrderId
     */
    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    /**
     * @param serviceOrderId the serviceOrderId to set
     */
    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    /**
     * @return the aggregatedOrder
     */
    public AggregatedOrder getAggregatedOrder() {
        return aggregatedOrder;
    }

    /**
     * @param aggregatedOrder the aggregatedOrder to set
     */
    public void setAggregatedOrder(AggregatedOrder aggregatedOrder) {
        this.aggregatedOrder = aggregatedOrder;
    }

}
