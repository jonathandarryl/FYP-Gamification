/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.MonthlyPlan;
import MRPII.entity.RawMaterial;
import MRPII.entity.SalesForecast;
import MRPII.entity.Supplier;
import MRPII.entity.WeeklyPlan;
import MRPII.session.ProductionPlanningSessionLocal;
import MRPII.session.RawMaterialManagementLocal;
import MRPII.session.SalesForecastSessionLocal;
import MRPII.session.SupplierManagementLocal;
import OES.entity.FinishedGood;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import util.exception.CompanyNotExistException;
import util.exception.MonthlyPlanNotExistException;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleMonthlyPlanConcurrentExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.MultipleWeeklyPlanConcurrentExistException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.SalesForecastNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.TwoListSizeNotEqualException;
import util.exception.WeeklyPlanNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "productionPlanningManagedBean")
@SessionScoped
public class ProductionPlanningManagedBean {

    @EJB
    private ProductionPlanningSessionLocal pps;
    @EJB
    private ProductInfoManagementLocal pim;
    @EJB
    private RawMaterialManagementLocal rmm;
    @EJB
    private SupplierManagementLocal sml;
    @EJB
    private SalesForecastSessionLocal sfs;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;

    private String yearString, monthString, ownerCompanyName, productName;
    private CustomerCompany select_Company;
    private FinishedGood selectProduct;
    private Supplier selectedSupplier;
    private RawMaterial selectedMaterial;
    private String targetInvString;
    private MonthlyPlan monthlyPlan;
    private String startYearString, endYearString;
    private String startMonthString, endMonthString;
    private ArrayList<MonthlyPlan> monthlyPlanList = new ArrayList();
    private Integer monthNumber;
    private ArrayList<Integer> numberList = new ArrayList();
    private ArrayList<String> monthStrList = new ArrayList<>();
    private String[] targetInvStr, quantities;
    private ArrayList<WeeklyPlan> weeklyPlanList = new ArrayList();
    private ArrayList<Double> targetInvList = new ArrayList();
    private ArrayList<FinishedGood> productList = new ArrayList();
    private ArrayList<RawMaterial> allMaterials = new ArrayList();
    private List<Supplier> supplierList = new ArrayList();
    private String statusMessage;
    private List<MonthlyPlan> allMonthlyPlan = new ArrayList();

    private LineChartModel salesForecastChart;
    private BarChartModel monthlyPlanListChart;

