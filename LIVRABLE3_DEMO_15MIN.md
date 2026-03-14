# Livrable 3 - Script demo 15 minutes

Date: 2026-03-14
Branche: partie3

## Objectif

Montrer en 15 minutes:

- les fonctionnalites vitales publiques,
- les espaces entreprise/candidat,
- la securite (401/403 et controle proprietaire),
- les messages manuels et automatiques,
- la qualite logicielle (tests + packaging).

## Setup avant demo (a faire avant la soutenance)

1. Lancer la base:

```powershell
cd dev
docker compose up -d db
```

2. Lancer l application:

```powershell
cd dev/JobManagement
mvn spring-boot:run
```

3. Ouvrir `http://localhost:8080`.

## Comptes de demonstration

- Admin: `admin@imt-atlantique.fr` / `password123`
- Company: `contact@sportinnovation.fr` / `password789`
- Applicant: `toto@mytoto.fr` / `password456`

## Deroule minute par minute

### 0:00 -> 2:00 | Parcours public

- Montrer la home page et la navigation.
- Ouvrir:
  - `/allcompanies` puis un detail entreprise,
  - `/allapplicants` puis un detail candidat,
  - `/alljobs` et `/allapplications`.
- Appliquer un filtre sur offres puis sur candidatures (secteur + rang).

### 2:00 -> 6:00 | Parcours entreprise

- Se connecter avec le compte company.
- Ouvrir le menu entreprise depuis la home.
- Montrer:
  - edition profil entreprise,
  - creation d une offre,
  - edition/suppression offre,
  - page matching de l offre.

### 6:00 -> 10:00 | Parcours candidat

- Se deconnecter puis se connecter avec le compte applicant.
- Montrer:
  - edition profil candidat,
  - creation d une candidature,
  - edition/suppression candidature,
  - page matching de la candidature.

### 10:00 -> 12:30 | Messagerie (manuel + auto)

- Depuis le parcours candidat: envoyer un message manuel vers une offre match.
- Se reconnecter en company et ouvrir `Check my messages`.
- Montrer recu/envoye et tri par date.
- Expliquer que les messages automatiques sont envoyes a la creation offre/candidature et couverts par tests `AutomaticMessageServiceTest`.

### 12:30 -> 14:00 | Securite et admin

- Se connecter en admin.
- Montrer gestion utilisateurs (add/list/delete).
- Expliquer les controles 401/403 et le controle proprietaire couverts par tests d integration portail.

### 14:00 -> 15:00 | Qualite et livraison

- Montrer les commandes executes:
  - `mvn clean`
  - `mvn test` (168 tests, 0 echec)
  - `mvn package -DskipTests`
- Montrer la presence du jar dans `dev/JobManagement/target/`.
- Conclure avec les documents livrable (`LIVRABLE3_RECETTE.md`, backlog, checklist packaging).

## Plan B (si incident live)

- Si l envoi manuel rate en direct: afficher les pages historiques deja peuplees.
- Si la base est indisponible: basculer sur les preuves automatisees (tests integration + capture commandes Maven).