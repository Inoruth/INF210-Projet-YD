package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: ApplicationJpaRepositoryTest
 * Cette classe teste les requetes JPA du repository cible.
 * Les tests s'executent sur une base de test pour valider la persistance et les recherches.
 * Les assertions controlent la coherence des resultats retournes par les methodes.
 * L'objectif est de securiser le mapping et le comportement des requetes.
 */

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
class ApplicationJpaRepositoryTest {

    @Autowired
    private ApplicationJpaRepository applicationRepository;

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

    // Ce test verifie le comportement de shouldFindByCandidateAndOwnership.
    @Test
    void shouldFindByCandidateAndOwnership() {
        Candidate owner = createCandidate("owner.candidate@imt-atlantique.fr", "Owner");
        Candidate other = createCandidate("other.candidate@imt-atlantique.fr", "Other");
        QualificationLevel level = createQualification("Level App Owner", (short) 3);

        Application ownerApplication = createApplication(owner, level, Set.of());
        createApplication(other, level, Set.of());

        List<Application> ownerApplications = applicationRepository.findByCandidateIdOrderByAppdateDesc(owner.getId());

        assertEquals(1, ownerApplications.size());
        assertEquals(ownerApplication.getId(), ownerApplications.get(0).getId());
        assertTrue(applicationRepository.findByIdAndCandidateId(ownerApplication.getId(), owner.getId()).isPresent());
        assertFalse(applicationRepository.findByIdAndCandidateId(ownerApplication.getId(), other.getId()).isPresent());
    }

    // Ce test verifie le comportement de shouldSearchByCriteria.
    @Test
    void shouldSearchByCriteria() {
        Candidate candidate = createCandidate("criteria.candidate@imt-atlantique.fr", "Criteria");
        Sector it = createSector("IT-App-Criteria");
        Sector finance = createSector("Finance-App-Criteria");
        QualificationLevel level2 = createQualification("Level 2 App Criteria", (short) 2);
        QualificationLevel level4 = createQualification("Level 4 App Criteria", (short) 4);

        Application expected = createApplication(candidate, level4, Set.of(it));
        createApplication(candidate, level2, Set.of(it));
        createApplication(candidate, level4, Set.of(finance));

        List<Application> filtered = applicationRepository.searchByCriteria(true, Set.of(it.getId()), (short) 3);

        assertEquals(1, filtered.size());
        assertEquals(expected.getId(), filtered.get(0).getId());
    }

    // Ce test verifie le comportement de shouldFindMatchingByJobOffer.
    @Test
    void shouldFindMatchingByJobOffer() {
        Company company = createCompany("matching.application.company@imt-atlantique.fr", "Application Match Company");
        Candidate candidate = createCandidate("matching.application.candidate@imt-atlantique.fr", "Durand");

        Sector it = createSector("IT-App-Matching");
        Sector finance = createSector("Finance-App-Matching");

        QualificationLevel level2 = createQualification("Level 2 App Matching", (short) 2);
        QualificationLevel level3 = createQualification("Level 3 App Matching", (short) 3);
        QualificationLevel level4 = createQualification("Level 4 App Matching", (short) 4);

        JobOffer offer = createOffer(company, level3, Set.of(it));

        Application expected = createApplication(candidate, level4, Set.of(it));
        createApplication(candidate, level2, Set.of(it));
        createApplication(candidate, level4, Set.of(finance));

        List<Application> matches = applicationRepository.findMatchingByJobOfferId(offer.getId());

        assertEquals(1, matches.size());
        assertEquals(expected.getId(), matches.get(0).getId());
    }

    // Ce test verifie le comportement de createCandidate.
    private Candidate createCandidate(String mail, String lastname) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(user, lastname, "Alice", "Rennes"));
    }

    // Ce test verifie le comportement de createCompany.
    private Company createCompany(String mail, String denomination) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        return companyRepository.save(new Company(user, denomination, "description", "Brest"));
    }

    // Ce test verifie le comportement de createQualification.
    private QualificationLevel createQualification(String label, short rank) {
        return qualificationLevelRepository.save(new QualificationLevel(label, rank));
    }

    // Ce test verifie le comportement de createSector.
    private Sector createSector(String label) {
        return sectorRepository.save(new Sector(label));
    }

    // Ce test verifie le comportement de createApplication.
    private Application createApplication(Candidate candidate, QualificationLevel level, Set<Sector> sectors) {
        Application application = new Application("CV", candidate, level);
        application.setSectors(sectors);
        return applicationRepository.save(application);
    }

    // Ce test verifie le comportement de createOffer.
    private JobOffer createOffer(Company company, QualificationLevel level, Set<Sector> sectors) {
        JobOffer offer = new JobOffer("Offer", "Task description", company, level);
        offer.setSectors(sectors);
        return jobOfferRepository.save(offer);
    }
}
