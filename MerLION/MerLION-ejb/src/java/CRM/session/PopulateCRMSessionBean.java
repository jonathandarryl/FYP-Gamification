/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceInvoice;
import CRM.entity.ServiceOrder;
import CRM.entity.ServiceQuotation;
import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.StorageRequest;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import WMS.session.StorageRequestSessionBeanLocal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
import util.exception.ServiceInvoiceAlreadyPaidException;
import util.exception.ServiceInvoiceNotExistException;
import util.exception.ServiceOrderAlreadyApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderStartAfterContractExpireException;
import util.exception.ServiceOrderStartBeforeContractCommmenceException;
import util.exception.ServiceQuotationAlreadyEstablishedContractException;
import util.exception.ServiceQuotationNotApprovedException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
@LocalBean
public class PopulateCRMSessionBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private MembershipSchemaMgtSessionBeanLocal msmsb;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ProductInfoManagementLocal pimsb;
    @EJB
    private ServiceQuotationManagementSessionLocal sqmsb;
    @EJB
    private ServiceContractManagementSessionLocal scmsb;
    @EJB
    private ServiceOrderManagementLocal som;
    @EJB
    private FacilityManagementLocal fm;
    @EJB
    private ServiceInvoiceAndPaymentManagementSessionLocal sip;
    @EJB
    private StorageRequestSessionBeanLocal srs;

    public Boolean isMembershipTierEmpty() {
        Query query = em.createQuery("SELECT m FROM MembershipSchema m");
        return query.getResultList().size() <= 0;
    }

    public Boolean isProductEmpty() {
        Query query = em.createQuery("SELECT p FROM Product p");
        return query.getResultList().size() <= 0;
    }

    public Boolean isServiceQuotationEmpty() {
        Query query = em.createQuery("SELECT s FROM ServiceQuotation s");
        return query.getResultList().size() <= 0;
    }

    public Boolean isServiceContractEmpty() {
        Query query = em.createQuery("SELECT s FROM ServiceContract s");
        return query.getResultList().size() <= 0;
    }

    public Boolean isServiceOrderEmpty() {
        Query query = em.createQuery("SELECT s FROM ServiceOrder s");
        return query.getResultList().size() <= 0;
    }
    
    public Boolean isInventoryEmpty() {
        Query query = em.createQuery("SELECT i FROM Inventory i");
        return query.getResultList().size() <= 0;
    }
    
    public void populateCRM() throws ProductAlreadyExistException {
        try {
            this.populateMembershipSchema("company2", "tier0", 0, 0.0);
            this.populateMembershipSchema("company2", "tier1", 3, 3.0);
            this.populateMembershipSchema("merlion", "bronze", 0, 0.0);
            this.populateMembershipSchema("merlion", "silver", 5, 5.0);
            this.populateMembershipSchema("merlion", "gold", 10, 10.0);
            this.populateMembershipSchema("Yunda", "bronze", 0, 0.0);
            this.populateMembershipSchema("Yunda", "silver", 5, 5.0);
            this.populateMembershipSchema("Yunda", "gold", 10, 10.0);

            this.populateProduct("company1", "oil", 20.0, "test", "set", 20.0, 20.0);
            this.populateProduct("Shenzhen Electronics", "iPhone cases", 50.0, "iPhone cases", "piece", 1000.0, 1000.0);
            this.populateProduct("FruitsExpress", "apple", 4.0, "apple", "kg", 1000.0, 1000.0);
            this.populateProduct("MalayMedi", "medicine", 50.0, "medicine", "box", 1000.0, 1000.0);
            this.populateProduct("GRNSOrder", "AggregatedOrder", 1.0, "AggregatedOrder", "unit", 1.0, 1.0);
            this.populateProduct("GRNSOrder", "StorageOrder", 1.0, "StorageOrder", "unit", 1.0, 1.0);
            this.populateProduct("GRNSOrder", "TransportOrder", 1.0, "TransportOrder", "unit", 1.0, 1.0);
            //this.populateProduct("Shenzhen Electronics", "Magnetic Button", 50.0, "medicine", "box", 1000.0, 1000.0);            

            //for final system release
            //change for warehouse owner name
            this.populateServiceQuotation("Yunda", "merlion", "Shenzhen Electronics", "iPhone cases", "for final release", "2014-10-01 00:00:00.000000000", "2016-10-10 00:00:00.000000000",
                    "10", "Yunda Warehouse", "250", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE");
            this.populateServiceQuotation("merlion", "Shenzhen Electronics", "Shenzhen Electronics", "iPhone cases", "for final release", "2014-10-01 00:00:00.000000000", "2016-10-10 00:00:00.000000000",
                    "10", "Warehouse Singapore", "250", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE");
            this.populateServiceQuotation("merlion", "FruitsExpress", "FruitsExpress", "apple", "for final release", "2014-10-08 00:00:00.000000000", "2016-10-08 00:00:00.000000000",
                    "10", "Warehouse Singapore", "260", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE");
            this.populateServiceQuotation("merlion", "MalayMedi", "MalayMedi", "medicine", "for final release", "2014-10-08 00:00:00.000000000", "2016-10-08 00:00:00.000000000",
                    "10", "Warehouse Singapore", "250", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE");
            //end for final system releasekljlkjkh

            this.populateServiceQuotation("company2", "company1", "company1", "oil", "for test", "2014-10-01 00:00:00.000000000", "2016-12-10 00:00:00.000000000",
                    "10", "warehouse storage", "250", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE");
            this.populateServiceQuotation("merlion", "company1", "company1", "oil", "for aggregation", "2014-10-01 00:00:00.000000000", "2016-12-10 00:00:00.000000000",
                    "10", "Warehouse Singapore", "250", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE");
            this.populateServiceQuotation("merlion", "company1", "company1", "oil", "for aggregation", "2014-10-01 00:00:00.000000000", "2016-12-10 00:00:00.000000000",
                    "10", "Warehouse Singapore", "240", "China", "Jiangsu", "5", "5", "5", "5", "UK", "England", "5", "5", "5", "5", "FALSE", "FALSE", "FALSE", "FALSE");

            this.populateServiceContract("merlion", "company1");
            this.populateServiceContract("merlion", "company1");
            this.populateServiceContract("company2", "company1");
            //for final system release
            this.populateServiceContract("Yunda", "merlion");
            this.populateServiceContract("merlion", "Shenzhen Electronics");
            this.populateServiceContract("merlion", "FruitsExpress");
            this.populateServiceContract("merlion", "MalayMedi");
            //end for final system release

            this.populateServiceOrder("company2", "company1", "company1", "oil", "test1", "2014-11-01 00:00:00.000000000", "2014-12-10 00:00:00.000000000", "1", "0", "0",
                    "10", "warehouse storage", "6000", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE", false);
            this.populateServiceOrder("merlion", "company1", "company1", "oil", "test2", "2014-11-01 00:00:00.000000000", "2014-12-10 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "6000", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE", false );
            this.populateServiceOrder("merlion", "company1", "company1", "oil", "test3", "2014-11-01 00:00:00.000000000", "2014-12-10 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "6000", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE", false );
            this.populateServiceOrder("merlion", "company1", "company1", "oil", "test4", "2014-11-03 00:00:00.000000000", "2014-12-11 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "6000", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE", false);
            this.populateServiceOrder("merlion", "company1", "company1", "oil", "test5", "2014-10-08 00:05:00.000000000", "2014-12-10 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "6000", "China", "Jiangsu", "5", "5", "5", "5", "UK", "England", "5", "5", "5", "5", "FALSE", "FALSE", "FALSE", "FALSE", false);
            this.populateServiceOrder("merlion", "company1", "company1", "oil", "test6", "2015-01-02 00:00:00.000000000", "2015-01-10 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "6000", "USA", "New York", "1", "1", "1", "1", "Singapore", "Singapore", "2", "2", "2", "2", "FALSE", "FALSE", "FALSE", "FALSE", false);
            //for final system release
            this.populateServiceOrder("Yunda", "merlion", "Shenzhen Electronics", "iPhone cases", "for final system release", "2014-11-10 00:00:00.000000000", "2014-11-30 00:00:00.000000000", "1", "0", "0",
                    "10", "Yunda Warehouse", "10000", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE", true);
            this.populateServiceOrder("merlion", "Shenzhen Electronics", "Shenzhen Electronics", "iPhone cases", "for final system release", "2014-11-01 00:00:00.000000000", "2014-12-10 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "10000", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE", true);
            this.populateServiceOrder("merlion", "FruitsExpress", "FruitsExpress", "apple", "for final system release", "2014-11-08 00:00:00.000000000", "2014-11-16 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "10000", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE", true);
            this.populateServiceOrder("merlion", "MalayMedi", "MalayMedi", "medicine", "for final system release", "2014-11-08 00:00:00.000000000", "2016-11-20 00:00:00.000000000", "1", "0", "0",
                    "10", "Warehouse Singapore", "10000", "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001", "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601", "FALSE", "FALSE", "FALSE", "FALSE", true);
            //end ofr final system release
        } catch (CompanyNotExistException | MultipleProductWithSameNameException | ServiceQuotationNotApprovedException | ServiceQuotationAlreadyEstablishedContractException | MembershipSchemaNotExistException | ServiceContractNotExistException | ServiceOrderStartAfterContractExpireException | ServiceOrderStartBeforeContractCommmenceException | ServiceContractNotSignedException | ProductNotExistException | ServiceOrderNotExistException | ServiceOrderAlreadyApprovedException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceQuotationNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void popInventory() {
        this.populateInventory("iPhone cases", "Shenzhen Electronics", 500.0, "Shenzhen Electronics Warehouse", "Shenzhen Electronics", "Zone A");
        this.populateInventory("Magnetic Button", "Shenzhen Electronics", 4000.0, "Shenzhen Electronics Warehouse", "Shenzhen Electronics", "Zone B");
        this.populateInventory("20cm*20cm Leather Piece", "Shenzhen Electronics", 4000.0, "Shenzhen Electronics Warehouse", "Shenzhen Electronics", "Zone B");
        this.populateInventory("50cm metal frame", "Shenzhen Electronics", 6000.0, "Shenzhen Electronics Warehouse", "Shenzhen Electronics", "Zone C");
        this.populateInventory("Plastic container", "Shenzhen Electronics", 4000.0, "Shenzhen Electronics Warehouse", "Shenzhen Electronics", "Zone C");
    }

    public void populateMembershipSchema(String companyName, String membershipTier, Integer boundaryPoint, Double discount) throws CompanyNotExistException {
        Company company = cmsbl.retrieveCompany(companyName);
        msmsb.createMembershipSchema(company.getId(), membershipTier, boundaryPoint, discount);
        System.out.println("\nPopulate membership schema");
    }

    public void populateProduct(String companyName, String productName, Double productPrice, String ProductDescription, String unit, Double capacity, Double batch) throws ProductAlreadyExistException, MultipleProductWithSameNameException, CompanyNotExistException {
        pimsb.createProduct(companyName, productName, productPrice, ProductDescription, unit, capacity, batch);
        System.out.println("\nPopulate product");
    }

    public void populateServiceQuotation(String pCompanyName, String cCompanyName, String oCompany, String productName, String description, String startTime, String endTime,
            String warehouseCapa, String warehouseName, String price, String sCountry, String sState, String sCity, String sStreet, String sBlockNo, String sPostal,
            String dCountry, String dState, String dCity, String dStreet, String dBlockNo, String dPostal, String per, String fla, String pha, String high) throws CompanyNotExistException, ServiceQuotationNotExistException, ProductNotExistException, MultipleProductWithSameNameException {
        Long partnerCompanyId = cmsbl.retrievePartnerCompany(pCompanyName).getId();
        System.out.println("Partner Company id" + partnerCompanyId);
        Long customerCompanyId = cmsbl.retrieveCompany(cCompanyName).getId();
        System.out.println("Customer Company Id " + customerCompanyId);
        Timestamp start = Timestamp.valueOf(startTime);
        Timestamp end = Timestamp.valueOf(endTime);
        Location source = new Location(sCountry, sState, sCity, sStreet, sBlockNo, sPostal);
        Location dest = new Location(dCountry, dState, dCity, dStreet, dBlockNo, dPostal);
        Boolean perish = Boolean.valueOf(per);
        Boolean flam = Boolean.valueOf(fla);
        Boolean phama = Boolean.valueOf(pha);
        Boolean highValue = Boolean.valueOf(high);
        Product prod = pimsb.retrieveProduct(oCompany, productName);
        Double quantity = Integer.parseInt(warehouseCapa) * prod.getQuantityInOneUnitCapacity();
        ServiceQuotation sq = sqmsb.addServiceQuotation(partnerCompanyId, customerCompanyId, description, start, end,
                Integer.parseInt(warehouseCapa), warehouseName, Double.parseDouble(price), source, dest, perish, flam, phama, highValue, prod, quantity);

        System.out.println("\nAfter quotation");
        sq.setApprovedOrNot(Boolean.TRUE);
        sq.setClientApprovedOrNot(Boolean.TRUE);
        sq.setEstablishedContractOrNot(Boolean.FALSE);
        sq.setArchivedOrNot(Boolean.FALSE);
        sq.setOrderNumber(20);
        sq.setStorageDurationDays(5);
        System.out.println("\nPopulate quotation");
    }

    public void populateServiceContract(String pCompanyName, String cCompanyName) throws ServiceQuotationNotExistException, CompanyNotExistException, ServiceQuotationNotApprovedException, ServiceQuotationAlreadyEstablishedContractException, MembershipSchemaNotExistException, ServiceContractNotExistException {
        ServiceQuotation sq = scmsb.retrieveSuitableServiceQuotationWithSpecificCompanyList(pCompanyName, cCompanyName).get(0);
        System.out.println("populateServiceContract: retrieveSuitableServiceQuotationWithSpecificCompanyList.size()=" + scmsb.retrieveSuitableServiceQuotationWithSpecificCompanyList(pCompanyName, cCompanyName).size());

        scmsb.createServiceContract(sq.getId());
        System.out.println("\nPopulate contract");
    }

    public void populateServiceOrder(String pCompanyName, String cCompanyName, String oCompanyName, String productName, String description, String startTime, String endTime, String t, String p, String v,
            String warehouseCapa, String warehouseName, String price, String sCountry, String sState, String sCity, String sStreet, String sBlockNo, String sPostal,
            String dCountry, String dState, String dCity, String dStreet, String dBlockNo, String dPostal, String per, String fla, String pha, String high, Boolean w) throws ServiceContractNotExistException, ServiceOrderStartAfterContractExpireException, ServiceOrderStartBeforeContractCommmenceException, ServiceContractNotSignedException, CompanyNotExistException, ProductNotExistException, MultipleProductWithSameNameException, ServiceOrderNotExistException, ServiceOrderAlreadyApprovedException {
//        Long partnerCompanyId = cmsbl.retrievePartnerCompany(pCompanyName).getId();
//        Long customerCompanyId = cmsbl.retrieveCustomerCompany(cCompanyName).getId();
        Timestamp start = Timestamp.valueOf(startTime);
        Timestamp end = Timestamp.valueOf(endTime);
        Location source = new Location(sCountry, sState, sCity, sStreet, sBlockNo, sPostal);
        Location dest = new Location(dCountry, dState, dCity, dStreet, dBlockNo, dPostal);
        Boolean perish = Boolean.valueOf(per);
        Boolean flam = Boolean.valueOf(fla);
        Boolean phama = Boolean.valueOf(pha);
        Boolean highValue = Boolean.valueOf(high);
        Product prod = pimsb.retrieveProduct(oCompanyName, productName);
        Double priceD = Double.parseDouble(price);
        ServiceContract sc = scmsb.retrieveServiceContractWithSpecificCompanyList(pCompanyName, cCompanyName).get(0);
        sc.setClientSignOrNot(Boolean.TRUE);
        em.merge(sc);
        Long warehouseId;
        try {
            warehouseId = fm.retrieveWarehouse(warehouseName, pCompanyName).getId();
            Long soId = som.createServiceOrder(pCompanyName, cCompanyName, start, end, priceD, description, prod,
                    1000.0, source, dest, warehouseId, Boolean.TRUE, w, perish, flam, phama, highValue, sc.getId());
            ServiceOrder so = som.retrieveServiceOrder(soId);
            Company client = so.getServiceContract().getClient();
            if(!client.getOwnedWarehouseList().isEmpty())
                so.setSourceWarehouse(client.getOwnedWarehouseList().get(0));

            if (!so.getServiceContract().getClient().getCompanyName().equals("company1")) {
                som.approveServiceOrder(so);
                ServiceInvoice si = so.getServiceInvoice();
                sip.createServicePayment(si.getId());
            }
            em.merge(so);

        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceInvoiceNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceInvoiceAlreadyPaidException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\nPopulate order");
    }

    public void populateInventory(String productName, String productOwnerName, Double quantity, String warehouseName, String warehouseOwnerName, String regionCode) {
        try {
            Product product = pimsb.retrieveGeneralProduct(productOwnerName, productName);
            Warehouse warehouse = fm.retrieveWarehouse(warehouseName, warehouseOwnerName);
            Company productOwnerCompany = cmsbl.retrieveCompany(productOwnerName);
            Company requestDealer = cmsbl.retrieveCompany(warehouseOwnerName);
            String requestSource = "Populate bean";
            StorageRequest sr = srs.createStorageRequestAndReturn(product, quantity, warehouse, productOwnerCompany, requestDealer, requestSource, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            srs.fulfillStorageRequest(sr, regionCode);
            System.out.println("\nPopulate Inventory for Product " + productName + " in Warehouse " + warehouseName);
        } catch (ProductNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleProductWithSameNameException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ShelfNotExistException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ShelfListInsufficientCapacityException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WarehouseSpecialRequirementException ex) {
            Logger.getLogger(PopulateCRMSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
