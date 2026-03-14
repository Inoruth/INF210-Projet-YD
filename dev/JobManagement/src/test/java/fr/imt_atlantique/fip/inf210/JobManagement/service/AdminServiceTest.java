package fr.imt_atlantique.fip.inf210.JobManagement.service;

/*
 * Fichier: AdminServiceTest
 * Cette classe teste la logique du service en mode unitaire.
 * Les dependances sont simulees pour isoler le comportement metier.
 * Les scenarios couvrent les cas nominaux et les cas d'erreur.
 * Les assertions verifient les resultats et les interactions attendues.
 */

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AdminJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AdminService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AdminServiceImpl;

class AdminServiceTest {

    private final AdminJpaRepository repository = mock(AdminJpaRepository.class);
    private final AdminService service = new AdminServiceImpl(repository);

    // Ce test verifie le comportement de shouldFindAdminByMail.
    @Test
    void shouldFindAdminByMail() {
        AppUser user = new AppUser("admin.service@imt-atlantique.fr", "pwd123", AppUser.UserType.admin);
        Admin admin = new Admin(user);
        when(repository.findByAppUserMail("admin.service@imt-atlantique.fr")).thenReturn(Optional.of(admin));

        Optional<Admin> found = service.findByMail("admin.service@imt-atlantique.fr");

        assertTrue(found.isPresent());
        verify(repository).findByAppUserMail("admin.service@imt-atlantique.fr");
    }

    // Ce test verifie le comportement de shouldReturnEmptyWhenAdminMailUnknown.
    @Test
    void shouldReturnEmptyWhenAdminMailUnknown() {
        when(repository.findByAppUserMail("unknown.admin@imt-atlantique.fr")).thenReturn(Optional.empty());

        Optional<Admin> found = service.findByMail("unknown.admin@imt-atlantique.fr");

        assertFalse(found.isPresent());
        verify(repository).findByAppUserMail("unknown.admin@imt-atlantique.fr");
    }

    // Ce test verifie le comportement de shouldSaveAdmin.
    @Test
    void shouldSaveAdmin() {
        Admin admin = new Admin(new AppUser("admin.save@imt-atlantique.fr", "pwd123", AppUser.UserType.admin));
        when(repository.save(admin)).thenReturn(admin);

        Admin saved = service.save(admin);

        assertEquals(admin, saved);
        verify(repository).save(admin);
    }
}
