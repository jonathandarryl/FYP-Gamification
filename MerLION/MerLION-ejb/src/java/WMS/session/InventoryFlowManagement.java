/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import Common.entity.Company;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Inventory;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InsufficientStorageInWarehouseException;
import util.exception.InventoryNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ShelfIncorrectProductException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Stateless
public class InventoryFlowManagement implements InventoryFlowManagementLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private FacilityManagementLocal fm;

    private Integer shelfSetCapacity(List<Shelf> shelfList) {
        Integer totalCapacity = 0;
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            if (!shelf.isArchivedOrNot()) {
                totalCapacity = totalCapacity + shelf.getCapacity();
            }
        }
        return totalCapacity;
    }

    private Double shelfCurQuantity(Set<Shelf> shelfList) {
        Double totalQuantity = 0.0;
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            if (!shelf.isArchivedOrNot()) {
                totalQuantity = totalQuantity + shelf.getCurQuantity();
            }
        }
        return totalQuantity;
    }

    private Boolean shelfSetAvailabilityCheck(List<Shelf> shelfList) {
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            if (!shelf.getAvailability()) {
                System.out.println("InventoryFlowManagement: shelfSetAvailabilityCheck: Shelf " + shelf.getSerialCode() + " has been occupied!");
                return false;
            }
        }
        return true;
    }

    private Integer getNeededCapacity(Product product, Double quantity) {
        Double quantityInOneUnitCapacity = product.getQuantityInOneUnitCapacity();
        Double neededCapacity = quantity / quantityInOneUnitCapacity;
        Integer result = (int) Math.ceil(neededCapacity);
        return result;
    }

