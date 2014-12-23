/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import OES.entity.Product;
import OES.entity.SalesOrder;
import OES.entity.SalesQuotation;
import WMS.entity.ExtractionRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;
import util.exception.SalesOrderAlreadyFulfilledException;
import util.exception.SalesOrderFinishedGoodtNotReadyException;
import util.exception.SalesOrderHasBeenSentException;
import util.exception.SalesOrderIsNotBackOrderException;
import util.exception.SalesOrderNotExistException;
import util.exception.SalesQuotationAlreadyEstablishedOrderException;
import util.exception.SalesQuotationNotApprovedException;
import util.exception.SalesQuotationNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface SalesOrderManagementLocal {

    public SalesQuotation createSalesQuotation(List<Product> productList, HashMap<Product, Double> productListMap, String customerName, String ownerCompanyName);

    public void updateSalesQuotation(List<Product> productList, HashMap<Product, Double> productListMap, String ownerCompanyName, String customerName, Long sqId)
            throws SalesQuotationNotExistException, CustomerNotExistException, MultipleCustomerWithSameNameException;

    public SalesQuotation retrieveSalesQuotation(Long sqId)
            throws SalesQuotationNotExistException;

    public SalesOrder createSalesOrder(Long sqId)
            throws SalesQuotationNotExistException, SalesQuotationNotApprovedException, SalesQuotationAlreadyEstablishedOrderException;

//    public void updateSalesOrder(Long sqId, Long soId);
    public SalesOrder retrieveSalesOrder(Long soId)
            throws SalesOrderNotExistException;

    public ArrayList<SalesQuotation> retrieveSalesQuotationList(String ownerCompanyName)
            throws SalesQuotationNotExistException;

    public void approveSalesQuotation(Long sqId)
            throws SalesQuotationNotExistException;
    
    public void approveSalesOrder(Long soId) 
            throws SalesOrderNotExistException;

    public ArrayList<SalesOrder> retrieveSalesOrderList(Timestamp startDate, Timestamp endDate, String companyName)
            throws SalesOrderNotExistException;

    public void markBackOrder(Long soId)
            throws SalesOrderNotExistException;

    public void updateBackOrder(Long soId)
            throws SalesOrderNotExistException, SalesOrderIsNotBackOrderException;

    public void fulfillSalesOrder(SalesOrder so)
            throws SalesOrderAlreadyFulfilledException, SalesOrderFinishedGoodtNotReadyException;

    public List<SalesOrder> retrieveBackOrderList(String ownerCompanyName) throws SalesOrderNotExistException;

    public ArrayList<SalesOrder> retrieveAllSalesOrderList(String companyName) throws SalesOrderNotExistException;

    public List<ExtractionRequest> retrieveExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder);

    public List<ExtractionRequest> retrieveFulfilledExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder);

    public List<ExtractionRequest> retrieveUnfulfilledExtractionRequestListForOneProductInOneSalesOrder(Product product, SalesOrder salesOrder);

    public Boolean checkOneProductReadyOrNot(Product product, SalesOrder salesOrder);

    public Boolean checkAllProductReadyOrNot(SalesOrder salesOrder);

    public void confirmProductReadyForSalesOrder(SalesOrder salesOrder);

    public void fulfillSalesOrderForPop(SalesOrder so);

}
