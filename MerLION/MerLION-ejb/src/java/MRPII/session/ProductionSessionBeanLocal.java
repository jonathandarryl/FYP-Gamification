/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import Common.entity.Company;
import MRPII.entity.Production;
import MRPII.entity.RawMaterial;
import OES.entity.SalesOrder;
import WMS.entity.ExtractionRequest;
import java.util.List;
import javax.ejb.Local;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.ProductionNotCommencedException;
import util.exception.ProductionNotExistException;
import util.exception.RawMaterialNotReadyException;

/**
 *
 * @author songhan
 */
@Local
public interface ProductionSessionBeanLocal {

    public void createProduction(String productName, String companyName, Integer batchNumber, SalesOrder so) 
            throws ProductNotExistException, MultipleProductWithSameNameException;    
    
    public List<ExtractionRequest> retrieveExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production);

    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production);

    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneRawMaterialInOneProduction(RawMaterial rm, Production production);
        
    public Boolean checkOneRawMaterialReadyOrNot(RawMaterial rm, Production production);
    
    public Boolean checkAllRawMaterialReadyOrNot(Production production);
    
    public Boolean confirmRawMaterialReadyForProduction (Production production);
    
    public void commenceProduction(Production production)
            throws RawMaterialNotReadyException;   
    
    public void completeProduction(Production production) 
            throws ProductionNotCommencedException;

    public Production retrieveProduction(Long id)
            throws ProductionNotExistException;    

    public List<Production> retrieveStandByProductionList(Company ownerCompany) 
            throws ProductionNotExistException;

    public List<Production> retrieveOngoingProductionList(Company ownerCompany) 
            throws ProductionNotExistException;

    public List<Production> retrieveCompletedProductionList(Company ownerCompany) throws ProductionNotExistException;
    
}
