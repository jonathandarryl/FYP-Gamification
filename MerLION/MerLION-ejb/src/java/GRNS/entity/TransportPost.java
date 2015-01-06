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

/**
 *
 * @author chongyangsun
 */
@Entity
public class TransportPost implements Serializable {
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
    private Double highestBid;
    private Double winningPrice;
    private Long winnerId;
    private String winnerType;
    private String winnerUsername;
    private Long winnerCompanyId;
    private String winnerCompanyName;
    private Boolean ended;
    private Boolean deleted;
    
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
    
    private List<Long> routesIdList;
    
    private Timestamp serviceStartTime;
    private Timestamp serviceEndTime;
    
    private Double capacity;
    
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Boolean orderCreated;
    private Long serviceOrderId;
    private Boolean paidOrNot;
    
    public TransportPost() {}
    
    public TransportPost(Account account, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, Boolean vickreyAuctionSpecified, Boolean blindAuctionSpecified, 
            Boolean englishAuctionSpecified, Location sourceLoc, Location destLoc, Double capacity, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) {
        this.setPostType("transport");
        Date postDate = new Date();
        this.setPostDateTime(new Timestamp(postDate.getTime()));
        this.setEndDateTime(endDateTime);
        this.setAccount(account);
        this.setCompanyId(companyId);
        this.setTitle(title);
        this.setContent(content);
        this.setInitBid(initBid);
        this.setHighestBid(0.0);
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

        this.setCapacity(capacity);
        this.setSourceLoc(sourceLoc);
        this.setDestLoc(destLoc);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setServiceStartTime(serviceStartTime);
        this.setServiceEndTime(serviceEndTime);
        this.setPaidOrNot(Boolean.FALSE);
        this.setOrderCreated(Boolean.FALSE);
    }
    
    public TransportPost(Account account, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, Boolean vickreyAuctionSpecified, Boolean blindAuctionSpecified, 
            Boolean englishAuctionSpecified, Location sourceLoc, Location destLoc, Double capacity, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime, List<Long> routesIdList) {
        this.setPostType("transport");
        Date postDate = new Date();
        this.setPostDateTime(new Timestamp(postDate.getTime()));
        this.setEndDateTime(endDateTime);
        this.setAccount(account);
        this.setCompanyId(companyId);
        this.setTitle(title);
        this.setContent(content);
        this.setInitBid(initBid);
        this.setHighestBid(0.0);
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

        this.setCapacity(capacity);
        this.setSourceLoc(sourceLoc);
        this.setDestLoc(destLoc);
        this.setPerishable(perishable);
        this.setFlammable(flammable);
        this.setPharmaceutical(pharmaceutical);
        this.setHighValue(highValue);
        this.setRoutesIdList(routesIdList);
        this.setServiceStartTime(serviceStartTime);
        this.setServiceEndTime(serviceEndTime);
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
        if (!(object instanceof TransportPost)) {
            return false;
        }
        TransportPost other = (TransportPost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GRNS.entity.TransportPost[ id=" + id + " ]";
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
     * @return the highestBid
     */
    public Double getHighestBid() {
        return highestBid;
    }

    /**
     * @param highestBid the highestBid to set
     */
    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
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
     * @return the routesIdList
     */
    public List<Long> getRoutesIdList() {
        return routesIdList;
    }

    /**
     * @param routesIdList the routesIdList to set
     */
    public void setRoutesIdList(List<Long> routesIdList) {
        this.routesIdList = routesIdList;
    }

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
     * @return the serviceStartTime
     */
    public Timestamp getServiceStartTime() {
        return serviceStartTime;
    }

    /**
     * @param serviceStartTime the serviceStartTime to set
     */
    public void setServiceStartTime(Timestamp serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    /**
     * @return the serviceEndTime
     */
    public Timestamp getServiceEndTime() {
        return serviceEndTime;
    }

    /**
     * @param serviceEndTime the serviceEndTime to set
     */
    public void setServiceEndTime(Timestamp serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
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
     * @return the capacity
     */
    public Double getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Double capacity) {
        this.capacity = capacity;
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

}
