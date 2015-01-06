/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.CompanyNotExistException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.RawMaterialNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ShelfNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "rawMaterialInventoryManagedBean")
@SessionScoped
public class RawMaterialInventoryManagedBean implements Serializable {

    /**
     * Creates a new instance of RawMaterialInventoryManagedBean
     */
    public RawMaterialInventoryManagedBean() {
    }

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private InventoryFlowManagementLocal fgim;

    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;

    private RawMaterial fg;

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

    public List<RawMaterial> retrieveAllRawMaterialList() {
        List<RawMaterial> fgList = new ArrayList<>();
        CustomerCompany curCompany = (CustomerCompany) ownerCompany;
        fgList = curCompany.getRawMaterialList();
        return fgList;
    }

    public Integer getProductListSize(List<RawMaterial> list) {
        return list.size();
    }

    public void retrieveRawMaterial(RawMaterial fg) throws IOException {
        System.out.println("Enter RawMaterialInventoryManagedBean: retrieveRawMaterial");
        this.fg = fg;
        redirectToPage("viewWarehouseList.xhtml");
        System.out.println("End of RawMaterialInventoryManagedBean: retrieveRawMaterial");
    }

    public List<Warehouse> retrieveWarehouseListForSpecificRawMaterial() {
        List<Warehouse> whList = new ArrayList<>();
        try {
            String fgName = fg.getProductName();
            whList = fml.retrieveWarehouseListForSpecificRawMaterial(fgName, ownerCompanyName);
            System.out.println("Retrieve All Warehouse List for product " + fgName + " Complete");
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
        return wh.getRawMaterialMap().get(fg);
    }

    public Integer getWarehouseListSize(List<Warehouse> list) {
        return list.size();
    }

    public void retrieveWarehouse(Warehouse wh) throws IOException {
        System.out.println("Enter RawMaterialInventoryManagedBean: retrieveWarehouse");
        this.wh = wh;
        redirectToPage("viewRawMaterialInWarehouse.xhtml");
        System.out.println("End of RawMaterialInventoryManagedBean: retrieveWarehouse");
    }

    public List<Shelf> retrieveShelfListForSpecificProductInWarehouse() {
        List<Shelf> shelfList = new ArrayList<>();
        try {
            shelfList = fml.retrieveShelfListForSpecificProductInSpecificWarehouse(wh, fg);
            statusMessage = "Retrieval of shelf list storing " + fg.getProductName() + " in Warehouse " + wh.getName() + " complete";
            System.out.println(statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return shelfList;
    }

    public void redirectToPage(String url) throws IOException {
        System.out.println("Redirecting to " + url);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }

    public void redirectToViewProductList() throws IOException {
        redirectToPage("viewAllRawMaterialInventoryList.xhtml");
    }

    public void redirectToViewWarehouseList() throws IOException {
        redirectToPage("viewWarehouseList.xhtml");
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

    public RawMaterial getFg() {
        return fg;
    }

    public void setFg(RawMaterial fg) {
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
