/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRM.session;

import Common.entity.Location;
import Common.session.CompanyManagementSessionBeanLocal;
import WMS.session.FacilityManagementLocal;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyNotExistException;
import util.exception.ShelfAlreadyExistException;
import util.exception.WarehouseAlreadyExistException;
import util.exception.WarehouseNotExistException;

/**
 *
 * @author andongmei
 */
@Stateless
@LocalBean
public class PopulateServiceCatalog {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    EntityManager em;

    @EJB
    private ServiceInfoMgtSessionBeanLocal simsbl;
    @EJB
    private FacilityManagementLocal fml;

    Long companyIdLogin;
    String companyNameLogin;

    public Boolean isWarehouseEmpty() {
        Query query = em.createQuery("SELECT w FROM Warehouse w");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isCatalogEmpty() {
        Query query = em.createQuery("SELECT s FROM ServiceCatalog s");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

public void populateCatalogList() {
        try {
            System.out.println("Populating Catalog Data...");
            this.populateWarehouse("W1", "12345678", "company1",
                    "Singapore", "Singapore", "Singapore", "Kent Ridge Drive", "12", "119243",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
            this.populateWarehouse("W2", "12345678", "company1",
                    "Singapore", "Singapore", "Singapore", "PGP", "40", "119393",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
            this.populateWarehouse("W3", "12345678", "company1",
                    "UK", "Scotland", "", "Perth Road", "40", "119393",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            //for Yunda
            this.populateWarehouse("Yunda Warehouse", "66537695", "Yunda",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            //  String regionCode, String shelvesNum, String capacity, String warehouseName, String ownerCompanyName
            this.populateShelf("Zone A", "500", "10", "Yunda Warehouse", "Yunda");
            this.populateShelf("Zone B", "500", "10", "Yunda Warehouse", "Yunda");
            this.populateShelf("Zone C", "500", "10", "Yunda Warehouse", "Yunda");

            //for Shenzhen Electronics
            this.populateWarehouse("Shenzhen Electronics Warehouse", "87898332", "Shenzhen Electronics",
                    "China", "Guangdong", "Shenzhen", "No.6 Guiyuan North Road, Luohu District", "6", "518001",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateShelf("Zone A", "500", "2", "Shenzhen Electronics Warehouse", "Shenzhen Electronics");
            this.populateShelf("Zone B", "500", "2", "Shenzhen Electronics Warehouse", "Shenzhen Electronics");
            this.populateShelf("Zone C", "500", "2", "Shenzhen Electronics Warehouse", "Shenzhen Electronics");

            //for Yunda
            this.populateWarehouse("Malaysia Warehouse Storage", "66537695", "Yunda",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem("Malaysia Warehouse Storage", "Yunda", "54", "Warehouse Services",
                    "We provide the best ever warehouse in the world, trust us!! ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE"
            );

            this.populateWarehouse("Jurong Storage Center Singapore", "66537695", "Yunda",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "TRUE", "FALSE", "FALSE", "FALSE", "20");

            this.populateCatalogItem(null, "Yunda", "54", "Singapore to Malaysia Transportation",
                    "We provide the best transportation services from Singapore to Malaysia. Choose us, you won't regret!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "TRUE");

            this.populateWarehouse("New York Warehouse Center", "66537695", "Yunda",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem("New York Warehouse Center", "Yunda", "54", "All in One International Logistics",
                    "We provide one-stop services. Just give us your goods, we deliver your goods from Malaysia to New York and help to keep them well in our warehouse. ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE");

            this.populateWarehouse("warehouse storage", "66537695", "Yunda",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem(null, "Yunda", "54", "USA to Malaysia",
                    "We help deliver your goods from USA to your home in Malaysia!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "TRUE");

            //for company6
            this.populateWarehouse("Malaysia Warehouse Storage", "12345678", "company2",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem("Malaysia Warehouse Storage", "company2", "6", "Warehouse Services",
                    "We provide the best ever warehouse in the world, trust us!! ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "FALSE"
            );

            this.populateWarehouse("Jurong Storage Center Singapore", "12345678", "company2",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "FALSE", "FALSE", "FALSE", "FALSE", "20");

            this.populateCatalogItem(null, "company2", "6", "Singapore to Malaysia Transportation",
                    "We provide the best transportation services from Singapore to Malaysia. Choose us, you won't regret!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "FALSE");

            this.populateWarehouse("New York Warehouse Center", "12345678", "company2",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem("New York Warehouse Center", "company2", "6", "All in One International Logistics",
                    "We provide one-stop services. Just give us your goods, we deliver your goods from Malaysia to New York and help to keep them well in our warehouse. ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "FALSE");

            this.populateWarehouse("warehouse storage", "12345678", "company2",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "TRUE", "TRUE", "TRUE", "TRUE", "20");

            this.populateCatalogItem(null, "company2", "6", "USA to Malaysia",
                    "We help deliver your goods from USA to your home in Malaysia!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "FALSE");

//            //for company11
//            this.populateWarehouse("warehouse Hi", "12345678", "company3",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse Hi", "company3", "11", "populatedServiceCatalogItem1forCompany11",
//                    "populatedServiceCatalogItem1", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //      "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "TRUE");
//
//            this.populateWarehouse("warehouse Hello World", "12345678", "company3",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse Hello World", "company3", "11", "populatedServiceCatalogItem2forCompany11",
//                    "populatedServiceCatalogItem1", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "TRUE");
//
//            this.populateWarehouse("warehouse no prob", "12345678", "company3",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse no prob", "company3", "11", "populatedServiceCatalogItem3forCompany11",
//                    "populatedServiceCatalogItem1", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "FALSE");
//
//            this.populateWarehouse("warehouse no worry", "12345678", "company3",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse no worry", "company3", "11", "populatedServiceCatalogItemforCompany11",
//                    "populatedServiceCatalogItem1", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //     "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "FALSE");
//            //for company16
//            this.populateWarehouse("warehouse love", "12345678", "company4",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse love", "company4", "16", "populatedServiceCatalogItem1forCompany16",
//                    "populatedServiceCatalogItem2", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //       "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "TRUE");
//
//            this.populateWarehouse("warehouse 4U", "12345678", "company4",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse 4U", "company4", "16", "populatedServiceCatalogItem2forCompany16",
//                    "populatedServiceCatalogItem2", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //     "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "TRUE");
//
//            this.populateWarehouse("warehouse3102", "12345678", "company4",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouse3102", "company4", "16", "populatedServiceCatalogItem3forCompany16",
//                    "populatedServiceCatalogItem2", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //     "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "FALSE");
//
//            this.populateWarehouse("warehouseEverything", "12345678", "company4",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//
//            this.populateCatalogItem("warehouseEverything", "company4", "16", "populatedServiceCatalogItemforCompany16",
//                    "populatedServiceCatalogItem2", "1000",
//                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
//                    //"1", "1", "1","1",
//                    "Singapore", "Singapore", "n.a", "n.a", "n.a", "n.a",
//                    "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    //     "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
//                    "TRUE", "TRUE", "TRUE", "TRUE", "UK", "Scotland", "n.a", "n.a", "n.a", "n.a", "FALSE");
//for company21
            this.populateWarehouse("Malaysia Warehouse Storage", "66537695", "merlion",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem("Malaysia Warehouse Storage", "merlion", "21", "Warehouse Services",
                    "We provide the best ever warehouse in the world, trust us!! ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE"
            );

            this.populateWarehouse("Jurong Storage Center Singapore", "66537695", "merlion",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "FALSE", "FALSE", "FALSE", "FALSE", "0.0");

            this.populateCatalogItem(null, "merlion", "21", "Singapore to Malaysia Transportation",
                    "We provide the best transportation services from Singapore to Malaysia. Choose us, you won't regret!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "TRUE");

            this.populateWarehouse("New York Warehouse Center", "66537695", "merlion",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem("New York Warehouse Center", "merlion", "21", "All in One International Logistics",
                    "We provide one-stop services. Just give us your goods, we deliver your goods from Malaysia to New York and help to keep them well in our warehouse. ", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE");

            this.populateWarehouse("warehouse storage", "66537695", "merlion",
                    "Singapore", "Singapore", "Singapore", "2 Jurong East Street 21", "20", "609601",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem(null, "merlion", "21", "USA to Malaysia",
                    "We help deliver your goods from USA to your home in Malaysia!", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "TRUE");

            this.populateWarehouse("Warehouse Singapore", "66537695", "merlion",
                    "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem("Warehouse Singapore", "merlion", "21", "iPhone+",
                    "dedicated for iPhone accessory", "1000",
                    "2014-01-01 00:00:00.000000000", "2050-12-01 00:00:00.000000000",
                    //"1", "1", "1","1",
                    "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001",
                    "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601",
                    //      "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "Singapore", "Singapore", "Singapore", "Jurong East Street 21", "2", "609601",
                    "TRUE");

            this.populateWarehouse("Warehouse Malaysia", "12345678", "merlion",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem("Warehouse Malaysia", "merlion", "21", "Warehouse Malaysia",
                    "Dedicated Warehouse Service in Malaysia", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur City Centre", "Petronas Twin Towers", "050088",
                    "TRUE");

            this.populateWarehouse("warehouse London", "66537695", "merlion",
                    "Singapore", "Singapore", "Singapore", "18 Jurong West Road", "Block 20", "609040",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");

            this.populateCatalogItem(null, "merlion", "21", "China Singapore Transporation Service",
                    "China Singapore Transportation Services", "1000",
                    "2014-12-01 00:00:00.000000000", "2015-12-01 00:00:00.000000000",
                    //"1", "1", "1","1",
                    "China", "Guangdong", "Shenzhen", "20 Shenzhen Road", "40", "889029",
                    "Singapore", "Singapore", "Singapore", "18 Jurong West", "20", "609040",
                    //     "UK", "Scotland", "n.a", "n.a", "n.a", "n.a",
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    null, null, null, null, null, null,
                    "TRUE");

            this.populateWarehouse("Warehouse USA", "68886666", "merlion",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE", "TRUE", "TRUE", "TRUE", "1000");

            this.populateCatalogItem("Warehouse USA", "merlion", "21", "iPhone +",
                    "dedicate to iPhone accessory logistics services", "1000",
                    "2014-01-01 00:00:00.000000000", "2050-12-31 00:00:00.000000000",
                    //"1", "1", "1","1",
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    "TRUE", "TRUE", "TRUE", "TRUE",
                    "USA", "New York", "New York", "350 5th Ave", "29", "010118",
                    "TRUE");

            this.populateWarehouse("FruitsExpress Warehouse", "12345678", "FruitsExpress",
                    "China", "Guang Dong", "Shenzhen", "Guiyuan North Road", "NO.6", "518001",
                    "TRUE", "TRUE", "TRUE", "TRUE", "0.1");
//           
            System.out.println("Catalog Data populated!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateWarehouse(String warehouseName, String contactNo, String ownerCompanyName,
            //  String sourceCountry, String sourceState, String sourceCity, String sourceStreet, String sourceBlockNo, String sourcePostalCode,
            //String destCountry, String destState, String destCity, String destStreet, String destBlockNo, String destPostalCode,
            String warehCountry, String warehState, String warehCity, String warehStreet, String warehBlockNo, String warehPostalCode,
            String perishableS, String flammableS, String pharmaceuticalS, String highValueS, String warehouseP
    ) {

        try {
            // Double price = Double.valueOf(p);

//            Timestamp startTime = Timestamp.valueOf(sTime);
//            Timestamp endTime = Timestamp.valueOf(eTime);
            Boolean perishable = Boolean.valueOf(perishableS);
            Boolean flammable = Boolean.valueOf(flammableS);
            Boolean pharmaceutical = Boolean.valueOf(pharmaceuticalS);
            Boolean highValue = Boolean.valueOf(highValueS);

            Double warehousePrice = Double.valueOf(warehouseP);

//            Location source = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
//            Location dest = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
            Location wareh = new Location(warehCountry, warehState, warehCity, warehStreet, warehBlockNo, warehPostalCode);

            fml.createWarehouse(warehouseName, contactNo, ownerCompanyName, wareh, perishable, flammable, pharmaceutical, highValue, warehousePrice);
            //fml.createWarehouse(warehouseName, contactNum, ownerCompanyName, wareh, perishable, flammable, pharmaceutical, highValue);
            // simsbl.createServiceCatalog(cId, serviceName, description, price, startTime, endTime, source, dest, wareh, perishable, flammable, pharmaceutical, highValue);
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateServiceCatalog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WarehouseAlreadyExistException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void populateShelf(String regionCode, String shelvesNum, String capacity, String warehouseName, String ownerCompanyName) {
        try {
            Integer numShelves = Integer.valueOf(shelvesNum);
            Integer shelfCapacity = Integer.valueOf(capacity);

            fml.createShelfListForSpecificWarehouse(regionCode, numShelves, shelfCapacity, warehouseName, ownerCompanyName);
        } catch (WarehouseNotExistException ex) {
            Logger.getLogger(PopulateServiceCatalog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ShelfAlreadyExistException ex) {
            Logger.getLogger(PopulateServiceCatalog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void populateCatalogItem(String warehouseNameCatalog, String companyName, String companyIdLogin, String serviceName, String description, String p, String sTime, String eTime,
            String sourceCountry, String sourceState, String sourceCity, String sourceStreet, String sourceBlockNo, String sourcePostalCode,
            String destCountry, String destState, String destCity, String destStreet, String destBlockNo, String destPostalCode,
            // String warehCountry, String warehState, String warehCity, String warehStreet, String warehBlockNo, String warehPostalCode,
            String perishableS, String flammableS, String pharmaceuticalS, String highValueS,
            String warehCountry, String warehState, String warehCity, String warehStreet, String warehBlockNo, String warehPostalCode, String publicOrNotS
    ) {

        try {
            Double price = Double.valueOf(p);

            Long cId = Long.valueOf(companyIdLogin);

            Timestamp startTime = Timestamp.valueOf(sTime);
            Timestamp endTime = Timestamp.valueOf(eTime);

            Boolean perishable = Boolean.valueOf(perishableS);
            Boolean flammable = Boolean.valueOf(flammableS);
            Boolean pharmaceutical = Boolean.valueOf(pharmaceuticalS);
            Boolean highValue = Boolean.valueOf(highValueS);

            Boolean publicOrNot = Boolean.valueOf(publicOrNotS);

            Location source = new Location(sourceCountry, sourceState, sourceCity, sourceStreet, sourceBlockNo, sourcePostalCode);
            Location dest = new Location(destCountry, destState, destCity, destStreet, destBlockNo, destPostalCode);
            Location wareh = new Location(warehCountry, warehState, warehCity, warehStreet, warehBlockNo, warehPostalCode);

          
            Long warehouseId = null;

            if (warehouseNameCatalog == null) {
                warehouseId = null;
                simsbl.createServiceCatalog(cId, serviceName, description, price, startTime, endTime, source, dest, warehouseId, wareh, perishable, flammable, pharmaceutical, highValue, publicOrNot);

            } else {
                try {
                    warehouseId = fml.retrieveWarehouse(warehouseNameCatalog, companyName).getId();
                    System.out.println(warehouseId);
                } catch (WarehouseNotExistException ex) {
                    Logger.getLogger(PopulateServiceCatalog.class.getName()).log(Level.SEVERE, null, ex);
                }

                simsbl.createServiceCatalog(cId, serviceName, description, price, startTime, endTime, source, dest, warehouseId, wareh, perishable, flammable, pharmaceutical, highValue, publicOrNot);
            }
        } catch (CompanyNotExistException ex) {
            Logger.getLogger(PopulateServiceCatalog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
