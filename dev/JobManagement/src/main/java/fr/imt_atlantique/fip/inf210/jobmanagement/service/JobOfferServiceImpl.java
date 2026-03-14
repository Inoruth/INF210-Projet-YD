package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: JobOfferServiceImpl
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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferJpaRepository jobOfferRepository;
    private final AutomaticMessageService automaticMessageService;

    public JobOfferServiceImpl(JobOfferJpaRepository jobOfferRepository,
                               AutomaticMessageService automaticMessageService) {
        this.jobOfferRepository = jobOfferRepository;
        this.automaticMessageService = automaticMessageService;
    }

    // Cette methode implemente l operation findAll.
    @Override
    public List<JobOffer> findAll() {
        return jobOfferRepository.findAll();
    }

    // Cette methode implemente l operation findById.
    @Override
    public Optional<JobOffer> findById(Integer id) {
        return jobOfferRepository.findById(id);
    }

    // Cette methode implemente l operation save.
    @Override
    public JobOffer save(JobOffer jobOffer) {
        boolean isNewOffer = jobOffer.getId() == null;
        JobOffer saved = jobOfferRepository.save(jobOffer);

        if (isNewOffer) {
            automaticMessageService.sendAutomaticMessagesForNewOffer(saved);
        }

        return saved;
    }

    // Cette methode implemente l operation findByCompanyId.
    @Override
    public List<JobOffer> findByCompanyId(Integer companyId) {
        return jobOfferRepository.findByCompanyIdOrderByPublicationdateDesc(companyId);
    }

    // Cette methode implemente l operation findByIdAndCompanyId.
    @Override
    public Optional<JobOffer> findByIdAndCompanyId(Integer id, Integer companyId) {
        return jobOfferRepository.findByIdAndCompanyId(id, companyId);
    }

    // Cette methode implemente l operation deleteByIdAndCompanyId.
    @Override
    public void deleteByIdAndCompanyId(Integer id, Integer companyId) {
        jobOfferRepository.findByIdAndCompanyId(id, companyId).ifPresent(jobOfferRepository::delete);
    }

    // Cette methode implemente l operation searchByCriteria.
    @Override
    public List<JobOffer> searchByCriteria(Set<Integer> sectorIds, Short minimumRank) {
        boolean filterBySectors = sectorIds != null && !sectorIds.isEmpty();
        Set<Integer> safeSectorIds = filterBySectors ? sectorIds : Collections.emptySet();
        return jobOfferRepository.searchByCriteria(filterBySectors, safeSectorIds, minimumRank);
    }

    // Cette methode implemente l operation findMatchingByApplicationId.
    @Override
    public List<JobOffer> findMatchingByApplicationId(Integer applicationId) {
        return jobOfferRepository.findMatchingByApplicationId(applicationId);
    }
}
