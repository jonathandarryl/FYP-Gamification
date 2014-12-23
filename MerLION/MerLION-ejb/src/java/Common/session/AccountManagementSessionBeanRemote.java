/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Account;
import Common.entity.Company;
import Common.entity.CompanyAdmin;
import Common.entity.CompanyAdminAccount;
import Common.entity.CompanyUser;
import Common.entity.CompanyUserAccount;
import Common.entity.Department;
import Common.entity.Scheduler;
import Common.entity.SystemAdmin;
import Common.entity.Title;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.TitleExistException;
import util.exception.TitleNotExistException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author chongyangsun
 */
@Remote
public interface AccountManagementSessionBeanRemote {
    public Account createAccount(String username, String userType, String firstName, String lastName, String phoneNo, String email, Department department, Title title, Company company, String companyType) throws UserExistException;
    public Account registerAccount(String username, String userType, String firstName, String lastName, String email, String contactNo, Department department, Title title, Company company, String companyType) throws UserExistException;
    public boolean checkLogin(String username, String password) throws UserNotExistException, PasswordNotMatchException;
    public boolean changePassword(Long id, String accountType, String oldPassword, String newPassword) throws AccountTypeNotExistException, PasswordTooSimpleException;
    public Account deleteAccount(Long accountId, String accountType) throws AccountTypeNotExistException;
    public List<Account> searchAccount(String criteria, String accountType, Company company) throws AccountTypeNotExistException;
    public Account activateAccount(Long id, String accountType) throws AccountTypeNotExistException;
    public boolean activateAccount(Long id);
    public List<Account> retriveDeactivatedAccountList();
    public Account deactivateAccount(Long id, String accountType) throws AccountTypeNotExistException;
    public boolean resetPassword(Long accountId, String accountType, String email) throws AccountTypeNotExistException;
    public boolean checkPasswordComplexity(String password);
    public Account retrieveAccount(Long accountId, String accountType) throws AccountTypeNotExistException;
    public Title createTitle(String titleName, Department department) throws TitleExistException;
    public Title registerTitle(String titleName, Department department) throws TitleExistException;
    public Boolean deleteTitle(Long titleId) throws TitleNotExistException;
    public Title retrieveTitle(String titleName, Department department) throws TitleNotExistException;
    public Title retrieveTitle(Long titleId) throws TitleNotExistException;
    //to be added, to return true if the account has been locked or deleted
    public Boolean checkLock(String username) throws AccountTypeNotExistException, UserNotExistException;
    public Boolean checkDelete(String username) throws AccountTypeNotExistException, UserNotExistException;
    public Account retrieveAccount(String username) throws AccountTypeNotExistException, UserNotExistException;
    public boolean updateProfile(Long accountId, String accountType, String username, String firstname, String lastname, String email, String contactNo) throws UserExistException;
    public Account createAccountForPopulateData(String username, String userType, String firstName, String lastName, String phoneNo, String email, Department department, Title title, Company company, String companyType) throws UserExistException;
    public List<Account> retrieveAllCompanyUserAccount(Long companyId, String companyType) throws CompanyNotExistException;
    public List<CompanyUserAccount> retrieveAllSystemUserAccount();
    public List<CompanyAdminAccount> retrieveAllCompanyAdminAccount();
    public CompanyUser retrieveCompanyUser(Long accountId) throws UserNotExistException;
    public CompanyAdmin retrieveCompanyAdmin(Long accountId) throws UserNotExistException;
    public SystemAdmin retrieveSystemAdmin(Long accountId) throws UserNotExistException;
    public List<CompanyAdminAccount> retrieveUnapprovedSystemAdminAccount();
    public boolean changeTitle(Long accountId, String userType, Long userId, Title title);
    public boolean changeDepartment(Long accountId, String userType, Long userId, Department department, Title title);
    public Scheduler createEvent(Long accountId, String eventId, String event, Boolean allDay, Timestamp startTime, Timestamp endTime);
    public Boolean updateEvent(Long accountId, String eventId, String event, Boolean allDay, Timestamp startTime, Timestamp endTime);
    public List<Scheduler> retrieveSchedule(Long accountId);
}
