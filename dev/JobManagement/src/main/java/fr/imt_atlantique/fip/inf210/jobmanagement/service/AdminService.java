package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;

public interface AdminService {

    Optional<Admin> findByMail(String mail);

    Admin save(Admin admin);
}
