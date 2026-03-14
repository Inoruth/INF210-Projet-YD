package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: CompanyPortalController
 * Ce controller expose les fonctionnalites vitales de l espace entreprise.
 * Il couvre la gestion du profil, la publication des offres et la consultation des candidatures matchantes.
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
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CompanyService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.SectorService;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyPortalController {

    private final CompanyService companyService;
    private final JobOfferService jobOfferService;
    private final ApplicationService applicationService;
    private final SectorService sectorService;
    private final QualificationLevelService qualificationLevelService;
    private final SectorJpaRepository sectorRepository;
    private final QualificationLevelRepository qualificationLevelRepository;

    public CompanyPortalController(CompanyService companyService,
                                   JobOfferService jobOfferService,
                                   ApplicationService applicationService,
                                   SectorService sectorService,
                                   QualificationLevelService qualificationLevelService,
                                   SectorJpaRepository sectorRepository,
                                   QualificationLevelRepository qualificationLevelRepository) {
        this.companyService = companyService;
        this.jobOfferService = jobOfferService;
        this.applicationService = applicationService;
        this.sectorService = sectorService;
        this.qualificationLevelService = qualificationLevelService;
        this.sectorRepository = sectorRepository;
        this.qualificationLevelRepository = qualificationLevelRepository;
    }

    // Affiche les offres de l entreprise connectee (ou ciblee par un admin).
    @GetMapping("/managemyoffers/{mail:.+}")
    public ModelAndView getMyOffers(@PathVariable String mail, HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);
        List<JobOffer> offers = jobOfferService.findByCompanyId(company.getId());

        ModelAndView mav = new ModelAndView("managemyofferstab.html");
        mav.addObject("company", company);
        mav.addObject("offers", offers);
        return mav;
    }

    // Ouvre le formulaire de publication d offre pour l entreprise connectee.
    @GetMapping("/publishjoboffer")
    public ModelAndView getPublishJobOfferForm(HttpSession session) {
        String companyMail = requireCompanySession(session);
        Company company = findCompanyOrThrow(companyMail);

        ModelAndView mav = new ModelAndView("publishjoboffer.html");
        mav.addObject("company", company);
        mav.addObject("sectors", getSortedSectors());
        mav.addObject("qualificationLevels", getSortedQualificationLevels());
        return mav;
    }

    // Publie une nouvelle offre pour l entreprise connectee.
    @PostMapping("/publishjoboffer")
    public String publishJobOffer(
            @RequestParam("title") String title,
            @RequestParam("taskdescription") String taskDescription,
            @RequestParam("qualificationLevelId") Integer qualificationLevelId,
            @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
            HttpSession session
    ) {
        String companyMail = requireCompanySession(session);
        Company company = findCompanyOrThrow(companyMail);

        String normalizedTitle = normalizeRequiredText(title);
        if (normalizedTitle.isEmpty()) {
            return "redirect:/publishjoboffer?error=title-required";
        }
        if (normalizedTitle.length() > 120) {
            return "redirect:/publishjoboffer?error=title-too-long";
        }

        String normalizedTaskDescription = normalizeRequiredText(taskDescription);
        if (normalizedTaskDescription.isEmpty()) {
            return "redirect:/publishjoboffer?error=taskdescription-required";
        }

        QualificationLevel qualificationLevel = qualificationLevelRepository.findById(qualificationLevelId)
                .orElse(null);
        if (qualificationLevel == null) {
            return "redirect:/publishjoboffer?error=qualification-required";
        }

        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        if (selectedSectorIds.isEmpty()) {
            return "redirect:/publishjoboffer?error=sectors-required";
        }

        List<Sector> sectors = sectorRepository.findAllById(selectedSectorIds);
        if (sectors.size() != selectedSectorIds.size()) {
            return "redirect:/publishjoboffer?error=invalid-sector";
        }

        JobOffer newJobOffer = new JobOffer(normalizedTitle, normalizedTaskDescription, company, qualificationLevel);
        newJobOffer.setSectors(new LinkedHashSet<>(sectors));
        jobOfferService.save(newJobOffer);

        return "redirect:/managemyoffers/" + companyMail + "?success=offer-created";
    }

    // Ouvre le formulaire de modification d une offre appartenant a l entreprise.
    @GetMapping("/managemyoffers/{mail:.+}/offer/{offerId}/edit")
    public ModelAndView getModifyJobOfferForm(@PathVariable String mail,
                                              @PathVariable Integer offerId,
                                              HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);
        JobOffer offer = jobOfferService.findByIdAndCompanyId(offerId, company.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job offer not found for this company"));

        ModelAndView mav = new ModelAndView("modifyjoboffer.html");
        mav.addObject("company", company);
        mav.addObject("offer", offer);
        mav.addObject("sectors", getSortedSectors());
        mav.addObject("qualificationLevels", getSortedQualificationLevels());
        return mav;
    }

    // Met a jour une offre appartenant a l entreprise.
    @PostMapping("/managemyoffers/{mail:.+}/offer/{offerId}/update")
    public String updateJobOffer(@PathVariable String mail,
                                 @PathVariable Integer offerId,
                                 @RequestParam("title") String title,
                                 @RequestParam("taskdescription") String taskDescription,
                                 @RequestParam("qualificationLevelId") Integer qualificationLevelId,
                                 @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
                                 HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);
        JobOffer offer = jobOfferService.findByIdAndCompanyId(offerId, company.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job offer not found for this company"));

        String normalizedTitle = normalizeRequiredText(title);
        if (normalizedTitle.isEmpty()) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=title-required";
        }
        if (normalizedTitle.length() > 120) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=title-too-long";
        }

        String normalizedTaskDescription = normalizeRequiredText(taskDescription);
        if (normalizedTaskDescription.isEmpty()) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=taskdescription-required";
        }

        QualificationLevel qualificationLevel = qualificationLevelRepository.findById(qualificationLevelId)
                .orElse(null);
        if (qualificationLevel == null) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=qualification-required";
        }

        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        if (selectedSectorIds.isEmpty()) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=sectors-required";
        }

        List<Sector> sectors = sectorRepository.findAllById(selectedSectorIds);
        if (sectors.size() != selectedSectorIds.size()) {
            return "redirect:/managemyoffers/" + mail + "/offer/" + offerId + "/edit?error=invalid-sector";
        }

        offer.setTitle(normalizedTitle);
        offer.setTaskdescription(normalizedTaskDescription);
        offer.setQualificationLevel(qualificationLevel);
        offer.setSectors(new LinkedHashSet<>(sectors));
        jobOfferService.save(offer);

        return "redirect:/managemyoffers/" + mail + "?success=offer-updated";
    }

    // Supprime une offre appartenant a l entreprise.
    @PostMapping("/managemyoffers/{mail:.+}/offer/{offerId}/delete")
    public String deleteJobOffer(@PathVariable String mail,
                                 @PathVariable Integer offerId,
                                 HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);
        JobOffer offer = jobOfferService.findByIdAndCompanyId(offerId, company.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job offer not found for this company"));

        jobOfferService.deleteByIdAndCompanyId(offer.getId(), company.getId());
        return "redirect:/managemyoffers/" + mail + "?success=offer-deleted";
    }

    // Ouvre le formulaire de modification du profil entreprise.
    @GetMapping("/modifycompanyprofile/{mail:.+}")
    public ModelAndView getModifyCompanyProfile(@PathVariable String mail, HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);

        ModelAndView mav = new ModelAndView("modifycompanyprofile.html");
        mav.addObject("company", company);
        return mav;
    }

    // Met a jour le profil entreprise.
    @PostMapping("/modifycompanyprofile")
    public String modifyCompanyProfile(
            @RequestParam("mail") String mail,
            @RequestParam("denomination") String denomination,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "city", required = false) String city,
            HttpSession session
    ) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);

        String normalizedDenomination = normalizeRequiredText(denomination);
        if (normalizedDenomination.isEmpty()) {
            return "redirect:/modifycompanyprofile/" + mail + "?error=denomination-required";
        }
        if (normalizedDenomination.length() > 100) {
            return "redirect:/modifycompanyprofile/" + mail + "?error=denomination-too-long";
        }

        String normalizedCity = normalizeOptionalText(city);
        if (normalizedCity != null && normalizedCity.length() > 100) {
            return "redirect:/modifycompanyprofile/" + mail + "?error=city-too-long";
        }

        company.setDenomination(normalizedDenomination);
        company.setDescription(normalizeOptionalText(description));
        company.setCity(normalizedCity);
        companyService.save(company);

        return "redirect:/managemyoffers/" + mail + "?success=profile-updated";
    }

    // Liste les candidatures qui matchent une offre de l entreprise.
    @GetMapping("/managemyoffers/{mail:.+}/offer/{offerId}/matches")
    public ModelAndView getMatchingApplicationsForOffer(@PathVariable String mail,
                                                         @PathVariable Integer offerId,
                                                         HttpSession session) {
        requireCompanyOwnerOrAdmin(session, mail);

        Company company = findCompanyOrThrow(mail);
        JobOffer ownedOffer = jobOfferService.findByIdAndCompanyId(offerId, company.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job offer not found for this company"));

        List<Application> matchingApplications = applicationService.findMatchingByJobOfferId(ownedOffer.getId());

        ModelAndView mav = new ModelAndView("offermatchingapplicationstab.html");
        mav.addObject("company", company);
        mav.addObject("offer", ownedOffer);
        mav.addObject("applications", matchingApplications);
        return mav;
    }

    private Company findCompanyOrThrow(String mail) {
        return companyService.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
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

    private void requireCompanyOwnerOrAdmin(HttpSession session, String targetMail) {
        AuthenticatedSession authenticatedSession = requireAuthenticatedSession(session);

        boolean isAdmin = AppUser.UserType.admin.name().equalsIgnoreCase(authenticatedSession.userType());
        boolean isCompanyOwner = AppUser.UserType.company.name().equalsIgnoreCase(authenticatedSession.userType())
                && authenticatedSession.mail().equalsIgnoreCase(targetMail);

        if (!isAdmin && !isCompanyOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company owner access required");
        }
    }

    private String requireCompanySession(HttpSession session) {
        AuthenticatedSession authenticatedSession = requireAuthenticatedSession(session);

        if (!AppUser.UserType.company.name().equalsIgnoreCase(authenticatedSession.userType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company access required");
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
