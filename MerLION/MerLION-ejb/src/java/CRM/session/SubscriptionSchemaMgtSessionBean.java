/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SubscriptionSchema;
import Common.entity.Company;
import Common.entity.SystemAdmin;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class SubscriptionSchemaMgtSessionBean implements SubscriptionSchemaMgtSessionBeanLocal, SubscriptionSchemaSessionBeanRemote {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public String createSubscriptionSchema(Long ownerId, Integer numOfYears, Double boundaryPrice) throws CompanyNotExistException{
        SystemAdmin pc = em.find(SystemAdmin.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            System.out.println("in create "+ownerId);
          em.persist(new SubscriptionSchema(pc, numOfYears, boundaryPrice));
          em.persist(pc);
          return "New Subscription schema added successfully!";
        }
    }
    
    @Override
    public String updateSubscriptionSchema(Long ownerId, Long Id, Integer numOfYears, Double boundaryPrice) throws CompanyNotExistException, SubscriptionSchemaNotExistException{
        SystemAdmin pc = em.find(SystemAdmin.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            SubscriptionSchema subscriptionSchema = em.find(SubscriptionSchema.class, Id);
            if (subscriptionSchema == null) {
                throw new SubscriptionSchemaNotExistException("Subscription Schema "+ Id +" does not exist!");
            } else {
                subscriptionSchema.setNumOfYears(numOfYears);
                subscriptionSchema.setBoundaryPrice(boundaryPrice);
                em.persist(subscriptionSchema);
                em.persist(pc);
                return "\nSubscription schema updated successfully!";
            }
        }
    }
    
    @Override
    public List<SubscriptionSchema> viewAllSubscriptionSchema(Long ownerId) throws CompanyNotExistException, SubscriptionSchemaNotExistException{
        SystemAdmin pc = em.find(SystemAdmin.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT s FROM SubscriptionSchema s WHERE s.owner.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<SubscriptionSchema> ssList = query.getResultList();
            if(ssList == null){
                throw new SubscriptionSchemaNotExistException("Schema does not exist!");
            }else{
                return ssList;
            }
        }
    }
    
    @Override
    public List<String> getPriceList() throws SubscriptionSchemaNotExistException{
        List<String> pList = new ArrayList<>();
        Query query = em.createQuery("SELECT s FROM SubscriptionSchema s");
        @SuppressWarnings("unchecked")
        List<SubscriptionSchema> ssList = query.getResultList();
            if(ssList == null){
                throw new SubscriptionSchemaNotExistException("Schema does not exist!");
            }else{
                for(SubscriptionSchema s: ssList){
                    pList.add(s.getBoundaryPrice().toString());
                }
            }
            return pList;
    }
    
    @Override
    public SubscriptionSchema viewSubscriptionSchema(Long ownerId, Long Id) throws CompanyNotExistException, SubscriptionSchemaNotExistException{
        SystemAdmin pc = em.find(SystemAdmin.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT c FROM SubscriptionSchema c WHERE c.company.id=?1 AND c.id=?2");
            query.setParameter(1, ownerId);
            query.setParameter(2, Id);
            SubscriptionSchema ss = (SubscriptionSchema) query.getSingleResult();
            if(ss == null){
                throw new SubscriptionSchemaNotExistException("Subscription Schema "+ Id +" does not exist!");
            }else{
                return ss;
            }
        }    
    }
    
    @Override
    public String deleteSubscriptionSchema (Long ownerId, Long Id) throws CompanyNotExistException, SubscriptionSchemaNotExistException{
        SystemAdmin pc = em.find(SystemAdmin.class, ownerId);
        if (pc == null){
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            Query query;
            query = em.createQuery("SELECT c FROM SubscriptionSchema c WHERE c.owner.id=?1 AND c.id=?2");
            query.setParameter(1, ownerId);
            query.setParameter(2, Id);
            SubscriptionSchema ss = (SubscriptionSchema) query.getSingleResult();
            if(ss == null){
                throw new SubscriptionSchemaNotExistException("Subscription Schema "+ Id +" does not exist!");
            }else{
                em.remove(ss);
                return "Subscription Schema " + Id + " removed sucessfully!";
            }
        } 
    }
}
