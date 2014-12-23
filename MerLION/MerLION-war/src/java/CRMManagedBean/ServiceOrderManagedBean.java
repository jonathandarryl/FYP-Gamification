/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceOrder;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceContractManagementSessionLocal;
import CRM.session.ServiceOrderManagementLocal;
import CRM.session.ServiceQuotationManagementSessionLocal;
import Common.entity.Company;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
import OES.entity.FinishedGood;
import OES.entity.Product;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.ExceedMaxCapacityException;
import util.exception.LocationNotMatchException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
import util.exception.ServiceOrderAlreadyApprovedException;
import util.exception.ServiceOrderAlreadyOutsourcedException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.ServiceOrderStartAfterContractExpireException;
import util.exception.ServiceOrderStartBeforeContractCommmenceException;
import util.exception.ServiceQuotationNotExistException;
import static CRMManagedBean.ServiceCatalogManagedBean.establishConnection;
import TMS.entity.TransOrder;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import util.exception.InsufficientInventoryException;

/**
 *
 * @author songhan
 */
@Named(value = "serviceOrderManagedBean")
@SessionScoped
public class ServiceOrderManagedBean implements Serializable {

    /**
     * Creates a new instance of NewServiceOrderManagedBean
     */
    public ServiceOrderManagedBean() {
    }

    @EJB
    private ServiceContractManagementSessionLocal scmsl;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ServiceOrderManagementLocal soml;
    @EJB
    private FacilityManagementLocal fml;
    @EJB
    private ServiceQuotationManagementSessionLocal sqm;

    private String statusMessage;
    private Company userCompany;
    private String userCompanyName;
    private String counterPartCompanyName;
    private Long userCompanyId;

    private ServiceContract sc;

    private Date startDate;
    private Date endDate;
    private String description;
    private Boolean isToEstablishTransOrder;
    private Boolean isToEstablishWarehouseOrder;
    private Boolean aggregateOrNot;
    private ServiceOrder so;
    private Warehouse sourceWarehouse;

    //For outscourcing order creation
    private Double price;
    private ServiceOrder outsourceToOrder;
    private ServiceOrder outsourceFromOrder;
    
    private String trackingNumber;
    
    private String interPrice;

    @PostConstruct
    public void init() {
        userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            userCompany = cmsbl.retrieveCompany(cId);
            userCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }
    
    public void changePrice(int i){
        price = so.getPrice();
        switch(i){
            case 0:
                interPrice = "S$ "+price.toString();
                break;
            case 1:
                price *= 0.8;
                interPrice = "$ "+price.toString();
                break;
            case 2:
                price *= 4.75;
                interPrice = "RMB "+price.toString();
                break;
            case 3:
                price *= 89.28;
                interPrice = "Yen "+price.toString();
                break; 
            case 4:
                price *= 0.62;
                interPrice = "Euro "+price.toString();
                break;  
            default:
                break;
        }
    }

