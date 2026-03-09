package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String welcomePage() {
        return "index";
    }

}
