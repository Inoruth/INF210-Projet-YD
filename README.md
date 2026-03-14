# INF210 - Job Management Portal

## Project goal

Web application built for INF210 to connect companies and candidates.
The stack is Spring Boot (MVC + Thymeleaf + JPA) with PostgreSQL.

## Main functional scope

- Public catalog:
	- companies and company details,
	- candidates and candidate details,
	- job offers and applications,
	- search filters by sectors and minimum qualification rank.
- Company area:
	- edit profile,
	- create/update/delete offers,
	- list matching applications,
	- send manual messages to candidates,
	- read sent/received message history.
- Candidate area:
	- edit profile,
	- create/update/delete applications,
	- list matching offers,
	- send manual messages to companies,
	- read sent/received message history.
- Admin area:
	- create/update/delete users,
	- access control by role and owner.

## Repository structure

```text
dev/
	compose.yaml                # PostgreSQL + pgAdmin (Docker)
	Data/SQL_Scripts/           # DB schema and seed data
	JobManagement/              # Spring Boot application
LIVRABLE3_BACKLOG.md          # Sprint tracking
PLAN_LIVRABLE3.md             # Execution plan and functional matrix
LIVRABLE3_RECETTE.md          # Recipe sheet (line-by-line)
LIVRABLE3_DEMO_15MIN.md       # Demo script
LIVRABLE3_PACKAGING_CHECKLIST.md
```

## Prerequisites

- Java 21
- Maven 3.9+
- One of the following:
	- Docker Desktop (recommended),
	- local PostgreSQL instance reachable on localhost:5432.

## Start with Docker (recommended)

1. Start database services:

```powershell
cd dev
docker compose up -d db
```

The first startup automatically:

- creates database `jobmanagement_db`,
- loads `create_db_jobmanagement.sql` (schema + seed users).

2. Start the application:

```powershell
cd dev/JobManagement
mvn spring-boot:run
```

3. Open the portal:

```text
http://localhost:8080/
```

## Start with local PostgreSQL (without Docker)

Keep default values from `application.properties`:

- database: `jobmanagement_db`
- user: `pguser`
- password: `pgpwd`

Then run:

```powershell
cd dev/JobManagement
mvn spring-boot:run
```

## Demo users (seeded by SQL script)

- Admin: `admin@imt-atlantique.fr` / `password123`
- Candidate: `toto@mytoto.fr` / `password456`
- Company: `contact@sportinnovation.fr` / `password789`

## Useful routes

- Home: `/`
- Public pages:
	- `/allcompanies`
	- `/allapplicants`
	- `/alljobs`
	- `/allapplications`
- Company portal:
	- `/managemyoffers/{mail}`
	- `/managemyoffers/{mail}/messages`
- Candidate portal:
	- `/managemyapplications/{mail}`
	- `/managemyapplications/{mail}/messages`

## Build and test

```powershell
cd dev/JobManagement
mvn clean
mvn test
mvn package -DskipTests
```

Expected current status:

- `mvn test` -> BUILD SUCCESS, 168 tests run, 0 failure, 0 error.
- packaged jar generated in `dev/JobManagement/target/`.

## Troubleshooting

If you see an error like `database "jobmanagement_db" does not exist`, your local Docker data may come from an older setup.

Reset and reinitialize the database:

```powershell
cd dev
docker compose down -v
docker compose up -d db
```

## Delivery documents

- `PLAN_LIVRABLE3.md`
- `LIVRABLE3_BACKLOG.md`
- `LIVRABLE3_RECETTE.md`
- `LIVRABLE3_DEMO_15MIN.md`
- `LIVRABLE3_PACKAGING_CHECKLIST.md`
- `RELEASE_NOTES_LIVRABLE2.md`
