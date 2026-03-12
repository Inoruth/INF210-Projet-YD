package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

/*
 * Fichier: QualificationLevelRepository
 * Cette interface est un repository Spring Data JPA.
 * Elle fournit l'acces aux donnees pour l'entite concernee.
 * Les methodes declarees ici sont utilisees pour les operations de lecture et d'ecriture.
 * La couche service utilise ce repository pour interagir avec la base.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;

@Repository
public interface QualificationLevelRepository extends JpaRepository<QualificationLevel, Integer> {
    
}
