/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author chongyangsun
 */
@Entity
public class Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean approvedOrNot;
    private String departmentName;
    @OneToMany(mappedBy = "department")
    private List<Title> titles = new ArrayList();
//    @OneToMany(mappedBy = "department")
//    private List<Announcement> announcements = new ArrayList();
    
    @ManyToOne
    private Company company;
    @OneToMany (mappedBy="department")
    private List<Account> account; 
    
    public Department(){
        
    }
    
    public Department(String departmentName, Company company){
        this.setDepartmentName(departmentName);
        this.setCompany(company);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public Department(String departmentName, Company company, Boolean register){
        this.setDepartmentName(departmentName);
        this.setCompany(company);
        this.setApprovedOrNot(register);
    }
   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Department)) {
            return false;
        }
        Department other = (Department) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getDepartmentName();
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

//    public List<Announcement> getAnnouncements() {
//        return announcements;
//    }
//
//    public void setAnnouncements(List<Announcement> announcements) {
//        this.announcements = announcements;
//    }

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
     * @return the account
     */
    public List<Account> getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(List<Account> account) {
        this.account = account;
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
}
