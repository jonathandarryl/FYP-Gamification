/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.ServiceCatalog;
import CRM.entity.ServiceQuotation;
import CRM.session.ServiceInfoMgtSessionBeanLocal;
import CRM.session.ServiceQuotationManagementSessionLocal;
import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import util.exception.MultipleProductWithSameNameException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.ProductNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceCatalogNotExistException;
import util.exception.ServiceCatalogNotPublishedException;
import util.exception.ServiceQuotationAlreadyApprovedException;
import util.exception.ServiceQuotationCannotBeAchivedException;
import util.exception.ServiceQuotationNotExistException;
import util.exception.ServiceQuotationStartAfterCatalogExpireException;
import util.exception.ServiceQuotationStartBeforeCatalogCommmenceException;
import util.exception.SpecialRequirementNotMetException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author songhan
 */
@Named(value = "serviceQuotationManagedBean")
@SessionScoped
public class ServiceQuotationManagedBean implements Serializable {

    /**
     * Creates a new instance of serviceQuotationManagedBean
     */
    public ServiceQuotationManagedBean() {
    }

    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private ServiceInfoMgtSessionBeanLocal sim;
    @EJB
    private ServiceQuotationManagementSessionLocal sqmsl;
    @EJB
    private ProductInfoManagementLocal pim;

    private String statusMessage;
    private String statusMessage2;
    private Company userCompany;
    private String userCompanyName;
    private String counterPartCompanyName;
    private Long userCompanyId;

    private ServiceQuotation sq;
    private Date startDate;
    private Date endDate;
    private String description;

    private String productName;
    private String productOwnerName;
    private Product product;
    private Double quantity;

    private Integer duration;
    private Integer orderNumber;

    private Double warehouseBufferRatio;

    private ServiceCatalog serviceCatalog;

    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;

    private Double price;

    private List<Double> priceList;