//    @Override
//    public void createInflowInventory(String productOwnerCompanyName, String productName, Double quantity, Boolean perishable, Boolean flammable,
//            Boolean pharmaceutical, Boolean highValue, String warehouseOwnerCompanyName, String warehouseName, ArrayList<String> shelfSerialCodeList)
//            throws ProductNotExistException, MultipleProductWithSameNameException, WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException {
//        Product finishedGood = pim.retrieveProduct(productOwnerCompanyName, productName);
//        Warehouse warehouse = fm.retrieveWarehouse(warehouseOwnerCompanyName, warehouseName);
//        List<Shelf> shelfList = fm.retrieveShelfList(shelfSerialCodeList, warehouseName, warehouseOwnerCompanyName);
//        Integer totalCapacity = shelfSetCapacity(shelfList);
//        Integer neededShelfCapacity = getNeededCapacity(finishedGood, quantity);
//
//        if (neededShelfCapacity > totalCapacity) {
//            throw new ShelfListInsufficientCapacityException("Inventory inflow creation failed. Selected shelf set capacity is insufficient.");
//        }
//
//        if (shelfSetAvailabilityCheck(shelfList)) {
//            throw new ShelfListInsufficientCapacityException("Inventory inflow creation failed. One of the selected shelf has been occupied.");
//        }
//
//        if (finishedGood == null) {
//            throw new ProductNotExistException("Inventory inflow creation failed. No product named" + productName + " exists in DB.");
//        }
//
//        if (warehouse == null) {
//            throw new WarehouseNotExistException("Inventory inflow creation failed. No warehouse named" + productName + " exists in DB.");
//        }
//
//        if (shelfList == null) {
//            throw new ShelfNotExistException("Inventory inflow creation failed. One of the selected shelves is not found.");
//        }
//
//        if ((Objects.equals(warehouse.isPerishable(), Boolean.FALSE) && Objects.equals(perishable, Boolean.TRUE))
//                || (Objects.equals(warehouse.isFlammable(), Boolean.FALSE) && Objects.equals(flammable, Boolean.TRUE))
//                || (Objects.equals(warehouse.isPharmaceutical(), Boolean.FALSE) && Objects.equals(pharmaceutical, Boolean.TRUE))
//                || (Objects.equals(warehouse.isHighValue(), Boolean.FALSE) && Objects.equals(highValue, Boolean.TRUE))) {
//            throw new WarehouseSpecialRequirementException("Warehouse does not meet special requirement (flammable, perishable, pharmaceutical, highValue)");
//        }
//
//        Inventory inv = new Inventory(quantity, neededShelfCapacity, perishable, flammable, pharmaceutical, highValue, finishedGood, "Inflow", shelfList);
//        Double newInventoryLevel = finishedGood.getInventoryLevel() + quantity;
//        finishedGood.setInventoryLevel(newInventoryLevel);
//
//        warehouse.setSpareCapacity(warehouse.getSpareCapacity() - totalCapacity);
//        warehouse.setUtilizedCapacity(warehouse.getUtilizedCapacity() + totalCapacity);
//        
//        HashMap<FinishedGood, Double> finishedGoodMap = warehouse.getFinishedGoodMap();
//        List<FinishedGood> finishedGoodList = warehouse.getFinishedGoodList();
//        if(finishedGoodMap.get((FinishedGood)finishedGood)==null){
//            finishedGoodList.add((FinishedGood)finishedGood);
//            finishedGoodMap.put((FinishedGood)finishedGood, quantity);
//        }
//        else{
//            Double curQuantity = finishedGoodMap.get((FinishedGood)finishedGood);
//            curQuantity = curQuantity+quantity;
//            finishedGoodMap.put((FinishedGood)finishedGood, curQuantity);
//        }
//        warehouse.setFinishedGoodMap(finishedGoodMap);
//        warehouse.setFinishedGoodList(finishedGoodList);
//
//        Double quantityInUnitCapacity = finishedGood.getQuantityInOneUnitCapacity();
//
//        Double remainingQuantity = quantity;
//        for (Object o : shelfList) {
//            Shelf shelf = (Shelf) o;
//            Double shelfQuantity = quantityInUnitCapacity * shelf.getCapacity();
//            if (remainingQuantity < shelfQuantity) {
//                shelf.setCurQuantity(remainingQuantity);
//            } else {
//                shelf.setCurQuantity(shelfQuantity);
//            }
//            shelf.setInventory(inv);
//            shelf.setAvailability(Boolean.FALSE);
//
//            em.persist(shelf);
//            remainingQuantity = remainingQuantity - shelfQuantity;
//        }
//        em.persist(warehouse);
//        em.persist(inv);
//        em.persist(finishedGood);
//
//        System.out.println("Inventory entry of " + quantity + " " + finishedGood.getUnit() + " " + productName + " added into the catalog successfully!");
//    }
    @Override
    public void createInflowInventory(Product product, Double quantity, Boolean perishable, Boolean flammable,
            Boolean pharmaceutical, Boolean highValue, Warehouse warehouse, ArrayList<Shelf> shelfList)
            throws WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException {
        Integer totalCapacity = shelfSetCapacity(shelfList);
        Integer neededShelfCapacity = getNeededCapacity(product, quantity);

        if (neededShelfCapacity > totalCapacity) {
            throw new ShelfListInsufficientCapacityException("Inventory inflow creation failed. Selected shelf set capacity is insufficient.");
        }

        if (!shelfSetAvailabilityCheck(shelfList)) {
            throw new ShelfListInsufficientCapacityException("Inventory inflow creation failed. One of the selected shelf has been occupied.");
        }

        if (shelfList == null) {
            throw new ShelfNotExistException("Inventory inflow creation failed. One of the selected shelves is not found.");
        }

        if ((Objects.equals(warehouse.isPerishable(), Boolean.FALSE) && Objects.equals(perishable, Boolean.TRUE))
                || (Objects.equals(warehouse.isFlammable(), Boolean.FALSE) && Objects.equals(flammable, Boolean.TRUE))
                || (Objects.equals(warehouse.isPharmaceutical(), Boolean.FALSE) && Objects.equals(pharmaceutical, Boolean.TRUE))
                || (Objects.equals(warehouse.isHighValue(), Boolean.FALSE) && Objects.equals(highValue, Boolean.TRUE))) {
            throw new WarehouseSpecialRequirementException("Warehouse does not meet special requirement (flammable, perishable, pharmaceutical, highValue)");
        }

        Inventory inv = new Inventory(warehouse, quantity, neededShelfCapacity, perishable, flammable, pharmaceutical, highValue, product, "Inflow", shelfList);
        Double newInventoryLevel = product.getInventoryLevel() + quantity;
        product.setInventoryLevel(newInventoryLevel);

        warehouse.setSpareCapacity(warehouse.getSpareCapacity() - totalCapacity);
        warehouse.setUtilizedCapacity(warehouse.getUtilizedCapacity() + totalCapacity);

        if (product.getProductType().equals("FinishedGood")) {
            HashMap<FinishedGood, Double> finishedGoodMap = warehouse.getFinishedGoodMap();

            if (finishedGoodMap.get((FinishedGood) product) == null) {
                System.out.println("Warehouse "+warehouse.getName()+" previously does not store any FinishedGood "+product.getProductName());
                warehouse.getFinishedGoodList().add((FinishedGood) product);
                finishedGoodMap.put((FinishedGood) product, quantity);
            } else {
                Double curQuantity = finishedGoodMap.get((FinishedGood) product);
                System.out.println("Warehouse "+warehouse.getName()+" previously stores FinishedGood "+product.getProductName()+" "+curQuantity+" "+product.getUnit());
                curQuantity = curQuantity + quantity;
                finishedGoodMap.put((FinishedGood) product, curQuantity);
            }
            warehouse.setFinishedGoodMap(finishedGoodMap);
            System.out.println("Warehouse "+warehouse.getName()+" previously stores RawMaterial "+product.getProductName()+" "+finishedGoodMap.get((FinishedGood)product)+" "+product.getUnit());
        } else {
            HashMap<RawMaterial, Double> rawMaterialMap = warehouse.getRawMaterialMap();

            if (rawMaterialMap.get((RawMaterial) product) == null) {
                System.out.println("Warehouse "+warehouse.getName()+" previously does not store any RawMaterial "+product.getProductName());
                warehouse.getRawMaterialList().add((RawMaterial) product);
                rawMaterialMap.put((RawMaterial) product, quantity);
            } else {
                Double curQuantity = rawMaterialMap.get((RawMaterial) product);
                System.out.println("Warehouse "+warehouse.getName()+" previously stores RawMaterial "+product.getProductName()+" "+curQuantity+" "+product.getUnit());
                curQuantity = curQuantity + quantity;
                rawMaterialMap.put((RawMaterial) product, curQuantity);                
            }
            warehouse.setRawMaterialMap(rawMaterialMap);
            System.out.println("Warehouse "+warehouse.getName()+" previously stores RawMaterial "+product.getProductName()+" "+rawMaterialMap.get((RawMaterial)product)+" "+product.getUnit());
        }

        Double quantityInUnitCapacity = product.getQuantityInOneUnitCapacity();

        Double remainingQuantity = quantity;
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            Double shelfQuantity = quantityInUnitCapacity * shelf.getCapacity();
            if (remainingQuantity < shelfQuantity) {
                shelf.setCurQuantity(remainingQuantity);
            } else {
                shelf.setCurQuantity(shelfQuantity);
            }
            shelf.setInventory(inv);
            shelf.setAvailability(Boolean.FALSE);

            em.merge(shelf);
            remainingQuantity = remainingQuantity - shelfQuantity;
        }
        em.merge(warehouse);
        em.persist(inv);
        em.merge(product);

        System.out.println("Inventory entry of " + quantity + " " + product.getUnit() + " " + product.getProductName() + " added into the catalog successfully!");
    }

