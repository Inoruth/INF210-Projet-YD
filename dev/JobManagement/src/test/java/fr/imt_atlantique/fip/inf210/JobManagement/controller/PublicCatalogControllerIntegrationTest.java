package fr.imt_atlantique.fip.inf210.JobManagement.controller;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.JobOfferJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PublicCatalogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void shouldRenderPublicListingPages() throws Exception {
        seedDomainData();

        mockMvc.perform(get("/allcompanies"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/allapplicants"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/alljobs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/allapplications"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRenderPublicDetailPages() throws Exception {
        SeedData data = seedDomainData();

        mockMvc.perform(get("/company/{mail}", data.companyMail()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/candidate/{mail}", data.candidateMail()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/job/{id}", data.jobOfferId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/application/{id}", data.applicationId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldApplySearchFiltersOnPublicPages() throws Exception {
        SeedData data = seedDomainData();

        mockMvc.perform(get("/alljobs")
                        .param("sectorIds", data.sectorId().toString())
                        .param("minimumRank", data.minimumRank().toString()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/allapplications")
                        .param("sectorIds", data.sectorId().toString())
                        .param("minimumRank", data.minimumRank().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundForUnknownPublicDetailResources() throws Exception {
        mockMvc.perform(get("/company/{mail}", "missing.company@imt-atlantique.fr"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/candidate/{mail}", "missing.candidate@imt-atlantique.fr"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/job/{id}", 999999))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/application/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    private SeedData seedDomainData() {
        String token = UUID.randomUUID().toString().substring(0, 8);

        AppUser companyUser = appUserRepository.save(new AppUser(
                "company." + token + "@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.company
        ));
        Company company = companyRepository.save(new Company(
                companyUser,
                "Company " + token,
                "Public company profile for tests",
                "Nantes"
        ));

        AppUser candidateUser = appUserRepository.save(new AppUser(
                "candidate." + token + "@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));
        Candidate candidate = candidateRepository.save(new Candidate(
                candidateUser,
                "Lastname" + token,
                "Firstname" + token,
                "Rennes"
        ));

        QualificationLevel qualificationLevel = qualificationLevelRepository.save(
                new QualificationLevel("Level " + token, (short) 5)
        );
        Sector sector = sectorRepository.save(new Sector("Sector " + token));

        JobOffer jobOffer = new JobOffer(
                "Offer " + token,
                "Detailed task description",
                company,
                qualificationLevel
        );
        jobOffer.getSectors().add(sector);
        JobOffer savedJobOffer = jobOfferRepository.save(jobOffer);

        Application application = new Application(
                "cv/path/" + token + ".pdf",
                candidate,
                qualificationLevel
        );
        application.getSectors().add(sector);
        Application savedApplication = applicationRepository.save(application);

        return new SeedData(
                companyUser.getMail(),
                candidateUser.getMail(),
                savedJobOffer.getId(),
                savedApplication.getId(),
                sector.getId(),
                qualificationLevel.getRank()
        );
    }

    private record SeedData(
            String companyMail,
            String candidateMail,
            Integer jobOfferId,
            Integer applicationId,
            Integer sectorId,
            Short minimumRank
    ) {
    }
}
