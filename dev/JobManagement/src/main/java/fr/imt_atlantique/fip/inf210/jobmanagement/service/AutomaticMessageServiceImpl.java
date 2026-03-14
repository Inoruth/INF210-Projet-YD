package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: AutomaticMessageServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;

@Service
public class AutomaticMessageServiceImpl implements AutomaticMessageService {

    private final ApplicationJpaRepository applicationRepository;
    private final JobOfferJpaRepository jobOfferRepository;
    private final MessageToOfferJpaRepository messageToOfferRepository;
    private final MessageToApplicationJpaRepository messageToApplicationRepository;

    public AutomaticMessageServiceImpl(ApplicationJpaRepository applicationRepository,
                                       JobOfferJpaRepository jobOfferRepository,
                                       MessageToOfferJpaRepository messageToOfferRepository,
                                       MessageToApplicationJpaRepository messageToApplicationRepository) {
        this.applicationRepository = applicationRepository;
        this.jobOfferRepository = jobOfferRepository;
        this.messageToOfferRepository = messageToOfferRepository;
        this.messageToApplicationRepository = messageToApplicationRepository;
    }

    // Cette methode implemente l operation sendAutomaticMessagesForNewOffer.
    @Override
    public int sendAutomaticMessagesForNewOffer(JobOffer jobOffer) {
        if (jobOffer == null || jobOffer.getId() == null) {
            return 0;
        }

        List<Application> matches = applicationRepository.findMatchingByJobOfferId(jobOffer.getId());
        int sentCount = 0;

        for (Application application : matches) {
            if (application.getId() == null) {
                continue;
            }

            boolean alreadyExists = messageToOfferRepository
                    .findByJobOfferIdAndApplicationId(jobOffer.getId(), application.getId())
                    .isPresent();
            if (alreadyExists) {
                continue;
            }

            MessageToOffer message = new MessageToOffer(
                    buildOfferToCandidateMessage(jobOffer, application),
                    jobOffer,
                    application
            );
            messageToOfferRepository.save(message);
            sentCount++;
        }

        return sentCount;
    }

    // Cette methode implemente l operation sendAutomaticMessagesForNewApplication.
    @Override
    public int sendAutomaticMessagesForNewApplication(Application application) {
        if (application == null || application.getId() == null) {
            return 0;
        }

        List<JobOffer> matches = jobOfferRepository.findMatchingByApplicationId(application.getId());
        int sentCount = 0;

        for (JobOffer jobOffer : matches) {
            if (jobOffer.getId() == null) {
                continue;
            }

            boolean alreadyExists = messageToApplicationRepository
                    .findByApplicationIdAndJobOfferId(application.getId(), jobOffer.getId())
                    .isPresent();
            if (alreadyExists) {
                continue;
            }

            MessageToApplication message = new MessageToApplication(
                    buildApplicationToCompanyMessage(application, jobOffer),
                    application,
                    jobOffer
            );
            messageToApplicationRepository.save(message);
            sentCount++;
        }

        return sentCount;
    }

    // Cette methode implemente l operation buildOfferToCandidateMessage.
    private String buildOfferToCandidateMessage(JobOffer offer, Application application) {
        return String.format(
                "Automatic message: your application #%d may match offer #%d (%s).",
                application.getId(),
                offer.getId(),
                offer.getTitle()
        );
    }

    // Cette methode implemente l operation buildApplicationToCompanyMessage.
    private String buildApplicationToCompanyMessage(Application application, JobOffer offer) {
        return String.format(
                "Automatic message: application #%d may match your offer #%d (%s).",
                application.getId(),
                offer.getId(),
                offer.getTitle()
        );
    }
}
