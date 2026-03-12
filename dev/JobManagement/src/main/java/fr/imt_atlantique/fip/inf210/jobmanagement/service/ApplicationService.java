package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: ApplicationService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;

public interface ApplicationService {

    // Cette methode implemente l operation findAll.
    List<Application> findAll();

    // Cette methode implemente l operation findById.
    Optional<Application> findById(Integer id);

    // Cette methode implemente l operation save.
    Application save(Application application);

    // Cette methode implemente l operation findByCandidateId.
    List<Application> findByCandidateId(Integer candidateId);

    // Cette methode implemente l operation findByIdAndCandidateId.
    Optional<Application> findByIdAndCandidateId(Integer id, Integer candidateId);

    // Cette methode implemente l operation deleteByIdAndCandidateId.
    void deleteByIdAndCandidateId(Integer id, Integer candidateId);

    // Cette methode implemente l operation searchByCriteria.
    List<Application> searchByCriteria(Set<Integer> sectorIds, Short minimumRank);

    // Cette methode implemente l operation findMatchingByJobOfferId.
    List<Application> findMatchingByJobOfferId(Integer jobOfferId);
}
