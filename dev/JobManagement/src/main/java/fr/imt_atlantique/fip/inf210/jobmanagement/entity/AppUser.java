package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "appusers")
public class AppUser {
    
    public enum UserType {
        applicant, company, admin
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String mail;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(name = "usertype", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserType usertype;
    
    // Default constructor
    public AppUser() {
    }
    
    // Constructor with all fields
    public AppUser(String mail, String password, UserType usertype) {
        this.mail = mail;
        this.password = password;
        this.usertype = usertype;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    // Getters and Setters
    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserType getUsertype() {
        return usertype;
    }
    
    public void setUsertype(UserType usertype) {
        this.usertype = usertype;
    }
    
    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", usertype=" + usertype +
                '}';
    }
}
