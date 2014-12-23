/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import CRM.entity.ServiceOrder;
import CRM.entity.WarehouseOrder;
import CRM.session.ServiceOrderManagementLocal;
import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import WMS.session.WarehouseOrderManagementSessionLocal;
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
import util.exception.ServiceOrderAlreadyEstablishedWarehouseOrderException;
import util.exception.ServiceOrderAlreadyFulfilledException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.TransOrderNotFulfilledException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseOrderNotExistException;
import util.exception.WarehouseOrderOrderAlreadyFulfilledException;

/**
 *
 * @author songhan
 */
@Named(value = "warehouseOrderManagedBean")
@SessionScoped
public class WarehouseOrderManagedBean implements Serializable {

    /**
     * Creates a new instance of WarehouseOrderManagedBean
     */
    public WarehouseOrderManagedBean() {
    }
    
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private ServiceOrderManagementLocal soml;
    @EJB
    private WarehouseOrderManagementSessionLocal wom;

    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;

    private WarehouseOrder wo;
    private ServiceOrder so;
    
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
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
    }
    
    public List<ServiceOrder> retrieveServiceOrderListForWarehouseOrderCreation() throws IOException {
        List<ServiceOrder> soList = new ArrayList<>();
        try {
            soList = soml.retrieveServiceOrderListForWarehouseOrderCreation(ownerCompanyName);
            statusMessage = "Retrieveing..";
            System.out.println("retrieveServiceOrderListForWarehouseOrderCreation complete.");
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return soList;
    }
    
    public void retrieveServiceOrder(ServiceOrder so) throws IOException{
        System.out.println("Entering WarehouseOrderManagedBean: retrieveServiceOrderListForWarehouseOrderCreation");
        this.so = so;
        redirectToPage("./warehouseOrderCreation.xhtml");
        System.out.println("End of WarehouseOrderManagedBean: retrieveServiceOrderListForWarehouseOrderCreation");
    }
    
    public void createWarehouseOrder(){
        System.out.println("Entering ServiceOrderManagedBean: createWarehouseOrder");
        try {
            Long soId = so.getId();
            wom.createWarehouseOrder(soId);
            statusMessage = "Warehouse Order creation successful!";
            displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("createWarehouseOrder complete.");
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (ServiceOrderNotApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotApprovedException: " + statusMessage);
        } catch (ServiceOrderAlreadyEstablishedWarehouseOrderException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyEstablishedWarehouseOrderException: " + statusMessage);
        } catch (ServiceOrderNotPaidException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotPaidException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        }
        System.out.println("End of ServiceOrderManagedBean: createWarehouseOrder");
    }
    
    public List<WarehouseOrder> retrieveAllOwningWarehouseOrderList() throws IOException{
        List<WarehouseOrder> woList = new ArrayList<>();
        try {
            woList = soml.retrieveOwningWarehouseOrderList(ownerCompanyName);
            statusMessage = "Retrieve Owning WarehouseOrder List successful!";
            System.out.println("retrieveOwningWarehouseOrderList complete.");
        } catch (WarehouseOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("WarehouseOrderNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return woList;
    }
    
    public List<WarehouseOrder> retrieveAllOutSourcingWarehouseOrderList() throws IOException{
        List<WarehouseOrder> woList = new ArrayList<>();
        try {
            woList = soml.retrieveOutsourcingWarehouseOrderList(ownerCompanyName);
            statusMessage = "Retrieve Outsourcing WarehouseOrder List successful!";
            System.out.println("retrieveOutSourcingWarehouseOrderList complete.");
        } catch (WarehouseOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("WarehouseOrderNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return woList;
    }
    
    public void retrieveIncomingWarehouseOrder(WarehouseOrder wo) throws IOException{
        System.out.println("Entering WarehouseOrderManagedBean: retrieveOwningWarehouseOrder");
        this.wo = wo;
        redirectToPage("./incomingWarehouseOrderDetail.xhtml");
        System.out.println("End of WarehouseOrderManagedBean: retrieveOwningWarehouseOrder");
    }
    
    public void retrieveOutgoingWarehouseOrder(WarehouseOrder wo) throws IOException{
        System.out.println("Entering WarehouseOrderManagedBean: retrieveOutgoingWarehouseOrder");
        this.wo = wo;
        redirectToPage("./outgoingWarehouseOrderDetail.xhtml");
        System.out.println("End of WarehouseOrderManagedBean: retrieveOutgoingWarehouseOrder");
    }
    
    public void fulfillWarehouseOrder(WarehouseOrder wo){
        System.out.println("Entering WarehouseOrderManagedBean: fulfillWarehouseOrder");
        try {
            wom.fulfillWarehouseOrder(wo);
            statusMessage = "Fulfillment of Warehouse Order "+wo.getId()+" has been confirmed";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (WarehouseOrderOrderAlreadyFulfilledException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseOrderOrderAlreadyFulfilledException: " + statusMessage);
        } catch (TransOrderNotFulfilledException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("TransOrderNotFulfilledException: " + statusMessage);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        }
        System.out.println("End of WarehouseOrderManagedBean: fulfillWarehouseOrder");
    }
    
    public void fulfillWarehouseOrder(){
        System.out.println("Entering WarehouseOrderManagedBean: fulfillWarehouseOrder");
        try {
            wom.fulfillWarehouseOrder(wo);
            statusMessage = "Fulfillment of Warehouse Order "+wo.getId()+" has been confirmed";
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (WarehouseOrderOrderAlreadyFulfilledException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseOrderOrderAlreadyFulfilledException: " + statusMessage);
        } catch (TransOrderNotFulfilledException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("TransOrderNotFulfilledException: " + statusMessage);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        }
        System.out.println("End of WarehouseOrderManagedBean: fulfillWarehouseOrder");
    }
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void redirectToPage(String url) throws IOException {
        System.out.println("Redirecting to "+url);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }
    
    public void redirectToViewOwningServiceOrderListForWarehouseOrderCreation() throws IOException{
        redirectToPage("viewServiceOrderListForWarehouseOrderCreation.xhtml");
    }
    
    public void redirectToRetrieveIncomingWarehouseOrderList() throws IOException{
        redirectToPage("retrieveIncomingWarehouseOrderList.xhtml");
    }
    
    public void redirectToRetrieveOutgoingWarehouseOrderList() throws IOException{
        redirectToPage("retrieveOutgoingWarehouseOrderList.xhtml");
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

    public WarehouseOrder getWo() {
        return wo;
    }

    public void setWo(WarehouseOrder wo) {
        this.wo = wo;
    }

    public ServiceOrder getSo() {
        return so;
    }

    public void setSo(ServiceOrder so) {
        this.so = so;
    }

    public Warehouse getWh() {
        return wh;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }
       
}
