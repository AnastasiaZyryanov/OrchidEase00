#  OrchidEase

##  Descrizione

**OrchidEase** è un'app Android pensata per aiutare gli appassionati di orchidee a gestire la cura delle proprie piante.  
L'app include:

-  Un **catalogo** informativo con dettagli sulla cura di diverse varietà di orchidee.
-  Una sezione personale **giardino** dove l'utente può aggiungere orchidee dalla propria collezione.
-  Un **calendario interattivo** per monitorare eventi legati alla cura (innaffiature, fertilizzazioni, ecc.).
-  **Notifiche automatiche** tramite AlarmManager per ricordare le attività di manutenzione.

Testato su **Android Studio Meerkat**.

---

## Come avviare il progetto

1. Aprire il progetto in **Android Studio Meerkat** (o versione successiva).
2. Verificare di avere installati:
   - **Kotlin 1.9.22**
   - **Gradle 8.1.0**
   - **Android Gradle Plugin 8.2.2**
3. Premere `Run` (▶️ o `Shift + F10`) per avviare l'app su emulatore o dispositivo reale.

---

## Struttura del progetto

### `data/`

- `local/` — database locale Room (orchidee personali).
- `remote/` — accesso a Supabase (catalogo con immagini) tramite API REST (usando Ktor).
- `domain/model/` — modelli di dominio condivisi tra livelli.

### `ui/`

- `calendar/` — calendario personalizzato con eventi.
- `catalog/` — visualizzazione del catalogo delle orchidee.
- `garden/` — gestione delle orchidee personali dell'utente.
- `home/` — schermata iniziale e riepilogo.
- `nav/` — navigazione con Compose.
- `receiver/` — gestione dei broadcast (per le notifiche).
- `theme/` — tema dell'app.
- `MainActivity.kt` — attività principale.

---

## Notifiche

- Implementate con **AlarmManager**.
- Le notifiche vengono attivate per ricordare eventi come innaffiatura e fertilizzazione, sulla base delle date inserite dall'utente.

---

## Note aggiuntive

- L'app non richiede login: tutti i dati dell'utente vengono salvati localmente, con accesso in sola lettura al catalogo da Supabase.
- Le cartelle temporanee (`build/`, `.gradle/`, `.idea/`, ecc.) non sono incluse nell'archivio.
- I dati delle orchidee personali sono archiviati in Room con possibilità di modifica, eliminazione e aggiunta immagini (fino a 5 per pianta).

---

## Autrice

Sviluppato come progetto individuale del corso **Laboratorio di programmazione per sistemi mobili  tablet** da Anastasia Zyryanova, studentessa di laurea Informatica presso l’Università di Trento.
