package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Admin;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AdminJpaRepository;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminJpaRepository adminRepository;

    public AdminServiceImpl(AdminJpaRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Optional<Admin> findByMail(String mail) {
        return adminRepository.findByAppUserMail(mail);
    }

    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}
