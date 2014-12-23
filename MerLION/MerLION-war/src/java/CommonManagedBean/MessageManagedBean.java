/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonManagedBean;

import Common.entity.Account;
import Common.entity.Announcement;
import Common.entity.Company;
import Common.entity.Department;
import Common.entity.Message;
import Common.entity.Notification;
import Common.entity.NotificationRecord;
import Common.session.AccountManagementSessionBeanLocal;
import Common.session.AnnouncementSessionBeanLocal;
import Common.session.CompanyManagementSessionBeanLocal;
import Common.session.MessageSessionBeanLocal;
import Common.session.NotificationSessionBeanLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import Logger.MyLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import util.exception.AccountTypeNotExistException;
import util.exception.CompanyNotExistException;
import util.exception.UserNotExistException;

/**
 *
 * @author Sun Mingjia
 */
@Named(value = "messageManagedBean")
@SessionScoped
public class MessageManagedBean implements Serializable {

    /**
     * Creates a new instance of MessageManagedBean
     */
    public MessageManagedBean() {
        try {
            MyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        LOGGER.setLevel(Level.INFO);
    }

    @EJB
    MessageSessionBeanLocal msbl;
    @EJB
    AccountManagementSessionBeanLocal amsbl;
    @EJB
    CompanyManagementSessionBeanLocal cmsbl;
    @EJB
    AnnouncementSessionBeanLocal asbl;
    @EJB
    NotificationSessionBeanLocal nsbl;

    private Message message;
    private Message selectedMsg;
    private Announcement announcement;
    private Announcement selectedAnnouncement;
    private Announcement selectedAnnouncementSent;
    private Notification notification;
    private NotificationRecord selectedNotification;
    private String receiverName;
    private String receiverType;
    private Long ID;
    private String senderType;
    private Long receiverID;
    private Long msgID;
    private Long annID;
    private Long departmentID;
    private String title;
    private String content;
    private List<Message> allMessage;
    private List<Message> allUnreadMessage;
    private Department department;
    private List<Department> selectedDepartment;
    private List<Announcement> allAnnouncement;
    private List<Announcement> allAnnouncementSent;
    private List<NotificationRecord> allNotification;
    private List<Message> filteredMsg;
    private int unreadMsg;
    private Long replyID;
    private Long sendID;
    private String replyMsgContent;
    private String replyTitle;
    private String companyName;
    private List<String> companyList;
    private List<Department> departmentList;
    private String userType;
    
    //for feedback
    private String feedbackName;
    private String replyTo;
    
    //    private Logger logger;
    private final static Logger LOGGER = Logger.getLogger(MessageManagedBean.class.getName());

    @PostConstruct
    public void init() {
        message = new Message();
        selectedMsg = new Message();
        selectedAnnouncement = new Announcement();
        selectedAnnouncementSent = new Announcement();
        allMessage = new ArrayList<Message>();
        allUnreadMessage = new ArrayList<Message>();
        department = new Department();
        selectedDepartment = new ArrayList<Department>();
        allAnnouncement = new ArrayList<Announcement>();
        allAnnouncementSent = new ArrayList<Announcement>();
        notification = new Notification();
        selectedNotification = new NotificationRecord();
        allNotification = new ArrayList<NotificationRecord>();
        filteredMsg = new ArrayList<Message>();
        companyList = new ArrayList<>();
        departmentList = new ArrayList<Department>();
    }

    public void populateCompany() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        companyList.clear();
        List<Company> allCompany = cmsbl.retrieveAllCompany();
        if (allCompany.isEmpty()) {
            this.warnMsg("No company");
        }
        for (int i = 0; i < allCompany.size(); i++) {
            companyList.add(allCompany.get(i).getCompanyName());
        }
    }

    public void sendMessage() throws AccountTypeNotExistException {
        try {
            ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
            receiverID = amsbl.retrieveAccount(receiverName).getId();
            message = msbl.createMessage(ID, receiverID, title, content);
            if (message != null) {
                this.faceMsg("Message sent");
            } else {
                this.errorMsg("Message sending unsuccessful");
            }
        } catch (UserNotExistException ex) {
            this.displayFaceMessage("Username of receiver does not exist");
        }
    }
    
    public void sendFeedback() throws AccountTypeNotExistException {
        try {
            receiverType = "SystemAdmin";
            receiverName = "MLAdmin1";
            ID = amsbl.retrieveAccount("MLAdmin1").getId();
            receiverID = amsbl.retrieveAccount(receiverName).getId();
            content = content + "<br />" + "Sender Name: "+feedbackName+"<br />"+"Sender Email: <a href=\"mailto:"+replyTo+"\">"+replyTo+"</a>";
            message = msbl.createMessage(ID, receiverID, title, content);
            if (message != null) {
                this.faceMsg("Message sent");
                content = null;
                receiverType = null;
                receiverName = null;
                feedbackName = null;
                replyTo = null;
            } else {
                this.errorMsg("Message sending unsuccessful");
            }
        } catch (UserNotExistException ex) {
            this.displayFaceMessage("Username of receiver does not exist");
        }
    }

