/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import MRPII.entity.BillOfMaterial;
import MRPII.entity.RawMaterial;
import MRPII.entity.ResourcePlan;
import MRPII.entity.Supplier;
import MRPII.entity.WeeklyPlan;
import OES.entity.FinishedGood;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;
import util.exception.BillOfMaterialNotExistException;
import util.exception.MonthlyPlanNotExistException;
import util.exception.MultipleMonthlyPlanConcurrentExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.MultipleWeeklyPlanConcurrentExistException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.ResourcePlanNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.WeeklyPlanNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface MaterialRequirementPlanningSessionLocal {
    public void createBillOfMaterial(String productOwnerCompanyName, String productName, List<RawMaterial> rawMaterialList, ArrayList<Double> quantityList)
            throws ProductNotExistException, MultipleProductWithSameNameException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
    
    public void updateBillOfMaterial(String productOwnerCompanyName, String productName, String rawMaterialName, Double quantity)
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException;
    
    public BillOfMaterial retieveBillOfMaterial(String productOwnerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException;
    
    public void deleteBillOfMaterial(String productOwnerCompanyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException;
    
    public ResourcePlan retrieveResourcePlan(Integer plannedYear, Integer plannedMonth, Integer plannedWeek, RawMaterial rm)
            throws ResourcePlanNotExistException;

    public ResourcePlan createResourcePlan(Double onHand, Integer year, Integer month, Integer week, FinishedGood fg, RawMaterial rm, Supplier supplier)
            throws WeeklyPlanNotExistException, BillOfMaterialNotExistException, MultipleWeeklyPlanConcurrentExistException, ProductNotExistException, MultipleProductWithSameNameException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException;

    public ArrayList<ResourcePlan> createResourcePlanForOneMonth(Double onHand, Integer year, Integer month, FinishedGood fg, RawMaterial rm, Supplier supplier) throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException, ResourcePlanNotExistException, SupplierNotExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException;
  
}
