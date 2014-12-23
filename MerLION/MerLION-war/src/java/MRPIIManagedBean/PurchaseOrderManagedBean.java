/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import MRPII.entity.PurchaseOrder;
import MRPII.entity.PurchaseQuotation;
import MRPII.entity.RawMaterial;
import MRPII.entity.Supplier;
import MRPII.session.PurchaseOrderManagementLocal;
import MRPII.session.RawMaterialManagementLocal;
import MRPII.session.SupplierManagementLocal;
import OES.entity.Customer;
import OES.entity.FinishedGood;
import OES.session.CustomerSessionBeanLocal;
import OES.session.ProductInfoManagementLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import WMS.session.StorageRequestSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.PurchaseOrderAlreadyDealtException;
import util.exception.PurchaseOrderHasBeenSentException;
import util.exception.PurchaseOrderNotExistException;
import util.exception.PurchaseQuotationNotApprovedException;
import util.exception.PurchaseQuotationNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SalesQuotationNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.SupplierNotProvideProductException;
import util.session.SendEmail;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "purchaseOrderManagedBean")
@SessionScoped
public class PurchaseOrderManagedBean implements Serializable {

    @EJB
    PurchaseOrderManagementLocal pom;
    @EJB
    FacilityManagementLocal fml;
    @EJB
    StorageRequestSessionBeanLocal srs;
    @EJB
    ProductInfoManagementLocal pim;
    @EJB
    SupplierManagementLocal sml;
    @EJB
    RawMaterialManagementLocal rmm;
    @EJB
    private CustomerSessionBeanLocal csb;

    private Long purQuotID, purOrderID;
    private PurchaseQuotation purQuot;
    private PurchaseOrder purOrder;
    private ArrayList<PurchaseOrder> goodsOrderList = new ArrayList();
    private ArrayList<PurchaseOrder> materialOrderList = new ArrayList();
    private ArrayList<PurchaseQuotation> goodsQuotList = new ArrayList();
    private ArrayList<PurchaseQuotation> materialQuotList = new ArrayList();
    private Date startDate, endDate;
    private String ownerCompany, supplierName, email, customerCompany;
    private CustomerCompany select_Company;
    private List<Warehouse> warehouseList = new ArrayList();
    private Warehouse warehouse;
    private SendEmail sendEmail;
    private Boolean perishable=Boolean.FALSE;
    private Boolean flammable=Boolean.FALSE;
    private Boolean pharmaceutical=Boolean.FALSE;
    private Boolean highValue=Boolean.FALSE;
    private FinishedGood selectedGood;
    private ArrayList<FinishedGood> allGoods=new ArrayList();
    private List<Supplier> supplierList=new ArrayList();
    private Supplier selectedSupplier;
    private String yearStr, monthStr, weekStr, batchStr;
    private ArrayList<RawMaterial> allMaterials=new ArrayList();
    private RawMaterial selectedMaterial;
    private Customer selectedCustomer;
    private List<Customer> customerList=new ArrayList();

