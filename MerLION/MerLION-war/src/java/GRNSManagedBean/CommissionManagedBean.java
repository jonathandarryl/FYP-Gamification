/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import GRNS.entity.CommissionPayment;
import GRNS.entity.CommissionPaymentReceipt;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import GRNS.session.CommissionManagementSessionBeanLocal;
import GRNS.session.PostManagementSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "commissionManagedBean")
@SessionScoped
public class CommissionManagedBean implements Serializable {

    /**
     * Creates a new instance of CommissionManagedBean
     */
    public CommissionManagedBean() {
    }

    @EJB
    CommissionManagementSessionBeanLocal cmsbl;
    @EJB
    PostManagementSessionBeanLocal pmsbl;

    private CommissionPayment cp;
    private CommissionPaymentReceipt cpr;
    private List<CommissionPayment> cpl;
    private List<CommissionPaymentReceipt> cprl;

    private boolean checkPayCommission;
    private boolean checkCreateReceipt;
    private boolean checkViewReceipt;
    private StoragePost selectedSP;
    private TransportPost selectedTP;
    private Long accountId;
    private String accountType;

    @PostConstruct
    public void init() {
        cp = new CommissionPayment();
        cpr = new CommissionPaymentReceipt();
        cpl = new ArrayList<>();
        cprl = new ArrayList<>();
        selectedSP = new StoragePost();
        selectedTP = new TransportPost();
    }

    public void setAction(Long postId, String postType) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        accountType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        checkPayCommission = false;
        checkCreateReceipt = false;
        checkViewReceipt = false;

        if (postType.equals("storage")) {
            selectedSP = pmsbl.retrieveStoragePostById(postId);
            if (!selectedSP.getPaidOrNot()) {
                this.checkPayCommission = true;
            } else if ((selectedSP.getPaidOrNot()) && (cmsbl.retrieveReceipt(postId, postType) == null)) {
                this.checkCreateReceipt = true;
            } else {
                cpr = cmsbl.retrieveReceipt(postId, postType);
                this.checkViewReceipt = true;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/commission/viewStoragePaymentReceipt.xhtml");
        } else {
            selectedTP = pmsbl.retrieveTransportPostById(postId);
            if (!selectedTP.getPaidOrNot()) {
                this.checkPayCommission = true;
            } else if ((selectedTP.getPaidOrNot()) && (cmsbl.retrieveReceipt(postId, postType) == null)) {
                this.checkCreateReceipt = true;
            } else {
                cpr = cmsbl.retrieveReceipt(postId, postType);
                this.checkViewReceipt = true;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/commission/viewTransportPaymentReceipt.xhtml");
        }
        System.out.println("check pay commission is " + checkPayCommission);
        System.out.println("Check create receipt is " + checkCreateReceipt);
        System.out.println("Check view receipt is " + checkViewReceipt);

    }

    public void createPayment(Long postId, String postType) {
        if (cmsbl.retrievePayment(postId, postType) != null) {
            this.errorMsg("You have already paid commission fee. Please go to billing management for details");
        } else {
            cp = cmsbl.createPayment(postId, postType);
            if (cp == null) {
                this.warnMsg("Payment not successful. Please try again.");
            } else {
                this.displayGrowl("Commission fee is paid. Thank you for your corporation.");
            }
            checkPayCommission = false;
            checkCreateReceipt = true;
            checkViewReceipt = false;
        }
    }

    public void createReceipt(Long postId, String postType) {
        if (cmsbl.retrieveReceipt(postId, postType) != null) {
            this.errorMsg("Receipt has already been issued. Please go to billing management for detail");
        } else {

            cp = cmsbl.retrievePayment(postId, postType);

            if (cp == null) {
                this.errorMsg("Please make commission payment before retrieving receipt");
            } else {
                cpr = cmsbl.createReceipt(postId, postType);
                if (cpr == null) {
                    this.warnMsg("Receipt not retrieve. Please try again");
                } else {
                    this.displayGrowl("Receipt successfully retrieved. You can now view your payment receipt");
                }
            }
            checkPayCommission = false;
            checkCreateReceipt = false;
            checkViewReceipt = true;
        }
    }

    public void retrieveReceipt(Long postId, String postType) {
        cpr = cmsbl.retrieveReceipt(postId, postType);
        if (cpr == null) {
            this.warnMsg("Receipt not successfully retrieved. Please try again");
        }
    }

    public void retrievePaymentList() {
        System.out.println("Inside retrieve payment list");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        accountType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        cpl = cmsbl.retrievePaymentList(accountId, accountType);

    }

    public void retrieveReceiptList() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        accountType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");

        cprl = cmsbl.retrieveReceiptList(accountId, accountType);

    }

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(response));
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public CommissionPayment getCp() {
        return cp;
    }

    public void setCp(CommissionPayment cp) {
        this.cp = cp;
    }

    public CommissionPaymentReceipt getCpr() {
        return cpr;
    }

    public void setCpr(CommissionPaymentReceipt cpr) {
        this.cpr = cpr;
    }

    public List<CommissionPayment> getCpl() {
        return cpl;
    }

    public void setCpl(List<CommissionPayment> cpl) {
        this.cpl = cpl;
    }

    public List<CommissionPaymentReceipt> getCprl() {
        return cprl;
    }

    public void setCprl(List<CommissionPaymentReceipt> cprl) {
        this.cprl = cprl;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isCheckPayCommission() {
        return checkPayCommission;
    }

    public void setCheckPayCommission(boolean checkPayCommission) {
        this.checkPayCommission = checkPayCommission;
    }

    public boolean isCheckCreateReceipt() {
        return checkCreateReceipt;
    }

    public void setCheckCreateReceipt(boolean checkCreateReceipt) {
        this.checkCreateReceipt = checkCreateReceipt;
    }

    public boolean isCheckViewReceipt() {
        return checkViewReceipt;
    }

    public void setCheckViewReceipt(boolean checkViewReceipt) {
        this.checkViewReceipt = checkViewReceipt;
    }

    public StoragePost getSelectedSP() {
        return selectedSP;
    }

    public void setSelectedSP(StoragePost selectedSP) {
        this.selectedSP = selectedSP;
    }

    public TransportPost getSelectedTP() {
        return selectedTP;
    }

    public void setSelectedTP(TransportPost selectedTP) {
        this.selectedTP = selectedTP;
    }

}