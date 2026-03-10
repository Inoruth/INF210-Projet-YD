package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;

@Repository
public interface MessageToApplicationJpaRepository extends JpaRepository<MessageToApplication, Integer> {

	Optional<MessageToApplication> findByApplicationIdAndJobOfferId(Integer applicationId, Integer jobOfferId);

	List<MessageToApplication> findByJobOfferCompanyIdOrderByPublicationdateDesc(Integer companyId);

	List<MessageToApplication> findByApplicationCandidateIdOrderByPublicationdateDesc(Integer candidateId);
}
