/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonManagedBean;

import Common.entity.Account;
import Common.entity.Company;
import Common.entity.CompanyAdmin;
import Common.entity.CompanyAdminAccount;
import Common.entity.CompanyUser;
import Common.entity.CompanyUserAccount;
import Common.entity.Department;
import Common.entity.SystemAdmin;
import Common.entity.SystemAdminAccount;
import Common.entity.Title;
import Common.session.AccountManagementSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import Logger.MyLogger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import util.exception.AccountTypeNotExistException;
import util.exception.PasswordNotMatchException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "loginManagedBean")
@SessionScoped
public class LoginManagedBean implements Serializable {

    public LoginManagedBean() {
        this.logInAttempts = 0;
        try {
            MyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        LOGGER.setLevel(Level.INFO);
    }

    private Account account;
    private String username;
    private String oldUsername;
    private String password;
    private int logInAttempts;
    private String userType;
    private String email;
    private final int max_attempts = 5;
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext ec = fc.getExternalContext();
    
    private final static Logger LOGGER = Logger.getLogger(LoginManagedBean.class.getName());

    @EJB
    AccountManagementSessionBeanLocal amsbl;

    @PostConstruct
    public void init() {
        account = new Account();
    }

    public void startSsl(ActionEvent actionEvent) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
    }

