package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;

@Repository
public interface ApplicationJpaRepository extends JpaRepository<Application, Integer> {

	List<Application> findByCandidateIdOrderByAppdateDesc(Integer candidateId);

	Optional<Application> findByIdAndCandidateId(Integer id, Integer candidateId);

	@Query("""
			select distinct a
			from Application a
			left join a.sectors s
			where (:filterBySectors = false or s.id in :sectorIds)
			and (:minimumRank is null or a.qualificationLevel.rank >= :minimumRank)
			order by a.appdate desc
			""")
	List<Application> searchByCriteria(@Param("filterBySectors") boolean filterBySectors,
									   @Param("sectorIds") Set<Integer> sectorIds,
									   @Param("minimumRank") Short minimumRank);

	@Query("""
			select distinct a
			from Application a
			join a.sectors s
			where a.qualificationLevel.rank >= (
				select jo.qualificationLevel.rank
				from JobOffer jo
				where jo.id = :jobOfferId
			)
			and s.id in (
				select offerSector.id
				from JobOffer jo
				join jo.sectors offerSector
				where jo.id = :jobOfferId
			)
			order by a.appdate desc
			""")
	List<Application> findMatchingByJobOfferId(@Param("jobOfferId") Integer jobOfferId);
}
