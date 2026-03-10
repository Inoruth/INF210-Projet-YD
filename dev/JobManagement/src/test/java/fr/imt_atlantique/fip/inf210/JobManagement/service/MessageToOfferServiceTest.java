package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldFindByOfferAndApplicationPair() {
        MessageToOffer message = new MessageToOffer();
        when(repository.findByJobOfferIdAndApplicationId(1, 2)).thenReturn(Optional.of(message));

        Optional<MessageToOffer> found = service.findByJobOfferAndApplication(1, 2);

        assertTrue(found.isPresent());
        verify(repository).findByJobOfferIdAndApplicationId(1, 2);
    }

    @Test
    void shouldListByCompany() {
        when(repository.findByJobOfferCompanyIdOrderByPublicationdateDesc(3))
                .thenReturn(List.of(new MessageToOffer(), new MessageToOffer()));

        List<MessageToOffer> messages = service.findByCompanyId(3);

        assertEquals(2, messages.size());
        verify(repository).findByJobOfferCompanyIdOrderByPublicationdateDesc(3);
    }
}
