/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import MRPII.entity.RawMaterial;
import MRPII.entity.Supplier;
import MRPII.session.RawMaterialManagementLocal;
import MRPII.session.SupplierManagementLocal;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SupplierAlreadyExistException;
import util.exception.SupplierNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "supplierManagedBean")
@SessionScoped
public class SupplierManagedBean {

    @EJB
    SupplierManagementLocal sml;
    @EJB
    RawMaterialManagementLocal rmm;
    @EJB
    ProductInfoManagementLocal pim;

    private CustomerCompany select_Company;
    private String ownerCompanyName;
    private Company company;
    private List<Product> materialList = new ArrayList();
    private List<Product> allMaterials = new ArrayList();
    private List<Product> selectedList = new ArrayList();
    private Product[] selectedMaterial;
    private Product rawMaterial;
    private String email;
    private List<String> materialStrList = new ArrayList();
    private List<String> updateList = new ArrayList();
    private String materialStr, supplierName, leadtime;
    private String[] leadTimeStr;
    private HashMap<Product, Integer> leadTimeMap = new HashMap();
    private Supplier supplier, selectedSupplier;
    private List<Supplier> supplierList = new ArrayList();

    /**
     * Creates a new instance of SupplierManagedBean
     */
    public SupplierManagedBean() {
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void retrieveRawMaterialList() {
        allMaterials.clear();
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        try {
            allMaterials.addAll(pim.retrieveAllProduct(ownerCompanyName));
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        }
    }

    public void addAction() {
        for (Object o : selectedMaterial) {
            Product m = (Product) o;
            if (!materialList.contains(m)) {
                materialList.add(m);
            }
        }
        this.displayFaceMessage("Selected materials have been added successfully!");
    }

    public void deleteAction(String mName) {
        materialStrList.remove(mName);
        this.displayFaceMessage("Selected product has been deleted successfully!");
    }

    public void addStrAction() {
        materialStrList.add(materialStr);
    }

    public void toNextPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        for (Object o : materialList) {
            Product m = (Product) o;
            if (materialList.indexOf(m) != materialList.lastIndexOf(m)) {
                materialList.remove(m);
            }
        }
        leadTimeStr = new String[materialList.size()];
        try {
            ec.redirect("CreateSupplier_2.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void preUpdateSupplier(Supplier s) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        supplier = s;
        try {
            ec.redirect("UpdateSupplier.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void addLeadTime(Product product, Integer index) {
        Integer q = Integer.parseInt(leadTimeStr[index]);
        leadTimeMap.put(product, q);
        System.out.println("leadTimeMap being put");
        if (leadTimeMap.isEmpty()) {
            System.out.println("leadTimeMap is empty");
        }
        this.displayFaceMessage("Lead Time has been added successfully!");
    }

    public void deleteMaterial(Product m) {
        try {
            sml.deleteMaterial(supplier, m);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("RawMaterialNotExistException: " + ex.getMessage());
        }
        this.displayFaceMessage("Material " + m.getProductName() + " has been deleted successfully!");
    }

    public void preAddMaterial() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        selectedList = allMaterials;
        for (Object o : supplier.getSupplyList()) {
            Product p = (Product) o;
            if (selectedList.contains(p)) {
                selectedList.remove(p);
            }
        }
        try {
            ec.redirect("AddMaterial.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void addMaterial(Product m) {
        Integer lTime = Integer.parseInt(leadtime);
        try {
            sml.addMaterial(supplier, m, lTime);
        } catch (RawMaterialAlreadyExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("RawMaterialAlreadyExistException: " + ex.getMessage());
        }
        this.displayFaceMessage("Material has been added successfully!");
    }

    public void createSupplier() {
        try {
            sml.addSupplier(supplierName, email, select_Company, materialList, leadTimeMap, materialStrList);
        } catch (SupplierAlreadyExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierAlreadyExistException: " + ex.getMessage());
        }
        this.displayFaceMessage("Supplier has been created successfully!");

    }

    public void updateMaterialStr() {
        updateList = supplier.getAllSupplyList();
    }

    public void deleteFromStrList(String m) {
        updateList.remove(m);
        this.displayFaceMessage("Delete successfully");
    }

    public void addFromStrList() {
        updateList.add(materialStr);
        this.displayFaceMessage("Add successfully");
    }

    public void updateAllStrList() {
        sml.updateStrList(supplier, updateList);
        try {
            sml.updateSupplierCommon(supplierName, select_Company);
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        }
        this.displayFaceMessage("Supplier has been updated successfully!");
    }

    public void retrieveAllSupplier() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        try {
            supplierList = sml.retrieveSupplierListForCompany(select_Company);
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveSingleSupplier(Supplier s) {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        supplier = s;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("ViewSingleSupplier.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void deleteSupplier() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        try {
            sml.removeSupplier(selectedSupplier.getName(), select_Company);
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        }
        this.displayFaceMessage("The supplier has been deleted successfully!");
    }

    public Integer getLeadTime(Product p) {
        return supplier.getLagTimeMap().get(p);
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
    }

    public List<String> getUpdateList() {
        return updateList;
    }

    public void setUpdateList(List<String> updateList) {
        this.updateList = updateList;
    }

    public void setMaterialStrList(ArrayList<String> materialStrList) {
        this.materialStrList = materialStrList;
    }

    public String getMaterialStr() {
        return materialStr;
    }

    public void setMaterialStr(String materialStr) {
        this.materialStr = materialStr;
    }

    public List<Product> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Product> materialList) {
        this.materialList = materialList;
    }

    public Product getRawMaterial() {
        return rawMaterial;
    }

    public String getLeadtime() {
        return leadtime;
    }

    public void setLeadtime(String leadtime) {
        this.leadtime = leadtime;
    }

    public void setRawMaterial(Product rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public List<String> getMaterialStrList() {
        return materialStrList;
    }

    public void setMaterialStrList(List<String> materialStrList) {
        this.materialStrList = materialStrList;
    }

    public Product[] getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(Product[] selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public String[] getLeadTimeStr() {
        return leadTimeStr;
    }

    public void setLeadTimeStr(String[] leadTimeStr) {
        this.leadTimeStr = leadTimeStr;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public HashMap<Product, Integer> getLeadTimeMap() {
        return leadTimeMap;
    }

    public void setLeadTimeMap(HashMap<Product, Integer> leadTimeMap) {
        this.leadTimeMap = leadTimeMap;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public List<Product> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<Product> selectedList) {
        this.selectedList = selectedList;
    }

    public List<Product> getAllMaterials() {
        return allMaterials;
    }

    public void setAllMaterials(List<Product> allMaterials) {
        this.allMaterials = allMaterials;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
