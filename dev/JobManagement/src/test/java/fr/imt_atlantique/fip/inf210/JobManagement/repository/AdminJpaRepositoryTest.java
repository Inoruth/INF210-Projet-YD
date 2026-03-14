package fr.imt_atlantique.fip.inf210.JobManagement.repository;

/*
 * Fichier: AdminJpaRepositoryTest
 * Cette classe teste les requetes JPA du repository cible.
 * Les tests s'executent sur une base de test pour valider la persistance et les recherches.
 * Les assertions controlent la coherence des resultats retournes par les methodes.
 * L'objectif est de securiser le mapping et le comportement des requetes.
 */

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AdminJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;

@DataJpaTest
class AdminJpaRepositoryTest {

    @Autowired
    private AdminJpaRepository adminRepository;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    // Ce test verifie le comportement de shouldFindAdminByAppUserMail.
    @Test
    void shouldFindAdminByAppUserMail() {
        AppUser user = appUserRepository.save(new AppUser(
                "admin.repo.test@imt-atlantique.fr",
                "pwd123",
                AppUser.UserType.admin
        ));
        adminRepository.save(new Admin(user));

        Optional<Admin> found = adminRepository.findByAppUserMail("admin.repo.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertTrue(found.get().getId().equals(user.getId()));
        assertFalse(adminRepository.findByAppUserMail("unknown.admin@imt-atlantique.fr").isPresent());
    }
}
