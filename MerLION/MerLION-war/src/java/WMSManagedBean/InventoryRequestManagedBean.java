/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import WMS.entity.ExtractionRequest;
import WMS.entity.StorageRequest;
import WMS.session.ExtractionRequestSessionBeanLocal;
import WMS.session.StorageRequestSessionBeanLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.ExtractionRequestNotExistException;
import util.exception.InsufficientStorageInWarehouseException;
import util.exception.ShelfIncorrectProductException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.StorageRequestNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Named(value = "inventoryRequestManagedBean")
@SessionScoped
public class InventoryRequestManagedBean implements Serializable {

    /**
     * Creates a new instance of InventoryRequestManagedBean
     */
    public InventoryRequestManagedBean() {
    }
    
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private StorageRequestSessionBeanLocal srs;
    @EJB
    private ExtractionRequestSessionBeanLocal ers;
    
    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;
    private String regionCode;
    
    private StorageRequest sr;
    private ExtractionRequest er;
    
    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }
    
    private void initUserInfo(Long cId){        
        try {
            ownerCompany = cmsbl.retrieveCompany(cId);
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }
    
    public List<StorageRequest> retrieveStorageRequestList(){
        List<StorageRequest> storageRequestList = new ArrayList<>();
        try {
            storageRequestList = srs.retrieveStorageRequestListForSpecificCompany(ownerCompany);
        } catch (StorageRequestNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("StorageRequestNotExistException: " + statusMessage);
        }
        return storageRequestList;
    }
    
    public Integer getStorageRequestListSize(List<StorageRequest> srList){
        return srList.size();
    }
    
    public void retrieveStorageRequest(StorageRequest sr) throws IOException{
        System.out.println("Enter InventoryRequestManagedBean: retrieveStorageRequest");
        this.sr = sr;
        
        redirectToPage("viewStorageRequest.xhtml");
        System.out.println("End of InventoryRequestManagedBean: retrieveStorageRequest");
    }
    
    public Integer generateNeededCapacity(){        
        Double quantityInOneUnitCapacity = sr.getProduct().getQuantityInOneUnitCapacity();
        Double neededCapacity = sr.getQuantity() / quantityInOneUnitCapacity;
        Integer result = (int) Math.ceil(neededCapacity);
        return result;
    }
    
    public void rejectStorageRequest(StorageRequest sr){
        System.out.println("Enter InventoryRequestManagedBean: rejectExtractionRequest");
        srs.rejectStorageRequest(sr);
        System.out.println("Enter InventoryRequestManagedBean: rejectExtractionRequest");
    }
    
    public void approveStorageRequest(){
        System.out.println("Enter InventoryRequestManagedBean: approveStorageRequest");
        try {
            srs.fulfillStorageRequest(sr, regionCode);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            statusMessage = "Storage Request approved successfully";
            System.out.println(statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        } catch (ShelfListInsufficientCapacityException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfListInsufficientCapacityException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (WarehouseSpecialRequirementException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseSpecialRequirementException: " + statusMessage);
        }
        System.out.println("End of InventoryRequestManagedBean: approveStorageRequest");
    }
    
    public List<ExtractionRequest> retrieveExtractionRequestList(){
        List<ExtractionRequest> extractionRequestList = new ArrayList<>();
        try {
            extractionRequestList = ers.retrieveExtractionRequestListForSpecificCompany(ownerCompany);
        } catch (ExtractionRequestNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ExtractionRequestNotExistException: " + statusMessage);
        } 
        return extractionRequestList;
    }
    
    public Integer getExtractionRequestListSize(List<ExtractionRequest> erList){
        return erList.size();
    }
    
    public void retrieveExtractionRequest(ExtractionRequest er) throws IOException{
        System.out.println("Enter InventoryRequestManagedBean: retrieveExtractionRequest");
        this.er = er;
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        System.out.println("End of InventoryRequestManagedBean: retrieveExtractionRequest");
    }
    
    public void rejectExtractionRequest(ExtractionRequest er){
        System.out.println("Enter InventoryRequestManagedBean: rejectExtractionRequest");
        ers.rejectExtractionRequest(er);
        System.out.println("Enter InventoryRequestManagedBean: rejectExtractionRequest");
    }
    
    public void approveExtractionRequest(){
        System.out.println("Enter InventoryRequestManagedBean: approveExtractionRequest");
        try {
            ers.fulfillExtractionRequest(er);
            RequestContext.getCurrentInstance().execute("PF('dlg').hide()");
            statusMessage = "Extraction Request approved successfully";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        } catch (InsufficientStorageInWarehouseException ex) {
            statusMessage =ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println( "InsufficientStorageInWarehouseException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfListInsufficientCapacityException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfListInsufficientCapacityException: " + statusMessage);
        } catch (WarehouseSpecialRequirementException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseSpecialRequirementException: " + statusMessage);
        } catch (ShelfIncorrectProductException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfIncorrectProductException: " + statusMessage);
        }
        System.out.println("End of InventoryRequestManagedBean: approveExtractionRequest");
    }
    
    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }
    
    public void redirectToViewStorageRequestList() throws IOException{
        redirectToPage("viewStorageRequestList.xhtml");
    }
    
    public void redirectToViewExtractionRequestList() throws IOException{
        redirectToPage("viewExtractionRequestList.xhtml");
    }
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public StorageRequest getSr() {
        return sr;
    }

    public void setSr(StorageRequest sr) {
        this.sr = sr;
    }

    public ExtractionRequest getEr() {
        return er;
    }

    public void setEr(ExtractionRequest er) {
        this.er = er;
    }
    
}
