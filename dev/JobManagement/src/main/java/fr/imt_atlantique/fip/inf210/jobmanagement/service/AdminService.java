package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: AdminService
 * Cette interface decrit le contrat du service.
 * Elle liste les operations metier exposees aux autres couches de l'application.
 * L'implementation concrete applique les regles et les acces base associes.
 * Cette separation facilite la maintenance et les tests.
 */

import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;

public interface AdminService {

    // Cette methode implemente l operation findByMail.
    Optional<Admin> findByMail(String mail);

    // Cette methode implemente l operation save.
    Admin save(Admin admin);
}
