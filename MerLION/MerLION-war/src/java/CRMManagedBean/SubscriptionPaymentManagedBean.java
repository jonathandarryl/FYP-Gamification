/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.SubscriptionPayment;
import CRM.entity.SubscriptionPaymentReceipt;
import CRM.session.SubscriptionPaymentMgtSessionBeanLocal;
import Common.entity.Company;
import Common.entity.Notification;
import Common.session.CompanyManagementSessionBeanLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionPaymentNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "subscriptionPaymentManagedBean")
@SessionScoped
public class SubscriptionPaymentManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @EJB
    private SubscriptionPaymentMgtSessionBeanLocal spm;
    
    @EJB
    private CompanyManagementSessionBeanLocal cm;
    
    private String paymentId;
    private String price;
    private String paymentTime;
    private String expectedPaymentTime;
    private String companyId;
    private String companyId2;
    private String companyId3;
    private String year;
    
    private String recieptId;
    private String recieptPrice;
    private String recieptTime;
    
    private String notiId;
    private String notiTime;
    private String notiContent;
    
    private String lockOrNot;
           
    /**
     * Creates a new instance of SubscriptionPaymentManagedBean
     */
    public SubscriptionPaymentManagedBean() {
    }

    public List<SubscriptionPayment> getAllSubscriptionPaymentAdmin(ActionEvent event){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("systemAdminId");
        try{
            return spm.viewAllSubscriptionPaymentAdmin();
        }catch(SubscriptionPaymentNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No subcription payment ID exception!!" , ""));  
        }
        return null;
    }
    
    public List<SubscriptionPayment> getAllSubscriptionPaymentOneCompany(ActionEvent event){
        try{
            Long cId = Long.parseLong(companyId);
            return spm.viewAllSubscriptionPayment(cId);
        }catch(CompanyNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company Found of this ID!!" , ""));
        }catch(SubscriptionPaymentNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No subcription payment ID exception!!" , ""));  
        }catch(NumberFormatException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID input so far" , "")); 
        }
        return null;
    }
    
    public List<SubscriptionPayment> getAllSubscriptionPaymentOneCompanyUser(ActionEvent event){
        try{
            Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            return spm.viewAllSubscriptionPayment(cId);
        }catch(CompanyNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID exception!!" , ""));  
        }catch(SubscriptionPaymentNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No subcription payment ID exception!!" , ""));  
        }catch(NumberFormatException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID input so far" , "")); 
        }
        return null;
    }

    public void makeNewSubPayment(ActionEvent event){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Double p = Double.parseDouble(price);
        Timestamp cur = new Timestamp(System.currentTimeMillis());
        try{
            System.out.println("in new payment "+cId);
            spm.createSubscriptionPayment(cId, p, cur);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID exception!!" , ""));  
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No subcription schema ID exception!!" , ""));  
        }
    }

    public void viewRecipt(SubscriptionPayment payment){
        SubscriptionPaymentReceipt receipt = payment.getRecipt();
        recieptId = receipt.getId().toString();
        recieptPrice = receipt.getPrice().toString();
        recieptTime = payment.getPaymentTime().toString();
        RequestContext.getCurrentInstance().execute("PF('Dialog1').show()");   
    }
    
    public void checkPayment(ActionEvent event){
        try{
            Long cId = Long.parseLong(companyId2);
            Long  id=(Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id"); 
            System.out.println(cId+" "+id);
            Notification n = spm.notificationRemind(id, cId);
            if(n!=null){
                notiId = n.getId().toString();
                notiTime = n.getNotifyTime().toString();
                notiContent = n.getContent();
            }else{
                notiId = "";
                notiTime = "";
                notiContent = "No notification find!";
            }
        }catch(CompanyNotExistException | NumberFormatException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID exception!!" , ""));
        }
        RequestContext.getCurrentInstance().execute("PF('Dialog2').show()");
    }
    
//    @Schedule(hour = "*", minute = "*", second = "*/5")
    private Boolean checkLockOrNot(Long cId){    
        try{
            return spm.checkLockOrNot(cId);             
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID exception!!" , ""));
        }
        return false;
    }
    
    public void viewLockStatus(ActionEvent event) throws CompanyNotExistException{
        try{
            Long cId = Long.parseLong(companyId3);
            //System.out.println(cId);
            lockOrNot = checkLockOrNot(cId).toString();
//            lockOrNot = cm.retrieveCompany(cId).getLockedOrNot().toString();
            RequestContext.getCurrentInstance().execute("PF('Dialog3').show()");
        }catch(NumberFormatException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company ID input so far" , "")); 
        }catch(Exception ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                 "No Company can be found" , "")); 
        }
    }
    
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getExpectedPaymentTime() {
        return expectedPaymentTime;
    }

    public void setExpectedPaymentTime(String expectedPaymentTime) {
        this.expectedPaymentTime = expectedPaymentTime;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRecieptId() {
        return recieptId;
    }

    public void setRecieptId(String recieptId) {
        this.recieptId = recieptId;
    }

    public String getRecieptPrice() {
        return recieptPrice;
    }

    public void setRecieptPrice(String recieptPrice) {
        this.recieptPrice = recieptPrice;
    }

    public String getRecieptTime() {
        return recieptTime;
    }

    public void setRecieptTime(String recieptTime) {
        this.recieptTime = recieptTime;
    }

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public void setNotiTime(String notiTime) {
        this.notiTime = notiTime;
    }

    public String getNotiContent() {
        return notiContent;
    }

    public void setNotiContent(String notiContent) {
        this.notiContent = notiContent;
    }

    public String getLockOrNot() {
        return lockOrNot;
    }

    public void setLockOrNot(String lockOrNot) {
        this.lockOrNot = lockOrNot;
    }

    public String getCompanyId2() {
        return companyId2;
    }

    public void setCompanyId2(String companyId2) {
        this.companyId2 = companyId2;
    }

    public String getCompanyId3() {
        return companyId3;
    }

    public void setCompanyId3(String companyId3) {
        this.companyId3 = companyId3;
    }

}
