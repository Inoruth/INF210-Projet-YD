package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: ApplicationController
 * Ce controller expose les vues publiques autour des candidatures.
 * Il couvre le listing global, la recherche par criteres et le detail d une candidature.
 * Les filtres s appuient sur les secteurs et niveaux de qualification disponibles.
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.ApplicationService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.SectorService;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final SectorService sectorService;
    private final QualificationLevelService qualificationLevelService;

    public ApplicationController(ApplicationService applicationService,
                                 SectorService sectorService,
                                 QualificationLevelService qualificationLevelService) {
        this.applicationService = applicationService;
        this.sectorService = sectorService;
        this.qualificationLevelService = qualificationLevelService;
    }

    // Liste toutes les candidatures ou applique les filtres de recherche publics.
    @GetMapping("/allapplications")
    public ModelAndView getAllApplications(
            @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
            @RequestParam(name = "minimumRank", required = false) Short minimumRank
    ) {
        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        boolean hasFilter = !selectedSectorIds.isEmpty() || minimumRank != null;

        List<Application> applications = (hasFilter
                ? applicationService.searchByCriteria(selectedSectorIds, minimumRank)
                : applicationService.findAll())
                .stream()
                .sorted(Comparator
                        .comparing(Application::getAppdate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Application::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        ModelAndView mav = new ModelAndView("allapplicationstab.html");
        mav.addObject("applications", applications);
        mav.addObject("sectors", sectorService.getAllSectors());
        mav.addObject("qualificationLevels", qualificationLevelService.getAllQualificationLevels());
        mav.addObject("selectedSectorIds", selectedSectorIds);
        mav.addObject("minimumRank", minimumRank);
        mav.addObject("hasFilter", hasFilter);
        return mav;
    }

    // Affiche le detail d une candidature.
    @GetMapping("/application/{id}")
    public ModelAndView getApplicationDetails(@PathVariable Integer id) {
        Application jobApplication = applicationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        ModelAndView mav = new ModelAndView("applicationdetails.html");
        mav.addObject("jobApplication", jobApplication);
        return mav;
    }

    private Set<Integer> normalizeSectorIds(List<Integer> sectorIds) {
        if (sectorIds == null) {
            return Collections.emptySet();
        }
        return sectorIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
