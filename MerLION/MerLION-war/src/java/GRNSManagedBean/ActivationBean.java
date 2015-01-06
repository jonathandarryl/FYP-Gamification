/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import GRNS.session.GRNSManagementSessionBeanLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author chongyangsun
 */
@ManagedBean
@RequestScoped
public class ActivationBean {

    /**
     * Creates a new instance of ActivationBean
     */
    public ActivationBean() {
    }
    @EJB
    GRNSManagementSessionBeanLocal gmbl;

    @ManagedProperty(value = "#{param.uuid}")
    private String uuid;
    private boolean valid;

    @PostConstruct
    public void init() {
    }
    
    public void activate() {
        System.out.println("inside activation. uuid = "+uuid);
        setValid(gmbl.activateExternalUserAccount(uuid));
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

}
