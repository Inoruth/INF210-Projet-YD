package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: JobOfferController
 * Ce controller expose les vues publiques autour des offres d emploi.
 * Il couvre le listing global, la recherche par criteres et le detail d une offre.
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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.JobOfferService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.SectorService;

@Controller
public class JobOfferController {

    private final JobOfferService jobOfferService;
    private final SectorService sectorService;
    private final QualificationLevelService qualificationLevelService;

    public JobOfferController(JobOfferService jobOfferService,
                              SectorService sectorService,
                              QualificationLevelService qualificationLevelService) {
        this.jobOfferService = jobOfferService;
        this.sectorService = sectorService;
        this.qualificationLevelService = qualificationLevelService;
    }

    // Liste toutes les offres ou applique les filtres de recherche publics.
    @GetMapping("/alljobs")
    public ModelAndView getAllJobOffers(
            @RequestParam(name = "sectorIds", required = false) List<Integer> sectorIds,
            @RequestParam(name = "minimumRank", required = false) Short minimumRank
    ) {
        Set<Integer> selectedSectorIds = normalizeSectorIds(sectorIds);
        boolean hasFilter = !selectedSectorIds.isEmpty() || minimumRank != null;

        List<JobOffer> jobs = (hasFilter
                ? jobOfferService.searchByCriteria(selectedSectorIds, minimumRank)
                : jobOfferService.findAll())
                .stream()
                .sorted(Comparator
                        .comparing(JobOffer::getPublicationdate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(JobOffer::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        ModelAndView mav = new ModelAndView("alljobstab.html");
        mav.addObject("jobs", jobs);
        mav.addObject("sectors", sectorService.getAllSectors());
        mav.addObject("qualificationLevels", qualificationLevelService.getAllQualificationLevels());
        mav.addObject("selectedSectorIds", selectedSectorIds);
        mav.addObject("minimumRank", minimumRank);
        mav.addObject("hasFilter", hasFilter);
        return mav;
    }

    // Affiche le detail d une offre d emploi.
    @GetMapping("/job/{id}")
    public ModelAndView getJobOfferDetails(@PathVariable Integer id) {
        JobOffer jobOffer = jobOfferService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job offer not found"));

        ModelAndView mav = new ModelAndView("jobdetails.html");
        mav.addObject("job", jobOffer);
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
