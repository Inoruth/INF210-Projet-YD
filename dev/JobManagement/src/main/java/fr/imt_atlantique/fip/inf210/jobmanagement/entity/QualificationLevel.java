package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: QualificationLevel
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
@Table(name = "qualificationlevels")
public class QualificationLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String label;

    @Column(unique = true)
    private Short rank;
    
    // Default constructor
    public QualificationLevel() {
    }
    
    // Constructor with label
    public QualificationLevel(String label) {
        this.label = label;
    }

    // Cette methode implemente l operation QualificationLevel.
    public QualificationLevel(String label, Short rank) {
        this.label = label;
        this.rank = rank;
    }
    
    // Getters and Setters
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

    // Cette methode implemente l operation getRank.
    public Short getRank() {
        return rank;
    }

    // Cette methode implemente l operation setRank.
    public void setRank(Short rank) {
        this.rank = rank;
    }
    
    // Cette methode implemente l operation toString.
    @Override
    public String toString() {
        return "QualificationLevel{" +
                "id=" + id +
                ", label='" + label + '\'' +
            ", rank=" + rank +
                '}';
    }
}