    public List<ServiceContract> retrieveOutgoingServiceContractList() throws IOException {
        List<ServiceContract> scList = new ArrayList<>();
        try {
            scList = scmsl.retrieveCurrentOutsourcingServiceContractList(userCompany.getCompanyName());
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return scList;
    }

    public void exportToPDF(ServiceOrder so) throws IOException, JRException, ClassNotFoundException, SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException, MessagingException {

        ServiceContract sc = so.getServiceContract();

        //Send To client 
        String clientName = sc.getServiceQuotation().getClientCompany().getCompanyName();
        String clientCountry = sc.getServiceQuotation().getClientCompany().getLocation().getCountry();
        String clientState = sc.getServiceQuotation().getClientCompany().getLocation().getState();
        String clientCity = sc.getServiceQuotation().getClientCompany().getLocation().getCity();
        String clientStreet = sc.getServiceQuotation().getClientCompany().getLocation().getStreet();
        String clientBlockNo = sc.getServiceQuotation().getClientCompany().getLocation().getBlockNo();
        String clientPostalCode = sc.getServiceQuotation().getClientCompany().getLocation().getPostalCode();
        String clientContactNo = sc.getServiceQuotation().getClientCompany().getContactNo();

        //From provider
        String providerName = sc.getProvider().getCompanyName();
        String providerCountry = sc.getProvider().getLocation().getCountry();
        String providerState = sc.getProvider().getLocation().getState();
        String providerCity = sc.getProvider().getLocation().getCity();
        String providerStreet = sc.getProvider().getLocation().getStreet();
        String providerBlockNo = sc.getProvider().getLocation().getBlockNo();
        String providerPostalCode = sc.getProvider().getLocation().getPostalCode();
        String providerContactNo = sc.getProvider().getContactNo();

        //basic info
        Timestamp creationTime = so.getCreationTime();
        //   String contractName = sc.getServiceQuotation().getServiceCatalog().getServiceName();
        String contractDescrip = sc.getServiceQuotation().getDescription();
        Timestamp contractStart = so.getStartTime();
        Timestamp contractEnd = so.getEndTime();
        String productName = sc.getServiceQuotation().getProduct().getProductName();
        Double productQuantity = sc.getServiceQuotation().getEstimatedQuantity();
        Double totalPrice = sc.getServiceQuotation().getTotalPrice();
        Boolean perishable = sc.getServiceQuotation().isPerishable();
        Boolean pharmaceutical = sc.getServiceQuotation().isPharmaceutical();
        Boolean highValue = sc.getServiceQuotation().isHighValue();
        Boolean flammable = sc.getServiceQuotation().isFlammable();

        //wh info
        String whName = sc.getServiceQuotation().getWarehouse().getName();
        String whContactNo = sc.getServiceQuotation().getWarehouse().getContactNo();
        Long whId = sc.getServiceQuotation().getWarehouse().getId();
        Double whPricePerCapacityPerDay = sc.getServiceQuotation().getWarehouse().getPricePerCapacityPerDay();
        Integer whDuration = sc.getServiceQuotation().getStorageDurationDays();
        Integer whQuantity = sc.getServiceQuotation().getRequiredWarehouseCapacity();
        Double whPrice = whPricePerCapacityPerDay * whDuration * whQuantity;

        String whCountry = sc.getServiceQuotation().getWarehouse().getLocation().getCountry();
        String whState = sc.getServiceQuotation().getWarehouse().getLocation().getState();
        String whCity = sc.getServiceQuotation().getWarehouse().getLocation().getCity();
        String whStreet = sc.getServiceQuotation().getWarehouse().getLocation().getStreet();
        String whBlockNo = sc.getServiceQuotation().getWarehouse().getLocation().getBlockNo();
        String whPostalCode = sc.getServiceQuotation().getWarehouse().getLocation().getPostalCode();

        //trans info
        Double tranPrice = totalPrice - whPrice;

        String srCountry = sc.getServiceQuotation().getSource().getCountry();
        String srState = sc.getServiceQuotation().getSource().getState();
        String srCity = sc.getServiceQuotation().getSource().getCity();
        String srStreet = sc.getServiceQuotation().getSource().getStreet();
        String srBlockNo = sc.getServiceQuotation().getSource().getBlockNo();
        String srPostalCode = sc.getServiceQuotation().getSource().getPostalCode();

        String destCountry = sc.getServiceQuotation().getDest().getCountry();
        String destState = sc.getServiceQuotation().getDest().getState();
        String destCity = sc.getServiceQuotation().getDest().getCity();
        String destStreet = sc.getServiceQuotation().getDest().getStreet();
        String destBlockNo = sc.getServiceQuotation().getDest().getBlockNo();
        String destPostalCode = sc.getServiceQuotation().getDest().getPostalCode();

        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports");

        Map parametersMap = new HashMap();

        parametersMap.put("CLIENTNAME", clientName);
        parametersMap.put("CLIENTCONTACTNO", clientContactNo);

        parametersMap.put("CLIENTCOUNTRY", clientCountry);
        parametersMap.put("CLIENTSTATE", clientState);
        parametersMap.put("CLIENTCITY", clientCity);
        parametersMap.put("CLIENTSTREET", clientStreet);
        parametersMap.put("CLIENTBLOCKNO", clientBlockNo);
        parametersMap.put("CLIENTPOSTALCODE", clientPostalCode);

        parametersMap.put("PROVIDERNAME", providerName);
        parametersMap.put("PROVIDERCONTACTNO", providerContactNo);

        parametersMap.put("PROVIDERCOUNTRY", providerCountry);
        parametersMap.put("PROVIDERSTATE", providerState);
        parametersMap.put("PROVIDERCITY", providerCity);
        parametersMap.put("PROVIDERSTREET", providerStreet);
        parametersMap.put("PROVIDERBLOCKNO", providerBlockNo);
        parametersMap.put("PROVIDERPOSTALCODE", providerPostalCode);

        parametersMap.put("SRCOUNTRY", srCountry);
        parametersMap.put("SRSTATE", srState);
        parametersMap.put("SRCITY", srCity);
        parametersMap.put("SRSTREET", srStreet);
        parametersMap.put("SRBLOCKNO", srBlockNo);
        parametersMap.put("SRPOSTALCODE", srPostalCode);

        parametersMap.put("DESTCOUNTRY", destCountry);
        parametersMap.put("DESTSTATE", destState);
        parametersMap.put("DESTCITY", destCity);
        parametersMap.put("DESTSTREET", destStreet);
        parametersMap.put("DESTBLOCKNO", destBlockNo);
        parametersMap.put("DESTPOSTALCODE", destPostalCode);

        parametersMap.put("CREATIONTIME", creationTime);
        //  parametersMap.put("CONTRACTNAME",contractName);
        parametersMap.put("CONTRACTDESCRIP", contractDescrip);
        parametersMap.put("CONTRACTSTART", contractStart);
        parametersMap.put("CONTRACTEND", contractEnd);
        parametersMap.put("PRODUCTQUANTITY", productQuantity);
        parametersMap.put("TOTALPRICE", totalPrice);
        parametersMap.put("PRODUCTNAME", productName);

        parametersMap.put("PERISHABLE", perishable);
        parametersMap.put("PHARMACEUTICAL", pharmaceutical);
        parametersMap.put("HIGHVALUE", highValue);
        parametersMap.put("FLAMMABLE", flammable);

        parametersMap.put("WHNAME", whName);
        parametersMap.put("WHCONTACTNO", whContactNo);
        parametersMap.put("WHID", whId);
        parametersMap.put("WHPPCPD", whPricePerCapacityPerDay);
        parametersMap.put("WHDURATION", whDuration);
        parametersMap.put("WHQUANTITY", whQuantity);

        parametersMap.put("WHPRICE", whPrice);

        parametersMap.put("WHCOUNTRY", whCountry);
        parametersMap.put("WHSTATE", whState);
        parametersMap.put("WHCITY", whCity);
        parametersMap.put("WHSTREET", whStreet);
        parametersMap.put("WHBLOCKNO", whBlockNo);
        parametersMap.put("WHPOSTALCODE", whPostalCode);

        parametersMap.put("TRANPRICE", tranPrice);

        parametersMap.put("PATH1", "http://localhost:8080/MerLION-war/Reports/order2.jpg");
        parametersMap.put("PATH2", "http://localhost:8080/MerLION-war/Reports/banner9.png");
        //parametersMap.put("PATH2", "http://localhost:8080/MerLION-war/Reports/sign.png");

        String queryString = "SELECT "
                + "     COMPANY.`BLOCKNO` AS COMPANY_BLOCKNO "
                + "FROM  `COMPANY` COMPANY"
                + " WHERE COMPANY.`COMPANYNAME` = '" + userCompanyName + "'";
        Statement stmt = establishConnection().createStatement();
        ResultSet rset = stmt.executeQuery(queryString);
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);

        InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/Reports/serviceOrder.jasper");

        String reportName = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports") + System.getProperty("file.separator") + "serviceOrder.jasper";
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametersMap, jasperReports);

