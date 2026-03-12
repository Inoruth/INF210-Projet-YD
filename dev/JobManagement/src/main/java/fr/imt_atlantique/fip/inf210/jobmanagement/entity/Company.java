package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: Company
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
@Table(name = "companies")
public class Company {

    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;

    @Column(nullable = false, length = 100)
    private String denomination;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 100)
    private String city;

    // Cette methode implemente l operation Company.
    public Company() {
    }

    // Cette methode implemente l operation Company.
    public Company(AppUser appUser, String denomination, String description, String city) {
        this.appUser = appUser;
        this.denomination = denomination;
        this.description = description;
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

    // Cette methode implemente l operation getDenomination.
    public String getDenomination() {
        return denomination;
    }

    // Cette methode implemente l operation setDenomination.
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    // Cette methode implemente l operation getDescription.
    public String getDescription() {
        return description;
    }

    // Cette methode implemente l operation setDescription.
    public void setDescription(String description) {
        this.description = description;
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
