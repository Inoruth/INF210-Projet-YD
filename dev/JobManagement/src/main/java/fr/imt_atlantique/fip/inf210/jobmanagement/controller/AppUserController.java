package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: AppUserController
 * Cette classe centralise les endpoints HTTP du module.
 * Elle lit les donnees de la requete, valide les entrees et controle les droits d'acces.
 * Elle construit la reponse (JSON, vue Thymeleaf ou redirection) selon le contexte.
 * La logique metier est deleguee aux services et repositories.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Candidate;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Company;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CandidateJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.CompanyJpaRepository;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;
import jakarta.servlet.http.HttpSession;




    

@Controller
public class AppUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserController.class);
    
    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CompanyJpaRepository companyRepository;

    @Autowired
    private CandidateJpaRepository candidateRepository;


    // Cette methode implemente l operation login.
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        String mail = credentials.get("mail");
        String password = credentials.get("password");
        
        // Validate input
        if (mail == null || mail.isEmpty() || password == null || password.isEmpty()) {
            response.put("success", false);
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Find user by email
        Optional<AppUser> userOptional = appUserService.findByMail(mail);
        
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            
            // Check password (note: in production, use encrypted passwords!)
            if (user.getPassword().equals(password)) {
                // Store user in session
                session.setAttribute("loggedInUser", user);
                session.setAttribute("userMail", user.getMail());
                session.setAttribute("userType", user.getUsertype().toString());
                
                response.put("success", true);
                response.put("mail", user.getMail());
                response.put("usertype", user.getUsertype().toString());
                return ResponseEntity.ok(response);
            }
        }
        
        // Authentication failed
        response.put("success", false);
        response.put("message", "Invalid email or password");
        return ResponseEntity.ok(response);
    }
    
    // Cette methode implemente l operation logout.
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate session
        session.invalidate();
        return "redirect:/";
    }

    // Cette methode implemente l operation getAddUserForm.
    @GetMapping("/adduser")
    public String getAddUserForm(HttpSession session){
        requireAdmin(session);
        return "userform.html";
    }
    
    @RequestMapping(path="/adduserdata",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processAddUserData(
            @RequestParam("mail") String mail,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("usertype") String usertype,
            HttpSession session) {

        requireAdmin(session);

        try {
            // Validate inputs
            if (mail == null || mail.trim().isEmpty()) {
                LOGGER.warn("User creation failed: email is required");
                return "redirect:/adduser?error=email-required";
            }
            
            if (password == null || password.length() < 4) {
                LOGGER.warn("User creation failed: password too short for mail={}", mail);
                return "redirect:/adduser?error=password-short";
            }
            
            if (!password.equals(confirmPassword)) {
                LOGGER.warn("User creation failed: password mismatch for mail={}", mail);
                return "redirect:/adduser?error=passwords-mismatch";
            }
            
            if (usertype == null || (!usertype.equals("company") && !usertype.equals("applicant"))) {
                LOGGER.warn("User creation failed: invalid usertype={} for mail={}", usertype, mail);
                return "redirect:/adduser?error=invalid-usertype";
            }
            
            // Check if user already exists
            Optional<AppUser> existing = appUserService.findByMail(mail);
            if (existing.isPresent()) {
                LOGGER.warn("User creation failed: email already exists mail={}", mail);
                return "redirect:/adduser?error=email-exists";
            }
            
            // Create new user
            AppUser newUser = new AppUser(mail, password, AppUser.UserType.valueOf(usertype));
            
            // Save to database
            AppUser savedUser = appUserService.save(newUser);

            if (savedUser.getUsertype() == AppUser.UserType.company) {
                companyRepository.save(new Company(
                        savedUser,
                        deriveDefaultProfileName(savedUser.getMail(), 100),
                        null,
                        null
                ));
            } else if (savedUser.getUsertype() == AppUser.UserType.applicant) {
                candidateRepository.save(new Candidate(
                        savedUser,
                        deriveDefaultProfileName(savedUser.getMail(), 50),
                        null,
                        null
                ));
            }

            LOGGER.info("User created successfully mail={} type={}", savedUser.getMail(), savedUser.getUsertype());
            
            // Redirect to home page with success message
            return "redirect:/manageusers?success=user-created";
            
        } catch (Exception e) {
            LOGGER.error("Error processing user data for mail={}: {}", mail, e.getMessage(), e);
            return "redirect:/adduser?error=server-error";
        }
    }
    

    // Cette methode implemente l operation deleteUser.
    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam("mail") String mail, HttpSession session) {
        requireAdmin(session);

        // Delete the user
        appUserService.deleteByMail(mail);
        return "redirect:/manageusers?success=user-deleted";
    }

    // Cette methode implemente l operation listUsersAndActions.
    @GetMapping("/manageusers")
    public ModelAndView listUsersAndActions(HttpSession session) {
        requireAdmin(session);

        ModelAndView mav = new ModelAndView("manageusers");
        mav.addObject("users", appUserService.findAll());
        return mav;
    }
    
    // Cette methode implemente l operation getModifyUserForm.
    @GetMapping("/modifyuser/{mail}")
    public ModelAndView getModifyUserForm(@PathVariable String mail, HttpSession session) {
        requireSelfOrAdmin(session, mail);

        ModelAndView model = new ModelAndView("modifyuser");    
        Optional<AppUser> userOptional = appUserService.findByMail(mail);
        if (userOptional.isPresent()) {
            model.addObject("user", userOptional.get());
        } else {
            model.addObject("error", "User not found");
            model.addObject("redirect", "/manageusers");
        }
        return model;
    }

    @PostMapping("/modifyuserdata")
    public String processModifyUserData(
            @RequestParam("mail") String mail,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session) {

        requireSelfOrAdmin(session, mail);

        try {
            // Validate inputs
            if (password == null || password.length() < 4) {
                LOGGER.warn("User update failed: password too short for mail={}", mail);
                return "redirect:/modifyuser/" + mail + "?error=password-short";
            }

            if (!password.equals(confirmPassword)) {
                LOGGER.warn("User update failed: password mismatch for mail={}", mail);
                return "redirect:/modifyuser/" + mail + "?error=passwords-mismatch";
            }

            // Update existing user
            Optional<AppUser> existing = appUserService.findByMail(mail);
            if (existing.isPresent()) {
                AppUser updatedUser = existing.get();
                updatedUser.setPassword(password);
              
                // Save to database
                AppUser savedUser = appUserService.save(updatedUser);
                LOGGER.info("User updated successfully mail={}", savedUser.getMail());

                // Redirect to manage users page with success message
                return "redirect:/manageusers?success=user-updated";
            } else {
                LOGGER.warn("User update failed: user not found mail={}", mail);
                return "redirect:/manageusers?error=user-not-found";
            }
            
        } catch (Exception e) {
            LOGGER.error("Error processing modified user data for mail={}: {}", mail, e.getMessage(), e);
            return "redirect:/manageusers?error=server-error";
        }
    }

    // Cette methode implemente l operation deriveDefaultProfileName.
    private String deriveDefaultProfileName(String mail, int maxLength) {
        if (mail == null || mail.isBlank()) {
            return "unknown";
        }

        int atIndex = mail.indexOf('@');
        String baseName = mail;
        if (atIndex > 0) {
            baseName = mail.substring(0, atIndex);
        }

        if (baseName.length() > maxLength) {
            return baseName.substring(0, maxLength);
        }

        return baseName;
    }

    // Cette methode implemente l operation requireAdmin.
    private void requireAdmin(HttpSession session) {
        requireAuthenticated(session);

        String userType = (String) session.getAttribute("userType");
        if (!AppUser.UserType.admin.name().equalsIgnoreCase(userType)) {
            // Cette methode implemente l operation ResponseStatusException.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    // Cette methode implemente l operation requireSelfOrAdmin.
    private void requireSelfOrAdmin(HttpSession session, String targetMail) {
        requireAuthenticated(session);

        String sessionMail = (String) session.getAttribute("userMail");
        String userType = (String) session.getAttribute("userType");
        boolean isAdmin = AppUser.UserType.admin.name().equalsIgnoreCase(userType);

        if (!isAdmin && !sessionMail.equalsIgnoreCase(targetMail)) {
            // Cette methode implemente l operation ResponseStatusException.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot modify another user");
        }
    }

    // Cette methode implemente l operation requireAuthenticated.
    private void requireAuthenticated(HttpSession session) {
        if (session == null) {
            // Cette methode implemente l operation ResponseStatusException.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Object sessionMail = session.getAttribute("userMail");
        Object userType = session.getAttribute("userType");
        if (!(sessionMail instanceof String mail) || mail.isBlank()
                || !(userType instanceof String type) || type.isBlank()) {
            // Cette methode implemente l operation ResponseStatusException.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }
    
}
