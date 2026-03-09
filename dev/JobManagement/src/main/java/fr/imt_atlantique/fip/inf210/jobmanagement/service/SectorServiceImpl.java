package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@Service
public class SectorServiceImpl implements SectorService {
    
    @Autowired
    private SectorJpaRepository sectorRepository;

    @Override
    public List<Sector> getAllSectors() {
        return sectorRepository.findAll();
    }
}
