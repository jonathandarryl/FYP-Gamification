/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WMS.session;

import CRM.entity.ServiceQuotation;
import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Location;
import OES.entity.Product;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ShelfAlreadyExistException;
import util.exception.ShelfNotExistException;
import util.exception.ShelfOccupiedException;
import util.exception.WarehouseAlreadyExistException;
import util.exception.WarehouseNotEmptyException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface FacilityManagementLocal {
    public void createWarehouse(String warehouseName, String contactNo, String ownerCompanyName, Location location, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Double pricePerCapacityPerDay)
            throws CompanyNotExistException, WarehouseAlreadyExistException;
    
    public void disgardShelfSet(String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException, WarehouseNotEmptyException;
    
    public void updateWarehouseCommom(String warehouseName,  String ownerCompanyName, String newWarehouseName, String contactNo, Location location, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws CompanyNotExistException, WarehouseNotExistException;
    
    public void updateWarehouseOwner(String warehouseName,  String ownerCompanyName, String newOwnerCompanyName)
            throws CompanyNotExistException, WarehouseNotExistException;
    
    public Warehouse retrieveWarehouse(String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException;
    
    public Warehouse retrieveWarehouse(Long warehouseId)
            throws WarehouseNotExistException;
    
    public List<Warehouse> retrieveOutsourcingWarehouseList(String ownerComapnyName) 
            throws CompanyNotExistException, ServiceContractNotExistException;
    
    public List<Warehouse> retrieveWarehouseListForSpecificRawMaterial(String productName, String productOwnerName) 
            throws CompanyNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;

    public List<Warehouse> retrieveWarehouseListForSpecificFinishedGood(String productName, String productOwnerName) 
            throws CompanyNotExistException, ProductNotExistException, MultipleProductWithSameNameException;

    
    public void deleteWarehouse(String warehouseName, String ownerCompanyName)
            throws CompanyNotExistException, WarehouseNotExistException, WarehouseNotEmptyException;
    
    public Integer companyOwnedWarehouseCapacity(Company partnerCompany);
    
    public void createShelf(String regionCode, Integer number, String serialCode, Integer capacity, String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException, ShelfAlreadyExistException;
//    
//    public void updateShelf(String serialCode, String warehouseName, String ownerCompanyName, Integer newCapacity)
//            throws WarehouseNotExistException, ShelfNotExistException;
//    
//    public void updateShelfList(String regionCode, Integer startNum, Integer endNum, String warehouseName, String ownerCompanyName, Integer newCapacity) 
//            throws WarehouseNotExistException, ShelfNotExistException;
//
//    
//    public void updateShelfList(ArrayList<String> serialCode, String warehouseName, String ownerCompanyName, ArrayList<String> newSerialCode, ArrayList<Integer> newCapacity)
//            throws WarehouseNotExistException, ShelfNotExistException;
    
    public Shelf retrieveShelf(String serialCode, String warehouseName, String ownerCompanyName)
            throws ShelfNotExistException;    
    
    public List<Shelf> retrieveShelfList(ArrayList<String> serialCodeList, String warehouseName, String ownerCompanyName)
            throws ShelfNotExistException;
    
    public void deleteShelf(String serialCode, String warehouseName, String ownerCompanyName)
            throws ShelfNotExistException, ShelfOccupiedException, WarehouseNotExistException;

    public Boolean verifyServiceQuotation(ServiceQuotation sq, Double bufferRatio);

    public List<Shelf> retrieveShelfListInSpecificWarehouse(String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfNotExistException;

    public List<Shelf> retrieveShelfListForSpecificProductInSpecificWarehouse(Warehouse warehouse, Product product) 
            throws ShelfNotExistException;
    
    public List<Shelf> retrieveShelfListForSpecificProductInSpecificWarehouse(String warehouseName, String ownerCompanyName, String productName, String productOwnerName) 
            throws ShelfNotExistException, WarehouseNotExistException, ProductNotExistException, MultipleProductWithSameNameException;
    

    public void createShelfListForSpecificWarehouse(String regionCode, Integer shelvesNum, Integer capacity, String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException, ShelfAlreadyExistException;    

    public Integer getRegionShelfNumber(String warehouseName, String warehouseOwnerName, String regionCode) 
            throws WarehouseNotExistException, ShelfNotExistException;

    public Integer getRegionCapacity(String warehouseName, String warehouseOwnerName, String regionCode) 
            throws WarehouseNotExistException, ShelfNotExistException;

    public Integer getRegionSpareCapacity(String warehouseName, String warehouseOwnerName, String regionCode)
            throws WarehouseNotExistException, ShelfNotExistException;

    public List<Shelf> retrieveShelfListInSpecificRegionInSpecificWarehouse(String regionCode, String warehouseName, String ownerCompanyName) throws WarehouseNotExistException, ShelfNotExistException;

    public void deleteShelvesInOneRegion(String regionCode, String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfNotExistException, ShelfOccupiedException;

    public List<Warehouse> retrieveWarehouseListForSpecificProduct(String productName, String productOwnerName) throws CompanyNotExistException, ProductNotExistException, MultipleProductWithSameNameException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;

    public List<Warehouse> retrieveOwnedWarehouseList(Company ownerCompany);
    
    }
