package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: MessageToApplicationService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;

public interface MessageToApplicationService {

    // Cette methode implemente l operation save.
    MessageToApplication save(MessageToApplication messageToApplication);

    // Cette methode implemente l operation findByApplicationAndJobOffer.
    Optional<MessageToApplication> findByApplicationAndJobOffer(Integer applicationId, Integer jobOfferId);

    // Cette methode implemente l operation findByCompanyId.
    List<MessageToApplication> findByCompanyId(Integer companyId);

    // Cette methode implemente l operation findByCandidateId.
    List<MessageToApplication> findByCandidateId(Integer candidateId);
}
