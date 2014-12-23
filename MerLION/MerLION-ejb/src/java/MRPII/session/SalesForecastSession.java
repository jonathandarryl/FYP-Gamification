/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MRPII.session;

import MRPII.entity.SalesForecast;
import OES.entity.FinishedGood;
import OES.session.ProductInfoManagementLocal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.ProductNotExistException;
import util.exception.SalesForecastNotExistException;

/**
 *
 * @author songhan
 */
@Stateless
public class SalesForecastSession implements SalesForecastSessionLocal {

    @PersistenceContext
    EntityManager em;
    @EJB
    private ProductInfoManagementLocal pim;

    @Override
    public HashMap<Date, Double> retrieveProductSalesRecord(String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException {
        FinishedGood targetProduct = pim.retrieveProduct(companyName, productName);
        HashMap<Date, Double> monthlySalesRecord = targetProduct.getMonthlySalesRecord();
        if (monthlySalesRecord.isEmpty()) {
            System.out.println("monthlySalesRecord is empty");
        } else {
            System.out.println("monthlySalesRecord is not empty");
        }
        if (monthlySalesRecord == null) {
            throw new MonthlyRecordNotExistException("No monthly sales record found for Product" + productName);
        }
        return monthlySalesRecord;
    }

    @Override
    public SalesForecast generateSalesForecast(Integer year, Integer month, String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, MultipleSalesForecastForSameMonthException {
        Calendar targetMonth = Calendar.getInstance();
        targetMonth.set(year, month - 1, 1, 0, 0, 0);
        targetMonth = eliminateMiliSecond(targetMonth);
        Calendar sampleTime = Calendar.getInstance();
        sampleTime = eliminateMiliSecond(sampleTime);

        Integer yearCount = 3;
        HashMap<Date, Double> monthlySalesRecord = retrieveProductSalesRecord(companyName, productName);
        if (monthlySalesRecord == null) {
            System.out.println("Product " + productName + " of Company " + companyName + " does not has any historial sales record!");
        } else {
            System.out.println("Historical Sales Record for Product " + productName + " of Company " + companyName);
            for (Map.Entry entry : monthlySalesRecord.entrySet()) {
                Date curDate = (Date) entry.getKey();
                Double quantity = (Double) entry.getValue();
                System.out.println("Date: " +curDate+"   Quantity: "+quantity);
            }
        }
        FinishedGood fg = pim.retrieveProduct(companyName, productName);

        Double totalQuantity = 0.0;
        for (int i = 1; i <= yearCount; i++) {
            sampleTime.set(year - i, month - 1, 1, 0, 0, 0);
            Date curDate = new Date(sampleTime.getTimeInMillis());
            System.out.println("Now retrieving sales record in " + curDate);
            Double curQuantity = monthlySalesRecord.get(curDate);
            if (curQuantity != null) {
                totalQuantity = totalQuantity + curQuantity;
            } else {
                System.out.println("NOT FOUND " + curDate);
            }
        }
        Date targetDate = new Date(targetMonth.getTime().getTime());
        System.out.println("targetMonth: " + targetDate);
        Timestamp targetTimestamp = new Timestamp(targetDate.getTime());
        Double forecastQuantity = totalQuantity / 3;
        forecastQuantity = Math.ceil(forecastQuantity);
        try {
            SalesForecast prevSf = retrieveSalesForecast(productName, companyName, year, month);
            System.out.println("Archiving existing sales forecast for " + year + "-" + month);
            prevSf.setArchivedOrNot(Boolean.TRUE);
            em.merge(prevSf);
        } catch (SalesForecastNotExistException ex) {
            System.out.println("No existing sales forecast for " + year + "-" + month + " found. Forecast creation proceed.");
        }
        SalesForecast sf = new SalesForecast(forecastQuantity, targetTimestamp, fg);
        em.persist(sf);
        return sf;
    }
    
    @Override
    public ArrayList<SalesForecast> generateSalesForecastList(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth,
            String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, ProductNotExistException, MultipleSalesForecastForSameMonthException{
        Integer monthNumber = endMonth - startMonth + 1;
        if (startYear < endYear) {
            monthNumber = monthNumber + 12 * (endYear - startYear);
        }

        ArrayList<SalesForecast> resultList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.set(startYear, startMonth - 1, 1, 0, 0, 0);
        startTime = eliminateMiliSecond(startTime);
        Calendar markTime = startTime;
        for(int i=0; i<monthNumber; i++) {
            Integer year = markTime.get(Calendar.YEAR);
            Integer month = markTime.get(Calendar.MONTH) + 1;
            SalesForecast curForecast = generateSalesForecast(year, month, companyName, productName);
            markTime.add(Calendar.MONTH, 1);
            resultList.add(curForecast);
        }
        return resultList;
    }

    @Override
    public SalesForecast retrieveSalesForecast(Long sfId)
            throws SalesForecastNotExistException {
        SalesForecast sf = em.find(SalesForecast.class, sfId);
        if (sf == null) {
            throw new SalesForecastNotExistException("Sales forecast " + sfId + " does not exist!");
        }
        return sf;
    }

    @Override
    public SalesForecast retrieveSalesForecast(String productName, String companyName, Integer year, Integer month)
            throws SalesForecastNotExistException, ProductNotExistException, MultipleProductWithSameNameException, MultipleSalesForecastForSameMonthException {
        FinishedGood product = pim.retrieveProduct(companyName, productName);
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(year, month - 1, 1, 0, 0, 0);
        targetTime = this.eliminateMiliSecond(targetTime);
        Timestamp time = new Timestamp(targetTime.getTimeInMillis());
        Query query = em.createQuery("select s from SalesForecast s where s.salesForecastDate=?1 and s.finishedGood=?2 and s.archivedOrNot=FALSE");
        query.setParameter(1, time);
        query.setParameter(2, product);
        ArrayList<SalesForecast> salesForecastList = new ArrayList(query.getResultList());
        if (salesForecastList.isEmpty()) {
            throw new SalesForecastNotExistException("Sales Forecast for " + month + "-" + year + " for " + productName + " of Company " + companyName + " does not exist!");
        }
        if (salesForecastList.size() > 1) {
            throw new MultipleSalesForecastForSameMonthException("Multiple Sales Forecast exist for the " + month + "-" + year + "!");
        }
        return salesForecastList.get(0);
    }

    @Override
    public ArrayList<SalesForecast> retrieveSalesForecast(String productName, String companyName, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth)
            throws SalesForecastNotExistException, ProductNotExistException, MultipleProductWithSameNameException {
        FinishedGood product = pim.retrieveProduct(companyName, productName);
        Calendar startTime = Calendar.getInstance();
        startTime.set(startYear, startMonth - 2, 1, 0, 0, 0);
        startTime = eliminateMiliSecond(startTime);
        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth - 1, 2, 0, 0, 0);
        endTime = eliminateMiliSecond(endTime);
        Timestamp start = new Timestamp(startTime.getTimeInMillis());
        Timestamp end = new Timestamp(endTime.getTimeInMillis());
        Query query = em.createQuery("select s from SalesForecast s where s.salesForecastDate between ?1 and ?2 and s.finishedGood=?3 and s.archivedOrNot=FALSE");
        query.setParameter(1, start);
        query.setParameter(2, end);
        query.setParameter(3, product);
        ArrayList<SalesForecast> salesForecastList = new ArrayList(query.getResultList());
        if (salesForecastList.isEmpty()) {
            throw new SalesForecastNotExistException("Sales Forecast from " + startMonth + "-" + startYear + " to " + endMonth + "-" + endYear + " for " + productName + " of Company " + companyName + " does not exist!");
        }
        return salesForecastList;
    }

    private Calendar eliminateMiliSecond(Calendar target) {
        Date time = new Date(target.getTime().getTime() - target.getTime().getTime() % 1000);
        target.setTime(time);
        return target;
    }

//    public FinishedGood retrieveProduct(String ownerCompanyName, String productName) {
//        Query query=em.createQuery("select f from FinishedGood f where f.productName=?1 and f.ownerCompanyName=?2 and f.archivedOrNot=FALSE");
//        query.setParameter(1, productName);
//        query.setParameter(2, ownerCompanyName);
//        ArrayList<FinishedGood>finishedGoodList=new ArrayList(query.getResultList());
//        
//        if(!finishedGoodList.isEmpty()){
//            FinishedGood product = finishedGoodList.get(0);
//            
//            return product;
//        }        
//        else{
//            return null;
//        }
//    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
