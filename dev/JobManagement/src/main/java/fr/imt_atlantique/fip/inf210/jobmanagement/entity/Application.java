package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: Application
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "text")
    private String cv;

    @Column(nullable = false)
    private LocalDate appdate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qualificationlevel_id", nullable = false)
    private QualificationLevel qualificationLevel;

    @ManyToMany
    @JoinTable(
            name = "application_sector",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "sector_id")
    )
    private Set<Sector> sectors = new LinkedHashSet<>();

    // Cette methode implemente l operation Application.
    public Application() {
    }

    // Cette methode implemente l operation Application.
    public Application(String cv, Candidate candidate, QualificationLevel qualificationLevel) {
        this.cv = cv;
        this.candidate = candidate;
        this.qualificationLevel = qualificationLevel;
    }

    // Cette methode implemente l operation getId.
    public Integer getId() {
        return id;
    }

    // Cette methode implemente l operation setId.
    public void setId(Integer id) {
        this.id = id;
    }

    // Cette methode implemente l operation getCv.
    public String getCv() {
        return cv;
    }

    // Cette methode implemente l operation setCv.
    public void setCv(String cv) {
        this.cv = cv;
    }

    // Cette methode implemente l operation getAppdate.
    public LocalDate getAppdate() {
        return appdate;
    }

    // Cette methode implemente l operation setAppdate.
    public void setAppdate(LocalDate appdate) {
        this.appdate = appdate;
    }

    // Cette methode implemente l operation getCandidate.
    public Candidate getCandidate() {
        return candidate;
    }

    // Cette methode implemente l operation setCandidate.
    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    // Cette methode implemente l operation getQualificationLevel.
    public QualificationLevel getQualificationLevel() {
        return qualificationLevel;
    }

    // Cette methode implemente l operation setQualificationLevel.
    public void setQualificationLevel(QualificationLevel qualificationLevel) {
        this.qualificationLevel = qualificationLevel;
    }

    // Cette methode implemente l operation getSectors.
    public Set<Sector> getSectors() {
        return sectors;
    }

    // Cette methode implemente l operation setSectors.
    public void setSectors(Set<Sector> sectors) {
        this.sectors = sectors;
    }
}
