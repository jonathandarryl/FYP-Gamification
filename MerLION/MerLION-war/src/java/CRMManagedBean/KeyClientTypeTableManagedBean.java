/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.entity.MembershipSchema;
import CRM.session.MembershipSchemaMgtSessionBeanLocal;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.PersistenceException;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@ManagedBean(name = "keyClientTypeTableManagedBean")
@Named(value = "keyClientTypeTableManagedBean")
@SessionScoped
public class KeyClientTypeTableManagedBean implements Serializable{
    private static final long serialVersionUID = 1L;
    @EJB
    private MembershipSchemaMgtSessionBeanLocal msmSessionBean;
    private String ownerId;
    private String typeId;
    /**
     * Creates a new instance of KeyClientTypeTableManagedBean
     */
    public KeyClientTypeTableManagedBean() {
    }
    
    public List<MembershipSchema> getMembershipSchemas(){
        //this is just dummy data
        //Long cId = Long.parseLong("1");
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        try{
            System.out.println("get membership schema " +cId);
            return msmSessionBean.viewAllMembershipSchema(cId);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));
        }
        return null;
    }
    
    public String deleteMembershipSchema(ActionEvent event) throws CompanyNotExistException, MembershipSchemaNotExistException{
        try{
            Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
            //String tId = (String) event.getComponent().getAttributes().get("typeId");
            
            Long tyId = (Long) event.getComponent().getAttributes().get("typeId");
            System.out.println(cId + "" + tyId);
            return msmSessionBean.deleteMembershipSchema(cId, tyId);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));
        }catch(MembershipSchemaNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No membership schema ID exception!!" , ""));
        }catch(Exception pex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Cannot delete or update a key client type since there are key clients of this type already!" , ""));
        }
        return null;
    }
}
