package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: QualificationLevelRepositoryTest
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

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;

@DataJpaTest
class QualificationLevelRepositoryTest {

    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    // Ce test verifie le comportement de shouldSaveAndListQualificationLevels.
    @Test
    void shouldSaveAndListQualificationLevels() {
        QualificationLevel ql = new QualificationLevel("Engineer", (short) 7);
        qualificationLevelRepository.save(ql);

        List<QualificationLevel> levels = qualificationLevelRepository.findAll();

        assertFalse(levels.isEmpty());
        assertTrue(levels.stream().anyMatch(level -> "Engineer".equals(level.getLabel()) && level.getRank() == 7));
    }
}
