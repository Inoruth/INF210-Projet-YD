# Release Notes - Livrable 2

Date: 2026-03-13
Branch: partie2
Reference tag: livrable2-2026-03-10
Latest delivery commit: 37be541

## Scope Delivered

- Data layer aligned with Part 2 schema:
  - entities,
  - repositories,
  - custom query methods.
- Business layer completed:
  - service contracts,
  - service implementations,
  - controller-to-service integration.
- Compliance and security behavior applied:
  - minimum password length enforced (4),
  - admin deletion blocked,
  - user management routes protected by authorization checks.
- Automatic messaging behavior implemented:
  - automatic message generation on offer/application creation,
  - matching based on qualification level and sector overlap.
- Referential integrity secured:
  - linked candidate/company profile is deleted before deleting the related app user.
- Code readability improved for submission:
  - technical comments added and enriched on livrable 2 modified files.

## Validation Evidence

- Full automated test suite: 152 tests, 0 failures, 0 errors, 0 skipped.
- Command used: mvn test.
- Build verification: mvn -DskipTests compile -> BUILD SUCCESS.

## Key Commits

- 37be541 docs: enrich livrable2 code comments
- edfeae8 test: reinforce controller and service coverage
- eff5f29 fix(controller): enforce authz on user management endpoints
- d40f7cc test(service): cover appuser deletion with linked profiles
- 804dcb0 fix(service): delete linked profile before appuser removal
- 13af476 test(service): cover automatic messaging and compliance rules
- 64a4e67 feat(service): auto-send messages on offer and application creation
- 2e66581 feat(compliance): enforce password policy and admin-safe deletion
- 96daff6 feat(service): implement livrable2 business service layer
- 8cdbdee feat(repository): add custom queries for livrable2 data access
