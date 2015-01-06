/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Location;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.TransOrderNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface OrderTrackingSessionBeanLocal {
    
    public String generateTrackingNumber (Long ownerId, Long transOrderId) throws CompanyNotExistException, TransOrderNotExistException;
   
    public Location transOrderTracking (Long ownerId, String trackingNumber) throws TransOrderNotExistException, CompanyNotExistException;
}
