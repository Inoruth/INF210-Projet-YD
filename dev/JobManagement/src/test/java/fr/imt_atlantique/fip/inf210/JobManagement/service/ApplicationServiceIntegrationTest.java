package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;

@SpringBootTest
@Transactional
class ApplicationServiceIntegrationTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private JobOfferJpaRepository jobOfferRepository;

    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    @Autowired
    private SectorJpaRepository sectorRepository;

    @Test
    void shouldSearchApplicationsByCriteriaThroughService() {
        Candidate candidate = createCandidate("integration.app.candidate@imt-atlantique.fr", "Durand");
        Sector it = sectorRepository.save(new Sector("IT-App-Service-Int"));
        Sector finance = sectorRepository.save(new Sector("Finance-App-Service-Int"));
        QualificationLevel level2 = qualificationLevelRepository.save(new QualificationLevel("L2-App-Service-Int", (short) 2));
        QualificationLevel level4 = qualificationLevelRepository.save(new QualificationLevel("L4-App-Service-Int", (short) 4));

        createApplication(candidate, level4, Set.of(it), "Expected CV");
        createApplication(candidate, level2, Set.of(it), "Low CV");
        createApplication(candidate, level4, Set.of(finance), "Wrong sector CV");

        List<Application> applications = applicationService.searchByCriteria(Set.of(it.getId()), (short) 3);

        assertEquals(1, applications.size());
        assertEquals("Expected CV", applications.get(0).getCv());
    }

    @Test
    void shouldFindMatchingApplicationsForOfferThroughService() {
        Candidate candidate = createCandidate("integration.app.match.candidate@imt-atlantique.fr", "Martin");
        Company company = createCompany("integration.app.match.company@imt-atlantique.fr", "Match Co");

        Sector it = sectorRepository.save(new Sector("IT-App-Match-Service-Int"));
        Sector finance = sectorRepository.save(new Sector("Finance-App-Match-Service-Int"));
        QualificationLevel level2 = qualificationLevelRepository.save(new QualificationLevel("L2-App-Match-Service-Int", (short) 2));
        QualificationLevel level3 = qualificationLevelRepository.save(new QualificationLevel("L3-App-Match-Service-Int", (short) 3));
        QualificationLevel level4 = qualificationLevelRepository.save(new QualificationLevel("L4-App-Match-Service-Int", (short) 4));

        JobOffer offer = createOffer(company, level3, Set.of(it));

        createApplication(candidate, level4, Set.of(it), "Matching CV");
        createApplication(candidate, level2, Set.of(it), "Too low CV");
        createApplication(candidate, level4, Set.of(finance), "Wrong sector CV");

        List<Application> matches = applicationService.findMatchingByJobOfferId(offer.getId());

        assertEquals(1, matches.size());
        assertEquals("Matching CV", matches.get(0).getCv());
    }

    @Test
    void shouldDeleteOnlyOwnedApplicationThroughService() {
        Candidate owner = createCandidate("integration.app.owner@imt-atlantique.fr", "Owner");
        Candidate other = createCandidate("integration.app.other@imt-atlantique.fr", "Other");
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("L-Delete-App-Int", (short) 3));

        Application application = createApplication(owner, level, Set.of(), "Owned CV");

        applicationService.deleteByIdAndCandidateId(application.getId(), other.getId());
        assertFalse(applicationService.findById(application.getId()).isEmpty());

        applicationService.deleteByIdAndCandidateId(application.getId(), owner.getId());
        assertFalse(applicationService.findById(application.getId()).isPresent());
    }

    private Candidate createCandidate(String mail, String lastname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(user, lastname, "Alice", "Rennes"));
    }

    private Company createCompany(String mail, String denomination) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        return companyRepository.save(new Company(user, denomination, "description", "Brest"));
    }

    private Application createApplication(Candidate candidate, QualificationLevel level, Set<Sector> sectors, String cv) {
        Application application = new Application(cv, candidate, level);
        application.setSectors(sectors);
        return applicationService.save(application);
    }

    private JobOffer createOffer(Company company, QualificationLevel level, Set<Sector> sectors) {
        JobOffer offer = new JobOffer("Offer", "Task", company, level);
        offer.setSectors(sectors);
        return jobOfferRepository.save(offer);
    }
}
