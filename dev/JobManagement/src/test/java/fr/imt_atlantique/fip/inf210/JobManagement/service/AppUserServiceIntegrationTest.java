package fr.imt_atlantique.fip.inf210.JobManagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;

@SpringBootTest
@Transactional
class AppUserServiceIntegrationTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Test
    void shouldCreateCompanyDefaultProfileWithServiceMethod() {
        AppUser companyUser = new AppUser(
                "company.default.profile@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.company
        );

        appUserService.saveWithDefaultProfile(companyUser);

        assertTrue(companyRepository.findByAppUserMail(companyUser.getMail()).isPresent());
        assertEquals(
                "company.default.profile",
                companyRepository.findByAppUserMail(companyUser.getMail()).get().getDenomination()
        );
    }

    @Test
    void shouldCreateApplicantDefaultProfileWithServiceMethod() {
        AppUser applicantUser = new AppUser(
                "applicant.default.profile@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        );

        appUserService.saveWithDefaultProfile(applicantUser);

        assertTrue(candidateRepository.findByAppUserMail(applicantUser.getMail()).isPresent());
        assertEquals(
                "applicant.default.profile",
                candidateRepository.findByAppUserMail(applicantUser.getMail()).get().getLastname()
        );
    }

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

    @Test
    void shouldDeleteCompanyUserWithProfile() {
        AppUser companyUser = appUserRepository.save(new AppUser(
                "company.integration@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.company
        ));
        companyRepository.save(new Company(companyUser, "Integration Company", null, null));

        appUserService.deleteByMail(companyUser.getMail());

        assertFalse(appUserRepository.findByMail(companyUser.getMail()).isPresent());
        assertTrue(companyRepository.findByAppUserMail(companyUser.getMail()).isEmpty());
    }

    @Test
    void shouldDeleteApplicantUserWithProfile() {
        AppUser candidateUser = appUserRepository.save(new AppUser(
                "candidate.integration@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));
        candidateRepository.save(new Candidate(candidateUser, "IntegrationCandidate", null, null));

        appUserService.deleteByMail(candidateUser.getMail());

        assertFalse(appUserRepository.findByMail(candidateUser.getMail()).isPresent());
        assertTrue(candidateRepository.findByAppUserMail(candidateUser.getMail()).isEmpty());
    }
}
