/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OESManagedBean;

import static CRMManagedBean.ServiceCatalogManagedBean.establishConnection;
import Common.entity.Company;
import Common.entity.Location;
import OES.entity.Invoice;
import OES.entity.Product;
import OES.entity.Receipt;
import OES.entity.SalesOrder;
import OES.session.InvoiceAndPaymentManagementLocal;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
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
import util.exception.InvoiceNotExistException;
import util.exception.ReceiptNotExistException;
import util.exception.SalesOrderNotBeenFulfilledException;
import util.exception.SalesOrderNotExistException;
import util.session.SendEmail;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "invoiceAndPaymentManagedBean")
@SessionScoped
public class InvoiceAndPaymentManagedBean {

    @EJB
    InvoiceAndPaymentManagementLocal ipm;

    private String country;
    private String state;
    private String city;
    private String street;
    private String blockNo;
    private String postalCode;
    private Long orderID, invoiceID, reID;
    private Date startDate, endDate;
    private Invoice invoice;
    private Receipt receipt;
    private SendEmail sendEmail;
    private String email, customerCompany, ownerCompany;
    private ArrayList<Invoice> inList = new ArrayList();
    private ArrayList<Receipt> reList = new ArrayList();
    private Company select_Company;
    private String billingNo;
    private SalesOrder salesOrder;
    private Product product;
    
    public InvoiceAndPaymentManagedBean() {
    }

    public String getCountry() {
        return country;
    }

