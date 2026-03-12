package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: AppUser
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

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

    // Cette methode implemente l operation getId.
    public Integer getId() {
        return id;
    }

    // Cette methode implemente l operation setId.
    public void setId(Integer id) {
        this.id = id;
    }
    
    // Getters and Setters
    public String getMail() {
        return mail;
    }
    
    // Cette methode implemente l operation setMail.
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    // Cette methode implemente l operation getPassword.
    public String getPassword() {
        return password;
    }
    
    // Cette methode implemente l operation setPassword.
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Cette methode implemente l operation getUsertype.
    public UserType getUsertype() {
        return usertype;
    }
    
    // Cette methode implemente l operation setUsertype.
    public void setUsertype(UserType usertype) {
        this.usertype = usertype;
    }
    
    // Cette methode implemente l operation toString.
    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", usertype=" + usertype +
                '}';
    }
}
