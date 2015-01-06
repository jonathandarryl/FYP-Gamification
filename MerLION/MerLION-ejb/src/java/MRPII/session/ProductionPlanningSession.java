/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import MRPII.entity.MonthlyPlan;
import MRPII.entity.SalesForecast;
import MRPII.entity.WeeklyPlan;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertEquals;
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
@Stateless
public class ProductionPlanningSession implements ProductionPlanningSessionLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private SalesForecastSessionLocal salesForecastSession;

    private Integer calculateWorkingDaysInWeek(Calendar startDate) {
        Calendar weekStartDate = startDate;
        Integer dayInMonth = weekStartDate.get(Calendar.DAY_OF_MONTH);
        Integer workingday = 5;

        if (weekStartDate.getActualMaximum(Calendar.DAY_OF_MONTH) - dayInMonth < 4) {
            workingday = weekStartDate.getActualMaximum(Calendar.DAY_OF_MONTH) - dayInMonth + 1;
        }
        return workingday;
    }

    private String returnWeekWorkingPeriodString(Calendar startDate, Calendar endDate) {
        String startStr = startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1);
        String endStr = endDate.get(Calendar.DAY_OF_MONTH) + "/" + (endDate.get(Calendar.MONTH) + 1);
        String result = startStr + "-" + endStr;
        return result;
    }

    private Calendar getFirstDayOfOneWeek(Integer year, Integer month, Integer week) {
        Calendar result = Calendar.getInstance();
        result.set(year, month - 1, 1, 0, 0, 0);
        Integer encounterMonday = 0;

        while (encounterMonday < week) {
            if (result.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                encounterMonday++;
            }
            if (Objects.equals(encounterMonday, week)) {
                break;
            }
            result.add(Calendar.DAY_OF_MONTH, 1);
        }
        result = eliminateMiliSecond(result);
        return result;
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

    @Override
    public MonthlyPlan generateMonthlyProductionPlan(Integer year, Integer month, String companyName, String productName, Double targetInventorylevel, Double prevInventoryLevel)
            throws MonthlyRecordNotExistException, ProductNotExistException, MultipleProductWithSameNameException, MultipleMonthlyPlanConcurrentExistException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException {
        try {
            MonthlyPlan oldMonthlyPlan = retrieveMonthlyProductionPlan(year, month, companyName, productName);
            oldMonthlyPlan.setArchivedOrNot(Boolean.TRUE);
            em.merge(oldMonthlyPlan);
        } catch (MonthlyPlanNotExistException ex) {
            System.out.println("There is no existing monthly plan. Proceed to generate a new one.");
        }
        Calendar targetMonth = Calendar.getInstance();
        targetMonth.set(year, month - 1, 1, 0, 0, 0);
        targetMonth = eliminateMiliSecond(targetMonth);
        SalesForecast sf = salesForecastSession.generateSalesForecast(year, month, companyName, productName);
        Timestamp targetTimestamp = new Timestamp(targetMonth.getTimeInMillis());
        Double productionPlan = sf.getAmount() - prevInventoryLevel + targetInventorylevel;
        MonthlyPlan monthlyPlan = new MonthlyPlan(targetTimestamp, productionPlan, targetInventorylevel, sf, year, month);

        em.persist(monthlyPlan);
        System.out.println("Monthly production plan for Month " + month + " in Year " + year + " of Company " + companyName + " has been created successfully.");
        return monthlyPlan;
    }

    @Override
    public MonthlyPlan retrieveMonthlyProductionPlan(Integer year, Integer month, String companyName, String productName)
            throws ProductNotExistException, MultipleMonthlyPlanConcurrentExistException, MonthlyPlanNotExistException, MultipleProductWithSameNameException {
        FinishedGood fg = pim.retrieveProduct(companyName, productName);
        Query query = em.createQuery("select m from MonthlyPlan m where m.planYear=?1 and m.planMonth=?2 and m.salesForecast.finishedGood=?3 and m.archivedOrNot=FALSE");
        query.setParameter(1, year);
        query.setParameter(2, month);
        query.setParameter(3, fg);
        ArrayList<MonthlyPlan> monthlyPlanList = new ArrayList(query.getResultList());
        if (monthlyPlanList.isEmpty()) {
            throw new MonthlyPlanNotExistException(year + "-" + month + " Monthly plan for product " + productName + " not found!");
        }
        if (monthlyPlanList.size() != 1) {
            throw new MultipleMonthlyPlanConcurrentExistException("Error! There are multiple monthly production plan for product" + productName + " in " + year + "-" + month);
        }
        assertEquals(monthlyPlanList.size(), 1);

        MonthlyPlan mp = (MonthlyPlan) monthlyPlanList.get(0);
        return mp;
    }

    @Override
    public ArrayList<MonthlyPlan> generateMonthlyProductionPlanList(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth,
            String companyName, String productName, Double startInventoryLevel, ArrayList<Double> targetInventoryList)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, ProductNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, TwoListSizeNotEqualException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException {
        Integer monthNumber = endMonth - startMonth + 1;
        if (startYear < endYear) {
            monthNumber = monthNumber + 12 * (endYear - startYear);
        }

        if (targetInventoryList.size() != monthNumber) {
            throw new TwoListSizeNotEqualException("The size of targetInventoryList is not equal to the number of selected months.");
        }

        ArrayList<MonthlyPlan> resultList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.set(startYear, startMonth - 1, 1, 0, 0, 0);
        startTime = eliminateMiliSecond(startTime);
        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth - 1, 2, 0, 0, 0);
        endTime = eliminateMiliSecond(endTime);
        Calendar markTime = startTime;
        int i = 0;
        Double curPrevInvLevel = startInventoryLevel;
        while (markTime.before(endTime)) {
            Double curTargetInv = targetInventoryList.get(i);
            Integer year = markTime.get(Calendar.YEAR);
            Integer month = markTime.get(Calendar.MONTH) + 1;
            MonthlyPlan curMonthlyPlan = generateMonthlyProductionPlan(year, month, companyName, productName, curTargetInv, curPrevInvLevel);
            i++;
            curPrevInvLevel = curTargetInv;
            markTime.add(Calendar.MONTH, 1);
            resultList.add(curMonthlyPlan);
        }
        return resultList;
    }

    @Override
    public WeeklyPlan generateWeeklyProductionPlan(Integer year, Integer month, Integer week, String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException {
        Calendar startDate = getFirstDayOfOneWeek(year, month, week);
        startDate = eliminateMiliSecond(startDate);
        MonthlyPlan monthlyPlan = retrieveMonthlyProductionPlan(year, month, companyName, productName);
        if (month == 12) {
            month = 1;
            year = year + 1;
        } else {
            month++;
        }
        MonthlyPlan nextMonthlyPlan = retrieveMonthlyProductionPlan(year, month, companyName, productName);
        WeeklyPlan wp = generateWeeklyProductionPlan(startDate, monthlyPlan, nextMonthlyPlan);
        return wp;
    }

    private WeeklyPlan generateWeeklyProductionPlan(Calendar startDate, MonthlyPlan monthlyPlan, MonthlyPlan nextMonthlyPlan) throws WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException {
        Double monthlyDemand = monthlyPlan.getProductionPlan();
        Double dailyDemand = monthlyDemand / monthlyPlan.getWorkingDay();
        Double monthlyDemand2 = nextMonthlyPlan.getProductionPlan();
        Double dailyDemand2 = monthlyDemand2 / monthlyPlan.getWorkingDay();

        Integer workingDay1 = calculateWorkingDaysInWeek(startDate);
        Calendar endDate = Calendar.getInstance();
        endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        endDate = eliminateMiliSecond(endDate);
        endDate.add(Calendar.DAY_OF_MONTH, 5);
        Double weeklyDemand = 0.0;
        if (endDate.get(Calendar.MONTH) != startDate.get(Calendar.MONTH)) {
            weeklyDemand = dailyDemand * workingDay1 + dailyDemand2 * (5 - workingDay1);
        } else {
            weeklyDemand = dailyDemand * workingDay1;
        }
        weeklyDemand = Math.ceil(weeklyDemand);

        Timestamp startTimestamp = new Timestamp(startDate.getTimeInMillis());

        String workingDayPeriodStr = returnWeekWorkingPeriodString(startDate, endDate);
        WeeklyPlan weeklyPlan = new WeeklyPlan(monthlyDemand, 5, weeklyDemand, startTimestamp, monthlyPlan, workingDayPeriodStr);
        weeklyPlan.setWorkingDaysInMonth(monthlyPlan.getWorkingDay());
        Calendar interDate = Calendar.getInstance();
        interDate = eliminateMiliSecond(interDate);
        if (endDate.get(Calendar.MONTH) != startDate.get(Calendar.MONTH)) {
            System.out.println("generateWeeklyProductionPlan: monthlyDemand=" + monthlyDemand + "    dailyDemand=" + dailyDemand);
            System.out.println("generateWeeklyProductionPlan: monthlyDemand2=" + monthlyDemand2 + "    dailyDemand2=" + dailyDemand2);
            weeklyPlan.setIsAtEndOfMonth(Boolean.TRUE);
            interDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            weeklyPlan.setMonthlyDemand1(monthlyDemand);
            weeklyPlan.setMonthlyDemand2(monthlyDemand2);
            weeklyPlan.setWeeklyDemand1(Math.ceil(dailyDemand * workingDay1));
            weeklyPlan.setWeeklyDemand2(Math.ceil(dailyDemand2 * (5 - workingDay1)));
            weeklyPlan.setWeeklyDemand(Math.ceil(dailyDemand * workingDay1) + Math.ceil(dailyDemand2 * (5 - workingDay1)));
            weeklyPlan.setWorkingDaysInMonth1(monthlyPlan.getWorkingDay());
            weeklyPlan.setWorkingDaysInMonth2(nextMonthlyPlan.getWorkingDay());
            weeklyPlan.setWorkingDaysInWeek1(workingDay1);
            weeklyPlan.setWorkingDaysInWeek2(5 - workingDay1);
            weeklyPlan.setWorkingDayPeriodStr1(returnWeekWorkingPeriodString(startDate, interDate));
            interDate.add(Calendar.DAY_OF_MONTH, 1);
            weeklyPlan.setWorkingDayPeriodStr2(returnWeekWorkingPeriodString(interDate, endDate));
        }
        em.persist(weeklyPlan);

        try {
            WeeklyPlan oldWeeklyPlan = retrieveWeeklyPlan(startDate, monthlyPlan);
            oldWeeklyPlan.setArchivedOrNot(Boolean.TRUE);
            em.merge(oldWeeklyPlan);
        } catch (WeeklyPlanNotExistException ex) {
            System.out.println("WeeklyPlanNotExist. Proceed to generate weekly plan.");
        }

        return weeklyPlan;
    }

    @Override
    public WeeklyPlan retrieveWeeklyPlan(Calendar startDate, MonthlyPlan monthlyPlan)
            throws WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException {
        Timestamp startTimestamp = new Timestamp(startDate.getTimeInMillis());
        Query query = em.createQuery("select w from WeeklyPlan w where w.monthlyPlan=?1 and w.weeklyPlanDate=?2 and w.archivedOrNot=FALSE");
        query.setParameter(1, monthlyPlan);
        query.setParameter(2, startTimestamp);
        ArrayList<WeeklyPlan> weeklyPlanList = new ArrayList(query.getResultList());
        if (weeklyPlanList.isEmpty()) {
            throw new WeeklyPlanNotExistException("Weekly production plan of for ");
        }
        if (weeklyPlanList.size() > 1) {
            throw new MultipleWeeklyPlanConcurrentExistException("Error! Mutiple weekly productin plan exist for the same product during the same week!");
        }

        WeeklyPlan weeklyPlan = (WeeklyPlan) weeklyPlanList.get(0);

        return weeklyPlan;
    }

    @Override
    public WeeklyPlan retrieveWeeklyPlan(Integer year, Integer month, Integer week, FinishedGood fg)
            throws WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException {
        Calendar startDate = this.getFirstDayOfOneWeek(year, month, week);
        Timestamp startTimestamp = new Timestamp(startDate.getTimeInMillis());
        Query query = em.createQuery("select w from WeeklyPlan w where w.monthlyPlan.salesForecast.finishedGood=?1 and w.weeklyPlanDate=?2 and w.archivedOrNot=FALSE");
        query.setParameter(1, fg);
        query.setParameter(2, startTimestamp);
        ArrayList<WeeklyPlan> weeklyPlanList = new ArrayList(query.getResultList());
        if (weeklyPlanList.isEmpty()) {
            throw new WeeklyPlanNotExistException("Weekly production plan of for ");
        }
        if (weeklyPlanList.size() > 1) {
            throw new MultipleWeeklyPlanConcurrentExistException("Error! Mutiple weekly productin plan exist for the same product during the same week!");
        }

        WeeklyPlan weeklyPlan = (WeeklyPlan) weeklyPlanList.get(0);

        return weeklyPlan;
    }

    @Override
    public ArrayList<WeeklyPlan> generateWeeklyProductionPlanOfTheMonth(Integer year, Integer month, String companyName, String productName, Double targetInventorylevel, Double prevInventoryLevel)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException, WeeklyPlanNotExistException, MultipleWeeklyPlanConcurrentExistException, SalesForecastNotExistException, MultipleSalesForecastForSameMonthException {
        Calendar startTime = getFirstDayOfOneWeek(year, month, 1);
        Calendar nextMonthStartTime = getFirstDayOfOneWeek(year, month + 1, 1);

        MonthlyPlan monthlyPlan = generateMonthlyProductionPlan(year, month, companyName, productName, targetInventorylevel, prevInventoryLevel);
        MonthlyPlan nextMonthlyPlan = generateMonthlyProductionPlan(year, month + 1, companyName, productName, targetInventorylevel, prevInventoryLevel);
        ArrayList<WeeklyPlan> weeklyPlanList = new ArrayList<>();

        System.out.println("generateWeeklyProductionPlanOfTheMonth: startTime.get(Calendar.MONTH): " + startTime.get(Calendar.MONTH));
        System.out.println("generateWeeklyProductionPlanOfTheMonth: month: " + month);

        while ((startTime.get(Calendar.MONTH) + 1) == month) {
            System.out.println("generateWeeklyProductionPlanOfTheMonth: current startTime.get(Calendar.MONTH): " + startTime.get(Calendar.MONTH));
            WeeklyPlan curWeeklyPlan = generateWeeklyProductionPlan(startTime, monthlyPlan, nextMonthlyPlan);
            startTime.add(Calendar.DAY_OF_MONTH, 7);
            weeklyPlanList.add(curWeeklyPlan);
        }
        return weeklyPlanList;
    }

    @Override
    public List<MonthlyPlan> retrieveMonthlyPlanList(String ownerCompanyName) throws MonthlyPlanNotExistException {
        Query query = em.createQuery("select m from MonthlyPlan m where m.salesForecast.finishedGood.ownerCompanyName=?1 and m.archivedOrNot=FALSE");
        query.setParameter(1, ownerCompanyName);
        List<MonthlyPlan> monthlyPlanList = new ArrayList(query.getResultList());
        if (monthlyPlanList.isEmpty()) {
            throw new MonthlyPlanNotExistException("Company " + ownerCompanyName + "'s has not generated Monthly Plan yet.");
        }
        return monthlyPlanList;
    }
}
