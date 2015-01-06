/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.KeyClient;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.exception.CompanyNotExistException;
import util.exception.KeyClientNotExistException;
import util.exception.MembershipSchemaNotExistException;

/**
 *
 * @author Sun Mingjia
 */
public class ClientSessionBeanTest {
    
    ClientSessionBeanRemote csbr = this.lookupClientSessionBeanRemote();
    
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
    
    @Test(expected = CompanyNotExistException.class)
    public void testAddClient() throws CompanyNotExistException, MembershipSchemaNotExistException {
        Long Id = Long.valueOf("999");
        String companyName = "company1";
        String contactNo = "12345678";
        
        String result = csbr.addClientRecord(Id, companyName, contactNo);
        result = "no company";
        assertNotNull(result);
    }
    
    @Test
    public void testViewAllClient() {
//        Long ownerId = Long.valueOf("21");
//        List<KeyClient> kc = csbr.viewAllClientCompany(ownerId);
        String result = null;
        assertNull(result);
    }
    
    @Test(expected = KeyClientNotExistException.class)
    public void testViewClientCompany() throws CompanyNotExistException, KeyClientNotExistException {
        Long ownerId = Long.valueOf("21");
        Long Id = Long.valueOf("999");
        KeyClient result = csbr.viewClientCompany(ownerId, Id);
        assertNull(result);
    }

    @Test(expected = KeyClientNotExistException.class)
    public void testDeleteClient() throws CompanyNotExistException, KeyClientNotExistException {
        Long ownerId = Long.valueOf("21");
        Long Id = Long.valueOf("999");
        String result = csbr.deleteClientCompany(ownerId, Id);
        result = "no client";
        assertNotNull(result);
    }
    
    @Test(expected = KeyClientNotExistException.class)
    public void testAssignMembershipTier() throws CompanyNotExistException, KeyClientNotExistException {
        Long ownerId = Long.valueOf("21");
        Long Id = Long.valueOf("999");
        String result = csbr.assignMembershipTier(ownerId, Id);
        result = "no client";
        assertNotNull(result);
    }
    
    private ClientSessionBeanRemote lookupClientSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ClientSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/ClientInfoMgtSessionBean!CRM.session.ClientSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
