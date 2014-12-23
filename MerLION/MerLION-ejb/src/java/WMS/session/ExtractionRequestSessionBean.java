/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.ServiceOrder;
import CRM.entity.WarehouseOrder;
import Common.entity.Company;
import MRPII.entity.Production;
import MRPII.entity.RawMaterial;
import MRPII.session.ProductionSessionBeanLocal;
import OES.entity.FinishedGood;
import OES.entity.Invoice;
import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.session.SalesOrderManagementLocal;
import TMS.entity.TransOrder;
import WMS.entity.ExtractionRequest;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ExtractionQuantityExceedProductionNeedException;
import util.exception.ExtractionQuantityExceedSalesOrderNeedException;
import util.exception.ExtractionRequestNotExistException;
import util.exception.InsufficientStorageInWarehouseException;
import util.exception.ShelfIncorrectProductException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Stateless
public class ExtractionRequestSessionBean implements ExtractionRequestSessionBeanLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private InventoryFlowManagementLocal fgi;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private ProductionSessionBeanLocal psb;
    @EJB
    private SalesOrderManagementLocal som;
    
    @Override
    public void createExtractionRequest(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer, String requestSource){
        ExtractionRequest er = new ExtractionRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestSource);
        em.persist(er);
    }
      
    @Override
    public void dealWithWarehouseOrder(WarehouseOrder wo){
        Product product = wo.getServiceOrder().getProduct();
        Double quantity = wo.getServiceOrder().getQuantity();
        Warehouse warehouse = wo.getWarehouse();
        Company requestInitiator = wo.getServiceOrder().getServiceContract().getServiceQuotation().getClientCompany();
        Company requestDealer = wo.getServiceOrder().getServiceContract().getServiceQuotation().getpCompany();
        String requestSource = "WarehouseOrder";
        ExtractionRequest er = new ExtractionRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestSource);
        em.persist(er);
    }
    
    @Override
    public void dealWithTransOrderExtraction(TransOrder to) 
            throws WarehouseNotExistException{
        ServiceOrder so = to.getServiceOrder();
        Product product = so.getProduct();
        Double quantity = so.getQuantity();
        Warehouse warehouse = so.getSourceWarehouse();
        Company requestDealer = product.getOwnerCompany();
        Company requestInitiator = so.getServiceContract().getProvider();
        String requestSource = "Transportation Order Extraction";
        this.createExtractionRequest(product, quantity, warehouse, requestInitiator, requestDealer, requestSource);     
        System.out.println("Finished ExtractionRequestSessionBean: dealWithTransOrderExtraction for TransOrder "+to.getTransOrderId());
    }
    
    //For each rawmaterial in the BOM, we need to create one extraction request
    @Override
    public void dealWithProductionRawMaterialExtraction(Product rawMaterial, Double quantity, Warehouse warehouse, Production production)
            throws ExtractionQuantityExceedProductionNeedException{
        Double totalNeedQuantity = production.getRawMaterialQuantityMap().get((RawMaterial)rawMaterial);
        if(quantity>totalNeedQuantity+1)
            throw new ExtractionQuantityExceedProductionNeedException("Request unable to be sent. This production process only needs "+totalNeedQuantity+" "+rawMaterial.getUnit()+" "+rawMaterial.getProductName()+"!");
        Company producer = rawMaterial.getOwnerCompany();
        String requestSource = "Production";
        ExtractionRequest er = new ExtractionRequest(rawMaterial, quantity, warehouse, producer, producer, requestSource);
        er.setProduction(production);        
        em.persist(er);
        production.getRawMaterialExtractionRequestList().add(er);
        em.merge(production);
        System.out.println("Production Extraction Request "+er.getId()+" for Raw Material "+rawMaterial.getProductName()+" successfully created.");
    }
    
    //For each product in the Invoice, we need to create one extraction request
    @Override
    public void dealWithSalesOrderFinishedGoodExtraction(Product product, Double quantity, Warehouse warehouse, SalesOrder so)
            throws ExtractionQuantityExceedSalesOrderNeedException{
        Double totalNeedQuantity = so.getProductQuantityMap().get((Product)product);
        if(quantity>totalNeedQuantity+1)
            throw new ExtractionQuantityExceedSalesOrderNeedException("Request unable to be sent. This Sales Order only needs "+totalNeedQuantity+" "+product.getUnit()+" "+product.getProductName()+"!");
        
        Company seller = product.getOwnerCompany();
        String requestSource = "SalesOrder";
        ExtractionRequest er = new ExtractionRequest(product, quantity, warehouse, seller, seller, requestSource);
        er.setSalesOrder(so);
        em.persist(er);
        so.getProductExtractionRequestList().add(er);
        em.merge(so);
        System.out.println("Sales Order Extraction Request "+er.getId()+" for FinishedGood "+product.getProductName()+" successfully created.");
    }
    
    @Override
    public List<ExtractionRequest> retrieveExtractionRequestListForSpecificCompany(Company requestDealer)
            throws ExtractionRequestNotExistException{
        Query query=em.createQuery("select s from ExtractionRequest s where s.requestDealer=?1 and s.fulfilledOrNot=FALSE and s.rejectedOrNot=FALSE");
        query.setParameter(1, requestDealer);
        ArrayList <ExtractionRequest> extractionRequestList=new ArrayList(query.getResultList());
        if(extractionRequestList.isEmpty())
            throw new ExtractionRequestNotExistException("No ExtractionRequest undealed by Company "+requestDealer.getCompanyName());
        return extractionRequestList;
    }
    
    @Override
    public void fulfillExtractionRequest(ExtractionRequest er) 
            throws ShelfNotExistException, InsufficientStorageInWarehouseException, WarehouseNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException, ShelfIncorrectProductException{
        Product product = er.getProduct();
        Double quantity = er.getQuantity();
        Warehouse warehouse = er.getWarehouse();
        
        Double currentInv = 0.0;
        if(product.getProductType().equals("FinishedGood"))
            currentInv = warehouse.getFinishedGoodMap().get((FinishedGood)product);
        else if(product.getProductType().equals("RawMaterial"))
            currentInv = warehouse.getRawMaterialMap().get((RawMaterial) product);
        
        if(currentInv<quantity)
            throw new InsufficientStorageInWarehouseException("ExtractionRequestSessionBean: Warehouse "+warehouse.getName()+" does not have sufficient inventory for "+quantity+" "+product.getProductName());
        
        ArrayList<Shelf> shelfList = getShelfList(warehouse, product, quantity);
        fgi.createOutflowInventory(product, quantity, warehouse, shelfList);
        er.setFulfilledOrNot(Boolean.TRUE);
        em.merge(er);
        if(er.getRequestSource().equals("Production"))
            psb.confirmRawMaterialReadyForProduction(er.getProduction());
        else if(er.getRequestSource().equals("SalesOrder"))
            som.confirmProductReadyForSalesOrder(er.getSalesOrder());
    }
    
    @Override
    public void rejectExtractionRequest(ExtractionRequest er){
        if(er.getRequestSource().equals("Production")){
            Production pro = er.getProduction();
            pro.getRawMaterialExtractionRequestList().remove(er);
            em.merge(pro);
        }
        else if(er.getRequestSource().equals("SalesOrder")){
            SalesOrder so = er.getSalesOrder();
            so.getProductExtractionRequestList().remove(er);
            em.merge(so);
        }
        er.setRejectedOrNot(Boolean.TRUE);
        em.merge(er);
    }
    
    private ArrayList<Shelf> getShelfList(Warehouse warehouse, Product product, Double quantity) 
            throws ShelfNotExistException{
        List<Shelf> shelfList = fml.retrieveShelfListForSpecificProductInSpecificWarehouse(warehouse, product);
        ArrayList<Shelf> result = new ArrayList<>();
        Double totalQuantity = 0.0;
        for(Object o: shelfList){
            Shelf curShelf = (Shelf) o;
            result.add(curShelf);
            Double curQuantity = curShelf.getCurQuantity();
            totalQuantity = totalQuantity + curQuantity;
            if(totalQuantity>=quantity)
                break;
        }
        return result;
    }
}
