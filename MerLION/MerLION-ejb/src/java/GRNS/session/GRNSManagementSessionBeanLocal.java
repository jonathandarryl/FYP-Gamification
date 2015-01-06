/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GRNS.session;

import Common.entity.Location;
import GRNS.entity.ExternalUserAccount;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotActivatedException;
import util.exception.CompanyExistException;
import util.exception.PasswordNotMatchException;
import util.exception.PasswordTooSimpleException;
import util.exception.UserExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author chongyangsun
 */
@Local
public interface GRNSManagementSessionBeanLocal {
    public ExternalUserAccount createExternalUserAccount(String username, String password, String firstName, String lastName, 
            String email, String contact, String companyName, String companyVAT, Location location, String companyContact) throws UserExistException, PasswordTooSimpleException, CompanyExistException;
    public boolean checkLogin(String username, String password) throws UserNotExistException, PasswordNotMatchException, AccountNotActivatedException;
    public Boolean updateExternalUserAccount(Long id, String firstName, String lastName, 
            String email, String contact, String companyName, String companyContact);
    public List<ExternalUserAccount> retrieveExternalUserAccountList();
    public Boolean activateExternalUserAccount(String verificationKey);
    public boolean resetPassword(String username, String email);
    public boolean checkPasswordComplexity(String password);
    public boolean changePassword(Long id, String oldPassword, String newPassword) throws PasswordTooSimpleException;
    public ExternalUserAccount retrieveExternalUserAccount(String username) throws UserNotExistException;
    public ExternalUserAccount retrieveExternalUserAccount(Long id) throws UserNotExistException;
}
