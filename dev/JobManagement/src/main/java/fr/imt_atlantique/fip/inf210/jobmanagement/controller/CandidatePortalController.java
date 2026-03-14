package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: CandidatePortalController
 * Ce controller expose les fonctionnalites vitales de l espace candidat.
 * Il couvre la gestion du profil, la publication des candidatures et la consultation des offres matchantes.
 * Les verifications d acces appliquent le role et la propriete des ressources.
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CandidateService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.SectorService;
import jakarta.servlet.http.HttpSession;

@Controller
public class CandidatePortalController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;
    private final JobOfferService jobOfferService;
    private final SectorService sectorService;
    private final QualificationLevelService qualificationLevelService;
    private final SectorJpaRepository sectorRepository;
    private final QualificationLevelRepository qualificationLevelRepository;

    public CandidatePortalController(CandidateService candidateService,
                                     ApplicationService applicationService,
                                     JobOfferService jobOfferService,
                                     SectorService sectorService,
                                     QualificationLevelService qualificationLevelService,
                                     SectorJpaRepository sectorRepository,
                                     QualificationLevelRepository qualificationLevelRepository) {
        this.candidateService = candidateService;
        this.applicationService = applicationService;
        this.jobOfferService = jobOfferService;
        this.sectorService = sectorService;
        this.qualificationLevelService = qualificationLevelService;
        this.sectorRepository = sectorRepository;
        this.qualificationLevelRepository = qualificationLevelRepository;
    }

    // Affiche les candidatures du candidat connecte (ou ciblees par un admin).
    @GetMapping("/managemyapplications/{mail:.+}")
    public ModelAndView getMyApplications(@PathVariable String mail, HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);
        List<Application> applications = applicationService.findByCandidateId(candidate.getId());

        ModelAndView mav = new ModelAndView("managemyapplicationstab.html");
        mav.addObject("candidate", candidate);
        mav.addObject("applications", applications);
        return mav;
    }

    // Ouvre le formulaire de publication de candidature pour le candidat connecte.
    @GetMapping("/publishapplication")
    public ModelAndView getPublishApplicationForm(HttpSession session) {
        String candidateMail = requireCandidateSession(session);
        Candidate candidate = findCandidateOrThrow(candidateMail);

        ModelAndView mav = new ModelAndView("publishapplication.html");
        mav.addObject("candidate", candidate);
        mav.addObject("sectors", getSortedSectors());
        mav.addObject("qualificationLevels", getSortedQualificationLevels());
        return mav;
    }

    // Publie une nouvelle candidature pour le candidat connecte.
    @PostMapping("/publishapplication")
    public String publishApplication(
            @RequestParam("cv") String cv,
            @RequestParam("qualificationLevelId") Integer qualificationLevelId,
            @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
            HttpSession session
    ) {
        String candidateMail = requireCandidateSession(session);
        Candidate candidate = findCandidateOrThrow(candidateMail);

        String normalizedCv = normalizeRequiredText(cv);
        if (normalizedCv.isEmpty()) {
            return "redirect:/publishapplication?error=cv-required";
        }

        QualificationLevel qualificationLevel = qualificationLevelRepository.findById(qualificationLevelId)
                .orElse(null);
        if (qualificationLevel == null) {
            return "redirect:/publishapplication?error=qualification-required";
        }

        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        if (selectedSectorIds.isEmpty()) {
            return "redirect:/publishapplication?error=sectors-required";
        }

        List<Sector> sectors = sectorRepository.findAllById(selectedSectorIds);
        if (sectors.size() != selectedSectorIds.size()) {
            return "redirect:/publishapplication?error=invalid-sector";
        }

        Application newApplication = new Application(normalizedCv, candidate, qualificationLevel);
        newApplication.setSectors(new LinkedHashSet<>(sectors));
        applicationService.save(newApplication);

        return "redirect:/managemyapplications/" + candidateMail + "?success=application-created";
    }

    // Ouvre le formulaire de modification d une candidature appartenant au candidat.
    @GetMapping("/managemyapplications/{mail:.+}/application/{applicationId}/edit")
    public ModelAndView getModifyApplicationForm(@PathVariable String mail,
                                                 @PathVariable Integer applicationId,
                                                 HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);
        Application jobApplication = applicationService.findByIdAndCandidateId(applicationId, candidate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for this candidate"));

        ModelAndView mav = new ModelAndView("modifyapplication.html");
        mav.addObject("candidate", candidate);
        mav.addObject("jobApplication", jobApplication);
        mav.addObject("sectors", getSortedSectors());
        mav.addObject("qualificationLevels", getSortedQualificationLevels());
        return mav;
    }

    // Met a jour une candidature appartenant au candidat.
    @PostMapping("/managemyapplications/{mail:.+}/application/{applicationId}/update")
    public String updateApplication(@PathVariable String mail,
                                    @PathVariable Integer applicationId,
                                    @RequestParam("cv") String cv,
                                    @RequestParam("qualificationLevelId") Integer qualificationLevelId,
                                    @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
                                    HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);
        Application jobApplication = applicationService.findByIdAndCandidateId(applicationId, candidate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for this candidate"));

        String normalizedCv = normalizeRequiredText(cv);
        if (normalizedCv.isEmpty()) {
            return "redirect:/managemyapplications/" + mail + "/application/" + applicationId + "/edit?error=cv-required";
        }

        QualificationLevel qualificationLevel = qualificationLevelRepository.findById(qualificationLevelId)
                .orElse(null);
        if (qualificationLevel == null) {
            return "redirect:/managemyapplications/" + mail + "/application/" + applicationId + "/edit?error=qualification-required";
        }

        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        if (selectedSectorIds.isEmpty()) {
            return "redirect:/managemyapplications/" + mail + "/application/" + applicationId + "/edit?error=sectors-required";
        }

        List<Sector> sectors = sectorRepository.findAllById(selectedSectorIds);
        if (sectors.size() != selectedSectorIds.size()) {
            return "redirect:/managemyapplications/" + mail + "/application/" + applicationId + "/edit?error=invalid-sector";
        }

        jobApplication.setCv(normalizedCv);
        jobApplication.setQualificationLevel(qualificationLevel);
        jobApplication.setSectors(new LinkedHashSet<>(sectors));
        applicationService.save(jobApplication);

        return "redirect:/managemyapplications/" + mail + "?success=application-updated";
    }

    // Supprime une candidature appartenant au candidat.
    @PostMapping("/managemyapplications/{mail:.+}/application/{applicationId}/delete")
    public String deleteApplication(@PathVariable String mail,
                                    @PathVariable Integer applicationId,
                                    HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);
        Application jobApplication = applicationService.findByIdAndCandidateId(applicationId, candidate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for this candidate"));

        applicationService.deleteByIdAndCandidateId(jobApplication.getId(), candidate.getId());
        return "redirect:/managemyapplications/" + mail + "?success=application-deleted";
    }

    // Ouvre le formulaire de modification du profil candidat.
    @GetMapping("/modifycandidateprofile/{mail:.+}")
    public ModelAndView getModifyCandidateProfile(@PathVariable String mail, HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);

        ModelAndView mav = new ModelAndView("modifycandidateprofile.html");
        mav.addObject("candidate", candidate);
        return mav;
    }

    // Met a jour le profil candidat.
    @PostMapping("/modifycandidateprofile")
    public String modifyCandidateProfile(
            @RequestParam("mail") String mail,
            @RequestParam("lastname") String lastname,
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "city", required = false) String city,
            HttpSession session
    ) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);

        String normalizedLastname = normalizeRequiredText(lastname);
        if (normalizedLastname.isEmpty()) {
            return "redirect:/modifycandidateprofile/" + mail + "?error=lastname-required";
        }
        if (normalizedLastname.length() > 50) {
            return "redirect:/modifycandidateprofile/" + mail + "?error=lastname-too-long";
        }

        String normalizedFirstname = normalizeOptionalText(firstname);
        if (normalizedFirstname != null && normalizedFirstname.length() > 50) {
            return "redirect:/modifycandidateprofile/" + mail + "?error=firstname-too-long";
        }

        String normalizedCity = normalizeOptionalText(city);
        if (normalizedCity != null && normalizedCity.length() > 100) {
            return "redirect:/modifycandidateprofile/" + mail + "?error=city-too-long";
        }

        candidate.setLastname(normalizedLastname);
        candidate.setFirstname(normalizedFirstname);
        candidate.setCity(normalizedCity);
        candidateService.save(candidate);

        return "redirect:/managemyapplications/" + mail + "?success=profile-updated";
    }

    // Liste les offres qui matchent une candidature du candidat.
    @GetMapping("/managemyapplications/{mail:.+}/application/{applicationId}/matches")
    public ModelAndView getMatchingOffersForApplication(@PathVariable String mail,
                                                         @PathVariable Integer applicationId,
                                                         HttpSession session) {
        requireCandidateOwnerOrAdmin(session, mail);

        Candidate candidate = findCandidateOrThrow(mail);
        Application ownedApplication = applicationService.findByIdAndCandidateId(applicationId, candidate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for this candidate"));

        List<JobOffer> offers = jobOfferService.findMatchingByApplicationId(ownedApplication.getId());

        ModelAndView mav = new ModelAndView("applicationmatchingofferstab.html");
        mav.addObject("candidate", candidate);
        mav.addObject("jobApplication", ownedApplication);
        mav.addObject("offers", offers);
        return mav;
    }

    private Candidate findCandidateOrThrow(String mail) {
        return candidateService.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
    }

    private List<Sector> getSortedSectors() {
        return sectorService.getAllSectors().stream()
                .sorted(Comparator.comparing(Sector::getLabel, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<QualificationLevel> getSortedQualificationLevels() {
        return qualificationLevelService.getAllQualificationLevels().stream()
                .sorted(Comparator
                        .comparing(QualificationLevel::getRank, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(QualificationLevel::getLabel, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private Set<Integer> normalizeSectorIds(List<Integer> sectorIds) {
        if (sectorIds == null) {
            return Collections.emptySet();
        }
        return sectorIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeRequiredText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void requireCandidateOwnerOrAdmin(HttpSession session, String targetMail) {
        AuthenticatedSession authenticatedSession = requireAuthenticatedSession(session);

        boolean isAdmin = AppUser.UserType.admin.name().equalsIgnoreCase(authenticatedSession.userType());
        boolean isCandidateOwner = AppUser.UserType.applicant.name().equalsIgnoreCase(authenticatedSession.userType())
                && authenticatedSession.mail().equalsIgnoreCase(targetMail);

        if (!isAdmin && !isCandidateOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Candidate owner access required");
        }
    }

    private String requireCandidateSession(HttpSession session) {
        AuthenticatedSession authenticatedSession = requireAuthenticatedSession(session);

        if (!AppUser.UserType.applicant.name().equalsIgnoreCase(authenticatedSession.userType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Candidate access required");
        }

        return authenticatedSession.mail();
    }

    private AuthenticatedSession requireAuthenticatedSession(HttpSession session) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Object sessionMail = session.getAttribute("userMail");
        Object sessionType = session.getAttribute("userType");
        if (!(sessionMail instanceof String mail) || mail.isBlank()
                || !(sessionType instanceof String userType) || userType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return new AuthenticatedSession(mail, userType);
    }

    private record AuthenticatedSession(String mail, String userType) {
    }
}
