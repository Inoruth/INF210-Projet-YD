# Livrable 3 - Packaging checklist

Date: 2026-03-14
Branche: partie3

## 1) Build et tests

- [x] `mvn clean`
  - Resultat: BUILD SUCCESS
  - Horodatage: 2026-03-14T16:40:11+01:00

- [x] `mvn test`
  - Resultat: BUILD SUCCESS
  - Detail: 168 tests, 0 failure, 0 error, 0 skipped
  - Horodatage: 2026-03-14T16:40:41+01:00

- [x] `mvn package -DskipTests`
  - Resultat: BUILD SUCCESS
  - Artefact:
    - `dev/JobManagement/target/JobManagement-0.0.1-SNAPSHOT.jar`
    - `dev/JobManagement/target/JobManagement-0.0.1-SNAPSHOT.jar.original`
  - Horodatage: 2026-03-14T16:41:10+01:00

## 2) Execution sur environnement propre

- [ ] Verifier `docker compose up -d` (base + pgadmin)
- [ ] Verifier `mvn spring-boot:run` sur machine finale
- [ ] Smoke test HTTP: home + pages publiques + login

Note: cette verification ne peut pas etre finalisee sur la machine actuelle car la commande `docker` est absente.

## 3) Controle archive de rendu

- [ ] Nom de l archive conforme consignes Moodle
- [ ] Contenu archive:
  - [ ] code source complet
  - [ ] scripts SQL et compose
  - [ ] documents de suivi (plan, backlog, recette, demo)
  - [ ] preuves build/test
- [ ] Test extraction archive dans dossier vide
- [ ] Rejeu rapide du run (`docker compose up`, `mvn spring-boot:run`) apres extraction

## 4) Documents a inclure

- `PLAN_LIVRABLE3.md`
- `LIVRABLE3_BACKLOG.md`
- `LIVRABLE3_RECETTE.md`
- `LIVRABLE3_DEMO_15MIN.md`
- `LIVRABLE3_PACKAGING_CHECKLIST.md`
- support officiel `Recette_application.pdf` complete