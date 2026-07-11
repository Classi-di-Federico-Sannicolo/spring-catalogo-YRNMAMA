# Catalogo Videogiochi Spring Boot

Un'applicazione web completa per la gestione di un catalogo videogiochi, con integrazione API RAWG e operazioni CRUD complete.

## Documentazione e Relazione Tecnica

La relazione tecnica di accompagnamento all'esame (scelte progettuali, test eseguiti e criteri di accettazione per le Task 1–4) è disponibile in [`docs/relazione-tecnica.md`](docs/relazione-tecnica.md).

## Descrizione

Questa applicazione Spring Boot permette di:
- **Gestire un catalogo locale** di videogiochi con operazioni CRUD complete
- **Integrare dati esterni** dall'API RAWG per videogiochi popolari
- **Fornire API REST** per l'accesso programmatico
- **Offrire un'interfaccia web** user-friendly per la gestione manuale

## Tecnologie Utilizzate

- **Java 21** - Linguaggio di programmazione
- **Spring Boot 4.0.5** - Framework web
- **Spring Data JPA** - ORM per database
- **MySQL** - Database relazionale
- **Thymeleaf** - Template engine per interfaccia web
- **RestTemplate** - Client HTTP per chiamate API esterne
- **Actuator + Micrometer Prometheus** - health check e metriche runtime
- **Grafana** - dashboard e alerting operativi
- **Docker** - Containerizzazione database e stack di osservabilità
- **Maven** - Gestione dipendenze

## Avvio Rapido

### Prerequisiti
- Java 21 o superiore
- Docker e Docker Compose
- Maven (opzionale, usa gli wrapper inclusi)

### 1. Avvia il stack completo
```bash
cd videogiochi
docker-compose up -d
```
Questo avvia il database MySQL, il backend Spring Boot e i servizi di monitoraggio Prometheus/Grafana.

### 2. Avvia l'Applicazione con profilo
```bash
# sviluppo
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

# produzione
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

### 3. Accedi all'Applicazione
- **Interfaccia Web**: http://localhost:8080
- **API Base**: http://localhost:8080/api
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000

## Monitoraggio e Alerting

Il progetto espone metriche JVM e HTTP tramite Spring Boot Actuator, esportate in Prometheus.

- Endpoint Prometheus: `/actuator/prometheus`
- Dashboard Grafana pre-configurate per JVM e alerting
- Regola di alert: `High500Errors` su spike di errori HTTP 500

## Verifica automatica

Per verificare il comportamento atteso del backend in ambiente test, esegui:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw -Dspring.profiles.active=test test
```

La suite include test del controller REST, del service e il bootstrapping dell'applicazione.

## Struttura del Progetto

```
src/main/java/itt/marconi/videogiochi/
├── config/
│   └── RestConfig.java              # Configurazione RestTemplate
├── controllers/
│   ├── CatalogoController.java      # Controller MVC per interfaccia web
│   └── VideogiocoRestController.java # Controller REST per API
├── domain/
│   ├── Videogioco.java              # Entità JPA
│   └── VideogiocoForm.java          # DTO per form
├── locale/
│   └── LocaleConfig.java            # Configurazione internazionalizzazione
├── repositories/
│   └── VideogiocoRepository.java    # Repository JPA
└── services/
    └── VideogiocoService.java       # Logica di business

src/main/resources/
├── templates/                       # Template Thymeleaf
│   ├── videogioco-list.html
│   ├── videogioco-form.html
│   ├── videogioco-detail.html
│   └── error/
├── messages_en.properties          # Messaggi inglese
├── messages_it.properties          # Messaggi italiano
└── application.properties          # Configurazioni app
```

## API REST

### API RAWG (Dati Esterni)

#### Ottieni videogiochi popolari da RAWG
```http
GET /api/giochi
```

**Risposta**: JSON completo dall'API RAWG con lista videogiochi popolari.

**Esempio**:
```bash
curl http://localhost:8080/api/giochi
```

### API Catalogo Locale (CRUD)

#### 1. CREATE - Crea nuovo videogioco
```http
POST /api/catalogo
Content-Type: application/json

{
  "titolo": "The Legend of Zelda",
  "produttore": "Nintendo",
  "genere": "Avventura",
  "anno": 1986
}
```

**Esempio**:
```bash
curl -X POST http://localhost:8080/api/catalogo \
  -H "Content-Type: application/json" \
  -d '{"titolo":"Super Mario","produttore":"Nintendo","genere":"Platform","anno":1985}'
```

