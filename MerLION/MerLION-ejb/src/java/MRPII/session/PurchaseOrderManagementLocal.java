/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import Common.entity.Company;
import MRPII.entity.PurchaseQuotation;
import MRPII.entity.PurchaseOrder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;
import util.exception.MultipleProductWithSameNameException;
import util.exception.ProductNotExistException;
import util.exception.PurchaseOrderAlreadyExistException;
import util.exception.PurchaseOrderHasBeenSentException;
import util.exception.PurchaseOrderNotExistException;
import util.exception.PurchaseQuotationNotApprovedException;
import util.exception.PurchaseQuotationNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.SupplierNotProvideProductException;

/**
 *
 * @author songhan
 */
@Local
public interface PurchaseOrderManagementLocal {
    public void createRawMaterialPurchaseQuotation(Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek, 
            Company buyerCompany, String supplierName, String producName, Integer batchNumber) 
            throws SupplierNotExistException, SupplierNotProvideProductException, ProductNotExistException, MultipleProductWithSameNameException;
    
    public void createFinishedGoodPurchaseQuotation(Integer scheduledSentYear, Integer scheduledSentMonth, Integer scheduledSentWeek, 
            Company buyerCompany, String supplierName, String producName, Integer batchNumber) 
            throws SupplierNotExistException, SupplierNotProvideProductException, ProductNotExistException, MultipleProductWithSameNameException;
    
    public List<PurchaseQuotation> retrieveAllPurchaseQuotationList(Company userCompany) 
            throws PurchaseQuotationNotExistException;
    
    public PurchaseQuotation retrievePurchaseQuotation(Long pqId) 
            throws PurchaseQuotationNotExistException;
    
    public void createPurchaseOrder(Long pqId)
            throws PurchaseQuotationNotExistException, PurchaseQuotationNotApprovedException;
//    public String updatePurchaseOrder(Long pqId, Long gpoId)throws PurchaseQuotationNotExistException;
    public PurchaseOrder retrievePurchaseOrder(Long gpoId)
            throws PurchaseOrderNotExistException;
    
    public void confirmPurchaseOrderArrival(PurchaseOrder po)
            throws PurchaseOrderAlreadyExistException;
    
    public void approvePurchaseQuotation(Long puId) 
            throws PurchaseQuotationNotExistException;

    public ArrayList<PurchaseQuotation> retrieveGoodsPurchaseQuotationList(String companyName) throws PurchaseQuotationNotExistException;

    public ArrayList<PurchaseQuotation> retrieveRawMaterialPurchaseQuotationList(String companyName) throws PurchaseQuotationNotExistException;

    public ArrayList<PurchaseOrder> retrieveGoodsPurchaseOrderList(String companyName) throws PurchaseOrderNotExistException;

    public ArrayList<PurchaseOrder> retrieveMaterialPurchaseOrderList(String companyName) throws PurchaseOrderNotExistException;
    
    public void sendOrder(PurchaseOrder pOrder) throws PurchaseOrderHasBeenSentException;
}
