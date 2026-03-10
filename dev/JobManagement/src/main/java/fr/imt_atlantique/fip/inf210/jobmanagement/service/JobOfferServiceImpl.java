package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferJpaRepository jobOfferRepository;

    public JobOfferServiceImpl(JobOfferJpaRepository jobOfferRepository) {
        this.jobOfferRepository = jobOfferRepository;
    }

    @Override
    public List<JobOffer> findAll() {
        return jobOfferRepository.findAll();
    }

    @Override
    public Optional<JobOffer> findById(Integer id) {
        return jobOfferRepository.findById(id);
    }

    @Override
    public JobOffer save(JobOffer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }

    @Override
    public List<JobOffer> findByCompanyId(Integer companyId) {
        return jobOfferRepository.findByCompanyIdOrderByPublicationdateDesc(companyId);
    }

    @Override
    public Optional<JobOffer> findByIdAndCompanyId(Integer id, Integer companyId) {
        return jobOfferRepository.findByIdAndCompanyId(id, companyId);
    }

    @Override
    public void deleteByIdAndCompanyId(Integer id, Integer companyId) {
        jobOfferRepository.findByIdAndCompanyId(id, companyId).ifPresent(jobOfferRepository::delete);
    }

    @Override
    public List<JobOffer> searchByCriteria(Set<Integer> sectorIds, Short minimumRank) {
        boolean filterBySectors = sectorIds != null && !sectorIds.isEmpty();
        Set<Integer> safeSectorIds = filterBySectors ? sectorIds : Collections.emptySet();
        return jobOfferRepository.searchByCriteria(filterBySectors, safeSectorIds, minimumRank);
    }

    @Override
    public List<JobOffer> findMatchingByApplicationId(Integer applicationId) {
        return jobOfferRepository.findMatchingByApplicationId(applicationId);
    }
}
