/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.CustomerCompany;
import MRPII.entity.BillOfMaterial;
import MRPII.entity.RawMaterial;
import MRPII.session.MaterialRequirementPlanningSessionLocal;
import MRPII.session.RawMaterialManagementLocal;
import OES.entity.FinishedGood;
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
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.BillOfMaterialNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "bomManagedBean")
@SessionScoped
public class BOMManagedBean {

    @EJB
    MaterialRequirementPlanningSessionLocal mrp;
    @EJB
    RawMaterialManagementLocal rmm;
    @EJB
    ProductInfoManagementLocal pim;

    private CustomerCompany select_Company;
    private String ownerCompanyName;
    private String productName;
    private ArrayList<RawMaterial> materialList = new ArrayList();
    private ArrayList<RawMaterial> allMaterials = new ArrayList();
    private List<RawMaterial> selectedList = new ArrayList();
    private RawMaterial[] selectedMaterial;
    private String[] quantities;
    private HashMap<RawMaterial, Double> materialListMap = new HashMap();
    private ArrayList<Double> quantityList = new ArrayList();
    private BillOfMaterial selectedBOM;
    private RawMaterial material;
    private Double quantity;
    private ArrayList<FinishedGood> productList = new ArrayList();
    private FinishedGood selectedProduct;

    /**
     * Creates a new instance of BOMManagedBean
     */
    public BOMManagedBean() {
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void createBOM() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        int size = selectedList.size();
        for (int i = 0; i < size; i++) {
            RawMaterial m = selectedList.get(i);
            double q = materialListMap.get(m);
            quantityList.add(i, q);
        }
        try {
            mrp.createBillOfMaterial(ownerCompanyName, productName, selectedList, quantityList);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
        this.displayFaceMessage("Bill of Material has been successfully created!");
    }

    public void retrieveMaterialList() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        try {
            allMaterials = rmm.retrieveRawMaterialList(ownerCompanyName);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    public void preAddAction() {
        productName = selectedProduct.getProductName();
        selectedList.clear();
        for (Object o : selectedMaterial) {
            RawMaterial item = (RawMaterial) o;
            selectedList.add(item);
        }

        quantities = new String[selectedList.size()];

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("CreateBOM_2.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void addAction(RawMaterial m, int index) {
        Double q = Double.parseDouble(quantities[index]);
        materialListMap.put(m, q);
        if (materialListMap.isEmpty()) {
            System.out.println("materialListMap is empty");
        }
        this.displayFaceMessage("Product has been added successfully!");
    }

    public void preUpdateBOM() {
        productName = selectedBOM.getFinishedGood().getProductName();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("UpdateBOM.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void updateBOM() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        try {
            mrp.updateBillOfMaterial(ownerCompanyName, productName, material.getProductName(), quantity);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage( ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (BillOfMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("BillOfMaterialNotExistException: " + ex.getMessage());
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage( ex.getMessage());
            System.out.println("RawMaterialNotExistException: " + ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleRawMaterialWithSameNameException: " + ex.getMessage());
        }
        this.displayFaceMessage("This BOM has been updated successfully!");
    }

    public void changeQuantity(RawMaterial m) {
        material = m;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("ChangeQuantity.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage("IOException: " + ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void retrieveAllProducts() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        try {
            productList = pim.retrieveProductList(ownerCompanyName);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveBOM(String pName) {
        System.out.println("Product name is " + pName);
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            BillOfMaterial bom = mrp.retieveBillOfMaterial(ownerCompanyName, pName);
            selectedBOM = bom;
            ec.redirect("ViewBOM.xhtml");
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (BillOfMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("BillOfMaterialNotExistException: " + ex.getMessage());
        } catch (IOException ex) {
            this.displayFaceMessage("IOException: " + ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }
    

    public void deleteBOM() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompanyName = select_Company.getCompanyName();
        try {
            mrp.deleteBillOfMaterial(ownerCompanyName, selectedBOM.getFinishedGood().getProductName());
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (BillOfMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("BillOfMaterialNotExistExceptionn: " + ex.getMessage());
        }
        this.displayFaceMessage("This BOM has been deleted successfully!");
    }

    public Double getQuantity(RawMaterial m) {
        return selectedBOM.getMaterialQuantityMap().get(m);
    }

    public CustomerCompany getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ArrayList<RawMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(ArrayList<RawMaterial> materialList) {
        this.materialList = materialList;
    }

    public RawMaterial[] getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(RawMaterial[] selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public void setSelectedList(ArrayList<RawMaterial> selectedList) {
        this.selectedList = selectedList;
    }

    public String[] getQuantities() {
        return quantities;
    }

    public void setQuantities(String[] quantities) {
        this.quantities = quantities;
    }

    public HashMap<RawMaterial, Double> getMaterialListMap() {
        return materialListMap;
    }

    public void setMaterialListMap(HashMap<RawMaterial, Double> materialListMap) {
        this.materialListMap = materialListMap;
    }

    public ArrayList<Double> getQuantityList() {
        return quantityList;
    }

    public void setQuantityList(ArrayList<Double> quantityList) {
        this.quantityList = quantityList;
    }

    public List<RawMaterial> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<RawMaterial> selectedList) {
        this.selectedList = selectedList;
    }

    public BillOfMaterial getSelectedBOM() {
        return selectedBOM;
    }

    public void setSelectedBOM(BillOfMaterial selectedBOM) {
        this.selectedBOM = selectedBOM;
    }

    public RawMaterial getMaterial() {
        return material;
    }

    public void setMaterial(RawMaterial material) {
        this.material = material;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public ArrayList<FinishedGood> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<FinishedGood> productList) {
        this.productList = productList;
    }

    public ArrayList<RawMaterial> getAllMaterials() {
        return allMaterials;
    }

    public void setAllMaterials(ArrayList<RawMaterial> allMaterials) {
        this.allMaterials = allMaterials;
    }

    public FinishedGood getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(FinishedGood selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

}
