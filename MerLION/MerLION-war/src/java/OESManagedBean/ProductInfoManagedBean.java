/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OESManagedBean;

import Common.entity.Company;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductAlreadyExistException;
import util.exception.ProductInventoryNotEmptyException;
import util.exception.ProductNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "productInfoManagedBean")
@SessionScoped
public class ProductInfoManagedBean implements Serializable {

    @EJB
    private ProductInfoManagementLocal prm;

    private static Double VOLUME_CAPACITY = 8.0;
    private static Double WEIGHT_CAPACITY = 1000.0;

    private String productName;
    private String productPrice;
    private String ownerCompanyName, company_RL;
    private String unit;
    private String quantityInOneBatch;
    private String productDescription;
    private Company company;
    private Double weight, volume;
    private Double capacityQuantity;

    private FinishedGood selectProduct = new FinishedGood();
    private ArrayList<FinishedGood> fgList = new ArrayList();

    public ProductInfoManagedBean() {
    }

    public FinishedGood getSelectProduct() {
        return selectProduct;
    }

    public String getCompany_RL() {
        return company_RL;
    }

    public static Double getVOLUME_CAPACITY() {
        return VOLUME_CAPACITY;
    }

    public static void setVOLUME_CAPACITY(Double VOLUME_CAPACITY) {
        ProductInfoManagedBean.VOLUME_CAPACITY = VOLUME_CAPACITY;
    }

    public static Double getWEIGHT_CAPACITY() {
        return WEIGHT_CAPACITY;
    }

    public static void setWEIGHT_CAPACITY(Double WEIGHT_CAPACITY) {
        ProductInfoManagedBean.WEIGHT_CAPACITY = WEIGHT_CAPACITY;
    }

    public void setCompany_RL(String company_RL) {
        this.company_RL = company_RL;
    }

    public void setSelectProduct(FinishedGood selectProduct) {
        this.selectProduct = selectProduct;
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

    public String getProductName() {
        return productName;
    }

    public ArrayList<FinishedGood> getFgList() {
        return fgList;
    }

    public void setFgList(ArrayList<FinishedGood> fgList) {
        this.fgList = fgList;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Double getCapacityQuantity() {
        return capacityQuantity;
    }

    public void setCapacityQuantity(Double CapacityQuantity) {
        this.capacityQuantity = CapacityQuantity;
    }

    public String getQuantityInOneBatch() {
        return quantityInOneBatch;
    }

    public void setQuantityInOneBatch(String quantityInOneBatch) {
        this.quantityInOneBatch = quantityInOneBatch;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void createProduct(ActionEvent event) throws ProductAlreadyExistException, MultipleProductWithSameNameException, CompanyNotExistException {
        Double price = Double.parseDouble(productPrice);
        Double bUnit = Double.parseDouble(quantityInOneBatch);

        Double vUnit = VOLUME_CAPACITY / volume;
        Double wUnit = WEIGHT_CAPACITY / weight;
        if (vUnit > wUnit) {
            capacityQuantity = wUnit;
        } else {
            capacityQuantity = vUnit;
        }

        try {
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompanyName = company.getCompanyName();
            prm.createProduct(ownerCompanyName, productName, price, productDescription, unit, capacityQuantity, bUnit);
            //statusMessage = "New Product Saved Successfully";
            this.displayFaceMessage("Product " + productName + " has been successfully added");
        } catch (ProductAlreadyExistException e) {
            //String message = "ProductAlreadyExist " + e.getMessage();
            this.displayFaceMessage("Product already exist");
        } catch (MultipleProductWithSameNameException e) {
            // String message = "MultipleProductWithSameName " + e.getMessage();
            this.displayFaceMessage("There are multiple products with the same name");
            // return "Error.xhtml";
        } catch (CompanyNotExistException e) {
            //  String message = "MultipleProductWithSameName " + e.getMessage();
            this.displayFaceMessage("Company does not exist");
        }
    }

    public void preUpdateProduct(FinishedGood product) throws IOException {
        setProductName(product.getProductName());
        setProductPrice(product.getProductPrice().toString());
        setProductDescription(product.getProductDescription());
        setOwnerCompanyName(product.getOwnerCompanyName());
        setCapacityQuantity(product.getQuantityInOneUnitCapacity());
        setQuantityInOneBatch(product.getQuantityInOneBatch().toString());
        setUnit(product.getUnit());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("UpdateProduct.xhtml");
    }

    public void updateProduct(ActionEvent event) throws ProductNotExistException, MultipleProductWithSameNameException {
        Double price = Double.parseDouble(productPrice);
        Double bUnit = Double.parseDouble(quantityInOneBatch);
        try {
            prm.updateProduct(ownerCompanyName, productName, price, productDescription, unit, capacityQuantity, bUnit);
            this.displayFaceMessage("Product " + productName + " has been successfully updated");
            //addMessage("Success", "Product updated");
        } catch (ProductNotExistException e) {
            // String message = "ProductAlreadyExist " + e.getMessage();
            this.displayFaceMessage("Product does not exist");
        } catch (MultipleProductWithSameNameException e) {
            //  String message = "MultipleProductWithSameName " + e.getMessage();
            this.displayFaceMessage("There are multiple products with the same name");
        }
    }

    public void deleteProduct() throws ProductNotExistException, MultipleProductWithSameNameException, ProductInventoryNotEmptyException {
        try {
            productName = selectProduct.getProductName();
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompanyName = company.getCompanyName();
            prm.deleteProduct(ownerCompanyName, productName);
            this.displayFaceMessage("Product " + productName + " has been successfully deleted");
        } catch (ProductNotExistException e) {
            // String message = "ProductAlreadyExist " + e.getMessage();
            this.displayFaceMessage("Product does not exist");
        } catch (MultipleProductWithSameNameException e) {
            // String message = "MultipleProductWithSameName " + e.getMessage();
            this.displayFaceMessage("There are multiple products with the same name");
        } catch (ProductInventoryNotEmptyException ex) {
            this.displayFaceMessage(ex.getMessage());
        }
    }

    public FinishedGood retrieveProduct(String productName) throws ProductNotExistException, MultipleProductWithSameNameException, IOException {
        try {
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompanyName = company.getCompanyName();
            selectProduct = prm.retrieveProduct(ownerCompanyName, productName);
   //      FacesContext fc = FacesContext.getCurrentInstance();
            //       ExternalContext ec = fc.getExternalContext();
            //        ec.redirect("DisplayProduct.xhtml");
            //         RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        } catch (ProductNotExistException e) {
            //String message = "ProductAlreadyExist " + e.getMessage();
            this.displayFaceMessage("This product does not exist");
        } catch (MultipleProductWithSameNameException e) {
            //  String message = "MultipleProductWithSameName " + e.getMessage();
            this.displayFaceMessage("There are multiple products with the same name");
        }
        return selectProduct;
    }

    public void retrieveProductList() throws ProductNotExistException, IOException {
        try {
            fgList.clear();
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            company_RL = company.getCompanyName();
            fgList.addAll(prm.retrieveProductList(company_RL));
        } catch (ProductNotExistException e) {
            this.displayFaceMessage("Product does not exist");
        }
    }

    public void makeProductPublic(String pName) {
        try {
            company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompanyName = company.getCompanyName();
            prm.makeProductPublic(pName, ownerCompanyName);
            this.displayFaceMessage("Product has been made public!");
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage("ProductNotExistException: "+ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage("MultipleProductWithSameNameException: "+ex.getMessage());
        }
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
