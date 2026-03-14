# Plan Livrable 3 - Execution et Qualite

Date: 2026-03-14
Branche cible: partie2

## Sources de reference

- Cahier des charges
- Partie 3 - services metier et couche presentation
- Partie 3 - organisation de l interface graphique
- Recette_application
- Evaluation du projet et livrables

## Objectif final

Terminer la connexion couche presentation <-> couche metier, couvrir toutes les fonctionnalites vitales, maximiser les mineures, livrer une application demontrable en 15 minutes avec une fiche recette propre et une archive executable sans modification.

## Etat de depart constate

- Fonctionnalites web deja presentes: authentification, gestion admin des utilisateurs, listing secteurs, listing niveaux de qualification.
- Couche metier deja riche: services pour entreprises, candidats, offres, candidatures, matching, messages et envoi automatique.
- Couche presentation encore partielle: plusieurs liens/menu existent mais les routes et pages associees ne sont pas encore implementees.

## Matrice de conformite fonctionnelle (CDC + Recette)

| ID | Fonctionnalite | Importance | Acces | Etat actuel | Ce qu il faut faire pour Livrable 3 | Validation recette |
|---|---|---|---|---|---|---|
| F01 | Se connecter | Fourni | Public | OK | Verifier robustesse messages erreur et session | Login valide/invalide |
| F02 | Se deconnecter | Fourni | Connecte | OK | Verifier retour etat non connecte | Logout + menu public |
| F03 | Lister entreprises | Vitale | Public | A faire | Ajouter route controller + vue liste + donnees attendues | Tableau complet visible |
| F04 | Afficher details entreprise | Vitale | Public | A faire | Ajouter page detail entreprise avec offres publiees | Detail entreprise accessible |
| F05 | Lister candidats | Vitale | Public | A faire | Ajouter route + vue liste candidats | Tableau complet visible |
| F06 | Afficher details candidature | Vitale | Public | A faire | Ajouter page detail candidature complete | Donnees candidature completes |
| F07 | Lister toutes les offres | Vitale | Public | A faire | Ajouter route + vue listing offres | Liste conforme CDC |
| F08 | Lister toutes les candidatures | Vitale | Public | A faire | Ajouter route + vue listing candidatures | Liste conforme CDC |
| F09 | Rechercher offres (secteur/rang) | Vitale | Public | A faire | Ajouter formulaire + route + resultat | Filtre conforme |
| F10 | Rechercher candidatures (secteur/rang) | Vitale | Public | A faire | Ajouter formulaire + route + resultat | Filtre conforme |
| F11 | Completer/modifier profil entreprise | Vitale | Company | A faire | Formulaire dedie + controle proprietaire + sauvegarde | Donnees modifiees |
| F12 | Lister mes offres (entreprise) | Vitale | Company | A faire | Route / vue perso + actions associees | Liste visible pour proprietaire |
| F13 | Creer offre | Vitale | Company | A faire | Formulaire creation offre + secteurs + qualif | Offre creee et visible |
| F14 | Lister candidatures matchant une offre | Vitale | Company | A faire | Route de matching depuis offre de l entreprise | Matching conforme regles |
| F15 | Completer/modifier profil candidat | Vitale | Candidate | A faire | Formulaire dedie + controle proprietaire + sauvegarde | Donnees modifiees |
| F16 | Lister mes candidatures (candidat) | Vitale | Candidate | A faire | Route / vue perso + actions associees | Liste visible pour proprietaire |
| F17 | Creer candidature | Vitale | Candidate | A faire | Formulaire creation candidature + secteurs + qualif | Candidature creee |
| F18 | Lister offres matchant une candidature | Vitale | Candidate | A faire | Route de matching depuis candidature du candidat | Matching conforme regles |
| F19 | Supprimer utilisateur + donnees liees | Vitale (partiellement fourni) | Admin | Partiel/OK backend | Valider totalement en presentation et tests integration | Suppression complete verifiee |
| F20 | Mettre a jour offre | Mineure | Company | A faire | Formulaire edition offre + controle proprietaire | Offre mise a jour |
| F21 | Supprimer offre | Mineure | Company | A faire | Action suppression + confirmation + cascade messages | Offre supprimee |
| F22 | Mettre a jour candidature | Mineure | Candidate | A faire | Formulaire edition candidature + controle proprietaire | Candidature mise a jour |
| F23 | Supprimer candidature | Mineure | Candidate | A faire | Action suppression + confirmation + cascade messages | Candidature supprimee |
| F24 | Message auto vers candidats (nouvelle offre) | Mineure | Company | Deja en service | Exposer clairement en UI (historique) et prouver comportement | Message auto visible |
| F25 | Message auto vers entreprises (nouvelle candidature) | Mineure | Candidate | Deja en service | Exposer clairement en UI (historique) et prouver comportement | Message auto visible |
| F26 | Message manuel entreprise -> candidat | Mineure | Company | A faire | Ecran envoi depuis contexte offre/matching | Message envoye |
| F27 | Message manuel candidat -> entreprise | Mineure | Candidate | A faire | Ecran envoi depuis contexte candidature/offre | Message envoye |
| F28 | Lister messages recus entreprise | Mineure | Company | A faire | Vue historique triee date | Historique conforme |
| F29 | Lister messages envoyes entreprise | Mineure | Company | A faire | Vue historique triee date | Historique conforme |
| F30 | Lister messages recus candidat | Mineure | Candidate | A faire | Vue historique triee date | Historique conforme |
| F31 | Lister messages envoyes candidat | Mineure | Candidate | A faire | Vue historique triee date | Historique conforme |

## Plan d execution concret

### Phase A - Stabilisation navigation et routes vitales publiques

- Corriger les liens de menu qui pointent vers des routes absentes.
- Implementer en priorite les routes et vues de consultation publique vitales.
- Uniformiser les templates avec les fragments Thymeleaf deja fournis.

### Phase B - Fonctionnalites vitales entreprise

- Ajouter les ecrans de profil entreprise et gestion de ses offres.
- Ajouter creation d offre et consultation des candidatures match.
- Verifier strictement les controles d acces (role + proprietaire).

### Phase C - Fonctionnalites vitales candidat

- Ajouter les ecrans de profil candidat et gestion de ses candidatures.
- Ajouter creation de candidature et consultation des offres match.
- Verifier strictement les controles d acces (role + proprietaire).

### Phase D - Mineures a forte valeur

- Mise a jour/suppression offres et candidatures.
- Flux messages manuels entreprise/candidat.
- Historiques messages envoyes/recus pour les deux profils.
- Mise en evidence UI des messages automatiques deja supportes par les services.

### Phase E - Qualite, tests, recette

- Etendre les tests d integration web par role et par route.
- Ajouter tests des flux critiques bout en bout (create -> match -> message -> suppression).
- Mettre a jour la fiche recette ligne par ligne avec O/N + commentaire factuel.
- Faire une repetition complete de la demo 15 minutes.

### Phase F - Packaging et rendu final

- Executer mvn clean puis mvn test.
- Verifier execution sur environnement propre sans modif manuelle.
- Produire l archive finale selon les consignes Moodle (nommage, contenu complet, fiche recette incluse).

## Definition de done Livrable 3

- 100 pourcent des fonctionnalites vitales demonstrables en direct.
- Zero lien mort dans la navigation.
- Controles d acces conformes pour admin, entreprise, candidat.
- Fiche recette completee et coherente avec l application.
- Suite de tests verte avant archivage.
- Archive executable sans intervention technique supplementaire.
