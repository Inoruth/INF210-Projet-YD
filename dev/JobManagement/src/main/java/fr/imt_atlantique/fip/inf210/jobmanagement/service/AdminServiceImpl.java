package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: AdminServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AdminJpaRepository;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminJpaRepository adminRepository;

    // Cette methode implemente l operation AdminServiceImpl.
    public AdminServiceImpl(AdminJpaRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Cette methode implemente l operation findByMail.
    @Override
    public Optional<Admin> findByMail(String mail) {
        return adminRepository.findByAppUserMail(mail);
    }

    // Cette methode implemente l operation save.
    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}
