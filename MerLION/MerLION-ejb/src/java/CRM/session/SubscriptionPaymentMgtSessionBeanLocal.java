/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SubscriptionPayment;
import CRM.entity.SubscriptionPaymentReceipt;
import Common.entity.Notification;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionPaymentNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface SubscriptionPaymentMgtSessionBeanLocal {

    public SubscriptionPayment viewSubscriptionPayment(Long companyId, Long Id) throws CompanyNotExistException, SubscriptionPaymentNotExistException;

    public List<SubscriptionPayment> viewAllSubscriptionPayment(Long companyId) throws CompanyNotExistException, SubscriptionPaymentNotExistException;

    public String createSubscriptionPayment(Long companyId, Double price, Timestamp paymentTime) throws CompanyNotExistException, SubscriptionSchemaNotExistException;
    
    //need to send notification and lock
    public Notification notificationRemind(Long accountId, Long companyId) throws CompanyNotExistException;
    
    public SubscriptionPaymentReceipt retrieveReceipt(Long ownerId, Long paymentId) throws CompanyNotExistException, SubscriptionPaymentNotExistException;

    public List<SubscriptionPayment> viewAllSubscriptionPaymentAdmin() throws SubscriptionPaymentNotExistException;

    public Boolean checkLockOrNot(Long cId) throws CompanyNotExistException;
 
}
