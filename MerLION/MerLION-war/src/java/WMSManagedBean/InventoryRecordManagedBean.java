/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import WMS.entity.Inventory;
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
import util.exception.InventoryNotExistException;
import util.exception.ServiceContractNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "inventoryRecordManagedBean")
@SessionScoped
public class InventoryRecordManagedBean implements Serializable {

    /**
     * Creates a new instance of InventoryRecordManagedBean
     */
    public InventoryRecordManagedBean() {
    }

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private InventoryFlowManagementLocal ifm;

    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;

    private Inventory inventory;
    
    private Warehouse wh;

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

    public List<Warehouse> retrieveAllWarehouse() {
        List<Warehouse> whList = new ArrayList<>();
        List<Warehouse> ownedList = ownerCompany.getOwnedWarehouseList();
        List<Warehouse> outsourcingList = new ArrayList<>();
        try {
            outsourcingList = fml.retrieveOutsourcingWarehouseList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        }
        whList.addAll(ownedList);
        whList.addAll(outsourcingList);
        return whList;
    }
    
    public void retrieveInflowRecordWarehouse(Warehouse wh) throws IOException{
        System.out.println("Enter InventoryRecordManagedBean: retrieveInflowRecordWarehouse");
        this.wh = wh;
        redirectToPage("viewInflowInventoryRecordListInWarehouse.xhtml");
        System.out.println("End of InventoryRecordManagedBean: retrieveInflowRecordWarehouse");
    }
    
    public void retrieveOutflowRecordWarehouse(Warehouse wh) throws IOException{
        System.out.println("Enter InventoryRecordManagedBean: retrieveOutflowRecordWarehouse");
        this.wh = wh;
        redirectToPage("viewOutflowInventoryRecordListInWarehouse.xhtml");
        System.out.println("End of InventoryRecordManagedBean: retrieveOutflowRecordWarehouse");
    }

    public List<Inventory> retrieveInflowInventoryList() {
        List<Inventory> result = new ArrayList<>();
        try {
            if (ownerCompany.getCompanyType().equals("1PL")) 
                result = ifm.retrieveInflowInventoryListForSpecificWarehouseForCustomerCompany(wh, ownerCompany);            
            else 
                result = ifm.retrieveInflowInventoryListForSpecificWarehouse(wh);
            statusMessage = "Inflow inventory list for Warehouse "+wh.getName()+" retrieval complete";

        } catch (InventoryNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("InventoryNotExistException: " + statusMessage);
        }
        return result;
    }
    
    public List<Inventory> retrieveOutflowInventoryList() {
        List<Inventory> result = new ArrayList<>();
        try {
            if (ownerCompany.getCompanyType().equals("1PL")) 
                result = ifm.retrieveOutflowInventoryListForSpecificWarehouseForCustomerCompany(wh, ownerCompany);            
            else 
                result = ifm.retrieveOutflowInventoryListForSpecificWarehouse(wh);
            statusMessage = "Outflow inventory list for Warehouse "+wh.getName()+" retrieval complete";

        } catch (InventoryNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("InventoryNotExistException: " + statusMessage);
        }
        return result;
    }
    
    public void retrieveInflowInventoryDetail(Inventory inv) throws IOException{
        System.out.println("Enter InventoryRecordManagedBean: retrieveInflowInventoryDetail");
        inventory = inv;
        redirectToPage("viewInflowInventoryDetail.xhtml");
        System.out.println("End of InventoryRecordManagedBean: retrieveInflowInventoryDetail");
    }
    
    public void retrieveOutflowInventoryDetail(Inventory inv) throws IOException{
        System.out.println("Enter InventoryRecordManagedBean: retrieveOutflowInventoryDetail");
        inventory = inv;
        redirectToPage("viewOutflowInventoryDetail.xhtml");
        System.out.println("End of InventoryRecordManagedBean: retrieveOutflowInventoryDetail");
    }
    
    public String getInventoryStorageLocation(){
        return ifm.getInventoryLocation(inventory);
    }
    
    public Integer getWarehouseListSize(List<Warehouse> list){
        return list.size();
    }
    
    public Integer getInventoryRecordListSize(List<Inventory> list){
        return list.size();
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }
    
    public void redirectToInflowInventoryRecordList() throws IOException {
        redirectToPage("viewInflowInventoryRecordListInWarehouse.xhtml");
    }
    
    public void redirectToOutflowInventoryRecordList() throws IOException {
        redirectToPage("viewOutflowInventoryRecordListInWarehouse.xhtml");
    }

    public FacilityManagementLocal getFml() {
        return fml;
    }

    public void setFml(FacilityManagementLocal fml) {
        this.fml = fml;
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

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Warehouse getWh() {
        return wh;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }
    
    

}
