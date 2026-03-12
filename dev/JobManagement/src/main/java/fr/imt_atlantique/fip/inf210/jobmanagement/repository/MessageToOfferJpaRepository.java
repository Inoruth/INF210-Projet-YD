package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

/*
 * Fichier: MessageToOfferJpaRepository
 * Cette interface est un repository Spring Data JPA.
 * Elle fournit l'acces aux donnees pour l'entite concernee.
 * Les methodes declarees ici sont utilisees pour les operations de lecture et d'ecriture.
 * La couche service utilise ce repository pour interagir avec la base.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;

@Repository
public interface MessageToOfferJpaRepository extends JpaRepository<MessageToOffer, Integer> {

	// Cette methode implemente l operation findByJobOfferIdAndApplicationId.
	Optional<MessageToOffer> findByJobOfferIdAndApplicationId(Integer jobOfferId, Integer applicationId);

	// Cette methode implemente l operation findByJobOfferCompanyIdOrderByPublicationdateDesc.
	List<MessageToOffer> findByJobOfferCompanyIdOrderByPublicationdateDesc(Integer companyId);

	// Cette methode implemente l operation findByApplicationCandidateIdOrderByPublicationdateDesc.
	List<MessageToOffer> findByApplicationCandidateIdOrderByPublicationdateDesc(Integer candidateId);
}
