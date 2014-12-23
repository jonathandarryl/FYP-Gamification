/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceContractRenewNotification;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceContractManagementSessionLocal;
import static CRMManagedBean.ServiceCatalogManagedBean.establishConnection;
import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Invoice;
import OES.entity.Product;
import OESManagedBean.InvoiceAndPaymentManagedBean;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.InvoiceNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceContractAlreadySignedException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
import util.exception.ServiceQuotationAlreadyEstablishedContractException;
import util.exception.ServiceQuotationNotApprovedException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ServiceQuotationStartAfterCatalogExpireException;
import util.exception.ServiceQuotationStartBeforeCatalogCommmenceException;

/**
 *
 * @author songhan
 */
@Named(value = "serviceContractManagedBean")
@SessionScoped
public class ServiceContractManagedBean implements Serializable{

    /**
     * Creates a new instance of NewServiceContractManagedBean
     */
    public ServiceContractManagedBean() {
    }

    @EJB
    private ServiceContractManagementSessionLocal scmsl;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;

    private String statusMessage;
    private Company userCompany;
    private String userCompanyName;
    private String counterPartCompanyName;
    private Long userCompanyId;

    private ServiceQuotation sq;
    private ServiceContract sc;

    private Date startDate;
    private Date endDate;
    private String description;
    
    private String currency = "";
    private Double price;
    private String ps;

