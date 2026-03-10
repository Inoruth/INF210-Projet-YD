package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldFindAdminByMail() {
        AppUser user = new AppUser("admin.service@imt-atlantique.fr", "pwd123", AppUser.UserType.admin);
        Admin admin = new Admin(user);
        when(repository.findByAppUserMail("admin.service@imt-atlantique.fr")).thenReturn(Optional.of(admin));

        Optional<Admin> found = service.findByMail("admin.service@imt-atlantique.fr");

        assertTrue(found.isPresent());
        verify(repository).findByAppUserMail("admin.service@imt-atlantique.fr");
    }

    @Test
    void shouldSaveAdmin() {
        Admin admin = new Admin(new AppUser("admin.save@imt-atlantique.fr", "pwd123", AppUser.UserType.admin));
        when(repository.save(admin)).thenReturn(admin);

        Admin saved = service.save(admin);

        assertEquals(admin, saved);
        verify(repository).save(admin);
    }
}
