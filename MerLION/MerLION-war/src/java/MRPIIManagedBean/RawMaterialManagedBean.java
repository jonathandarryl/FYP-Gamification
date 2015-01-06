/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import MRPII.entity.RawMaterial;
import MRPII.session.RawMaterialManagementLocal;
import java.io.IOException;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.CompanyNotExistException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "rawMaterialManagedBean")
@SessionScoped
public class RawMaterialManagedBean {

    @EJB
    RawMaterialManagementLocal rmm;

    private static Double VOLUME_CAPACITY = 8.0;
    private static Double WEIGHT_CAPACITY = 1000.0;

    private Company select_Company;
    private String ownerCompanyName;
    private Company company;
    private Double weight, volume;
    private Double batchQuantity, capacityQuantity, materialPrice;
    private String materialName, materialDescription, unit;
    private RawMaterial rawMaterial;
    private ArrayList<RawMaterial> materialList=new ArrayList();

    /**
     * Creates a new instance of RawMaterialManagedBean
     */
    public RawMaterialManagedBean() {
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void createRawMaterial() {
        Double vUnit = VOLUME_CAPACITY / volume;
        Double wUnit = WEIGHT_CAPACITY / weight;
        if (vUnit > wUnit) {
            capacityQuantity = wUnit;
        } else {
            capacityQuantity = vUnit;
        }

        company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = company.getCompanyName();

        try {
            rmm.createRawMaterial(ownerCompanyName, materialName, materialPrice, materialDescription, unit, capacityQuantity, batchQuantity);
        } catch (RawMaterialAlreadyExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (CompanyNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }

        this.displayFaceMessage("Raw Material " + materialName + " has been successfully added");
    }

    public void retrieveMaterialList() {
        materialList.clear();
        company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = company.getCompanyName();
        try {
            materialList.addAll(rmm.retrieveRawMaterialList(ownerCompanyName));
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    public void deleteMaterial() {
        materialName = rawMaterial.getProductName();
        company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = company.getCompanyName();
        try {
            rmm.deleteRawMaterial(ownerCompanyName, materialName);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
        this.displayFaceMessage("Raw Material " + materialName + " has been successfully deleted");
    }

    public void preUpdateMaterial(RawMaterial material) throws IOException {
        materialName=material.getProductName();
        materialPrice=material.getProductPrice();
        materialDescription=material.getProductDescription();
        ownerCompanyName=material.getOwnerCompanyName();
        capacityQuantity=material.getQuantityInOneUnitCapacity();
        batchQuantity=material.getQuantityInOneBatch();
        unit=material.getUnit();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("UpdateRawMaterial.xhtml");
    }

    public void updateMaterial() {
        try {
            rmm.updateRawMaterial(ownerCompanyName, materialName, materialPrice, materialDescription, unit, capacityQuantity, batchQuantity);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
        this.displayFaceMessage("Raw Material " + materialName + " has been successfully updated");
    }
    
    public void retrieveMaterial(String mName){
        company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = company.getCompanyName();
        try {
            rawMaterial=rmm.retrieveRawMaterial(ownerCompanyName, mName);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(Company select_Company) {
        this.select_Company = select_Company;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public static Double getVOLUME_CAPACITY() {
        return VOLUME_CAPACITY;
    }

    public static void setVOLUME_CAPACITY(Double VOLUME_CAPACITY) {
        RawMaterialManagedBean.VOLUME_CAPACITY = VOLUME_CAPACITY;
    }

    public static Double getWEIGHT_CAPACITY() {
        return WEIGHT_CAPACITY;
    }

    public static void setWEIGHT_CAPACITY(Double WEIGHT_CAPACITY) {
        RawMaterialManagedBean.WEIGHT_CAPACITY = WEIGHT_CAPACITY;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getBatchQuantity() {
        return batchQuantity;
    }

    public void setBatchQuantity(Double batchQuantity) {
        this.batchQuantity = batchQuantity;
    }

    public Double getCapacityQuantity() {
        return capacityQuantity;
    }

    public void setCapacityQuantity(Double capacityQuantity) {
        this.capacityQuantity = capacityQuantity;
    }

    public Double getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(Double materialPrice) {
        this.materialPrice = materialPrice;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<RawMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(ArrayList<RawMaterial> materialList) {
        this.materialList = materialList;
    }

}
