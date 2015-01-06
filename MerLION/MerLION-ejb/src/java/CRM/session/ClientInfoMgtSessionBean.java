/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.KeyClient;
import CRM.entity.MembershipSchema;
import CRM.entity.ServiceOrder;
import Common.entity.Company;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.KeyClientNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.ServiceOrderNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class ClientInfoMgtSessionBean implements ClientInfoMgtSessionBeanLocal, ClientSessionBeanRemote {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private MembershipSchemaMgtSessionBeanLocal msm;
    @EJB
    private ServiceOrderManagementLocal som;

    //only for 1PL
    //B.3.1
    @Override
    public String addClientRecord(Long ownerId, String companyName, String contactNo)
            throws CompanyNotExistException, MembershipSchemaNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " does not exist!");
        } else {
            //System.out.println("client record "+ownerId);
            //MembershipSchema ms = em.find(MembershipSchema.class, memSchemaId);
//            Query query = em.createQuery("SELECT k FROM MembershipSchema k WHERE k.ownerCompany.id=?1");
//            query.setParameter(1, ownerId);
//            @SuppressWarnings("unchecked")
            List<MembershipSchema> msList = pc.getMembershipSchema();
            MembershipSchema ms = new MembershipSchema();
            if (msList.isEmpty()) {
                System.out.println("msList is empty");
                throw new MembershipSchemaNotExistException("Membership schema List not exist! ");
            }
            for (MembershipSchema o : msList) {
                if (o.getBoundaryPoint().equals(0)) {
                    ms = o;
                    break;
                }
            }
            if (ms == null) {
                throw new MembershipSchemaNotExistException("Membership schema not exist! ");
            }
            String membershipTier = ms.getMembershipTier();
            List<KeyClient> kcList = pc.getKeyClient();
            if (!keyClientExist(companyName, kcList)) {
                KeyClient kc = new KeyClient();
                kc.setCompany(pc);
                kc.setCompanyName(companyName);
                kc.setCumulativeMembershipPoints(0);
                kc.setMembershipSchema(ms);
                kc.setMembershipTier(membershipTier);
                kc.setContactNo(contactNo);
                pc.getKeyClient().add(kc);
                em.persist(pc);
                em.persist(kc);

                return "New client company added successfully!";
            }
            return "Client already exist!";
        }
    }

    private Boolean keyClientExist(String clientName, List<KeyClient> kcList) {
        for (Object o : kcList) {
            KeyClient curKc = (KeyClient) o;
            if (curKc.getCompanyName().equals(clientName)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    //B.3.2
    @Override
    public String updateClientRecord(Long ownerId, Long Id, String companyName, String membershipTier, String contactNo, Integer cumulativeMembershipPoints)
            throws CompanyNotExistException, KeyClientNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " does not exist!");
        } else {
            KeyClient kc = em.find(KeyClient.class, Id);
            if (kc == null) {
                throw new KeyClientNotExistException("Key Client " + Id + " not exist!");
            } else {
                kc.setCompanyName(companyName);
                kc.setContactNo(contactNo);
                kc.setCumulativeMembershipPoints(cumulativeMembershipPoints);
                kc.setMembershipTier(membershipTier);
                em.persist(kc);
                return "\nClient company information updated successfully!";
            }
        }
    }

    //B.3.3
    @SuppressWarnings("unchecked")
    @Override
    public List<KeyClient> viewAllClientCompany(Long ownerId) throws KeyClientNotExistException {
        Query query;
        query = em.createQuery("SELECT k FROM KeyClient k WHERE k.company.id=?1");
        query.setParameter(1, ownerId);
        List<KeyClient> kList = query.getResultList();
        if (kList == null) {
            throw new KeyClientNotExistException("Key Clisnt " + ownerId + " not exist!");
        } else {
            return kList;
        }
    }

    @Override
    public KeyClient viewClientCompany(Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            KeyClient kc = em.find(KeyClient.class, Id);
            if (kc == null) {
                throw new KeyClientNotExistException("Key Client " + Id + " not exist!");
            } else {
                return kc;
            }
        }
    }

    //B.3.4
    @Override
    public String deleteClientCompany(Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            //System.out.println("before find kc");
            KeyClient kc = em.find(KeyClient.class, Id);
            if (kc == null) {
                throw new KeyClientNotExistException("Key Client " + Id + " not exist!");
            } else {
                //System.out.println("before remove");
                em.remove(kc);
                return "\nCustomer company information delete successfully!";
            }
        }
    }

    //B.4.b.4
    @Override
    public String calculateMembershipPoint(Long ownerId, Long soId) throws CompanyNotExistException, KeyClientNotExistException, ServiceOrderNotExistException {
        Company customerCompany = em.find(Company.class, ownerId);
        if (customerCompany == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            //get new points, converted from order price
            ServiceOrder order = som.retrieveServiceOrder(soId);
            Integer newPoints = (int) (order.getPrice() / 1000);
            String cName = order.getServiceContract().getServiceQuotation().getClientCompany().getCompanyName();
            //add to correponding client account
            Query query;
            query = em.createQuery("SELECT k FROM KeyClient k WHERE k.companyName=?1 ");
            query.setParameter(1, cName);
            if (query.getResultList() == null) {
                throw new KeyClientNotExistException("Key Client " + ownerId + " not exist!");
            } else {
                KeyClient client = (KeyClient) query.getResultList().get(0);
                //old points add new points
                client.setCumulativeMembershipPoints(newPoints + client.getCumulativeMembershipPoints());
                em.merge(client);
                em.merge(customerCompany);
                assignMembershipTier(ownerId, client.getId());
                em.merge(client);
                em.merge(customerCompany);
                return "\nClient cumulative membership points updated successfully!";

            }
        }
    }

    @Override
    public String assignMembershipTier(Long ownerId, Long Id) throws CompanyNotExistException, KeyClientNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Key Client " + ownerId + " not exist!");
        } else {
            KeyClient client = em.find(KeyClient.class, Id);
            if (client == null) {
                throw new KeyClientNotExistException("Key Client " + Id + " not exist!");
            } else {
                Integer points = client.getCumulativeMembershipPoints();
                List<MembershipSchema> mList;
                //mList = new ArrayList<>(msm.viewAllMembershipSchema(ownerId));
                mList = pc.getMembershipSchema();
                ArrayList<Integer> pointList = new ArrayList<>();
                for (MembershipSchema m : mList) {
                    pointList.add(m.getBoundaryPoint());
                }
                Collections.sort(pointList);
                String tier = "";
                if (points >= pointList.get(pointList.size() - 1)) {
                    points = pointList.get(pointList.size() - 1);
                } else {
                    for (int i = 0; i < pointList.size(); i++) {
                        if (points < pointList.get(i)) {
                            points = pointList.get(i - 1);
                            break;
                        }
                    }
                }
                for (MembershipSchema m : mList) {
                    if (m.getBoundaryPoint().equals(points)) {
                        tier = m.getMembershipTier();
                        break;
                    }
                }
                client.setMembershipTier(tier);
                em.persist(client);
                return "\nClient membership tie set successfully! Tier: " + tier;
            }
        }
    }
}
