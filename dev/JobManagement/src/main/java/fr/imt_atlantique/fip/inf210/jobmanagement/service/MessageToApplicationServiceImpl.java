package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: MessageToApplicationServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;

@Service
public class MessageToApplicationServiceImpl implements MessageToApplicationService {

    private final MessageToApplicationJpaRepository messageToApplicationRepository;

    // Cette methode implemente l operation MessageToApplicationServiceImpl.
    public MessageToApplicationServiceImpl(MessageToApplicationJpaRepository messageToApplicationRepository) {
        this.messageToApplicationRepository = messageToApplicationRepository;
    }

    // Cette methode implemente l operation save.
    @Override
    public MessageToApplication save(MessageToApplication messageToApplication) {
        return messageToApplicationRepository.save(messageToApplication);
    }

    // Cette methode implemente l operation findByApplicationAndJobOffer.
    @Override
    public Optional<MessageToApplication> findByApplicationAndJobOffer(Integer applicationId, Integer jobOfferId) {
        return messageToApplicationRepository.findByApplicationIdAndJobOfferId(applicationId, jobOfferId);
    }

    // Cette methode implemente l operation findByCompanyId.
    @Override
    public List<MessageToApplication> findByCompanyId(Integer companyId) {
        return messageToApplicationRepository.findByJobOfferCompanyIdOrderByPublicationdateDesc(companyId);
    }

    // Cette methode implemente l operation findByCandidateId.
    @Override
    public List<MessageToApplication> findByCandidateId(Integer candidateId) {
        return messageToApplicationRepository.findByApplicationCandidateIdOrderByPublicationdateDesc(candidateId);
    }
}
