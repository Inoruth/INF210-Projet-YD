package fr.imt_atlantique.fip.inf210.JobManagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppUserControllerAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @Test
    void shouldReturnBadRequestWhenLoginMailMissing() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"pwd1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email and password are required"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginPasswordMissing() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"user@imt-atlantique.fr\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email and password are required"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginPayloadIsEmpty() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email and password are required"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginMailIsBlank() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"\",\"password\":\"pwd1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturnFailureWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"unknown@imt-atlantique.fr\",\"password\":\"pwd1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldReturnFailureWhenPasswordIsWrong() throws Exception {
        appUserRepository.save(new AppUser("known@imt-atlantique.fr", "rightpwd", AppUser.UserType.applicant));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"known@imt-atlantique.fr\",\"password\":\"wrongpwd\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldLoginAndStoreSessionAttributes() throws Exception {
        appUserRepository.save(new AppUser("valid@imt-atlantique.fr", "pwd1234", AppUser.UserType.company));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"valid@imt-atlantique.fr\",\"password\":\"pwd1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.mail").value("valid@imt-atlantique.fr"))
                .andExpect(jsonPath("$.usertype").value("company"))
                .andExpect(request().sessionAttribute("userMail", "valid@imt-atlantique.fr"))
                .andExpect(request().sessionAttribute("userType", "company"));
    }

    @Test
    void shouldLoginApplicantAndExposeApplicantUserType() throws Exception {
        appUserRepository.save(new AppUser("applicant.valid@imt-atlantique.fr", "pwd1234", AppUser.UserType.applicant));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mail\":\"applicant.valid@imt-atlantique.fr\",\"password\":\"pwd1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.usertype").value("applicant"))
                .andExpect(request().sessionAttribute("userType", "applicant"));
    }

    @Test
    void shouldLogoutAndRedirectToHome() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "logout@imt-atlantique.fr");
        session.setAttribute("userType", "applicant");

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
