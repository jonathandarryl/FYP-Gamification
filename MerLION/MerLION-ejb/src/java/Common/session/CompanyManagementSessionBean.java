/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Common.session;

import Common.entity.Company;
import Common.entity.CompanyAdmin;
import Common.entity.CompanyAdminAccount;
import Common.entity.CustomerCompany;
import Common.entity.Department;
import Common.entity.Location;
import Common.entity.MerLION;
import Common.entity.PartnerCompany;
import Common.entity.Title;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CompanyExistException;
import util.exception.CompanyNotExistException;
import util.exception.DepartmentExistException;
import util.exception.DepartmentNotExistException;
import util.session.SendEmail;

/**
 *
 * @author chongyangsun
 */
@Stateless
public class CompanyManagementSessionBean implements CompanyManagementSessionBeanLocal, CompanyManagementSessionBeanRemote{

    @PersistenceContext
    private EntityManager em;

    private SendEmail sendEmail;

    @Override
    public Company retrieveCompany(String companyName) throws CompanyNotExistException {
        System.out.println(companyName);
        Query query = em.createQuery("select c from Company c where c.companyName=:name and c.lockedOrNot='false' and c.deleteOrNot='false'");
        query.setParameter("name", companyName);

        List<Company> companyList = new ArrayList<>(query.getResultList());
        if (companyList.isEmpty()) {
            System.out.println("no company found!");
            throw new CompanyNotExistException("Company " + companyName + " does not exist!");
        }
        Company company = companyList.get(0);
        return company;
    }

