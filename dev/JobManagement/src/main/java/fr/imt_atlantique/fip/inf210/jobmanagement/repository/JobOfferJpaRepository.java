package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

/*
 * Fichier: JobOfferJpaRepository
 * Cette interface est un repository Spring Data JPA.
 * Elle fournit l'acces aux donnees pour l'entite concernee.
 * Les methodes declarees ici sont utilisees pour les operations de lecture et d'ecriture.
 * La couche service utilise ce repository pour interagir avec la base.
 */

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;

@Repository
public interface JobOfferJpaRepository extends JpaRepository<JobOffer, Integer> {

	// Cette methode implemente l operation findByCompanyIdOrderByPublicationdateDesc.
	List<JobOffer> findByCompanyIdOrderByPublicationdateDesc(Integer companyId);

	// Cette methode implemente l operation findByIdAndCompanyId.
	Optional<JobOffer> findByIdAndCompanyId(Integer id, Integer companyId);

	@Query("""
			select distinct jo
			from JobOffer jo
			left join jo.sectors s
			where (:filterBySectors = false or s.id in :sectorIds)
			and (:minimumRank is null or jo.qualificationLevel.rank >= :minimumRank)
			order by jo.publicationdate desc
			""")
	List<JobOffer> searchByCriteria(@Param("filterBySectors") boolean filterBySectors,
									@Param("sectorIds") Set<Integer> sectorIds,
									@Param("minimumRank") Short minimumRank);

	@Query("""
			select distinct jo
			from JobOffer jo
			join jo.sectors s
			where jo.qualificationLevel.rank <= (
				select a.qualificationLevel.rank
				from Application a
				where a.id = :applicationId
			)
			and s.id in (
				select appSector.id
				from Application a
				join a.sectors appSector
				where a.id = :applicationId
			)
			order by jo.publicationdate desc
			""")
	// Cette methode implemente l operation findMatchingByApplicationId.
	List<JobOffer> findMatchingByApplicationId(@Param("applicationId") Integer applicationId);
}
