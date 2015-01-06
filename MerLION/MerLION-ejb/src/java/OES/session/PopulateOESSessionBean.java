/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.CustomerCompany;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.FinishedGood;
import OES.entity.Invoice;
import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.entity.SalesQuotation;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.CustomerAlreadyExistException;
import util.exception.CustomerNotExistException;
import util.exception.InvoiceNotExistException;
import util.exception.MultipleCustomerWithSameNameException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductNotExistException;
import util.exception.SalesOrderAlreadyFulfilledException;
import util.exception.SalesOrderFinishedGoodtNotReadyException;
import util.exception.SalesOrderNotBeenFulfilledException;
import util.exception.SalesOrderNotExistException;
import util.exception.SalesQuotationAlreadyEstablishedOrderException;
import util.exception.SalesQuotationNotApprovedException;
import util.exception.SalesQuotationNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
@LocalBean
public class PopulateOESSessionBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ProductInfoManagementLocal pimsb;
    @EJB
    private SalesOrderManagementLocal som;
    @EJB
    private CustomerSessionBeanLocal csb;
    @EJB
    private InvoiceAndPaymentManagementLocal ipm;

    public Boolean isProductEmpty() {
        Query query = em.createQuery("SELECT s FROM FinishedGood s");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isSalesQuotEmpty() {
        Query query = em.createQuery("SELECT s FROM SalesQuotation s");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void populateDataList() {
        System.out.println("Populating product data...");
        this.populateCustomer("Challenger", "zhongwei210@gmail.com", "98116116", "Shenzhen Electronics", "Singapore", "Singapore", "Singapore", "Holland Road", "3", "249473");
        this.populateCustomer("SMU", "zhongwei210@gmail.com", "98116116", "company1", "Singapore", "Singapore", "Singapore", "Holland Road", "3", "249473");
        this.populateCustomer("SMU", "zhongwei210@gmail.com", "98116116", "Shenzhen Electronics", "Singapore", "Singapore", "Singapore", "Holland Road", "3", "249473");
        this.populateProduct("company1", "iPhone cases", 200.00, "iPhone cases", "set", 20.0, 50.0);
        this.populateProduct("company1", "Computer", 200.00, "computer", "set", 20.0, 50.0);
        this.populateProduct("company1", "TV", 300.00, "TV", "set", 20.0, 50.0);
        this.populateProduct("company1", "Washing machine", 400.00, "washing machine", "set", 20.0, 50.0);
        this.populateProduct("company1", "Clock", 100.00, "clock", "set", 20.0, 50.0);
        this.populateProduct("Shenzhen Electronics", "iPhone cases", 50.00, "iPhone cases", "piece", 1000.0, 1000.0);

        this.populateSpecialCase("Shenzhen Electronics", "iPhone cases");
        this.populateSpecialCase2("company1", "Computer");
        this.populateSpecialCase3("company1", "Computer");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 3400.0, "Challenger", "2011-01-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2900.0, "Challenger", "2012-01-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2800.0, "Challenger", "2013-01-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 3100.0, "Challenger", "2014-01-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1400.0, "Challenger", "2011-02-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1200.0, "Challenger", "2012-02-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1200.0, "Challenger", "2013-02-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1500.0, "Challenger", "2014-02-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2000.0, "Challenger", "2011-03-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1500.0, "Challenger", "2012-03-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1800.0, "Challenger", "2013-03-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1900.0, "Challenger", "2014-03-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1000.0, "Challenger", "2011-04-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1200.0, "Challenger", "2012-04-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1300.0, "Challenger", "2013-04-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 900.0, "Challenger", "2014-04-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2000.0, "Challenger", "2011-05-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1900.0, "Challenger", "2012-05-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2900.0, "Challenger", "2013-05-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2300.0, "Challenger", "2014-05-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1400.0, "Challenger", "2011-06-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2700.0, "Challenger", "2012-06-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2300.0, "Challenger", "2013-06-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2000.0, "Challenger", "2014-06-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2400.0, "Challenger", "2011-07-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1700.0, "Challenger", "2012-07-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 3300.0, "Challenger", "2013-07-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2500.0, "Challenger", "2014-07-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 4800.0, "Challenger", "2011-08-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2700.0, "Challenger", "2012-08-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2300.0, "Challenger", "2013-08-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 1500.0, "Challenger", "2014-08-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 3200.0, "Challenger", "2011-09-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 4800.0, "Challenger", "2012-09-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2300.0, "Challenger", "2013-09-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2200.0, "Challenger", "2011-10-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2500.0, "Challenger", "2012-10-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 4900.0, "Challenger", "2013-10-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 3000.0, "Challenger", "2011-11-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2500.0, "Challenger", "2012-11-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 4000.0, "Challenger", "2013-11-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2500.0, "Challenger", "2011-12-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 2000.0, "Challenger", "2012-12-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("Shenzhen Electronics", "iPhone cases", 4000.0, "Challenger", "2013-12-01 00:00:00.000000000");

        this.populateSalesOrderForSalesRecord("company1", "Computer", 3000.0, "SMU", "2011-11-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("company1", "Computer", 2500.0, "SMU", "2012-11-01 00:00:00.000000000");
        this.populateSalesOrderForSalesRecord("company1", "Computer", 4000.0, "SMU", "2013-11-01 00:00:00.000000000");
    }

    public void populateProduct(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch) {
        try {
            try {
                pimsb.createProduct(ownerCompanyName, productName, productPrice, productDescription, unit, quantityInOneUnitCapacity, qunatityInOneBatch);
            } catch (MultipleProductWithSameNameException ex) {
                System.out.println(ex.getMessage());
            } catch (ProductAlreadyExistException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println("populateProduct: After create product");
            pimsb.makeProductPublic(productName, ownerCompanyName);
            FinishedGood product = pimsb.retrieveProduct(ownerCompanyName, productName);
            ArrayList<Product> productList = new ArrayList();
            productList.add(product);
            HashMap<Product, Double> productListMap = new HashMap();
            productListMap.put(product, 20.0);
            SalesQuotation sQuot = som.createSalesQuotation(productList, productListMap, "SMU", ownerCompanyName);
            som.approveSalesQuotation(sQuot.getId());
            SalesOrder so = som.createSalesOrder(sQuot.getId());
            som.approveSalesOrder(so.getId());
            som.fulfillSalesOrderForPop(so);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotApprovedException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateCustomer(String name, String email, String contactNo, String ownerCompanyName, String country, String state, String city, String street, String blockNo, String postalCode) {
        Location loc = new Location(country, state, city, street, blockNo, postalCode);
        try {
            CustomerCompany ownerCompany = cmsbl.retrieveCustomerCompany(ownerCompanyName);
            csb.createCustomer(name, email, contactNo, loc, ownerCompany);
        } catch (CustomerAlreadyExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CustomerNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleCustomerWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateSpecialCase(String ownerCompanyName, String productName) {
        try {
            FinishedGood product = pimsb.retrieveProduct(ownerCompanyName, productName);
            ArrayList<Product> productList = new ArrayList();
            productList.add(product);
            HashMap<Product, Double> productListMap = new HashMap();
            productListMap.put(product, 1000.0);
            SalesQuotation sQuot = som.createSalesQuotation(productList, productListMap, "Challenger", ownerCompanyName);
            som.approveSalesQuotation(sQuot.getId());
            SalesOrder sOrder = som.createSalesOrder(sQuot.getId());
            som.approveSalesOrder(sOrder.getId());
            som.markBackOrder(sOrder.getId());
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotApprovedException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateSpecialCase2(String ownerCompanyName, String productName) {
        try {
            FinishedGood product = pimsb.retrieveProduct(ownerCompanyName, productName);
            ArrayList<Product> productList = new ArrayList();
            productList.add(product);
            HashMap<Product, Double> productListMap = new HashMap();
            productListMap.put(product, 1000.0);
            SalesQuotation sQuot = som.createSalesQuotation(productList, productListMap, "SMU", ownerCompanyName);
            som.approveSalesQuotation(sQuot.getId());
            SalesOrder sOrder = som.createSalesOrder(sQuot.getId());
            Timestamp date = Timestamp.valueOf("2014-10-01 00:00:00.000000000");
            sOrder.setSalesOrderDate(date);
            som.approveSalesOrder(sOrder.getId());
            som.fulfillSalesOrderForPop(sOrder);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotApprovedException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateSpecialCase3(String ownerCompanyName, String productName) {
        try {
            FinishedGood product = pimsb.retrieveProduct(ownerCompanyName, productName);
            ArrayList<Product> productList = new ArrayList();
            productList.add(product);
            HashMap<Product, Double> productListMap = new HashMap();
            productListMap.put(product, 1000.0);
            SalesQuotation sQuot = som.createSalesQuotation(productList, productListMap, "SMU", ownerCompanyName);
            som.approveSalesQuotation(sQuot.getId());
            SalesOrder sOrder = som.createSalesOrder(sQuot.getId());
            Timestamp date = Timestamp.valueOf("2014-09-01 00:00:00.000000000");
            sOrder.setSalesOrderDate(date);
            som.approveSalesOrder(sOrder.getId());
            som.fulfillSalesOrderForPop(sOrder);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotApprovedException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateSalesOrderForSalesRecord(String ownerCompanyName, String productName, Double quantity, String supplierName, String dateStr) {
        try {
            FinishedGood product = pimsb.retrieveProduct(ownerCompanyName, productName);
            ArrayList<Product> productList = new ArrayList();
            productList.add(product);
            HashMap<Product, Double> productListMap = new HashMap();
            productListMap.put(product, quantity);
            SalesQuotation sQuot = som.createSalesQuotation(productList, productListMap, supplierName, ownerCompanyName);
            som.approveSalesQuotation(sQuot.getId());
            SalesOrder sOrder = som.createSalesOrder(sQuot.getId());
            Timestamp date = Timestamp.valueOf(dateStr);
            sOrder.setSalesOrderDate(date);
            som.approveSalesOrder(sOrder.getId());
            som.fulfillSalesOrderForPop(sOrder);
            Invoice inv = ipm.createInvoice(sOrder.getId(), sOrder.getSalesQuotation().getCustomer().getLocation());
            String billNo = System.currentTimeMillis() + "";
            ipm.confirmPayment(inv.getId(), billNo);
            ipm.createReceipt(inv.getId());
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationNotApprovedException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SalesOrderNotBeenFulfilledException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvoiceNotExistException ex) {
            Logger.getLogger(PopulateOESSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
