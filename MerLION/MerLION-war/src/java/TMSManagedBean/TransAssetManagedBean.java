/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMSManagedBean;

import TMS.entity.Drivers;
import TMS.entity.Vehicles;
import TMS.session.TransAssetMgtSessionBeanLocal;
import TMS.session.TransOperationMgtSessionBeanLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "transAssetManagedBean")
@SessionScoped
public class TransAssetManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private TransAssetMgtSessionBeanLocal tam;
    @EJB
    private TransOperationMgtSessionBeanLocal tomsb;
    
    private String vehiclesId = "0";
    private String type;
    private String useYear;
    private Boolean tranAvail;
    private String capacity;
    private String maxDistance;
    private String transCost;
    private String velocity;
    private Boolean perishable;
    private Boolean flammable;
    private Boolean pharmaceutical;
    private Boolean highValue;
    
    private Drivers[] driversArray;
    private List<Drivers> driversList = new ArrayList<>();
    /**
     * Creates a new instance of TransAssetManagedBean
     */
    public TransAssetManagedBean() {
    }

    public void addTransAsset() throws IOException{
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println("Add "+cId);
        Integer year = Integer.parseInt(useYear);
        Double cost = Double.parseDouble(transCost);
        Long capa = Long.parseLong(capacity);
        Long max = Long.parseLong(maxDistance);
        Double vel = Double.parseDouble(velocity);
        try{
            switch (type){
                case "Truck":
                    tam.createTrucks(cId, year, capa, max, cost, vel,
                            perishable, flammable, pharmaceutical, highValue, flammable, null);
                    break;
                case "Plane":
                    tam.createPlanes(cId, year, capa, max, cost, vel,
                            perishable, flammable, pharmaceutical, highValue, flammable);
                    break;
                case "Vessel":
                    tam.createVessel(cId, year, capa, max, cost, vel,
                            perishable, flammable, pharmaceutical, highValue, flammable);
                    break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Asset type not exist." , "")); 
                    break;
            }
            this.displayGrowl("Information for the asset is successfully created.");
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception." , ""));           
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./viewAllTransAsset.xhtml");   
    }
    
    public List<Vehicles> getTransVehicles() {
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println(cId);
        try{
            return tam.viewAllVehicles(cId);
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }catch(VehicleNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No Information of Vehicles found." , ""));           
        }
        return null;
    }
    
    public void deleteTransAsset(Long assetId, String type){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            tam.deleteTransAsset(cId, type, assetId);
            this.faceMsg("asset delete record successfully!");
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }catch(VehicleNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No Vehicle so far!!" , ""));           
        }
    }
    
    public void viewTransAsset(Vehicles v) throws IOException{
        vehiclesId = v.getVehiclesId().toString();
        type = v.getType();
        capacity = v.getCapacity().toString();
        useYear = v.getUseYear().toString();
        transCost = v.getTransCost().toString();
        maxDistance = v.getMaxDistance().toString();
        highValue = v.getPerishable();
        flammable = v.getFlammable();
        tranAvail = v.getAvail();
        velocity = v.getVelocity().toString();
        perishable = v.getPerishable();
        pharmaceutical = v.getPharmaceutical();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateTransAsset.xhtml");   
    }
    
    public void updateTransAsset(ActionEvent event) throws IOException{
        System.out.println("in update");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Integer year = Integer.parseInt(useYear);
        Double cost = Double.parseDouble(transCost);
        Long capa = Long.parseLong(capacity);
        Long max = Long.parseLong(maxDistance);
        Long vId = Long.parseLong(vehiclesId);
        Double vel = Double.parseDouble(velocity);
        try{
            if(type.equals("Truck")){
                System.out.println("it is a truck");
                tam.updateTrucks(cId, vId, year, capa, max, cost, vel, perishable, flammable, pharmaceutical, highValue, flammable);
            }else{
                tam.updatePlaneOrVessel(cId, vId, year, capa, max, cost, vel, perishable, flammable, pharmaceutical, highValue, flammable);
            }
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }catch(VehicleNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No Information of Vehicles Found!" , ""));           
        }
        System.out.println("after update");
        this.displayGrowl("Update successful");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./viewAllTransAsset.xhtml"); 
    }
    
    public void goToAssignDriver(Long truckId) throws IOException{
        vehiclesId = truckId.toString();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("/MerLION-war/TMSWeb/routes/assignDriversToVehicle.xhtml"); 
    }
    
    public void assignDriversToTruck() throws IOException{
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long truckId = Long.parseLong(vehiclesId);
        driversList = new ArrayList<>();
        driversList.addAll(Arrays.asList(driversArray));
        try{
            tomsb.assignDriversToTruck(cId, truckId, driversList);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            this.faceMsg("Driver Assignment Successful!");
            ec.redirect("/MerLION-war/TMSWeb/routes/assignVehiclesToRoute.xhtml"); 
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "No company ID exception!!" , ""));                
        }catch(VehicleNotExistException vex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "No vehicles ID exception!!" , ""));                    
        }
        this.displayGrowl("Driver successfully assigned to the truck");
    }
    
    public void goToViewDriver(Long truckId) throws IOException{
        vehiclesId = truckId.toString();
        RequestContext.getCurrentInstance().execute("PF('Dialog1').show()");      
    }
    
    public List<Drivers> viewAssignedDrivers(){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long truckId = Long.parseLong(vehiclesId);
        try {
            return tomsb.viewAssignedDrivers(cId, truckId);
        } catch (CompanyNotExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "No company ID exception!!" , ""));        
        } catch (VehicleNotExistException ex) {
            return null;
        }
        return null;
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }
    
    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(response));
        context.getExternalContext().getFlash().setKeepMessages(true);
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
    
    public String getVehiclesId() {
        return vehiclesId;
    }

    public void setVehiclesId(String vehiclesId) {
        this.vehiclesId = vehiclesId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUseYear() {
        return useYear;
    }

    public void setUseYear(String useYear) {
        this.useYear = useYear;
    }

    public Boolean getTranAvail() {
        return tranAvail;
    }

    public void setTranAvail(Boolean tranAvail) {
        this.tranAvail = tranAvail;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(String maxDistance) {
        this.maxDistance = maxDistance;
    }

    public String getTransCost() {
        return transCost;
    }

    public void setTransCost(String transCost) {
        this.transCost = transCost;
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

    public String getVelocity() {
        return velocity;
    }

    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }

    public TransAssetMgtSessionBeanLocal getTam() {
        return tam;
    }

    public void setTam(TransAssetMgtSessionBeanLocal tam) {
        this.tam = tam;
    }

    public Drivers[] getDriversArray() {
        return driversArray;
    }

    public void setDriversArray(Drivers[] driversArray) {
        this.driversArray = driversArray;
    }

    public List<Drivers> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Drivers> driversList) {
        this.driversList = driversList;
    }

}
