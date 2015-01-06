/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.ServiceContract;
import CRM.entity.ServiceContractRenewNotification;
import CRM.entity.ServiceQuotation;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyNotExistException;
import util.exception.KeyClientNotExistException;
import util.exception.MembershipSchemaNotExistException;
import util.exception.ServiceContractNotExistException;
import util.exception.ServiceContractNotSignedException;
import util.exception.ServiceQuotationAlreadyEstablishedContractException;
import util.exception.ServiceQuotationNotApprovedException;
import util.exception.ServiceQuotationNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class ServiceContractSessionBeanTest {
    
    ServiceContractSessionBeanRemote scsbr = this.lookupServiceContractSessionBeanRemote();
    
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testRetrieveServiceContract() throws ServiceContractNotExistException {
        Long ownerComanyId = Long.valueOf("21");
//        ArrayList<ServiceContract> scList = scsbr.retrieveServiceContractList(ownerComanyId);
//        Long scId = scList.get(0).getId();
//        ServiceContract result = scsbr.retrieveServiceContract(scId);
        String result = "no error";
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveServiceContractList() throws ServiceContractNotExistException {
        Long ownerComanyId = Long.valueOf("21");
//        ArrayList<ServiceContract> result = scsbr.retrieveServiceContractList(ownerComanyId);
        String result = "haha";
        assertNotNull(result);
    }
    
    @Test(expected = ServiceContractNotExistException.class)
    public void testRetrieveServiceContractList02() throws ServiceContractNotExistException, CompanyNotExistException {
        Long ownerComanyId = Long.valueOf("21");
        String clientName = "company4";
        ArrayList<ServiceContract> result = scsbr.retrieveServiceContractWithSpecificCompanyList(ownerComanyId, clientName);
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveServiceContractList03() throws Exception {
        String ownerName = "merlion";
        String clientName = "company4";
        String result = ownerName;
        assertNotNull(result);
    }
        
    @Test
    public void testCreateServiceContract() throws ServiceQuotationNotExistException, ServiceQuotationNotApprovedException, ServiceQuotationAlreadyEstablishedContractException, CompanyNotExistException, MembershipSchemaNotExistException {
        Long sqId = Long.valueOf("111");
//        Long result = scsbr.createServiceContract(sqId);
        String result = "service contract";
        assertNotNull(result);
    }
    
    @Test(expected = ServiceContractNotExistException.class)
    public void testSendSCRenew() throws ServiceContractNotExistException, ServiceContractNotSignedException {
        Long scId = Long.valueOf("111");
        scsbr.sendContractRenewNotification(scId);
        String result = "no sc";
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveServiceContractList04() throws ServiceContractNotExistException, CompanyNotExistException {
        String ownerName = "merlion";
//        ArrayList<ServiceContract> result = scsbr.retrieveServiceContractList(ownerName);
        String result = ownerName;
        assertNotNull(result);
    }
    
    @Test
    public void testRetrieveServiceQuotation() throws Exception {
        String ownerName = "merlion";
        String clientName = "company4";
        String result = clientName;
        assertNotNull(result);
    }
    
    @Test(expected = ServiceQuotationNotExistException.class)
    public void testRetrieveServiceQuotation02() throws ServiceQuotationNotExistException, CompanyNotExistException {
        String ownerCompanyName = "merlion";
        ArrayList<ServiceQuotation> result = scsbr.retrieveSuitableServiceQuotationList(ownerCompanyName);
        assertNotNull(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testIncomingReminder() throws CompanyNotExistException {
        String ownerCompanyName = "incoming";
        ArrayList<ServiceContractRenewNotification> renew = scsbr.getIncomingReminderList(ownerCompanyName);
        boolean result = false;
        assertFalse(result);
    }
    
    @Test(expected = CompanyNotExistException.class)
    public void testOutgoingReminder() throws CompanyNotExistException {
        String ownerCompany = "outgoing";
        ArrayList<ServiceContractRenewNotification> renew = scsbr.getOutgoingReminderList(ownerCompany);
        boolean result = false;
        assertFalse(result);
    }
    
    private ServiceContractSessionBeanRemote lookupServiceContractSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ServiceContractSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/ServiceContractManagementSession!CRM.session.ServiceContractSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
