package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.QualificationLevelServiceImpl;

class QualificationLevelServiceTest {

    private final QualificationLevelRepository repository = mock(QualificationLevelRepository.class);
    private final QualificationLevelServiceImpl service = new QualificationLevelServiceImpl();

    private void setUp() {
        ReflectionTestUtils.setField(service, "qualificationLevelRepository", repository);
    }

    @Test
    void shouldReturnAllQualificationLevels() {
        setUp();
        when(repository.findAll()).thenReturn(List.of(
                new QualificationLevel("Licence", (short) 3),
                new QualificationLevel("Master", (short) 4)
        ));

        List<QualificationLevel> levels = service.getAllQualificationLevels();

        assertEquals(2, levels.size());
    }
}
