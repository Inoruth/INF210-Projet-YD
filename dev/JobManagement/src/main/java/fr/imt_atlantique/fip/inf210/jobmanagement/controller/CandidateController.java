package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: CandidateController
 * Ce controller expose les pages publiques liees aux candidats.
 * Il gere le listing des candidats et l affichage du detail d un candidat.
 * Les donnees de candidatures associees sont chargees pour construire les vues.
 */

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CandidateService;

@Controller
public class CandidateController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;

    public CandidateController(CandidateService candidateService, ApplicationService applicationService) {
        this.candidateService = candidateService;
        this.applicationService = applicationService;
    }

    // Affiche tous les candidats avec leur nombre de candidatures.
    @GetMapping("/allapplicants")
    public ModelAndView getAllCandidates() {
        List<CandidateSummaryView> candidates = candidateService.findAll().stream()
                .map(candidate -> new CandidateSummaryView(
                        candidate.getAppUser().getMail(),
                        candidate.getLastname(),
                        candidate.getFirstname(),
                        candidate.getCity(),
                        applicationService.findByCandidateId(candidate.getId()).size()
                ))
                .sorted(Comparator.comparing(CandidateSummaryView::lastname, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(CandidateSummaryView::firstname, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        ModelAndView mav = new ModelAndView("allapplicantstab.html");
        mav.addObject("candidates", candidates);
        return mav;
    }

    // Affiche le detail d un candidat et la liste de ses candidatures.
    @GetMapping("/candidate/{mail:.+}")
    public ModelAndView getCandidateDetails(@PathVariable String mail) {
        Candidate candidate = candidateService.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        List<Application> applications = applicationService.findByCandidateId(candidate.getId());

        ModelAndView mav = new ModelAndView("candidatedetails.html");
        mav.addObject("candidate", candidate);
        mav.addObject("applications", applications);
        return mav;
    }

    public record CandidateSummaryView(
            String mail,
            String lastname,
            String firstname,
            String city,
            int applicationsCount
    ) {
    }
}
