/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPIIManagedBean;

import MRPII.session.PopulateMRPSessionBean;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Zhong Wei
 */
@Named(value = "populateMRPManagedBean")
@ApplicationScoped
public class PopulateMRPManagedBean {
    @EJB
    PopulateMRPSessionBean pmrp;

    /**
     * Creates a new instance of PopulateMRPManagedBean
     */
    public PopulateMRPManagedBean() {
    }
    
    public void populateMaterial(){
        if(pmrp.isRawMaterialEmpty())
            pmrp.populateDataList();
    }
    
}
