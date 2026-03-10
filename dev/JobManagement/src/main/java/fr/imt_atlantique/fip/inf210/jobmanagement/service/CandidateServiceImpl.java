package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateJpaRepository candidateRepository;

    public CandidateServiceImpl(CandidateJpaRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    @Override
    public Optional<Candidate> findByMail(String mail) {
        return candidateRepository.findByAppUserMail(mail);
    }

    @Override
    public List<Candidate> searchByLastname(String lastnamePart) {
        return candidateRepository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc(lastnamePart);
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }
}
