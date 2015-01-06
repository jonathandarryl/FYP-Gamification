/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package Common.entity;

import GRNS.entity.AggregationPost;
import GRNS.entity.BiddingRecord;
import GRNS.entity.StoragePost;
import GRNS.entity.TransportPost;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author chongyangsun
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String contactNo;
    private String password;
    private String salt;
    private String accountType;
    private boolean lockedOrNot;
    private boolean deleteOrNot;
    private boolean approvedOrNot;
    
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateOfIssue;
    
    @OneToMany(cascade={CascadeType.PERSIST},mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();
    @OneToMany(cascade={CascadeType.PERSIST},mappedBy = "account")
    private List<Announcement> announcements = new ArrayList<>();
    @OneToMany(mappedBy = "sender")
    private List<Notification> notifications;
    @OneToMany(mappedBy = "account")
    private List<NotificationRecord> notificationRecords;
    
    @OneToOne(mappedBy="account")
    private User user;
    @ManyToOne
    private Company company;
    @ManyToOne
    private Department department;
    
    @OneToMany(mappedBy="account")
    private List<AggregationPost> aggregationPosts;
    @OneToMany(mappedBy="account")
    private List<StoragePost> storagePosts;
    @OneToMany(mappedBy="account")
    private List<TransportPost> transportPosts;
    @OneToMany(mappedBy="account")
    private List<BiddingRecord> biddingRecords;
    
    public Account(){
    }
    
    public Account(String username, String password, String salt, Company company, Department department, String accountType) {
        this.setUsername(username);
        this.setPassword(password);
        this.setSalt(salt);
        this.setLockedOrNot(Boolean.FALSE);
        Date dateOfIssue = new Date();
        this.setDateOfIssue(dateOfIssue);
        this.setCompany(company);
        this.setDepartment(department);
        this.setApprovedOrNot(Boolean.TRUE);
        this.setAccountType(accountType);
// To output the date in formatted manner        
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
    }
    
    public Account(String username, String password, String salt, Company company, Department department, String accountType, Boolean register) {
        this.setUsername(username);
        this.setPassword(password);
        this.setSalt(salt);
        this.setLockedOrNot(Boolean.FALSE);
        Date dateOfIssue = new Date();
        this.setDateOfIssue(dateOfIssue);
        this.setCompany(company);
        this.setDepartment(department);
        this.setApprovedOrNot(register);
        this.setAccountType(accountType);
// To output the date in formatted manner        
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the dateOfIssue
     */
    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    /**
     * @param dateOfIssue the dateOfIssue to set
     */
    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    /**
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * @return the announcements
     */
    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * @param announcements the announcements to set
     */
    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * @return the notifications
     */
    public List<Notification> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the notificationRecords
     */
    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    /**
     * @param notificationRecords the notificationRecords to set
     */
    public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
        this.notificationRecords = notificationRecords;
    }

    /**
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * @return the lockedOrNot
     */
    public boolean isLockedOrNot() {
        return lockedOrNot;
    }

    /**
     * @param lockedOrNot the lockedOrNot to set
     */
    public void setLockedOrNot(boolean lockedOrNot) {
        this.lockedOrNot = lockedOrNot;
    }

    /**
     * @return the deleteOrNot
     */
    public boolean isDeleteOrNot() {
        return deleteOrNot;
    }

    /**
     * @param deleteOrNot the deleteOrNot to set
     */
    public void setDeleteOrNot(boolean deleteOrNot) {
        this.deleteOrNot = deleteOrNot;
    }

    /**
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the approvedOrNot
     */
    public boolean isApprovedOrNot() {
        return approvedOrNot;
    }

    /**
     * @param approvedOrNot the approvedOrNot to set
     */
    public void setApprovedOrNot(boolean approvedOrNot) {
        this.approvedOrNot = approvedOrNot;
    }

    /**
     * @return the biddingRecords
     */
    public List<BiddingRecord> getBiddingRecords() {
        return biddingRecords;
    }

    /**
     * @param biddingRecords the biddingRecords to set
     */
    public void setBiddingRecords(List<BiddingRecord> biddingRecords) {
        this.biddingRecords = biddingRecords;
    }

    /**
     * @return the aggregationPosts
     */
    public List<AggregationPost> getAggregationPosts() {
        return aggregationPosts;
    }

    /**
     * @param aggregationPosts the aggregationPosts to set
     */
    public void setAggregationPosts(List<AggregationPost> aggregationPosts) {
        this.aggregationPosts = aggregationPosts;
    }

    /**
     * @return the storagePosts
     */
    public List<StoragePost> getStoragePosts() {
        return storagePosts;
    }

    /**
     * @param storagePosts the storagePosts to set
     */
    public void setStoragePosts(List<StoragePost> storagePosts) {
        this.storagePosts = storagePosts;
    }

    /**
     * @return the transportPosts
     */
    public List<TransportPost> getTransportPosts() {
        return transportPosts;
    }

    /**
     * @param transportPosts the transportPosts to set
     */
    public void setTransportPosts(List<TransportPost> transportPosts) {
        this.transportPosts = transportPosts;
    }

    /**
     * @return the accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the contactNo
     */
    public String getContactNo() {
        return contactNo;
    }

    /**
     * @param contactNo the contactNo to set
     */
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

}
