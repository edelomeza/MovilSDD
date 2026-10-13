# FT-02 store-skeleton — Contrato de store cifrado sin tablas

> Rector: constitución §6 + `AGENTS.md` §6 §9. Sin tablas, sin migraciones, sin seed (llegan con la primera entidad).

## 1. Contexto y objetivo

Dejar verificado el contrato de persistencia: deps Security-Crypto + DataStore resuelven, reglas backup/extracción vivas, docs vivos reales. Sin escribir tablas.

## 2. Requisitos funcionales

- Deps `security-crypto` + `datastore-preferences` verificadas en `assembleDebug`.
- `backup_rules.xml` + `data_extraction_rules.xml` vivos y referenciados en manifest.
- `docs/endpoints-internos.md`, `rate-matrix.md` (timeout 60s), `asvs-l2-checklist.md` con contenido real (no stubs).
- Deuda Database/seed registrada con issue.

## 3. Requisitos no funcionales

Sensible nunca en `Bundle`/logs/`BuildConfig`. Backup documentado fuera del repo. Sin flag legacy.

## 4. Arquitectura y contratos internos

Sin código nuevo en `data/` (andamio). Specs futuras enlazan `docs/`, no duplican. Convención `RowVersion: String` opaca + prefijo + decimales fijos queda documentada.

## 5. Criterios de aceptación

- Given `assembleDebug` Then deps cifrado resuelven.
- Given manifest Then `allowBackup=false` + reglas referenciadas.
- Given spec futura Then enlaza `docs/`, no copia tablas.

## 6. Plan de pruebas

Sin suites nuevas (Database difiere). `assembleDebug` + `endpoints-validator` OK como verificación.

## 7. Riesgos, límites y deuda

Migraciones/seed/Database tests pendientes de primera entidad (issue).

## Aprobación

| Estado | Revisor | Fecha | Evidencia |
|---|---|---|---|
| ✅ Aprobada | Usuario (aceptación expresa en sesión) | 2026-10-13 | CHECKLIST_PR.md OK. Sin conflicto con constitución. `docs/` vivos + `assembleDebug` verde; deuda Database con issue |
