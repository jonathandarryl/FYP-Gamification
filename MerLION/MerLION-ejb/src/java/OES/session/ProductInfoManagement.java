/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.FinishedGood;
import OES.entity.Product;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static junit.framework.TestCase.assertNotNull;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductInventoryNotEmptyException;
import util.exception.ProductNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class ProductInfoManagement implements ProductInfoManagementLocal, ProductSessionBeanRemote {

    @PersistenceContext
    EntityManager em;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;

    //Pre-condition: owner company must exist and the company name is unique
    @Override
    public void createProduct(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws ProductAlreadyExistException, MultipleProductWithSameNameException, CompanyNotExistException {
        FinishedGood fg = null;
        try {
            fg = retrieveProduct(ownerCompanyName, productName);
        } catch (ProductNotExistException ex) {
            System.out.println("No existing product with the same name as " + productName + ". New product creation proceeds.");
        }
        if (fg != null) {
            throw new ProductAlreadyExistException("Product " + productName + " already exist in Company " + ownerCompanyName + "'s product list. Creation failed.");
        }

        CustomerCompany ownerCompany = (CustomerCompany) cmsb.retrieveCustomerCompany(ownerCompanyName);
        assertNotNull(ownerCompany);

        FinishedGood product = new FinishedGood(productName, productPrice, productDescription, ownerCompany, "FinishedGood", unit, quantityInOneUnitCapacity, qunatityInOneBatch);
        ownerCompany.getProductList().add((FinishedGood) product);
        em.persist(product);
        em.merge(ownerCompany);
        System.out.println("Product added into the catalog successfully!");
    }

    @Override
    public void updateProduct(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws ProductNotExistException, MultipleProductWithSameNameException {
        FinishedGood product = retrieveProduct(ownerCompanyName, productName);

        if (product != null) {
            product.setProductDescription(productDescription);
            product.setProductName(productName);
            product.setProductPrice(productPrice);
            product.setUnit(unit);
            product.setQuantityInOneUnitCapacity(quantityInOneUnitCapacity);
            product.setQuantityInOneBatch(qunatityInOneBatch);
            em.merge(product);
            System.out.println("Product info update successful.");
        } else {
            System.out.println("No product named " + productName + " found");
        }
    }

    /**
     *
     * @param ownerCompanyName
     * @param productName
     * @return
     * @throws util.exception.ProductNotExistException
     * @throws util.exception.MultipleProductWithSameNameException
     */
    @Override
    public FinishedGood retrieveProduct(String ownerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException {
        Query query = em.createQuery("select f from FinishedGood f where f.productName=?1 and f.ownerCompanyName=?2 and f.archivedOrNot=FALSE");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<FinishedGood> finishedGoodList = new ArrayList(query.getResultList());

        if (finishedGoodList.isEmpty()) {
            throw new ProductNotExistException("Product " + productName + " does not exist in Company " + ownerCompanyName + "'s product list.");
        }

        if (finishedGoodList.size() > 1) {
            throw new MultipleProductWithSameNameException("Error! Multiple products with the same name under the same company exist!");
        }
        FinishedGood product = finishedGoodList.get(0);
        return product;
//        try {
//            Query query=em.createQuery("select f from FinishedGood f where f.productName=?1 and f.ownerCompanyName=?2 and f.archivedOrNot=FALSE");
//            query.setParameter(1, productName);
//            query.setParameter(2, ownerCompanyName);
//            ArrayList<FinishedGood>finishedGoodList=new ArrayList(query.getResultList());
//            FinishedGood product = finishedGoodList.get(0);
//            return product;
//        } catch(Exception e) {
//            throw new ProductNotExistException("Product "+productName+" does not exist in Company "+ownerCompanyName+"'s product list.");
//        }
    }

    @Override
    public Product retrieveGeneralProduct(String ownerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException {
        Query query = em.createQuery("select p from Product p where p.productName=?1 and p.ownerCompanyName=?2 and p.archivedOrNot=FALSE");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<Product> productList = new ArrayList(query.getResultList());

        if (productList.isEmpty()) {
            System.out.println("empty");
            throw new ProductNotExistException("Product " + productName + " does not exist in Company " + ownerCompanyName + "'s product list.");
        }

        if (productList.size() > 1) {
            System.out.println("not empty");
            throw new MultipleProductWithSameNameException("Error! Multiple products with the same name under the same company exist!");
        }
        Product product = productList.get(0);
        return product;
    }

    @Override
    public ArrayList<FinishedGood> retrieveProductList(String ownerCompanyName) throws ProductNotExistException {
        Query query = em.createQuery("select f from FinishedGood f where f.ownerCompanyName=?1 and f.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompanyName);
        ArrayList<FinishedGood> finishedGoodList = new ArrayList(query.getResultList());
        if (finishedGoodList.isEmpty()) {
            throw new ProductNotExistException("Company " + ownerCompanyName + "'s product list is empty.");
        }
        return finishedGoodList;
    }

    @Override
    public ArrayList<Product> retrieveAllProduct(String ownerCompanyName) throws ProductNotExistException {
        Query query = em.createQuery("select f from Product f where f.ownerCompanyName=?1 and f.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompanyName);
        ArrayList<Product> goodList = new ArrayList(query.getResultList());
        if (goodList.isEmpty()) {
            throw new ProductNotExistException("Company " + ownerCompanyName + "'s product list is empty.");
        }
        return goodList;
    }

    @Override
    public void deleteProduct(String ownerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, ProductInventoryNotEmptyException {
        FinishedGood product = retrieveProduct(ownerCompanyName, productName);
        CustomerCompany company = product.getOwnerCompany();
        if (product.getInventoryLevel() != 0.0) {
            throw new ProductInventoryNotEmptyException("Product " + productName + " deletion failed. Inventory for this product has not been emptyed yet.");
        }
        company.getProductList().remove(product);
        product.setArchivedOrNot(Boolean.TRUE);
        em.merge(company);
        em.merge(product);
        System.out.println("Product archived successfully!");

    }

    @Override
    public void makeProductPublic(String productName, String ownerCompanyName) throws ProductNotExistException, MultipleProductWithSameNameException {
        Query query = em.createQuery("select p from Product p where p.productName=?1 and p.ownerCompanyName=?2 and p.archivedOrNot=FALSE");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<Product> productList = new ArrayList(query.getResultList());

        if (productList.isEmpty()) {
            System.out.println("empty");
            throw new ProductNotExistException("Product " + productName + " does not exist in Company " + ownerCompanyName + "'s product list.");
        }

        if (productList.size() > 1) {
            System.out.println("not empty");
            throw new MultipleProductWithSameNameException("Error! Multiple products with the same name under the same company exist!");
        }
        Product product = productList.get(0);
        product.setPublicOrNot(Boolean.TRUE);
        em.merge(product);
    }

    @Override
    public ArrayList<Product> retrieveAllCompanyProduct() throws ProductNotExistException {
        Query query = em.createQuery("select f from Product f where f.archivedOrNot=FALSE and f.publicOrNot=TRUE");
        ArrayList<Product> goodList = new ArrayList(query.getResultList());
        if (goodList.isEmpty()) {
            throw new ProductNotExistException("Product list is empty");
        }
        return goodList;
    }

}
