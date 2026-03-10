package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;

public interface ApplicationService {

    List<Application> findAll();

    Optional<Application> findById(Integer id);

    Application save(Application application);

    List<Application> findByCandidateId(Integer candidateId);

    Optional<Application> findByIdAndCandidateId(Integer id, Integer candidateId);

    void deleteByIdAndCandidateId(Integer id, Integer candidateId);

    List<Application> searchByCriteria(Set<Integer> sectorIds, Short minimumRank);

    List<Application> findMatchingByJobOfferId(Integer jobOfferId);
}
