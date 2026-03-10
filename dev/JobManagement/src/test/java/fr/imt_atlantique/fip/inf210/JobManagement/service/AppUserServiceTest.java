package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserServiceImpl;

class AppUserServiceTest {

    private final AppUserJpaRepository repository = mock(AppUserJpaRepository.class);
    private final AppUserService service = new AppUserServiceImpl(repository);

    @Test
    void shouldFindByMail() {
        AppUser user = new AppUser("svc.test@imt-atlantique.fr", "pwd", AppUser.UserType.admin);
        when(repository.findByMail("svc.test@imt-atlantique.fr")).thenReturn(Optional.of(user));

        Optional<AppUser> found = service.findByMail("svc.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("svc.test@imt-atlantique.fr", found.get().getMail());
    }

    @Test
    void shouldDeleteByMailWhenUserExists() {
        AppUser user = new AppUser("delete.test@imt-atlantique.fr", "pwd", AppUser.UserType.applicant);
        when(repository.findByMail("delete.test@imt-atlantique.fr")).thenReturn(Optional.of(user));

        service.deleteByMail("delete.test@imt-atlantique.fr");

        verify(repository, times(1)).delete(user);
    }

    @Test
    void shouldNotDeleteAdminUser() {
        AppUser admin = new AppUser("admin.test@imt-atlantique.fr", "pwd", AppUser.UserType.admin);
        when(repository.findByMail("admin.test@imt-atlantique.fr")).thenReturn(Optional.of(admin));

        service.deleteByMail("admin.test@imt-atlantique.fr");

        verify(repository, never()).delete(admin);
    }

    @Test
    void shouldReturnAllUsers() {
        when(repository.findAll()).thenReturn(List.of(
                new AppUser("u1@imt-atlantique.fr", "pwd", AppUser.UserType.company),
                new AppUser("u2@imt-atlantique.fr", "pwd", AppUser.UserType.applicant)
        ));

        List<AppUser> users = service.findAll();

        assertEquals(2, users.size());
    }
}
