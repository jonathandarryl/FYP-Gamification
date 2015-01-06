/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.session;

import CRM.entity.SubscriptionSchema;
import java.util.List;
import javax.ejb.Local;
import util.exception.CompanyNotExistException;
import util.exception.SubscriptionSchemaNotExistException;

/**
 *
 * @author HanXiangyu
 */
@Local
public interface SubscriptionSchemaMgtSessionBeanLocal {
    //B.6.a.1
    public String createSubscriptionSchema(Long ownerId, Integer numOfYears, Double boundaryPrice) throws CompanyNotExistException;
    //B.6.a.2
    public String updateSubscriptionSchema(Long ownerId, Long Id, Integer numOfYears, Double boundaryPrice)throws CompanyNotExistException, SubscriptionSchemaNotExistException;
    //B.6.a.3
    public List<SubscriptionSchema> viewAllSubscriptionSchema(Long ownerId) throws CompanyNotExistException, SubscriptionSchemaNotExistException;

    public SubscriptionSchema viewSubscriptionSchema(Long ownerId, Long Id) throws CompanyNotExistException, SubscriptionSchemaNotExistException;

    public String deleteSubscriptionSchema(Long ownerId, Long Id) throws CompanyNotExistException, SubscriptionSchemaNotExistException;

    public List<String> getPriceList() throws SubscriptionSchemaNotExistException;

}
