/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.ServiceOrder;
import CRM.entity.WarehouseOrder;
import CRM.session.ServiceOrderManagementLocal;
import Common.entity.Location;
import GRNS.entity.AggregationPost;
import TMS.session.TransOrderMgtSessionBean;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccessDeniedException;
import util.exception.ServiceOrderAlreadyEstablishedWarehouseOrderException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.TransOrderNotFulfilledException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseOrderNotExistException;
import util.exception.WarehouseOrderOrderAlreadyFulfilledException;

/**
 *
 * @author songhan
 */
@Stateless
public class WarehouseOrderManagementSession implements WarehouseOrderManagementSessionLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private FacilityManagementLocal fm;
    @EJB
    private StorageRequestSessionBeanLocal srs;
    @EJB
    private ServiceOrderManagementLocal som;

    @Override
    public void createWarehouseOrder(Long soId)
            throws ServiceOrderNotExistException, ServiceOrderNotApprovedException, ServiceOrderAlreadyEstablishedWarehouseOrderException, ServiceOrderNotPaidException, WarehouseNotExistException {
        ServiceOrder so = som.retrieveServiceOrder(soId);
        Long warehouseId = so.getWarehouseId();
        Warehouse warehouse = fm.retrieveWarehouse(warehouseId);
        WarehouseOrder wo = new WarehouseOrder(so, warehouse);
        if (!so.isApprovedOrNot()) {
            throw new ServiceOrderNotApprovedException("Creation failed. Service Order " + soId + " has not been approved yet!");
        }
        if (so.getEstablishedWarehouseOrderOrNot()) {
            throw new ServiceOrderAlreadyEstablishedWarehouseOrderException("Creation failed. Service Order " + soId + " has already been associate with a warehouse order!");
        }
        if (!so.isPaidOrNot()) {
            throw new ServiceOrderNotPaidException("Creation failed. Service Order " + soId + " has not been paid yet!");
        }
//        Location source = new Location("N.A.", "N.A.", "N.A.", "N.A.", "N.A.", "N.A.");
//        so.setSourceLoc(source);
        so.setEstablishedWarehouseOrderOrNot(Boolean.TRUE);
        em.merge(so);
        em.persist(wo);
        System.out.println("Warehouse order creation successful for service order " + soId);
    }

    @Override
    public ArrayList<WarehouseOrder> getUnfulfilledIncomingWarehouseOrderList(String ownerCompanyName, String warehouseName)
            throws WarehouseNotExistException, WarehouseOrderNotExistException, AccessDeniedException {
        Warehouse wh = fm.retrieveWarehouse(warehouseName, ownerCompanyName);
        Long whId = wh.getId();
        Query query = em.createQuery("select w from WarehouseOrder w where w.serviceOrder.warehouseId=?1 and w.fulfilledOrNot=FALSE and w.serviceOrder.fulfilledOrNot=TRUE and w.serviceOrder.transOrder.fulfilledOrNot=TRUE");
        query.setParameter(1, whId);
        ArrayList<WarehouseOrder> warehouseOrderList = new ArrayList(query.getResultList());

        if (warehouseOrderList.isEmpty()) {
            throw new WarehouseOrderNotExistException("No unfulfilled warehouse order found for Warehouse " + whId);
        }

        return warehouseOrderList;
    }

    @Override
    public void fulfillWarehouseOrder(WarehouseOrder wo)
            throws TransOrderNotFulfilledException, WarehouseOrderOrderAlreadyFulfilledException, ServiceOrderNotExistException {
        ServiceOrder so = wo.getServiceOrder();
        if (wo.isFulfilledOrNot()) {
            throw new WarehouseOrderOrderAlreadyFulfilledException("Warehouse Order " + wo.getId() + " has already been fulfilled!");
        }
        if (!so.isFulfilledOrNot() && so.getEstablishedTransOrderOrNot() && !so.getTransOrder().getFulfilledOrNot()) {
            throw new TransOrderNotFulfilledException("The related Transportation Order has not been fulfilled yet. Cargo has not arrived at the warehouse!");
        }
        if (!so.isFulfilledOrNot() && so.getEstablishedTransOrderOrNot() && so.getTransOrder().getFulfilledOrNot()) {
            so.setFulfilledOrNot(Boolean.TRUE);
            if(so.getIsFromOutsourcing()){
                ServiceOrder fromSo = som.retrieveServiceOrder(so.getFromServiceOrderId());
                fromSo.setFulfilledOrNot(Boolean.TRUE);
            }                
        }
        if (!so.isFulfilledOrNot() && !so.getIsToEstablishTransOrder()) {
            so.setFulfilledOrNot(Boolean.TRUE);
            if(so.getIsFromOutsourcing()){
                ServiceOrder fromSo = som.retrieveServiceOrder(so.getFromServiceOrderId());
                fromSo.setFulfilledOrNot(Boolean.TRUE);
            }     
        }
        wo.setFulfilledOrNot(Boolean.TRUE);
        /**
         * ************** This part is for GRNS Aggregated order, please do not edit
         *
         */
        if (so.getFromGRNS()) {
            so.setFulfilledOrNot(Boolean.TRUE);
            em.merge(so);
        }
        /**
         * ************** This is the end for GRNS Aggregated order ***************************
         */
        em.merge(so);
        em.merge(wo);
        srs.dealWithWarehouseOrder(wo);
        System.out.println("Warehouse order " + wo.getId() + " has been fulfilled successfully.");
    }

}