//    @Override
//    public void createOutflowInventory(String productOwnerCompanyName, String productName, Double quantity, Boolean perishable, Boolean flammable,
//            Boolean pharmaceutical, Boolean highValue, String warehouseOwnerCompanyName, String warehouseName, ArrayList<String> shelfSerialCodeList)
//            throws ProductNotExistException, MultipleProductWithSameNameException, WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, ShelfIncorrectProductException, InsufficientStorageInWarehouseException {
//        Product finishedGood = pim.retrieveProduct(productOwnerCompanyName, productName);
//        Warehouse warehouse = fm.retrieveWarehouse(warehouseOwnerCompanyName, warehouseName);
//        List<Shelf> shelfList = fm.retrieveShelfList(shelfSerialCodeList, warehouseName, warehouseOwnerCompanyName);
//        Integer totalCapacity = shelfSetCapacity(shelfList);
//        Integer neededShelfCapacity = getNeededCapacity(finishedGood, quantity);
//        
//        HashMap<FinishedGood, Double> finishedGoodMap = warehouse.getFinishedGoodMap();
//        List<FinishedGood> finishedGoodList = warehouse.getFinishedGoodList();
//        Double curQuantity = finishedGoodMap.get((FinishedGood)finishedGood);
//        
//        if(curQuantity<quantity)
//            throw new InsufficientStorageInWarehouseException("Warehouse "+warehouseName+" does not have sufficient storage for product "+productName+"!");
//        
//        if(Objects.equals(curQuantity, quantity)){
//            finishedGoodList.remove((FinishedGood)finishedGood);
//            finishedGoodMap.remove((FinishedGood)finishedGood);
//        }
//        else{            
//            curQuantity = curQuantity-quantity;
//            finishedGoodMap.put((FinishedGood)finishedGood, curQuantity);
//        }
//        warehouse.setFinishedGoodMap(finishedGoodMap);
//        warehouse.setFinishedGoodList(finishedGoodList);
//
//        if (neededShelfCapacity > totalCapacity) {
//            throw new ShelfListInsufficientCapacityException("Inventory outflow creation failed. Selected shelf set capacity is insufficient.");
//        }
//
//        if (shelfSetAvailabilityCheck(shelfList)) {
//            throw new ShelfListInsufficientCapacityException("Inventory outflow creation failed. One of the selected shelf has been occupied.");
//        }
//
//        if (finishedGood == null) {
//            throw new ProductNotExistException("Inventory outflow creation failed. No product named" + productName + " exists in DB.");
//        }
//
//        if (warehouse == null) {
//            throw new WarehouseNotExistException("Inventory outflow creation failed. No warehouse named" + warehouseName + " exists in DB.");
//        }
//
//        if (shelfList == null) {
//            throw new ShelfNotExistException("Inventory outflow creation failed. One of the selected shelves is not found.");
//        }
//
//        Inventory inv = new Inventory(quantity, neededShelfCapacity, perishable, flammable, pharmaceutical, highValue, finishedGood, "Outflow", shelfList);
//        Double newInventoryLevel = finishedGood.getInventoryLevel() - quantity;
//        finishedGood.setInventoryLevel(newInventoryLevel);
//
//        warehouse.setSpareCapacity(warehouse.getSpareCapacity() + totalCapacity);
//        warehouse.setUtilizedCapacity(warehouse.getUtilizedCapacity() - totalCapacity);
//        
//        for (Object o : shelfList) {
//            Shelf shelf = (Shelf) o;
//            if (!shelf.getInventory().getProduct().getProductName().equals(productName)) {
//                throw new ShelfIncorrectProductException("Inventory outflow creation failed. One of the selected shelf does not have the target product.");
//            }
//        }
//        for (Object o : shelfList) {
//            Shelf shelf = (Shelf) o;
//            shelf.setCurQuantity(0.0);
//            shelf.setInventory(null);
//            shelf.setAvailability(Boolean.TRUE);
//
//            em.persist(shelf);
//        }
//        em.persist(warehouse);
//        em.persist(inv);
//        em.persist(finishedGood);
//
//        System.out.println("Inventory entry of " + quantity + " " + finishedGood.getUnit() + " " + productName + " added into the catalog successfully!");
//    }
    @Override
    public void createOutflowInventory(Product product, Double quantity, Warehouse warehouse, ArrayList<Shelf> shelfList)
            throws WarehouseNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException, InsufficientStorageInWarehouseException, ShelfIncorrectProductException {
        Integer totalCapacity = shelfSetCapacity(shelfList);
        Integer neededShelfCapacity = getNeededCapacity(product, quantity);

        if (product.getProductType().equals("FinishedGood")) {
            HashMap<FinishedGood, Double> finishedGoodMap = warehouse.getFinishedGoodMap();
            Double curQuantity = finishedGoodMap.get((FinishedGood) product);
            if (curQuantity < quantity) {
                throw new InsufficientStorageInWarehouseException("Warehouse " + warehouse.getName() + " does not have sufficient storage for product " + product.getProductName() + "!");
            }

            if (Objects.equals(curQuantity, quantity)) {
                warehouse.getFinishedGoodList().remove((FinishedGood) product);
                finishedGoodMap.remove((FinishedGood) product);
            } else {
                curQuantity = curQuantity - quantity;
                finishedGoodMap.put((FinishedGood) product, curQuantity);
            }
            warehouse.setFinishedGoodMap(finishedGoodMap);
        } else {
            HashMap<RawMaterial, Double> rawMaterialMap = warehouse.getRawMaterialMap();
            Double curQuantity = rawMaterialMap.get((RawMaterial) product);
            if (curQuantity < quantity) {
                throw new InsufficientStorageInWarehouseException("Warehouse " + warehouse.getName() + " does not have sufficient storage for product " + product.getProductName() + "!");
            }

            if (Objects.equals(curQuantity, quantity)) {
                warehouse.getRawMaterialList().remove((RawMaterial) product);
                rawMaterialMap.remove((RawMaterial) product);
            } else {
                curQuantity = curQuantity - quantity;
                rawMaterialMap.put((RawMaterial) product, curQuantity);
            }
            warehouse.setRawMaterialMap(rawMaterialMap);
        }

        if (neededShelfCapacity > totalCapacity) {
            throw new ShelfListInsufficientCapacityException("Inventory outflow creation failed. Selected shelf set capacity is insufficient.");
        }

        if (shelfSetAvailabilityCheck(shelfList)) {
            throw new ShelfListInsufficientCapacityException("Inventory outflow creation failed. One of the selected shelf has been occupied.");
        }

        if (shelfList == null) {
            throw new ShelfNotExistException("Inventory outflow creation failed. One of the selected shelves is not found.");
        }

        Inventory inv = new Inventory(warehouse, quantity, neededShelfCapacity, product, shelfList);
        Double newInventoryLevel = product.getInventoryLevel() - quantity;
        product.setInventoryLevel(newInventoryLevel);

        warehouse.setSpareCapacity(warehouse.getSpareCapacity() + totalCapacity);
        warehouse.setUtilizedCapacity(warehouse.getUtilizedCapacity() - totalCapacity);

        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            if (!shelf.getInventory().getProduct().getProductName().equals(product.getProductName())) {
                throw new ShelfIncorrectProductException("Inventory outflow creation failed. One of the selected shelf does not have the target product.");
            }
        }

        Double remainingQuantity = quantity;
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            Double curShelfQuantity = shelf.getCurQuantity();
            if (curShelfQuantity < remainingQuantity) {
                shelf.setCurQuantity(0.0);
                shelf.setInventory(null);
                shelf.setAvailability(Boolean.TRUE);
            } else {
                shelf.setCurQuantity(curShelfQuantity - remainingQuantity);
            }

            em.merge(shelf);
        }
        em.merge(warehouse);
        em.persist(inv);
        em.merge(product);

        System.out.println("Inventory entry of " + quantity + " " + product.getUnit() + " " + product.getProductName() + " added into the catalog successfully!");
    }

    @Override
    public ArrayList<Shelf> getShelfListForSpecificProductInOneWarehouse(FinishedGood product, Warehouse warehouse)
            throws ShelfNotExistException {
        Query query = em.createQuery("select s from Shelf s where s.inventory.product=?1 and s.warehouse=?2 and s.archivedOrNot=FALSE ");
        query.setParameter(1, product);
        query.setParameter(2, warehouse);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if (shelfList.isEmpty()) {
            throw new ShelfNotExistException("There is no shelf in Warehouse " + warehouse.getName() + " containing product " + product.getProductName() + " of Company " + product.getOwnerCompanyName() + " not found.");
        }
        return shelfList;
    }

    @Override
    public HashMap<Warehouse, Double> getWarehouseQuantityMapForSpecificProduct(String productName, String ownerCompanyName)
            throws ProductNotExistException, ShelfNotExistException, MultipleProductWithSameNameException {
        Product product = pim.retrieveGeneralProduct(ownerCompanyName, productName);
        Query query = em.createQuery("select s from Shelf s where s.inventory.product=?1 and s.archivedOrNot=FALSE ");
        query.setParameter(1, product);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if (shelfList.isEmpty()) {
            throw new ShelfNotExistException("There is no shelf in any warehouse containing product " + product.getProductName() + " of Company " + product.getOwnerCompanyName() + " not found.");
        }
        HashMap<Warehouse, Double> resultMap = new HashMap<>();
        for (Object o : shelfList) {
            Shelf shelf = (Shelf) o;
            Warehouse curWare = shelf.getWarehouse();
            Double quantity = shelf.getCurQuantity();
            if (resultMap.get(curWare) != null) {
                Double curQuantity = resultMap.get(curWare);
                resultMap.put(curWare, quantity + curQuantity);
            } else {
                resultMap.put(curWare, quantity);
            }
        }
        return resultMap;
    }

    @Override
    public void updateInventory(Long invId, Boolean perishable, Boolean flammable,
            Boolean pharmaceutical, Boolean highValue) throws InventoryNotExistException {
        Inventory targetInv = em.find(Inventory.class, invId);
        if (targetInv == null) {
            throw new InventoryNotExistException("Inventory replacement failed. Inventry entry " + invId + " not found.");
        }

        targetInv.setPerishable(perishable);
        targetInv.setFlammable(flammable);
        targetInv.setPharmaceutical(pharmaceutical);
        targetInv.setHighValue(highValue);
        em.merge(targetInv);
        System.out.println("Inventory " + invId + " update successful");
    }

    //For warehouse owner use
    @Override
    public void replaceInventory(Long invId, ArrayList<String> serialCodeList, String warehouseName, String ownerCompanyName)
            throws InventoryNotExistException, ShelfNotExistException, ShelfListInsufficientCapacityException, ShelfIncorrectProductException {
        Inventory targetInv = em.find(Inventory.class, invId);
        if (targetInv == null) {
            throw new InventoryNotExistException("Inventory " + invId + " not found");
        }
        List<Shelf> newShelfList = fm.retrieveShelfList(serialCodeList, warehouseName, ownerCompanyName);
        if (targetInv.getNeededShelfCapacity() > shelfSetCapacity(newShelfList)) {
            throw new ShelfListInsufficientCapacityException("Inventory replacement failed. Targeted shelf set does not have sufficent capacity");
        }
        if (shelfSetAvailabilityCheck(newShelfList)) {
            throw new ShelfIncorrectProductException("Inventory replacement failed. One of the selected shelf has been occupied.");
        }

        List<Shelf> oldShelfList = targetInv.getShelfList();
        for (Object o : oldShelfList) {
            Shelf shelf = (Shelf) o;
            shelf.setInventory(null);
            em.merge(shelf);
        }

        for (Object o : newShelfList) {
            Shelf shelf = (Shelf) o;
            shelf.setInventory(targetInv);
            em.merge(shelf);
        }
        targetInv.setShelfList(newShelfList);
        em.merge(targetInv);
        System.out.println("Inventory replacement successful");
    }

    //For product owner use, retrieving all inflow inventory record of a specific producct
    @Override
    public ArrayList<Inventory> retrieveInflowInventoryList(String ownerCompanyName, String productName) throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.product.productName=?1 and i.product.ownerCompanyName=?2 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood' and i.flowType='Inflow'");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<Inventory> finishedGoodInventroyList = new ArrayList(query.getResultList());
        if (finishedGoodInventroyList.isEmpty()) {
            throw new InventoryNotExistException("Inflow inventory record about product " + productName + " of Company " + ownerCompanyName + " not found.");
        }
        return finishedGoodInventroyList;
    }

    @Override
    public ArrayList<Inventory> retrieveInflowInventoryListForSpecificWarehouse(Warehouse warehouse)
            throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.warehouse=?1 and i.archivedOrNot=FALSE and i.flowType='Inflow'");
        query.setParameter(1, warehouse);
        ArrayList<Inventory> inventroyList = new ArrayList(query.getResultList());
        if (inventroyList.isEmpty()) {
            throw new InventoryNotExistException("Inflow inventory record in Warehouse " + warehouse.getName() + " not found.");
        }
        return inventroyList;
    }

    @Override
    public ArrayList<Inventory> retrieveInflowInventoryListForSpecificWarehouseForCustomerCompany(Warehouse warehouse, Company ownerCompany)
            throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.warehouse=?1 and i.product.ownerCompany=?2 and i.archivedOrNot=FALSE and i.flowType='Inflow'");
        query.setParameter(1, warehouse);
        query.setParameter(2, ownerCompany);
        ArrayList<Inventory> inventroyList = new ArrayList(query.getResultList());
        if (inventroyList.isEmpty()) {
            throw new InventoryNotExistException("Inflow inventory record for Company " + ownerCompany.getCompanyName() + " in Warehouse " + warehouse.getName() + " not found.");
        }
        return inventroyList;
    }

    @Override
    public ArrayList<Inventory> retrieveOutflowInventoryList(String ownerCompanyName, String productName) throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.product.productName=?1 and i.product.ownerCompanyName=?2 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood' and i.flowType='Outflow'");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<Inventory> finishedGoodInventroyList = new ArrayList(query.getResultList());
        if (finishedGoodInventroyList.isEmpty()) {
            throw new InventoryNotExistException("Outflow inventory record about product " + productName + " of Company " + ownerCompanyName + " not found.");
        }
        return finishedGoodInventroyList;
    }

    @Override
    public ArrayList<Inventory> retrieveOutflowInventoryListForSpecificWarehouse(Warehouse warehouse)
            throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.warehouse=?1 and i.archivedOrNot=FALSE and i.flowType='Outflow'");
        query.setParameter(1, warehouse);
        ArrayList<Inventory> inventroyList = new ArrayList(query.getResultList());
        if (inventroyList.isEmpty()) {
            throw new InventoryNotExistException("Outflow inventory record in Warehouse " + warehouse.getName() + " not found.");
        }
        return inventroyList;
    }

    @Override
    public ArrayList<Inventory> retrieveOutflowInventoryListForSpecificWarehouseForCustomerCompany(Warehouse warehouse, Company ownerCompany)
            throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.warehouse=?1 and i.product.ownerCompany=?2 and i.archivedOrNot=FALSE and i.flowType='Outflow'");
        query.setParameter(1, warehouse);
        query.setParameter(2, ownerCompany);
        ArrayList<Inventory> inventroyList = new ArrayList(query.getResultList());
        if (inventroyList.isEmpty()) {
            throw new InventoryNotExistException("Outflow inventory record for Company " + ownerCompany.getCompanyName() + " in Warehouse " + warehouse.getName() + " not found.");
        }
        return inventroyList;
    }

    @Override
    public void deleteInventory(Inventory inv) {
        inv.setArchivedOrNot(Boolean.TRUE);
        em.merge(inv);
    }

    @Override
    public void deleteProductInventory(String ownerCompanyName, String productName) throws InventoryNotExistException {
        Query query = em.createQuery("select i from Inventory i where i.product.productName=?1 and i.product.ownerCompanyName=?2 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        List<Inventory> finishedGoodInventroyList = query.getResultList();

        if (finishedGoodInventroyList.isEmpty()) {
            throw new InventoryNotExistException("No inventory entry record of Product " + productName + " of Company " + ownerCompanyName + " found.");
        }

        for (Object o : finishedGoodInventroyList) {
            Inventory curInv = (Inventory) o;
            deleteInventory(curInv);
        }
        System.out.println("Inflow and outflow inventory entries of Product " + productName + " of Company " + ownerCompanyName + " has been removed.");
    }

    @Override
    public String getInventoryLocation(Inventory inv) {
        List<Shelf> shelfList = inv.getShelfList();
        Integer size = shelfList.size();
        String startShelfCode = shelfList.get(0).getSerialCode();
        String endShelfCode = shelfList.get(size - 1).getSerialCode();
        return startShelfCode + " to " + endShelfCode;
    }

//    private Double calculateInventoryList(List<Inventory> invList) {
//        Double invLevel = 0.0;
//        for (Object o : invList) {
//            Inventory curInv = (Inventory) o;
//            switch (curInv.getFlowType()) {
//                case "Inflow":
//                    invLevel = invLevel + curInv.getQuantity();
//                    break;
//                case "Outflow":
//                    invLevel = invLevel - curInv.getQuantity();
//                    break;
//            }
//        }
//        return invLevel;
//    }
//
//    private HashMap<Product, List<Inventory>> createProductInventoryMap(List<Inventory> invList) {
//        HashMap<Product, List<Inventory>> productInventoryMap = new HashMap<>();
//        for (Object o : invList) {
//            Inventory curInv = (Inventory) o;
//            Product curProduct = curInv.getProduct();
//            List<Inventory> curInvList = productInventoryMap.get(curProduct);
//            if (curInvList == null) {
//                curInvList = new ArrayList<>();
//                curInvList.add(curInv);
//                productInventoryMap.put(curProduct, curInvList);
//            } else {
//                curInvList.add(curInv);
//            }
//        }
//        return productInventoryMap;
//    }
//
//    private HashMap<Product, Double> getInventoryLevelList(HashMap<Product, List<Inventory>> productInventoryMap) {
//        HashMap<Product, Double> localInventoryLevelList = new HashMap<>();
//
//        for (Map.Entry entry : productInventoryMap.entrySet()) {
//            Product curPro = (Product) entry.getKey();
//            Double invLevel = calculateInventoryList((List) entry.getValue());
//            localInventoryLevelList.put(curPro, invLevel);
//        }
//        return localInventoryLevelList;
//    }
//    //For warehouse owner company usage
//    @Override
//    public Double retrieveLocalInventoryLevel(String productOwnerCompanyName, Long warehouseId, String productName) throws InventoryNotExistException {
//        Query query = em.createQuery("select i from Inventory i where i.product.productName=?1 and i.product.ownerCompanyName=?2 and i.id=?3 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
//        query.setParameter(1, productName);
//        query.setParameter(2, productOwnerCompanyName);
//        query.setParameter(3, warehouseId);
//        List<Inventory> finishedGoodInventroyList = query.getResultList();
//        if (finishedGoodInventroyList.isEmpty()) {
//            throw new InventoryNotExistException("No inventory record of product " + productName + " of Company " + productOwnerCompanyName + " in Warehouse " + warehouseId);
//        }
//        return calculateInventoryList(finishedGoodInventroyList);
//    }
//    @Override
//    public HashMap<Product, Double> retrieveLocalInventoryLevelList(String productOwnerCompanyName, Long warehouseId) throws InventoryNotExistException {
//        Query query = em.createQuery("select i from Inventory i where i.product.ownerCompanyName=?2 and i.id=?3 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
//        query.setParameter(2, productOwnerCompanyName);
//        query.setParameter(3, warehouseId);
//        List<Inventory> finishedGoodInventroyList = query.getResultList();
//        if (finishedGoodInventroyList.isEmpty()) {
//            throw new InventoryNotExistException("No inventory record of Company " + productOwnerCompanyName + " in Warehouse " + warehouseId);
//        }
//        HashMap<Product, List<Inventory>> productInventoryMap = createProductInventoryMap(finishedGoodInventroyList);
//        return getInventoryLevelList(productInventoryMap);
//    }
//    @Override
//    public HashMap<Product, Double> retrieveLocalInventoryLevelWholeList(Long warehouseId) 
//            throws InventoryNotExistException {
//        Query query = em.createQuery("select i from Inventory i where i.id=?3 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
//        query.setParameter(3, warehouseId);
//        List<Inventory> finishedGoodInventroyList = query.getResultList();
//        if (finishedGoodInventroyList.isEmpty()) {
//            throw new InventoryNotExistException("No inventory record of product in Warehouse " + warehouseId);
//        }
//        HashMap<Product, List<Inventory>> productInventoryMap = createProductInventoryMap(finishedGoodInventroyList);
//        return getInventoryLevelList(productInventoryMap);
//    }
//    //For inventory owner company usage
//    //At company scale
//    @Override //the total inventory level of a selected product in all warehouses
//    public Double retrieveCompanyInventoryLevel(String productOwnerCompanyName, String productName) 
//            throws InventoryNotExistException {
//        Query query = em.createQuery("select i from Inventory i where i.product.productName=?1 and i.product.ownerCompanyName=?2 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
//        query.setParameter(1, productName);
//        query.setParameter(2, productOwnerCompanyName);
//        List<Inventory> finishedGoodInventroyList = query.getResultList();
//        if (finishedGoodInventroyList.isEmpty()) {
//            throw new InventoryNotExistException("No inventory record of product " + productName + " of Company " + productOwnerCompanyName);
//        }
//        return calculateInventoryList(finishedGoodInventroyList);
//    }
//    @Override //A list of total inventory levels of all products in all warehouses
//    public HashMap<Product, Double> retrieveCompanyInventoryLevelList(String productOwnerCompanyName) throws InventoryNotExistException {
//        Query query = em.createQuery("select i from Inventory i where i.product.ownerCompanyName=?2 and i.archivedOrNot=FALSE and i.product.productType='FinishedGood'");
//        query.setParameter(2, productOwnerCompanyName);
//        List<Inventory> finishedGoodInventroyList = query.getResultList();
//        if (finishedGoodInventroyList.isEmpty()) {
//            throw new InventoryNotExistException("No inventory record of products of Company " + productOwnerCompanyName);
//        }
//        HashMap<Product, List<Inventory>> productInventoryMap = createProductInventoryMap(finishedGoodInventroyList);
//        return getInventoryLevelList(productInventoryMap);
//    }
}
