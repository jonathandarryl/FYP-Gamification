/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.SalesForecast;
import MRPII.session.SalesForecastSessionLocal;
import OES.entity.Product;
import OES.session.ProductInfoManagementLocal;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import util.exception.CompanyNotExistException;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.ProductNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "salesForecastManagedBean")
@SessionScoped
public class SalesForecastManagedBean {

    @EJB
    SalesForecastSessionLocal sfs;
    @EJB
    ProductInfoManagementLocal pim;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;

    private String productName, ownerCompanyName;
    private HashMap<Date, Double> pastRecordMap = new HashMap<>();
    private Company select_Company;
    private Set<Date> calendarSet = new HashSet();
    private Date targetDate;
    private SalesForecast salesF;
    private String yearString, monthString;
    private LineChartModel salesForecastChart;
    private String statusMessage;

    /**
     * Creates a new instance of SalesForecastManagedBean
     */
    public SalesForecastManagedBean() {
    }

    @PostConstruct
    public void init() {
        Long userCompanyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        initUserInfo(userCompanyId);
        statusMessage = "inti:";
    }

    private void initUserInfo(Long cId) {
        try {
            select_Company = cmsbl.retrieveCompany(cId);
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

    public void retrievePastsSales(Product product) throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, IOException {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            productName = product.getProductName();
            select_Company = (Company) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("company");
            ownerCompanyName = select_Company.getCompanyName();

            pastRecordMap = sfs.retrieveProductSalesRecord(ownerCompanyName, productName);
            if (pastRecordMap.isEmpty()) {
                System.out.println("pastRecordMap is empty");
            } else {
                System.out.println("pastRecordMap is not empty");
            }
            calendarSet = pastRecordMap.keySet();
            ec.redirect("DisplayPastSalesRecord.xhtml");
        } catch (ProductNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("ProductNotExistException: " + e.getMessage());
        } catch (MultipleProductWithSameNameException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + e.getMessage());
        } catch (MonthlyRecordNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + e.getMessage());
        }
    }

    public Double retrieveSinglePastSale(Date cal) {
        Double sales = pastRecordMap.get(cal);
        return sales;
    }
    
    //Charts for the past three years and current year
    public List<BarChartModel> generateSalesRecordChartList(){
        Calendar curTime = Calendar.getInstance();
        Integer curYear = curTime.get(Calendar.YEAR);
        List<BarChartModel> result = new ArrayList<>(0);
        for(int i=3; i>=0; i--){
            result.add(this.generateSalesRecordChart(curYear-i));
        }
        return result;
    }
    
    private BarChartModel generateSalesRecordChart(Integer year) {
        BarChartModel salesRecordChart = new BarChartModel();
        ChartSeries salesQuantitys = new ChartSeries();
        Product curPro;
        try {
            curPro = pim.retrieveProduct(ownerCompanyName, productName);
            String unit = curPro.getUnit();
            salesQuantitys.setLabel(productName);
            
            List<Double> quantityList = new ArrayList<>();
            for (int month = 1; month<=12; month++) {
                Date curDate = this.getDateFromInteger(year, month);
                Double quantity = 0.0;
                if(pastRecordMap.get(curDate)!=null)
                    quantity = pastRecordMap.get(curDate);
                salesQuantitys.set(getMonthInEnglish(month), quantity);
                quantityList.add(quantity);
            }
            salesRecordChart.addSeries(salesQuantitys);            
            
            salesRecordChart.setTitle("Sales Record for " + productName + " in Year " + year);
            salesRecordChart.setAnimate(true);
            salesRecordChart.setLegendPosition("ne");
            salesRecordChart.setShowPointLabels(true);
            Axis yAxis = salesRecordChart.getAxis(AxisType.Y);
            yAxis.setLabel("Sales Quantity (" + unit + ")");
            yAxis.setMin(0);
            yAxis.setMax(getMaxQuantity(quantityList)+200);
            salesRecordChart.getAxes().put(AxisType.X, new CategoryAxis("Month"));

        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } 
        return salesRecordChart;
    }

    public void preGenerateSalesForecast(Product product) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        productName = product.getProductName();
        RequestContext.getCurrentInstance().execute("PF('dlg').show()");
    }

    public void generateSalesForecast() throws ProductNotExistException, MultipleProductWithSameNameException,
            MonthlyRecordNotExistException, MultipleSalesForecastForSameMonthException, IOException {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            Integer year = Integer.parseInt(yearString);
            Integer month = Integer.parseInt(monthString);

            salesF = sfs.generateSalesForecast(year, month, ownerCompanyName, productName);
            this.generateSalesForecastChart(year, month);
            ec.redirect("DisplaySalesForecast.xhtml");
        } catch (ProductNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("ProductNotExistException: " + e.getMessage());
        } catch (MultipleProductWithSameNameException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + e.getMessage());
        } catch (MonthlyRecordNotExistException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + e.getMessage());
        } catch (MultipleSalesForecastForSameMonthException e) {
            this.displayFaceMessage(e.getMessage());
            System.out.println("MultipleSalesForecastForSameMonthException: " + e.getMessage());
        }
    }

    public void generateSalesForecastChart(Integer year, Integer month) {
        System.out.println("Enter generateSalesForecastChart");
        salesForecastChart = new LineChartModel();
        LineChartSeries salesQuantitys = new LineChartSeries();
        Product curPro;
        try {
            curPro = pim.retrieveProduct(ownerCompanyName, productName);
            String unit = curPro.getUnit();
            salesQuantitys.setLabel(productName);

            pastRecordMap = sfs.retrieveProductSalesRecord(ownerCompanyName, productName);
            List<Double> quantityList = new ArrayList<>();
            for (int i = 3; i > 0; i--) {
                Date curDate = this.getDateFromInteger(year - i, month);
                Double quantity = 0.0;
                if(pastRecordMap.get(curDate)!=null)
                    quantity = pastRecordMap.get(curDate);
                salesQuantitys.set(year - i, quantity);
                quantityList.add(quantity);
            }
            salesQuantitys.set(year, salesF.getAmount());
            salesForecastChart.addSeries(salesQuantitys);
            
            quantityList.add(salesF.getAmount());
            
            salesForecastChart.setTitle("Likely sales trend for " + productName + " in " + getMonthInEnglish(month) + " " + year);
            salesForecastChart.setAnimate(true);
            salesForecastChart.setLegendPosition("e");
            salesForecastChart.setShowPointLabels(true);
            Axis yAxis = salesForecastChart.getAxis(AxisType.Y);
            yAxis.setLabel("Sales Quantity (" + unit + ")");
            yAxis.setMin(0);
            yAxis.setMax(getMaxQuantity(quantityList)+500);
            salesForecastChart.getAxes().put(AxisType.X, new CategoryAxis("Years"));

        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (MonthlyRecordNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MonthlyRecordNotExistException: " + ex.getMessage());
        }
        System.out.println("End of generateSalesForecastChart");
    }
    
    private Double getMaxQuantity(List<Double> list){
        Double result = 0.0;
        for(Double curQ: list){
            if(curQ>result)
                result = curQ;
        }
        return result;
    }

    private Date getDateFromInteger(Integer year, Integer month) {
        Calendar result = Calendar.getInstance();
        result.set(year, month - 1, 1, 0, 0, 0);
        result = eliminateMiliSecond(result);
        Date date = new Date(result.getTimeInMillis());
        return date;
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

    public String getMonthInEnglish(Integer month) {
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public HashMap<Date, Double> getPastRecordMap() {
        return pastRecordMap;
    }

    public void setPastRecordMap(HashMap<Date, Double> pastRecordMap) {
        this.pastRecordMap = pastRecordMap;
    }

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(Company select_Company) {
        this.select_Company = select_Company;
    }

    public Set<Date> getCalendarSet() {
        return calendarSet;
    }

    public void setCalendarSet(Set<Date> calendarSet) {
        this.calendarSet = calendarSet;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public SalesForecast getSalesF() {
        return salesF;
    }

    public void setSalesF(SalesForecast salesF) {
        this.salesF = salesF;
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

    public LineChartModel getSalesForecastChart() {
        return salesForecastChart;
    }

    public void setSalesForecastChart(LineChartModel salesForecastChart) {
        this.salesForecastChart = salesForecastChart;
    }

}
