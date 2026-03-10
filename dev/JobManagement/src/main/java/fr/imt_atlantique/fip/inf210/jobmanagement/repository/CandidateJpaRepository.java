package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;

@Repository
public interface CandidateJpaRepository extends JpaRepository<Candidate, Integer> {

	Optional<Candidate> findByAppUserMail(String mail);

	List<Candidate> findByLastnameContainingIgnoreCaseOrderByLastnameAsc(String lastnamePart);
}
