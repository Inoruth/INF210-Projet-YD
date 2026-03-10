package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AutomaticMessageService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AutomaticMessageServiceImpl;

class AutomaticMessageServiceTest {

    private final ApplicationJpaRepository applicationRepository = mock(ApplicationJpaRepository.class);
    private final JobOfferJpaRepository jobOfferRepository = mock(JobOfferJpaRepository.class);
    private final MessageToOfferJpaRepository messageToOfferRepository = mock(MessageToOfferJpaRepository.class);
    private final MessageToApplicationJpaRepository messageToApplicationRepository = mock(MessageToApplicationJpaRepository.class);

    private final AutomaticMessageService service = new AutomaticMessageServiceImpl(
            applicationRepository,
            jobOfferRepository,
            messageToOfferRepository,
            messageToApplicationRepository
    );

    @Test
    void shouldSendAutomaticMessagesForNewOfferWithoutDuplicates() {
        JobOffer offer = new JobOffer();
        offer.setId(1);
        offer.setTitle("Backend Engineer");

        Application first = new Application();
        first.setId(10);
        Application second = new Application();
        second.setId(20);

        when(applicationRepository.findMatchingByJobOfferId(1)).thenReturn(List.of(first, second));
        when(messageToOfferRepository.findByJobOfferIdAndApplicationId(1, 10)).thenReturn(Optional.empty());
        when(messageToOfferRepository.findByJobOfferIdAndApplicationId(1, 20)).thenReturn(Optional.of(new MessageToOffer()));
        when(messageToOfferRepository.save(any(MessageToOffer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int sent = service.sendAutomaticMessagesForNewOffer(offer);

        assertEquals(1, sent);
        ArgumentCaptor<MessageToOffer> captor = ArgumentCaptor.forClass(MessageToOffer.class);
        verify(messageToOfferRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getMessage().startsWith("Automatic message:"));
    }

    @Test
    void shouldSendAutomaticMessagesForNewApplicationWithoutDuplicates() {
        Application application = new Application();
        application.setId(2);

        JobOffer first = new JobOffer();
        first.setId(100);
        first.setTitle("Data Engineer");
        JobOffer second = new JobOffer();
        second.setId(200);
        second.setTitle("Java Engineer");

        when(jobOfferRepository.findMatchingByApplicationId(2)).thenReturn(List.of(first, second));
        when(messageToApplicationRepository.findByApplicationIdAndJobOfferId(2, 100)).thenReturn(Optional.empty());
        when(messageToApplicationRepository.findByApplicationIdAndJobOfferId(2, 200)).thenReturn(Optional.of(new MessageToApplication()));
        when(messageToApplicationRepository.save(any(MessageToApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int sent = service.sendAutomaticMessagesForNewApplication(application);

        assertEquals(1, sent);
        ArgumentCaptor<MessageToApplication> captor = ArgumentCaptor.forClass(MessageToApplication.class);
        verify(messageToApplicationRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getMessage().startsWith("Automatic message:"));
    }
}
