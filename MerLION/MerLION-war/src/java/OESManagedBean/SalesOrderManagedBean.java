package OESManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Customer;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.entity.SalesQuotation;
import OES.session.CustomerSessionBeanLocal;
import OES.session.InvoiceAndPaymentManagementLocal;
import OES.session.ProductInfoManagementLocal;
import OES.session.SalesOrderManagementLocal;
import WMS.entity.ExtractionRequest;
import WMS.entity.Warehouse;
import WMS.session.ExtractionRequestSessionBeanLocal;
import WMS.session.FacilityManagementLocal;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.CustomerNotExistException;
import util.exception.ExtractionQuantityExceedProductionNeedException;
import util.exception.ExtractionQuantityExceedSalesOrderNeedException;
import util.exception.MultipleCustomerWithSameNameException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.SalesOrderAlreadyFulfilledException;
import util.exception.SalesOrderFinishedGoodtNotReadyException;
import util.exception.SalesOrderIsNotBackOrderException;
import util.exception.SalesOrderNotBeenFulfilledException;
import util.exception.SalesOrderNotExistException;
import util.exception.SalesQuotationAlreadyEstablishedOrderException;
import util.exception.SalesQuotationNotApprovedException;
import util.exception.SalesQuotationNotExistException;
import util.session.SendEmail;

