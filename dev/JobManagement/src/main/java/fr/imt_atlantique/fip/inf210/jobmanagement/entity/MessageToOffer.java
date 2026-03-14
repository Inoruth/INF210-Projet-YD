package fr.imt_atlantique.fip.inf210.jobmanagement.entity;

/*
 * Fichier: MessageToOffer
 * Cette classe represente une entite persistante du domaine metier.
 * Les annotations JPA decrivent le mapping table, colonnes et relations.
 * Elle transporte les donnees entre la base de donnees et la couche service.
 * Son contenu doit rester coherent avec le schema et les contraintes de persistance.
 */

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "message_to_offer",
        uniqueConstraints = @UniqueConstraint(name = "uq_mto_pair", columnNames = {"joboffer_id", "application_id"})
)
public class MessageToOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate publicationdate = LocalDate.now();

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "joboffer_id", nullable = false)
    private JobOffer jobOffer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Cette methode implemente l operation MessageToOffer.
    public MessageToOffer() {
    }

    // Cette methode implemente l operation MessageToOffer.
    public MessageToOffer(String message, JobOffer jobOffer, Application application) {
        this.message = message;
        this.jobOffer = jobOffer;
        this.application = application;
    }

    // Cette methode implemente l operation getId.
    public Integer getId() {
        return id;
    }

    // Cette methode implemente l operation setId.
    public void setId(Integer id) {
        this.id = id;
    }

    // Cette methode implemente l operation getPublicationdate.
    public LocalDate getPublicationdate() {
        return publicationdate;
    }

    // Cette methode implemente l operation setPublicationdate.
    public void setPublicationdate(LocalDate publicationdate) {
        this.publicationdate = publicationdate;
    }

    // Cette methode implemente l operation getMessage.
    public String getMessage() {
        return message;
    }

    // Cette methode implemente l operation setMessage.
    public void setMessage(String message) {
        this.message = message;
    }

    // Cette methode implemente l operation getJobOffer.
    public JobOffer getJobOffer() {
        return jobOffer;
    }

    // Cette methode implemente l operation setJobOffer.
    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;
    }

    // Cette methode implemente l operation getApplication.
    public Application getApplication() {
        return application;
    }

    // Cette methode implemente l operation setApplication.
    public void setApplication(Application application) {
        this.application = application;
    }
}
