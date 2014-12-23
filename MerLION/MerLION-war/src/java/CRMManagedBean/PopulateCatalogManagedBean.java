/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

import CRM.session.PopulateServiceCatalog;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author andongmei
 */
@Named(value = "populateCatalogManagedBean")
@ApplicationScoped
public class PopulateCatalogManagedBean {
    
    @EJB
    private PopulateServiceCatalog psc;

    /**
     * Creates a new instance of PopulateCatalogManagedBean
     */
    public PopulateCatalogManagedBean() {
    }
    
    
    
    public void populateCatalog() {
     
         
        if (psc.isCatalogEmpty()) {
            psc.populateCatalogList();
        }
        
        
        
    }
}
