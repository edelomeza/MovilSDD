# FT-01 núcleo — Transversal + test-infra base (sin entidad)

> Rector: constitución §1–§5 + `AGENTS.md` §3–§5 §7. Sin módulo/entidad de aplicación.

## 1. Contexto y objetivo

Crear el esqueleto transversal sobre el que nacerá la primera entidad: `common/`, `util/`, `di/`, `ui/navigation/`, `MainActivity` real, test-infra base. `domain/` y `data/` quedan como andamio vacío.

## 2. Requisitos funcionales

- `common/SealedResult<T>` (éxito/error tipado), `Page<T>` genérico, `CorrelationId` opaco.
- `util/Logger` sanitizado (sin PII/token, detalle solo `BuildConfig.DEBUG`).
- `di/` Koin: `coreModule/dataModule/featureModule`, `single/factory/viewModel` explícitos, config lazy (timeout 60s).
- `ui/navigation/Routes.kt` (`@Serializable`) + `NavGraph` centralizado; `MainActivity` delega (quitar `Greeting`).
- Test-infra: `shared/TestDataFactory` genérico + `security/SanitizationTest` + 1 dummy en `domain/` (`AGENTS.md` §3).

## 3. Requisitos no funcionales

Stateless UI, real-first, inglés en código, complejidad ≤10/≤15, `max_line_length=160`. Sin I/O en `domain/`. Estáticos con `Reset()` + lock si aplican.

## 4. Arquitectura y contratos internos

`com.example.movilsdd.{common,util,di,ui/navigation}` con código; `domain/{model,repository,usecase}`, `data/{remote,local,repository}` vacíos. `ui → domain ← data`. Error map §5 documentado en `Logger`. `UiState/UiEvent`, Previews, `collectAsStateWithLifecycle()` cuando haya pantallas (aún no).

## 5. Criterios de aceptación

- Given `SealedResult` When error Then tipo sellado sin excepciones tragadas.
- Given log con PII When `Logger` Then redactado; en release sin detalle.
- Given arranque When Koin Then grafo válido con overrides lazy en tests.
- Given `NavHost` When ruta Then `@Serializable`, sin strings `{id}`.

## 6. Plan de pruebas

Unit JUnit5 (SealedResult, Page, Logger) + `SanitizationTest` + 1 dummy. `runTest` donde haya corrutinas. Sin Database/Contract (difieren con primera entidad).

## 7. Riesgos, límites y deuda

Type-safe navigation exige plugin serialization (nuevo, evaluar licencia/mantenimiento/seguridad según §3). Database/Contract pendientes.

## Aprobación

| Estado | Revisor | Fecha | Evidencia |
|---|---|---|---|
| ✅ Aprobada | Usuario (aceptación expresa en sesión) | 2026-10-13 | CHECKLIST_PR.md OK. Sin conflicto con constitución. Implementado y verde: common/util/di/navigation + tests JUnit5 |
