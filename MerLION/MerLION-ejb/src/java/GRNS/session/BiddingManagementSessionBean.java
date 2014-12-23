/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.session;

import Common.entity.Account;
import GRNS.entity.*;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BidInvalidException;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class BiddingManagementSessionBean implements BiddingManagementSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Bid createNewBid(Long accountId, Long postId, String postType, Double bid) throws BidInvalidException {
        if (postType.equals("aggregation")) {
            AggregationPost ap = em.find(AggregationPost.class, postId);
            if (ap == null) {
                System.out.println("aggregation post does not exist!");
                return null;
            }

            Bid lowestBid = this.retrieveWinningBid(postId, postType);
            Double lowest;
            if (lowestBid == null) {
                lowest = ap.getInitBid();
            } else {
                lowest = lowestBid.getCurrentBid();
            }

            if (bid > (lowest - 10.0)) {
                throw new BidInvalidException("New bid is higher than current possible bid");
            }
            ap.setLowestBid(bid);
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(ap);
            return newBid;
        } else if (postType.equals("storage")) {
            StoragePost sp = em.find(StoragePost.class, postId);
            if (sp == null) {
                System.out.println("storage post does not exist!");
                return null;
            }

            Bid highestBid = this.retrieveWinningBid(postId, postType);
            Double highest;
            if (highestBid == null) {
                highest = sp.getInitBid();
            } else {
                highest = highestBid.getCurrentBid();
            }
            if (bid < (highest + 10)) {
                throw new BidInvalidException("New bid is lower than current possible bid");
            }
            sp.setHighestBid(bid);
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(sp);
            return newBid;
        } else {
            TransportPost tp = em.find(TransportPost.class, postId);
            if (tp == null) {
                System.out.println("post does not exist!");
                return null;
            }

            Bid highestBid = this.retrieveWinningBid(postId, postType);
            Double highest;
            if (highestBid == null) {
                highest = tp.getInitBid();
            } else {
                highest = highestBid.getCurrentBid();
            }
            if (bid < (highest + 10)) {
                throw new BidInvalidException("New bid is lower than current possible bid");
            }
            tp.setHighestBid(bid);
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(tp);
            return newBid;
        }

    }

    @Override
    public Bid retrieveWinningBid(Long postId, String postType) {
        System.out.println("Inside retrieve winning bid");
        if (postType.equals("storage") || postType.equals("transport")) {
            Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id");
            query.setParameter("type", postType);
            query.setParameter("id", postId);
            List<Bid> bidList = new ArrayList(query.getResultList());
            if (bidList.isEmpty()) {
                System.out.println("No Bid for this post " + postId + " " + postType);
                return null;
            }
            Bid highestBid = new Bid();
            for (Bid b : bidList) {
                if (highestBid.getCurrentBid() == null) {
                    highestBid = b;
                } else if (highestBid.getCurrentBid() < b.getCurrentBid()) {
                    highestBid = b;
                }
            }
            return highestBid;

        } else {//post type aggregation. retrieve the lowest bid.
            Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id");
            query.setParameter("type", postType);
            query.setParameter("id", postId);
            List<Bid> bidList = new ArrayList(query.getResultList());
            System.out.println("Size of bidList is" + bidList.size());
            if (bidList.isEmpty()) {
                System.out.println("No Bid for this post " + postId + " " + postType);
                return null;
            }
            Bid lowestBid = new Bid();
            for (Bid b : bidList) {
                if (lowestBid.getCurrentBid() == null) {
                    System.out.println("lowest null");
                    lowestBid = b;
                } else if (lowestBid.getCurrentBid() > b.getCurrentBid()) {
                    System.out.println("lowest changed");
                    lowestBid = b;
                }
            }
            return lowestBid;
        }
    }

    @Override
    public Bid retrieveNextWinningBid(Long postId, String postType) {
        System.out.println("Inside retrieve next winning bid");
        if (postType.equals("storage") || postType.equals("transport")) {
            Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id");
            query.setParameter("type", postType);
            query.setParameter("id", postId);
            List<Bid> bidList = new ArrayList(query.getResultList());
            if (bidList.isEmpty()) {
                System.out.println("No Bid for this post " + postId + " " + postType);
                return null;
            }
            Bid highestBid = new Bid();
            Bid nextBid = new Bid();
            for (Bid b : bidList) {
                if (highestBid.getCurrentBid() == null) {
                    highestBid = b;
                    nextBid = b;
                } else if (highestBid.getCurrentBid() > b.getCurrentBid() && nextBid.getCurrentBid() < b.getCurrentBid()) {
                    nextBid = b;
                } else if (highestBid.getCurrentBid() < b.getCurrentBid()) {
                    nextBid = highestBid;
                    highestBid = b;
                }
            }
            return nextBid;

        } else {//post type aggregation. retrieve the lowest bid.
            Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id");
            query.setParameter("type", postType);
            query.setParameter("id", postId);
            List<Bid> bidList = new ArrayList(query.getResultList());
            System.out.println("Size of bidList is" + bidList.size());
            if (bidList.isEmpty()) {
                System.out.println("No Bid for this post " + postId + " " + postType);
                return null;
            }
            Bid lowestBid = new Bid();
            Bid nextBid = new Bid();
            for (Bid b : bidList) {
                if (lowestBid.getCurrentBid() == null) {
                    lowestBid = b;
                    nextBid = b;
                } else if (lowestBid.getCurrentBid() < b.getCurrentBid() && nextBid.getCurrentBid() > b.getCurrentBid()) {
                    nextBid = b;
                } else if (lowestBid.getCurrentBid() > b.getCurrentBid()) {
                    nextBid = lowestBid;
                    lowestBid = b;
                }
            }
            return nextBid;
        }
    }

    @Override
    public Integer retrieveBidsCount(Long postId, String postType) {
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id");
        query.setParameter("type", postType);
        query.setParameter("id", postId);
        List<Bid> bidList = new ArrayList(query.getResultList());
        if (bidList.isEmpty()) {
            System.out.println("No Bid for this post " + postId + " " + postType);
            return 0;
        }
        Integer count = bidList.size();
        return count;
    }

    @Override
    public Boolean retractingBid(Long accountId, Long postId, String postType) {
        return true;
    }

    @Override
    public Boolean updateBid(Long accountId, Long postId, String postType, Double bid) {
        return true;// not necessary
    }

    @Override
    public Boolean completeBid(Long postId, String postType) {
        Bid winningBid = this.retrieveWinningBid(postId, postType);
        Bid nextWinningBid = this.retrieveNextWinningBid(postId, postType);
        if (postType.equals("aggregation")) {
            AggregationPost ap = em.find(AggregationPost.class, postId);
            if (winningBid == null) {
                ap.setLowestBid(ap.getInitBid());
                ap.setWinnerId(null);
                ap.setWinnerType(null);
                ap.setWinnerUsername("None");
                ap.setWinnerCompanyId(null);
                ap.setWinnerCompanyName("None");
                ap.setWinningPrice(null);
            } else if (!ap.getReservePriceSpecified()) {
                ap.setLowestBid(winningBid.getCurrentBid());
                ap.setWinnerId(winningBid.getBidderId());
                ap.setWinnerType(winningBid.getBidderType());
                ap.setWinnerUsername(winningBid.getBidderUsername());
                ap.setWinnerCompanyId(winningBid.getBidderCompanyId());
                ap.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (ap.getVickreyAuctionSpecified()) {
                    if (winningBid.getCurrentBid().equals(nextWinningBid.getCurrentBid())) { //only one bid
                        winningBid.setWinningPrice(ap.getInitBid());
                        ap.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        ap.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    ap.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            } else if (ap.getReservePriceSpecified() && winningBid.getCurrentBid() > ap.getReservePrice()) {
//                ap.setLowestBid(ap.getInitBid());
                ap.setWinnerId(null);
                ap.setWinnerType(null);
                ap.setWinnerUsername("None");
                ap.setWinnerCompanyId(null);
                ap.setWinnerCompanyName("None");
                ap.setWinningPrice(null);
            } else {
                ap.setLowestBid(winningBid.getCurrentBid());
                ap.setWinnerId(winningBid.getBidderId());
                ap.setWinnerType(winningBid.getBidderType());
                ap.setWinnerUsername(winningBid.getBidderUsername());
                ap.setWinnerCompanyId(winningBid.getBidderCompanyId());
                ap.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (ap.getVickreyAuctionSpecified()) {
                    if (nextWinningBid.getCurrentBid() > ap.getReservePrice()) {
                        winningBid.setWinningPrice(ap.getReservePrice());
                        ap.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        ap.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    ap.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            }
            ap.setEnded(Boolean.TRUE);

            em.merge(ap);
            return true;
        } else if (postType.equals("storage")) {
            StoragePost sp = em.find(StoragePost.class, postId);
            if (winningBid == null) {
                sp.setHighestBid(sp.getInitBid());
                sp.setWinnerId(null);
                sp.setWinnerType(null);
                sp.setWinnerUsername("None");
                sp.setWinnerCompanyId(null);
                sp.setWinnerCompanyName("None");
            } else if (!sp.getReservePriceSpecified()) {
                sp.setHighestBid(winningBid.getCurrentBid());
                sp.setWinnerId(winningBid.getBidderId());
                sp.setWinnerType(winningBid.getBidderType());
                sp.setWinnerUsername(winningBid.getBidderUsername());
                sp.setWinnerCompanyId(winningBid.getBidderCompanyId());
                sp.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (sp.getVickreyAuctionSpecified()) {
                    if (winningBid.getCurrentBid().equals(nextWinningBid.getCurrentBid())) { //only one bid
                        winningBid.setWinningPrice(sp.getInitBid());
                        sp.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        sp.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    sp.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            } else if (sp.getReservePriceSpecified() && winningBid.getCurrentBid() < sp.getReservePrice()) {
//                sp.setLowestBid(ap.getInitBid());
                sp.setWinnerId(null);
                sp.setWinnerType(null);
                sp.setWinnerUsername("None");
                sp.setWinnerCompanyId(null);
                sp.setWinnerCompanyName("None");
            } else {
                sp.setHighestBid(winningBid.getCurrentBid());
                sp.setWinnerId(winningBid.getBidderId());
                sp.setWinnerType(winningBid.getBidderType());
                sp.setWinnerUsername(winningBid.getBidderUsername());
                sp.setWinnerCompanyId(winningBid.getBidderCompanyId());
                sp.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (sp.getVickreyAuctionSpecified()) {
                    if (nextWinningBid.getCurrentBid() < sp.getReservePrice()) {
                        winningBid.setWinningPrice(sp.getReservePrice());
                        sp.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        sp.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    sp.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            }
            sp.setEnded(Boolean.TRUE);

            em.merge(sp);
            return true;
        } else {
            TransportPost tp = em.find(TransportPost.class, postId);
            if (winningBid == null) {
                tp.setHighestBid(tp.getInitBid());
                tp.setWinnerId(null);
                tp.setWinnerType(null);
                tp.setWinnerUsername("None");
                tp.setWinnerCompanyId(null);
                tp.setWinnerCompanyName("None");
            } else if (!tp.getReservePriceSpecified()) {
                tp.setHighestBid(winningBid.getCurrentBid());
                tp.setWinnerId(winningBid.getBidderId());
                tp.setWinnerType(winningBid.getBidderType());
                tp.setWinnerUsername(winningBid.getBidderUsername());
                tp.setWinnerCompanyId(winningBid.getBidderCompanyId());
                tp.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (tp.getVickreyAuctionSpecified()) {
                    if (winningBid.getCurrentBid().equals(nextWinningBid.getCurrentBid())) { //only one bid
                        winningBid.setWinningPrice(tp.getInitBid());
                        tp.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        tp.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    tp.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            } else if (tp.getReservePriceSpecified() && winningBid.getCurrentBid() < tp.getReservePrice()) {
//                tp.setLowestBid(ap.getInitBid());
                tp.setWinnerId(null);
                tp.setWinnerType(null);
                tp.setWinnerUsername("None");
                tp.setWinnerCompanyId(null);
                tp.setWinnerCompanyName("None");
            } else {
                tp.setHighestBid(winningBid.getCurrentBid());
                tp.setWinnerId(winningBid.getBidderId());
                tp.setWinnerType(winningBid.getBidderType());
                tp.setWinnerUsername(winningBid.getBidderUsername());
                tp.setWinnerCompanyId(winningBid.getBidderCompanyId());
                tp.setWinnerCompanyName(winningBid.getBidderCompanyName());
                if (tp.getVickreyAuctionSpecified()) {
                    if (nextWinningBid.getCurrentBid() < tp.getReservePrice()) {
                        winningBid.setWinningPrice(tp.getReservePrice());
                        tp.setWinningPrice(winningBid.getWinningPrice());
                    } else {
                        winningBid.setWinningPrice(nextWinningBid.getCurrentBid());
                        tp.setWinningPrice(winningBid.getWinningPrice());
                    }
                } else {
                    winningBid.setWinningPrice(winningBid.getCurrentBid());
                    tp.setWinningPrice(winningBid.getWinningPrice());
                }
                em.merge(winningBid);
            }
            tp.setEnded(Boolean.TRUE);

            em.merge(tp);
            return true;
        }
    }

    @Override
    public Boolean checkBidAlreadyExist(Long accountId, Long postId, String postType) {
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postType= :type AND b.postId= :id AND b.bidderId=:bidder");
        query.setParameter("type", postType);
        query.setParameter("id", postId);
        query.setParameter("bidder", accountId);
        List<Bid> bidList = new ArrayList(query.getResultList());
        if (bidList.isEmpty()) {
            System.out.println("No Bid for this post " + postId + " " + postType + " from account " + accountId);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Bid> retrieveAllBids(Long postId, String postType) {
        Query query = em.createQuery("SELECT b FROM Bid b WHERE b.postId=:post AND b.postType=:type");
        query.setParameter("post", postId);
        query.setParameter("type", postType);
        List<Bid> bidList = new ArrayList(query.getResultList());
        return bidList;
    }
    
    @Override
    public Bid createBlindBid(Long accountId, Long postId, String postType, Double bid) throws BidInvalidException {
        if (postType.equals("aggregation")) {
            AggregationPost ap = em.find(AggregationPost.class, postId);
            if (ap == null) {
                System.out.println("aggregation post does not exist!");
                return null;
            }
            //retrieve current lowest bid of aggregation post
            Bid lowestBid = this.retrieveWinningBid(postId, postType);
            Double lowest;
            // if no previous bid, assign lowest
            if (lowestBid == null) {
                lowest = ap.getInitBid();
            } else {
                lowest = lowestBid.getCurrentBid();
            }
            // if ap do not have lowest bid number, assign it!
            if (ap.getLowestBid() == null) {
                ap.setLowestBid(lowest);
            } else if(ap.getLowestBid()>bid) {
                ap.setLowestBid(bid);
            }
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(ap);
            return newBid;
        } else if (postType.equals("storage")) {
            StoragePost sp = em.find(StoragePost.class, postId);
            if (sp == null) {
                System.out.println("storage post does not exist!");
                return null;
            }

            Bid highestBid = this.retrieveWinningBid(postId, postType);
            Double highest;
            if (highestBid == null) {
                highest = sp.getInitBid();
            } else {
                highest = highestBid.getCurrentBid();
            }
            if(sp.getHighestBid() == null) {
                sp.setHighestBid(highest);
            } else if (sp.getHighestBid()<bid) {
                sp.setHighestBid(bid);
            }
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(sp);
            return newBid;
        } else {
            TransportPost tp = em.find(TransportPost.class, postId);
            if (tp == null) {
                System.out.println("post does not exist!");
                return null;
            }

            Bid highestBid = this.retrieveWinningBid(postId, postType);
            Double highest;
            if (highestBid == null) {
                highest = tp.getInitBid();
            } else {
                highest = highestBid.getCurrentBid();
            }
            if(tp.getHighestBid() == null) {
                tp.setHighestBid(highest);
            } else if (tp.getHighestBid()<bid) {
                tp.setHighestBid(bid);
            }
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new BidInvalidException("account id does not exist!");
            }
            Long companyId = null;
            String companyName = null;
            if (account.getAccountType().equals("External")) {
                ExternalUserAccount eua = em.find(ExternalUserAccount.class, accountId);
                companyId = eua.getExternalCompany().getId();
                companyName = eua.getExternalCompany().getCompanyName();
            } else {
                companyId = account.getCompany().getId();
                companyName = account.getCompany().getCompanyName();
            }
            
            String username = account.getUsername();
            Bid newBid = new Bid(postId, postType, accountId, username, companyId, companyName, bid);
            newBid.setBidderType(account.getAccountType());
            em.persist(newBid);
            em.merge(tp);
            return newBid;
        }
    }

}
