/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMSManagedBean;

import CRM.entity.ServiceOrder;
import CRM.session.ServiceOrderManagementLocal;
import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import Common.session.MessageSessionBeanLocal;
import OES.entity.Product;
import TMS.entity.Routes;
import TMS.entity.TransOrder;
import TMS.entity.Vehicles;
import TMS.session.DriverMgtSessionBeanLocal;
import TMS.session.OrderTrackingSessionBeanLocal;
import TMS.session.TransAssetMgtSessionBeanLocal;
import TMS.session.TransOperationMgtSessionBeanLocal;
import TMS.session.TransOrderMgtSessionBeanLocal;
import TMS.session.TransRouteOptiSessionBeanLocal;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import util.exception.CompanyNotExistException;
import util.exception.DriverNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;
import util.exception.ServiceOrderAlreadyEstablishedTransOrderException;
import util.exception.ServiceOrderNotApprovedException;
import util.exception.ServiceOrderNotExistException;
import util.exception.ServiceOrderNotPaidException;
import util.exception.StartNotBeforeEndException;
import util.exception.TransOrderNotExistException;
import util.exception.VehicleNoDriversException;
import util.exception.VehicleNoTimetableException;
import util.exception.VehicleNotExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "transportationOrderManagedBean")
@SessionScoped
public class TransportationOrderManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private TransOrderMgtSessionBeanLocal tomsb;
    @EJB
    private OrderTrackingSessionBeanLocal otsb;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private TransOperationMgtSessionBeanLocal topmsb;
    @EJB
    private TransRouteOptiSessionBeanLocal trosb;
    @EJB
    private DriverMgtSessionBeanLocal dmsb;
    @EJB
    private ServiceOrderManagementLocal soml;
    @EJB
    private MessageSessionBeanLocal msb;
    @EJB
    private TransAssetMgtSessionBeanLocal tamsb;

    private String ownerCompanyName;
    private String clientCompanyName;
    private TransOrder transOrder;
    private TransOrder toForRoutes;
    private ServiceOrder so;
    private ServiceOrder serviceOrder;
    private Long soId;
    private Timestamp creationTime;
    private Double price;
    private Double quantity;
    private Product product;
    private String productUnit;
    private Long warehouseId;
    private Long serviceContractId;
    private Boolean approvedOrNot;
    private Boolean paidOrNot;
    private Boolean establishedTransOrderOrNot;
    private Boolean establishedWarehouseOrderOrNot;
    private Boolean fulfilledOrNot;

    private String transOrderId = "0";
    private Long relatedContractId;
    private Long relatedServiceOrderId;
    private Long relatedRouteId = 0L;
    private Long transportationOrderId = 0L;
    private Boolean fulfillOrNot;
    private Date startDate;
    private Date endDate;
    private String description;
    private String productName;
    private String productQuantity;

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

    private String sourceLat;
    private String sourceLng;
    private String destLat;
    private String destLng;

    private String currCountry;
    private String currState;
    private String currCity;
    private String currStreet;
    private String currBlockNo;
    private String currPostalCode;

    private String currAddress;

    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;

    private String statusMessage;
    private String trackingNumber;

    private Long ownerId;
    private List<Routes> assignedRoutes = new ArrayList<>();
    private Routes[] routesArray;
    private List<Vehicles> selectedVehicle = new ArrayList<>();
    private Vehicles[] vehicleArray;

    private List<Routes> shortestRoutes;
    private List<Routes> leastRoutes;
    private double distance;
    private int transit;

    private Date deliverStartDate;
    private Date deliverEndDate;
    private Timestamp deliverStartTime;
    private Timestamp deliverEndTime;

    private Location start;
    private Location end;

    private Vehicles vehicle;
    private ScheduleModel eventModel = new DefaultScheduleModel();
    private String routeMode;
    private Routes relatedRoute;
    
    private List<Routes> rList;

    @PostConstruct
    public void init() {
        ownerId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(ownerId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "retrieveSuitableServiceQuotationListWithSepcificCompany: "
                    + statusMessage, ""));
        }
    }

    /**
     * Creates a new instance of TransportationOrderManagedBean
     */
    public TransportationOrderManagedBean() {
    }

    public List<ServiceOrder> retrieveServiceOrderListForTransOrderCreation() throws IOException {
        try {
//            redirectToPage("/MerLION-war/CRMWeb/serviceOrder/viewOwningServiceOrderListForTransOrderCreation.xhtml");
            return soml.retrieveServiceOrderListForTransOrderCreation(ownerCompanyName);
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = "ServiceOrderNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        return null;
    }

    public void retrieveServiceOrderForTransOrderCreation(ServiceOrder serviceOrder) throws IOException {
        System.out.println("Entering ServiceOrderManagedBean: retrieveServiceOrderForTransOrderCreation");
        so = serviceOrder;
        soId = so.getId();
        ownerCompanyName = so.getServiceContract().getServiceQuotation().getpCompany().getCompanyName();
        clientCompanyName = so.getServiceContract().getServiceQuotation().getClientCompany().getCompanyName();
        creationTime = so.getCreationTime();
        startDate = so.getStartTime();
        endDate = so.getEndTime();
        description = so.getDescription();
        product = so.getProduct();
        productName = product.getProductName();
        productUnit = product.getUnit();
        quantity = so.getQuantity();
        price = so.getPrice();
        sourceCountry = so.getSourceLoc().getCountry();
        sourceState = so.getSourceLoc().getState();
        sourceCity = so.getSourceLoc().getCity();
        sourceStreet = so.getSourceLoc().getStreet();
        sourceBlockNo = so.getSourceLoc().getBlockNo();
        sourcePostalCode = so.getSourceLoc().getPostalCode();
        destCountry = so.getDestLoc().getCountry();
        destState = so.getDestLoc().getState();
        destCity = so.getDestLoc().getCity();
        destStreet = so.getDestLoc().getStreet();
        destBlockNo = so.getDestLoc().getBlockNo();
        destPostalCode = so.getDestLoc().getPostalCode();
        warehouseId = so.getWarehouseId();
        serviceContractId = so.getServiceContract().getId();
        perishable = so.isPerishable();
        flammable = so.isFlammable();
        pharmaceutical = so.isPharmaceutical();
        highValue = so.isHighValue();
        approvedOrNot = so.isApprovedOrNot();
        paidOrNot = so.isPaidOrNot();
        fulfilledOrNot = so.isFulfilledOrNot();
        establishedTransOrderOrNot = so.getEstablishedTransOrderOrNot();
        establishedWarehouseOrderOrNot = so.getEstablishedWarehouseOrderOrNot();

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Retrieveing", null);
        FacesContext.getCurrentInstance().addMessage(null, message);

        System.out.println("End of ServiceOrderManagedBean: retrieveServiceOrderForTransOrderCreation");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/transOrderCreation.xhtml");
        System.out.println("Redirected to transOrderCreation.xhtml");
    }

    public void createTransOrder() {
        System.out.println("Entering ServiceOrderManagedBean: createTransOrder");
        try {
            //soList.remove(so);
            transportationOrderId = soml.createTransportationOrder(soId);
            transOrder = tomsb.retrieveTransOrder(transportationOrderId);
            msb.createMessage(so.getServiceContract().getProvider().getId(), so.getServiceContract().getClient().getId(), "Tracking Number for Order " + transOrder.getTransOrderId(), transOrder.getTrackingNumber());
            statusMessage = "TransOrder creation successful!";
            this.faceMsg(statusMessage);
            RequestContext.getCurrentInstance().execute("PF('dlg').show()");
            System.out.println("createTransOrder complete.");
        } catch (ServiceOrderNotExistException ex) {
            statusMessage = "ServiceOrderNotExistException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceOrderNotApprovedException ex) {
            statusMessage = "ServiceOrderNotApprovedException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceOrderAlreadyEstablishedTransOrderException ex) {
            statusMessage = "ServiceOrderAlreadyEstablishedTransOrWarehouseOrderException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        } catch (ServiceOrderNotPaidException ex) {
            statusMessage = "ServiceOrderNotPaidException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        } catch (TransOrderNotExistException ex) {
            statusMessage = "TransOrderNotEixstException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        } catch (WarehouseNotExistException ex) {
            statusMessage = "WarehouseNotExistException: " + ex.getMessage();
            this.errorMsg(statusMessage);
            System.out.println(statusMessage);
        }
        System.out.println("End of ServiceOrderManagedBean: createTransOrder");
    }

    public void redirectToViewOwningServiceOrderListForTransOrderCreation() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/viewTransOrder.xhtml");
    }

    public List<TransOrder> getAllTransOrder() {
        try {
            return tomsb.viewAllTransOrders(ownerId);
        } catch (CompanyNotExistException sex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                 "No Company ID exception!!" , ""));  
        } catch (TransOrderNotExistException sex) {
            this.errorMsg("transorder not found");
        } catch (NumberFormatException sex) {
            this.errorMsg(statusMessage);
        }
        return null;
    }

    public void goToTrackingNumber(TransOrder order) {
        relatedServiceOrderId = order.getServiceOrder().getId();
        trackingNumber = order.getTrackingNumber();
        RequestContext.getCurrentInstance().execute("PF('Dialog2').show()");
    }

    public void goToUpdate(TransOrder order) throws IOException {
        transOrder = order;
        transOrderId = order.getTransOrderId().toString();
        ownerCompanyName = order.getServiceOrder().getServiceContract().getProvider().getCompanyName();
        clientCompanyName = order.getServiceOrder().getServiceContract().getClient().getCompanyName();
        relatedContractId = order.getServiceOrder().getServiceContract().getId();
        relatedServiceOrderId = order.getServiceOrder().getId();
        fulfillOrNot = order.getFulfilledOrNot();
        serviceOrder = order.getServiceOrder();
        description = order.getServiceOrder().getDescription();
        product = order.getServiceOrder().getProduct();
        productName = order.getProduct().getProductName();
        productQuantity = order.getQuantity().toString();
        sourceCountry = order.getStartPoint().getCountry();
        sourceState = order.getStartPoint().getState();
        sourceCity = order.getStartPoint().getCity();
        sourceStreet = order.getStartPoint().getStreet();
        sourceBlockNo = order.getStartPoint().getBlockNo();
        sourcePostalCode = order.getStartPoint().getPostalCode();

        destCountry = order.getEndPoint().getCountry();
        destState = order.getEndPoint().getState();
        destCity = order.getEndPoint().getCity();
        destStreet = order.getEndPoint().getStreet();
        destBlockNo = order.getEndPoint().getBlockNo();
        destPostalCode = order.getEndPoint().getPostalCode();

        currCountry = order.getCurrentLocation().getCountry();
        currState = order.getCurrentLocation().getState();
        currCity = order.getCurrentLocation().getCity();
        currStreet = order.getCurrentLocation().getStreet();
        currBlockNo = order.getCurrentLocation().getBlockNo();
        currPostalCode = order.getCurrentLocation().getPostalCode();

        perishable = order.getPerishable();
        flammable = order.getFlammable();
        pharmaceutical = order.getPharmaceutical();
        highValue = order.getHighValue();

        // Geocoding~~
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(order.getStartPoint().toString()).setLanguage("en").getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        GeocoderRequest geocoderRequest2 = new GeocoderRequestBuilder().setAddress(order.getEndPoint().toString()).setLanguage("en").getGeocoderRequest();
        GeocodeResponse geocoderResponse2 = geocoder.geocode(geocoderRequest2);
        if (!geocoderResponse.getResults().isEmpty()) {
            sourceLat = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().toPlainString();
            sourceLng = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().toPlainString();
        } else {
            sourceLat = null;
            sourceLng = null;
        }
        if (!geocoderResponse2.getResults().isEmpty()) {
            destLat = geocoderResponse2.getResults().get(0).getGeometry().getLocation().getLat().toPlainString();
            destLng = geocoderResponse2.getResults().get(0).getGeometry().getLocation().getLng().toPlainString();
        } else {
            destLat = null;
            destLng = null;
        }
        //end of geocoding

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/updateTransportationOrder.xhtml");
    }

    public void updateTransOrder() throws IOException {
        System.out.println("Entering ServiceOrderManagedBean: updateTransOrder");
        if (transOrder.getFulfilledOrNot()) {
            this.errorMsg("Cannot update a fulfilled transportation order!");
            return;
        }
        Location curr = new Location(currCountry, currState, currCity, currStreet, currBlockNo, currPostalCode);
        try {
            tomsb.updateTransOrder(ownerId, relatedServiceOrderId, curr);
            this.faceMsg("Transportation order updated successfully!");
        } catch (TransOrderNotExistException ex) {
            this.errorMsg("transportation order not found!");
        } catch (CompanyNotExistException ex) {
            this.errorMsg("company not found!");
        } catch (ServiceOrderNotExistException ex) {
            this.errorMsg("order not found!");
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/viewTransOrder.xhtml");
    }

    public void fulfilTransOrder() throws IOException {
        System.out.println("Entering ServiceOrderManagedBean: fulfilTransOrder");
        Location curr = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
        try {
            tomsb.updateTransOrder(ownerId, relatedServiceOrderId, curr);
            this.faceMsg("Transportation order fulfilled successfully!");
        } catch (TransOrderNotExistException ex) {
            this.errorMsg("transportation order not found!");
        } catch (CompanyNotExistException ex) {
            this.errorMsg("company not found!");
        } catch (ServiceOrderNotExistException ex) {
            this.errorMsg("order not found!");
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/viewTransOrder.xhtml");
    }

    public void comeBackTracking() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/tracking/inputTrackingNumber.xhtml");
    }

    public Location trackingTransOrder() {
        try {
            return otsb.transOrderTracking(ownerId, trackingNumber);
        } catch (CompanyNotExistException sex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                 "No Company ID exception!!" , ""));  
        } catch (TransOrderNotExistException sex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No transportation order exception!!", ""));
        } catch (NumberFormatException sex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No Company ID input so far", ""));
        }
        return null;
    }

    public void goToAssign(TransOrder order) throws IOException {
        if (order.getFulfilledOrNot()) {
            this.errorMsg("Cannot design routes for a fulfilled transportation order!");
            return;
        }
        toForRoutes = order;
        transportationOrderId = order.getTransOrderId();
        start = order.getStartPoint();
        end = order.getEndPoint();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./assignRoutesToOrder.xhtml");
    }

    public void goToOpti(TransOrder order) throws IOException {
        transOrder = order;
        product = order.getProduct();
        quantity = order.getQuantity();
        transOrderId = order.getTransOrderId().toString();
        start = order.getStartPoint();
        end = order.getEndPoint();
        System.out.println("order start and end: " + start.getBlockNo() + " " + end.getBlockNo());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./optimizationTransOrder.xhtml");
    }
    
    public void goToOptiForOptiRoute(TransOrder order) throws IOException {
        transOrder = order;
        product = order.getProduct();
        quantity = order.getQuantity();
        transOrderId = order.getTransOrderId().toString();
        start = order.getStartPoint();
        end = order.getEndPoint();
        System.out.println("order start and end: " + start.getBlockNo() + " " + end.getBlockNo());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./optimizationTransOrder.xhtml");
    }

    public void goToOptiRoute(TransOrder order) throws IOException {
        transOrder = order;
        product = order.getProduct();
        quantity = order.getQuantity();
        transOrderId = order.getTransOrderId().toString();
        start = order.getStartPoint();
        end = order.getEndPoint();
        System.out.println("order start and end: " + start.getBlockNo() + " " + end.getBlockNo());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./optimizationRouteTransOrder.xhtml"); 
        }

    public Double totalDistance(List<Routes> routes) {
        Double distance = 0.0;
        for (Routes r : routes) {
            distance += r.getDistance();
        }
        return distance;
    }

    public void assignRoutesToOrder() throws IOException {
        try {
            Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            Long orderId = transportationOrderId;
            if (routesArray.length == 0) {
                this.errorMsg("Please select at least one route");
                return;
            }
            assignedRoutes = new ArrayList<>();
            for (Object o : routesArray) {
                assignedRoutes.add((Routes) o);
            }
            assignedRoutes = topmsb.checkGoThrough(cId, orderId, assignedRoutes);
            if (assignedRoutes == null) {
                statusMessage = "The routes cannot fulfill the transportation routes!";
                this.errorMsg(statusMessage);
            } else {
                System.out.println("after check, can go through");
                topmsb.assignRoutesToOrder(cId, orderId, (ArrayList<Routes>) assignedRoutes);
                this.faceMsg("Routes designed successfully!");
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                ec.redirect("./viewTransOrder.xhtml");
            }
        } catch (CompanyNotExistException ex) {
            this.errorMsg("company not found!");
        } catch (TransOrderNotExistException ex) {
            this.errorMsg("transorder not found");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("No routes records so far!");
        }
    }

    public void viewAssignedRoutes(TransOrder order) {
        transportationOrderId = order.getTransOrderId();
        RequestContext.getCurrentInstance().execute("PF('Dialog1').show()");
    }

    public List<Routes> getPossibleRoutes() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        // Long orderId = Long.parseLong(destCity)
        try {
            return trosb.possibleRoutes(cId, start, end);
        } catch (CompanyNotExistException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                 "No Company ID exception!!" , ""));         
        } catch (RoutesNotExistException ex) {
            this.errorMsg("routes not exist");
        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return null;
    }

    public void popOptimalRoute() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        distance = 0.0;
        transit = 0;
        System.out.println("Inside get optimal routes");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        // Long orderId = Long.parseLong(destCity)
        try {
            List<List<Routes>> track = trosb.optimizeTrack(cId, start, end);
            if (track.isEmpty()) {
                shortestRoutes = null;
                leastRoutes = null;
            } else {
                shortestRoutes = track.get(0);
                leastRoutes = track.get(0);
                for (List<Routes> l : track) {
                    double a = 0.0;
                    int b = 0;
                    for(Routes r: l) {
                        a += r.getDistance();
                        b += 1;
                    }
                    if(distance == 0.0) {
                        distance = a;
                    } else if(a<distance) {
                        distance = a;
                        shortestRoutes = l;
                    }
                    if(transit == 0) {
                        transit = b;
                    } else if(b<transit) {
                        transit = b;
                        leastRoutes = l;
                    }
                }
            }
        } catch (CompanyNotExistException ex) {

        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
    }

    public List<List<Routes>> getOptimalRoutes() {
        System.out.println("Inside get optimal routes");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        // Long orderId = Long.parseLong(destCity)
        try {
            return trosb.optimizeTrack(cId, start, end);
        } catch (CompanyNotExistException ex) {

        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return null;
    }
    
    public void goToOptiVehicles(List<Routes> routeList) throws IOException{
        rList = routeList;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./optimizationTransOrder.xhtml");
    }
    
    public List<Vehicles> getFastestVehicles() {      
        return trosb.CheapestVehiclesForRouteList(transOrder.getTransOrderId(), rList);      
    }

    public String getShortestTime() {
        try {
            return String.format("%.2f", trosb.fastestPriceForQuotation(ownerId, product, quantity, start, end));
        } catch (CompanyNotExistException ex) {
            displayFaceMessage("Company not exist");
        } catch (RoutesNotExistException ex) {
            displayFaceMessage("Routes not assigned yet!");
        } catch (NoVehiclesAssignedException ex) {
            this.errorMsg("There is no vehicle assigned to this route!");
        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return null;
    }

    public List<Vehicles> getCheapestVehicles() {
        return trosb.fastestVehiclesForRouteList(transOrder.getTransOrderId(), rList);
    }

    public String getCheapestPrice() {
        try {
            return String.format("%.2f", trosb.cheapestPriceForQuotation(ownerId, product, quantity, start, end));
        } catch (CompanyNotExistException ex) {
            displayFaceMessage("Company not exist");
        } catch (RoutesNotExistException ex) {
            displayFaceMessage("Routes not assigned yet!");
        } catch (NoVehiclesAssignedException ex) {
            this.errorMsg("There is no vehicle assigned to this route!");
        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return null;
    }
    
    public List<Vehicles> getValuestVehicles() {
        return trosb.ValuestVehiclesForRouteList(transOrder.getTransOrderId(), rList);
    }

    public void goBackOpti() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/viewTransOrder.xhtml");
    }

    public List<Routes> viewAssignedRoutesList() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long orderId = transportationOrderId;
        try {
            return topmsb.viewRoutes(cId, orderId);
        } catch (RoutesNotExistException sex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No Routes exception!!", ""));
        } catch (TransOrderNotExistException sex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                 "No Order exception!!" , ""));  
        } catch (CompanyNotExistException sex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                 "No Company ID exception!!" , ""));  
        }
        return null;
    }

    public void goToAssignedVehicleList(Routes r) throws IOException {
        relatedRouteId = r.getRoutesId();
        relatedRoute = r;
        if (r.getMode().equals("byTruck")) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("/MerLION-war/TMSWeb/transportationOrder/assignTruckToRouteToOrder.xhtml");
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("/MerLION-war/TMSWeb/transportationOrder/assignVehicleToRouteToOrder.xhtml");
        }
    }

    public List<Vehicles> getCapableVehicles() throws IOException {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            List<Vehicles> vList = tomsb.retrieveCapableVehicles(cId, transportationOrderId, relatedRouteId);
            if (vList == null) {
                this.faceMsg("There is no related vehicles in this route, please assign first!");
            }
            //return tomsb.retrieveCapableVehicles(cId, transportationOrderId, relatedRouteId);
            return vList;
        } catch (CompanyNotExistException ex) {
            displayFaceMessage("No company!");
        } catch (TransOrderNotExistException ex) {
            displayFaceMessage("No trans order!");
        } catch (RoutesNotExistException ex) {
            displayFaceMessage("No route!");
        } catch (VehicleNotExistException ex) {
            displayFaceMessage("No vehicle assigned!");
        }
        return null;
    }

    public List<Vehicles> getAssignedVehicles() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            return trosb.viewAssignedVehicles(cId, relatedRouteId);
        } catch (CompanyNotExistException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                    "No company ID exception!!" , ""));           
        } catch (RoutesNotExistException ex) {
            this.errorMsg("There is no routes in the system!");
        }
        return null;
    }

    public void viewAssignedVList(Routes r) {
        relatedRouteId = r.getRoutesId();
        routeMode = r.getMode();
        RequestContext.getCurrentInstance().execute("PF('Dialog1').show()");
    }

    public void goToSelectVehicles() throws IOException {
        if (routeMode.toLowerCase().equals("bytruck")) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("/MerLION-war/TMSWeb/transportationOrder/assignTruckToRouteToOrder.xhtml");
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("/MerLION-war/TMSWeb/transportationOrder/assignVehicleToRouteToOrder.xhtml");
        }
    }

    public List<Vehicles> getSelectedVehicles() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long rId = relatedRouteId;
        try {
            return trosb.viewSelectedVehicles(transportationOrderId, rId);
        } catch (RoutesNotExistException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                    "No company ID exception!!" , ""));           
        } catch (NoPossibleRoutesException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return null;
    }

    public void scheduleDrivers(Long driverId) throws IOException {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long orderId = transportationOrderId;
        try {
            if (!dmsb.arrangeTimetable(cId, orderId, driverId).equals("time conflict")) {
                RequestContext.getCurrentInstance().execute("PF('Dialog1').hide()");
            } else {
                this.errorMsg("There is no possible routes in the system to deliver the order!");
            }
        } catch (TransOrderNotExistException ex) {
            displayFaceMessage("No trans order!");
        } catch (CompanyNotExistException ex) {
            displayFaceMessage("No company !");
        } catch (DriverNotExistException ex) {
            displayFaceMessage("No driver!");
        } catch (StartNotBeforeEndException ex) {
            displayFaceMessage("start time not before end!");
        } catch (VehicleNoTimetableException ex) {
            displayFaceMessage("Please Schedule Vehicle Time first!");
        }
    }

    public String viewSelectedDriver(Long truckId) {
        Long orderId = transportationOrderId;
        try {
            if (tamsb.viewVehicles(truckId) == null) {
                displayFaceMessage("select planes");
            } else if (dmsb.viewSelectedDriver(orderId, truckId) == null) {
                return "No driver selected so far";
            } else {
                return dmsb.viewSelectedDriver(orderId, truckId).getDriverId().toString();
            }
        } catch (TransOrderNotExistException ex) {
            this.errorMsg("transorder not exist");
        } catch (VehicleNotExistException ex) {
            this.errorMsg("There is no possible routes in the system to deliver the order!");
        }
        return "No driver selected so far";
    }

    public void assignVehiclesToRouteToOrder() throws IOException {
        if (vehicleArray.length == 0) {
            this.faceMsg("Please select at least one truck");
            return;
        }
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long orderId = transportationOrderId;
        selectedVehicle = new ArrayList<>();
        Double slowest = vehicleArray[0].getVelocity();
        for (Vehicles vehicleArray1 : vehicleArray) {
            selectedVehicle.add(vehicleArray1);
            if (vehicleArray1.getVelocity() < slowest) {
                slowest = vehicleArray1.getVelocity();
            }
        }
        if (!tomsb.checkTotalCapacity(orderId, selectedVehicle)) {
            displayFaceMessage("You should select more vehicle since capacity not enough!");
        } else {
            deliverStartTime = new Timestamp(startDate.getTime());
        
            try {
                for (Vehicles v : selectedVehicle) {
                ArrayList<Timestamp> tList = (ArrayList<Timestamp>) topmsb.scheduleDeliverTime(cId, orderId, v.getVehiclesId(), deliverStartTime, slowest);
                if (tList == null) {
                    this.errorMsg("Time conflict!");
                } else {
                    System.out.println("time table size in managed bean" + tList.size());
                    String eventName = "Vehicle ID: " + v.getVehiclesId() + "\nTransportation Order: " + orderId;
                    Date from = new Date(tList.get(tList.size() - 2).getTime());
                    Date to = new Date(tList.get(tList.size() - 1).getTime());
                    eventModel.addEvent(new DefaultScheduleEvent(eventName, from, to));
                    System.out.println("after add event");
                    }
                }
                this.faceMsg("Vehicles Selected Successfully!");
                } catch (CompanyNotExistException ex) {
                    statusMessage = "CompanyNotExistException: " + ex.getMessage();
                    this.displayFaceMessage(statusMessage);
                    System.out.println(statusMessage);
                } catch (TransOrderNotExistException ex) {
                    statusMessage = "TransOrderNotExistException: " + ex.getMessage();
                    this.displayFaceMessage(statusMessage);
                    System.out.println(statusMessage);
                } catch (VehicleNotExistException ex) {
                    statusMessage = "VehicleNotExistException: " + ex.getMessage();
                    this.displayFaceMessage(statusMessage);
                    System.out.println(statusMessage);
                } catch (VehicleNoDriversException ex) {
                    statusMessage = "This Vehicle has no drivers" + ex.getMessage();
                    this.displayFaceMessage(statusMessage);
                    System.out.println(statusMessage);
                }
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("/MerLION-war/TMSWeb/transportationOrder/assignRoutesToOrder.xhtml");
        }
    }

    public void goBackTimetable() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./assignRoutesToOrder.xhtml");
    }

    public void goToTracking() throws IOException {
        populateLocation();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/tracking/trackCurrentLocation.xhtml");
    }

    public void populateLocation() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        //Long orderId = relatedServiceOrderId;
        try {
            transOrder = tomsb.retrieveTransOrder(cId, trackingNumber);
        } catch (TransOrderNotExistException ex) {
            statusMessage = "TransOrerNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        } catch (CompanyNotExistException ex) {
            statusMessage = "CompanyNotExistException: " + ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println(statusMessage);
        }
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        if (transOrder.getCurrentLocation() != null) {
            currCountry = transOrder.getCurrentLocation().getCountry();
            currState = transOrder.getCurrentLocation().getState();
            currCity = transOrder.getCurrentLocation().getCity();
            currStreet = transOrder.getCurrentLocation().getStreet();
            currBlockNo = transOrder.getCurrentLocation().getBlockNo();
            currPostalCode = transOrder.getCurrentLocation().getPostalCode();
            currAddress = currStreet + ", " + currBlockNo + ", " + currCity + " " + currState + " " + currCountry + ", " + currPostalCode;
            System.out.println(currAddress);
        }
    }

    public void goToVehicleTimetable(Vehicles v) throws IOException {
        vehicle = v;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/transportationOrder/viewVehicleTimetable.xhtml");
    }

    public void viewVehicleTimetable() {
        System.out.println("in view event");
//        ArrayList<Timestamp> tList = vehicle.getTimetable();
//        System.out.println(tList.size());
//        String eventName;
//        Date from, to;
//        for(int i=0; i<tList.size(); i+=2){
//           eventName = "Transportation Order " + vehicle.getTransOrderId();
//           from = new Date(tList.get(i).getTime());
//           to = new Date(tList.get(i+1).getTime());
//           eventModel.addEvent(new DefaultScheduleEvent(eventName, from, to));
//           System.out.println("after add event" + i);
//       }
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

    public TransOrderMgtSessionBeanLocal getTomsb() {
        return tomsb;
    }

    public void setTomsb(TransOrderMgtSessionBeanLocal tomsb) {
        this.tomsb = tomsb;
    }

    public CompanyManagementSessionBeanLocal getCmsbl() {
        return cmsbl;
    }

    public void setCmsbl(CompanyManagementSessionBeanLocal cmsbl) {
        this.cmsbl = cmsbl;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getTransOrderId() {
        return transOrderId;
    }

    public void setTransOrderId(String transOrderId) {
        this.transOrderId = transOrderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public OrderTrackingSessionBeanLocal getOtsb() {
        return otsb;
    }

    public void setOtsb(OrderTrackingSessionBeanLocal otsb) {
        this.otsb = otsb;
    }

    public Long getRelatedContractId() {
        return relatedContractId;
    }

    public void setRelatedContractId(Long relatedContractId) {
        this.relatedContractId = relatedContractId;
    }

    public Long getRelatedServiceOrderId() {
        return relatedServiceOrderId;
    }

    public void setRelatedServiceOrderId(Long relatedServiceOrderId) {
        this.relatedServiceOrderId = relatedServiceOrderId;
    }

    public Boolean getFulfillOrNot() {
        return fulfillOrNot;
    }

    public void setFulfillOrNot(Boolean fulfillOrNot) {
        this.fulfillOrNot = fulfillOrNot;
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

    public String getCurrCountry() {
        return currCountry;
    }

    public void setCurrCountry(String currCountry) {
        this.currCountry = currCountry;
    }

    public String getCurrState() {
        return currState;
    }

    public void setCurrState(String currState) {
        this.currState = currState;
    }

    public String getCurrCity() {
        return currCity;
    }

    public void setCurrCity(String currCity) {
        this.currCity = currCity;
    }

    public String getCurrStreet() {
        return currStreet;
    }

    public void setCurrStreet(String currStreet) {
        this.currStreet = currStreet;
    }

    public String getCurrBlockNo() {
        return currBlockNo;
    }

    public void setCurrBlockNo(String currBlockNo) {
        this.currBlockNo = currBlockNo;
    }

    public String getCurrPostalCode() {
        return currPostalCode;
    }

    public void setCurrPostalCode(String currPostalCode) {
        this.currPostalCode = currPostalCode;
    }

    public List<Routes> getAssignedRoutes() {
        return assignedRoutes;
    }

    public void setAssignedRoutes(List<Routes> assignedRoutes) {
        this.assignedRoutes = assignedRoutes;
    }

    public Routes[] getRoutesArray() {
        return routesArray;
    }

    public void setRoutesArray(Routes[] routesArray) {
        this.routesArray = routesArray;
    }

    public TransOperationMgtSessionBeanLocal getTopmsb() {
        return topmsb;
    }

    public void setTopmsb(TransOperationMgtSessionBeanLocal topmsb) {
        this.topmsb = topmsb;
    }

    public Date getDeliverStartDate() {
        return deliverStartDate;
    }

    public void setDeliverStartDate(Date deliverStartDate) {
        this.deliverStartDate = deliverStartDate;
    }

    public Date getDeliverEndDate() {
        return deliverEndDate;
    }

    public void setDeliverEndDate(Date deliverEndDate) {
        this.deliverEndDate = deliverEndDate;
    }

    public Timestamp getDeliverStartTime() {
        return deliverStartTime;
    }

    public void setDeliverStartTime(Timestamp deliverStartTime) {
        this.deliverStartTime = deliverStartTime;
    }

    public Timestamp getDeliverEndTime() {
        return deliverEndTime;
    }

    public void setDeliverEndTime(Timestamp deliverEndTime) {
        this.deliverEndTime = deliverEndTime;
    }

    public Vehicles[] getVehicleArray() {
        return vehicleArray;
    }

    public void setVehicleArray(Vehicles[] vehicleArray) {
        this.vehicleArray = vehicleArray;
    }

    public Long getRelatedRouteId() {
        return relatedRouteId;
    }

    public void setRelatedRouteId(Long relatedRouteId) {
        this.relatedRouteId = relatedRouteId;
    }

    public List<Vehicles> getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(List<Vehicles> selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public TransRouteOptiSessionBeanLocal getTrosb() {
        return trosb;
    }

    public void setTrosb(TransRouteOptiSessionBeanLocal trosb) {
        this.trosb = trosb;
    }

    public DriverMgtSessionBeanLocal getDmsb() {
        return dmsb;
    }

    public void setDmsb(DriverMgtSessionBeanLocal dmsb) {
        this.dmsb = dmsb;
    }

    public String getCurrAddress() {
        return currAddress;
    }

    public void setCurrAddress(String currAddress) {
        this.currAddress = currAddress;
    }

    public TransOrder getTransOrder() {
        return transOrder;
    }

    public void setTransOrder(TransOrder transOrder) {
        this.transOrder = transOrder;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public Vehicles getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicles vehicle) {
        this.vehicle = vehicle;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ServiceOrderManagementLocal getSoml() {
        return soml;
    }

    public void setSoml(ServiceOrderManagementLocal soml) {
        this.soml = soml;
    }

    public ServiceOrder getSo() {
        return so;
    }

    public void setSo(ServiceOrder so) {
        this.so = so;
    }

    public Long getSoId() {
        return soId;
    }

    public void setSoId(Long soId) {
        this.soId = soId;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public Boolean getApprovedOrNot() {
        return approvedOrNot;
    }

    public void setApprovedOrNot(Boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    public Boolean getPaidOrNot() {
        return paidOrNot;
    }

    public void setPaidOrNot(Boolean paidOrNot) {
        this.paidOrNot = paidOrNot;
    }

    public Boolean getEstablishedTransOrderOrNot() {
        return establishedTransOrderOrNot;
    }

    public void setEstablishedTransOrderOrNot(Boolean establishedTransOrderOrNot) {
        this.establishedTransOrderOrNot = establishedTransOrderOrNot;
    }

    public Boolean getEstablishedWarehouseOrderOrNot() {
        return establishedWarehouseOrderOrNot;
    }

    public void setEstablishedWarehouseOrderOrNot(Boolean establishedWarehouseOrderOrNot) {
        this.establishedWarehouseOrderOrNot = establishedWarehouseOrderOrNot;
    }

    public Boolean getFulfilledOrNot() {
        return fulfilledOrNot;
    }

    public void setFulfilledOrNot(Boolean fulfilledOrNot) {
        this.fulfilledOrNot = fulfilledOrNot;
    }

    public Long getTransportationOrderId() {
        return transportationOrderId;
    }

    public void setTransportationOrderId(Long transportationOrderId) {
        this.transportationOrderId = transportationOrderId;
    }

    public MessageSessionBeanLocal getMsb() {
        return msb;
    }

    public void setMsb(MessageSessionBeanLocal msb) {
        this.msb = msb;
    }

    public TransAssetMgtSessionBeanLocal getTamsb() {
        return tamsb;
    }

    public void setTamsb(TransAssetMgtSessionBeanLocal tamsb) {
        this.tamsb = tamsb;
    }

    public String getRouteMode() {
        return routeMode;
    }

    public void setRouteMode(String routeMode) {
        this.routeMode = routeMode;
    }

    public Routes getRelatedRoute() {
        return relatedRoute;
    }

    public void setRelatedRoute(Routes relatedRoute) {
        this.relatedRoute = relatedRoute;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(String sourceLat) {
        this.sourceLat = sourceLat;
    }

    public String getSourceLng() {
        return sourceLng;
    }

    public void setSourceLng(String sourceLng) {
        this.sourceLng = sourceLng;
    }

    public String getDestLat() {
        return destLat;
    }

    public void setDestLat(String destLat) {
        this.destLat = destLat;
    }

    public String getDestLng() {
        return destLng;
    }

    public void setDestLng(String destLng) {
        this.destLng = destLng;
    }

    public List<Routes> getShortestRoutes() {
        return shortestRoutes;
    }

    public void setShortestRoutes(List<Routes> shortestRoutes) {
        this.shortestRoutes = shortestRoutes;
    }

    public List<Routes> getLeastRoutes() {
        return leastRoutes;
    }

    public void setLeastRoutes(List<Routes> leastRoutes) {
        this.leastRoutes = leastRoutes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTransit() {
        return transit;
    }

    public void setTransit(int transit) {
        this.transit = transit;
    }

    public List<Routes> getrList() {
        return rList;
    }

    public void setrList(List<Routes> rList) {
        this.rList = rList;
    }

    public TransOrder getToForRoutes() {
        return toForRoutes;
    }

    public void setToForRoutes(TransOrder toForRoutes) {
        this.toForRoutes = toForRoutes;
    }

}
