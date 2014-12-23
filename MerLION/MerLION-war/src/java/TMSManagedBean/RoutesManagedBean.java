/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMSManagedBean;

import Common.entity.Location;
import TMS.entity.Routes;
import TMS.entity.Vehicles;
import TMS.session.TransAssetMgtSessionBeanLocal;
import TMS.session.TransOperationMgtSessionBeanLocal;
import TMS.session.TransRouteOptiSessionBeanLocal;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.RoutesNotExistException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "routesManagedBean")
@SessionScoped
public class RoutesManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private TransRouteOptiSessionBeanLocal trosb;
    @EJB
    private TransOperationMgtSessionBeanLocal tomsb;
    @EJB
    private TransAssetMgtSessionBeanLocal tamsb;

//    private final GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyA9xH_zqONP2OGG8MGgkkgWuDz78ACcr4w");
    private String sourceCountry;
    private String sourceState;
    private String sourceCity;
    private String sourceStreet;
    private String sourceBlockNo;
    private String sourcePostalCode;
    private Location sourceLocation;

    private String destCountry;
    private String destState;
    private String destCity;
    private String destStreet;
    private String destBlockNo;
    private String destPostalCode;
    private Location destLocation;

    private String sourceLat;
    private String sourceLng;
    private String destLat;
    private String destLng;
    private boolean isTruck;

    private String companyId;
    private String routeId = "1";
    private String distance;
    private String mode;

    private ArrayList<Vehicles> vList = new ArrayList<>();
    private ArrayList<Vehicles> tList = new ArrayList<>();
    private ArrayList<Vehicles> pList = new ArrayList<>();
    private Vehicles[] vehicleArray;
    private Vehicles[] truckArray;
    private Vehicles[] planeArray;

    /**
     * Creates a new instance of RoutesManagedBean
     */
    public RoutesManagedBean() {
    }

    public void addNewRoute() throws IOException {
        Location source = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
        Location dest = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Double d = Double.parseDouble(distance);
        try {
            trosb.addNewRoutes(cId, source, dest, mode, d);
            this.faceMsg("Routes added successfully!");
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./routesMgtPage.xhtml");
    }

    public List<Routes> getRoutesList() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            return trosb.viewAllRoutes(cId);
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("No Routes retrieved!");
        }
        return null;
    }

    public List<Vehicles> getVesselList() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            return tamsb.viewAllVessel(cId);
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (VehicleNotExistException ex) {
            this.errorMsg("Vehicles cannot retrieve!");
        }
        return null;
    }

    public List<Vehicles> getTruckList() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            return tamsb.viewAllTrucks(cId);
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (VehicleNotExistException ex) {
            this.errorMsg("Vehicle cannot retrieve!");
        }
        return null;
    }

    public List<Vehicles> getPlaneList() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            return tamsb.viewAllPlanes(cId);
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (VehicleNotExistException ex) {
            this.errorMsg("vehicle cannot retrieve!");
        }
        return null;
    }

    public void deleteRoutes(Long rId) {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try {
            trosb.deleteRoutes(cId, rId);
            this.faceMsg("Routes record deleted successfully!");
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("Routes cannot retrieve!");
        }
    }

    public void gotoUpdate(Routes r) throws IOException, Exception {
        if (r.getMode().equals("byTruck")) {
            isTruck = true;
        } else {
            isTruck = false;
        }
        sourceCountry = r.getStartOfRoute().getCountry();
        sourceState = r.getStartOfRoute().getState();
        sourceCity = r.getStartOfRoute().getCity();
        sourceStreet = r.getStartOfRoute().getStreet();
        sourceBlockNo = r.getStartOfRoute().getBlockNo();
        sourcePostalCode = r.getStartOfRoute().getPostalCode();
        sourceLocation = r.getStartOfRoute();

        destCountry = r.getDestOfRoute().getCountry();
        destState = r.getDestOfRoute().getState();
        destCity = r.getDestOfRoute().getCity();
        destStreet = r.getDestOfRoute().getStreet();
        destBlockNo = r.getDestOfRoute().getBlockNo();
        destPostalCode = r.getDestOfRoute().getPostalCode();
        destLocation = r.getDestOfRoute();

        routeId = r.getRoutesId().toString();
        mode = r.getMode();
        distance = r.getDistance().toString();
        // Geocoding~~
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(r.getStartOfRoute().toString()).setLanguage("en").getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        GeocoderRequest geocoderRequest2 = new GeocoderRequestBuilder().setAddress(r.getDestOfRoute().toString()).setLanguage("en").getGeocoderRequest();
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
//        GeocodingResult[] results = GeocodingApi.geocode(context, r.getStartOfRoute().toString()).await();
//        System.out.println(results[0].formattedAddress);
        //end of geocoding

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateRoutesInfo.xhtml");
    }

    public void updateRoutesInfo() throws IOException {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Double dis = Double.parseDouble(distance);
        Long rId = Long.parseLong(routeId);
        Location source = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
        Location dest = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);

        try {
            trosb.updateRoutes(cId, rId, source, dest, mode, dis);
            this.faceMsg("Routes information updated successfully!!");
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("Routes cannot retrieve!");
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./routesMgtPage.xhtml");
    }

    public void gotoAssign(Routes r) throws IOException {
        routeId = r.getRoutesId().toString();
        mode = r.getMode();
        switch (mode) {
            case "byVessel":
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                ec.redirect("./selectVesselList.xhtml");
                break;
            case "byTruck":
                FacesContext fc2 = FacesContext.getCurrentInstance();
                ExternalContext ec2 = fc2.getExternalContext();
                ec2.redirect("./selectTruckList.xhtml");
                break;
            case "byPlane":
                FacesContext fc3 = FacesContext.getCurrentInstance();
                ExternalContext ec3 = fc3.getExternalContext();
                ec3.redirect("./selectPlaneList.xhtml");
                break;
            default:
                break;
        }
    }

    public void assignVehicles() throws IOException {
        if (vehicleArray.length == 0) {
            this.errorMsg("Please select at least one vehicles!");
            return;
        }
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long rId = Long.parseLong(routeId);
        vList.clear();
        for (Object o : vehicleArray) {
            vList.add((Vehicles) o);
        }
        try {
            if (!tomsb.assignVehicleToRoute(cId, rId, vList)) {
                errorMsg("Vehicles cannot assign!\nPlease assign drivers!");
            } else {
                this.faceMsg("Vehicles Assigned Successfully!");
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                ec.redirect("./routesMgtPage.xhtml");
            }
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("Routes cannot retrieve!");
        }
    }

    public void reassignVehicles() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long rId = Long.parseLong(routeId);
        try {
            tomsb.reassignVehiclesToRoute(cId, rId);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            this.faceMsg("Please assign vehicles!");
            ec.redirect("./assignVehiclesToRoute.xhtml");        
        } catch (CompanyNotExistException ex) {
            this.errorMsg("no company found");
        } catch (RoutesNotExistException ex) {
            this.errorMsg("no route found");
        } catch (IOException ex) {
            this.errorMsg("cannot find the page");
        }
    }

    public void goToAssignDrivers() throws IOException {
        FacesContext fc3 = FacesContext.getCurrentInstance();
        ExternalContext ec3 = fc3.getExternalContext();
        ec3.redirect("./assignDriversToVehicle.xhtml");
    }

    public void viewAssignedVList(Routes r) {
        routeId = r.getRoutesId().toString();
        RequestContext.getCurrentInstance().execute("PF('Dialog1').show()");
    }

    public List<Vehicles> getAssignedVehicles() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long rId = Long.parseLong(routeId);
        try {
            return trosb.viewAssignedVehicles(cId, rId);
        } catch (RoutesNotExistException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                    "No company ID exception!!" , ""));           
        } catch (CompanyNotExistException ex) {
            this.errorMsg("Company cannot retrieve!");
        }

        return null;
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

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(response));
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public TransRouteOptiSessionBeanLocal getTrosb() {
        return trosb;
    }

    public void setTrosb(TransRouteOptiSessionBeanLocal trosb) {
        this.trosb = trosb;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ArrayList<Vehicles> getvList() {
        return vList;
    }

    public void setvList(ArrayList<Vehicles> vList) {
        this.vList = vList;
    }

    public Vehicles[] getVehicleArray() {
        return vehicleArray;
    }

    public void setVehicleArray(Vehicles[] vehicleArray) {
        this.vehicleArray = vehicleArray;
    }

    public TransOperationMgtSessionBeanLocal getTomsb() {
        return tomsb;
    }

    public void setTomsb(TransOperationMgtSessionBeanLocal tomsb) {
        this.tomsb = tomsb;
    }

    public TransAssetMgtSessionBeanLocal getTamsb() {
        return tamsb;
    }

    public void setTamsb(TransAssetMgtSessionBeanLocal tamsb) {
        this.tamsb = tamsb;
    }

    public ArrayList<Vehicles> gettList() {
        return tList;
    }

    public void settList(ArrayList<Vehicles> tList) {
        this.tList = tList;
    }

    public ArrayList<Vehicles> getpList() {
        return pList;
    }

    public void setpList(ArrayList<Vehicles> pList) {
        this.pList = pList;
    }

    public Vehicles[] getTruckArray() {
        return truckArray;
    }

    public void setTruckArray(Vehicles[] truckArray) {
        this.truckArray = truckArray;
    }

    public Vehicles[] getPlaneArray() {
        return planeArray;
    }

    public void setPlaneArray(Vehicles[] planeArray) {
        this.planeArray = planeArray;
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

    public boolean isIsTruck() {
        return isTruck;
    }

    public void setIsTruck(boolean isTruck) {
        this.isTruck = isTruck;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Location getDestLocation() {
        return destLocation;
    }

    public void setDestLocation(Location destLocation) {
        this.destLocation = destLocation;
    }

}
