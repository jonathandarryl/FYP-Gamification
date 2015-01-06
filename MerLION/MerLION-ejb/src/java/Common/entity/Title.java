/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class Title implements Serializable {
    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String titleName;
    @OneToMany
    private List<User> user = new ArrayList();
    @ManyToOne
    private Department department;
    private boolean approvedOrNot;
    
    public Title(){
    }
    
    public Title(String titleName, Department department){
        this.setTitleName(titleName);
        this.setDepartment(department);
        this.setApprovedOrNot(Boolean.TRUE);
    }
    
    public Title(String titleName, Department department, Boolean register){
        this.setTitleName(titleName);
        this.setDepartment(department);
        this.setApprovedOrNot(register);
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
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName the titleName to set
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * @return the user
     */
    public List<User> getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(List<User> user) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Title)) {
            return false;
        }
        Title other = (Title) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getTitleName();
    }
    
}
