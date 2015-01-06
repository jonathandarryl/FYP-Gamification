/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.KeyClient;
import CRM.session.ClientInfoMgtSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.CompanyNotExistException;
import util.exception.KeyClientNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@ManagedBean(name = "keyClientManagedBean")
@Named(value = "keyClientManagedBean")
@SessionScoped
public class KeyClientManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @EJB
    private ClientInfoMgtSessionBeanLocal cim;
    private String companyName;
    private String ownerId;
    private String contactNo;
    private String clientId;
    private String membershipPoint;
    private String membershipTier;
    private KeyClient keyClient = new KeyClient();
    
    
    
    /**
     * Creates a new instance of KeyClientManagedBean
     */
    public KeyClientManagedBean() {
    }
    
    public void addNewKeyClient(ActionEvent event){
        //Long cId = Long.parseLong(ownerId);
        //use dummy data here for testing
        //Long cId = Long.parseLong("1");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println(cId);
        try{
            cim.addClientRecord(cId, companyName, contactNo);
        }catch(CompanyNotExistException | MembershipSchemaNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID & Membership exception!!" , ""));
        }
    }
    
    public List<KeyClient> getKeyClient(ActionEvent event){
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
             return cim.viewAllClientCompany(cId);
        }catch(KeyClientNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No membership schema ID exception!!" , ""));
        }
        return null;
    }

    public void viewClient(KeyClient client) throws IOException {
        //System.out.println("in view");
        clientId = client.getId().toString();
        companyName = client.getCompanyName();
        contactNo = client.getContactNo();
        membershipTier = client.getMembershipTier();
        membershipPoint = client.getCumulativeMembershipPoints().toString();
        //this.keyClient = (KeyClient) event.getComponent().getAttributes().get("client");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateKeyClient.xhtml");
    }
    
    public void viewClientAdmin(KeyClient client) throws IOException {
        //System.out.println("in view");
        clientId = client.getId().toString();
        companyName = client.getCompanyName();
        contactNo = client.getContactNo();
        membershipTier = client.getMembershipTier();
        membershipPoint = client.getCumulativeMembershipPoints().toString();
        //this.keyClient = (KeyClient) event.getComponent().getAttributes().get("client");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateKeyClientAdmin.xhtml");
    }
    
    public String deleteKeyClient(Long clientId){
        //Long cId = Long.parseLong(ownerId);
        //use dummy data here for testing
        //System.out.println("in delete");
        //Long cId = Long.parseLong("1");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            System.out.println("before delete" + cId + clientId);
            return cim.deleteClientCompany(cId, clientId);
        }catch(CompanyNotExistException | KeyClientNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No membership schema ID exception!!" , ""));
        }
        return "";
    }
    
    public void updateKeyClient(ActionEvent event) throws IOException{
        //Long cId = Long.parseLong(ownerId);
        //use dummy data here for testing
        //Long cId = Long.parseLong("1");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long keyId = Long.parseLong(clientId);
        Integer point = Integer.parseInt(membershipPoint);
        try{
            cim.updateClientRecord(cId, keyId, companyName, membershipTier, contactNo, point);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            this.faceMsg("Updated Successful!");
            ec.redirect("./keyClientMgtPage.xhtml");
        }catch(CompanyNotExistException | KeyClientNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No membership schema ID exception!!" , ""));
        }
    }
    
    public void updateKeyClientAdmin(ActionEvent event) throws IOException{
        //Long cId = Long.parseLong(ownerId);
        //use dummy data here for testing
        //Long cId = Long.parseLong("1");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long keyId = Long.parseLong(clientId);
        Integer point = Integer.parseInt(membershipPoint);
        try{
            cim.updateClientRecord(cId, keyId, companyName, membershipTier, contactNo, point);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("./keyClientMgtPageAdmin.xhtml");
        }catch(CompanyNotExistException | KeyClientNotExistException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No membership schema ID exception!!" , ""));
        }
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }
    
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMembershipPoint() {
        return membershipPoint;
    }

    public void setMembershipPoint(String membershipPoint) {
        this.membershipPoint = membershipPoint;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public KeyClient getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(KeyClient keyClient) {
        this.keyClient = keyClient;
    }
    
}
