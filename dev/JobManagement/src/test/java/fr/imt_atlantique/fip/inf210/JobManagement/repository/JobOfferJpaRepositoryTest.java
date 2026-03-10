package fr.imt_atlantique.fip.inf210.JobManagement.repository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

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
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@DataJpaTest
class JobOfferJpaRepositoryTest {

    @Autowired
    private JobOfferJpaRepository jobOfferRepository;

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
    void shouldFindByCompanyAndOwnership() {
        Company owner = createCompany("owner.company@imt-atlantique.fr", "Owner Company");
        Company other = createCompany("other.company@imt-atlantique.fr", "Other Company");
        QualificationLevel level = createQualification("Level Owner", (short) 3);

        JobOffer ownerOffer = createOffer(owner, level, "Offer Owner", Set.of());
        createOffer(other, level, "Offer Other", Set.of());

        List<JobOffer> ownerOffers = jobOfferRepository.findByCompanyIdOrderByPublicationdateDesc(owner.getId());

        assertEquals(1, ownerOffers.size());
        assertEquals(ownerOffer.getId(), ownerOffers.get(0).getId());
        assertTrue(jobOfferRepository.findByIdAndCompanyId(ownerOffer.getId(), owner.getId()).isPresent());
        assertFalse(jobOfferRepository.findByIdAndCompanyId(ownerOffer.getId(), other.getId()).isPresent());
    }

    @Test
    void shouldSearchByCriteria() {
        Company company = createCompany("criteria.company@imt-atlantique.fr", "Criteria Company");
        Sector it = createSector("IT-Criteria");
        Sector finance = createSector("Finance-Criteria");
        QualificationLevel level2 = createQualification("Level 2 Criteria", (short) 2);
        QualificationLevel level4 = createQualification("Level 4 Criteria", (short) 4);

        JobOffer offerHighIt = createOffer(company, level4, "High IT", Set.of(it));
        createOffer(company, level2, "Low IT", Set.of(it));
        createOffer(company, level4, "High Finance", Set.of(finance));

        List<JobOffer> filtered = jobOfferRepository.searchByCriteria(true, Set.of(it.getId()), (short) 3);

        assertEquals(1, filtered.size());
        assertEquals(offerHighIt.getId(), filtered.get(0).getId());
    }

    @Test
    void shouldFindMatchingByApplication() {
        Company company = createCompany("matching.company@imt-atlantique.fr", "Matching Company");
        Candidate candidate = createCandidate("matching.candidate@imt-atlantique.fr", "Durand");

        Sector it = createSector("IT-Matching");
        Sector finance = createSector("Finance-Matching");

        QualificationLevel level3 = createQualification("Level 3 Matching", (short) 3);
        QualificationLevel level4 = createQualification("Level 4 Matching", (short) 4);
        QualificationLevel level5 = createQualification("Level 5 Matching", (short) 5);

        Application application = createApplication(candidate, level4, Set.of(it));

        JobOffer expected = createOffer(company, level3, "Expected Match", Set.of(it));
        createOffer(company, level5, "Too High", Set.of(it));
        createOffer(company, level3, "Wrong Sector", Set.of(finance));

        List<JobOffer> matches = jobOfferRepository.findMatchingByApplicationId(application.getId());

        assertEquals(1, matches.size());
        assertEquals(expected.getId(), matches.get(0).getId());
    }

    private Company createCompany(String mail, String denomination) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        return companyRepository.save(new Company(user, denomination, "description", "Brest"));
    }

    private Candidate createCandidate(String mail, String lastname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(user, lastname, "Alice", "Rennes"));
    }

    private QualificationLevel createQualification(String label, short rank) {
        return qualificationLevelRepository.save(new QualificationLevel(label, rank));
    }

    private Sector createSector(String label) {
        return sectorRepository.save(new Sector(label));
    }

    private JobOffer createOffer(Company company, QualificationLevel level, String title, Set<Sector> sectors) {
        JobOffer offer = new JobOffer(title, "Task for " + title, company, level);
        offer.setSectors(sectors);
        return jobOfferRepository.save(offer);
    }

    private Application createApplication(Candidate candidate, QualificationLevel level, Set<Sector> sectors) {
        Application application = new Application("CV", candidate, level);
        application.setSectors(sectors);
        return applicationRepository.save(application);
    }
}
