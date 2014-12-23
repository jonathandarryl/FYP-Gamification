/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CommonManagedBean;

import Common.session.PopulateGRNSSessionBean;
import Common.session.PopulateSessionBean;
import GRNS.session.PostManagementSessionBeanLocal;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import util.exception.AccountTypeNotExistException;

/**
 *
 * @author chongyangsun
 */
@Named(value = "populateManagedBean")
@ApplicationScoped
public class PopulateManagedBean {

    // Session Beans
    @EJB
    private PopulateSessionBean psb;
    @EJB
    private PostManagementSessionBeanLocal pmsbl;
    @EJB
    private PopulateGRNSSessionBean pgsb;

    public PopulateManagedBean() {
    }

    public void populateAccounts() throws AccountTypeNotExistException {
        if (psb.isAccountEmpty()) {
            psb.populateDataList();
        }
        if(psb.isMessageEmpty()) {
            psb.populateMessage();
        }
        
        if(psb.isAnnouncementEmpty()) {
            psb.populateAnnouncement();
        }
        
        if(psb.isNotificationEmpty()) {
            psb.populateNotification();
        }
        
        
    }
    
    public void populateGRNS() {
        if(pgsb.isAggregationPostEmpty()) {
            pgsb.populateAggregationPost();
        }
        
        if(pgsb.isStoragePostEmpty()) {
            pgsb.populateStoragePost();
        }
        
        if(pgsb.isTransportPostEmpty()) {
            pgsb.populateTransportPost();
        }
        
        if(pgsb.isBiddingEmpty()) {
            pgsb.populateBidding();
        }
        
        pmsbl.restartAllTimers();
    }
        
}
