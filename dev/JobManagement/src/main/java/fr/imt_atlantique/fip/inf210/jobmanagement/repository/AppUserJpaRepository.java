package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;

@Repository
public interface AppUserJpaRepository extends JpaRepository<AppUser, Integer> {

	Optional<AppUser> findByMail(String mail);

	boolean existsByMail(String mail);
}
