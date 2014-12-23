/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SalesLead;
import Common.entity.Company;
import Common.entity.PartnerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.SalesLeadAlreadyExistException;
import util.exception.SalesLeadNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class SalesLeadManagementSession implements SalesLeadManagementSessionLocal {
    @PersistenceContext
    private EntityManager em;   
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    
    @Override
    public void createSalesLead(Long ownerCompanyName, Long clientCompanyName)throws CompanyNotExistException, SalesLeadAlreadyExistException{
        System.out.println("SalesLeadManagementSession: createSalesLead partnerCompanyId "+ownerCompanyName+" customerCompanyId "+clientCompanyName);
        PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        SalesLead salesLead = new SalesLead(ownerCompany, clientCompany);
        if(salesLeadExistOrNot(ownerCompany.getSalesLeadList(), salesLead))
            throw new SalesLeadAlreadyExistException("Company "+clientCompanyName+" already exists in Company "+ownerCompanyName+"'s sales lead list.");

        ownerCompany.getSalesLeadList().add(salesLead);
        em.merge(ownerCompany);
        em.persist(salesLead);            
    }
    @Override
    public SalesLead createSalesLeadWithReturn(String ownerCompanyName, String clientCompanyName) throws CompanyNotExistException, SalesLeadAlreadyExistException{
        System.out.println("SalesLeadManagementSession: createSalesLead partnerCompanyId "+ownerCompanyName+" customerCompanyId "+clientCompanyName);
        PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        SalesLead salesLead = new SalesLead(ownerCompany, clientCompany);
        if(salesLeadExistOrNot(ownerCompany.getSalesLeadList(), salesLead))
            throw new SalesLeadAlreadyExistException("Company "+clientCompanyName+" already exists in Company "+ownerCompanyName+"'s sales lead list.");

        ownerCompany.getSalesLeadList().add(salesLead);
        em.merge(ownerCompany);
        em.persist(salesLead);  
        
        return salesLead;
    }
    
    private Boolean salesLeadExistOrNot(List<SalesLead> slList, SalesLead sl){
        if(slList.isEmpty())
            return Boolean.FALSE;
        for(Object o: slList){
            SalesLead curSl = (SalesLead) o;
            if(curSl.getClientCompany().getCompanyName().equals(sl.getClientCompany().getCompanyName()))
                return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public SalesLead retrieveSalesLead(Long ownerCompanyName, Long clientCompanyName) 
            throws CompanyNotExistException, SalesLeadNotExistException{
        PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query=em.createQuery("select s from SalesLead s where s.ownerCompany=?1 and s.clientCompany=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        ArrayList<SalesLead>salesLeadList=new ArrayList(query.getResultList());
        if(salesLeadList.isEmpty())
            throw new SalesLeadNotExistException("Sales lead with Client company "+clientCompanyName+" does not exist!");
        SalesLead salesLead = salesLeadList.get(0);
        return salesLead;          
    }
     @Override
    public SalesLead retrieveSalesLead(Long slId)
            throws SalesLeadNotExistException{
        SalesLead sl = em.find(SalesLead.class, slId);
        if(sl==null)
            throw new SalesLeadNotExistException("SalesLead "+slId+" not exist!");
        return sl;
    }
    
    @Override
    public List<SalesLead> retrieveSalesLeadList(Long ownerCompanyId) throws CompanyNotExistException{
        List<SalesLead> salesLeadList = new ArrayList<>();
        try{
            PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyId);
            if(ownerCompany == null)
                throw new Exception("Partner company "+ownerCompanyId+" does not exist!");           
            salesLeadList = ownerCompany.getSalesLeadList();
        }
        catch(Exception ex){
           System.out.println(ex.getMessage());
        }
        return salesLeadList;
    }
    
    @Override
    public void updateSalesLead(Long slId, String description) throws SalesLeadNotExistException{
        SalesLead sl = em.find(SalesLead.class, slId);
        if(sl==null)
            throw new SalesLeadNotExistException("SalesLead "+slId+" not found!");
        sl.setDescription(description);
        em.merge(sl);
    }
    
    @Override
    public void removeSalesLead(Long salesLeadId) throws SalesLeadNotExistException{
        try{
            SalesLead salesLead = em.find(SalesLead.class, salesLeadId);
            if(salesLead==null)
                throw new SalesLeadNotExistException("SalesLead "+salesLeadId+" not exist!");
            em.remove(salesLead);
        }
        catch(Exception ex){
           System.out.println(ex.getMessage());
        }
    }
    
    @Override
    public void createSalesLead(String ownerCompanyName, String clientCompanyName)throws CompanyNotExistException, SalesLeadAlreadyExistException{
        PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        SalesLead salesLead = new SalesLead(ownerCompany, clientCompany);
        if(salesLeadExistOrNot(ownerCompany.getSalesLeadList(), salesLead))
            throw new SalesLeadAlreadyExistException("Company "+clientCompanyName+" already exists in Company "+ownerCompanyName+"'s sales lead list.");

        ownerCompany.getSalesLeadList().add(salesLead);
        em.merge(ownerCompany);
        em.persist(salesLead);            
    }
    
    @Override
    public SalesLead retrieveSalesLead(String ownerCompanyName, String clientCompanyName)  throws CompanyNotExistException{
        PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
        Company clientCompany = cmsb.retrieveCompany(clientCompanyName);
        Query query=em.createQuery("select s from SalesLead s where s.ownerCompany=?1 and s.clientCompany=?2");
        query.setParameter(1, ownerCompany);
        query.setParameter(2, clientCompany);
        ArrayList<SalesLead>salesLeadList=new ArrayList(query.getResultList());
        if(salesLeadList.isEmpty())
            throw new CompanyNotExistException("Sales lead with Client company "+clientCompanyName+" does not exist!");
        SalesLead salesLead = salesLeadList.get(0);
        return salesLead;          
    }
    
    @Override
    public List<SalesLead> retrieveSalesLeadList(String ownerCompanyName) throws CompanyNotExistException{
        List<SalesLead> salesLeadList = new ArrayList<>();
        try{
            PartnerCompany ownerCompany = (PartnerCompany) cmsb.retrieveCompany(ownerCompanyName);
            if(ownerCompany == null)
                throw new Exception("Partner company "+ownerCompanyName+" does not exist!");
            salesLeadList = ownerCompany.getSalesLeadList();
        }
        catch(Exception ex){
           System.out.println(ex.getMessage());
        }
        return salesLeadList;
    }
    
}