#### 2. READ - Lista tutti i videogiochi
```http
GET /api/catalogo
GET /api/catalogo?search={testo}
```

**Parametri**:
- `search` (opzionale): filtro per titolo

**Esempi**:
```bash
# Lista tutti
curl http://localhost:8080/api/catalogo

# Cerca per titolo
curl "http://localhost:8080/api/catalogo?search=Mario"
```

#### 3. READ - Ottieni videogioco specifico
```http
GET /api/catalogo/{id}
```

**Esempio**:
```bash
curl http://localhost:8080/api/catalogo/123e4567-e89b-12d3-a456-426614174000
```

#### 4. UPDATE - Modifica videogioco
```http
PUT /api/catalogo/{id}
Content-Type: application/json

{
  "titolo": "Nuovo Titolo",
  "produttore": "Nuovo Produttore",
  "genere": "Nuovo Genere",
  "anno": 2024
}
```

**Esempio**:
```bash
curl -X PUT http://localhost:8080/api/catalogo/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{"titolo":"Mario Kart 8 Deluxe","produttore":"Nintendo","genere":"Racing","anno":2017}'
```

#### 5. DELETE - Elimina videogioco specifico
```http
DELETE /api/catalogo/{id}
```

**Esempio**:
```bash
curl -X DELETE http://localhost:8080/api/catalogo/123e4567-e89b-12d3-a456-426614174000
```

#### 6. DELETE - Svuota tutto il catalogo
```http
DELETE /api/catalogo
```

**Esempio**:
```bash
curl -X DELETE http://localhost:8080/api/catalogo
```

## Interfaccia Web

### Pagine Disponibili

#### 1. Lista Videogiochi
- **URL**: `http://localhost:8080/`
- **Funzionalità**:
  - Visualizza tutti i videogiochi in tabella
  - Ricerca per titolo
  - Link per dettagli, modifica, eliminazione
  - Pulsante per aggiungere nuovo videogioco
  - Pulsante per svuotare catalogo

#### 2. Dettagli Videogioco
- **URL**: `http://localhost:8080/item/{id}`
- **Funzionalità**:
  - Mostra informazioni complete del videogioco
  - Pulsanti per modificare o eliminare

#### 3. Nuovo Videogioco
- **URL**: `http://localhost:8080/new`
- **Funzionalità**:
  - Form per inserire nuovo videogioco
  - Validazione campi obbligatori
  - Redirect alla pagina dettagli dopo salvataggio

#### 4. Modifica Videogioco
- **URL**: `http://localhost:8080/item/edit/{id}`
- **Funzionalità**:
  - Form pre-compilato con dati esistenti
  - Modifica e salvataggio
  - Validazione campi

## Configurazione

### Database
Il database MySQL è configurato via Docker Compose:
- **Database**: `videogiochi_db`
- **Username**: `root`
- **Password**: `root`
- **Port**: `3306`

### API RAWG
- **API Key**: Configurata nel codice (da sostituire con chiave personale)
- **Endpoint**: `https://api.rawg.io/api/games`

### Applicazione
- **Porta**: `8080`
- **Contesto**: `/`
- **Database**: Auto-configurato con JPA

## Esempi di Utilizzo

### 1. Gestione Catalogo via API
```bash
# Crea un videogioco
curl -X POST http://localhost:8080/api/catalogo \
  -H "Content-Type: application/json" \
  -d '{"titolo":"Final Fantasy VII","produttore":"Square Enix","genere":"RPG","anno":1997}'

# Lista tutti i videogiochi
curl http://localhost:8080/api/catalogo

# Cerca videogiochi
curl "http://localhost:8080/api/catalogo?search=Final"

# Ottieni dati RAWG
curl http://localhost:8080/api/giochi | jq '.results[0:3]'
```

### 2. Workflow Interfaccia Web
1. Vai su `http://localhost:8080`
2. Clicca "Aggiungi Nuovo Videogioco"
3. Compila il form e salva
4. Visualizza i dettagli del videogioco creato
5. Modifica o elimina come necessario

## Risoluzione Problemi

### Porta 8080 già in uso
```bash
# Uccidi processi sulla porta 8080
lsof -ti:8080 | xargs kill -9

# Oppure cambia porta nell'application.properties
server.port=8081
```

### Database non disponibile
```bash
# Assicurati che Docker sia in esecuzione
docker-compose ps

# Riavvia il database
docker-compose restart mysql-db
```

### API RAWG non funziona
- Verifica che l'API key sia valida
- Controlla la connessione internet
- L'API RAWG potrebbe avere limiti di rate


