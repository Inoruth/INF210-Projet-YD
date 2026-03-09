package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.MediaType;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.AppUser;

import java.util.Map;
import java.util.Optional;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.AppUserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;




    

@Controller
public class AppUserController {
    
    @Autowired
    private AppUserService appUserService;


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
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate session
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/adduser")
    public String getAddUserForm(){
        return "userform.html";
    }
    
    @RequestMapping(path="/adduserdata",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processAddUserData(
            @RequestParam("mail") String mail,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("usertype") String usertype) {

        try {
            // Validate inputs
            if (mail == null || mail.trim().isEmpty()) {
                System.out.println("Error: Email is required");
                return "redirect:/adduser?error=email-required";
            }
            
            if (password == null || password.length() < 3) {
                System.out.println("Error: Password must be at least 3 characters");
                return "redirect:/adduser?error=password-short";
            }
            
            if (!password.equals(confirmPassword)) {
                System.out.println("Error: Passwords do not match");
                return "redirect:/adduser?error=passwords-mismatch";
            }
            
            if (usertype == null || (!usertype.equals("company") && !usertype.equals("applicant"))) {
                System.out.println("Error: Invalid user type");
                return "redirect:/adduser?error=invalid-usertype";
            }
            
            // Check if user already exists
            Optional<AppUser> existing = appUserService.findByMail(mail);
            if (existing.isPresent()) {
                System.out.println("Error: User with this email already exists");
                return "redirect:/adduser?error=email-exists";
            }
            
            // Create new user
            AppUser newUser = new AppUser(mail, password, AppUser.UserType.valueOf(usertype));
            
            // Save to database
            AppUser savedUser = appUserService.save(newUser);
            System.out.println("User saved successfully: " + savedUser);
            
            // Redirect to home page with success message
            return "redirect:/manageusers?success=user-created";
            
        } catch (Exception e) {
            System.out.println("Error processing user data: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/adduser?error=server-error";
        }
    }
    

    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam("mail") String mail, HttpSession session) {
        // Delete the user
        appUserService.deleteByMail(mail);
        return "redirect:/manageusers?success=user-deleted";
    }

    @GetMapping("/manageusers")
    public ModelAndView listUsersAndActions() {
        ModelAndView mav = new ModelAndView("manageusers");
        mav.addObject("users", appUserService.findAll());
        return mav;
    }
    
    @GetMapping("/modifyuser/{mail}")
    public ModelAndView getModifyUserForm(@PathVariable String mail) {
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
            @RequestParam("confirmPassword") String confirmPassword) {

        try {
            // Validate inputs
            if (password == null || password.length() < 3) {
                System.out.println("Error: Password must be at least 3 characters");
                return "redirect:/modifyuser/" + mail + "?error=password-short";
            }

            if (!password.equals(confirmPassword)) {
                System.out.println("Error: Passwords do not match");
                return "redirect:/modifyuser/" + mail + "?error=passwords-mismatch";
            }

            // Update existing user
            Optional<AppUser> existing = appUserService.findByMail(mail);
            if (existing.isPresent()) {
                AppUser updatedUser = existing.get();
                updatedUser.setPassword(password);
              
                // Save to database
                AppUser savedUser = appUserService.save(updatedUser);
                System.out.println("Updated user successfully: " + savedUser);

                // Redirect to manage users page with success message
                return "redirect:/manageusers?success=user-updated";
            } else {
                System.out.println("Error: User does not exist");
                return "redirect:/manageusers?error=user-not-found";
            }
            
        } catch (Exception e) {
            System.out.println("Error processing modified user data: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/manageusers?error=server-error";
        }
    }
    
}
