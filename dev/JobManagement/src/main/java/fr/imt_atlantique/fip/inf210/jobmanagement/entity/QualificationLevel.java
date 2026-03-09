package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "qualificationlevel") 
public class QualificationLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String label;
    
    // Default constructor
    public QualificationLevel() {
    }
    
    // Constructor with label
    public QualificationLevel(String label) {
        this.label = label;
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
    
    @Override
    public String toString() {
        return "QualificationLevel{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
