package fr.imt_atlantique.fip.inf210.jobmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;

@Repository
public interface QualificationLevelRepository extends JpaRepository<QualificationLevel, Integer> {
    
}
