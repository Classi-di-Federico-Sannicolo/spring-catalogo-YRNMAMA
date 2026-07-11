# Relazione Tecnica — Esame UF13 "Sviluppi SpringBoot"

Progetto: **Catalogo Videogiochi Spring Boot**
Progetto di partenza: repository personale sviluppato durante la prima parte del corso (gestione catalogo videogiochi con CRUD, integrazione API RAWG, interfaccia Thymeleaf).
Branch finale di consegna: `deploy` (ottenuto mergiando i branch `feature/task-1` … `feature/task-4`).

---

## 1. Vincoli tecnici generali rispettati

- **Layer DTO**: i controller REST non espongono mai le entità JPA. Tutti gli scambi in uscita usano `ApiResponse<T>` e, per i dati di dominio, `VideogiocoDto` (in ingresso `VideogiocoForm`). L'entità `Videogioco` resta confinata al service/repository.
- **Tipizzazione esplicita**: nessun uso di `Void` generico non necessario; i metodi annotati con `@Validated` usano DTO dedicati e `ApiResponse<T>` è parametrizzato sul tipo concreto (`ApiResponse<Void>`, `ApiResponse<VideogiocoDto>`, ...).
- **Branching strategy**: ogni task è stato sviluppato nel proprio branch `feature/task-x` e unito nel branch `deploy`.

| Task | Branch | Merge in `deploy` |
|------|--------|-------------------|
| 1 — Risposte standardizzate & errori centralizzati | `feature/task-1` | `26e7ce5` |
| 2 — Containerizzazione multi-stage & profili | `feature/task-2` | `c92f8d6` |
| 3 — Monitoraggio & alerting | `feature/task-3` | `beb7682` |
| 4 — Robustezza & automazione test | `feature/task-4` | `d22315c` |

---

## 2. Task 1 — Standardizzazione delle risposte e centralizzazione degli errori (OBBLIGATORIA)

**Scelte progettuali**
- Creata la classe generica `ApiResponse<T>` (`src/main/java/itt/marconi/videogiochi/api/ApiResponse.java`) che incapsula lo stato dell'operazione (`success`, `fail`, `error`), i `data`, un `message`, una mappa `errors` e un `timestamp`. I metodi statici di factory `success(...)`, `fail(...)`, `error(...)` garantiscono un unico contratto JSend coerente.
- Implementato `GlobalExceptionHandler` con `@RestControllerAdvice` (`src/main/java/itt/marconi/videogiochi/api/GlobalExceptionHandler.java`) che centralizza la formattazione degli errori HTTP, eliminando i blocchi `try-catch` dai controller.
- I controller (`VideogiocoRestController`) restituiscono esclusivamente `ResponseEntity<ApiResponse<...>>`.

**Validazione real-time (400 Bad Request)**
- Gli endpoint di creazione/aggiornamento accettano `@Validated @RequestBody VideogiocoForm` (vincoli `@NotEmpty`, `@Size`, `@NotNull`, `@Min`, `@Max`).
- `MethodArgumentNotValidException` viene intercettata e restituisce `400` con corpo `fail` contenente la mappa `campo → messaggio`, conforme a JSend.

**Isolamento delle eccezioni**
- Nessun `try-catch` per la formattazione degli errori nei controller; `ResponseStatusException` (es. `404 Not Found` su risorsa assente) è gestita centralmente e tradotta nel formato standardizzato.

---

## 3. Task 2 — Containerizzazione multi-stage e isolamento dei profili

**Dockerfile multi-stage** (`Dockerfile`)
- Stage `AS build`: immagine `maven:3.9.9-eclipse-temurin-21` che compila il sorgente con `mvn clean package`.
- Stage di esecuzione: immagine JRE minimale `eclipse-temurin:21-jre-alpine` in cui viene copiato **solo** l'artefatto `.jar`. L'immagine finale non contiene Maven né il JDK di build, riducendo l'impronta.

**Gestione dei profili Spring (dev / prod)**
- `application.properties` imposta il profilo attivo tramite `SPRING_PROFILES_ACTIVE` (default `dev`).
- `application-dev.properties`: logging `TRACE` + `org.hibernate.SQL`/`org.hibernate.orm.jdbc.bind` a `TRACE` (tracciamento query SQL).
- `application-prod.properties`: logging `INFO` con scrittura su file rotante.
- `logback-spring.xml`: profilo `dev` su `CONSOLE`; profilo `prod` su `ROLLING_FILE` (`/var/log/videogiochi/videogiochi.log`) con `SizeAndTimeBasedRollingPolicy` (max 10MB, 30 giorni di storico), salvare i log sull'host.

