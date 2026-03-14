package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;

public interface AppUserService {

    Optional<AppUser> findByMail(String mail);

    AppUser save(AppUser user);

    void deleteByMail(String mail);

    List<AppUser> findAll();
}
