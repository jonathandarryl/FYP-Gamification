/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMS.session;

import CRM.entity.ServiceOrder;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceOrderManagementLocal;
import Common.entity.Company;
import Common.entity.Location;
import Common.entity.PartnerCompany;
import GRNS.entity.AggregationPost;
import GRNS.session.PostManagementSessionBeanLocal;
import OES.entity.Product;
import TMS.entity.Drivers;
import TMS.entity.Routes;
import TMS.entity.TransOrder;
import TMS.entity.Trucks;
import TMS.entity.Vehicles;
import WMS.session.ExtractionRequestSessionBeanLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceOrderNotExistException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNotExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class TransOrderMgtSessionBean implements TransOrderMgtSessionBeanLocal {

    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;

    @EJB
    private OrderTrackingSessionBeanLocal otsbl;
    @EJB
    private TransRouteOptiSessionBeanLocal trosbl;
    @EJB
    private PostManagementSessionBeanLocal pmsbl;
    @EJB
    private ServiceOrderManagementLocal somsbl;
    @EJB
    private ExtractionRequestSessionBeanLocal ers;

    @Override
    public Long createTransOrder(ServiceOrder so) throws ServiceOrderNotExistException, WarehouseNotExistException {
        if (so == null) {
            throw new ServiceOrderNotExistException("Service Order not exist!");
        } else {
            TransOrder transOrder = new TransOrder(so);
            transOrder.setCurrentLocation(so.getSourceLoc());
            transOrder.setProduct(so.getProduct());
            transOrder.setQuantity(so.getQuantity());
            transOrder.setFlammable(so.isFlammable());
            transOrder.setHighValue(so.isHighValue());
            transOrder.setPerishable(so.isPerishable());
            transOrder.setPharmaceutical(so.isPharmaceutical());
            so.setTransOrder(transOrder);
            em.persist(transOrder);
            em.persist(so);
            try {
                otsbl.generateTrackingNumber(transOrder.getServiceOrder().getServiceContract().getProvider().getId(), transOrder.getTransOrderId());
            } catch (CompanyNotExistException ex) {
                Logger.getLogger(TransOrderMgtSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransOrderNotExistException ex) {
                Logger.getLogger(TransOrderMgtSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            em.merge(transOrder);
            if (so.getSourceWarehouse() != null) {
                System.out.println("Service Order "+so.getId()+" has a source warehouse. Initiate Extraction Request Creation");
                ers.dealWithTransOrderExtraction(transOrder);
            }
            else
                System.out.println("Service Order "+so.getId()+" does not have a source warehouse. No Extraction Request Created");
            return transOrder.getTransOrderId();
        }
    }

    @Override
    public List<TransOrder> viewAllTransOrders(Long ownerId) throws CompanyNotExistException, TransOrderNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query query = em.createQuery("select t from TransOrder t WHERE t.serviceOrder.serviceContract.provider.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<TransOrder> transOrderList = new ArrayList<>(query.getResultList());
            if (transOrderList.isEmpty()) {
                throw new TransOrderNotExistException("Transportation order not exist!");
            } else {
                return transOrderList;
            }
        }
    }

    @Override
    public List<TransOrder> viewAllTransOrdersClient(Long clientId) throws CompanyNotExistException, TransOrderNotExistException {
        Company pc = em.find(Company.class, clientId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + clientId + " not exist!");
        } else {
            Query query = em.createQuery("select t from TransOrder t WHERE t.serviceOrder.serviceContract.client.id=?1");
            query.setParameter(1, clientId);
            @SuppressWarnings("unchecked")
            List<TransOrder> transOrderList = new ArrayList<>(query.getResultList());
            if (transOrderList.isEmpty()) {
                throw new TransOrderNotExistException("Transportation order not exist!");
            } else {
                return transOrderList;
            }
        }
    }

    @Override
    public TransOrder retrieveTransOrder(Long ownerId, Long toId)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            ServiceOrder to = em.find(ServiceOrder.class, toId);
            if (to == null || !to.getServiceContract().getProvider().getId().equals(ownerId)) {
                throw new TransOrderNotExistException("Transportation order " + toId + " not found!");
            } else {
                return to.getTransOrder();
            }
        }
    }

    @Override
    public TransOrder retrieveTransOrder(Long ownerId, String trackingNumber)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query q = em.createQuery("SELECT o FROM TransOrder o WHERE o.trackingNumber=?1");
            q.setParameter(1, trackingNumber);
            TransOrder to = (TransOrder) q.getSingleResult();
            if (to == null) {
                throw new TransOrderNotExistException("Transportation order " + trackingNumber + " not found!");
            } else {
                return to;
            }
        }
    }

    @Override
    public TransOrder retrieveTransOrder(Long toId)
            throws TransOrderNotExistException {
        TransOrder to = em.find(TransOrder.class, toId);
        if (to == null) {
            throw new TransOrderNotExistException("TransOrder " + toId + " not found!");
        }
        return to;
    }

    @Override
    public TransOrder retrieveTransOrderClient(Long clientId, Long toId)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company pc = em.find(Company.class, clientId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + clientId + " not exist!");
        } else {
            TransOrder to = em.find(TransOrder.class, toId);
            if (to == null || !to.getServiceOrder().getServiceContract().getClient().getId().equals(clientId)) {
                throw new TransOrderNotExistException("Transportation order " + toId + " not found!");
            } else {
                return to;
            }
        }
    }

    @Override
    public void updateTransOrder(Long ownerId, Long transOrderId, Location currentLocation)
            throws TransOrderNotExistException, CompanyNotExistException,ServiceOrderNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            TransOrder transOrder = retrieveTransOrder(ownerId, transOrderId);
            transOrder.setCurrentLocation(currentLocation);
            if (trosbl.locationEqual(transOrder.getEndPoint(), currentLocation)) {
                fulfillTransOrder(ownerId, transOrderId);
            }
            em.merge(transOrder);
