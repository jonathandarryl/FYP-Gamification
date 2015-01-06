/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OES.session;

import MRPII.entity.MonthlyPlan;
import OES.entity.FinishedGood;
import OES.entity.Product;
import java.util.ArrayList;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.MonthlyPlanNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductInventoryNotEmptyException;
import util.exception.ProductNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface ProductInfoManagementLocal {

    /**
     *
     * @param ownerCompanyName
     * @param productName
     * @param productPrice
     * @param productDescription
     * @param unit
     * @param quantityInOneUnitCapacity
     * @param qunatityInOneBatch
     * @throws util.exception.ProductAlreadyExistException
     * @throws util.exception.MultipleProductWithSameNameException
     * @throws util.exception.CompanyNotExistException
     */
    public void createProduct(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws ProductAlreadyExistException, MultipleProductWithSameNameException, CompanyNotExistException;
    
    public void updateProduct(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws ProductNotExistException, MultipleProductWithSameNameException;
    
    public FinishedGood retrieveProduct(String ownerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException;
    
    public ArrayList<FinishedGood> retrieveProductList(String ownerCompanyName) 
            throws ProductNotExistException;
    
    public void deleteProduct(String ownerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, ProductInventoryNotEmptyException;

    public Product retrieveGeneralProduct(String ownerCompanyName, String productName) 
            throws ProductNotExistException, MultipleProductWithSameNameException;
            
    public ArrayList<Product> retrieveAllProduct(String ownerCompanyName) 
            throws ProductNotExistException;
    
    public ArrayList<Product> retrieveAllCompanyProduct() throws ProductNotExistException;
    
    public void makeProductPublic(String productName, String ownerCompanyName) 
            throws ProductNotExistException, MultipleProductWithSameNameException;
    
}
