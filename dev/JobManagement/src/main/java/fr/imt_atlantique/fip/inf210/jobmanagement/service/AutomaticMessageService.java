package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: AutomaticMessageService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;

public interface AutomaticMessageService {

    // Cette methode implemente l operation sendAutomaticMessagesForNewOffer.
    int sendAutomaticMessagesForNewOffer(JobOffer jobOffer);

    // Cette methode implemente l operation sendAutomaticMessagesForNewApplication.
    int sendAutomaticMessagesForNewApplication(Application application);
}
