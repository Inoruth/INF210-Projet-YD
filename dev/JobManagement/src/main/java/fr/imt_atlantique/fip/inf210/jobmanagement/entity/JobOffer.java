package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: JobOffer
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
@Table(name = "joboffers")
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String taskdescription;

    @Column(nullable = false)
    private LocalDate publicationdate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qualificationlevel_id", nullable = false)
    private QualificationLevel qualificationLevel;

    @ManyToMany
    @JoinTable(
            name = "joboffer_sector",
            joinColumns = @JoinColumn(name = "joboffer_id"),
            inverseJoinColumns = @JoinColumn(name = "sector_id")
    )
    private Set<Sector> sectors = new LinkedHashSet<>();

    // Cette methode implemente l operation JobOffer.
    public JobOffer() {
    }

    // Cette methode implemente l operation JobOffer.
    public JobOffer(String title, String taskdescription, Company company, QualificationLevel qualificationLevel) {
        this.title = title;
        this.taskdescription = taskdescription;
        this.company = company;
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

    // Cette methode implemente l operation getTitle.
    public String getTitle() {
        return title;
    }

    // Cette methode implemente l operation setTitle.
    public void setTitle(String title) {
        this.title = title;
    }

    // Cette methode implemente l operation getTaskdescription.
    public String getTaskdescription() {
        return taskdescription;
    }

    // Cette methode implemente l operation setTaskdescription.
    public void setTaskdescription(String taskdescription) {
        this.taskdescription = taskdescription;
    }

    // Cette methode implemente l operation getPublicationdate.
    public LocalDate getPublicationdate() {
        return publicationdate;
    }

    // Cette methode implemente l operation setPublicationdate.
    public void setPublicationdate(LocalDate publicationdate) {
        this.publicationdate = publicationdate;
    }

    // Cette methode implemente l operation getCompany.
    public Company getCompany() {
        return company;
    }

    // Cette methode implemente l operation setCompany.
    public void setCompany(Company company) {
        this.company = company;
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
