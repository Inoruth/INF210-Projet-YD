package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: CandidateService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;

public interface CandidateService {

    // Cette methode implemente l operation findAll.
    List<Candidate> findAll();

    // Cette methode implemente l operation findByMail.
    Optional<Candidate> findByMail(String mail);

    // Cette methode implemente l operation searchByLastname.
    List<Candidate> searchByLastname(String lastnamePart);

    // Cette methode implemente l operation save.
    Candidate save(Candidate candidate);
}
