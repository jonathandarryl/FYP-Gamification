/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.MembershipSchema;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface MembershipSchemaMgtSessionBeanLocal {
    //B.4.a.1
    public String createMembershipSchema(Long ownerId, String membershipTier, Integer boundaryPoint, Double discountPerOrder)throws CompanyNotExistException;
    //B.4.a.2
    public String updateClientRecord(Long ownerId, Long Id, String membershipTier, Integer boundaryPoint, Double discountPerOrder)throws CompanyNotExistException, MembershipSchemaNotExistException;
    //B.4.a.3
    public List<MembershipSchema> viewAllMembershipSchema(Long ownerId) throws CompanyNotExistException;
    public MembershipSchema viewMembershipSchema(Long ownerId, Long Id)throws CompanyNotExistException, MembershipSchemaNotExistException;
    //B.4.a.4
    public String deleteMembershipSchema (Long ownerId, Long Id) throws CompanyNotExistException, MembershipSchemaNotExistException;

}
