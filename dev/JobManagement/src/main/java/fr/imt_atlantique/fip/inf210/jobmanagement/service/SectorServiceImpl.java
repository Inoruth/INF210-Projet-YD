package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: SectorServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Sector;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.SectorJpaRepository;

@Service
public class SectorServiceImpl implements SectorService {
    
    @Autowired
    private SectorJpaRepository sectorRepository;

    // Cette methode implemente l operation getAllSectors.
    @Override
    public List<Sector> getAllSectors() {
        return sectorRepository.findAll();
    }
}
