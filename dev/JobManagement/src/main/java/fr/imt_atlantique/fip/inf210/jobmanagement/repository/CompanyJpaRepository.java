package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Integer> {

	Optional<Company> findByAppUserMail(String mail);

	List<Company> findByDenominationContainingIgnoreCaseOrderByDenominationAsc(String denominationPart);
}