    @PostConstruct
    public void init() {
        userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        if (userCompany.getCompanyType().equals("1PL")) {
            productOwnerName = userCompanyName;
        }
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

    public List<ServiceCatalog> retrieveServiceCatalogList() {
        System.out.println("Enter ServiceQuotationManagedBean: retrieveServiceCatalogList");
        List<ServiceCatalog> result = new ArrayList<>();
        try {
            result = sim.viewAllServiceCatalog(userCompanyId);
        } catch (ServiceCatalogNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceCatalogNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: retrieveServiceCatalogList");
        return result;
    }

    public List<ServiceCatalog> retrievePublicServiceCatalogList() {
        System.out.println("Enter ServiceQuotationManagedBean: retrievePublicServiceCatalogList");
        List<ServiceCatalog> result = new ArrayList<>();
        try {
            result = sim.retrievePublicServiceCatalog();
        } catch (ServiceCatalogNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceCatalogNotExistException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: retrievePublicServiceCatalogList");
        return result;
    }

    public void retrieveServiceCatalog(ServiceCatalog sc) throws IOException {
        serviceCatalog = sc;
        redirectToPage("createServiceQuotation.xhtml");
    }

    public void retrievePublicServiceCatalog(ServiceCatalog sc) throws IOException {
        serviceCatalog = sc;
        redirectToPage("requestForServiceQuotationCreation.xhtml");
    }

    public void createServiceQuotation() {
        System.out.println("Enter ServiceQuotationManagedBean: createServiceQuotation");
        try {
            product = pim.retrieveGeneralProduct(productOwnerName, productName);
            sq = sqmsl.initiateServiceQuotation(serviceCatalog, counterPartCompanyName, description, startDate, endDate, product, quantity, duration, orderNumber);
            priceList = new ArrayList<>();
            priceList.add(0, sq.getOptiCheapestPrice());
            priceList.add(1, sq.getOptiPriceForShortestTime());
            priceList.add(2, sq.getOptiValuestPrice());
            statusMessage = "Service Quotation creation successful";
            System.out.println(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg5').show()");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
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
        System.out.println("End of ServiceQuotationManagedBean: createServiceQuotation");
    }

    public void requestForServiceQuotation() {
        System.out.println("Enter ServiceQuotationManagedBean: requestForServiceQuotation");
        try {
            product = pim.retrieveGeneralProduct(productOwnerName, productName);
            sq = sqmsl.requestForServiceQuotation(serviceCatalog, userCompanyName, description, startDate, endDate, perishable, flammable, pharmaceutical, highValue, product, quantity, duration, orderNumber);
            priceList = new ArrayList<>();
            Double cheapest = Math.ceil(sq.getOptiCheapestPrice());
            Double shortest = Math.ceil(sq.getOptiPriceForShortestTime());
            Double efficient = Math.ceil(sq.getOptiValuestPrice());
            priceList.add(0, cheapest);
            priceList.add(1, shortest);
            priceList.add(2, efficient);
            System.out.println("cheapest: " + sq.getOptiCheapestPrice());
            System.out.println("shortest time: " + sq.getOptiPriceForShortestTime());
            System.out.println("cost-efficient: " + sq.getOptiValuestPrice());
            statusMessage = "Service Quotation Request Sent Successfully";
            System.out.println(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg5').show()");
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("WarehouseNotExistException: " + statusMessage);
        } catch (ServiceCatalogNotPublishedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceCatalogNotPublishedException: " + statusMessage);
        } catch (SpecialRequirementNotMetException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("SpecialRequirementNotMetException: " + statusMessage);
        } catch (ProductNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ProductNotExistException: " + statusMessage);
        } catch (MultipleProductWithSameNameException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("MultipleProductWithSameNameException: " + statusMessage);
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
        System.out.println("End of ServiceQuotationManagedBean: requestForServiceQuotation");
    }

    public void choosePrice() {
        System.out.println("Enter ServiceQuotationManagedBean: choosePrice");
        sqmsl.choosePrice(price, sq);
        statusMessage = "Choose $" + price;
        RequestContext.getCurrentInstance().execute("PF('dlg5').hide()");
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
        System.out.println(statusMessage);
        System.out.println("End of ServiceQuotationManagedBean: choosePrice");
    }

    public List<ServiceQuotation> retrieveServiceQuotationList() {
        List<ServiceQuotation> result = new ArrayList<>();
        try {
            result = sqmsl.retrieveAllServiceQuotationList(userCompanyName);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }

        return result;
    }

    public List<ServiceQuotation> retrieveOutgoingServiceQuotationList() {
        List<ServiceQuotation> result = new ArrayList<>();
        try {
            result = sqmsl.retrieveOutsourcingServiceQuotationList(userCompanyName);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
        return result;
    }

    public void approveServiceQuotation(ServiceQuotation serviceQuotation) {
        System.out.println("Enter ServiceQuotationManagedBean: approveServiceQuotation");
        try {
            sqmsl.approveServiceQuotation(serviceQuotation.getId());
            statusMessage = "ServiceQuotation " + serviceQuotation.getId() + " has been approved!";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyApprovedException: " + statusMessage);
        }

        System.out.println("End of ServiceQuotationManagedBean: approveServiceQuotation");
    }

    public void approveServiceQuotationInViewPage() {
        System.out.println("Enter ServiceQuotationManagedBean: approveServiceQuotationInViewPage");
        try {
            sqmsl.approveServiceQuotation(sq.getId());
            statusMessage = "Approve successful!";
            RequestContext.getCurrentInstance().execute("PF('dlg4').show()");
            System.out.println("ServiceQuotation " + sq.getId() + " has been approved!");
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyApprovedException: " + statusMessage);
        }

        System.out.println("End of ServiceQuotationManagedBean: approveServiceQuotationInViewPage");
    }

    public void approveOutsourcingServiceQuotation(ServiceQuotation serviceQuotation) {
        System.out.println("Enter ServiceQuotationManagedBean: approveOutsourcingServiceQuotation");
        try {
            sqmsl.approveOutsourcingServiceQuotation(serviceQuotation.getId());
            statusMessage = "ServiceQuotation " + serviceQuotation.getId() + " has been approved!";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyApprovedException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: approveOutsourcingServiceQuotation");
    }

    public void approveOutsourcingServiceQuotationInViewPage() {
        System.out.println("Enter ServiceQuotationManagedBean: approveOutsourcingServiceQuotationInViewPage");
        try {
            sqmsl.approveOutsourcingServiceQuotation(sq.getId());
            statusMessage = "ServiceQuotation " + sq.getId() + " has been approved!";
            this.displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg1').show()");
            System.out.println(statusMessage);
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationAlreadyApprovedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationAlreadyApprovedException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: approveOutsourcingServiceQuotationInViewPage");
    }

    public void archiveServiceQuotation(ServiceQuotation serviceQuotation) throws IOException {
        System.out.println("Enter ServiceQuotationManagedBean: archiveServiceQuotation");
        try {
            sqmsl.removeServiceQuotation(serviceQuotation.getId());
            statusMessage = "ServiceQuotation " + serviceQuotation.getId() + " has been Archived!";
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);

        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationCannotBeAchivedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationCannotBeAchivedException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: archiveServiceQuotation");
    }

    public void archiveServiceQuotationInViewPage() throws IOException {
        System.out.println("Enter ServiceQuotationManagedBean: archiveServiceQuotationInViewPage");
        try {
            sqmsl.removeServiceQuotation(sq.getId());
            statusMessage = "Finished archive sq " + sq.getId();
            System.out.println("Finished archive sq " + sq.getId());
            displayFaceMessage(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg1').show()");
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        } catch (ServiceQuotationCannotBeAchivedException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationCannotBeAchivedException: " + statusMessage);
        }
        System.out.println("End of ServiceQuotationManagedBean: archiveServiceQuotationInViewPage");
    }

    public void rejectServiceQuotationRequest(ServiceQuotation sq) {
        System.out.println("Enter ServiceQuotationManagedBean: rejectServiceQuotationRequest");
        sqmsl.rejectIncomingServiceQuotation(sq);
        statusMessage = "Service Quotation " + sq.getId() + " rejected";
        displayFaceMessage(statusMessage);
        System.out.println("End of ServiceQuotationManagedBean: rejectServiceQuotationRequest");
    }

    public void rejectServiceQuotationRequestInView() {
        System.out.println("Enter ServiceQuotationManagedBean: rejectServiceQuotationRequestInView");
        sqmsl.rejectIncomingServiceQuotation(sq);
        statusMessage = "Service Quotation " + sq.getId() + " rejected";
        displayFaceMessage(statusMessage);
        System.out.println("End of ServiceQuotationManagedBean: rejectServiceQuotationRequestInView");
    }

    public void verifyServiceQuotationWarehouse() {
        System.out.println("Enter ServiceQuotationManagedBean: verifyServiceQuotationWarehouse");
        RequestContext.getCurrentInstance().execute("PF('dlg2').show()");
        System.out.println("End of ServiceQuotationManagedBean: verifyServiceQuotationWarehouse");
    }

    public String retrieveWarehouseVerificationResult() {
        String result = "Verification result Not available";
        try {
            if (sqmsl.verifyServiceQuotationWarehouse(sq.getId(), 0.0)) {
                result = "Our company's warehouses have sufficient storage space for Service Quotation " + sq.getId();
                statusMessage = "";
            } 
            else {
                result = "Our company's warehouses will not have sufficient storage space for Service Quotation " + sq.getId();
                statusMessage = "Seeking other logistic companys for help is advisable";
            }
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        }
        return result;
    }

    public void verifyServiceQuotationTransportation() {
        System.out.println("Enter ServiceQuotationManagedBean: verifyServiceQuotationTransportation");
        try {
            if (sqmsl.verifyServiceQuotationTransportation(sq.getId())) {
                statusMessage = "Our company's transportation assets have sufficient capacity for Service Quotation " + sq.getId();
            } else {
                statusMessage = "Our company's transportation assets will not have sufficient capacity for Service Quotation " + sq.getId();
                statusMessage2 = "Seeking other logistic companys for help is advisable";
            }

            RequestContext.getCurrentInstance().execute("PF('dlg3').show()");
            System.out.println("Transportation capacity verification complete.");
        } catch (ServiceQuotationNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("ServiceQuotationNotExistException: " + statusMessage);
        }

        System.out.println("End of ServiceQuotationManagedBean: verifyServiceQuotationTransportation");
    }

    public void retrieveServiceQuotation(ServiceQuotation sq) throws IOException {
        System.out.println("Enter ServiceQuotationManagedBean: retrieveServiceQuotation");
        this.sq = sq;
        description = sq.getDescription();
        startDate = sq.getStartDate();
        endDate = sq.getEndDate();
        quantity = sq.getEstimatedQuantity();
        duration = sq.getStorageDurationDays();
        orderNumber = sq.getOrderNumber();
        redirectToPage("viewAndUpdateServiceQuotation.xhtml");
        System.out.println("End of ServiceQuotationManagedBean: retrieveServiceQuotation");
    }

    public void retrieveOutgoingServiceQuotation(ServiceQuotation sq) throws IOException {
        System.out.println("Enter ServiceQuotationManagedBean: retrieveOutgoingServiceQuotation");
        this.sq = sq;
        description = sq.getDescription();
        startDate = sq.getStartDate();
        endDate = sq.getEndDate();
        quantity = sq.getEstimatedQuantity();
        duration = sq.getStorageDurationDays();
        orderNumber = sq.getOrderNumber();
        redirectToPage("viewAndUpdateOutgoingServiceQuotation.xhtml");
        System.out.println("End of ServiceQuotationManagedBean: retrieveOutgoingServiceQuotation");
    }

    public void updateServiceQuotation() {
        System.out.println("Enter ServiceQuotationManagedBean: updateServiceQuotation");
        try {
            sq = sqmsl.updateServiceQuotation(sq, description, startDate, endDate, quantity, duration, orderNumber);
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
            System.out.println("NoVehiclesAssignedException: " + statusMessage);
        }
        priceList = new ArrayList<>();
        priceList.add(0, sq.getOptiCheapestPrice());
        priceList.add(1, sq.getOptiPriceForShortestTime());
        priceList.add(2, sq.getOptiValuestPrice());
        statusMessage = "Update complete";
        displayFaceMessage(statusMessage);
        RequestContext.getCurrentInstance().execute("PF('dlg5').show()");
        System.out.println("Update complete.");
        System.out.println("End of ServiceQuotationManagedBean: updateServiceQuotation");
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

    public void redirectToViewServiceQuotationList() throws IOException {
        redirectToPage("viewServiceQuotationList.xhtml");
    }

    public void redirectToViewOutgoingServiceQuotationList() throws IOException {
        redirectToPage("viewOutgoingServiceQuotationList.xhtml");
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

    public ServiceCatalog getServiceCatalog() {
        return serviceCatalog;
    }

    public void setServiceCatalog(ServiceCatalog serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    public Double getWarehouseBufferRatio() {
        return warehouseBufferRatio;
    }

    public void setWarehouseBufferRatio(Double warehouseBufferRatio) {
        this.warehouseBufferRatio = warehouseBufferRatio;
    }

    public String getCounterPartCompanyName() {
        return counterPartCompanyName;
    }

    public void setCounterPartCompanyName(String counterPartCompanyName) {
        this.counterPartCompanyName = counterPartCompanyName;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductOwnerName() {
        return productOwnerName;
    }

    public void setProductOwnerName(String productOwnerName) {
        this.productOwnerName = productOwnerName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Double> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<Double> priceList) {
        this.priceList = priceList;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatusMessage2() {
        return statusMessage2;
    }

    public void setStatusMessage2(String statusMessage2) {
        this.statusMessage2 = statusMessage2;
    }

}
