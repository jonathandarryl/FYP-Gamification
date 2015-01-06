/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.Account;
import GRNS.entity.AggregationPost;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import GRNS.session.BiddingManagementSessionBeanLocal;
import GRNS.session.PostManagementSessionBeanLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.sql.Timestamp;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountTypeNotExistException;
import util.exception.BidInvalidException;
import util.exception.UserNotExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author sunmingjia
 */
@Stateless
@LocalBean
public class PopulateGRNSSessionBean {

    @PersistenceContext
    EntityManager em;
    
    @EJB
    PostManagementSessionBeanLocal pmsbl;
    @EJB
    BiddingManagementSessionBeanLocal bmsbl;
    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    FacilityManagementLocal fmsbl;
    
    public Boolean isAggregationPostEmpty() {
        Query query = em.createQuery("SELECT a FROM AggregationPost a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public Boolean isStoragePostEmpty() {
        Query query = em.createQuery("SELECT a FROM StoragePost a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public Boolean isTransportPostEmpty() {
        Query query = em.createQuery("SELECT a FROM TransportPost a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public Boolean isBiddingEmpty() {
        Query query = em.createQuery("SELECT a FROM Bid a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public void populateAggregationPost() {
        try {
            System.out.println("Populating GRNS...");
            System.out.println("Populating AggregationPost...");
            this.populateAggregationPostList("MLMarketing", "merlion", "Apple and Pear to ship from Singapore to New York", 
                    "Apple and Pear to ship from Singapore to New York", 5000.0, Boolean.FALSE, null, "english", 1000.0, 
                    "1A Kent Ridge Road", null, "Singapore", "Singapore", "Singapore", "119224", 
                    "350 5th Ave", null, "New York", "NY", "United States", "010118", 
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateAggregationPostList("MLMarketing", "merlion", "Books to ship from Singapore to New York", 
                    "Books to ship from Singapore to New York", 1000.0, Boolean.FALSE, null, "english", 1000.0, 
                    "1A Kent Ridge Road", null, "Singapore", "Singapore", "Singapore", "119224", 
                    "350 5th Ave", null, "New York", "NY", "United States", "010118", 
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateAggregationPostList("MLMarketing", "merlion", "Furniture to ship from Singapore to Beijing", 
                    "Furniture to ship from Singapore to Beijing", 10000.0, Boolean.TRUE, 9000.0, "vickrey", 5000.0, 
                    "15 Computing Drive", null, "Singapore", "Singapore", "Singapore", "117417", 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            this.populateAggregationPostList("MLMarketing", "merlion", "Furniture Digital Chips to ship from Shenzhen to Beijing", 
                    "Furniture Digital Chips to ship from Shenzhen to Beijing", 20000.0, Boolean.TRUE, 17000.0, "vickrey", 10000.0, 
                    "2 Donghuan Road", "10th Yousong Industrial District, Longhua, Baoan", "Shenzhen", "Guangdong", "China", "518109", 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            System.out.println("Finish populating AggregationPost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populateAggregationPostList(String username, String companyName, String title, String content, Double initBid, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double quantity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) throws AccountTypeNotExistException, UserNotExistException {
        Account a = amsbl.retrieveAccount(username);
        Long accountId = a.getId();
        Long companyId = a.getCompany().getId();
        Date current = new Date();
        long sevenDays = 7 * 24 * 60 * 60 * 1000;
        long oneMinute = 1 * 60 * 1000;
        Timestamp endTime;
        if(title.startsWith("Furniture")) {
            endTime = new Timestamp(current.getTime()+sevenDays);
        } else {
            endTime = new Timestamp(current.getTime()+oneMinute);
        }
        Timestamp requestStartTime = new Timestamp(current.getTime()+sevenDays);
        Timestamp requestEndTime = new Timestamp(requestStartTime.getTime()+sevenDays);
        pmsbl.createAggregationPost(accountId, companyId, title, content, initBid, endTime, 
                reservePriceSpecified, reservePrice, auctionType, quantity, 
                sourceStreet, sourceBlockNo, sourceCity, sourceState, sourceCountry, sourcePostalCode, 
                destStreet, destBlockNo, destCity, destState, destCountry, destPostalCode, 
                perishable, flammable, pharmaceutical, highValue, requestStartTime, requestEndTime);
    }
    
    public void populateStoragePost() {
        try {
            System.out.println("Populating StoragePost...");
            this.populateStoragePostList("MLMarketing", "Warehouse available in Singapore", 
                    "Warehouse available in Singapore", 500.0, Boolean.FALSE, null, "english", "Warehouse Singapore", 1000.0, 
                    "1A Kent Ridge Road", null, "Singapore", "Singapore", "Singapore", "119224", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateStoragePostList("MLMarketing", "Warehouse in West Singapore", 
                    "Warehouse available in West Singapore", 500.0, Boolean.TRUE, 800.0, "english", "Warehouse Singapore", 1000.0, 
                    "22 Prince George's Park", null, "Singapore", "Singapore", "Singapore", "118420", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateStoragePostList("MLMarketing", "Furniture Warehouse available in Beijing", 
                    "Furniture Warehouse Capacity available in Beijing", 8000.0, Boolean.TRUE, 9000.0, "vickrey", "Warehouse London", 5000.0, 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            this.populateStoragePostList("MLMarketing", "Warehouse Capacity available in Beijing", 
                    "Warehouse Capacity available in Beijing", 6000.0, Boolean.TRUE, 7000.0, "blind", "Warehouse London", 5000.0, 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            System.out.println("Finish populating StoragePost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populateStoragePostList(String username, String title, String content, Double initBid, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, String warehouseName, Double capacity, 
            String street, String blockNo, String city, String state, String country, String postalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) throws AccountTypeNotExistException, UserNotExistException, WarehouseNotExistException {
        Account a = amsbl.retrieveAccount(username);
        Warehouse warehouse = fmsbl.retrieveWarehouse(warehouseName, "merlion");
        Long accountId = a.getId();
        Long companyId = a.getCompany().getId();
        Date current = new Date();
        long sevenDays = 7 * 24 * 60 * 60 * 1000;
        long oneMinute = 1 * 60 * 1000;
        Timestamp endTime;
        if(title.endsWith("Beijing")) {
            endTime = new Timestamp(current.getTime()+sevenDays);
        } else {
            endTime = new Timestamp(current.getTime()+oneMinute);
        }
        Timestamp requestStartTime = new Timestamp(current.getTime()+sevenDays);
        Timestamp requestEndTime = new Timestamp(requestStartTime.getTime()+sevenDays);
        StoragePost sp = pmsbl.createStoragePost(accountId, companyId, title, content, initBid, endTime, reservePriceSpecified, reservePrice, 
                auctionType, warehouse.getId(), capacity, warehouse.getLocation().getStreet(), warehouse.getLocation().getBlockNo(), 
                warehouse.getLocation().getCity(), warehouse.getLocation().getState(), warehouse.getLocation().getCountry(), warehouse.getLocation().getPostalCode(), 
                perishable, flammable, pharmaceutical, highValue, requestStartTime, requestEndTime);
        sp.setWarehouseName(warehouseName);
        em.merge(sp);
    }
    
    public void populateTransportPost() {
        try {
            System.out.println("Populating TransportPost...");
            this.populateTransportPostList("MLMarketing", "Shipping from Singapore to New York", 
                    "Apple and Pear to ship from Singapore to New York", 5000.0, Boolean.FALSE, null, "english", 1000.0, 
                    "1A Kent Ridge Road", null, "Singapore", "Singapore", "Singapore", "119224", 
                    "350 5th Ave", null, "New York", "NY", "United States", "010118", 
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateTransportPostList("MLMarketing", "Shipping from Singapore to US", 
                    "Books to ship from Singapore to New York", 1000.0, Boolean.FALSE, null, "vickrey", 1000.0, 
                    "1A Kent Ridge Road", null, "Singapore", "Singapore", "Singapore", "119224", 
                    "350 5th Ave", null, "New York", "NY", "United States", "010118", 
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            this.populateTransportPostList("MLMarketing", "Shipping from Singapore to Beijing", 
                    "Furniture to ship from Singapore to Beijing", 10000.0, Boolean.TRUE, 12000.0, "english", 5000.0, 
                    "15 Computing Drive", null, "Singapore", "Singapore", "Singapore", "117417", 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            this.populateTransportPostList("MLMarketing", "Shipping Capacity Available from Shenzhen to Beijing", 
                    "Furniture Digital Chips to ship from Shenzhen to Beijing", 17000.0, Boolean.TRUE, 18000.0, "vickrey", 10000.0, 
                    "2 Donghuan Road", "10th Yousong Industrial District, Longhua, Baoan", "Shenzhen", "Guangdong", "China", "518109", 
                    "255 Wangfujing St", null, "Beijing", "Beijing", "China", "100006", 
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            System.out.println("Finish populating TransportPost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populateTransportPostList(String username, String title, String content, Double initBid, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double capacity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue) throws AccountTypeNotExistException, UserNotExistException {
        Account a = amsbl.retrieveAccount(username);
        Long accountId = a.getId();
        Long companyId = a.getCompany().getId();
        Date current = new Date();
        long sevenDays = 7 * 24 * 60 * 60 * 1000;
        long oneMinute = 1 * 60 * 1000;
        Timestamp endTime;
        if(title.endsWith("Beijing")) {
            endTime = new Timestamp(current.getTime()+sevenDays);
        } else {
            endTime = new Timestamp(current.getTime()+oneMinute);
        }
        Timestamp requestStartTime = new Timestamp(current.getTime()+sevenDays);
        Timestamp requestEndTime = new Timestamp(requestStartTime.getTime()+sevenDays);
        pmsbl.createTransportPost(accountId, companyId, title, content, initBid, endTime, reservePriceSpecified, reservePrice, 
                auctionType, capacity, sourceStreet, sourceBlockNo, sourceCity, sourceState, sourceCountry, sourcePostalCode, 
                destStreet, destBlockNo, destCity, destState, destCountry, destPostalCode, 
                perishable, flammable, pharmaceutical, highValue, requestStartTime, requestEndTime);
    }
    
    public void populateBidding() {
        try {
            System.out.println("Populating Bids...");
            this.populateBiddingList("YDMarketing", 4500.0, "Apple and Pear to ship from Singapore to New York", "aggregation");
            this.populateBiddingList("DHLMarketing", 4000.0, "Apple and Pear to ship from Singapore to New York", "aggregation");
            this.populateBiddingList("YDMarketing", 3900.0, "Apple and Pear to ship from Singapore to New York", "aggregation");
            
            this.populateBiddingList("YDMarketing", 900.0, "Books to ship from Singapore to New York", "aggregation");
            this.populateBiddingList("DHLMarketing", 850.0, "Books to ship from Singapore to New York", "aggregation");
            
            this.populateBiddingList("YDMarketing", 9500.0, "Furniture to ship from Singapore to Beijing", "aggregation");
            this.populateBiddingList("DHLMarketing", 8500.0, "Furniture to ship from Singapore to Beijing", "aggregation");
            this.populateBiddingList("manager21", 8300.0, "Furniture to ship from Singapore to Beijing", "aggregation");
            this.populateBiddingList("YDMarketing", 8200.0, "Furniture to ship from Singapore to Beijing", "aggregation");
            
            this.populateBiddingList("YDMarketing", 18000.0, "Furniture Digital Chips to ship from Shenzhen to Beijing", "aggregation");
            this.populateBiddingList("DHLMarketing", 17000.0, "Furniture Digital Chips to ship from Shenzhen to Beijing", "aggregation");
            this.populateBiddingList("manager21", 16000.0, "Furniture Digital Chips to ship from Shenzhen to Beijing", "aggregation");
            this.populateBiddingList("YDMarketing", 15800.0, "Furniture Digital Chips to ship from Shenzhen to Beijing", "aggregation");
            
            
            this.populateBiddingList("manager11", 600.0, "Warehouse available in Singapore", "storage");
            this.populateBiddingList("manager21", 650.0, "Warehouse available in Singapore", "storage");
            this.populateBiddingList("SZMarketing", 700.0, "Warehouse available in Singapore", "storage");
            
            this.populateBiddingList("YDMarketing", 700.0, "Warehouse in West Singapore", "storage");
            this.populateBiddingList("DHLMarketing", 850.0, "Warehouse in West Singapore", "storage");
            
            this.populateBiddingList("YDMarketing", 8500.0, "Furniture Warehouse available in Beijing", "storage");
            this.populateBiddingList("DHLMarketing", 8600.0, "Furniture Warehouse available in Beijing", "storage");
            this.populateBiddingList("manager21", 9100.0, "Furniture Warehouse available in Beijing", "storage");
            this.populateBiddingList("YDMarketing", 9800.0, "Furniture Warehouse available in Beijing", "storage");
            
            this.populateBiddingList("YDMarketing", 8000.0, "Warehouse Capacity available in Beijing", "storage");
            this.populateBiddingList("DHLMarketing", 10000.0, "Warehouse Capacity available in Beijing", "storage");
            
            
            this.populateBiddingList("manager11", 5200.0, "Shipping from Singapore to New York", "transport");
            this.populateBiddingList("manager21", 5250.0, "Shipping from Singapore to New York", "transport");
            this.populateBiddingList("SZMarketing", 5700.0, "Shipping from Singapore to New York", "transport");
            
            this.populateBiddingList("YDMarketing", 1100.0, "Shipping from Singapore to US", "transport");
            this.populateBiddingList("DHLMarketing", 1400.0, "Shipping from Singapore to US", "transport");
            
            this.populateBiddingList("YDMarketing", 10100.0, "Shipping from Singapore to Beijing", "transport");
            this.populateBiddingList("DHLMarketing", 12000.0, "Shipping from Singapore to Beijing", "transport");
            this.populateBiddingList("manager21", 12200.0, "Shipping from Singapore to Beijing", "transport");
            this.populateBiddingList("YDMarketing", 13000.0, "Shipping from Singapore to Beijing", "transport");
            
            this.populateBiddingList("YDMarketing", 18100.0, "Shipping Capacity Available from Shenzhen to Beijing", "transport");
            this.populateBiddingList("DHLMarketing", 20000.0, "Shipping Capacity Available from Shenzhen to Beijing", "transport");
            
            System.out.println("Finish populating Bids");
            System.out.println("Finish GRNS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populateBiddingList(String bidder, Double bid, String post, String postType) throws UserNotExistException, AccountTypeNotExistException, BidInvalidException {
        Account a = amsbl.retrieveAccount(bidder);
        Long accountId = a.getId();
        Long companyId = a.getCompany().getId();
        if(postType.equals("aggregation")) {
            AggregationPost ap = pmsbl.searchAggregationPost(post).get(0);
            System.out.println(ap.getTitle());
            Long postId = ap.getId();
            if(ap.getAuctionType().equals("blind")) {
                bmsbl.createBlindBid(accountId, postId, postType, bid);
            } else {
                bmsbl.createNewBid(accountId, postId, postType, bid);
            }
        } else if (postType.equals("storage")) {
            StoragePost sp = pmsbl.searchStoragePost(post).get(0);
            System.out.println(sp.getTitle());
            Long postId = sp.getId();
            if(sp.getAuctionType().equals("blind")) {
                bmsbl.createBlindBid(accountId, postId, postType, bid);
            } else {
                bmsbl.createNewBid(accountId, postId, postType, bid);
            }
        } else {
            TransportPost tp = pmsbl.searchTransportPost(post).get(0);
            System.out.println(tp.getTitle());
            Long postId = tp.getId();
            if(tp.getAuctionType().equals("blind")) {
                bmsbl.createBlindBid(accountId, postId, postType, bid);
            } else {
                bmsbl.createNewBid(accountId, postId, postType, bid);
            }
        }
    }
}
