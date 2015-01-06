/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TMS.session;

import TMS.entity.Drivers;
import TMS.entity.Vehicles;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.VehicleNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface TransAssetMgtSessionBeanLocal {    
//    public String selectCreateTransAsset(int choice);
    public String createTrucks(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail, Drivers driver)throws CompanyNotExistException;
    public String createPlanes(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail)throws CompanyNotExistException;
    public String createVessel(Long ownerId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail)throws CompanyNotExistException;
    
    public List<Vehicles> viewAllTrucks(Long companyId)throws CompanyNotExistException, VehicleNotExistException;
    public List<Vehicles> viewAllPlanes(Long companyId)throws CompanyNotExistException, VehicleNotExistException;
    public List<Vehicles> viewAllVessel(Long companyId)throws CompanyNotExistException, VehicleNotExistException;
    public List<Vehicles> viewAllVehicles(Long companyId)throws CompanyNotExistException, VehicleNotExistException;
            
    public String updateTrucks(Long ownerId, Long vehiclesId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail)throws CompanyNotExistException, VehicleNotExistException;
    public String updatePlaneOrVessel(Long ownerId, Long vehiclesId, Integer useYear, Long capacity, Long maxDistance, Double transCost, Double velocity, Boolean perishable, Boolean flammable, Boolean pharmaceutical, Boolean highvalue, Boolean avail)throws CompanyNotExistException, VehicleNotExistException;
    
    public String deleteTransAsset(Long ownerId, String type, Long vehiclesId)throws CompanyNotExistException, VehicleNotExistException;

    public Vehicles viewVehicles(Long vId) throws VehicleNotExistException;
}
