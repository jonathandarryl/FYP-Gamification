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
import Common.entity.User;
import Common.session.AccountManagementSessionBeanLocal;
import Common.session.CompanyManagementSessionBeanLocal;
import Logger.MyLogger;
import java.io.File;
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
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "commonFuncManagedBean")
@SessionScoped
public class CommonFuncManagedBean implements Serializable {

    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    CompanyManagementSessionBeanLocal cmsbl;

    private String userType;
    private String username;
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String password;
    private String newpassword1;
    private String newpassword2;
    private Account account;
    private Title title;
    private Long uid;
    private Company company;
    private String companyName;
    private Long cid;
    private String contactNo;
    private String companyType;
    private String companyVAT;

    private String street;
    private String blockNo;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String companyAddress;

    private Department department;
    private List<Department> departmentList;
    private List<Title> titleList;
    private Long did;
    private CompanyUser companyUser;
    private CompanyUserAccount companyUserAccount;
    private boolean is1PL;
    private boolean isMarketing = false;
    private boolean isManufacturing = false;
    private boolean isTransportation = false;
    private boolean isWarehouse = false;
    private boolean isOperating = false;
    
    private UploadedFile imageFile;

//    private Logger logger;
    private final static Logger LOGGER = Logger.getLogger(CommonFuncManagedBean.class.getName());

    @PostConstruct
    public void init() {
        account = new Account();
        companyUser = new CompanyUser();
        companyUserAccount = new CompanyUserAccount();
        departmentList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    /**
     * Creates a new instance of CommonFuncManagedBean
     */
    public CommonFuncManagedBean() {
        try {
            MyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        LOGGER.setLevel(Level.INFO);
    }

    public void changePassword() throws UserNotExistException, PasswordNotMatchException, AccountTypeNotExistException, IOException, PasswordTooSimpleException {

        //get session attribute
//            username = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
//            userType = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");            
        //retrieve account information
//            id=(Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");            
        //check if the old password is correct
        boolean check = amsbl.checkLogin(username, password);

        //if the password is correct
        if (check == true) {

            //check complexity of new password
            boolean checkCmpx = amsbl.checkPasswordComplexity(newpassword1);
            if (checkCmpx == true) {
                boolean checkChange = amsbl.changePassword(id, userType, password, newpassword1);
                System.out.println("Check status is " + checkChange);
                if (checkChange == true) {
                    this.faceMsg("Your password has been updated. Please log in again");
                    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
                }
                if (checkChange == false) {
                    this.errorMsg("Update not committed");
                }
            } else if (checkCmpx == false) {
                this.warnMsg("New password strength is too weak, please change to a more complex password");
            }
        } //if the password is incorrect
        else if (check == false) {
            this.errorMsg("Password incorrect");
        }
    }

    public void updateProfile() throws AccountTypeNotExistException {
        try {
            boolean checkUpdate = amsbl.updateProfile(id, userType, username, firstName, lastName, email, contactNo);
            if (checkUpdate == true) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", null);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", username);
                System.out.println("Username is " + username);
                this.displayGrowl("Update successful");
            }
            if (checkUpdate == false) {
                this.displayGrowl("Error in updating");
            }
        } catch (UserExistException ex) {
            this.displayGrowl("Username already exists");
        }
    }
    
    public void handleImageUpload(FileUploadEvent event) throws UserNotExistException {
        this.displayGrowl(event.getFile().getFileName()+ " is uploaded.");
        Long userId = (Long)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        User u = amsbl.retrieveCompanyUser(userId);
        
    }

