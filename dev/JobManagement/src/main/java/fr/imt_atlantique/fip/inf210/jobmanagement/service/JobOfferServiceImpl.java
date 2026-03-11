package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;

@Service
@Transactional(readOnly = true)
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferJpaRepository jobOfferRepository;
    private final AutomaticMessageService automaticMessageService;

    public JobOfferServiceImpl(JobOfferJpaRepository jobOfferRepository,
                               AutomaticMessageService automaticMessageService) {
        this.jobOfferRepository = jobOfferRepository;
        this.automaticMessageService = automaticMessageService;
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
    @Transactional
    public JobOffer save(JobOffer jobOffer) {
        boolean isNewOffer = jobOffer.getId() == null;
        JobOffer saved = jobOfferRepository.save(jobOffer);

        if (isNewOffer) {
            automaticMessageService.sendAutomaticMessagesForNewOffer(saved);
        }

        return saved;
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
    @Transactional
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
