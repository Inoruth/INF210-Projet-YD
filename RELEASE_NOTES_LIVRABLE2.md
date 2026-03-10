# Release Notes - Livrable 2

Date: 2026-03-10
Branch: `partie2`

## Scope Delivered

- Data layer aligned with Part 2 schema (entities, repositories, custom queries).
- Business layer completed (services + implementations).
- Compliance updates applied:
  - minimum password length set to 4,
  - admin account deletion prevented,
  - automatic messages sent on offer/application creation.
- FK-safe user deletion fixed for company/applicant profiles.

## Validation Evidence

- Automated tests: `89 passed, 0 failed`.
- Runtime smoke tests on `http://localhost:8080`:
  - `GET /` -> 200
  - `GET /adduser` -> 200
  - `GET /manageusers` -> 200
  - `GET /allqualifications` -> 200
  - `GET /allsectors` -> 200
- End-to-end flow validated:
  - create user,
  - login,
  - delete user,
  - verify user no longer present.

## Key Commits

- `d40f7cc` test(service): cover appuser deletion with linked profiles
- `804dcb0` fix(service): delete linked profile before appuser removal
- `13af476` test(service): cover automatic messaging and compliance rules
- `64a4e67` feat(service): auto-send messages on offer and application creation
- `2e66581` feat(compliance): enforce password policy and admin-safe deletion
