/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonManagedBean;

import Common.entity.Account;
import Common.entity.Company;
import Common.entity.CompanyAdmin;
import Common.entity.CompanyUser;
import Common.entity.CompanyUserAccount;
import Common.entity.Department;
import Common.entity.SystemAdmin;
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
import util.exception.CompanyNotExistException;
import util.exception.TitleExistException;
import util.exception.TitleNotExistException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "companyAdminManagedBean")
@SessionScoped
public class CompanyAdminManagedBean implements Serializable {

    public CompanyAdminManagedBean() {
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

    private Title title;
    private Title oldTitle;
    private Title userTitle;
    private Department department;
    private Department oldDepartment;
    private Department userDepartment;
    private String userType;
    private Company company;
    private Long companyID;
    private CompanyUser companyUser;
    private CompanyUserAccount companyUserAccount;
    private List<Department> departmentList = new ArrayList<>();
    private List<Title> titleList = new ArrayList<>();
    private String titleName;
    private Long titleID;
    private List<Account> companyUsers;
    private Account selectedUser;
    private List<String> titlesForDisplayUser;
    private Long userId;
    private Long companyUserId;
    private String username;
    private String newTitle;
    private String fn;
    private String ln;
    private String email;
    private String phoneNo;
    private String criteria;
    private List<Account> accountList;
    private String userType2;
    private String companyName;
    private List<Company> allCompany;
    
        //    private Logger logger;
    private final static Logger LOGGER = Logger.getLogger(CompanyAdminManagedBean.class.getName());

    @PostConstruct
    public void init() {
        company = new Company();
        department = new Department();
        title = new Title();
        companyUser = new CompanyUser();
        companyUserAccount = new CompanyUserAccount();
        companyUsers = new ArrayList<>();
        selectedUser = new Account();
        titlesForDisplayUser = new ArrayList();
        userTitle = new Title();
        oldTitle = new Title();
        userDepartment = new Department();
        oldDepartment = new Department();
        accountList = new ArrayList<>();
        allCompany = new ArrayList<>();
    }

    public void saveUser() throws CompanyNotExistException {
        try {
            System.out.println("Inside save user");
            System.out.println("Company ID is " + companyID);
            userType = "User";
            Account account = amsbl.createAccount(getCompanyUserAccount().getUsername(), userType, getCompanyUser().getFirstName(), getCompanyUser().getLastName(), getCompanyUserAccount().getContactNo(), getCompanyUser().getEmail(), getCompanyUserAccount().getDepartment(), getCompanyUser().getTitle(), getCompany(), getCompany().getCompanyType());
            if (account != null) {
                this.faceMsg("Account successfully added. Please check email for password");
            } else {
                this.errorMsg("Account not added, please try again");
            }
        } catch (UserExistException ex) {
            this.warnMsg("Username already exists in the database");
        }
    }

    public void viewCompanyUsers() throws CompanyNotExistException, UserNotExistException {
        if(FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        Long companyID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println("Company ID is " + companyID);
        String companyType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyType");
        companyUsers = amsbl.retrieveAllCompanyUserAccount(companyID, companyType);
        if (companyUsers.isEmpty()) {
            System.out.println("After retrieve all company user account");
        }
        if (!companyUsers.isEmpty()) {
            System.out.println("It is not empty!!");
        }
//        for (Account a : companyUsers) {
//            Long accID = a.getId();
//            System.out.println("ACCID is " + accID);
//            titleName = amsbl.retrieveCompanyUser(accID).getTitle().getTitleName();
//            System.out.println("Title name is " + titleName);
//        }
    }

    public String viewSelectedTitle(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        Title t = amsbl.retrieveCompanyUser(accId).getTitle();
        String titleName = null;
        if (t != null) {
            titleName = t.getTitleName();
            System.out.println("Title name is " + titleName);
        }
//        titlesForDisplayUser.set(accId.intValue(), titleName);
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

    public String viewSelectedTitleSearch(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String titleName = null;
        if (userType2.equals("User")) {
            CompanyUser cu = amsbl.retrieveCompanyUser(accId);
            if (cu != null) {
                Title t = cu.getTitle();
                titleName = t.getTitleName();
            }
        }
        if (userType2.equals("Admin")) {
            CompanyAdmin ca = amsbl.retrieveCompanyAdmin(accId);
            if (ca != null) {
                Title t = ca.getTitle();
                titleName = t.getTitleName();
            }
        }
        if (userType2.equals("SystemAdmin")) {
            SystemAdmin sa = amsbl.retrieveSystemAdmin(accId);
            if (sa != null) {
                Title t = sa.getTitle();
                titleName = t.getTitleName();
            }
        }
        return titleName;
    }

    public String viewSelectedFNSearch(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String firstName = null;
        if (userType2.equals("User")) {
            firstName = amsbl.retrieveCompanyUser(accId).getFirstName();
        }
        if (userType2.equals("Admin")) {
            firstName = amsbl.retrieveCompanyAdmin(accId).getFirstName();
        }
        if (userType2.equals("SystemAdmin")) {
            firstName = amsbl.retrieveSystemAdmin(accId).getFirstName();
        }
        return firstName;
    }

    public String viewSelectedLNSearch(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String lastName = null;
        if (userType2.equals("User")) {
            lastName = amsbl.retrieveCompanyUser(accId).getLastName();
        }
        if (userType2.equals("Admin")) {
            lastName = amsbl.retrieveCompanyAdmin(accId).getLastName();
        }
        if (userType2.equals("SystemAdmin")) {
            lastName = amsbl.retrieveSystemAdmin(accId).getLastName();
        }
        return lastName;
    }

    public String viewSelectedEmailSearch(Long accId) throws UserNotExistException {
        System.out.println("ACCID is " + accId);
        String email = null;
        if (userType2.equals("User")) {
            email = amsbl.retrieveCompanyUser(accId).getEmail();
        }
        if (userType2.equals("Admin")) {
            email = amsbl.retrieveCompanyAdmin(accId).getEmail();
        }
        if (userType2.equals("SystemAdmin")) {
            email = amsbl.retrieveSystemAdmin(accId).getEmail();
        }
        return email;
    }

    public void viewCompanyUsersProfile(Long accId) throws IOException, AccountTypeNotExistException {
        System.out.println("Inside redirect page");
        userId = accId;
        CompanyUserAccount a = (CompanyUserAccount) amsbl.retrieveAccount(userId, "User");
        username = a.getUsername();
        CompanyUser cu = a.getCompanyUser();
        companyUserId = cu.getId();
        fn = cu.getFirstName();
        ln = cu.getLastName();
        email = cu.getEmail();
        phoneNo = a.getContactNo();
        oldDepartment = a.getDepartment();
        oldTitle = cu.getTitle();
        departmentList = a.getCompany().getDepartment();
        System.out.println("DepartmentList size is " + departmentList.size());
        titleList = a.getDepartment().getTitles();
        System.out.println("TitleList size is " + titleList.size());
        System.out.println("username of the company user is " + username);
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateCompanyUser.xhtml");
    }

    public void setTitleOption() {
        System.out.println("Selected Department, set title option");
        titleList = userDepartment.getTitles();
    }

    public void updateCompanyUsers() throws AccountTypeNotExistException, UserExistException, IOException {
        System.out.println("Inside update company user");
        System.out.println("new first name is " + fn);
        System.out.println("new last name is " + ln);
        System.out.println("Department of user is " + userDepartment.getDepartmentName());
        System.out.println("Title of user is " + userTitle.getTitleName());
        boolean checkDT = amsbl.changeDepartment(userId, "User", companyUserId, userDepartment, userTitle);
        boolean checkUpdate = amsbl.updateProfile(userId, "User", username, fn, ln, email, phoneNo);
        if ((checkUpdate == true) && (checkDT == true)) {
            this.faceMsg("Account updated");
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewCompanyUser.xhtml");
        } else {
            this.errorMsg("Account update unsuccessful");
        }
    }

    public void deleteCompanyUsers(Long userID) throws AccountTypeNotExistException {
        System.out.println("Inside delete company user");
        Account account = amsbl.retrieveAccount(userID, "User");
        System.out.println("Account ID is " + account.getId());
        amsbl.deleteAccount(userID, "User");
        if (account.isDeleteOrNot() == false) {
            this.faceMsg("Account is successfully deleted");
        }
        if (account.isDeleteOrNot() == true) {
            this.errorMsg("Account is not deleted");
        }
    }

    public void searchCompanyUser() throws AccountTypeNotExistException, IOException {
        try {
            System.out.println("Inside search user");
            company = cmsbl.retrieveCompany(companyName);
            System.out.println("Criteria is " + criteria);
            accountList = amsbl.searchAccount(criteria, userType2, company);
            System.out.println("CompanyName is " + company.getCompanyName());
            if (accountList == null) {
                this.errorMsg("No record matching the criteria");
                System.out.println("accountList is null");
            } else {
                String ut = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
                if (ut.equals("Admin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserSearchResult.xhtml");
                }
                if (ut.equals("User")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserSearchResultCompanyUser.xhtml");
                }
                if (ut.equals("SystemAdmin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/CommonWeb/companyUserSearchResultSystemAdmin.xhtml");
                }
            }
        } catch (CompanyNotExistException ex) {
            this.warnMsg("Company Name does not exist");
        }
    }
    
    public void searchCompany() {
        System.out.println("Inside search company");
        allCompany = cmsbl.retrieveAllCompany();
        if (allCompany.isEmpty()) {
            this.warnMsg("No company found");
        }
    }

    public void creatTitle() {
        try {
            amsbl.createTitle(titleName, department);
        } catch (TitleExistException ex) {
            this.warnMsg("Title already exists");
        }
    }

    public void deleteTitle() throws TitleNotExistException {
        boolean checkDel = amsbl.deleteTitle(titleID);
        if (checkDel == true) {
            this.faceMsg("Title successfully deleted");
        }
        if (checkDel == false) {
            this.errorMsg("Title delete unsuccessful. There are users associated with this particular title");
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
        LOGGER.info("MESSAGE INFO: " + response);
    }

    public List<String> getTitlesForDisplayUser() {
        return titlesForDisplayUser;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Company> getAllCompany() {
        return allCompany;
    }

    public void setAllCompany(List<Company> allCompany) {
        this.allCompany = allCompany;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public Long getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(Long companyUserId) {
        this.companyUserId = companyUserId;
    }

    public Title getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(Title userTitle) {
        this.userTitle = userTitle;
    }

    public Department getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(Department userDepartment) {
        this.userDepartment = userDepartment;
    }

    public void setTitlesForDisplayUser(List<String> titlesForDisplayUser) {
        this.titlesForDisplayUser = titlesForDisplayUser;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public Title getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(Title oldTitle) {
        this.oldTitle = oldTitle;
    }

    public Department getOldDepartment() {
        return oldDepartment;
    }

    public void setOldDepartment(Department oldDepartment) {
        this.oldDepartment = oldDepartment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Account getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Account selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<Account> getCompanyUsers() {
        return companyUsers;
    }

    public void setCompanyUsers(List<Account> companyUsers) {
        this.companyUsers = companyUsers;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Long getTitleID() {
        return titleID;
    }

    public void setTitleID(Long titleID) {
        this.titleID = titleID;
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

    public String getUserType2() {
        return userType2;
    }

    public void setUserType2(String userType2) {
        this.userType2 = userType2;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * @return the companyUserAccount
     */
    public CompanyUserAccount getCompanyUserAccount() {
        return companyUserAccount;
    }

    /**
     * @param companyUserAccount the companyUserAccount to set
     */
    public void setCompanyUserAccount(CompanyUserAccount companyUserAccount) {
        this.companyUserAccount = companyUserAccount;
    }

    /**
     * @return the companyUser
     */
    public CompanyUser getCompanyUser() {
        return companyUser;
    }

    /**
     * @param companyUser the companyUser to set
     */
    public void setCompanyUser(CompanyUser companyUser) {
        this.companyUser = companyUser;
    }

    /**
     * @return the companyID
     */
    public Long getCompanyID() {
        return companyID;
    }

    /**
     * @param companyID the companyID to set
     */
    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    
}
