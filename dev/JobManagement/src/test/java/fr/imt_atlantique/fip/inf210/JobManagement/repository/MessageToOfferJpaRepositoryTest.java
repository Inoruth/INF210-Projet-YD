package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: MessageToOfferJpaRepositoryTest
 * Cette classe teste les requetes JPA du repository cible.
 * Les tests s'executent sur une base de test pour valider la persistance et les recherches.
 * Les assertions controlent la coherence des resultats retournes par les methodes.
 * L'objectif est de securiser le mapping et le comportement des requetes.
 */

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@DataJpaTest
class MessageToOfferJpaRepositoryTest {

    @Autowired
    private MessageToOfferJpaRepository messageRepository;

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

    // Ce test verifie le comportement de shouldFindByUniqueOfferApplicationPair.
    @Test
    void shouldFindByUniqueOfferApplicationPair() {
        TestDataset data = createDataset();

        assertTrue(messageRepository
                .findByJobOfferIdAndApplicationId(data.offerOne.getId(), data.applicationOne.getId())
                .isPresent());
    }

    // Ce test verifie le comportement de shouldListCompanyMessagesOrderedByPublicationDateDesc.
    @Test
    void shouldListCompanyMessagesOrderedByPublicationDateDesc() {
        TestDataset data = createDataset();

        List<MessageToOffer> messages = messageRepository
                .findByJobOfferCompanyIdOrderByPublicationdateDesc(data.company.getId());

        assertEquals(2, messages.size());
        assertEquals(data.messageTwo.getId(), messages.get(0).getId());
        assertEquals(data.messageOne.getId(), messages.get(1).getId());
    }

    // Ce test verifie le comportement de shouldListCandidateMessagesOrderedByPublicationDateDesc.
    @Test
    void shouldListCandidateMessagesOrderedByPublicationDateDesc() {
        TestDataset data = createDataset();

        List<MessageToOffer> messages = messageRepository
                .findByApplicationCandidateIdOrderByPublicationdateDesc(data.candidate.getId());

        assertEquals(2, messages.size());
        assertEquals(data.messageTwo.getId(), messages.get(0).getId());
        assertEquals(data.messageOne.getId(), messages.get(1).getId());
    }

    // Ce test verifie le comportement de createDataset.
    private TestDataset createDataset() {
        Company company = createCompany("mto.company@imt-atlantique.fr", "MTO Company");
        Candidate candidate = createCandidate("mto.candidate@imt-atlantique.fr", "MtoCandidate");

        Sector sector = sectorRepository.save(new Sector("MTO-IT"));
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("MTO-Level", (short) 4));

        JobOffer offerOne = createOffer(company, level, "MTO Offer One", Set.of(sector));
        JobOffer offerTwo = createOffer(company, level, "MTO Offer Two", Set.of(sector));

        Application applicationOne = createApplication(candidate, level, Set.of(sector), "CV-1");
        Application applicationTwo = createApplication(candidate, level, Set.of(sector), "CV-2");

        MessageToOffer messageOne = new MessageToOffer("First message", offerOne, applicationOne);
        messageOne.setPublicationdate(LocalDate.of(2026, 1, 10));
        messageOne = messageRepository.save(messageOne);

        MessageToOffer messageTwo = new MessageToOffer("Second message", offerTwo, applicationTwo);
        messageTwo.setPublicationdate(LocalDate.of(2026, 1, 15));
        messageTwo = messageRepository.save(messageTwo);

        // Ce test verifie le comportement de TestDataset.
        return new TestDataset(company, candidate, offerOne, applicationOne, messageOne, messageTwo);
    }

    // Ce test verifie le comportement de createCompany.
    private Company createCompany(String mail, String denomination) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        return companyRepository.save(new Company(user, denomination, "description", "Brest"));
    }

    // Ce test verifie le comportement de createCandidate.
    private Candidate createCandidate(String mail, String lastname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(user, lastname, "Alice", "Rennes"));
    }

    // Ce test verifie le comportement de createOffer.
    private JobOffer createOffer(Company company, QualificationLevel level, String title, Set<Sector> sectors) {
        JobOffer offer = new JobOffer(title, "Task description", company, level);
        offer.setSectors(sectors);
        return jobOfferRepository.save(offer);
    }

    // Ce test verifie le comportement de createApplication.
    private Application createApplication(Candidate candidate, QualificationLevel level, Set<Sector> sectors, String cv) {
        Application application = new Application(cv, candidate, level);
        application.setSectors(sectors);
        return applicationRepository.save(application);
    }

    private record TestDataset(
            Company company,
            Candidate candidate,
            JobOffer offerOne,
            Application applicationOne,
            MessageToOffer messageOne,
            MessageToOffer messageTwo
    ) {
    }
}
