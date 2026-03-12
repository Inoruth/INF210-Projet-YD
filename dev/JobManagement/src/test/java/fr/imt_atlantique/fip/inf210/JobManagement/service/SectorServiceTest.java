package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.SectorServiceImpl;

class SectorServiceTest {

    private final SectorJpaRepository repository = mock(SectorJpaRepository.class);
    private final SectorServiceImpl service = new SectorServiceImpl();

    private void setUp() {
        ReflectionTestUtils.setField(service, "sectorRepository", repository);
    }

    @Test
    void shouldReturnAllSectors() {
        setUp();
        when(repository.findAll()).thenReturn(List.of(new Sector("IT"), new Sector("Finance")));

        List<Sector> sectors = service.getAllSectors();

        assertEquals(2, sectors.size());
        verify(repository).findAll();
    }

    @Test
    void shouldReturnNoSectorsWhenRepositoryIsEmpty() {
        setUp();
        when(repository.findAll()).thenReturn(List.of());

        List<Sector> sectors = service.getAllSectors();

        assertEquals(0, sectors.size());
        verify(repository).findAll();
    }
}
