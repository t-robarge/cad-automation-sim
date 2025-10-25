Parts API — Usage Guide

This document explains how to build, configure, run, and use the **Parts API** and its embedded admin UI.

---

## Overview
- Java 21 + Javalin REST API for managing mechanical parts and features (e.g., holes, slots).
- SQLite persistence via JDBC (no external DB required).
- Zero-dependency admin UI (static HTML/JS) served by the API.
- Packaged as shaded (fat) JARs for single-command run.

---

## Prerequisites
- **Java 21+**
- **Maven 3.9+**
- (Optional) `curl` and `jq` for quick API testing

---

## Build

```bash
# from repo root
mvn -DskipTests clean package
```

**Outputs (in `target/`):**
- `new-sim-…-api.jar`  → runnable API (serves the web UI)
- `new-sim-…-seed.jar` → one-shot data seeder (idempotent)

> Dev shortcuts (optional during development):  
> `mvn -q exec:java@api` and `mvn -q exec:java@seed`

---

## Configuration

Set via environment variables (or JVM `-D` properties if preferred):

- `PORT` — HTTP port (default: `7010`)
- `JDBC_URL` — SQLite JDBC URL (default: `jdbc:sqlite:./data/parts.db`)

**Examples:**
```bash
# Run on port 8080
PORT=8080 java -jar target/new-sim-0.1.0-SNAPSHOT-api.jar

# Use a custom database file
JDBC_URL="jdbc:sqlite:/absolute/path/parts.db" java -jar target/new-sim-0.1.0-SNAPSHOT-api.jar
```

**Notes**
- SQLite foreign keys are enabled per-connection (`PRAGMA foreign_keys = ON`).
- Schema uses `ON DELETE CASCADE` so deleting a part removes its features.

---

## Seed Demo Data (optional)

```bash
java -jar target/new-sim-0.1.0-SNAPSHOT-seed.jar
```
- Safe to re-run (idempotent); won’t duplicate rows.

---

## Run the API

```bash
# defaults shown
PORT=7010 JDBC_URL="jdbc:sqlite:./data/parts.db" java -jar target/new-sim-0.1.0-SNAPSHOT-api.jar
```
On startup:
```
API listening on http://localhost:7010
```

---

## Open the Web UI

Open in your browser:
```
http://localhost:7010/
```
The UI supports:
- Listing parts with feature counts
- Creating & deleting parts
- Adding & deleting features
- (If enabled) running rules for a part

---

## REST Endpoints

**Base:** `http://localhost:<PORT>`

### Health
- `GET /health` → `"ok"`

### Parts
- `GET /parts` → list all parts (with `featureCount`)
- `POST /parts` → create part  
  **Body:**
  ```json
  { "partNumber": "DEMO-PLATE", "name": "Demo Plate", "unit": "MM" }
  ```
- `GET /parts/{partNumber}` → get a single part **including** `features`
- `DELETE /parts/{partNumber}` → delete a part (features deleted via cascade)

### Features
- `POST /parts/{partNumber}/features` → add feature  
  **Body:**
  ```json
  { "type": "HOLE", "x": 25, "y": 30, "d1": 12, "d2": 0 }
  ```
- `DELETE /parts/{partNumber}/features/{featureId}` → delete feature

### Rules (optional)
- `POST /rules/run/{partNumber}` → run rules for a part
- `GET /rules/violations/{partNumber}` → list rule violations

---

## Quick Test Commands

```bash
# Health
curl -s http://localhost:7010/health

# List parts
curl -s http://localhost:7010/parts | jq .

# Create a part
curl -s -X POST http://localhost:7010/parts   -H 'Content-Type: application/json'   -d '{"partNumber":"DEMO-PLATE","name":"Demo Plate","unit":"MM"}' | jq .

# Get a part (includes features)
curl -s http://localhost:7010/parts/DEMO-PLATE | jq .

# Add a feature
curl -s -X POST http://localhost:7010/parts/DEMO-PLATE/features   -H 'Content-Type: application/json'   -d '{"type":"HOLE","x":25,"y":30,"d1":12,"d2":0}' | jq .

# Delete a feature (replace 12 with actual ID)
curl -i -X DELETE http://localhost:7010/parts/DEMO-PLATE/features/12

# Delete the part (features cascade)
curl -i -X DELETE http://localhost:7010/parts/DEMO-PLATE
```

---

## Troubleshooting

- **Port already in use** → choose a new port:
  ```bash
  PORT=7080 java -jar target/new-sim-0.1.0-SNAPSHOT-api.jar
  ```
- **Unique constraint failed: `parts.part_number`** → part exists. Delete it via API or rely on idempotent seed.
- **Seeded data not visible** → ensure seed and API use the **same** `JDBC_URL`.

---

## Project Layout (key files)

```
src/
  main/
    java/app/parts/api/ApiServer.java      # Javalin API + routes
    java/app/parts/Seed.java               # idempotent seeder
    java/app/parts/Db.java                 # JDBC + schema (SQLite)
    resources/public/index.html            # embedded admin UI (served by API)
target/
  new-sim-0.1.0-SNAPSHOT-api.jar           # runnable API jar
  new-sim-0.1.0-SNAPSHOT-seed.jar          # runnable seed jar
```
