# Manuell testning

Den här filen beskriver manuella API-tester som använts för att kontrollera att tjänsten fungerar.

## Testade scenarier

- Skapa ett ärende
- Lista ärenden
- Uppdatera status från `NEW` till `IN_PROGRESS`
- Lägga till kommentar
- Filtrera ärenden på status
- Kontrollera felhantering för otillåten statusändring från `NEW` till `CLOSED`

## Förväntat resultat

Giltiga anrop ska returnera `200 OK` eller `201 Created`.

Otillåten statusändring från `NEW` direkt till `CLOSED` ska returnera `400 Bad Request`.

Anrop mot ett ärende som inte finns ska returnera `404 Not Found`.