/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Company;
import Common.entity.Location;
import TMS.entity.TransOrder;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.TransOrderNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class OrderTrackingSessionBean implements OrderTrackingSessionBeanLocal {

    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;
    
    //D.6.1
    @Override
    public String generateTrackingNumber (Long ownerId, Long transOrderId) throws CompanyNotExistException, TransOrderNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
              throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            TransOrder transOrder = em.find(TransOrder.class, transOrderId);
            if (transOrder == null) {
              throw new TransOrderNotExistException("transportation order " + transOrderId + " not exist!");
            } else {
                String trackingNumber = "";
                String sb = transOrder.getRecvTime().toString();
                for(int i=0; i<sb.length(); i++){
                    char c = sb.charAt(i);
                    if(c >=48 && c <= 57){
                        trackingNumber += c;
                    }
                }
                trackingNumber += transOrder.getTransOrderId().toString();
                transOrder.setTrackingNumber(trackingNumber);
                em.persist(transOrder);
                return trackingNumber;
            }
        }
    }
    
    //D.6.2
    @Override
    public Location transOrderTracking (Long ownerId, String trackingNumber) 
            throws TransOrderNotExistException, CompanyNotExistException{
        Company pc = em.find(Company.class, ownerId);
        if (pc == null){
               throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }else{
            Query query=em.createQuery("select t from TransOrder t WHERE t.trackingNumber=?1");
            query.setParameter(1, trackingNumber);         
           if (query.getSingleResult() == null) {
                throw new TransOrderNotExistException("transportation order not exist!");
           }else {
                return ((TransOrder)query.getSingleResult()).getCurrentLocation();
           }
        }
    }

}
