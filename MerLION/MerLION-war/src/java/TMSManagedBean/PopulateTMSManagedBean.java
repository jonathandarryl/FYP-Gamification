/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMSManagedBean;

import TMS.session.PopulateTMSSessionBean;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author HanXiangyu
 */
@Named(value = "populateTMSManagedBean")
@ApplicationScoped
public class PopulateTMSManagedBean {

    @EJB
    private PopulateTMSSessionBean ptsb;
    /**
     * Creates a new instance of PopulateTMSManagedBean
     */
    public PopulateTMSManagedBean() {
    }
    
    public void populateTMS(){
        if(ptsb.isVehicleEmpty() && ptsb.isDriverEmpty() && ptsb.isRouteEmpty()){
            ptsb.populateTMS();
        }
    } 
    
    public void populateTMSForMerlion(){
        if(ptsb.isMerlionEmpty()){
            ptsb.populateForMerlion();
        }
    }
}
