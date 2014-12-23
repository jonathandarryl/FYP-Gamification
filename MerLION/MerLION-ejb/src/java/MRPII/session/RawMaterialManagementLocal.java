/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import MRPII.entity.RawMaterial;
import java.util.ArrayList;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.RawMaterialAlreadyExistException;
import util.exception.RawMaterialNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface RawMaterialManagementLocal {
    public void createRawMaterial(String ownerCompanyName, String productName, Double productPrice, String productDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws RawMaterialAlreadyExistException, CompanyNotExistException;
    
    public void updateRawMaterial(String ownerCompanyName, String rawMaterialName, Double rawMaterialPrice, String rawMaterialDescription, String unit, Double quantityInOneUnitCapacity, Double qunatityInOneBatch)
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
    
    public RawMaterial retrieveRawMaterial(String ownerCompanyName, String RawMaterialName)
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
    
    public ArrayList<RawMaterial> retrieveRawMaterialList(String ownerCompanyName) 
            throws RawMaterialNotExistException;
    
    public void deleteRawMaterial(String ownerCompanyName, String RawMaterialName)
            throws RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
}
