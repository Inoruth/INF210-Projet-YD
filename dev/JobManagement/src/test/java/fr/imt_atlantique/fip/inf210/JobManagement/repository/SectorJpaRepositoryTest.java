package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: SectorJpaRepositoryTest
 * Cette classe teste les requetes JPA du repository cible.
 * Les tests s'executent sur une base de test pour valider la persistance et les recherches.
 * Les assertions controlent la coherence des resultats retournes par les methodes.
 * L'objectif est de securiser le mapping et le comportement des requetes.
 */

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@DataJpaTest
class SectorJpaRepositoryTest {

    @Autowired
    private SectorJpaRepository sectorRepository;

    // Ce test verifie le comportement de shouldSaveAndListSectors.
    @Test
    void shouldSaveAndListSectors() {
        Sector sector = new Sector("QA/Testing");
        sectorRepository.save(sector);

        List<Sector> sectors = sectorRepository.findAll();

        assertFalse(sectors.isEmpty());
        assertTrue(sectors.stream().anyMatch(s -> "QA/Testing".equals(s.getLabel())));
    }
}
