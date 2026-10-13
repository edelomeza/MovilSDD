# Plan — FT-02 store-skeleton

> Sin código `data/` (andamio). Agente: `docs-keeper` + `security-reviewer` (gate).

## 1. Diseño

- Verificar resolución de `security-crypto` + `datastore-preferences` (ya en `app/build.gradle.kts:62-63`).
- Revisar `backup_rules.xml` + `data_extraction_rules.xml`: excluir ficheros de store sensible.
- `docs/`: `endpoints-internos.md` (tabla vacía real N/A con evidencia: sin red), `rate-matrix.md` (timeout 60s + página por defecto 20 pendiente de medición), `asvs-l2-checklist.md` (marcar aplicables V2/V4/V6/V7/V10).

## 2. UI y navegación

No aplica.

## 3. DI

Sin cambios (puntos de extensión ya en `DataModule`).

## 4. Gates

`assembleDebug` + `endpoints-validator` OK + `security-reviewer` PASS + deuda Database registrada.

## Tareas

Ver `tasks.md`.
