/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WMS.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceContractManagementSessionLocal;
import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Location;
import Common.entity.PartnerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
import MRPII.session.RawMaterialManagementLocal;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ShelfAlreadyExistException;
import util.exception.ShelfNotExistException;
import util.exception.ShelfOccupiedException;
import util.exception.WarehouseAlreadyExistException;
import util.exception.WarehouseNotEmptyException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class FacilityManagement implements FacilityManagementLocal {
    @PersistenceContext
    EntityManager em;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private ServiceContractManagementSessionLocal scm;
    @EJB
    private RawMaterialManagementLocal rmm;
    
    private Integer shelfSetCapacity(Set<Shelf> shelfList){
        Integer totalCapacity = 0;
        for (Object o: shelfList){
            Shelf shelf = (Shelf) o;
            if(!shelf.isArchivedOrNot())
                totalCapacity = totalCapacity + shelf.getCapacity();
        }     
        return totalCapacity;
    }
    
    private Boolean shelfSetAvailabilityCheck(List<Shelf> shelfList){
        for (Object o: shelfList){
            Shelf shelf = (Shelf) o;
            if(!shelf.getAvailability())
                return false;
        }     
        return true;
    }
    
//    public Company retrieveCompany(String companyName){
//        Query query = em.createQuery("select c from Company c where c.companyName=?1 and c.lockedOrNot=false");
//        query.setParameter(1, companyName);
//        ArrayList<Company> ownerCompanyList = new ArrayList(query.getResultList());
//        Company company = ownerCompanyList.get(0);
//        return company;
//    }
    
       
    @Override
    public void createWarehouse(String warehouseName, String contactNo, String ownerCompanyName, Location location, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Double pricePerCapacityPerDay)
            throws CompanyNotExistException, WarehouseAlreadyExistException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);        
        Warehouse warehouse = null;
        try {
            warehouse = this.retrieveWarehouse(warehouseName, ownerCompanyName);
        } catch (WarehouseNotExistException ex) {
            System.out.println("Currently no warehouse with the name "+warehouseName +" under Company "+ownerCompanyName+". Creation proceeds.");
        }
        
        if(warehouse!=null)
            throw new WarehouseAlreadyExistException("Warehouse with name "+warehouseName+" already exists under Company "+ownerCompanyName+"!");
        warehouse = new Warehouse(warehouseName, contactNo, ownerCompany, location, perishable, flammable, pharmaceutical, highValue, pricePerCapacityPerDay);
        List<Warehouse> whList = ownerCompany.getOwnedWarehouseList();
        whList.add(warehouse);
        ownerCompany.setOwnedWarehouseList(whList);
        em.persist(warehouse);
        em.merge(ownerCompany);
        
        System.out.println("Warehouse creation successful. Warehouse "+warehouseName+" Id "+warehouse.getId()+" has been added to Company "+ownerCompanyName);
    }
    
    //populate data
    
    
   
    
    @Override//a total replacement of shelf list will be needed when the warehouse is facing a major upgrade. All record in the old shelf list will all be disgarded 
                //At this point, the warehouse is assumed to have no inventory (the availability of all shelves are TRUE)
    public void disgardShelfSet(String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException, WarehouseNotEmptyException{
        Warehouse targetWarehouse = retrieveWarehouse(ownerCompanyName, warehouseName);
        List<Shelf> shelfList = targetWarehouse.getShelfList();        
        if(!shelfSetAvailabilityCheck(shelfList)){
            System.out.println( "The warehouse has not been emptyed yet. Some of the shelves are not empty.");
            throw new WarehouseNotEmptyException("The warehouse has not been emptyed yet. Some of the shelves are not empty.");
        }
            
        if(shelfList!=null){
            for (Object o: shelfList){
                Shelf curShelf = (Shelf) o;
                curShelf.setArchivedOrNot(Boolean.TRUE);
                em.merge(curShelf);
            }
        }
        System.out.println( "Current shelf set disgard successful. ");
    }
    
    @Override
    public void updateWarehouseCommom(String warehouseName, String ownerCompanyName, String newWarehouseName, String contactNo, Location location, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue)
            throws CompanyNotExistException, WarehouseNotExistException{
        Warehouse targetWarehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        if(targetWarehouse==null)
            System.out.println( "Warehouse update failed. Warehouse "+ownerCompanyName+"has been archived or does not exist in database.");        
        targetWarehouse.setName(newWarehouseName);
        targetWarehouse.setContactNo(contactNo);
        targetWarehouse.setLocation(location);
        em.merge(targetWarehouse);
        System.out.println( "Warehouse common update successful. Here are the latest warehouse info\n"
                + "Warehouse name: "+newWarehouseName+"\n"
                + "Contact: "+contactNo+"\n"
                + "Location are too long so I won't display it here. Anyway it is a success");
    }
    
    @Override
    public void updateWarehouseOwner(String warehouseName, String ownerCompanyName, String newOwnerCompanyName)throws CompanyNotExistException, WarehouseNotExistException{
        Company newOwnerCompany = cmsb.retrieveCompany(newOwnerCompanyName);
        if(newOwnerCompany==null)
            System.out.println( "Alternation of ownership failed. The new owner company "+newOwnerCompanyName+"is not found.");
        Warehouse targetWarehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        if(targetWarehouse==null)
            System.out.println( "Warehouse update failed. Warehouse "+ownerCompanyName+"has been archived or does not exist in database.");
        Company oldOwnerCompany = cmsb.retrieveCompany(ownerCompanyName);
        oldOwnerCompany.getOwnedWarehouseList().remove(targetWarehouse);
        targetWarehouse.setOwnerCompany(newOwnerCompany);
        newOwnerCompany.getOwnedWarehouseList().add(targetWarehouse);
        
        em.merge(oldOwnerCompany);
        em.merge(newOwnerCompany);
        em.merge(targetWarehouse);
        
        System.out.println( "Alternation of ownership successful.\n "
                + "Ownership of Warehouse "+warehouseName+" has been shifted from Company "+ownerCompanyName+" to Company "+newOwnerCompanyName+".");
    }
    
    @Override
    public Warehouse retrieveWarehouse(String warehouseName, String ownerCompanyName) throws WarehouseNotExistException{
        Query query = em.createQuery("select w from Warehouse w where w.ownerCompany.companyName=?1 and w.name=?2 and w.archivedOrNot=false");
        query.setParameter(1, ownerCompanyName);
        query.setParameter(2, warehouseName);
        ArrayList<Warehouse> warehouseList = new ArrayList(query.getResultList());
        if(warehouseList.isEmpty())
            throw new WarehouseNotExistException("Warehouse "+warehouseName+" of Company "+ownerCompanyName+" not exist!");
        Warehouse warehouse = warehouseList.get(0);
        return warehouse;
    }
    
    @Override
    public Warehouse retrieveWarehouse(Long warehouseId)
            throws WarehouseNotExistException{
        Warehouse warehouse = em.find(Warehouse.class, warehouseId);
        if(warehouse==null)
            throw new WarehouseNotExistException("Warehouse "+warehouseId+" not found!");
        return warehouse;
    }
    
    @Override
    public List<Warehouse> retrieveWarehouseListForSpecificProduct(String productName, String productOwnerName)
            throws CompanyNotExistException, ProductNotExistException, MultipleProductWithSameNameException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        List<Warehouse> result = new ArrayList<>();
        Company ownerCompany = cmsb.retrieveCompany(productOwnerName);
        Product product = pim.retrieveProduct(productOwnerName, productName);
        if(product.getProductType().equals("FinishedGood"))
            result = this.retrieveWarehouseListForSpecificFinishedGood(productName, productOwnerName);
        else
            result = this.retrieveWarehouseListForSpecificRawMaterial(productName, productOwnerName);
        
        return result;
    }
    
    @Override
    public List<Warehouse> retrieveWarehouseListForSpecificFinishedGood(String productName, String productOwnerName)
            throws CompanyNotExistException, ProductNotExistException, MultipleProductWithSameNameException{
        Company ownerCompany = cmsb.retrieveCompany(productOwnerName);
        FinishedGood product = pim.retrieveProduct(productOwnerName, productName);
        List<Warehouse> warehouseList = ownerCompany.getOwnedWarehouseList();
        List<Warehouse> result = new ArrayList<>();
        List<Warehouse> otsourcingWhList = new ArrayList<>();
        try {
            otsourcingWhList = retrieveOutsourcingWarehouseList(productOwnerName);
            warehouseList.addAll(otsourcingWhList);
        } catch (ServiceContractNotExistException ex) {
            System.out.println("Company "+productOwnerName+" does not has outsourcing warehouse. Proceed.");
        }
        for(Object o: warehouseList){
            Warehouse curWh = (Warehouse) o;
            if(curWh.getFinishedGoodList().contains(product))
                result.add(curWh);
        }
        return result;
    }
    
    @Override
    public List<Warehouse> retrieveWarehouseListForSpecificRawMaterial(String productName, String productOwnerName)
            throws CompanyNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        Company ownerCompany = cmsb.retrieveCompany(productOwnerName);
        RawMaterial product = rmm.retrieveRawMaterial(productOwnerName, productName);
        List<Warehouse> warehouseList = ownerCompany.getOwnedWarehouseList();
        List<Warehouse> result = new ArrayList<>();
        List<Warehouse> otsourcingWhList = new ArrayList<>();
        try {
            otsourcingWhList = retrieveOutsourcingWarehouseList(productOwnerName);
            warehouseList.addAll(otsourcingWhList);
        } catch (ServiceContractNotExistException ex) {
            System.out.println("Company "+productOwnerName+" does not has outsourcing warehouse. Proceed.");
        }
        
        for(Object o: warehouseList){
            Warehouse curWh = (Warehouse) o;
            if(curWh.getRawMaterialList().contains(product))
                result.add(curWh);
        }
        return result;
    }
    
    @Override
    public List<Warehouse> retrieveOwnedWarehouseList(Company ownerCompany) {
        Query query = em.createQuery("select w from Warehouse w where w.ownerCompany=?1 and w.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompany);
        List<Warehouse> warehouseList = new ArrayList(query.getResultList());
        return warehouseList;
    }
    
    @Override
    public List<Warehouse> retrieveOutsourcingWarehouseList(String ownerComapnyName) 
            throws CompanyNotExistException, ServiceContractNotExistException{
        List<ServiceContract> serviceContractList = scm.retrieveCurrentOutsourcingServiceContractList(ownerComapnyName);
        List<Warehouse> warehouseList = new ArrayList<>();
        for(Object o: serviceContractList){
            ServiceContract sc = (ServiceContract) o;
            Warehouse curWh = sc.getServiceQuotation().getWarehouse();
            warehouseList.add(curWh);
        }
        return warehouseList;
    }
    
    @Override //Assumption: this warehouse has been emptyed
    public void deleteWarehouse(String warehouseName, String ownerCompanyName) 
            throws CompanyNotExistException, WarehouseNotExistException, WarehouseNotEmptyException{
        Company ownerCompany = cmsb.retrieveCompany(ownerCompanyName);
        Warehouse targetWarehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        if(targetWarehouse==null)
            System.out.println( "Warehouse removal failed. Warehouse "+ownerCompanyName+"has been archived or does not exist in database.");  
        disgardShelfSet(ownerCompanyName, warehouseName);
        ownerCompany.getOwnedWarehouseList().remove(targetWarehouse);
        em.merge(ownerCompany);
        targetWarehouse.setArchivedOrNot(Boolean.TRUE);        
        em.merge(targetWarehouse);
        System.out.println( "Warehouse "+warehouseName+" of Company "+ownerCompanyName+" has been archived successfully.");
    }
    
    @Override
    public Integer companyOwnedWarehouseCapacity(Company partnerCompany){
        List<Warehouse> warehouseList = partnerCompany.getOwnedWarehouseList();
        
        Integer totalSpareCapacity = 0;
        if(warehouseList.isEmpty())
            return totalSpareCapacity;
        else{
            for(Object o: warehouseList){
                Warehouse curWare = (Warehouse) o;
                Integer curCapacity = curWare.getCapacity();
                totalSpareCapacity = totalSpareCapacity + curCapacity;
            }
            return totalSpareCapacity;
        }        
    }
    
    @Override
    public Integer getRegionShelfNumber(String warehouseName, String warehouseOwnerName, String regionCode) 
            throws WarehouseNotExistException, ShelfNotExistException{
        Warehouse wh = retrieveWarehouse(warehouseName, warehouseOwnerName);
        return retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, warehouseOwnerName).size();
    }
    
    @Override
    public Integer getRegionCapacity(String warehouseName, String warehouseOwnerName, String regionCode) 
            throws WarehouseNotExistException, ShelfNotExistException{
        Warehouse wh = retrieveWarehouse(warehouseName, warehouseOwnerName);
        List<Shelf> shelfList = retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, warehouseOwnerName);
        Integer result = 0;
        for(Object o: shelfList){
            Shelf curShelf = (Shelf) o;
            result = result+curShelf.getCapacity();
        }
        return result;
    }
    
    @Override
    public Integer getRegionSpareCapacity(String warehouseName, String warehouseOwnerName, String regionCode) 
            throws WarehouseNotExistException, ShelfNotExistException{
        List<Shelf> shelfList = retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, warehouseOwnerName);
        Integer result = 0;
        for(Object o: shelfList){
            Shelf curShelf = (Shelf) o;
            if(curShelf.getAvailability())
                result = result+curShelf.getCapacity();
        }
        return result;
    }

    @Override
    public void createShelf(String regionCode, Integer number, String serialCode, Integer capacity, String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfAlreadyExistException{
        Warehouse warehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        try {
            Shelf alreadyExist = retrieveShelf(regionCode, warehouseName, ownerCompanyName);
            if(alreadyExist!=null)
                throw new ShelfAlreadyExistException("Shelf creation failed. Shelf with serial code "+serialCode+" already exist!");            
        } catch (ShelfNotExistException ex) {
            System.out.println("No existing shelf with the same serial code found. Creation proceeds.");
        }
        
        Shelf shelf = new Shelf(regionCode, number, serialCode, capacity, warehouse);
        warehouse.getShelfList().add(shelf);
        warehouse.setCapacity(warehouse.getCapacity()+capacity);
        warehouse.setSpareCapacity(warehouse.getSpareCapacity()+capacity);
        List<String> regionList = warehouse.getRegionList();
        if(!regionList.contains(regionCode))
            regionList.add(regionCode);
        
        em.persist(shelf);
        em.merge(warehouse);
        
        System.out.println( "Shelf creation successful. Shelf Id "+serialCode+" has been added to Warehouse "+warehouseName);
    }
    
    @Override
    public void createShelfListForSpecificWarehouse(String regionCode, Integer shelvesNum, Integer capacity, String warehouseName, String ownerCompanyName)
            throws WarehouseNotExistException, ShelfAlreadyExistException{
        Integer existingShelfNum = 0;
        if(regionCode == null)
            throw new ShelfAlreadyExistException("Shelf creation failed. Region code is null.");
        
        try {
            List<Shelf> shelfList = retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, ownerCompanyName);
            existingShelfNum = getMaxNoOfShelfList(shelfList);
        } catch (ShelfNotExistException ex) {
            System.out.println("Current region have no shelf. Proceed");
        }        
            
        for(Integer i=existingShelfNum+1; i<existingShelfNum+shelvesNum+1; i++){
            String numStr = i.toString();
            if(i<10)
                numStr = "00"+numStr;
            else if(i<100 && i>=10)
                numStr = "0"+numStr;
            String serialCode = regionCode+numStr;
            createShelf(regionCode, i, serialCode, capacity, warehouseName, ownerCompanyName);
        }
    }
    
    private Integer getMaxNoOfShelfList(List<Shelf> shelfList){
        Integer result = 0;
        Integer size = shelfList.size();
        for(int i=size-1; i>=0; i--){
            Integer curInt = shelfList.get(i).getNumber();
            if(result<curInt)
                result = curInt;
        }
        return result;
    }
