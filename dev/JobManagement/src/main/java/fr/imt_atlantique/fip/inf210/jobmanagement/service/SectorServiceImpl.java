package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.*;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.*;

@Service
public class SectorServiceImpl implements SectorService {
    
    @Autowired
    private SectorJpaRepository sectorRepository;

    public List<Sector> getAllSectors() {
        return sectorRepository.findAll();
    }
}
