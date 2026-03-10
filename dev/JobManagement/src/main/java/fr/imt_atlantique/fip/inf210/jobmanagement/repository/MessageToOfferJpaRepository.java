package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;

@Repository
public interface MessageToOfferJpaRepository extends JpaRepository<MessageToOffer, Integer> {

	Optional<MessageToOffer> findByJobOfferIdAndApplicationId(Integer jobOfferId, Integer applicationId);

	List<MessageToOffer> findByJobOfferCompanyIdOrderByPublicationdateDesc(Integer companyId);

	List<MessageToOffer> findByApplicationCandidateIdOrderByPublicationdateDesc(Integer candidateId);
}