    public Long getInvoiceID() {
        return invoiceID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getReID() {
        return reID;
    }

    public void setReID(Long reID) {
        this.reID = reID;
    }

    public ArrayList<Receipt> getReList() {
        return reList;
    }

    public void setReList(ArrayList<Receipt> reList) {
        this.reList = reList;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Invoice> getInList() {
        return inList;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public void setInList(ArrayList<Invoice> inList) {
        this.inList = inList;
    }

    public void setInvoiceID(Long invoiceID) {
        this.invoiceID = invoiceID;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
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

    public String getCustomerCompany() {
        return customerCompany;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
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

    public String getBillingNo() {
        return billingNo;
    }

    public void setBillingNo(String billingNo) {
        this.billingNo = billingNo;
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

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void createInvoice() throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException {
        try {
            Location loc = new Location(country, state, city, street, blockNo, postalCode);
            ipm.createInvoice(orderID, loc);
            this.displayFaceMessage("Invoice has been successfully created!");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("This sales order does not exist");
        } catch (SalesOrderNotBeenFulfilledException e) {
            this.displayFaceMessage("This sales order has not been fulfilled");
        }
    }

    public void retrieveInvoice(Long inID) throws InvoiceNotExistException, IOException {
        try {
            invoiceID = inID;
            invoice = ipm.retrieveInvoice(invoiceID);
       //     FacesContext fc = FacesContext.getCurrentInstance();
            //      ExternalContext ec = fc.getExternalContext();
            //    ec.redirect("DisplayInvoice.xhtml");
        } catch (InvoiceNotExistException e) {
            this.displayFaceMessage("This invoice does not exist");
        }
    }

    public void retrieveInvoiceList() throws InvoiceNotExistException, IOException {
        inList.clear();
        select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        try {
            System.out.println("Enter retrieveInvoiceList try catch");
            inList.addAll(ipm.retrieveAllInvoiceList(ownerCompany));
            System.out.println("invoices are successfully added into inList");
        } catch (InvoiceNotExistException e) {
            System.out.println(e.getMessage());
            this.displayFaceMessage("This invoice does not exist");
        }
    }

    public void preConfirmPayment(Long inID) {
        invoiceID = inID;
    }

    public void confirmPayment() throws InvoiceNotExistException {
        try {
            ipm.confirmPayment(invoiceID, billingNo);
            RequestContext.getCurrentInstance().execute("PF('paymentdlg').hide()");
            this.displayFaceMessage("Payment has been successfully confirmed!");
        } catch (InvoiceNotExistException e) {
            this.displayFaceMessage("This invoice does not exist");
        }
    }

    public void createReceipt(Long inID) throws SalesOrderNotExistException, SalesOrderNotBeenFulfilledException {
        try {
            ipm.createReceipt(inID);
            this.displayFaceMessage("Receipt has been successfully created");
        } catch (SalesOrderNotExistException e) {
            this.displayFaceMessage("Sales order does not exist");
        } catch (SalesOrderNotBeenFulfilledException e) {
            this.displayFaceMessage("Sales order has not been fulfilled");
        } catch (InvoiceNotExistException ex) {
            this.displayFaceMessage("Invoice does not exist");
        }
    }

    public void retrieveReceipt(Long receiptID) throws ReceiptNotExistException, IOException {
        try {
            reID = receiptID;
            receipt = ipm.retrieveReceipt(reID);
        } catch (ReceiptNotExistException e) {
            this.displayFaceMessage("Receipt does not exist");
        }
    }

    public void retrieveReceiptList() throws IOException, ReceiptNotExistException {
        reList.clear();
        select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
        ownerCompany = select_Company.getCompanyName();
        try {
            reList.addAll(ipm.retrieveAllReceiptList(ownerCompany));
        } catch (ReceiptNotExistException e) {
            this.displayFaceMessage("Receipt does not exist");
        }
    }
    
       public void exportToPDFReceipt (Long receiptID) throws IOException, JRException, ClassNotFoundException, SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException{      
        try {
            Invoice invoice = ipm.retrieveReceipt(receiptID).getInvoice();           
            Long invoiceId = invoice.getId();           
            customerCompany = invoice.getSalesOrder().getSalesQuotation().getCustomer().getName();           
            Double totalPrice = invoice.getSalesOrder().getSalesQuotation().getTotalPrice();
            Double singlePrice = ((Product) invoice.getSalesOrder().getProductList().toArray()[0]).getProductPrice();
            Double quantityDouble = totalPrice/singlePrice;           
            Integer quantity = quantityDouble.intValue();          
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports");
            
            Map parametersMap = new HashMap();
            parametersMap.put("CompanyName",ownerCompany);
            parametersMap.put("QUANTITY",quantity);
            
            parametersMap.put("PATH1", "http://localhost:8080/MerLION-war/Reports/payment.png");
            parametersMap.put("PATH2", "http://localhost:8080/MerLION-war/Reports/banner9.png");
            
            String queryString= "SELECT " +
                    
                    "     CUSTOMER.`CONTACTNO` AS CUSTOMER_CONTACTNO, " +
                    "     CUSTOMER.`EMAIL` AS CUSTOMER_EMAIL, " +
                    "     CUSTOMER.`NAME` AS CUSTOMER_NAME, " +
                    "     INVOICE.`ID` AS INVOICE_ID, " +
                    "     INVOICE.`INVOICEDATE` AS INVOICE_INVOICEDATE, " +
                    "     INVOICE.`OWNERCOMPANYNAME` AS INVOICE_OWNERCOMPANYNAME, " +
                    "     INVOICE.`PAIDORNOT` AS INVOICE_PAIDORNOT," +
                    "     INVOICE.`BLOCKNO` AS INVOICE_BLOCKNO," +
                    "     INVOICE.`CITY` AS INVOICE_CITY," +
                    "     INVOICE.`COUNTRY` AS INVOICE_COUNTRY," +
                    "     INVOICE.`POSTALCODE` AS INVOICE_POSTALCODE," +
                    "     INVOICE.`STATE` AS INVOICE_STATE," +
                    "     INVOICE.`STREET` AS INVOICE_STREET," +          
                    "     INVOICE.`SALESORDER_ID` AS INVOICE_SALESORDER_ID," +
                    "     COMPANY.`CONTACTNO` AS COMPANY_CONTACTNO, " +
                    "     SALESQUOTATION.`TOTALPRICE` AS SALESQUOTATION_TOTALPRICE, " +
                    "     PRODUCT.`PRODUCTNAME` AS PRODUCTNAME, " +
                    "     PRODUCT.`PRODUCTPRICE` AS PRODUCTPRICE, " +
                    "     PRODUCT.`UNIT` AS UNITNAME, " +
                    "     COMPANY.`VAT` AS COMPANY_VAT, " +
                    "     COMPANY.`COUNTRY` AS COMPANY_COUNTRY, " +
                    "     COMPANY.`STATE` AS COMPANY_STATE, " +                  
                    "     COMPANY.`CITY` AS COMPANY_CITY, " +
                    "     COMPANY.`STREET` AS COMPANY_STREET, " +
                    "     COMPANY.`POSTALCODE` AS COMPANY_POSTALCODE, " +
                    "     COMPANY.`BLOCKNO` AS COMPANY_BLOCKNO " +                    
                    "FROM `PRODUCT` PRODUCT, `SALESORDER_PRODUCT` SALESORDER_PRODUCT, `SALESORDER` SALESORDER, `SALESQUOTATION` SALESQUOTATION, `COMPANY` COMPANY, `INVOICE` INVOICE, `CUSTOMER` CUSTOMER"                 
                    + " WHERE INVOICE.`OWNERCOMPANYNAME` = '"+ownerCompany + "'"
                           + " AND INVOICE.`ID` = '"+invoiceId + "'"              
                    + " AND CUSTOMER.`NAME` = '"+customerCompany + "'"
                    + " AND INVOICE.`SALESORDER_ID` = SALESORDER.`ID`"
                    + " AND SALESORDER.`ID` = SALESQUOTATION.`SALESORDER_ID`"
                    + " AND SALESORDER.`ID` = SALESORDER_PRODUCT.`SalesOrder_ID`"
                    + " AND SALESORDER_PRODUCT.`productList_ID` = PRODUCT.`ID`"
                    + " AND COMPANY.`COMPANYNAME` = '"+ownerCompany + "'"
                    ;
            Statement stmt = establishConnection().createStatement();
            ResultSet rset = stmt.executeQuery(queryString);
            JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);
            
            InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/Reports/paymentReceipt.jasper");
            
            
            String salesInvoice = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports") + System.getProperty("file.separator") + "paymentReceipt.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream , parametersMap, jasperReports);
            
            System.out.println("after jasper print");
            
            String filename =realPath + System.getProperty("file.separator") +"paymentReceipt.pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint,filename);
                 
            System.out.println("after jasper export");
            
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
            reID = receiptID;
            select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            Receipt toSend = ipm.retrieveReceipt(reID);
            email = toSend.getInvoice().getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            customerCompany = toSend.getInvoice().getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            String subject = "Merlion Platform - Sending Receipt " + reID + " From " + ownerCompany;
            
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
                String content = "Dear " + customerCompany
                    + ",  \nThank you very much for making payment to " + ownerCompany + " !"
                    + "\nBelow is the receipt for your order:"
                    + "\n\tReceipt ID: " + toSend.getId()
                    + "\n\tDate: " + toSend.getReceiptDate()
                    + "\n\tSaling Pary: " + ownerCompany
                    + "\n\tReceiving Party: " + customerCompany
                    + "\n\tTotal Price: " + toSend.getInvoice().getSalesOrder().getSalesQuotation().getTotalPrice()
                    + "\n"
                    + "\n\tNote: Please do not reply this email. If you have further questions, please go to the contact form page and submit there."
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
        } catch (ReceiptNotExistException ex) {
            this.errorMsg("no reciept found");
        } catch (MessagingException ex) {
            this.errorMsg("cannot send email");
        }

        
    }
  
    
    @SuppressWarnings("unchecked")
     public void exportToPDF (Long inID) throws IOException, JRException, ClassNotFoundException, SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException{
        try {
            invoiceID = inID;
            select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            Invoice toSend = ipm.retrieveInvoice(invoiceID);
            email = toSend.getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            customerCompany = toSend.getSalesOrder().getSalesQuotation().getCustomer().getName();
            String subject = "Merlion Platform - Sending Invoice " + invoiceID + " From " + ownerCompany;
           
            Double totalPrice = toSend.getSalesOrder().getSalesQuotation().getTotalPrice();
            Double singlePrice = ((Product) toSend.getSalesOrder().getProductList().toArray()[0]).getProductPrice();
            Double quantityDouble = totalPrice/singlePrice;
            
            Integer quantity = quantityDouble.intValue();
            
          
            
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports");
            
            Map parametersMap = new HashMap();
            parametersMap.put("CompanyName",ownerCompany);
            parametersMap.put("QUANTITY",quantity);
            
            parametersMap.put("PATH1", "http://localhost:8080/MerLION-war/Reports/banner-others-payments.jpg");
            parametersMap.put("PATH2", "http://localhost:8080/MerLION-war/Reports/banner9.png");
            
            String queryString= "SELECT " +
                    
                    "     CUSTOMER.`CONTACTNO` AS CUSTOMER_CONTACTNO, " +
                    "     CUSTOMER.`EMAIL` AS CUSTOMER_EMAIL, " +
                    "     CUSTOMER.`NAME` AS CUSTOMER_NAME, " +
                    "     INVOICE.`ID` AS INVOICE_ID, " +
                    "     INVOICE.`INVOICEDATE` AS INVOICE_INVOICEDATE, " +
                    "     INVOICE.`OWNERCOMPANYNAME` AS INVOICE_OWNERCOMPANYNAME, " +
                    "     INVOICE.`PAIDORNOT` AS INVOICE_PAIDORNOT," +
                    "     INVOICE.`BLOCKNO` AS INVOICE_BLOCKNO," +
                    "     INVOICE.`CITY` AS INVOICE_CITY," +
                    "     INVOICE.`COUNTRY` AS INVOICE_COUNTRY," +
                    "     INVOICE.`POSTALCODE` AS INVOICE_POSTALCODE," +
                    "     INVOICE.`STATE` AS INVOICE_STATE," +
                    "     INVOICE.`STREET` AS INVOICE_STREET," +          
                    "     INVOICE.`SALESORDER_ID` AS INVOICE_SALESORDER_ID," +
                    "     COMPANY.`CONTACTNO` AS COMPANY_CONTACTNO, " +  
                    "     SALESQUOTATION.`TOTALPRICE` AS SALESQUOTATION_TOTALPRICE, " + 
                    "     PRODUCT.`PRODUCTNAME` AS PRODUCTNAME, " +    
                    "     PRODUCT.`PRODUCTPRICE` AS PRODUCTPRICE, " +  
                      "     PRODUCT.`UNIT` AS UNITNAME, " +  
                    "     COMPANY.`VAT` AS COMPANY_VAT, " +                  
                    "     COMPANY.`COUNTRY` AS COMPANY_COUNTRY, " +                  
                    "     COMPANY.`STATE` AS COMPANY_STATE, " +                  
                    "     COMPANY.`CITY` AS COMPANY_CITY, " +
                    "     COMPANY.`STREET` AS COMPANY_STREET, " +
                    "     COMPANY.`POSTALCODE` AS COMPANY_POSTALCODE, " +                 
                    "     COMPANY.`BLOCKNO` AS COMPANY_BLOCKNO " +                 
                  
                    "FROM `PRODUCT` PRODUCT, `SALESORDER_PRODUCT` SALESORDER_PRODUCT, `SALESORDER` SALESORDER, `SALESQUOTATION` SALESQUOTATION, `COMPANY` COMPANY, `INVOICE` INVOICE, `CUSTOMER` CUSTOMER"
                  
                    + " WHERE INVOICE.`OWNERCOMPANYNAME` = '"+ownerCompany + "'"
                    + " AND INVOICE.`ID` = '"+inID + "'"              
                    + " AND CUSTOMER.`NAME` = '"+customerCompany + "'"
                    + " AND INVOICE.`SALESORDER_ID` = SALESORDER.`ID`"
                    + " AND SALESORDER.`ID` = SALESQUOTATION.`SALESORDER_ID`"
                    + " AND SALESORDER.`ID` = SALESORDER_PRODUCT.`SalesOrder_ID`"
                    + " AND SALESORDER_PRODUCT.`productList_ID` = PRODUCT.`ID`"
                    + " AND COMPANY.`COMPANYNAME` = '"+ownerCompany + "'"
                  ;
            Statement stmt = establishConnection().createStatement();
            ResultSet rset = stmt.executeQuery(queryString);
            JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);
            
            InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/Reports/salesInvoice.jasper");
                   
//          String salesInvoice = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports") + System.getProperty("file.separator") + "salesInvoice.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream , parametersMap, jasperReports);
//        JasperViewer.viewReport(jasperPrint);
            
            System.out.println("after jasper print");
            
            String filename = realPath + System.getProperty("file.separator") +"salesInvoice.pdf";
            //Report saved in specified path
            JasperExportManager.exportReportToPdfFile(jasperPrint,filename);
            
            System.out.println("after jasper export");
             
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
                String content = "Dear " + customerCompany
                    + ": \nThank you very much for sending goods order to " + ownerCompany + " !"
                    + "\nBelow is the invoice for your order:"
                    + "\n\tInvoice ID: " + toSend.getId()
                    + "\n\tDate: " + toSend.getInvoiceDate().toString()
                    + "\n\tSaling Pary: " + ownerCompany
                    + "\n\tReceiving Party: " + customerCompany
                    + "\n\tAddress: " + toSend.getWarehouseLoc().getCountry() + "," + toSend.getWarehouseLoc().getState() + "," + toSend.getWarehouseLoc().getCity()
                    + "\n\t\t" + toSend.getWarehouseLoc().getStreet() + "," + toSend.getWarehouseLoc().getBlockNo() + "," + toSend.getWarehouseLoc().getPostalCode()
                    + "\n\tTotal Price: " + toSend.getSalesOrder().getSalesQuotation().getTotalPrice()
                    + "\n\n"
                    + "Please remember to make your payment.A receipt will be send to you after the saling party has confirmed the payment. Thank you."
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
        } catch (InvoiceNotExistException ex) {
            this.errorMsg("no invoice found!");
        } catch (MessagingException ex) {
            this.errorMsg("no msg found!");
        }
    }
    

    public void sendInvoice(Long inID) throws MessagingException, InvoiceNotExistException {
        try {
            invoiceID = inID;
            select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            Invoice toSend = ipm.retrieveInvoice(invoiceID);
            email = toSend.getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            customerCompany = toSend.getSalesOrder().getSalesQuotation().getCustomer().getName();
            String subject = "Merlion Platform - Sending Invoice " + invoiceID + " From " + ownerCompany;
            System.out.println("Inside send email");

            String content = "<h2>Dear " + customerCompany
                    + ",</h2><br /><h1>  Thank you very much for sending goods order to " + ownerCompany + " ! </h1><br />"
                    + "<h1>Below is the invoice for your order:</h1>"
                    + "<h2 align=\"center\">Invoice ID: " + toSend.getId()
                    + "<br />Date: " + toSend.getInvoiceDate().toString()
                    + "<br />Saling Pary: " + ownerCompany
                    + "<br />Receiving Party: " + customerCompany
                    + "<br />Address: " + toSend.getWarehouseLoc().getCountry() + "," + toSend.getWarehouseLoc().getState() + "," + toSend.getWarehouseLoc().getCity()
                    + "<br />         " + toSend.getWarehouseLoc().getStreet() + "," + toSend.getWarehouseLoc().getBlockNo() + "," + toSend.getWarehouseLoc().getPostalCode()
                    + "<br />Total Price: " + toSend.getSalesOrder().getSalesQuotation().getTotalPrice()
                    + "</h2><br />"
                    + "<p style=\"color: #ff0000;\">Please remember to make your payment.A receipt will be send to you after the saling party has confirmed the payment. Thank you.</p>"
                    + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                    + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
            System.out.println(content);
            sendEmail.run(email, subject, content);
            this.displayFaceMessage("Invoice has been sent successfully via Email!");
        } catch (InvoiceNotExistException e) {
            this.displayFaceMessage("Invoice does not exist");
        }
    }

    public void preSendReceipt(Long receiptID) throws IOException {
        reID = receiptID;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("SendReceipt.xhtml");
    }

    public void sendReceipt(Long receiptID) throws MessagingException, ReceiptNotExistException {
        try {
            reID = receiptID;
            select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompany = select_Company.getCompanyName();
            Receipt toSend = ipm.retrieveReceipt(reID);
            email = toSend.getInvoice().getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            customerCompany = toSend.getInvoice().getSalesOrder().getSalesQuotation().getCustomer().getEmail();
            String subject = "Merlion Platform - Sending Receipt " + reID + " From " + ownerCompany;
            System.out.println("Inside send email");

            String content = "<h2>Dear " + customerCompany
                    + ",</h2><br /><h1>  Thank you very much for making payment to " + ownerCompany + " ! </h1><br />"
                    + "<h1>Below is the receipt for your order:</h1>"
                    + "<h2 align=\"center\">Receipt ID: " + toSend.getId()
                    + "<br />Date: " + toSend.getReceiptDate()
                    + "<br />Saling Pary: " + ownerCompany
                    + "<br />Receiving Party: " + customerCompany
                    + "<br />Total Price: " + toSend.getInvoice().getSalesOrder().getSalesQuotation().getTotalPrice()
                    + "</h2><br />"
                    + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                    + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
            System.out.println(content);
            sendEmail.run(email, subject, content);
            this.displayFaceMessage("Receipt has been sent successfully via Email!");
        } catch (ReceiptNotExistException e) {
            this.displayFaceMessage("The receipt does not exist.");
        }
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

}
