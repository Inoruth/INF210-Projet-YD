package fr.imt_atlantique.fip.inf210.JobManagement.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;

@SpringBootTest
@Transactional
class CandidateJpaRepositoryTest {

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private AppUserJpaRepository appUserRepository;

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

    private void createCandidate(String mail, String lastname, String firstname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        candidateRepository.save(new Candidate(user, lastname, firstname, "Rennes"));
    }
}
