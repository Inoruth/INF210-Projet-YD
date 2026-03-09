package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sector")
public class Sector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "label", nullable = false, unique = true, length = 50)
    private String label;
    
    public Sector() {
    }
    
    public Sector(String label) {
        this.label = label;
    }
    
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
}