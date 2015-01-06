/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import MRPII.entity.MonthlyPlan;
import MRPII.entity.WeeklyPlan;
import OES.entity.FinishedGood;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Local;
import util.exception.MonthlyPlanNotExistException;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleMonthlyPlanConcurrentExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.MultipleWeeklyPlanConcurrentExistException;
import util.exception.ProductNotExistException;
import util.exception.SalesForecastNotExistException;
import util.exception.TwoListSizeNotEqualException;
import util.exception.WeeklyPlanNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface ProductionPlanningSessionLocal {
    //Precon: targetMonth should be the beginning of the first day of a month, e.g. 2014 Nov 1 00:00:00
    public MonthlyPlan generateMonthlyProductionPlan(Integer year, Integer month, String companyName, String productName, Double targetInventorylevel, Double prevInventoryLevel)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, ProductNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException;
    
    //Precon: startMonth should be the beginning of the fist day of a month, e.g. 2014 Nov 1 00:00:00
            //endMonth should be the beginning of the second day of the end month, e.g. 2015 Mar 2 00:00:00
            //The maximum number of continuous months is 12
    
    public ArrayList<MonthlyPlan> generateMonthlyProductionPlanList(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth, String companyName, String productName, Double startInventoryLevel, ArrayList<Double> targetInventoryList)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, TwoListSizeNotEqualException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException;
    
    public MonthlyPlan retrieveMonthlyProductionPlan(Integer year, Integer month, String companyName, String productName)
            throws ProductNotExistException, MultipleMonthlyPlanConcurrentExistException, MonthlyPlanNotExistException, MultipleProductWithSameNameException;
    
    public WeeklyPlan generateWeeklyProductionPlan(Integer year, Integer month, Integer week, String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException;
    
    public ArrayList<WeeklyPlan>generateWeeklyProductionPlanOfTheMonth(Integer year, Integer month, String companyName, String productName, Double targetInventorylevel, Double prevInventoryLevel)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException;
        
    public WeeklyPlan retrieveWeeklyPlan(Calendar startDate, MonthlyPlan monthlyPlan) 
            throws WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException;

    public WeeklyPlan retrieveWeeklyPlan(Integer year, Integer month, Integer week, FinishedGood fg) 
            throws WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException;
    
    public List<MonthlyPlan> retrieveMonthlyPlanList(String ownerCompanyName)
            throws MonthlyPlanNotExistException;
 
}
