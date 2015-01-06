/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.MembershipSchema;
import Common.entity.Company;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class MembershipSchemaMgtSessionBean implements MembershipSchemaMgtSessionBeanLocal, MembershipSchemaSessionBeanRemote {
    @PersistenceContext
    private EntityManager em;

    @Override
    public String createMembershipSchema(Long ownerId, String membershipTier, Integer boundaryPoint, Double discountPerOrder) 
            throws CompanyNotExistException{
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else{
            MembershipSchema ms = new MembershipSchema(membershipTier, boundaryPoint, discountPerOrder); 
            ms.setOwnerCompany(c);
            if(c.getMembershipSchema()==null){                
                 List<MembershipSchema> mList  = new ArrayList<>();
                 mList.add(ms);
            }else{
                c.getMembershipSchema().add(ms);
            }
            //em.persist();
            em.persist(c);
            em.persist(ms);
        }
          return "New membership schema added successfully!";
    }

    @Override
    public String updateClientRecord(Long ownerId, Long Id, String membershipTier, Integer boundaryPoint, Double discountPerOrder)
    throws CompanyNotExistException, MembershipSchemaNotExistException{
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else {
            MembershipSchema membershipSchema = em.find(MembershipSchema.class, Id);
            if (membershipSchema == null) {
                throw new MembershipSchemaNotExistException("membership schema "+ Id +" cannot find!");
            } else {
                membershipSchema.setBoundaryPoint(boundaryPoint);
                membershipSchema.setDiscountPerOrder(discountPerOrder);
                membershipSchema.setMembershipTier(membershipTier);
                em.persist(membershipSchema);
                return "\nClient membership schema updated successfully!";
            }
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "unchecked"})
    public List<MembershipSchema> viewAllMembershipSchema(Long ownerId) throws CompanyNotExistException{
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else {
            Query query;
            query = em.createQuery("SELECT c FROM MembershipSchema c WHERE c.ownerCompany.id=?1");
            query.setParameter(1,ownerId);
            return query.getResultList();
//            return c.getMembershipSchema();
        }
    }

    @Override
    public MembershipSchema viewMembershipSchema(Long ownerId, Long Id) throws CompanyNotExistException, MembershipSchemaNotExistException{
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else {
            MembershipSchema ms = em.find(MembershipSchema.class, Id);
            if (ms == null) {
                throw new MembershipSchemaNotExistException("membership schema "+ Id +" cannot find!");
            } else {
                return ms;
            }
        }
    }

    @Override
    public String deleteMembershipSchema (Long ownerId, Long Id) throws CompanyNotExistException, MembershipSchemaNotExistException{
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company "+ ownerId +" does not exist!");
        }else {
            MembershipSchema ms = em.find(MembershipSchema.class, Id);
            if (ms == null) {
                throw new MembershipSchemaNotExistException("membership schema "+ Id +" cannot find!");
            } else {
                System.out.println("before remove");
                em.remove(ms);
                em.persist(c);
                return "\nMembership schema deleted sucessfully!";
            }
        }
    }
}