    public void viewAllMessage() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        System.out.println("Inside viewAllMessage");
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        allMessage = msbl.getAllMessages(ID);
//        System.out.println("Message is not null "+allMessage.get(0).getContent());
        if (allMessage == null) {
            this.faceMsg("No message");
        }
    }

    public void viewAllUnreadMessage() {
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        allUnreadMessage = msbl.getUnreadMessages(ID);
        if (allUnreadMessage == null) {
            this.faceMsg("No unread message");
        }
    }

    public void readMessage(Long msgID) {
        System.out.println("Inside read message");
        boolean checkMsg = msbl.readMessage(msgID);

        for (Message message : allMessage) {
            if (message.getId().equals(msgID)) {
                message.setReadOrNot(true);
            }
        }

        System.out.println("Read status is " + checkMsg);
        if (checkMsg == false) {
            this.errorMsg("Error displaying message");
        }
    }

    public void countUnreadMessage() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            System.out.println("lala");
            return;
        }
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        unreadMsg = msbl.unreadCount(ID);
    }

    public void deleteMessage(Long msgID) {
        System.out.println("Inside delete message");
        boolean checkDelete = msbl.deleteMessage(msgID);
        if (checkDelete == false) {
            this.errorMsg("Error deleting message");
        }
    }

    public void replyMessage(Message msg) {
        System.out.println("Inside reply message");
        replyID = msg.getSender().getId();
        System.out.println("reply Id is " + replyID);
        sendID = msg.getReceiverId();
        System.out.println("send Id is " + sendID);
        replyTitle = "RE: " + msg.getTitle();
    }

    public void sendReply() {
        System.out.println("Inside send reply");
        System.out.println("Reply content is " + replyMsgContent);
        Message msg = msbl.createMessage(sendID, replyID, replyTitle, replyMsgContent);
        if (msg != null) {
            this.faceMsg("Message sent");
        } else {
            this.errorMsg("Message send failure");
        }
    }

    public void sendAnnouncement() throws AccountTypeNotExistException {
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        senderType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        Account account = amsbl.retrieveAccount(ID, senderType);
        System.out.println("Account username is " + account.getUsername());
        for (int i = 0; i < selectedDepartment.size(); i++) {
            department = selectedDepartment.get(i);
            System.out.println("Department selected is " + department);
            announcement = asbl.createAnnouncement(title, content, account, department.getId());
        }
        if (announcement != null) {
            this.faceMsg("Announcement sent");
        } else {
            this.errorMsg("Announcement sending unsuccessful");
        }
    }

    public void viewAllAnnouncement() {
        departmentID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("departmentID");
        System.out.println("Department ID of the current user is " + departmentID);
        allAnnouncement = asbl.getDepartmentAnnouncements(departmentID);
        if (allAnnouncement == null) {
            this.faceMsg("No announcement");
        }
    }

    public void viewAllAnnouncementSent() throws AccountTypeNotExistException {
        System.out.println("Inside view all announcement sent");
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        allAnnouncementSent = asbl.getAllAnnouncements(ID);
        System.out.println("heihei");
        if (allAnnouncementSent == null) {
            this.faceMsg("You have not sent any announcement");
        }
    }

    public void deleteAnnouncement(Long annID) {
        System.out.println("Inside delete announcement");
        boolean checkDelete = asbl.deleteAnnouncement(annID);
        if (checkDelete == false) {
            this.errorMsg("Error deleting announcement");
        }
    }

    public void setDepartmentOption() throws CompanyNotExistException {
        System.out.println("Selected Department, set title option");
        Company c = cmsbl.retrieveCompany(companyName);
        departmentList = c.getDepartment();
    }

    public void sendNotification() throws AccountTypeNotExistException {
        System.out.println("Inside notification");
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        userType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userType");
        if (userType.equals("User")) {
            Account acc = amsbl.retrieveAccount(ID, "User");
            for (int i = 0; i < selectedDepartment.size(); i++) {
                department = selectedDepartment.get(i);
                System.out.println("Department selected is " + department);
                Long departmentID = department.getId();
                System.out.println("Department ID is " + departmentID);
                notification = nsbl.createNotification(acc, departmentID, content);
            }
        }
        if (userType.equals("Admin")) {
            Account acc = amsbl.retrieveAccount(ID, "Admin");
            for (int i = 0; i < selectedDepartment.size(); i++) {
                department = selectedDepartment.get(i);
                System.out.println("Department selected is " + department);
                Long departmentID = department.getId();
                System.out.println("Department ID is " + departmentID);
                notification = nsbl.createNotification(acc, departmentID, content);
            }
        }
        if (userType.equals("SystemAdmin")) {
            Account acc = amsbl.retrieveAccount(ID, "SystemAdmin");
            for (int i = 0; i < selectedDepartment.size(); i++) {
                department = selectedDepartment.get(i);
                System.out.println("Department selected is " + department);
                Long departmentID = department.getId();
                System.out.println("Department ID is " + departmentID);
                notification = nsbl.createNotification(acc, departmentID, content);
            }
        }

        if (notification != null) {
            this.faceMsg("Notification sent");
        } else {
            this.errorMsg("Notification sending unsuccessful");
        }
    }

    public void viewAllNotification() {
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        System.out.println("ID of the current user is " + ID);
        allNotification = nsbl.getNotficationRecords(ID);
//        System.out.println("notification size is "+allNotification.size());
        if (allNotification == null) {
            this.faceMsg("No notification");
        }
    }

    public void deleteNotification(Long notificationID) {
        System.out.println("Inside delete notification");
        System.out.println("Notification ID is " + notificationID);
        ID = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        boolean checkDelete = nsbl.deleteNotificationRecord(notificationID);
        System.out.println("Delete status is " + checkDelete);
        allNotification = nsbl.getNotficationRecords(ID);
        if (checkDelete == true) {
            this.faceMsg("Notification successfully deleted");
        }
        if (checkDelete == false) {
            this.errorMsg("Notification not deleted");
        }
    }

    private void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }
    
    private void warnMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        LOGGER.info("MESSAGE INFO: " + message);
    }
    
    private void displayFaceMessage(String response) {
        FacesMessage msg = new FacesMessage(response);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getReplyTitle() {
        return replyTitle;
    }

    public void setReplyTitle(String replyTitle) {
        this.replyTitle = replyTitle;
    }

    public List<String> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<String> companyList) {
        this.companyList = companyList;
    }

    public List<Message> getFilteredMsg() {
        return filteredMsg;
    }

    public void setFilteredMsg(List<Message> filteredMsg) {
        this.filteredMsg = filteredMsg;
    }

    public List<NotificationRecord> getAllNotification() {
        return allNotification;
    }

    public void setAllNotification(List<NotificationRecord> allNotification) {
        this.allNotification = allNotification;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public NotificationRecord getSelectedNotification() {
        return selectedNotification;
    }

    public void setSelectedNotification(NotificationRecord selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    public Announcement getSelectedAnnouncement() {
        return selectedAnnouncement;
    }

    public void setSelectedAnnouncement(Announcement selectedAnnouncement) {
        this.selectedAnnouncement = selectedAnnouncement;
    }

    public List<Announcement> getAllAnnouncementSent() {
        return allAnnouncementSent;
    }

    public void setAllAnnouncementSent(List<Announcement> allAnnouncementSent) {
        this.allAnnouncementSent = allAnnouncementSent;
    }

    public Announcement getSelectedAnnouncementSent() {
        return selectedAnnouncementSent;
    }

    public void setSelectedAnnouncementSent(Announcement selectedAnnouncementSent) {
        this.selectedAnnouncementSent = selectedAnnouncementSent;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getAnnID() {
        return annID;
    }

    public void setAnnID(Long annID) {
        this.annID = annID;
    }

    public Long getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(Long departmentID) {
        this.departmentID = departmentID;
    }

    public Long getReplyID() {
        return replyID;
    }

    public void setReplyID(Long replyID) {
        this.replyID = replyID;
    }

    public Long getSendID() {
        return sendID;
    }

    public void setSendID(Long sendID) {
        this.sendID = sendID;
    }

    public String getReplyMsgContent() {
        return replyMsgContent;
    }

    public void setReplyMsgContent(String replyMsgContent) {
        this.replyMsgContent = replyMsgContent;
    }

    public List<Announcement> getAllAnnouncement() {
        return allAnnouncement;
    }

    public void setAllAnnouncement(List<Announcement> allAnnouncement) {
        this.allAnnouncement = allAnnouncement;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Department> getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(List<Department> selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public Message getSelectedMsg() {
        return selectedMsg;
    }

    public void setSelectedMsg(Message selectedMsg) {
        this.selectedMsg = selectedMsg;
    }

    public List<Message> getAllMessage() {
        return allMessage;
    }

    public void setAllMessage(List<Message> allMessage) {
        this.allMessage = allMessage;
    }

    public int getUnreadMsg() {
        return unreadMsg;
    }

    public void setUnreadMsg(int unreadMsg) {
        this.unreadMsg = unreadMsg;
    }

    public List<Message> getAllUnreadMessage() {
        return allUnreadMessage;
    }

    public void setAllUnreadMessage(List<Message> allUnreadMessage) {
        this.allUnreadMessage = allUnreadMessage;
    }

    /**
     * @return the receiverName
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * @param receiverName the receiverName to set
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * @return the receiverType
     */
    public String getReceiverType() {
        return receiverType;
    }

    /**
     * @param receiverType the receiverType to set
     */
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getMsgID() {
        return msgID;
    }

    public void setMsgID(Long msgID) {
        this.msgID = msgID;
    }

    public Long getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Long receiverID) {
        this.receiverID = receiverID;
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

    public String getFeedbackName() {
        return feedbackName;
    }

    public void setFeedbackName(String feedbackName) {
        this.feedbackName = feedbackName;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

}
