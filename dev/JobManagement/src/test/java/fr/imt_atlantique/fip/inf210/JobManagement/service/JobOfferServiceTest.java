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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferServiceImpl;

class JobOfferServiceTest {

    private final JobOfferJpaRepository repository = mock(JobOfferJpaRepository.class);
    private final JobOfferService service = new JobOfferServiceImpl(repository);

    @Test
    void shouldDeleteOnlyWhenOwnedByCompany() {
        JobOffer offer = new JobOffer();
        when(repository.findByIdAndCompanyId(10, 4)).thenReturn(Optional.of(offer));

        service.deleteByIdAndCompanyId(10, 4);

        verify(repository).delete(offer);
    }

    @Test
    void shouldSearchWithoutSectorFilterWhenNoSectorsProvided() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 3)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(null, (short) 3);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 3);
    }

    @Test
    void shouldSearchWithSectorFilter() {
        Set<Integer> sectorIds = Set.of(1, 2);
        when(repository.searchByCriteria(true, sectorIds, (short) 4)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(sectorIds, (short) 4);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq((short) 4));
    }
}
