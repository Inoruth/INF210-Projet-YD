package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: MessageToOfferServiceTest
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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToOfferServiceImpl;

class MessageToOfferServiceTest {

    private final MessageToOfferJpaRepository repository = mock(MessageToOfferJpaRepository.class);
    private final MessageToOfferService service = new MessageToOfferServiceImpl(repository);

    // Ce test verifie le comportement de shouldFindByOfferAndApplicationPair.
    @Test
    void shouldFindByOfferAndApplicationPair() {
        MessageToOffer message = new MessageToOffer();
        when(repository.findByJobOfferIdAndApplicationId(1, 2)).thenReturn(Optional.of(message));

        Optional<MessageToOffer> found = service.findByJobOfferAndApplication(1, 2);

        assertTrue(found.isPresent());
        verify(repository).findByJobOfferIdAndApplicationId(1, 2);
    }

    // Ce test verifie le comportement de shouldReturnEmptyForUnknownOfferAndApplicationPair.
    @Test
    void shouldReturnEmptyForUnknownOfferAndApplicationPair() {
        when(repository.findByJobOfferIdAndApplicationId(1, 99)).thenReturn(Optional.empty());

        Optional<MessageToOffer> found = service.findByJobOfferAndApplication(1, 99);

        assertFalse(found.isPresent());
        verify(repository).findByJobOfferIdAndApplicationId(1, 99);
    }

    // Ce test verifie le comportement de shouldListByCompany.
    @Test
    void shouldListByCompany() {
        when(repository.findByJobOfferCompanyIdOrderByPublicationdateDesc(3))
                .thenReturn(List.of(new MessageToOffer(), new MessageToOffer()));

        List<MessageToOffer> messages = service.findByCompanyId(3);

        assertEquals(2, messages.size());
        verify(repository).findByJobOfferCompanyIdOrderByPublicationdateDesc(3);
    }

    // Ce test verifie le comportement de shouldReturnEmptyListWhenCompanyHasNoOfferMessages.
    @Test
    void shouldReturnEmptyListWhenCompanyHasNoOfferMessages() {
        when(repository.findByJobOfferCompanyIdOrderByPublicationdateDesc(99)).thenReturn(List.of());

        List<MessageToOffer> messages = service.findByCompanyId(99);

        assertEquals(0, messages.size());
        verify(repository).findByJobOfferCompanyIdOrderByPublicationdateDesc(99);
    }

    // Ce test verifie le comportement de shouldListByCandidate.
    @Test
    void shouldListByCandidate() {
        when(repository.findByApplicationCandidateIdOrderByPublicationdateDesc(4))
                .thenReturn(List.of(new MessageToOffer()));

        List<MessageToOffer> messages = service.findByCandidateId(4);

        assertEquals(1, messages.size());
        verify(repository).findByApplicationCandidateIdOrderByPublicationdateDesc(4);
    }

    // Ce test verifie le comportement de shouldSaveMessageToOffer.
    @Test
    void shouldSaveMessageToOffer() {
        MessageToOffer message = new MessageToOffer();
        when(repository.save(message)).thenReturn(message);

        MessageToOffer saved = service.save(message);

        assertEquals(message, saved);
        verify(repository).save(message);
    }
}
