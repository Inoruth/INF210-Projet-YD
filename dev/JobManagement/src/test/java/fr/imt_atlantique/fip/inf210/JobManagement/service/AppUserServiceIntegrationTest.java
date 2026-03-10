package fr.imt_atlantique.fip.inf210.JobManagement.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;

@SpringBootTest
@Transactional
class AppUserServiceIntegrationTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Test
    void shouldDeleteApplicantButKeepAdmin() {
        AppUser admin = appUserRepository.save(new AppUser(
                "admin.integration@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.admin
        ));
        AppUser applicant = appUserRepository.save(new AppUser(
                "applicant.integration@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        appUserService.deleteByMail(admin.getMail());
        appUserService.deleteByMail(applicant.getMail());

        assertTrue(appUserRepository.findByMail(admin.getMail()).isPresent());
        assertFalse(appUserRepository.findByMail(applicant.getMail()).isPresent());
    }
}