//            System.out.println("\nTransportation order information with ID: " + transOrderId +" updated successfully!");        
        }
    }

    @Override
    public void fulfillTransOrder(Long ownerId, Long transOrderId)
            throws TransOrderNotExistException, CompanyNotExistException, ServiceOrderNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            TransOrder transOrder = retrieveTransOrder(ownerId, transOrderId);
            transOrder.setFulfilledOrNot(Boolean.TRUE);
            //release vehicles
            Query query = em.createQuery("SELECT v FROM Vehicles v WHERE v.transOrderId=?1");
            query.setParameter(1, transOrderId);
            for(Object o: query.getResultList()){
                Vehicles v = (Vehicles) o;
                v.setTransOrderId(null);
                em.merge(v);
            }
            //release drivers
            Query q = em.createQuery("SELECT d FROM Drivers d WHERE d.transOrderId=?1");
            q.setParameter(1, transOrderId);
            @SuppressWarnings("unchecked")
            List<Drivers> dList = (List<Drivers>)q.getResultList();
            for(Drivers d: dList){
                d.setTrucks(null);
                d.setTransOrderId(null);
                em.merge(d);
            }
            em.merge(transOrder);
            ServiceOrder so = transOrder.getServiceOrder();
            if (!so.isFulfilledOrNot() && so.getEstablishedWarehouseOrderOrNot() && !so.getWarehouseOrder().isFulfilledOrNot()) {
                System.out.println("warehouse order exists! Service order pending fulfilled.");
            }
            if (!so.isFulfilledOrNot() && so.getEstablishedWarehouseOrderOrNot() && so.getWarehouseOrder().isFulfilledOrNot()) {
                so.setFulfilledOrNot(Boolean.TRUE);
                if (so.getIsFromOutsourcing()) {
                    ServiceOrder fromSo = somsbl.retrieveServiceOrder(so.getFromServiceOrderId());
                    fromSo.setFulfilledOrNot(Boolean.TRUE);
                }
            }
            if (!so.isFulfilledOrNot() && !so.getIsToEstablishWarehouseOrder()) {
                so.setFulfilledOrNot(Boolean.TRUE);
                if (so.getIsFromOutsourcing()) {
                    ServiceOrder fromSo = somsbl.retrieveServiceOrder(so.getFromServiceOrderId());
                    fromSo.setFulfilledOrNot(Boolean.TRUE);
                }
            }
            if (so.getFromGRNS()) {
                so.setFulfilledOrNot(Boolean.TRUE);
                if (so.getPostType().equals("aggregation")) {
                    AggregationPost ap = pmsbl.retrieveAggregationPostById(so.getPostId());
                    List<Long> soList = ap.getServiceOrderIdList();
                    if (ap.getServiceOrderIdList() != null) {
                        for (Long s : soList) {
                            try {
                                ServiceOrder serviceOrder = somsbl.retrieveServiceOrder(s);
                                serviceOrder.setFulfilledOrNot(Boolean.TRUE);
                                em.merge(serviceOrder);
                            } catch (ServiceOrderNotExistException ex) {
                                throw new ServiceOrderNotExistException("service order not exist!");
                            }
                        }
                    }
                }
                em.merge(so);
            }
        }
    }

    @Override
    public void deleteTransOrder(Long ownerId, Long transOrderId)
            throws TransOrderNotExistException, CompanyNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            TransOrder transOrder = retrieveTransOrder(ownerId, transOrderId);
            em.remove(transOrder);
        }
    }

    @Override
    public Boolean verifyViability(ServiceQuotation sq) {
        return true;
//        Company pc = sq.getpCompany();
//        //verify route viability
//        Location start = sq.getSource();
//        Location dest = sq.getDest(); 
//        Query q = em.createQuery("SELECT r FROM Routes r WHERE r.pCompany.id=?1");
//        q.setParameter(1, pc.getId());
//        @SuppressWarnings("unchecked")
//        List<Routes> routes = (List<Routes>) q.getResultList();  
//        Boolean can = true;
//        
//        for(int i=0; i<routes.size() && can; i++){
//            if(routes.get(i).getVehicles().isEmpty()) {
//                System.out.println("in routes: "+ i);
//                return false;
//            }
//            int total = 0;
//            for(Vehicles v: routes.get(i).getVehicles()){
//                total += v.getCapacity();
//            }
//            if(total<sq.getRequiredWarehouseCapacity()){
//                return false;
//            }
//            can = false;
//            for(int j=i; j<routes.size(); j++){
//                if(trosbl.locationEqual(routes.get(j).getStartOfRoute(), start)){            
//                    Routes temp = routes.get(i);
//                    routes.set(i, routes.get(j));
//                    routes.set(j, temp);
//                    start = routes.get(i).getDestOfRoute();
//                    can = true;
//                    break;
//                }
//            }         
//        }
//        if(can && trosbl.locationEqual(routes.get(routes.size()-1).getDestOfRoute(), dest)){                   
//            return true;
//        }else{
//            return false;
//        }

    }

    @Override
    public Boolean checkTotalCapacity(Long orderId, List<Vehicles> vList) {
        TransOrder order = em.find(TransOrder.class, orderId);
        Long capa = 0L;
        for (Vehicles v : vList) {
            capa += v.getCapacity();
        }
        return (order.getQuantity() / order.getProduct().getQuantityInOneUnitCapacity()) <= capa;
    }

    @Override
    public List<Vehicles> retrieveCapableVehicles(Long ownerId, Long orderId, Long routeId) throws CompanyNotExistException, TransOrderNotExistException, RoutesNotExistException, VehicleNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            TransOrder transOrder = em.find(TransOrder.class, orderId);
            Routes route = em.find(Routes.class, routeId);
            if (transOrder == null) {
                throw new TransOrderNotExistException("Transportation Order " + orderId + " not exist!");
            } else if (route == null) {
                throw new RoutesNotExistException("Route " + routeId + " not exist!");
            } else {
                List<Vehicles> vList = (List<Vehicles>) route.getVehicles();
                if (vList == null) {
                    throw new VehicleNotExistException("Route " + routeId + " not assign Vehicles!");
                } else {
                    List<Vehicles> result = new ArrayList<>();
                    for (Vehicles v : vList) {
                        if (checkVehicleFourBoolean(transOrder, v) && !orderId.equals(v.getVehiclesId())) {
                            result.add(v);
                        }
                    }
                    return result;
                }
            }
        }
    }

    private Boolean checkVehicleFourBoolean(TransOrder order, Vehicles v) {
        if (order.getFlammable() && !v.getFlammable()) {
            return false;
        } else if (order.getHighValue() && !v.getHighValue()) {
            return false;
        } else if (order.getPerishable() && !v.getPerishable()) {
            return false;
        } else {
            return !(order.getPharmaceutical() && !v.getPharmaceutical());
        }
    }
}
