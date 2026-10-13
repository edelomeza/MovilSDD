# Plan — FT-03 gates-cierre

> Sin código. Agentes: `coverage-delta-reporter`, `traceability-clerk`, `release-checker`.

## 1. Diseño

- Kover: verificar reporte + umbral 45%; si el andamio vacío impide el umbral, excluir paquetes vacíos con justificación o registrar deuda.
- Delta advisory sobre `merge-base main` (sin `main` aún: documentar primera medición).

## 2. UI y navegación

No aplica.

## 3. DI

No aplica.

## 4. Gates

`testDebugUnitTest koverVerify` → delta advisory → `critic` PASS → `endpoints` OK → CodeQL/Semgrep 0 nuevos → `assembleRelease bundleRelease` → `Memoria.md` → tag `v0.1.0-ft3`.

## Tareas

Ver `tasks.md`.
