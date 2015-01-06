/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceContractManagementSessionLocal;
import CRM.session.ServiceOrderManagementLocal;
import CRM.session.ServiceQuotationManagementSessionLocal;
import Common.entity.Account;
import Common.entity.Company;
import Common.entity.CompanyUserAccount;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import Common.session.MessageSessionBeanLocal;
import GRNS.entity.AggregatedOrder;
import GRNS.entity.AggregationPost;
import GRNS.entity.Bid;
import GRNS.entity.ExternalUserAccount;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.PostNotExistException;
import util.exception.ProductNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceQuotationAlreadyEstablishedContractException;
import util.exception.ServiceQuotationNotApprovedException;
import util.exception.ServiceQuotationNotExistException;
import util.session.SendEmail;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class PostManagementSessionBean implements PostManagementSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @EJB
    private BiddingManagementSessionBeanLocal bmsbl;

    @EJB
    private ProductInfoManagementLocal pimsbl;

    @EJB
    private MessageSessionBeanLocal msbl;

    @EJB
    private ServiceQuotationManagementSessionLocal sqmsbl;

    @EJB
    private ServiceContractManagementSessionLocal scmsbl;

    @EJB
    private ServiceOrderManagementLocal somsbl;

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;

    @EJB
    private FacilityManagementLocal fmsbl;

    private SendEmail sendEmail;

    @Override
    public void restartAllTimers() {
        Query query1 = em.createQuery("SELECT a FROM AggregationPost a WHERE a.deleted='false' AND a.ended='false'");
        List<AggregationPost> aggregationPostList = new ArrayList(query1.getResultList());
        Query query2 = em.createQuery("SELECT a FROM StoragePost a WHERE a.deleted='false' AND a.ended='false'");
        List<StoragePost> storagePostList = new ArrayList(query2.getResultList());
        Query query3 = em.createQuery("SELECT a FROM TransportPost a WHERE a.deleted='false' AND a.ended='false'");
        List<TransportPost> transportPostList = new ArrayList(query3.getResultList());

        TimerService timerService = ctx.getTimerService();
        Collection timers = timerService.getTimers();
        Date now = new Date();
        for (AggregationPost ap : aggregationPostList) {
            boolean flag = true;
            String info = "a" + ap.getId();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    flag = false;
                }
            }
            if (flag) {
                Date end = new Date(ap.getEndDateTime().getTime());
                Timer apTimer = timerService.createTimer(end, info);

            }
        }
        for (StoragePost sp : storagePostList) {
            boolean flag = true;
            String info = "s" + sp.getId();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    flag = false;
                }
            }
            if (flag) {
                Date end = new Date(sp.getEndDateTime().getTime());
                Timer spTimer = timerService.createTimer(end, info);

            }
        }
        for (TransportPost tp : transportPostList) {
            boolean flag = true;
            String info = "t" + tp.getId();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    flag = false;
                }
            }
            if (flag) {
                Date end = new Date(tp.getEndDateTime().getTime());
                Timer tpTimer = timerService.createTimer(end, info);

            }
        }
    }

    @Override
    public AggregationPost createAggregationPost(Long accountId, Long companyId, String title, String content, Double initBid,
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double quantity,
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode,
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime) {

        Account account = em.find(Account.class, accountId);
        Location sourceLoc = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
        Location destLoc = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
        if (!reservePriceSpecified) {
            reservePrice = 999999999999.9;// infinite high value
        }

        Boolean v = Boolean.FALSE;
        Boolean b = Boolean.FALSE;
        Boolean e = Boolean.TRUE;
        if (auctionType.equals("vickreyAuction")) {
            v = Boolean.TRUE;
            b = Boolean.FALSE;
            e = Boolean.FALSE;
        } else if (auctionType.equals("blindAuction")) {
            v = Boolean.FALSE;
            b = Boolean.TRUE;
            e = Boolean.FALSE;
        }
        AggregationPost aggregationPost = new AggregationPost(account, companyId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, v, b, e,
                sourceLoc, destLoc, quantity, perishable, flammable, pharmaceutical, highValue, requestStartTime, requestEndTime);
        aggregationPost.setLowestBid(initBid);

        em.persist(aggregationPost);
        em.flush();

        Date end = new Date(endDateTime.getTime());
        TimerService timerService = ctx.getTimerService();
        String info = "a" + aggregationPost.getId();
        Timer apTimer = timerService.createTimer(end, info);

        TimerHandle timerHandle = apTimer.getHandle();
        aggregationPost.setTimerHandle(timerHandle);

        em.merge(aggregationPost);

        account.getAggregationPosts().add(aggregationPost);
        em.merge(account);

        return aggregationPost;

    }

    @Override
    public StoragePost createStoragePost(Long accountId, Long companyId, String title, String content, Double initBid, Timestamp endDateTime,
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Long warehouseId, Double capacity,
            String street, String blockNo, String city, String state, String country, String postalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) {

        Account account = em.find(Account.class, accountId);
        Location location = new Location(country, state, city, street, blockNo, postalCode);
        if (!reservePriceSpecified) {
            reservePrice = 0.0;
        }
        Boolean v = Boolean.FALSE;
        Boolean b = Boolean.FALSE;
        Boolean e = Boolean.TRUE;
        if (auctionType.equals("vickreyAuction")) {
            v = Boolean.TRUE;
            b = Boolean.FALSE;
            e = Boolean.FALSE;
        } else if (auctionType.equals("blindAuction")) {
            v = Boolean.FALSE;
            b = Boolean.TRUE;
            e = Boolean.FALSE;
        }

        StoragePost storagePost = new StoragePost(account, companyId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, v, b,
                e, warehouseId, location, capacity, perishable, flammable, pharmaceutical, highValue, serviceStartTime, serviceEndTime);
        storagePost.setHighestBid(initBid);

        em.persist(storagePost);
        em.flush();

        Date end = new Date(endDateTime.getTime());
        TimerService timerService = ctx.getTimerService();
        String info = "s" + storagePost.getId();
        Timer spTimer = timerService.createTimer(end, info);
        TimerHandle timerHandl = spTimer.getHandle();
        storagePost.setTimerHandle(timerHandl);

        em.merge(storagePost);

        account.getStoragePosts().add(storagePost);
        em.merge(account);

        return storagePost;
    }

    @Override
    public TransportPost createTransportPost(Long accountId, Long companyId, String title, String content, Double initBid, Timestamp endDateTime,
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double capacity,
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode,
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) {

        System.out.println("Inside ejb create transport post");
        System.out.println("Auction type is " + auctionType);
        Account account = em.find(Account.class, accountId);
        Location sourceLoc = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
        Location destLoc = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
        if (!reservePriceSpecified) {
            reservePrice = 0.0;
        }
        Boolean v = Boolean.FALSE;
        Boolean b = Boolean.FALSE;
        Boolean e = Boolean.TRUE;
        if (auctionType.equals("vickreyAuction")) {
            v = Boolean.TRUE;
            b = Boolean.FALSE;
            e = Boolean.FALSE;
        } else if (auctionType.equals("blindAuction")) {
            v = Boolean.FALSE;
            b = Boolean.TRUE;
            e = Boolean.FALSE;
        }

        TransportPost transportPost = new TransportPost(account, companyId, title, content, initBid, endDateTime,
                reservePriceSpecified, reservePrice, v, b,
                e, sourceLoc, destLoc, capacity, perishable, flammable, pharmaceutical, highValue, serviceStartTime, serviceEndTime);
        transportPost.setHighestBid(initBid);

        em.persist(transportPost);
        em.flush();

        Date end = new Date(endDateTime.getTime());
        TimerService timerService = ctx.getTimerService();
        String info = "t" + transportPost.getId();
        Timer tpTimer = timerService.createTimer(end, info);
        TimerHandle timerHandle = tpTimer.getHandle();
        transportPost.setTimerHandle(timerHandle);

        em.merge(transportPost);

        account.getTransportPosts().add(transportPost);
        em.merge(account);

        System.out.println("Inside ejb");
        System.out.println("Auction type is " + transportPost.getAuctionType());
        return transportPost;
    }

    @Override
    public Boolean updateAggregationPost(Long postId, String title, String content, Double initBid,
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double quantity,
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode,
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp requestStartTime, Timestamp requestEndTime) throws PostNotExistException {

        AggregationPost ap = em.find(AggregationPost.class, postId);
        if (ap == null) {
            throw new PostNotExistException("aggregation post " + postId + " does not exist!");
        }
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType='aggregation' AND b.postId= :id");
        query.setParameter("id", postId);
        List<Bid> bidList = new ArrayList(query.getResultList());
        if (!bidList.isEmpty()) {
            return false;
        }

        if (!ap.getReservePriceSpecified().equals(reservePriceSpecified)) {
            ap.setReservePriceSpecified(reservePriceSpecified);
        }
        if (!ap.getReservePrice().equals(reservePrice)) {
            ap.setReservePrice(reservePrice);
        }
        if (auctionType.equals("vickreyAuction")) {
            ap.setVickreyAuctionSpecified(Boolean.TRUE);
            ap.setBlindAuctionSpecified(Boolean.FALSE);
            ap.setEnglishAuctionSpecified(Boolean.FALSE);
        } else if (auctionType.equals("blindAuction")) {
            ap.setVickreyAuctionSpecified(Boolean.FALSE);
            ap.setBlindAuctionSpecified(Boolean.TRUE);
            ap.setEnglishAuctionSpecified(Boolean.FALSE);
        } else {
            ap.setVickreyAuctionSpecified(Boolean.FALSE);
            ap.setBlindAuctionSpecified(Boolean.FALSE);
            ap.setEnglishAuctionSpecified(Boolean.TRUE);
        }
        if (!ap.getTitle().equals(title)) {
            ap.setTitle(title);
        }
        if (!ap.getContent().equals(content)) {
            ap.setContent(content);
        }
        if (ap.getLowestBid() == 0.0 && !ap.getInitBid().equals(initBid)) {
            ap.setInitBid(initBid);
        }
        if (!ap.getQuantity().equals(quantity)) {
            ap.setQuantity(quantity);
        }
        if (!ap.getEndDateTime().equals(endDateTime)) {
            ap.setEndDateTime(endDateTime);
            String info = "a" + postId;
            TimerService timerService = ctx.getTimerService();
            Collection timers = timerService.getTimers();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    timer.cancel();
                }
            }
            Date end = new Date(endDateTime.getTime());
            Timer newTimer = timerService.createTimer(end, info);
            TimerHandle timerHandle = newTimer.getHandle();
            ap.setTimerHandle(timerHandle);
        }
        if (!ap.getSourceLoc().getStreet().equals(sourceStreet) || !ap.getSourceLoc().getBlockNo().equals(sourceBlockNo)
                || !ap.getSourceLoc().getCity().equals(sourceCity) || !ap.getSourceLoc().getState().equals(sourceState)
                || !ap.getSourceLoc().getCountry().equals(sourceCountry) || !ap.getSourceLoc().getPostalCode().equals(sourcePostalCode)) {
            Location sourceLoc = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
            ap.setSourceLoc(sourceLoc);
        }
        if (!ap.getDestLoc().getStreet().equals(destStreet) || !ap.getDestLoc().getBlockNo().equals(destBlockNo)
                || !ap.getDestLoc().getCity().equals(destCity) || !ap.getDestLoc().getState().equals(destState)
                || !ap.getDestLoc().getCountry().equals(destCountry) || !ap.getDestLoc().getPostalCode().equals(destPostalCode)) {
            Location destLoc = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
            ap.setDestLoc(destLoc);
        }
        if (!ap.getPerishable().equals(perishable)) {
            ap.setPerishable(perishable);
        }
        if (!ap.getFlammable().equals(flammable)) {
            ap.setFlammable(flammable);
        }
        if (!ap.getPharmaceutical().equals(pharmaceutical)) {
            ap.setPharmaceutical(pharmaceutical);
        }
        if (!ap.getHighValue().equals(highValue)) {
            ap.setHighValue(highValue);
        }
        if (!ap.getRequestStartTime().equals(requestStartTime)) {
            ap.setRequestStartTime(requestStartTime);
        }
        if (!ap.getRequestEndTime().equals(requestEndTime)) {
            ap.setRequestEndTime(requestEndTime);
        }

        em.merge(ap);

        return true;
    }

    @Override
    public Boolean updateStoragePost(Long postId, String title, String content, Double initBid, Timestamp endDateTime,
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Long warehouseId, Double capacity,
            String street, String blockNo, String city, String state, String country, String postalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) throws PostNotExistException {

        StoragePost sp = em.find(StoragePost.class, postId);
        if (sp == null) {
            throw new PostNotExistException("storage post " + postId + " does not exist!");
        }
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType='storage' AND b.postId= :id");
        query.setParameter("id", postId);
        List<Bid> bidList = new ArrayList(query.getResultList());
        if (!bidList.isEmpty()) {
            return false;
        }

        if (!sp.getReservePriceSpecified().equals(reservePriceSpecified)) {
            sp.setReservePriceSpecified(reservePriceSpecified);
        }
        if (!sp.getReservePrice().equals(reservePrice)) {
            sp.setReservePrice(reservePrice);
        }
        if (auctionType.equals("vickreyAuction")) {
            sp.setVickreyAuctionSpecified(Boolean.TRUE);
            sp.setBlindAuctionSpecified(Boolean.FALSE);
            sp.setEnglishAuctionSpecified(Boolean.FALSE);
        } else if (auctionType.equals("blindAuction")) {
            sp.setVickreyAuctionSpecified(Boolean.FALSE);
            sp.setBlindAuctionSpecified(Boolean.TRUE);
            sp.setEnglishAuctionSpecified(Boolean.FALSE);
        } else {
            sp.setVickreyAuctionSpecified(Boolean.FALSE);
            sp.setBlindAuctionSpecified(Boolean.FALSE);
            sp.setEnglishAuctionSpecified(Boolean.TRUE);
        }
        if (!sp.getTitle().equals(title)) {
            sp.setTitle(title);
        }
        if (!sp.getContent().equals(content)) {
            sp.setContent(content);
        }
        if (sp.getHighestBid() == 0.0 && !sp.getInitBid().equals(initBid)) {
            sp.setInitBid(initBid);
        }
        if (!sp.getEndDateTime().equals(endDateTime)) {
            sp.setEndDateTime(endDateTime);
            String info = "s" + postId;
            TimerService timerService = ctx.getTimerService();
            Collection timers = timerService.getTimers();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    timer.cancel();
                }
            }
            Date end = new Date(endDateTime.getTime());
            Timer newTimer = timerService.createTimer(end, info);
            TimerHandle timerHandle = newTimer.getHandle();
            sp.setTimerHandle(timerHandle);
        }
        if (!sp.getCapacity().equals(capacity)) {
            sp.setCapacity(capacity);
        }
        if (!sp.getWarehouseId().equals(warehouseId)) {
            sp.setWarehouseId(warehouseId);
        }
        if (!sp.getPerishable().equals(perishable)) {
            sp.setPerishable(perishable);
        }
        if (!sp.getFlammable().equals(flammable)) {
            sp.setFlammable(flammable);
        }
        if (!sp.getPharmaceutical().equals(pharmaceutical)) {
            sp.setPharmaceutical(pharmaceutical);
        }
        if (!sp.getHighValue().equals(highValue)) {
            sp.setHighValue(highValue);
        }
        if (!sp.getLocation().getStreet().equals(street) || !sp.getLocation().getBlockNo().equals(blockNo)
                || !sp.getLocation().getCity().equals(city) || !sp.getLocation().getState().equals(state)
                || !sp.getLocation().getCountry().equals(country) || !sp.getLocation().getPostalCode().equals(postalCode)) {
            Location location = new Location(country, state, city, street, blockNo, postalCode);
            sp.setLocation(location);
        }
        if (!sp.getServiceStartTime().equals(serviceStartTime)) {
            sp.setServiceStartTime(serviceStartTime);
        }
        if (!sp.getServiceEndTime().equals(serviceEndTime)) {
            sp.setServiceEndTime(serviceEndTime);
        }

        em.merge(sp);

        return true;
    }

    @Override
    public Boolean updateTransportPost(Long postId, String title, String content, Double initBid, Timestamp endDateTime,
            Boolean reservePriceSpecified, Double reservePrice, String auctionType, Double capacity,
            String sourceStreet, String sourceBlockNo, String sourceCity, String sourceState, String sourceCountry, String sourcePostalCode,
            String destStreet, String destBlockNo, String destCity, String destState, String destCountry, String destPostalCode,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Timestamp serviceStartTime, Timestamp serviceEndTime) throws PostNotExistException {

        TransportPost tp = em.find(TransportPost.class, postId);
        if (tp == null) {
            throw new PostNotExistException("transport post " + postId + " does not exist!");
        }
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType='transport' AND b.postId= :id");
        query.setParameter("id", postId);
        List<Bid> bidList = new ArrayList(query.getResultList());
        if (!bidList.isEmpty()) {
            return false;
        }

        if (!tp.getReservePriceSpecified().equals(reservePriceSpecified)) {
            tp.setReservePriceSpecified(reservePriceSpecified);
        }
        if (!tp.getReservePrice().equals(reservePrice)) {
            tp.setReservePrice(reservePrice);
        }
        if (auctionType.equals("vickreyAuction")) {
            tp.setVickreyAuctionSpecified(Boolean.TRUE);
            tp.setBlindAuctionSpecified(Boolean.FALSE);
            tp.setEnglishAuctionSpecified(Boolean.FALSE);
        } else if (auctionType.equals("blindAuction")) {
            tp.setVickreyAuctionSpecified(Boolean.FALSE);
            tp.setBlindAuctionSpecified(Boolean.TRUE);
            tp.setEnglishAuctionSpecified(Boolean.FALSE);
        } else {
            tp.setVickreyAuctionSpecified(Boolean.FALSE);
            tp.setBlindAuctionSpecified(Boolean.FALSE);
            tp.setEnglishAuctionSpecified(Boolean.TRUE);
        }
        if (!tp.getTitle().equals(title)) {
            tp.setTitle(title);
        }
        if (!tp.getContent().equals(content)) {
            tp.setContent(content);
        }
        if (tp.getHighestBid() == 0.0 && !tp.getInitBid().equals(initBid)) {
            tp.setInitBid(initBid);
        }
        if (!tp.getCapacity().equals(capacity)) {
            tp.setCapacity(capacity);
        }
        if (!tp.getEndDateTime().equals(endDateTime)) {
            tp.setEndDateTime(endDateTime);
            String info = "t" + postId;
            TimerService timerService = ctx.getTimerService();
            Collection timers = timerService.getTimers();
            for (Object obj : timers) {
                Timer timer = (Timer) obj;
                if (timer.getInfo().toString().equals(info)) {
                    timer.cancel();
                }
            }
            Date end = new Date(endDateTime.getTime());
            Timer newTimer = timerService.createTimer(end, info);
            TimerHandle timerHandle = newTimer.getHandle();
            tp.setTimerHandle(timerHandle);
        }
        if (!tp.getSourceLoc().getStreet().equals(sourceStreet) || !tp.getSourceLoc().getBlockNo().equals(sourceBlockNo)
                || !tp.getSourceLoc().getCity().equals(sourceCity) || !tp.getSourceLoc().getState().equals(sourceState)
                || !tp.getSourceLoc().getCountry().equals(sourceCountry) || !tp.getSourceLoc().getPostalCode().equals(sourcePostalCode)) {
            Location sourceLoc = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
            tp.setSourceLoc(sourceLoc);
        }
        if (!tp.getDestLoc().getStreet().equals(destStreet) || !tp.getDestLoc().getBlockNo().equals(destBlockNo)
                || !tp.getDestLoc().getCity().equals(destCity) || !tp.getDestLoc().getState().equals(destState)
                || !tp.getDestLoc().getCountry().equals(destCountry) || !tp.getDestLoc().getPostalCode().equals(destPostalCode)) {
            Location destLoc = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
            tp.setDestLoc(destLoc);
        }
        if (!tp.getPerishable().equals(perishable)) {
            tp.setPerishable(perishable);
        }
        if (!tp.getFlammable().equals(flammable)) {
            tp.setFlammable(flammable);
        }
        if (!tp.getPharmaceutical().equals(pharmaceutical)) {
            tp.setPharmaceutical(pharmaceutical);
        }
        if (!tp.getHighValue().equals(highValue)) {
            tp.setHighValue(highValue);
        }
        if (!tp.getServiceStartTime().equals(serviceStartTime)) {
            tp.setServiceStartTime(serviceStartTime);
        }
        if (!tp.getServiceEndTime().equals(serviceEndTime)) {
            tp.setServiceEndTime(serviceEndTime);
        }

        em.merge(tp);

        return true;
    }

    @Override
    public List<AggregationPost> retrieveAggregationPost(Long accountId) {

        Account account = em.find(Account.class, accountId);
        Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.account= :acc AND a.deleted='false'");
        query.setParameter("acc", account);
        List<AggregationPost> aggregationPostList = new ArrayList(query.getResultList());

//        for(AggregationPost ap: aggregationPostList) {
//            boolean flag = true;
//            String info = "a" + ap.getId();
//            TimerService timerService = ctx.getTimerService();
//            Collection timers = timerService.getTimers();
//            for (Object obj : timers) {
//                Timer timer = (Timer) obj;
//                if (timer.getInfo().toString().equals(info)) {
//                    flag = false;
//                }
//            }
//            if(flag) {
//                TimerHandle th = ap.getTimerHandle();
//                Timer apTimer = th.getTimer();
//            }
//        }
        return aggregationPostList;
    }

    @Override
    public List<StoragePost> retrieveStoragePost(Long accountId) {

        Account account = em.find(Account.class, accountId);
        Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.account= :acc AND s.deleted='false'");
        query.setParameter("acc", account);
        List<StoragePost> storagePostList = new ArrayList(query.getResultList());

        return storagePostList;
    }

    @Override
    public List<TransportPost> retrieveTransportPost(Long accountId) {

        Account account = em.find(Account.class, accountId);
        Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.account= :acc AND t.deleted='false'");
        query.setParameter("acc", account);
        List<TransportPost> transportPostList = new ArrayList(query.getResultList());

        return transportPostList;
    }

    @Override
    public List<AggregationPost> retrieveAggregationPostByCompany(Long companyId) {

        Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.companyId= :company AND a.deleted='false'");
        query.setParameter("company", companyId);
        List<AggregationPost> aggregationPostList = new ArrayList(query.getResultList());

        return aggregationPostList;
    }

    @Override
    public List<StoragePost> retrieveStoragePostByCompany(Long companyId) {

        Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.companyId= :company AND s.deleted='false'");
        query.setParameter("company", companyId);
        List<StoragePost> storagePostList = new ArrayList(query.getResultList());

        return storagePostList;
    }

    @Override
    public List<TransportPost> retrieveTransportPostByCompany(Long companyId) {

        Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.companyId= :company AND t.deleted='false'");
        query.setParameter("company", companyId);
        List<TransportPost> transportPostList = new ArrayList(query.getResultList());

        return transportPostList;
    }

    @Override
    @Timeout
    public void setPostAsEnded(Timer timer) throws PostNotExistException {
        if (timer.getInfo().toString().startsWith("a")) {
            String info = timer.getInfo().toString();
            System.out.println("Timer Info " + info);
            info = info.substring(1);
            System.out.println("Post ID extract from timer Info " + info);
            Long postId = Long.parseLong(info);
            AggregationPost aggregationPost = em.find(AggregationPost.class, postId);
            if (aggregationPost == null) {
                throw new PostNotExistException("aggregation post " + postId + " does not exist!");
            }
            bmsbl.completeBid(postId, "aggregation");
            if (aggregationPost.getWinnerId() != null) {
                // set related service order to pending payment;
                if (aggregationPost.getAggregatedOrder() != null) {
                    AggregatedOrder ao = aggregationPost.getAggregatedOrder();
                    List<ServiceOrder> soList = ao.getServiceOrders();
                    for (ServiceOrder s : soList) {
                        Double newPrice = (s.getPrice() / aggregationPost.getInitBid()) * aggregationPost.getWinningPrice();
                        s.setPrice(newPrice);
                        em.merge(s);
                    }
                }

                msbl.createMessage(aggregationPost.getAccount().getId(), aggregationPost.getWinnerId(), "Congratulations!",
                        "You have won the auction for Post " + aggregationPost.getTitle() + " ./nPlease go to GRNS to check details.");
                Account account = em.find(Account.class, aggregationPost.getWinnerId());
                if (account.getAccountType().equals("External")) {
                    ExternalUserAccount eua = em.find(ExternalUserAccount.class, aggregationPost.getWinnerId());
                    try {
                        this.SendNotificationEmail(eua.getUsername(), eua.getEmail(), aggregationPost.getTitle());
                    } catch (MessagingException ex) {
                        Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (account.getAccountType().equals("User")) {
                        CompanyUserAccount cua = em.find(CompanyUserAccount.class, aggregationPost.getWinnerId());
                        try {
                            this.SendNotificationEmail(account.getUsername(), cua.getCompanyUser().getEmail(), aggregationPost.getTitle());
                        } catch (MessagingException ex) {
                            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

//            aggregationPost.setEnded(true);
//            em.merge(aggregationPost);
            System.out.println("Aggregation Post " + postId + " has ended!");
        } else if (timer.getInfo().toString().startsWith("s")) {
            String info = timer.getInfo().toString();
            System.out.println("Timer Info " + info);
            info = info.substring(1);
            System.out.println("Post ID extract from timer Info " + info);
            Long postId = Long.parseLong(info);
            StoragePost storagePost = em.find(StoragePost.class, postId);
            if (storagePost == null) {
                throw new PostNotExistException("storage post " + postId + " does not exist!");
            }
            bmsbl.completeBid(postId, "storage");
            if (storagePost.getWinnerId() != null) {
                msbl.createMessage(storagePost.getAccount().getId(), storagePost.getWinnerId(), "Congratulations!",
                        "You have won the auction for Post " + storagePost.getTitle() + " ./nPlease go to GRNS to check details.");
                Account account = em.find(Account.class, storagePost.getWinnerId());
                if (account.getAccountType().equals("External")) {
                    ExternalUserAccount eua = em.find(ExternalUserAccount.class, storagePost.getWinnerId());
                    try {
                        this.SendNotificationEmail(eua.getUsername(), eua.getEmail(), storagePost.getTitle());
                    } catch (MessagingException ex) {
                        Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (account.getAccountType().equals("User")) {
                        CompanyUserAccount cua = em.find(CompanyUserAccount.class, storagePost.getWinnerId());
                        try {
                            this.SendNotificationEmail(account.getUsername(), cua.getCompanyUser().getEmail(), storagePost.getTitle());
                        } catch (MessagingException ex) {
                            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
//            storagePost.setEnded(true);
//            em.merge(storagePost);
            System.out.println("Storage Post " + postId + " has ended!");
        } else if (timer.getInfo().toString().startsWith("t")) {
            String info = timer.getInfo().toString();
            System.out.println("Timer Info " + info);
            info = info.substring(1);
            System.out.println("Post ID extract from timer Info " + info);
            Long postId = Long.parseLong(info);
            TransportPost transportPost = em.find(TransportPost.class, postId);
            if (transportPost == null) {
                throw new PostNotExistException("transport post " + postId + " does not exist!");
            }
            bmsbl.completeBid(postId, "transport");
            if (transportPost.getWinnerId() != null) {
                msbl.createMessage(transportPost.getAccount().getId(), transportPost.getWinnerId(), "Congratulations!",
                        "You have won the auction for Post " + transportPost.getTitle() + " ./nPlease go to GRNS to check details.");
                Account account = em.find(Account.class, transportPost.getWinnerId());
                if (account.getAccountType().equals("External")) {
                    ExternalUserAccount eua = em.find(ExternalUserAccount.class, transportPost.getWinnerId());
                    try {
                        this.SendNotificationEmail(eua.getUsername(), eua.getEmail(), transportPost.getTitle());
                    } catch (MessagingException ex) {
                        Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (account.getAccountType().equals("User")) {
                        CompanyUserAccount cua = em.find(CompanyUserAccount.class, transportPost.getWinnerId());
                        try {
                            this.SendNotificationEmail(account.getUsername(), cua.getCompanyUser().getEmail(), transportPost.getTitle());
                        } catch (MessagingException ex) {
                            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
//            transportPost.setEnded(true);
//            em.merge(transportPost);
            System.out.println("Transport Post " + postId + " has ended!");
        }
    }

    private void SendNotificationEmail(String username, String email, String title) throws MessagingException {
        String subject = "Merlion Platform - GRNS - Bidding updates";
        System.out.println("Inside send email");

        String content = "<h2>Dear " + username
                + ",</h2><br />Congratulations! You have won the auction <br /><h2>" + title + "</h2><br />"
                + "<p style=\"color: #ff0000;\">Please log on to GRNS and check for details. Thank you.</p>"
                + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>GRNS Customer Support</p>";

        sendEmail.run(email, subject, content);
        System.out.println("email sent!");
    }

    @Override
    public List<AggregationPost> retrieveAllAggregationPost(String flag) {
        if (flag.equals("english")) {
            Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.deleted='false' AND a.ended='false' AND a.englishAuctionSpecified='true'");
            List<AggregationPost> apList = new ArrayList(query.getResultList());
            return apList;
        } else if (flag.equals("blind")) {
            Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.deleted='false' AND a.ended='false' AND a.blindAuctionSpecified='true'");
            List<AggregationPost> apList = new ArrayList(query.getResultList());
            return apList;
        } else if (flag.equals("vickrey")) {
            Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.deleted='false' AND a.ended='false' AND a.vickreyAuctionSpecified='true'");
            List<AggregationPost> apList = new ArrayList(query.getResultList());
            return apList;
        } else {
            Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.deleted='false' AND a.ended='false'");
            List<AggregationPost> apList = new ArrayList(query.getResultList());
            return apList;
        }
    }

    @Override
    public List<StoragePost> retrieveAllStoragePost(String flag) {
        if (flag.equals("english")) {
            Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.deleted='false' AND s.ended='false' AND s.englishAuctionSpecified='true'");
            List<StoragePost> spList = new ArrayList(query.getResultList());
            return spList;
        } else if (flag.equals("blind")) {
            Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.deleted='false' AND s.ended='false' AND s.blindAuctionSpecified='true'");
            List<StoragePost> spList = new ArrayList(query.getResultList());
            return spList;
        } else if (flag.equals("vickrey")) {
            Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.deleted='false' AND s.ended='false' AND s.vickreyAuctionSpecified='true'");
            List<StoragePost> spList = new ArrayList(query.getResultList());
            return spList;
        } else {
            Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.deleted='false' AND s.ended='false'");
            List<StoragePost> spList = new ArrayList(query.getResultList());
            return spList;
        }
    }

    @Override
    public List<TransportPost> retrieveAllTransportPost(String flag) {
        if (flag.equals("english")) {
            Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.deleted='false' AND t.ended='false' AND t.englishAuctionSpecified='true'");
            List<TransportPost> tpList = new ArrayList(query.getResultList());
            return tpList;
        } else if (flag.equals("blind")) {
            Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.deleted='false' AND t.ended='false' AND t.blindAuctionSpecified='true'");
            List<TransportPost> tpList = new ArrayList(query.getResultList());
            return tpList;
        } else if (flag.equals("vickrey")) {
            Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.deleted='false' AND t.ended='false' AND t.vickreyAuctionSpecified='true'");
            List<TransportPost> tpList = new ArrayList(query.getResultList());
            return tpList;
        } else {
            Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.deleted='false' AND t.ended='false'");
            List<TransportPost> tpList = new ArrayList(query.getResultList());
            return tpList;
        }
    }

    @Override
    public AggregationPost retrieveAggregationPostById(Long postId) {
        return em.find(AggregationPost.class, postId);
    }

    @Override
    public StoragePost retrieveStoragePostById(Long postId) {
        return em.find(StoragePost.class, postId);
    }

    @Override
    public TransportPost retrieveTransportPostById(Long postId) {
        return em.find(TransportPost.class, postId);
    }

    @Override
    public AggregationPost retrieveAggregationPostByElement(Long posterId, Timestamp startDateTime) {
        Account account = em.find(Account.class, posterId);
        if (account == null) {
            System.out.println("account does not exist!");
            return null;
        }
        Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.account=:acc AND a.postDateTime=:time");
        query.setParameter("acc", account);
        query.setParameter("time", startDateTime);
        List<AggregationPost> apList = new ArrayList(query.getResultList());
        if (apList.isEmpty()) {
            System.out.println("post does not exist!");
            return null;
        }

        return apList.get(0);
    }

    @Override
    public StoragePost retrieveStoragePostByElement(Long posterId, Timestamp startDateTime) {
        Account account = em.find(Account.class, posterId);
        if (account == null) {
            System.out.println("account does not exist!");
            return null;
        }
        Query query = em.createQuery("SELECT s FROM StoragePost s WHERE s.account=?1 AND s.postDateTime='" + startDateTime.toString() + "'");
        query.setParameter(1, account);
        List<StoragePost> spList = new ArrayList(query.getResultList());
        if (spList.isEmpty()) {
            System.out.println("post does not exist!");
            return null;
        }

        return spList.get(0);
    }

    @Override
    public TransportPost retrieveTransportPostByElement(Long posterId, Timestamp startDateTime) {
        Account account = em.find(Account.class, posterId);
        if (account == null) {
            System.out.println("account does not exist!");
            return null;
        }
        Query query = em.createQuery("SELECT t FROM TransportPost t WHERE t.account=:acc AND t.postDateTime=:time");
        query.setParameter("acc", account);
        query.setParameter("time", startDateTime);
        List<TransportPost> tpList = new ArrayList(query.getResultList());
        if (tpList.isEmpty()) {
            System.out.println("post does not exist!");
            return null;
        }

        return tpList.get(0);
    }

    @Override
    public List<AggregationPost> retrieveBidAggregationPost(Long bidderId) {
        Query query = em.createQuery("SELECT distinct b.postId FROM Bid b WHERE b.bidderId=:id AND b.postType='aggregation' ORDER By b.id DESC");
        query.setParameter("id", bidderId);
        List<Long> apIdList = new ArrayList(query.getResultList());
        List<AggregationPost> apList = new ArrayList();
        for (Long id : apIdList) {
            AggregationPost ap = em.find(AggregationPost.class, id);
            apList.add(ap);
        }
        return apList;
    }

    @Override
    public List<StoragePost> retrieveBidStoragePost(Long bidderId) {
        Query query = em.createQuery("SELECT distinct b.postId FROM Bid b WHERE b.bidderId=:id AND b.postType='storage' ORDER By b.id DESC");
        query.setParameter("id", bidderId);
        List<Long> spIdList = new ArrayList(query.getResultList());
        List<StoragePost> spList = new ArrayList();
        for (Long id : spIdList) {
            StoragePost sp = em.find(StoragePost.class, id);
            spList.add(sp);
        }
        return spList;
    }

    @Override
    public List<TransportPost> retrieveBidTransportPost(Long bidderId) {
        Query query = em.createQuery("SELECT distinct b.postId FROM Bid b WHERE b.bidderId=:id AND b.postType='transport' ORDER By b.id DESC");
        query.setParameter("id", bidderId);
        List<Long> tpIdList = new ArrayList(query.getResultList());
        List<TransportPost> tpList = new ArrayList();
        for (Long id : tpIdList) {
            TransportPost tp = em.find(TransportPost.class, id);
            tpList.add(tp);
        }
        return tpList;
    }

    @Override
    public List<AggregationPost> filterAggergationPostByPrice(List<AggregationPost> apList, Double lowPrice, Double highPrice) {
        List<AggregationPost> result = new ArrayList();
        for (AggregationPost a : apList) {
            if (a.getLowestBid() >= lowPrice && a.getLowestBid() <= highPrice) {
                result.add(a);
            }
        }
        return result;
    }

    @Override
    public List<StoragePost> filterStoragePostByPrice(List<StoragePost> spList, Double lowPrice, Double highPrice) {
        List<StoragePost> result = new ArrayList();
        for (StoragePost s : spList) {
            if (s.getHighestBid() >= lowPrice && s.getHighestBid() <= highPrice) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public List<TransportPost> filterTransportPostByPrice(List<TransportPost> tpList, Double lowPrice, Double highPrice) {
        List<TransportPost> result = new ArrayList();
        for (TransportPost t : tpList) {
            if (t.getHighestBid() >= lowPrice && t.getHighestBid() <= highPrice) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public List<AggregationPost> filterAggregationPostByTime(List<AggregationPost> apList, String flag) {
        if (flag.equals("new")) {
            List<AggregationPost> result = new ArrayList();
            for (AggregationPost a : apList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() - oneDay);
                if (a.getPostDateTime().after(temp) && a.getPostDateTime().before(now)) {
                    result.add(a);
                }
            }
            return result;
        } else {
            List<AggregationPost> result = new ArrayList();
            for (AggregationPost a : apList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() + oneDay);
                if (a.getEndDateTime().before(temp) && a.getEndDateTime().after(now)) {
                    result.add(a);
                }
            }
            return result;
        }
    }

    @Override
    public List<StoragePost> filterStoragePostByTime(List<StoragePost> spList, String flag) {
        if (flag.equals("new")) {
            List<StoragePost> result = new ArrayList();
            for (StoragePost s : spList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() - oneDay);
                if (s.getPostDateTime().after(temp) && s.getPostDateTime().before(now)) {
                    result.add(s);
                }
            }
            return result;
        } else {
            List<StoragePost> result = new ArrayList();
            for (StoragePost s : spList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() + oneDay);
                if (s.getEndDateTime().before(temp) && s.getEndDateTime().after(now)) {
                    result.add(s);
                }
            }
            return result;
        }
    }

    @Override
    public List<TransportPost> filterTransportPostByTime(List<TransportPost> tpList, String flag) {
        if (flag.equals("new")) {
            List<TransportPost> result = new ArrayList();
            for (TransportPost t : tpList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() - oneDay);
                if (t.getPostDateTime().after(temp) && t.getPostDateTime().before(now)) {
                    result.add(t);
                }
            }
            return result;
        } else {
            List<TransportPost> result = new ArrayList();
            for (TransportPost t : tpList) {
                Date now = new Date();
                Timestamp temp = new Timestamp(now.getTime());
                // 84600000 milliseconds in a day
                long oneDay = 1 * 24 * 60 * 60 * 1000;
                // to add to the timestamp
                temp.setTime(temp.getTime() + oneDay);
                if (t.getEndDateTime().before(temp) && t.getEndDateTime().after(now)) {
                    result.add(t);
                }
            }
            return result;
        }
    }

    @Override
    public List<AggregationPost> searchAggregationPost(String criteria) {
        String[] criteriaList = criteria.split(" ");
        Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.ended='false'");
        List<AggregationPost> apList = new ArrayList(query.getResultList());
        List<AggregationPost> result = new ArrayList();
        for (AggregationPost ap : apList) {
            boolean flag = true;
            for (String c : criteriaList) {
                if (!ap.getTitle().toLowerCase().contains(c.toLowerCase())) {
                    flag = false;
                }
            }
            if (flag) {
                result.add(ap);
            }
        }
        return result;
    }

    @Override
    public List<StoragePost> searchStoragePost(String criteria) {
        String[] criteriaList = criteria.split(" ");
        Query query = em.createQuery("SELECT a FROM StoragePost a WHERE a.ended='false'");
        List<StoragePost> spList = new ArrayList(query.getResultList());
        List<StoragePost> result = new ArrayList();
        for (StoragePost sp : spList) {
            boolean flag = true;
            for (String c : criteriaList) {
                if (!sp.getTitle().toLowerCase().contains(c.toLowerCase())) {
                    flag = false;
                }
            }
            if (flag) {
                result.add(sp);
            }
        }
        return result;
    }

    @Override
    public List<TransportPost> searchTransportPost(String criteria) {
        String[] criteriaList = criteria.split(" ");
        Query query = em.createQuery("SELECT a FROM TransportPost a WHERE a.ended='false'");
        List<TransportPost> tpList = new ArrayList(query.getResultList());
        List<TransportPost> result = new ArrayList();
        for (TransportPost tp : tpList) {
            boolean flag = true;
            for (String c : criteriaList) {
                if (!tp.getTitle().toLowerCase().contains(c.toLowerCase())) {
                    flag = false;
                }
            }
            if (flag) {
                result.add(tp);
            }
        }
        return result;
    }

    @Override
    public List<AggregationPost> retrieveWinningAggregationPost(Long accountId) {
        Query query = em.createQuery("SELECT a FROM AggregationPost a WHERE a.ended='true' AND a.winnerId=:id");
        query.setParameter("id", accountId);
        List<AggregationPost> apList = new ArrayList(query.getResultList());
        return apList;
    }

    @Override
    public List<StoragePost> retrieveWinningStoragePost(Long accountId) {
        Query query = em.createQuery("SELECT a FROM StoragePost a WHERE a.ended='true' AND a.winnerId=:id");
        query.setParameter("id", accountId);
        List<StoragePost> spList = new ArrayList(query.getResultList());
        return spList;
    }

    @Override
    public List<TransportPost> retrieveWinningTransportPost(Long accountId) {
        Query query = em.createQuery("SELECT a FROM TransportPost a WHERE a.ended='true' AND a.winnerId=:id");
        query.setParameter("id", accountId);
        List<TransportPost> tpList = new ArrayList(query.getResultList());
        return tpList;
    }

    @Override
    public AggregationPost extendAggregationPost(Long postId, Timestamp endDateTime) {
        AggregationPost ap = em.find(AggregationPost.class, postId);
        if (!ap.getEnded()) {
            System.out.println("Post not ended");
            return null;
        }
        if (endDateTime.before(new Date())) {
            System.out.println("end time before today!");
            return null;
        }
        ap.setEndDateTime(endDateTime);
        ap.setEnded(Boolean.FALSE);
        em.merge(ap);
        return ap;
    }

    @Override
    public StoragePost extendStoragePost(Long postId, Timestamp endDateTime) {
        StoragePost sp = em.find(StoragePost.class, postId);
        if (!sp.getEnded()) {
            System.out.println("Post not ended");
            return null;
        }
        if (endDateTime.before(new Date())) {
            System.out.println("end time before today!");
            return null;
        }
        sp.setEndDateTime(endDateTime);
        sp.setEnded(Boolean.FALSE);
        em.merge(sp);
        return sp;
    }

    @Override
    public TransportPost extendTransportPost(Long postId, Timestamp endDateTime) {
        TransportPost tp = em.find(TransportPost.class, postId);
        if (!tp.getEnded()) {
            System.out.println("Post not ended");
            return null;
        }
        if (endDateTime.before(new Date())) {
            System.out.println("end time before today!");
            return null;
        }
        tp.setEndDateTime(endDateTime);
        tp.setEnded(Boolean.FALSE);
        em.merge(tp);
        return tp;
    }

    @Override
    public ServiceOrder createAggregationServiceOrder(Long postId) {
        AggregationPost ap = em.find(AggregationPost.class, postId);
        if (ap == null) {
            System.out.println("Cannot find post");
            return null;
        }

        try {
            Company client = cmsbl.retrieveCompany(ap.getCompanyId());
            Company provider = cmsbl.retrieveCompany(ap.getWinnerCompanyId());
            Product product = pimsbl.retrieveProduct("GRNSOrder", "AggregatedOrder");
            ServiceQuotation sq = new ServiceQuotation(provider, client, "GRNS Aggregated Order", ap.getRequestStartTime(),
                    ap.getRequestEndTime(), ap.getQuantity().intValue(), 0, ap.getSourceLoc(), ap.getDestLoc(), ap.getPerishable(),
                    ap.getFlammable(), ap.getPharmaceutical(), ap.getHighValue(), null, product, ap.getQuantity(),
                    ap.getId(), ap.getPostType());
            em.persist(sq);
            ServiceContract sc = new ServiceContract(client, provider, ap.getId(), ap.getPostType());
            em.persist(sc);
            sq.setServiceContract(sc);
            sq.setEstablishedContractOrNot(Boolean.TRUE);
            sc.setServiceQuotation(sq);
            em.merge(sq);
            em.merge(sc);

            ServiceOrder so = new ServiceOrder(ap.getRequestStartTime(), ap.getRequestEndTime(), ap.getWinningPrice(),
                    "GRNS Aggregated Order", product, ap.getQuantity(), ap.getQuantity(), ap.getSourceLoc(), ap.getDestLoc(), null,
                    Boolean.TRUE, Boolean.FALSE, ap.getPerishable(), ap.getFlammable(), ap.getPharmaceutical(), ap.getHighValue(),
                    sc, ap.getId(), ap.getPostType());
            sc.getServiceOrder().add(so);
            em.persist(so);
            Long soId = so.getId();
            System.out.println("Service order id " + soId);
            em.merge(sc);
            ap.setOrderCreated(Boolean.TRUE);
            ap.setServiceOrderId(soId);
            em.merge(ap);
//            List<ServiceOrder> soList = ap.getAggregatedOrder().getServiceOrders();
//            if (!soList.isEmpty()) {
//                for (ServiceOrder s : soList) {
//                    s.setIsToEstablishTransOrder(Boolean.FALSE);
//                    s.setIsToEstablishWarehouseOrder(Boolean.FALSE);
//                    em.merge(s);
//                }
//            }
            if (ap.getAggregatedOrder() != null) {
                List<Long> soIdList = ap.getServiceOrderIdList();
                for (Long l : soIdList) {
                    ServiceOrder s = somsbl.retrieveServiceOrder(l);
                    s.setIsToEstablishTransOrder(Boolean.FALSE);
                    s.setIsToEstablishWarehouseOrder(Boolean.FALSE);
                    em.merge(s);
                }
            }
            return so;
        } catch (Exception ex) {
            System.out.println("create GRNS transport order Exception");
            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ServiceOrder createStorageServiceOrder(Long postId) {
        StoragePost sp = em.find(StoragePost.class, postId);
        if (sp == null) {
            System.out.println("Cannot find post");
            return null;
        }

        try {
            Company client = cmsbl.retrieveCompany(sp.getWinnerCompanyId());
            Company provider = cmsbl.retrieveCompany(sp.getCompanyId());
            Product product = pimsbl.retrieveProduct("GRNSOrder", "StorageOrder");
            Warehouse warehouse = fmsbl.retrieveWarehouse(sp.getWarehouseId());
            int duration = (int) ((sp.getServiceEndTime().getTime() - sp.getServiceStartTime().getTime()) / (1.0 * 24 * 60 * 60 * 1000));
            ServiceQuotation sq = new ServiceQuotation(provider, client, "GRNS Storage Order", sp.getServiceStartTime(),
                    sp.getServiceEndTime(), sp.getCapacity().intValue(), duration, sp.getLocation(), sp.getLocation(), sp.getPerishable(),
                    sp.getFlammable(), sp.getPharmaceutical(), sp.getHighValue(), warehouse, product, sp.getCapacity(),
                    sp.getId(), sp.getPostType());
            em.persist(sq);
            ServiceContract sc = new ServiceContract(client, provider, sp.getId(), sp.getPostType());
            em.persist(sc);
            sq.setServiceContract(sc);
            sq.setEstablishedContractOrNot(Boolean.TRUE);
            sc.setServiceQuotation(sq);
            em.merge(sq);
            em.merge(sc);

            ServiceOrder so = new ServiceOrder(sp.getServiceStartTime(), sp.getEndDateTime(), sp.getWinningPrice(),
                    "GRNS Storage Order", product, sp.getCapacity(), sp.getCapacity(), sp.getLocation(), sp.getLocation(), sp.getWarehouseId(),
                    Boolean.FALSE, Boolean.TRUE, sp.getPerishable(), sp.getFlammable(), sp.getPharmaceutical(), sp.getHighValue(),
                    sc, sp.getId(), sp.getPostType());
            sc.getServiceOrder().add(so);
            em.persist(so);
            Long soId = so.getId();
            System.out.println("Service order id " + soId);
            em.merge(sc);
            sp.setOrderCreated(Boolean.TRUE);
            sp.setServiceOrderId(soId);
            em.merge(sp);
            return so;
        } catch (Exception ex) {
            System.out.println("create GRNS transport order Exception");
            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ServiceOrder createTransportServiceOrder(Long postId) {
        TransportPost tp = em.find(TransportPost.class, postId);
        if (tp == null) {
            System.out.println("Cannot find post");
            return null;
        }
        try {
            Company client = cmsbl.retrieveCompany(tp.getWinnerCompanyId());
            Company provider = cmsbl.retrieveCompany(tp.getCompanyId());
            Product product = pimsbl.retrieveProduct("GRNSOrder", "TransportOrder");
            ServiceQuotation sq = new ServiceQuotation(provider, client, "GRNS Transport Order", tp.getServiceStartTime(),
                    tp.getServiceEndTime(), tp.getCapacity().intValue(), 0, tp.getSourceLoc(), tp.getDestLoc(), tp.getPerishable(),
                    tp.getFlammable(), tp.getPharmaceutical(), tp.getHighValue(), null, product, tp.getCapacity(),
                    tp.getId(), tp.getPostType());
            em.persist(sq);
            ServiceContract sc = new ServiceContract(client, provider, tp.getId(), tp.getPostType());
            em.persist(sc);
            sq.setServiceContract(sc);
            sq.setEstablishedContractOrNot(Boolean.TRUE);
            sc.setServiceQuotation(sq);
            em.merge(sq);
            em.merge(sc);

            ServiceOrder so = new ServiceOrder(tp.getServiceStartTime(), tp.getEndDateTime(), tp.getWinningPrice(),
                    "GRNS Tranport Order", product, tp.getCapacity(), tp.getCapacity(), tp.getSourceLoc(), tp.getDestLoc(), null,
                    Boolean.TRUE, Boolean.FALSE, tp.getPerishable(), tp.getFlammable(), tp.getPharmaceutical(), tp.getHighValue(),
                    sc, tp.getId(), tp.getPostType());
            sc.getServiceOrder().add(so);
            em.persist(so);
            Long soId = so.getId();
            System.out.println("Service order id " + soId);
            em.merge(sc);
            tp.setOrderCreated(Boolean.TRUE);
            tp.setServiceOrderId(soId);
            em.merge(tp);
            return so;

        } catch (Exception ex) {
            System.out.println("create GRNS transport order Exception");
            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Boolean fulfillAggregationPost(Long postId) {
        return true;
    }

    @Override
    public Boolean fulfillStoragePost(Long postId) {
        return true;
    }

    @Override
    public Boolean fulfillTransportPost(Long postId) {
        return true;
    }
}