    public void login() throws IOException, AccountTypeNotExistException {

        //set the session attribute
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
//        ec.getSessionMap().put("userType", userType);
//        ec.getSessionMap().put("username", username);
        if ((username!=null) && (!username.equals(oldUsername))) {
            logInAttempts=0;
            oldUsername=username;
        }
        try {
            if (amsbl.checkDelete(username)) {
                this.errorMsg("User account has been deleted");
            } 
            if (amsbl.checkLock(username)) {
                this.errorMsg("Account has been locked. Please approach system admin for activation.");
            } 
            if (logInAttempts == max_attempts) {
                System.out.println("Inside lock account");
                account = amsbl.retrieveAccount(username);
                userType = account.getAccountType();
                System.out.println("Account username is "+account.getUsername());
                amsbl.deactivateAccount(account.getId(), userType);
                this.errorMsg("Account has been locked. Please approach system admin for activation.");
            }
            if ((username != null) && (password != null) && (amsbl.checkDelete(username) == false) && (amsbl.checkLock(username) == false) && logInAttempts < max_attempts) {
                boolean check = amsbl.checkLogin(username, password);
                System.out.println("after check login. check is " + check);
                Account acc = amsbl.retrieveAccount(username);
                userType = acc.getAccountType();
                System.out.println("Account retrieved. Account id: " + acc.getId() + ", username: " + acc.getUsername());
                if (!acc.isApprovedOrNot()) {
                    this.warnMsg("Your registration is not approved, please check your email for further update");
                    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                    this.resetLogin();
                    //this.clear("loginPG");
                } else {
                    System.out.println("Account is approved!");
                    if (check == true) {
                        System.out.println("Inside check == true");
                        this.logInAttempts = 0;
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userType", userType);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("id", acc.getId());
                        System.out.println("Inside login, user ID is " + acc.getId());
                        FacesContext fc1 = FacesContext.getCurrentInstance();
                        ExternalContext ec1 = fc1.getExternalContext();
                        Company company;
                        Long cid;
                        String companyType;
                        Department department;
                        Long did;
                        Title title;
                        Long systemAdminId;
                        if (userType.equals("Admin")) {
                            CompanyAdminAccount caa = (CompanyAdminAccount) amsbl.retrieveAccount(acc.getId(), userType);
                            CompanyAdmin ca = (CompanyAdmin) amsbl.retrieveCompanyAdmin(caa.getId());
                            title = ca.getTitle();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("title", title);
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", ca.getId());
                            company = caa.getCompany();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("company", company);
                            cid = company.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyID", cid);
                            String companyName = company.getCompanyName();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyName", companyName);
                            companyType = company.getCompanyType();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyType", companyType);
                            department = caa.getDepartment();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("department", department);
                            did = department.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("departmentID", did);
                            ec1.redirect("/MerLION-war/CommonWeb/companyAdminHome.xhtml");

                        } else if (userType.equals("SystemAdmin")) {
                            SystemAdminAccount saa = (SystemAdminAccount) amsbl.retrieveAccount(acc.getId(), userType);
                            SystemAdmin sa = amsbl.retrieveSystemAdmin(saa.getId());
                            title = sa.getTitle();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("title", title);
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", sa.getId());
                            company = saa.getCompany();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("company", company);
                            cid = company.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyID", cid);
                            companyType = company.getCompanyType();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyType", companyType);
                            String companyName = company.getCompanyName();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyName", companyName);
                            department = saa.getDepartment();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("department", department);
                            did = department.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("departmentID", did);
                            ec1.redirect("/MerLION-war/CommonWeb/systemAdminHome.xhtml");
                        } else if (userType.equals("User")) {
                            CompanyUserAccount cua = (CompanyUserAccount) amsbl.retrieveAccount(acc.getId(), userType);
                            CompanyUser cu = amsbl.retrieveCompanyUser(cua.getId());
                            title = cu.getTitle();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("title", title);
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", cu.getId());
                            company = cua.getCompany();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("company", company);
                            cid = company.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyID", cid);
                            companyType = company.getCompanyType();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyType", companyType);
                            String companyName = company.getCompanyName();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("companyName", companyName);
                            department = cua.getDepartment();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("department", department);
                            did = department.getId();
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("departmentID", did);
                            ec1.redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
                        }
                    }
                    if (check == false) {
                        this.logInAttempts++;
                        System.out.println("login attempts: " + logInAttempts);
                        if (max_attempts - logInAttempts > 0) {
                            this.errorMsg("You have " + (max_attempts - logInAttempts) + " try before your account is frozen.");
                            if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") == null) {
                                System.out.println("username is null");
                            }
                            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
//                        this.displayFaceMessage("You have "+(max_attempts-logInAttempts)+"try before your account is frozen.");
                        }
                    }
                    
                }
            }
            
        } catch (UserNotExistException ex) {
            this.errorMsg("Username does not exist");
        } catch (PasswordNotMatchException ex) {
            this.errorMsg("Username and password do not match");
        } catch (AccountTypeNotExistException ex) {
            this.errorMsg("Account Type is invalid");
        }
//        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
    }

    public void resetPassword() throws AccountTypeNotExistException {
        try {
            System.out.println("username: " + username + " and user type: " + userType);
            if (userType.equals("SystemAdmin")) {
                SystemAdminAccount saa = (SystemAdminAccount) amsbl.retrieveAccount(username);
                Long id = saa.getId();
                String emailAddress = saa.getSystemAdmin().getEmail();
                amsbl.resetPassword(id, userType, emailAddress);
                logInAttempts = 0;
                this.displayFaceMessage("Your new password has been sent to your default email address.");
            }
            if (userType.equals("Admin")) {
                CompanyAdminAccount caa = (CompanyAdminAccount) amsbl.retrieveAccount(username);
                Long id = caa.getId();
//                System.out.println("Company Admin Account username is "+caa.getUsername());
                String emailAddress = caa.getCompanyAdmin().getEmail();
//                System.out.println("emailAddress is "+emailAddress);
                amsbl.resetPassword(id, userType, emailAddress);
                logInAttempts = 0;
                this.displayFaceMessage("Your new password has been sent to your default email address.");
            }
            if (userType.equals("User")) {
                CompanyUserAccount cua = (CompanyUserAccount) amsbl.retrieveAccount(username);
                Long id = cua.getId();
                String emailAddress = cua.getCompanyUser().getEmail();
                amsbl.resetPassword(id, userType, emailAddress);
                logInAttempts = 0;
                this.displayFaceMessage("Your new password has been sent to your default email address.");
            }
            
        } catch (UserNotExistException ex) {
            this.displayFaceMessage("Username does not exist");
        }
    }

    public void logout() throws IOException {
        System.out.println("Inside logout");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        System.out.println("testtest");
        String serverName = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
        String serverPort = "8080";
        FacesContext.getCurrentInstance().getExternalContext().redirect("http://" + serverName + ":" + serverPort + "/MerLION-war/index.xhtml");
    }

    private void resetLogin() {
        this.username = null;
        this.password = null;
    }
//    public String clear(final String parentComponentId) {
//        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
//        UIComponent com = view.findComponent(parentComponentId);
//        if (null != com) {
//            List<UIComponent> components = com.getChildren();
//            for (UIComponent component : components) {
//                if (component instanceof UIInput) {
//                    UIInput input = (UIInput) component;
//                    // JSF 1.1+ 
////                input.setSubmittedValue(null);
////                input.setValue(null);
////                input.setLocalValueSet(false);
////                input.setValid(true);
//                    // JSF 1.2+
//                    input.resetValue();
//                }
//            }
//        }
//        return null;
//    }

    public void checkAlreadyLoggedIn() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") != null) {
            username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");

            if (this.username != null) {
                System.out.println("already logged in");
                if (this.userType.equals("Admin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyAdminHome.xhtml");
                }
                if (this.userType.equals("SystemAdmin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/systemAdminHome.xhtml");
                }
                if (this.userType.equals("User")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
                }
                if (this.userType.equals("External")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
                }
            }
        } else
            System.out.println("Not Logged in");
    }

    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }
    
    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }
    
    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + response);
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getLogInAttempts() {
        return logInAttempts;
    }

    public void setLogInAttempts(int logInAttempts) {
        this.logInAttempts = logInAttempts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
