package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private final AppUserJpaRepository appUserRepository;

    public AppUserServiceImpl(AppUserJpaRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Optional<AppUser> findByMail(String mail) {
        return appUserRepository.findById(mail);
    }

    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    @Override
    public void deleteByMail(String mail) {
        appUserRepository.deleteById(mail);
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }
}
