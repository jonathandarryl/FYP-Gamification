/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRMManagedBean;

import CRM.entity.MembershipSchema;
import CRM.session.MembershipSchemaMgtSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import util.exception.CompanyNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@ManagedBean(name = "keyClientTypeManagedBean")
@Named(value = "keyClientTypeManagedBean")
@SessionScoped
public class KeyClientTypeManagedBean implements Serializable{
    private static final long serialVersionUID = 1L;

    @EJB
    private MembershipSchemaMgtSessionBeanLocal msmSessionBean;
    
    private String ownerId;
    private String typeId;
    private String membershipTier;
    private String boundaryPoint;
    private String discountPerOrder;
    private String tIdForUpdate;
    private String tierList;
    private Integer pointList;    
    private Double discountList;
    /**
     * Creates a new instance of KeyClientTypeManagedBean
     */
    public KeyClientTypeManagedBean() {
    }

//    public void addTierAndPoint(ActionEvent event){
//        try{
//            Integer point = Integer.parseInt(boundaryPoint);
//            Double discount = Double.parseDouble(discountPerOrder);
//            tierList.add(membershipTier);
//            pointList.add(point);
//            discountList.add(discount);
//        }catch(NullPointerException nex){
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                    "ArrayList null exception!!" , ""));
//        }
//    }
    
    public void addNewKeyClientType(ActionEvent event){   
        //Long companyId = Long.parseLong(ownerId);
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        System.out.println(cId);
        Integer point = Integer.parseInt(boundaryPoint);
        Double discount = Double.parseDouble(discountPerOrder);
        try{
             String a = msmSessionBean.createMembershipSchema(cId, membershipTier, point, discount);
             System.out.println(a);
        }catch(CompanyNotExistException cex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "No company ID exception!!" , ""));
        }
    }
    
    public void goToUpdate(MembershipSchema ms) throws IOException{
        //Long updateId = (Long) event.getComponent().getAttributes().get("typeId");
        ownerId = ms.getOwnerCompany().getId().toString();
        typeId = ms.getId().toString();
        boundaryPoint = ms.getBoundaryPoint().toString();
        discountPerOrder = ms.getDiscountPerOrder().toString();
        membershipTier = ms.getMembershipTier();
        System.out.println(ownerId+" "+typeId+" "+boundaryPoint+" "+discountPerOrder+" "+membershipTier);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./updateKeyClientType.xhtml");  
    }
            
    public void updateKeyClientType(ActionEvent event) throws CompanyNotExistException, MembershipSchemaNotExistException, IOException{
        //dummy data
        //Long cId = Long.parseLong("1");
        try{
        Long cId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        Long tId = Long.parseLong(typeId);
        Integer point = Integer.parseInt(boundaryPoint);
        Double discount = Double.parseDouble(discountPerOrder);
        msmSessionBean.updateClientRecord(cId, tId, membershipTier, point, discount);
        }
        catch(Exception pex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Cannot delete or update a key client type since there are key clients of this type already!" , ""));
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.redirect("./viewAllKeyClientType.xhtml"); 
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public String getBoundaryPoint() {
        return boundaryPoint;
    }

    public void setBoundaryPoint(String boundaryPoint) {
        this.boundaryPoint = boundaryPoint;
    }

    public String getDiscountPerOrder() {
        return discountPerOrder;
    }

    public void setDiscountPerOrder(String discountPerOrder) {
        this.discountPerOrder = discountPerOrder;
    }

    public String getTierList() {
        return tierList;
    }

    public void setTierList(String tierList) {
        this.tierList = tierList;
    }

    public Integer getPointList() {
        return pointList;
    }

    public void setPointList(Integer pointList) {
        this.pointList = pointList;
    }

    public Double getDiscountList() {
        return discountList;
    }

    public void setDiscountList(Double discountList) {
        this.discountList = discountList;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String gettIdForUpdate() {
        return tIdForUpdate;
    }

    public void settIdForUpdate(String tIdForUpdate) {
        this.tIdForUpdate = tIdForUpdate;
    }

}
