package fr.imt_atlantique.fip.inf210.JobManagement.service;

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

    @Test
    void shouldReturnEmptyWhenCandidateMailUnknown() {
        when(repository.findByAppUserMail("unknown.candidate@imt-atlantique.fr")).thenReturn(Optional.empty());

        Optional<Candidate> found = service.findByMail("unknown.candidate@imt-atlantique.fr");

        assertFalse(found.isPresent());
        verify(repository).findByAppUserMail("unknown.candidate@imt-atlantique.fr");
    }

    @Test
    void shouldSearchByLastname() {
        when(repository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc("du"))
                .thenReturn(List.of(new Candidate(), new Candidate()));

        List<Candidate> candidates = service.searchByLastname("du");

        assertEquals(2, candidates.size());
        verify(repository).findByLastnameContainingIgnoreCaseOrderByLastnameAsc("du");
    }

    @Test
    void shouldReturnNoCandidateWhenLastnameSearchHasNoMatch() {
        when(repository.findByLastnameContainingIgnoreCaseOrderByLastnameAsc("zzz"))
                .thenReturn(List.of());

        List<Candidate> candidates = service.searchByLastname("zzz");

        assertEquals(0, candidates.size());
        verify(repository).findByLastnameContainingIgnoreCaseOrderByLastnameAsc("zzz");
    }

    @Test
    void shouldFindAllCandidates() {
        when(repository.findAll()).thenReturn(List.of(new Candidate(), new Candidate()));

        List<Candidate> candidates = service.findAll();

        assertEquals(2, candidates.size());
        verify(repository).findAll();
    }

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
