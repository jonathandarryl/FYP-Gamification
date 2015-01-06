/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OESManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Customer;
import OES.session.CustomerSessionBeanLocal;
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
import util.exception.CustomerAlreadyExistException;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;

/**
 *
 * @author songhan
 */
@Named(value = "customerManagedBean")
@SessionScoped
public class CustomerManagedBean implements Serializable {

    /**
     * Creates a new instance of CustomerManagedBean
     */
    public CustomerManagedBean() {
    }
    
    @EJB
    private CustomerSessionBeanLocal csb;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    
    private String statusMessage;
    private Company ownerCompany;
    private String ownerCompanyName;
    
    private String name;
    private String contactNo;
    private String email;
    
    private String country;
    private String state;
    private String city;
    private String street;
    private String blockNo;
    private String postalCode;
    
    private Customer customer;
    private Customer selectCustomer;
    
    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
    }
    
    private void initUserInfo(Long cId){        
        try {
            ownerCompany = cmsbl.retrieveCompany(cId);
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "initUserInfo: "
                    + statusMessage, ""));
        }
    }
    
    public void createCustomer(){
        System.out.println("Enter CustomerManagedBean: createCustomer");
        Location loc = new Location(country, state, city, street, blockNo, postalCode);
        try {
            csb.createCustomer(name, email, contactNo, loc, (CustomerCompany)ownerCompany);
        } catch (CustomerAlreadyExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("CustomerAlreadyExistException: " + ex.getMessage());
        } catch (CustomerNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("CustomerNotExistException: " + ex.getMessage());
        } catch (MultipleCustomerWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleCustomerWithSameNameException: " + ex.getMessage());
        }
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
    }
    
    public List<Customer> retrieveCustomerList(){
        System.out.println("Enter CustomerManagedBean: retrieveCustomerList");
        List<Customer> customerList = csb.retrieveCustomerList((CustomerCompany)ownerCompany);
        if(customerList.isEmpty())
            statusMessage = "Company "+ownerCompanyName+" currently has not customer recorded!";
        System.out.println(statusMessage);
        System.out.println("End of CustomerManagedBean: retrieveCustomerList");
        return customerList;
    }
    
    public void removeCustomer(){
        System.out.println("Enter CustomerManagedBean: removeCustomer");
        csb.deleteCustomer(selectCustomer);
        statusMessage = "Delete successfully!";
        displayFaceMessage(statusMessage);
        System.out.println(statusMessage);
        System.out.println("End of CustomerManagedBean: removeCustomer");
    }
    public void preUpdateCustomer(Customer cc){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        customer=cc;
        country=cc.getLocation().getCountry();
        state=cc.getLocation().getState();
        city=cc.getLocation().getCity();
        street=cc.getLocation().getStreet();
        blockNo=cc.getLocation().getBlockNo();
        postalCode=cc.getLocation().getPostalCode();
        name=cc.getName();
        email=cc.getEmail();
        contactNo=cc.getContactNo();
        try {
            ec.redirect("updateCustomer.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(CustomerManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void updateCustomer(){
        System.out.println("Enter CustomerManagedBean: updateCustomer");
        Location loc = new Location(country, state, city, street, blockNo, postalCode);
        csb.updateCustomer(name, email, contactNo, loc, customer);
        statusMessage = "Info of Company "+ownerCompanyName+"'s customer "+name+" has been successfully updated.";
        displayFaceMessage(statusMessage);
        System.out.println(statusMessage);
        System.out.println("End of CustomerManagedBean: updateCustomer");
    }
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
        System.out.println("Redirecting to page "+url);
    }
    
    public void redirectToCreateCustomer() throws IOException{
        redirectToPage("createCustomer.xhtml");
    }
    
    public void redirectToViewCustomerList() throws IOException{
        redirectToPage("viewCustomerList.xhtml");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Customer getSelectCustomer() {
        return selectCustomer;
    }

    public void setSelectCustomer(Customer selectCustomer) {
        this.selectCustomer = selectCustomer;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    
}
