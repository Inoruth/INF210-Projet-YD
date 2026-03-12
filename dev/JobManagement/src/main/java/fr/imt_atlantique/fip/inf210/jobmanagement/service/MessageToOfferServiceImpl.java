package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: MessageToOfferServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;

@Service
public class MessageToOfferServiceImpl implements MessageToOfferService {

    private final MessageToOfferJpaRepository messageToOfferRepository;

    // Cette methode implemente l operation MessageToOfferServiceImpl.
    public MessageToOfferServiceImpl(MessageToOfferJpaRepository messageToOfferRepository) {
        this.messageToOfferRepository = messageToOfferRepository;
    }

    // Cette methode implemente l operation save.
    @Override
    public MessageToOffer save(MessageToOffer messageToOffer) {
        return messageToOfferRepository.save(messageToOffer);
    }

    // Cette methode implemente l operation findByJobOfferAndApplication.
    @Override
    public Optional<MessageToOffer> findByJobOfferAndApplication(Integer jobOfferId, Integer applicationId) {
        return messageToOfferRepository.findByJobOfferIdAndApplicationId(jobOfferId, applicationId);
    }

    // Cette methode implemente l operation findByCompanyId.
    @Override
    public List<MessageToOffer> findByCompanyId(Integer companyId) {
        return messageToOfferRepository.findByJobOfferCompanyIdOrderByPublicationdateDesc(companyId);
    }

    // Cette methode implemente l operation findByCandidateId.
    @Override
    public List<MessageToOffer> findByCandidateId(Integer candidateId) {
        return messageToOfferRepository.findByApplicationCandidateIdOrderByPublicationdateDesc(candidateId);
    }
}
