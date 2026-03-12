package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

/*
 * Fichier: MainController
 * Cette classe centralise les endpoints HTTP du module.
 * Elle lit les donnees de la requete, valide les entrees et controle les droits d'acces.
 * Elle construit la reponse (JSON, vue Thymeleaf ou redirection) selon le contexte.
 * La logique metier est deleguee aux services et repositories.
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Cette methode implemente l operation welcomePage.
    @GetMapping("/")
    public String welcomePage() {
        return "index";
    }

}
