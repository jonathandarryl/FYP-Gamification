/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.session.PopulateCRMSessionBean;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import util.exception.ProductAlreadyExistException;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "populateCRMManagedBean")
@ApplicationScoped
public class PopulateCRMManagedBean {

    @EJB
    private PopulateCRMSessionBean pcrm;
    /**
     * Creates a new instance of PopulateCRMManagedBean
     */
    public PopulateCRMManagedBean() {
    }
    
    public void populateCRM() throws ProductAlreadyExistException{
        if(pcrm.isMembershipTierEmpty() && pcrm.isProductEmpty() && pcrm.isServiceQuotationEmpty() &&
            pcrm.isServiceContractEmpty() && pcrm.isServiceOrderEmpty()){
            pcrm.populateCRM();
        }
    }
    
    public void populateInventory(){
        if(pcrm.isInventoryEmpty())
            pcrm.popInventory();
    }
}
