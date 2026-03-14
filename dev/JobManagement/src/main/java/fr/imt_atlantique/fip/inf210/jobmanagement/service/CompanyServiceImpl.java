package fr.imt_atlantique.fip.inf210.jobmanagement.service;

/*
 * Fichier: CompanyServiceImpl
 * Cette classe implemente les operations metier du service.
 * Elle orchestre les repositories pour appliquer les regles fonctionnelles du domaine.
 * Elle traite les cas limites (donnees absentes, contraintes metier, dependances).
 * Les controllers s'appuient sur cette implementation pour executer les actions metier.
 */

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyJpaRepository companyRepository;

    // Cette methode implemente l operation CompanyServiceImpl.
    public CompanyServiceImpl(CompanyJpaRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Cette methode implemente l operation findAll.
    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    // Cette methode implemente l operation findByMail.
    @Override
    public Optional<Company> findByMail(String mail) {
        return companyRepository.findByAppUserMail(mail);
    }

    // Cette methode implemente l operation searchByDenomination.
    @Override
    public List<Company> searchByDenomination(String denominationPart) {
        return companyRepository.findByDenominationContainingIgnoreCaseOrderByDenominationAsc(denominationPart);
    }

    // Cette methode implemente l operation save.
    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
