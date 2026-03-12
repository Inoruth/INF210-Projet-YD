package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: QualificationLevelServiceTest
 * Cette classe teste la logique du service en mode unitaire.
 * Les dependances sont simulees pour isoler le comportement metier.
 * Les scenarios couvrent les cas nominaux et les cas d'erreur.
 * Les assertions verifient les resultats et les interactions attendues.
 */

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelServiceImpl;

class QualificationLevelServiceTest {

    private final QualificationLevelRepository repository = mock(QualificationLevelRepository.class);
    private final QualificationLevelServiceImpl service = new QualificationLevelServiceImpl();

    // Ce test verifie le comportement de setUp.
    private void setUp() {
        ReflectionTestUtils.setField(service, "qualificationLevelRepository", repository);
    }

    // Ce test verifie le comportement de shouldReturnAllQualificationLevels.
    @Test
    void shouldReturnAllQualificationLevels() {
        setUp();
        when(repository.findAll()).thenReturn(List.of(
                new QualificationLevel("Licence", (short) 3),
                new QualificationLevel("Master", (short) 4)
        ));

        List<QualificationLevel> levels = service.getAllQualificationLevels();

        assertEquals(2, levels.size());
        verify(repository).findAll();
    }

    // Ce test verifie le comportement de shouldReturnNoQualificationLevelsWhenRepositoryIsEmpty.
    @Test
    void shouldReturnNoQualificationLevelsWhenRepositoryIsEmpty() {
        setUp();
        when(repository.findAll()).thenReturn(List.of());

        List<QualificationLevel> levels = service.getAllQualificationLevels();

        assertEquals(0, levels.size());
        verify(repository).findAll();
    }
}
