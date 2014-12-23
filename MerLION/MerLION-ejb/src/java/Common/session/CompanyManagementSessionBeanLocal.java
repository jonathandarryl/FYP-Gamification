/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Company;
import Common.entity.CustomerCompany;
import Common.entity.Department;
import Common.entity.PartnerCompany;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyExistException;
import util.exception.CompanyNotExistException;
import util.exception.DepartmentExistException;
import util.exception.DepartmentNotExistException;
/**
 *
 * @author chongyangsun
 */
@Local
public interface CompanyManagementSessionBeanLocal {
    public Company retrieveCompany(String companyName) throws CompanyNotExistException;
    public CustomerCompany retrieveCustomerCompany(String companyName) throws CompanyNotExistException;
    public PartnerCompany retrievePartnerCompany(String companyName) throws CompanyNotExistException;
    public boolean deactivateCompany(Long companyId, String companyType) throws CompanyNotExistException;
    public Company retrieveCompany(Long companyId) throws CompanyNotExistException;
    public CustomerCompany createCustomerCompany(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException;
    public CustomerCompany registerCustomerCompany(String companyName, String companyType, String contactNo, String country, String state, String city, String street, String blockNo, String postalCode, String VAT) throws CompanyExistException;
    public CustomerCompany updateCustomerCompany(Long companyId, String companyName, String companyType, String contactNo, String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException;
    public Boolean deleteCustomerCompany(Long companyId) throws CompanyNotExistException;
    public PartnerCompany createPartnerCompany(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException;
    public PartnerCompany registerPartnerCompany(String companyName, String companyType, String contactNo, String country, String state, String city, String street, String blockNo, String postalCode, String VAT) throws CompanyExistException;
    public PartnerCompany updatePartnerCompany(Long companyId, String companyName, String companyType, String contactNo,String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException;
    public Boolean deletePartnerCompany(Long companyId) throws CompanyNotExistException;
    public Company createMerLION(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException;
    public Company updateMerLION(Long companyId, String companyName, String companyType, String contactNo, String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException;
    public Department createDepartment(String departmentName, Company company) throws DepartmentExistException;
    public Department registerDepartment(String departmentName, Company company) throws DepartmentExistException;
    public Department retrieveDepartment(String departmentName, Company company) throws DepartmentNotExistException;
    public Department retrieveDepartment(Long departmentId) throws DepartmentNotExistException;
    public Boolean deleteDepartment(String departmentName, Company company) throws DepartmentNotExistException, CompanyNotExistException;
    public boolean approveRequest(Long accountId, Long userId, Long companyId, String companyType);
    public List<Company> retrieveAllCompany();
    public List<Company> retrieveLockedCompany();
    public Company retrieveMerLION(Long companyId) throws CompanyNotExistException;
}
