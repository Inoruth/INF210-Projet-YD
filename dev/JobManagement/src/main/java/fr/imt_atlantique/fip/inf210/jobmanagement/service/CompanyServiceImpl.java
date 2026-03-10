package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyJpaRepository companyRepository;

    public CompanyServiceImpl(CompanyJpaRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Optional<Company> findByMail(String mail) {
        return companyRepository.findByAppUserMail(mail);
    }

    @Override
    public List<Company> searchByDenomination(String denominationPart) {
        return companyRepository.findByDenominationContainingIgnoreCaseOrderByDenominationAsc(denominationPart);
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
