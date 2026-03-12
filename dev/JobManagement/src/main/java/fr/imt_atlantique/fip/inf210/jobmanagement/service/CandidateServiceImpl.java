package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: CandidateServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateJpaRepository candidateRepository;

    // Cette methode implemente l operation CandidateServiceImpl.
    public CandidateServiceImpl(CandidateJpaRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    // Cette methode implemente l operation findAll.
    @Override
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    // Cette methode implemente l operation findByMail.
    @Override
    public Optional<Candidate> findByMail(String mail) {
        return candidateRepository.findByAppUserMail(mail);
    }

    // Cette methode implemente l operation searchByLastname.
    @Override
    public List<Candidate> searchByLastname(String lastnamePart) {
        return candidateRepository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc(lastnamePart);
    }

    // Cette methode implemente l operation save.
    @Override
    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }
}
