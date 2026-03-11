package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@Service
@Transactional(readOnly = true)
public class AppUserServiceImpl implements AppUserService {

    private final AppUserJpaRepository appUserRepository;
    private final CompanyJpaRepository companyRepository;
    private final CandidateJpaRepository candidateRepository;

    @Autowired
    public AppUserServiceImpl(
            AppUserJpaRepository appUserRepository,
            CompanyJpaRepository companyRepository,
            CandidateJpaRepository candidateRepository) {
        this.appUserRepository = appUserRepository;
        this.companyRepository = companyRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Optional<AppUser> findByMail(String mail) {
        return appUserRepository.findByMail(mail);
    }

    @Override
    @Transactional
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    @Override
    @Transactional
    public AppUser saveWithDefaultProfile(AppUser user) {
        AppUser savedUser = appUserRepository.save(user);

        if (savedUser.getUsertype() == AppUser.UserType.company) {
            companyRepository.save(new Company(
                    savedUser,
                    deriveDefaultProfileName(savedUser.getMail(), 100),
                    null,
                    null
            ));
        } else if (savedUser.getUsertype() == AppUser.UserType.applicant) {
            candidateRepository.save(new Candidate(
                    savedUser,
                    deriveDefaultProfileName(savedUser.getMail(), 50),
                    null,
                    null
            ));
        }

        return savedUser;
    }

    @Override
    @Transactional
    public void deleteByMail(String mail) {
        appUserRepository.findByMail(mail)
                .ifPresent(user -> {
                    if (user.getUsertype() == AppUser.UserType.admin) {
                        return;
                    }

                    if (user.getUsertype() == AppUser.UserType.company) {
                        companyRepository.findByAppUserMail(mail).ifPresent(companyRepository::delete);
                    } else if (user.getUsertype() == AppUser.UserType.applicant) {
                        candidateRepository.findByAppUserMail(mail).ifPresent(candidateRepository::delete);
                    }

                    appUserRepository.delete(user);
                });
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    private String deriveDefaultProfileName(String mail, int maxLength) {
        if (mail == null || mail.isBlank()) {
            return "unknown";
        }

        int atIndex = mail.indexOf('@');
        String baseName = mail;
        if (atIndex > 0) {
            baseName = mail.substring(0, atIndex);
        }

        if (baseName.length() > maxLength) {
            return baseName.substring(0, maxLength);
        }

        return baseName;
    }
}
