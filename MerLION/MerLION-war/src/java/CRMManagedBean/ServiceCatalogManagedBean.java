/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceContract;
import CRM.session.ServiceContractManagementSessionLocal;
import CRM.session.ServiceInfoMgtSessionBeanLocal;
import Common.entity.Company;
import Common.entity.Location;
import Common.entity.PartnerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import WMS.entity.Warehouse;
import WMS.session.FacilityManagementLocal;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.ServiceCatalogNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.YellowPageAlreadyExistException;
import util.exception.YellowPageNotExistException;

/**
 *
 * @author andongmei
 */
@ManagedBean(name = "serviceCatalogManagedBean")

@Named(value = "serviceCatalogManagedBean")
@SessionScoped

public class ServiceCatalogManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of ServiceCatalogManagedBean
     */
    @EJB
    private ServiceInfoMgtSessionBeanLocal simsbl;
    @EJB
    private ServiceContractManagementSessionLocal scmsl;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private FacilityManagementLocal fml;

    private String msgCreateCatalog;
    private String msgUpdateCatalog;

    private String msgYellowPageAlready;

    private String msgCatalogDeleted;

    private String msgMakeItYellowPage;
    //  private String msg;
    private ServiceCatalog particularServiceItem;
    private List<ServiceCatalog> allServiceCatalogItems = new ArrayList<>();
    private ServiceCatalog sCatalog;

    private PartnerCompany pCompany;
    private Company company;

    //for store companyId and serviceId for update
    private String companyIdUpdate;
    private String serviceIdUpdate;

    private String sourceCountryD;
    private String sourceStateD;
    private String sourceCityD;
    private String sourceStreetD;
    private String sourceBlockNoD;
    private String sourcePostalCodeD;

    private String destCountryD;
    private String destStateD;
    private String destCityD;
    private String destStreetD;
    private String destBlockNoD;
    private String destPostalCodeD;

    private String warehCountryD;
    private String warehStateD;
    private String warehCityD;
    private String warehStreetD;
    private String warehBlockNoD;
    private String warehPostalCodeD;

    private Boolean perishableD;
    private Boolean flammableD;
    private Boolean pharmaceuticalD;
    private Boolean highValueD;

    private String serviceIdDetail;
    private String companyIdDetail;
    private String sNameDetail;
    private String descriptionDetail;
    private Date startTimeDe;
    private Date endTimeDe;
    private String priceDetail;
    //for store companyId for creating and updateing new s
    private String companyId;
    private String serviceId;

    private Long companyIdLogin;

    private String serviceName;
    private String price;
    private String description;
    private Date startTime;
    private Date endTime;

    private String sourceCountry;
    private String sourceState;
    private String sourceCity;
    private String sourceStreet;
    private String sourceBlockNo;
    private String sourcePostalCode;

    private String destCountry;
    private String destState;
    private String destCity;
    private String destStreet;
    private String destBlockNo;
    private String destPostalCode;

    private String warehCountry;
    private String warehState;
    private String warehCity;
    private String warehStreet;
    private String warehBlockNo;
    private String warehPostalCode;

    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;

    private String companyIdYellow;
    private String sNameYellow;
    private String descriptionYellow;
    private Date startTimeYellow;
    private Date endTimeYellow;
    private String priceYellow;

    private String colorCode = "background: yellow";

    private String companyNameLogin;

    private Boolean publicOrNot;
    private Boolean publicOrNotD;

    private ServiceContract sContract;

    private ServiceContract particularContractItem;

    private Warehouse warehouse;

    private String statusMessage;

    private Long warehouseId;
    private Long warehouseIdD;

    @PostConstruct
    public void init() {
        try {
            companyIdLogin = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            companyNameLogin = cmsbl.retrieveCompany(companyIdLogin).getCompanyName();
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection establishConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String oracleURL = "jdbc:mysql://localhost:3306/is3102g02";
            connection = DriverManager.getConnection(oracleURL, "root", "adminadmin");
            System.out.println("after connection");
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            System.out.println("c");
        }
        return connection;

    }

    public void testReportPDF(ActionEvent event) throws IOException, JRException, ClassNotFoundException, SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
//        InputStream reportStream=this.getClass().getResourceAsStream("/MerLION-war/Reports/salesInvoice.jrxml");
//      JasperDesign jasperDesign = JRXmlLoader.load("salesInvoice.jrxml");    

        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports");

        Map parametersMap = new HashMap();
        parametersMap.put("CompanyName", companyNameLogin);
        parametersMap.put("path1", "http://localhost:8080/MerLION-war/Reports/payment.png");
        parametersMap.put("path2", "http://localhost:8080/MerLION-war/Reports/MerLIONLogo.png");

