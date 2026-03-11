package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;

@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationJpaRepository applicationRepository;
    private final AutomaticMessageService automaticMessageService;

    public ApplicationServiceImpl(ApplicationJpaRepository applicationRepository,
                                  AutomaticMessageService automaticMessageService) {
        this.applicationRepository = applicationRepository;
        this.automaticMessageService = automaticMessageService;
    }

    @Override
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    @Override
    public Optional<Application> findById(Integer id) {
        return applicationRepository.findById(id);
    }

    @Override
    @Transactional
    public Application save(Application application) {
        boolean isNewApplication = application.getId() == null;
        Application saved = applicationRepository.save(application);

        if (isNewApplication) {
            automaticMessageService.sendAutomaticMessagesForNewApplication(saved);
        }

        return saved;
    }

    @Override
    public List<Application> findByCandidateId(Integer candidateId) {
        return applicationRepository.findByCandidateIdOrderByAppdateDesc(candidateId);
    }

    @Override
    public Optional<Application> findByIdAndCandidateId(Integer id, Integer candidateId) {
        return applicationRepository.findByIdAndCandidateId(id, candidateId);
    }

    @Override
    @Transactional
    public void deleteByIdAndCandidateId(Integer id, Integer candidateId) {
        applicationRepository.findByIdAndCandidateId(id, candidateId).ifPresent(applicationRepository::delete);
    }

    @Override
    public List<Application> searchByCriteria(Set<Integer> sectorIds, Short minimumRank) {
        boolean filterBySectors = sectorIds != null && !sectorIds.isEmpty();
        Set<Integer> safeSectorIds = filterBySectors ? sectorIds : Collections.emptySet();
        return applicationRepository.searchByCriteria(filterBySectors, safeSectorIds, minimumRank);
    }

    @Override
    public List<Application> findMatchingByJobOfferId(Integer jobOfferId) {
        return applicationRepository.findMatchingByJobOfferId(jobOfferId);
    }
}