    @Override
    public CustomerCompany retrieveCustomerCompany(String companyName) throws CompanyNotExistException {
        System.out.println(companyName);
        CustomerCompany customerCompany = null;
        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1 and c.lockedOrNot='false' and c.deleteOrNot='false'");
        query.setParameter(1, companyName);
        ArrayList<CustomerCompany> customerCompanyList = new ArrayList(query.getResultList());
        if (customerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyName + " does not exist!");
        }
        customerCompany = customerCompanyList.get(0);
        return customerCompany;
    }

    @Override
    public PartnerCompany retrievePartnerCompany(String companyName) throws CompanyNotExistException {
        PartnerCompany partnerCompany = null;
        Query query = em.createQuery("select c from PartnerCompany c where c.companyName=?1 and c.lockedOrNot='false' and c.deleteOrNot='false'");
        query.setParameter(1, companyName);
        ArrayList<PartnerCompany> partnerCompanyList = new ArrayList(query.getResultList());
        if (partnerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyName + " does not exist!");
        }
        partnerCompany = partnerCompanyList.get(0);
        return partnerCompany;
    }

    @Override
    public Company retrieveCompany(Long companyId) throws CompanyNotExistException {
//        CustomerCompany ownerCompany = null;
//        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1 and c.lockedOrnot=FALSE");
//        query.setParameter(1, companyName);
//        ArrayList<CustomerCompany> ownerCompanyList = new ArrayList(query.getResultList());
//        if(ownerCompanyList.isEmpty()){
//            throw new CompanyNotExistException("Company "+companyName+" does not exist!");
//        }        
//        ownerCompany = ownerCompanyList.get(0);  
//        return ownerCompany;

        Company company = em.find(Company.class, companyId);
        if (company == null) {
            throw new CompanyNotExistException("Company id: " + companyId + " does not exist!");
        }
        System.out.println("The retrieved Company is " + company.getCompanyName());
        return company;
    }
    
    @Override
    public Company retrieveMerLION(Long companyId) throws CompanyNotExistException {
        MerLION company = em.find(MerLION.class, companyId);
        if (company == null) {
            throw new CompanyNotExistException("Company id: " + companyId + " does not exist!");
        }
        System.out.println("The retrieved Company is " + company.getCompanyName());
        return company;
    }


    @Override
    public boolean deactivateCompany(Long companyId, String companyType) throws CompanyNotExistException {
        if (companyType.equals("2PL") || companyType.equals("3PL") || companyType.equals("4PL")) {
            PartnerCompany partnerCompany = em.find(PartnerCompany.class, companyId);
            if (partnerCompany == null) {
                throw new CompanyNotExistException("Company " + companyId + " does not exist");
            }
            partnerCompany.setLockedOrNot(Boolean.TRUE);
            return true;
        } else {
            CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
            if (customerCompany == null) {
                throw new CompanyNotExistException("Company " + companyId + " does not exist");
            }
            customerCompany.setLockedOrNot(Boolean.TRUE);
            return true;
        }
    }

    @Override
    public CustomerCompany createCustomerCompany(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException {
        CustomerCompany customerCompany = null;
        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1");
        query.setParameter(1, companyName);
        ArrayList<Company> customerCompanyList = new ArrayList(query.getResultList());
        if (!customerCompanyList.isEmpty()) {
            if (customerCompanyList.get(0).getDeleteOrNot()) {
                throw new CompanyExistException("Company " + companyName + " is already exist and customer company is DELETED!");
            }
            throw new CompanyExistException("Company " + companyName + " is already exist!");
        }

        customerCompany = new CustomerCompany(companyName, companyType, contactNo);
        customerCompany.setVAT(VAT);
        em.persist(customerCompany);
        em.flush();
        System.out.println("In creating customer company - " + customerCompany.getCompanyName() + ", account ID: " + customerCompany.getId());
        return customerCompany;
    }

    @Override
    public CustomerCompany registerCustomerCompany(String companyName, String companyType, String contactNo, 
            String country, String state, String city, String street, String blockNo, String postalCode, String VAT) throws CompanyExistException {
        CustomerCompany customerCompany = null;
        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1");
        query.setParameter(1, companyName);
        ArrayList<Company> customerCompanyList = new ArrayList(query.getResultList());
        if (!customerCompanyList.isEmpty()) {
            if (customerCompanyList.get(0).getDeleteOrNot()) {
                throw new CompanyExistException("Company " + companyName + " is already exist and customer company is DELETED!");
            }
            throw new CompanyExistException("Company " + companyName + " is already exist!");
        }

        customerCompany = new CustomerCompany(companyName, companyType, contactNo, Boolean.FALSE);
        Location location = new Location(country, state, city, street, blockNo, postalCode);
        customerCompany.setLocation(location);
        customerCompany.setVAT(VAT);
        em.persist(customerCompany);
        em.flush();
        System.out.println("In creating customer company - " + customerCompany.getCompanyName() + ", account ID: " + customerCompany.getId());
        return customerCompany;
    }

    @Override
    public CustomerCompany updateCustomerCompany(Long companyId, String companyName, String companyType, String contactNo,
            String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException {
//        CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
        System.out.println("inside sessionbean");
        Query query = em.createQuery("select c from CustomerCompany c where c.id=?1 and c.lockedOrNot='false' and c.deleteOrNot='false'");
        query.setParameter(1, companyId);
        ArrayList<CustomerCompany> customerCompanyList = new ArrayList(query.getResultList());
        if (customerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyName + " does not exist!");
        }
        CustomerCompany customerCompany = customerCompanyList.get(0);
        if (!customerCompany.getCompanyName().equals(companyName)) {
            customerCompany.setCompanyName(companyName);
        }
        if (!customerCompany.getCompanyType().equals(companyType)) {
            customerCompany.setCompanyType(companyType);
        }
        if (!customerCompany.getContactNo().equals(contactNo)) {
            customerCompany.setContactNo(contactNo);
        }
        if (customerCompany.getLocation() == null) {
            Location location = new Location(country, state, city, street, blockNo, postalCode);
            customerCompany.setLocation(location);
        } else {
            if (customerCompany.getLocation().getCountry() == null || (customerCompany.getLocation().getCountry() != null && !customerCompany.getLocation().getCountry().equals(country))) {
                customerCompany.getLocation().setCountry(country);
                System.out.println("country is " + country);
            }
            if (customerCompany.getLocation().getState() == null || (customerCompany.getLocation().getState() != null && !customerCompany.getLocation().getState().equals(state))) {
                customerCompany.getLocation().setState(state);
            }
            if (customerCompany.getLocation().getCity() == null || (customerCompany.getLocation().getCity() != null && !customerCompany.getLocation().getCity().equals(city))) {
                customerCompany.getLocation().setCity(city);
            }
            if (customerCompany.getLocation().getStreet() == null || (customerCompany.getLocation().getStreet() != null && !customerCompany.getLocation().getStreet().equals(street))) {
                customerCompany.getLocation().setStreet(street);
            }
            if (customerCompany.getLocation().getBlockNo() == null || (customerCompany.getLocation().getBlockNo() != null && !customerCompany.getLocation().getBlockNo().equals(blockNo))) {
                customerCompany.getLocation().setBlockNo(blockNo);
            }
            if (customerCompany.getLocation().getPostalCode() == null || (customerCompany.getLocation().getPostalCode() != null && !customerCompany.getLocation().getPostalCode().equals(postalCode))) {
                customerCompany.getLocation().setPostalCode(postalCode);
            }
        }
        em.merge(customerCompany);
        em.flush();
        System.out.println("In updating customer company - " + customerCompany.getCompanyName() + ", account ID: " + customerCompany.getId());
        return customerCompany;
    }

    @Override
    public Boolean deleteCustomerCompany(Long companyId) throws CompanyNotExistException {
//        CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
        Query query = em.createQuery("select c from CustomerCompany c where c.id=?1");
        query.setParameter(1, companyId);
        ArrayList<CustomerCompany> customerCompanyList = new ArrayList(query.getResultList());
        if (customerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyId + " does not exist!");
        }
        CustomerCompany customerCompany = customerCompanyList.get(0);
        try {
            customerCompany.setDeleteOrNot(Boolean.TRUE);
            em.merge(customerCompany);
            em.flush();
            System.out.println("In deleting customer company - " + customerCompany.getCompanyName() + ", account ID: " + customerCompany.getId());
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public PartnerCompany createPartnerCompany(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException {
        PartnerCompany partnerCompany = null;
        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1");
        query.setParameter(1, companyName);
        ArrayList<Company> partnerCompanyList = new ArrayList(query.getResultList());
        if (!partnerCompanyList.isEmpty()) {
            if (partnerCompanyList.get(0).getDeleteOrNot()) {
                throw new CompanyExistException("Company " + companyName + " is already exist and partner company is DELETED!");
            }
            throw new CompanyExistException("Company " + companyName + " is already exist!");
        }

        partnerCompany = new PartnerCompany(companyName, companyType, contactNo);
        partnerCompany.setVAT(VAT);
        em.persist(partnerCompany);
        em.flush();
        System.out.println("In creating partner company - " + partnerCompany.getCompanyName() + ", account ID: " + partnerCompany.getId());
        return partnerCompany;
    }

    @Override
    public PartnerCompany registerPartnerCompany(String companyName, String companyType, String contactNo, 
            String country, String state, String city, String street, String blockNo, String postalCode, String VAT) throws CompanyExistException {
        PartnerCompany partnerCompany = null;
        Query query = em.createQuery("select c from CustomerCompany c where c.companyName=?1");
        query.setParameter(1, companyName);
        ArrayList<Company> partnerCompanyList = new ArrayList(query.getResultList());
        if (!partnerCompanyList.isEmpty()) {
            if (partnerCompanyList.get(0).getDeleteOrNot()) {
                throw new CompanyExistException("Company " + companyName + " is already exist and partner company is DELETED!");
            }
            throw new CompanyExistException("Company " + companyName + " is already exist!");
        }

        partnerCompany = new PartnerCompany(companyName, companyType, contactNo, Boolean.FALSE);
        Location location = new Location(country, state, city, street, blockNo, postalCode);
        partnerCompany.setLocation(location);
        partnerCompany.setVAT(VAT);
        em.persist(partnerCompany);
        em.flush();
        System.out.println("In creating partner company - " + partnerCompany.getCompanyName() + ", account ID: " + partnerCompany.getId());
        return partnerCompany;
    }

    @Override
    public PartnerCompany updatePartnerCompany(Long companyId, String companyName, String companyType, String contactNo,
            String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException {
//        CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
        Query query = em.createQuery("select c from PartnerCompany c where c.id=?1 and c.lockedOrNot='false' and c.deleteOrNot='false'");
        query.setParameter(1, companyId);
        ArrayList<PartnerCompany> partnerCompanyList = new ArrayList(query.getResultList());
        if (partnerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyName + " does not exist!");
        }
        PartnerCompany partnerCompany = partnerCompanyList.get(0);
        if (!partnerCompany.getCompanyName().equals(companyName)) {
            partnerCompany.setCompanyName(companyName);
        }
        if (!partnerCompany.getCompanyType().equals(companyType)) {
            partnerCompany.setCompanyType(companyType);
        }
        if (!partnerCompany.getContactNo().equals(contactNo)) {
            partnerCompany.setContactNo(contactNo);
        }
        if (partnerCompany.getLocation() == null) {
            Location location = new Location(country, state, city, street, blockNo, postalCode);
            partnerCompany.setLocation(location);
        } else {
            if (partnerCompany.getLocation().getCountry() == null || (partnerCompany.getLocation().getCountry() != null && !partnerCompany.getLocation().getCountry().equals(country))) {
                partnerCompany.getLocation().setCountry(country);
            }
            if (partnerCompany.getLocation().getState() == null || (partnerCompany.getLocation().getState() != null && !partnerCompany.getLocation().getState().equals(state))) {
                partnerCompany.getLocation().setState(state);
            }
            if (partnerCompany.getLocation().getCity() == null || (partnerCompany.getLocation().getCity() != null && !partnerCompany.getLocation().getCity().equals(city))) {
                partnerCompany.getLocation().setCity(city);
            }
            if (partnerCompany.getLocation().getStreet() == null || (partnerCompany.getLocation().getStreet() != null && !partnerCompany.getLocation().getStreet().equals(street))) {
                partnerCompany.getLocation().setStreet(street);
            }
            if (partnerCompany.getLocation().getBlockNo() == null || (partnerCompany.getLocation().getBlockNo() != null && !partnerCompany.getLocation().getBlockNo().equals(blockNo))) {
                partnerCompany.getLocation().setBlockNo(blockNo);
            }
            if (partnerCompany.getLocation().getPostalCode() == null || (partnerCompany.getLocation().getPostalCode() != null && !partnerCompany.getLocation().getPostalCode().equals(postalCode))) {
                partnerCompany.getLocation().setPostalCode(postalCode);
            }
        }
        em.merge(partnerCompany);
        em.flush();
        System.out.println("In updating partner company - " + partnerCompany.getCompanyName() + ", account ID: " + partnerCompany.getId());
        return partnerCompany;
    }

    @Override
    public Boolean deletePartnerCompany(Long companyId) throws CompanyNotExistException {
//        CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
        Query query = em.createQuery("select c from CustomerCompany c where c.id=?1");
        query.setParameter(1, companyId);
        ArrayList<PartnerCompany> partnerCompanyList = new ArrayList(query.getResultList());
        if (partnerCompanyList.isEmpty()) {
            throw new CompanyNotExistException("Company " + companyId + " does not exist!");
        }
        PartnerCompany partnerCompany = partnerCompanyList.get(0);
        try {
            partnerCompany.setDeleteOrNot(Boolean.TRUE);
            em.merge(partnerCompany);
            em.flush();
            System.out.println("In deleting customer company - " + partnerCompany.getCompanyName() + ", account ID: " + partnerCompany.getId());
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public Department createDepartment(String departmentName, Company company) throws DepartmentExistException {
        Query query = em.createQuery("select d from Department d where d.departmentName=?1 and d.company=?2");
        query.setParameter(1, departmentName);
        query.setParameter(2, company);
        ArrayList<Department> departmentList = new ArrayList(query.getResultList());
        if (departmentList.isEmpty()) {
            System.out.println("List is empty");
        }
        if (!departmentList.isEmpty()) {
            System.out.println("List is not empty");
            throw new DepartmentExistException("Department " + departmentName + " for company " + company.getCompanyName() + " is already exist");
        }
        Department department = new Department(departmentName, company);
        company.getDepartment().add(department);
        
        em.persist(department);
        em.merge(company);
        System.out.println("In creating department - " + department.getDepartmentName() + " for company " + company.getCompanyName());
        return department;
    }

    @Override
    public Department registerDepartment(String departmentName, Company company) throws DepartmentExistException {
        Query query = em.createQuery("select d from Department d where d.departmentName=?1 and d.company=?2");
        query.setParameter(1, departmentName);
        query.setParameter(2, company);
        ArrayList<Department> departmentList = new ArrayList(query.getResultList());
        if (departmentList.isEmpty()) {
            System.out.println("List is empty");
        }
        if (!departmentList.isEmpty()) {
            System.out.println("List is not empty");
            throw new DepartmentExistException("Department " + departmentName + " for company " + company.getCompanyName() + " is already exist");
        }
        Department department = new Department(departmentName, company, Boolean.FALSE);
        company.getDepartment().add(department);
        
        em.persist(department);
        em.merge(company);
        System.out.println("In creating department - " + department.getDepartmentName() + " for company " + company.getCompanyName());
        return department;
    }

    @Override
    public Boolean deleteDepartment(String departmentName, Company company) throws DepartmentNotExistException, CompanyNotExistException {
        if (em.find(Company.class, company.getId()) == null) {
            throw new CompanyNotExistException("Company " + company.getCompanyName() + " does not exist!");
        }
        Query query = em.createQuery("select d from Department d where d.departmentName=?1 and d.company=?2");
        query.setParameter(1, departmentName);
        query.setParameter(2, company);
        ArrayList<Department> departmentList = new ArrayList(query.getResultList());
        if (!departmentList.isEmpty()) {
            throw new DepartmentNotExistException("Department " + departmentName + " for company " + company.getCompanyName() + " does not already exist");
        }
        Department department = em.find(Department.class, departmentList.get(0).getId());
        try {
            em.remove(department);
            em.flush();
            System.out.println("In deleting department - " + department.getDepartmentName() + " for company " + company.getCompanyName());
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public Department retrieveDepartment(String departmentName, Company company) throws DepartmentNotExistException {
        Query query = em.createQuery("select d from Department d where d.departmentName=?1 and d.company=?2");
        query.setParameter(1, departmentName);
        query.setParameter(2, company);
        ArrayList<Department> departmentList = new ArrayList(query.getResultList());
        if (departmentList.isEmpty()) {
            throw new DepartmentNotExistException("Department " + departmentName + " for company " + company.getCompanyName() + " does not exist");
        }
        Department department = departmentList.get(0);
        return department;
    }

    @Override
    public Department retrieveDepartment(Long departmentId) throws DepartmentNotExistException {
        Department department = em.find(Department.class, departmentId);
        if (department == null) {
            throw new DepartmentNotExistException("Department " + departmentId + " does not exist!");
        }
        return department;
    }

    @Override
    public boolean approveRequest(Long accountId, Long userId, Long companyId, String companyType) {
        System.out.println("Inside session bean, approve request");
        CompanyAdminAccount companyAdminAccount = em.find(CompanyAdminAccount.class, accountId);
        companyAdminAccount.setApprovedOrNot(true);
        CompanyAdmin companyAdmin = em.find(CompanyAdmin.class, userId);
        companyAdmin.setApprovedOrNot(true);
        String firstName = companyAdmin.getFirstName();
        String lastName = companyAdmin.getLastName();
        String email = companyAdmin.getEmail();
        String username = companyAdminAccount.getUsername();
        Company company = companyAdminAccount.getCompany();
        if (companyType.equals("1PL")) {
            CustomerCompany customerCompany = em.find(CustomerCompany.class, companyId);
            if (customerCompany == null) {
                System.out.println("Company not found");
            }
            customerCompany.setApprovedOrNot(true);
            List<Department> departmentList = customerCompany.getDepartment();
            if (departmentList == null) {
                System.out.println("Department list is not found");
            }
            for (Department d : departmentList) {
                d.setApprovedOrNot(true);
                List<Title> titleList = d.getTitles();
                if (!titleList.isEmpty()) {
                    for (Title t : titleList) {
                        t.setApprovedOrNot(true);
                    }
                }
            }
            try {
                SendEmail(firstName, lastName, username, email, company.getCompanyName());
            } catch (MessagingException ex) {
                System.out.println("Error sending email.");
                Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        } else if (companyType.equals("2PL") || companyType.equals("3PL") || companyType.equals("4PL")) {
            PartnerCompany partnerCompany = em.find(PartnerCompany.class, companyId);
            partnerCompany.setApprovedOrNot(true);
            List<Department> departmentList = partnerCompany.getDepartment();
            for (Department d : departmentList) {
                d.setApprovedOrNot(true);
                List<Title> titleList = d.getTitles();
                if (!titleList.isEmpty()) {
                    for (Title t : titleList) {
                        t.setApprovedOrNot(true);
                    }
                }
            }
            try {
                SendEmail(firstName, lastName, username, email, company.getCompanyName());
            } catch (MessagingException ex) {
                System.out.println("Error sending email.");
                Logger.getLogger(AccountManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }

    private void SendEmail(String firstName, String lastName, String username, String email, String companyName) throws MessagingException {
        String subject = "Merlion Platform - Account \"" + username + "\" Approved!";
        System.out.println("Inside send email");

        String content = "<h2>Dear " + firstName + " " + lastName
                + ",</h2><br /><h1>  Congratulations! Your account has been successfully approved on MerLION Platform!</h1><br />"
                + "<h2 align=\"center\">Username: " + username
                + "<br />Company: " + companyName + "</h2><br />"
                + "<p style=\"color: #ff0000;\">Please remember to change your password on your first login. Thank you.</p>"
                + "<br /><p>Note: Please do not reply this email. If you have further questions, please go to the contact form page and submit there.</p>"
                + "<p>Thank you.</p><br /><br /><p>Regards,</p><p>MerLION Platform User Support</p>";
        System.out.println(content);
        sendEmail.run(email, subject, content);
    }

    //!!!ATTENTION!!! Only for pupolating data usage!!!
    @Override
    public Company createMerLION(String companyName, String companyType, String contactNo, String VAT) throws CompanyExistException {
        MerLION merlion = new MerLION(companyName, companyType, contactNo);
        merlion.setVAT(VAT);
        em.persist(merlion);
        em.flush();
        System.out.println("In creating merlion - " + merlion.getCompanyName() + ", account ID: " + merlion.getId());
        return merlion;
    }

    @Override
    public Company updateMerLION(Long companyId, String companyName, String companyType, String contactNo,
            String country, String state, String city, String street, String blockNo, String postalCode) throws CompanyNotExistException {
        MerLION merlion = em.find(MerLION.class, companyId);

        if (!merlion.getContactNo().equals(contactNo)) {
            merlion.setContactNo(contactNo);
        }

        if (merlion.getLocation() == null) {
            Location location = new Location(country, state, city, street, blockNo, postalCode);
            merlion.setLocation(location);
        } else {
            if (merlion.getLocation().getCountry() == null || (merlion.getLocation().getCountry() != null && !merlion.getLocation().getCountry().equals(country))) {
                merlion.getLocation().setCountry(country);
            }
            if (merlion.getLocation().getState() == null || (merlion.getLocation().getState() != null && !merlion.getLocation().getState().equals(state))) {
                merlion.getLocation().setState(state);
            }
            if (merlion.getLocation().getCity() == null || (merlion.getLocation().getCity() != null && !merlion.getLocation().getCity().equals(city))) {
                merlion.getLocation().setCity(city);
            }
            if (merlion.getLocation().getStreet() == null || (merlion.getLocation().getStreet() != null && !merlion.getLocation().getStreet().equals(street))) {
                merlion.getLocation().setStreet(street);
            }
            if (merlion.getLocation().getBlockNo() == null || (merlion.getLocation().getBlockNo() != null && !merlion.getLocation().getBlockNo().equals(blockNo))) {
                merlion.getLocation().setBlockNo(blockNo);
            }
            if (merlion.getLocation().getPostalCode() == null || (merlion.getLocation().getPostalCode() != null && !merlion.getLocation().getPostalCode().equals(postalCode))) {
                merlion.getLocation().setPostalCode(postalCode);
            }
        }
        em.merge(merlion);
        em.flush();
        System.out.println("In updating merlion - " + merlion.getCompanyName() + ", account ID: " + merlion.getId());
        return merlion;
    }

    @Override
    public List<Company> retrieveAllCompany() {
        Query query = em.createQuery("SELECT c FROM CustomerCompany c WHERE c.lockedOrNot='false'");
        Query query2 = em.createQuery("SELECT c FROM PartnerCompany c WHERE c.lockedOrNot='false'");
        List<Company> companyList = new ArrayList(query.getResultList());
        companyList.addAll(query2.getResultList());
        return companyList;
    }

    @Override
    public List<Company> retrieveLockedCompany() {
        Query query = em.createQuery("SELECT c FROM CustomerCompany c WHERE c.lockedOrNot='true'");
        Query query2 = em.createQuery("SELECT c FROM PartnerCompany c WHERE c.lockedOrNot='true'");
        List<Company> companyList = new ArrayList(query.getResultList());
        companyList.addAll(query2.getResultList());
        return companyList;
    }
}
