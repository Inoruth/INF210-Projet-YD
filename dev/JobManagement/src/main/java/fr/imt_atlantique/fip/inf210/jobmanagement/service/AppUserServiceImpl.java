package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: AppUserServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@Service
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

    // Cette methode implemente l operation findByMail.
    @Override
    public Optional<AppUser> findByMail(String mail) {
        return appUserRepository.findByMail(mail);
    }

    // Cette methode implemente l operation save.
    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    // Cette methode implemente l operation deleteByMail.
    @Override
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

    // Cette methode implemente l operation findAll.
    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }
}