        System.out.println("after jasper print");

        String filename = realPath + System.getProperty("file.separator") + "serviceOrder.pdf";

        //Report saved in specified path
        JasperExportManager.exportReportToPdfFile(jasperPrint, filename);

        System.out.println("after jasper export");

        String email = "hanxiangyuxy@gmail.com";
        String subject = "Merlion Platform - Order " + sc.getId() + " From " + providerName;
                
        Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "");
            props.put("mail.smtp.port", "");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "false");
 
            //javax.mail.Authenticator auth = new SMTPAuthenticator();
 
            Session session = Session.getDefaultInstance(props);
            session.setDebug(false);
 
            Message msg = new MimeMessage(session);
            
            String fromAddress = "merlion.no.reply@gmail.com";
            String smtpHost = "smtp.gmail.com"; // set the mailbox to send from 
            int smtpPort = 587; // set port
            String username = "merlion.no.reply@gmail.com"; // mailbox address
            String password = "IS3102JiaYou"; // *password*
            //String subject = "test pdf attachement";
 
            if (msg != null)
            {
                //msg.setHeader("X-Mailer", ApplicationParameters.getEmailMailer());
                msg.setFrom(new InternetAddress(fromAddress));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
                msg.setSubject(subject);
 
                Date sentDate = new Date();
                msg.setSentDate(sentDate);
 
                Multipart multipart = new MimeMultipart();
                MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
                MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
                String content = "Dear " + clientName
                    + ": \nThank you very much for signing order with " + providerName + " !"
                    + "\nBelow is the information of your order:"
                    + "\n\tOrder ID: " + so.getId()
                    + "\n\tDate: " + so.getCreationTime()  
                        
                    + "\nPlease remember to make your payment.A receipt will be send to you after the saling party has confirmed the payment. Thank you."
                    + "\nNote: Please do not reply this email. If you have further questions, please go to the contact form page and submit there."
                    + "\nThank you."
                    + "\nRegards,"
                    + "\nMerLION Platform User Support";

                mimeBodyPart1.setText(content);
 
                DataSource dataSource = new FileDataSource(filename);
                mimeBodyPart2.setDataHandler(new DataHandler(dataSource));
                mimeBodyPart2.setFileName(filename);
 
                multipart.addBodyPart(mimeBodyPart1);
                multipart.addBodyPart(mimeBodyPart2);
 
                msg.setContent(multipart);
 
                Transport transport = session.getTransport("smtp"); // set protocol
                transport.connect(smtpHost, smtpPort, username, password); // connect mail server
                transport.sendMessage(msg, msg.getAllRecipients()); // send email
                transport.close(); // close connection
                System.out.println("send email ok");
                this.faceMsg("Email sent successfully!");
            }
    }

    public void retrieveServiceContract(ServiceContract sc) throws IOException {
        System.out.println("Entering ServiceOrderManagedBean: retrieveServiceContract");
        this.sc = sc;
        redirectToPage("createServiceOrder.xhtml");
        System.out.println("End of ServiceOrderManagedBean: retrieveServiceContract");
    }

    public void createServiceOrder() throws IOException {
        System.out.println("Entering ServiceOrderManagedBean: createServiceOrder");

        Timestamp startTime = new Timestamp(startDate.getTime());
        Timestamp endTime = new Timestamp(endDate.getTime());
        try {
            Product product = sc.getServiceQuotation().getProduct();
            Double quantity = sc.getServiceQuotation().getEstimatedQuantity();
            Double price = sc.getServiceQuotation().getTotalPrice();
            Location source = sc.getServiceQuotation().getSource();
            Location dest = sc.getServiceQuotation().getDest();
            Long warehouseId = sc.getServiceQuotation().getWarehouse().getId();
            String providerCompanyName = sc.getProvider().getCompanyName();
            String clientCompanyName = sc.getClient().getCompanyName();
            Boolean perishable = sc.getServiceQuotation().isPerishable();
            Boolean flammable = sc.getServiceQuotation().isFlammable();
            Boolean pharmaceutical = sc.getServiceQuotation().isPharmaceutical();
            Boolean highValue = sc.getServiceQuotation().isHighValue();
            Long serviceContractId = sc.getId();
            Long soId = soml.createServiceOrder(providerCompanyName, clientCompanyName, startTime, endTime, price, description, product, quantity, source, dest, warehouseId,
                    isToEstablishTransOrder, isToEstablishWarehouseOrder, perishable, flammable, pharmaceutical, highValue, serviceContractId, aggregateOrNot);
            so = soml.retrieveServiceOrder(soId);
            statusMessage = "Add New Service Order: New Service Order ID is " + soId;
            this.displayFaceMessage("Add New Service Order: New Service Order ID is " + soId);
            this.redirectToPage("chooseSourceWarehouse.xhtml");
            System.out.println("New service Order creation complete.");
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (ServiceOrderStartAfterContractExpireException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderStartAfterContractExpireException: " + statusMessage);
        } catch (ServiceOrderStartBeforeContractCommmenceException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderStartBeforeContractCommmenceException: " + statusMessage);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (InsufficientInventoryException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("InsufficientInventoryException: " + statusMessage);
        } 

        System.out.println("End of ServiceOrderManagedBean: createServiceOrder");
    }

    public List<Warehouse> retrieveWarehouseContainSpecificProduct() {
        List<Warehouse> result = new ArrayList<>();
        Product product = so.getProduct();
        String productName = product.getProductName();
        String productOwner = product.getOwnerCompanyName();

        try {
            result = fml.retrieveWarehouseListForSpecificProduct(productName, productOwner);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
        } catch (RawMaterialNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("RawMaterialNotExistException: " + statusMessage);
        } catch (MultipleRawMaterialWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MultipleRawMaterialWithSameNameException: " + statusMessage);
        }
        return result;
    }

    public void setSourceWarehouseForServiceOrder(Warehouse sourceWarehouse) {
        System.out.println("Enter ServiceOrderManagedBean: setSourceWarehouseForServiceOrder");
        soml.setSourceWarehouse(so, sourceWarehouse);
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        System.out.println("End of ServiceOrderManagedBean: setSourceWarehouseForServiceOrder");
    }

    public Double getProductQuantityForSpecificWarehouse(Warehouse wh) {
        Product product = so.getProduct();
        Double result = 0.0;
        if (product.getProductType().equals("FinishedGood")) {
            FinishedGood fg = (FinishedGood) product;
            result = wh.getFinishedGoodMap().get(fg);
        } else {
            RawMaterial rm = (RawMaterial) product;
            result = wh.getRawMaterialMap().get(rm);
        }
        return result;
    }

    public Integer getWarehouseListSize(List<Warehouse> list) {
        return list.size();
    }

    public List<ServiceOrder> retrieveOutsourcingServiceOrderList() throws IOException {
        List<ServiceOrder> soList = new ArrayList<>();
        try {
            soList = soml.retrieveOutsourcingServiceOrderList(userCompanyName);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return soList;
    }

    public void retrieveOutsourcingServiceOrder(ServiceOrder so) throws IOException {
        System.out.println("Enter ServiceOrderManagedBean: retrieveOutsourcingServiceOrder");
        interPrice = "S$ "+so.getPrice().toString();
        this.so = so;
        if (so.getIsToOutsource()) {
            retrieveOutsourceToServiceOrder();
        }
        if (so.getIsFromOutsourcing()) {
            retrieveOutsourceFromServiceOrder();
        }
        redirectToPage("viewOutgoingServiceOrder.xhtml");
        System.out.println("End of ServiceOrderManagedBean: retrieveOutsourcingServiceOrder");
    }

    public List<ServiceOrder> retrieveOwningServiceOrderList() throws IOException {
        List<ServiceOrder> soList = new ArrayList<>();
        try {
            soList = soml.retrieveOwningServiceOrderList(userCompanyName);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return soList;
    }

    public void approveServiceOrder(ServiceOrder serviceOrder) {
        System.out.println("Entering ServiceOrderManagedBean: approveServiceOrder");
        Long soId = serviceOrder.getId();
        try {
            soml.approveServiceOrder(serviceOrder.getId());
            statusMessage = "Service Order is approved and invoice has been sent successful!";
            displayFaceMessage(statusMessage);
            System.out.println("Approve ServiceOrder " + soId + " complete.");
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (ServiceOrderAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyApprovedException: " + statusMessage);
        }
        System.out.println("End of ServiceOrderManagedBean: approveServiceOrder");
    }

    public void approveServiceOrder() {
        System.out.println("Entering ServiceOrderManagedBean: approveServiceOrder");
        Long soId = so.getId();
        try {
            soml.approveServiceOrder(so.getId());
            so.setApprovedOrNot(Boolean.TRUE);
            statusMessage = "Approval successful!";
            displayFaceMessage(statusMessage);
            System.out.println("Approve ServiceOrder " + soId + " complete.");
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotExistException: " + statusMessage);
        } catch (ServiceOrderAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyApprovedException: " + statusMessage);
        }
        System.out.println("End of ServiceOrderManagedBean: approveServiceOrder");
    }

    public void createOutsourceOrderForPartnerCompanyUseOnly() {
        System.out.println("Entering ServiceOrderManagedBean: createOutsourceOrderForPartnerCompanyUseOnly");
        try {
            outsourceToOrder = soml.createOutsourceServiceOrder(so, sc, description);
            statusMessage = "Order Creation Successful";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceOrderNotApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotApprovedException: " + statusMessage);
        } catch (ServiceOrderNotPaidException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderNotPaidException: " + statusMessage);
        } catch (ServiceOrderAlreadyOutsourcedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyOutsourcedException: " + statusMessage);
        } catch (LocationNotMatchException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("LocationNotMatchException: " + statusMessage);
        } catch (ExceedMaxCapacityException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ExceedMaxCapacityException: " + statusMessage);
        }
        System.out.println("End of ServiceOrderManagedBean: createOutsourceOrderForPartnerCompanyUseOnly");
    }

    public ServiceOrder retrieveOutsourceToServiceOrder() {
        outsourceToOrder = new ServiceOrder();
        try {
            outsourceToOrder = soml.retrieveServiceOrder(so.getToServiceOrderId());
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyOutsourcedException: " + statusMessage);
        }
        return outsourceToOrder;
    }

    public ServiceOrder retrieveOutsourceFromServiceOrder() {
        outsourceFromOrder = new ServiceOrder();
        try {
            outsourceFromOrder = soml.retrieveServiceOrder(so.getFromServiceOrderId());
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceOrderAlreadyOutsourcedException: " + statusMessage);
        }
        return outsourceFromOrder;
    }

    public void retrieveOwningServiceOrder(ServiceOrder so) throws IOException {
        System.out.println("Enter ServiceOrderManagedBean: retrieveOwningServiceOrder");
        this.so = so;
        interPrice = "S$ "+so.getPrice().toString();
        if (so.getIsToOutsource()) {
            retrieveOutsourceToServiceOrder();
        }
        if (so.getIsFromOutsourcing()) {
            retrieveOutsourceFromServiceOrder();
        }

        redirectToPage("viewServiceOrder.xhtml");
        System.out.println("End of ServiceOrderManagedBean: retrieveOwningServiceOrder");
    }

    public Boolean verifyCapacityResult() {
        Boolean result = true;
        ServiceQuotation sq = so.getServiceContract().getServiceQuotation();
        Long sqId = sq.getId();
        try {
            if (sqm.verifyServiceQuotationTransportation(sqId) && sqm.verifyServiceQuotationWarehouse(sqId, 0.0)) {
                result = false;
            }
        } catch (ServiceQuotationNotExistException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    public Boolean retrieveWarehouseServiceFulfilledOrNot(ServiceOrder so) {
        Boolean result = Boolean.FALSE;
        try {
            if (so.getIsToEstablishWarehouseOrder()) {
                if (so.getIsToOutsource()) {
                    ServiceOrder outSo = soml.retrieveServiceOrder(so.getToServiceOrderId());
                    if(outSo.getEstablishedWarehouseOrderOrNot())
                        result = outSo.isFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
                else{
                    if(so.getEstablishedWarehouseOrderOrNot())
                        result = so.getWarehouseOrder().isFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
            }
        } catch (ServiceOrderNotExistException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public Boolean retrieveTransServiceFulfilledOrNot(ServiceOrder so) {
        Boolean result = Boolean.FALSE;
        try {
            if (so.getIsToEstablishTransOrder()) {
                if (so.getIsToOutsource()) {
                    ServiceOrder outSo = soml.retrieveServiceOrder(so.getToServiceOrderId());
                    if(outSo.getEstablishedTransOrderOrNot())
                        result = outSo.getTransOrder().getFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
                else{
                    if(so.getEstablishedTransOrderOrNot())
                        result = so.getTransOrder().getFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
            }
        } catch (ServiceOrderNotExistException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public Boolean retrieveWarehouseServiceFulfilledOrNot() {
        Boolean result = Boolean.FALSE;
        try {
            if (so.getIsToEstablishWarehouseOrder()) {
                if (so.getIsToOutsource()) {
                    ServiceOrder outSo = soml.retrieveServiceOrder(so.getToServiceOrderId());
                    if(outSo.getEstablishedWarehouseOrderOrNot())
                        result = outSo.getWarehouseOrder().isFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
                else{
                    if(so.getEstablishedWarehouseOrderOrNot())
                        result = so.getWarehouseOrder().isFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
            }
        } catch (ServiceOrderNotExistException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public Boolean retrieveTransServiceFulfilledOrNot() {
        Boolean result = Boolean.FALSE;
        try {
            if (so.getIsToEstablishTransOrder()) {
                if (so.getIsToOutsource()) {
                    ServiceOrder outSo = soml.retrieveServiceOrder(so.getToServiceOrderId());
                    if(outSo.getEstablishedTransOrderOrNot())
                        result = outSo.getTransOrder().getFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
                else{
                    if(so.getEstablishedTransOrderOrNot())
                        result = so.getTransOrder().getFulfilledOrNot();
                    else
                        result = Boolean.FALSE;
                }
            }
        } catch (ServiceOrderNotExistException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public void goToTrackingNumber(ServiceOrder so) {
        trackingNumber = so.getTransOrder().getTrackingNumber();
        RequestContext.getCurrentInstance().execute("PF('Dialog2').show()");
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
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

    public void redirectToViewServiceOrderList() throws IOException {
        redirectToPage("viewServiceOrderList.xhtml");
    }

    public void redirectToViewOutgoingServiceOrderList() throws IOException {
        redirectToPage("viewOutgoingServiceOrderList.xhtml");
    }

    public void addIsToEstablishTransOrderMessage() {
        String summary = isToEstablishTransOrder ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addIsToEstablishWarehouseOrderMessage() {
        String summary = isToEstablishWarehouseOrder ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addAggregateOrNotMessage() {
        String summary = aggregateOrNot ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public ServiceContractManagementSessionLocal getScmsl() {
        return scmsl;
    }

    public void setScmsl(ServiceContractManagementSessionLocal scmsl) {
        this.scmsl = scmsl;
    }

    public CompanyManagementSessionBeanLocal getCmsbl() {
        return cmsbl;
    }

    public void setCmsbl(CompanyManagementSessionBeanLocal cmsbl) {
        this.cmsbl = cmsbl;
    }

    public ServiceOrderManagementLocal getSoml() {
        return soml;
    }

    public void setSoml(ServiceOrderManagementLocal soml) {
        this.soml = soml;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Company getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(Company userCompany) {
        this.userCompany = userCompany;
    }

    public String getUserCompanyName() {
        return userCompanyName;
    }

    public void setUserCompanyName(String userCompanyName) {
        this.userCompanyName = userCompanyName;
    }

    public String getCounterPartCompanyName() {
        return counterPartCompanyName;
    }

    public void setCounterPartCompanyName(String counterPartCompanyName) {
        this.counterPartCompanyName = counterPartCompanyName;
    }

    public Long getUserCompanyId() {
        return userCompanyId;
    }

    public void setUserCompanyId(Long userCompanyId) {
        this.userCompanyId = userCompanyId;
    }

    public ServiceContract getSc() {
        return sc;
    }

    public void setSc(ServiceContract sc) {
        this.sc = sc;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsToEstablishTransOrder() {
        return isToEstablishTransOrder;
    }

    public void setIsToEstablishTransOrder(Boolean isToEstablishTransOrder) {
        this.isToEstablishTransOrder = isToEstablishTransOrder;
    }

    public Boolean getIsToEstablishWarehouseOrder() {
        return isToEstablishWarehouseOrder;
    }

    public void setIsToEstablishWarehouseOrder(Boolean isToEstablishWarehouseOrder) {
        this.isToEstablishWarehouseOrder = isToEstablishWarehouseOrder;
    }

    public Boolean getAggregateOrNot() {
        return aggregateOrNot;
    }

    public void setAggregateOrNot(Boolean aggregateOrNot) {
        this.aggregateOrNot = aggregateOrNot;
    }

    public ServiceOrder getSo() {
        return so;
    }

    public void setSo(ServiceOrder so) {
        this.so = so;
    }

    public Warehouse getSourceWarehouse() {
        return sourceWarehouse;
    }

    public void setSourceWarehouse(Warehouse sourceWarehouse) {
        this.sourceWarehouse = sourceWarehouse;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ServiceOrder getOutsourceToOrder() {
        return outsourceToOrder;
    }

    public void setOutsourceToOrder(ServiceOrder outsourceToOrder) {
        this.outsourceToOrder = outsourceToOrder;
    }

    public ServiceOrder getOutsourceFromOrder() {
        return outsourceFromOrder;
    }

    public void setOutsourceFromOrder(ServiceOrder outsourceFromOrder) {
        this.outsourceFromOrder = outsourceFromOrder;
    }

    public FacilityManagementLocal getFml() {
        return fml;
    }

    public void setFml(FacilityManagementLocal fml) {
        this.fml = fml;
    }

    public ServiceQuotationManagementSessionLocal getSqm() {
        return sqm;
    }

    public void setSqm(ServiceQuotationManagementSessionLocal sqm) {
        this.sqm = sqm;
    }

    public String getInterPrice() {
        return interPrice;
    }

    public void setInterPrice(String interPrice) {
        this.interPrice = interPrice;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

}
