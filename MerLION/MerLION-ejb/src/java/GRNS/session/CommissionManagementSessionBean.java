/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.session;

import GRNS.entity.AggregationPost;
import GRNS.entity.CommissionPayment;
import GRNS.entity.CommissionPaymentReceipt;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sunmingjia
 */
@Stateless
public class CommissionManagementSessionBean implements CommissionManagementSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CommissionPayment createPayment(Long postId, String postType) {
        Query query = em.createQuery("SELECT c FROM CommissionPayment c WHERE c.postId=:id AND c.postType=:type");
        query.setParameter("id", postId);
        query.setParameter("type", postType);
        List<CommissionPayment> cpList = new ArrayList(query.getResultList());
        if (!cpList.isEmpty()) {
            System.out.println("Commission exist");
            return null;
        }
        
        if (postType.equals("aggregation")) {
            AggregationPost ap = em.find(AggregationPost.class, postId);
            if (ap == null) {
                System.out.println("Aggregation post not exist");
                return null;
            }
            CommissionPayment cp = new CommissionPayment((ap.getWinningPrice()*0.2), Long.parseLong("24"), "SystemAdmin", Long.parseLong("21"),
                    "merlion", ap.getAccount().getId(), ap.getAccount().getAccountType(), ap.getCompanyId(), ap.getAccount().getCompany().getCompanyName(),
                    postId, postType);
            em.persist(cp);
            ap.setPaidOrNot(Boolean.TRUE);
            em.merge(ap);
            return cp;
        } else if (postType.equals("storage")) {
            StoragePost sp = em.find(StoragePost.class, postId);
            if (sp == null) {
                System.out.println("Storage post not exist");
                return null;
            }

            CommissionPayment cp = new CommissionPayment((sp.getWinningPrice()*0.2), Long.parseLong("24"), "SystemAdmin", Long.parseLong("21"),
                    "merlion", sp.getAccount().getId(), sp.getAccount().getAccountType(), sp.getCompanyId(), sp.getAccount().getCompany().getCompanyName(),
                    postId, postType);
            em.persist(cp);
            sp.setPaidOrNot(Boolean.TRUE);
            em.merge(sp);
            return cp;
        } else {
            TransportPost tp = em.find(TransportPost.class, postId);
            if (tp == null) {
                System.out.println("Transport post not exist");
                return null;
            }

            CommissionPayment cp = new CommissionPayment((tp.getWinningPrice()*0.2), Long.parseLong("24"), "SystemAdmin", Long.parseLong("21"),
                    "merlion", tp.getAccount().getId(), tp.getAccount().getAccountType(), tp.getCompanyId(), tp.getAccount().getCompany().getCompanyName(),
                    postId, postType);
            em.persist(cp);
            tp.setPaidOrNot(Boolean.TRUE);
            em.merge(tp);
            return cp;
        }
    }

    @Override
    public CommissionPaymentReceipt createReceipt(Long postId, String postType) {
        System.out.println("Inside create receipt - ejb");
        Query query = em.createQuery("SELECT c FROM CommissionPayment c WHERE c.postId=:id AND c.postType=:type");
        query.setParameter("id", postId);
        query.setParameter("type", postType);
        List<CommissionPayment> cpList = new ArrayList(query.getResultList());
        if (cpList.isEmpty()) {
            System.out.println("Commission Not exist");
            return null;
        }
        CommissionPayment cp = cpList.get(0);
        
        Query q = em.createQuery("SELECT c FROM CommissionPaymentReceipt c WHERE c.postId=:id AND c.postType=:type");
        q.setParameter("id", postId);
        q.setParameter("type", postType);
        List<CommissionPaymentReceipt> cprList = new ArrayList(q.getResultList());
        if (!cprList.isEmpty()) {
            System.out.println("Receipt exist");
            return null;
        }
        
        CommissionPaymentReceipt receipt = new CommissionPaymentReceipt(cp);
        em.persist(receipt);
        cp.setReceipt(receipt);
        em.merge(cp);
        System.out.println("Amount of commission payment in receipt is "+receipt.getPrice());
        System.out.println("Time of creation of commission payment receipt is "+receipt.getCreationTime());
        
        return receipt;
    }

    @Override
    public CommissionPayment retrievePayment(Long postId, String postType) {
        Query query = em.createQuery("SELECT c FROM CommissionPayment c WHERE c.postId=:id AND c.postType=:type");
        query.setParameter("id", postId);
        query.setParameter("type", postType);
        List<CommissionPayment> cpList = new ArrayList(query.getResultList());
        if (cpList.isEmpty()) {
            System.out.println("Payment Not exist");
            return null;
        }
        return cpList.get(0);
    }

    @Override
    public CommissionPaymentReceipt retrieveReceipt(Long postId, String postType) {
        Query query = em.createQuery("SELECT c FROM CommissionPaymentReceipt c WHERE c.postId=:id AND c.postType=:type");
        query.setParameter("id", postId);
        query.setParameter("type", postType);
        List<CommissionPaymentReceipt> cprList = new ArrayList(query.getResultList());
        if (cprList.isEmpty()) {
            System.out.println("Receipt Not exist");
            return null;
        }
        return cprList.get(0);
    }

    @Override
    public Boolean paidOrNot(Long postId, String postType) {
        Query query = em.createQuery("SELECT c FROM CommissionPayment c WHERE c.postId=:id AND c.postType=:type");
        query.setParameter("id", postId);
        query.setParameter("type", postType);
        List<CommissionPayment> cp = new ArrayList(query.getResultList());
        if (cp.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<CommissionPayment> retrievePaymentList(Long accId, String accountType) {
        Query query = em.createQuery("SELECT c FROM CommissionPayment c WHERE c.senderId=:id AND c.senderType=:type");
        query.setParameter("id", accId);
        query.setParameter("type", accountType);
        List<CommissionPayment> cpList = new ArrayList(query.getResultList());
        if (cpList.isEmpty()) {
            System.out.println("Commission payment in ejb is empty");
            return null;
        }
        return cpList;
    }

    @Override
    public List<CommissionPaymentReceipt> retrieveReceiptList(Long accId, String accountType) {
        Query query = em.createQuery("SELECT c FROM CommissionPaymentReceipt c WHERE c.receiverId=:id AND c.receiverType=:type");
        query.setParameter("id", accId);
        query.setParameter("type", accountType);
        List<CommissionPaymentReceipt> cprList = new ArrayList(query.getResultList());
        if (cprList.isEmpty()) {
            return null;
        }
        return cprList;
    }
}
