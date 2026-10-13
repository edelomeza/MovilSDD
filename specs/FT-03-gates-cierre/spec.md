# FT-03 gates-cierre — Cobertura, gates y tag liberable

> Rector: constitución §7 §8 + `AGENTS.md` §4 + `CHECKLIST_PR.md`. Sin CD.

## 1. Contexto y objetivo

Cerrar la arquitectura base como liberable: cobertura, gates SDD, artefacto release listo sin publicar, tag `v0.1.0-ft3`.

## 2. Requisitos funcionales

- `koverVerify ≥45%` verde; delta `--min 80 --advisory` informado (no bloquea).
- `critic-guardrails` PASS, `check_endpoints-internos` OK, CodeQL/Semgrep 0 nuevos.
- `./gradlew assembleRelease bundleRelease --no-daemon` listo (`KEYSTORE_*` solo CI).
- Tag `v0.1.0-ft3`, `Memoria.md` al día, specs conciliadas.

## 3. Requisitos no funcionales

Mutation nightly advisory (no bloquea). Tras mutación, `build` antes de `--no-build`. `main` protegida + 1 revisor.

## 4. Arquitectura y contratos internos

Sin código. Solo gates + `Memoria.md` + `docs/` + tag.

## 5. Criterios de aceptación

- Given `testDebugUnitTest koverVerify` Then ≥45% y 100% tests verdes.
- Given release Then artefactos firmables generados localmente, sin publicar.
- Given cierre Then `traceability-clerk` sin drift y Aprobación resuelta por FT.

## 6. Plan de pruebas

Las suites FT-00/FT-01 como evidencia. Sin suites nuevas.

## 7. Riesgos, límites y deuda

Cobertura real hoy baja (poco código): si `<45%`, registrar y subir con primera entidad o excluir andamio vacío con justificación medida.

## Aprobación

| Estado | Revisor | Fecha | Evidencia |
|---|---|---|---|
| ✅ Aprobada | Usuario (aceptación expresa en sesión) | 2026-10-13 | CHECKLIST_PR.md OK. Sin conflicto con constitución. koverVerify ≥45% + `assembleRelease` verdes; tag pendiente (sin repo git) |
