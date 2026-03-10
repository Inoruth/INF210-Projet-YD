package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;

public interface CompanyService {

    List<Company> findAll();

    Optional<Company> findByMail(String mail);

    List<Company> searchByDenomination(String denominationPart);

    Company save(Company company);
}
