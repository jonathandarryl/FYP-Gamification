/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.SubscriptionSchema;
import CRM.session.SubscriptionSchemaMgtSessionBeanLocal;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "subscriptionSchemaManagedBean")
@SessionScoped
public class SubscriptionSchemaManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private SubscriptionSchemaMgtSessionBeanLocal ssm;
    private String ownerId;
    private String numOfYears;
    private String boundaryPrice;
    private String schemaId;
    private List<String> priceList;
    
    /**
     * Creates a new instance of SubscriptionSchemaManagedBean
     */
    public SubscriptionSchemaManagedBean() {
    }

    public void addSubSchema(ActionEvent event){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        //System.out.println("Add "+cId);
        Integer year = Integer.parseInt(numOfYears);
        Double price = Double.parseDouble(boundaryPrice);
        try{
            ssm.createSubscriptionSchema(cId, year, price);
        }catch(CompanyNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }
    }
    
    public List<SubscriptionSchema> getSubscriptionSchema(ActionEvent event){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        //System.out.println("View"+cId);
        try{
            return ssm.viewAllSubscriptionSchema(cId);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No SubscriptionSchema ID exception!!" , ""));           
        }
        return null;
    }
    
    public void getSchemaPriceList(){
    Long cId = 25L;
    priceList = new ArrayList<>();
        try{
             List<SubscriptionSchema> sList = ssm.viewAllSubscriptionSchema(cId);
             for(SubscriptionSchema s: sList){
                 //System.out.println(s.getBoundaryPrice());
                 priceList.add(s.getBoundaryPrice().toString());
             }
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));           
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No SubscriptionSchema ID exception!!" , ""));           
        }
        //return pList;
    }
    
    public void getPriceListNew(){
        priceList = new ArrayList<>();
        try{
            priceList = ssm.getPriceList();
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No SubscriptionSchema ID exception!!" , ""));           
        }
    }
    
    public String deleteSubscriptionSchema(Long sId){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        //Long sId = Long.parseLong(schemaId);
        try{
            return ssm.deleteSubscriptionSchema(cId, sId);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No SubscriptionSchema ID exception!!" , ""));
        }catch(Exception pex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Cannot delete or update a schema since someone has paid already!" , ""));
        }
        return "";
    }
    
    public void viewSchema(SubscriptionSchema ss) throws IOException{
        schemaId = ss.getId().toString();
        numOfYears = ss.getNumOfYears().toString();
        boundaryPrice = ss.getBoundaryPrice().toString();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateSubscriptionSchema.xhtml");   
    }
    
    public void updateSchema(ActionEvent event) throws IOException{
        //
        //dummy data
        //Long cId = 25L;
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        Long sId = Long.parseLong(schemaId);
        Integer year = Integer.parseInt(numOfYears);
        Double price = Double.parseDouble(boundaryPrice);
        try{
            ssm.updateSubscriptionSchema(cId, sId, year, price);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));
        }catch(SubscriptionSchemaNotExistException sex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No SubscriptionSchema ID exception!!" , ""));
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./subscriptionSchemaMgt.xhtml"); 
    }
    
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getNumOfYears() {
        return numOfYears;
    }

    public void setNumOfYears(String numOfYears) {
        this.numOfYears = numOfYears;
    }

    public String getBoundaryPrice() {
        return boundaryPrice;
    }

    public void setBoundaryPrice(String boundaryPrice) {
        this.boundaryPrice = boundaryPrice;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public List<String> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<String> priceList) {
        this.priceList = priceList;
    }
}