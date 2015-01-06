/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.MonthlyPlan;
import MRPII.entity.Production;
import MRPII.entity.RawMaterial;
import MRPII.session.ProductionSessionBeanLocal;
import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.session.SalesOrderManagementLocal;
import WMS.entity.ExtractionRequest;
import WMS.entity.Warehouse;
import WMS.session.ExtractionRequestSessionBeanLocal;
import WMS.session.FacilityManagementLocal;
import WMS.session.StorageRequestSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.ExtractionQuantityExceedProductionNeedException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ProductionNotCommencedException;
import util.exception.ProductionNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.RawMaterialNotReadyException;
import util.exception.SalesOrderNotExistException;
import util.exception.ServiceContractNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "productionManagedBean")
@SessionScoped
public class ProductionManagedBean implements Serializable {

    /**
     * Creates a new instance of ProductionManagedBean
     */
    public ProductionManagedBean() {
    }

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ProductionSessionBeanLocal psbl;
    @EJB
    private SalesOrderManagementLocal soml;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private ExtractionRequestSessionBeanLocal ers;
    @EJB
    private StorageRequestSessionBeanLocal srs;

    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;

    private Production production;

    private SalesOrder so;

    private Product product;
    private RawMaterial rm;
    private Warehouse wh;

    private Integer batchNumber;

    private Double rawMaterialRequest;
    
