package fr.imt_atlantique.fip.inf210.JobManagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppUserControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;

    @Test
    void shouldReturnUnauthorizedForAnonymousManageUsers() throws Exception {
        mockMvc.perform(get("/manageusers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenSessionLacksUserType() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "admin@imt-atlantique.fr");

        mockMvc.perform(get("/manageusers").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenSessionLacksUserMail() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userType", AppUser.UserType.admin.name());

        mockMvc.perform(get("/manageusers").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForAnonymousAddUserForm() throws Exception {
        mockMvc.perform(get("/adduser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenForNonAdminAddUserForm() throws Exception {
        mockMvc.perform(get("/adduser")
                        .session(buildSession("user.company@imt-atlantique.fr", AppUser.UserType.company)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedForAnonymousAddUserData() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mail", "new.user@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "company"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenForNonAdminAddUserData() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("user.company@imt-atlantique.fr", AppUser.UserType.company))
                        .param("mail", "new.user@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "company"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToCreateCompanyUser() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "new.company.user@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "company"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageusers?success=user-created"));

        AppUser created = appUserRepository.findByMail("new.company.user@imt-atlantique.fr").orElseThrow();
        assertEquals(AppUser.UserType.company, created.getUsertype());
        assertTrue(companyRepository.findByAppUserMail(created.getMail()).isPresent());
    }

    @Test
    void shouldAllowAdminToCreateApplicantUser() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "new.applicant.user@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "applicant"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageusers?success=user-created"));

        AppUser created = appUserRepository.findByMail("new.applicant.user@imt-atlantique.fr").orElseThrow();
        assertEquals(AppUser.UserType.applicant, created.getUsertype());
        assertTrue(candidateRepository.findByAppUserMail(created.getMail()).isPresent());
    }

    @Test
    void shouldRejectAdminAddUserWhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "short.password@imt-atlantique.fr")
                        .param("password", "abc")
                        .param("confirmPassword", "abc")
                        .param("usertype", "company"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adduser?error=password-short"));
    }

    @Test
    void shouldRejectAdminAddUserWhenPasswordMismatch() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "mismatch.password@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd5678")
                        .param("usertype", "company"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adduser?error=passwords-mismatch"));
    }

    @Test
    void shouldRejectAdminAddUserWhenTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "invalid.type@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adduser?error=invalid-usertype"));
    }

    @Test
    void shouldRejectAdminAddUserWhenMailAlreadyExists() throws Exception {
        appUserRepository.save(new AppUser("already.exists@imt-atlantique.fr", "pwd1234", AppUser.UserType.company));

        mockMvc.perform(post("/adduserdata")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", "already.exists@imt-atlantique.fr")
                        .param("password", "pwd1234")
                        .param("confirmPassword", "pwd1234")
                        .param("usertype", "company"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adduser?error=email-exists"));
    }

    @Test
    void shouldReturnForbiddenForNonAdminManageUsers() throws Exception {
        mockMvc.perform(get("/manageusers")
                        .session(buildSession("user.company@imt-atlantique.fr", AppUser.UserType.company)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnOkForAdminManageUsers() throws Exception {
        mockMvc.perform(get("/manageusers")
                        .session(buildSession("user.admin@imt-atlantique.fr", AppUser.UserType.admin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedForAnonymousDeleteUser() throws Exception {
        mockMvc.perform(post("/deleteuser").param("mail", "target@imt-atlantique.fr"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenForNonAdminDeleteUser() throws Exception {
        mockMvc.perform(post("/deleteuser")
                        .session(buildSession("user.company@imt-atlantique.fr", AppUser.UserType.company))
                        .param("mail", "target@imt-atlantique.fr"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToModifyOwnProfileForm() throws Exception {
        AppUser user = appUserRepository.save(new AppUser(
                "owner.modify@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        mockMvc.perform(get("/modifyuser/{mail}", user.getMail())
                        .session(buildSession(user.getMail(), user.getUsertype())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyUserFromModifyingAnotherProfileForm() throws Exception {
        AppUser owner = appUserRepository.save(new AppUser(
                "owner.two@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));
        AppUser other = appUserRepository.save(new AppUser(
                "other.user@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        mockMvc.perform(get("/modifyuser/{mail}", other.getMail())
                        .session(buildSession(owner.getMail(), owner.getUsertype())))
                .andExpect(status().isForbidden());
    }

        @Test
        void shouldReturnUnauthorizedForAnonymousModifyUserForm() throws Exception {
                mockMvc.perform(get("/modifyuser/{mail}", "any.user@imt-atlantique.fr"))
                                .andExpect(status().isUnauthorized());
        }

    @Test
    void shouldReturnUnauthorizedForAnonymousModifyUserData() throws Exception {
        mockMvc.perform(post("/modifyuserdata")
                        .param("mail", "target@imt-atlantique.fr")
                        .param("password", "newpwd")
                        .param("confirmPassword", "newpwd"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyUserFromModifyingAnotherProfileData() throws Exception {
        AppUser owner = appUserRepository.save(new AppUser(
                "owner.data@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));
        AppUser other = appUserRepository.save(new AppUser(
                "other.data@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        mockMvc.perform(post("/modifyuserdata")
                        .session(buildSession(owner.getMail(), owner.getUsertype()))
                        .param("mail", other.getMail())
                        .param("password", "newpwd")
                        .param("confirmPassword", "newpwd"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToModifyOwnProfileData() throws Exception {
        AppUser user = appUserRepository.save(new AppUser(
                "owner.update@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        mockMvc.perform(post("/modifyuserdata")
                        .session(buildSession(user.getMail(), user.getUsertype()))
                        .param("mail", user.getMail())
                        .param("password", "newpwd")
                        .param("confirmPassword", "newpwd"))
                .andExpect(status().is3xxRedirection());

        AppUser reloaded = appUserRepository.findByMail(user.getMail()).orElseThrow();
        assertEquals("newpwd", reloaded.getPassword());
    }

    @Test
    void shouldAllowAdminToModifyAnotherProfileData() throws Exception {
        AppUser other = appUserRepository.save(new AppUser(
                "target.update@imt-atlantique.fr",
                "pwd1234",
                AppUser.UserType.applicant
        ));

        mockMvc.perform(post("/modifyuserdata")
                        .session(buildSession("admin.user@imt-atlantique.fr", AppUser.UserType.admin))
                        .param("mail", other.getMail())
                        .param("password", "adminset")
                        .param("confirmPassword", "adminset"))
                .andExpect(status().is3xxRedirection());

        AppUser reloaded = appUserRepository.findByMail(other.getMail()).orElseThrow();
        assertEquals("adminset", reloaded.getPassword());
    }

        @Test
        void shouldRedirectWhenModifyUserDataPasswordIsTooShort() throws Exception {
                AppUser user = appUserRepository.save(new AppUser(
                                "short.update@imt-atlantique.fr",
                                "pwd1234",
                                AppUser.UserType.applicant
                ));

                mockMvc.perform(post("/modifyuserdata")
                                                .session(buildSession(user.getMail(), user.getUsertype()))
                                                .param("mail", user.getMail())
                                                .param("password", "abc")
                                                .param("confirmPassword", "abc"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/modifyuser/" + user.getMail() + "?error=password-short"));
        }

        @Test
        void shouldRedirectWhenModifyUserDataPasswordMismatch() throws Exception {
                AppUser user = appUserRepository.save(new AppUser(
                                "mismatch.update@imt-atlantique.fr",
                                "pwd1234",
                                AppUser.UserType.applicant
                ));

                mockMvc.perform(post("/modifyuserdata")
                                                .session(buildSession(user.getMail(), user.getUsertype()))
                                                .param("mail", user.getMail())
                                                .param("password", "newpwd")
                                                .param("confirmPassword", "newpwd2"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/modifyuser/" + user.getMail() + "?error=passwords-mismatch"));
        }

    private MockHttpSession buildSession(String mail, AppUser.UserType userType) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", mail);
        session.setAttribute("userType", userType.name());
        return session;
    }
}
