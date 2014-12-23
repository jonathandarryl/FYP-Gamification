/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRMManagedBean;

 
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
 
@ViewScoped

/**
 *
 * @author andongmei
 */
//@Named(value = "countryStateManagedBean")

//@ManagedBean(name = "countryStateManagedBean")


@ManagedBean (name = "countryStateWManagedBean")
@Named(value = "countryStateWManagedBean")

public class CountryStateWManagedBean implements Serializable {
     
    private final Map<String,Map<String,String>> data = new HashMap<>();
    private String country;
    private String state; 
    private Map<String,String> countries;
    private Map<String,String> states;
     
    @PostConstruct
    public void init() {
        countries  = new HashMap<>();
        countries.put("USA", "USA");
        countries.put("Singapore", "Singapore"); //Singapore
        countries.put("China", "China"); //China
        countries.put("UK", "UK"); //UK
        countries.put("N.A.", "N.A."); //N.A.
         
        Map<String,String> map = new HashMap<>();
        map.put("New York", "New York");
        map.put("Washington", "Washington");
        map.put("Texas", "Texas");
        data.put("USA", map);
         
        map = new HashMap<>();
        map.put("Singapore", "Singapore");
        data.put("Singapore", map);
         
        map = new HashMap<>();
        map.put("Guangdong", "Guangdong");
        map.put("Fujian", "Fujian");
        map.put("Jiangsu", "Jiangsu");
        data.put("China", map);
        
        map = new HashMap<>();
        map.put("Wales", "Wales");
        map.put("England", "England");
        map.put("Scotland", "Scotland");
        data.put("UK", map);
         
        map = new HashMap<>();
        map.put("N.A.", "N.A.");
        data.put("N.A.", map);
      
    }

    /**
     *
     */
    public CountryStateWManagedBean() {
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }
 
    public String getCountry() {
        return country;
    }
 
    public void setCountry(String country) {
        this.country = country;
    }
 
    public String getState() {
        return state;
    }
 
    public void setState(String state) {
        this.state = state;
    }
 
    public Map<String, String> getCountries() {
        return countries;
    }
 
    public Map<String, String> getStates() {
        return states;
    }
 
     public void onCountryChange(String c) {
        System.out.print("countrychange");
        
        if(c !=null && !c.equals(""))
        {
            
            states = data.get(c);
            
              System.out.print(c);
        }
        else
        { states = new HashMap<>();
        System.out.print("country is null");
        
        }
    }

}