    private Integer minimunBatchNumber;
    private Warehouse warehouse;
    

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            ownerCompany = cmsbl.retrieveCompany(cId);
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }

    public List<SalesOrder> retrieveBackOrderList() {
        List<SalesOrder> soList = new ArrayList<>();
        try {
            soList = soml.retrieveBackOrderList(ownerCompanyName);
            statusMessage = "Back Order List retrieved successfully!";
        } catch (SalesOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("SalesOrderNotExistException: " + statusMessage);
        }
        return soList;
    }

    public void retrieveBackOrder(SalesOrder so) throws IOException {
        System.out.println("Enter ProductionManagedBean: retrieveBackOrderList");
        this.so = so;
        redirectToPage("viewBackOrderDetailForProductionCreation.xhtml");
        System.out.println("End of ProductionManagedBean: retrieveBackOrderList");
    }

    public Double getSingleRequiredQuantity(Product product) {
        return so.getSalesQuotation().getProductListMap().get(product);
    }

    public void retrieveProduct(Product product) throws IOException {
        System.out.println("Enter ProductionManagedBean: retrieveBackOrderList");
        this.product = product;
        calculateMinimunBatchNumber();
    }

    public Integer calculateMinimunBatchNumber() {
        minimunBatchNumber = (int) Math.ceil(getSingleRequiredQuantity(product) / product.getQuantityInOneBatch());
        return minimunBatchNumber;
    }

    public void createProduction() {
        System.out.println("Enter ProductionManagedBean: createProduction");
        String productName = product.getProductName();
        Double quantity = so.getSalesQuotation().getProductListMap().get(product);

        try {
            psbl.createProduction(productName, ownerCompanyName, batchNumber, so);
            statusMessage = "Production created successfully! Product: " + productName + " Quantity: " + quantity + " Batch Number: " + batchNumber;
            System.out.println(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg1').hide()");
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
        }
        System.out.println("End of ProductionManagedBean: createProduction");
    }

    public List<Production> retrieveStandByProductionList() {
        List<Production> productionList = new ArrayList<>();
        try {
            productionList = psbl.retrieveStandByProductionList(ownerCompany);
            System.out.println(statusMessage);
        } catch (ProductionNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductionNotExistException: " + statusMessage);
        }
        return productionList;
    }

    public void retrieveStandByProductionForRawMaterialSchedule(Production production) throws IOException {
        this.production = production;
        redirectToPage("scheduleRawMaterialForProduction.xhtml");
    }

    public Boolean checkAllReadyOrNot() {
        return psbl.checkAllRawMaterialReadyOrNot(production);
    }

    public Double getSingleRequiredRawMaterialQuantity(RawMaterial rm) {
        HashMap<RawMaterial, Double> materialQuantityMap = production.getRawMaterialQuantityMap();
        return materialQuantityMap.get(rm);
    }

    public Double getSingleRequiredRawMaterialQuantity() {
        HashMap<RawMaterial, Double> materialQuantityMap = production.getRawMaterialQuantityMap();
        return materialQuantityMap.get(rm);
    }

    public Boolean checkOneRawMaterialReadyOrNot(RawMaterial rm) {
        System.out.println("ProductionManagedBean: checkOneRawMaterialReadyOrNot: Now checking raw material " + rm.getProductName());
        Boolean result = null;
        try {
            result = psbl.checkOneRawMaterialReadyOrNot(rm, production);
        } catch (java.lang.NullPointerException ex) {
            System.out.println("Catched null point exception for Raw Material " + rm.getProductName());
        } catch (javax.ejb.EJBException ex) {
            System.out.println("Catched EJB exception for Raw Material " + rm.getProductName());
        }
        return result;
    }

    public void retrieveRawMaterialForExtraction(RawMaterial rm) throws IOException {
        System.out.println("Enter ProductionManagedBean: requestForRawMaterialExtraction");
        this.rm = rm;
        redirectToPage("viewWarehouseListForRawMaterialExtraction.xhtml");
        System.out.println("End of ProductionManagedBean: requestForRawMaterialExtraction");
    }

    public Double retrieveTotalScheduledRawMaterialQuantity() {
        List<ExtractionRequest> erList = psbl.retrieveExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public Double retrieveOnHandRawMaterialQuantity() {
        List<ExtractionRequest> erList = psbl.retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public Double retrieveArrivingRawMaterialQuantity() {
        List<ExtractionRequest> erList = psbl.retrieveUnfulfilledExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public void confirmRawMaterialReadyForProduction() {
        System.out.println("Enter ProductionManagedBean: confirmRawMaterialReadyForProduction");
        if(psbl.confirmRawMaterialReadyForProduction(production)){
            statusMessage = "All materials are ready for production.";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        else{
            statusMessage = "Materials are not yet ready for production.";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
            
        System.out.println("End of ProductionManagedBean: confirmRawMaterialReadyForProduction");
    }

    public void viewExtractionRequestList() throws IOException {
        redirectToPage("viewExtractionRequestList.xhtml");
    }

    public List<ExtractionRequest> retrieveExtractionRequestListForOneRawMaterialInOneProduction() {
        List<ExtractionRequest> erList = psbl.retrieveExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        return erList;
    }

    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction() {
        List<ExtractionRequest> erList = psbl.retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        return erList;
    }

    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneRawMaterialInOneProduction() {
        List<ExtractionRequest> erList = psbl.retrieveUnfulfilledExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        return erList;
    }

    public Integer getExtractionRequestListSize(List<ExtractionRequest> erList) {
        return erList.size();
    }

    public List<Warehouse> retrieveWarehouseListForSpecificRawMaterial() {
        List<Warehouse> whList = new ArrayList<>();
        try {
            String fgName = rm.getProductName();
            whList = fml.retrieveWarehouseListForSpecificRawMaterial(fgName, ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (RawMaterialNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("RawMaterialNotExistException: " + statusMessage);
        } catch (MultipleRawMaterialWithSameNameException ex) {
            statusMessage = ex.getMessage();
            System.out.println("MultipleRawMaterialWithSameNameException: " + statusMessage);
        }
        return whList;
    }

    public Double getRawMaterialQuantityForSpecificWarehouse(Warehouse wh) {
        return wh.getRawMaterialMap().get(rm);
    }

    public Integer getWarehouseListSize(List<Warehouse> list) {
        return list.size();
    }

    public void initiateExtractionRequest(Warehouse warehouse) {
        wh = warehouse;
        System.out.println(statusMessage);
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
    }

    public void createExtractionRequest() {
        System.out.println("Enter ProductionManagedBean: createExtractionRequest");
        try {
            ers.dealWithProductionRawMaterialExtraction(rm, rawMaterialRequest, wh, production);
            statusMessage = "Extraction Request Created successfully!";
            RequestContext.getCurrentInstance().execute("PF('dlg').hide()");
            System.out.println(statusMessage);
            this.displayFaceMessage(statusMessage);
        } catch (ExtractionQuantityExceedProductionNeedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        System.out.println("End of ProductionManagedBean: createExtractionRequest");
    }

    public void commenceProduction(Production production) {
        System.out.println("Enter ProductionManagedBean: commenceProduction");
        try {
            psbl.commenceProduction(production);
            statusMessage = "Production Commenced";
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println(statusMessage);
        } catch (RawMaterialNotReadyException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("RawMaterialNotReadyException: " + statusMessage);
        }
        System.out.println("End of ProductionManagedBean: commenceProduction");
    }

    public List<Production> retrieveOngoingProductionList() {
        List<Production> result = new ArrayList<>();
        try {
            result = psbl.retrieveOngoingProductionList(ownerCompany);
        } catch (ProductionNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductionNotExistException: " + statusMessage);
        }
        return result;
    }

    public void retrieveOngoingProduction(Production production) throws IOException {
        System.out.println("Enter RawMaterialInventoryManagedBean: retrieveProduction");
        this.production = production;
        statusMessage = "Ongoing Production " + production.getId() + " retrieved";
        System.out.println(statusMessage);
        redirectToPage("viewOngoingProductionDetail.xhtml");
        System.out.println("End of RawMaterialInventoryManagedBean: retrieveProduction");
    }

    public List<Warehouse> retreiveAllWarehouse() {
        List<Warehouse> result = new ArrayList<>();
        result = fml.retrieveOwnedWarehouseList(ownerCompany);
        try {
            List<Warehouse> outsourcingList = fml.retrieveOutsourcingWarehouseList(ownerCompanyName);
            result.addAll(outsourcingList);
        } catch (CompanyNotExistException ex) {
        } catch (ServiceContractNotExistException ex) {
            System.out.println("Company " + ownerCompany + " does not have any outsourcing Warehouse.");
        }
        return result;
    }

    public void confirmFulfillment() {
        System.out.println("Enter RawMaterialInventoryManagedBean: confirmFulfillment");
        try {
            srs.dealWithProduction(production, warehouse, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            statusMessage = "Stock-in Request sent successfully.";
            System.out.println(statusMessage);
            psbl.completeProduction(production);
            statusMessage = "Stock-in Request sent successfully. Production Completes";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ProductionNotCommencedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductionNotExistException: " + statusMessage);
        }
        System.out.println("Enter RawMaterialInventoryManagedBean: confirmFulfillment");
    }

    public List<Production> retrieveCompletedProductionList() {
        List<Production> result = new ArrayList<>();
        try {
            result = psbl.retrieveCompletedProductionList(ownerCompany);
        } catch (ProductionNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductionNotExistException: " + statusMessage);
        }
        return result;
    }

    public void retrieveCompletedProduction(Production production) throws IOException {
        System.out.println("Enter RawMaterialInventoryManagedBean: retrieveCompletedProduction");
        this.production = production;
        redirectToPage("viewCompletedProductionDetail.xhtml");
        System.out.println("End of RawMaterialInventoryManagedBean: retrieveCompletedProduction");
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
        System.out.println("Redirecting to page " + url);
    }

    public void redirectToViewStandByProductionList() throws IOException {
        redirectToPage("viewStandByProductionList.xhtml");
    }

    public void redirectToScheduleRawMaterialForProduction() throws IOException {
        redirectToPage("scheduleRawMaterialForProduction.xhtml");
    }

    public void redirectToViewWarehouseList() throws IOException {
        redirectToPage("viewWarehouseListForRawMaterialExtraction.xhtml");
    }

    public void redirectToViewOngoingProductionList() throws IOException {
        redirectToPage("viewOngoingProductionList.xhtml");
    }

    public void redirectToViewCompletedProductionList() throws IOException {
        redirectToPage("viewCompletedProductionList.xhtml");
    }
    
    public void redirectToOngoingProductionList() throws IOException{
        redirectToPage("viewCompletedProductionList.xhtml");
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public SalesOrder getSo() {
        return so;
    }

    public void setSo(SalesOrder so) {
        this.so = so;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RawMaterial getRm() {
        return rm;
    }

    public void setRm(RawMaterial rm) {
        this.rm = rm;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Double getRawMaterialRequest() {
        return rawMaterialRequest;
    }

    public void setRawMaterialRequest(Double rawMaterialRequest) {
        this.rawMaterialRequest = rawMaterialRequest;
    }

    public Warehouse getWh() {
        return wh;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }

    public Integer getMinimunBatchNumber() {
        return minimunBatchNumber;
    }

    public void setMinimunBatchNumber(Integer minimunBatchNumber) {
        this.minimunBatchNumber = minimunBatchNumber;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

}
