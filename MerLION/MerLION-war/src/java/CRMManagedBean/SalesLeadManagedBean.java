/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.SalesLead;
import CRM.session.SalesLeadManagementSessionLocal;
import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.CompanyNotExistException;
import util.exception.SalesLeadAlreadyExistException;
import util.exception.SalesLeadNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "salesLeadManagedBean")
@SessionScoped
public class SalesLeadManagedBean implements Serializable {

    public SalesLeadManagedBean() {
    }

    @EJB
    private SalesLeadManagementSessionLocal slmsl;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    
    private String ownerCompanyName;
    private Long ownerCompanyId;
    private String clientCompanyName;
    private Long clientCompanyId;
    private List<SalesLead> slList;
    private SalesLead sl;
    private Long slId;
    private String description;
    private Company ownerCompany;
    private Company clientCompany;

    private String statusMessage;

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);  //For current testing usage. Should get from session
        retrieveSalesLeadList();
    }

    private void initUserInfo(Long cId){        
        try {
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "retrieveSuitableServiceQuotationListWithSepcificCompany: "
                    + statusMessage, ""));
        }
    }
    
    public void redirectToViewSalesLeadList() throws IOException{
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./salesLeadIndex.xhtml");
    }
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void addSalesLeadEntry(){
        System.out.println("Entering SalesLeadManagedBean: addSalesLeadEntry");
        try {
            sl = slmsl.createSalesLeadWithReturn(ownerCompanyName, clientCompanyName);
            statusMessage = "New sales lead added successful!";
            this.displayFaceMessage(statusMessage);
            slList.add(sl);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "addSalesLeadEntry: "
                    + statusMessage, ""));
        } catch (SalesLeadAlreadyExistException ex) {
            statusMessage = "SalesLeadAlreadyExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "addSalesLeadEntry: "
                    + statusMessage, ""));
        }
        System.out.println("End of SalesLeadManagedBean: addSalesLeadEntry");
    }
    
    public void retrieveSalesLeadList() {
        System.out.println("Entering SalesLeadManagedBean: retrieveSalesLeadList");
        try {
            slList = slmsl.retrieveSalesLeadList(ownerCompanyName);
            statusMessage = "SalesLeadManagedBean: retrieve SalesLeadList Successfully!";
            System.out.println(statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "retrieveSalesLeadList: "
                    + statusMessage, ""));
        }
        System.out.println("End of SalesLeadManagedBean: retrieveSalesLeadList");
    }

    public void retrieveSalesLead(SalesLead salesLead) throws IOException {
        System.out.println("Entering SalesLeadManagedBean: retrieveSalesLead");

        sl = salesLead;
        slId = salesLead.getId();
        description = salesLead.getDescription();
        ownerCompany = salesLead.getOwnerCompany();
        clientCompany = salesLead.getClientCompany();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateSalesLead.xhtml");
        System.out.println("End of SalesLeadManagedBean: retrieveSalesLead");
    }

    public void updateSalesLead() {
        System.out.println("Entering SalesLeadManagedBean: updateSalesLead");
        try {
            slmsl.updateSalesLead(slId, description);
            statusMessage = "SalesLeadManagedBean: update SalesLead " + slId + " Successfully!";
            Integer index = slList.indexOf(sl);  
            slList.remove(sl);
            sl = slmsl.retrieveSalesLead(slId);
            if(index<slList.size()-1)
                slList.add(index, sl);
            else
                slList.add(sl);
            System.out.println(statusMessage);
            this.faceMsg("Update Successful!");
        } catch (SalesLeadNotExistException ex) {
            statusMessage = "updateSalesLead: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "updateSalesLead: "
                    + statusMessage, ""));
        }
        System.out.println("End of SalesLeadManagedBean: updateSalesLead");
    }
    
    public void removeSalesLead(SalesLead salesLead){
        System.out.println("Entering SalesLeadManagedBean: removeSalesLead");
        try {
            slmsl.removeSalesLead(salesLead.getId());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion successful!", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            slList.remove(salesLead);
        } catch (SalesLeadNotExistException ex) {
            statusMessage = "removeSalesLead: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "removeSalesLead: "
                    + statusMessage, ""));
        }
        System.out.println("End of SalesLeadManagedBean: removeSalesLead");
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public Long getOwnerCompanyId() {
        return ownerCompanyId;
    }

    public void setOwnerCompanyId(Long ownerCompanyId) {
        this.ownerCompanyId = ownerCompanyId;
    }

    public List<SalesLead> getSlList() {
        return slList;
    }

    public void setSlList(List<SalesLead> slList) {
        this.slList = slList;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public SalesLead getSl() {
        return sl;
    }

    public void setSl(SalesLead sl) {
        this.sl = sl;
    }

    public Long getSlId() {
        return slId;
    }

    public void setSlId(Long slId) {
        this.slId = slId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public Company getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(Company clientCompany) {
        this.clientCompany = clientCompany;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public Long getClientCompanyId() {
        return clientCompanyId;
    }

    public void setClientCompanyId(Long clientCompanyId) {
        this.clientCompanyId = clientCompanyId;
    }

}