    @PostConstruct
    public void init() {
        userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    public void changePrice(int i){
        price = sc.getServiceQuotation().getTotalPrice();
        switch(i){
            case 0:
                price *= 0.8;
                ps = "S$ "+price.toString();
                break;
            case 1:
                price *= 0.8;
                ps = "$ "+price.toString();
                break;
            case 2:
                price *= 4.75;
                ps = "RMB "+price.toString();
                break;
            case 3:
                price *= 89.28;
                ps = "Yen "+price.toString();
                break; 
            case 4:
                price *= 0.62;
                ps = "Euro "+price.toString();
                break;  
            default:
                break;
        }
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

    public List<ServiceQuotation> retrieveServiceQuotationList() {
        List<ServiceQuotation> result = new ArrayList<>();
        try {
            result = scmsl.retrieveSuitableServiceQuotationList(userCompanyName);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return result;
    }

    public void createServiceContract(ServiceQuotation serviceQuotation) {
        System.out.println("Entering ServiceContractManagedBean: createServiceContract");
        try {
            Long sqId = serviceQuotation.getId();
            scmsl.createServiceContract(sqId);
            statusMessage = "Contract creation successful!";
            displayFaceMessage(statusMessage);
            System.out.println("New service contract creation complete.");
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationNotApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotApprovedException: " + statusMessage);
        } catch (ServiceQuotationAlreadyEstablishedContractException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyEstablishedContractException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (MembershipSchemaNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MembershipSchemaNotExistException: " + statusMessage);
        } catch (Exception ex) {
            statusMessage = "Please contact your company administrator to create a basic tier.";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }

        System.out.println("End of createServiceContract.");
    }

    public void retrieveServiceQuotation(ServiceQuotation sq) throws IOException {
        System.out.println("Entering ServiceContractManagedBean: retrieveServiceQuotation");
        this.sq = sq;
        redirectToPage("viewServiceQuotationForContractCreation.xhtml");
        System.out.println("End of ServiceContractManagedBean: retrieveServiceQuotation");
    }

    public void createServiceContractInViewPage() {
        System.out.println("Entering ServiceContractManagedBean: createServiceContractInViewPage");
        try {
            Long sqId = sq.getId();
            Long scId = scmsl.createServiceContract(sqId);
            statusMessage = "Contract " + scId + " creation successful!";
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("New service contract creation complete.");
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationNotApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotApprovedException: " + statusMessage);
        } catch (ServiceQuotationAlreadyEstablishedContractException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyEstablishedContractException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (MembershipSchemaNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MembershipSchemaNotExistException: " + statusMessage);
        }

        System.out.println("End of ServiceContractManagedBean: createServiceContractInViewPage.");
    }

    public List<ServiceContract> retrieveServiceContractList() throws IOException {
        List<ServiceContract> scList = new ArrayList<>();
        try {
            scList = scmsl.retrieveServiceContractList(userCompany.getCompanyName());
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return scList;
    }

    public void retrieveServiceContract(ServiceContract sc) throws IOException {
        System.out.println("Entering ServiceContractManagedBean: retrieveServiceContract");
        this.sc = sc;
        price = sc.getServiceQuotation().getTotalPrice();
        ps = "S$ "+price.toString();
        description = sc.getServiceQuotation().getDescription();
        startDate = sc.getServiceQuotation().getStartDate();
        endDate = sc.getServiceQuotation().getEndDate();
        redirectToPage("viewAndUpdateServiceContract.xhtml");
        System.out.println("End of ServiceContractManagedBean: retrieveServiceContract");
    }

    
    public void exportToPDF (ServiceContract sc) throws IOException, JRException, ClassNotFoundException, SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException, AddressException, MessagingException
    
    {
        
        //Send To client 
        String clientName = sc.getServiceQuotation().getClientCompany().getCompanyName();
        String clientCountry = sc.getServiceQuotation().getClientCompany().getLocation().getCountry();
        String clientState = sc.getServiceQuotation().getClientCompany().getLocation().getState();
        String clientCity = sc.getServiceQuotation().getClientCompany().getLocation().getCity();
        String clientStreet = sc.getServiceQuotation().getClientCompany().getLocation().getStreet();
        String clientBlockNo = sc.getServiceQuotation().getClientCompany().getLocation().getBlockNo();
        String clientPostalCode = sc.getServiceQuotation().getClientCompany().getLocation().getPostalCode();
        String clientContactNo = sc.getServiceQuotation().getClientCompany().getContactNo();
//        sc.getServiceQuotation().getClientCompany().getDepartment().get(0).getAccount().get(0).getUser().getEmail();
     
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
        Timestamp creationTime = sc.getCreationTime();
     //   String contractName = sc.getServiceQuotation().getServiceCatalog().getServiceName();
        String contractDescrip = sc.getServiceQuotation().getDescription();
        Timestamp contractStart = sc.getServiceQuotation().getStartDate();
        Timestamp contractEnd = sc.getServiceQuotation().getEndDate();
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

        parametersMap.put("CLIENTNAME",clientName);
        parametersMap.put("CLIENTCONTACTNO",clientContactNo);
        
        parametersMap.put("CLIENTCOUNTRY",clientCountry);
        parametersMap.put("CLIENTSTATE",clientState);
        parametersMap.put("CLIENTCITY",clientCity);
        parametersMap.put("CLIENTSTREET",clientStreet);
        parametersMap.put("CLIENTBLOCKNO",clientBlockNo);
        parametersMap.put("CLIENTPOSTALCODE",clientPostalCode);
        
        parametersMap.put("PROVIDERNAME",providerName);
        parametersMap.put("PROVIDERCONTACTNO",providerContactNo);
        
        parametersMap.put("PROVIDERCOUNTRY",providerCountry);
        parametersMap.put("PROVIDERSTATE",providerState);
        parametersMap.put("PROVIDERCITY",providerCity);
        parametersMap.put("PROVIDERSTREET",providerStreet);
        parametersMap.put("PROVIDERBLOCKNO",providerBlockNo);
        parametersMap.put("PROVIDERPOSTALCODE",providerPostalCode);
        
        parametersMap.put("SRCOUNTRY",srCountry);
        parametersMap.put("SRSTATE",srState);
        parametersMap.put("SRCITY",srCity);
        parametersMap.put("SRSTREET",srStreet);
        parametersMap.put("SRBLOCKNO",srBlockNo);
        parametersMap.put("SRPOSTALCODE",srPostalCode);

        parametersMap.put("DESTCOUNTRY",destCountry);
        parametersMap.put("DESTSTATE",destState);
        parametersMap.put("DESTCITY",destCity);
        parametersMap.put("DESTSTREET",destStreet);
        parametersMap.put("DESTBLOCKNO",destBlockNo);
        parametersMap.put("DESTPOSTALCODE",destPostalCode);

        parametersMap.put("CREATIONTIME",creationTime);
      //  parametersMap.put("CONTRACTNAME",contractName);
        parametersMap.put("CONTRACTDESCRIP",contractDescrip);
        parametersMap.put("CONTRACTSTART",contractStart);
        parametersMap.put("CONTRACTEND",contractEnd);
        parametersMap.put("PRODUCTQUANTITY",productQuantity);
        parametersMap.put("TOTALPRICE",totalPrice);
        parametersMap.put("PRODUCTNAME",productName);
       
        parametersMap.put("PERISHABLE",perishable);     
        parametersMap.put("PHARMACEUTICAL",pharmaceutical);
        parametersMap.put("HIGHVALUE",highValue);
        parametersMap.put("FLAMMABLE",flammable);
        
        parametersMap.put("WHNAME",whName);
        parametersMap.put("WHCONTACTNO",whContactNo);
        parametersMap.put("WHID",whId);
        parametersMap.put("WHPPCPD",whPricePerCapacityPerDay);
        parametersMap.put("WHDURATION",whDuration);
        parametersMap.put("WHQUANTITY",whQuantity);
        
        parametersMap.put("WHPRICE",whPrice);
        
        parametersMap.put("WHCOUNTRY",whCountry);
        parametersMap.put("WHSTATE",whState);
        parametersMap.put("WHCITY",whCity);
        parametersMap.put("WHSTREET",whStreet);
        parametersMap.put("WHBLOCKNO",whBlockNo);
        parametersMap.put("WHPOSTALCODE",whPostalCode);
      
        parametersMap.put("TRANPRICE",tranPrice);
     
        parametersMap.put("PATH1", "http://localhost:8080/MerLION-war/Reports/c1.jpg");
        parametersMap.put("PATH2", "http://localhost:8080/MerLION-war/Reports/banner9.png");
        parametersMap.put("PATH3", "http://localhost:8080/MerLION-war/Reports/sign.png");

        String queryString= "SELECT " +

                "     COMPANY.`BLOCKNO` AS COMPANY_BLOCKNO " +                 

                "FROM  `COMPANY` COMPANY"    
                + " WHERE COMPANY.`COMPANYNAME` = '"+userCompanyName + "'"
              ;
        Statement stmt = establishConnection().createStatement();
        ResultSet rset = stmt.executeQuery(queryString);
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);

        InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/Reports/serviceContract.jasper");

        String reportName = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports") + System.getProperty("file.separator") + "serviceContract.jasper";
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream , parametersMap, jasperReports);

        System.out.println("after jasper print");

        String filename =realPath + System.getProperty("file.separator") +"serviceContract.pdf";

        //Report saved in specified path
        JasperExportManager.exportReportToPdfFile(jasperPrint,filename);

        System.out.println("after jasper export");
        
        String email = "hanxiangyuxy@gmail.com";
        String subject = "Merlion Platform - Contract " + sc.getId() + " From " + providerName;
                
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
                    + ": \nThank you very much for signing contract with " + providerName + " !"
                    + "\nBelow is the contract for your order:"
                    + "\n\tContract ID: " + sc.getId()
                    + "\n\tDate: " + sc.getCreationTime()  
                        
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
    
    
    
    
    
    public void updateServiceContract() {
        System.out.println("Entering ServiceContractManagedBean: updateServiceContract");
        try {
            scmsl.updateServiceContract(sc, description, startDate, endDate);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (RoutesNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("RoutesNotExistException: " + statusMessage);
        } catch (ServiceQuotationStartBeforeCatalogCommmenceException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationStartBeforeCatalogCommmenceException: " + statusMessage);
        } catch (ServiceQuotationStartAfterCatalogExpireException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationStartAfterCatalogExpireException: " + statusMessage);
        } catch (NoVehiclesAssignedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("NoVehiclesAssignedException: " + statusMessage);
         } catch (NoPossibleRoutesException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("NoPossibleRoutesException: " + statusMessage);
         }
        statusMessage = "Contract " + sc.getId() + " update successful!";
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        System.out.println(statusMessage);
        System.out.println("End of ServiceContractManagedBean: updateServiceContract");
    }

    public List<ServiceContract> retrieveOutgoingServiceContractList() throws IOException {
        List<ServiceContract> scList = new ArrayList<>();
        try {
            scList = scmsl.retrieveOutsourcingServiceContractList(userCompany.getCompanyName());
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return scList;
    }

    public void retrieveOutgoingServiceContract(ServiceContract sc) throws IOException {
        System.out.println("Entering ServiceContractManagedBean: retrieveOutgoingServiceContract");
        this.sc = sc;
        price = sc.getServiceQuotation().getTotalPrice();
        ps = "S$ " + price.toString();
        description = sc.getServiceQuotation().getDescription();
        startDate = sc.getServiceQuotation().getStartDate();
        endDate = sc.getServiceQuotation().getEndDate();
        redirectToPage("viewAndUpdateOutgoingServiceContract.xhtml");
        System.out.println("End of ServiceContractManagedBean: retrieveOutgoingServiceContract");
    }
    
    public void updateOutgoingServiceContract() {
        System.out.println("Entering ServiceContractManagedBean: updateOutgoingServiceContract");
        try {
            scmsl.updateServiceContract(sc, description, startDate, endDate);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (RoutesNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("RoutesNotExistException: " + statusMessage);
        } catch (ServiceQuotationStartBeforeCatalogCommmenceException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationStartBeforeCatalogCommmenceException: " + statusMessage);
        } catch (ServiceQuotationStartAfterCatalogExpireException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationStartAfterCatalogExpireException: " + statusMessage);
        } catch (NoVehiclesAssignedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("NoVehiclesAssignedException: " + statusMessage);
         } catch (NoPossibleRoutesException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("NoPossibleRoutesException: " + statusMessage);
         }
        statusMessage = "Contract " + sc.getId() + " update successful!";
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        System.out.println(statusMessage);
        System.out.println("End of ServiceContractManagedBean: updateOutgoingServiceContract");
    }
    
    public void signOutsourcingContract(){
        System.out.println("Entering ServiceContractManagedBean: signServiceContract");
        try {
            Long scId = sc.getId();
            scmsl.signOutsourcingContract(scId);
            statusMessage = "Service Contract "+scId+" has been signed successfully!";
            this.displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg1').show()");
            System.out.println(statusMessage);
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (ServiceContractAlreadySignedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractAlreadySignedException: " + statusMessage);
        }
        System.out.println("End of ServiceContractManagedBean: signServiceContract");
    }

    public void sendRenewReminder(ServiceContract serviceContract) {
        System.out.println("Entering ServiceContractManagedBean: sendPaymentReminder");
        try {
            scmsl.sendContractRenewNotification(serviceContract.getId());
            statusMessage = "Reminder sent successful!";
            this.displayFaceMessage(statusMessage);
            System.out.println("Reminder sent successful");
        } catch (ServiceContractNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractNotExistException: " + statusMessage);
        } catch (ServiceContractNotSignedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceContractNotSignedException: " + statusMessage);
        }
        System.out.println("End of ServiceContractManagedBean: sendPaymentReminder.");
    }

    public List<ServiceContractRenewNotification> retrieveIncomingReminderList() throws IOException {
        List<ServiceContractRenewNotification> reminderList = new ArrayList<>();
        try {
            reminderList = scmsl.getIncomingReminderList(userCompanyName);
            System.out.println("Retrieve incoming reminder list successfully!");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return reminderList;
    }

    public List<ServiceContractRenewNotification> retrieveOutgoingReminderList() throws IOException {
        List<ServiceContractRenewNotification> reminderList = new ArrayList<>();
        try {
            reminderList = scmsl.getOutgoingReminderList(userCompanyName);
            System.out.println("Retrieve outgoing reminder list successfully!");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return reminderList;
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

    public void redirectToViewServiceContractList() throws IOException {
        redirectToPage("viewServiceContractList.xhtml");
    }
    
    public void redirectToViewOutgoingServiceContractList() throws IOException {
        redirectToPage("viewOutgoingServiceContractList.xhtml");
    }

    public void redirectToViewServiceQuotationList() throws IOException {
        redirectToPage("viewServiceQuotationList.xhtml");
    }

    public void redirectToViewOutgoingServiceQuotationList() throws IOException {
        redirectToPage("viewOutgoingServiceQuotationList.xhtml");
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

    public ServiceQuotation getSq() {
        return sq;
    }

    public void setSq(ServiceQuotation sq) {
        this.sq = sq;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
   
}
