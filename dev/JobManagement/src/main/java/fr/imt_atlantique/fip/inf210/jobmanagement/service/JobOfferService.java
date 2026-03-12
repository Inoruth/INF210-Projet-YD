package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: JobOfferService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;

public interface JobOfferService {

    // Cette methode implemente l operation findAll.
    List<JobOffer> findAll();

    // Cette methode implemente l operation findById.
    Optional<JobOffer> findById(Integer id);

    // Cette methode implemente l operation save.
    JobOffer save(JobOffer jobOffer);

    // Cette methode implemente l operation findByCompanyId.
    List<JobOffer> findByCompanyId(Integer companyId);

    // Cette methode implemente l operation findByIdAndCompanyId.
    Optional<JobOffer> findByIdAndCompanyId(Integer id, Integer companyId);

    // Cette methode implemente l operation deleteByIdAndCompanyId.
    void deleteByIdAndCompanyId(Integer id, Integer companyId);

    // Cette methode implemente l operation searchByCriteria.
    List<JobOffer> searchByCriteria(Set<Integer> sectorIds, Short minimumRank);

    // Cette methode implemente l operation findMatchingByApplicationId.
    List<JobOffer> findMatchingByApplicationId(Integer applicationId);
}
