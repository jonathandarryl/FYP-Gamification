/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import Common.entity.Location;
import OES.entity.Product;
import TMS.entity.Routes;
import TMS.entity.Vehicles;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.NoVehiclesAssignedException;
import util.exception.RoutesNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface TransRouteOptiSessionBeanLocal {
    public void addNewRoutes(Long cId, Location start, Location end, String mode, Double distance) throws CompanyNotExistException;

    public void updateRoutes(Long cId, Long rId, Location start, Location end, String mode, Double distance) throws CompanyNotExistException, RoutesNotExistException;

    public List<Routes> viewAllRoutes(Long ownerId) throws CompanyNotExistException, RoutesNotExistException;

    public void deleteRoutes(Long ownerId, Long rId) throws CompanyNotExistException, RoutesNotExistException;    

    public List<Vehicles> viewAssignedVehicles(Long ownerId, Long rId) throws CompanyNotExistException, RoutesNotExistException;

    public List<Routes> possibleRoutes(Long ownerId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public List<Vehicles> optiCheapestRoute(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public List<Vehicles> optiFastestRoute(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public Double optiShortestTime(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public Double optiCheapestPrice(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public Double optiPriceForShortestTime(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public Double optiValuestPrice(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException;

    public List<Vehicles> viewSelectedVehicles(Long orderId, Long routeId) throws RoutesNotExistException, NoPossibleRoutesException;

    public Double fastestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException;

    public Double cheapestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException;

    public Double valuestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException;

    public Boolean checkTransportationCapacityForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException;

    public Boolean locationEqual(Location l1, Location l2);
    
    public List<List<Routes>> optimizeTrack(Long ownerId, Location start, Location end) throws NoPossibleRoutesException, CompanyNotExistException;

    public Routes viewRoutes(Long rId) throws RoutesNotExistException;

    public List<Vehicles> fastestVehiclesForRouteList(Long orderId, List<Routes> selected);

    public List<Vehicles> CheapestVehiclesForRouteList(Long orderId, List<Routes> selected);

    public List<Vehicles> ValuestVehiclesForRouteList(Long orderId, List<Routes> selected);
}
