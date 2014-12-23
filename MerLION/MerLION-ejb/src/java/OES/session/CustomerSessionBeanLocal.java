/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OES.session;

import Common.entity.CustomerCompany;
import Common.entity.Location;
import OES.entity.Customer;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerAlreadyExistException;
import util.exception.CustomerNotExistException;
import util.exception.MultipleCustomerWithSameNameException;

/**
 *
 * @author songhan
 */
@Local
public interface CustomerSessionBeanLocal {

    public void createCustomer(String name, String email, String contactNo, Location location, CustomerCompany company)
            throws CustomerAlreadyExistException, CustomerNotExistException, MultipleCustomerWithSameNameException;

    public void updateCustomer(String name, String email, String contactNo, Location location, Customer customer);

    public void deleteCustomer(Customer customer);

    public List<Customer> retrieveCustomerList(CustomerCompany company);
    
    public Customer retrieveCustomer(String customerName, String ownerCompany)
            throws CustomerNotExistException, MultipleCustomerWithSameNameException;
}
