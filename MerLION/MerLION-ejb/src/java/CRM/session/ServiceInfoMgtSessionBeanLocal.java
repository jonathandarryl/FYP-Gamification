/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import CRM.entity.ServiceCatalog;
import Common.entity.Location;
import WMS.entity.Warehouse;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.ServiceCatalogNotExistException;
import util.exception.YellowPageNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface ServiceInfoMgtSessionBeanLocal {

    //B.1.1

    public String createServiceCatalog(Long Id, String serviceName, String description, Double price, Timestamp startTime, Timestamp endTime,
            Location source, Location dest, Long warehouseId, Location warehLoca,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Boolean publicOrNot) throws CompanyNotExistException;

    //B.1.2

    public String updateServiceCatalog(Long ownerId, Long Id, String serviceName, String description, Double price, Timestamp startTime, Timestamp endTime,
            Location source, Location dest, Long warehouseId, Location warehLoca,
            Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highValue, Boolean publicOrNot) throws CompanyNotExistException, ServiceCatalogNotExistException;

    //B.1.3

    public ServiceCatalog viewServiceCatalog(Long ownerId, Long Id) throws ServiceCatalogNotExistException, CompanyNotExistException;

    public List<ServiceCatalog> viewAllServiceCatalog(Long ownerId) throws ServiceCatalogNotExistException, CompanyNotExistException;

    public ServiceCatalog viewPublicServiceCatalog(Long ownerId, Long Id, Boolean publicOrNot) throws ServiceCatalogNotExistException, CompanyNotExistException, YellowPageNotExistException;

    public List<ServiceCatalog> viewAllPublicServiceCatalog(Long ownerId) throws ServiceCatalogNotExistException, CompanyNotExistException;

    public List<ServiceCatalog> displayAllYellowPageInfo() throws ServiceCatalogNotExistException;

    public void approveServiceCatalog(Long Id);

    public void hideIt(Long Id);

    public String deleteServiceCatalog(Long ownerId, Long Id)
            throws CompanyNotExistException, ServiceCatalogNotExistException, YellowPageNotExistException;

    public List<ServiceCatalog> retrievePublicServiceCatalog() throws ServiceCatalogNotExistException;

}
