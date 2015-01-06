/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CommonManagedBean;

import Common.entity.Scheduler;
import Common.session.AccountManagementSessionBeanLocal;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
 
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author chongyangsun
 */
@ManagedBean
@Named(value = "scheduleView")
@SessionScoped
public class ScheduleView implements Serializable {

    @EJB
    private AccountManagementSessionBeanLocal amsbl;
    
    private Long accountId;
    private String eventId;
    private ScheduleModel eventModel;
    
    private ScheduleEvent event = new DefaultScheduleEvent();
 
    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
//        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
//        eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
//        eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));
//        eventModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", theDayAfter3Pm(), fourDaysLater3pm()));
    }
    
    public void populateSchedule(){
        if (FacesContext.getCurrentInstance().getResponseComplete()) {
            return;
        }
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        
        List<Scheduler> sList = amsbl.retrieveSchedule(accountId);
        if(!sList.isEmpty()) {
            eventModel = new DefaultScheduleModel();
            for(Scheduler s: sList) {
                System.out.println("all day? "+s.getAllDay());
                System.out.println("Start time " + s.getStartTime());
                System.out.println("End time " + s.getEndTime());
                eventModel.addEvent(new DefaultScheduleEvent(s.getEvent(), new Date(s.getStartTime().getTime()), new Date(s.getEndTime().getTime()),s.getAllDay().booleanValue()));
            }
        }
    }
     
    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);    //set random day of month
         
        return date.getTime();
    }
     
    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
         
        return calendar.getTime();
    }
     
    public ScheduleModel getEventModel() {
        return eventModel;
    }
 
    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
 
        return calendar;
    }
     
    private Date previousDay8Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 8);
         
        return t.getTime();
    }
     
    private Date previousDay11Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 11);
         
        return t.getTime();
    }
     
    private Date today1Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 1);
         
        return t.getTime();
    }
     
    private Date theDayAfter3Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);     
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 3);
         
        return t.getTime();
    }
 
    private Date today6Pm() {
        Calendar t = (Calendar) today().clone(); 
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 6);
         
        return t.getTime();
    }
     
    private Date nextDay9Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 9);
         
        return t.getTime();
    }
     
    private Date nextDay11Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 11);
         
        return t.getTime();
    }
     
    private Date fourDaysLater3pm() {
        Calendar t = (Calendar) today().clone(); 
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
        t.set(Calendar.HOUR, 3);
         
        return t.getTime();
    }
     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    public void addEvent(ActionEvent actionEvent) {
        accountId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id");
        if(event.getId() == null) {
            eventModel.addEvent(event);
            String id = event.getTitle()+event.getStartDate().getTime()+event.getEndDate().getTime();
            System.out.println("Event id "+id);
            Scheduler scheduler = amsbl.createEvent(accountId, id, event.getTitle(), event.isAllDay(), new Timestamp(event.getStartDate().getTime()), new Timestamp(event.getEndDate().getTime()));
            if(scheduler != null) {
                faceMsg("Event Added Successfully!");
            }
        }
        else {
            eventModel.updateEvent(event);
            
            if(amsbl.updateEvent(accountId, eventId, event.getTitle(), event.isAllDay(), new Timestamp(event.getStartDate().getTime()), new Timestamp(event.getEndDate().getTime())))
                faceMsg("Event Update Successful!");
        }
         
        event = new DefaultScheduleEvent();
    }
    
    private void faceMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        eventId = event.getTitle()+event.getStartDate().getTime()+event.getEndDate().getTime();
        
        System.out.println(eventId);
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
     
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    
}
