package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: Candidate
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;

    @Column(nullable = false, length = 50)
    private String lastname;

    @Column(length = 50)
    private String firstname;

    @Column(length = 100)
    private String city;

    // Cette methode implemente l operation Candidate.
    public Candidate() {
    }

    // Cette methode implemente l operation Candidate.
    public Candidate(AppUser appUser, String lastname, String firstname, String city) {
        this.appUser = appUser;
        this.lastname = lastname;
        this.firstname = firstname;
        this.city = city;
    }

    // Cette methode implemente l operation getId.
    public Integer getId() {
        return id;
    }

    // Cette methode implemente l operation setId.
    public void setId(Integer id) {
        this.id = id;
    }

    // Cette methode implemente l operation getAppUser.
    public AppUser getAppUser() {
        return appUser;
    }

    // Cette methode implemente l operation setAppUser.
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    // Cette methode implemente l operation getLastname.
    public String getLastname() {
        return lastname;
    }

    // Cette methode implemente l operation setLastname.
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // Cette methode implemente l operation getFirstname.
    public String getFirstname() {
        return firstname;
    }

    // Cette methode implemente l operation setFirstname.
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    // Cette methode implemente l operation getCity.
    public String getCity() {
        return city;
    }

    // Cette methode implemente l operation setCity.
    public void setCity(String city) {
        this.city = city;
    }
}
