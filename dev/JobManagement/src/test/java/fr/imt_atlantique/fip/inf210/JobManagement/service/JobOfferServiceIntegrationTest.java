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
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.ApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;

@SpringBootTest
@Transactional
class JobOfferServiceIntegrationTest {

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private ApplicationJpaRepository applicationRepository;

    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    @Autowired
    private SectorJpaRepository sectorRepository;

    @Test
    void shouldSearchOffersByCriteriaThroughService() {
        Company company = createCompany("integration.offer.company@imt-atlantique.fr", "Integration Co");
        Sector it = sectorRepository.save(new Sector("IT-Offer-Service-Int"));
        Sector finance = sectorRepository.save(new Sector("Finance-Offer-Service-Int"));
        QualificationLevel level2 = qualificationLevelRepository.save(new QualificationLevel("L2-Offer-Service-Int", (short) 2));
        QualificationLevel level4 = qualificationLevelRepository.save(new QualificationLevel("L4-Offer-Service-Int", (short) 4));

        createOffer(company, level4, "Expected Offer", Set.of(it));
        createOffer(company, level2, "Low Offer", Set.of(it));
        createOffer(company, level4, "Wrong Sector Offer", Set.of(finance));

        List<JobOffer> offers = jobOfferService.searchByCriteria(Set.of(it.getId()), (short) 3);

        assertEquals(1, offers.size());
        assertEquals("Expected Offer", offers.get(0).getTitle());
    }

    @Test
    void shouldFindMatchingOffersForApplicationThroughService() {
        Company company = createCompany("integration.offer.company2@imt-atlantique.fr", "Integration Co 2");
        Candidate candidate = createCandidate("integration.offer.candidate@imt-atlantique.fr", "Durand");

        Sector it = sectorRepository.save(new Sector("IT-Offer-Match-Int"));
        Sector finance = sectorRepository.save(new Sector("Finance-Offer-Match-Int"));

        QualificationLevel level3 = qualificationLevelRepository.save(new QualificationLevel("L3-Offer-Match-Int", (short) 3));
        QualificationLevel level4 = qualificationLevelRepository.save(new QualificationLevel("L4-Offer-Match-Int", (short) 4));
        QualificationLevel level5 = qualificationLevelRepository.save(new QualificationLevel("L5-Offer-Match-Int", (short) 5));

        Application application = createApplication(candidate, level4, Set.of(it));

        createOffer(company, level3, "Matching Offer", Set.of(it));
        createOffer(company, level5, "Too High Offer", Set.of(it));
        createOffer(company, level3, "Wrong Sector Offer", Set.of(finance));

        List<JobOffer> matches = jobOfferService.findMatchingByApplicationId(application.getId());

        assertEquals(1, matches.size());
        assertEquals("Matching Offer", matches.get(0).getTitle());
    }

    @Test
    void shouldDeleteOnlyOwnedOfferThroughService() {
        Company owner = createCompany("integration.offer.owner@imt-atlantique.fr", "Owner Co");
        Company other = createCompany("integration.offer.other@imt-atlantique.fr", "Other Co");
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("L-Delete-Offer-Int", (short) 3));

        JobOffer offer = createOffer(owner, level, "Owned Offer", Set.of());

        jobOfferService.deleteByIdAndCompanyId(offer.getId(), other.getId());
        assertFalse(jobOfferService.findById(offer.getId()).isEmpty());

        jobOfferService.deleteByIdAndCompanyId(offer.getId(), owner.getId());
        assertFalse(jobOfferService.findById(offer.getId()).isPresent());
    }

    private Company createCompany(String mail, String denomination) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        return companyRepository.save(new Company(user, denomination, "description", "Brest"));
    }

    private Candidate createCandidate(String mail, String lastname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(user, lastname, "Alice", "Rennes"));
    }

    private JobOffer createOffer(Company company, QualificationLevel level, String title, Set<Sector> sectors) {
        JobOffer offer = new JobOffer(title, "Task description", company, level);
        offer.setSectors(sectors);
        return jobOfferService.save(offer);
    }

    private Application createApplication(Candidate candidate, QualificationLevel level, Set<Sector> sectors) {
        Application application = new Application("CV", candidate, level);
        application.setSectors(sectors);
        return applicationRepository.save(application);
    }
}
