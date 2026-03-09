package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

import fr.imt_atlantique.fip.inf210.jobmanagement.repository.AppUserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private AppUserJpaRepository appUserRepository;

    @GetMapping("/")
    public String welcomePage() {
        return "index";
    }

}
