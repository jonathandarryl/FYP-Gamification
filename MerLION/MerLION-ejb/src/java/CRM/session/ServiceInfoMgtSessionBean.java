/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.ServiceCatalog;
import Common.entity.Company;
import Common.entity.Location;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.ServiceCatalogNotExistException;
import util.exception.YellowPageNotExistException;

/**
 *
 * @author HanXiangyu 1PL user this to find service provider 4PL/5PL can user
 * this to find partner
 */
@Stateless
public class ServiceInfoMgtSessionBean implements ServiceInfoMgtSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    //B.1.1
    @Override
    public String createServiceCatalog(Long Id, String serviceName, String description, Double price, Timestamp startTime, Timestamp endTime,
            Location source, Location dest, Long warehouseId, Location warehLoca,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Boolean publicOrNot)
            throws CompanyNotExistException {
        Company Company = em.find(Company.class, Id);
        if (Company == null) {
            throw new CompanyNotExistException("Company " + Id + " does not exist!");
        } else {
            if (Company.getCompanyType().equals("CustomerCompany")) {
                return "\nCustomer Company cannot create Service Catalog!";
            } else {
                List<ServiceCatalog> serviceList = Company.getSvcCatalogList();
                ServiceCatalog serviceCatalog = new ServiceCatalog(serviceName, description, price, startTime, endTime, source, dest, warehouseId, warehLoca, perishable, flammable, pharmaceutical, highValue, publicOrNot);
                serviceCatalog.setCompany(Company);
                if (serviceList.isEmpty()) {
                    serviceList = new ArrayList<>();
                    serviceList.add(serviceCatalog);
                    Company.setSvcCatalogList(serviceList);
                } else {
                    serviceList.add(serviceCatalog);
                }
                em.persist(serviceCatalog);

                em.merge(Company);
                return "\nService Catalog information is created successfully!";
            }
        }
    }

    //B.1.2
    @Override
    public String updateServiceCatalog(Long ownerId, Long Id, String serviceName, String description, Double price, Timestamp startTime, Timestamp endTime,
            Location source, Location dest, Long warehouseId, Location warehLoca,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Boolean publicOrNot)
            throws CompanyNotExistException, ServiceCatalogNotExistException {
        ServiceCatalog serviceCatalog = em.find(ServiceCatalog.class, Id);
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " does not exist!");
        } else {
            if (serviceCatalog == null) {
                throw new ServiceCatalogNotExistException("Service catalog " + Id + " does not exist!");
            } else {
                serviceCatalog.setServiceName(serviceName);
                serviceCatalog.setDescription(description);
                serviceCatalog.setPrice(price);
                serviceCatalog.setCompany(c);
                serviceCatalog.setStartTime(startTime);
                serviceCatalog.setEndTime(endTime);
//                serviceCatalog.setAvailPlaneNumber(availPlaneNumber);
//                serviceCatalog.setAvailTruckNumber(availTruckNumber);
//                serviceCatalog.setAvailVesselNumber(availVesselNumber);
//                serviceCatalog.setAvailWarehouseCapacity(availWarehouseCapacity);
                serviceCatalog.setSource(source);
                serviceCatalog.setDest(dest);
                serviceCatalog.setWarehouseId(warehouseId);
                serviceCatalog.setFlammable(flammable);
                serviceCatalog.setHighValue(highValue);
                serviceCatalog.setPerishable(perishable);
                serviceCatalog.setPharmaceutical(pharmaceutical);
                serviceCatalog.setPublicOrNot(publicOrNot);

                em.persist(serviceCatalog);
                em.merge(c);

                return "\nService Catalog information is updated successfully!";
            }
        }
    }

    //B.1.3
    @Override
    @SuppressWarnings("unchecked")
    public List<ServiceCatalog> viewAllServiceCatalog(Long ownerId)
            throws ServiceCatalogNotExistException, CompanyNotExistException {
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query query;
            query = em.createQuery("SELECT s FROM ServiceCatalog s WHERE s.company.id=?1 and s.archivedOrNot=FALSE");
            query.setParameter(1, ownerId);
            List<ServiceCatalog> kList = query.getResultList();
            if (kList == null) {
                throw new ServiceCatalogNotExistException("Service Catalog " + ownerId + " not exist!");
            } else {
                return kList;
            }
        }
    }

    @Override
    public ServiceCatalog viewServiceCatalog(Long ownerId, Long Id)
            throws ServiceCatalogNotExistException, CompanyNotExistException {

        Company c = em.find(Company.class, ownerId);

        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            ServiceCatalog kc = em.find(ServiceCatalog.class, Id);
            if (kc == null || !Objects.equals(kc.getCompany().getId(), ownerId)) {
                throw new ServiceCatalogNotExistException("Key Client " + Id + " not exist!");
            } else {
                return kc;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ServiceCatalog> viewAllPublicServiceCatalog(Long ownerId)
            throws ServiceCatalogNotExistException, CompanyNotExistException {
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query query;
            query = em.createQuery("SELECT s FROM ServiceCatalog s WHERE s.company.id=?1 and s.archivedOrNot=FALSE and s.publicOrNot=TRUE");
            query.setParameter(1, ownerId);
            List<ServiceCatalog> kList = query.getResultList();
            if (kList == null) {
                throw new ServiceCatalogNotExistException("Service Catalog " + ownerId + " not exist!");
            } else {
                return kList;
            }
        }
    }

    @Override
    public List<ServiceCatalog> retrievePublicServiceCatalog() throws ServiceCatalogNotExistException {
        Query query = em.createQuery("SELECT s FROM ServiceCatalog s WHERE s.archivedOrNot=FALSE and s.publicOrNot=TRUE");
        List<ServiceCatalog> kList = query.getResultList();
        if (kList == null) {
            throw new ServiceCatalogNotExistException("No company has yet published any Service Catalog!");
        }
        return kList;
    }

    @Override
    public ServiceCatalog viewPublicServiceCatalog(Long ownerId, Long Id, Boolean publicOrNot)
            throws ServiceCatalogNotExistException, CompanyNotExistException, YellowPageNotExistException {

        Company c = em.find(Company.class, ownerId);

        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            ServiceCatalog kc = em.find(ServiceCatalog.class, Id);
            if (kc == null || !Objects.equals(kc.getCompany().getId(), ownerId)) {
                throw new ServiceCatalogNotExistException("Key Client " + Id + " not exist!");
            } else {
                if (publicOrNot != true) {
                    throw new YellowPageNotExistException("Yellow Page Not Exist");
                } else {
                    return kc;
                }

            }
        }
    }

    @Override
    public List<ServiceCatalog> displayAllYellowPageInfo() throws ServiceCatalogNotExistException {
        Query query;
        query = em.createQuery("SELECT c FROM ServiceCatalog c WHERE c.publicOrNot=TRUE");

        List<ServiceCatalog> pageList = query.getResultList();
        if (pageList == null) {
            throw new ServiceCatalogNotExistException("Yellow Page Item does not exist!");
        } else {
            return pageList;
        }
    }

    @Override
    public void approveServiceCatalog(Long Id) {

        ServiceCatalog sc = em.find(ServiceCatalog.class, Id);
        sc.setPublicOrNot(Boolean.TRUE);
    }

    @Override
    public void hideIt(Long Id) {

        ServiceCatalog sc = em.find(ServiceCatalog.class, Id);
        sc.setPublicOrNot(Boolean.FALSE);
    }

    @Override
    public String deleteServiceCatalog(Long ownerId, Long Id)
            throws CompanyNotExistException, ServiceCatalogNotExistException, YellowPageNotExistException {
        //Company pc = em.find(Company.class, ownerId);
        Company c = em.find(Company.class, ownerId);
        if (c == null) {
            throw new CompanyNotExistException("Company " + ownerId + " does not exist!");
        } else {
            Query query;
            query = em.createQuery("SELECT c FROM ServiceCatalog c WHERE c.company.id=?1 AND c.id=?2  and c.archivedOrNot=FALSE");
            query.setParameter(1, ownerId);
            query.setParameter(2, Id);
            System.out.println("Page :" + ownerId + " " + Id);
            ServiceCatalog ss = (ServiceCatalog) query.getSingleResult();
            if (ss == null) {
                throw new ServiceCatalogNotExistException("Service Catalog " + Id + " does not exist!");
            } else {
                ss.setArchivedOrNot(Boolean.TRUE);

                em.merge(ss);
                return "Service Catalog " + Id + " is removed sucessfully!";
            }
        }
    }
}
