/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMSManagedBean;

import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Shelf;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ShelfAlreadyExistException;
import util.exception.ShelfNotExistException;
import util.exception.ShelfOccupiedException;
import util.exception.WarehouseAlreadyExistException;
import util.exception.WarehouseNotEmptyException;
import util.exception.WarehouseNotExistException;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author songhan
 */
@Named(value = "facilityManagedBean")
@SessionScoped
public class FacilityManagedBean implements Serializable {

    /**
     * Creates a new instance of FacilityManagedBean
     */
    public FacilityManagedBean() {
    }

    @EJB
    private ProductInfoManagementLocal piml;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;

    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;
    private String newOwnerCompanyName;
    private String warehouseName;
    private String contactNo;
    private Location warehouseLoc;
    private String country;
    private String state;
    private String city;
    private String street;
    private String blockNo;
    private String postalCode;

    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;

    private Double pricePerCapacityPerDay;

    private String regionCode;
    private Integer shelvesNum;
    private Integer shelfCapacity;

    private List<Warehouse> warehouseList;
    private List<Warehouse> outsourcingWarehouseList;
    private Warehouse wh;

    private List<Shelf> shelfList;
    private Shelf shelf;

    private List<String> regionList;

    private List<String> addressList;

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        warehouseList = new ArrayList<>();
        outsourcingWarehouseList = new ArrayList<>();
        shelfList = new ArrayList<>();
        shelf = new Shelf();
        wh = new Warehouse();
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            ownerCompany = cmsbl.retrieveCompany(cId);
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }

    public void refreshUserInfo() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        try {
            ownerCompany = cmsbl.retrieveCompany(ownerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }

    public void createWarehouse() {
        System.out.println("Enter FacilityManagedBean: createWarehouse");
        warehouseLoc = new Location(country, state, city, street, blockNo, postalCode);
        try {
            fml.createWarehouse(warehouseName, contactNo, ownerCompanyName, warehouseLoc, perishable, flammable, pharmaceutical, highValue, pricePerCapacityPerDay);
            statusMessage = "Warehouse creation successful";
            this.displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("Warehouse creation complete");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (WarehouseAlreadyExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseAlreadyExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: createWarehouse");
    }

    public void discardAllCurrentShelf() {
        System.out.println("Enter FacilityManagedBean: discardAllCurrentShelf");
        try {
            fml.disgardShelfSet(warehouseName, ownerCompanyName);
            System.out.println("Discard all current shelves in warehouse " + warehouseName + " complete");
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (WarehouseNotEmptyException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotEmptyException: " + statusMessage);
        }
        System.out.println("Enter FacilityManagedBean: discardAllCurrentShelf");
    }

    public void updateWarehouseCommon() {
        System.out.println("Enter FacilityManagedBean: updateWarehouseCommon");
        try {
            warehouseLoc = new Location(country, state, city, street, blockNo, postalCode);
            fml.updateWarehouseCommom(warehouseName, ownerCompanyName, warehouseName, contactNo, warehouseLoc, perishable, flammable, pharmaceutical, highValue);
            statusMessage = "Warehouse info update successful";
            this.displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("Discard all current shelves in warehouse " + warehouseName + " complete");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: updateWarehouseCommon");
    }

    public void updateWarehouseOwner() {
        System.out.println("Enter FacilityManagedBean: updateWarehouseOwner");
        try {
            fml.updateWarehouseOwner(warehouseName, ownerCompanyName, newOwnerCompanyName);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: updateWarehouseOwner");
    }

    public String getWarehouseAddressString() {
        Location curLoc = wh.getLocation();
        String curAddress = curLoc.getBlockNo() + ", " + curLoc.getStreet() + ", " + curLoc.getCity() + ", " + curLoc.getState() + ", " + curLoc.getCountry() + ", " + curLoc.getPostalCode();
        return curAddress;
    }

    public void getAddressList(List<Warehouse> whList) {
        addressList = new ArrayList<>();
        for (Warehouse wh : whList) {
            Location curLoc = wh.getLocation();
            String curAddress = curLoc.getBlockNo() + ", " + curLoc.getStreet() + ", " + curLoc.getCity() + ", " + curLoc.getState() + ", " + curLoc.getCountry() + ", " + curLoc.getPostalCode();
            addressList.add(curAddress);
            System.out.println(curAddress);
        }
    }

    public void retrieveWarehouseList() throws IOException {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        
        addressList = null;
        System.out.println("Enter FacilityManagedBean: retrieveWarehouseList");
        warehouseList = fml.retrieveOwnedWarehouseList(ownerCompany);
        getAddressList(warehouseList);
        if (warehouseList.isEmpty()) {
            statusMessage = "We currently do not own any warehouse!";
            System.out.println(statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveWarehouseList");
    }

    public void retrieveOutsourcingWarehouseList() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        addressList = null;
        System.out.println("Enter FacilityManagedBean: retrieveOutsourcingWarehouseList");
        try {
            addressList = new ArrayList<>();
            outsourcingWarehouseList = fml.retrieveOutsourcingWarehouseList(ownerCompanyName);
            getAddressList(outsourcingWarehouseList);
            statusMessage = "Outsourcing warehouse list retrieved successfully!";
            System.out.println(statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        }
        if (outsourcingWarehouseList.isEmpty()) {
            statusMessage = "We currently are not renting any warehouse from other companys!";
            System.out.println(statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveOutsourcingWarehouseList");
    }

    public void retrieveWarehouse(Warehouse warehouse)
            throws IOException {
        System.out.println("Enter FacilityManagedBean: retrieveWarehouse");
        wh = warehouse;
        warehouseName = wh.getName();
        contactNo = wh.getContactNo();
        warehouseLoc = wh.getLocation();
        country = warehouseLoc.getCountry();
        state = warehouseLoc.getState();
        city = warehouseLoc.getCity();
        street = warehouseLoc.getStreet();
        blockNo = warehouseLoc.getBlockNo();
        postalCode = warehouseLoc.getPostalCode();
        regionList = wh.getRegionList();
        perishable = wh.isPerishable();
        flammable = wh.isFlammable();
        pharmaceutical = wh.isPharmaceutical();
        highValue = wh.isHighValue();
        pricePerCapacityPerDay = wh.getPricePerCapacityPerDay();
        redirectToPage("./warehouseDetail.xhtml");
        System.out.println("Enter FacilityManagedBean: retrieveWarehouse");
    }

    public void retrieveOutsourcingWarehouse(Warehouse warehouse)
            throws IOException {
        System.out.println("Enter FacilityManagedBean: retrieveWarehouse");
        wh = warehouse;
        warehouseName = wh.getName();
        contactNo = wh.getContactNo();
        warehouseLoc = wh.getLocation();
        country = warehouseLoc.getCountry();
        state = warehouseLoc.getState();
        city = warehouseLoc.getCity();
        street = warehouseLoc.getStreet();
        blockNo = warehouseLoc.getBlockNo();
        postalCode = warehouseLoc.getPostalCode();
        regionList = wh.getRegionList();
        perishable = wh.isPerishable();
        flammable = wh.isFlammable();
        pharmaceutical = wh.isPharmaceutical();
        highValue = wh.isHighValue();
        pricePerCapacityPerDay = wh.getPricePerCapacityPerDay();
        redirectToPage("./outsourcingWarehouseDetail.xhtml");
        System.out.println("Enter FacilityManagedBean: retrieveWarehouse");
    }

    public Integer getWarehouseShelfNumber() {
        Integer result = 0;
        try {
            result = fml.retrieveShelfListInSpecificWarehouse(warehouseName, ownerCompanyName).size();
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return result;
    }

    public void refreshWarehouseDetail() {
        System.out.println("Enter FacilityManagedBean: refreshWarehouseDetail");
        Long whId = wh.getId();
        try {
            wh = fml.retrieveWarehouse(whId);
            warehouseName = wh.getName();
            contactNo = wh.getContactNo();
            warehouseLoc = wh.getLocation();
            country = warehouseLoc.getCountry();
            state = warehouseLoc.getState();
            city = warehouseLoc.getCity();
            street = warehouseLoc.getStreet();
            blockNo = warehouseLoc.getBlockNo();
            postalCode = warehouseLoc.getPostalCode();
            regionList = wh.getRegionList();
            perishable = wh.isPerishable();
            flammable = wh.isFlammable();
            pharmaceutical = wh.isPharmaceutical();
            highValue = wh.isHighValue();
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }

        System.out.println("End of FacilityManagedBean: refreshWarehouseDetail");
    }

    public List<FinishedGood> retrieveFinshedGoodList() {
        List<FinishedGood> productList = wh.getFinishedGoodList();
        if (ownerCompany.getCompanyType().equals("1PL")) {
            for (Object o : productList) {
                FinishedGood curProduct = (FinishedGood) o;
                if (!curProduct.getOwnerCompany().getCompanyName().equals(ownerCompanyName)) {
                    productList.remove(curProduct);
                }
            }
        }
        return productList;
    }

    public List<RawMaterial> retrieveRawMaterialList() {
        List<RawMaterial> productList = wh.getRawMaterialList();
        if (ownerCompany.getCompanyType().equals("1PL")) {
            for (Object o : productList) {
                RawMaterial curProduct = (RawMaterial) o;
                if (!curProduct.getOwnerCompany().getCompanyName().equals(ownerCompanyName)) {
                    productList.remove(curProduct);
                }
            }
        }
        return productList;
    }

    public Double getFinishedGoodQuantity(FinishedGood finishedGood) {
        return wh.getFinishedGoodMap().get(finishedGood);
    }

    public Double getRawMaterialQuantity(RawMaterial rawMaterial) {
        return wh.getRawMaterialMap().get(rawMaterial);
    }

    public void retrieveShelfListForProductInWarehouse(FinishedGood finishedGood) {
        System.out.println("Enter FacilityManagedBean: retrieveShelfListForProductInWarehouse");
        String productName = finishedGood.getProductName();
        String productOwnerName = finishedGood.getOwnerCompany().getCompanyName();
        try {
            shelfList = fml.retrieveShelfListForSpecificProductInSpecificWarehouse(warehouseName, ownerCompanyName, productName, productOwnerName);
            statusMessage = "Shelf List Retrieved Successfully!";
            System.out.println(statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveShelfListForProductInWarehouse");
    }

    public void retrieveShelfListForRawMaterialInWarehouse(RawMaterial rawMaterial) {
        System.out.println("Enter FacilityManagedBean: retrieveShelfListForRawMaterialInWarehouse");
        String productName = rawMaterial.getProductName();
        String productOwnerName = rawMaterial.getOwnerCompany().getCompanyName();
        try {
            shelfList = fml.retrieveShelfListForSpecificProductInSpecificWarehouse(warehouseName, ownerCompanyName, productName, productOwnerName);
            statusMessage = "Shelf List Retrieved Successfully!";
            System.out.println(statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveShelfListForRawMaterialInWarehouse");
    }

    public void deleteWarehouse() {
        System.out.println("Enter FacilityManagedBean: retrieveOutsourcingWarehouseList");
        warehouseName = wh.getName();
        try {
            fml.deleteWarehouse(warehouseName, ownerCompanyName);
            warehouseList.remove(wh);
            System.out.println("Deletion completes");
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        } catch (WarehouseNotExistException ex) {
            statusMessage =ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println( "WarehouseNotExistException: " + statusMessage);
        } catch (WarehouseNotEmptyException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotEmptyException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveOutsourcingWarehouseList");
    }

    public Integer getRegionShelfNumber(String regionCode) {
        Integer result = 0;
        try {
            result = fml.getRegionShelfNumber(warehouseName, wh.getOwnerCompany().getCompanyName(), regionCode);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return result;
    }

    public Integer getRegionCapacity(String regionCode) {
        Integer result = 0;
        try {
            result = fml.getRegionCapacity(warehouseName, wh.getOwnerCompany().getCompanyName(), regionCode);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return result;
    }

    public Integer getRegionSpareCapacity(String regionCode) {
        Integer result = 0;
        try {
            result = fml.getRegionSpareCapacity(warehouseName, wh.getOwnerCompany().getCompanyName(), regionCode);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        return result;
    }

    public void retrieveShelfListForSpecificRegion() throws IOException {
        System.out.println("Enter FacilityManagedBean: retrieveShelfListForSpecificRegion for region " + regionCode);
        String warehouseOwnerName = wh.getOwnerCompany().getCompanyName();
        try {
            shelfList = fml.retrieveShelfListInSpecificRegionInSpecificWarehouse(regionCode, warehouseName, warehouseOwnerName);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ShelfNotExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: retrieveShelfListForSpecificRegion for region " + regionCode);
    }

    public String shelfProductName(Shelf shelf) {
        if (shelf.getAvailability()) {
            return "--";
        } else {
            return shelf.getInventory().getProduct().getProductName();
        }
    }

    public String shelfProductUnit(Shelf shelf) {
        if (shelf.getAvailability()) {
            return "";
        } else {
            return shelf.getInventory().getProduct().getUnit();
        }
    }

    public void createShelfListForWarehouse() {
        System.out.println("Enter FacilityManagedBean: createShelfListForWarehouse");
        try {
            fml.createShelfListForSpecificWarehouse(regionCode, shelvesNum, shelfCapacity, warehouseName, ownerCompanyName);
            statusMessage = "New shelves added successfully! Region: " + regionCode + " Number: " + shelvesNum + " Capacity: " + shelfCapacity;
            this.displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("Shelves creation for Warehouse " + warehouseName + " complete");
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfAlreadyExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfAlreadyExistException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: createShelfListForWarehouse");
    }

    public void deleteRegion(String regionCode) {
        System.out.println("Enter FacilityManagedBean: deleteRegion");
        try {
            fml.deleteShelvesInOneRegion(regionCode, warehouseName, ownerCompanyName);
            regionList.remove(regionCode);
            statusMessage = "Region: " + regionCode + " has been deleted successfully ";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ShelfNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfNotExistException: " + statusMessage);
        } catch (ShelfOccupiedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ShelfOccupiedException: " + statusMessage);
        }
        System.out.println("End of FacilityManagedBean: deleteRegion");
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void addPerishableMessage() {
        String summary = perishable ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addFlammableMessage() {
        String summary = flammable ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addPharmaceuticalMessage() {
        String summary = pharmaceutical ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addHighValueMessage() {
        String summary = highValue ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
    }

    public void redirectToViewOwnedWarehouseList() throws IOException {
        redirectToPage("viewOwnedWarehouseList.xhtml");
    }

    public void redirectToViewOutsourcingWarehouseList() throws IOException {
        redirectToPage("viewOutsourcingWarehouseList.xhtml");
    }

    public void redirectToUpdateOwnedWarehouseInfo() throws IOException {
        redirectToPage("updateOwnedWarehouseInfo.xhtml");
    }

    public void redirectToViewWarehouseLayout() throws IOException {
        redirectToPage("viewWarehouseLayout.xhtml");
    }

    public void redirectToViewWarehouseFinishedGoodList() throws IOException {
        redirectToPage("viewWarehouseFinishedGoodList.xhtml");
    }

    public void redirectToViewWarehouseRawMaterialList() throws IOException {
        redirectToPage("viewWarehouseRawMaterialList.xhtml");
    }

    public void redirectToViewRegionShelfList(String regionCode) throws IOException {
        this.regionCode = regionCode;
        redirectToPage("viewRegionShelfList.xhtml");
    }

    public void redirectToViewRegionShelfList() throws IOException {
        redirectToPage("viewRegionShelfList.xhtml");
    }

    public void redirectToCreateShelf(String regionCode) throws IOException {
        this.regionCode = regionCode;
        redirectToPage("createShelves.xhtml");
    }

    public void redirectToCreateShelfInNewRegion() throws IOException {
        redirectToPage("createShelvesInNewRegion.xhtml");
    }

    public void redirectToWarehouseDetail() throws IOException {
        redirectToPage("warehouseDetail.xhtml");
    }

    public PieChartModel getWarehouseCapacityChart(Warehouse wh) {
        PieChartModel pcm = new PieChartModel();
        pcm.set("Spare Capacity", wh.getSpareCapacity());
        pcm.set("Used Capacity", wh.getUtilizedCapacity());
        pcm.setTitle("Space Usage");
        pcm.setShowDataLabels(true);
        pcm.setDiameter(200);
        pcm.setLegendPosition("e");

        return pcm;
    }
    
    public PieChartModel getRegionCapacityChart(String regionCode) {
        PieChartModel pcm = new PieChartModel();
        pcm.set("Spare Capacity", getRegionSpareCapacity(regionCode));
        pcm.set("Used Capacity", getRegionCapacity(regionCode)-getRegionSpareCapacity(regionCode));
        pcm.setTitle(regionCode);
        pcm.setShowDataLabels(true);
        pcm.setDiameter(150);
        pcm.setLegendPosition("e");

        return pcm;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Company getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Company ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public String getNewOwnerCompanyName() {
        return newOwnerCompanyName;
    }

    public void setNewOwnerCompanyName(String newOwnerCompanyName) {
        this.newOwnerCompanyName = newOwnerCompanyName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Location getWarehouseLoc() {
        return warehouseLoc;
    }

    public void setWarehouseLoc(Location warehouseLoc) {
        this.warehouseLoc = warehouseLoc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getPerishable() {
        return perishable;
    }

    public void setPerishable(Boolean perishable) {
        this.perishable = perishable;
    }

    public Boolean getFlammable() {
        return flammable;
    }

    public void setFlammable(Boolean flammable) {
        this.flammable = flammable;
    }

    public Boolean getPharmaceutical() {
        return pharmaceutical;
    }

    public void setPharmaceutical(Boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    public Boolean getHighValue() {
        return highValue;
    }

    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

    public Double getPricePerCapacityPerDay() {
        return pricePerCapacityPerDay;
    }

    public void setPricePerCapacityPerDay(Double pricePerCapacityPerDay) {
        this.pricePerCapacityPerDay = pricePerCapacityPerDay;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Integer getShelvesNum() {
        return shelvesNum;
    }

    public void setShelvesNum(Integer shelvesNum) {
        this.shelvesNum = shelvesNum;
    }

    public Integer getShelfCapacity() {
        return shelfCapacity;
    }

    public void setShelfCapacity(Integer shelfCapacity) {
        this.shelfCapacity = shelfCapacity;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public List<Warehouse> getOutsourcingWarehouseList() {
        return outsourcingWarehouseList;
    }

    public void setOutsourcingWarehouseList(List<Warehouse> outsourcingWarehouseList) {
        this.outsourcingWarehouseList = outsourcingWarehouseList;
    }

    public Warehouse getWh() {
        return wh;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }

    public List<Shelf> getShelfList() {
        return shelfList;
    }

    public void setShelfList(List<Shelf> shelfList) {
        this.shelfList = shelfList;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public List<String> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<String> regionList) {
        this.regionList = regionList;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

}
