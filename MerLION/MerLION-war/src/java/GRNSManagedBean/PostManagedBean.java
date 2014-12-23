/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import CRM.entity.ServiceOrder;
import Common.entity.Account;
import Common.entity.Company;
import Common.entity.Location;
import Common.session.AccountManagementSessionBeanLocal;
import GRNS.entity.AggregationPost;
import GRNS.entity.Bid;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import GRNS.session.BiddingManagementSessionBeanLocal;
import GRNS.session.PostManagementSessionBeanLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import util.exception.AccountTypeNotExistException;
import util.exception.BidInvalidException;
import util.exception.PostNotExistException;
import util.exception.UserNotExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "postManagedBean")
@SessionScoped
public class PostManagedBean implements Serializable {

    /**
     * Creates a new instance of PostManagedBean
     */
    public PostManagedBean() {
    }

    // initialize variables for storage post
    private Long accountId;
    private Long companyId;
    private String title;
    private String content;
    private Double initBid;
    private Timestamp endDateTime;
    private boolean reservePriceSpecified;
    private Double reservePrice;
    private String auctionType;
    private String warehouseName;
    private Long warehouseId;
    private Double capacity;
    private String street;
    private String blockNo;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Location location;

    private boolean perishable;
    private boolean flammable;
    private boolean pharmaceutical;
    private boolean highValue;

    // variables for aggregation post
    private Double quantity;
    private String sourceStreet;
    private String sourceBlockNo;
    private String sourceCity;
    private String sourceState;
    private String sourceCountry;
    private String sourcePostalCode;
    private String destStreet;
    private String destBlockNo;
    private String destCity;
    private String destState;
    private String destCountry;
    private String destPostalCode;
    private Timestamp startTime;
    private Timestamp endTime;

    private Date date;
    private Date startDate;
    private Date endDate;
    private String booleanPrice;
    private Long storagePostId;
    private Long transportPostId;
    private Long aggregationPostId;

    private List<StoragePost> currentSPList;
    private List<StoragePost> historySPList;
    private List<StoragePost> biddingSPList;
    private List<StoragePost> historyBiddingSPList;
    private StoragePost selectedSP;
    private StoragePost onChangeSP;

    private List<TransportPost> currentTPList;
    private List<TransportPost> historyTPList;
    private List<TransportPost> biddingTPList;
    private List<TransportPost> historyBiddingTPList;
    private TransportPost selectedTP;
    private TransportPost onChangeTP;

    private List<AggregationPost> currentAPList;
    private List<AggregationPost> historyAPList;
    private List<AggregationPost> biddingAPList;
    private List<AggregationPost> historyBiddingAPList;
    private AggregationPost selectedAP;
    private AggregationPost onChangeAP;

    private List<AggregationPost> allAP;
    private List<StoragePost> allSP;
    private List<TransportPost> allTP;

    private String option;
    private Double minPrice;
    private Double maxPrice;
    private String postType;
    private boolean checkIfEnded;
    private boolean checkIfNotEnded;
    private boolean checkIfNotAuthor;
    private boolean checkStatus;
    private boolean checkBlind;
    private boolean checkFirstBlind;
    private Double placeBid;
    private int bidsCount;
    private List<Bid> allBids;

    private String searchCriteria;
    private String searchType;
    private List<String> warehouseNameList;

    @EJB
    PostManagementSessionBeanLocal pmsbl;
    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    BiddingManagementSessionBeanLocal bmsbl;
    @EJB
    FacilityManagementLocal fml;

    @PostConstruct
    public void init() {
        currentSPList = new ArrayList<>();
        historySPList = new ArrayList<>();
        biddingSPList = new ArrayList<>();
        historyBiddingSPList = new ArrayList<>();
        selectedSP = new StoragePost();
        onChangeSP = new StoragePost();
        currentTPList = new ArrayList<>();
        historyTPList = new ArrayList<>();
        biddingTPList = new ArrayList<>();
        historyBiddingTPList = new ArrayList<>();
        selectedTP = new TransportPost();
        onChangeTP = new TransportPost();
        currentAPList = new ArrayList<>();
        historyAPList = new ArrayList<>();
        historyBiddingAPList = new ArrayList<>();
        biddingAPList = new ArrayList<>();
        selectedAP = new AggregationPost();
        onChangeAP = new AggregationPost();
        location = new Location();
        allAP = new ArrayList<>();
        allSP = new ArrayList<>();
        allTP = new ArrayList<>();
        warehouseNameList = new ArrayList<>();
    }

    public void createAggregationPost() throws IOException {
        System.out.println("Inside create aggregation post");

        endDateTime = new Timestamp(date.getTime());
        startTime = new Timestamp(startDate.getTime());
        endTime = new Timestamp(endDate.getTime());

        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        companyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");

        AggregationPost ap = pmsbl.createAggregationPost(accountId, companyId, title, content, initBid, endDateTime, reservePriceSpecified,
                reservePrice, auctionType, quantity, sourceStreet, sourceBlockNo, sourceCity, sourceState, sourceCountry, sourcePostalCode,
                destStreet, destBlockNo, destCity, destState, destCountry, destPostalCode, perishable, flammable, pharmaceutical, highValue,
                startTime, endTime);
        if (ap != null) {
            this.displayGrowl("Aggregation post successfully created");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewUserCurrentAggregationPost.xhtml");
            title = null;
            content = null;
            initBid = null;
            endDateTime = null;
            date = null;
            reservePriceSpecified = false;
            reservePrice = null;
            auctionType = null;
            quantity = null;
            warehouseId = null;
            sourceStreet = null;
            sourceBlockNo = null;
            sourceCity = null;
            sourceState = null;
            sourceCountry = null;
            sourcePostalCode = null;
            destStreet = null;
            destBlockNo = null;
            destCity = null;
            destState = null;
            destCountry = null;
            destPostalCode = null;
            perishable = false;
            flammable = false;
            pharmaceutical = false;
            highValue = false;
            startTime = null;
            startDate = null;
            endTime = null;
            endDate = null;
        } else if (ap == null) {
            this.errorMsg("Aggregation post is not created");
        }
    }

