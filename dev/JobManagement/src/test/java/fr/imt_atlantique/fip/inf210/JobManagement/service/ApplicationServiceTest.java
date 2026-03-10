package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationServiceImpl;

class ApplicationServiceTest {

    private final ApplicationJpaRepository repository = mock(ApplicationJpaRepository.class);
    private final ApplicationService service = new ApplicationServiceImpl(repository);

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
}
