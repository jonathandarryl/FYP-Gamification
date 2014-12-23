/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GRNSManagedBean;

import CRM.entity.ServiceOrder;
import GRNS.entity.AggregatedOrder;
import GRNS.entity.AggregationPost;
import GRNS.session.AggregationSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import util.exception.OrderNotEnoughException;
import util.exception.OrderNotMatchException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "aggregationManagedBean")
@SessionScoped
public class AggregationManagedBean implements Serializable {

    /**
     * Creates a new instance of AggregationManagedBean
     */
    public AggregationManagedBean() {
    }

    private List<ServiceOrder> allAggregationOrder;
    private List<ServiceOrder> aggregationCart;
    private List<AggregatedOrder> aggregatedOrder;
    private ServiceOrder selectedSo;
    private AggregatedOrder selectedAo;
    private AggregationPost ap;

    //Long aoId, Long accountId, Long companyId, String title, String content, 
//            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType
    private Long aoId;
    private Long accountId;
    private Long companyId;
    private String title;
    private String content;
    private Date date;
    private Timestamp endDateTime;
    private boolean reservePriceSpecified;
    private Double reservePrice;
    private String auctionType;
    private Double initBid;

    @EJB
    AggregationSessionBeanLocal asbl;

    @PostConstruct
    public void init() {
        allAggregationOrder = new ArrayList<>();
        selectedSo = new ServiceOrder();
        aggregationCart = new ArrayList<>();
        aggregatedOrder = new ArrayList<>();
        selectedAo = new AggregatedOrder();
        ap = new AggregationPost();
    }

    public void viewAllAggregationOrder() {
        System.out.println("Inside view all aggregation order");
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        this.allAggregationOrder = asbl.retrieveAggregationOrderList();
        System.out.println("Size of all service order is " + allAggregationOrder.size());
        if (allAggregationOrder.isEmpty()) {
            this.warnMsg("There is currently no service order to be aggregated");
        }
    }

    public void addToAggregateCart(Long soId) {
        System.out.println("Inside add to aggregate cart");
        System.out.println("Service order Id is " + soId);
        boolean checkAdd = asbl.addToAggregationCart(soId);
        if (checkAdd == true) {
            this.displayGrowl("Order successfully added to aggregation cart");
        } else {
            this.errorMsg("Order not added to aggregation cart");
        }
    }

    public void retrieveAggregationCart() throws IOException {
        System.out.println("Inside retrieve aggregation cart");
        aggregationCart = null;
        aggregationCart = asbl.retrieveAggregationCart();
        System.out.println("Size of aggregation cart is " + aggregationCart);

        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/aggregation/retrieveAggregationCart.xhtml");
    }

    public void removeAggregationCart(Long soId) {
        boolean checkRemove = asbl.removeAggregationCart(soId);

        if (checkRemove == true) {
            List<ServiceOrder> temp = new ArrayList();
            for (ServiceOrder s : aggregationCart) {
                if (!s.getId().equals(soId)) {
                    temp.add(s);
                }
            }
            aggregationCart = temp;
            this.displayGrowl("Order is successfully removed from aggregation cart");
        } else {
            this.errorMsg("Order not removed from aggregation cart");
        }
    }

    public void completeAggregationCart() throws IOException {
        System.out.println("Inside create complete aggregation cart");
        try {
            boolean checkComplete = asbl.completeAggregationCart();
            System.out.println("Boolean check complete is "+checkComplete);
            if (checkComplete == true) {
                this.displayGrowl("Aggregated order successfully created.");
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/aggregation/viewAggregatedOrder.xhtml");
            } else {
                this.errorMsg("Check out unsuccessful");
            }
        } catch (OrderNotMatchException ex) {
            this.errorMsg("These orders do not meet the requirements of aggregation. Please check again");
        } catch (OrderNotEnoughException ex) {
            this.errorMsg("There is insufficient number of service orders to be aggregated");
        }
    }

    public void viewAggregatedOrder() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        this.aggregatedOrder = asbl.retrieveAggregatedOrderList();
    }

    public void aggregationPostDetail(AggregatedOrder ao) throws IOException {
        //Long aoId, Long accountId, Long companyId, String title, String content, 
//            Timestamp endDateTime, Boolean reservePriceSpecified, Double reservePrice, String auctionType
        selectedAo = ao;
        aoId = ao.getId();
        initBid = ao.getPrice();
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        companyId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("companyID");
        FacesContext.getCurrentInstance().getExternalContext().redirect("/MerLION-war/GRNSWeb/aggregation/aggregationPostDetail.xhtml");
    }

    public void createAggregationPost() {
        endDateTime = new Timestamp(date.getTime());
        ap = asbl.postAggregatedOrder(aoId, accountId, companyId, title, content, endDateTime, reservePriceSpecified, reservePrice, auctionType);
        if (ap != null) {
            this.displayGrowl("Post successfully created");
        } else {
            this.errorMsg("Post not created. Please try again");
        }
    }

    private void displayGrowl(String response) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(response));
//        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public List<ServiceOrder> getAllAggregationOrder() {
        return allAggregationOrder;
    }

    public void setAllAggregationOrder(List<ServiceOrder> allAggregationOrder) {
        this.allAggregationOrder = allAggregationOrder;
    }

    public ServiceOrder getSelectedSo() {
        return selectedSo;
    }

    public void setSelectedSo(ServiceOrder selectedSo) {
        this.selectedSo = selectedSo;
    }

    public List<ServiceOrder> getAggregationCart() {
        return aggregationCart;
    }

    public void setAggregationCart(List<ServiceOrder> aggregationCart) {
        this.aggregationCart = aggregationCart;
    }

    public List<AggregatedOrder> getAggregatedOrder() {
        return aggregatedOrder;
    }

    public void setAggregatedOrder(List<AggregatedOrder> aggregatedOrder) {
        this.aggregatedOrder = aggregatedOrder;
    }

    public AggregatedOrder getSelectedAo() {
        return selectedAo;
    }

    public void setSelectedAo(AggregatedOrder selectedAo) {
        this.selectedAo = selectedAo;
    }

    public Long getAoId() {
        return aoId;
    }

    public void setAoId(Long aoId) {
        this.aoId = aoId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public AggregationPost getAp() {
        return ap;
    }

    public void setAp(AggregationPost ap) {
        this.ap = ap;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReservePriceSpecified() {
        return reservePriceSpecified;
    }

    public void setReservePriceSpecified(boolean reservePriceSpecified) {
        this.reservePriceSpecified = reservePriceSpecified;
    }

    public Double getInitBid() {
        return initBid;
    }

    public void setInitBid(Double initBid) {
        this.initBid = initBid;
    }

}