    public void populateAllWarehouse() throws AccountTypeNotExistException, UserNotExistException {
        String temp = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        String username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        if (!temp.equals("External")) {
            Account a = amsbl.retrieveAccount(username);
            Company c = a.getCompany();
            List<Warehouse> warehouseList = c.getOwnedWarehouseList();
            if (warehouseList.isEmpty()) {
                System.out.println("Warehouse list is empty");
            }
            this.warehouseNameList.clear();
            for (int i = 0; i < warehouseList.size(); i++) {
                String name = warehouseList.get(i).getName();
                this.warehouseNameList.add(name);
            }
        }
    }

    public void createStoragePost() throws WarehouseNotExistException, IOException {
        endDateTime = new Timestamp(date.getTime());
        startTime = new Timestamp(startDate.getTime());
        endTime = new Timestamp(endDate.getTime());

        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        companyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");

        String companyName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyName");

        if (warehouseName != null) {
            Warehouse w = fml.retrieveWarehouse(warehouseName, companyName);
            warehouseId = w.getId();
            this.street = w.getLocation().getStreet();
            this.blockNo = w.getLocation().getBlockNo();
            this.city = w.getLocation().getCity();
            this.state = w.getLocation().getState();
            this.country = w.getLocation().getCountry();
            this.postalCode = w.getLocation().getPostalCode();
        }

        StoragePost sp = pmsbl.createStoragePost(accountId, companyId, title, content, initBid, endDateTime, reservePriceSpecified,
                reservePrice, auctionType, warehouseId, capacity, street,
                blockNo, city, state, country, postalCode, perishable, flammable, pharmaceutical, highValue, startTime, endTime);

        if (sp != null) {
            this.displayGrowl("Storage post successfully created");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewUserCurrentStoragePost.xhtml");
            title = null;
            content = null;
            initBid = null;
            endDateTime = null;
            date = null;
            reservePriceSpecified = false;
            reservePrice = null;
            auctionType = null;
            warehouseId = null;
            capacity = null;
            street = null;
            blockNo = null;
            city = null;
            state = null;
            country = null;
            postalCode = null;
            perishable = false;
            flammable = false;
            pharmaceutical = false;
            highValue = false;
            startTime = null;
            startDate = null;
            endTime = null;
            endDate = null;
        } else if (sp == null) {
            this.errorMsg("Storage post is not created");
        }
    }

    public void createTransportPost() throws IOException {

        endDateTime = new Timestamp(date.getTime());
        startTime = new Timestamp(startDate.getTime());
        endTime = new Timestamp(endDate.getTime());

        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        companyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");

        TransportPost tp = pmsbl.createTransportPost(accountId, companyId, title, content, initBid, endDateTime, reservePriceSpecified,
                reservePrice, auctionType, capacity, sourceStreet, sourceBlockNo, sourceCity, sourceState, sourceCountry, sourcePostalCode,
                destStreet, destBlockNo, destCity, destState, destCountry, destPostalCode, perishable, flammable, pharmaceutical, highValue,
                startTime, endTime);

        if (tp != null) {
            this.displayGrowl("Transport post successfully created");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewUserCurrentTransportPost.xhtml");
            title = null;
            content = null;
            initBid = null;
            endDateTime = null;
            date = null;
            reservePriceSpecified = false;
            reservePrice = null;
            auctionType = null;
            capacity = null;
            sourceStreet = null;
            sourceBlockNo = null;
            sourceCity = null;
            sourceState = null;
            sourceCountry = null;
            sourcePostalCode = null;
            destStreet = null;
            destBlockNo = null;
            destCity = null;
            destState = null;
            destCountry = null;
            destPostalCode = null;
            perishable = false;
            flammable = false;
            pharmaceutical = false;
            highValue = false;
            startTime = null;
            startDate = null;
            endTime = null;
            endDate = null;
        } else if (tp == null) {
            this.errorMsg("Transport post is not created");
        }
    }

    public void viewOneAggregationPost(Long postId) throws IOException {
        onChangeAP = pmsbl.retrieveAggregationPostById(postId);

        this.aggregationPostId = onChangeAP.getId();
        this.title = onChangeAP.getTitle();
        this.content = onChangeAP.getContent();
        this.initBid = onChangeAP.getInitBid();
        this.endDateTime = onChangeAP.getEndDateTime();
        this.reservePriceSpecified = onChangeAP.getReservePriceSpecified();
        this.reservePrice = onChangeAP.getReservePrice();
        if (onChangeAP.getBlindAuctionSpecified() == true) {
            this.auctionType = "Blind Auction";
        }
        if (onChangeAP.getEnglishAuctionSpecified() == true) {
            this.auctionType = "English Auction";
        }
        if (onChangeAP.getVickreyAuctionSpecified() == true) {
            this.auctionType = "Vickrey Auction";
        }
        this.quantity = onChangeAP.getQuantity();
        this.startTime = onChangeAP.getRequestStartTime();
        this.endTime = onChangeAP.getRequestEndTime();
        this.sourceStreet = onChangeAP.getSourceLoc().getStreet();
        this.sourceBlockNo = onChangeAP.getSourceLoc().getBlockNo();
        this.sourceCity = onChangeAP.getSourceLoc().getCity();
        this.sourceState = onChangeAP.getSourceLoc().getState();
        this.sourceCountry = onChangeAP.getSourceLoc().getCountry();
        this.sourcePostalCode = onChangeAP.getSourceLoc().getPostalCode();
        this.destStreet = onChangeAP.getDestLoc().getStreet();
        this.destBlockNo = onChangeAP.getDestLoc().getBlockNo();
        this.destCity = onChangeAP.getDestLoc().getCity();
        this.destState = onChangeAP.getDestLoc().getState();
        this.destCountry = onChangeAP.getDestLoc().getCountry();
        this.destPostalCode = onChangeAP.getDestLoc().getPostalCode();
        this.perishable = onChangeAP.getPerishable();
        this.flammable = onChangeAP.getFlammable();
        this.pharmaceutical = onChangeAP.getPharmaceutical();
        this.highValue = onChangeAP.getHighValue();

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/updateAggregationPost.xhtml");
    }

