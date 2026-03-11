package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;

public interface AppUserService {

    public Optional<AppUser> findByMail(String mail);
    public AppUser save(AppUser user);
    public AppUser saveWithDefaultProfile(AppUser user);
    public void deleteByMail(String mail);
    public List<AppUser> findAll();
}
