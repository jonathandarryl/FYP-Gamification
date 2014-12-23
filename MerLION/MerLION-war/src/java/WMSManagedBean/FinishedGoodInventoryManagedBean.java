/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import WMS.session.InventoryFlowManagementLocal;
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
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ShelfNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "finishedGoodInventoryManagedBean")
@SessionScoped
public class FinishedGoodInventoryManagedBean implements Serializable {

    /**
     * Creates a new instance of FinishedGoodInventoryManagedBean
     */
    public FinishedGoodInventoryManagedBean() {
    }
    
    @EJB
    private ProductInfoManagementLocal piml;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private InventoryFlowManagementLocal fgim;
    
    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;

    private FinishedGood fg;
    
    private Warehouse wh;

    private Shelf shelf;
    
    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        shelf = new Shelf();
        wh = new Warehouse();
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
    
    public List<FinishedGood> retrieveAllFinishedGoodList(){
        List<FinishedGood> fgList = new ArrayList<>();
        try {
            fgList = piml.retrieveProductList(ownerCompanyName);
            System.out.println("Retrieve All FinishedGood List Complete");
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductNotExistException: " + statusMessage);
        }
        return fgList;
    }
    
    public Integer getProductListSize(List<FinishedGood> list){
        return list.size();
    }
    
    public void retrieveFinishedGood(FinishedGood fg) throws IOException{
        System.out.println("Enter FinishedGoodInventoryManagedBean: retrieveFinishedGood");
        this.fg = fg;
        redirectToPage("viewWarehouseList.xhtml");
        System.out.println("End of FinishedGoodInventoryManagedBean: retrieveFinishedGood");
    }
    
    public List<Warehouse> retrieveWarehouseListForSpecificFinishedGood(){
        List<Warehouse> whList = new ArrayList<>();
        try {        
            String fgName = fg.getProductName();
            whList = fml.retrieveWarehouseListForSpecificFinishedGood(fgName, ownerCompanyName);
        } catch (CompanyNotExistException ex) {
           statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);        
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
        }
        return whList;
    }
    
    public Double getFinishedGoodQuantityForSpecificWarehouse(Warehouse wh){
        return wh.getFinishedGoodMap().get(fg);
    }
    
    public Integer getWarehouseListSize(List<Warehouse> list){
        return list.size();
    }
    
    public void retrieveWarehouse(Warehouse wh) throws IOException{
        System.out.println("Enter FinishedGoodInventoryManagedBean: retrieveWarehouse");
        this.wh = wh;
        redirectToPage("viewFinishedGoodInWarehouse.xhtml");
        System.out.println("End of FinishedGoodInventoryManagedBean: retrieveWarehouse");
    }
    
    public List<Shelf> retrieveShelfListForSpecificProductInWarehouse(){
        List<Shelf> shelfList = new ArrayList<>();
        try {
            shelfList = fml.retrieveShelfListForSpecificProductInSpecificWarehouse(wh, fg);
            statusMessage = "Retrieval of shelf list storing "+fg.getProductName()+" in Warehouse "+wh.getName()+" complete";
            System.out.println(statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return shelfList;
    }
    
    public void redirectToPage(String url) throws IOException {
        System.out.println("Redirecting to "+url);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }
    
    public void redirectToViewProductList() throws IOException{
        redirectToPage("viewAllFinishedGoodInventoryList.xhtml");
    }
    
    public void redirectToViewWarehouseList() throws IOException{
        redirectToPage("viewWarehouseList.xhtml");
    }
        
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public InventoryFlowManagementLocal getFgim() {
        return fgim;
    }

    public void setFgim(InventoryFlowManagementLocal fgim) {
        this.fgim = fgim;
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

    public FinishedGood getFg() {
        return fg;
    }

    public void setFg(FinishedGood fg) {
        this.fg = fg;
    }

    public Warehouse getWh() {
        return wh;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }
    
    
}