    public void updateAggregationPost() throws PostNotExistException {
        Timestamp temp = this.endDateTime;
        if (date != null) {
            temp = new Timestamp(date.getTime());
        } else {
            temp = this.endDateTime;
        }
        if (temp != this.endDateTime) {
            this.endDateTime = temp;
        }

        Timestamp temp2 = this.startTime;
        if (startDate != null) {
            temp2 = new Timestamp(startDate.getTime());
        } else {
            temp2 = this.startTime;
        }
        if (temp2 != this.startTime) {
            this.startTime = temp2;
        }

        Timestamp temp3 = this.endTime;
        if (endDate != null) {
            temp3 = new Timestamp(endDate.getTime());
        } else {
            temp3 = this.endTime;
        }
        if (temp3 != this.endTime) {
            this.endTime = temp3;
        }

        boolean checkAP = pmsbl.updateAggregationPost(aggregationPostId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, auctionType, quantity, sourceStreet, sourceBlockNo,
                sourceCity, sourceState, sourceCountry, sourcePostalCode, destStreet, destBlockNo, destCity,
                destState, destCountry, destPostalCode, perishable, flammable, pharmaceutical, highValue, startTime, endTime);

        if (checkAP == true) {
            this.displayGrowl("Update successful");
        } else {
            this.errorMsg("Post cannot be updated at the current stage. There are already bidders for this post.");
        }
    }

