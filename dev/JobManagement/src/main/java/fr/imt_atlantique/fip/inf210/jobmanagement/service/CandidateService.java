package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;

public interface CandidateService {

    List<Candidate> findAll();

    Optional<Candidate> findByMail(String mail);

    List<Candidate> searchByLastname(String lastnamePart);

    Candidate save(Candidate candidate);
}
