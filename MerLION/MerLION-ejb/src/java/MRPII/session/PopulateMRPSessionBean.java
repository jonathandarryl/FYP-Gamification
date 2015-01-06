/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SupplierAlreadyExistException;
import util.exception.SupplierNotExistException;
import util.exception.SupplierNotProvideProductException;

/**
 *
 * @author Zhong Wei
 */
@Stateless
@LocalBean
public class PopulateMRPSessionBean {

    @EJB
    RawMaterialManagementLocal rmm;
    @EJB
    SupplierManagementLocal sml;
    @EJB
    CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    MaterialRequirementPlanningSessionLocal mrp;
    @EJB
    PurchaseOrderManagementLocal poml;
    @EJB
    ProductInfoManagementLocal pim;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    EntityManager em;

    public Boolean isRawMaterialEmpty() {
        Query query = em.createQuery("SELECT w FROM RawMaterial w");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void populateDataList() {
        this.populateRawMaterial("Shenzhen Electronics", "Magnetic Button", 1.00, "magnetic button", "piece", 500.0, 500.0);
        this.populateRawMaterial("Shenzhen Electronics", "20cm*20cm Leather Piece", 1.00, "20cm*20cm Leather Piece", "set", 500.0, 500.0);
        this.populateRawMaterial("Shenzhen Electronics", "50cm metal frame", 1.00, "50cm metal frame", "piece", 500.0, 500.0);
        this.populateRawMaterial("Shenzhen Electronics", "Plastic container", 1.00, "Plastic container", "set", 500.0, 500.0);
        this.populateSupplier("Foxconn", "Shenzhen Electronics");
        this.populateSecondSupplier("PerfectCase", "Shenzhen Electronics");
        this.populateBOM("Shenzhen Electronics", "iPhone cases");
        this.populateRawMaterial("company1", "Magnetic Button", 1.00, "magnetic button", "piece", 500.0, 500.0);
        this.populateRawMaterial("company1", "20cm*20cm Leather Piece", 1.00, "20cm*20cm Leather Piece", "set", 500.0, 500.0);
        this.populateRawMaterial("company1", "50cm metal frame", 1.00, "50cm metal frame", "piece", 500.0, 500.0);
        this.populateRawMaterial("company1", "Plastic container", 1.00, "Plastic container", "set", 500.0, 500.0);
        this.populateSupplier("Foxconn", "company1");
        this.populateBOM("company1", "iPhone cases");
        this.populateRawMaterialPurchaseQuotation(2014, 12, 1, "Shenzhen Electronics", "Foxconn", "magnetic button", 10);
        this.populateRawMaterialPurchaseQuotation(2014, 12, 1, "Shenzhen Electronics", "Foxconn", "20cm*20cm Leather Piece", 10);
        this.populateRawMaterialPurchaseQuotation(2014, 12, 1, "Shenzhen Electronics", "Foxconn", "50cm metal frame", 10);
        this.populateRawMaterialPurchaseQuotation(2014, 12, 1, "Shenzhen Electronics", "Foxconn", "Plastic container", 10);
    }

    public void populateRawMaterial(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch) {
        try {
            rmm.createRawMaterial(ownerCompanyName, productName, productPrice, productDescription, unit, quantityInOneUnitCapacity, qunatityInOneBatch);
            /*      RawMaterial material=rmm.retrieveRawMaterial(ownerCompanyName, productName);
             List<Product> productList=new ArrayList();
             HashMap<Product, Integer> leadTimeMap=new HashMap();
             List<String> supplyStr=new ArrayList();
             productList.add(material);
             leadTimeMap.put(material, 2);
             supplyStr.add("car");
             CustomerCompany cc=cmsbl.retrieveCustomerCompany(ownerCompanyName);    
             sml.addSupplier(supplierName, cc, productList, leadTimeMap, supplyStr);    */
        } catch (RawMaterialAlreadyExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateSupplier(String supplierName, String ownerCompanyName) {
        try {
            RawMaterial magneticButton = rmm.retrieveRawMaterial(ownerCompanyName, "Magnetic Button");
            RawMaterial leather = rmm.retrieveRawMaterial(ownerCompanyName, "20cm*20cm Leather Piece");
            RawMaterial metal = rmm.retrieveRawMaterial(ownerCompanyName, "50cm metal frame");
            RawMaterial container = rmm.retrieveRawMaterial(ownerCompanyName, "Plastic container");
            List<Product> productList = new ArrayList();
            HashMap<Product, Integer> leadTimeMap = new HashMap();
            List<String> supplyStr = new ArrayList();
            productList.add(magneticButton);
            productList.add(leather);
            productList.add(metal);
            productList.add(container);
            leadTimeMap.put(magneticButton, 2);
            leadTimeMap.put(leather, 2);
            leadTimeMap.put(metal, 2);
            leadTimeMap.put(container, 2);
            supplyStr.add("car");
            CustomerCompany cc = cmsbl.retrieveCustomerCompany(ownerCompanyName);
            sml.addSupplier(supplierName,"zhongwei210@gmail.com", cc, productList, leadTimeMap, supplyStr);
        } catch (RawMaterialNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleRawMaterialWithSameNameException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SupplierAlreadyExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void populateSecondSupplier(String supplierName, String ownerCompanyName) {
        try {
            FinishedGood iphone=pim.retrieveProduct(ownerCompanyName, "iPhone cases");
            List<Product> productList = new ArrayList();
            HashMap<Product, Integer> leadTimeMap = new HashMap();
            List<String> supplyStr = new ArrayList();
            productList.add(iphone);
            leadTimeMap.put(iphone, 2);
            supplyStr.add("car");
            CustomerCompany cc = cmsbl.retrieveCustomerCompany(ownerCompanyName);
            sml.addSupplier(supplierName,"zhongwei210@gmail.com", cc, productList, leadTimeMap, supplyStr);
        } catch (SupplierAlreadyExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateBOM(String ownerCompanyName, String productName) {
        try {
            RawMaterial magneticButton = rmm.retrieveRawMaterial(ownerCompanyName, "Magnetic Button");
            RawMaterial leather = rmm.retrieveRawMaterial(ownerCompanyName, "20cm*20cm Leather Piece");
            RawMaterial metal = rmm.retrieveRawMaterial(ownerCompanyName, "50cm metal frame");
            RawMaterial container = rmm.retrieveRawMaterial(ownerCompanyName, "Plastic container");
            List<RawMaterial> productList = new ArrayList();
            productList.add(magneticButton);
            productList.add(leather);
            productList.add(metal);
            productList.add(container);
            ArrayList<Double> quantityList=new ArrayList();
            quantityList.add(2.0);
            quantityList.add(1.0);
            quantityList.add(1.0);
            quantityList.add(1.0);
            mrp.createBillOfMaterial(ownerCompanyName, productName, productList, quantityList);
        } catch (RawMaterialNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleRawMaterialWithSameNameException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateMRPSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void populateRawMaterialPurchaseQuotation(Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek, 
            String buyerCompanyName, String supplierName, String producName, Integer batchNumber){
        try {
            Company buyerCompany = cmsbl.retrieveCompany(buyerCompanyName);
            poml.createRawMaterialPurchaseQuotation(scheduledSentYear, scheduledSentMonth, scheduledSentWeek, buyerCompany, supplierName, producName, batchNumber);
        } catch (CompanyNotExistException ex) {
            System.out.println("populateRawMaterialPurchaseQuotation: CompanyNotExistException");
        } catch (SupplierNotExistException ex) {
            System.out.println("populateRawMaterialPurchaseQuotation: SupplierNotExistException");
        } catch (SupplierNotProvideProductException ex) {
            System.out.println("populateRawMaterialPurchaseQuotation: SupplierNotProvideProductException");
        } catch (ProductNotExistException ex) {
            System.out.println("populateRawMaterialPurchaseQuotation: ProductNotExistException");
        } catch (MultipleProductWithSameNameException ex) {
            System.out.println("populateRawMaterialPurchaseQuotation: MultipleProductWithSameNameException");
        }        
    }
    
    
}
