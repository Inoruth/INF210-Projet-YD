package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: CandidateServiceTest
 * Cette classe teste la logique du service en mode unitaire.
 * Les dependances sont simulees pour isoler le comportement metier.
 * Les scenarios couvrent les cas nominaux et les cas d'erreur.
 * Les assertions verifient les resultats et les interactions attendues.
 */

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CandidateService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CandidateServiceImpl;

class CandidateServiceTest {

    private final CandidateJpaRepository repository = mock(CandidateJpaRepository.class);
    private final CandidateService service = new CandidateServiceImpl(repository);

    // Ce test verifie le comportement de shouldFindByMail.
    @Test
    void shouldFindByMail() {
        Candidate candidate = new Candidate();
        candidate.setLastname("Durand");
        when(repository.findByAppUserMail("candidate.service@imt-atlantique.fr")).thenReturn(Optional.of(candidate));

        Optional<Candidate> found = service.findByMail("candidate.service@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("Durand", found.get().getLastname());
        verify(repository).findByAppUserMail("candidate.service@imt-atlantique.fr");
    }

    // Ce test verifie le comportement de shouldReturnEmptyWhenCandidateMailUnknown.
    @Test
    void shouldReturnEmptyWhenCandidateMailUnknown() {
        when(repository.findByAppUserMail("unknown.candidate@imt-atlantique.fr")).thenReturn(Optional.empty());

        Optional<Candidate> found = service.findByMail("unknown.candidate@imt-atlantique.fr");

        assertFalse(found.isPresent());
        verify(repository).findByAppUserMail("unknown.candidate@imt-atlantique.fr");
    }

    // Ce test verifie le comportement de shouldSearchByLastname.
    @Test
    void shouldSearchByLastname() {
        when(repository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc("du"))
                .thenReturn(List.of(new Candidate(), new Candidate()));

        List<Candidate> candidates = service.searchByLastname("du");

        assertEquals(2, candidates.size());
        verify(repository).findByLastnameContainingIgnoreCaseOrderByLastnameAsc("du");
    }

    // Ce test verifie le comportement de shouldReturnNoCandidateWhenLastnameSearchHasNoMatch.
    @Test
    void shouldReturnNoCandidateWhenLastnameSearchHasNoMatch() {
        when(repository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc("zzz"))
                .thenReturn(List.of());

        List<Candidate> candidates = service.searchByLastname("zzz");

        assertEquals(0, candidates.size());
        verify(repository).findByLastnameContainingIgnoreCaseOrderByLastnameAsc("zzz");
    }

    // Ce test verifie le comportement de shouldFindAllCandidates.
    @Test
    void shouldFindAllCandidates() {
        when(repository.findAll()).thenReturn(List.of(new Candidate(), new Candidate()));

        List<Candidate> candidates = service.findAll();

        assertEquals(2, candidates.size());
        verify(repository).findAll();
    }

    // Ce test verifie le comportement de shouldSaveCandidate.
    @Test
    void shouldSaveCandidate() {
        Candidate candidate = new Candidate();
        candidate.setLastname("Saved");
        when(repository.save(candidate)).thenReturn(candidate);

        Candidate saved = service.save(candidate);

        assertEquals(candidate, saved);
        verify(repository).save(candidate);
    }
}
