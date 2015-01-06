/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import MRPII.entity.PurchaseOrder;
import MRPII.entity.PurchaseQuotation;
import MRPII.entity.Supplier;
import OES.entity.Product;
import OES.entity.SalesQuotation;
import OES.session.ProductInfoManagementLocal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.PurchaseOrderAlreadyExistException;
import util.exception.PurchaseOrderHasBeenSentException;
import util.exception.PurchaseOrderNotExistException;
import util.exception.PurchaseQuotationNotApprovedException;
import util.exception.PurchaseQuotationNotExistException;
import util.exception.SalesQuotationNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.SupplierNotProvideProductException;

/**
 *
 * @author songhan
 */
@Stateless
public class PurchaseOrderManagement implements PurchaseOrderManagementLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private MaterialRequirementPlanningSessionLocal mrp;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private RawMaterialManagementLocal rmm;
    @EJB
    private SupplierManagementLocal sm;

    @Override
    public void createRawMaterialPurchaseQuotation(Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek,
            Company buyerCompany, String supplierName, String productName, Integer batchNumber)
            throws SupplierNotExistException, SupplierNotProvideProductException, ProductNotExistException, MultipleProductWithSameNameException {

        Supplier supplier = sm.retrieveSupplier(supplierName, (CustomerCompany) buyerCompany);
        Product product = pim.retrieveGeneralProduct(buyerCompany.getCompanyName(), productName);
        if (!supplier.getSupplyList().contains(product)) {
            throw new SupplierNotProvideProductException("Product " + product.getProductName() + " is not in Supplier " + supplierName + "'s supply List");
        }

        Integer leadTime = supplier.getLagTimeMap().get(product);
        Calendar arrivalDate = this.getFirstDayOfOneWeek(scheduledSentYear, scheduledSentMonth, scheduledSentWeek);
        System.out.println("createRawMaterialPurchaseQuotation: arrivalDate: " + arrivalDate.toString());
        Timestamp sentTime = new Timestamp(arrivalDate.getTimeInMillis());
        System.out.println("createRawMaterialPurchaseQuotation: Timestamp sentTime: " + sentTime.toString());
        arrivalDate.add(Calendar.WEEK_OF_MONTH, leadTime);
        Timestamp arrivalTime = new Timestamp(arrivalDate.getTimeInMillis());
        System.out.println("createRawMaterialPurchaseQuotation: Timestamp arrivalTime: " + sentTime.toString());
        Integer scheduledArrivalYear = arrivalDate.get(Calendar.YEAR);
        Integer scheduledArrivalMonth = arrivalDate.get(Calendar.MONTH) + 1;
        arrivalDate.setFirstDayOfWeek(Calendar.MONDAY);
        Integer scheduledArrivalWeek = arrivalDate.get(Calendar.WEEK_OF_MONTH);
        String purchaseType = "RawMaterial";

        PurchaseQuotation pq = new PurchaseQuotation(sentTime, scheduledSentYear, scheduledSentMonth, scheduledSentWeek,
                arrivalTime, scheduledArrivalYear, scheduledArrivalMonth, scheduledArrivalWeek,
                purchaseType, buyerCompany, supplier, product, batchNumber);
        em.persist(pq);
    }

    @Override
    public void createFinishedGoodPurchaseQuotation(Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek,
            Company buyerCompany, String supplierName, String producName, Integer batchNumber)
            throws SupplierNotExistException, SupplierNotProvideProductException, ProductNotExistException, MultipleProductWithSameNameException {

        Supplier supplier = sm.retrieveSupplier(supplierName, (CustomerCompany) buyerCompany);
        Product product = pim.retrieveGeneralProduct(buyerCompany.getCompanyName(), producName);
        if (!supplier.getSupplyList().contains(product)) {
            throw new SupplierNotProvideProductException("Product " + product.getProductName() + " is not in Supplier " + supplierName + "'s supply List");
        }

        Integer leadTime = supplier.getLagTimeMap().get(product);
        Calendar arrivalDate = this.getFirstDayOfOneWeek(scheduledSentYear, scheduledSentMonth, scheduledSentWeek);
        Timestamp sentTime = new Timestamp(arrivalDate.getTimeInMillis());
        arrivalDate.add(Calendar.WEEK_OF_MONTH, leadTime);
        Timestamp arrivalTime = new Timestamp(arrivalDate.getTimeInMillis());
        Integer scheduledArrivalYear = arrivalDate.get(Calendar.YEAR);
        Integer scheduledArrivalMonth = arrivalDate.get(Calendar.MONTH);
        arrivalDate.setFirstDayOfWeek(Calendar.MONDAY);
        Integer scheduledArrivalWeek = arrivalDate.get(Calendar.WEEK_OF_MONTH);
        String purchaseType = "FinishedGood";

        PurchaseQuotation pq = new PurchaseQuotation(sentTime, scheduledSentYear, scheduledSentMonth, scheduledSentWeek,
                arrivalTime, scheduledArrivalYear, scheduledArrivalMonth, scheduledArrivalWeek,
                purchaseType, buyerCompany, supplier, product, batchNumber);
        em.persist(pq);
    }

    @Override
    public List<PurchaseQuotation> retrieveAllPurchaseQuotationList(Company userCompany)
            throws PurchaseQuotationNotExistException {
        Query query = em.createQuery("select p from PurchaseQuotation p where p.buyerCompany=?1");
        query.setParameter(1, userCompany);
        ArrayList<PurchaseQuotation> purchaseQuotationList = new ArrayList(query.getResultList());
        if (purchaseQuotationList.isEmpty()) {
            throw new PurchaseQuotationNotExistException("No Purchase Quotation under company " + userCompany.getCompanyName() + "found");
        }
        return purchaseQuotationList;
    }

    private Calendar getFirstDayOfOneWeek(Integer year, Integer month, Integer week) {
        Calendar result = Calendar.getInstance();
        result.set(year, month - 1, 1, 0, 0, 0);
        System.out.println("getFirstDayOfOneWeek: start: " + result.toString());
        Integer encounterMonday = 0;

        while (encounterMonday < week) {
            if (result.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                encounterMonday++;
            }
            if (Objects.equals(encounterMonday, week)) {
                System.out.println("getFirstDayOfOneWeek: Week " + week + " in Month " + month + " found: " + result.toString());
                break;
            }
            result.add(Calendar.DAY_OF_MONTH, 1);
        }
        System.out.println("getFirstDayOfOneWeek: Before eliminateMiliSecond: " + result.toString());
        result = eliminateMiliSecond(result);
        System.out.println("getFirstDayOfOneWeek: After eliminateMiliSecond: " + result.toString());
        return result;
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

    @Override
    public PurchaseQuotation retrievePurchaseQuotation(Long pqId)
            throws PurchaseQuotationNotExistException {
        PurchaseQuotation purchaseQuotation = em.find(PurchaseQuotation.class, pqId);
        if (purchaseQuotation == null) {
            throw new PurchaseQuotationNotExistException("PurchaseQuotation " + pqId + " does not exist!");
        }
        return purchaseQuotation;
    }

    @Override
    public void createPurchaseOrder(Long pqId)
            throws PurchaseQuotationNotExistException, PurchaseQuotationNotApprovedException {
        PurchaseQuotation pq = retrievePurchaseQuotation(pqId);
        if (!pq.isApprovedOrNot()) {
            throw new PurchaseQuotationNotApprovedException("Purchase Quotation " + pqId + " has not been approved yet! Purchase order creation failed.");
        }
        if (pq != null) {
            PurchaseOrder po = new PurchaseOrder(pq);
            pq.setPurchaseOrder(po);
            em.persist(po);
            em.merge(pq);
            System.out.println("New Purchase Order with id " + po.getId() + " has been created based on Quotation " + pq.getId());
        } else {
            System.out.println("Failed. Purchase quotation " + pqId + " is not found");
        }
    }
//
//    @Override
//    public String updatePurchaseOrder(Long pqId, Long poId) {
//        PurchaseQuotation pq = retrievePurchaseQuotation(pqId);
//        PurchaseOrder po = retrievePurchaseOrder(poId);
//        if(pq==null)
//            return "Failed. Purchase quotation "+pqId+" is not found";
//        else if(po==null)
//            return "Failed. Purchase order "+poId+" is not found";
//        else{
//            pq.setPurchaseOrder(po);
//            po.setPurchaseQuotation(pq);
//            em.persist(po);
//            em.persist(pq);
//            return "New Purchase Order with id "+poId+" has been created based on Quotation "+pqId;
//        }       
//    }

    @Override
    public PurchaseOrder retrievePurchaseOrder(Long pgoId) throws PurchaseOrderNotExistException {
        PurchaseOrder purchaseOrder = em.find(PurchaseOrder.class, pgoId);
        if (purchaseOrder == null) {
            throw new PurchaseOrderNotExistException("Purchase order " + pgoId + " does not exist!");
        }
        return purchaseOrder;
    }

    @Override
    public ArrayList<PurchaseQuotation> retrieveGoodsPurchaseQuotationList(String companyName) throws PurchaseQuotationNotExistException {
        Query query = em.createQuery("select p from PurchaseQuotation p where p.buyerCompany.companyName=?3 and p.purchaseType='FinishedGood'");
        query.setParameter(3, companyName);
        ArrayList<PurchaseQuotation> goodsPurchaseQuotationList = new ArrayList(query.getResultList());
        if (goodsPurchaseQuotationList.isEmpty()) {
            throw new PurchaseQuotationNotExistException("No finished goods purchase quotation records found for Company " + companyName);
        }
        return goodsPurchaseQuotationList;
    }

    @Override
    public ArrayList<PurchaseQuotation> retrieveRawMaterialPurchaseQuotationList(String companyName) throws PurchaseQuotationNotExistException {
        Query query = em.createQuery("select p from PurchaseQuotation p where p.buyerCompany.companyName=?3 and p.purchaseType='RawMaterial'");
        query.setParameter(3, companyName);
        ArrayList<PurchaseQuotation> rawMaterialPurchaseQuotationList = new ArrayList(query.getResultList());
        if (rawMaterialPurchaseQuotationList.isEmpty()) {
            throw new PurchaseQuotationNotExistException("No raw material purchase quotation records found for Company " + companyName);
        }
        return rawMaterialPurchaseQuotationList;
    }

    @Override
    public ArrayList<PurchaseOrder> retrieveGoodsPurchaseOrderList(String companyName) throws PurchaseOrderNotExistException {
        Query query = em.createQuery("select p from PurchaseOrder p where p.ownerCmpanyName=?3 and p.purchaseType='FinishedGood'");
        query.setParameter(3, companyName);
        ArrayList<PurchaseOrder> GoodsPurchaseOrderList = new ArrayList(query.getResultList());
        if (GoodsPurchaseOrderList.isEmpty()) {
            throw new PurchaseOrderNotExistException("No finished goods purchase order records found for Company " + companyName);
        }
        return GoodsPurchaseOrderList;
    }

    @Override
    public ArrayList<PurchaseOrder> retrieveMaterialPurchaseOrderList(String companyName) throws PurchaseOrderNotExistException {
        Query query = em.createQuery("select p from PurchaseOrder p where p.ownerCmpanyName=?3 and p.purchaseType='RawMaterial'");
        query.setParameter(3, companyName);
        ArrayList<PurchaseOrder> GoodsPurchaseOrderList = new ArrayList(query.getResultList());
        if (GoodsPurchaseOrderList.isEmpty()) {
            throw new PurchaseOrderNotExistException("No raw material purchase order records found for Company " + companyName);
        }
        return GoodsPurchaseOrderList;
    }

    @Override
    public void confirmPurchaseOrderArrival(PurchaseOrder po) throws PurchaseOrderAlreadyExistException {
        if (po.getArrivedOrNot()) {
            throw new PurchaseOrderAlreadyExistException("Arrival of this PurchaseOrder has already been confirmed.");
        }
        Product product = po.getPurchaseQuotation().getProduct();
        po.setArrivedOrNot(Boolean.TRUE);
        em.merge(po);
    }

    @Override
    public void approvePurchaseQuotation(Long puId) throws PurchaseQuotationNotExistException {
        PurchaseQuotation purQuotation = retrievePurchaseQuotation(puId);
        purQuotation.setApprovedOrNot(Boolean.TRUE);
        em.merge(purQuotation);
    }

    @Override
    public void sendOrder(PurchaseOrder pOrder) throws PurchaseOrderHasBeenSentException {
        if (pOrder.isSendOrNot()) {
            throw new PurchaseOrderHasBeenSentException("Failed. Purchase Order " + pOrder.getId() + " has already been sent to the supplier.");
        }
        pOrder.setSendOrNot(Boolean.TRUE);
        em.merge(pOrder);
        System.out.println("Sales order " + pOrder.getId() + " has been marked as back order");
    }
}
