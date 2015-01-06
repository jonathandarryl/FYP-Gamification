/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.WarehouseOrder;
import Common.entity.Company;
import MRPII.entity.Production;
import MRPII.entity.PurchaseOrder;
import OES.entity.Product;
import WMS.entity.StorageRequest;
import WMS.entity.Warehouse;
import java.util.List;
import javax.ejb.Local;
import util.exception.PurchaseOrderAlreadyDealtException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.StorageRequestNotExistException;
import util.exception.TransOrderNotFulfilledException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseOrderOrderAlreadyFulfilledException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Local
public interface StorageRequestSessionBeanLocal {

    public void dealWithProduction(Production production, Warehouse warehouse, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue);

    public void dealWithPurchaseOrder(PurchaseOrder po, Warehouse warehouse, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws PurchaseOrderAlreadyDealtException;

    public void dealWithWarehouseOrder(WarehouseOrder wo)
            throws TransOrderNotFulfilledException;

    public List<StorageRequest> retrieveStorageRequestListForSpecificCompany(Company requestDealer) 
            throws StorageRequestNotExistException;

    public void fulfillStorageRequest(StorageRequest sr, String regionCode) 
            throws ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseNotExistException, WarehouseSpecialRequirementException;

    public void rejectStorageRequest(StorageRequest sr);

    public StorageRequest createStorageRequestAndReturn(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer, String requestResource, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue);
    
}
