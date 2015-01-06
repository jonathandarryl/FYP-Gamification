/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MRPII.session;

import MRPII.entity.SalesForecast;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import javax.ejb.Local;
import util.exception.MonthlyRecordNotExistException;
import util.exception.MultipleProductWithSameNameException;
import util.exception.MultipleSalesForecastForSameMonthException;
import util.exception.ProductNotExistException;
import util.exception.SalesForecastNotExistException;

/**
 *
 * @author songhan
 */
@Local
public interface SalesForecastSessionLocal {
    public HashMap<Date, Double> retrieveProductSalesRecord (String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException;
    
    public SalesForecast generateSalesForecast(Integer year, Integer month, String companyName, String productName)
            throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, MultipleSalesForecastForSameMonthException;
    
    public SalesForecast retrieveSalesForecast(Long sfId)throws SalesForecastNotExistException;

    public ArrayList<SalesForecast> retrieveSalesForecast(String productName, String companyName, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) 
            throws SalesForecastNotExistException, ProductNotExistException, MultipleProductWithSameNameException;

    public SalesForecast retrieveSalesForecast(String productName, String companyName, Integer year, Integer month) throws SalesForecastNotExistException, ProductNotExistException, MultipleProductWithSameNameException, MultipleSalesForecastForSameMonthException;

    public ArrayList<SalesForecast> generateSalesForecastList(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth, String companyName, String productName) throws ProductNotExistException, MultipleProductWithSameNameException, MonthlyRecordNotExistException, ProductNotExistException, MultipleSalesForecastForSameMonthException;
}
