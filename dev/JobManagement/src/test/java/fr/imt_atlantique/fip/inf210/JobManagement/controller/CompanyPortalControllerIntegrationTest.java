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
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;
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
        private MessageToOfferJpaRepository messageToOfferRepository;

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

        mockMvc.perform(get("/managemyoffers/{mail}/messages", "anonymous.company@imt-atlantique.fr"))
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

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        "anonymous.company@imt-atlantique.fr", 1, 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Unauthorized message"))
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

        mockMvc.perform(get("/managemyoffers/{mail}/messages", targetCompany.getAppUser().getMail())
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

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        targetCompany.getAppUser().getMail(), savedTargetOffer.getId(), 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(ownerSession)
                        .param("message", "Forbidden message"))
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

    @Test
    void shouldAllowCompanyToSendManualMessageAndViewHistory() throws Exception {
        String token = token();

        Company company = seedCompany("messages." + token + "@imt-atlantique.fr", "Messages Corp " + token);
        Candidate candidate = seedCandidate("candidate.messages." + token + "@imt-atlantique.fr", "Candidate" + token);

        QualificationLevel sharedLevel = qualificationLevelRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> qualificationLevelRepository.save(new QualificationLevel("Fallback Message Level " + token, (short) 30)));
        Sector sharedSector = sectorRepository.save(new Sector("Shared Sector " + token));

        JobOffer offer = new JobOffer("Messaging offer " + token, "Offer description", company, sharedLevel);
        offer.getSectors().add(sharedSector);
        JobOffer savedOffer = jobOfferRepository.save(offer);

        Application application = new Application("cv-message-" + token + ".pdf", candidate, sharedLevel);
        application.getSectors().add(sharedSector);
        Application savedApplication = applicationRepository.save(application);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        company.getAppUser().getMail(), savedOffer.getId(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("message", "Manual message from company " + token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "/offer/" + savedOffer.getId() + "/matches?success=manual-message-sent"));

        String updatedMessage = "Updated message from company " + token;
        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        company.getAppUser().getMail(), savedOffer.getId(), savedApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("message", updatedMessage)
                        .param("origin", "messages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "/messages?success=manual-message-sent"));

        String persistedMessage = messageToOfferRepository
                .findByJobOfferIdAndApplicationId(savedOffer.getId(), savedApplication.getId())
                .orElseThrow()
                .getMessage();
        assertEquals(updatedMessage, persistedMessage);

        mockMvc.perform(get("/managemyoffers/{mail}/messages", company.getAppUser().getMail())
                        .session(companySession))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidOfferCreationPayloads() throws Exception {
        String token = token();
        Company company = seedCompany("invalid.offer." + token + "@imt-atlantique.fr", "Invalid Offer Corp " + token);
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Offer Validation Level " + token, (short) 9));
        Sector sector = sectorRepository.save(new Sector("Offer Validation Sector " + token));

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "   ")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishjoboffer?error=title-required"));

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "x".repeat(121))
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishjoboffer?error=title-too-long"));

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", "999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishjoboffer?error=qualification-required"));

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishjoboffer?error=sectors-required"));

        mockMvc.perform(post("/publishjoboffer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", "999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/publishjoboffer?error=invalid-sector"));
    }

    @Test
    void shouldRejectInvalidCompanyProfileUpdatePayloads() throws Exception {
        String token = token();
        Company company = seedCompany("invalid.profile." + token + "@imt-atlantique.fr", "Profile Invalid Corp " + token);
        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(post("/modifycompanyprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("mail", company.getAppUser().getMail())
                        .param("denomination", "   ")
                        .param("description", "Some description")
                        .param("city", "Nantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycompanyprofile/" + company.getAppUser().getMail() + "?error=denomination-required"));

        mockMvc.perform(post("/modifycompanyprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("mail", company.getAppUser().getMail())
                        .param("denomination", "x".repeat(101))
                        .param("description", "Some description")
                        .param("city", "Nantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycompanyprofile/" + company.getAppUser().getMail() + "?error=denomination-too-long"));

        mockMvc.perform(post("/modifycompanyprofile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("mail", company.getAppUser().getMail())
                        .param("denomination", "Valid denomination")
                        .param("description", "Some description")
                        .param("city", "x".repeat(101)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/modifycompanyprofile/" + company.getAppUser().getMail() + "?error=city-too-long"));
    }

    @Test
    void shouldRejectInvalidManualMessagePayloadsForCompany() throws Exception {
        String token = token();

        Company company = seedCompany("messages.invalid." + token + "@imt-atlantique.fr", "Messages Invalid Corp " + token);
        Candidate matchingCandidate = seedCandidate("candidate.matching." + token + "@imt-atlantique.fr", "CandidateMatching" + token);
        Candidate nonMatchingCandidate = seedCandidate("candidate.nonmatching." + token + "@imt-atlantique.fr", "CandidateNonMatching" + token);

        QualificationLevel sharedLevel = qualificationLevelRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> qualificationLevelRepository.save(new QualificationLevel("Fallback Message Validation Level " + token, (short) 31)));
        Sector sharedSector = sectorRepository.save(new Sector("Shared Message Validation Sector " + token));
        Sector nonMatchingSector = sectorRepository.save(new Sector("Non Matching Message Validation Sector " + token));

        JobOffer offer = new JobOffer("Messaging validation offer " + token, "Offer description", company, sharedLevel);
        offer.getSectors().add(sharedSector);
        JobOffer savedOffer = jobOfferRepository.save(offer);

        Application matchingApplication = new Application("matching-cv-" + token + ".pdf", matchingCandidate, sharedLevel);
        matchingApplication.getSectors().add(sharedSector);
        Application savedMatchingApplication = applicationRepository.save(matchingApplication);

        Application nonMatchingApplication = new Application("non-matching-cv-" + token + ".pdf", nonMatchingCandidate, sharedLevel);
        nonMatchingApplication.getSectors().add(nonMatchingSector);
        Application savedNonMatchingApplication = applicationRepository.save(nonMatchingApplication);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        company.getAppUser().getMail(), savedOffer.getId(), savedMatchingApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("message", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "/offer/" + savedOffer.getId() + "/matches?error=message-required"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        company.getAppUser().getMail(), savedOffer.getId(), savedMatchingApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("message", "x".repeat(4001)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/managemyoffers/" + company.getAppUser().getMail() + "/offer/" + savedOffer.getId() + "/matches?error=message-too-long"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/application/{applicationId}/message",
                        company.getAppUser().getMail(), savedOffer.getId(), savedNonMatchingApplication.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("message", "This message should fail"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidOfferUpdatePayloads() throws Exception {
        String token = token();
        Company company = seedCompany("invalid.update." + token + "@imt-atlantique.fr", "Invalid Update Corp " + token);
        QualificationLevel level = qualificationLevelRepository.save(new QualificationLevel("Offer Update Validation Level " + token, (short) 11));
        Sector sector = sectorRepository.save(new Sector("Offer Update Validation Sector " + token));

        JobOffer offer = new JobOffer("Initial title " + token, "Initial description", company, level);
        offer.getSectors().add(sector);
        JobOffer savedOffer = jobOfferRepository.save(offer);

        MockHttpSession companySession = buildSession(company.getAppUser().getMail(), AppUser.UserType.company);

        String editBase = "/managemyoffers/" + company.getAppUser().getMail() + "/offer/" + savedOffer.getId() + "/edit?error=";

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "   ")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "title-required"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "x".repeat(121))
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "title-too-long"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "   ")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "taskdescription-required"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", "999999")
                        .param("sectorIds", sector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "qualification-required"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "sectors-required"));

        mockMvc.perform(post("/managemyoffers/{mail}/offer/{offerId}/update", company.getAppUser().getMail(), savedOffer.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(companySession)
                        .param("title", "Valid title")
                        .param("taskdescription", "Valid task")
                        .param("qualificationLevelId", level.getId().toString())
                        .param("sectorIds", "999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(editBase + "invalid-sector"));
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