@ManagedBean(name = "salesOrderManagedBean")
@SessionScoped
public class SalesOrderManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private SalesOrderManagementLocal som;
    @EJB
    private InvoiceAndPaymentManagementLocal ipm;
    @EJB
    private CustomerSessionBeanLocal csb;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ExtractionRequestSessionBeanLocal ers;

    private String productName, company, customerName;
    private Long quotID, orderID;
    private Company userCompany;
    private String ownerCompany;

    private String price, quantity;
    private String[] quantities;

    private ArrayList<Double> quantityList = new ArrayList<>();
    private ArrayList<SalesQuotation> salesQuotList = new ArrayList<>();
    private List<Product> productList = new ArrayList();
    private SalesQuotation salesQ;
    private Product changeQ, selectedProduct;
    private SalesOrder salesOrder;
    private FinishedGood[] selectedProducts;
    private Date startDate, endDate;
    private HashMap<Product, Double> productListMap = new HashMap<>();
    private ArrayList<FinishedGood> fgList = new ArrayList<>();
    private ArrayList<SalesOrder> salesOrderList = new ArrayList<>();
    private CustomerCompany select_Company;
    private Customer selectedCustomer;
    private List<Customer> customerList = new ArrayList();
    private SendEmail sendEmail;
    private String email;

    private Product product;
    private String statusMessage;

    private Double finishedGoodRequest;
    private Warehouse wh;

    public SalesOrderManagedBean() {
    }

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            userCompany = cmsbl.retrieveCompany(cId);
            ownerCompany = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
    }

    public void retrieveCustomerList() {
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        customerList = csb.retrieveCustomerList(select_Company);
    }

    public void preAddAction() throws IOException {
        productList.clear();
        for (Object o : selectedProducts) {
            FinishedGood item = (FinishedGood) o;
            productList.add(item);
        }

        quantities = new String[productList.size()];

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("CreateSalesQuot_3.xhtml");
    }

    public void addAction(FinishedGood product, int index) throws ProductNotExistException, MultipleProductWithSameNameException {
        System.out.println("enter addAction");
        System.out.println("index: " + index + "   " + quantities[index]);

        Double q = Double.parseDouble(quantities[index]);
        //     productList.add(product);
        //     System.out.println("product added into productList");
        productListMap.put(product, q);
        System.out.println("productListMap being put");
        if (productListMap.isEmpty()) {
            System.out.println("productListMap is empty");
        }
        this.displayFaceMessage("Product has been added successfully!");
    }

    // public void addQuantity(FinishedGood product) {
    //      System.out.println("Enter addQuantity");
    //     Double q = Double.parseDouble(quantity);
    //     System.out.println("quantity is "+quantity);
    //   quantityList.add(q);
    //    this.displayFaceMessage("Add successful!");
    //     System.out.println("already add q");
    //     if(quantityList.isEmpty())
    //        System.out.println("quantityList is empty");
    //   }
    public void createSalesQuotation() throws IOException {
        //     int count = quantityList.size();
        //     System.out.println("Count number is "+count);
        //      int i = 0;
        //      if (productList.isEmpty())
        //         System.out.println("productList is empty");
        //     for (Object o : productList) {
        //         Product curProduct = (Product) o;
        //         if (i <= count) {
        //           productListMap.put(curProduct, quantityList.get(i));
        //             if(productListMap.isEmpty())
        //                 System.out.println("productListMap is empty");
        //          }
        //          i++;
        //      }
        select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        company = select_Company.getCompanyName();
        System.out.println("customerName is " + customerName);
        System.out.println("owner company is " + company);
        if (productList.isEmpty()) {
            System.out.println("productList is empty");
        }
        if (productListMap.isEmpty()) {
            System.out.println("productListMap is empty");
        }
        som.createSalesQuotation(productList, productListMap, customerName, company);
        this.displayFaceMessage("Sales Quotation has been created successfully!");
        productList.clear();
        productListMap.clear();
    }

    public void retrieveProductList() throws ProductNotExistException, IOException {
        try {
            customerName = selectedCustomer.getName();
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            company = select_Company.getCompanyName();
            fgList = pim.retrieveProductList(company);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("CreateSalesQuot_2.xhtml");
        } catch (ProductNotExistException e) {
            this.displayFaceMessage("Product does not exist");
        }
    }

    public void retrieveSalesQuotList() throws SalesQuotationNotExistException, IOException {
        try {
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            salesQuotList = som.retrieveSalesQuotationList(ownerCompany);
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("Sales quotation does not exist");
        }
    }

    public void preUpdateSalesQuotation(SalesQuotation sq) throws IOException {
        salesQ = sq;
        setProductList(sq.getProductList());
        setProductListMap(sq.getProductListMap());
        setCustomerName(sq.getCustomer().getName());
        setQuotID(sq.getId());
        //   getListQuantity(sq.getProductListMap());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("UpdateSalesQuot.xhtml");
    }

    //   public void getListQuantity(HashMap<Product, Double> pList) {
    //       for (Map.Entry entry : pList.entrySet()) {
    //           Product pItem = (Product) entry.getKey();
    //         Integer qItem = (Integer) entry.getValue();
    //       }
    //  }
    public Double getSingleQuantity(Product prod) {
        System.out.println("Product name is " + prod.getProductName());
        Double singleQ = salesQ.getProductListMap().get(prod);
        return singleQ;
    }
    
    public Double getOrderSingleQuantity(Product prod) {
        System.out.println("Product name is " + prod.getProductName());
        Double singleQ = salesOrder.getProductQuantityMap().get(prod);
        return singleQ;
    }

    public void removeFromProductList() throws SalesQuotationNotExistException {
        try {
            //    for (Object o : salesQ.getProductList()) {
            //           Product item = (Product) o;
            //           if (!item.equals(selectedProduct)) {
            //               productList.add(item);
            //           }
            //      }
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            salesQ.getProductList().remove(selectedProduct);
            productList.addAll(salesQ.getProductList());
            productList.remove(selectedProduct);
            if (productList.isEmpty()) {
                System.out.println("productList is empty");
            }
            salesQ.getProductListMap().remove(selectedProduct);
            productListMap.putAll(salesQ.getProductListMap());
            productListMap.remove(selectedProduct);
            String cName = salesQ.getCustomer().getName();
            Long qID = salesQ.getId();
            som.updateSalesQuotation(productList, productListMap, ownerCompany, cName, qID);
            this.displayFaceMessage("Product has been successfully deleted from the list!");
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        } catch (CustomerNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("CustomerNotExistException: " + ex.getMessage());
        } catch (MultipleCustomerWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleCustomerWithSameNameException: " + ex.getMessage());
        }
    }

    public void preChangeQuantity(Product prod) throws IOException {
        changeQ = prod;
        productName = prod.getProductName();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("ChangeQuantity.xhtml");

    }

    public void changeQuantity() throws SalesQuotationNotExistException, IOException {
        try {
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            System.out.println("quantity is " + quantity);
            Double changed = Double.parseDouble(quantity);
            salesQ.getProductListMap().put(changeQ, changed);
            System.out.println("salesQ, quantity is " + salesQ.getProductListMap().get(changeQ));
            productListMap.putAll(salesQ.getProductListMap());
            System.out.println("productListMap, quantity is " + productListMap.get(changeQ));
            String cName = salesQ.getCustomer().getName();
            Long qID = salesQ.getId();
            som.updateSalesQuotation(productList, productListMap, ownerCompany, cName, qID);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("UpdateSalesQuot.xhtml");
            this.displayFaceMessage("Product quantity has been updated successfully!");
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        } catch (CustomerNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("CustomerNotExistException: " + ex.getMessage());
        } catch (MultipleCustomerWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleCustomerWithSameNameException: " + ex.getMessage());
        }
    }

    public void updateSalesQuotation() {
        try {
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            Long qID = salesQ.getId();
            productListMap.putAll(salesQ.getProductListMap());
            productList.addAll(salesQ.getProductList());
            som.updateSalesQuotation(productList, productListMap, ownerCompany, customerName, qID);
            this.displayFaceMessage("Sales quotation has been successfully updated!");
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        } catch (CustomerNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("CustomerNotExistException: " + ex.getMessage());
        } catch (MultipleCustomerWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleCustomerWithSameNameException: " + ex.getMessage());
        }
    }

    public void retrieveSalesQuotation(Long qID) throws IOException, SalesQuotationNotExistException {
        try {
            quotID = qID;
            salesQ = som.retrieveSalesQuotation(quotID);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("DisplaySalesQuot.xhtml");
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        }
    }

    public void approveSalesQuotation(Long qID) throws SalesQuotationNotExistException {
        try {
            quotID = qID;
            som.approveSalesQuotation(quotID);
            this.displayFaceMessage("Sales Quotation has been successfully approved!");
        } catch (SalesQuotationNotExistException e) {
            this.displayFaceMessage("The sales quotation does not exist");
        }
    }

    public void createSalesOrder(Long qID) throws SalesQuotationNotExistException {
        try {
            quotID = qID;
            som.createSalesOrder(quotID);
            this.displayFaceMessage("Sales Order has been successfully created!");
        } catch (SalesQuotationNotExistException ex) {
            String statusMesage =ex.getMessage();
            System.out.println(statusMesage);
            this.displayFaceMessage(statusMesage);
        } catch (SalesQuotationNotApprovedException ex) {
            String statusMesage = ex.getMessage();
            System.out.println(statusMesage);
            this.displayFaceMessage(statusMesage);
        } catch (SalesQuotationAlreadyEstablishedOrderException ex) {
            String statusMesage = ex.getMessage();
            System.out.println(statusMesage);
            this.displayFaceMessage(statusMesage);
        }
    }

    public void retrieveSalesOrder(Long oID) throws SalesOrderNotExistException, IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            orderID = oID;
            salesOrder = som.retrieveSalesOrder(orderID);
            ec.redirect("DisplaySalesOrder.xhtml");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("The sales order does not exist");
        }
    }

    public void retrieveSalesOrderList() throws SalesOrderNotExistException, IOException {
        System.out.println("enter retrieveSalesOrderList");
        System.out.println("Before entering try catch");
        try {
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            salesOrderList = som.retrieveAllSalesOrderList(ownerCompany);
            System.out.println("order list retrieved");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("The sales order does not exist");
        }
    }

    public void markBackOrder(Long oID) throws SalesOrderNotExistException {
        try {
            orderID = oID;
            som.markBackOrder(orderID);
            this.displayFaceMessage("Successfully marked as back order!");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("The sales order does not exist");
        }
    }

    public void approveSalesOrder(Long oID) throws SalesOrderNotExistException {
        try {
            orderID = oID;
            salesOrder = som.retrieveSalesOrder(oID);
            som.approveSalesOrder(orderID);

            //         Integer index = salesOrderList.indexOf(salesOrder);
            //           so = soml.retrieveServiceOrder(serviceOrder.getId());
            //       soList.remove(serviceOrder);
            //       if(index<soList.size()-1)
            //             soList.add(index, so);
            //       else
            //             soList.add(so);
            this.displayFaceMessage("The sales order has been successfully approved!");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("The sales order does not exist");
        }
    }

    public void retrieveSalesOrderForProductExtraction(SalesOrder so) throws IOException {
        salesOrder = so;
        orderID = so.getId();
        redirectToPage("scheduleFinishedGoodExtraction.xhtml");
    }

    public Boolean checkAllReadyOrNot() {
        return som.checkAllProductReadyOrNot(salesOrder);
    }

    public Double getSingleRequiredProductQuantity(Product product) {
        HashMap<Product, Double> materialQuantityMap = salesOrder.getProductQuantityMap();
        return materialQuantityMap.get(product);
    }

    public Double getSingleRequiredProductQuantity() {
        HashMap<Product, Double> materialQuantityMap = salesOrder.getProductQuantityMap();
        return materialQuantityMap.get(product);
    }

    public Double retrieveTotalScheduledProductQuantity() {
        List<ExtractionRequest> erList = som.retrieveExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public Double retrieveOnHandProductQuantity() {
        List<ExtractionRequest> erList = som.retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public Double retrieveArrivingProductQuantity() {
        List<ExtractionRequest> erList = som.retrieveUnfulfilledExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        Double result = 0.0;
        for (Object o : erList) {
            ExtractionRequest curEr = (ExtractionRequest) o;
            result = result + curEr.getQuantity();
        }
        return result;
    }

    public void confirmProductReadyForSalesOrder() {
        System.out.println("Enter SalesOrderManagedBean: confiproductProductReadyForSalesOrder");
        som.confirmProductReadyForSalesOrder(salesOrder);
        System.out.println("End of SalesOrderManagedBean: confiproductProductReadyForSalesOrder");
    }

    public void viewExtractionRequestList() throws IOException {
        redirectToPage("viewExtractionRequestList.xhtml");
    }

    public List<ExtractionRequest> retrieveExtractionRequestListForOneProductInOneSalesOrder() {
        List<ExtractionRequest> erList = som.retrieveExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        return erList;
    }

    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder() {
        List<ExtractionRequest> erList = som.retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        return erList;
    }

    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneProductInOneSalesOrder() {
        List<ExtractionRequest> erList = som.retrieveUnfulfilledExtractionRequestListForOneProductInOneSalesOrder(product, salesOrder);
        return erList;
    }

    public Integer getExtractionRequestListSize(List<ExtractionRequest> erList) {
        return erList.size();
    }

    public void retrieveProductForExtraction(Product product) throws IOException {
        this.product = product;
        redirectToPage("viewWarehouseListForFinishedGoodExtraction.xhtml");
    }

    public List<Warehouse> retrieveWarehouseListForSpecificProduct() {
        List<Warehouse> whList = new ArrayList<>();
        try {
            String fgName = product.getProductName();
            whList = fml.retrieveWarehouseListForSpecificFinishedGood(fgName, ownerCompany);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        return whList;
    }

    public Double getProductQuantityForSpecificWarehouse(Warehouse wh) {
        return wh.getFinishedGoodMap().get((FinishedGood) product);
    }

    public Integer getWarehouseListSize(List<Warehouse> list) {
        return list.size();
    }

    public void initiateExtractionRequest(Warehouse warehouse) {
        wh = warehouse;
        System.out.println(statusMessage);
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
    }

    public void createExtractionRequest() {
        System.out.println("Enter ProductionManagedBean: createExtractionRequest");

        try {
            ers.dealWithSalesOrderFinishedGoodExtraction(product, finishedGoodRequest, wh, salesOrder);
            statusMessage = "Extraction Request Created successfully!";
            RequestContext.getCurrentInstance().execute("PF('dlg').hide()");
            System.out.println(statusMessage);
            this.displayFaceMessage(statusMessage);
        } catch (ExtractionQuantityExceedSalesOrderNeedException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }

        System.out.println("End of ProductionManagedBean: createExtractionRequest");
    }

    public void fulfillSalesOrder() {
        try {
            som.fulfillSalesOrder(salesOrder);
            Location loc = salesOrder.getSalesQuotation().getCustomer().getLocation();
            ipm.createInvoice(orderID, loc);
            this.displayFaceMessage("Sales Order " + orderID + " has been fulfilled and invoice has been issued.");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (SalesOrderAlreadyFulfilledException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println( e.getMessage());
        } catch (SalesOrderNotBeenFulfilledException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (SalesOrderFinishedGoodtNotReadyException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    public Boolean checkOneRawMaterialReadyOrNot(Product product) {
        System.out.println("SalesOrderManagedBean: checkOneRawMaterialReadyOrNot: Now checking finished good " + product.getProductName());
        Boolean result = null;
        try {
            result = som.checkOneProductReadyOrNot(product, salesOrder);
        } catch (java.lang.NullPointerException ex) {
            System.out.println("Catched null point exception for FinishedGood " + product.getProductName());
        } catch (javax.ejb.EJBException ex) {
            System.out.println("Catched EJB exception for FinishedGood " + product.getProductName());
        }
        return result;
    }

    public void updateBackOrder(Long oID) throws SalesOrderNotExistException, SalesOrderIsNotBackOrderException {
        try {
            orderID = oID;
            som.updateBackOrder(orderID);
            this.displayFaceMessage("Back order has been successfully updated as Cancelled!");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("The sales order does not exist");
        } catch (SalesOrderIsNotBackOrderException e) {
            this.displayFaceMessage("The sales order is not a back order");
        }
    }
    
    public void sendQuot(SalesQuotation sQuot) throws MessagingException{
            select_Company = (CustomerCompany) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            email = sQuot.getCustomer().getEmail();
            String cc =sQuot.getCustomer().getEmail();
            String subject = "Merlion Platform - Sending Sales Quotation " + sQuot.getId() + " From " + ownerCompany;
            System.out.println("Inside send email");

            String content = "<h2>Dear " + cc
                    + ",</h2><br /><h1>  Thank you very much for ordering from " + ownerCompany + " ! </h1><br />"
                    + "<h1>Below is your order, please confirm the details:</h1>"
                    + "<h2 align=\"center\">Sales Quotation ID: " + sQuot.getId()
                    + "<br />Date: " + sQuot.getSalesQuotationDate()
                    + "<br />Saling Pary: " + ownerCompany
                    + "<br />Receiving Party: " + cc
                    + "<br />Total Price: " + sQuot.getTotalPrice()
                    + "<br />" + sQuot.toString()
                    + "</h2><br />"
                    + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                    + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
            System.out.println(content);
            sendEmail.run(email, subject, content);
            this.displayFaceMessage("Sales Quotation has been sent successfully via Email!");
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void redirectToPage(String url) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect(url);
        System.out.println("Redirecting to page " + url);
    }

    public void redirectToScheduleProductForSalesOrder() throws IOException {
        redirectToPage("scheduleFinishedGoodExtraction.xhtml");
    }
    
    public void redirectToViewWarehouseList() throws IOException {
        redirectToPage("viewWarehouseListForFinishedGoodExtraction.xhtml");
    }
    
    public void redirectToSalesOrderList() throws IOException{
        redirectToPage("DisplaySalesOrderList.xhtml");
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getQuotID() {
        return quotID;
    }

    public void setQuotID(Long quotID) {
        this.quotID = quotID;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String[] getQuantities() {
        return quantities;
    }

    public void setQuantities(String[] quantities) {
        this.quantities = quantities;
    }

    public ArrayList<Double> getQuantityList() {
        return quantityList;
    }

    public void setQuantityList(ArrayList<Double> quantityList) {
        this.quantityList = quantityList;
    }

    public ArrayList<SalesQuotation> getSalesQuotList() {
        return salesQuotList;
    }

    public void setSalesQuotList(ArrayList<SalesQuotation> salesQuotList) {
        this.salesQuotList = salesQuotList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public SalesQuotation getSalesQ() {
        return salesQ;
    }

    public void setSalesQ(SalesQuotation salesQ) {
        this.salesQ = salesQ;
    }

    public Product getChangeQ() {
        return changeQ;
    }

    public void setChangeQ(Product changeQ) {
        this.changeQ = changeQ;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public FinishedGood[] getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(FinishedGood[] selectedProducts) {
        this.selectedProducts = selectedProducts;
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

    public HashMap<Product, Double> getProductListMap() {
        return productListMap;
    }

    public void setProductListMap(HashMap<Product, Double> productListMap) {
        this.productListMap = productListMap;
    }

    public ArrayList<FinishedGood> getFgList() {
        return fgList;
    }

    public void setFgList(ArrayList<FinishedGood> fgList) {
        this.fgList = fgList;
    }

    public ArrayList<SalesOrder> getSalesOrderList() {
        return salesOrderList;
    }

    public void setSalesOrderList(ArrayList<SalesOrder> salesOrderList) {
        this.salesOrderList = salesOrderList;
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Company getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(Company userCompany) {
        this.userCompany = userCompany;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Double getFinishedGoodRequest() {
        return finishedGoodRequest;
    }

    public void setFinishedGoodRequest(Double finishedGoodRequest) {
        this.finishedGoodRequest = finishedGoodRequest;
    }

    public Warehouse getWh() {
        return wh;
    }

    public SendEmail getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(SendEmail sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWh(Warehouse wh) {
        this.wh = wh;
    }

}
