package fr.imt_atlantique.fip.inf210.JobManagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppUserControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Test
    void shouldReturnUnauthorizedForAnonymousManageUsers() throws Exception {
        mockMvc.perform(get("/manageusers"))
                .andExpect(status().isUnauthorized());
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

    private MockHttpSession buildSession(String mail, AppUser.UserType userType) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", mail);
        session.setAttribute("userType", userType.name());
        return session;
    }
}
