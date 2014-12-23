/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.WarehouseOrder;
import java.util.ArrayList;
import javax.ejb.Local;
import util.exception.AccessDeniedException;
import util.exception.ServiceOrderAlreadyEstablishedWarehouseOrderException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.TransOrderNotFulfilledException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseOrderNotExistException;
import util.exception.WarehouseOrderOrderAlreadyFulfilledException;

/**
 *
 * @author songhan
 */
@Local
public interface WarehouseOrderManagementSessionLocal {

    public void createWarehouseOrder(Long soId) 
            throws ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedWarehouseOrderException, ServiceOrderNotPaidException, WarehouseNotExistException;

    public ArrayList<WarehouseOrder> getUnfulfilledIncomingWarehouseOrderList(String ownerCompanyName, String warehouseName)
            throws WarehouseNotExistException, WarehouseOrderNotExistException, AccessDeniedException;

    public void fulfillWarehouseOrder(WarehouseOrder wo)
            throws TransOrderNotFulfilledException, WarehouseOrderOrderAlreadyFulfilledException, ServiceOrderNotExistException;

}
