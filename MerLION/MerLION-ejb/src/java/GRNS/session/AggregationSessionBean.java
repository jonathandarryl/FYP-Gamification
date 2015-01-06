/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import CRM.session.ServiceOrderManagementLocal;
import Common.entity.Account;
import Common.entity.CompanyUserAccount;
import Common.entity.Location;
import Common.session.MessageSessionBeanLocal;
import GRNS.entity.AggregatedOrder;
import GRNS.entity.AggregationCart;
import GRNS.entity.AggregationPost;
import GRNS.entity.ExternalUserAccount;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import util.exception.OrderNotEnoughException;
import util.exception.OrderNotMatchException;
import util.exception.PostNotExistException;
import util.exception.ServiceOrderNotExistException;
import util.session.SendEmail;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class AggregationSessionBean implements AggregationSessionBeanLocal {
    
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private ServiceOrderManagementLocal soml;
    
    @Resource
    private SessionContext ctx;
    
    @EJB
    private BiddingManagementSessionBeanLocal bmsbl;

    @EJB
    private MessageSessionBeanLocal msbl;

    private SendEmail sendEmail;
    
    @Override
    public List<ServiceOrder> retrieveAggregationOrderList() {
        Query query = em.createQuery("SELECT s FROM ServiceOrder s WHERE s.isToAggregate='true' AND s.addedToCart='false' AND s.paidOrNot='false'");
        List<ServiceOrder> soList = new ArrayList(query.getResultList());
        List<ServiceOrder> result = new ArrayList();
        for(ServiceOrder s: soList) {
            if(s.getServiceContract().getProvider().getCompanyType().equals("5PL")) {
                result.add(s);
            }
        }
        return result;
    }
    
    @Override
    public Boolean addToAggregationCart(Long orderId) {
        Query query = em.createQuery("SELECT a FROM AggregationCart a WHERE a.serviceOrderId=:id");
        query.setParameter("id", orderId);
        List<AggregationCart> acList = new ArrayList(query.getResultList());
        if(!acList.isEmpty()) {
            return false;
        }
        AggregationCart ac = new AggregationCart(orderId);
        ServiceOrder so = em.find(ServiceOrder.class, orderId);
        if(so == null) {
            return false;
        }
        so.setAddedToCart(Boolean.TRUE);
        em.merge(so);
        em.persist(ac);
        return true;
    }
    
    @Override
    public List<ServiceOrder> retrieveAggregationCart() {
        System.out.println("Inside retrieve Aggregation Cart -ejb");
        Query query = em.createQuery("SELECT a FROM AggregationCart a WHERE a.finished='false'");
        List<AggregationCart> acList = new ArrayList(query.getResultList());
        List<ServiceOrder> soList = new ArrayList();
        for(AggregationCart a: acList) {
            try {
                ServiceOrder so = soml.retrieveServiceOrder(a.getServiceOrderId());
                if(so != null) {
                    soList.add(so);
                }
                System.out.println("Service order Id -ejb is "+a.getId());
            } catch (ServiceOrderNotExistException ex) {
                Logger.getLogger(AggregationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return soList;
    }
    
    @Override
    public Boolean removeAggregationCart(Long orderId) {
        Query query = em.createQuery("SELECT a FROM AggregationCart a WHERE a.finished='false' AND a.serviceOrderId=:id");
        query.setParameter("id", orderId);
        List<AggregationCart> acList = new ArrayList(query.getResultList());
        if(acList.isEmpty()) {
            System.out.println("cannot find item in Aggregation cart");
            return false;
        }
        AggregationCart ac = acList.get(0);
        ServiceOrder so = em.find(ServiceOrder.class, ac.getServiceOrderId());
        so.setAddedToCart(Boolean.FALSE);
        em.merge(so);
        em.remove(ac);
        em.flush();
        return true;
    }
    
    @Override
    public Boolean completeAggregationCart() throws OrderNotMatchException, OrderNotEnoughException {
        System.out.println("Inside ejb complete aggregation cart");
        Query query = em.createQuery("SELECT a FROM AggregationCart a WHERE a.finished='false'");
        List<AggregationCart> acList = new ArrayList(query.getResultList());
        if(acList.isEmpty()) {
            System.out.println("AggregationCart is empty!");
            return false;
        }
        if(acList.size()<2) {
            System.out.println("2 or more service order is needed!");
            throw new OrderNotEnoughException("2 or more service order is needed to aggregate!");
        }

        Double price = 0.0;
        Timestamp startTime = null;
        Timestamp endTime = null;
        List<ServiceOrder> serviceOrders = new ArrayList();
        List<ServiceContract> serviceContracts = new ArrayList();
        List<Long> companyIdList = new ArrayList(); //provider company id
        List<Long> productIdList = new ArrayList();
        List<Double> quantityList = new ArrayList();
        Double capacity = 0.0;
        Location sourceLoc = new Location();
        Location destLoc = new Location();
        Long warehouseId = null;
        Boolean perishable = null;
        Boolean flammable = null;
        Boolean pharmaceutical = null;
        Boolean highValue = null;
        
        Timestamp temp1 = null; // earliest end time
        Timestamp temp2 = null; // latest end time
        long threeDays = 3 * 24 * 60 * 60 * 1000;
        
        for(AggregationCart a: acList) {
            try {
                ServiceOrder so = soml.retrieveServiceOrder(a.getServiceOrderId());
                if(startTime == null) {
                    startTime = so.getStartTime();
                } else {
                    if(so.getStartTime().before(startTime)) {
                        startTime = so.getStartTime();
                    }
                }
                if(endTime == null) {
                    endTime = so.getEndTime();
                    temp1 = endTime;
                    temp2 = endTime;
                } else {
                    if(so.getEndTime().before(temp1)) {
                        temp1 = so.getEndTime();
                        endTime = temp1; // set end time to the earliest end time of all orders
                    }
                    if(so.getEndTime().after(temp2)) {
                        temp2 = so.getEndTime();
                    }
                }
                price += so.getPrice();
                serviceOrders.add(so);
                serviceContracts.add(so.getServiceContract());
                companyIdList.add(so.getServiceContract().getProvider().getId());
                productIdList.add(so.getProduct().getId());
                quantityList.add(so.getQuantity());
                capacity += so.getCapacity();
                if(sourceLoc.getCountry() == null) {
                    sourceLoc = so.getSourceLoc();
                } else { // check if source loc match
                    if(!sourceLoc.getCity().equals(so.getSourceLoc().getCity()) ||
                            !sourceLoc.getState().equals(so.getSourceLoc().getState()) ||
                            !sourceLoc.getCountry().equals(so.getSourceLoc().getCountry())) {
                        throw new OrderNotMatchException("order source location does not match!");
                    }
                }
                if(destLoc.getCountry() == null) {
                    destLoc = so.getDestLoc();
                } else { // check if dest loc match
                    if(!destLoc.getCity().equals(so.getDestLoc().getCity()) ||
                            !destLoc.getState().equals(so.getDestLoc().getState()) ||
                            !destLoc.getCountry().equals(so.getDestLoc().getCountry())) {
                        throw new OrderNotMatchException("order dest location does not match!");
                    }
                }
                if(warehouseId == null && so.getWarehouseId() != null) {
                    warehouseId = so.getWarehouseId();
                } else if(warehouseId != null && so.getWarehouseId() != null) {
                    if(!warehouseId.equals(so.getWarehouseId())) {
                        throw new OrderNotMatchException("Warehouse id not match!");
                    }
                } else if(warehouseId != null && so.getWarehouseId() == null) {
                    throw new OrderNotMatchException("Warehouse id not match!");
                } else if(warehouseId == null && so.getWarehouseId() == null) {
                    warehouseId = null;
                } else {
                    throw new OrderNotMatchException("Warehouse id not match!");
                }
                
                if(perishable == null) {
                    perishable = so.isPerishable();
                } else {
                    if(!perishable.equals(so.isPerishable())) {
                        throw new OrderNotMatchException("perishable not match!");
                    }
                }
                
                if(flammable == null) {
                    flammable = so.isFlammable();
                } else {
                    if(!flammable.equals(so.isFlammable())) {
                        throw new OrderNotMatchException("flammable not match!");
                    }
                }
                
                if(pharmaceutical == null) {
                    pharmaceutical = so.isPharmaceutical();
                } else {
                    if(!pharmaceutical.equals(so.isPharmaceutical())) {
                        throw new OrderNotMatchException("pharmaceutical not match!");
                    }
                }
                
                if(highValue == null) {
                    highValue = so.isHighValue();
                } else {
                    if(!highValue.equals(so.isHighValue())) {
                        throw new OrderNotMatchException("highValue not match!");
                    }
                }
                
            } catch (ServiceOrderNotExistException ex) {
                Logger.getLogger(AggregationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        temp1.setTime(temp1.getTime() + threeDays);
        if(temp1.before(temp2)) {
            throw new OrderNotMatchException("order end time does not fall into 3 days range.");
        }
        // add buffer to price - at least 20% profit
        price = price * 0.8;
        
        AggregatedOrder ao = new AggregatedOrder(startTime, endTime, price, sourceLoc, 
            destLoc, warehouseId, perishable, flammable, pharmaceutical, highValue);
        ao.setCompanyIdList(companyIdList);
        ao.setProductIdList(productIdList);
        ao.setQuantityList(quantityList);
        ao.setServiceOrders(serviceOrders);
        ao.setServiceContracts(serviceContracts);
        em.persist(ao);
        
        for(AggregationCart a: acList) {
            try {
                a.setFinished(Boolean.TRUE);
                em.merge(a);
                //set service order to aggregated
                ServiceOrder so = soml.retrieveServiceOrder(a.getServiceOrderId());
                so.setAggregated(Boolean.TRUE);
                so.setAggregatedOrder(ao);
                ServiceContract sc = so.getServiceContract();
                sc.setAggregatedOrder(ao);
                em.merge(so);
                em.merge(sc);
            } catch (ServiceOrderNotExistException ex) {
                Logger.getLogger(AggregationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("EJB complete aggregation cart reaches an end");
        return true;
    }
    
    @Override
    public List<AggregatedOrder> retrieveAggregatedOrderList() {
        Query query = em.createQuery("SELECT a FROM AggregatedOrder a WHERE a.published='false'");
        List<AggregatedOrder> aoList = new ArrayList(query.getResultList());
        return aoList;
    }
    
    @Override
    public AggregationPost postAggregatedOrder(Long aoId, Long accountId, Long companyId, String title, String content, 
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType) {
        AggregatedOrder ao = em.find(AggregatedOrder.class, aoId);
        if(ao == null) {
            System.out.println("Cannot find aggregated order");
            return null;
        }
        Account account = em.find(Account.class, accountId);
        if(account == null) {
            System.out.println("Cannot find account");
            return null;
        }
        
        Location sourceLoc = new Location(ao.getSourceLoc().getCountry(), ao.getSourceLoc().getState(), 
                ao.getSourceLoc().getCity(), ao.getSourceLoc().getStreet(), ao.getSourceLoc().getBlockNo(), 
                ao.getSourceLoc().getPostalCode());
        Location destLoc = new Location(ao.getDestLoc().getCountry(), ao.getDestLoc().getState(), 
                ao.getDestLoc().getCity(), ao.getDestLoc().getStreet(), ao.getDestLoc().getBlockNo(), 
                ao.getDestLoc().getPostalCode());
        if (!reservePriceSpecified) {
            System.out.println("no reserve price specified");
            reservePrice = 999999999999.9;// infinite high value if not specify reserved price - for coding convenience
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
        Double quantity = 0.0;
        for(Double q: ao.getQuantityList()) {
            quantity += q;
        }
        List<Long> serviceOrderIdList = new ArrayList();
        for(ServiceOrder s: ao.getServiceOrders()) {
            serviceOrderIdList.add(s.getId());
        }
        List<Long> serviceContractIdList = new ArrayList();
        for(ServiceContract s: ao.getServiceContracts()) {
            serviceContractIdList.add(s.getId());
        }
        AggregationPost aggregationPost = new AggregationPost(account, companyId, title, content, ao.getPrice(), endDateTime,
                reservePriceSpecified, reservePrice, v, b, e, sourceLoc, destLoc, 
                quantity, ao.isPerishable(), ao.isFlammable(), ao.isPharmaceutical(), ao.isHighValue(), 
                ao.getStartTime(), ao.getEndTime());
          
        aggregationPost.setLowestBid(ao.getPrice());
        aggregationPost.setCompanyIdList(ao.getCompanyIdList());
        aggregationPost.setServiceOrderIdList(serviceOrderIdList);
        aggregationPost.setServiceContractIdList(serviceContractIdList);
        aggregationPost.setProductIdList(ao.getProductIdList());
        aggregationPost.setQuantityList(ao.getQuantityList());
        aggregationPost.setAggregatedOrder(ao);

        em.persist(aggregationPost);
        ao.setAggregationPost(aggregationPost);
        em.merge(ao);
        em.flush();
        
        Date end = new Date(endDateTime.getTime());
        TimerService timerService = ctx.getTimerService();
        String info = "a" + aggregationPost.getId();
        System.out.println("Timer info "+info);
        Timer apTimer = timerService.createTimer(end, info);

        TimerHandle timerHandle = apTimer.getHandle();
        aggregationPost.setTimerHandle(timerHandle);
        
        em.merge(aggregationPost);
        
        ao.setPublished(Boolean.TRUE);
        em.merge(ao);

        account.getAggregationPosts().add(aggregationPost);
        em.merge(account);

        return aggregationPost;
        
    }
    
    @Override
    @Timeout
    public void setPostAsEnded(Timer timer) throws PostNotExistException {
        if (timer.getInfo().toString().startsWith("a")) {
            String info = timer.getInfo().toString();
            System.out.println("Timer Info "+info);
            info = info.substring(1);
            System.out.println("Post ID extract from timer Info "+info);
            Long postId = Long.parseLong(info);
            AggregationPost aggregationPost = em.find(AggregationPost.class, postId);
            if (aggregationPost == null) {
                throw new PostNotExistException("aggregation post " + postId + " does not exist!");
            }
            bmsbl.completeBid(postId, "aggregation");
            if (aggregationPost.getWinnerId() != null) {
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
        }
//        } else if (timer.getInfo().toString().startsWith("s")) {
//            String info = timer.getInfo().toString();
//            System.out.println("Timer Info "+info);
//            info = info.substring(1);
//            System.out.println("Post ID extract from timer Info "+info);
//            Long postId = Long.parseLong(info);
//            StoragePost storagePost = em.find(StoragePost.class, postId);
//            if (storagePost == null) {
//                throw new PostNotExistException("storage post " + postId + " does not exist!");
//            }
//            bmsbl.completeBid(postId, "storage");
//            if (storagePost.getWinnerId() != null) {
//                msbl.createMessage(storagePost.getAccount().getId(), storagePost.getWinnerId(), "Congratulations!",
//                        "You have won the auction for Post " + storagePost.getTitle() + " ./nPlease go to GRNS to check details.");
//                Account account = em.find(Account.class, storagePost.getWinnerId());
//                if (account.getAccountType().equals("External")) {
//                    ExternalUserAccount eua = em.find(ExternalUserAccount.class, storagePost.getWinnerId());
//                    try {
//                        this.SendNotificationEmail(eua.getUsername(), eua.getEmail(), storagePost.getTitle());
//                    } catch (MessagingException ex) {
//                        Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    if (account.getAccountType().equals("User")) {
//                        CompanyUserAccount cua = em.find(CompanyUserAccount.class, storagePost.getWinnerId());
//                        try {
//                            this.SendNotificationEmail(account.getUsername(), cua.getCompanyUser().getEmail(), storagePost.getTitle());
//                        } catch (MessagingException ex) {
//                            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//            }
////            storagePost.setEnded(true);
////            em.merge(storagePost);
//            System.out.println("Storage Post " + postId + " has ended!");
//        } else if (timer.getInfo().toString().startsWith("t")) {
//            String info = timer.getInfo().toString();
//            System.out.println("Timer Info "+info);
//            info = info.substring(1);
//            System.out.println("Post ID extract from timer Info "+info);
//            Long postId = Long.parseLong(info);
//            TransportPost transportPost = em.find(TransportPost.class, postId);
//            if (transportPost == null) {
//                throw new PostNotExistException("transport post " + postId + " does not exist!");
//            }
//            bmsbl.completeBid(postId, "transport");
//            if (transportPost.getWinnerId() != null) {
//                msbl.createMessage(transportPost.getAccount().getId(), transportPost.getWinnerId(), "Congratulations!",
//                        "You have won the auction for Post " + transportPost.getTitle() + " ./nPlease go to GRNS to check details.");
//                Account account = em.find(Account.class, transportPost.getWinnerId());
//                if (account.getAccountType().equals("External")) {
//                    ExternalUserAccount eua = em.find(ExternalUserAccount.class, transportPost.getWinnerId());
//                    try {
//                        this.SendNotificationEmail(eua.getUsername(), eua.getEmail(), transportPost.getTitle());
//                    } catch (MessagingException ex) {
//                        Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    if (account.getAccountType().equals("User")) {
//                        CompanyUserAccount cua = em.find(CompanyUserAccount.class, transportPost.getWinnerId());
//                        try {
//                            this.SendNotificationEmail(account.getUsername(), cua.getCompanyUser().getEmail(), transportPost.getTitle());
//                        } catch (MessagingException ex) {
//                            Logger.getLogger(PostManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//            }
////            transportPost.setEnded(true);
////            em.merge(transportPost);
//            System.out.println("Transport Post " + postId + " has ended!");
//        }
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
}
