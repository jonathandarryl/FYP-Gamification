/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPIIManagedBean;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.session.CompanyManagementSessionBeanLocal;
import MRPII.entity.BillOfMaterial;
import MRPII.entity.RawMaterial;
import MRPII.entity.ResourcePlan;
import MRPII.entity.SalesForecast;
import MRPII.entity.Supplier;
import MRPII.entity.WeeklyPlan;
import MRPII.session.MaterialRequirementPlanningSessionLocal;
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
import java.util.HashMap;
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
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import util.exception.BillOfMaterialNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.MonthlyPlanNotExistException;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleMonthlyPlanConcurrentExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleRawMaterialWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.MultipleWeeklyPlanConcurrentExistException;
import util.exception.ProductNotExistException;
import util.exception.RawMaterialNotExistException;
import util.exception.ResourcePlanNotExistException;
import util.exception.SupplierNotExistException;
import util.exception.WeeklyPlanNotExistException;

/**
 *
 * @author Zhong Wei
 */
@ManagedBean(name = "materialReqPlanningManagedBean")
@SessionScoped
public class MaterialReqPlanningManagedBean {

    @EJB
    MaterialRequirementPlanningSessionLocal mrp;
    @EJB
    ProductionPlanningSessionLocal pps;
    @EJB
    ProductInfoManagementLocal pim;
    @EJB
    RawMaterialManagementLocal rmm;
    @EJB
    SupplierManagementLocal sml;
    @EJB
    private CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    private SalesForecastSessionLocal sfs;

    private CustomerCompany select_Company;
    private ResourcePlan resourcePlan;
    private WeeklyPlan weeklyPlan;
    private String ownerCompanyName, materialName, supplierName, productName;
    private String yearStr, monthStr;
    private Integer year, month, batchSize;
    private Double targetInv, preInv;
    private ArrayList<WeeklyPlan> weeklyPlanList = new ArrayList();
    private FinishedGood product;
    private ArrayList<Integer> batchList = new ArrayList();
    private ArrayList<Integer> weekList = new ArrayList();
    private String[] batchAr;
    private ArrayList<ResourcePlan> resourcePlanList = new ArrayList();
    private FinishedGood fg;
    private Supplier supplier;
    private RawMaterial rawmaterial;
    private ArrayList<FinishedGood> productList = new ArrayList();
    private List<RawMaterial> allMaterials = new ArrayList();
    private List<Supplier> supplierList = new ArrayList();
    private FinishedGood selectProduct;
    private Supplier selectedSupplier;
    private RawMaterial selectedMaterial;
    private String statusMessage;

    private ArrayList<String> monthStrList = new ArrayList<>();

    private LineChartModel materialForecastChart;

    /**
     * Creates a new instance of MaterialReqPlanningManagedBean
     */
    public MaterialReqPlanningManagedBean() {
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
            this.displayFaceMessage("ProductNotExistException: " + ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        }
    }

    public void retrieveMaterialList() {
        generateMaterialForecastChart();
        allMaterials = selectProduct.getBillOfMaterial().getRawMaterialList();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("GenerateResourcePlan_2.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(MaterialReqPlanningManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrieveAllSupplier() {
        supplierList = selectedMaterial.getSupplierList();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect("GenerateResourcePlan_3.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(MaterialReqPlanningManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateResourcePlan() {
        month = Integer.parseInt(monthStr);
        year = Integer.parseInt(yearStr);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            System.out.println("raw material name is " + selectedMaterial.getProductName());
            resourcePlanList = mrp.createResourcePlanForOneMonth(selectedMaterial.getInventoryLevel(), year, month, selectProduct, selectedMaterial, selectedSupplier);
            ec.redirect("DisplayResourcePlan.xhtml");
        } catch (ProductNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ProductNotExistException: " + ex.getMessage());
        } catch (MultipleProductWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleProductWithSameNameException: " + ex.getMessage());
        } catch (BillOfMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("BillOfMaterialNotExistException: " + ex.getMessage());
        } catch (RawMaterialNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("RawMaterialNotExistException: " + ex.getMessage());
        } catch (MultipleRawMaterialWithSameNameException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleRawMaterialWithSameNameException: " + ex.getMessage());
        } catch (ResourcePlanNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("ResourcePlanNotExistException: " + ex.getMessage());
        } catch (SupplierNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("SupplierNotExistException: " + ex.getMessage());
        } catch (WeeklyPlanNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("WeeklyPlanNotExistException: " + ex.getMessage());
        } catch (MultipleWeeklyPlanConcurrentExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleWeeklyPlanConcurrentExistException: " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(MaterialReqPlanningManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MonthlyPlanNotExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MonthlyPlanNotExistException: " + ex.getMessage());
        } catch (MultipleMonthlyPlanConcurrentExistException ex) {
            this.displayFaceMessage(ex.getMessage());
            System.out.println("MultipleMonthlyPlanConcurrentExistException: " + ex.getMessage());
        }
    }

