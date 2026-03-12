package fr.imt_atlantique.fip.inf210.JobManagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CompanyService;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.CompanyServiceImpl;

class CompanyServiceTest {

    private final CompanyJpaRepository repository = mock(CompanyJpaRepository.class);
    private final CompanyService service = new CompanyServiceImpl(repository);

    @Test
    void shouldFindByMail() {
        Company company = new Company();
        company.setDenomination("Acme");
        when(repository.findByAppUserMail("company.service@imt-atlantique.fr")).thenReturn(Optional.of(company));

        Optional<Company> found = service.findByMail("company.service@imt-atlantique.fr");

        assertTrue(found.isPresent());
        assertEquals("Acme", found.get().getDenomination());
        verify(repository).findByAppUserMail("company.service@imt-atlantique.fr");
    }

    @Test
    void shouldReturnEmptyWhenCompanyMailUnknown() {
        when(repository.findByAppUserMail("unknown.company@imt-atlantique.fr")).thenReturn(Optional.empty());

        Optional<Company> found = service.findByMail("unknown.company@imt-atlantique.fr");

        assertFalse(found.isPresent());
        verify(repository).findByAppUserMail("unknown.company@imt-atlantique.fr");
    }

    @Test
    void shouldSearchByDenomination() {
        when(repository.findByDenominationContainingIgnoreCaseOrderByDenominationAsc("ac"))
                .thenReturn(List.of(new Company(), new Company()));

        List<Company> companies = service.searchByDenomination("ac");

        assertEquals(2, companies.size());
        verify(repository).findByDenominationContainingIgnoreCaseOrderByDenominationAsc("ac");
    }

    @Test
    void shouldReturnNoCompanyWhenDenominationSearchHasNoMatch() {
        when(repository.findByDenominationContainingIgnoreCaseOrderByDenominationAsc("zzz"))
                .thenReturn(List.of());

        List<Company> companies = service.searchByDenomination("zzz");

        assertEquals(0, companies.size());
        verify(repository).findByDenominationContainingIgnoreCaseOrderByDenominationAsc("zzz");
    }

    @Test
    void shouldFindAllCompanies() {
        when(repository.findAll()).thenReturn(List.of(new Company(), new Company(), new Company()));

        List<Company> companies = service.findAll();

        assertEquals(3, companies.size());
        verify(repository).findAll();
    }

    @Test
    void shouldSaveCompany() {
        Company company = new Company();
        company.setDenomination("Saved Co");
        when(repository.save(company)).thenReturn(company);

        Company saved = service.save(company);

        assertEquals(company, saved);
        verify(repository).save(company);
    }
}
