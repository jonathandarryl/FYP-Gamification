/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OESManagedBean;

import OES.session.PopulateOESSessionBean;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Zhong Wei
 */
@Named(value = "populateOESManagedBean")
@ApplicationScoped
public class PopulateOESManagedBean {
    @EJB
    private PopulateOESSessionBean pob;

    /**
     * Creates a new instance of PopulateOESManagedBean
     */
    public PopulateOESManagedBean() {
    }
    
    public void populateProduct(){
        if(pob.isProductEmpty()||pob.isSalesQuotEmpty())
            pob.populateDataList();
    }
    
}
