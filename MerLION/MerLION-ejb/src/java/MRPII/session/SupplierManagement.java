/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import Common.entity.CustomerCompany;
import MRPII.entity.RawMaterial;
import MRPII.entity.Supplier;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SupplierAlreadyExistException;
import util.exception.SupplierNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class SupplierManagement implements SupplierManagementLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private RawMaterialManagementLocal rmm;
    @EJB
    private ProductInfoManagementLocal pim;

    @Override
    public void addSupplier(String supplierName, String email, CustomerCompany ownerCompany, List<Product> supplyProductList, HashMap<Product, Integer> lagTimeMap, List<String> allSupplyProductList)
            throws SupplierAlreadyExistException {
        Supplier supplier = null;
        try {
            supplier = retrieveSupplier(supplierName, ownerCompany);
        } catch (SupplierNotExistException ex) {
            System.out.println("Supplier not in current list. Creation continues.");
        }
        if (supplier != null) {
            throw new SupplierAlreadyExistException("Supplier named " + supplierName + " already exists!");
        }
        supplier = new Supplier(supplierName, email, ownerCompany, supplyProductList, lagTimeMap, allSupplyProductList);
        em.persist(supplier);
        ownerCompany.getSupplierList().add(supplier);
        em.merge(ownerCompany);
        

        for (Object o : supplyProductList) {
            Product curMaterial = (Product) o;
            curMaterial.getSupplierList().add(supplier);
            em.merge(curMaterial);
        }

        System.out.println("New supplier " + supplierName + " added to provide raw materials.");
    }

    @Override
    public void updateStrList(Supplier supplier,List<String> allSupplyProductList) {
        supplier.setAllSupplyList(allSupplyProductList);
        em.merge(supplier);
    }

    @Override
    public void deleteMaterial(Supplier supplier, Product selectedMaterial) throws RawMaterialNotExistException {
        List<Product> updateList = new ArrayList();
        updateList = supplier.getSupplyList();
        if (!updateList.contains(selectedMaterial)) {
            throw new RawMaterialNotExistException("Raw Material " + selectedMaterial.getProductName() + " does not exist in the list!");
        } else {
            updateList.remove(selectedMaterial);
            supplier.setSupplyList(updateList);
        }
        
        HashMap<Product, Integer> updateMap = new HashMap();
        updateMap = supplier.getLagTimeMap();
        if (!updateMap.containsKey(selectedMaterial)) {
            throw new RawMaterialNotExistException("Raw Material " + selectedMaterial.getProductName() + " does not exist in the list!");
        } else {
            updateMap.remove(selectedMaterial);
            supplier.setLagTimeMap(updateMap);
        }
        em.merge(supplier);
    }

    @Override
    public void addMaterial(Supplier supplier, Product selectedMaterial, Integer leadTime) throws RawMaterialAlreadyExistException {
        List<Product> updateList = new ArrayList();
        updateList = supplier.getSupplyList();
        if (updateList.contains(selectedMaterial)) {
            throw new RawMaterialAlreadyExistException("Raw Material " + selectedMaterial.getProductName() + " already exists in the list!");
        } else {
            updateList.add(selectedMaterial);
            supplier.setSupplyList(updateList);
        }

        HashMap<Product, Integer> updateMap = new HashMap();
        updateMap = supplier.getLagTimeMap();
        if (updateMap.containsKey(selectedMaterial)) {
            throw new RawMaterialAlreadyExistException("Raw Material " + selectedMaterial.getProductName() + " already exists in the list!");
        } else {
            updateMap.put(selectedMaterial, leadTime);
            supplier.setLagTimeMap(updateMap);
        }
        em.merge(supplier);
    }

    @Override
    public Supplier retrieveSupplier(String supplierName, CustomerCompany ownerCompany)
            throws SupplierNotExistException {
        Query query = em.createQuery("select s from Supplier s where s.name=?1 and s.archivedOrNot=FALSE and s.supplyToCompany=?2");
        query.setParameter(1, supplierName);
        query.setParameter(2, ownerCompany);
        ArrayList<Supplier> supplierList = new ArrayList(query.getResultList());
        if (supplierList.isEmpty()) {
            throw new SupplierNotExistException("Supplier " + supplierName + " does not exist!");
        }
        assertEquals(supplierList.size(), 1);
        Supplier supplier = supplierList.get(0);
        return supplier;
    }

    @Override
    public List<Supplier> retrieveSupplierListForCompany(CustomerCompany ownerCompany)
            throws SupplierNotExistException {
        List<Supplier> supplierList = ownerCompany.getSupplierList();
        if (supplierList.isEmpty()) {
            throw new SupplierNotExistException("Company " + ownerCompany.getCompanyName() + " does not have any supplier!");
        }
        return supplierList;
    }

    @Override
    public ArrayList<Supplier> retrieveSupplierListForMaterial(String ownerCompanyName, String materialName)
            throws SupplierNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException {
        RawMaterial rm = rmm.retrieveRawMaterial(ownerCompanyName, materialName);
        assertNotNull(rm);
        List<Supplier> list = rm.getSupplierList();
        if (list.isEmpty()) {
            throw new SupplierNotExistException("Company " + ownerCompanyName + " does not have any supplier!");
        }
        ArrayList<Supplier> supplierList = new ArrayList<>();
        for (Object o : list) {
            Supplier curSupplier = (Supplier) o;
            if (!curSupplier.isArchivedOrNot()) {
                supplierList.add(curSupplier);
            }
        }
        return supplierList;
    }

    @Override
    public List<Supplier> retrieveSupplierListForProduct(String ownerCompanyName, String productName)
            throws SupplierNotExistException, ProductNotExistException, MultipleProductWithSameNameException {
        Product product = pim.retrieveProduct(ownerCompanyName, productName);
        assertNotNull(product);
        List<Supplier> list = product.getSupplierList();
        if (list.isEmpty()) {
            throw new SupplierNotExistException("Company " + ownerCompanyName + " does not have any supplier!");
        }
        ArrayList<Supplier> supplierList = new ArrayList<>();
        for (Object o : list) {
            Supplier curSupplier = (Supplier) o;
            if (!curSupplier.isArchivedOrNot()) {
                supplierList.add(curSupplier);
            }
        }
        return supplierList;
    }

    @Override
    public void removeSupplier(String supplierName, CustomerCompany ownerCompany)
            throws SupplierNotExistException {
        Supplier supplier = retrieveSupplier(supplierName, ownerCompany);
        List<Product> productList = supplier.getSupplyList();
        for (Object o : productList) {
            Product curPro = (Product) o;
            curPro.getSupplierList().remove(supplier);
            em.merge(curPro);
        }
        ownerCompany.getSupplierList().remove(supplier);
        em.merge(ownerCompany);
        supplier.setArchivedOrNot(Boolean.TRUE);
        em.merge(supplier);
        System.out.println("Supplier " + supplierName + " archived successfully.");
    }

    @Override
    public void updateSupplierCommon(String supplierName, CustomerCompany ownerCompany)
            throws SupplierNotExistException {
        Supplier supplier = retrieveSupplier(supplierName, ownerCompany);
        assertNotNull(supplier);
        String oldName = supplier.getName();
        supplier.setName(supplierName);
        em.merge(supplier);
        System.out.println("Supplier " + oldName + "has started to use its new name " + supplierName);
    }

    @Override
    public void addMaterialSupplier(Supplier supplier, CustomerCompany ownerCompany, Product rm, Integer lagTime)
            throws SupplierNotExistException {
        List<Supplier> list = rm.getSupplierList();
        if (!list.contains(supplier)) {
            list.add(supplier);
        }
        rm.setSupplierList(list);
        List<Product> rmList = supplier.getSupplyList();
        if (!rmList.contains(rm)) {
            rmList.add(rm);
        }
        supplier.setSupplyList(rmList);
        supplier.getLagTimeMap().put(rm, lagTime);
        em.merge(rm);
        em.merge(supplier);
        System.out.println("Supplier " + supplier.getName() + " are now providing Raw material " + rm.getProductName());
    }

    @Override
    public void removeMaterialSupplier(Supplier supplier, String ownerCompanyName, String materialName)
            throws SupplierNotExistException, ProductNotExistException, MultipleProductWithSameNameException {
        Product rm = pim.retrieveGeneralProduct(ownerCompanyName, materialName);

        List<Supplier> list = rm.getSupplierList();
        list.remove(supplier);
        rm.setSupplierList(list);
        List<Product> rmList = supplier.getSupplyList();
        rmList.remove(rm);
        supplier.setSupplyList(rmList);
        supplier.getLagTimeMap().remove(rm);
        em.merge(rm);
        em.merge(supplier);
        System.out.println("Supplier " + supplier.getName() + " no longer provide Raw material " + materialName);
    }

}