    public void viewUserCurrentAggregationPost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        List<AggregationPost> temp = pmsbl.retrieveAggregationPost(accountId);
        this.currentAPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            AggregationPost ap = temp.get(i);
            boolean flag = ap.getEnded();
            if (flag == false) {
                this.currentAPList.add(ap);
            }
        }
    }

    public void viewUserHistoryAggregationPost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        postType = "aggregation";
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        List<AggregationPost> temp = pmsbl.retrieveAggregationPost(accountId);
        this.historyAPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            AggregationPost ap = temp.get(i);
            boolean flag = ap.getEnded();
            System.out.println("Flag is " + flag);
            // remember to change it back to true
            // this is only for testing
            if (flag == true) {
                this.historyAPList.add(ap);
            }
        }
    }

    public void retrieveAggregationPostWithWinner() throws IOException {
        List<AggregationPost> temp = new ArrayList();
        for (int i = 0; i < historyAPList.size(); i++) {
            AggregationPost p = historyAPList.get(i);
            temp.add(p);
        }
        this.historyAPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            AggregationPost a = temp.get(i);
            if (a.getWinningPrice() != null) {
                historyAPList.add(a);
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewHistoryAggregationWithWinner.xhtml");
    }

    public void viewAggregationBid() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        List<AggregationPost> temp = pmsbl.retrieveBidAggregationPost(accountId);
        this.biddingAPList.clear();
        this.historyBiddingAPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getEnded() == false) {
                biddingAPList.add(temp.get(i));
            } else {
                historyBiddingAPList.add(temp.get(i));
            }
        }
    }

    public void viewOneStoragePost(Long postId) throws IOException {
        onChangeSP = pmsbl.retrieveStoragePostById(postId);

        this.storagePostId = onChangeSP.getId();
        this.title = onChangeSP.getTitle();
        this.content = onChangeSP.getContent();
        this.initBid = onChangeSP.getInitBid();
        this.endDateTime = onChangeSP.getEndDateTime();
        this.reservePriceSpecified = onChangeSP.getReservePriceSpecified();
        this.reservePrice = onChangeSP.getReservePrice();
        if (onChangeSP.getBlindAuctionSpecified() == true) {
            this.auctionType = "Blind Auction";
        }
        if (onChangeSP.getEnglishAuctionSpecified() == true) {
            this.auctionType = "English Auction";
        }
        if (onChangeSP.getVickreyAuctionSpecified() == true) {
            this.auctionType = "Vickrey Auction";
        }
        this.warehouseId = onChangeSP.getWarehouseId();
        this.capacity = onChangeSP.getCapacity();
        this.startTime = onChangeSP.getServiceStartTime();
        this.endTime = onChangeSP.getServiceEndTime();
        this.street = onChangeSP.getLocation().getStreet();
        this.blockNo = onChangeSP.getLocation().getBlockNo();
        this.city = onChangeSP.getLocation().getCity();
        this.state = onChangeSP.getLocation().getState();
        this.country = onChangeSP.getLocation().getCountry();
        this.postalCode = onChangeSP.getLocation().getPostalCode();
        this.perishable = onChangeSP.getPerishable();
        this.flammable = onChangeSP.getFlammable();
        this.pharmaceutical = onChangeSP.getPharmaceutical();
        this.highValue = onChangeSP.getHighValue();

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/updateStoragePost.xhtml");
    }

    public void updateStoragePost() throws PostNotExistException {
        System.out.println("Inside update storage post");

        Timestamp temp = this.endDateTime;
        if (date != null) {
            temp = new Timestamp(date.getTime());
        } else {
            temp = this.endDateTime;
        }
        if (temp != this.endDateTime) {
            this.endDateTime = temp;
        }

        Timestamp temp2 = this.startTime;
        if (startDate != null) {
            temp2 = new Timestamp(startDate.getTime());
        } else {
            temp2 = this.startTime;
        }
        if (temp2 != this.startTime) {
            this.startTime = temp2;
        }

        Timestamp temp3 = this.endTime;
        if (endDate != null) {
            temp3 = new Timestamp(endDate.getTime());
        } else {
            temp3 = this.endTime;
        }
        if (temp3 != this.endTime) {
            this.endTime = temp3;
        }

        boolean checkSP = pmsbl.updateStoragePost(storagePostId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, auctionType, warehouseId, capacity, street, blockNo,
                city, state, country, postalCode, perishable, flammable, pharmaceutical, highValue, startTime, endTime);

        if (checkSP == true) {
            this.displayGrowl("Update successful");
        } else {
            this.errorMsg("Post cannot be updated at the current state. There are already bidders for this post.");
        }
    }

    public void viewUserCurrentStoragePost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        List<StoragePost> temp = pmsbl.retrieveStoragePost(accountId);
        this.currentSPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            StoragePost sp = temp.get(i);
            boolean flag = sp.getEnded();
            if (flag == false) {
                this.currentSPList.add(sp);
            }
        }
    }

    public void viewUserHistoryStoragePost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        postType = "storage";
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        List<StoragePost> temp = pmsbl.retrieveStoragePost(accountId);
        this.historySPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            StoragePost sp = temp.get(i);
            boolean flag = sp.getEnded();
            System.out.println("Flag is " + flag);
            // remember to change it back to true
            // this is only for testing
            if (flag == true) {
                this.historySPList.add(sp);
            }
        }
    }

    public void retrieveStoragePostWithWinner() throws IOException {
        System.out.println("Size of the history SP list is " + historySPList.size());
        List<StoragePost> temp = new ArrayList();
        for (int i = 0; i < historySPList.size(); i++) {
            StoragePost p = historySPList.get(i);
            temp.add(p);
        }
        this.historySPList.clear();
        System.out.println("Size of the list is " + temp.size());
        for (int i = 0; i < temp.size(); i++) {
            StoragePost s = temp.get(i);
            System.out.println("Winner price is " + s.getWinningPrice());
            if (s.getWinningPrice() != null) {
                historySPList.add(s);
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewHistoryStorageWithWinner.xhtml");
    }

    public void viewStorageBid() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        List<StoragePost> temp = pmsbl.retrieveBidStoragePost(accountId);
        this.biddingSPList.clear();
        this.historyBiddingSPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getEnded() == false) {
                biddingSPList.add(temp.get(i));
            } else {
                historyBiddingSPList.add(temp.get(i));
            }
        }
    }

    public void viewOneTransportPost(Long postId) throws IOException {
        onChangeTP = pmsbl.retrieveTransportPostById(postId);

        this.transportPostId = onChangeTP.getId();
        this.title = onChangeTP.getTitle();
        this.content = onChangeTP.getContent();
        this.initBid = onChangeTP.getInitBid();
        this.endDateTime = onChangeTP.getEndDateTime();
        this.reservePriceSpecified = onChangeTP.getReservePriceSpecified();
        this.reservePrice = onChangeTP.getReservePrice();
        if (onChangeTP.getBlindAuctionSpecified() == true) {
            this.auctionType = "Blind Auction";
        }
        if (onChangeTP.getEnglishAuctionSpecified() == true) {
            this.auctionType = "English Auction";
        }
        if (onChangeTP.getVickreyAuctionSpecified() == true) {
            this.auctionType = "Vickrey Auction";
        }
        this.capacity = onChangeTP.getCapacity();
        this.startTime = onChangeTP.getServiceStartTime();
        this.endTime = onChangeTP.getServiceEndTime();
        this.sourceStreet = onChangeTP.getSourceLoc().getStreet();
        this.sourceBlockNo = onChangeTP.getSourceLoc().getBlockNo();
        this.sourceCity = onChangeTP.getSourceLoc().getCity();
        this.sourceState = onChangeTP.getSourceLoc().getState();
        this.sourceCountry = onChangeTP.getSourceLoc().getCountry();
        this.sourcePostalCode = onChangeTP.getSourceLoc().getPostalCode();
        this.destStreet = onChangeTP.getDestLoc().getStreet();
        this.destBlockNo = onChangeTP.getDestLoc().getBlockNo();
        this.destCity = onChangeTP.getDestLoc().getCity();
        this.destState = onChangeTP.getDestLoc().getState();
        this.destCountry = onChangeTP.getDestLoc().getCountry();
        this.destPostalCode = onChangeTP.getDestLoc().getPostalCode();
        this.perishable = onChangeTP.getPerishable();
        this.flammable = onChangeTP.getFlammable();
        this.pharmaceutical = onChangeTP.getPharmaceutical();
        this.highValue = onChangeTP.getHighValue();

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/updateTransportPost.xhtml");
    }

    public void updateTransportPost() throws PostNotExistException {
        Timestamp temp = this.endDateTime;
        if (date != null) {
            temp = new Timestamp(date.getTime());
        } else {
            temp = this.endDateTime;
        }
        if (temp != this.endDateTime) {
            this.endDateTime = temp;
        }

        Timestamp temp2 = this.startTime;
        if (startDate != null) {
            temp2 = new Timestamp(startDate.getTime());
        } else {
            temp2 = this.startTime;
        }
        if (temp2 != this.startTime) {
            this.startTime = temp2;
        }

        Timestamp temp3 = this.endTime;
        if (endDate != null) {
            temp3 = new Timestamp(endDate.getTime());
        } else {
            temp3 = this.endTime;
        }
        if (temp3 != this.endTime) {
            this.endTime = temp3;
        }

        boolean checkTP = pmsbl.updateTransportPost(transportPostId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, auctionType, capacity, sourceStreet, sourceBlockNo,
                sourceCity, sourceState, sourceCountry, sourcePostalCode, destStreet, destBlockNo, destCity,
                destState, destCountry, destPostalCode, perishable, flammable, pharmaceutical, highValue, startTime, endTime);

        if (checkTP == true) {
            this.displayGrowl("Update successful");
        } else {
            this.errorMsg("Post cannot be updated at the current state. There are already bidders for this post.");
        }
    }

    public void viewUserCurrentTransportPost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        List<TransportPost> temp = pmsbl.retrieveTransportPost(accountId);
        this.currentTPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            TransportPost tp = temp.get(i);
            boolean flag = tp.getEnded();
            if (flag == false) {
                this.currentTPList.add(tp);
            }
        }
    }

    public void viewUserHistoryTransportPost() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        postType = "transport";
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        List<TransportPost> temp = pmsbl.retrieveTransportPost(accountId);
        this.historyTPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            TransportPost tp = temp.get(i);
            boolean flag = tp.getEnded();
            System.out.println("Flag is " + flag);
            // remember to change it back to true
            // this is only for testing
            if (flag == true) {
                this.historyTPList.add(tp);
            }
        }
    }

    public void retrieveTransportPostWithWinner() throws IOException {
        List<TransportPost> temp = new ArrayList();
        for (int i = 0; i < historyTPList.size(); i++) {
            TransportPost p = historyTPList.get(i);
            temp.add(p);
        }
        this.historyTPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            TransportPost t = temp.get(i);
            if (t.getWinningPrice() != null) {
                historyTPList.add(t);
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewHistoryTransportWithWinner.xhtml");
    }

    public void viewTransportBid() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        List<TransportPost> temp = pmsbl.retrieveBidTransportPost(accountId);
        this.biddingTPList.clear();
        this.historyBiddingTPList.clear();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getEnded() == false) {
                biddingTPList.add(temp.get(i));
            } else {
                historyBiddingTPList.add(temp.get(i));
            }
        }
    }

    public void viewAllStoragePost() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allSP = pmsbl.retrieveAllStoragePost("all");
        this.postType = "storage";

        if (allSP.isEmpty()) {
            this.displayGrowl("There are no storage post");
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllStoragePost.xhtml");
    }

    public void viewAllTransportPost() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allTP = pmsbl.retrieveAllTransportPost("all");
        this.postType = "transport";

        if (allTP.isEmpty()) {
            this.displayGrowl("There are no transport post");
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllTransportPost.xhtml");
    }

    public void viewAllAggregationPost() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allAP = pmsbl.retrieveAllAggregationPost("all");
        this.postType = "aggregation";

        if (allAP.isEmpty()) {
            this.displayGrowl("There are no aggregation post");
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllAggregationPost.xhtml");
    }

    public void viewPurchaseHistoryAggregation() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        biddingAPList = pmsbl.retrieveWinningAggregationPost(accountId);
    }

    public void viewPurchaseHistoryStorage() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        biddingSPList = pmsbl.retrieveWinningStoragePost(accountId);
    }

    public void viewPurchaseHistoryTransport() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

        biddingTPList = pmsbl.retrieveWinningTransportPost(accountId);
    }

    public void searchPost() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (searchType.equals("aggregation")) {
            allAP = pmsbl.searchAggregationPost(searchCriteria);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllAggregationPost.xhtml");
        }
        if (searchType.equals("storage")) {
            allSP = pmsbl.searchStoragePost(searchCriteria);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllStoragePost.xhtml");
        }
        if (searchType.equals("transport")) {
            allTP = pmsbl.searchTransportPost(searchCriteria);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllTransportPost.xhtml");
        }
    }

    public void setFilterEnglish() throws IOException {
        this.auctionType = "english";
        this.filterByType(auctionType);
    }

    public void setFilterBlind() throws IOException {
        this.auctionType = "blind";
        this.filterByType(auctionType);
    }

    public void setFilterVickrey() throws IOException {
        this.auctionType = "vickrey";
        this.filterByType(auctionType);
    }

    private void filterByType(String auctionType) throws IOException {
        if (postType.equals("aggregation")) {
            allAP = pmsbl.retrieveAllAggregationPost(auctionType);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllAggregationPost.xhtml");
        }
        if (postType.equals("storage")) {
            allSP = pmsbl.retrieveAllStoragePost(auctionType);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllStoragePost.xhtml");
        }
        if (postType.equals("transport")) {
            allTP = pmsbl.retrieveAllTransportPost(auctionType);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllTransportPost.xhtml");
        }
    }

    public void setFilterNew() throws IOException {
        this.option = "new";
        this.filterByTime("new");
    }

    public void setFilterEnding() throws IOException {
        this.option = "ending";
        this.filterByTime("ending");
    }

    private void filterByTime(String flag) throws IOException {
        if (postType.equals("aggregation")) {
            allAP = pmsbl.filterAggregationPostByTime(allAP, flag);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllAggregationPost.xhtml");
        }
        if (postType.equals("storage")) {
            allSP = pmsbl.filterStoragePostByTime(allSP, flag);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllStoragePost.xhtml");
        }
        if (postType.equals("transport")) {
            allTP = pmsbl.filterTransportPostByTime(allTP, flag);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllTransportPost.xhtml");
        }
    }

    public void filterByPrice() throws IOException {
        if (postType.equals("aggregation")) {
            allAP = pmsbl.filterAggergationPostByPrice(allAP, minPrice, maxPrice);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllAggregationPost.xhtml");
        }
        if (postType.equals("storage")) {
            allSP = pmsbl.filterStoragePostByPrice(allSP, minPrice, maxPrice);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllStoragePost.xhtml");
        }
        if (postType.equals("transport")) {
            allTP = pmsbl.filterTransportPostByPrice(allTP, minPrice, maxPrice);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewAllTransportPost.xhtml");
        }
        minPrice = null;
        maxPrice = null;
    }

    public void placeBid(Long postId) {
        try {
            accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");

            Bid temp = new Bid();
            if (auctionType.equals("blindAuction")) {
                System.out.println("Inside create blind bid");
                temp = bmsbl.createBlindBid(accountId, postId, postType, placeBid);
            } else {
                temp = bmsbl.createNewBid(accountId, postId, postType, placeBid);
            }

            if (temp != null) {
                if (postType.equals("aggregation")) {
                    selectedAP.setLowestBid(placeBid);
                } else if (postType.equals("transport")) {
                    selectedTP.setHighestBid(placeBid);
                } else if (postType.equals("storage")) {
                    selectedSP.setHighestBid(placeBid);
                }
                placeBid = null;
                bidsCount = bmsbl.retrieveBidsCount(postId, postType);
                allBids = bmsbl.retrieveAllBids(postId, postType);
                this.displayGrowl("You have successfully put a bid");
            }
        } catch (BidInvalidException ex) {
            if (postType.equals("aggregation")) {
                this.errorMsg("Bidding should be at least 10 dollars lower than the lowest bid to be valid");
            } else {
                this.errorMsg("Bidding should be at least 10 dollars higher than the highest bid to be valid");
            }
        }
    }

    public void viewDetailAggregationPost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedAP = pmsbl.retrieveAggregationPostById(postId);
        auctionType = selectedAP.getAuctionType();
        this.checkIfBlind();
        checkIfEnded = selectedAP.getEnded();
        postType = "aggregation";
        bidsCount = bmsbl.retrieveBidsCount(postId, postType);
        allBids = bmsbl.retrieveAllBids(postId, postType);

        if (!selectedAP.getAccount().getId().equals(accountId)) {
            this.checkIfNotAuthor = true;
        } else {
            this.checkIfNotAuthor = false;
        }

        if (this.checkIfEnded == true) {
            this.checkIfNotEnded = false;
        } else {
            this.checkIfNotEnded = true;
        }

        if ((this.checkIfNotEnded) && (this.checkIfNotAuthor)) {
            this.checkStatus = true;
        } else {
            this.checkStatus = false;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewOneAggregationPost.xhtml");
    }

    public void viewDetailStoragePost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedSP = pmsbl.retrieveStoragePostById(postId);
        checkIfEnded = selectedSP.getEnded();
        postType = "storage";
        auctionType = selectedSP.getAuctionType();
        this.checkIfBlind();
        bidsCount = bmsbl.retrieveBidsCount(postId, postType);
        allBids = bmsbl.retrieveAllBids(postId, postType);

