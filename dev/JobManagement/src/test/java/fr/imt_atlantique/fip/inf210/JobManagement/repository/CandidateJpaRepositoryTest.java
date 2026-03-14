package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: CandidateJpaRepositoryTest
 * Cette classe teste les requetes JPA du repository cible.
 * Les tests s'executent sur une base de test pour valider la persistance et les recherches.
 * Les assertions controlent la coherence des resultats retournes par les methodes.
 * L'objectif est de securiser le mapping et le comportement des requetes.
 */

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;

@DataJpaTest
class CandidateJpaRepositoryTest {

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    // Ce test verifie le comportement de shouldFindCandidateByAppUserMail.
    @Test
    void shouldFindCandidateByAppUserMail() {
        AppUser user = appUserRepository.save(new AppUser(
                "candidate.repo.test@imt-atlantique.fr",
                "pwd123",
                AppUser.UserType.applicant
        ));
        candidateRepository.save(new Candidate(user, "Durand", "Alice", "Brest"));

        Optional<Candidate> found = candidateRepository.findByAppUserMail("candidate.repo.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("Durand", found.get().getLastname());
    }

    // Ce test verifie le comportement de shouldSearchCandidatesByLastname.
    @Test
    void shouldSearchCandidatesByLastname() {
        createCandidate("candidate.one@imt-atlantique.fr", "Dubois", "Zoe");
        createCandidate("candidate.two@imt-atlantique.fr", "Dumas", "Leo");
        createCandidate("candidate.three@imt-atlantique.fr", "Martin", "Eva");

        List<Candidate> candidates = candidateRepository
                .findByLastnameContainingIgnoreCaseOrderByLastnameAsc("du");

        assertEquals(2, candidates.size());
        assertEquals("Dubois", candidates.get(0).getLastname());
        assertEquals("Dumas", candidates.get(1).getLastname());
    }

    // Ce test verifie le comportement de createCandidate.
    private void createCandidate(String mail, String lastname, String firstname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        candidateRepository.save(new Candidate(user, lastname, firstname, "Rennes"));
    }
}
