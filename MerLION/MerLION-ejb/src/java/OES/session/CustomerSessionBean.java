/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.CustomerCompany;
import Common.entity.Location;
import OES.entity.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerAlreadyExistException;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;

/**
 *
 * @author songhan
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanLocal, CustomerSessionBeanRemote {

    @PersistenceContext
    EntityManager em;

    @Override
    public void createCustomer(String name, String email, String contactNo, Location location, CustomerCompany ownerCompany)
    throws CustomerAlreadyExistException, CustomerNotExistException, MultipleCustomerWithSameNameException{
        Customer customer = null;
        try {
            customer = retrieveCustomer(name, ownerCompany.getCompanyName());
        } catch (CustomerNotExistException ex) {
            System.out.println("No existing customer with the same name as " + name + ". New customer creation proceeds.");
        } catch (MultipleCustomerWithSameNameException ex) {
            System.out.println("There are multiple customers with the same name.");
        }
        if (customer != null) {
            throw new CustomerAlreadyExistException("Customer " + name + " already exist in Company " + ownerCompany + "'s customer list. Creation failed.");
        }

        customer = new Customer(name, email, contactNo, location, ownerCompany);
        em.persist(customer);
    }

    @Override
    public void updateCustomer(String name, String email, String contactNo, Location location, Customer customer) {
        customer.setName(name);
        customer.setContactNo(contactNo);
        customer.setEmail(email);
        customer.setLocation(location);
        em.merge(customer);
    }

    @Override
    public void deleteCustomer(Customer customer) {
        customer.setArchivedOrNot(Boolean.TRUE);
        em.merge(customer);
    }

    @Override
    public List<Customer> retrieveCustomerList(CustomerCompany company) {
        Query query = em.createQuery("select c from Customer c where c.seller=?1 and c.archivedOrNot=FALSE");
        query.setParameter(1, company);
        List<Customer> customerList = new ArrayList(query.getResultList());
        return customerList;
    }

    @Override
    public Customer retrieveCustomer(String customerName, String ownerCompany) throws CustomerNotExistException, MultipleCustomerWithSameNameException {
        Query query = em.createQuery("select c from Customer c where c.name=?1 and c.seller.companyName=?2 and c.archivedOrNot=FALSE");
        query.setParameter(1, customerName);
        query.setParameter(2, ownerCompany);
        ArrayList<Customer> customerList = new ArrayList(query.getResultList());
        if (customerList.isEmpty()) {
            System.out.println("empty");
            throw new CustomerNotExistException("Customer " + customerName + " does not exist in Company " + ownerCompany + "'s product list.");
        }

        if (customerList.size() > 1) {
            System.out.println("not empty");
            throw new MultipleCustomerWithSameNameException("Error! Multiple customers with the same name under the same company exist!");
        }
        System.out.println("out");
        Customer customer = customerList.get(0);
        System.out.println("going to return");
        return customer;
    }
}
