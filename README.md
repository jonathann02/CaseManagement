# Case Management API

En enkel Spring Boot-applikation för handläggare som behöver skapa, följa och filtrera ärenden.

## Tech stack

- Java 17
- Spring Boot
- Spring Data JPA
- H2 in-memory database
- REST API
- JUnit 5 / Spring Boot Test

## Funktioner

- Skapa ärende
- Lista ärenden
- Visa ett specifikt ärende
- Uppdatera status
- Lägg till kommentar
- Filtrera på status och prioritet
- Audit log för skapat ärende, statusändring och ny kommentar

## Statusar

- `NEW`
- `IN_PROGRESS`
- `WAITING_FOR_CUSTOMER`
- `RESOLVED`
- `CLOSED`

Tillåtna statusövergångar:

- `NEW` -> `IN_PROGRESS`
- `IN_PROGRESS` -> `WAITING_FOR_CUSTOMER`, `RESOLVED`
- `WAITING_FOR_CUSTOMER` -> `IN_PROGRESS`, `RESOLVED`
- `RESOLVED` -> `IN_PROGRESS`, `CLOSED`
- `CLOSED` har inga tillåtna övergångar

## Prioriteter

- `LOW`
- `MEDIUM`
- `HIGH`
- `URGENT`

## Köra lokalt

Krav: Java 17+ och Maven.

```bash
mvn spring-boot:run
```

API:t körs på:

```text
http://localhost:8080
```

H2-konsol:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:casemanagement
```

## Tester

```bash
mvn test
```

Testerna täcker:

- Skapa ärende
- Statusändring från `NEW` till `IN_PROGRESS`
- Ogiltig statusändring från `NEW` till `CLOSED`
- Kommentar får timestamp
- Filtrering på status

## API

### Skapa ärende

```http
POST /api/cases
```

```json
{
  "title": "Missing document",
  "description": "Customer needs to submit one more document.",
  "category": "BENEFITS",
  "priority": "HIGH",
  "assignedTo": "anna@example.com"
}
```

### Lista ärenden

```http
GET /api/cases
GET /api/cases?status=NEW
GET /api/cases?priority=HIGH
GET /api/cases?status=IN_PROGRESS&priority=HIGH
```

### Visa ärende

```http
GET /api/cases/{id}
```

### Uppdatera status

```http
PATCH /api/cases/{id}/status
```

```json
{
  "status": "IN_PROGRESS"
}
```

### Lägg till kommentar

```http
POST /api/cases/{id}/comments
```

```json
{
  "author": "anna",
  "text": "Customer has been contacted."
}
```
