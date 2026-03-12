package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: MessageToApplicationServiceTest
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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToApplicationServiceImpl;

class MessageToApplicationServiceTest {

    private final MessageToApplicationJpaRepository repository = mock(MessageToApplicationJpaRepository.class);
    private final MessageToApplicationService service = new MessageToApplicationServiceImpl(repository);

    // Ce test verifie le comportement de shouldFindByApplicationAndOfferPair.
    @Test
    void shouldFindByApplicationAndOfferPair() {
        MessageToApplication message = new MessageToApplication();
        when(repository.findByApplicationIdAndJobOfferId(1, 2)).thenReturn(Optional.of(message));

        Optional<MessageToApplication> found = service.findByApplicationAndJobOffer(1, 2);

        assertTrue(found.isPresent());
        verify(repository).findByApplicationIdAndJobOfferId(1, 2);
    }

    // Ce test verifie le comportement de shouldReturnEmptyForUnknownApplicationAndOfferPair.
    @Test
    void shouldReturnEmptyForUnknownApplicationAndOfferPair() {
        when(repository.findByApplicationIdAndJobOfferId(1, 99)).thenReturn(Optional.empty());

        Optional<MessageToApplication> found = service.findByApplicationAndJobOffer(1, 99);

        assertFalse(found.isPresent());
        verify(repository).findByApplicationIdAndJobOfferId(1, 99);
    }

    // Ce test verifie le comportement de shouldListByCandidate.
    @Test
    void shouldListByCandidate() {
        when(repository.findByApplicationCandidateIdOrderByPublicationdateDesc(3))
                .thenReturn(List.of(new MessageToApplication(), new MessageToApplication()));

        List<MessageToApplication> messages = service.findByCandidateId(3);

        assertEquals(2, messages.size());
        verify(repository).findByApplicationCandidateIdOrderByPublicationdateDesc(3);
    }

    // Ce test verifie le comportement de shouldReturnEmptyListWhenCandidateHasNoApplicationMessages.
    @Test
    void shouldReturnEmptyListWhenCandidateHasNoApplicationMessages() {
        when(repository.findByApplicationCandidateIdOrderByPublicationdateDesc(99)).thenReturn(List.of());

        List<MessageToApplication> messages = service.findByCandidateId(99);

        assertEquals(0, messages.size());
        verify(repository).findByApplicationCandidateIdOrderByPublicationdateDesc(99);
    }

    // Ce test verifie le comportement de shouldListByCompany.
    @Test
    void shouldListByCompany() {
        when(repository.findByJobOfferCompanyIdOrderByPublicationdateDesc(4))
                .thenReturn(List.of(new MessageToApplication()));

        List<MessageToApplication> messages = service.findByCompanyId(4);

        assertEquals(1, messages.size());
        verify(repository).findByJobOfferCompanyIdOrderByPublicationdateDesc(4);
    }

    // Ce test verifie le comportement de shouldSaveMessageToApplication.
    @Test
    void shouldSaveMessageToApplication() {
        MessageToApplication message = new MessageToApplication();
        when(repository.save(message)).thenReturn(message);

        MessageToApplication saved = service.save(message);

        assertEquals(message, saved);
        verify(repository).save(message);
    }
}
