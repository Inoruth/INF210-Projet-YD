package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationLevelRepository extends JpaRepository<QualificationLevel, Long> {
    
}
