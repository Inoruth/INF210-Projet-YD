package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: ApplicationServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationJpaRepository applicationRepository;
    private final AutomaticMessageService automaticMessageService;

    public ApplicationServiceImpl(ApplicationJpaRepository applicationRepository,
                                  AutomaticMessageService automaticMessageService) {
        this.applicationRepository = applicationRepository;
        this.automaticMessageService = automaticMessageService;
    }

    // Cette methode implemente l operation findAll.
    @Override
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    // Cette methode implemente l operation findById.
    @Override
    public Optional<Application> findById(Integer id) {
        return applicationRepository.findById(id);
    }

    // Cette methode implemente l operation save.
    @Override
    public Application save(Application application) {
        boolean isNewApplication = application.getId() == null;
        Application saved = applicationRepository.save(application);

        if (isNewApplication) {
            automaticMessageService.sendAutomaticMessagesForNewApplication(saved);
        }

        return saved;
    }

    // Cette methode implemente l operation findByCandidateId.
    @Override
    public List<Application> findByCandidateId(Integer candidateId) {
        return applicationRepository.findByCandidateIdOrderByAppdateDesc(candidateId);
    }

    // Cette methode implemente l operation findByIdAndCandidateId.
    @Override
    public Optional<Application> findByIdAndCandidateId(Integer id, Integer candidateId) {
        return applicationRepository.findByIdAndCandidateId(id, candidateId);
    }

    // Cette methode implemente l operation deleteByIdAndCandidateId.
    @Override
    public void deleteByIdAndCandidateId(Integer id, Integer candidateId) {
        applicationRepository.findByIdAndCandidateId(id, candidateId).ifPresent(applicationRepository::delete);
    }

    // Cette methode implemente l operation searchByCriteria.
    @Override
    public List<Application> searchByCriteria(Set<Integer> sectorIds, Short minimumRank) {
        boolean filterBySectors = sectorIds != null && !sectorIds.isEmpty();
        Set<Integer> safeSectorIds = filterBySectors ? sectorIds : Collections.emptySet();
        return applicationRepository.searchByCriteria(filterBySectors, safeSectorIds, minimumRank);
    }

    // Cette methode implemente l operation findMatchingByJobOfferId.
    @Override
    public List<Application> findMatchingByJobOfferId(Integer jobOfferId) {
        return applicationRepository.findMatchingByJobOfferId(jobOfferId);
    }
}
