/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SalesLead;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.SalesLeadAlreadyExistException;
import util.exception.SalesLeadNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface SalesLeadManagementSessionLocal {
    //B.3.c.1
    public void createSalesLead(Long partnerCompanyId, Long customerCompanyId) 
            throws CompanyNotExistException, SalesLeadAlreadyExistException; //SalesLeadAlreadyExistException should be handled in the web tier
    //B.3.c.3
    public SalesLead retrieveSalesLead(Long partnerCompanyId, Long customerCompanyId) 
            throws CompanyNotExistException, SalesLeadNotExistException;
    
    public SalesLead retrieveSalesLead(Long slId) 
            throws SalesLeadNotExistException;
    
    public void removeSalesLead(Long salesLeadId) 
            throws SalesLeadNotExistException;

    public List<SalesLead> retrieveSalesLeadList(Long ownerCompanyId) 
            throws CompanyNotExistException;

    public void createSalesLead(String ownerCompanyName, String clientCompanyName) 
            throws CompanyNotExistException, SalesLeadAlreadyExistException;
    
    public SalesLead createSalesLeadWithReturn(String ownerCompanyName, String clientCompanyName) 
            throws CompanyNotExistException, SalesLeadAlreadyExistException;

    public SalesLead retrieveSalesLead(String ownerCompanyName, String clientCompanyName) 
            throws CompanyNotExistException;

    public List<SalesLead> retrieveSalesLeadList(String ownerCompanyName)
            throws CompanyNotExistException;

    public void updateSalesLead(Long slId, String description) 
            throws SalesLeadNotExistException;  
    
}