//        if (allBids.isEmpty()) {
//            this.faceMsg("There are no Bid.");
//        }
        if (!selectedSP.getAccount().getId().equals(accountId)) {
            this.checkIfNotAuthor = true;
        } else {
            this.checkIfNotAuthor = false;
        }

        if (this.checkIfEnded == true) {
            this.checkIfNotEnded = false;
        } else {
            this.checkIfNotEnded = true;
        }

        if ((this.checkIfNotEnded) && (this.checkIfNotAuthor)) {
            this.checkStatus = true;
        } else {
            this.checkStatus = false;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewOneStoragePost.xhtml");
    }

    public void viewDetailTransportPost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedTP = pmsbl.retrieveTransportPostById(postId);
        checkIfEnded = selectedTP.getEnded();
        postType = "transport";
        auctionType = selectedTP.getAuctionType();
        this.checkIfBlind();
        bidsCount = bmsbl.retrieveBidsCount(postId, postType);
        allBids = bmsbl.retrieveAllBids(postId, postType);

        if (!selectedTP.getAccount().getId().equals(accountId)) {
            this.checkIfNotAuthor = true;
        } else {
            this.checkIfNotAuthor = false;
        }

        if (this.checkIfEnded == true) {
            this.checkIfNotEnded = false;
        } else {
            this.checkIfNotEnded = true;
        }

        if ((this.checkIfNotEnded) && (this.checkIfNotAuthor)) {
            this.checkStatus = true;
        } else {
            this.checkStatus = false;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/postManagement/viewOneTransportPost.xhtml");
    }

    private void checkIfBlind() {
        if (auctionType.equals("blindAuction")) {
            this.checkBlind = true;
        } else {
            this.checkBlind = false;
        }
    }

    public void setExtend(Long postId) {
        System.out.println("Set extend: postType is " + postType);
        if (postType.equals("aggregation")) {
            this.selectedAP = pmsbl.retrieveAggregationPostById(postId);
        } else if (postType.equals("storage")) {
            this.selectedSP = pmsbl.retrieveStoragePostById(postId);
        } else if (postType.equals("transport")) {
            this.selectedTP = pmsbl.retrieveTransportPostById(postId);
        }
    }

    public void extendAggregation() {
        System.out.println("Inside extend aggregation post");
        Long postId = selectedAP.getId();
        System.out.println("post Id is " + postId);
        if (selectedAP.getWinnerId() != null) {
            this.errorMsg("You cannot extend aggregation post that already has a winner!");
        } else {
            if (date == null) {
                this.errorMsg("Please enter end time of the post!");
            } else {
                endDateTime = new Timestamp(date.getTime());
                AggregationPost ap = pmsbl.extendAggregationPost(postId, endDateTime);
                if (ap == null) {
                    this.errorMsg("Aggregation post not extended. End time must be after today!");
                } else {
                    this.displayGrowl("Aggregation post successfully extend.");
                }
            }
        }
    }

    public void extendStorage() {
        System.out.println("Inside extend storage post");
        Long postId = selectedSP.getId();
        System.out.println("post Id is " + postId);
        if (selectedSP.getWinnerId() != null) {
            this.errorMsg("You cannot extend aggregation post that already has a winner!");
        } else {
            if (date == null) {
                this.errorMsg("Please enter end time of the post!");
            } else {
                endDateTime = new Timestamp(date.getTime());
                StoragePost sp = pmsbl.extendStoragePost(postId, endDateTime);
                if (sp == null) {
                    this.errorMsg("Aggregation post not extended. End time must be after today!");
                } else {
                    this.displayGrowl("Aggregation post successfully extend.");
                }
            }
        }
    }

    public void extendTransport() {
        System.out.println("Inside extend trasnport post");
        Long postId = selectedTP.getId();
        System.out.println("post Id is " + postId);
        if (selectedTP.getWinnerId() != null) {
            this.errorMsg("You cannot extend aggregation post that already has a winner!");
        } else {
            if (date == null) {
                this.errorMsg("Please enter end time of the post!");
            } else {
                endDateTime = new Timestamp(date.getTime());
                TransportPost tp = pmsbl.extendTransportPost(postId, endDateTime);
                if (tp == null) {
                    this.errorMsg("Aggregation post not extended. End time must be after today!");
                } else {
                    this.displayGrowl("Aggregation post successfully extend.");
                }
            }
        }
    }
    
    public void createAggregationOrder(Long postId) {
        System.out.println("Inside create aggregation order");
        try {
            String winnerType = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            selectedAP = pmsbl.retrieveAggregationPostById(postId);
            String authorName = selectedAP.getAccount().getUsername();
            Account a = amsbl.retrieveAccount(authorName);
            String authorType = a.getAccountType();
            System.out.println("Author type is "+authorType);
            System.out.println("Winner type is "+winnerType);
            if (authorType.equals("User")&&winnerType.equals("User")) {
                ServiceOrder so = pmsbl.createAggregationServiceOrder(postId);
                if (so==null) {
                    this.errorMsg("Service order not created. Please try again");
                } else {
                    this.displayGrowl("Service order successfully created.");
                }
            } else {
                this.errorMsg("Either you or author of the post is not subscribed user. Service order cannot be created. Sorry for the inconvenience caused.");
            }
        } catch (AccountTypeNotExistException ex) {
            Logger.getLogger(PostManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserNotExistException ex) {
            this.errorMsg("Account of author does not exist!");
        }
    }
    
    public void createStorageOrder(Long postId) {
        System.out.println("Inside create storage order");
        try {
            String winnerType = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            selectedSP = pmsbl.retrieveStoragePostById(postId);
            String authorName = selectedSP.getAccount().getUsername();
            Account a = amsbl.retrieveAccount(authorName);
            String authorType = a.getAccountType();
            System.out.println("Author type is "+authorType);
            System.out.println("Winner type is "+winnerType);
            if (authorType.equals("User")&&winnerType.equals("User")) {
                ServiceOrder so = pmsbl.createStorageServiceOrder(postId);
                if (so==null) {
                    this.errorMsg("Service order not created. Please try again");
                } else {
                    this.displayGrowl("Service order successfully created.");
                }
            } else {
                this.errorMsg("Either you or author of the post is not subscribed user. Service order cannot be created. Sorry for the inconvenience caused.");
            }
        } catch (AccountTypeNotExistException ex) {
            Logger.getLogger(PostManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserNotExistException ex) {
            this.errorMsg("Account of author does not exist!");
        }
    }
    
    public void createTransportOrder(Long postId) {
        System.out.println("Inside create transport order");
        try {
            String winnerType = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            selectedTP = pmsbl.retrieveTransportPostById(postId);
            String authorName = selectedTP.getAccount().getUsername();
            Account a = amsbl.retrieveAccount(authorName);
            String authorType = a.getAccountType();
            System.out.println("Author type is "+authorType);
            System.out.println("Winner type is "+winnerType);
            if (authorType.equals("User")&&winnerType.equals("User")) {
                ServiceOrder so = pmsbl.createTransportServiceOrder(postId);
                if (so==null) {
                    this.errorMsg("Service order not created. Please try again");
                } else {
                    this.displayGrowl("Service order successfully created.");
                }
            } else {
                this.errorMsg("Either you or author of the post is not subscribed user. Service order cannot be created. Sorry for the inconvenience caused.");
            }
        } catch (AccountTypeNotExistException ex) {
            Logger.getLogger(PostManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserNotExistException ex) {
            this.errorMsg("Account of author does not exist!");
        }
    }
    
    public int getTimeLeftAP(AggregationPost ap) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (ap.getEndDateTime().getTime() - temp.getTime()) / 1000;
        return left;
    }
    public int getDayLeftAP(AggregationPost ap) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (ap.getEndDateTime().getTime() - temp.getTime()) / (24*60*60*1000);
        return left;
    }
    public int getTimeLeftSP(StoragePost sp) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (sp.getEndDateTime().getTime() - temp.getTime()) / 1000;
        return left;
    }
    public int getDayLeftSP(StoragePost sp) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (sp.getEndDateTime().getTime() - temp.getTime()) / (24*60*60*1000);
        return left;
    }
    public int getTimeLeftTP(TransportPost tp) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (tp.getEndDateTime().getTime() - temp.getTime()) / 1000;
        return left;
    }
    public int getDayLeftTP(TransportPost tp) {
        Date now = new Date();
        Timestamp temp = new Timestamp(now.getTime());
        int left = (int) (tp.getEndDateTime().getTime() - temp.getTime()) / (24*60*60*1000);
        return left;
    }

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(response));
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getInitBid() {
        return initBid;
    }

    public void setInitBid(Double initBid) {
        this.initBid = initBid;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    public boolean isReservePriceSpecified() {
        return reservePriceSpecified;
    }

    public void setReservePriceSpecified(boolean reservePriceSpecified) {
        this.reservePriceSpecified = reservePriceSpecified;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isPerishable() {
        return perishable;
    }

    public void setPerishable(boolean perishable) {
        this.perishable = perishable;
    }

    public boolean isFlammable() {
        return flammable;
    }

    public void setFlammable(boolean flammable) {
        this.flammable = flammable;
    }

    public boolean isPharmaceutical() {
        return pharmaceutical;
    }

    public void setPharmaceutical(boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    public boolean isHighValue() {
        return highValue;
    }

    public void setHighValue(boolean highValue) {
        this.highValue = highValue;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getSourceStreet() {
        return sourceStreet;
    }

    public void setSourceStreet(String sourceStreet) {
        this.sourceStreet = sourceStreet;
    }

    public String getSourceBlockNo() {
        return sourceBlockNo;
    }

    public void setSourceBlockNo(String sourceBlockNo) {
        this.sourceBlockNo = sourceBlockNo;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getSourcePostalCode() {
        return sourcePostalCode;
    }

    public void setSourcePostalCode(String sourcePostalCode) {
        this.sourcePostalCode = sourcePostalCode;
    }

    public String getDestStreet() {
        return destStreet;
    }

    public void setDestStreet(String destStreet) {
        this.destStreet = destStreet;
    }

    public String getDestBlockNo() {
        return destBlockNo;
    }

    public void setDestBlockNo(String destBlockNo) {
        this.destBlockNo = destBlockNo;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public String getDestPostalCode() {
        return destPostalCode;
    }

    public void setDestPostalCode(String destPostalCode) {
        this.destPostalCode = destPostalCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBooleanPrice() {
        return booleanPrice;
    }

    public void setBooleanPrice(String booleanPrice) {
        this.booleanPrice = booleanPrice;
    }

    public Long getStoragePostId() {
        return storagePostId;
    }

    public void setStoragePostId(Long storagePostId) {
        this.storagePostId = storagePostId;
    }

    public Long getTransportPostId() {
        return transportPostId;
    }

    public void setTransportPostId(Long transportPostId) {
        this.transportPostId = transportPostId;
    }

    public Long getAggregationPostId() {
        return aggregationPostId;
    }

    public void setAggregationPostId(Long aggregationPostId) {
        this.aggregationPostId = aggregationPostId;
    }

    public List<StoragePost> getCurrentSPList() {
        return currentSPList;
    }

    public void setCurrentSPList(List<StoragePost> currentSPList) {
        this.currentSPList = currentSPList;
    }

    public List<StoragePost> getHistorySPList() {
        return historySPList;
    }

    public void setHistorySPList(List<StoragePost> historySPList) {
        this.historySPList = historySPList;
    }

    public StoragePost getSelectedSP() {
        return selectedSP;
    }

    public void setSelectedSP(StoragePost selectedSP) {
        this.selectedSP = selectedSP;
    }

    public StoragePost getOnChangeSP() {
        return onChangeSP;
    }

    public void setOnChangeSP(StoragePost onChangeSP) {
        this.onChangeSP = onChangeSP;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<TransportPost> getCurrentTPList() {
        return currentTPList;
    }

    public void setCurrentTPList(List<TransportPost> currentTPList) {
        this.currentTPList = currentTPList;
    }

    public List<TransportPost> getHistoryTPList() {
        return historyTPList;
    }

    public void setHistoryTPList(List<TransportPost> historyTPList) {
        this.historyTPList = historyTPList;
    }

    public TransportPost getSelectedTP() {
        return selectedTP;
    }

    public void setSelectedTP(TransportPost selectedTP) {
        this.selectedTP = selectedTP;
    }

    public TransportPost getOnChangeTP() {
        return onChangeTP;
    }

    public void setOnChangeTP(TransportPost onChangeTP) {
        this.onChangeTP = onChangeTP;
    }

    public List<AggregationPost> getCurrentAPList() {
        return currentAPList;
    }

    public void setCurrentAPList(List<AggregationPost> currentAPList) {
        this.currentAPList = currentAPList;
    }

    public List<AggregationPost> getHistoryAPList() {
        return historyAPList;
    }

    public void setHistoryAPList(List<AggregationPost> historyAPList) {
        this.historyAPList = historyAPList;
    }

    public AggregationPost getSelectedAP() {
        return selectedAP;
    }

    public void setSelectedAP(AggregationPost selectedAP) {
        this.selectedAP = selectedAP;
    }

    public AggregationPost getOnChangeAP() {
        return onChangeAP;
    }

    public void setOnChangeAP(AggregationPost onChangeAP) {
        this.onChangeAP = onChangeAP;
    }

    public List<AggregationPost> getAllAP() {
        return allAP;
    }

    public void setAllAP(List<AggregationPost> allAP) {
        this.allAP = allAP;
    }

    public List<StoragePost> getAllSP() {
        return allSP;
    }

    public void setAllSP(List<StoragePost> allSP) {
        this.allSP = allSP;
    }

    public List<TransportPost> getAllTP() {
        return allTP;
    }

    public void setAllTP(List<TransportPost> allTP) {
        this.allTP = allTP;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isCheckIfEnded() {
        return checkIfEnded;
    }

    public void setCheckIfEnded(boolean checkIfEnded) {
        this.checkIfEnded = checkIfEnded;
    }

    public Double getPlaceBid() {
        return placeBid;
    }

    public void setPlaceBid(Double placeBid) {
        this.placeBid = placeBid;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public boolean isCheckIfNotEnded() {
        return checkIfNotEnded;
    }

    public void setCheckIfNotEnded(boolean checkIfNotEnded) {
        this.checkIfNotEnded = checkIfNotEnded;
    }

    public List<StoragePost> getBiddingSPList() {
        return biddingSPList;
    }

    public void setBiddingSPList(List<StoragePost> biddingSPList) {
        this.biddingSPList = biddingSPList;
    }

    public List<TransportPost> getBiddingTPList() {
        return biddingTPList;
    }

    public void setBiddingTPList(List<TransportPost> biddingTPList) {
        this.biddingTPList = biddingTPList;
    }

    public List<AggregationPost> getBiddingAPList() {
        return biddingAPList;
    }

    public void setBiddingAPList(List<AggregationPost> biddingAPList) {
        this.biddingAPList = biddingAPList;
    }

    public boolean isCheckIfNotAuthor() {
        return checkIfNotAuthor;
    }

    public void setCheckIfNotAuthor(boolean checkIfNotAuthor) {
        this.checkIfNotAuthor = checkIfNotAuthor;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public List<StoragePost> getHistoryBiddingSPList() {
        return historyBiddingSPList;
    }

    public void setHistoryBiddingSPList(List<StoragePost> historyBiddingSPList) {
        this.historyBiddingSPList = historyBiddingSPList;
    }

    public List<TransportPost> getHistoryBiddingTPList() {
        return historyBiddingTPList;
    }

    public void setHistoryBiddingTPList(List<TransportPost> historyBiddingTPList) {
        this.historyBiddingTPList = historyBiddingTPList;
    }

    public List<AggregationPost> getHistoryBiddingAPList() {
        return historyBiddingAPList;
    }

    public void setHistoryBiddingAPList(List<AggregationPost> historyBiddingAPList) {
        this.historyBiddingAPList = historyBiddingAPList;
    }

    public int getBidsCount() {
        return bidsCount;
    }

    public void setBidsCount(int bidsCount) {
        this.bidsCount = bidsCount;
    }

    public List<Bid> getAllBids() {
        return allBids;
    }

    public void setAllBids(List<Bid> allBids) {
        this.allBids = allBids;
    }

    public boolean isCheckBlind() {
        return checkBlind;
    }

    public void setCheckBlind(boolean checkBlind) {
        this.checkBlind = checkBlind;
    }

    public boolean isCheckFirstBlind() {
        return checkFirstBlind;
    }

    public void setCheckFirstBlind(boolean checkFirstBlind) {
        this.checkFirstBlind = checkFirstBlind;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public List<String> getWarehouseNameList() {
        return warehouseNameList;
    }

    public void setWarehouseNameList(List<String> warehouseNameList) {
        this.warehouseNameList = warehouseNameList;
    }

}
