package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserServiceImpl;

class AppUserServiceTest {

    private final AppUserJpaRepository repository = mock(AppUserJpaRepository.class);
    private final CompanyJpaRepository companyRepository = mock(CompanyJpaRepository.class);
    private final CandidateJpaRepository candidateRepository = mock(CandidateJpaRepository.class);
    private final AppUserService service = new AppUserServiceImpl(repository, companyRepository, candidateRepository);

    @Test
    void shouldFindByMail() {
        AppUser user = new AppUser("svc.test@imt-atlantique.fr", "pwd", AppUser.UserType.admin);
        when(repository.findByMail("svc.test@imt-atlantique.fr")).thenReturn(Optional.of(user));

        Optional<AppUser> found = service.findByMail("svc.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("svc.test@imt-atlantique.fr", found.get().getMail());
    }

    @Test
    void shouldReturnEmptyWhenMailIsUnknown() {
        when(repository.findByMail("unknown.service@imt-atlantique.fr")).thenReturn(Optional.empty());

        Optional<AppUser> found = service.findByMail("unknown.service@imt-atlantique.fr");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldSaveUser() {
        AppUser user = new AppUser("save.service@imt-atlantique.fr", "pwd", AppUser.UserType.company);
        when(repository.save(user)).thenReturn(user);

        AppUser saved = service.save(user);

        assertEquals(user, saved);
        verify(repository).save(user);
    }

    @Test
    void shouldDeleteByMailWhenUserExists() {
        AppUser user = new AppUser("delete.test@imt-atlantique.fr", "pwd", AppUser.UserType.applicant);
        when(repository.findByMail("delete.test@imt-atlantique.fr")).thenReturn(Optional.of(user));
        when(candidateRepository.findByAppUserMail("delete.test@imt-atlantique.fr")).thenReturn(Optional.empty());

        service.deleteByMail("delete.test@imt-atlantique.fr");

        verify(candidateRepository, times(1)).findByAppUserMail("delete.test@imt-atlantique.fr");
        verify(repository, times(1)).delete(user);
    }

    @Test
    void shouldDeleteCompanyProfileBeforeUser() {
        AppUser user = new AppUser("company.delete@imt-atlantique.fr", "pwd", AppUser.UserType.company);
        Company company = new Company();
        company.setAppUser(user);

        when(repository.findByMail("company.delete@imt-atlantique.fr")).thenReturn(Optional.of(user));
        when(companyRepository.findByAppUserMail("company.delete@imt-atlantique.fr")).thenReturn(Optional.of(company));

        service.deleteByMail("company.delete@imt-atlantique.fr");

        InOrder inOrder = inOrder(companyRepository, repository);
        inOrder.verify(companyRepository).findByAppUserMail("company.delete@imt-atlantique.fr");
        inOrder.verify(companyRepository).delete(company);
        inOrder.verify(repository).delete(user);
    }

    @Test
    void shouldDeleteApplicantProfileBeforeUser() {
        AppUser user = new AppUser("candidate.delete@imt-atlantique.fr", "pwd", AppUser.UserType.applicant);
        Candidate candidate = new Candidate();
        candidate.setAppUser(user);

        when(repository.findByMail("candidate.delete@imt-atlantique.fr")).thenReturn(Optional.of(user));
        when(candidateRepository.findByAppUserMail("candidate.delete@imt-atlantique.fr")).thenReturn(Optional.of(candidate));

        service.deleteByMail("candidate.delete@imt-atlantique.fr");

        InOrder inOrder = inOrder(candidateRepository, repository);
        inOrder.verify(candidateRepository).findByAppUserMail("candidate.delete@imt-atlantique.fr");
        inOrder.verify(candidateRepository).delete(candidate);
        inOrder.verify(repository).delete(user);
    }

    @Test
    void shouldDeleteCompanyUserWhenNoCompanyProfileExists() {
        AppUser user = new AppUser("company.noprofile@imt-atlantique.fr", "pwd", AppUser.UserType.company);
        when(repository.findByMail("company.noprofile@imt-atlantique.fr")).thenReturn(Optional.of(user));
        when(companyRepository.findByAppUserMail("company.noprofile@imt-atlantique.fr")).thenReturn(Optional.empty());

        service.deleteByMail("company.noprofile@imt-atlantique.fr");

        verify(companyRepository).findByAppUserMail("company.noprofile@imt-atlantique.fr");
        verify(repository).delete(user);
    }

    @Test
    void shouldNotDeleteAdminUser() {
        AppUser admin = new AppUser("admin.test@imt-atlantique.fr", "pwd", AppUser.UserType.admin);
        when(repository.findByMail("admin.test@imt-atlantique.fr")).thenReturn(Optional.of(admin));

        service.deleteByMail("admin.test@imt-atlantique.fr");

        verify(repository, never()).delete(admin);
        verifyNoInteractions(companyRepository, candidateRepository);
    }

    @Test
    void shouldDoNothingWhenDeletingUnknownUser() {
        when(repository.findByMail("unknown@imt-atlantique.fr")).thenReturn(Optional.empty());

        service.deleteByMail("unknown@imt-atlantique.fr");

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any(AppUser.class));
        verifyNoInteractions(companyRepository, candidateRepository);
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
