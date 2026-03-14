package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: MessageServicesIntegrationTest
 * Cette classe teste le service avec le contexte Spring complet.
 * Elle valide l'integration entre la couche metier, les repositories et la base de test.
 * Les scenarios verifies reproduisent les flux fonctionnels importants.
 * Ces tests detectent les regressions entre couches applicatives.
 */

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.MessageToOfferService;

@SpringBootTest
@Transactional
class MessageServicesIntegrationTest {

    @Autowired
    private MessageToOfferService messageToOfferService;

    @Autowired
    private MessageToApplicationService messageToApplicationService;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private JobOfferJpaRepository jobOfferRepository;

    @Autowired
    private ApplicationJpaRepository applicationRepository;

    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    @Autowired
    private SectorJpaRepository sectorRepository;

    // Ce test verifie le comportement de shouldSaveAndReadMessagesThroughServices.
    @Test
    void shouldSaveAndReadMessagesThroughServices() {
        TestDataset data = createDataset();

        assertTrue(messageToOfferService
                .findByJobOfferAndApplication(data.offer.getId(), data.application.getId())
                .isPresent());
        assertTrue(messageToApplicationService
                .findByApplicationAndJobOffer(data.application.getId(), data.offer.getId())
                .isPresent());

        List<MessageToOffer> companyToCandidate = messageToOfferService.findByCompanyId(data.company.getId());
        List<MessageToOffer> candidateViewOfferMessages = messageToOfferService.findByCandidateId(data.candidate.getId());

        List<MessageToApplication> candidateToCompany = messageToApplicationService.findByCandidateId(data.candidate.getId());
        List<MessageToApplication> companyViewApplicationMessages = messageToApplicationService.findByCompanyId(data.company.getId());

        assertEquals(1, companyToCandidate.size());
        assertEquals(1, candidateViewOfferMessages.size());
        assertEquals(1, candidateToCompany.size());
        assertEquals(1, companyViewApplicationMessages.size());

        assertEquals("Message to candidate", companyToCandidate.get(0).getMessage());
        assertEquals("Message to company", candidateToCompany.get(0).getMessage());
    }

    // Ce test verifie le comportement de createDataset.
    private TestDataset createDataset() {
        AppUser companyUser = appUserRepository.save(new AppUser(
                "service.msg.company@imt-atlantique.fr",
                "pwd123",
                AppUser.UserType.company
        ));
        Company company = companyRepository.save(new Company(companyUser, "Messaging Co", "desc", "Brest"));

        AppUser candidateUser = appUserRepository.save(new AppUser(
                "service.msg.candidate@imt-atlantique.fr",
                "pwd123",
                AppUser.UserType.applicant
        ));
        Candidate candidate = candidateRepository.save(new Candidate(candidateUser, "Durand", "Alice", "Rennes"));

        Sector sector = sectorRepository.save(new Sector("MSG-Sector-Int"));
        QualificationLevel qualification = qualificationLevelRepository.save(new QualificationLevel("MSG-Level-Int", (short) 4));

        JobOffer offer = new JobOffer("Offer", "Task", company, qualification);
        offer.setSectors(Set.of(sector));
        offer = jobOfferRepository.save(offer);

        Application application = new Application("CV", candidate, qualification);
        application.setSectors(Set.of(sector));
        application = applicationRepository.save(application);

        MessageToOffer toOffer = new MessageToOffer("Message to candidate", offer, application);
        toOffer.setPublicationdate(LocalDate.of(2026, 3, 10));
        messageToOfferService.save(toOffer);

        MessageToApplication toApplication = new MessageToApplication("Message to company", application, offer);
        toApplication.setPublicationdate(LocalDate.of(2026, 3, 10));
        messageToApplicationService.save(toApplication);

        // Ce test verifie le comportement de TestDataset.
        return new TestDataset(company, candidate, offer, application);
    }

    private record TestDataset(
            Company company,
            Candidate candidate,
            JobOffer offer,
            Application application
    ) {
    }
}