---

## 4. Task 3 — Monitoraggio proattivo e sistemi di alerting

**Stack di osservabilità**
- Dipendenze `spring-boot-starter-actuator` e `micrometer-registry-prometheus` (`pom.xml`).
- Actuator espone `health`, `info`, `prometheus`, `metrics` (`application.properties`).

**Orchestrazione** (`docker-compose.yaml`)
- Servizio `prometheus` con `prometheus.yml` configurato per lo *scraping* dell'endpoint `/actuator/prometheus` del backend a intervalli di `15s`.
- Servizio `grafana` con *provisioning* automatico di datasource (Prometheus), dashboard JVM (`jvm-overview.json`, template community) e regola di alert.

**Alert rule**
- `grafana/provisioning/alerting/alert-rules.yml` definisce `High500Errors`:
  - `expr: rate(http_server_requests_seconds_count{status="500"}[5m]) > 10`
  - `for: 2m`, `severity: critical`.
- **Criterio di accettazione**: durante la difesa è possibile simulare errori HTTP 500; superata la soglia di 10 chiamate d'errore l'allarme passa in stato `Firing`.

---

## 5. Task 4 — Robustezza del codice e automazione dei test

**Test del Controller** (`VideogiocoRestControllerWebMvcTest`)
- `@WebMvcTest(VideogiocoRestController.class)` + `@Import(GlobalExceptionHandler.class)`, Mockito bean via `@MockitoBean`.
- Verifica con `jsonPath` la struttura del JSON (`$.status`, `$.data[0].titolo`, `$.message`) per:
  - successo `200 OK` (lista standardizzata);
  - risorsa non trovata `404 Not Found` (`$.status = fail`);
  - errore di business `400 Bad Request` (`$.status = fail`).

**Test del Service** (`VideogiocoServiceTest`)
- `@ExtendWith(MockitoExtension.class)` con `@Mock` per `VideogiocoRepository` e `@InjectMocks` per il service.
- `saveShouldClearIdBeforePersisting`: tramite `verify(...)` e `argThat(...)` si certifica che, prima del salvataggio, l'`id` sia azzerato (`v.getId() == null`) impedendo la corruzione di record esistenti (Task 4 business rule).
- `saveShouldRejectNullForm`: verifica il sollevamento di `IllegalArgumentException`.

**Esecuzione dei test**
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw -Dspring.profiles.active=test test
```
Risultato: **BUILD SUCCESS — Tests run: 6, Failures: 0, Errors: 0** (`VideogiochiApplicationTests` x1, `VideogiocoRestControllerWebMvcTest` x3, `VideogiocoServiceTest` x2).

> Nota: il progetto richiede Java 21. Con una JDK ≤ 17 i test falliscono con `UnsupportedClassVersionError` (JUnit Platform compilato per Java 17+).

---

## 6. Criteri di accettazione soddisfatti

| Criterio | Stato |
|----------|-------|
| Task 1 completa e corretta (sufficienza) | ✅ |
| Risposte sempre standardizzate (`ApiResponse`) | ✅ |
| Errori centralizzati via `@RestControllerAdvice` | ✅ |
| 400 + mappa errori per validazione | ✅ |
| Nessun `try-catch` nei controller per formattazione | ✅ |
| Dockerfile multi-stage (build / JRE-alpine) | ✅ |
| Profili `dev` (TRACE+SQL) / `prod` (INFO+file rotante) | ✅ |
| Actuator + Micrometer Prometheus | ✅ |
| Prometheus + Grafana in compose | ✅ |
| Alert su errori 500 > 10 | ✅ |
| Test controller (`@WebMvcTest` + `jsonPath`) | ✅ |
| Test service (`MockitoExtension` + `verify`/`argThat`, azzeramento ID) | ✅ |
| Relazione in `docs/` collegata al README | ✅ |

---

## 7. Istruzioni di avvio e verifica

```bash
cd videogiochi

# Stack completo (DB + backend + Prometheus + Grafana)
docker-compose up -d

# Backend in locale
SPRING_PROFILES_ACTIVE=dev  ./mvnw spring-boot:run   # sviluppo
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run   # produzione (log su file)

# Test automatici
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw -Dspring.profiles.active=test test
```

Punti di accesso: Web `http://localhost:8080`, API `http://localhost:8080/api`, Prometheus `http://localhost:9090`, Grafana `http://localhost:3000`.
