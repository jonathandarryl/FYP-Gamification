/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceQuotation;
import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Product;
import TMS.session.TransOrderMgtSessionBeanLocal;
import TMS.session.TransRouteOptiSessionBeanLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;
import util.exception.SalesLeadAlreadyExistException;
import util.exception.ServiceCatalogNotPublishedException;
import util.exception.ServiceQuotationAlreadyApprovedException;
import util.exception.ServiceQuotationCannotBeAchivedException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ServiceQuotationStartAfterCatalogExpireException;
import util.exception.ServiceQuotationStartBeforeCatalogCommmenceException;
import util.exception.SpecialRequirementNotMetException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class ServiceQuotationManagementSession implements ServiceQuotationManagementSessionLocal, ServiceQuotationSessionBeanRemote {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private SalesLeadManagementSessionLocal slm;
    @EJB
    private TransOrderMgtSessionBeanLocal tom;
//    private TransAssetMgtSessionBean tam;
    @EJB
    private FacilityManagementLocal fm;
//    private ServiceContractManagementSession scm;
    @EJB
    private TransRouteOptiSessionBeanLocal tro;
//    @EJB
//    peivate FacilityManagementLocal fm;

    private Timestamp getTimestamp(Integer year, Integer month, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        Timestamp ts = new Timestamp(cal.getTimeInMillis());
        return ts;
    }

    @Override
    public Integer calculateCapacity(Double quantity, Product product) {
        Double quantityInOneUnitCapacity = product.getQuantityInOneUnitCapacity();
        Double neededCapacity = quantity / quantityInOneUnitCapacity;
        Integer result = (int) Math.ceil(neededCapacity);
        return result;
    }

    @Override
    public ServiceQuotation addServiceQuotation(Long partnerCompanyId, Long customerCompanyId, String description, Timestamp startTime, Timestamp endTime,
            Integer requiredWarehouseCapacity, String warehouseName, Double totalPrice,
            Location source, Location dest,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Product product, Double estimatedQuantity)
            throws CompanyNotExistException {
        try {
            Company ownerCompany = cmsb.retrieveCompany(partnerCompanyId);
            //List<Warehouse> warehouseList = ownerCompany.getOwnedWarehouseList();
            Warehouse wh = fm.retrieveWarehouse(warehouseName, ownerCompany.getCompanyName());
            Company clientCompany = cmsb.retrieveCompany(customerCompanyId);
            System.out.println("ServiceQuotationManagementSession: addServiceQuotation partnerCompanyId " + partnerCompanyId + " customerCompanyId " + customerCompanyId);

            ServiceQuotation sq = new ServiceQuotation(ownerCompany, clientCompany, description, startTime, endTime, requiredWarehouseCapacity, totalPrice, source, dest,
                    perishable, flammable, pharmaceutical, highValue, wh, product, estimatedQuantity);
            em.persist(sq);
            try {
                slm.createSalesLead(partnerCompanyId, customerCompanyId);
            } catch (SalesLeadAlreadyExistException ex) {
                System.out.println(ex.getMessage());
            }
            return sq;
        } catch (WarehouseNotExistException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    @Override
    public ServiceQuotation initiateServiceQuotation(ServiceCatalog sc, String customerCompanyName, String description, Date start, Date end, Product product, Double estimatedQuantity, Integer duration, Integer orderNumber)
            throws WarehouseNotExistException, CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException, NoVehiclesAssignedException, NoPossibleRoutesException {
        try {
            Company ownerCompany = sc.getCompany();
            Warehouse wh = fm.retrieveWarehouse(sc.getWarehouseId());
            String partnerCompanyName = ownerCompany.getCompanyName();
            Company clientCompany = cmsb.retrieveCustomerCompany(customerCompanyName);
            Timestamp startTime = new Timestamp(start.getTime());
            Timestamp endTime = new Timestamp(end.getTime());

            if (startTime.after(sc.getEndTime())) {
                throw new ServiceQuotationStartAfterCatalogExpireException("Service Quotation Creation Failure. Service Catalog " + sc.getId() + " will have been expired when the quotation starts. Please renew your Catalog or create a new Catalog.");
            }

            if (startTime.before(sc.getStartTime())) {
                throw new ServiceQuotationStartBeforeCatalogCommmenceException("Service Quotation Creation Failure. Service Catalog " + sc.getId() + " has not commenced when the quotation starts. Please renew your Catalog or create a new Catalog.");
            }

            Integer requiredWarehouseCapacity = calculateCapacity(estimatedQuantity, product);
            Double warehouseCost = wh.getPricePerCapacityPerDay() * requiredWarehouseCapacity * duration;
            Location source = sc.getSource();
            Location dest = sc.getDest();
            Boolean perishable = sc.getPerishable();
            Boolean flammable = sc.getFlammable();
            Boolean pharmaceutical = sc.getPharmaceutical();
            Boolean highValue = sc.getHighValue();
            try {
                slm.createSalesLead(partnerCompanyName, customerCompanyName);
            } catch (SalesLeadAlreadyExistException ex) {
                System.out.println(ex.getMessage());
            }

            ServiceQuotation sq = new ServiceQuotation(ownerCompany, clientCompany, description, startTime, endTime, requiredWarehouseCapacity, duration, source, dest,
                    perishable, flammable, pharmaceutical, highValue, wh, product, estimatedQuantity, orderNumber);

            sq.setServiceCatalog(sc);

            Long ownerCompanyId = ownerCompany.getId();
            Double optiCheapestPrice = tro.cheapestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;
            Double optiPriceForShortestTime = tro.fastestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;
            Double optiValuestPrice = tro.valuestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;

            sq.setOptiCheapestPrice(optiCheapestPrice);
            sq.setOptiPriceForShortestTime(optiPriceForShortestTime);
            sq.setOptiValuestPrice(optiValuestPrice);

            em.persist(sq);
            return sq;
        } catch (NoVehiclesAssignedException ex) {
            throw new NoVehiclesAssignedException("No vehicles assigned for this route");
        } catch (NoPossibleRoutesException ex) {
            throw new NoPossibleRoutesException("No routes can go through this quotation!");
        }
    }

    @Override
    public ServiceQuotation requestForServiceQuotation(ServiceCatalog sc, String clientName, String description, Date start, Date end,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Product product, Double estimatedQuantity, Integer duration, Integer orderNumber)
            throws ServiceCatalogNotPublishedException, CompanyNotExistException, WarehouseNotExistException, SpecialRequirementNotMetException,
            RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException, NoVehiclesAssignedException, NoPossibleRoutesException {
        Company provider = sc.getCompany();
        if (!sc.getPublicOrNot()) {
            throw new ServiceCatalogNotPublishedException("Request creation failed. Service Catalog " + sc.getServiceName() + " has not been published by Provider " + provider.getCompanyName());
        }

        Warehouse wh = fm.retrieveWarehouse(sc.getWarehouseId());
        String partnerCompanyName = provider.getCompanyName();
        Company clientCompany = cmsb.retrieveCompany(clientName);
        Timestamp startTime = new Timestamp(start.getTime());
        Timestamp endTime = new Timestamp(end.getTime());

        if (startTime.after(sc.getEndTime())) {
            throw new ServiceQuotationStartAfterCatalogExpireException("Service Quotation Creation Failure. Service Catalog " + sc.getId() + " will have been expired when the quotation starts. Please renew your Catalog or create a new Catalog.");
        }

        if (startTime.before(sc.getStartTime())) {
            throw new ServiceQuotationStartBeforeCatalogCommmenceException("Service Quotation Creation Failure. Service Catalog " + sc.getId() + " has not commenced when the quotation starts. Please renew your Catalog or create a new Catalog.");
        }

        Integer requiredWarehouseCapacity = calculateCapacity(estimatedQuantity, product);
        Double warehouseCost = wh.getPricePerCapacityPerDay() * requiredWarehouseCapacity * duration;
        Location source = sc.getSource();
        Location dest = sc.getDest();
        if ((perishable && !sc.getPerishable())
                || (flammable && !sc.getFlammable())
                || (pharmaceutical && !sc.getPharmaceutical())
                || (highValue && !sc.getHighValue())) {
            throw new SpecialRequirementNotMetException("Service Quotation Request creation failed. Special Requirements in the catalog does not meet client's requirement");
        }
        try {
            slm.createSalesLead(partnerCompanyName, clientName);
        } catch (SalesLeadAlreadyExistException ex) {
            System.out.println(ex.getMessage());
        }

        ServiceQuotation sq = new ServiceQuotation(provider, clientCompany, description, startTime, endTime, requiredWarehouseCapacity, duration, source, dest,
                perishable, flammable, pharmaceutical, highValue, wh, product, estimatedQuantity, orderNumber);

        sq.setServiceCatalog(sc);

        Long ownerCompanyId = provider.getId();
        try {
            Double optiCheapestPrice = tro.cheapestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;
            Double optiPriceForShortestTime = tro.fastestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;
            Double optiValuestPrice = tro.valuestPriceForQuotation(ownerCompanyId, sq.getProduct(), estimatedQuantity, source, dest) + warehouseCost;

            sq.setOptiCheapestPrice(optiCheapestPrice);
            sq.setOptiPriceForShortestTime(optiPriceForShortestTime);
            sq.setOptiValuestPrice(optiValuestPrice);
        } catch (NoVehiclesAssignedException ex) {
            throw new NoVehiclesAssignedException("No vehicles assigned for this route");
        } catch (NoPossibleRoutesException ex) {
            throw new NoPossibleRoutesException("No routes can go through this quotation!");
        }
        em.persist(sq);
        return sq;
    }

    @Override
    public void choosePrice(Double chosenPrice, ServiceQuotation sq) {
        sq.setTotalPrice(chosenPrice);
        em.merge(sq);
    }

    @Override
    public ServiceQuotation updateServiceQuotation(ServiceQuotation sq, String description, Date start, Date end, Double quantity, Integer duration, Integer orderNumber)
            throws CompanyNotExistException, RoutesNotExistException, ServiceQuotationStartBeforeCatalogCommmenceException, ServiceQuotationStartAfterCatalogExpireException, NoVehiclesAssignedException, NoPossibleRoutesException {
        Timestamp startTime = new Timestamp(start.getTime());
        Timestamp endTime = new Timestamp(end.getTime());
        if (startTime.after(sq.getServiceCatalog().getEndTime())) {
            throw new ServiceQuotationStartAfterCatalogExpireException("Service Quotation Creation Failure. Service Catalog " + sq.getServiceCatalog().getId() + " will have been expired when the quotation starts. Please renew your Catalog or create a new Catalog.");
        }

        if (startTime.before(sq.getServiceCatalog().getStartTime())) {
            throw new ServiceQuotationStartBeforeCatalogCommmenceException("Service Quotation Creation Failure. Service Catalog " + sq.getServiceCatalog().getId() + " has not commenced when the quotation starts. Please renew your Catalog or create a new Catalog.");
        }

        sq.setStartDate(startTime);
        sq.setEndDate(endTime);
        sq.setDescription(description);
        sq.setEstimatedQuantity(quantity);
        sq.setRequiredWarehouseCapacity(calculateCapacity((quantity), sq.getProduct()));
        sq.setStorageDurationDays(duration);
        sq.setOrderNumber(orderNumber);
        Warehouse wh = sq.getWarehouse();
        Double warehouseCost = wh.getPricePerCapacityPerDay() * sq.getRequiredWarehouseCapacity() * duration;
        Location source = sq.getSource();
        Location dest = sq.getDest();
        Long ownerCompanyId = sq.getpCompany().getId();
        try {
            Double optiCheapestPrice = tro.cheapestPriceForQuotation(ownerCompanyId, sq.getProduct(), quantity, source, dest) + warehouseCost;
            Double optiPriceForShortestTime = tro.fastestPriceForQuotation(ownerCompanyId, sq.getProduct(), quantity, source, dest) + warehouseCost;
            Double optiValuestPrice = tro.valuestPriceForQuotation(ownerCompanyId, sq.getProduct(), quantity, source, dest) + warehouseCost;

            sq.setOptiCheapestPrice(optiCheapestPrice);
            sq.setOptiPriceForShortestTime(optiPriceForShortestTime);
            sq.setOptiValuestPrice(optiValuestPrice);
        } catch (NoVehiclesAssignedException ex) {
            throw new NoVehiclesAssignedException("No vehicles assigned for this route");
        } catch (NoPossibleRoutesException ex) {
            throw new NoPossibleRoutesException("No routes can go through this quotation!");
        }
        em.merge(sq);

        return sq;
    }

    @Override
    public ServiceQuotation retrieveServiceQuotation(Long sqId) throws ServiceQuotationNotExistException {
        Query query = em.createQuery("select s from ServiceQuotation s where s.id=?1");
        query.setParameter(1, sqId);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if (serviceQuotationList.isEmpty()) {
            throw new ServiceQuotationNotExistException("Service quotation with id " + sqId + " does not exist!");
        }
        ServiceQuotation serviceQuotation = serviceQuotationList.get(0);
        return serviceQuotation;
    }

    @Override
    public ArrayList<ServiceQuotation> retrieveServiceQuotationList(String ownerCompanyName, String clientCompanyName)
            throws CompanyNotExistException, ServiceQuotationNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query = em.createQuery("select s from ServiceQuotation s where s.pCompany=?1 and s.clientCompany=?2 and s.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if (serviceQuotationList.isEmpty()) {
            throw new ServiceQuotationNotExistException("Sales Quotation with Client company " + clientCompanyName + " does not exist!");
        }

        return serviceQuotationList;
    }

    @Override
    public ArrayList<ServiceQuotation> retrieveAllServiceQuotationList(String ownerCompanyName)
            throws ServiceQuotationNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query = em.createQuery("select s from ServiceQuotation s where s.pCompany=?1 and s.archivedOrNot=FALSE and s.rejectedByProivderOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if (serviceQuotationList.isEmpty()) {
            throw new ServiceQuotationNotExistException("Company " + ownerCompanyName + " does not have any service quotation!");
        }

        return serviceQuotationList;
    }

    @Override
    public ArrayList<ServiceQuotation> retrieveOutsourcingServiceQuotationList(String ownerCompanyName)
            throws ServiceQuotationNotExistException, CompanyNotExistException {
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Query query = em.createQuery("select s from ServiceQuotation s where s.clientCompany=?1 and s.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        ArrayList<ServiceQuotation> serviceQuotationList = new ArrayList(query.getResultList());
        if (serviceQuotationList.isEmpty()) {
            throw new ServiceQuotationNotExistException("Company " + ownerCompanyName + " does not have any service quotation!");
        }

        return serviceQuotationList;
    }

    @Override
    public void removeServiceQuotation(Long sqId)
            throws ServiceQuotationNotExistException, ServiceQuotationCannotBeAchivedException {
        ServiceQuotation targetSq = retrieveServiceQuotation(sqId);
        if (targetSq.getEstablishedContractOrNot()) {
            throw new ServiceQuotationCannotBeAchivedException("Archiving failed. There is a contract already bonded with Service Quotation " + sqId);
        }
        targetSq.setArchivedOrNot(Boolean.TRUE);
        em.merge(targetSq);
    }

    @Override
    public Boolean verifyServiceQuotationWarehouse(Long sqId, Double warehouseBufferRatio)
            throws ServiceQuotationNotExistException {
        ServiceQuotation targetSq = retrieveServiceQuotation(sqId);
//        PartnerCompany ownerCompany = targetSq.getpCompany();
//        Long comId = ownerCompany.getId();
//        
//        Integer owningTruckNumber = tam.viewAllTrucks(comId).size() + scm.getOutsourcingTruckNumber(comId);
//        Integer owningPlaneNumber = tam.viewAllPlanes(comId).size() + scm.getOutsourcingPlaneNumber(comId);
//        Integer owningVesselNumber = tam.viewAllVessel(comId).size() + scm.getOutsourcingVesselNumber(comId);
//        Integer owningWarehouseCapacity = fm.companyOwnedWarehouseSpareCapacity(comId) + scm.getOutsourcingWarehouseCapacity(comId);
//        
//        if(owningTruckNumber>=targetSq.getRequiredTruckNumber() &&
//           owningPlaneNumber>=targetSq.getRequiredPlaneNumber() &&
//           owningVesselNumber>=targetSq.getRequiredVesselNumber() &&
//           owningWarehouseCapacity>=targetSq.getRequiredWarehouseCapacity())
//            return Boolean.TRUE;
//        else
//            return Boolean.FALSE;
        return fm.verifyServiceQuotation(targetSq, warehouseBufferRatio);
    }

    @Override
    public Boolean verifyServiceQuotationTransportation(Long sqId)
            throws ServiceQuotationNotExistException {
        ServiceQuotation targetSq = retrieveServiceQuotation(sqId);
        return tom.verifyViability(targetSq);
    }

    @Override
    public void approveServiceQuotation(Long sqId)
            throws ServiceQuotationNotExistException, ServiceQuotationAlreadyApprovedException {
        ServiceQuotation targetSq = retrieveServiceQuotation(sqId);
        if (targetSq.isApprovedOrNot()) {
            throw new ServiceQuotationAlreadyApprovedException("ServiceQuotation " + sqId + " has already been approved!");
        }
        targetSq.setApprovedOrNot(Boolean.TRUE);
        System.out.println("Service Quotation " + sqId + " has been approved!");
    }

    @Override
    public void approveOutsourcingServiceQuotation(Long sqId)
            throws ServiceQuotationNotExistException, ServiceQuotationAlreadyApprovedException {
        ServiceQuotation targetSq = retrieveServiceQuotation(sqId);
        if (targetSq.getClientApprovedOrNot()) {
            throw new ServiceQuotationAlreadyApprovedException("Outsourcing Service Quotation " + sqId + " has already been approved!");
        }
        targetSq.setClientApprovedOrNot(Boolean.TRUE);
        System.out.println("Outsourcing Service Quotation " + sqId + " has been approved!");
    }

    @Override
    public void rejectIncomingServiceQuotation(ServiceQuotation sq) {
        sq.setRejectedByProivderOrNot(Boolean.TRUE);
        em.merge(sq);
    }
}
