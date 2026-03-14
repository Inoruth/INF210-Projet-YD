package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: Admin
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;

    // Cette methode implemente l operation Admin.
    public Admin() {
    }

    // Cette methode implemente l operation Admin.
    public Admin(AppUser appUser) {
        this.appUser = appUser;
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
}
