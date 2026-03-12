package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: MessageToOfferService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;

public interface MessageToOfferService {

    // Cette methode implemente l operation save.
    MessageToOffer save(MessageToOffer messageToOffer);

    // Cette methode implemente l operation findByJobOfferAndApplication.
    Optional<MessageToOffer> findByJobOfferAndApplication(Integer jobOfferId, Integer applicationId);

    // Cette methode implemente l operation findByCompanyId.
    List<MessageToOffer> findByCompanyId(Integer companyId);

    // Cette methode implemente l operation findByCandidateId.
    List<MessageToOffer> findByCandidateId(Integer candidateId);
}
