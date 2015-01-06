/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.TitleExistException;
import util.exception.TitleNotExistException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;
import util.session.GeneratePassword;
import util.session.GoogleMail;
import util.session.SendEmail;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class AccountManagementSessionBean implements AccountManagementSessionBeanLocal, AccountManagementSessionBeanRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private static final Random RANDOM = new SecureRandom();
    /**
     * Length of password.
     *
     * @see #passwordAssign()
     */
    public static final int SALT_LENGTH = 8;
    @PersistenceContext
    private EntityManager em;
//    private GoogleMail gm;
    private SendEmail sendEmail;

    @Override
    public Account createAccount(String username, String userType, String firstName, String lastName, String phoneNo, String email, Department department, Title title, Company company, String companyType) throws UserExistException {
        String salt = "";
        String letters = "0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
        System.out.println("Inside createAccount");

        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (!temp.isEmpty()) {
            System.out.println("User " + username + " exists!");
            throw new UserExistException("User " + username + " exists!");
        }
        System.out.println("Username passes check!");

        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            salt += letters.substring(index, index + 1);
        }
        String password = GeneratePassword.createPassword();
        String tempPassword = password;

        //send password to user through email
        try {
            SendEmail(firstName, lastName, username, email, tempPassword, company.getCompanyName());
        } catch (MessagingException ex) {
            System.out.println("Error sending email.");
            Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        password = passwordHash(password + salt);
        System.out.println("Password after hash&salt: " + password);
        if (userType.equals("Admin")) {
            //admin account creation --by system admin
            System.out.println("In Creating company admin account");
            CompanyAdminAccount companyAdminAccount = new CompanyAdminAccount(username, password, salt, department, company, "Admin");
            companyAdminAccount.setContactNo(phoneNo);
            department.getAccount().add(companyAdminAccount);
            company.getAccount().add(companyAdminAccount);
            System.out.println("Account successfully created");
            CompanyAdmin companyAdmin = new CompanyAdmin(firstName, lastName, email, companyAdminAccount);
            companyAdminAccount.setCompanyAdmin(companyAdmin);
            System.out.println("User successfully created!");
            title.getUser().add(companyAdmin);
            companyAdmin.setTitle(title);
            System.out.println("User Title associated!");
            em.persist(companyAdminAccount);
            em.persist(companyAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("Company admin account, account ID: " + companyAdminAccount.getId());
            return companyAdminAccount;
        } else if (userType.equals("SystemAdmin")) {
            //system admin account creation --by system admin
            SystemAdminAccount systemAdminAccount = new SystemAdminAccount(username, password, salt, department, company, "SystemAdmin");
            systemAdminAccount.setContactNo(phoneNo);
            department.getAccount().add(systemAdminAccount);
            company.getAccount().add(systemAdminAccount);
            SystemAdmin systemAdmin = new SystemAdmin(firstName, lastName, email, systemAdminAccount);
            systemAdminAccount.setSystemAdmin(systemAdmin);
            title.getUser().add(systemAdmin);
            systemAdmin.setTitle(title);
            em.persist(systemAdminAccount);
            em.persist(systemAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("System admin account, account ID: " + systemAdminAccount.getId());
            return systemAdminAccount;
        } else {
            //merlion user account creation --by system admin
            CompanyUserAccount companyUserAccount = new CompanyUserAccount(username, password, salt, department, company, "User");
            companyUserAccount.setContactNo(phoneNo);
            department.getAccount().add(companyUserAccount);
            company.getAccount().add(companyUserAccount);
            CompanyUser companyUser = new CompanyUser(firstName, lastName, email, companyUserAccount);
            companyUserAccount.setCompanyUser(companyUser);
            title.getUser().add(companyUser);
            companyUser.setTitle(title);
            em.persist(companyUserAccount);
            em.persist(companyUser);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("Company user account, account ID: " + companyUserAccount.getId());
            return companyUserAccount;
        }
    }

    @Override
    public Account registerAccount(String username, String userType, String firstName, String lastName, String email, String contactNo, Department department, Title title, Company company, String companyType) throws UserExistException {
        String salt = "";
        String letters = "0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
        System.out.println("Inside createAccount");

        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (!temp.isEmpty()) {
            System.out.println("User " + username + " exists!");
            throw new UserExistException("User " + username + " exists!");
        }
        System.out.println("Username passes check!");

        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            salt += letters.substring(index, index + 1);
        }
        String password = GeneratePassword.createPassword();
        String tempPassword = password;

        //send password to user through email
        try {
            SendEmail(firstName, lastName, username, email, tempPassword, company.getCompanyName());
        } catch (MessagingException ex) {
            System.out.println("Error sending email.");
            Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        password = passwordHash(password + salt);
        System.out.println("Password after hash&salt: " + password);
        if (userType.equals("Admin")) {
            //admin account creation --by system admin
            System.out.println("In Creating company admin account");
            CompanyAdminAccount companyAdminAccount = new CompanyAdminAccount(username, password, salt, department, company, "Admin", Boolean.FALSE);
            companyAdminAccount.setContactNo(contactNo);
            department.getAccount().add(companyAdminAccount);
            company.getAccount().add(companyAdminAccount);
            System.out.println("Account successfully created");
            CompanyAdmin companyAdmin = new CompanyAdmin(firstName, lastName, email, companyAdminAccount, Boolean.FALSE);
            companyAdminAccount.setCompanyAdmin(companyAdmin);
            System.out.println("User successfully created!");
            title.getUser().add(companyAdmin);
            companyAdmin.setTitle(title);
            System.out.println("User Title associated!");
            em.persist(companyAdminAccount);
            em.persist(companyAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("Company admin account, account ID: " + companyAdminAccount.getId());
            return companyAdminAccount;
        } else if (userType.equals("SystemAdmin")) {
            //system admin account creation --by system admin
            SystemAdminAccount systemAdminAccount = new SystemAdminAccount(username, password, salt, department, company, "SystemAdmin", Boolean.FALSE);
            systemAdminAccount.setContactNo(contactNo);
            department.getAccount().add(systemAdminAccount);
            company.getAccount().add(systemAdminAccount);
            SystemAdmin systemAdmin = new SystemAdmin(firstName, lastName, email, systemAdminAccount, Boolean.FALSE);
            systemAdminAccount.setSystemAdmin(systemAdmin);
            title.getUser().add(systemAdmin);
            systemAdmin.setTitle(title);
            em.persist(systemAdminAccount);
            em.persist(systemAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("System admin account, account ID: " + systemAdminAccount.getId());
            return systemAdminAccount;
        } else {
            return null;
        }
    }

    @Override
    public boolean checkLogin(String username, String password) throws UserNotExistException, PasswordNotMatchException {
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String userType = temp.get(0).getAccountType();

        if (userType.equals("Admin")) {
            Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.username = :user AND c.lockedOrNot = :lock AND c.deleteOrNot = :delete");
            query.setParameter("user", username);
            query.setParameter("lock", false);
            query.setParameter("delete", false);
            List<CompanyAdminAccount> companyAdminAccountList = query.getResultList();
            if (companyAdminAccountList.isEmpty()) {
                System.out.println("User " + username + " does not exist!");
                throw new UserNotExistException("User " + username + " does not exist, please try again");
            }
            CompanyAdminAccount companyAdminAccount = companyAdminAccountList.get(0);
            String salt = companyAdminAccount.getSalt();
            password = passwordHash(password + salt);
            if (password.equals(companyAdminAccount.getPassword())) {

                return true;//login successful
            } else {
                System.out.println("password false");
                return false;//password does not match, enter again
            }
        } else if (userType.equals("User")) {
            Query query = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.username = :user AND c.lockedOrNot = :lock AND c.deleteOrNot = :delete");
            query.setParameter("user", username);
            query.setParameter("lock", false);
            query.setParameter("delete", false);
            List<CompanyUserAccount> companyUserAccountList = query.getResultList();
            if (companyUserAccountList.isEmpty()) {
                System.out.println("User " + username + " does not exist!");
                throw new UserNotExistException("User " + username + " does not exist, please try again");
            }
            CompanyUserAccount companyUserAccount = companyUserAccountList.get(0);
            String salt = companyUserAccount.getSalt();
            password = passwordHash(password + salt);
            if (password.equals(companyUserAccount.getPassword())) {
                return true;//login successful
            } else {
                return false;//password does not match, enter again
            }
        } else if (userType.equals("SystemAdmin")) {
            System.out.println("inside check login for system admin haha~~~");
            Query query = em.createQuery("SELECT s FROM SystemAdminAccount s WHERE s.username = :user AND s.lockedOrNot = :lock AND s.deleteOrNot = :delete");
            query.setParameter("user", username);
            query.setParameter("lock", false);
            query.setParameter("delete", false);
            List<SystemAdminAccount> systemAdminAccountList = query.getResultList();
            if (systemAdminAccountList.isEmpty()) {
                System.out.println("User " + username + " does not exist!");
                throw new UserNotExistException("User " + username + " does not exist, please try again");
            }
            SystemAdminAccount systemAdminAccount = systemAdminAccountList.get(0);
            String salt = systemAdminAccount.getSalt();
            password = passwordHash(password + salt);
            System.out.println("Password after hash: " + password);
            if (password.equals(systemAdminAccount.getPassword())) {
                System.out.println("Check login success!");
                return true;//login successful
            } else {
                System.out.println("Check login failed");
                return false;//password does not match, enter again
            }
        } else {
            System.out.println("User type invalid???");
            return false;//user type invalid
        }
    }

    // Update accounts
    @Override
    public boolean changePassword(Long id, String accountType, String oldPassword, String newPassword) throws AccountTypeNotExistException, PasswordTooSimpleException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, id);
            if (companyAdminAccount == null) {
                return false;//cannot find account
            }
            if (oldPassword != null && newPassword != null && passwordHash(oldPassword + companyAdminAccount.getSalt()).equals(companyAdminAccount.getPassword())) {
                if (!checkPasswordComplexity(newPassword)) {
                    throw new PasswordTooSimpleException("password is too simple");
                }
                newPassword = passwordHash(newPassword + companyAdminAccount.getSalt());
                companyAdminAccount.setPassword(newPassword);
            } else {
                return false;//invalid input
            }

            em.merge(companyAdminAccount);
            em.flush();
            return true;
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, id);
            if (companyUserAccount == null) {
                return false;//cannot find account
            }
            if (oldPassword != null && newPassword != null && passwordHash(oldPassword + companyUserAccount.getSalt()).equals(companyUserAccount.getPassword())) {
                if (!checkPasswordComplexity(newPassword)) {
                    throw new PasswordTooSimpleException("password is too simple");
                }
                newPassword = passwordHash(newPassword + companyUserAccount.getSalt());
                companyUserAccount.setPassword(newPassword);
            } else {
                return false;//invalid input
            }

            em.merge(companyUserAccount);
            em.flush();
            return true;
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, id);
            if (systemAdminAccount == null) {
                return false;//cannot find account
            }
            if (oldPassword != null && newPassword != null && passwordHash(oldPassword + systemAdminAccount.getSalt()).equals(systemAdminAccount.getPassword())) {
                if (!checkPasswordComplexity(newPassword)) {
                    throw new PasswordTooSimpleException("password is too simple");
                }
                newPassword = passwordHash(newPassword + systemAdminAccount.getSalt());
                systemAdminAccount.setPassword(newPassword);
            } else {
                return false;//invalid input
            }

            em.merge(systemAdminAccount);
            em.flush();
            return true;
        } else {
            throw new AccountTypeNotExistException("Account type " + accountType + " is incorrect, please check");
        }
    }

    // To delete account
    @Override
    public Account deleteAccount(Long accountId, String accountType) throws AccountTypeNotExistException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
            if (companyAdminAccount == null) {
                return null;
            }
            companyAdminAccount.setDeleteOrNot(Boolean.TRUE);
            CompanyAdmin companyAdmin = companyAdminAccount.getCompanyAdmin();
            companyAdmin.setDeleteOrNot(Boolean.TRUE);
            em.merge(companyAdminAccount);
            em.merge(companyAdmin);
            em.flush();
            return companyAdminAccount;
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, accountId);
            if (companyUserAccount == null) {
                return null;
            }
            companyUserAccount.setDeleteOrNot(Boolean.TRUE);
            CompanyUser companyUser = companyUserAccount.getCompanyUser();
            companyUser.setDeleteOrNot(Boolean.TRUE);
            em.merge(companyUserAccount);
            em.merge(companyUser);
            em.flush();
            return companyUserAccount;
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, accountId);
            if (systemAdminAccount == null) {
                return null;
            }
            systemAdminAccount.setDeleteOrNot(Boolean.TRUE);
            SystemAdmin systemAdmin = systemAdminAccount.getSystemAdmin();
            systemAdmin.setDeleteOrNot(Boolean.TRUE);
            em.merge(systemAdminAccount);
            em.merge(systemAdmin);
            em.flush();
            return systemAdminAccount;
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    // Search accounts
    @Override
    public List<Account> searchAccount(String criteria, String accountType, Company company) throws AccountTypeNotExistException {
        if (accountType.equals("Admin")) {
            Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.company=:company1 and c.username LIKE '%" + criteria + "%'");
            query.setParameter("company1", company);
            List<Account> userAccountList = query.getResultList();
            return userAccountList;
        } else if (accountType.equals("User")) {
            Query query = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.company=:company1 and c.username LIKE '%" + criteria + "%'");
            query.setParameter("company1", company);
            List<Account> userAccountList = query.getResultList();
            return userAccountList;
        } else if (accountType.equals("SystemAdmin")) {
            Query query = em.createQuery(" SELECT c FROM SystemAdminAccount c WHERE c.username LIKE '%" + criteria + "%'");
            List<Account> userAccountList = query.getResultList();
            return userAccountList;
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    // Unlock accounts
    @Override
    public Account activateAccount(Long id, String accountType) throws AccountTypeNotExistException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, id);
            if (companyAdminAccount != null && companyAdminAccount.isLockedOrNot()) {
                companyAdminAccount.setLockedOrNot(false);
                em.merge(companyAdminAccount);
                em.flush();
                return companyAdminAccount;
            } else {
                return null;//cannot find account
            }
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, id);
            if (companyUserAccount != null && companyUserAccount.isLockedOrNot()) {
                companyUserAccount.setLockedOrNot(false);
                em.merge(companyUserAccount);
                em.flush();
                return companyUserAccount;
            } else {
                return null;//cannot find account
            }
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, id);
            if (systemAdminAccount != null && systemAdminAccount.isLockedOrNot()) {
                systemAdminAccount.setLockedOrNot(false);
                em.merge(systemAdminAccount);
                em.flush();
                return systemAdminAccount;
            } else {
                return null;//cannot find account
            }
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    @Override
    public boolean activateAccount(Long id) {
        Account account = em.find(Account.class, id);
        if (account != null && account.isLockedOrNot()) {
            account.setLockedOrNot(false);
            em.merge(account);
            em.flush();
        }
        return true;
    }

    // Lock account
    @Override
    public Account deactivateAccount(Long id, String accountType) throws AccountTypeNotExistException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, id);
            if (companyAdminAccount != null && !companyAdminAccount.isLockedOrNot()) {
                companyAdminAccount.setLockedOrNot(Boolean.TRUE);
                em.merge(companyAdminAccount);
                em.flush();
                return companyAdminAccount;
            } else {
                return null;//cannot find account
            }
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, id);
            if (companyUserAccount != null && !companyUserAccount.isLockedOrNot()) {
                companyUserAccount.setLockedOrNot(Boolean.TRUE);
                em.merge(companyUserAccount);
                em.flush();
                return companyUserAccount;
            } else {
                return null;//cannot find account
            }
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, id);
            if (systemAdminAccount != null && !systemAdminAccount.isLockedOrNot()) {
                systemAdminAccount.setLockedOrNot(Boolean.TRUE);
                em.merge(systemAdminAccount);
                em.flush();
                return systemAdminAccount;
            } else {
                return null;//cannot find account
            }
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    @Override
    public List<Account> retriveDeactivatedAccountList() {
        Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.lockedOrNot='true'");
        List<Account> accountList = new ArrayList(query.getResultList());
        Query query2 = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.lockedOrNot='true'");
        Query query3 = em.createQuery("SELECT c FROM SystemAdminAccount c WHERE c.lockedOrNot='true'");
        accountList.addAll(query2.getResultList());
        accountList.addAll(query3.getResultList());
        return accountList;
    }

    // Reset password
    @Override
    public boolean resetPassword(Long accountId, String accountType, String email) throws AccountTypeNotExistException {

        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
            String password = GeneratePassword.createPassword();
            String salt = companyAdminAccount.getSalt();
            //send password to user through email
            try {
                SendResetEmail(companyAdminAccount.getCompanyAdmin().getFirstName(), companyAdminAccount.getCompanyAdmin().getLastName(), companyAdminAccount.getUsername(), companyAdminAccount.getCompanyAdmin().getEmail(), password, companyAdminAccount.getCompany().getCompanyName());
            } catch (MessagingException ex) {
                System.out.println("Error sending email.");
                Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            password = passwordHash(password + salt);
            companyAdminAccount.setPassword(password);
            em.merge(companyAdminAccount);
            em.flush();
            return true;
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, accountId);
            String password = GeneratePassword.createPassword();
            String salt = companyUserAccount.getSalt();
            //send password to user through email
            try {
                SendResetEmail(companyUserAccount.getCompanyUser().getFirstName(), companyUserAccount.getCompanyUser().getLastName(), companyUserAccount.getUsername(), companyUserAccount.getCompanyUser().getEmail(), password, companyUserAccount.getCompany().getCompanyName());
            } catch (MessagingException ex) {
                System.out.println("Error sending email.");
                Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            password = passwordHash(password + salt);
            companyUserAccount.setPassword(password);
            em.merge(companyUserAccount);
            em.flush();
            return true;
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, accountId);
            String password = GeneratePassword.createPassword();
            String salt = systemAdminAccount.getSalt();
            //send password to user through email
            try {
                SendResetEmail(systemAdminAccount.getSystemAdmin().getFirstName(), systemAdminAccount.getSystemAdmin().getLastName(), systemAdminAccount.getUsername(), systemAdminAccount.getSystemAdmin().getEmail(), password, systemAdminAccount.getCompany().getCompanyName());
            } catch (MessagingException ex) {
                System.out.println("Error sending email.");
                Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            password = passwordHash(password + salt);
            systemAdminAccount.setPassword(password);
            em.merge(systemAdminAccount);
            em.flush();
            return true;
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    // Check password complexity
    @Override
    public boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) {
            return false;
        }

        char pw[] = password.toCharArray();
        boolean alphabet = false;
        boolean digit = false;
        for (int i = 0; i < pw.length; i++) {
            if (Character.isLetter(pw[i])) {
                alphabet = true;
            }
            if (Character.isDigit(pw[i])) {
                digit = true;
            }
            if (alphabet && digit) {
                return true;
            }
        }
        return false;
    }

    // Hash password function
    private String passwordHash(String pass) {
        String md5 = null;

        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(pass.getBytes(), 0, pass.length());

            //Converts message digest value in base 16 (hex) 
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    // Retrieve account
    @Override
    public Account retrieveAccount(Long accountId, String accountType) throws AccountTypeNotExistException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
            System.out.println("The retrieved Account is " + companyAdminAccount.getUsername());
            return companyAdminAccount;
        } else if (accountType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, accountId);
            System.out.println("The retrieved Account is " + companyUserAccount.getUsername());
            companyUserAccount.getCompanyUser();
            return companyUserAccount;
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, accountId);
            System.out.println("The retrieved Account is " + systemAdminAccount.getUsername());
            return systemAdminAccount;
        } else {
            throw new AccountTypeNotExistException("Account type does not exist!");
        }
    }

    @Override
    public Title createTitle(String titleName, Department department) throws TitleExistException {
        Query query = em.createQuery("select t from Title t where t.titleName=?1 AND t.department=?2");
        query.setParameter(1, titleName);
        query.setParameter(2, department);
        ArrayList<Title> titleList = new ArrayList(query.getResultList());
        if (titleList.isEmpty()) {
            System.out.println("TitleList is empty");
        } else if (!titleList.isEmpty()) {
            System.out.println("TitleList is not empty" + titleList.get(0).getTitleName() + " " + titleList.get(0).getDepartment().getDepartmentName());
            throw new TitleExistException("Title " + titleName + " " + department.getId() + " already exists!");
        }
        Title title = new Title(titleName, department);
        department.getTitles().add(title);

        em.persist(title);
        em.merge(department);
        return title;
    }

    @Override
    public Title registerTitle(String titleName, Department department) throws TitleExistException {
        Query query = em.createQuery("select t from Title t where t.titleName=?1 AND t.department=?2");
        query.setParameter(1, titleName);
        query.setParameter(2, department);
        ArrayList<Title> titleList = new ArrayList(query.getResultList());
//        if(titleList.isEmpty()) {
//            System.out.println("TitleList is empty");
//        } else if(!titleList.isEmpty()){
//            System.out.println("TitleList is not empty" + titleList.get(0).getTitleName()+ " "+ titleList.get(0).getDepartment().getDepartmentName());
//            throw new TitleExistException("Title "+titleName + " " + department.getId() + " already exists!");
//        }
        Title title = new Title(titleName, department, Boolean.FALSE);
        department.getTitles().add(title);

        em.persist(title);
        em.merge(department);
        return title;
    }

    @Override
    public Boolean deleteTitle(Long titleId) throws TitleNotExistException {
        Query query = em.createQuery("select t from Title t where t.id=?1");
        query.setParameter(1, titleId);
        ArrayList<Title> titleList = new ArrayList(query.getResultList());
        if (titleList.isEmpty()) {
            throw new TitleNotExistException("Title " + titleId + " does not exists!");
        }
        Title title = em.find(Title.class, titleId);
        if (!title.getUser().isEmpty()) {
            return Boolean.FALSE;//title associate with user, cannot be deleted!
        }
        try {
            em.remove(title);
            em.flush();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    private void SendEmail(String firstName, String lastName, String username, String email, String password, String companyName) throws MessagingException {
        String subject = "Merlion Platform - Account \"" + username + "\" Created - Pending Approval";
        System.out.println("Inside send email");

        String content = "<h2>Dear " + firstName + " " + lastName
                + ",</h2><br /><h1>  Congratulations! You have successfully registered on MerLION Platform!</h1><br />"
                + "<h1>Welcome to Merlion Platform.</h1>"
                + "<h2 align=\"center\">Username: " + username
                + "<br />Password: " + password + "<br />Company: " + companyName + "</h2><br />"
                + "<p style=\"color: #ff0000;\">Please remember to change your password on your first login after your account being approved. Thank you.</p>"
                + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
        System.out.println(content);
        sendEmail.run(email, subject, content);
    }

    private void SendResetEmail(String firstName, String lastName, String username, String email, String password, String companyName) throws MessagingException {
        String subject = "Merlion Platform - Account \"" + username + "\" Created - Pending Approval";
        System.out.println("Inside send email");

        String content = "<h2>Dear " + firstName + " " + lastName
                + ",</h2><br />Our system detect that you have reset your password. Please refer to the following information.<br />"
                + "<h2 align=\"center\">Username: " + username
                + "<br />New Password: " + password + "<br />Company: " + companyName + "</h2><br />"
                + "<p style=\"color: #ff0000;\">Please remember to change your password on your first login after you change your password. Thank you.</p>"
                + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
        System.out.println(content);
        sendEmail.run(email, subject, content);
    }

    @Override
    public Boolean checkLock(String username) throws AccountTypeNotExistException, UserNotExistException {
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String accountType = temp.get(0).getAccountType();

        if (accountType.equals("Admin")) {
            Query query = em.createQuery("select c from CompanyAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyAdminAccount> companyAdminAccountList = new ArrayList(query.getResultList());
            if (companyAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyAdminAccount companyAdminAccount = companyAdminAccountList.get(0);
            if (companyAdminAccount.isLockedOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (accountType.equals("User")) {
            Query query = em.createQuery("select c from CompanyUserAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyUserAccount> companyUserAccountList = new ArrayList(query.getResultList());
            if (companyUserAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyUserAccount companyUserAccount = companyUserAccountList.get(0);
            if (companyUserAccount.isLockedOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (accountType.equals("SystemAdmin")) {
            Query query = em.createQuery("select c from SystemAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<SystemAdminAccount> systemAdminAccountList = new ArrayList(query.getResultList());
            if (systemAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            SystemAdminAccount systemAdminAccount = systemAdminAccountList.get(0);
            if (systemAdminAccount.isLockedOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else {
            throw new AccountTypeNotExistException("Username: " + username + " does not exist!");
        }
    }

    @Override
    public Boolean checkDelete(String username) throws AccountTypeNotExistException, UserNotExistException {
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String accountType = temp.get(0).getAccountType();

        if (accountType.equals("Admin")) {
            Query query = em.createQuery("select c from CompanyAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyAdminAccount> companyAdminAccountList = new ArrayList(query.getResultList());
            if (companyAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyAdminAccount companyAdminAccount = companyAdminAccountList.get(0);
            if (companyAdminAccount.isDeleteOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (accountType.equals("User")) {
            Query query = em.createQuery("select c from CompanyUserAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyUserAccount> companyUserAccountList = new ArrayList(query.getResultList());
            if (companyUserAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyUserAccount companyUserAccount = companyUserAccountList.get(0);
            if (companyUserAccount.isDeleteOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (accountType.equals("SystemAdmin")) {
            Query query = em.createQuery("select c from SystemAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<SystemAdminAccount> systemAdminAccountList = new ArrayList(query.getResultList());
            if (systemAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            SystemAdminAccount systemAdminAccount = systemAdminAccountList.get(0);
            if (systemAdminAccount.isDeleteOrNot()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else {
            throw new AccountTypeNotExistException("Username: " + username + " does not exist!");
        }
    }

    @Override
    public Account retrieveAccount(String username) throws AccountTypeNotExistException, UserNotExistException {
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String accountType = temp.get(0).getAccountType();

        if (accountType.equals("Admin")) {
            Query query = em.createQuery("select c from CompanyAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyAdminAccount> companyAdminAccountList = new ArrayList(query.getResultList());
            if (companyAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyAdminAccount companyAdminAccount = companyAdminAccountList.get(0);
            return companyAdminAccount;
        } else if (accountType.equals("User")) {
            Query query = em.createQuery("select c from CompanyUserAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<CompanyUserAccount> companyUserAccountList = new ArrayList(query.getResultList());
            if (companyUserAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            CompanyUserAccount companyUserAccount = companyUserAccountList.get(0);
            return companyUserAccount;
        } else if (accountType.equals("SystemAdmin")) {
            Query query = em.createQuery("select c from SystemAdminAccount c where c.username= :user");
            query.setParameter("user", username);
            ArrayList<SystemAdminAccount> systemAdminAccountList = new ArrayList(query.getResultList());
            if (systemAdminAccountList.isEmpty()) {
                throw new UserNotExistException("Username: " + username + " does not exists!");
            }
            SystemAdminAccount systemAdminAccount = systemAdminAccountList.get(0);
            return systemAdminAccount;
        } else {
            throw new AccountTypeNotExistException("Username: " + username + " does not exist!");
        }
    }

    @Override
    public boolean updateProfile(Long accountId, String accountType, String username, String firstName, String lastName, String email, String contactNo) throws UserExistException {
        if (accountType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
            if (companyAdminAccount == null) {
                return false;//cannot find account
            }
            if (username != null && !username.equals(companyAdminAccount.getUsername())) {
                Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.username = :user");
                query.setParameter("user", username);
                List<CompanyAdminAccount> companyAdminAccountList = query.getResultList();
                if (!companyAdminAccountList.isEmpty()) {
                    throw new UserExistException("Username already exists!");
                }
                companyAdminAccount.setUsername(username);
            }
            CompanyAdmin companyAdmin = companyAdminAccount.getCompanyAdmin();
            if (firstName != null && !firstName.equals(companyAdmin.getFirstName())) {
                companyAdmin.setFirstName(firstName);
            }
            if (lastName != null && !lastName.equals(companyAdmin.getLastName())) {
                companyAdmin.setLastName(lastName);
            }
            if (email != null && !email.equals(companyAdmin.getEmail())) {
                companyAdmin.setEmail(email);
            }
            if (contactNo != null && !contactNo.equals(companyAdminAccount.getContactNo())) {
                companyAdminAccount.setContactNo(contactNo);
            }

            em.merge(companyAdminAccount);
            em.merge(companyAdmin);
            em.flush();
            return true;
        } else if (accountType.equals("User")) {
            System.out.println("Inside session bean");
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, accountId);
            if (companyUserAccount == null) {
                return false;//cannot find account
            }
            if (username != null && !username.equals(companyUserAccount.getUsername())) {
                Query query = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.username = :user");
                query.setParameter("user", username);
                List<CompanyUserAccount> companyUserAccountList = query.getResultList();
                if (!companyUserAccountList.isEmpty()) {
                    throw new UserExistException("Username already exists!");
                }
                companyUserAccount.setUsername(username);
            }
            CompanyUser companyUser = companyUserAccount.getCompanyUser();
            if (firstName != null && !firstName.equals(companyUser.getFirstName())) {
                companyUser.setFirstName(firstName);
            }
            if (lastName != null && !lastName.equals(companyUser.getLastName())) {
                companyUser.setLastName(lastName);
            }
            if (email != null && !email.equals(companyUser.getEmail())) {
                companyUser.setEmail(email);
            }
            if (contactNo != null && !contactNo.equals(companyUserAccount.getContactNo())) {
                companyUserAccount.setContactNo(contactNo);
            }

            em.merge(companyUserAccount);
            em.merge(companyUser);
            em.flush();
            return true;
        } else if (accountType.equals("SystemAdmin")) {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, accountId);
            if (systemAdminAccount == null) {
                return false;//cannot find account
            }
            if (username != null && !username.equals(systemAdminAccount.getUsername())) {
                Query query = em.createQuery("SELECT c FROM SystemAdminAccount c WHERE c.username = :user");
                query.setParameter("user", username);
                List<SystemAdminAccount> systemAdminAccountList = query.getResultList();
                if (!systemAdminAccountList.isEmpty()) {
                    throw new UserExistException("Username already exists!");
                }
                systemAdminAccount.setUsername(username);
            }
            SystemAdmin systemAdmin = systemAdminAccount.getSystemAdmin();
            if (firstName != null && !firstName.equals(systemAdmin.getFirstName())) {
                systemAdmin.setFirstName(firstName);
            }
            if (lastName != null && !lastName.equals(systemAdmin.getLastName())) {
                systemAdmin.setLastName(lastName);
            }
            if (email != null && !email.equals(systemAdmin.getEmail())) {
                systemAdmin.setEmail(email);
            }
            if (contactNo != null && !contactNo.equals(systemAdminAccount.getContactNo())) {
                systemAdminAccount.setContactNo(contactNo);
            }

            em.merge(systemAdminAccount);
            em.merge(systemAdmin);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Account createAccountForPopulateData(String username, String userType, String firstName, String lastName, String phoneNo, String email, Department department, Title title, Company company, String companyType) throws UserExistException {
        String salt = "";
        String letters = "0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
        System.out.println("Inside createAccount");

        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if (!temp.isEmpty()) {
            System.out.println("User " + username + " exists!");
            throw new UserExistException("User " + username + " exists!");
        }
        System.out.println("Username passes check!");

        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            salt += letters.substring(index, index + 1);
        }
        String password = "password";
        password = passwordHash(password + salt);
        System.out.println("Password after hash&salt: " + password);
        if (userType.equals("Admin")) {
            //admin account creation --by system admin
            System.out.println("In Creating company admin account");
            CompanyAdminAccount companyAdminAccount = new CompanyAdminAccount(username, password, salt, department, company, "Admin");
            companyAdminAccount.setContactNo(phoneNo);
            department.getAccount().add(companyAdminAccount);
            company.getAccount().add(companyAdminAccount);
            System.out.println("Account successfully created");
            CompanyAdmin companyAdmin = new CompanyAdmin(firstName, lastName, email, companyAdminAccount);
            companyAdminAccount.setCompanyAdmin(companyAdmin);
            System.out.println("User successfully created!");
            title.getUser().add(companyAdmin);
            companyAdmin.setTitle(title);
            System.out.println("User Title associated!");
            em.persist(companyAdminAccount);
            em.persist(companyAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("Company admin account, account ID: " + companyAdminAccount.getId());
            return companyAdminAccount;
        } else if (userType.equals("SystemAdmin")) {
            //system admin account creation --by system admin
            SystemAdminAccount systemAdminAccount = new SystemAdminAccount(username, password, salt, department, company, "SystemAdmin");
            systemAdminAccount.setContactNo(phoneNo);
            department.getAccount().add(systemAdminAccount);
            company.getAccount().add(systemAdminAccount);
            SystemAdmin systemAdmin = new SystemAdmin(firstName, lastName, email, systemAdminAccount);
            systemAdminAccount.setSystemAdmin(systemAdmin);
            title.getUser().add(systemAdmin);
            systemAdmin.setTitle(title);
            em.persist(systemAdminAccount);
            em.persist(systemAdmin);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("System admin account, account ID: " + systemAdminAccount.getId());
            return systemAdminAccount;
        } else {
            //merlion user account creation --by system admin
            CompanyUserAccount companyUserAccount = new CompanyUserAccount(username, password, salt, department, company, "User");
            companyUserAccount.setContactNo(phoneNo);
            department.getAccount().add(companyUserAccount);
            company.getAccount().add(companyUserAccount);
            CompanyUser companyUser = new CompanyUser(firstName, lastName, email, companyUserAccount);
            companyUserAccount.setCompanyUser(companyUser);
            title.getUser().add(companyUser);
            companyUser.setTitle(title);
            em.persist(companyUserAccount);
            em.persist(companyUser);
            em.merge(title);
            em.merge(department);
            em.merge(company);
            System.out.println("Company user account, account ID: " + companyUserAccount.getId());
            return companyUserAccount;
        }
    }

    @Override
    public Title retrieveTitle(String titleName, Department department) throws TitleNotExistException {
        Query query = em.createQuery("select t from Title t where t.titleName=?1 and t.department=?2");
        query.setParameter(1, titleName);
        query.setParameter(2, department);
        ArrayList<Title> titleList = new ArrayList(query.getResultList());
        if (titleList.isEmpty()) {
            throw new TitleNotExistException("Title " + titleName + " for department " + department.getDepartmentName() + " does not already exist");
        }
        Title title = titleList.get(0);
        return title;
    }

    @Override
    public Title retrieveTitle(Long titleId) throws TitleNotExistException {
        Title title = em.find(Title.class, titleId);
        if (title == null) {
            throw new TitleNotExistException("Title " + titleId + " does not exist!");
        }
        return title;
    }

    @Override
    public List<Account> retrieveAllCompanyUserAccount(Long companyId, String companyType) throws CompanyNotExistException {
        if (companyType.equals("1PL")) {
            CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
            if (customerCompany == null) {
                throw new CompanyNotExistException("Company: " + companyId + " does not exist!");
            }
            List<Account> companyUserAccountList = customerCompany.getAccount();
            List<Account> accountList = new ArrayList<>();
            for (Account a : companyUserAccountList) {
                if (!a.getDepartment().getDepartmentName().equals("Account Management")) {
                    if (!a.isDeleteOrNot() && !a.isLockedOrNot()) {
                        accountList.add(a);
                    }
                }
            }
            return accountList;
        } else if (companyType.equals("5PL")) {
            Company company = em.find(Company.class, companyId);
            if (company == null) {
                throw new CompanyNotExistException("Company: " + companyId + " does not exist!");
            }
            List<Account> companyUserAccountList = company.getAccount();
            List<Account> accountList = new ArrayList<>();
            for (Account a : companyUserAccountList) {
                if (!a.isDeleteOrNot() && !a.isLockedOrNot()) {
                    accountList.add(a);
                }
            }
            return accountList;
        } else {
            PartnerCompany partnerCompany = em.find(PartnerCompany.class, companyId);
            if (partnerCompany == null) {
                throw new CompanyNotExistException("Company: " + companyId + " does not exist!");
            }
            List<Account> companyUserAccountList = partnerCompany.getAccount();
            List<Account> accountList = new ArrayList<>();
            for (Account a : companyUserAccountList) {
                if (!a.isDeleteOrNot() && !a.isLockedOrNot()) {
                    accountList.add(a);
                }
            }
            return accountList;
        }
    }

    @Override
    public List<CompanyUserAccount> retrieveAllSystemUserAccount() {
        Query query = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.deleteOrNot='false' AND c.lockedOrNot='false'");
        List<CompanyUserAccount> accountList = new ArrayList(query.getResultList());
//        Query query2 = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.deleteOrNot='false'");
//        accountList.addAll(query2.getResultList());
        return accountList;
    }

    @Override
    public List<CompanyAdminAccount> retrieveAllCompanyAdminAccount() {
        Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.deleteOrNot='false' AND c.lockedOrNot='false'");
        List<CompanyAdminAccount> accountList = new ArrayList(query.getResultList());
        return accountList;
    }

    @Override
    public CompanyUser retrieveCompanyUser(Long accountId) throws UserNotExistException {
        System.out.println("Inside retrieve company user");
        Query q = em.createQuery("SELECT c FROM CompanyUserAccount c WHERE c.id=:ID AND c.deleteOrNot='false'");
        q.setParameter("ID", accountId);
        System.out.println("accountId is " + accountId);
        List<CompanyUserAccount> companyUserAccountList = new ArrayList(q.getResultList());
        CompanyUserAccount companyUserAccount = companyUserAccountList.get(0);
        System.out.println("Company User Account is " + companyUserAccount);
        Query query = em.createQuery("SELECT u FROM CompanyUser u WHERE u.companyUserAccount=:acc AND u.deleteOrNot='false'");
        query.setParameter("acc", companyUserAccount);
        List<CompanyUser> companyUserList = new ArrayList(query.getResultList());
        CompanyUser companyUser = companyUserList.get(0);
        System.out.println("company User is " + companyUser);
        return companyUser;
    }

    @Override
    public CompanyAdmin retrieveCompanyAdmin(Long accountId) throws UserNotExistException {
        System.out.println("Inside retrieve company admin");
        Query q = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.id=:ID AND c.deleteOrNot='false'");
        q.setParameter("ID", accountId);
        System.out.println("accountId is " + accountId);
        List<CompanyAdminAccount> companyAdminAccountList = new ArrayList(q.getResultList());
        CompanyAdminAccount companyAdminAccount = companyAdminAccountList.get(0);
        System.out.println("Company admin Account is " + companyAdminAccount);
        Query query = em.createQuery("SELECT u FROM CompanyAdmin u WHERE u.companyAdminAccount=:acc AND u.deleteOrNot='false'");
        query.setParameter("acc", companyAdminAccount);
        List<CompanyAdmin> companyAdminList = new ArrayList(query.getResultList());
        CompanyAdmin companyAdmin = companyAdminList.get(0);
        return companyAdmin;
    }

    @Override
    public SystemAdmin retrieveSystemAdmin(Long accountId) throws UserNotExistException {
        System.out.println("Inside retrieve system admin");
        Query q = em.createQuery("SELECT s FROM SystemAdminAccount s WHERE s.id=:ID AND s.deleteOrNot='false'");
        q.setParameter("ID", accountId);
        System.out.println("accountId is " + accountId);
        List<SystemAdminAccount> systemAdminAccountList = new ArrayList(q.getResultList());
        SystemAdminAccount systemAdminAccount = systemAdminAccountList.get(0);
        System.out.println("Company admin Account is " + systemAdminAccount);
        Query query = em.createQuery("SELECT u FROM SystemAdmin u WHERE u.systemAdminAccount=:acc AND u.deleteOrNot='false'");
        query.setParameter("acc", systemAdminAccount);
        List<SystemAdmin> systemAdminList = new ArrayList(query.getResultList());
        SystemAdmin systemAdmin = systemAdminList.get(0);
        return systemAdmin;
    }

    @Override
    public List<CompanyAdminAccount> retrieveUnapprovedSystemAdminAccount() {
        Query query = em.createQuery("SELECT c FROM CompanyAdminAccount c WHERE c.approvedOrNot=:approved");
        query.setParameter("approved", Boolean.FALSE);
        return new ArrayList(query.getResultList());
    }

    @Override
    public boolean changeTitle(Long accountId, String userType, Long userId, Title title) {
        if (userType.equals("Admin")) {
            CompanyAdmin companyAdmin = em.find(CompanyAdmin.class, userId);
            if (!companyAdmin.getTitle().getTitleName().equals(title.getTitleName())) {
                companyAdmin.setTitle(title);
            }
            return true;
        } else if (userType.equals("User")) {
            CompanyUser companyUser = em.find(CompanyUser.class, userId);
            if (!companyUser.getTitle().getTitleName().equals(title.getTitleName())) {
                companyUser.setTitle(title);
            }
            return true;
        } else {
            SystemAdmin systemAdmin = em.find(SystemAdmin.class, userId);
            if (!systemAdmin.getTitle().getTitleName().equals(title.getTitleName())) {
                systemAdmin.setTitle(title);
            }
            return true;
        }
    }

    @Override
    public boolean changeDepartment(Long accountId, String userType, Long userId, Department department, Title title) {
        if (userType.equals("Admin")) {
            CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
            if (!companyAdminAccount.getDepartment().getDepartmentName().equals(department.getDepartmentName())) {
                companyAdminAccount.setDepartment(null);
                CompanyAdmin companyAdmin = em.find(CompanyAdmin.class, userId);
                companyAdmin.setTitle(null);

                companyAdminAccount.setDepartment(department);
                companyAdmin.setTitle(title);
            }
            return true;
        } else if (userType.equals("User")) {
            CompanyUserAccount companyUserAccount = em.find(CompanyUserAccount.class, accountId);
            if (!companyUserAccount.getDepartment().getDepartmentName().equals(department.getDepartmentName())) {
                companyUserAccount.setDepartment(null);
                CompanyUser companyUser = em.find(CompanyUser.class, userId);
                companyUser.setTitle(null);

                companyUserAccount.setDepartment(department);
                companyUser.setTitle(title);
            }
            return true;
        } else {
            SystemAdminAccount systemAdminAccount = em.find(SystemAdminAccount.class, accountId);
            if (!systemAdminAccount.getDepartment().getDepartmentName().equals(department.getDepartmentName())) {
                systemAdminAccount.setDepartment(null);
                SystemAdmin systemAdmin = em.find(SystemAdmin.class, userId);
                systemAdmin.setTitle(null);

                systemAdminAccount.setDepartment(department);
                systemAdmin.setTitle(title);
            }
            return true;
        }
    }

    @Override
    public Scheduler createEvent(Long accountId, String eventId, String event, Boolean allDay, Timestamp startTime, Timestamp endTime) {
        Scheduler scheduler = new Scheduler(accountId, eventId, event, startTime, endTime, allDay);
        em.persist(scheduler);
        return scheduler;
    }

    @Override
    public Boolean updateEvent(Long accountId, String eventId, String event, Boolean allDay, Timestamp startTime, Timestamp endTime) {
        Query query = em.createQuery("SELECT s FROM Scheduler s WHERE s.eventId=:id and s.accountId=:acc");
        query.setParameter("id", eventId);
        query.setParameter("acc", accountId);
        List<Scheduler> sList = new ArrayList(query.getResultList());
        if(sList.isEmpty()) {
            return false;
        }
        Scheduler scheduler = sList.get(0);
        scheduler.setAllDay(allDay);
        scheduler.setStartTime(startTime);
        scheduler.setEndTime(endTime);
        scheduler.setEvent(event);
        em.merge(scheduler);
        return true;
    }

    @Override
    public List<Scheduler> retrieveSchedule(Long accountId) {
        Query query = em.createQuery("SELECT s FROM Scheduler s WHERE s.accountId=:acc");
        query.setParameter("acc", accountId);
        List<Scheduler> sList = new ArrayList(query.getResultList());
        return sList;
    }
}
