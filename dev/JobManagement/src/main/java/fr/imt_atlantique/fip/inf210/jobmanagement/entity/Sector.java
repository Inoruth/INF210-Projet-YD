package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: Sector
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sectors")
public class Sector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "label", nullable = false, unique = true, length = 50)
    private String label;
    
    // Cette methode implemente l operation Sector.
    public Sector() {
    }
    
    // Cette methode implemente l operation Sector.
    public Sector(String label) {
        this.label = label;
    }
    
    // Cette methode implemente l operation getId.
    public Integer getId() {
        return id;
    }
    
    // Cette methode implemente l operation setId.
    public void setId(Integer id) {
        this.id = id;
    }
    
    // Cette methode implemente l operation getLabel.
    public String getLabel() {
        return label;
    }
    
    // Cette methode implemente l operation setLabel.
    public void setLabel(String label) {
        this.label = label;
    }
}