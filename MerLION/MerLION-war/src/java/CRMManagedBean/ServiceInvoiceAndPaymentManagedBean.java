/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.ServiceInvoice;
import CRM.entity.ServicePayment;
import CRM.entity.ServicePaymentReceipt;
import CRM.session.ServiceInvoiceAndPaymentManagementSessionLocal;
import Common.session.CompanyManagementSessionBeanLocal;
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
import util.exception.ServiceInvoiceAlreadyPaidException;
import util.exception.ServiceInvoiceNotExistException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.ServicePaymentAlreadySentException;
import util.exception.ServicePaymentNotExistException;
import util.exception.ServicePaymentReceiptNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "serviceInvoiceAndPaymentManagedBean")
@SessionScoped
public class ServiceInvoiceAndPaymentManagedBean implements Serializable {

    public ServiceInvoiceAndPaymentManagedBean() {
    }

    @EJB
    private ServiceInvoiceAndPaymentManagementSessionLocal siml;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;

    private String ownerCompanyName;

    private ServiceInvoice si;
    private Long siId;

    private ServicePayment sp;
    private Long spId;

    private ServicePaymentReceipt spr;
    private Long sprId;

    private String statusMessage;

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        si = new ServiceInvoice();
        sp = new ServicePayment();
        spr = new ServicePaymentReceipt();
    }

    private void initUserInfo(Long cId) {
        try {
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }

    public List<ServiceInvoice> retrieveOutgoingServiceInvoiceList() throws IOException {
        List<ServiceInvoice> result = new ArrayList<>();
        try {
            result = siml.retrieveOutgoingServiceInvoiceList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServiceInvoiceNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceInvoiceNotExistException: " + statusMessage);
        }
        return result;
    }

    public List<ServiceInvoice> retrieveIncomingServiceInvoiceList() throws IOException {
        List<ServiceInvoice> result = new ArrayList<>();
        try {
            result = siml.retrieveIncomingServiceInvoiceList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServiceInvoiceNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceInvoiceNotExistException: " + statusMessage);
        }
        return result;
    }

    public void makePayment(ServiceInvoice serviceInvoice) {
        System.out.println("Entering ServiceInvoiceAndPaymentManagedBean: makePayment");
        si = serviceInvoice;
        siId = si.getId();

        try {
            siml.createServicePayment(siId);
            statusMessage = "Payment made successfully!";
            displayFaceMessage(statusMessage);
            System.out.println("makePayment complete.");

        } catch (ServiceInvoiceNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceInvoiceNotExistException: " + statusMessage);
        } catch (ServiceInvoiceAlreadyPaidException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceInvoiceAlreadyPaidException: " + statusMessage);
        } 
        System.out.println("End of ServiceInvoiceAndPaymentManagedBean: makePayment");
    }

    public List<ServicePayment> retrieveOutgoingServicePaymentList() throws IOException {
        List<ServicePayment> result = new ArrayList<>();
        try {
            result = siml.retrieveOutgoingServicePaymentList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServicePaymentNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentNotExistException: " + statusMessage);
        }
        return result;
    }

    public List<ServicePayment> retrieveIncomingServicePaymentList() throws IOException {
        List<ServicePayment> result = new ArrayList<>();
        try {
            result = siml.retrieveIncomingServicePaymentList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServicePaymentNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentNotExistException: " + statusMessage);
        }
        return result;
    }

    public void issueReceipt(ServicePayment servicePayment) {
        System.out.println("Entering ServiceInvoiceAndPaymentManagedBean: sendReceipt");
        sp = servicePayment;
        spId = sp.getId();
        Long soId = sp.getServiceInvoice().getServiceOrder().getId();
        try {         
            siml.createServicePaymentReceipt(soId);
            statusMessage = "Receipt issued successfully!";
            displayFaceMessage(statusMessage);
            System.out.println("sendReceipt complete.");
        } catch (ServicePaymentNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentNotExistException: " + statusMessage);
        } catch (ServiceOrderNotPaidException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotPaidException: " + statusMessage);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (ServicePaymentAlreadySentException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentAlreadySentException: " + statusMessage);
        }
        System.out.println("End of ServiceInvoiceAndPaymentManagedBean: sendReceipt");
    }

    public List<ServicePaymentReceipt> retrieveOutgoingServicePaymentReceiptList() throws IOException {
        List<ServicePaymentReceipt> result = new ArrayList<>();
        try {
            result = siml.retrieveOutgoingServicePaymentReceiptList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServicePaymentReceiptNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentReceiptNotExistException: " + statusMessage);
        }
        return result;
    }

    public List<ServicePaymentReceipt> retrieveIncomingServicePaymentReceiptList() throws IOException {
        List<ServicePaymentReceipt> result = new ArrayList<>();
        try {
            result = siml.retrieveIncomingServicePaymentReceiptList(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServicePaymentReceiptNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServicePaymentReceiptNotExistException: " + statusMessage);
        }
        return result;
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

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public ServiceInvoice getSi() {
        return si;
    }

    public void setSi(ServiceInvoice si) {
        this.si = si;
    }

    public Long getSiId() {
        return siId;
    }

    public void setSiId(Long siId) {
        this.siId = siId;
    }

    public ServicePayment getSp() {
        return sp;
    }

    public void setSp(ServicePayment sp) {
        this.sp = sp;
    }

    public Long getSpId() {
        return spId;
    }

    public void setSpId(Long spId) {
        this.spId = spId;
    }

    public ServicePaymentReceipt getSpr() {
        return spr;
    }

    public void setSpr(ServicePaymentReceipt spr) {
        this.spr = spr;
    }

    public Long getSprId() {
        return sprId;
    }

    public void setSprId(Long sprId) {
        this.sprId = sprId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

}
