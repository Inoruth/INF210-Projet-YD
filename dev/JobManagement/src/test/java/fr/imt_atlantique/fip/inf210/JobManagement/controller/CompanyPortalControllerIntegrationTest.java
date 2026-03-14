package fr.imt_atlantique.fip.inf210.JobManagement.controller;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
class CompanyPortalControllerIntegrationTest {

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
    void shouldReturnUnauthorizedForAnonymousCompanyPortalAccess() throws Exception {
        mockMvc.perform(get("/publishjoboffer"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyoffers/{mail}", "anonymous.company@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/modifycompanyprofile/{mail}", "anonymous.company@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/matches", "anonymous.company@imt-atlantique.fr", 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/edit", "anonymous.company@imt-atlantique.fr", 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", "anonymous.company@imt-atlantique.fr", 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Unauthorized title")
                        .param("taskdescription", "Unauthorized task")
                        .param("qualificationLevelId", "1")
                        .param("sectorIds", "1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/delete", "anonymous.company@imt-atlantique.fr", 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectCompanyAccessToAnotherCompanyResources() throws Exception {
        String token = token();
        Company ownerCompany = seedCompany("owner." + token + "@imt-atlantique.fr", "Owner Corp " + token);
        Company targetCompany = seedCompany("target." + token + "@imt-atlantique.fr", "Target Corp " + token);

        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Level " + token, (short) 4));
        Sector sector = sectorRepository.save(new Sector("Sector " + token));
        JobOffer targetOffer = new JobOffer("Target offer " + token, "Target description", targetCompany, level);
        targetOffer.getSectors().add(sector);
        JobOffer savedTargetOffer = jobOfferRepository.save(targetOffer);

        MockHttpSession ownerSession = buildSession(ownerCompany.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(get("/managemyoffers/{mail}", targetCompany.getAppUser().getMail()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/modifycompanyprofile/{mail}", targetCompany.getAppUser().getMail()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/matches", targetCompany.getAppUser().getMail(), savedTargetOffer.getId())
                        .session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/edit", targetCompany.getAppUser().getMail(), savedTargetOffer.getId())
                        .session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", targetCompany.getAppUser().getMail(), savedTargetOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("title", "Forbidden update")
                        .param("taskdescription", "Should be blocked")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/delete", targetCompany.getAppUser().getMail(), savedTargetOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/modifycompanyprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("mail", targetCompany.getAppUser().getMail())
                        .param("denomination", "Illicit change")
                        .param("description", "Should be blocked")
                        .param("city", "Nantes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowCompanyToUpdateOwnProfile() throws Exception {
        String token = token();
        Company company = seedCompany("profile." + token + "@imt-atlantique.fr", "Profile Corp " + token);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(get("/modifycompanyprofile/{mail}", company.getAppUser().getMail()).session(companySession))
                .andExpect(status().isOk());

        mockMvc.perform(post("/modifycompanyprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("mail", company.getAppUser().getMail())
                        .param("denomination", "Updated Profile Corp")
                        .param("description", "Updated description")
                        .param("city", "Rennes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "?success=profile-updated"));

        Company reloaded = companyRepository.findByAppUserMail(company.getAppUser().getMail()).orElseThrow();
        assertEquals("Updated Profile Corp", reloaded.getDenomination());
        assertEquals("Updated description", reloaded.getDescription());
        assertEquals("Rennes", reloaded.getCity());
    }

    @Test
    void shouldCreateOfferAndRenderOwnedOffersAndMatches() throws Exception {
        String token = token();

        Company company = seedCompany("offers." + token + "@imt-atlantique.fr", "Offers Corp " + token);
        QualificationLevel offerLevel = qualificationLevelRepository.save(new QualificationLevel("Offer Level " + token, (short) 4));
        QualificationLevel candidateLevel = qualificationLevelRepository.save(new QualificationLevel("Candidate Level " + token, (short) 6));
        Sector sector = sectorRepository.save(new Sector("IT " + token));

        Candidate candidate = seedCandidate("candidate." + token + "@imt-atlantique.fr", "Candidate" + token);
        Application candidateApplication = new Application("cv/path/" + token + ".pdf", candidate, candidateLevel);
        candidateApplication.getSectors().add(sector);
        applicationRepository.save(candidateApplication);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Java Engineer " + token)
                        .param("taskdescription", "Build and maintain platform services")
                        .param("qualificationLevelId", offerLevel.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "?success=offer-created"));

        List<JobOffer> offers = jobOfferRepository.findByCompanyIdOrderByPublicationdateDesc(company.getId());
        assertFalse(offers.isEmpty());
        JobOffer createdOffer = offers.get(0);

        mockMvc.perform(get("/managemyoffers/{mail}", company.getAppUser().getMail()).session(companySession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/matches", company.getAppUser().getMail(), createdOffer.getId())
                        .session(companySession))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowCompanyToUpdateAndDeleteOwnOffer() throws Exception {
        String token = token();

        Company company = seedCompany("edit." + token + "@imt-atlantique.fr", "Edit Corp " + token);
        QualificationLevel initialLevel = qualificationLevelRepository.save(new QualificationLevel("Initial Level " + token, (short) 3));
        QualificationLevel updatedLevel = qualificationLevelRepository.save(new QualificationLevel("Updated Level " + token, (short) 6));
        Sector initialSector = sectorRepository.save(new Sector("Initial Sector " + token));
        Sector updatedSector = sectorRepository.save(new Sector("Updated Sector " + token));

        JobOffer offer = new JobOffer("Initial title " + token, "Initial task description", company, initialLevel);
        offer.getSectors().add(initialSector);
        JobOffer savedOffer = jobOfferRepository.save(offer);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(get("/managemyoffers/{mail}/offer/{offerId}/edit", company.getAppUser().getMail(), savedOffer.getId())
                        .session(companySession))
                .andExpect(status().isOk());

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Updated title " + token)
                        .param("taskdescription", "Updated task description")
                        .param("qualificationLevelId", updatedLevel.getId().toString())
                        .param("sectorIds", updatedSector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "?success=offer-updated"));

        JobOffer updatedOffer = jobOfferRepository.findById(savedOffer.getId()).orElseThrow();
        assertEquals("Updated title " + token, updatedOffer.getTitle());
        assertEquals("Updated task description", updatedOffer.getTaskdescription());
        assertEquals(updatedLevel.getId(), updatedOffer.getQualificationLevel().getId());
        assertEquals(1, updatedOffer.getSectors().size());
        assertTrue(updatedOffer.getSectors().stream().anyMatch(s -> s.getId().equals(updatedSector.getId())));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/delete", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "?success=offer-deleted"));

        assertFalse(jobOfferRepository.findById(savedOffer.getId()).isPresent());
    }

    private Company seedCompany(String mail, String denomination) {
        AppUser companyUser = appUserRepository.save(new AppUser(mail, "pwd1234", AppUser.UserType.company));
        return companyRepository.save(new Company(companyUser, denomination, "Initial description", "Nantes"));
    }

    private Candidate seedCandidate(String mail, String lastname) {
        AppUser candidateUser = appUserRepository.save(new AppUser(mail, "pwd1234", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(candidateUser, lastname, "First", "Rennes"));
    }

    private MockHttpSession buildSession(String mail, AppUser.UserType userType) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", mail);
        session.setAttribute("userType", userType.name());
        return session;
    }

    private String token() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
