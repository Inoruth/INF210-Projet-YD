package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserJpaRepository extends JpaRepository<AppUser, String> {
    
}
