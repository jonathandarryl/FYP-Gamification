/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WMS.session;

import Common.entity.Company;
import OES.entity.FinishedGood;
import OES.entity.Product;
import WMS.entity.Inventory;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.Local;
import util.exception.InsufficientStorageInWarehouseException;
import util.exception.InventoryNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ShelfIncorrectProductException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Local
public interface InventoryFlowManagementLocal {
//    public void createInflowInventory(String productOwnerCompanyName, String productName, Double quantity,
//            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, 
//            String WarehouseOwnerCompanyName,String warehouseName, ArrayList<String> shelfSerialCodeList)
//            throws ProductNotExistException, MultipleProductWithSameNameException, WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException;
//    
    public void createInflowInventory(Product finishedGood, Double quantity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Warehouse warehouse, ArrayList<Shelf> shelfList) 
            throws WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException;
    
//    public void createOutflowInventory(String productOwnerCompanyName, String productName, Double quantity, Boolean perishable, Boolean flammable, 
//                                    Boolean pharmaceutical, Boolean highValue, String warehouseOwnerCompanyName,String warehouseName, ArrayList<String> shelfSerialCodeList)
//            throws ProductNotExistException, MultipleProductWithSameNameException, WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, ShelfIncorrectProductException, InsufficientStorageInWarehouseException;
//    
    public void createOutflowInventory(Product finishedGood, Double quantity, Warehouse warehouse, ArrayList<Shelf> shelfList) 
            throws WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException, InsufficientStorageInWarehouseException, ShelfIncorrectProductException;
    
    //Handle inventory special requirement update
    public void updateInventory(Long invId, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws InventoryNotExistException;
    
    //Handle inventory replacement within the same warehouse
    public void replaceInventory(Long invId, ArrayList<String> serialCodeList, String warehouseName, String ownerCompanyName)
            throws InventoryNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, ShelfIncorrectProductException;
    
    public ArrayList<Inventory> retrieveInflowInventoryList(String ownerCompanyName, String productName)
            throws InventoryNotExistException;
    
    public ArrayList<Inventory> retrieveOutflowInventoryList(String ownerCompanyName, String productName)
            throws InventoryNotExistException;
//    
//    //the inventory level of a selected product in a selected warehouse
//    public Double retrieveLocalInventoryLevel(String productOwnerCompanyName,Long warehouseId, String productName)
//            throws InventoryNotExistException;
//    
//    //a list of the inventory level of all product of a selected company in a selected warehouse
//    public HashMap<Product, Double> retrieveLocalInventoryLevelList(String productOwnerCompanyName,Long warehouseId)
//            throws InventoryNotExistException;
//    
//    //For warehouse owner company usage, view all products stored in this warehouse
//    public HashMap<Product, Double> retrieveLocalInventoryLevelWholeList(Long warehouseId)
//            throws InventoryNotExistException;
//    
////    //At company scale
////        //the total inventory level of a selected product in all warehouse
////    public Double retrieveCompanyInventoryLevel(String productOwnerCompanyName, String productName)
////            throws InventoryNotExistException;
//    
//        //A list of total inventory levels of all products in all warehouse
//    public HashMap<Product, Double> retrieveCompanyInventoryLevelList(String productOwnerCompanyName)
//            throws InventoryNotExistException;
    
    public void deleteInventory(Inventory inv);
    
    public void deleteProductInventory(String ownerCompanyName, String productName)
            throws InventoryNotExistException;

    public ArrayList<Shelf> getShelfListForSpecificProductInOneWarehouse(FinishedGood product, Warehouse warehouse) 
            throws ShelfNotExistException;

    public HashMap<Warehouse, Double> getWarehouseQuantityMapForSpecificProduct(String productName, String ownerCompanyName) 
            throws ProductNotExistException, ShelfNotExistException, MultipleProductWithSameNameException;

    public ArrayList<Inventory> retrieveInflowInventoryListForSpecificWarehouse(Warehouse warehouse) throws InventoryNotExistException;

    public ArrayList<Inventory> retrieveInflowInventoryListForSpecificWarehouseForCustomerCompany(Warehouse warehouse, Company ownerCompany) throws InventoryNotExistException;

    public ArrayList<Inventory> retrieveOutflowInventoryListForSpecificWarehouse(Warehouse warehouse) throws InventoryNotExistException;

    public ArrayList<Inventory> retrieveOutflowInventoryListForSpecificWarehouseForCustomerCompany(Warehouse warehouse, Company ownerCompany) throws InventoryNotExistException;

    public String getInventoryLocation(Inventory inv);
    
}
