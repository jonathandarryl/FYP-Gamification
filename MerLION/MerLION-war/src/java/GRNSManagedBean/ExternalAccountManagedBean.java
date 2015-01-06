/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import Common.entity.ExternalCompany;
import Common.entity.Location;
import GRNS.entity.ExternalUserAccount;
import GRNS.session.GRNSManagementSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import util.exception.AccountNotActivatedException;
import util.exception.CompanyExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "externalAccountManagedBean")
@SessionScoped
public class ExternalAccountManagedBean implements Serializable {

    /**
     * Creates a new instance of AccountManagedBean
     */
    public ExternalAccountManagedBean() {
    }

    @EJB
    GRNSManagementSessionBeanLocal gmsbl;

    private String userType;
    private String username;
    private String oldUsername;
    private String password;
    private String password1;
    private Long accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String companyName;
    private String companyType;
    private String companyVAT;
    private Location location;
    private Long companyId;
    private String companyContact;
    private ExternalUserAccount euAccount;

    private String newPassword1;
    private String newPassword2;

    private boolean checkLogin;
    private boolean checkIfML;
    private boolean checkIfExternal;

    @PostConstruct
    public void init() {
        euAccount = new ExternalUserAccount();
        location = new Location();
    }

    public void createAccount() {
        try {
            euAccount = gmsbl.createExternalUserAccount(username, password, firstName, lastName, email, contact, companyName, companyVAT, location, companyContact);
            if (euAccount != null) {
                this.displayGrowl("External User Account is Successfully Created");
            } else {
                this.errorMsg("External User Not Created. Please Try Again");
            }
        } catch (UserExistException ex) {
            this.errorMsg("Username already exist");
        } catch (PasswordTooSimpleException ex) {
            this.errorMsg("Password strength is too weak");
        } catch (CompanyExistException ex) {
            this.errorMsg("Company name already exist");
        }
    }

    public void login() throws IOException {
        System.out.println("Username is " + username);
        System.out.println("Password is " + password);
        try {
            boolean checkLogin = gmsbl.checkLogin(username, password);
            System.out.println("Check login status is " + checkLogin);
            if (checkLogin == true) {
                ExternalUserAccount eua = gmsbl.retrieveExternalUserAccount(username);
                ExternalCompany ec = (ExternalCompany) eua.getExternalCompany();
                accountId = eua.getId();
                userType = eua.getAccountType();
                firstName = eua.getFirstName();
                lastName = eua.getLastName();
                email = eua.getEmail();
                contact = eua.getContact();
                companyName = ec.getCompanyName();
                companyType = ec.getCompanyType();
                companyContact = ec.getContactNo();
                companyId = ec.getId();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("id", accountId);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userType", userType);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyName", companyName);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyType", companyType);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyID", companyId);

                if (userType.equals("External")) {
                    this.checkIfExternal = true;
                } else {
                    this.checkIfExternal = false;
                }
                password=null;
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
            } else {
                this.errorMsg("Username and password does not match");
            }
        } catch (UserNotExistException ex) {
            this.errorMsg("Username does not exist");
        } catch (PasswordNotMatchException ex) {
            this.errorMsg("Username and password do not match");
        } catch (AccountNotActivatedException ex) {
            this.errorMsg("Account is not activated, please check your email");
        }
    }

    public void updateProfile() {
        boolean checkUpdate = gmsbl.updateExternalUserAccount(accountId, firstName, lastName, email, contact, companyName, companyContact);
        if (checkUpdate == true) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", null);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
            System.out.println("Username is " + username);
            this.displayGrowl("Update successful");
        }
        if (checkUpdate == false) {
            this.displayGrowl("Error in updating");
        }
    }

    public void changePassword() throws UserNotExistException {
        try {
            boolean flag = gmsbl.checkLogin(username, password);
            if (flag == true) {
                boolean complexity = gmsbl.checkPasswordComplexity(newPassword1);
                if (complexity == false) {
                    this.errorMsg("Strength of New Password is Too Weak.");
                } else {
                    try {
                        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
                        boolean change = gmsbl.changePassword(accountId, password, newPassword1);
                        if (change == true) {
                            this.displayGrowl("Password Updated");
                        } else {
                            this.errorMsg("Password Update Unsuccessful");
                        }
                    } catch (PasswordTooSimpleException ex) {
                        this.errorMsg("Strengh of New Password is Too Weak");
                    }
                }
            } else {
                this.errorMsg("Password Wrong");
            }
        } catch (PasswordNotMatchException ex) {
            this.errorMsg("Wrong password");
        } catch (AccountNotActivatedException ex) {
            this.errorMsg("Account not activated");
        }
    }

    public void resetPassword() {
            boolean checkReset = gmsbl.resetPassword(username, email);
            if (checkReset == true) {
                this.displayGrowl("New password has been sent to your email");
            } else {
                this.errorMsg("Password reset fails");
            }

    }

    public void checkIfAlreadyLoggedIn() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") != null) {
            username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");

            if (this.username != null) {
                System.out.println("already logged in");
                if (FacesContext.getCurrentInstance().getResponseComplete()) {
                    return;
                }
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
            }
        } else {
            System.out.println("Not Logged in");
        }
    }

    public void checkIfMerlion() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        String temp = "5PL";
        String flag = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
        if (flag.equals(temp)) {
            this.checkIfML = true;
        } else {
            this.checkIfML = false;
        }
    }
    
    public void checkIfMerlionUser() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        String temp = "5PL";
        String flag = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
        if (flag.equals(temp)) {
            
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
        }
    }

    public void checkIfExternalAccount() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") == null) {
            System.out.println("Session time out");
            this.faceMsg("Session time-out. Please login again");
            if(checkIfExternal == false)
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
            else
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/externalAccount/loginGRNS.xhtml");
        }
    }

    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        System.out.println("testtest");
        String serverName = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
        String serverPort = "8080";
        FacesContext.getCurrentInstance().getExternalContext().redirect("http://" + serverName + ":" + serverPort + "/MerLION-war/index.xhtml");
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
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

    public ExternalUserAccount getEuAccount() {
        return euAccount;
    }

    public void setEuAccount(ExternalUserAccount euAccount) {
        this.euAccount = euAccount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public boolean isCheckIfML() {
        return checkIfML;
    }

    public void setCheckIfML(boolean checkIfML) {
        this.checkIfML = checkIfML;
    }

    public boolean isCheckIfExternal() {
        return checkIfExternal;
    }

    public void setCheckIfExternal(boolean checkIfExternal) {
        this.checkIfExternal = checkIfExternal;
    }

    public boolean isCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(boolean checkLogin) {
        this.checkLogin = checkLogin;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getCompanyVAT() {
        return companyVAT;
    }

    public void setCompanyVAT(String companyVAT) {
        this.companyVAT = companyVAT;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
