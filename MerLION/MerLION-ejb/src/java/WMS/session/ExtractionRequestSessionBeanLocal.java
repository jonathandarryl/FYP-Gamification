/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMS.session;

import CRM.entity.WarehouseOrder;
import Common.entity.Company;
import MRPII.entity.Production;
import MRPII.entity.RawMaterial;
import OES.entity.Product;
import OES.entity.SalesOrder;
import TMS.entity.TransOrder;
import WMS.entity.ExtractionRequest;
import WMS.entity.Warehouse;
import java.util.List;
import javax.ejb.Local;
import util.exception.ExtractionQuantityExceedProductionNeedException;
import util.exception.ExtractionQuantityExceedSalesOrderNeedException;
import util.exception.ExtractionRequestNotExistException;
import util.exception.InsufficientStorageInWarehouseException;
import util.exception.ShelfIncorrectProductException;
import util.exception.ShelfListInsufficientCapacityException;
import util.exception.ShelfNotExistException;
import util.exception.WarehouseNotExistException;
import util.exception.WarehouseSpecialRequirementException;

/**
 *
 * @author songhan
 */
@Local
public interface ExtractionRequestSessionBeanLocal {

    public void createExtractionRequest(Product product, Double quantity, Warehouse warehouse, Company requestInitiator, Company requestDealer, String requestSource);

    public void dealWithWarehouseOrder(WarehouseOrder wo);

    public List<ExtractionRequest> retrieveExtractionRequestListForSpecificCompany(Company requestDealer) 
            throws ExtractionRequestNotExistException;

    public void fulfillExtractionRequest(ExtractionRequest er) 
            throws ShelfNotExistException, InsufficientStorageInWarehouseException, WarehouseNotExistException, ShelfListInsufficientCapacityException, WarehouseSpecialRequirementException, ShelfIncorrectProductException;

    public void dealWithProductionRawMaterialExtraction(Product rawMaterial, Double quantity, Warehouse warehouse, Production production)
            throws ExtractionQuantityExceedProductionNeedException;

    public void dealWithSalesOrderFinishedGoodExtraction(Product product, Double quantity, Warehouse warehouse, SalesOrder so)
            throws ExtractionQuantityExceedSalesOrderNeedException;

    public void rejectExtractionRequest(ExtractionRequest er);

    public void dealWithTransOrderExtraction(TransOrder to) throws WarehouseNotExistException;
    
}