    public void setAllVariables() throws AccountTypeNotExistException, IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            System.out.println("lala");
            return;
        }
        username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        System.out.println("Username is " + username);
        userType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        System.out.println("Usertype is " + userType);
        id = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        System.out.println("ID is " + id);
        if (userType == null) {
            return;
//            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
        }
        if (userType.equals("Admin")) {
            CompanyAdminAccount caa = (CompanyAdminAccount) amsbl.retrieveAccount(id, userType);
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            companyName = company.getCompanyName();
            contactNo = company.getContactNo();
            companyVAT = company.getVAT();
            departmentList = company.getDepartment();
            System.out.println("Contact no. is " + contactNo);
            cid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            companyType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
            if (companyType.equals("1PL")) {
                is1PL = true;
            } else {
                is1PL = false;
            }
            department = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
            did = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("departmentID");
            System.out.println("Company Id is " + cid);
            System.out.println("Department ID is " + did);
            CompanyAdmin ca = caa.getCompanyAdmin();
            if (caa.getCompanyAdmin() == null) {
                System.out.println("user is null");
            }
            firstName = ca.getFirstName();
            lastName = ca.getLastName();
            email = ca.getEmail();
            phoneNo = caa.getContactNo();
            title = (Title) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("title");
            uid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        }

        if (userType.equals("SystemAdmin")) {
            SystemAdminAccount saa = (SystemAdminAccount) amsbl.retrieveAccount(id, userType);
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            companyName = company.getCompanyName();
            contactNo = company.getContactNo();
            companyVAT = company.getVAT();
            departmentList = company.getDepartment();
            System.out.println("Contact no. is " + contactNo);
            cid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            companyType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
            is1PL = false;
            department = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
            did = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("departmentID");
            System.out.println("Company Id is " + cid);
            System.out.println("Department ID is " + did);
            SystemAdmin sa = saa.getSystemAdmin();
            firstName = sa.getFirstName();
            System.out.println("First Name is " + firstName);
            lastName = sa.getLastName();
            email = sa.getEmail();
            phoneNo = saa.getContactNo();
            title = (Title) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("title");
            uid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        }

        if (userType.equals("User")) {
            CompanyUserAccount cua = (CompanyUserAccount) amsbl.retrieveAccount(id, userType);
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            companyName = company.getCompanyName();
            contactNo = company.getContactNo();
            companyVAT = company.getVAT();
            departmentList = company.getDepartment();
            cid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            companyType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
            if (companyType.equals("1PL")) {
                is1PL = true;
            } else {
                is1PL = false;
            }            
            department = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
            String d = department.getDepartmentName();
            if(d.equals("Sales and Marketing Department"))
                isMarketing = true;
            else if(d.equals("Transportation Department"))
                isTransportation = true;
            else if(d.equals("Warehouse Department"))
                isWarehouse = true;
            else if(d.equals("Manufacturing Department"))
                isManufacturing = true;
            else if(d.equals("Operation Department"))
                isOperating = true;
            
            did = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("departmentID");
            CompanyUser cu = cua.getCompanyUser();
            firstName = cu.getFirstName();
            System.out.println("First Name is " + firstName);
            lastName = cu.getLastName();
            System.out.println("Last Name is " + lastName);
            email = cu.getEmail();
            phoneNo = cua.getContactNo();
            System.out.println("Email is " + email);
            title = (Title) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("title");
            uid = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        }
    }

    public void populateLocation() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (company.getLocation() != null) {
            country = company.getLocation().getCountry();
            state = company.getLocation().getState();
            city = company.getLocation().getCity();
            street = company.getLocation().getStreet();
            blockNo = company.getLocation().getBlockNo();
            postalCode = company.getLocation().getPostalCode();
            companyAddress = street + ", " + blockNo + ", " + city + " " + state + " " + country + ", " + postalCode;
            System.out.println(companyAddress);
        }
    }

    public void updateCompany() throws CompanyNotExistException {
        System.out.println("Inside update company, Company Type is " + companyType);
        if (companyType.equals("1PL")) {
            cmsbl.updateCustomerCompany(cid, companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        }
        if (companyType.equals("2PL") || companyType.equals("3PL") || companyType.equals("4PL")) {
            cmsbl.updatePartnerCompany(cid, companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        }
        if (companyType.equals("5PL")) {
            cmsbl.updateMerLION(cid, companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        }
        System.out.println("Contact no. is " + contactNo);
        System.out.println("Street is " + street);
        this.displayGrowl("Update successful");
        company = cmsbl.retrieveCompany(cid);
    }

    public void saveUser() throws CompanyNotExistException {
        try {
            System.out.println("Inside save user");
            System.out.println("Company ID is " + cid);
            Account account = amsbl.createAccount(getCompanyUserAccount().getUsername(), "User", getCompanyUser().getFirstName(), getCompanyUser().getLastName(), getCompanyUserAccount().getContactNo(), getCompanyUser().getEmail(), getCompanyUserAccount().getDepartment(), getCompanyUser().getTitle(), getCompany(), getCompany().getCompanyType());
            if (account != null) {
                this.faceMsg("Account successfully added. Please check your email for password");
            } else {
                this.errorMsg("Account not added, please check again");
            }
        } catch (UserExistException ex) {
            this.warnMsg("Username already exists in the database");
        }
    }

    public void setTitleOption() {
        System.out.println("Selected Department");
        titleList = getCompanyUserAccount().getDepartment().getTitles();
    }

    public void forcelogout() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        System.out.println("Force Logout!");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
    }

    public void checkStatus() throws IOException {
//        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") == null) {
        System.out.println("Inside check status");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") == null) {
            System.out.println("Session time out");
            this.faceMsg("Session time-out. Please login again");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
        }
    }

    public void checkIfUser() throws IOException {
        System.out.println("Inside check if user");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType") != null) {
            userType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            if (this.userType.equals("User")) {
            } else {
                if (this.userType.equals("Admin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyAdminHome.xhtml");
                }
                if (this.userType.equals("SystemAdmin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/systemAdminHome.xhtml");
                }
                if (this.userType.equals("External")) {
                    System.out.println("Access denied.");
                    this.faceMsg("Access Denied.");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
                }
            }
        }
    }

    public void checkIfCompanyAdmin() throws IOException {
        System.out.println("Inside check if company admin");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType") != null) {
            userType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            if (this.userType.equals("Admin")) {
            } else {
                if (this.userType.equals("User")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
                }
                if (this.userType.equals("SystemAdmin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/systemAdminHome.xhtml");
                }
                if (this.userType.equals("External")) {
                    System.out.println("Access denied.");
                    this.faceMsg("Access Denied.");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
                }
            }
        }
    }

    public void checkIfSystemAdmin() throws IOException {
        System.out.println("Inside check if system admin");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType") != null) {
            userType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
            if (this.userType.equals("SystemAdmin")) {
            } else {
                if (this.userType.equals("Admin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyAdminHome.xhtml");
                }
                if (this.userType.equals("User")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
                }
                if (this.userType.equals("External")) {
                    System.out.println("Access denied.");
                    this.faceMsg("Access Denied.");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/myGRNS.xhtml");
                }
            }
        }
    }

    public void checkIfCustomerCompany() throws IOException {
        System.out.println("Inside check if customer company");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (!FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType").equals("1PL")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }

    public void checkIfPartnerCompany() throws IOException {
        System.out.println("Inside check if partnercompany");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType").equals("1PL")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }

    public void checkIfSalesAndMarketing() throws IOException {
        System.out.println("Inside check if sales and marketing");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Department d = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
        String dName = d.getDepartmentName();
        if (!dName.equals("Sales and Marketing Department")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }

    public void checkIfTransportation() throws IOException {
        System.out.println("Inside check if transportation");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Department d = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
        String dName = d.getDepartmentName();
        if (!dName.equals("Transportation Department")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }

    public void checkIfWarehouse() throws IOException {
        System.out.println("Inside check if warehouse");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Department d = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
        String dName = d.getDepartmentName();
        if (!dName.equals("Warehouse Department")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }

    public void checkIfManufacture() throws IOException {
        System.out.println("Inside check if manufacture");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Department d = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
        String dName = d.getDepartmentName();
        if (!dName.equals("Manufacturing Department")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
    }
    
    public void checkIfOperating() throws IOException {
        System.out.println("Inside check if operating");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Department d = (Department) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("department");
        String dName = d.getDepartmentName();
        if (!dName.equals("Operation Department")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserHome.xhtml");
        }
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

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("updateProfile", new FacesMessage(response));
        LOGGER.info("GROWL INFO: " + response);
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        LOGGER.info("MESSAGE INFO: " + response);
    }

    public CompanyUser getCompanyUser() {
        return companyUser;
    }

    public void setCompanyUser(CompanyUser companyUser) {
        this.companyUser = companyUser;
    }

    public CompanyUserAccount getCompanyUserAccount() {
        return companyUserAccount;
    }

    public void setCompanyUserAccount(CompanyUserAccount companyUserAccount) {
        this.companyUserAccount = companyUserAccount;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    public List<Title> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<Title> titleList) {
        this.titleList = titleList;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(String userType) {
        this.userType = userType;
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

    /**
     * @return the newpassword1
     */
    public String getNewpassword1() {
        return newpassword1;
    }

    /**
     * @param newpassword1 the newpassword1 to set
     */
    public void setNewpassword1(String newpassword1) {
        this.newpassword1 = newpassword1;
    }

    /**
     * @return the newpassword2
     */
    public String getNewpassword2() {
        return newpassword2;
    }

    /**
     * @param newpassword2 the newpassword2 to set
     */
    public void setNewpassword2(String newpassword2) {
        this.newpassword2 = newpassword2;
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
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public boolean isIs1PL() {
        return is1PL;
    }

    public void setIs1PL(boolean is1PL) {
        this.is1PL = is1PL;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCompanyVAT() {
        return companyVAT;
    }

    public void setCompanyVAT(String companyVAT) {
        this.companyVAT = companyVAT;
    }

    public boolean isIsMarketing() {
        return isMarketing;
    }

    public void setIsMarketing(boolean isMarketing) {
        this.isMarketing = isMarketing;
    }

    public boolean isIsManufacturing() {
        return isManufacturing;
    }

    public void setIsManufacturing(boolean isManufacturing) {
        this.isManufacturing = isManufacturing;
    }

    public boolean isIsTransportation() {
        return isTransportation;
    }

    public void setIsTransportation(boolean isTransportation) {
        this.isTransportation = isTransportation;
    }

    public boolean isIsWarehouse() {
        return isWarehouse;
    }

    public void setIsWarehouse(boolean isWarehouse) {
        this.isWarehouse = isWarehouse;
    }

    public boolean isIsOperating() {
        return isOperating;
    }

    public void setIsOperating(boolean isOperating) {
        this.isOperating = isOperating;
    }

    public UploadedFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(UploadedFile imageFile) {
        this.imageFile = imageFile;
    }

}
