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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToApplicationServiceImpl;

class MessageToApplicationServiceTest {

    private final MessageToApplicationJpaRepository repository = mock(MessageToApplicationJpaRepository.class);
    private final MessageToApplicationService service = new MessageToApplicationServiceImpl(repository);

    @Test
    void shouldFindByApplicationAndOfferPair() {
        MessageToApplication message = new MessageToApplication();
        when(repository.findByApplicationIdAndJobOfferId(1, 2)).thenReturn(Optional.of(message));

        Optional<MessageToApplication> found = service.findByApplicationAndJobOffer(1, 2);

        assertTrue(found.isPresent());
        verify(repository).findByApplicationIdAndJobOfferId(1, 2);
    }

    @Test
    void shouldReturnEmptyForUnknownApplicationAndOfferPair() {
        when(repository.findByApplicationIdAndJobOfferId(1, 99)).thenReturn(Optional.empty());

        Optional<MessageToApplication> found = service.findByApplicationAndJobOffer(1, 99);

        assertFalse(found.isPresent());
        verify(repository).findByApplicationIdAndJobOfferId(1, 99);
    }

    @Test
    void shouldListByCandidate() {
        when(repository.findByApplicationCandidateIdOrderByPublicationdateDesc(3))
                .thenReturn(List.of(new MessageToApplication(), new MessageToApplication()));

        List<MessageToApplication> messages = service.findByCandidateId(3);

        assertEquals(2, messages.size());
        verify(repository).findByApplicationCandidateIdOrderByPublicationdateDesc(3);
    }

    @Test
    void shouldReturnEmptyListWhenCandidateHasNoApplicationMessages() {
        when(repository.findByApplicationCandidateIdOrderByPublicationdateDesc(99)).thenReturn(List.of());

        List<MessageToApplication> messages = service.findByCandidateId(99);

        assertEquals(0, messages.size());
        verify(repository).findByApplicationCandidateIdOrderByPublicationdateDesc(99);
    }

    @Test
    void shouldListByCompany() {
        when(repository.findByJobOfferCompanyIdOrderByPublicationdateDesc(4))
                .thenReturn(List.of(new MessageToApplication()));

        List<MessageToApplication> messages = service.findByCompanyId(4);

        assertEquals(1, messages.size());
        verify(repository).findByJobOfferCompanyIdOrderByPublicationdateDesc(4);
    }

    @Test
    void shouldSaveMessageToApplication() {
        MessageToApplication message = new MessageToApplication();
        when(repository.save(message)).thenReturn(message);

        MessageToApplication saved = service.save(message);

        assertEquals(message, saved);
        verify(repository).save(message);
    }
}
