/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.KeyClient;
import CRM.entity.MembershipSchema;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
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
public class MembershipSchemaSessionBeanTest {
    
    MembershipSchemaSessionBeanRemote mssbr = this.lookupMembershipSchemaSessionBeanRemote();
    
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
    public void testCreateSchema() throws CompanyNotExistException {
        Long ownerId = Long.valueOf("99");
        String membershipTier = "testmember";
        Integer boundaryPoint = 20;
        Double discount = 0.20;
        String result = mssbr.createMembershipSchema(ownerId, membershipTier, boundaryPoint, discount);
        result = "not created";
        assertNotNull(result);
    }
    
    @Test
    public void testViewAllSchema() throws CompanyNotExistException {
        Long ownerId = Long.valueOf("21");
        List<MembershipSchema> result = mssbr.viewAllMembershipSchema(ownerId);
        assertNotNull(result);
    }
    
    @Test(expected = MembershipSchemaNotExistException.class)
    public void testViewSchema() throws CompanyNotExistException, MembershipSchemaNotExistException {
        Long ownerId = Long.valueOf("21");
        Long Id = Long.valueOf("999");
        MembershipSchema ms = mssbr.viewMembershipSchema(ownerId, Id);
        String result = "no ms";
        assertNotNull(result);
    }
    
    @Test(expected = MembershipSchemaNotExistException.class)
    public void testDeleteSchema() throws CompanyNotExistException, MembershipSchemaNotExistException {
        Long ownerId = Long.valueOf("21");
        Long Id = Long.valueOf("999");
        String result = mssbr.deleteMembershipSchema(ownerId, Id);
        assertNotNull(result);
    }
    
    private MembershipSchemaSessionBeanRemote lookupMembershipSchemaSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (MembershipSchemaSessionBeanRemote)c.lookup("java:global/MerLION/MerLION-ejb/MembershipSchemaMgtSessionBean!CRM.session.MembershipSchemaSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
