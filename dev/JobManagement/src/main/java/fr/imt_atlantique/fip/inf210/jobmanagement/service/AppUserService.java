package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import java.util.List;
import java.util.Optional;

public interface AppUserService {

    public Optional<AppUser> findByMail(String mail);
    public AppUser save(AppUser user);
    public void deleteByMail(String mail);
    public List<AppUser> findAll();
}
