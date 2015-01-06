/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CRM.entity;

import Common.entity.Company;
import Common.entity.PartnerCompany;
import GRNS.entity.AggregatedOrder;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import util.xmladapter.TimestampAdapter;

/**
 *
 * @author HanXiangyu
 */
@Entity
public class ServiceContract implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp creationTime;
    @OneToOne
    private ServiceQuotation serviceQuotation;
    @OneToMany(mappedBy="serviceContract")
    private List<ServiceOrder> serviceOrder;
    @ManyToOne
    private Company client;
    @ManyToOne
    private Company provider;

    private Boolean clientSignOrNot;
    // this is for GRNS order
    private Boolean fromGRNS;
    
    @ManyToOne
    private AggregatedOrder aggregatedOrder;
    
    private String postType;
    private Long postId;
    

    public ServiceContract() {
    }

    public ServiceContract(ServiceQuotation serviceQuotation) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.serviceQuotation = serviceQuotation;
        this.client = serviceQuotation.getClientCompany();
        this.provider = serviceQuotation.getpCompany();
        this.clientSignOrNot = Boolean.FALSE;
        this.setFromGRNS(Boolean.FALSE);
        this.setPostId(null);
        this.setPostType(null);
    }
    
    // for GRNS order creation
    public ServiceContract(Company client, Company provider, Long postId, String postType) {
        this.creationTime = new Timestamp(System.currentTimeMillis());
        this.setServiceQuotation(null);
        this.setClient(client);
        this.setProvider(provider);
        this.setClientSignOrNot(Boolean.TRUE);
        this.setFromGRNS(Boolean.TRUE);
        this.setPostId(postId);
        this.setPostType(postType);
    }
    
    public Company getClient() {
        return client;
    }

    public void setClient(Company Client) {
        this.client = Client;
    }

    public Company getProvider() {
        return provider;
    }

    public void setProvider(Company provider) {
        this.provider = provider;
    }


    public List<ServiceOrder> getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(List<ServiceOrder> serviceOrder) {
        this.serviceOrder = serviceOrder;
    }
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public ServiceQuotation getServiceQuotation() {
        return serviceQuotation;
    }

    public void setServiceQuotation(ServiceQuotation serviceQuotation) {
        this.serviceQuotation = serviceQuotation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getClientSignOrNot() {
        return clientSignOrNot;
    }

    public void setClientSignOrNot(Boolean clientSignOrNot) {
        this.clientSignOrNot = clientSignOrNot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceContract)) {
            return false;
        }
        ServiceContract other = (ServiceContract) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CRM.entity.ServiceContract[ id=" + id + " ]";
    }

    /**
     * @return the aggregatedOrder
     */
    public AggregatedOrder getAggregatedOrder() {
        return aggregatedOrder;
    }

    /**
     * @param aggregatedOrder the aggregatedOrder to set
     */
    public void setAggregatedOrder(AggregatedOrder aggregatedOrder) {
        this.aggregatedOrder = aggregatedOrder;
    }

    /**
     * @return the fromGRNS
     */
    public Boolean getFromGRNS() {
        return fromGRNS;
    }

    /**
     * @param fromGRNS the fromGRNS to set
     */
    public void setFromGRNS(Boolean fromGRNS) {
        this.fromGRNS = fromGRNS;
    }

    /**
     * @return the postType
     */
    public String getPostType() {
        return postType;
    }

    /**
     * @param postType the postType to set
     */
    public void setPostType(String postType) {
        this.postType = postType;
    }

    /**
     * @return the postId
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
}
