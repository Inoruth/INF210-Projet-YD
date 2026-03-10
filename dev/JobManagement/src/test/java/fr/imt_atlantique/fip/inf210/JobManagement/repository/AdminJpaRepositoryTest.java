package fr.imt_atlantique.fip.inf210.JobManagement.repository;

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
