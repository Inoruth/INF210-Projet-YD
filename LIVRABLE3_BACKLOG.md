# Backlog Livrable 3 - Suivi implementation

Date: 2026-03-14
Etat: sprint 1 a 4 termines, sprint 5 et 6 en finalisation

## Statut actuel

- Sprint 1: termine (routes et pages publiques vitales + tests integration publics).
- Sprint 2: termine (profil entreprise, publication offre, gestion des offres, matching offres/candidatures + tests integration).
- Sprint 3: termine (profil candidat, publication candidature, gestion candidatures, matching candidatures/offres + tests integration).
- Sprint 4: termine (edition/suppression offre et candidature, messagerie manuelle bidirectionnelle, historiques messages cote entreprise et candidat).
- Sprint 5: en cours de cloture.
	- tests integration web des routes vitales: OK.
	- tests controles acces 401/403 routes sensibles: OK.
	- tests de non-regression flux critiques (create/update/delete/match/message): OK.
	- fiche recette ligne par ligne: brouillon markdown ajoute (reste la recopie finale dans le support officiel PDF).
	- script de demo 15 minutes: prepare.
- Sprint 6: en cours de cloture.
	- `mvn clean`: OK (BUILD SUCCESS).
	- `mvn test`: OK (BUILD SUCCESS, 168 tests, 0 failure, 0 error).
	- `mvn package -DskipTests`: OK (jar executable genere).
	- verification execution sur environnement propre: partiellement bloquee sur cette machine (Docker absent).
	- verification archive finale et nommage: a faire juste avant rendu.

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

## Reste a faire avant soumission finale

- Executer un run complet `docker compose up -d` + `mvn spring-boot:run` sur une machine avec Docker.
- Reporter les validations O/N/commentaires dans la fiche recette officielle PDF.
- Generer l archive finale livrable 3 avec verification manuelle du contenu.
