package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: JobOfferServiceTest
 * Cette classe teste la logique du service en mode unitaire.
 * Les dependances sont simulees pour isoler le comportement metier.
 * Les scenarios couvrent les cas nominaux et les cas d'erreur.
 * Les assertions verifient les resultats et les interactions attendues.
 */

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

    // Ce test verifie le comportement de shouldDeleteOnlyWhenOwnedByCompany.
    @Test
    void shouldDeleteOnlyWhenOwnedByCompany() {
        JobOffer offer = new JobOffer();
        when(repository.findByIdAndCompanyId(10, 4)).thenReturn(Optional.of(offer));

        service.deleteByIdAndCompanyId(10, 4);

        verify(repository).delete(offer);
    }

    // Ce test verifie le comportement de shouldNotDeleteWhenOfferIsNotOwnedByCompany.
    @Test
    void shouldNotDeleteWhenOfferIsNotOwnedByCompany() {
        when(repository.findByIdAndCompanyId(10, 4)).thenReturn(Optional.empty());

        service.deleteByIdAndCompanyId(10, 4);

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any(JobOffer.class));
    }

    // Ce test verifie le comportement de shouldFindAllOffers.
    @Test
    void shouldFindAllOffers() {
        when(repository.findAll()).thenReturn(List.of(new JobOffer(), new JobOffer()));

        List<JobOffer> offers = service.findAll();

        assertEquals(2, offers.size());
        verify(repository).findAll();
    }

    // Ce test verifie le comportement de shouldFindOfferById.
    @Test
    void shouldFindOfferById() {
        JobOffer offer = new JobOffer();
        when(repository.findById(42)).thenReturn(Optional.of(offer));

        Optional<JobOffer> found = service.findById(42);

        assertTrue(found.isPresent());
        verify(repository).findById(42);
    }

    // Ce test verifie le comportement de shouldReturnEmptyWhenOfferIdDoesNotExist.
    @Test
    void shouldReturnEmptyWhenOfferIdDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        Optional<JobOffer> found = service.findById(99);

        assertFalse(found.isPresent());
        verify(repository).findById(99);
    }

    // Ce test verifie le comportement de shouldFindOffersByCompanyId.
    @Test
    void shouldFindOffersByCompanyId() {
        when(repository.findByCompanyIdOrderByPublicationdateDesc(5)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.findByCompanyId(5);

        assertEquals(1, offers.size());
        verify(repository).findByCompanyIdOrderByPublicationdateDesc(5);
    }

    // Ce test verifie le comportement de shouldFindOfferByIdAndCompanyId.
    @Test
    void shouldFindOfferByIdAndCompanyId() {
        JobOffer offer = new JobOffer();
        when(repository.findByIdAndCompanyId(11, 5)).thenReturn(Optional.of(offer));

        Optional<JobOffer> found = service.findByIdAndCompanyId(11, 5);

        assertTrue(found.isPresent());
        verify(repository).findByIdAndCompanyId(11, 5);
    }

    // Ce test verifie le comportement de shouldReturnEmptyWhenOfferDoesNotBelongToCompany.
    @Test
    void shouldReturnEmptyWhenOfferDoesNotBelongToCompany() {
        when(repository.findByIdAndCompanyId(11, 99)).thenReturn(Optional.empty());

        Optional<JobOffer> found = service.findByIdAndCompanyId(11, 99);

        assertFalse(found.isPresent());
        verify(repository).findByIdAndCompanyId(11, 99);
    }

    // Ce test verifie le comportement de shouldSearchWithoutSectorFilterWhenNoSectorsProvided.
    @Test
    void shouldSearchWithoutSectorFilterWhenNoSectorsProvided() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 3)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(null, (short) 3);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 3);
    }

    // Ce test verifie le comportement de shouldSearchWithoutSectorFilterWhenSectorSetIsEmpty.
    @Test
    void shouldSearchWithoutSectorFilterWhenSectorSetIsEmpty() {
        when(repository.searchByCriteria(false, Collections.emptySet(), (short) 4)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(Collections.emptySet(), (short) 4);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(false, Collections.emptySet(), (short) 4);
    }

    // Ce test verifie le comportement de shouldSearchByCriteriaWithNullMinimumRank.
    @Test
    void shouldSearchByCriteriaWithNullMinimumRank() {
        Set<Integer> sectorIds = Set.of(3);
        when(repository.searchByCriteria(true, sectorIds, null)).thenReturn(List.of(new JobOffer(), new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(sectorIds, null);

        assertEquals(2, offers.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq(null));
    }

    // Ce test verifie le comportement de shouldSearchWithSectorFilter.
    @Test
    void shouldSearchWithSectorFilter() {
        Set<Integer> sectorIds = Set.of(1, 2);
        when(repository.searchByCriteria(true, sectorIds, (short) 4)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> offers = service.searchByCriteria(sectorIds, (short) 4);

        assertEquals(1, offers.size());
        verify(repository).searchByCriteria(eq(true), eq(sectorIds), eq((short) 4));
    }

    // Ce test verifie le comportement de shouldTriggerAutomaticMessagesWhenCreatingOffer.
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

    // Ce test verifie le comportement de shouldNotTriggerAutomaticMessagesWhenUpdatingOffer.
    @Test
    void shouldNotTriggerAutomaticMessagesWhenUpdatingOffer() {
        JobOffer existingOffer = new JobOffer();
        existingOffer.setId(10);
        when(repository.save(existingOffer)).thenReturn(existingOffer);

        service.save(existingOffer);

        verify(automaticMessageService, never()).sendAutomaticMessagesForNewOffer(existingOffer);
    }

    // Ce test verifie le comportement de shouldFindMatchingByApplicationId.
    @Test
    void shouldFindMatchingByApplicationId() {
        when(repository.findMatchingByApplicationId(12)).thenReturn(List.of(new JobOffer()));

        List<JobOffer> matches = service.findMatchingByApplicationId(12);

        assertEquals(1, matches.size());
        verify(repository).findMatchingByApplicationId(12);
    }
}
