/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TMS.session;

import Common.entity.Company;
import Common.entity.Location;
import Common.entity.PartnerCompany;
import OES.entity.Product;
import TMS.entity.Routes;
import TMS.entity.TransOrder;
import TMS.entity.Vehicles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.NoPossibleRoutesException;
import util.exception.RoutesNotExistException;
import util.exception.NoVehiclesAssignedException;

/**
 *
 * @author HanXiangyu
 */
@Stateless
public class TransRouteOptiSessionBean implements TransRouteOptiSessionBeanLocal {

    @PersistenceContext(unitName = "MerLION-ejbPU")
    private EntityManager em;

    @Override
    public void addNewRoutes(Long cId, Location start, Location end, String mode, Double distance) throws CompanyNotExistException {
        Company pc = em.find(Company.class, cId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + cId + " not exist!");
        } else {
            Routes routes = new Routes();
            routes.setDestOfRoute(end);
            routes.setDistance(distance);
            routes.setMode(mode);
            routes.setStartOfRoute(start);
            routes.setpCompany((PartnerCompany) pc);
            em.persist(routes);
        }
    }

    @Override
    public void deleteRoutes(Long ownerId, Long rId) throws CompanyNotExistException, RoutesNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Routes routes = em.find(Routes.class, rId);
            if (routes == null) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                em.remove(routes);
            }
        }
    }
    
    @Override
    public Routes viewRoutes(Long rId) throws RoutesNotExistException{
        Query query = em.createQuery("SELECT r FROM Routes r WHERE r.routesId=?1");
            query.setParameter(1, rId);
            @SuppressWarnings("unchecked")
            Routes rList = (Routes) query.getSingleResult();
            if (rList == null) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                return rList;
            }
    }

    @Override
    public List<Routes> viewAllRoutes(Long ownerId) throws CompanyNotExistException, RoutesNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query query = em.createQuery("SELECT r FROM Routes r WHERE r.pCompany.id=?1");
            query.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<Routes> rList = (List<Routes>) query.getResultList();
            if (rList == null) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                return rList;
            }
        }
    }

    @Override
    public void updateRoutes(Long cId, Long rId, Location start, Location end, String mode, Double distance) throws CompanyNotExistException, RoutesNotExistException {
        Company pc = em.find(Company.class, cId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + cId + " not exist!");
        } else {
            Routes routes = em.find(Routes.class, rId);
            if (routes == null) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                routes.setDestOfRoute(end);
                routes.setDistance(distance);
                routes.setMode(mode);
                routes.setStartOfRoute(start);
                em.persist(routes);
            }
        }
    }

    @Override
    public List<Vehicles> viewAssignedVehicles(Long ownerId, Long rId) throws CompanyNotExistException, RoutesNotExistException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Routes routes = em.find(Routes.class, rId);
            if (routes == null) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                return (List<Vehicles>) routes.getVehicles();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Vehicles> viewSelectedVehicles(Long orderId, Long routeId) throws RoutesNotExistException {
        Routes routes = em.find(Routes.class, routeId);
        if (routes == null) {
            throw new RoutesNotExistException("Routes not exist!");
        } else {
            List<Vehicles> vList = (List<Vehicles>) routes.getVehicles();
            List<Vehicles> result = new ArrayList<>();
            Query q = em.createQuery("SELECT v FROM Vehicles v WHERE v.transOrderId=?1 AND v.route.routesId=?2");
            q.setParameter(1, orderId);
            q.setParameter(2, routeId);
//                 for(Vehicles v: vList){
//                     if(!(v.getTransOrderId()==null) && v.getTransOrderId().equals(orderId)){
//                         result.add(v);
//                     }
//                 }
            return q.getResultList();
        }
    }

    @Override
    public List<Vehicles> optiFastestRoute(Long ownerId, Long orderId, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        List<Vehicles> result = new ArrayList<>();
        for (Routes s : selected) {
            result.add(selectFastestVehicle(s, orderId));
        }
        return result;
    }
    
    public List<Vehicles> fastestVehiclesForRouteList(Long orderId, List<Routes> selected){
        List<Vehicles> result = new ArrayList<>();
        for (Routes s : selected) {
            result.add(selectFastestVehicle(s, orderId));
        }
        return result;
    }
    
    @Override
    public List<Vehicles> CheapestVehiclesForRouteList(Long orderId, List<Routes> selected){
        List<Vehicles> result = new ArrayList<>();
        for (Routes s : selected) {
            result.add(selectCheapestVehicle(s, orderId));
        }
        return result;
    }
    
    @Override
    public List<Vehicles> ValuestVehiclesForRouteList(Long orderId, List<Routes> selected){
        List<Vehicles> result = new ArrayList<>();
        for (Routes s : selected) {
            result.add(selectValuestVehicle(s, orderId));
        }
        return result;
    }

    @Override
    public Double fastestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException {
        List<Routes> rList = possibleRoutes(ownerId, start, end);
        Double q = quantity / p.getQuantityInOneUnitCapacity();
        Double price = 0.0;
        for (Routes r : rList) {
            try {
                Vehicles[] vArray = sortFastestVehicles(r);
                int index = 0;
                while (q > 0 && index < vArray.length) {
                    price += r.getDistance() / vArray[index].getTransCost();
                    q -= vArray[index].getCapacity();
                    index++;
                }
            } catch (NoVehiclesAssignedException ex) {
                throw new NoVehiclesAssignedException("Please assign vehicles first");
            }
        }
        return price;
    }

    @Override
    public Double cheapestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException {
        List<Routes> rList = possibleRoutes(ownerId, start, end);
        Double q = quantity / p.getQuantityInOneUnitCapacity();
        Double price = 0.0;
        for (Routes r : rList) {
            try {
                Vehicles[] vArray = sortCheapestVehicles(r);
                int index = 0;
                while (q > 0 && index < vArray.length) {
                    price += r.getDistance() / vArray[index].getTransCost();
                    q -= vArray[index].getCapacity();
                    index++;
                }
            } catch (NoVehiclesAssignedException ex) {
                throw new NoVehiclesAssignedException("Please assign vehicles first");
            }
        }
        return price;
    }

    @Override
    public Double valuestPriceForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException {
        List<Routes> rList = possibleRoutes(ownerId, start, end);
        Double q = quantity / p.getQuantityInOneUnitCapacity();
        Double price = 0.0;
        for (Routes r : rList) {
            try {
                Vehicles[] vArray = sortValuestVehicles(r);
                int index = 0;
                while (q > 0 && index < vArray.length) {
                    price += r.getDistance() / vArray[index].getTransCost();
                    q -= vArray[index].getCapacity();
                    index++;
                }
            } catch (NoVehiclesAssignedException ex) {
                throw new NoVehiclesAssignedException("Please assign vehicles first");
            }
        }
        return price;
    }

    @Override
    public Double optiShortestTime(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        Double result = 0.0;
        for (Routes s : selected) {
            result += s.getDistance() / selectFastestVehicle(s, orderId).getVelocity();
        }
        return result;
    }

    @Override
    public Double optiPriceForShortestTime(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        Double result = 0.0;
        for (Routes s : selected) {
            result += s.getDistance() / selectFastestVehicle(s, orderId).getTransCost();
        }
        return result;
    }

    @Override
    public List<Vehicles> optiCheapestRoute(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        List<Vehicles> result = new ArrayList<>();
        for (Routes s : selected) {
            result.add(selectCheapestVehicle(s, orderId));
        }
        return result;
    }

    @Override
    public Double optiCheapestPrice(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        Double result = 0.0;
        TransOrder order = em.find(TransOrder.class, orderId);
        Double quantity = order.getQuantity() / order.getProduct().getQuantityInOneUnitCapacity();
        for (Routes s : selected) {
            result += s.getDistance() / selectCheapestVehicle(s, orderId).getTransCost();
        }
        return result;
    }

    @Override
    public Double optiValuestPrice(Long ownerId, Long orderId, Location start, Location end) throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        ArrayList<Routes> selected = (ArrayList<Routes>) possibleRoutes(ownerId, start, end);
        Double result = 0.0;
        for (Routes s : selected) {
            result += s.getDistance() / selectValuestVehicle(s, orderId).getTransCost();
        }
        return result;
    }

    @Override
    public List<Routes> possibleRoutes(Long ownerId, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoPossibleRoutesException {
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        } else {
            Query q = em.createQuery("SELECT r FROM Routes r WHERE r.pCompany.id=?1");
            q.setParameter(1, ownerId);
            @SuppressWarnings("unchecked")
            List<Routes> routes = q.getResultList();
            ArrayList<Routes> selected = new ArrayList<>();
            if (routes.isEmpty()) {
                throw new RoutesNotExistException("Routes not exist!");
            } else {
                for (Routes r : routes) {
                    if (locationEqual(r.getStartOfRoute(), start)) {
                        selected.add(r);
                        break;
                    }
                }
                if (selected.isEmpty()) {
                    throw new NoPossibleRoutesException("Your TMS cannot go through this route!");
                }
                Routes last = selected.get(0);
                while (!locationEqual(last.getDestOfRoute(), end)) {
                    for (Routes r : routes) {
                        if (locationEqual(r.getStartOfRoute(), last.getDestOfRoute())) {
                            selected.add(r);
                            last = r;
                            break;
                        }
                    }
                }
                return selected;
            }
        }
    }
    @Override
    public List<List<Routes>> optimizeTrack(Long ownerId, Location start, Location end) throws NoPossibleRoutesException, CompanyNotExistException {
        System.out.println("inside optimize track");
        Company pc = em.find(Company.class, ownerId);
        if (pc == null) {
            throw new CompanyNotExistException("Company " + ownerId + " not exist!");
        }
        Query q = em.createQuery("SELECT r FROM Routes r WHERE r.pCompany.id=?1");
        q.setParameter(1, ownerId);
        List<Routes> routes = q.getResultList();
        System.out.println("after retrieve all routes");

        List<List<Routes>> tracks = new ArrayList();
        for (Routes r : routes) {
            if (locationEqual(r.getStartOfRoute(), start)) {
                List<List<Routes>> temp = this.findNextRoute(r, routes, end);
                for (List<Routes> l : temp) {
                    System.out.println("inside first loop");
                    List<Routes> path = new ArrayList();
                    path.addAll(l);
                    System.out.println("Selected Routes "+ path.toString());
                    tracks.add(path);
                }
            }
        }
        for(List<Routes> m: tracks) {
            System.out.println(m);
        }
        return tracks;
    }

    private List<List<Routes>> findNextRoute(Routes last, List<Routes> routes, Location end) {
        System.out.println("inside find next routes");
        List<List<Routes>> tracks = new ArrayList();
        if (locationEqual(last.getDestOfRoute(), end)) {
            System.out.println("inside deep down");
            List<Routes> path = new ArrayList();
            path.add(last);
            tracks.add(path);
            return tracks;
        } else {
            for (Routes r : routes) {
                if (locationEqual(r.getStartOfRoute(), last.getDestOfRoute())) {
                    System.out.println("findnextroute inside 2");
                    List<List<Routes>> temp = this.findNextRoute(r, routes, end);
                    for (List<Routes> p : temp) {
                        List<Routes> path = new ArrayList();
                        path.add(last);
                        path.addAll(p);
                        tracks.add(path);
                    }
                }
            }
            return tracks;
        }
    }

    private Vehicles selectFastestVehicle(Routes route, Long orderId) {
        if (route.getVehicles() == null) {
            return null;
        }
        List<Vehicles> vList = (List<Vehicles>) route.getVehicles();
        if (vList.isEmpty()) {
            return null;
        } else {
            Vehicles result = vList.get(0);
            Double fast = result.getVelocity();
            for (Vehicles v : vList) {
                if (v.getVelocity() >= fast && (v.getTransOrderId() == null || !orderId.equals(v.getTransOrderId()))) {
                    fast = v.getVelocity();
                    result = v;
                }
            }
            return result;
        }
    }

    private Vehicles selectCheapestVehicle(Routes route, Long orderId) {
        if (route.getVehicles() == null) {
            return null;
        }
        List<Vehicles> vList = (List<Vehicles>) route.getVehicles();
        if (vList.isEmpty()) {
            return null;
        } else {
            Vehicles result = vList.get(0);
            Double low = result.getTransCost();
            for (Vehicles v : vList) {
                if (v.getTransCost() < low && (v.getTransOrderId() == null || !orderId.equals(v.getTransOrderId()))) {
                    low = v.getTransCost();
                    result = v;
                }
            }
            return result;
        }
    }

    private Vehicles selectValuestVehicle(Routes route, Long orderId) {
        if (route.getVehicles() == null) {
            return null;
        }
        List<Vehicles> vList = (List<Vehicles>) route.getVehicles();
        if (vList.isEmpty()) {
            return null;
        } else {
            Vehicles result = vList.get(0);
            Double low = result.getTransCost() / result.getCapacity();
            for (Vehicles v : vList) {
                if (v.getTransCost() < low && (v.getTransOrderId() == null || !orderId.equals(v.getTransOrderId()))) {
                    low = v.getTransCost() / v.getCapacity();
                    result = v;
                }
            }
            return result;
        }
    }

    @Override
    public Boolean locationEqual(Location l1, Location l2) {
        Boolean check = l1.getCountry().equals(l2.getCountry()) && l1.getState().equals(l2.getState()) && l1.getCity().equals(l2.getCity())
                && l1.getStreet().equals(l2.getStreet()) && l1.getPostalCode().equals(l2.getPostalCode());
        if (l1.getBlockNo() == null && l2.getBlockNo() == null) {
            return check;
        } else if (l1.getBlockNo() != null && l2.getBlockNo() != null) {
//            System.out.println(l1.getBlockNo()+" "+l2.getBlockNo());
            return check && l1.getBlockNo().equals(l2.getBlockNo());
        } else {
            return false;
        }
    }

    private Vehicles[] sortFastestVehicles(Routes r) throws NoVehiclesAssignedException {
        if (r.getVehicles().isEmpty()) {
            throw new NoVehiclesAssignedException();
        }
        List<Vehicles> v = (List<Vehicles>) r.getVehicles();
//       System.out.println("size of v: " + v.size());
        Vehicles[] vList = new Vehicles[v.size()];
        for (int i = 0; i < v.size(); i++) {
            vList[i] = v.get(i);
        }
        Vehicles temp;
//       System.out.println("size of vList: " + vList.length);
        //List<Vehicles> result = new ArrayList<>();
        for (int i = 0; i < vList.length; i++) {
            for (int j = i + 1; j < vList.length; j++) {
                if (vList[j].getVelocity() > vList[i].getVelocity()) {
                    temp = vList[i];
                    vList[i] = vList[j];
                    vList[j] = temp;
                }
            }
        }
        return vList;
    }

    private Vehicles[] sortCheapestVehicles(Routes r) throws NoVehiclesAssignedException {
        if (r.getVehicles() == null) {
            throw new NoVehiclesAssignedException();
        }
        List<Vehicles> v = (List<Vehicles>) r.getVehicles();
//       System.out.println("size of v: " + v.size());
        Vehicles[] vList = new Vehicles[v.size()];
        for (int i = 0; i < v.size(); i++) {
            vList[i] = v.get(i);
        }
        Vehicles temp;
//       System.out.println("size of vList: " + vList.length);
        for (int i = 0; i < vList.length; i++) {
            for (int j = i + 1; j < vList.length; j++) {
                if (vList[j].getTransCost() < vList[i].getTransCost()) {
                    temp = vList[i];
                    vList[i] = vList[j];
                    vList[j] = temp;
                }
            }
        }
        return vList;
    }

    private Vehicles[] sortValuestVehicles(Routes r) throws NoVehiclesAssignedException {
        if (r.getVehicles() == null) {
            throw new NoVehiclesAssignedException();
        }
        List<Vehicles> v = (List<Vehicles>) r.getVehicles();
        Vehicles[] vList = new Vehicles[v.size()];
        for (int i = 0; i < v.size(); i++) {
            vList[i] = v.get(i);
        }
        Vehicles temp;
//       System.out.println("size of v: " + vList.length);      
        //List<Vehicles> result = new ArrayList<>();
        for (int i = 0; i < vList.length; i++) {
            for (int j = i + 1; j < vList.length; j++) {
                if ((vList[j].getTransCost() / vList[j].getCapacity()) < (vList[i].getTransCost() / vList[i].getCapacity())) {
                    temp = vList[i];
                    vList[i] = vList[j];
                    vList[j] = temp;
                }
            }
        }
        return vList;
    }

    @Override
    public Boolean checkTransportationCapacityForQuotation(Long ownerId, Product p, Double quantity, Location start, Location end)
            throws CompanyNotExistException, RoutesNotExistException, NoVehiclesAssignedException, NoPossibleRoutesException {
        //add detailed methods
        List<Routes> rList = possibleRoutes(ownerId, start, end);
        Double capa = quantity / p.getQuantityInOneUnitCapacity();
        for (Routes r : rList) {
            if (r.getVehicles() == null) {
                return false;
            }
            if (r.getVehicles() == null) {
                throw new NoVehiclesAssignedException();
            }
            List<Vehicles> vList = (List<Vehicles>) r.getVehicles();
            Double total = 0.0;
            for (Vehicles v : vList) {
                total += v.getCapacity();
            }
            if (total < capa) {
                return false;
            }
        }
        return true;
    }

}
