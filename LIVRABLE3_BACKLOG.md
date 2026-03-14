# Backlog Livrable 3 - Demarrage implementation

Date: 2026-03-14
Etat: implementation en cours

## Statut actuel

- Sprint 1: termine (routes et pages publiques vitales + tests d integration publics).
- Sprint 2: termine (profil entreprise, publication d offre, gestion des offres, matching offres/candidatures + tests d integration).
- Sprint 3: termine (profil candidat, publication de candidature, gestion des candidatures, matching candidatures/offres + tests d integration).
- Sprint 4 a 6: a poursuivre.

## Sprint 1 - Fonctionnalites vitales publiques

- Creer CompanyController avec routes de consultation publique.
- Creer CandidateController avec routes de consultation publique.
- Creer JobOfferController avec routes de listing/detail/recherche publique.
- Creer ApplicationController avec routes de listing/detail/recherche publique.
- Ajouter les templates Thymeleaf associes aux pages publiques.
- Raccorder la navigation principale aux nouvelles routes.

## Sprint 2 - Fonctionnalites vitales entreprise

- Ajouter edition du profil entreprise connectee.
- Ajouter publication d offre.
- Ajouter listing des offres de l entreprise connectee.
- Ajouter listing des candidatures matchant une offre de l entreprise.
- Verifier controles d acces role + proprietaire.

## Sprint 3 - Fonctionnalites vitales candidat

- Ajouter edition du profil candidat connecte.
- Ajouter publication de candidature.
- Ajouter listing des candidatures du candidat connecte.
- Ajouter listing des offres matchant une candidature du candidat.
- Verifier controles d acces role + proprietaire.

## Sprint 4 - Mineures prioritaires

- Edition et suppression offre.
- Edition et suppression candidature.
- Messagerie manuelle entreprise vers candidat.
- Messagerie manuelle candidat vers entreprise.
- Historiques messages envoyes et recus des deux cotes.
- Verification visuelle des messages automatiques deja implantes au niveau service.

## Sprint 5 - Qualite et recette

- Ajouter tests d integration web des routes vitales.
- Ajouter tests des controles d acces (401/403) sur routes sensibles.
- Ajouter tests de non-regression sur flux critiques.
- Completer la fiche recette ligne par ligne avec O/N et commentaires.
- Preparer un script de demo 15 minutes.

## Sprint 6 - Packaging et rendu

- Executer mvn clean puis mvn test.
- Verifier execution de l application sur environnement propre.
- Verifier contenu final de l archive livrable 3.
- Produire archive de rendu au bon format et bon nom.
