/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.session;

import GRNS.entity.CommissionPayment;
import GRNS.entity.CommissionPaymentReceipt;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author sunmingjia
 */
@Local
public interface CommissionManagementSessionBeanLocal {
    public CommissionPayment createPayment(Long postId, String postType);
    public CommissionPaymentReceipt createReceipt(Long postId, String postType);
    public CommissionPayment retrievePayment(Long postId, String postType);
    public CommissionPaymentReceipt retrieveReceipt(Long postId, String postType);
    public Boolean paidOrNot(Long postId, String postType);
    public List<CommissionPayment> retrievePaymentList(Long accId, String accountType);
    public List<CommissionPaymentReceipt> retrieveReceiptList(Long accId, String accountType);
}
