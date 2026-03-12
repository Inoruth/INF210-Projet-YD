package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationServiceImpl;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AutomaticMessageService;

class ApplicationServiceTest {

    private final ApplicationJpaRepository repository = mock(ApplicationJpaRepository.class);
    private final AutomaticMessageService automaticMessageService = mock(AutomaticMessageService.class);
    private final ApplicationService service = new ApplicationServiceImpl(repository, automaticMessageService);

    @Test
    void shouldDeleteOnlyWhenOwnedByCandidate() {
        Application application = new Application();
        when(repository.findByIdAndCandidateId(10, 4)).thenReturn(Optional.of(application));

        service.deleteByIdAndCandidateId(10, 4);

        verify(repository).delete(application);
    }

    @Test
    void shouldNotDeleteWhenApplicationIsNotOwnedByCandidate() {
        when(repository.findByIdAndCandidateId(10, 4)).thenReturn(Optional.empty());

        service.deleteByIdAndCandidateId(10, 4);

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any(Application.class));
    }

    @Test
    void shouldFindAllApplications() {
        when(repository.findAll()).thenReturn(List.of(new Application(), new Application()));

        List<Application> applications = service.findAll();

        assertEquals(2, applications.size());
        verify(repository).findAll();
    }

    @Test
    void shouldFindApplicationById() {
        Application application = new Application();
        when(repository.findById(24)).thenReturn(Optional.of(application));

        Optional<Application> found = service.findById(24);

        assertTrue(found.isPresent());
        verify(repository).findById(24);
    }

    @Test
    void shouldReturnEmptyWhenApplicationIdDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        Optional<Application> found = service.findById(99);

        assertFalse(found.isPresent());
        verify(repository).findById(99);
    }

    @Test
    void shouldFindApplicationsByCandidateId() {
        when(repository.findByCandidateIdOrderByAppdateDesc(5)).thenReturn(List.of(new Application()));

        List<Application> applications = service.findByCandidateId(5);

        assertEquals(1, applications.size());
        verify(repository).findByCandidateIdOrderByAppdateDesc(5);
    }

    @Test
    void shouldFindApplicationByIdAndCandidateId() {
        Application application = new Application();
        when(repository.findByIdAndCandidateId(11, 5)).thenReturn(Optional.of(application));

        Optional<Application> found = service.findByIdAndCandidateId(11, 5);

        assertTrue(found.isPresent());
        verify(repository).findByIdAndCandidateId(11, 5);
    }

    @Test
    void shouldReturnEmptyWhenApplicationDoesNotBelongToCandidate() {
        when(repository.findByIdAndCandidateId(11, 99)).thenReturn(Optional.empty());

        Optional<Application> found = service.findByIdAndCandidateId(11, 99);

        assertFalse(found.isPresent());
        verify(repository).findByIdAndCandidateId(11, 99);
    }

    @Test
    void shouldSearchWithoutSectorFilterWhenNoSectorsProvided() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 3)).thenReturn(List.of(new Application()));

        List<Application> applications = service.searchByCriteria(null, (short) 3);

        assertEquals(1, applications.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 3);
    }

    @Test
    void shouldSearchWithoutSectorFilterWhenSectorSetIsEmpty() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 4)).thenReturn(List.of(new Application()));

        List<Application> applications = service.searchByCriteria(Collections.emptySet(), (short) 4);

        assertEquals(1, applications.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 4);
    }

    @Test
    void shouldSearchByCriteriaWithNullMinimumRank() {
        Set<Integer> sectorIds = Set.of(9);
        when(repository.searchByCriteria(true, sectorIds, null)).thenReturn(List.of(new Application(), new Application()));

        List<Application> applications = service.searchByCriteria(sectorIds, null);

        assertEquals(2, applications.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq(null));
    }

    @Test
    void shouldSearchWithSectorFilter() {
        Set<Integer> sectorIds = Set.of(1, 2);
        when(repository.searchByCriteria(true, sectorIds, (short) 4)).thenReturn(List.of(new Application()));

        List<Application> applications = service.searchByCriteria(sectorIds, (short) 4);

        assertEquals(1, applications.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq((short) 4));
    }

    @Test
    void shouldTriggerAutomaticMessagesWhenCreatingApplication() {
        Application newApplication = new Application();
        Application savedApplication = new Application();
        savedApplication.setId(24);
        when(repository.save(newApplication)).thenReturn(savedApplication);

        Application result = service.save(newApplication);

        assertEquals(24, result.getId());
        verify(automaticMessageService).sendAutomaticMessagesForNewApplication(savedApplication);
    }

    @Test
    void shouldNotTriggerAutomaticMessagesWhenUpdatingApplication() {
        Application existingApplication = new Application();
        existingApplication.setId(7);
        when(repository.save(existingApplication)).thenReturn(existingApplication);

        service.save(existingApplication);

        verify(automaticMessageService, never()).sendAutomaticMessagesForNewApplication(existingApplication);
    }

    @Test
    void shouldFindMatchingByJobOfferId() {
        when(repository.findMatchingByJobOfferId(12)).thenReturn(List.of(new Application()));

        List<Application> matches = service.findMatchingByJobOfferId(12);

        assertEquals(1, matches.size());
        verify(repository).findMatchingByJobOfferId(12);
    }
}
