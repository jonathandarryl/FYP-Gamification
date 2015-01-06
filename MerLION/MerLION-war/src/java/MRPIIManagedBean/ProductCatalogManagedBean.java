/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPIIManagedBean;

import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "productCatalogManagedBean")
@SessionScoped
public class ProductCatalogManagedBean {
    @EJB
    ProductInfoManagementLocal pim;
    
    private ArrayList<Product> allProducts=new ArrayList();
    private Product product;

    /**
     * Creates a new instance of ProductCatalogManagedBean
     */
    public ProductCatalogManagedBean() {
    }
    
     private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
     public void retrieveAllProduct(){
        try {
            allProducts=pim.retrieveAllCompanyProduct();
        } catch (ProductNotExistException ex) {
           this.displayFaceMessage(ex.getMessage());
        }
     }
     
     public void retrieveProduct(String ownerCompanyName, String productName){
        try {
            product=pim.retrieveGeneralProduct(ownerCompanyName, productName);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
        }
     }

    public ArrayList<Product> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(ArrayList<Product> allProducts) {
        this.allProducts = allProducts;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
     
     
}
