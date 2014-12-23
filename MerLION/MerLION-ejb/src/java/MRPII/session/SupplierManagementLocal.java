/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import Common.entity.CustomerCompany;
import MRPII.entity.Supplier;
import OES.entity.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Local;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SupplierAlreadyExistException;
import util.exception.SupplierNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface SupplierManagementLocal {
    public void addSupplier(String supplierName, String email, CustomerCompany ownerCompany, List<Product> supplyProductList, HashMap<Product, Integer> lagTimeMap, List<String> allSupplyProductList) 
            throws SupplierAlreadyExistException;
       
    public Supplier retrieveSupplier(String supplierName, CustomerCompany ownerCompany)
            throws SupplierNotExistException;
    
    public List<Supplier> retrieveSupplierListForCompany(CustomerCompany ownerCompany) 
            throws SupplierNotExistException;
    
    public ArrayList<Supplier> retrieveSupplierListForMaterial(String ownerCompanyName, String materialName)
            throws SupplierNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
    
    public List<Supplier> retrieveSupplierListForProduct(String ownerCompanyName, String productName) 
            throws SupplierNotExistException, ProductNotExistException, MultipleProductWithSameNameException;
        
    public void removeSupplier(String supplierName, CustomerCompany ownerCompany)
            throws SupplierNotExistException;

    public void updateSupplierCommon(String supplierName, CustomerCompany ownerCompany) 
            throws SupplierNotExistException;

    public void addMaterialSupplier(Supplier supplier, CustomerCompany ownerCompany, Product rm, Integer lagTime) 
            throws SupplierNotExistException;

    public void removeMaterialSupplier(Supplier supplier, String ownerCompanyName, String materialName) 
            throws SupplierNotExistException, ProductNotExistException, MultipleProductWithSameNameException;    

    public void updateStrList(Supplier supplier,List<String> allSupplyProductList);
    
    public void deleteMaterial(Supplier supplier, Product selectedMaterial)
            throws RawMaterialNotExistException;
    
    public void addMaterial(Supplier supplier, Product selectedMaterial, Integer leadTime)
            throws RawMaterialAlreadyExistException;
 }   