    public PurchaseOrderManagedBean() {
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void retrieveCustomerList(){
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        customerList=csb.retrieveCustomerList(select_Company);
    }

    public void addMessage(Boolean value1) {
        String summary = value1 ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void retrieveProductList() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        try {
            allGoods = pim.retrieveProductList(ownerCompany);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage( ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        }
    }
    
    public void retrieveMaterialList(){
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        try {
            allMaterials=rmm.retrieveRawMaterialList(ownerCompany);
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("RawMaterialNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveSupplierList() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        try {
            supplierList = sml.retrieveSupplierListForCompany(select_Company);
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        }
    }

    public void preCreateGoodsQuot() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        supplierList=selectedGood.getSupplierList();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("CreateGoodsQuot_2.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }
    
    public void preCreateGoodsQuotNext(){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("CreateGoodsQuot_3.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(PurchaseOrderManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createGoodsPurchaseQuot() {
        Integer year=Integer.parseInt(yearStr);
        Integer month=Integer.parseInt(monthStr);
        Integer week=Integer.parseInt(weekStr);
        String sName=selectedSupplier.getName();
        String pName=selectedGood.getProductName();
        Integer batchNo=Integer.parseInt(batchStr);
        try {
            pom.createFinishedGoodPurchaseQuotation(year, month, week, select_Company, sName, pName, batchNo);
            this.displayFaceMessage("Purchase Quotation has been successfully created!");
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        } catch (SupplierNotProvideProductException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotProvideProductException: " + ex.getMessage());
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        }
    }
    
    public void preCreateMaterialQuot() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        supplierList=selectedMaterial.getSupplierList();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("CreateMaterialQuot_2.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }
    
    public void preCreateMaterialQuotNext(){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("CreateMaterialQuot_3.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void createMaterialPurchaseQuot() {
        Integer year=Integer.parseInt(yearStr);
        Integer month=Integer.parseInt(monthStr);
        Integer week=Integer.parseInt(weekStr);
        String sName=selectedSupplier.getName();
        String pName=selectedMaterial.getProductName();
        Integer batchNo=Integer.parseInt(batchStr);
        try {
            pom.createRawMaterialPurchaseQuotation(year, month, week, select_Company, sName, pName, batchNo);
            this.displayFaceMessage("Purchase Quotation has been successfully created!");
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        } catch (SupplierNotProvideProductException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotProvideProductException: " + ex.getMessage());
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        }
    }

    public void retrievePurchaseQuot(Long pqID) throws PurchaseQuotationNotExistException, IOException {
        try {
            purQuotID = pqID;
            purQuot = pom.retrievePurchaseQuotation(purQuotID);
   //         FacesContext fc = FacesContext.getCurrentInstance();
    //        ExternalContext ec = fc.getExternalContext();
     //       ec.redirect("DisplayPurchaseQuot.xhtml");
        } catch (PurchaseQuotationNotExistException e) {
            this.displayFaceMessage("The purchase quotation does not exist");
        }
    }

    public void createPurchaseOrder(Long pqID) throws PurchaseQuotationNotExistException {
        try {
            purQuotID = pqID;
            System.out.println("PurchaseQuotation ID: " + purQuotID);
            pom.createPurchaseOrder(purQuotID);
            this.displayFaceMessage("Purchase Order has been successfully created!");
        } catch (PurchaseQuotationNotExistException e) {
            System.out.println(e.getMessage());
            this.displayFaceMessage("The purchase quotation does not exist");
        } catch (PurchaseQuotationNotApprovedException ex) {
            System.out.println(ex.getMessage());
            this.displayFaceMessage("PurchaseQuotationNotApprovedException: "+ex.getMessage());
        }
    }

    public void retrievePurchaseOrder(Long orderID) throws PurchaseOrderNotExistException, IOException {
        try {
            purOrderID = orderID;
            purOrder = pom.retrievePurchaseOrder(purOrderID);
    //        FacesContext fc = FacesContext.getCurrentInstance();
    //        ExternalContext ec = fc.getExternalContext();
    //        ec.redirect("DisplayPurchaseOrder.xhtml");
        } catch (PurchaseOrderNotExistException e) {
            this.displayFaceMessage("The purchase oder does not exist");
        }
    }

    public void retrieveGoodsOrderList() throws PurchaseOrderNotExistException, IOException {
        try {
            goodsOrderList.clear();
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            if (select_Company == null) {
                throw new NullPointerException("select_Company not exist!");
            }
            ownerCompany = select_Company.getCompanyName();
            goodsOrderList.addAll(pom.retrieveGoodsPurchaseOrderList(ownerCompany));
        } catch (PurchaseOrderNotExistException e) {

            this.displayFaceMessage("The purchase order does not exist");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            this.displayFaceMessage(e.getMessage());
        }
    }

    public void retrieveGoodsQuotList() throws IOException, PurchaseQuotationNotExistException {
        try {
            goodsQuotList.clear();
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            if (select_Company == null) {
                throw new NullPointerException("select_Company not exist!");
            }
            ownerCompany = select_Company.getCompanyName();
            goodsQuotList.addAll(pom.retrieveGoodsPurchaseQuotationList(ownerCompany));
        } catch (PurchaseQuotationNotExistException e) {
            System.out.println(e.getMessage());
        }
    }

    public void retrieveMaterialOrderList() throws IOException {
        try {
            materialOrderList.clear();
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            materialOrderList.addAll(pom.retrieveMaterialPurchaseOrderList(ownerCompany));
        } catch (PurchaseOrderNotExistException e) {
              System.out.println(e.getMessage());
        }
    }

    public void retrieveMaterialQuotList() throws IOException, PurchaseQuotationNotExistException {
        try {
            materialQuotList.clear();
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            if (select_Company == null) {
                throw new NullPointerException("select_Company not exist!");
            }
            ownerCompany = select_Company.getCompanyName();
            materialQuotList.addAll(pom.retrieveRawMaterialPurchaseQuotationList(ownerCompany));
        } catch (PurchaseQuotationNotExistException e) {
              System.out.println(e.getMessage());
        }
    }
    
    public void approvePurchaseQuotation(Long qID) throws SalesQuotationNotExistException {
        try {
            purQuotID=qID;
            pom.approvePurchaseQuotation(purQuotID);
            this.displayFaceMessage("Sales Quotation has been successfully approved!");
        } catch (PurchaseQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        }
    }

    public void preSendOrder(Long orID) {
        Company cc = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        purOrderID = orID;
        warehouseList = fml.retrieveOwnedWarehouseList(cc);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("SendOrder.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage("IOException: " + ex.getMessage());
        }
    }

    public void chooseWarehouse(Warehouse w) {
        warehouse = w;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("SendOrder_2.xhtml");
        } catch (IOException ex) {
            this.displayFaceMessage("IOException: " + ex.getMessage());
        }
    }

    public void sendWarehouseRequest(PurchaseOrder po) {
        try {
            srs.dealWithPurchaseOrder(po, warehouse, perishable, flammable, pharmaceutical, highValue);
        } catch (PurchaseOrderAlreadyDealtException ex) {
            System.out.println(ex.getMessage());
            this.displayFaceMessage(ex.getMessage());
        }
    }

    public void sendOrder(Warehouse w) {
        System.out.println("enter sendOrder()");
        warehouse = w;
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        PurchaseOrder toSend;
        try {
            System.out.println("before retrievePurchaseOrder)");
            toSend = pom.retrievePurchaseOrder(purOrderID);
            pom.sendOrder(toSend);
            supplierName=toSend.getPurchaseQuotation().getSupplier().getName();
            email=toSend.getPurchaseQuotation().getSupplier().getEmail();

            String subject = "Merlion Platform - Sending Purchase Order " + purOrderID + " From " + ownerCompany;
            System.out.println("Inside send email");

            String content = "<h2>Dear " + supplierName
                    + ",</h2><br /><h1>  Our company, " + ownerCompany + ", sincerely request to purchase following product from you:</h1><br />"
                    + "<h1>Below is the invoice for your order:</h1>"
                    + "<h2 align=\"center\">Order ID: " + toSend.getId()
                    + "<br />Product: " + toSend.getPurchaseQuotation().getProduct().getProductName()
                    + "<br />Quantity: " + toSend.getPurchaseQuotation().getQuantity()
          //          + "<br />Scheduled Sending Date: " + toSend.getPurchaseQuotation().getScheduledSentDate().toString()
         //           + "<br />Scheduled Arrival Date: " + toSend.getPurchaseQuotation().getScheduledArrivalDate().toString()
                    + "<br />Address: " + warehouse.getLocation().getCountry() + "," + warehouse.getLocation().getState() + "," + warehouse.getLocation().getCity()
                    + "<br />         " + warehouse.getLocation().getStreet() + "," + warehouse.getLocation().getBlockNo() + "," + warehouse.getLocation().getPostalCode()
                    + "</h2><br />"
                    + "<p style=\"color: #ff0000;\">We will make payment once we received the invoice of goods. Thank you.</p>"
                    + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                    + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
            System.out.println(content);
            sendEmail.run(email, subject, content);
            System.out.println("after sending email");
            sendWarehouseRequest(toSend);
            System.out.println("after sending warehouse request");
        } catch (PurchaseOrderNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
        } catch (PurchaseOrderHasBeenSentException ex) {
           this.displayFaceMessage(ex.getMessage());
        }
        this.displayFaceMessage("Invoice has been sent successfully via Email!");
    }

    public Long getPurQuotID() {
        return purQuotID;
    }

    public void setPurQuotID(Long purQuotID) {
        this.purQuotID = purQuotID;
    }

    public Long getPurOrderID() {
        return purOrderID;
    }

    public void setPurOrderID(Long purOrderID) {
        this.purOrderID = purOrderID;
    }

    public PurchaseQuotation getPurQuot() {
        return purQuot;
    }

    public void setPurQuot(PurchaseQuotation purQuot) {
        this.purQuot = purQuot;
    }

    public PurchaseOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurchaseOrder purOrder) {
        this.purOrder = purOrder;
    }

    public ArrayList<PurchaseOrder> getGoodsOrderList() {
        return goodsOrderList;
    }

    public void setGoodsOrderList(ArrayList<PurchaseOrder> goodsOrderList) {
        this.goodsOrderList = goodsOrderList;
    }

    public ArrayList<PurchaseOrder> getMaterialOrderList() {
        return materialOrderList;
    }

    public void setMaterialOrderList(ArrayList<PurchaseOrder> materialOrderList) {
        this.materialOrderList = materialOrderList;
    }

    public ArrayList<PurchaseQuotation> getGoodsQuotList() {
        return goodsQuotList;
    }

    public void setGoodsQuotList(ArrayList<PurchaseQuotation> goodsQuotList) {
        this.goodsQuotList = goodsQuotList;
    }

    public ArrayList<PurchaseQuotation> getMaterialQuotList() {
        return materialQuotList;
    }

    public void setMaterialQuotList(ArrayList<PurchaseQuotation> materialQuotList) {
        this.materialQuotList = materialQuotList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerCompany() {
        return customerCompany;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public SendEmail getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(SendEmail sendEmail) {
        this.sendEmail = sendEmail;
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

    public FinishedGood getSelectedGood() {
        return selectedGood;
    }

    public void setSelectedGood(FinishedGood selectedGood) {
        this.selectedGood = selectedGood;
    }

    public ArrayList<FinishedGood> getAllGoods() {
        return allGoods;
    }

    public void setAllGoods(ArrayList<FinishedGood> allGoods) {
        this.allGoods = allGoods;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(ArrayList<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public String getYearStr() {
        return yearStr;
    }

    public void setYearStr(String yearStr) {
        this.yearStr = yearStr;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
    }

    public String getBatchStr() {
        return batchStr;
    }

    public void setBatchStr(String batchStr) {
        this.batchStr = batchStr;
    }

    public ArrayList<RawMaterial> getAllMaterials() {
        return allMaterials;
    }

    public void setAllMaterials(ArrayList<RawMaterial> allMaterials) {
        this.allMaterials = allMaterials;
    }

    public RawMaterial getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(RawMaterial selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

}