//        Statement st2=establishConnection().createStatement();
//        String queryString = "SELECT " +
//        "INVOICE.ID, INVOICE.INVOICEDATE, INVOICE.OWNERCOMPANYNAME, INVOICE.PAIDORNOT, INVOICE.SALESORDER_ID"
//                + " AS INVOICE_ID, INVOICE_INVOICEDATE, INVOICE_OWNERCOMPANYNAME, "
//                + "INVOICE_PAIDORNOT, INVOICE_SALESORDER_ID" +
//                " FROM INVOICE INVOICE "
//                + "WHERE INVOICE_OWNERCOMPANYNAME = " + companyNameLogin;
        String queryString = "SELECT "
                + "     INVOICE.`ID` AS INVOICE_ID, "
                + "     INVOICE.`INVOICEDATE` AS INVOICE_INVOICEDATE, "
                + "     INVOICE.`OWNERCOMPANYNAME` AS INVOICE_OWNERCOMPANYNAME, "
                + "     INVOICE.`PAIDORNOT` AS INVOICE_PAIDORNOT,"
                + "     INVOICE.`BLOCKNO` AS INVOICE_BLOCKNO,"
                + "     INVOICE.`CITY` AS INVOICE_CITY,"
                + "     INVOICE.`COUNTRY` AS INVOICE_COUNTRY,"
                + "     INVOICE.`POSTALCODE` AS INVOICE_POSTALCODE,"
                + "     INVOICE.`STATE` AS INVOICE_STATE,"
                + "     INVOICE.`STREET` AS INVOICE_STREET,"
                + "     INVOICE.`SALESORDER_ID` AS INVOICE_SALESORDER_ID "
                + "FROM `INVOICE` INVOICE"
                + " WHERE INVOICE.`OWNERCOMPANYNAME` = '" + companyNameLogin + "'";
        Statement stmt = establishConnection().createStatement();
        ResultSet rset = stmt.executeQuery(queryString);
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);

        InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/Reports/salesInvoice.jasper");

//        JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/Users/andongmei/NetBeansProjects/MerLION/MerLION-war/web/Reports/salesInvoice.jrxml"));    
//        JasperReport salesInvoice=JasperCompileManager.compileReport(jasperDesign);
        String salesInvoice = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports") + System.getProperty("file.separator") + "salesInvoice.jasper";
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametersMap, jasperReports);
//        JasperViewer.viewReport(jasperPrint);  

        System.out.println("after jasper print");

        String filename = realPath + System.getProperty("file.separator") + "salesInvoiceTest2.pdf";
        //Report saved in specified path
        JasperExportManager.exportReportToPdfFile(jasperPrint, filename);

        //Report open in Runtime
        //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " +filename);
        System.out.println("after jasper export");
