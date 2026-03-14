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
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CandidatePortalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private JobOfferJpaRepository jobOfferRepository;

    @Autowired
    private ApplicationJpaRepository applicationRepository;

        @Autowired
        private MessageToApplicationJpaRepository messageToApplicationRepository;

    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    @Autowired
    private SectorJpaRepository sectorRepository;

    @Test
    void shouldReturnUnauthorizedForAnonymousCandidatePortalAccess() throws Exception {
        mockMvc.perform(get("/publishapplication"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyapplications/{mail}", "anonymous.candidate@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/modifycandidateprofile/{mail}", "anonymous.candidate@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/matches", "anonymous.candidate@imt-atlantique.fr", 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/edit", "anonymous.candidate@imt-atlantique.fr", 1))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/managemyapplications/{mail}/messages", "anonymous.candidate@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", "anonymous.candidate@imt-atlantique.fr", 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("cv", "unauthorized-cv.pdf")
                        .param("qualificationLevelId", "1")
                        .param("sectorIds", "1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/delete", "anonymous.candidate@imt-atlantique.fr", 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        "anonymous.candidate@imt-atlantique.fr", 1, 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Unauthorized message"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectApplicantAccessToAnotherApplicantResources() throws Exception {
        String token = token();
        Candidate ownerCandidate = seedCandidate("owner." + token + "@imt-atlantique.fr", "Owner" + token);
        Candidate targetCandidate = seedCandidate("target." + token + "@imt-atlantique.fr", "Target" + token);

        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Level " + token, (short) 5));
        Sector sector = sectorRepository.save(new Sector("Sector " + token));
        Application targetApplication = new Application("target/cv/" + token + ".pdf", targetCandidate, level);
        targetApplication.getSectors().add(sector);
        Application savedTargetApplication = applicationRepository.save(targetApplication);

        MockHttpSession ownerSession = buildSession(ownerCandidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(get("/managemyapplications/{mail}", targetCandidate.getAppUser().getMail()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/modifycandidateprofile/{mail}", targetCandidate.getAppUser().getMail()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/matches",
                        targetCandidate.getAppUser().getMail(),
                        savedTargetApplication.getId()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/edit",
                        targetCandidate.getAppUser().getMail(),
                        savedTargetApplication.getId()).session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/managemyapplications/{mail}/messages", targetCandidate.getAppUser().getMail())
                        .session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update",
                        targetCandidate.getAppUser().getMail(),
                        savedTargetApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("cv", "forbidden-cv.pdf")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/delete",
                        targetCandidate.getAppUser().getMail(),
                        savedTargetApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        targetCandidate.getAppUser().getMail(), savedTargetApplication.getId(), 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("message", "Forbidden message"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("mail", targetCandidate.getAppUser().getMail())
                        .param("lastname", "Illicit change")
                        .param("firstname", "Blocked")
                        .param("city", "Nantes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowApplicantToUpdateOwnProfile() throws Exception {
        String token = token();
        Candidate candidate = seedCandidate("profile." + token + "@imt-atlantique.fr", "Profile" + token);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(get("/modifycandidateprofile/{mail}", candidate.getAppUser().getMail()).session(candidateSession))
                .andExpect(status().isOk());

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("mail", candidate.getAppUser().getMail())
                        .param("lastname", "Updated Lastname")
                        .param("firstname", "Updated Firstname")
                        .param("city", "Rennes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "?success=profile-updated"));

        Candidate reloaded = candidateRepository.findByAppUserMail(candidate.getAppUser().getMail()).orElseThrow();
        assertEquals("Updated Lastname", reloaded.getLastname());
        assertEquals("Updated Firstname", reloaded.getFirstname());
        assertEquals("Rennes", reloaded.getCity());
    }

    @Test
    void shouldCreateApplicationAndRenderOwnedApplicationsAndMatches() throws Exception {
        String token = token();

        Candidate candidate = seedCandidate("applications." + token + "@imt-atlantique.fr", "Applicant" + token);
        QualificationLevel applicationLevel = qualificationLevelRepository.save(new QualificationLevel("Application Level " + token, (short) 6));
        QualificationLevel offerLevel = qualificationLevelRepository.save(new QualificationLevel("Offer Level " + token, (short) 4));
        Sector sector = sectorRepository.save(new Sector("IT " + token));

        Company company = seedCompany("company." + token + "@imt-atlantique.fr", "Company " + token);
        JobOffer offer = new JobOffer("Offer " + token, "Opportunity description", company, offerLevel);
        offer.getSectors().add(sector);
        jobOfferRepository.save(offer);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(post("/publishapplication")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "cv/path/" + token + ".pdf")
                        .param("qualificationLevelId", applicationLevel.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "?success=application-created"));

        List<Application> applications = applicationRepository.findByCandidateIdOrderByAppdateDesc(candidate.getId());
        assertFalse(applications.isEmpty());
        Application createdApplication = applications.get(0);

        mockMvc.perform(get("/managemyapplications/{mail}", candidate.getAppUser().getMail()).session(candidateSession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/matches",
                        candidate.getAppUser().getMail(),
                        createdApplication.getId()).session(candidateSession))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowCandidateToUpdateAndDeleteOwnApplication() throws Exception {
        String token = token();

        Candidate candidate = seedCandidate("edit." + token + "@imt-atlantique.fr", "Candidate" + token);
        QualificationLevel initialLevel = qualificationLevelRepository.save(new QualificationLevel("Initial Level " + token, (short) 3));
        QualificationLevel updatedLevel = qualificationLevelRepository.save(new QualificationLevel("Updated Level " + token, (short) 7));
        Sector initialSector = sectorRepository.save(new Sector("Initial Sector " + token));
        Sector updatedSector = sectorRepository.save(new Sector("Updated Sector " + token));

        Application application = new Application("initial-cv-" + token + ".pdf", candidate, initialLevel);
        application.getSectors().add(initialSector);
        Application savedApplication = applicationRepository.save(application);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(get("/managemyapplications/{mail}/application/{applicationId}/edit", candidate.getAppUser().getMail(), savedApplication.getId())
                        .session(candidateSession))
                .andExpect(status().isOk());

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "updated-cv-" + token + ".pdf")
                        .param("qualificationLevelId", updatedLevel.getId().toString())
                        .param("sectorIds", updatedSector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "?success=application-updated"));

        Application updatedApplication = applicationRepository.findById(savedApplication.getId()).orElseThrow();
        assertEquals("updated-cv-" + token + ".pdf", updatedApplication.getCv());
        assertEquals(updatedLevel.getId(), updatedApplication.getQualificationLevel().getId());
        assertEquals(1, updatedApplication.getSectors().size());
        assertTrue(updatedApplication.getSectors().stream().anyMatch(s -> s.getId().equals(updatedSector.getId())));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/delete", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "?success=application-deleted"));

        assertFalse(applicationRepository.findById(savedApplication.getId()).isPresent());
    }

    @Test
    void shouldAllowCandidateToSendManualMessageAndViewHistory() throws Exception {
        String token = token();

        Candidate candidate = seedCandidate("messages." + token + "@imt-atlantique.fr", "Candidate" + token);
        Company company = seedCompany("company.messages." + token + "@imt-atlantique.fr", "Message Company " + token);

        QualificationLevel sharedLevel = qualificationLevelRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> qualificationLevelRepository.save(new QualificationLevel("Fallback Message Level " + token, (short) 30)));
        Sector sharedSector = sectorRepository.save(new Sector("Shared Sector " + token));

        Application application = new Application("candidate-cv-" + token + ".pdf", candidate, sharedLevel);
        application.getSectors().add(sharedSector);
        Application savedApplication = applicationRepository.save(application);

        JobOffer offer = new JobOffer("Message Offer " + token, "Offer description", company, sharedLevel);
        offer.getSectors().add(sharedSector);
        JobOffer savedOffer = jobOfferRepository.save(offer);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        candidate.getAppUser().getMail(), savedApplication.getId(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("message", "Manual message from candidate " + token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "/application/" + savedApplication.getId() + "/matches?success=manual-message-sent"));

        String updatedMessage = "Updated message from candidate " + token;
        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        candidate.getAppUser().getMail(), savedApplication.getId(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("message", updatedMessage))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "/application/" + savedApplication.getId() + "/matches?success=manual-message-sent"));

        String persistedMessage = messageToApplicationRepository
                .findByApplicationIdAndJobOfferId(savedApplication.getId(), savedOffer.getId())
                .orElseThrow()
                .getMessage();
        assertEquals(updatedMessage, persistedMessage);

        mockMvc.perform(get("/managemyapplications/{mail}/messages", candidate.getAppUser().getMail())
                        .session(candidateSession))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidApplicationCreationPayloads() throws Exception {
        String token = token();
        Candidate candidate = seedCandidate("invalid.application." + token + "@imt-atlantique.fr", "InvalidApplicant" + token);
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Application Validation Level " + token, (short) 8));
        Sector sector = sectorRepository.save(new Sector("Application Validation Sector " + token));

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(post("/publishapplication")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "   ")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishapplication?error=cv-required"));

        mockMvc.perform(post("/publishapplication")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "valid-cv.pdf")
                        .param("qualificationLevelId", "999999")
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishapplication?error=qualification-required"));

        mockMvc.perform(post("/publishapplication")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "valid-cv.pdf")
                        .param("qualificationLevelId", level.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishapplication?error=sectors-required"));

        mockMvc.perform(post("/publishapplication")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "valid-cv.pdf")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", "999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishapplication?error=invalid-sector"));
    }

    @Test
    void shouldRejectInvalidCandidateProfileUpdatePayloads() throws Exception {
        String token = token();
        Candidate candidate = seedCandidate("invalid.profile." + token + "@imt-atlantique.fr", "InvalidProfile" + token);
        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("mail", candidate.getAppUser().getMail())
                        .param("lastname", "   ")
                        .param("firstname", "Valid")
                        .param("city", "Nantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycandidateprofile/" + candidate.getAppUser().getMail() + "?error=lastname-required"));

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("mail", candidate.getAppUser().getMail())
                        .param("lastname", "x".repeat(51))
                        .param("firstname", "Valid")
                        .param("city", "Nantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycandidateprofile/" + candidate.getAppUser().getMail() + "?error=lastname-too-long"));

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("mail", candidate.getAppUser().getMail())
                        .param("lastname", "Valid")
                        .param("firstname", "x".repeat(51))
                        .param("city", "Nantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycandidateprofile/" + candidate.getAppUser().getMail() + "?error=firstname-too-long"));

        mockMvc.perform(post("/modifycandidateprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("mail", candidate.getAppUser().getMail())
                        .param("lastname", "Valid")
                        .param("firstname", "Valid")
                        .param("city", "x".repeat(101)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycandidateprofile/" + candidate.getAppUser().getMail() + "?error=city-too-long"));
    }

    @Test
    void shouldRejectInvalidManualMessagePayloadsForCandidate() throws Exception {
        String token = token();

        Candidate candidate = seedCandidate("messages.invalid." + token + "@imt-atlantique.fr", "InvalidMsgCandidate" + token);
        Company matchingCompany = seedCompany("company.matching." + token + "@imt-atlantique.fr", "Matching Company " + token);
        Company nonMatchingCompany = seedCompany("company.nonmatching." + token + "@imt-atlantique.fr", "NonMatching Company " + token);

        QualificationLevel sharedLevel = qualificationLevelRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> qualificationLevelRepository.save(new QualificationLevel("Fallback Candidate Message Level " + token, (short) 31)));
        Sector sharedSector = sectorRepository.save(new Sector("Shared Candidate Message Sector " + token));
        Sector nonMatchingSector = sectorRepository.save(new Sector("Non Matching Candidate Message Sector " + token));

        Application application = new Application("candidate-message-cv-" + token + ".pdf", candidate, sharedLevel);
        application.getSectors().add(sharedSector);
        Application savedApplication = applicationRepository.save(application);

        JobOffer matchingOffer = new JobOffer("Matching offer " + token, "Offer description", matchingCompany, sharedLevel);
        matchingOffer.getSectors().add(sharedSector);
        JobOffer savedMatchingOffer = jobOfferRepository.save(matchingOffer);

        JobOffer nonMatchingOffer = new JobOffer("Non matching offer " + token, "Offer description", nonMatchingCompany, sharedLevel);
        nonMatchingOffer.getSectors().add(nonMatchingSector);
        JobOffer savedNonMatchingOffer = jobOfferRepository.save(nonMatchingOffer);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        candidate.getAppUser().getMail(), savedApplication.getId(), savedMatchingOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("message", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "/application/" + savedApplication.getId() + "/matches?error=message-required"));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        candidate.getAppUser().getMail(), savedApplication.getId(), savedMatchingOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("message", "x".repeat(4001)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyapplications/" + candidate.getAppUser().getMail() + "/application/" + savedApplication.getId() + "/matches?error=message-too-long"));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/offer/{offerId}/message",
                        candidate.getAppUser().getMail(), savedApplication.getId(), savedNonMatchingOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("message", "This message should fail"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidApplicationUpdatePayloads() throws Exception {
        String token = token();
        Candidate candidate = seedCandidate("invalid.update." + token + "@imt-atlantique.fr", "InvalidUpdateCandidate" + token);
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Application Update Validation Level " + token, (short) 12));
        Sector sector = sectorRepository.save(new Sector("Application Update Validation Sector " + token));

        Application application = new Application("initial-cv-" + token + ".pdf", candidate, level);
        application.getSectors().add(sector);
        Application savedApplication = applicationRepository.save(application);

        MockHttpSession candidateSession = buildSession(candidate.getAppUser().getMail(), AppUser.UserType.applicant);

        String editBase = "/managemyapplications/" + candidate.getAppUser().getMail() + "/application/" + savedApplication.getId() + "/edit?error=";

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "   ")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "cv-required"));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "updated-cv.pdf")
                        .param("qualificationLevelId", "999999")
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "qualification-required"));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "updated-cv.pdf")
                        .param("qualificationLevelId", level.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "sectors-required"));

        mockMvc.perform(post("/managemyapplications/{mail}/application/{applicationId}/update", candidate.getAppUser().getMail(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(candidateSession)
                        .param("cv", "updated-cv.pdf")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", "999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "invalid-sector"));
    }

    private Candidate seedCandidate(String mail, String lastname) {
        AppUser candidateUser = appUserRepository.save(new AppUser(mail, "pwd1234", AppUser.UserType.applicant));
        return candidateRepository.save(new Candidate(candidateUser, lastname, "First", "Nantes"));
    }

    private Company seedCompany(String mail, String denomination) {
        AppUser companyUser = appUserRepository.save(new AppUser(mail, "pwd1234", AppUser.UserType.company));
        return companyRepository.save(new Company(companyUser, denomination, "Company description", "Rennes"));
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
