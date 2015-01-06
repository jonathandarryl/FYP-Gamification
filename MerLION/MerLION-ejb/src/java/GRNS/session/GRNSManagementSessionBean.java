/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNS.session;

import Common.entity.Account;
import Common.entity.ExternalCompany;
import Common.entity.Location;
import Common.session.AccountManagementSessionBean;
import GRNS.entity.ExternalUserAccount;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountNotActivatedException;
import util.exception.CompanyExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;
import util.session.GeneratePassword;
import util.session.SendEmail;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class GRNSManagementSessionBean implements GRNSManagementSessionBeanLocal {

    private static final Random RANDOM = new SecureRandom();
    public static final int SALT_LENGTH = 8;
    @PersistenceContext
    private EntityManager em;
    private SendEmail sendEmail;

    @Override
    public ExternalUserAccount createExternalUserAccount(String username, String password, String firstName, String lastName,
            String email, String contact, String companyName, String companyVAT, Location location, String companyContact) throws UserExistException, PasswordTooSimpleException, CompanyExistException {
        if(!this.checkPasswordComplexity(password)) {
            throw new PasswordTooSimpleException("Password too simple");
        }
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.username=:user");
        q.setParameter("user", username);
        List<Account> temp = new ArrayList(q.getResultList());
        if(!temp.isEmpty()) {
            System.out.println("Username " + username + " exists!");
            throw new UserExistException("Username " + username + " already exist, please try another one");
        }
        
        String salt = "";
        String letters = "0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
        System.out.println("Inside createAccount");

        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            salt += letters.substring(index, index + 1);
        }
        
        password = passwordHash(password + salt);
        String verificationKey = passwordHash(username+password);
        Query query = em.createQuery("SELECT e FROM ExternalCompany e WHERE e.companyName=:name");
        query.setParameter("name", companyName);
        List<ExternalCompany> companyList = new ArrayList(query.getResultList());
        if(!companyList.isEmpty()) {
            throw new CompanyExistException("External Company "+companyName+" Exists!");
        }
        ExternalCompany company = new ExternalCompany(companyName, companyContact);
        company.setVAT(companyVAT);
        company.setLocation(location);

        
        ExternalUserAccount eua = new ExternalUserAccount(username, password, salt, company, firstName, lastName, 
            email, contact, verificationKey);
        eua.setAccountType("External");
        company.setExternalUserAccount(eua);
        em.persist(eua);
        em.persist(company);

        String link = "https://localhost:8181/MerLION-war/GRNSWeb/externalAccount/activation.xhtml?uuid="+verificationKey;
        try {
            SendActivationEmail(firstName, lastName, username, email, link, company.getCompanyName());
        } catch (MessagingException ex) {
            System.out.println("Error sending email.");
            Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eua;
    }
    
    @Override
    public boolean checkLogin(String username, String password) throws UserNotExistException, PasswordNotMatchException, AccountNotActivatedException {
        System.out.println("Inside EJB check login");
        Query q = em.createQuery("SELECT a FROM ExternalUserAccount a WHERE a.username=:user");
        q.setParameter("user", username);
        List<ExternalUserAccount> temp = new ArrayList(q.getResultList());
        if(temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String userType = temp.get(0).getAccountType();
        System.out.println("User type is "+userType);
        ExternalUserAccount eua = temp.get(0);
        
        if (userType.equals("External")) {
            if(!(eua.isActivated() && eua.getVerificationKey().equals(""))) {
                throw new AccountNotActivatedException("Account is not activated. Please check your email.");
            }
//            Query query = em.createQuery("SELECT c FROM ExternalUserAccount c WHERE c.username = :user AND c.lockedOrNot = :lock AND c.deleteOrNot = :delete");
//            query.setParameter("user", username);
//            query.setParameter("lock", false);
//            query.setParameter("delete", false);
            String salt = eua.getSalt();
            password = passwordHash(password + salt);
            if (password.equals(eua.getPassword())) {
                return true;//login successful
            } else {
                System.out.println("password false");
                return false;//password does not match, enter again
            }
        } else {
            System.out.println("User type invalid???");
            return false;//user type invalid
        }
    }

    @Override
    public Boolean updateExternalUserAccount(Long id, String firstName, String lastName,
            String email, String contact, String companyName, String companyContact) {
        ExternalUserAccount eua = em.find(ExternalUserAccount.class, id);
        if(eua == null) {
            return false;
        }
        if(eua.getFirstName().equals(firstName)) {
            eua.setFirstName(firstName);
        }
        if(eua.getLastName().equals(lastName)) {
            eua.setLastName(lastName);
        }
        if(eua.getEmail().equals(email)) {
            eua.setEmail(email);
        }
        if(eua.getContact().equals(contact)) {
            eua.setContact(contact);
        }
        ExternalCompany ec = (ExternalCompany) eua.getExternalCompany();
        if(ec.getCompanyName().equals(companyName)) {
            ec.setCompanyName(companyName);
        }
        if(ec.getContactNo().equals(companyContact)) {
            ec.setContactNo(companyContact);
        }
        em.merge(eua);
        em.merge(ec);
        return true;
    }

    @Override
    public List<ExternalUserAccount> retrieveExternalUserAccountList() {
        Query query = em.createQuery("SELECT e FROM ExternalUserAccount e WHERE e.deleteOrNot='false'");
        return new ArrayList(query.getResultList());
    }

    @Override
    public Boolean activateExternalUserAccount(String verificationKey) {
        System.out.println("inside session been activate user account");
        Query query = em.createQuery("SELECT e FROM ExternalUserAccount e WHERE e.verificationKey=:key");
        query.setParameter("key", verificationKey);
        List<ExternalUserAccount> userList = new ArrayList(query.getResultList());
        if(userList.isEmpty()) {
            System.out.println("list is empty");
            return false;
        } else {
            ExternalUserAccount eua = userList.get(0);
            eua.setActivated(true);
            eua.setVerificationKey("");
            em.merge(eua);
            return true;
        }
    }

    @Override
    public boolean resetPassword(String username, String email) {
        Query query = em.createQuery("SELECT e FROM ExternalUserAccount e WHERE e.username=:user AND e.email=:useremail");
        query.setParameter("user", username);
        query.setParameter("useremail", email);
        List<ExternalUserAccount> userList = new ArrayList(query.getResultList());
        if (userList.isEmpty()) {
            return false;
        }
        ExternalUserAccount eua = userList.get(0);
        String salt = eua.getSalt();
        String password = GeneratePassword.createPassword();
        try {
            SendResetEmail(eua.getFirstName(), eua.getLastName(), eua.getUsername(), eua.getEmail(), password, eua.getExternalCompany().getCompanyName());
        } catch (MessagingException ex) {
            System.out.println("Error sending email.");
            Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        password = passwordHash(password + salt);
        eua.setPassword(password);
        em.merge(eua);
        return true;
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

    private void SendResetEmail(String firstName, String lastName, String username, String email, String password, String companyName) throws MessagingException {
        String subject = "Merlion Platform - GRNS - Account \"" + username + "\" Reset Password";
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
    
    private void SendActivationEmail(String firstName, String lastName, String username, String email, String link, String companyName) throws MessagingException {
        String subject = "Merlion Platform - GRNS - Account \"" + username + "\" Created - Pending Activation";
        System.out.println("Inside send email");

        String content = "<h2>Dear " + firstName + " " + lastName
                + ",</h2><br />Our system detect that you have reset your password. Please refer to the following information.<br />"
                + "<h2 align=\"center\">Username: " + username
                + "<br />Company: " + companyName + "</h2><br />"
                + "<p style=\"color: #ff0000;\">Please click on the following link to activate your account. Thank you.</p>"
                + "<br /><a href=\""+link+"\">"+link+"</a>"
                + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
        System.out.println(content);
        sendEmail.run(email, subject, content);
    }

    @Override
    public boolean changePassword(Long id, String oldPassword, String newPassword) throws PasswordTooSimpleException {
        ExternalUserAccount eua = em.find(ExternalUserAccount.class, id);
        if(eua == null) {
            return false;
        }
        String salt = eua.getSalt();
        String password = eua.getPassword();
        oldPassword = passwordHash(oldPassword+salt);
        if(!oldPassword.equals(password)) {
            return false;
        } else {
            newPassword = passwordHash(newPassword+salt);
            eua.setPassword(newPassword);
            em.merge(eua);
            return true;
        }
    }
    
    @Override
    public ExternalUserAccount retrieveExternalUserAccount(String username) throws UserNotExistException {
        Query q = em.createQuery("SELECT a FROM ExternalUserAccount a WHERE a.username=:user");
        q.setParameter("user", username);
        List<ExternalUserAccount> temp = new ArrayList(q.getResultList());
        if(temp.isEmpty()) {
            System.out.println("Username " + username + " does not exist!");
            throw new UserNotExistException("Username " + username + " does not exist, please try again");
        }
        String userType = temp.get(0).getAccountType();
        ExternalUserAccount eua = temp.get(0);
        return eua;
    }
    
    @Override
    public ExternalUserAccount retrieveExternalUserAccount(Long id) throws UserNotExistException {
        Query q = em.createQuery("SELECT a FROM ExternalUserAccount a WHERE a.id=:i");
        q.setParameter("i", id);
        List<ExternalUserAccount> temp = new ArrayList(q.getResultList());
        if(temp.isEmpty()) {
            System.out.println("Account ID " + id + " does not exist!");
            throw new UserNotExistException("Account ID " + id + " does not exist, please try again");
        }
        String userType = temp.get(0).getAccountType();
        ExternalUserAccount eua = temp.get(0);
        
        return eua;
    }
}
