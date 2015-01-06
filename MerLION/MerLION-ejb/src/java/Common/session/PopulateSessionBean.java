/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.session;

import Common.entity.*;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyExistException;
import util.exception.CompanyNotExistException;
import util.exception.DepartmentExistException;
import util.exception.DepartmentNotExistException;
import util.exception.TitleExistException;
import util.exception.TitleNotExistException;
import util.exception.UserExistException;

/**
 *
 * @author chongyangsun
 */
@Stateless
@LocalBean
public class PopulateSessionBean {

    @PersistenceContext
    EntityManager em;

    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    MessageSessionBeanLocal msbl;
    @EJB
    AnnouncementSessionBeanLocal asbl;
    @EJB
    NotificationSessionBeanLocal nsbl;

    public Boolean isAccountEmpty() {
        Query query = em.createQuery("SELECT a FROM Account a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isMessageEmpty() {
        Query query = em.createQuery("SELECT m FROM Message m");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAnnouncementEmpty() {
        Query query = em.createQuery("SELECT a FROM Announcement a");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isNotificationEmpty() {
        Query query = em.createQuery("SELECT n FROM Notification n");
        if (query.getResultList().size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void populateDataList() {
        try {
            System.out.println("Populating Account...");
            this.populateCustomerCompany("admin1", "Admin", "fn1", "ln1", "12345678", "zhongwei210@gmail.com", "company1", "1PL", "12345678", "Account Management", "Manager", "1A Kent Ridge Road", "", "Singapore", "Singapore", "Singapore", "119224", "ABC12345");
            this.populatePartnerCompany("admin2", "Admin", "fn2", "ln2", "12345678", "sun.mingjia.echo@gmail.com", "company2", "2PL", "12345678", "Account Management", "Manager", "22 Prince George's Park", "", "Singapore", "Singapore", "Singapore", "118420", "ABC12346");
            this.populatePartnerCompany("admin3", "Admin", "fn3", "ln3", "12345678", "annieandongmei@gmail.com", "company3", "3PL", "12345678", "Account Management", "Manager", "2C Boon Tiong Road", "", "Singapore", "Singapore", "Singapore", "166002", "ABC12347");
            this.populatePartnerCompany("admin4", "Admin", "fn4", "ln4", "12345678", "hanxiangyuxy@gmail.com", "company4", "4PL", "12345678", "Account Management", "Manager", "10 Bayfront Ave", "", "Singapore", "Singapore", "Singapore", "018956", "ABC12348");
            this.populateMerLion("MLAdmin1", "SystemAdmin", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Account Management", "Manager", "13 Computing Dr", "", "Singapore", "Singapore", "Singapore", "117417", "ABC12349");
            this.populateMerLionAccountStaff("MLAdmin2", "SystemAdmin", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Account Management", "Staff");

            this.populateCustomerCompany("SZEAdmin", "Admin", "Dongmei", "An", "12345678", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Account Management", "Manager", "No.6 Guiyuan North Road", "Luohu District", "Shenzhen", "Guangdong", "China", "518001", "CN12345678");
            this.populateCustomerAccount("SZManufacture", "User", "Annie", "An", "23456789", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Manufacturing Department", "Manager");
            this.populateCustomerAccount("SZMarketing", "User", "Annie", "An", "23456789", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Sales and Marketing Department", "Manager");
            this.populateCustomerAccount("SZOperation", "User", "Annie", "An", "23456789", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Operation Department", "Manager");
            this.populateCustomerAccount("SZTransportation", "User", "Annie", "An", "23456789", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Transportation Department", "Manager");
            this.populateCustomerAccount("SZWarehouse", "User", "Annie", "An", "23456789", "annieandongmei@gmail.com", "Shenzhen Electronics", "1PL", "65436543", "Warehouse Department", "Manager");

            this.populatePartnerCompany("YDAdmin", "Admin", "Runfeng", "Han", "34567890", "hanrunfeng@gmail.com", "Yunda", "2PL", "65433456", "Account Management", "Manager", "6679 Yinggang East Road", "Qingpu District", "Shanghai", "Shanghai", "China", "201700", "CN11223344");
            this.populatePartnerAccount("YDWarehouse", "User", "Runfeng", "Han", "34567890", "hanrunfeng@gmail.com", "Yunda", "2PL", "65433456", "Warehouse Department", "Manager");
            this.populatePartnerAccount("YDTransportation", "User", "Runfeng", "Han", "34567890", "hanrunfeng@gmail.com", "Yunda", "2PL", "65433456", "Transportation Department", "Manager");
            this.populatePartnerAccount("YDOperation", "User", "Runfeng", "Han", "34567890", "hanrunfeng@gmail.com", "Yunda", "2PL", "65433456", "Operation Department", "Manager");
            this.populatePartnerAccount("YDMarketing", "User", "Runfeng", "Han", "34567890", "hanrunfeng@gmail.com", "Yunda", "2PL", "65433456", "Sales and Marketing Department", "Manager");

            this.populateCustomerCompany("MMAdmin", "Admin", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Account Management", "Manager", "Kuala Lumpur City Centre", "PETRONAS Twin Towers", "Kuala Lumpur", "Kuala Lumpur", "Malaysia", "050088", "MY12345678");
            this.populateCustomerAccount("MMMarketing", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Sales and Marketing Department", "Manager");
            this.populateCustomerAccount("MMOperation", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Operation Department", "Manager");
            this.populateCustomerAccount("MMManufacture", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Manufacturing Department", "Manager");
            this.populateCustomerAccount("MMTransportation", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Transportation Department", "Manager");
            this.populateCustomerAccount("MMWarehouse", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "MalayMedi", "1PL", "20515000", "Warehouse Department", "Manager");

            this.populateCustomerCompany("FEAdmin", "Admin", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Account Management", "Manager", "1 Infinite Loop", "", "Cupertino", "California", "Cupertino", "095014", "US12345678");
            this.populateCustomerAccount("FEMarketing", "User", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Sales and Marketing Department", "Manager");
            this.populateCustomerAccount("FEOperation", "User", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Operation Department", "Manager");
            this.populateCustomerAccount("FEManufacture", "User", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Manufacturing Department", "Manager");
            this.populateCustomerAccount("FETransportation", "User", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Transportation Department", "Manager");
            this.populateCustomerAccount("FEWarehouse", "User", "Xiangyu", "Han", "23456543", "hanxiangyuxy@gmail.com", "FruitsExpress", "1PL", "22345000", "Warehouse Department", "Manager");

            this.populatePartnerCompany("DHLAdmin", "Admin", "Annie", "An", "12345678", "annieandongmei@gmail.com", "DHL", "3PL", "49436543", "Account Management", "Manager", "21 Tampines Avenue", "Temasek Polytechnic", "Singapore", "Singapore", "Singapore", "529757", "SG11223344");
            this.populatePartnerAccount("DHLOperation", "User", "Annie", "An", "12345678", "annieandongmei@gmail.com", "DHL", "3PL", "49436543", "Operation Department", "Manager");
            this.populatePartnerAccount("DHLMarketing", "User", "Annie", "An", "12345678", "annieandongmei@gmail.com", "DHL", "3PL", "49436543", "Sales and Marketing Department", "Manager");
            this.populatePartnerAccount("DHLTransportation", "User", "Annie", "An", "12345678", "annieandongmei@gmail.com", "DHL", "3PL", "49436543", "Transportation Department", "Manager");
            this.populatePartnerAccount("DHLWarehouse", "User", "Annie", "An", "12345678", "annieandongmei@gmail.com", "DHL", "3PL", "49436543", "Warehouse Department", "Manager");

            this.populateCustomerCompany("CAdmin", "Admin", "Dongmei", "An", "12345678", "annieandongmei@gmail.com", "Challenger", "1PL", "65436543", "Account Management", "Manager", "109 N Bridge Rd", "Funan DigitaLife Mall", "Singapore", "Singapore", "Singapore", "179097", "SG12545678");
            this.populateCustomerAccount("CMarketing", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "Challenger", "1PL", "65436543", "Sales and Marketing Department", "Manager");
            this.populateCustomerAccount("COperation", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "Challenger", "1PL", "65436543", "Operation Department", "Manager");
            this.populateCustomerAccount("CManufacture", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "Challenger", "1PL", "65436543", "Manufacturing Department", "Manager");
            this.populateCustomerAccount("CTransportation", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "Challenger", "1PL", "65436543", "Transportation Department", "Manager");
            this.populateCustomerAccount("CWarehouse", "User", "Wei", "Zhong", "98765432", "zhongwei210@gmail.com", "Challenger", "1PL", "65436543", "Warehouse Department", "Manager");

            this.populateCustomerAccount("manager11", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Sales and Marketing Department", "Manager");
            this.populateCustomerAccountstaff("staff11", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Sales and Marketing Department", "Staff");
            this.populateCustomerAccount("manager12", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Transportation Department", "Manager");
            this.populateCustomerAccountstaff("staff12", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Transportation Department", "Staff");
            this.populateCustomerAccount("manager13", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Warehouse Department", "Manager");
            this.populateCustomerAccountstaff("staff13", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Warehouse Department", "Staff");
            this.populateCustomerAccount("manager14", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Manufacturing Department", "Manager");
            this.populateCustomerAccountstaff("staff14", "User", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Manufacturing Department", "Staff");
            this.populateCustomerAccount("manager15", "User", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "company1", "1PL", "12345678", "Operation Department", "Manager");

            this.populatePartnerAccount("manager21", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Sales and Marketing Department", "Manager");
            this.populatePartnerAccountStaff("staff21", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Sales and Marketing Department", "Staff");
            this.populatePartnerAccount("manager22", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Transportation Department", "Manager");
            this.populatePartnerAccountStaff("staff22", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Transportation Department", "Staff");
            this.populatePartnerAccount("manager23", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Warehouse Department", "Manager");
            this.populatePartnerAccountStaff("staff23", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Warehouse Department", "Staff");
            this.populatePartnerAccount("manager24", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Manufacturing Department", "Manager");
            this.populatePartnerAccountStaff("staff24", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "company2", "2PL", "12345678", "Manufacturing Department", "Staff");

            this.populatePartnerAccount("manager31", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Sales and Marketing Department", "Manager");
            this.populatePartnerAccountStaff("staff31", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Sales and Marketing Department", "Staff");
            this.populatePartnerAccount("manager32", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Transportation Department", "Manager");
            this.populatePartnerAccountStaff("staff32", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Transportation Department", "Staff");
            this.populatePartnerAccount("manager33", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Warehouse Department", "Manager");
            this.populatePartnerAccountStaff("staff33", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Warehouse Department", "Staff");
            this.populatePartnerAccount("manager34", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Manufacturing Department", "Manager");
            this.populatePartnerAccountStaff("staff34", "User", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "company3", "3PL", "12345678", "Manufacturing Department", "Staff");

            this.populatePartnerAccount("manager41", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Sales and Marketing Department", "Manager");
            this.populatePartnerAccountStaff("staff41", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Sales and Marketing Department", "Staff");
            this.populatePartnerAccount("manager42", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Transportation Department", "Manager");
            this.populatePartnerAccountStaff("staff42", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Transportation Department", "Staff");
            this.populatePartnerAccount("manager43", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Warehouse Department", "Manager");
            this.populatePartnerAccountStaff("staff43", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Warehouse Department", "Staff");
            this.populatePartnerAccount("manager44", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Manufacturing Department", "Manager");
            this.populatePartnerAccountStaff("staff44", "User", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "company4", "4PL", "12345678", "Manufacturing Department", "Staff");

            this.populateMerLionAccount("MLMarketing", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Sales and Marketing Department", "Manager");
            this.populateMerLionAccountStaff("MLMarketingStaff", "User", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Sales and Marketing Department", "Staff");
            this.populateMerLionAccount("MLTransportation", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Transportation Department", "Manager");
            this.populateMerLionAccountStaff("MLTransportationStaff", "User", "fn2", "ln2", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Transportation Department", "Staff");
            this.populateMerLionAccount("MLWarehouse", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Warehouse Department", "Manager");
            this.populateMerLionAccountStaff("MLWarehouseStaff", "User", "fn3", "ln3", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Warehouse Department", "Staff");
            this.populateMerLionAccount("MLManufacture", "User", "fn4", "ln4", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Manufacturing Department", "Manager");
            this.populateMerLionAccountStaff("MLManufactureStaff", "User", "fn5", "ln5", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Manufacturing Department", "Staff");
            this.populateMerLionAccount("MLAggregation", "User", "Chongyang", "Sun", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Demand Aggregation Department", "Manager");
            this.populateMerLionAccountStaff("MLAggregationStaff", "User", "Chongyang", "Sun", "12345678", "suncyqq@gmail.com", "merlion", "5PL", "12345678", "Demand Aggregation Department", "Staff");
            this.populateCustomerCompany("GRNSProductAdmin", "Admin", "fn1", "ln1", "12345678", "suncyqq@gmail.com", "GRNSOrder", "1PL", "12345678", "Account Management", "Manager", "1A Kent Ridge Road", "", "Singapore", "Singapore", "Singapore", "119224", "ABCDEFG");

            System.out.println("Account populated!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateCustomerCompany(String username, String userType, String firstName, String lastName,
            String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName,
            String titleName, String street, String blockNo, String city, String state, String country, String postalCode, String VAT) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException {
        cmsbl.createCustomerCompany(companyName, companyType, contactNo, VAT);
        CustomerCompany cc = cmsbl.retrieveCustomerCompany(companyName);
        CustomerCompany customerCompany = cmsbl.updateCustomerCompany(cc.getId(), companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        Department department = cmsbl.createDepartment(departmentName, customerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, customerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateCustomerAccount(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        CustomerCompany customerCompany = cmsbl.retrieveCustomerCompany(companyName);
        Department department = cmsbl.createDepartment(departmentName, customerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, customerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateCustomerAccountstaff(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        CustomerCompany customerCompany = cmsbl.retrieveCustomerCompany(companyName);
        Department department = cmsbl.retrieveDepartment(departmentName, customerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, customerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populatePartnerCompany(String username, String userType, String firstName, String lastName, String phoneNo,
            String email, String companyName, String companyType, String contactNo, String departmentName, String titleName,
            String street, String blockNo, String city, String state, String country, String postalCode, String VAT) throws CompanyExistException, DepartmentExistException, TitleExistException, UserExistException, AccountTypeNotExistException, CompanyNotExistException {
        cmsbl.createPartnerCompany(companyName, companyType, contactNo, VAT);
        PartnerCompany pc = cmsbl.retrievePartnerCompany(companyName);
        PartnerCompany partnerCompany = cmsbl.updatePartnerCompany(pc.getId(), companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        Department department = cmsbl.createDepartment(departmentName, partnerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, partnerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populatePartnerAccount(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        PartnerCompany partnerCompany = cmsbl.retrievePartnerCompany(companyName);
        Department department = cmsbl.createDepartment(departmentName, partnerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, partnerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populatePartnerAccountStaff(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        PartnerCompany partnerCompany = cmsbl.retrievePartnerCompany(companyName);
        Department department = cmsbl.retrieveDepartment(departmentName, partnerCompany);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, partnerCompany, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateMerLion(String username, String userType, String firstName, String lastName, String phoneNo,
            String email, String companyName, String companyType, String contactNo, String departmentName, String titleName,
            String street, String blockNo, String city, String state, String country, String postalCode, String VAT) throws CompanyExistException, DepartmentExistException, TitleExistException, UserExistException, AccountTypeNotExistException, CompanyNotExistException {
        cmsbl.createMerLION(companyName, companyType, contactNo, VAT);
        Company c = cmsbl.retrieveCompany(companyName);
        Company merlion = cmsbl.updateMerLION(c.getId(), companyName, companyType, contactNo, country, state, city, street, blockNo, postalCode);
        Department department = cmsbl.createDepartment(departmentName, merlion);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, merlion, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateMerLionAccount(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        Company company = cmsbl.retrieveCompany(companyName);
        Department department = cmsbl.createDepartment(departmentName, company);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, company, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateMerLionAccountStaff(String username, String userType, String firstName, String lastName, String phoneNo, String email, String companyName, String companyType, String contactNo, String departmentName, String titleName) throws CompanyExistException, DepartmentExistException, CompanyNotExistException, TitleExistException, UserExistException, AccountTypeNotExistException, DepartmentNotExistException, TitleNotExistException {
        Company company = cmsbl.retrieveCompany(companyName);
        Department department = cmsbl.retrieveDepartment(departmentName, company);
        Title title = amsbl.createTitle(titleName, department);
        Account account = amsbl.createAccountForPopulateData(username, userType, firstName, lastName, phoneNo, email, department, title, company, companyType);
        amsbl.activateAccount(account.getId(), userType);
    }

    public void populateMessage() {
        System.out.println("Populating Message ...");
        this.populateMessageList(Long.parseLong(String.valueOf("24")), Long.parseLong(String.valueOf("4")), "Hello from System Admin", "Hello! This is a sample message from System Admin");
        this.populateMessageList(Long.parseLong(String.valueOf("24")), Long.parseLong(String.valueOf("9")), "Hello from System Admin", "Hello! This is a sample message from System Admin");
        this.populateMessageList(Long.parseLong(String.valueOf("24")), Long.parseLong(String.valueOf("14")), "Hello from System Admin", "Hello! This is a sample message from System Admin");
        this.populateMessageList(Long.parseLong(String.valueOf("24")), Long.parseLong(String.valueOf("19")), "Hello from System Admin", "Hello! This is a sample message from System Admin");
        this.populateMessageList(Long.parseLong(String.valueOf("4")), Long.parseLong(String.valueOf("24")), "Hello", "Hello! This is a sample message from Admin1");
        this.populateMessageList(Long.parseLong(String.valueOf("9")), Long.parseLong(String.valueOf("24")), "Hello", "Hello! This is a sample message from Admin2");
        this.populateMessageList(Long.parseLong(String.valueOf("14")), Long.parseLong(String.valueOf("24")), "Hello", "Hello! This is a sample message from Admin3");
        this.populateMessageList(Long.parseLong(String.valueOf("19")), Long.parseLong(String.valueOf("24")), "Hello", "Hello! This is a sample message from Admin4");
        this.populateMessageList(Long.parseLong(String.valueOf("9")), Long.parseLong(String.valueOf("4")), "Hello", "Hello! This is a sample message from Admin2");
        this.populateMessageList(Long.parseLong(String.valueOf("14")), Long.parseLong(String.valueOf("4")), "Hello", "Hello! This is a sample message from Admin3");
        this.populateMessageList(Long.parseLong(String.valueOf("19")), Long.parseLong(String.valueOf("4")), "Hello", "Hello! This is a sample message from Admin4");
        System.out.println("Message populated!");
    }

    public void populateMessageList(Long senderId, Long receiverId, String title, String content) {
        msbl.createMessage(senderId, receiverId, title, content);
    }

    public void populateAnnouncement() throws AccountTypeNotExistException {
        System.out.println("Populating Announcement ...");
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Account Management", Long.parseLong(String.valueOf("4")), "Admin", Long.parseLong(String.valueOf("2")));
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Sales and Marketing", Long.parseLong(String.valueOf("4")), "Admin", Long.parseLong(String.valueOf("29")));
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Transportation Department", Long.parseLong(String.valueOf("4")), "Admin", Long.parseLong(String.valueOf("36")));
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Account Management", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("22")));
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Sales and Marketing", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("141")));
        this.populateAnnoucementList("Sample Announcemnt", "Sample Announcement to department Transportation Department", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("148")));
        System.out.println("Announcement populated!");
    }

    public void populateAnnoucementList(String title, String content, Long accountId, String accountType, Long departmentId) throws AccountTypeNotExistException {
        Account account = amsbl.retrieveAccount(accountId, accountType);
        asbl.createAnnouncement(title, content, account, departmentId);
    }

    public void populateNotification() throws AccountTypeNotExistException {
        System.out.println("Populating Notification ...");
        this.populateNotificationList("Sample Notification for fee payment", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("2")));
        this.populateNotificationList("Sample Notification for fee payment", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("7")));
        this.populateNotificationList("Sample Notification for fee payment", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("12")));
        this.populateNotificationList("Sample Notification for fee payment", Long.parseLong(String.valueOf("24")), "SystemAdmin", Long.parseLong(String.valueOf("17")));
        System.out.println("Notification populated!");
    }

    public void populateNotificationList(String content, Long accountId, String accountType, Long departmentId) throws AccountTypeNotExistException {
        Account account = amsbl.retrieveAccount(accountId, accountType);
        nsbl.createNotification(account, departmentId, content);
    }
}
