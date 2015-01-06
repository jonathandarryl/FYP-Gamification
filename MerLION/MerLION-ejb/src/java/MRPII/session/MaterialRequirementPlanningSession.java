/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import MRPII.entity.BillOfMaterial;
import MRPII.entity.PurchaseOrder;
import MRPII.entity.RawMaterial;
import MRPII.entity.ResourcePlan;
import MRPII.entity.Supplier;
import MRPII.entity.WeeklyPlan;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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
@Stateless
public class MaterialRequirementPlanningSession implements MaterialRequirementPlanningSessionLocal {
    @PersistenceContext
    EntityManager em;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private RawMaterialManagementLocal rmm;
    @EJB
    private SupplierManagementLocal sm;
    @EJB
    private ProductionPlanningSessionLocal pps;
        
    @Override
    public void createBillOfMaterial(String productOwnerCompanyName, String productName, List<RawMaterial> rawMaterialList, ArrayList<Double> quantityList) 
            throws ProductNotExistException, MultipleProductWithSameNameException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        FinishedGood fg = pim.retrieveProduct(productOwnerCompanyName, productName);
    //    assertNotNull(fg);
    //    assertEquals(rawMaterialNameList.size(), quantityList.size());
        HashMap <RawMaterial, Double> materialQuantityMap = new HashMap<>();
        int size = rawMaterialList.size();
        for (int i=0; i<size; i++){
            RawMaterial curMaterial=rawMaterialList.get(i);
            materialQuantityMap.put(curMaterial, quantityList.get(i));
        }
        System.out.println("before creating bom");
        BillOfMaterial bom = new BillOfMaterial(fg, rawMaterialList, materialQuantityMap);
        em.persist(bom);
        System.out.println("successfully persisted bom");
        fg.setBillOfMaterial(bom);
        System.out.println("set fg");
        em.merge(fg);
        System.out.println("successfully merged fg");
    }

    @Override
    public void updateBillOfMaterial(String productOwnerCompanyName, String productName, String rawMaterialName, Double quantity) 
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException{
        BillOfMaterial bom = retieveBillOfMaterial(productOwnerCompanyName, productName);
        assertNotNull(bom);
        RawMaterial rm = rmm.retrieveRawMaterial(productOwnerCompanyName, rawMaterialName);
        assertNotNull(rm);  
        bom.getMaterialQuantityMap().put(rm, quantity);
        em.merge(bom);
    }

    @Override
    public BillOfMaterial retieveBillOfMaterial(String productOwnerCompanyName, String productName)
        throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException{
        FinishedGood fg = pim.retrieveProduct(productOwnerCompanyName, productName);
        assertNotNull(fg);
        BillOfMaterial bom = fg.getBillOfMaterial();
        if(bom==null)
            throw new BillOfMaterialNotExistException("BOM for product "+productName+" not found!");
        else System.out.println("bom product is "+bom.getFinishedGood().getProductName());
        assertNotNull(bom);
        return bom;
    }

    @Override
    public void deleteBillOfMaterial(String productOwnerCompanyName, String productName) 
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException{
        BillOfMaterial bom = retieveBillOfMaterial(productOwnerCompanyName, productName);
        assertNotNull(bom);
        bom.setArchivedOrNot(Boolean.TRUE);
        em.merge(bom);
    }

    
    @Override
    public ResourcePlan createResourcePlan(Double onHand, Integer year, Integer month, Integer week, FinishedGood fg, RawMaterial rm, Supplier supplier) 
            throws WeeklyPlanNotExistException, BillOfMaterialNotExistException, MultipleWeeklyPlanConcurrentExistException, ProductNotExistException, MultipleProductWithSameNameException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException{
        String companyName = fg.getOwnerCompanyName();
        String productName = fg.getProductName();
        WeeklyPlan wp = pps.generateWeeklyProductionPlan(year, month, week, companyName, productName);
        Double MPSweeklyDemand = wp.getWeeklyDemand();
        Integer leadTime = supplier.getLagTimeMap().get(rm);
        BillOfMaterial bom = fg.getBillOfMaterial();
        if(bom==null)
            throw new BillOfMaterialNotExistException("BOM for product "+productName+" not found!");
        Double grossRequirement = MPSweeklyDemand * bom.getMaterialQuantityMap().get(rm);
        Double scheduleReceipt = 0.0;
        Double plannedReceipt = 0.0;
        Double plannedOrders = 0.0;
        
        if(week<=leadTime)
            scheduleReceipt = this.retrieveScheduleReceipt(year, month, week, rm);        
        else
            plannedReceipt = this.retrieveScheduleReceipt(year, month, week, rm);
        
        Calendar curTime = this.getFirstDayOfOneWeek(year, month, week);
        Integer weekCountInMonth = curTime.getActualMaximum(Calendar.WEEK_OF_MONTH);
        if(week<=weekCountInMonth-leadTime)
            plannedOrders = this.retrievePlannedOrders(year, month, week, rm);
        
        ResourcePlan rp = new ResourcePlan(grossRequirement, plannedOrders, scheduleReceipt, plannedReceipt, onHand, wp, rm, supplier);
        em.persist(rp);
        
        return rp;
    }
    
    @Override
    public ArrayList<ResourcePlan> createResourcePlanForOneMonth(Double onHand, Integer year, Integer month, FinishedGood fg, RawMaterial rm, Supplier supplier)
            throws ProductNotExistException, MultipleProductWithSameNameException, BillOfMaterialNotExistException, RawMaterialNotExistException, MultipleRawMaterialWithSameNameException, ResourcePlanNotExistException, SupplierNotExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException{
        ArrayList<ResourcePlan> rpList = new ArrayList<>();
        Calendar curTime = this.getFirstDayOfOneWeek(year, month, 1);
        Integer weekCountInMonth = curTime.getActualMaximum(Calendar.WEEK_OF_MONTH);
        for(int i=1; i<weekCountInMonth+1; i++){
            ResourcePlan curRp = this.createResourcePlan(onHand, year, month, i, fg, rm, supplier);
            onHand = onHand + curRp.getScheduledReceipt() +curRp.getPlannedReceipt() - curRp.getGrossRequirements();
            rpList.add(curRp);
        }
        
        return rpList;
    }
    
    @Override
    public ResourcePlan retrieveResourcePlan(Integer plannedYear, Integer plannedMonth, Integer plannedWeek, RawMaterial rm)
            throws ResourcePlanNotExistException{
        Query query=em.createQuery("select r from ResourcePlan r where r.plannedYear=?1 and r.plannedMonth=?2 and r.plannedWeek=?3 and r.rawMaterial=?4");
        query.setParameter(1, plannedYear);
        query.setParameter(2, plannedMonth);
        query.setParameter(3, plannedWeek);
        query.setParameter(4, rm);
        ArrayList <ResourcePlan> resourcePlanList=new ArrayList(query.getResultList());     
        
        if(resourcePlanList.isEmpty())
            throw new ResourcePlanNotExistException("Resource plan of for Year"+plannedYear+" Month"+plannedMonth+" not found!");
        ResourcePlan result = (ResourcePlan) resourcePlanList.get(0);
        return result;
    }

    private Double retrieveScheduleReceipt(Integer plannedYear, Integer plannedMonth, Integer plannedWeek, RawMaterial rm){
        
        Query query=em.createQuery("select s from PurchaseOrder s where s.purchaseQuotation.scheduledArrivalYear=?1 and s.purchaseQuotation.scheduledArrivalMonth=?2 and s.purchaseQuotation.scheduledArrivalWeek=?3 and s.purchaseQuotation.product=?4");
        query.setParameter(1, plannedYear);
        query.setParameter(2, plannedMonth);
        query.setParameter(3, plannedWeek);
        query.setParameter(4, rm);
        ArrayList <PurchaseOrder> purchaseOrderList=new ArrayList(query.getResultList());
        
        Double result = 0.0;                
        if(purchaseOrderList.isEmpty())
            return result;
                
        for(Object o: purchaseOrderList){
            PurchaseOrder curPo = (PurchaseOrder) o;
            result = result+curPo.getPurchaseQuotation().getQuantity();
        }
        return result;
    }
    
    private Double retrievePlannedOrders(Integer plannedYear, Integer plannedMonth, Integer plannedWeek, RawMaterial rm){
        Query query=em.createQuery("select s from PurchaseOrder s where s.purchaseQuotation.scheduledSentYear=?1 and s.purchaseQuotation.scheduledSentMonth=?2 and s.purchaseQuotation.scheduledSentWeek=?3 and s.purchaseQuotation.product=?4");
        query.setParameter(1, plannedYear);
        query.setParameter(2, plannedMonth);
        query.setParameter(3, plannedWeek);
        query.setParameter(4, rm);
        ArrayList <PurchaseOrder> purchaseOrderList=new ArrayList(query.getResultList());
        
        Double result = 0.0;                
        if(purchaseOrderList.isEmpty())
            return result;
                
        for(Object o: purchaseOrderList){
            PurchaseOrder curPo = (PurchaseOrder) o;
            result = result+curPo.getPurchaseQuotation().getQuantity();
        }
        return result;
    }
    
    private Calendar getFirstDayOfOneWeek(Integer year, Integer month, Integer week){
        Calendar result = Calendar.getInstance();
        result.set(year, month-1, 1, 0, 0, 0);
        Integer encounterMonday = 0;
        
        while(encounterMonday<week){
            if(result.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
                encounterMonday++;
            if(Objects.equals(encounterMonday, week))
                break;
            result.add(Calendar.DAY_OF_MONTH, 1);
        }
        result = eliminateMiliSecond(result);
        return result;
    }
    
    private Calendar eliminateMiliSecond(Calendar target){
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime()%1000);
        target.setTime(time);
        return target;
    }
}
