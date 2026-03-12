package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    void shouldReturnZeroForNullOffer() {
        int sent = service.sendAutomaticMessagesForNewOffer(null);

        assertEquals(0, sent);
        verifyNoInteractions(applicationRepository, messageToOfferRepository);
    }

    @Test
    void shouldReturnZeroForOfferWithoutId() {
        JobOffer offer = new JobOffer();

        int sent = service.sendAutomaticMessagesForNewOffer(offer);

        assertEquals(0, sent);
        verifyNoInteractions(applicationRepository, messageToOfferRepository);
    }

    @Test
    void shouldSkipApplicationsWithoutIdWhenSendingOfferMessages() {
        JobOffer offer = new JobOffer();
        offer.setId(9);
        offer.setTitle("QA Engineer");

        Application withId = new Application();
        withId.setId(15);
        Application withoutId = new Application();

        when(applicationRepository.findMatchingByJobOfferId(9)).thenReturn(List.of(withId, withoutId));
        when(messageToOfferRepository.findByJobOfferIdAndApplicationId(9, 15)).thenReturn(Optional.empty());

        int sent = service.sendAutomaticMessagesForNewOffer(offer);

        assertEquals(1, sent);
        verify(messageToOfferRepository).save(any(MessageToOffer.class));
        verify(messageToOfferRepository, never()).findByJobOfferIdAndApplicationId(9, null);
    }

    @Test
    void shouldReturnZeroWhenAllOfferMessagesAlreadyExist() {
        JobOffer offer = new JobOffer();
        offer.setId(3);

        Application first = new Application();
        first.setId(10);
        Application second = new Application();
        second.setId(20);

        when(applicationRepository.findMatchingByJobOfferId(3)).thenReturn(List.of(first, second));
        when(messageToOfferRepository.findByJobOfferIdAndApplicationId(3, 10)).thenReturn(Optional.of(new MessageToOffer()));
        when(messageToOfferRepository.findByJobOfferIdAndApplicationId(3, 20)).thenReturn(Optional.of(new MessageToOffer()));

        int sent = service.sendAutomaticMessagesForNewOffer(offer);

        assertEquals(0, sent);
        verify(messageToOfferRepository, never()).save(any(MessageToOffer.class));
    }

    @Test
    void shouldReturnZeroWhenNoApplicationMatchesForOffer() {
        JobOffer offer = new JobOffer();
        offer.setId(33);

        when(applicationRepository.findMatchingByJobOfferId(33)).thenReturn(List.of());

        int sent = service.sendAutomaticMessagesForNewOffer(offer);

        assertEquals(0, sent);
        verify(messageToOfferRepository, never()).save(any(MessageToOffer.class));
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

    @Test
    void shouldReturnZeroForNullApplication() {
        int sent = service.sendAutomaticMessagesForNewApplication(null);

        assertEquals(0, sent);
        verifyNoInteractions(jobOfferRepository, messageToApplicationRepository);
    }

    @Test
    void shouldReturnZeroForApplicationWithoutId() {
        Application application = new Application();

        int sent = service.sendAutomaticMessagesForNewApplication(application);

        assertEquals(0, sent);
        verifyNoInteractions(jobOfferRepository, messageToApplicationRepository);
    }

    @Test
    void shouldSkipOffersWithoutIdWhenSendingApplicationMessages() {
        Application application = new Application();
        application.setId(14);

        JobOffer withId = new JobOffer();
        withId.setId(101);
        withId.setTitle("Backend Engineer");
        JobOffer withoutId = new JobOffer();

        when(jobOfferRepository.findMatchingByApplicationId(14)).thenReturn(List.of(withId, withoutId));
        when(messageToApplicationRepository.findByApplicationIdAndJobOfferId(14, 101)).thenReturn(Optional.empty());

        int sent = service.sendAutomaticMessagesForNewApplication(application);

        assertEquals(1, sent);
        verify(messageToApplicationRepository).save(any(MessageToApplication.class));
        verify(messageToApplicationRepository, never()).findByApplicationIdAndJobOfferId(14, null);
    }

    @Test
    void shouldReturnZeroWhenAllApplicationMessagesAlreadyExist() {
        Application application = new Application();
        application.setId(5);

        JobOffer first = new JobOffer();
        first.setId(201);
        JobOffer second = new JobOffer();
        second.setId(202);

        when(jobOfferRepository.findMatchingByApplicationId(5)).thenReturn(List.of(first, second));
        when(messageToApplicationRepository.findByApplicationIdAndJobOfferId(5, 201)).thenReturn(Optional.of(new MessageToApplication()));
        when(messageToApplicationRepository.findByApplicationIdAndJobOfferId(5, 202)).thenReturn(Optional.of(new MessageToApplication()));

        int sent = service.sendAutomaticMessagesForNewApplication(application);

        assertEquals(0, sent);
        verify(messageToApplicationRepository, never()).save(any(MessageToApplication.class));
    }

    @Test
    void shouldReturnZeroWhenNoOfferMatchesForApplication() {
        Application application = new Application();
        application.setId(44);

        when(jobOfferRepository.findMatchingByApplicationId(44)).thenReturn(List.of());

        int sent = service.sendAutomaticMessagesForNewApplication(application);

        assertEquals(0, sent);
        verify(messageToApplicationRepository, never()).save(any(MessageToApplication.class));
    }
}
