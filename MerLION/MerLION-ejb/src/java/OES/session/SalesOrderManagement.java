/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.Company;
import Common.entity.ExternalCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Customer;
import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.entity.SalesQuotation;
import WMS.entity.ExtractionRequest;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;
import util.exception.SalesOrderAlreadyFulfilledException;
import util.exception.SalesOrderFinishedGoodtNotReadyException;
import util.exception.SalesOrderHasBeenSentException;
import util.exception.SalesOrderIsNotBackOrderException;
import util.exception.SalesOrderNotExistException;
import util.exception.SalesQuotationAlreadyEstablishedOrderException;
import util.exception.SalesQuotationNotApprovedException;
import util.exception.SalesQuotationNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class SalesOrderManagement implements SalesOrderManagementLocal, SalesOrderSessionBeanRemote {

    @PersistenceContext
    EntityManager em;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private CustomerSessionBeanLocal csbl;

    @Override
    public SalesQuotation createSalesQuotation(List<Product> productList, HashMap<Product, Double> productListMap, String customerName, String ownerCompanyName) {
        Customer customer = null;
        try {
            customer = csbl.retrieveCustomer(customerName, ownerCompanyName);
        } catch (CustomerNotExistException ex) {
            Logger.getLogger(SalesOrderManagement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MultipleCustomerWithSameNameException ex) {
            Logger.getLogger(SalesOrderManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double totalPrice = getTotalPrice(productListMap);
        SalesQuotation salesQuotation = new SalesQuotation(productList, totalPrice, productListMap, customer, ownerCompanyName);
        salesQuotation.setSalesQuotationDate(new Timestamp(System.currentTimeMillis()));
        em.persist(salesQuotation);
        return salesQuotation;
    }

    @Override
    public void updateSalesQuotation(List<Product> productList, HashMap<Product, Double> productListMap, String ownerCompanyName, String customerName, Long sqId)
            throws SalesQuotationNotExistException, CustomerNotExistException, MultipleCustomerWithSameNameException {
        Customer customer = null;
        try {
            customer = csbl.retrieveCustomer(customerName, ownerCompanyName);
        } catch (CustomerNotExistException | MultipleCustomerWithSameNameException ex) {
            Logger.getLogger(SalesOrderManagement.class.getName()).log(Level.SEVERE, null, ex);
        }

        SalesQuotation salesQuotation = retrieveSalesQuotation(sqId);
        salesQuotation.setCustomer(customer);
        salesQuotation.setProductList(productList);
        salesQuotation.setProductListMap(productListMap);
        salesQuotation.setTotalPrice(getTotalPrice(productListMap));
        salesQuotation.setSalesQuotationDate(new Timestamp(System.currentTimeMillis()));
        em.merge(salesQuotation);
        System.out.println("Sales quotation info update successful.");
    }

    @Override
    public SalesQuotation retrieveSalesQuotation(Long sqId) throws SalesQuotationNotExistException {
        SalesQuotation salesQuotation = em.find(SalesQuotation.class, sqId);
        if (salesQuotation == null) {
            throw new SalesQuotationNotExistException("SalesQuotation " + sqId + " not found!");
        }
        return salesQuotation;
    }

    @Override
    public ArrayList<SalesQuotation> retrieveSalesQuotationList(String ownerCompanyName) throws SalesQuotationNotExistException {
        Query query = em.createQuery("select s from SalesQuotation s where s.ownerCompanyName=?1 and s.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompanyName);
        ArrayList<SalesQuotation> salesQuotationList = new ArrayList(query.getResultList());
        if (salesQuotationList.isEmpty()) {
            throw new SalesQuotationNotExistException("No sales quotation under company " + ownerCompanyName + "found");
        }
        return salesQuotationList;
    }

    @Override
    public void approveSalesQuotation(Long sqId) throws SalesQuotationNotExistException {
        SalesQuotation salesQuotation = retrieveSalesQuotation(sqId);
        salesQuotation.setApprovedOrNot(Boolean.TRUE);
        em.merge(salesQuotation);
    }

    @Override
    public void approveSalesOrder(Long soId) throws SalesOrderNotExistException {
        SalesOrder salesOrder = retrieveSalesOrder(soId);
        salesOrder.setApprovedOrNot(Boolean.TRUE);
        em.merge(salesOrder);
    }

    @Override
    public SalesOrder createSalesOrder(Long sqId)
            throws SalesQuotationNotExistException, SalesQuotationNotApprovedException, SalesQuotationAlreadyEstablishedOrderException {
        SalesQuotation sq = retrieveSalesQuotation(sqId);
        if (!sq.isApprovedOrNot()) {
            throw new SalesQuotationNotApprovedException("Sales Quotation " + sqId + " has not been approved yet!");
        }
        if (sq.getSalesOrder() != null) {
            throw new SalesQuotationAlreadyEstablishedOrderException("Sales Quotation " + sqId + "has already established a sales order!");
        }
        SalesOrder so = new SalesOrder(sq);
        sq.setSalesOrder(so);
        em.persist(so);
        em.merge(sq);
        System.out.println("New Sales Order with id " + so.getId() + " has been created based on Quotation " + sq.getId());
        return so;
    }

//    @Override
//    public void updateSalesOrder(Long sqId, Long soId) {
//        SalesQuotation sq = retrieveSalesQuotation(sqId);
//        SalesOrder so = retrieveSalesOrder(soId);
//        if(sq==null)
//            System.out.println( "Failed. Sales quotation "+sqId+" is not found");
//        else if(so==null)
//            System.out.println("Failed. Sales quotation "+soId+" is not found");
//        else{
//            sq.setSalesOrder(so);
//            so.setSalesQuotation(sq);
//            em.persist(so);
//            em.persist(sq);
//            System.out.println( "New Sales Order with id "+soId+" has been created based on Quotation "+sqId);
//        }       
//    }
    @Override
    public SalesOrder retrieveSalesOrder(Long soId) throws SalesOrderNotExistException {
        SalesOrder salesOrder = em.find(SalesOrder.class, soId);
        if (salesOrder == null) {
            throw new SalesOrderNotExistException("Sales Order " + soId + " does not exist!");
        }
        return salesOrder;
    }

    @Override
    public ArrayList<SalesOrder> retrieveAllSalesOrderList(String companyName) throws SalesOrderNotExistException {
        Query query = em.createQuery("select s from SalesOrder s where s.ownerCompanyName=?3 and s.archivedOrNot=FALSE");
        query.setParameter(3, companyName);
        ArrayList<SalesOrder> salesOrderList = new ArrayList<>(query.getResultList());
        System.out.println("before if ");
        if (salesOrderList.isEmpty()) {
            System.out.println("in if ");
            throw new SalesOrderNotExistException("No sales order records found for " + companyName + "!");
        }
        return salesOrderList;
    }

    @Override
    public ArrayList<SalesOrder> retrieveSalesOrderList(Timestamp startDate, Timestamp endDate, String companyName) throws SalesOrderNotExistException {
        Query query = em.createQuery("select s from SalesOrder s where s.salesOrderDate between ?1 and ?2 and s.ownerCompanyName=?3 and s.archivedOrNot=FALSE");
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, companyName);
        ArrayList<SalesOrder> salesOrderList = new ArrayList<>(query.getResultList());
        System.out.println("before if ");
        if (salesOrderList.isEmpty()) {
            System.out.println("in if ");
            throw new SalesOrderNotExistException("No sales order records found for " + companyName + "!");
        }
        return salesOrderList;
    }

    @Override
    public void markBackOrder(Long soId) throws SalesOrderNotExistException {
        SalesOrder so = retrieveSalesOrder(soId);
        so.setIsBackOrder(Boolean.TRUE);
        em.merge(so);
        System.out.println("Sales order " + soId + " has been marked as back order");
    }

    @Override
    public List<SalesOrder> retrieveBackOrderList(String ownerCompanyName) throws SalesOrderNotExistException {
        Query query = em.createQuery("select s from SalesOrder s where s.ownerCompanyName=?1 and s.archivedOrNot=FALSE and s.isBackOrder=TRUE and s.isFulfilled=FALSE");
        query.setParameter(1, ownerCompanyName);
        ArrayList<SalesOrder> salesOrderList = new ArrayList<>(query.getResultList());
        if (salesOrderList.isEmpty()) {
            throw new SalesOrderNotExistException("No Back Order found for " + ownerCompanyName + "!");
        }
        return salesOrderList;
    }

    @Override
    public void updateBackOrder(Long soId) throws SalesOrderNotExistException, SalesOrderIsNotBackOrderException {
        SalesOrder so = retrieveSalesOrder(soId);

        if (so.isIsBackOrder()) {
            so.setIsBackOrder(Boolean.FALSE);
            em.merge(so);
            System.out.println("Back order " + soId + " has been fulfilled.");
        } else {
            throw new SalesOrderIsNotBackOrderException("Sales order " + soId + " is not a back order.");
        }

    }

    private Double getTotalPrice(Map<Product, Double> productList) {
        Double totalPrice = 0.0;
        for (Map.Entry entry : productList.entrySet()) {
            Product curItem = (Product) entry.getKey();
            Double quantity = (Double) entry.getValue();
            Double itemPrice = curItem.getProductPrice();
            totalPrice = totalPrice + quantity * itemPrice;
        }
        return totalPrice;
    }

    @Override
    public void fulfillSalesOrder(SalesOrder so)
            throws SalesOrderAlreadyFulfilledException, SalesOrderFinishedGoodtNotReadyException {       
        if (so.isIsFulfilled()) {
            throw new SalesOrderAlreadyFulfilledException("Fulfillmet failed. Sales Order " + so.getId() + " has already been fulfilled.");
        }
        if(!so.getProductReadyOrNot())
            throw new SalesOrderFinishedGoodtNotReadyException("Fulfillmet failed. Finished Goods for Sales Order " + so.getId() + " are not ready yet.");
        recordSalesRecord(so);
        so.setIsFulfilled(Boolean.TRUE);
        so.setIsBackOrder(Boolean.FALSE);
        em.merge(so);
    }

    private void recordSalesRecord(SalesOrder so) {
        SalesQuotation sq = so.getSalesQuotation();
        Map<Product, Double> productList = sq.getProductListMap();
        for (Map.Entry entry : productList.entrySet()) {
            Product curItem = (Product) entry.getKey();
            Double quantity = (Double) entry.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(so.getSalesOrderDate());
            Integer year = cal.get(Calendar.YEAR);
            Integer month = cal.get(Calendar.MONTH);
            cal.set(year, month, 1, 0, 0, 0);
            cal = eliminateMiliSecond(cal);
            Date target = new Date(cal.getTime().getTime());
            HashMap<Date, Double> monthlySalesRecord = curItem.getMonthlySalesRecord();
            if (monthlySalesRecord.get(target) != null) {
                Double newQuantity = quantity + monthlySalesRecord.get(target);
                monthlySalesRecord.put(target, newQuantity);
            } else {
                monthlySalesRecord.put(target, quantity);
            }
            curItem.setMonthlySalesRecord(monthlySalesRecord);
            em.merge(curItem);
        }
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

    @Override
    public List<ExtractionRequest> retrieveExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder) {
        List<ExtractionRequest> erList = salesOrder.getProductExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            Product curRm = (Product) curEr.getProduct();
            if (curRm.equals(product)) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder) {
        List<ExtractionRequest> erList = salesOrder.getProductExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            Product curRm = (Product) curEr.getProduct();
            if (curRm.equals(product) && curEr.getFulfilledOrNot()) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder) {
        List<ExtractionRequest> erList = salesOrder.getProductExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            Product curRm = (Product) curEr.getProduct();
            if (curRm.equals(product) && !curEr.getFulfilledOrNot()) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public Boolean checkOneProductReadyOrNot(Product product, SalesOrder salesOrder) {
        System.out.println("checkOneProductReadyOrNot: Checking finished good " + product.getProductName());
        Double neededQuantity = salesOrder.getProductQuantityMap().get(product);
        HashMap<Product, Boolean> materialReadyMap = salesOrder.getProductReadyOrNotMap();
        List<ExtractionRequest> erList = retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        Double arrivedQuantity = 0.0;

        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            arrivedQuantity = arrivedQuantity + curEr.getQuantity();
            if (arrivedQuantity >= neededQuantity) {
                materialReadyMap.put(product, Boolean.TRUE);
                salesOrder.setProductReadyOrNotMap(materialReadyMap);
                em.merge(salesOrder);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean checkAllProductReadyOrNot(SalesOrder salesOrder) {
        List<Product> productList = salesOrder.getProductList();
        HashMap<Product, Boolean> materialReadyOrNotMap = salesOrder.getProductReadyOrNotMap();

        for (Object o : productList) {
            Product product = (Product) o;
            if (!checkOneProductReadyOrNot(product, salesOrder)) {
                System.out.println("materialReadyOrNotMap.get(product): " + materialReadyOrNotMap.get(product).toString());
                System.out.println("Product " + product.getProductName() + " failed the check");
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public void confirmProductReadyForSalesOrder(SalesOrder salesOrder) {
        Long salesOrderId = salesOrder.getId();
        if (checkAllProductReadyOrNot(salesOrder)) {
            salesOrder.setProductReadyOrNot(Boolean.TRUE);
            em.merge(salesOrder);
            System.out.println("Auto-confiproduct SUCCESSFUL. All raw materials are ready for salesOrder " + salesOrderId);
        } else {
            System.out.println("Auto-confiproduct FAILED. All raw materials are NOT ready for salesOrder " + salesOrderId);
        }
    }

    @Override
    public void fulfillSalesOrder(Long soId) throws SalesOrderNotExistException, SalesOrderAlreadyFulfilledException, SalesOrderFinishedGoodtNotReadyException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fulfillSalesOrderForPop(SalesOrder so) {
        recordSalesRecord(so);
        so.setIsFulfilled(Boolean.TRUE);
        em.merge(so);
    }
    
}