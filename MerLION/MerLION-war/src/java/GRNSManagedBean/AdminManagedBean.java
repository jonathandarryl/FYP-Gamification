/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import GRNS.entity.AggregationPost;
import GRNS.entity.ExternalUserAccount;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import GRNS.session.GRNSManagementSessionBeanLocal;
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
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "adminManagedBean")
@SessionScoped
public class AdminManagedBean implements Serializable {

    /**
     * Creates a new instance of AdminManagedBean
     */
    public AdminManagedBean() {
    }

    @EJB
    GRNSManagementSessionBeanLocal gmsbl;
    @EJB
    PostManagementSessionBeanLocal pmsbl;

    private ExternalUserAccount selectedUser;
    private List<ExternalUserAccount> allUserList;
    private Long accountId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String companyName;
    private String companyContact;
    
    private List<AggregationPost> allAP;
    private AggregationPost selectedAP;
    private List<StoragePost> allSP;
    private StoragePost selectedSP;
    private List<TransportPost> allTP;
    private TransportPost selectedTP;

    @PostConstruct
    public void init() {
        selectedUser = new ExternalUserAccount();
        allUserList = new ArrayList<>();
        allAP = new ArrayList<>();
        allSP = new ArrayList<>();
        allTP = new ArrayList<>();
        selectedAP = new AggregationPost();
        selectedSP = new StoragePost();
        selectedTP = new TransportPost();
    }

    public void viewUserList() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }

        this.allUserList = gmsbl.retrieveExternalUserAccountList();
        if (allUserList.isEmpty()) {
            this.warnMsg("There is currently no GRNS user");
        }
    }

    public void viewOneUser(Long accountId) throws UserNotExistException, IOException {
        selectedUser = gmsbl.retrieveExternalUserAccount(accountId);
        System.out.println("Username is " + selectedUser.getUsername());
        
        this.accountId = accountId;
        this.username = selectedUser.getUsername();
        this.firstName = selectedUser.getFirstName();
        this.lastName = selectedUser.getLastName();
        this.email = selectedUser.getEmail();
        this.contact = selectedUser.getContact();
        this.companyName = selectedUser.getExternalCompany().getCompanyName();
        this.companyContact = selectedUser.getExternalCompany().getContactNo();

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/admin/updateUser.xhtml");
    }

    public void updateUser() {
        boolean checkUpdate = gmsbl.updateExternalUserAccount(accountId, firstName, lastName, email, contact, companyName, companyContact);
        if (checkUpdate == true) {
            System.out.println("Username is " + username);
            this.displayGrowl("Update successful");
        }
        if (checkUpdate == false) {
            this.displayGrowl("Error in updating");
        }
    }
    
    public void viewAggregation() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allAP = pmsbl.retrieveAllAggregationPost("all");
    }
    
    public void viewStorage() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allSP = pmsbl.retrieveAllStoragePost("all");
    }
    
    public void viewTransport() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allTP = pmsbl.retrieveAllTransportPost("all");
    }
    
    public void deletePost(Long postId) {
        
    }
    
    public void viewDetailAggregationPost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedAP = pmsbl.retrieveAggregationPostById(postId);
        
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/admin/viewOneAggregationPost.xhtml");
    }
    
    public void viewDetailStoragePost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedSP = pmsbl.retrieveStoragePostById(postId);
 
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/admin/viewOneStoragePost.xhtml");
    }

    public void viewDetailTransportPost(Long postId) throws IOException {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        selectedTP = pmsbl.retrieveTransportPostById(postId);

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/admin/viewOneTransportPost.xhtml");
    }

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("updateProfile", new FacesMessage(response));
//        context.getExternalContext().getFlash().setKeepMessages(true);
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

    public void setAllUserList(List<ExternalUserAccount> allUserList) {
        this.allUserList = allUserList;
    }

    public List<ExternalUserAccount> getAllUserList() {
        return allUserList;
    }

    public ExternalUserAccount getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(ExternalUserAccount selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public List<AggregationPost> getAllAP() {
        return allAP;
    }

    public void setAllAP(List<AggregationPost> allAP) {
        this.allAP = allAP;
    }

    public List<StoragePost> getAllSP() {
        return allSP;
    }

    public void setAllSP(List<StoragePost> allSP) {
        this.allSP = allSP;
    }

    public List<TransportPost> getAllTP() {
        return allTP;
    }

    public void setAllTP(List<TransportPost> allTP) {
        this.allTP = allTP;
    }

    public AggregationPost getSelectedAP() {
        return selectedAP;
    }

    public void setSelectedAP(AggregationPost selectedAP) {
        this.selectedAP = selectedAP;
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