    /**
     * Creates a new instance of ProductionPlanningManagedBean
     */
    public ProductionPlanningManagedBean() {
    }

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            select_Company = (CustomerCompany) cmsbl.retrieveCompany(cId);
            ownerCompanyName = cmsbl.retrieveCompany(cId).getCompanyName();
        } catch (CompanyNotExistException ex) {
            statusMessage = ex.getMessage();
            this.displayFaceMessage(statusMessage);
            System.out.println("CompanyNotExistException: " + statusMessage);
        }
    }

    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void retrieveAllProducts() {
        try {
            productList = pim.retrieveProductList(ownerCompanyName);
        } catch (ProductNotExistException ex) {
            System.out.println("ProductNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveMaterialList() {
        try {
            allMaterials = rmm.retrieveRawMaterialList(ownerCompanyName);
        } catch (RawMaterialNotExistException ex) {
            System.out.println("RawMaterialNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveAllSupplier() {
        try {
            supplierList = sml.retrieveSupplierListForCompany(select_Company);
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        }
    }

    public void generateMonthlyProductionPlan() throws ProductNotExistException, MultipleProductWithSameNameException, IOException,
            MonthlyRecordNotExistException, MonthlyPlanNotExistException, MultipleMonthlyPlanConcurrentExistException,
            SalesForecastNotExistException, MultipleSalesForecastForSameMonthException {
        try {
            Integer year = Integer.parseInt(yearString);
            Integer month = Integer.parseInt(monthString);
            Double targetInv = Double.parseDouble(targetInvString);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            productName = selectProduct.getProductName();
            Double preInv = selectProduct.getInventoryLevel();

            monthlyPlan = pps.generateMonthlyProductionPlan(year, month, ownerCompanyName, productName, targetInv, preInv);
            ec.redirect("DisplayMonthlyProductionPlan.xhtml");
        } catch (ProductNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("ProductNotExistException: " + e.getMessage());
        } catch (MultipleProductWithSameNameException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + e.getMessage());
        } catch (MonthlyRecordNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + e.getMessage());
        } catch (MonthlyPlanNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MonthlyPlanNotExistException: " + e.getMessage());
        } catch (MultipleMonthlyPlanConcurrentExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleMonthlyPlanConcurrentExistException: " + e.getMessage());
        } catch (SalesForecastNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("SalesForecastNotExistException: " + e.getMessage());
        } catch (MultipleSalesForecastForSameMonthException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleSalesForecastForSameMonthException: " + e.getMessage());
        }
    }

    public void retrieveProduct(String pName) throws ProductNotExistException, MultipleProductWithSameNameException, IOException {
        try {
            selectProduct = pim.retrieveProduct(ownerCompanyName, pName);
        } catch (ProductNotExistException e) {
            this.displayFaceMessage("This product does not exist");
        } catch (MultipleProductWithSameNameException e) {
            this.displayFaceMessage("There are multiple products with the same name");
        }
    }

    public void preGenerateMonthlyProductionList() throws IOException {
        targetInvList.clear();
        productName = selectProduct.getProductName();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Integer startYear = Integer.parseInt(startYearString);
        Integer startMonth = Integer.parseInt(startMonthString);
        Integer endYear = Integer.parseInt(endYearString);
        Integer endMonth = Integer.parseInt(endMonthString);
        monthNumber = endMonth - startMonth + 1;
        numberList = new ArrayList<>();

        if (startYear < endYear) {
            monthNumber = monthNumber + 12 * (endYear - startYear);
        }
        for (int i = 0; i < monthNumber; i++) {
            numberList.add(i, i + 1);
        }

        this.setMonthTimeStrList(startYear, startMonth, endYear, endMonth);
        this.generateForecastChart(startYear, startMonth, endYear, endMonth);
        quantities = new String[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            targetInvList.add(0.00);
        }

        ec.redirect("GenerateMonthlyProductionList_2.xhtml");
    }

    private void setMonthTimeStrList(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {
        Integer monthNumber = endMonth - startMonth + 1;
        if (startYear < endYear) {
            monthNumber = monthNumber + 12 * (endYear - startYear);
        }
        monthStrList = new ArrayList<>();
        Calendar startTime = this.getDateFromInteger(startYear, startMonth);
        for (int i = 0; i < monthNumber + 1; i++) {
            String curTimeStr = this.retrieveMonthInEnglish(startTime.get(Calendar.MONTH) + 1) + " " + startTime.get(Calendar.YEAR);
            startTime.add(Calendar.MONTH, 1);
            monthStrList.add(i, curTimeStr);
        }
    }

    private Calendar getDateFromInteger(Integer year, Integer month) {
        Calendar result = Calendar.getInstance();
        result.set(year, month - 1, 1, 0, 0, 0);
        result = eliminateMiliSecond(result);
        Date date = new Date(result.getTimeInMillis());
        result.setTime(date);
        return result;
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

    public void generateForecastChart(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {
        salesForecastChart = new LineChartModel();
        LineChartSeries salesQuantitys = new LineChartSeries();
        Product curPro;
        try {
            curPro = pim.retrieveProduct(ownerCompanyName, productName);
            String unit = curPro.getUnit();
            salesQuantitys.setLabel(productName);
            List<SalesForecast> forecastList = sfs.generateSalesForecastList(startYear, startMonth, endYear, endMonth + 1, ownerCompanyName, productName);
            this.setMonthTimeStrList(startYear, startMonth, endYear, endMonth + 1);
            List<Double> quantityList = new ArrayList<>();
            for (int i = 0; i < monthNumber + 1; i++) {
                SalesForecast s = forecastList.get(i);
                Double curAmount = s.getAmount();
                String timeStr = monthStrList.get(i);
                salesQuantitys.set(timeStr, curAmount);
                quantityList.add(curAmount);
            }
            salesForecastChart.addSeries(salesQuantitys);

            salesForecastChart.setTitle("Sales Forecast for " + productName + " from " + retrieveMonthInEnglish(startMonth) + " " + startYear + " to " + retrieveMonthInEnglish(endMonth) + " " + endYear);
            salesForecastChart.setAnimate(true);
            salesForecastChart.setLegendPosition("e");
            salesForecastChart.setShowPointLabels(true);
            Axis yAxis = salesForecastChart.getAxis(AxisType.Y);
            yAxis.setLabel("Forecast Quantity (" + unit + ")");
            yAxis.setMin(0);
            yAxis.setMax(getMaxQuantity(quantityList) + 500);
            salesForecastChart.getAxes().put(AxisType.X, new CategoryAxis("Month"));
            this.setMonthTimeStrList(startYear, startMonth, endYear, endMonth);
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (MonthlyRecordNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + ex.getMessage());
        } catch (MultipleSalesForecastForSameMonthException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleSalesForecastForSameMonthException: " + ex.getMessage());
        }
    }

    private Double getMaxQuantity(List<Double> list) {
        Double result = 0.0;
        for (Double curQ : list) {
            if (curQ > result) {
                result = curQ;
            }
        }
        result = result - result % 100 + 200;
        return result;
    }

    public void addAction(int index) {
        Double q = Double.parseDouble(quantities[index]);
        targetInvList.set(index, q);
        this.displayFaceMessage("Target Inventory has been successfully added!");
    }

    public void generateMonthlyProductionList() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Integer startYear = Integer.parseInt(startYearString);
        Integer startMonth = Integer.parseInt(startMonthString);
        Integer endYear = Integer.parseInt(endYearString);
        Integer endMonth = Integer.parseInt(endMonthString);
        this.setMonthTimeStrList(startYear, startMonth, endYear, endMonth);
        try {
            retrieveProduct(productName);
            Double startInv = selectProduct.getInventoryLevel();
            monthlyPlanList = pps.generateMonthlyProductionPlanList(startYear, startMonth, endYear, endMonth, ownerCompanyName, productName, startInv, targetInvList);
            generateMonthlyPlanListChart();
            ec.redirect("DisplayMonthlyProductionList.xhtml");

        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (IOException ex) {
            this.displayFaceMessage("IOException: " + ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        } catch (MonthlyRecordNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + ex.getMessage());
        } catch (MonthlyPlanNotExistException ex) {
            System.out.println("MonthlyPlanNotExistException: " + ex.getMessage());
        } catch (MultipleMonthlyPlanConcurrentExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleMonthlyPlanConcurrentExistException: " + ex.getMessage());
        } catch (TwoListSizeNotEqualException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("TwoListSizeNotEqualException: " + ex.getMessage());
        } catch (SalesForecastNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SalesForecastNotExistException: " + ex.getMessage());
        } catch (MultipleSalesForecastForSameMonthException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleSalesForecastForSameMonthException: " + ex.getMessage());
        }
    }

    public void generateMonthlyPlanListChart() {
        System.out.println("Enter generateMonthlyPlanListChart");
        monthlyPlanListChart = new BarChartModel();

        Integer startYear = Integer.parseInt(startYearString);
        Integer startMonth = Integer.parseInt(startMonthString);
        Integer endYear = Integer.parseInt(endYearString);
        Integer endMonth = Integer.parseInt(endMonthString);

        LineChartSeries salesQuantitys = new LineChartSeries();
        salesQuantitys.setLabel("Forecast Sales");
        LineChartSeries productionQuantitys = new LineChartSeries();
        productionQuantitys.setLabel("Production Plan");
        BarChartSeries targetInventoryQuantitys = new BarChartSeries();
        targetInventoryQuantitys.setLabel("Target Inventory");
        Product curPro;

        Double startInv = selectProduct.getInventoryLevel();
//        
//        Calendar curMonth = this.getDateFromInteger(startYear, startMonth-1);
//        String beginningTimeStr = this.retrieveMonthInEnglish(curMonth.get(Calendar.MONTH)-1)+" "+curMonth.get(Calendar.YEAR);
//        targetInventoryQuantitys.set(beginningTimeStr, startInv);

        try {
            curPro = pim.retrieveProduct(ownerCompanyName, productName);
            String unit = curPro.getUnit();

            List<Double> quantityList = new ArrayList<>();
            System.out.println("generateMonthlyPlanListChart: Start Inputing data");
            for (int i = 0; i < monthNumber; i++) {
                MonthlyPlan m = monthlyPlanList.get(i);
                Double curSale = m.getSalesForecast().getAmount();
                Double curProduction = m.getProductionPlan();
                Double curInv = m.getTargetInventoryLevel();
                String timeStr = monthStrList.get(i);
                salesQuantitys.set(timeStr, curSale);
                productionQuantitys.set(timeStr, curProduction);
                targetInventoryQuantitys.set(timeStr, curInv);
                quantityList.add(curSale);
                quantityList.add(curProduction);
                quantityList.add(curInv);
            }
            System.out.println("generateMonthlyPlanListChart: End of Inputing data");
            monthlyPlanListChart.addSeries(targetInventoryQuantitys);
            monthlyPlanListChart.addSeries(salesQuantitys);
            monthlyPlanListChart.addSeries(productionQuantitys);

            monthlyPlanListChart.setTitle("Production Plan for " + productName + " from " + retrieveMonthInEnglish(startMonth) + " " + startYear + " to " + retrieveMonthInEnglish(endMonth) + " " + endYear);
            monthlyPlanListChart.setAnimate(true);
            monthlyPlanListChart.setLegendPosition("ne");
            monthlyPlanListChart.setShowPointLabels(true);
            Axis yAxis = monthlyPlanListChart.getAxis(AxisType.Y);
            yAxis.setLabel("Quantity (" + unit + ")");
            yAxis.setMin(0);
            yAxis.setMax(getMaxQuantity(quantityList));
            monthlyPlanListChart.getAxes().put(AxisType.X, new CategoryAxis("Month"));
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        }
        System.out.println("End of generateMonthlyPlanListChart");
    }

    public void generateWeeklyProductionPlan(Integer year, Integer month, Double targetInv) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        monthString = month + "";

        try {
            retrieveProduct(productName);
            Double preInv = selectProduct.getInventoryLevel();
            weeklyPlanList = pps.generateWeeklyProductionPlanOfTheMonth(year, month, ownerCompanyName, productName, targetInv, preInv);
            if (weeklyPlanList.size() == 5) {
                WeeklyPlan wp = weeklyPlanList.get(4);
                WeeklyPlan lastHalf = new WeeklyPlan();

                lastHalf.setPlannedWeek(wp.getPlannedWeek());
                lastHalf.setWorkingDayPeriodStr(wp.getWorkingDayPeriodStr2());
                lastHalf.setWorkingDaysInWeek(wp.getWorkingDaysInWeek2());
                lastHalf.setWorkingDaysInMonth(wp.getWorkingDaysInMonth2());
                lastHalf.setMonthlyDemand(wp.getMonthlyDemand2());
                lastHalf.setWeeklyDemand(wp.getWeeklyDemand2());
                
                wp.setWorkingDayPeriodStr(wp.getWorkingDayPeriodStr1());
                wp.setWorkingDaysInWeek(wp.getWorkingDaysInWeek1());
                wp.setWorkingDaysInMonth(wp.getWorkingDaysInMonth1());
                wp.setMonthlyDemand(wp.getMonthlyDemand1());
                wp.setWeeklyDemand(wp.getWeeklyDemand1());
                weeklyPlanList.add(lastHalf);
            } else if (weeklyPlanList.size() == 4 && weeklyPlanList.get(3).isIsAtEndOfMonth()) {
                WeeklyPlan wp = weeklyPlanList.get(3);
                WeeklyPlan lastHalf = new WeeklyPlan();

                lastHalf.setPlannedWeek(wp.getPlannedWeek());
                lastHalf.setWorkingDayPeriodStr(wp.getWorkingDayPeriodStr2());
                lastHalf.setWorkingDaysInWeek(wp.getWorkingDaysInWeek2());
                lastHalf.setWorkingDaysInMonth(wp.getWorkingDaysInMonth2());
                lastHalf.setMonthlyDemand(wp.getMonthlyDemand2());
                lastHalf.setWeeklyDemand(wp.getWeeklyDemand2());

                wp.setWorkingDayPeriodStr(wp.getWorkingDayPeriodStr1());
                wp.setWorkingDaysInWeek(wp.getWorkingDaysInWeek1());
                wp.setWorkingDaysInMonth(wp.getWorkingDaysInMonth1());
                wp.setMonthlyDemand(wp.getMonthlyDemand1());
                wp.setWeeklyDemand(wp.getWeeklyDemand1());
                weeklyPlanList.add(lastHalf);
            }
            ec.redirect("DisplayWeeklyPlan.xhtml");
        } catch (MonthlyRecordNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + ex.getMessage());
        } catch (MonthlyPlanNotExistException ex) {
            System.out.println("MonthlyPlanNotExistException: " + ex.getMessage());
        } catch (MultipleMonthlyPlanConcurrentExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleMonthlyPlanConcurrentExistException: " + ex.getMessage());
        } catch (WeeklyPlanNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("WeeklyPlanNotExistException: " + ex.getMessage());
        } catch (MultipleWeeklyPlanConcurrentExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleWeeklyPlanConcurrentExistException: " + ex.getMessage());
        } catch (SalesForecastNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SalesForecastNotExistExceptionn: " + ex.getMessage());
        } catch (MultipleSalesForecastForSameMonthException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleSalesForecastForSameMonthException: " + ex.getMessage());
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public String retrieveMonthInEnglish() {
        Integer month = Integer.parseInt(monthString);
        String result = "";
        switch (month) {
            case 1:
                result = "Jan";
                break;
            case 2:
                result = "Feb";
                break;
            case 3:
                result = "Mar";
                break;
            case 4:
                result = "Apr";
                break;
            case 5:
                result = "May";
                break;
            case 6:
                result = "Jun";
                break;
            case 7:
                result = "Jul";
                break;
            case 8:
                result = "Aug";
                break;
            case 9:
                result = "Sep";
                break;
            case 10:
                result = "Oct";
                break;
            case 11:
                result = "Nov";
                break;
            case 12:
                result = "Dec";
                break;
            default:
                break;
        }
        return result;
    }

    private String retrieveMonthInEnglish(Integer month) {
        String result = "";
        switch (month) {
            case 1:
                result = "Jan";
                break;
            case 2:
                result = "Feb";
                break;
            case 3:
                result = "Mar";
                break;
            case 4:
                result = "Apr";
                break;
            case 5:
                result = "May";
                break;
            case 6:
                result = "Jun";
                break;
            case 7:
                result = "Jul";
                break;
            case 8:
                result = "Aug";
                break;
            case 9:
                result = "Sep";
                break;
            case 10:
                result = "Oct";
                break;
            case 11:
                result = "Nov";
                break;
            case 12:
                result = "Dec";
                break;
            default:
                break;
        }
        return result;
    }

    public void goBack() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect("DisplayMonthlyProductionList.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ProductionPlanningManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrieveMonthlyPlanList() {
        try {
            allMonthlyPlan = pps.retrieveMonthlyPlanList(ownerCompanyName);
        } catch (MonthlyPlanNotExistException ex) {
           this.displayFaceMessage(ex.getMessage());
        }
    }

    public String getYearString() {
        return yearString;
    }

    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public FinishedGood getSelectProduct() {
        return selectProduct;
    }

    public void setSelectProduct(FinishedGood selectProduct) {
        this.selectProduct = selectProduct;
    }

    public String getTargetInvString() {
        return targetInvString;
    }

    public void setTargetInvString(String targetInvString) {
        this.targetInvString = targetInvString;
    }

    public MonthlyPlan getMonthlyPlan() {
        return monthlyPlan;
    }

    public void setMonthlyPlan(MonthlyPlan monthlyPlan) {
        this.monthlyPlan = monthlyPlan;
    }

    public String getStartYearString() {
        return startYearString;
    }

    public void setStartYearString(String startYearString) {
        this.startYearString = startYearString;
    }

    public String getEndYearString() {
        return endYearString;
    }

    public void setEndYearString(String endYearString) {
        this.endYearString = endYearString;
    }

    public List<MonthlyPlan> getAllMonthlyPlan() {
        return allMonthlyPlan;
    }

    public void setAllMonthlyPlan(List<MonthlyPlan> allMonthlyPlan) {
        this.allMonthlyPlan = allMonthlyPlan;
    }

    public String getStartMonthString() {
        return startMonthString;
    }

    public void setStartMonthString(String startMonthString) {
        this.startMonthString = startMonthString;
    }

    public String getEndMonthString() {
        return endMonthString;
    }

    public void setEndMonthString(String endMonthString) {
        this.endMonthString = endMonthString;
    }

    public ArrayList<MonthlyPlan> getMonthlyPlanList() {
        return monthlyPlanList;
    }

    public void setMonthlyPlanList(ArrayList<MonthlyPlan> monthlyPlanList) {
        this.monthlyPlanList = monthlyPlanList;
    }

    public Integer getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(Integer monthNumber) {
        this.monthNumber = monthNumber;
    }

    public ArrayList<Integer> getNumberList() {
        return numberList;
    }

    public void setNumberList(ArrayList<Integer> numberList) {
        this.numberList = numberList;
    }

    public String[] getTargetInvStr() {
        return targetInvStr;
    }

    public void setTargetInvStr(String[] targetInvStr) {
        this.targetInvStr = targetInvStr;
    }

    public ArrayList<WeeklyPlan> getWeeklyPlanList() {
        return weeklyPlanList;
    }

    public void setWeeklyPlanList(ArrayList<WeeklyPlan> weeklyPlanList) {
        this.weeklyPlanList = weeklyPlanList;
    }

    public ArrayList<Double> getTargetInvList() {
        return targetInvList;
    }

    public void setTargetInvList(ArrayList<Double> targetInvList) {
        this.targetInvList = targetInvList;
    }

    public String[] getQuantities() {
        return quantities;
    }

    public void setQuantities(String[] quantities) {
        this.quantities = quantities;
    }

    public ArrayList<FinishedGood> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<FinishedGood> productList) {
        this.productList = productList;
    }

    public ArrayList<RawMaterial> getAllMaterials() {
        return allMaterials;
    }

    public void setAllMaterials(ArrayList<RawMaterial> allMaterials) {
        this.allMaterials = allMaterials;
    }

    public RawMaterial getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(RawMaterial selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public ArrayList<String> getMonthStrList() {
        return monthStrList;
    }

    public void setMonthStrList(ArrayList<String> monthStrList) {
        this.monthStrList = monthStrList;
    }

    public LineChartModel getSalesForecastChart() {
        return salesForecastChart;
    }

    public void setSalesForecastChart(LineChartModel salesForecastChart) {
        this.salesForecastChart = salesForecastChart;
    }

    public BarChartModel getMonthlyPlanListChart() {
        return monthlyPlanListChart;
    }

    public void setMonthlyPlanListChart(BarChartModel monthlyPlanListChart) {
        this.monthlyPlanListChart = monthlyPlanListChart;
    }

}
