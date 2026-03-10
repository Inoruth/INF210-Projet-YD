package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;

public interface JobOfferService {

    List<JobOffer> findAll();

    Optional<JobOffer> findById(Integer id);

    JobOffer save(JobOffer jobOffer);

    List<JobOffer> findByCompanyId(Integer companyId);

    Optional<JobOffer> findByIdAndCompanyId(Integer id, Integer companyId);

    void deleteByIdAndCompanyId(Integer id, Integer companyId);

    List<JobOffer> searchByCriteria(Set<Integer> sectorIds, Short minimumRank);

    List<JobOffer> findMatchingByApplicationId(Integer applicationId);
}
