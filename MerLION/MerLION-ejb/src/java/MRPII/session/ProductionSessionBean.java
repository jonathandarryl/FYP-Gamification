/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import Common.entity.Company;
import MRPII.entity.BillOfMaterial;
import MRPII.entity.Production;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import OES.entity.SalesOrder;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.ExtractionRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ProductionNotCommencedException;
import util.exception.ProductionNotExistException;
import util.exception.RawMaterialNotReadyException;

/**
 *
 * @author songhan
 */
@Stateless
public class ProductionSessionBean implements ProductionSessionBeanLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private ProductInfoManagementLocal pim;

    @Override
    public void createProduction(String productName, String companyName, Integer batchNumber, SalesOrder so)
            throws ProductNotExistException, MultipleProductWithSameNameException {
        FinishedGood fg = pim.retrieveProduct(companyName, productName);
        Double quantity = batchNumber * fg.getQuantityInOneBatch();
        Production production = new Production(fg, batchNumber, quantity, so);

        Double productQuantity = production.getQuantity();
        List<RawMaterial> rawMaterialListForProduction = new ArrayList<>();
        HashMap<RawMaterial, Double> materialQuantityMapForProduction = new HashMap<>();
        HashMap<RawMaterial, Boolean> rawMaterialReadyOrNotMap = new HashMap<>();

        BillOfMaterial bom = production.getFinishedGood().getBillOfMaterial();
        List<RawMaterial> rawMaterialList = bom.getRawMaterialList();
        HashMap<RawMaterial, Double> materialQuantityMap = bom.getMaterialQuantityMap();

        for (Object o : rawMaterialList) {
            RawMaterial rm = (RawMaterial) o;
            rawMaterialListForProduction.add(rm);
            Double neededQuantity = productQuantity * materialQuantityMap.get(rm);
            materialQuantityMapForProduction.put(rm, neededQuantity);
            rawMaterialReadyOrNotMap.put(rm, Boolean.FALSE);
        }
        production.setRawMaterialList(rawMaterialList);
        production.setRawMaterialQuantityMap(materialQuantityMapForProduction);
        production.setRawMaterialReadyOrNotMap(rawMaterialReadyOrNotMap);
        em.persist(production);
        so.getProductionList().add(production);
        em.merge(so);
    }

    @Override
    public List<Production> retrieveStandByProductionList(Company ownerCompany)
            throws ProductionNotExistException {
        Query query = em.createQuery("select p from Production p where p.finishedGood.ownerCompany=?1 and p.commencedOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        ArrayList<Production> productionList = new ArrayList(query.getResultList());
        if (productionList.isEmpty()) {
            throw new ProductionNotExistException("No stand by production for company " + ownerCompany.getCompanyName() + "found");
        }
        return productionList;
    }
    
    @Override
    public List<Production> retrieveOngoingProductionList(Company ownerCompany)
            throws ProductionNotExistException {
        Query query = em.createQuery("select p from Production p where p.finishedGood.ownerCompany=?1 and p.commencedOrNot=TRUE and p.completeOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        ArrayList<Production> productionList = new ArrayList(query.getResultList());
        if (productionList.isEmpty()) {
            throw new ProductionNotExistException("No ongoing production for company " + ownerCompany.getCompanyName() + "found");
        }
        return productionList;
    }
    
    @Override
    public List<Production> retrieveCompletedProductionList(Company ownerCompany)
            throws ProductionNotExistException {
        Query query = em.createQuery("select p from Production p where p.finishedGood.ownerCompany=?1 and p.completeOrNot=TRUE");
        query.setParameter(1, ownerCompany);
        ArrayList<Production> productionList = new ArrayList(query.getResultList());
        if (productionList.isEmpty()) {
            throw new ProductionNotExistException("No completed production for company " + ownerCompany.getCompanyName() + "found");
        }
        return productionList;
    }

    @Override
    public List<ExtractionRequest> retrieveExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production) {
        List<ExtractionRequest> erList = production.getRawMaterialExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            RawMaterial curRm = (RawMaterial) curEr.getProduct();
            if (curRm.equals(rm)) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production) {
        List<ExtractionRequest> erList = production.getRawMaterialExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            RawMaterial curRm = (RawMaterial) curEr.getProduct();
            if (curRm.equals(rm) && curEr.getFulfilledOrNot()) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production) {
        List<ExtractionRequest> erList = production.getRawMaterialExtractionRequestList();
        List<ExtractionRequest> result = new ArrayList<>();
        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            RawMaterial curRm = (RawMaterial) curEr.getProduct();
            if (curRm.equals(rm) && !curEr.getFulfilledOrNot()) {
                result.add(curEr);
            }
        }
        return result;
    }

    @Override
    public Boolean checkOneRawMaterialReadyOrNot(RawMaterial rm, Production production) {
        System.out.println("checkOneRawMaterialReadyOrNot: Checking raw material "+rm.getProductName());
        BillOfMaterial bom = production.getFinishedGood().getBillOfMaterial();
        Double productQuantity = production.getQuantity();
        HashMap<RawMaterial, Boolean> materialReadyMap = production.getRawMaterialReadyOrNotMap();
        HashMap<RawMaterial, Double> materialQuantityMap = bom.getMaterialQuantityMap();
        List<ExtractionRequest> erList = retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction(rm, production);
        Double neededQuantity = productQuantity * materialQuantityMap.get(rm);
        Double arrivedQuantity = 0.0;

        for (Object obj : erList) {
            ExtractionRequest curEr = (ExtractionRequest) obj;
            arrivedQuantity = arrivedQuantity + curEr.getQuantity();
            if (arrivedQuantity >= neededQuantity) {
                materialReadyMap.put(rm, Boolean.TRUE);
                production.setRawMaterialReadyOrNotMap(materialReadyMap);
                em.merge(production);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean checkAllRawMaterialReadyOrNot(Production production) {
        List<RawMaterial> rawMaterialList = production.getRawMaterialList();
        HashMap<RawMaterial, Boolean> materialReadyOrNotMap = production.getRawMaterialReadyOrNotMap();

        for (Object o : rawMaterialList) {
            RawMaterial rm = (RawMaterial) o;
            if (!checkOneRawMaterialReadyOrNot(rm, production)) {
                System.out.println("materialReadyOrNotMap.get(rm): "+materialReadyOrNotMap.get(rm).toString());
                System.out.println("RawMaterial "+rm.getProductName()+" failed the check");
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean confirmRawMaterialReadyForProduction(Production production){
        Long productionId = production.getId();
        if (checkAllRawMaterialReadyOrNot(production)) {
            production.setRawMaterialReadyorNot(Boolean.TRUE);
            Timestamp commenceTime = new Timestamp(System.currentTimeMillis());
            production.setStartTime(commenceTime);
            em.merge(production);
            System.out.println("Auto-confirm SUCCESSFUL. All raw materials are ready for production "+productionId);
            return Boolean.TRUE;
        }
        else{
            System.out.println("Auto-confirm FAILED. All raw materials are NOT ready for production "+productionId);
            return Boolean.FALSE;
        }
    }

    @Override
    public void commenceProduction(Production production)
            throws RawMaterialNotReadyException {
        Long productionId = production.getId();
        if (!production.getRawMaterialReadyorNot()) {
            throw new RawMaterialNotReadyException("Raw materials for Production Process "+productionId+" have not yet been prepared.");
        }
        production.setCommencedOrNot(Boolean.TRUE);
        Timestamp commenceTime = new Timestamp(System.currentTimeMillis());
        production.setStartTime(commenceTime);
        em.merge(production);
    }

    @Override
    public void completeProduction(Production production)
            throws ProductionNotCommencedException {
        Long productionId = production.getId();
        if (!production.getCommencedOrNot()) {
            throw new ProductionNotCommencedException("Production " + productionId + " has not commenced yet!");
        }
        production.setCompleteOrNot(Boolean.TRUE);

        SalesOrder so = production.getSalesOrder();
        if (checkAllProductionCompleteOrNot(so)) {
            so.setIsBackOrder(Boolean.FALSE);
            em.merge(so);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        production.setCompleteTime(endTime);
        em.merge(production);
    }

    private Boolean checkAllProductionCompleteOrNot(SalesOrder so) {
        Boolean result = Boolean.TRUE;
        System.out.println("Entering checkAllProductionCompleteOrNot");
        for (Object o : so.getProductionList()) {
            Production curPro = (Production) o;
            System.out.println("checkAllProductionCompleteOrNot: Checking Production "+curPro.getId()+" in Sales Order "+so.getId());
            if (!curPro.getCompleteOrNot()) {
                System.out.println("checkAllProductionCompleteOrNot: Failed");
                result = Boolean.FALSE;
            }
        }
        return result;
    }

    @Override
    public Production retrieveProduction(Long id)
            throws ProductionNotExistException {
        Production production = em.find(Production.class, id);
        if (production == null) {
            throw new ProductionNotExistException("Production ID " + id + " does not exist!");
        }
        return production;
    }

}
