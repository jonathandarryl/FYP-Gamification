/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMSManagedBean;

import TMS.entity.Drivers;
import TMS.session.DriverMgtSessionBeanLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import util.exception.CompanyNotExistException;
import util.exception.DriverNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "driverManagedBean")
@SessionScoped
public class DriverManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EJB
    private DriverMgtSessionBeanLocal dmsb;
    
    private String driverId;
    private String driverName;
    private String age;
    private String gender;
    private String licenseNo;
    private String drivingAge;
           
    /**
     * Creates a new instance of DriverManagedBean
     */
    public DriverManagedBean() {
    }

    public void addNewDriver(){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println(cId);
        Integer a = Integer.parseInt(age);
        Integer dAge = Integer.parseInt(drivingAge);
        try{
             dmsb.addNewDriver(cId, driverName, gender, a, licenseNo, dAge);
             this.faceMsg("Driver record added successfully!");
        }catch(CompanyNotExistException ex){
            this.errorMsg("No company can be retrieved!!");          
        }
    }
    
    public List<Drivers> getDriverList(){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            //System.out.println("get driver from company: "+ cId);
          return dmsb.viewAllDriversInfo(cId);
        }catch(CompanyNotExistException ex){
            this.errorMsg("No company can be retrieved!!");           
        }catch(DriverNotExistException ex){
            this.errorMsg("No driver can be retrieved!!");           
        }
        return null;
    }
    
    public List<Drivers> getAllDriverList(){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            return dmsb.viewAllDrivers(cId);
        }catch(CompanyNotExistException ex){
            this.errorMsg("No company can be retrieved!!");                   
        }catch(DriverNotExistException ex){
            this.errorMsg("No driver can be retrieved!!");           
        }
        return null;
    }
    
    public void deleteDrvier(Long dId){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            dmsb.deleteDriver(cId, dId);
            this.faceMsg("Driver record deleted successfully!");
        }catch(CompanyNotExistException ex){
            this.errorMsg("No company can be retrieved!!");            
        }catch(DriverNotExistException ex){
            this.errorMsg("No driver can be retrieved!!");             
        }
    }
    
    public void goToUpdate(Drivers driver) throws IOException{
        driverId = driver.getDriverId().toString();
        driverName = driver.getName();
        age = driver.getAge().toString();
        gender = driver.getGender();
        licenseNo = driver.getLicenseNo();
        drivingAge = driver.getDrivingAge().toString();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateDriverInfo.xhtml");  
    }
    
    public void updateDriverBasicInfo() throws IOException{
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println(cId);
        Long dId = Long.parseLong(driverId);
        Integer a = Integer.parseInt(age);
        Integer dAge = Integer.parseInt(drivingAge);
        try{
            dmsb.updateDriver(cId, dId, driverName, gender, a, licenseNo, dAge);
            this.faceMsg("Driver information updated successfully!");
        }catch(CompanyNotExistException ex){
            this.errorMsg("No company can be retrieved!!");  
        }catch(DriverNotExistException ex){
            this.errorMsg("No driver can be retrieved!!");         
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./driverMgtPage.xhtml");  
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
    
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getDrivingAge() {
        return drivingAge;
    }

    public void setDrivingAge(String drivingAge) {
        this.drivingAge = drivingAge;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
}
