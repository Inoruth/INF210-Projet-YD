package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

/*
 * Fichier: CandidateJpaRepository
 * Cette interface est un repository Spring Data JPA.
 * Elle fournit l'acces aux donnees pour l'entite concernee.
 * Les methodes declarees ici sont utilisees pour les operations de lecture et d'ecriture.
 * La couche service utilise ce repository pour interagir avec la base.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;

@Repository
public interface CandidateJpaRepository extends JpaRepository<Candidate, Integer> {

	// Cette methode implemente l operation findByAppUserMail.
	Optional<Candidate> findByAppUserMail(String mail);

	// Cette methode implemente l operation findByLastnameContainingIgnoreCaseOrderByLastnameAsc.
	List<Candidate> findByLastnameContainingIgnoreCaseOrderByLastnameAsc(String lastnamePart);
}