//        
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
//        ec.redirect("/MerLION-war/Reports/salesInvoice.html");
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public ServiceCatalogManagedBean() {
    }

    public ServiceContract getsContract() {
        return sContract;
    }

    public void setsContract(ServiceContract sContract) {
        this.sContract = sContract;
    }

    public ServiceContract getParticularContractItem() {
        return particularContractItem;
    }

    public void setParticularContractItem(ServiceContract particularContractItem) {
        this.particularContractItem = particularContractItem;
    }

    public List<ServiceContract> allServiceContractItems() {
        try {
            return scmsl.retrieveOutsourcingServiceContractList(companyNameLogin);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createServiceContractInViewPage: "
                    + statusMessage, ""));
        } catch (ServiceContractNotExistException ex) {
            statusMessage = "ServiceContractNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createServiceContractInViewPage: "
                    + statusMessage, ""));
        }
        return null;
    }

    public void updateItem(ActionEvent event) throws CompanyNotExistException, ServiceCatalogNotExistException, IOException {
        System.out.print("updateItem");
        Double p = Double.valueOf(priceDetail);

        Timestamp sTime = new Timestamp(startTimeDe.getTime());
        Timestamp eTime = new Timestamp(endTimeDe.getTime());

        Long cIdUItem = Long.valueOf(companyIdDetail);
        Long sIdUItem = Long.valueOf(serviceIdDetail);

//      
        Location source = new Location(sourceCountryD, sourceStateD, sourceCityD, sourceStreetD, sourceBlockNoD, sourcePostalCodeD);
        Location dest = new Location(destCountryD, destStateD, destCityD, destStreetD, destBlockNoD, destPostalCodeD);
        Location warehLocaD = new Location(warehCountryD, warehStateD, warehCityD, warehStreetD, warehBlockNoD, warehPostalCodeD);

        msgUpdateCatalog = simsbl.updateServiceCatalog(cIdUItem, sIdUItem, sNameDetail, descriptionDetail, p, sTime, eTime,
                source, dest, warehouseIdD, warehLocaD,
                perishableD, flammableD, pharmaceuticalD, highValueD, publicOrNotD);
        RequestContext.getCurrentInstance().execute("PF('dlgUpdate').show()");
        //   this.displayUpdateMessage(msgUpdateCatalog);

    }

    public void delete(ActionEvent event) {
        
        if (particularServiceItem == null)
        {
         RequestContext.getCurrentInstance().execute("PF('noEntry').show()");
        }
        else
        {
        System.out.print("Delete");
        Long cIdDetail = particularServiceItem.getCompany().getId();
        Long sIdDetail = particularServiceItem.getId();

        System.out.print(cIdDetail);
        System.out.print(sIdDetail);

        try {
            msgCatalogDeleted = simsbl.deleteServiceCatalog(cIdDetail, sIdDetail);

//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(c));
            RequestContext.getCurrentInstance().execute("PF('dlgDelete').show()");

        } catch (CompanyNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceCatalogNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (YellowPageNotExistException ex) {
            statusMessage = "YellowPageNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        }
    }

    public void makeItPublic(ActionEvent event) {

        if (particularServiceItem == null)
        {
         RequestContext.getCurrentInstance().execute("PF('noEntry').show()");
        }
        else if (particularServiceItem.getPublicOrNot()==true)
        {
          RequestContext.getCurrentInstance().execute("PF('alreadyPublic').show()");
       
        }
        else
        {
        
        simsbl.approveServiceCatalog(particularServiceItem.getId());
        RequestContext.getCurrentInstance().execute("PF('dlgYellowSuccess').show()");
        //  ypmsbl.createYellowPage(cIdDetaily, serviceName, description, priceDetaily, startTimeDey, endTimeDey)
        //  this.displayMakeItYellowPageMessage(msgMakeItYellowPage);
        }
    }

    public void hideIt(ActionEvent event) {
        
        if (particularServiceItem == null)
        {
         RequestContext.getCurrentInstance().execute("PF('noEntry').show()");
        }
         else if (particularServiceItem.getPublicOrNot()==false)
        {
          RequestContext.getCurrentInstance().execute("PF('alreadyUnpublic').show()");
       
        }
        else
        {

        simsbl.hideIt(particularServiceItem.getId());
        RequestContext.getCurrentInstance().execute("PF('dlgHideIt').show()");
        //  ypmsbl.createYellowPage(cIdDetaily, serviceName, description, priceDetaily, startTimeDey, endTimeDey)
        // this.displayMakeItYellowPageMessage(msgMakeItYellowPage);
        }
    }

    private void displayMakeItYellowPageMessage(String message) throws IOException {
        System.out.print("displayMakeItYellowPageMsg");

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, msgMakeItYellowPage);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);

    }

    private void displayUpdateMessage(String message) throws IOException {
        System.out.print("displayUpdateMsg");

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, msgUpdateCatalog);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);

    }

    public void createServiceCatalogItem(ActionEvent event) {
        try {
            System.out.print(sourceState);
            System.out.print(destState);
            System.out.print("create");
            Long cId = companyIdLogin;
            Double p = Double.valueOf(price);
            Timestamp sTime = new Timestamp(startTime.getTime());
            Timestamp eTime = new Timestamp(endTime.getTime());
            Location source = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
            Location dest = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
            Long wId = warehouseId;

            Location warehLoca = new Location(null, null, null, null, null, null);

            if (wId != null) {
                warehLoca = fml.retrieveWarehouse(warehouseId).getLocation();
            }

            try {

                String a = simsbl.createServiceCatalog(cId, serviceName, description, p, sTime, eTime, source, dest, wId, warehLoca, perishable, flammable, pharmaceutical, highValue, publicOrNot);
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(a));
                RequestContext.getCurrentInstance().execute("PF('dlg').show()");
                System.out.println(a);

            } catch (CompanyNotExistException ex) {
                Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                this.displayNoCompany("This Company ID doesn't exist. Please check your input, or add this company as your partner company!");

            }
        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void retrieveWarehouseInfo(ActionEvent event) {

        try {
            if (warehouseId == null) {

                warehCountry = null;
                warehState = null;
                warehCity = null;
                warehStreet = null;
                warehBlockNo = null;
                warehPostalCode = null;
            } else {
                Warehouse warehouse = fml.retrieveWarehouse(warehouseId);

                setWarehCountry(warehouse.getLocation().getCountry());
                setWarehState(warehouse.getLocation().getState());
                setWarehCity(warehouse.getLocation().getCity());
                setWarehStreet(warehouse.getLocation().getStreet());
                setWarehBlockNo(warehouse.getLocation().getBlockNo());
                setWarehPostalCode(warehouse.getLocation().getPostalCode());
            }
        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void retrieveWarehouseInfoD(ActionEvent event) {

        try {
            Warehouse warehouseD = fml.retrieveWarehouse(warehouseIdD);

            setWarehCountryD(warehouseD.getLocation().getCountry());
            setWarehStateD(warehouseD.getLocation().getState());
            setWarehCityD(warehouseD.getLocation().getCity());
            setWarehStreetD(warehouseD.getLocation().getStreet());
            setWarehBlockNoD(warehouseD.getLocation().getBlockNo());
            setWarehPostalCodeD(warehouseD.getLocation().getPostalCode());
        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void displayNoCompany(String message) {
        FacesMessage msg = new FacesMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void viewDetail2(ActionEvent event) {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.redirect("viewDetailCatalog.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void viewDetailContract(ActionEvent event) {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.redirect("viewAndUpdateOutsourcingServiceContract.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testUpdate(ActionEvent event) {

        if (particularServiceItem == null) {
            
             RequestContext.getCurrentInstance().execute("PF('noEntry').show()");
       

        } else {
            try {
                System.out.println("redirectDetail");

                setCompanyIdDetail(particularServiceItem.getCompany().getId().toString());
                setServiceIdDetail(particularServiceItem.getId().toString());
                setStartTimeDe(particularServiceItem.getStartTime());
                setEndTimeDe(particularServiceItem.getEndTime());
                setPriceDetail(particularServiceItem.getPrice().toString());
                setSNameDetail(particularServiceItem.getServiceName());
                setDescriptionDetail(particularServiceItem.getDescription());

                setSourceCountryD(particularServiceItem.getSource().getCountry());
                setSourceStateD(particularServiceItem.getSource().getState());
                setSourceCityD(particularServiceItem.getSource().getCity());
                setSourceStreetD(particularServiceItem.getSource().getStreet());
                setSourceBlockNoD(particularServiceItem.getSource().getBlockNo());
                setSourcePostalCodeD(particularServiceItem.getSource().getPostalCode());

                setDestCountryD(particularServiceItem.getDest().getCountry());
                setDestStateD(particularServiceItem.getDest().getState());
                setDestCityD(particularServiceItem.getDest().getCity());
                setDestStreetD(particularServiceItem.getDest().getStreet());
                setDestBlockNoD(particularServiceItem.getDest().getBlockNo());
                setDestPostalCodeD(particularServiceItem.getDest().getPostalCode());

                setPerishableD(particularServiceItem.getPerishable());
                setFlammableD(particularServiceItem.getFlammable());
                setPharmaceuticalD(particularServiceItem.getPharmaceutical());
                setHighValueD(particularServiceItem.getHighValue());
                setPublicOrNotD(particularServiceItem.getHighValue());

                //  setDestBlockNoD(particularServiceItem.getSource().getBlockNo());
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();

                ec.redirect("testUpdate.xhtml");

            } catch (IOException ex) {
                Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void viewYellowPage(ActionEvent event) {

        if (particularServiceItem == null)
        {
         RequestContext.getCurrentInstance().execute("PF('noEntry').show()");
        }
        else
        {
        
        try {
            System.out.println("redirectDetail");

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.redirect("onlyViewDetail.xhtml");

        } catch (IOException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }

    public String getMsgCreateCatalog() throws CompanyNotExistException {

        return msgCreateCatalog = simsbl.createServiceCatalog(sCatalog.getId(), sCatalog.getServiceName(), sCatalog.getDescription(), sCatalog.getPrice(), sCatalog.getStartTime(), sCatalog.getEndTime(), sCatalog.getSource(), sCatalog.getDest(), sCatalog.getWarehouseId(), sCatalog.getWarehLoca(), sCatalog.getPerishable(), sCatalog.getFlammable(), sCatalog.getPharmaceutical(), sCatalog.getHighValue(), sCatalog.getPublicOrNot());
    }

    public void setMsgCreateCatalog(String msgCreateCatalog) {
        this.msgCreateCatalog = msgCreateCatalog;
    }

    public String getMsgUpdateCatalog() throws CompanyNotExistException, ServiceCatalogNotExistException {
        return msgUpdateCatalog = simsbl.updateServiceCatalog(company.getId(), sCatalog.getId(), sCatalog.getServiceName(), sCatalog.getDescription(), sCatalog.getPrice(), sCatalog.getStartTime(), sCatalog.getEndTime(),
                sCatalog.getSource(), sCatalog.getDest(), sCatalog.getWarehouseId(), sCatalog.getWarehLoca(), sCatalog.getPerishable(), sCatalog.getFlammable(), sCatalog.getPharmaceutical(), sCatalog.getHighValue(), sCatalog.getPublicOrNot());
    }

    public void setMsgUpdateCatalog(String updateCatalogMessage) {
        this.msgUpdateCatalog = updateCatalogMessage;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<ServiceCatalog> getAllServiceCatalogItems() {

        try {
            return simsbl.viewAllServiceCatalog(companyIdLogin);
        } catch (ServiceCatalogNotExistException | CompanyNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public List<ServiceCatalog> getAllPublicServiceCatalogItems() {

        try {
            return simsbl.viewAllPublicServiceCatalog(companyIdLogin);
        } catch (ServiceCatalogNotExistException | CompanyNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public List<ServiceCatalog> getAllPublicServiceCatalogItemsOfAllCompany() {

        try {
            return simsbl.displayAllYellowPageInfo();
        } catch (ServiceCatalogNotExistException ex) {
            Logger.getLogger(ServiceCatalogManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public List<Warehouse> retrieveOwningWarehouseList() {
        System.out.println("Enter retrieveOwningWarehouseList");
        List<Warehouse> whList = new ArrayList<>();
        Company loginCompany = new Company();
        try {
            loginCompany = cmsbl.retrieveCompany(companyIdLogin);
        } catch (CompanyNotExistException ex) {
            String statusMessage = "CompanyNotExistException: " + ex.getMessage();
            displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }

        if (loginCompany.getOwnedWarehouseList().isEmpty()) {
            String statusMessage = "Company does not own any warehouse!";
            displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
            return whList;
        }

        whList = loginCompany.getOwnedWarehouseList();
        System.out.println("End of retrieveOwningWarehouseList");
        return whList;
    }

    public String getMsgYellowPageAlready() {
        return msgYellowPageAlready;
    }

    public void setMsgYellowPageAlready(String msgYellowPageAlready) {
        this.msgYellowPageAlready = msgYellowPageAlready;
    }

    public void setAllServiceCatalogItems(List<ServiceCatalog> allServiceCatalogItems) {
        this.allServiceCatalogItems = allServiceCatalogItems;
    }

    public ServiceCatalog getParticularServiceItem() {
        return particularServiceItem;
    }

    public void setParticularServiceItem(ServiceCatalog particularServiceItem) {
        this.particularServiceItem = particularServiceItem;
    }

    public PartnerCompany getPCompany() {
        return pCompany;
    }

    public void setPCompany(PartnerCompany pCompany) {
        this.pCompany = pCompany;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ServiceCatalog getSCatalog() {
        return sCatalog;
    }

    public void setSCatalog(ServiceCatalog sCatalog) {
        this.sCatalog = sCatalog;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyIdLogin(Long companyIdLogin) {
        this.companyIdLogin = companyIdLogin;
    }

    public Long getCompanyIdLogin() {
        return companyIdLogin;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getServiceIdUpdate() {
        return serviceIdUpdate;
    }

    public void setServiceIdUpdate(String serviceIdUpdate) {
        this.serviceIdUpdate = serviceIdUpdate;
    }

    public String getCompanyIdUpdate() {
        return companyIdUpdate;
    }

    public void setCompanyIdUpdate(String companyIdUpdate) {
        this.companyIdUpdate = companyIdUpdate;
    }
//
//    public CountryStateManagedBean getCsmb() {
//        return csmb;
//    }
//
//    public void setCsmb(CountryStateManagedBean csmb) {
//        this.csmb = csmb;
//    }
//
//    public CountryStateDManagedBean getCsdmb() {
//        return csdmb;
//    }
//
//    public void setCsdmb(CountryStateDManagedBean csdmb) {
//        this.csdmb = csdmb;
//    }

    public String getServiceIdDetail() {
        return serviceIdDetail;
    }

    public void setServiceIdDetail(String serviceIdDetail) {
        this.serviceIdDetail = serviceIdDetail;
    }

    public String getCompanyIdDetail() {
        return companyIdDetail;
    }

    public void setCompanyIdDetail(String companyIdDetail) {
        this.companyIdDetail = companyIdDetail;
    }

    public String getSNameDetail() {
        return sNameDetail;
    }

    public void setSNameDetail(String sNameDetail) {
        this.sNameDetail = sNameDetail;
    }

    public String getDescriptionDetail() {
        return descriptionDetail;
    }

    public void setDescriptionDetail(String descriptionDetail) {
        this.descriptionDetail = descriptionDetail;
    }

    public Date getStartTimeDe() {
        return startTimeDe;
    }

    public void setStartTimeDe(Date startTimeDe) {
        this.startTimeDe = startTimeDe;
    }

    public Date getEndTimeDe() {
        return endTimeDe;
    }

    public void setEndTimeDe(Date endTimeDe) {
        this.endTimeDe = endTimeDe;
    }

    public String getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(String priceDetail) {
        this.priceDetail = priceDetail;
    }

    public void reset() {
        RequestContext.getCurrentInstance().reset("form1:panel2");
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getMsgMakeItYellowPage() {
        return msgMakeItYellowPage;
    }

    public void setMsgMakeItYellowPage(String msgMakeItYellowPage) {
        this.msgMakeItYellowPage = msgMakeItYellowPage;
    }

    public String getCompanyIdYellow() {
        return companyIdYellow;
    }

    public void setCompanyIdYellow(String companyIdYellow) {
        this.companyIdYellow = companyIdYellow;
    }

    public String getsNameYellow() {
        return sNameYellow;
    }

    public void setsNameYellow(String sNameYellow) {
        this.sNameYellow = sNameYellow;
    }

    public String getDescriptionYellow() {
        return descriptionYellow;
    }

    public void setDescriptionYellow(String descriptionYellow) {
        this.descriptionYellow = descriptionYellow;
    }

    public Date getStartTimeYellow() {
        return startTimeYellow;
    }

    public void setStartTimeYellow(Date startTimeYellow) {
        this.startTimeYellow = startTimeYellow;
    }

    public Date getEndTimeYellow() {
        return endTimeYellow;
    }

    public void setEndTimeYellow(Date endTimeYellow) {
        this.endTimeYellow = endTimeYellow;
    }

    public String getPriceYellow() {
        return priceYellow;
    }

    public void setPriceYellow(String priceYellow) {
        this.priceYellow = priceYellow;
    }
//
//    public String getRequiredTruckNumberStr() {
//        return requiredTruckNumberStr;
//    }
//
//    public void setRequiredTruckNumberStr(String requiredTruckNumberStr) {
//        this.requiredTruckNumberStr = requiredTruckNumberStr;
//    }
//
//    public String getRequiredPlaneNumberStr() {
//        return requiredPlaneNumberStr;
//    }
//
//    public void setRequiredPlaneNumberStr(String requiredPlaneNumberStr) {
//        this.requiredPlaneNumberStr = requiredPlaneNumberStr;
//    }
//
//    public String getRequiredVesselNumberStr() {
//        return requiredVesselNumberStr;
//    }
//
//    public void setRequiredVesselNumberStr(String requiredVesselNumberStr) {
//        this.requiredVesselNumberStr = requiredVesselNumberStr;
//    }
//
//    public String getRequiredWarehouseCapacityStr() {
//        return requiredWarehouseCapacityStr;
//    }
//
//    public void setRequiredWarehouseCapacityStr(String requiredWarehouseCapacityStr) {
//        this.requiredWarehouseCapacityStr = requiredWarehouseCapacityStr;
//    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getSourceStreet() {
        return sourceStreet;
    }

    public void setSourceStreet(String sourceStreet) {
        this.sourceStreet = sourceStreet;
    }

    public String getSourceBlockNo() {
        return sourceBlockNo;
    }

    public void setSourceBlockNo(String sourceBlockNo) {
        this.sourceBlockNo = sourceBlockNo;
    }

    public String getSourcePostalCode() {
        return sourcePostalCode;
    }

    public void setSourcePostalCode(String sourcePostalCode) {
        this.sourcePostalCode = sourcePostalCode;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestStreet() {
        return destStreet;
    }

    public void setDestStreet(String destStreet) {
        this.destStreet = destStreet;
    }

    public String getDestBlockNo() {
        return destBlockNo;
    }

    public void setDestBlockNo(String destBlockNo) {
        this.destBlockNo = destBlockNo;
    }

    public String getDestPostalCode() {
        return destPostalCode;
    }

    public void setDestPostalCode(String destPostalCode) {
        this.destPostalCode = destPostalCode;
    }

    public Boolean isPerishable() {
        return perishable;
    }

    public void setPerishable(Boolean perishable) {
        this.perishable = perishable;
    }

    public Boolean isFlammable() {
        return flammable;
    }

    public void setFlammable(Boolean flammable) {
        this.flammable = flammable;
    }

    public Boolean isPharmaceutical() {
        return pharmaceutical;
    }

    public void setPharmaceutical(Boolean pharmaceutical) {
        this.pharmaceutical = pharmaceutical;
    }

    public Boolean isHighValue() {
        return highValue;
    }

    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

//    public String getRequiredTruckNumberStrD() {
//        return requiredTruckNumberStrD;
//    }
//    public void setRequiredTruckNumberStrD(String requiredTruckNumberStrD) {
//        this.requiredTruckNumberStrD = requiredTruckNumberStrD;
//    }
//
//    public String getRequiredPlaneNumberStrD() {
//        return requiredPlaneNumberStrD;
//    }
//
//    public void setRequiredPlaneNumberStrD(String requiredPlaneNumberStrD) {
//        this.requiredPlaneNumberStrD = requiredPlaneNumberStrD;
//    }
//
//    public String getRequiredVesselNumberStrD() {
//        return requiredVesselNumberStrD;
//    }
//
//    public void setRequiredVesselNumberStrD(String requiredVesselNumberStrD) {
//        this.requiredVesselNumberStrD = requiredVesselNumberStrD;
//    }
//
//    public String getRequiredWarehouseCapacityStrD() {
//        return requiredWarehouseCapacityStrD;
//    }
//
//    public void setRequiredWarehouseCapacityStrD(String requiredWarehouseCapacityStrD) {
//        this.requiredWarehouseCapacityStrD = requiredWarehouseCapacityStrD;
//    }
    public String getSourceCountryD() {
        return sourceCountryD;
    }

    public void setSourceCountryD(String sourceCountryD) {
        this.sourceCountryD = sourceCountryD;
    }

    public String getSourceStateD() {
        return sourceStateD;
    }

    public void setSourceStateD(String sourceStateD) {
        this.sourceStateD = sourceStateD;
    }

    public String getSourceCityD() {
        return sourceCityD;
    }

    public void setSourceCityD(String sourceCityD) {
        this.sourceCityD = sourceCityD;
    }

    public String getSourceStreetD() {
        return sourceStreetD;
    }

    public void setSourceStreetD(String sourceStreetD) {
        this.sourceStreetD = sourceStreetD;
    }

    public String getSourceBlockNoD() {
        return sourceBlockNoD;
    }

    public void setSourceBlockNoD(String sourceBlockNoD) {
        this.sourceBlockNoD = sourceBlockNoD;
    }

    public String getSourcePostalCodeD() {
        return sourcePostalCodeD;
    }

    public void setSourcePostalCodeD(String sourcePostalCodeD) {
        this.sourcePostalCodeD = sourcePostalCodeD;
    }

    public String getDestCountryD() {
        return destCountryD;
    }

    public void setDestCountryD(String destCountryD) {
        this.destCountryD = destCountryD;
    }

    public String getDestStateD() {
        return destStateD;
    }

    public void setDestStateD(String destStateD) {
        this.destStateD = destStateD;
    }

    public String getDestCityD() {
        return destCityD;
    }

    public void setDestCityD(String destCityD) {
        this.destCityD = destCityD;
    }

    public String getDestStreetD() {
        return destStreetD;
    }

    public void setDestStreetD(String destStreetD) {
        this.destStreetD = destStreetD;
    }

    public String getDestBlockNoD() {
        return destBlockNoD;
    }

    public void setDestBlockNoD(String destBlockNoD) {
        this.destBlockNoD = destBlockNoD;
    }

    public String getDestPostalCodeD() {
        return destPostalCodeD;
    }

    public void setDestPostalCodeD(String destPostalCodeD) {
        this.destPostalCodeD = destPostalCodeD;
    }

    public Boolean getPerishableD() {
        return perishableD;
    }

    public void setPerishableD(Boolean perishableD) {
        this.perishableD = perishableD;
    }

    public Boolean getFlammableD() {
        return flammableD;
    }

    public void setFlammableD(Boolean flammableD) {
        this.flammableD = flammableD;
    }

    public Boolean getPharmaceuticalD() {
        return pharmaceuticalD;
    }

    public void setPharmaceuticalD(Boolean pharmaceuticalD) {
        this.pharmaceuticalD = pharmaceuticalD;
    }

    public Boolean getHighValueD() {
        return highValueD;
    }

    public void setHighValueD(Boolean highValueD) {
        this.highValueD = highValueD;
    }

    public Boolean getPerishable() {
        return perishable;
    }

    public Boolean getFlammable() {
        return flammable;
    }

    public Boolean getPharmaceutical() {
        return pharmaceutical;
    }

    public Boolean getHighValue() {
        return highValue;
    }

    public void addPerishableMessage() {
        String summary = perishable ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addFlammableMessage() {
        String summary = flammable ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addPharmaceuticalMessage() {
        String summary = pharmaceutical ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addHighValueMessage() {
        String summary = highValue ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public void addPublicOrNotMessage() {
        String summary = publicOrNot ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
    }

    public ServiceCatalog getsCatalog() {
        return sCatalog;
    }

    public void setsCatalog(ServiceCatalog sCatalog) {
        this.sCatalog = sCatalog;
    }

    public PartnerCompany getpCompany() {
        return pCompany;
    }

    public void setpCompany(PartnerCompany pCompany) {
        this.pCompany = pCompany;
    }

    public String getsNameDetail() {
        return sNameDetail;
    }

    public void setsNameDetail(String sNameDetail) {
        this.sNameDetail = sNameDetail;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCompanyNameLogin() {
        return companyNameLogin;
    }

    public void setCompanyNameLogin(String companyNameLogin) {
        this.companyNameLogin = companyNameLogin;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getWarehCountryD() {
        return warehCountryD;
    }

    public void setWarehCountryD(String warehCountryD) {
        this.warehCountryD = warehCountryD;
    }

    public String getWarehStateD() {
        return warehStateD;
    }

    public void setWarehStateD(String warehStateD) {
        this.warehStateD = warehStateD;
    }

    public String getWarehCityD() {
        return warehCityD;
    }

    public void setWarehCityD(String warehCityD) {
        this.warehCityD = warehCityD;
    }

    public String getWarehStreetD() {
        return warehStreetD;
    }

    public void setWarehStreetD(String warehStreetD) {
        this.warehStreetD = warehStreetD;
    }

    public String getWarehBlockNoD() {
        return warehBlockNoD;
    }

    public void setWarehBlockNoD(String warehBlockNoD) {
        this.warehBlockNoD = warehBlockNoD;
    }

    public String getWarehPostalCodeD() {
        return warehPostalCodeD;
    }

    public void setWarehPostalCodeD(String warehPostalCodeD) {
        this.warehPostalCodeD = warehPostalCodeD;
    }

    public String getWarehCountry() {
        return warehCountry;
    }

    public void setWarehCountry(String warehCountry) {
        this.warehCountry = warehCountry;
    }

    public String getWarehState() {
        return warehState;
    }

    public void setWarehState(String warehState) {
        this.warehState = warehState;
    }

    public String getWarehCity() {
        return warehCity;
    }

    public void setWarehCity(String warehCity) {
        this.warehCity = warehCity;
    }

    public String getWarehStreet() {
        return warehStreet;
    }

    public void setWarehStreet(String warehStreet) {
        this.warehStreet = warehStreet;
    }

    public String getWarehBlockNo() {
        return warehBlockNo;
    }

    public void setWarehBlockNo(String warehBlockNo) {
        this.warehBlockNo = warehBlockNo;
    }

    public String getWarehPostalCode() {
        return warehPostalCode;
    }

    public void setWarehPostalCode(String warehPostalCode) {
        this.warehPostalCode = warehPostalCode;
    }

    public String getMsgCatalogDeleted() {
        return msgCatalogDeleted;
    }

    public void setMsgCatalogDeleted(String msgCatalogDeleted) {
        this.msgCatalogDeleted = msgCatalogDeleted;
    }

    public Long getWarehouseIdD() {
        return warehouseIdD;
    }

    public void setWarehouseIdD(Long warehouseIdD) {
        this.warehouseIdD = warehouseIdD;
    }

    public Boolean getPublicOrNot() {
        return publicOrNot;
    }

    public void setPublicOrNot(Boolean publicOrNot) {
        this.publicOrNot = publicOrNot;
    }

    public Boolean getPublicOrNotD() {
        return publicOrNotD;
    }

    public void setPublicOrNotD(Boolean publicOrNotD) {
        this.publicOrNotD = publicOrNotD;
    }

}
