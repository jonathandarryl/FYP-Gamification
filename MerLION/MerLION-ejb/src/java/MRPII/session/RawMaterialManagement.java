/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.RawMaterial;
import OES.entity.Product;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static junit.framework.TestCase.assertNotNull;
import util.exception.CompanyNotExistException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class RawMaterialManagement implements RawMaterialManagementLocal {
    @PersistenceContext
    EntityManager em;
    @EJB
    private CompanyManagementSessionBeanLocal cmsb;
    //Pre-condition: owner company must exist and the company name is unique
    @Override
    public void createRawMaterial(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch) 
            throws RawMaterialAlreadyExistException, CompanyNotExistException{
        try{  
            RawMaterial fg = retrieveRawMaterial(ownerCompanyName, productName);
            if(fg!=null)
                throw new RawMaterialAlreadyExistException("RawMaterial "+productName+" already exist in Company "+ownerCompanyName+"'s product list. Creation failed.");
        }
        catch(RawMaterialNotExistException ex){
            System.out.println("No existing product with the same name as "+productName+". New product creation proceeds.");
        }
        catch(MultipleRawMaterialWithSameNameException ex){
            
        }
        finally{
            CustomerCompany ownerCompany = (CustomerCompany) cmsb.retrieveCompany(ownerCompanyName);
            assertNotNull(ownerCompany);            

            RawMaterial product=new RawMaterial(productName, productPrice, productDescription, ownerCompany, "RawMaterial", unit, quantityInOneUnitCapacity, qunatityInOneBatch);
            ownerCompany.getRawMaterialList().add((RawMaterial)product);
            em.persist(product);        
            em.merge(ownerCompany);
            System.out.println("Product added into the catalog successfully!");                  
        }
    }

    @Override
    public void updateRawMaterial(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        RawMaterial product = retrieveRawMaterial(ownerCompanyName, productName);
        
        if(product!=null){
            product.setProductDescription(productDescription);
            product.setProductName(productName);
            product.setProductPrice(productPrice);
            product.setUnit(unit);
            product.setQuantityInOneUnitCapacity(quantityInOneUnitCapacity);
            product.setQuantityInOneBatch(qunatityInOneBatch);
            em.merge(product);
            System.out.println("Raw Material info update successful.");
        }        
        else{
            System.out.println("No raw material named "+productName+" found");
        }
    }

    @Override
    public RawMaterial retrieveRawMaterial(String ownerCompanyName, String productName) 
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        Query query=em.createQuery("select r from RawMaterial r where r.productName=?1 and r.ownerCompanyName=?2 and r.archivedOrNot=FALSE");
        query.setParameter(1, productName);
        query.setParameter(2, ownerCompanyName);
        ArrayList<RawMaterial>rawMaterialList=new ArrayList(query.getResultList());
        
        if(rawMaterialList.isEmpty())
            throw new RawMaterialNotExistException("Raw material "+productName+" not found."); 
        if(rawMaterialList.size()>1)
            throw new MultipleRawMaterialWithSameNameException("Error! Multiple raw material with the same name under the same company exist!");
        RawMaterial product = rawMaterialList.get(0);            
        return product;
    }
    
    @Override
    public ArrayList<RawMaterial> retrieveRawMaterialList(String ownerCompanyName)
             throws RawMaterialNotExistException{
        Query query=em.createQuery("select r from RawMaterial r where r.ownerCompanyName=?1 and r.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompanyName);
        ArrayList<RawMaterial>rawMaterialList=new ArrayList(query.getResultList());
        if(rawMaterialList.isEmpty())
            throw new RawMaterialNotExistException("No raw material under Company "+ownerCompanyName+" found."); 
        return rawMaterialList;
    }

    @Override
    public void deleteRawMaterial(String ownerCompanyName, String productName) 
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        RawMaterial product = retrieveRawMaterial(ownerCompanyName, productName);
        
        if(product!=null){
            product.setArchivedOrNot(Boolean.TRUE);
            em.merge(product);        
            System.out.println("RawMaterial archived successfully!");
        }       
        else{
            System.out.println("Failed. No such RawMaterial"+productName+ " exists in "+ownerCompanyName+"'s current RawMaterial catalog");
        }
    }

}
