/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.session;

import CRM.entity.ServiceOrder;
import GRNS.entity.AggregatedOrder;
import GRNS.entity.AggregationCart;
import GRNS.entity.AggregationPost;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Timer;
import util.exception.OrderNotEnoughException;
import util.exception.OrderNotMatchException;
import util.exception.PostNotExistException;

/**
 *
 * @author chongyangsun
 */
@Local
public interface AggregationSessionBeanLocal {
    public List<ServiceOrder> retrieveAggregationOrderList();
    public Boolean addToAggregationCart(Long orderId);
    public List<ServiceOrder> retrieveAggregationCart();
    public Boolean removeAggregationCart(Long orderId);
    public Boolean completeAggregationCart() throws OrderNotMatchException, OrderNotEnoughException;
    public List<AggregatedOrder> retrieveAggregatedOrderList();
    public AggregationPost postAggregatedOrder(Long aoId, Long accountId, Long companyId, String title, String content, 
            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType);
    public void setPostAsEnded(Timer timer) throws PostNotExistException;
}
