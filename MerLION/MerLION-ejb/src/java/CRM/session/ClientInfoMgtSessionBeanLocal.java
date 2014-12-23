/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.KeyClient;
import CRM.entity.ServiceOrder;
import Common.entity.Company;
import Common.entity.CustomerCompany;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.KeyClientNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.ServiceOrderNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface ClientInfoMgtSessionBeanLocal {
    //B.4.b.1
    public String addClientRecord(Long ownerId, String companyName, String contactNo) throws CompanyNotExistException,MembershipSchemaNotExistException;    
    //B.4.b.2
    public String updateClientRecord(Long ownerId, Long Id, String companyName, String membershipTier, String contactNo, Integer cumulativeMembershipPoints) throws CompanyNotExistException, KeyClientNotExistException;
    //B.4.b.3
    public List<KeyClient> viewAllClientCompany(Long ownerId) throws KeyClientNotExistException;
    
    public KeyClient viewClientCompany(Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException;  
    public String deleteClientCompany (Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException;
    //B.4.b.4
    public String assignMembershipTier(Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException;
    //B.4.b.5
    public String calculateMembershipPoint(Long ownerId, Long soId)throws CompanyNotExistException, KeyClientNotExistException, ServiceOrderNotExistException;

}
