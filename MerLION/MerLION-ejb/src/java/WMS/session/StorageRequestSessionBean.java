/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.WarehouseOrder;
import Common.entity.Company;
import MRPII.entity.Production;
import OES.entity.FinishedGood;
import OES.entity.Product;
import MRPII.entity.PurchaseOrder;
import WMS.entity.Shelf;
import WMS.entity.StorageRequest;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
@Stateless
public class StorageRequestSessionBean implements StorageRequestSessionBeanLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private InventoryFlowManagementLocal fgi;
    @EJB
    private WarehouseOrderManagementSessionLocal wom;
    
    private void createStorageRequest(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer,  String requestResource, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue){
        StorageRequest sr = new StorageRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestResource, perishable, flammable, pharmaceutical, highValue);
        em.persist(sr);
    }   
    
    @Override
    public StorageRequest createStorageRequestAndReturn(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer,  String requestResource, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue){
        StorageRequest sr = new StorageRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestResource, perishable, flammable, pharmaceutical, highValue);
        em.persist(sr);
        return sr;
    }   
        
    @Override
    public void dealWithProduction(Production production, Warehouse warehouse, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue){
        FinishedGood fg = production.getFinishedGood();
        Double quantity = production.getQuantity();
        Company requestInitiator = fg.getOwnerCompany();
        Company requestDealer = fg.getOwnerCompany();
        String requestSource = "Production";
        createStorageRequest(fg, quantity, warehouse, requestInitiator, requestDealer, requestSource, perishable, flammable, pharmaceutical, highValue);
    }
    
    @Override
    public void dealWithPurchaseOrder(PurchaseOrder po, Warehouse warehouse, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws PurchaseOrderAlreadyDealtException{
        if(po.getArrivedOrNot())
            throw new PurchaseOrderAlreadyDealtException("This purchase order has already been dealt with!");
        Product product = po.getPurchaseQuotation().getProduct();
        Double quantity = po.getPurchaseQuotation().getQuantity();
        Company requestInitiator = product.getOwnerCompany();
        Company requestDealer = product.getOwnerCompany();
        String requestSource = "PurchaseOrder";
        createStorageRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestSource, perishable, flammable, pharmaceutical, highValue);
        po.setArrivedOrNot(Boolean.TRUE);
        em.merge(po);
    }
    
    @Override
    public void dealWithWarehouseOrder(WarehouseOrder wo)
            throws TransOrderNotFulfilledException{
        System.out.println("StorageRequestSessionBean: Enter dealWithWarehouseOrder");
        Product product = wo.getServiceOrder().getProduct();
        Double quantity = wo.getServiceOrder().getQuantity();
        Warehouse warehouse = wo.getWarehouse();
        Company requestInitiator = wo.getServiceOrder().getServiceContract().getClient();
        Company requestDealer = wo.getServiceOrder().getServiceContract().getProvider();
        String requestSource = "WarehouseOrder";
        createStorageRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestSource, wo.getServiceOrder().isPerishable(), wo.getServiceOrder().isFlammable(), wo.getServiceOrder().isPharmaceutical(), wo.getServiceOrder().isHighValue());
        System.out.println("StorageRequestSessionBean: Finished dealWithWarehouseOrder");
    }
    
    @Override
    public List<StorageRequest> retrieveStorageRequestListForSpecificCompany(Company requestDealer) 
            throws StorageRequestNotExistException{
        Query query=em.createQuery("select s from StorageRequest s where s.requestDealer=?1 and s.fulfilledOrNot=FALSE");
        query.setParameter(1, requestDealer);
        ArrayList <StorageRequest> storageRequestList=new ArrayList(query.getResultList());
        if(storageRequestList.isEmpty())
            throw new StorageRequestNotExistException("No StorageRequest undealed by Company "+requestDealer.getCompanyName());
        return storageRequestList;
    }
    
    @Override
    public void fulfillStorageRequest(StorageRequest sr, String regionCode) 
            throws ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseNotExistException, WarehouseSpecialRequirementException{
        Product product = sr.getProduct();
        Double quantity = sr.getQuantity();
        Warehouse warehouse = sr.getWarehouse();
        ArrayList<Shelf> shelfList = getShelfList(product, quantity, warehouse, regionCode);
        fgi.createInflowInventory(product, quantity, sr.getPerishable(), sr.getFlammable(), sr.getPharmaceutical(), sr.getHighValue(), warehouse, shelfList);
        sr.setFulfilledOrNot(Boolean.TRUE);
        em.merge(sr);
    }
    
    @Override
    public void rejectStorageRequest(StorageRequest sr){
        sr.setRejectedOrNot(Boolean.TRUE);
        em.merge(sr);
    }
    
    private ArrayList<Shelf> getShelfList(Product product, Double quantity, Warehouse warehouse, String regionCode) 
            throws ShelfNotExistException, ShelfListInsufficientCapacityException{
        Integer neededCapacity = getNeededCapacity(product, quantity);
        List<Shelf> allShelfList = retrieveAvailableShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouse);
        if(shelfSetCapacity(allShelfList)<neededCapacity)
            throw new ShelfListInsufficientCapacityException("StorageRequestSessionBean: Inventory inflow creation failed. Selected shelf set of Region "+regionCode+" capacity is insufficient.");
        ArrayList<Shelf> result = new ArrayList<>();
        Integer currentCapacity = 0;
        for(Object o: allShelfList){
            Shelf curShelf = (Shelf) o;
            result.add(curShelf);
            currentCapacity = currentCapacity + curShelf.getCapacity();
            if(currentCapacity>=neededCapacity)
                break;
        }
        return result;
    }
    
    private List<Shelf> retrieveAvailableShelfListInSpecificRegionInSpecificWarehouse(String regionCode, Warehouse warehouse) 
            throws ShelfNotExistException{
        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.regionCode=?2 and s.archivedOrNot=FALSE and s.availability=TRUE");
        query.setParameter(1, warehouse);
        query.setParameter(2, regionCode);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Region "+regionCode+" does not have any available shelf!");
        return shelfList;
    }
    
    private Integer shelfSetCapacity(List<Shelf> shelfList) {
        Integer totalCapacity = 0;
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            if (!shelf.isArchivedOrNot()) {
                totalCapacity = totalCapacity + shelf.getCapacity();
            }
        }
        return totalCapacity;
    }
    
    private Integer getNeededCapacity(Product product, Double quantity) {
        Double quantityInOneUnitCapacity = product.getQuantityInOneUnitCapacity();
        Double neededCapacity = quantity/quantityInOneUnitCapacity;
        Integer result = (int) Math.ceil(neededCapacity);
        return result;
    }
}
