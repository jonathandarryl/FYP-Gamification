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
import Common.entity.Location;
import Common.entity.Title;
import Common.session.AccountManagementSessionBeanLocal;
import Common.session.CompanyManagementSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import Logger.MyLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyExistException;
import util.exception.CompanyNotExistException;
import util.exception.DepartmentExistException;
import util.exception.TitleExistException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "systemAdminManagedBean")
@SessionScoped
public class SystemAdminManagedBean implements Serializable{
    
    
    public SystemAdminManagedBean() {      
        try {
            MyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        LOGGER.setLevel(Level.INFO);
    }
    
    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    CompanyManagementSessionBeanLocal cmsbl;
    
    private String welcome="Have a nice day at work!";
    private String contactNo;
    private Company company;
    private Location location;
    private String companyType;
    private CompanyAdmin companyAdmin;
    private CompanyAdminAccount companyAdminAccount;
    private Department department;
    private Title title;
    private List<Department> departmentList = new ArrayList<>();
    private List<Title> titleList = new ArrayList<>();
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext ec = fc.getExternalContext();
    private List<CompanyUserAccount> allSystemUser;
    private List<CompanyAdminAccount> allCompanyAdmin;
    private String companyName;
    private String companyType2;
    private String userType;
    private String username;
    private String username2;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private List<Department> dList;
    private Department department2;
    private Title title2;
    private Company company2;
    private List<CompanyAdminAccount> allRegistration;
    private Long userId;
    private String fn;
    private String ln;
    private Department oldDepartment;
    private Title oldTitle;
    private Department userDepartment;
    private Title userTitle;
    private List<Department> userDList = new ArrayList<>();
    private List<Title> userTList = new ArrayList<>();
    private Long companyUserId;
    private Long adminId;
    private String username3;
    private Long companyAdminId;
    private String adminfn;
    private String adminln;
    private String adminEmail;
    private String adminPhoneNo;
    private Department adminDepartment;
    private Title oldAdminTitle;
    private Title adminTitle;
    private List<Title> adminTList;
    private String userTypeLock;
    private List<Account> allLockedAccount;
    
            //    private Logger logger;
    private final static Logger LOGGER = Logger.getLogger(SystemAdminManagedBean.class.getName());
    
    @PostConstruct
    public void init() {    
        companyAdmin=new CompanyAdmin();
        companyAdminAccount=new CompanyAdminAccount();
        company=new Company();
        location = new Location();
        company.setLocation(location);
        company2=new Company();
        department=new Department();
        title=new Title();
        department2=new Department();
        title2=new Title();
        dList=new ArrayList<>();
        allRegistration=new ArrayList<>();
        allSystemUser=new ArrayList<>();
        oldDepartment=new Department();
        oldTitle=new Title();
        userDepartment=new Department();
        userTitle=new Title();
        userDList=new ArrayList<>();
        userTList=new ArrayList<>();
        adminDepartment=new Department();
        oldAdminTitle=new Title();
        adminTList=new ArrayList<>();
        adminTitle=new Title();
    }
    
    /**
     *
     * @throws CompanyExistException
     * @throws DepartmentExistException
     * @throws TitleExistException
     */
    
    public void saveCompany() throws IOException {
        String response = null;
        
        try {
        //create customer company
        if (getCompanyType().equals("1PL")) {
            setCompany(cmsbl.registerCustomerCompany(getCompany().getCompanyName(), getCompanyType(), getCompany().getContactNo(), 
                    getCompany().getLocation().getCountry(), getCompany().getLocation().getState(), getCompany().getLocation().getCity(), 
                    getCompany().getLocation().getStreet(), getCompany().getLocation().getBlockNo(), getCompany().getLocation().getPostalCode(), getCompany().getVAT()));
            response = "Customer Company successfully added";
        }
        //create partner company
        if ((getCompanyType().equals("2PL")|(getCompanyType().equals("3PL"))|getCompanyType().equals("4PL"))|(getCompanyType().equals("5PL"))) {
            setCompany(cmsbl.registerPartnerCompany(getCompany().getCompanyName(), getCompanyType(), getCompany().getContactNo(), 
                    getCompany().getLocation().getCountry(), getCompany().getLocation().getState(), getCompany().getLocation().getCity(), 
                    getCompany().getLocation().getStreet(), getCompany().getLocation().getBlockNo(), getCompany().getLocation().getPostalCode(), getCompany().getVAT()));
            response = "Partner Company successfully added";
        }
        //trigger creation of department
        if (response !=null) {
            //create default department
            //System.out.println(response);
            Company c=getCompany();
            //System.out.println(c.getCompanyName());
            departmentList.add(cmsbl.registerDepartment("Sales and Marketing Department", c));
            departmentList.add(cmsbl.registerDepartment("Transportation Department", c));
            departmentList.add(cmsbl.registerDepartment("Warehouse Department", c));
            departmentList.add(cmsbl.registerDepartment("Manufacturing Department", c));
            department = cmsbl.registerDepartment("Account Management", c);
            departmentList.add(department);
        
            //create default title
            this.saveTitle(departmentList);
            System.out.println("Title saved!");
        }
        else {
            this.errorMsg("No response from server. Company not added");
        }
        
        this.saveCompanyAdmin();
        
        } catch (CompanyExistException ex) {
            this.errorMsg("Company already exists in the database");
        } catch (DepartmentExistException ex) {
            this.errorMsg("Department already exists in the database");
        } catch (TitleExistException ex) {
            this.errorMsg("Title already exists in the database");
        }
    }
    
    
    private void saveTitle(List departmentList) throws TitleExistException, DepartmentExistException {
        Title t = new Title();
        for (int i=0; i<departmentList.size();i++) {
            System.out.println("Department Id is " + ((Department)departmentList.get(i)).getId() + " and Name is " + ((Department)departmentList.get(i)).getDepartmentName());
            Department department=(Department)departmentList.get(i);
            t = amsbl.registerTitle("Manager", department);
            //System.out.println(t.getTitleName());
            amsbl.registerTitle("Staff", department);
            if(department.getDepartmentName().equals("Account Management"))
                title = t;
        }
    }
    
    private void saveCompanyAdmin() throws DepartmentExistException, TitleExistException, IOException {
        try {
            //        System.err.println("Error in save companyAdmin");
            
            String u = "Admin";
            //System.out.println(department.getDepartmentName());
            // System.out.println(title.getTitleName());
            amsbl.registerAccount(getCompanyAdminAccount().getUsername(), u, getCompanyAdmin().getFirstName(), getCompanyAdmin().getLastName(), getCompanyAdmin().getEmail(), getCompanyAdminAccount().getContactNo(), department, title, getCompany(), getCompany().getCompanyType());
//            this.displayGrowl("Company admin account successfully created!");
//            this.displayFaceMessage("Company successfully created");
//            this.displayFaceMessage("Department successfully created");
//            this.displayFaceMessage("Title successfully created");
//            this.displayFaceMessage("Company admin successfully created");
            this.resetPanel();
            this.displayFaceMessage("Company admin successfully registered!");
//            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/login.xhtml");
        } catch (UserExistException ex) {
            this.errorMsg("Username already exists in the database");
        }
    }
    
    public void checkCompany() throws IOException {
        try {
            company2 = cmsbl.retrieveCompany(companyName);
            if (company2.getCompanyType().equals(companyType2)) {
                dList = company2.getDepartment();
                System.out.println("Department name is "+dList.get(0).getDepartmentName());
                FacesContext.getCurrentInstance().getExternalContext().redirect("companyUserCreation2.xhtml");
            }
            else {
                this.errorMsg("Cmpany type and name does not match");
            }
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company name does not exist");
        }
    }
    
    public void setTitleOption() {
        System.out.println("Selected Department");
        titleList = department2.getTitles();
    }
    
    public void createCompanyUser() throws CompanyNotExistException {
        try {
            Account a = amsbl.createAccount(username, userType, firstName, lastName, phoneNo, email, department2, title2, company2, companyType2);
            if (a==null) {
                this.errorMsg("Account not created");
            }
            if (a!=null) {
                this.displayFaceMessage("Account created");
            }
        } catch (UserExistException ex) {
            this.errorMsg("Username already exists");
        }
    }
    
    public void viewAllSystemUsers() {
        if(FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allSystemUser = amsbl.retrieveAllSystemUserAccount();
        System.out.println("hahaha");
        if (allSystemUser.isEmpty()) {
            System.out.println("Empty");
        }
        if (allSystemUser==null) {
            this.warnMsg("There are no company users");
        }
    }
    
    public void viewAllCompanyAdmin() throws CompanyNotExistException {
        if(FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allCompanyAdmin = amsbl.retrieveAllCompanyAdminAccount();
        if (allCompanyAdmin==null) {
            this.warnMsg("There are no company admin");
        }
    }
    
    public String viewSelectedTitle(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String titleName = amsbl.retrieveCompanyUser(accId).getTitle().getTitleName();
        System.out.println("Title name is " + titleName);
        return titleName;
    }

    public String viewSelectedFN(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String firstName = amsbl.retrieveCompanyUser(accId).getFirstName();
        return firstName;
    }

    public String viewSelectedLN(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String lastName = amsbl.retrieveCompanyUser(accId).getLastName();
        return lastName;
    }
    public String viewSelectedEmail(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String email = amsbl.retrieveCompanyUser(accId).getEmail();
        return email;
    }
    
    public String viewSelectedAdminTitle(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String titleName = amsbl.retrieveCompanyAdmin(accId).getTitle().getTitleName();
        System.out.println("Title name is " + titleName);
        return titleName;
    }

    public String viewSelectedAdminFN(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String firstName = amsbl.retrieveCompanyAdmin(accId).getFirstName();
        return firstName;
    }

    public String viewSelectedAdminLN(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String lastName = amsbl.retrieveCompanyAdmin(accId).getLastName();
        return lastName;
    }

    public String viewSelectedAdminEmail(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String email = amsbl.retrieveCompanyAdmin(accId).getEmail();
        return email;
    }
    
    public void viewCompanyUsersProfile(Long accId) throws IOException, AccountTypeNotExistException {
        System.out.println("Inside redirect page");
        userId = accId;
        CompanyUserAccount a = (CompanyUserAccount)amsbl.retrieveAccount(userId, "User");
        username2 = a.getUsername();
        CompanyUser cu = a.getCompanyUser();
        companyUserId = cu.getId();
        fn = cu.getFirstName();
        ln = cu.getLastName();
        email = cu.getEmail();
        phoneNo = a.getContactNo();
        oldDepartment = a.getDepartment();
        oldTitle = cu.getTitle();
        userDList = a.getCompany().getDepartment();
        userTList = a.getDepartment().getTitles();
        System.out.println("username of the company user is " + username2);
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/updateCompanyUserSystemAdmin.xhtml");
    }
    
    public void viewCompanyAdminProfile(Long accId) throws IOException, AccountTypeNotExistException {
        System.out.println("Inside redirect page");
        adminId = accId;
        CompanyAdminAccount a = (CompanyAdminAccount)amsbl.retrieveAccount(adminId, "Admin");
        username3 = a.getUsername();
        CompanyAdmin ca = a.getCompanyAdmin();
        companyAdminId = ca.getId();
        adminfn = ca.getFirstName();
        adminln = ca.getLastName();
        adminEmail = ca.getEmail();
        adminPhoneNo = a.getContactNo();
        adminDepartment = a.getDepartment();
        oldAdminTitle = ca.getTitle();
        adminTList = a.getDepartment().getTitles();
        System.out.println("username of the company user is " + username3);
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/updateCompanyAdmin.xhtml");
    }
    
    public void setTitle() {
        System.out.println("Selected Department, set title option");
        titleList = userDepartment.getTitles();
    }
    
    public void updateCompanyUsers() throws AccountTypeNotExistException, UserExistException, IOException {
        System.out.println("Inside update company user");
        System.out.println("new first name is "+fn);
        System.out.println("new last name is "+ln);
        boolean checkDT = amsbl.changeDepartment(userId, "User", companyUserId, userDepartment, userTitle);
        boolean checkUpdate = amsbl.updateProfile(userId, "User", username, fn, ln, email, phoneNo);
        if ((checkUpdate == true) && (checkDT==true)) {
            this.faceMsg("Account updated");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/viewAllCompanyUser.xhtml");
        } else {
            this.displayFaceMessage("Account update unsuccessful");
        }
    }
    
    public void updateCompanyAdmin() throws AccountTypeNotExistException, UserExistException, IOException {
        System.out.println("Inside update company admin");
        System.out.println("new first name is "+adminfn);
        System.out.println("new last name is "+adminln);
        boolean checkDT = amsbl.changeTitle(adminId, "Admin", companyAdminId, adminTitle);
        boolean checkUpdate = amsbl.updateProfile(adminId, "Admin", username3, adminfn, adminln, adminEmail, adminPhoneNo);
        if ((checkUpdate == true) && (checkDT==true)) {
            this.faceMsg("Account updated");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/viewAllCompanyAdmin.xhtml");
        } else {
            this.errorMsg("Account update unsuccessful");
        }
    }

    public void deleteCompanyUsers(Long userID) throws AccountTypeNotExistException {
        System.out.println("Inside delete company user");
        Account account = amsbl.retrieveAccount(userID, "User");
        System.out.println("Account ID is "+account.getId());
        amsbl.deleteAccount(userID, "User");
        if (account.isDeleteOrNot() == false) {
            this.displayFaceMessage("Account is successfully deleted");
        }
        if (account.isDeleteOrNot() == true) {
            this.errorMsg("Account is not deleted");
        }
    }
    
    public void deleteCompanyAdmin(Long userID) throws AccountTypeNotExistException {
        System.out.println("Inside delete company user");
        Account account = amsbl.retrieveAccount(userID, "Admin");
        System.out.println("Account ID is "+account.getId());
        amsbl.deleteAccount(userID, "Admin");
        if (account.isDeleteOrNot() == false) {
            this.displayFaceMessage("Account is successfully deleted");
        }
        if (account.isDeleteOrNot() == true) {
            this.errorMsg("Account is not deleted");
        }
    }
    
    public void viewAllRegistration() {
        if(FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        allRegistration = amsbl.retrieveUnapprovedSystemAdminAccount();
    }
    
    public void approveCompanyAdmin(Long companyAdminAccountId) throws AccountTypeNotExistException {
        System.out.println("Inside approve request");
        CompanyAdminAccount caa = (CompanyAdminAccount)amsbl.retrieveAccount(companyAdminAccountId, "Admin");
        Long accountID = caa.getId();
        Long userID = caa.getCompanyAdmin().getId();
        System.out.println("CompanyAdmin userID is "+userID);
        Long companyId = caa.getCompany().getId();
        String companyT = caa.getCompany().getCompanyType();
        System.out.println("Company type is "+companyT);
        boolean checkApprove = cmsbl.approveRequest(accountID, userID, companyId, companyT);
        if (checkApprove==true) {
//            for(CompanyAdminAccount c: allRegistration) {
//                if(c.getId().equals(companyAdminAccountId)) {
//                    c.setApprovedOrNot(Boolean.TRUE);
//                    allRegistration.remove(c);
//                }
//            }
            this.displayFaceMessage("Registration approved");
        }
        else {
            this.errorMsg("Registration is not approved");
        }
    }
    
    private void resetPanel() {
        this.company= new Company();
        this.location=new Location();
        company.setLocation(location);
        this.companyAdmin= new CompanyAdmin();
        this.companyAdminAccount = new CompanyAdminAccount();
        this.contactNo=null;
        this.companyType=null;
    }
    
    public void lockUser(Long id) throws AccountTypeNotExistException, IOException {
        
        Account a = amsbl.deactivateAccount(id, "User");
        if (a.isLockedOrNot()==true) {
            this.faceMsg("Account is locked");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/viewAllCompanyUser.xhtml");
//            FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURI());
        }
        else {
            this.errorMsg("Account not locked, please check again");
        }
    }
    
    public void lockAdmin(Long id) throws AccountTypeNotExistException, IOException {
        System.out.println("Inside lock admin");
        Account a = amsbl.deactivateAccount(id, "Admin");
        if (a.isLockedOrNot()==true) {
            this.faceMsg("Account is locked");
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/viewAllCompanyAdmin.xhtml");
//            FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURI());
        }
        else {
            this.errorMsg("Account not locked, please check again");
        }
    }
    
    public void viewLockedAccount() {
        System.out.println("Inside view locked account");
        allLockedAccount = amsbl.retriveDeactivatedAccountList();
        if (allLockedAccount == null) {
            this.warnMsg("No locked account");
        }
    }
    
    public void unlockAccount(Long id) throws AccountTypeNotExistException {
        boolean checkUnlock = amsbl.activateAccount(id);
        if (checkUnlock==true) {
            this.displayFaceMessage("Account is activated");
        }
        else {
            this.errorMsg("Account is not activated");
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
    
    
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
//        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + response);
    }
    
//    private void displayGrowl(String response) {
//        FacesMessage msg = new FacesMessage(response);
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
//        LOGGER.info("MESSAGE INFO: " + response);
//    }

    public List<Account> getAllLockedAccount() {
        return allLockedAccount;
    }

    public void setAllLockedAccount(List<Account> allLockedAccount) {
        this.allLockedAccount = allLockedAccount;
    }

    public String getUserTypeLock() {
        return userTypeLock;
    }

    public void setUserTypeLock(String userTypeLock) {
        this.userTypeLock = userTypeLock;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getUsername3() {
        return username3;
    }

    public void setUsername3(String username3) {
        this.username3 = username3;
    }

    public Long getCompanyAdminId() {
        return companyAdminId;
    }

    public void setCompanyAdminId(Long companyAdminId) {
        this.companyAdminId = companyAdminId;
    }

    public String getAdminfn() {
        return adminfn;
    }

    public void setAdminfn(String adminfn) {
        this.adminfn = adminfn;
    }

    public String getAdminln() {
        return adminln;
    }

    public void setAdminln(String adminln) {
        this.adminln = adminln;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Department getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(Department adminDepartment) {
        this.adminDepartment = adminDepartment;
    }

    public Title getOldAdminTitle() {
        return oldAdminTitle;
    }

    public void setOldAdminTitle(Title oldAdminTitle) {
        this.oldAdminTitle = oldAdminTitle;
    }

    public List<Title> getAdminTList() {
        return adminTList;
    }

    public void setAdminTList(List<Title> adminTList) {
        this.adminTList = adminTList;
    }

    public List<CompanyAdminAccount> getAllCompanyAdmin() {
        return allCompanyAdmin;
    }

    public void setAllCompanyAdmin(List<CompanyAdminAccount> allCompanyAdmin) {
        this.allCompanyAdmin = allCompanyAdmin;
    }

    public Long getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(Long companyUserId) {
        this.companyUserId = companyUserId;
    }

    public List<Department> getUserDList() {
        return userDList;
    }

    public void setUserDList(List<Department> userDList) {
        this.userDList = userDList;
    }

    public List<Title> getUserTList() {
        return userTList;
    }

    public void setUserTList(List<Title> userTList) {
        this.userTList = userTList;
    }

    public Department getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(Department userDepartment) {
        this.userDepartment = userDepartment;
    }

    public Title getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(Title userTitle) {
        this.userTitle = userTitle;
    }

    public Department getOldDepartment() {
        return oldDepartment;
    }

    public void setOldDepartment(Department oldDepartment) {
        this.oldDepartment = oldDepartment;
    }

    public Title getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(Title oldTitle) {
        this.oldTitle = oldTitle;
    }

    public List<CompanyAdminAccount> getAllRegistration() {
        return allRegistration;
    }

    public void setAllRegistration(List<CompanyAdminAccount> allRegistration) {
        this.allRegistration = allRegistration;
    }

    public List<CompanyUserAccount> getAllSystemUser() {
        return allSystemUser;
    }

    public void setAllSystemUser(List<CompanyUserAccount> allSystemUser) {
        this.allSystemUser = allSystemUser;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public List<Department> getdList() {
        return dList;
    }

    public void setdList(List<Department> dList) {
        this.dList = dList;
    }

    public Department getDepartment2() {
        return department2;
    }

    public void setDepartment2(Department department2) {
        this.department2 = department2;
    }

    public Title getTitle2() {
        return title2;
    }

    public void setTitle2(Title title2) {
        this.title2 = title2;
    }

    public Company getCompany2() {
        return company2;
    }

    public void setCompany2(Company company2) {
        this.company2 = company2;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public List<Title> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<Title> titleList) {
        this.titleList = titleList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType2() {
        return companyType2;
    }

    public void setCompanyType2(String companyType2) {
        this.companyType2 = companyType2;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }    

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Title getAdminTitle() {
        return adminTitle;
    }

    public void setAdminTitle(Title adminTitle) {
        this.adminTitle = adminTitle;
    }

    /**
     * @return the companyAdminAccount
     */
    public CompanyAdminAccount getCompanyAdminAccount() {
        return companyAdminAccount;
    }

    /**
     * @param companyAdminAccount the companyAdminAccount to set
     */
    public void setCompanyAdminAccount(CompanyAdminAccount companyAdminAccount) {
        this.companyAdminAccount = companyAdminAccount;
    }

    /**
     * @return the companyAdmin
     */
    public CompanyAdmin getCompanyAdmin() {
        return companyAdmin;
    }

    /**
     * @param companyAdmin the companyAdmin to set
     */
    public void setCompanyAdmin(CompanyAdmin companyAdmin) {
        this.companyAdmin = companyAdmin;
    }

    /**
     * @return the companyType
     */
    public String getCompanyType() {
        return companyType;
    }

    /**
     * @param companyType the companyType to set
     */
    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAdminPhoneNo() {
        return adminPhoneNo;
    }

    public void setAdminPhoneNo(String adminPhoneNo) {
        this.adminPhoneNo = adminPhoneNo;
    }
    
}
