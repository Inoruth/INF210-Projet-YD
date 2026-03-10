package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldSearchWithoutSectorFilterWhenNoSectorsProvided() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 3)).thenReturn(List.of(new Application()));

        List<Application> applications = service.searchByCriteria(null, (short) 3);

        assertEquals(1, applications.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 3);
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
}
