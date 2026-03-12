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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AutomaticMessageService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferServiceImpl;

class JobOfferServiceTest {

    private final JobOfferJpaRepository repository = mock(JobOfferJpaRepository.class);
    private final AutomaticMessageService automaticMessageService = mock(AutomaticMessageService.class);
    private final JobOfferService service = new JobOfferServiceImpl(repository, automaticMessageService);

    @Test
    void shouldDeleteOnlyWhenOwnedByCompany() {
        JobOffer offer = new JobOffer();
        when(repository.findByIdAndCompanyId(10, 4)).thenReturn(Optional.of(offer));

        service.deleteByIdAndCompanyId(10, 4);

        verify(repository).delete(offer);
    }

    @Test
    void shouldNotDeleteWhenOfferIsNotOwnedByCompany() {
        when(repository.findByIdAndCompanyId(10, 4)).thenReturn(Optional.empty());

        service.deleteByIdAndCompanyId(10, 4);

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any(JobOffer.class));
    }

    @Test
    void shouldFindAllOffers() {
        when(repository.findAll()).thenReturn(List.of(new JobOffer(), new JobOffer()));

        List<JobOffer> offers = service.findAll();

        assertEquals(2, offers.size());
        verify(repository).findAll();
    }

    @Test
    void shouldFindOfferById() {
        JobOffer offer = new JobOffer();
        when(repository.findById(42)).thenReturn(Optional.of(offer));

        Optional<JobOffer> found = service.findById(42);

        assertTrue(found.isPresent());
        verify(repository).findById(42);
    }

    @Test
    void shouldReturnEmptyWhenOfferIdDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        Optional<JobOffer> found = service.findById(99);

        assertFalse(found.isPresent());
        verify(repository).findById(99);
    }

    @Test
    void shouldFindOffersByCompanyId() {
        when(repository.findByCompanyIdOrderByPublicationdateDesc(5)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.findByCompanyId(5);

        assertEquals(1, offers.size());
        verify(repository).findByCompanyIdOrderByPublicationdateDesc(5);
    }

    @Test
    void shouldFindOfferByIdAndCompanyId() {
        JobOffer offer = new JobOffer();
        when(repository.findByIdAndCompanyId(11, 5)).thenReturn(Optional.of(offer));

        Optional<JobOffer> found = service.findByIdAndCompanyId(11, 5);

        assertTrue(found.isPresent());
        verify(repository).findByIdAndCompanyId(11, 5);
    }

    @Test
    void shouldReturnEmptyWhenOfferDoesNotBelongToCompany() {
        when(repository.findByIdAndCompanyId(11, 99)).thenReturn(Optional.empty());

        Optional<JobOffer> found = service.findByIdAndCompanyId(11, 99);

        assertFalse(found.isPresent());
        verify(repository).findByIdAndCompanyId(11, 99);
    }

    @Test
    void shouldSearchWithoutSectorFilterWhenNoSectorsProvided() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 3)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(null, (short) 3);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 3);
    }

    @Test
    void shouldSearchWithoutSectorFilterWhenSectorSetIsEmpty() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 4)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(Collections.emptySet(), (short) 4);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 4);
    }

    @Test
    void shouldSearchByCriteriaWithNullMinimumRank() {
        Set<Integer> sectorIds = Set.of(3);
        when(repository.searchByCriteria(true, sectorIds, null)).thenReturn(List.of(new JobOffer(), new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(sectorIds, null);

        assertEquals(2, offers.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq(null));
    }

    @Test
    void shouldSearchWithSectorFilter() {
        Set<Integer> sectorIds = Set.of(1, 2);
        when(repository.searchByCriteria(true, sectorIds, (short) 4)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(sectorIds, (short) 4);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq((short) 4));
    }

    @Test
    void shouldTriggerAutomaticMessagesWhenCreatingOffer() {
        JobOffer newOffer = new JobOffer();
        JobOffer savedOffer = new JobOffer();
        savedOffer.setId(42);
        when(repository.save(newOffer)).thenReturn(savedOffer);

        JobOffer result = service.save(newOffer);

        assertEquals(42, result.getId());
        verify(automaticMessageService).sendAutomaticMessagesForNewOffer(savedOffer);
    }

    @Test
    void shouldNotTriggerAutomaticMessagesWhenUpdatingOffer() {
        JobOffer existingOffer = new JobOffer();
        existingOffer.setId(10);
        when(repository.save(existingOffer)).thenReturn(existingOffer);

        service.save(existingOffer);

        verify(automaticMessageService, never()).sendAutomaticMessagesForNewOffer(existingOffer);
    }

    @Test
    void shouldFindMatchingByApplicationId() {
        when(repository.findMatchingByApplicationId(12)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> matches = service.findMatchingByApplicationId(12);

        assertEquals(1, matches.size());
        verify(repository).findMatchingByApplicationId(12);
    }
}
