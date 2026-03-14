package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: CompanyController
 * Ce controller expose les pages publiques liees aux entreprises.
 * Il gere le listing des entreprises et l affichage du detail d une entreprise.
 * Les donnees sont construites via la couche service puis projetees vers la vue Thymeleaf.
 */

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CompanyService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;

@Controller
public class CompanyController {

    private final CompanyService companyService;
    private final JobOfferService jobOfferService;

    public CompanyController(CompanyService companyService, JobOfferService jobOfferService) {
        this.companyService = companyService;
        this.jobOfferService = jobOfferService;
    }

    // Affiche toutes les entreprises avec leur nombre d offres.
    @GetMapping("/allcompanies")
    public ModelAndView getAllCompanies() {
        List<CompanySummaryView> companies = companyService.findAll().stream()
                .map(company -> new CompanySummaryView(
                        company.getAppUser().getMail(),
                        company.getDenomination(),
                        company.getDescription(),
                        company.getCity(),
                        jobOfferService.findByCompanyId(company.getId()).size()
                ))
                .sorted(Comparator.comparing(CompanySummaryView::denomination, String.CASE_INSENSITIVE_ORDER))
                .toList();

        ModelAndView mav = new ModelAndView("allcompaniestab.html");
        mav.addObject("companies", companies);
        return mav;
    }

    // Affiche le detail d une entreprise et ses offres publiees.
    @GetMapping("/company/{mail:.+}")
    public ModelAndView getCompanyDetails(@PathVariable String mail) {
        Company company = companyService.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        List<JobOffer> offers = jobOfferService.findByCompanyId(company.getId());

        ModelAndView mav = new ModelAndView("companydetails.html");
        mav.addObject("company", company);
        mav.addObject("offers", offers);
        return mav;
    }

    public record CompanySummaryView(
            String mail,
            String denomination,
            String description,
            String city,
            int offersCount
    ) {
    }
}
