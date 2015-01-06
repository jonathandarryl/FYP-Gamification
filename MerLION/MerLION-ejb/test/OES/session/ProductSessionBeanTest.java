/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import OES.entity.FinishedGood;
import OES.entity.Product;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductInventoryNotEmptyException;
import util.exception.ProductNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class ProductSessionBeanTest {

    ProductSessionBeanRemote psbr = this.lookupProductSessionBeanRemote();


    public ProductSessionBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test(expected = ProductAlreadyExistException.class)
    public void testCreateProduct() throws ProductAlreadyExistException, MultipleProductWithSameNameException, CompanyNotExistException, ProductNotExistException {
        String ownerCompanyName = "company1";
        String productName = "TV";
        Double productPrice = 200.00;
        String productDescription = "test";
        String unit = "box";
        Double quantityInOneUnitCapacity = 100.00;
        Double qunatityInOneBatch = 50.00;
        psbr.createProduct(ownerCompanyName, productName, productPrice, productDescription, unit, quantityInOneUnitCapacity, qunatityInOneBatch);
        FinishedGood result = psbr.retrieveProduct(ownerCompanyName, productName);
        assertNotNull(result);
    }
    
    @Test
    public void testUpdateProduct() throws ProductNotExistException, MultipleProductWithSameNameException {
        String ownerCompanyName = "company1";
        String productName = "TV";
        Double productPrice = 200.00;
        String productDescription = "test";
        String unit = "box";
        Double quantityInOneUnitCapacity = 20.00;
        double qunatityInOneBatch = 20.00;
        psbr.updateProduct(ownerCompanyName, productName, productPrice, productDescription, unit, quantityInOneUnitCapacity, qunatityInOneBatch);
        boolean result = false;
        if (psbr.retrieveProduct(ownerCompanyName, productName).getQuantityInOneBatch()==qunatityInOneBatch) {
            result=true;
        }
        assertTrue(result);
    }
    
    @Test
    public void testRetrieveProduct() throws Exception{
        String ownerCompanyName = "company1";
        String productName = "TV";
        FinishedGood result = psbr.retrieveProduct(ownerCompanyName, productName);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveProductList() throws Exception{
        String companyName = "company1";
        ArrayList<FinishedGood> result = psbr.retrieveProductList(companyName);
        assertNotNull(result);
    }
    
    @Test(expected = ProductNotExistException.class)
    public void testDeleteProduct() throws ProductNotExistException, MultipleProductWithSameNameException, ProductInventoryNotEmptyException {
        String ownerCompanyName = "company1";
        String productName = "testDelete";
        psbr.deleteProduct(ownerCompanyName, productName);
        FinishedGood result = psbr.retrieveProduct(ownerCompanyName, productName);
        assertNull(result);
    }
    
    @Test
    public void testRetrieveGeneralProduct() throws Exception {
        String ownerCompanyName = "company1";
        String productName = "Clock";
        Product result = psbr.retrieveGeneralProduct(ownerCompanyName, productName);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveAllProduct() throws Exception {
        String ownerCompanyName = "MalayMedi";
        ArrayList<Product> result = psbr.retrieveAllProduct(ownerCompanyName);
        assertNotNull(result);
    }

    private ProductSessionBeanRemote lookupProductSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ProductSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/ProductInfoManagement!OES.session.ProductSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}