    public void generateMaterialForecastChart() {
        System.out.println("Enter generateMaterialForecastChart");
        month = Integer.parseInt(monthStr);
        year = Integer.parseInt(yearStr);
        Integer startYear = year;
        Integer startMonth = month;
        Calendar endTime = this.getDateFromInteger(year, month + 2);
        Integer endYear = endTime.get(Calendar.YEAR);
        Integer endMonth = endTime.get(Calendar.MONTH) + 1;

        Integer monthNumber = endMonth - startMonth + 1;
        if (startYear < endYear) {
            monthNumber = monthNumber + 12 * (endYear - startYear);
        }

        String productUnit = selectProduct.getUnit();
        materialForecastChart = new LineChartModel();
        LineChartSeries salesQuantitys = new LineChartSeries();
        salesQuantitys.setLabel(selectProduct.getProductName()+"("+productUnit+")");

        BillOfMaterial bom = selectProduct.getBillOfMaterial();
        List<RawMaterial> rmList = bom.getRawMaterialList();
        HashMap<RawMaterial, Double> quantityMap = bom.getMaterialQuantityMap();

        try {
            List<SalesForecast> forecastList = sfs.generateSalesForecastList(startYear, startMonth, endYear, endMonth + 1, ownerCompanyName, selectProduct.getProductName());
            List<Double> quantityList = new ArrayList<>();
            this.setMonthTimeStrList(startYear, startMonth, endYear, endMonth + 1);

            for (int i = 0; i < monthNumber + 1; i++) {
                SalesForecast s = forecastList.get(i);
                Double curAmount = s.getAmount();
                String timeStr = monthStrList.get(i);
                salesQuantitys.set(timeStr, curAmount);
                quantityList.add(curAmount);
            }
            System.out.println("generateMaterialForecastChart: end of product Line for "+selectProduct.getProductName());
            for (RawMaterial curRm : rmList) {
                LineChartSeries curRmQuantity = new LineChartSeries();
                curRmQuantity.setLabel(curRm.getProductName()+"("+curRm.getUnit()+")");
                Double neededUnitQuantity = quantityMap.get(curRm);

                for (int i = 0; i < monthNumber + 1; i++) {
                    SalesForecast s = forecastList.get(i);
                    Double curAmount = s.getAmount();
                    curAmount = curAmount * neededUnitQuantity;
                    String timeStr = monthStrList.get(i);
                    curRmQuantity.set(timeStr, curAmount);
                    quantityList.add(curAmount);
                }
                materialForecastChart.addSeries(curRmQuantity);
                System.out.println("generateMaterialForecastChart: end of material Line for "+curRm.getProductName());
            }
            materialForecastChart.addSeries(salesQuantitys);

            materialForecastChart.setTitle("Materials Need Forecast for " + selectProduct.getProductName() + " from " + retrieveMonthInEnglish(startMonth) + " " + startYear + " to " + retrieveMonthInEnglish(endMonth) + " " + endYear);
            materialForecastChart.setAnimate(true);
            materialForecastChart.setLegendPosition("e");
            materialForecastChart.setShowPointLabels(true);
            Axis yAxis = materialForecastChart.getAxis(AxisType.Y);
            yAxis.setLabel("Forecast Quantity ");
            yAxis.setMin(0);
            yAxis.setMax(getMaxQuantity(quantityList) + 500);
            materialForecastChart.getAxes().put(AxisType.X, new CategoryAxis("Month"));
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
        System.out.println("Enter generateMaterialForecastChart");
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

    public Company getSelect_Company() {
        return select_Company;
    }

    public void setSelect_Company(CustomerCompany select_Company) {
        this.select_Company = select_Company;
    }

    public ResourcePlan getResourcePlan() {
        return resourcePlan;
    }

    public void setResourcePlan(ResourcePlan resourcePlan) {
        this.resourcePlan = resourcePlan;
    }

    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public String getOwnerCompanyName() {
        return ownerCompanyName;
    }

    public void setOwnerCompanyName(String ownerCompanyName) {
        this.ownerCompanyName = ownerCompanyName;
    }

    public String getYearStr() {
        return yearStr;
    }

    public void setYearStr(String yearStr) {
        this.yearStr = yearStr;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getTargetInv() {
        return targetInv;
    }

    public void setTargetInv(Double targetInv) {
        this.targetInv = targetInv;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public ArrayList<WeeklyPlan> getWeeklyPlanList() {
        return weeklyPlanList;
    }

    public void setWeeklyPlanList(ArrayList<WeeklyPlan> weeklyPlanList) {
        this.weeklyPlanList = weeklyPlanList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public FinishedGood getProduct() {
        return product;
    }

    public void setProduct(FinishedGood product) {
        this.product = product;
    }

    public Double getPreInv() {
        return preInv;
    }

    public void setPreInv(Double preInv) {
        this.preInv = preInv;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public ArrayList<Integer> getBatchList() {
        return batchList;
    }

    public void setBatchList(ArrayList<Integer> batchList) {
        this.batchList = batchList;
    }

    public ArrayList<Integer> getWeekList() {
        return weekList;
    }

    public void setWeekList(ArrayList<Integer> weekList) {
        this.weekList = weekList;
    }

    public String[] getBatchAr() {
        return batchAr;
    }

    public void setBatchAr(String[] batchAr) {
        this.batchAr = batchAr;
    }

    public ArrayList<ResourcePlan> getResourcePlanList() {
        return resourcePlanList;
    }

    public void setResourcePlanList(ArrayList<ResourcePlan> resourcePlanList) {
        this.resourcePlanList = resourcePlanList;
    }

    public FinishedGood getFg() {
        return fg;
    }

    public void setFg(FinishedGood fg) {
        this.fg = fg;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RawMaterial getRawmaterial() {
        return rawmaterial;
    }

    public void setRawmaterial(RawMaterial rawmaterial) {
        this.rawmaterial = rawmaterial;
    }

    public ArrayList<FinishedGood> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<FinishedGood> productList) {
        this.productList = productList;
    }

    public List<RawMaterial> getAllMaterials() {
        return allMaterials;
    }

    public void setAllMaterials(ArrayList<RawMaterial> allMaterials) {
        this.allMaterials = allMaterials;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    public FinishedGood getSelectProduct() {
        return selectProduct;
    }

    public void setSelectProduct(FinishedGood selectProduct) {
        this.selectProduct = selectProduct;
    }

    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public RawMaterial getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(RawMaterial selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public LineChartModel getMaterialForecastChart() {
        return materialForecastChart;
    }

    public void setMaterialForecastChart(LineChartModel materialForecastChart) {
        this.materialForecastChart = materialForecastChart;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ArrayList<String> getMonthStrList() {
        return monthStrList;
    }

    public void setMonthStrList(ArrayList<String> monthStrList) {
        this.monthStrList = monthStrList;
    }

}
