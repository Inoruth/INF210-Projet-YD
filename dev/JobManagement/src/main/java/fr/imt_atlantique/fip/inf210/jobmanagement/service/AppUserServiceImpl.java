package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserJpaRepository appUserRepository;

    @Autowired
    public AppUserServiceImpl(AppUserJpaRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Optional<AppUser> findByMail(String mail) {
        return appUserRepository.findByMail(mail);
    }

    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    @Override
    public void deleteByMail(String mail) {
        appUserRepository.findByMail(mail)
                .filter(user -> user.getUsertype() != AppUser.UserType.admin)
                .ifPresent(appUserRepository::delete);
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }
}
