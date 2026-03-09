package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

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

    public QualificationLevel(String label, Short rank) {
        this.label = label;
        this.rank = rank;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public Short getRank() {
        return rank;
    }

    public void setRank(Short rank) {
        this.rank = rank;
    }
    
    @Override
    public String toString() {
        return "QualificationLevel{" +
                "id=" + id +
                ", label='" + label + '\'' +
            ", rank=" + rank +
                '}';
    }
}
