package fr.imt_atlantique.fip.inf210.JobManagement.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@SpringBootTest
class SectorJpaRepositoryTest {

    @Autowired
    private SectorJpaRepository sectorRepository;

    @Test
    void shouldSaveAndListSectors() {
        Sector sector = new Sector("QA/Testing");
        sectorRepository.save(sector);

        List<Sector> sectors = sectorRepository.findAll();

        assertFalse(sectors.isEmpty());
        assertTrue(sectors.stream().anyMatch(s -> "QA/Testing".equals(s.getLabel())));
    }
}
