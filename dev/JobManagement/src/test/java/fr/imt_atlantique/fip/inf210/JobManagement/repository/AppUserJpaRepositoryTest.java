package fr.imt_atlantique.fip.inf210.JobManagement.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;

@DataJpaTest
class AppUserJpaRepositoryTest {

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Test
    void shouldSaveAndFindByMail() {
        AppUser user = new AppUser("repo.test@imt-atlantique.fr", "pwd123", AppUser.UserType.applicant);
        appUserRepository.save(user);

        Optional<AppUser> found = appUserRepository.findByMail("repo.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("repo.test@imt-atlantique.fr", found.get().getMail());
        assertEquals(AppUser.UserType.applicant, found.get().getUsertype());
    }

    @Test
    void shouldReturnTrueWhenMailExists() {
        AppUser user = new AppUser("exists.test@imt-atlantique.fr", "pwd123", AppUser.UserType.company);
        appUserRepository.save(user);

        assertTrue(appUserRepository.existsByMail("exists.test@imt-atlantique.fr"));
        assertFalse(appUserRepository.existsByMail("missing.test@imt-atlantique.fr"));
    }
}