//
//    @Override
//    public void updateShelf(String serialCode, String warehouseName, String ownerCompanyName, Integer newCapacity)
//            throws WarehouseNotExistException, ShelfNotExistException{
//        Shelf targetShelf = retrieveShelf(serialCode, warehouseName, ownerCompanyName);
//        Warehouse warehouse = retrieveWarehouse(ownerCompanyName, warehouseName);
//                        
//        warehouse.setCapacity(warehouse.getCapacity()-targetShelf.getCapacity()+newCapacity);
//        warehouse.setSpareCapacity(warehouse.getSpareCapacity()+newCapacity);
//        targetShelf.setCapacity(newCapacity);
//        
//        em.persist(warehouse);
//        em.persist(targetShelf);
//    }
    
//    //pre-con: the 3 ArrayList should be the same size
//    @Override
//    public void updateShelfList(ArrayList<String> serialCode, String warehouseName, String ownerCompanyName, ArrayList<String> newSerialCode, ArrayList<Integer> newCapacity)
//            throws WarehouseNotExistException, ShelfNotExistException {
//        if(!(serialCode.size()==newSerialCode.size() && newSerialCode.size()==newCapacity.size()))
//            System.out.println( "Update shelf list failed! The number of new serialCodes is not equal to the number of old serialCodes.");
//        Integer size = serialCode.size();
//        for(int i=0; i<size; i++){
//            String curSerialCode = serialCode.get(i);
//            String curNewSerialCode = newSerialCode.get(i);
//            Integer curNewCapacity = newCapacity.get(i);
//            updateShelf(curSerialCode, warehouseName, ownerCompanyName, curNewSerialCode, curNewCapacity);
//        }
//        System.out.println( "Shelf update successful. The new Serial Code is "+newSerialCode+". The new capacity of the shelf is "+newCapacity+".");
//    }
//    
//    @Override
//    public void updateShelfList(String regionCode, Integer startNum, Integer endNum, String warehouseName, String ownerCompanyName, Integer newCapacity)
//            throws WarehouseNotExistException, ShelfNotExistException{
//        Warehouse warehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
//        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.regionCode=?2 and s.number BETWEEN ?3 AND ?4 and s.archivedOrNot=false");
//        query.setParameter(1, warehouse);
//        query.setParameter(2, regionCode);
//        query.setParameter(3, startNum);
//        query.setParameter(4, endNum);
//        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
//        if(shelfList.isEmpty())
//            throw new ShelfNotExistException("Shlef does not exist!");
//        for(Object o: shelfList){
//            Shelf curShelf = (Shelf) o;
//            updateShelf(curShelf.getSerialCode(), warehouseName, ownerCompanyName, newCapacity);
//        }
//    }
    
    @Override
    public Shelf retrieveShelf(String serialCode, String warehouseName, String ownerCompanyName) throws ShelfNotExistException{
        Query query = em.createQuery("select s from Shelf s where s.warehouse.ownerCompany.companyName=?1 and s.warehouse.name=?2 and s.serialCode=?3 and s.archivedOrNot=false");
        query.setParameter(1, ownerCompanyName);
        query.setParameter(2, warehouseName);
        query.setParameter(3, serialCode);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Shlef "+serialCode+" in Warehouse "+warehouseName+" of Company "+ownerCompanyName+" does not exist!");
        Shelf shelf = shelfList.get(0);
        return shelf;
    }
    
    @Override
    public List<Shelf> retrieveShelfList(ArrayList<String> serialCodeList, String warehouseName, String ownerCompanyName) throws ShelfNotExistException{
        if(serialCodeList.isEmpty())
            throw new ShelfNotExistException("Input serial code list is empty.");
        
        List<Shelf> shelfList = new ArrayList<>();
        for(Object o: serialCodeList){
            String curCode = (String) o;
            Shelf curShelf = retrieveShelf(curCode, warehouseName, ownerCompanyName);
            if(curShelf == null)
                return null;
            shelfList.add((Shelf) o);
        }
        return shelfList;
    }
    
    @Override
    public List<Shelf> retrieveShelfListInSpecificWarehouse(String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfNotExistException{
        Warehouse warehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.archivedOrNot=FALSE");
        query.setParameter(1, warehouse);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Warehouse "+warehouseName+" of Company "+ownerCompanyName+" does not have any shelf!");
        return shelfList;
    }
    
    @Override
    public List<Shelf> retrieveShelfListInSpecificRegionInSpecificWarehouse(String regionCode, String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfNotExistException{
        Warehouse warehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.regionCode=?2 and s.archivedOrNot=FALSE");
        query.setParameter(1, warehouse);
        query.setParameter(2, regionCode);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Region "+regionCode+" does not have any shelf!");
        return shelfList;
    }
    
    @Override
    public List<Shelf> retrieveShelfListForSpecificProductInSpecificWarehouse(Warehouse warehouse, Product product) 
            throws ShelfNotExistException{
        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.archivedOrNot=FALSE and s.inventory.product=?2");
        query.setParameter(1, warehouse);
        query.setParameter(2, product);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Warehouse "+warehouse.getName()+" of Company "+warehouse.getOwnerCompany().getCompanyName()+" does not have any shelf containing Product "+product.getProductName()+"!");
        return shelfList;
    }
    
    @Override
    public List<Shelf> retrieveShelfListForSpecificProductInSpecificWarehouse(String warehouseName, String ownerCompanyName, String productName, String productOwnerName) 
            throws ShelfNotExistException, WarehouseNotExistException, ProductNotExistException, MultipleProductWithSameNameException{
        Warehouse warehouse= retrieveWarehouse(warehouseName, ownerCompanyName);
        Product product = pim.retrieveGeneralProduct(productOwnerName, productName);
        Query query = em.createQuery("select s from Shelf s where s.warehouse=?1 and s.archivedOrNot=FALSE and s.inventory.product=?2");
        query.setParameter(1, warehouse);
        query.setParameter(2, product);
        ArrayList<Shelf> shelfList = new ArrayList(query.getResultList());
        if(shelfList.isEmpty())
            throw new ShelfNotExistException("Warehouse "+warehouse.getName()+" of Company "+warehouse.getOwnerCompany().getCompanyName()+" does not have any shelf containing Product "+product.getProductName()+"!");
        return shelfList;
    }

    @Override
    public void deleteShelf(String serialCode, String warehouseName, String ownerCompanyName) 
            throws ShelfNotExistException, ShelfOccupiedException, WarehouseNotExistException{
        Shelf targetShelf = retrieveShelf(serialCode, warehouseName, ownerCompanyName);
        if(!targetShelf.getAvailability())
            throw new ShelfOccupiedException( "Shelf removal failed. Shelf "+serialCode+"in Warehouse "+warehouseName+" of Company "+ownerCompanyName+" is still occupied.");
        targetShelf.setArchivedOrNot(Boolean.TRUE);
        Warehouse warehouse = targetShelf.getWarehouse();
        warehouse.setCapacity(warehouse.getCapacity()-targetShelf.getCapacity());
        warehouse.setSpareCapacity(warehouse.getSpareCapacity()-targetShelf.getCapacity());
        
        List<String> regionList = warehouse.getRegionList();
        try{
            List<Shelf> curRegionShelfList = retrieveShelfListInSpecificRegionInSpecificWarehouse(targetShelf.getRegionCode(), warehouseName, ownerCompanyName);
        }catch(ShelfNotExistException ex){
            regionList.remove(targetShelf.getRegionCode());
        }            
        
        warehouse.setRegionList(regionList);
        em.merge(targetShelf);
        em.merge(warehouse);
        
        System.out.println( "Shelf "+serialCode+"in Warehouse "+warehouseName+" of Company "+ownerCompanyName+" has been archived successfully.");
    }
    
    @Override
    public void deleteShelvesInOneRegion(String regionCode, String warehouseName, String ownerCompanyName) 
            throws WarehouseNotExistException, ShelfNotExistException, ShelfOccupiedException{
        List<Shelf> targetShelfList = retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, ownerCompanyName);        
        for(Object o: targetShelfList){
            Shelf curShelf = (Shelf) o;
            String serialCode = curShelf.getSerialCode();
            deleteShelf(serialCode, warehouseName, ownerCompanyName);
        }
        Warehouse warehouse = retrieveWarehouse(warehouseName, ownerCompanyName);
        List<String> regionList = warehouse.getRegionList();
        regionList.remove(regionCode);
        warehouse.setRegionList(regionList);
        em.merge(warehouse);
    }

    @Override
    public Boolean verifyServiceQuotation(ServiceQuotation sq, Double bufferRatio){
        Integer neededCapacity = sq.getRequiredWarehouseCapacity();
        PartnerCompany ownerCompany = (PartnerCompany)sq.getpCompany();
        List<Warehouse> ownedWarehouseList = ownerCompany.getOwnedWarehouseList();
        for(Warehouse curWare: ownedWarehouseList)
            if(neededCapacity<=curWare.getCapacity())
                return Boolean.TRUE;       
        return Boolean.FALSE;
    }
    
    private Integer getOutsourcingWarehouseCapacity(Timestamp contractStartTime, PartnerCompany ownerCompany){
        Integer result = 0;
        List<ServiceContract> outsourcingContractList = ownerCompany.getOutsourcingServiceContract();
        if(outsourcingContractList.isEmpty())
            return result;
        else{
            for(Object o: outsourcingContractList){
                ServiceContract curCon  = (ServiceContract) o;
                Timestamp endTime = curCon.getServiceQuotation().getEndDate();
                
                if(contractStartTime.before(endTime)){
                    Integer curCapacity = curCon.getServiceQuotation().getRequiredWarehouseCapacity();
                    result = result + curCapacity;
                }
                
            }
            return result;
        }       
    }
    
    
}
