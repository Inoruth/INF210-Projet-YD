package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: CompanyService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;

public interface CompanyService {

    // Cette methode implemente l operation findAll.
    List<Company> findAll();

    // Cette methode implemente l operation findByMail.
    Optional<Company> findByMail(String mail);

    // Cette methode implemente l operation searchByDenomination.
    List<Company> searchByDenomination(String denominationPart);

    // Cette methode implemente l operation save.
    Company save(Company company);
}
