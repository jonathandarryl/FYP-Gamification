/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.session;

import CRM.entity.ServiceOrder;
import GRNS.entity.*;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Timer;
import util.exception.PostNotExistException;

/**
 *
 * @author chongyangsun
 */
@Local
public interface PostManagementSessionBeanLocal {
    public void restartAllTimers();
    public AggregationPost createAggregationPost(Long accountId, Long companyId, String title, String content, Double initBid, 
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double quantity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime);
    public StoragePost createStoragePost(Long accountId, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Long warehouseId, Double capacity, 
            String street, String blockNo, String city, String state, String country, String postalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime);
    public TransportPost createTransportPost(Long accountId, Long companyId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double capacity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime);
    public Boolean updateAggregationPost(Long postId, String title, String content, Double initBid, 
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double quantity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime) throws PostNotExistException;
    public Boolean updateStoragePost(Long postId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Long warehouseId, Double capacity, 
            String street, String blockNo, String city, String state, String country, String postalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) throws PostNotExistException;
    public Boolean updateTransportPost(Long postId, String title, String content, Double initBid, Timestamp endDateTime, 
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double capacity, 
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode, 
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode, 
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) throws PostNotExistException;
    public List<AggregationPost> retrieveAggregationPost(Long posterId);
    public List<StoragePost> retrieveStoragePost(Long posterId);
    public List<TransportPost> retrieveTransportPost(Long posterId);
    public List<AggregationPost> retrieveAggregationPostByCompany(Long companyId);
    public List<StoragePost> retrieveStoragePostByCompany(Long companyId);
    public List<TransportPost> retrieveTransportPostByCompany(Long companyId);
    public List<AggregationPost> retrieveAllAggregationPost(String flag);// all, english, vickrey, blind
    public List<StoragePost> retrieveAllStoragePost(String flag);
    public List<TransportPost> retrieveAllTransportPost(String flag);
    public void setPostAsEnded(Timer timer) throws PostNotExistException;
    public AggregationPost retrieveAggregationPostById(Long postId);
    public StoragePost retrieveStoragePostById(Long postId);
    public TransportPost retrieveTransportPostById(Long postId);
    public AggregationPost retrieveAggregationPostByElement(Long posterId, Timestamp startDateTime);
    public StoragePost retrieveStoragePostByElement(Long posterId, Timestamp startDateTime);
    public TransportPost retrieveTransportPostByElement(Long posterId, Timestamp startDateTime);
    public List<AggregationPost> retrieveBidAggregationPost(Long bidderId);
    public List<StoragePost> retrieveBidStoragePost(Long bidderId);
    public List<TransportPost> retrieveBidTransportPost(Long bidderId);
    public List<AggregationPost> filterAggergationPostByPrice(List<AggregationPost> apList, Double lowPrice, Double highPrice);
    public List<StoragePost> filterStoragePostByPrice(List<StoragePost> spList, Double lowPrice, Double highPrice);
    public List<TransportPost> filterTransportPostByPrice(List<TransportPost> tpList, Double lowPrice, Double highPrice);
    public List<AggregationPost> filterAggregationPostByTime(List<AggregationPost> apList, String flag); //new, closing
    public List<StoragePost> filterStoragePostByTime(List<StoragePost> spList, String flag);
    public List<TransportPost> filterTransportPostByTime(List<TransportPost> tpList, String flag);
    public List<AggregationPost> searchAggregationPost(String criteria);
    public List<StoragePost> searchStoragePost(String criteria);
    public List<TransportPost> searchTransportPost(String criteria);
    public List<AggregationPost> retrieveWinningAggregationPost(Long accountId);
    public List<StoragePost> retrieveWinningStoragePost(Long accountId);
    public List<TransportPost> retrieveWinningTransportPost(Long accountId);
    public AggregationPost extendAggregationPost(Long postId, Timestamp endDateTime);
    public StoragePost extendStoragePost(Long postId, Timestamp endDateTime);
    public TransportPost extendTransportPost(Long postId, Timestamp endDateTime);
    // Integration with CRM, WMS, TMS
    public ServiceOrder createAggregationServiceOrder(Long postId);
    public ServiceOrder createStorageServiceOrder(Long postId);
    public ServiceOrder createTransportServiceOrder(Long postId);
    public Boolean fulfillAggregationPost(Long postId);
    public Boolean fulfillStoragePost(Long postId);
    public Boolean fulfillTransportPost(Long postId);
}
