/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SubscriptionPayment;
import CRM.entity.SubscriptionPaymentReceipt;
import CRM.entity.SubscriptionSchema;
import Common.entity.Account;
import Common.entity.Company;
import Common.entity.Department;
import Common.entity.Notification;
import Common.session.CompanyManagementSessionBeanLocal;
import Common.session.NotificationSessionBeanLocal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionPaymentNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class SubscriptionPaymentMgtSessionBean implements SubscriptionPaymentMgtSessionBeanLocal{
    @PersistenceContext
    private EntityManager em;   
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private NotificationSessionBeanLocal nsb;
    
    @Override
    public String createSubscriptionPayment(Long companyId, Double price, Timestamp paymentTime)
    throws CompanyNotExistException, SubscriptionSchemaNotExistException{  
        Company company = em.find(Company.class, companyId);
        SubscriptionSchema schema;
        if (company == null){
              throw new CompanyNotExistException("Company " + companyId + " not exist!");
        }else{
            Query query = em.createQuery("SELECT s FROM SubscriptionSchema s WHERE s.boundaryPrice=?2");
            query.setParameter(2, price);
            try{
                schema = (SubscriptionSchema) query.getSingleResult();
            }catch(NoResultException ex) {
                throw new SubscriptionSchemaNotExistException("Subscription Schema " + price + " not exist!");
            } 
                //compute the price for subscription based on schema
                //System.out.println("before new");
                SubscriptionPayment sp = new SubscriptionPayment();
                SubscriptionPaymentReceipt spr = new SubscriptionPaymentReceipt();
                sp.setSchema(schema);
                sp.setPrice(price);
                sp.setPaymentTime(paymentTime);
                Integer year = schema.getNumOfYears();
                if(company.getSubscriptionPayment().isEmpty()){ 
                    System.out.println("payment empty");
                    sp.setExpectedPaymentTime(addExpectedPaymentTime(Calendar.getInstance(),year));
                    List<SubscriptionPayment> sList= new ArrayList<>();
                    sList.add(sp);
                    company.setSubscriptionPayment(sList);
                }else{                   
                    List<SubscriptionPayment> subPayList = company.getSubscriptionPayment();
                    SubscriptionPayment subPay = subPayList.get(subPayList.size()-1);
                    System.out.println(subPayList.size() +" "+ subPay.getId());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(subPay.getExpectedPaymentTime());
                    sp.setExpectedPaymentTime(addExpectedPaymentTime(cal, year));
                    subPayList.add(sp);
                }
                sp.setCompany(company);
                spr.setPrice(sp.getPrice());
                spr.setReceiveTime(paymentTime);
                spr.setSubscriptionPayment(sp);
                spr.setCompany(company);
                sp.setRecipt(spr);
                em.persist(spr);
                em.persist(sp);
                em.merge(company);               
                return "\nSubscription payment information created successfully!";
            }
        }
    
    
    public Timestamp addExpectedPaymentTime(Calendar cal, Integer year){
        Timestamp exTime;
        cal.add(Calendar.YEAR, year);
        exTime = new Timestamp(cal.getTimeInMillis());
        return exTime;
    }
    
    @Override
    public List<SubscriptionPayment> viewAllSubscriptionPaymentAdmin() throws SubscriptionPaymentNotExistException{
        Query query;
            query = em.createQuery("SELECT s FROM SubscriptionPayment s");
            @SuppressWarnings("unchecked")
            List<SubscriptionPayment> kList = query.getResultList();
            if (kList == null) {
              throw new SubscriptionPaymentNotExistException("Subsrciption payment not exist!");
            }else {
                return kList;
            }
    }
    
    @Override
    public List<SubscriptionPayment> viewAllSubscriptionPayment(Long companyId) throws CompanyNotExistException, SubscriptionPaymentNotExistException{
        Company company = em.find(Company.class, companyId);
        if (company == null){
              throw new CompanyNotExistException("Company " + companyId + " not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT s FROM SubscriptionPayment s WHERE s.company.id=?1");
            query.setParameter(1, companyId);
            @SuppressWarnings("unchecked")
            List<SubscriptionPayment> kList = query.getResultList();
            if (kList == null) {
              throw new SubscriptionPaymentNotExistException("Subsrciption payment not exist!");
            }else {
                return kList;
            }
        }
    }
    
    @Override
    public SubscriptionPayment viewSubscriptionPayment(Long companyId, Long Id) throws CompanyNotExistException, SubscriptionPaymentNotExistException{
        Company company = em.find(Company.class, companyId);
        if (company == null){
              throw new CompanyNotExistException("Company " + companyId + " not exist!");
        }else{
            SubscriptionPayment sp = em.find(SubscriptionPayment.class, Id);
            if (sp == null){
              throw new SubscriptionPaymentNotExistException("Subscription Payment " + Id + " not exist!");
            }else{
                Query query;
                query = em.createQuery("SELECT s FROM SubscriptionPayment s WHERE s.company.id=?1 AND s.id=?2");
                query.setParameter(1, companyId);
                query.setParameter(2, Id);
                return (SubscriptionPayment) query.getSingleResult();
            }
        }
    }
    
    @Override
    public Notification notificationRemind(Long aId, Long cId) throws CompanyNotExistException{
        Company company = em.find(Company.class, cId);
        Account account = em.find(Account.class, aId);
        if (company == null || account == null){
            throw new CompanyNotExistException("Company " + cId + " not exist!");
        }else{
            System.out.println("create nofi");
            Query q = em.createQuery("SELECT d FROM Department d WHERE d.company.id=?1 AND d.departmentName=?2");
            q.setParameter(1, cId);
            q.setParameter(2, "Account Management");
            Department d = (Department) q.getSingleResult();
            
            SubscriptionPayment sp = company.getSubscriptionPayment().get(company.getSubscriptionPayment().size()-1);
            //get time should pay for next payment
            Calendar cal = Calendar.getInstance();
            cal.setTime(sp.getExpectedPaymentTime());
            cal.add(Calendar.MONTH, -1);
            //compare whether payment expired
            Calendar now = Calendar.getInstance();
            Notification n;
            if (now.after(cal)){
                n = nsb.createNotification(account, d.getId(), "Please make your subsrciption payment ASAP!");
                n.setNotifyTime(cal.getTime());
                System.out.println("after create nofi");
                //should set receiver and sender            
                em.persist(n);
            }else {
                n = null;
            }
            return n;
        }
    }
    
    @Override
    public SubscriptionPaymentReceipt retrieveReceipt (Long ownerId, Long paymentId) throws CompanyNotExistException, SubscriptionPaymentNotExistException{
        Company company = em.find(Company.class, ownerId);
        if (company == null){
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query = em.createQuery("SELECT s FROM SubscriptionPayment s WHERE s.company.id=?1 AND s.id=?2");
            query.setParameter(1, ownerId);
            query.setParameter(2, paymentId);
            SubscriptionPayment sp = (SubscriptionPayment) query.getSingleResult();
            if(sp == null){
            throw new SubscriptionPaymentNotExistException("SubscriptionPayment " + paymentId + " not exist!");
            }else{
                return sp.getRecipt();
            }
        }
    }
    
    @Override
    public Boolean checkLockOrNot(Long cId) throws CompanyNotExistException{
        Company c = em.find(Company.class, cId);
        Boolean lock = false;
        if (c==null){
                throw new CompanyNotExistException("Company not exist!");
        }else{       
                SubscriptionPayment sp = c.getSubscriptionPayment().get(c.getSubscriptionPayment().size()-1);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sp.getExpectedPaymentTime());
                Calendar now = Calendar.getInstance();
                if(now.after(cal)){
                    lock = cmsb.deactivateCompany(cId,c.getCompanyType());//lock           
                }else{
                    lock = false;//not lock
                }
            }
        return lock;
    }
}