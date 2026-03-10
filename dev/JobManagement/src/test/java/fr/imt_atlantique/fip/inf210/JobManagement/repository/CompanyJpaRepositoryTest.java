package fr.imt_atlantique.fip.inf210.JobManagement.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@SpringBootTest
@Transactional
class CompanyJpaRepositoryTest {

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Test
    void shouldFindCompanyByAppUserMail() {
        AppUser user = appUserRepository.save(new AppUser(
                "company.repo.test@imt-atlantique.fr",
                "pwd123",
                AppUser.UserType.company
        ));
        companyRepository.save(new Company(user, "Acme Testing", "QA company", "Brest"));

        Optional<Company> found = companyRepository.findByAppUserMail("company.repo.test@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("Acme Testing", found.get().getDenomination());
    }

    @Test
    void shouldSearchCompaniesByDenomination() {
        createCompany("company.one@imt-atlantique.fr", "Acme Consulting", "Nantes");
        createCompany("company.two@imt-atlantique.fr", "Acme Labs", "Rennes");
        createCompany("company.three@imt-atlantique.fr", "Zenith Group", "Paris");

        List<Company> companies = companyRepository
                .findByDenominationContainingIgnoreCaseOrderByDenominationAsc("acme");

        assertEquals(2, companies.size());
        assertEquals("Acme Consulting", companies.get(0).getDenomination());
        assertEquals("Acme Labs", companies.get(1).getDenomination());
    }

    private void createCompany(String mail, String denomination, String city) {
        AppUser user = appUserRepository.save(new AppUser(mail, "pwd123", AppUser.UserType.company));
        companyRepository.save(new Company(user, denomination, "description", city));
    }
}
