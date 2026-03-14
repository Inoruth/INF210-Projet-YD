# Livrable 3 - Fiche recette (version markdown)

Date: 2026-03-14
Branche: partie3

## Legende

- O: valide
- N: non valide
- P: partiel

## Preconditions de recette

- Base initialisee avec `dev/Data/SQL_Scripts/create_db_jobmanagement.sql`.
- Application demarree sur `http://localhost:8080`.
- Comptes de reference:
  - admin: `admin@imt-atlantique.fr` / `password123`
  - company: `contact@sportinnovation.fr` / `password789`
  - applicant: `toto@mytoto.fr` / `password456`

## Validation ligne par ligne

| ID | Fonctionnalite | Resultat | Preuve principale | Commentaire |
|---|---|---|---|---|
| F01 | Se connecter | O | AppUserControllerAuthIntegrationTest: `shouldLoginAndStoreSessionAttributes` | Session utilisateur et role verifies. |
| F02 | Se deconnecter | O | AppUserControllerAuthIntegrationTest: `shouldLogoutAndRedirectToHome` | Retour accueil confirme. |
| F03 | Lister entreprises | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicListingPages` | Route `/allcompanies` disponible. |
| F04 | Afficher details entreprise | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicDetailPages` | Route `/company/{mail}` avec offres publiees. |
| F05 | Lister candidats | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicListingPages` | Route `/allapplicants` disponible. |
| F06 | Afficher details candidature | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicDetailPages` | Route `/application/{id}` disponible. |
| F07 | Lister toutes les offres | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicListingPages` | Route `/alljobs` disponible. |
| F08 | Lister toutes les candidatures | O | PublicCatalogControllerIntegrationTest: `shouldRenderPublicListingPages` | Route `/allapplications` disponible. |
| F09 | Rechercher offres (secteur/rang) | O | PublicCatalogControllerIntegrationTest: `shouldApplySearchFiltersOnPublicPages` | Filtres `sectorIds` et `minimumRank` actifs. |
| F10 | Rechercher candidatures (secteur/rang) | O | PublicCatalogControllerIntegrationTest: `shouldApplySearchFiltersOnPublicPages` | Filtres `sectorIds` et `minimumRank` actifs. |
| F11 | Completer/modifier profil entreprise | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToUpdateOwnProfile` | Controle proprietaire + role valide. |
| F12 | Lister mes offres (entreprise) | O | CompanyPortalControllerIntegrationTest: `shouldCreateOfferAndRenderOwnedOffersAndMatches` | Route `/managemyoffers/{mail}` validee. |
| F13 | Creer offre | O | CompanyPortalControllerIntegrationTest: `shouldCreateOfferAndRenderOwnedOffersAndMatches` | Publication offre validee. |
| F14 | Lister candidatures matchant une offre | O | CompanyPortalControllerIntegrationTest: `shouldCreateOfferAndRenderOwnedOffersAndMatches` | Route de matching validee. |
| F15 | Completer/modifier profil candidat | O | CandidatePortalControllerIntegrationTest: `shouldAllowApplicantToUpdateOwnProfile` | Controle proprietaire + role valide. |
| F16 | Lister mes candidatures (candidat) | O | CandidatePortalControllerIntegrationTest: `shouldCreateApplicationAndRenderOwnedApplicationsAndMatches` | Route `/managemyapplications/{mail}` validee. |
| F17 | Creer candidature | O | CandidatePortalControllerIntegrationTest: `shouldCreateApplicationAndRenderOwnedApplicationsAndMatches` | Publication candidature validee. |
| F18 | Lister offres matchant une candidature | O | CandidatePortalControllerIntegrationTest: `shouldCreateApplicationAndRenderOwnedApplicationsAndMatches` | Route de matching validee. |
| F19 | Supprimer utilisateur + donnees liees | O | AppUserControllerSecurityIntegrationTest + AppUserServiceTest | Suppression admin protegee, suppression profils lies couverte. |
| F20 | Mettre a jour offre | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToUpdateAndDeleteOwnOffer` | Mise a jour offre validee. |
| F21 | Supprimer offre | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToUpdateAndDeleteOwnOffer` | Suppression offre validee. |
| F22 | Mettre a jour candidature | O | CandidatePortalControllerIntegrationTest: `shouldAllowCandidateToUpdateAndDeleteOwnApplication` | Mise a jour candidature validee. |
| F23 | Supprimer candidature | O | CandidatePortalControllerIntegrationTest: `shouldAllowCandidateToUpdateAndDeleteOwnApplication` | Suppression candidature validee. |
| F24 | Message auto vers candidats (nouvelle offre) | O | AutomaticMessageServiceTest: `shouldSendAutomaticMessagesForNewOfferWithoutDuplicates` | Non regression messages automatiques validee. |
| F25 | Message auto vers entreprises (nouvelle candidature) | O | AutomaticMessageServiceTest: `shouldSendAutomaticMessagesForNewApplicationWithoutDuplicates` | Non regression messages automatiques validee. |
| F26 | Message manuel entreprise -> candidat | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToSendManualMessageAndViewHistory` | Envoi manuel valide. |
| F27 | Message manuel candidat -> entreprise | O | CandidatePortalControllerIntegrationTest: `shouldAllowCandidateToSendManualMessageAndViewHistory` | Envoi manuel valide. |
| F28 | Lister messages recus entreprise | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToSendManualMessageAndViewHistory` | Historique entreprise recu/tri date valide. |
| F29 | Lister messages envoyes entreprise | O | CompanyPortalControllerIntegrationTest: `shouldAllowCompanyToSendManualMessageAndViewHistory` | Historique entreprise envoye/tri date valide. |
| F30 | Lister messages recus candidat | O | CandidatePortalControllerIntegrationTest: `shouldAllowCandidateToSendManualMessageAndViewHistory` | Historique candidat recu/tri date valide. |
| F31 | Lister messages envoyes candidat | O | CandidatePortalControllerIntegrationTest: `shouldAllowCandidateToSendManualMessageAndViewHistory` | Historique candidat envoye/tri date valide. |

## Verification technique globale

- `mvn clean`: BUILD SUCCESS.
- `mvn test`: BUILD SUCCESS, 168 tests, 0 failure, 0 error.
- `mvn package -DskipTests`: BUILD SUCCESS, jar genere.

## Point d attention avant rendu

- Rejouer une passe visuelle complete sur machine finale (Docker disponible), puis recopier ce tableau dans le format officiel PDF de recette.