package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorJpaRepository extends JpaRepository<Sector, Long> {
    
}
