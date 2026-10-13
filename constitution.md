---
version: 1.0.0
ratified: 2026-10-12
last_amended: 2026-10-12
status: active
source_of_truth: .specify/memory/constitution.md
mirror: ./constitution.md
methodology: SDD
---
# Constitución — MovilSDD

> Rector SDD. Ante conflicto con otro artefacto, manda esta constitución.
> Filosofía madre: cada feature sale liberable con 00+01+02+03+04 aplicado.
> Fuera de alcance en esta etapa: publicación Play Store, FCM/push, biometría,
> multi-módulo/KMP, OTel/Prometheus full, benchmarks/chaos, Pact-real, hosting/CD.

## 1. Propósito y alcance

App base Android stateless en UI en Kotlin + Compose + Clean 3 capas + Koin.
Metodología SDD. Toda funcionalidad nace de spec en `/specs` (FT-00…FT-0X).
Objetivo: cada feature spec→model→repository→usecase→viewmodel→screen→route→DI→tests→gates→tag liberable con `assembleRelease` listo, sin publicar aún.

## 2. Principios no negociables

Stateless UI: sin estado en proceso fuera de `UiState`. Persistente solo en store cifrado/local. Efímero solo en memoria con TTL documentado — deuda documentada, se pierde al reciclar.
Seguridad primero: validar en frontera UI/UseCase, denegar por defecto, mínimo privilegio, sin secretos en código/logs/errores.
Sin estado oculto: todo compartido en DI; lo estático expone `Reset()` + lock para tests.
Fail-fast: errores uniformes vía `SealedResult`, sin tragar excepciones. Detalle solo `BuildConfig.DEBUG`.
Empírico antes que teórico: medir antes de fijar timeouts/tamaños página/umbrales. Documentar límites en `Memoria.md`, prohibido workaround sin issue.
Real-first: prohibido fake en prod. Cada slice usa implementación real + fakes solo en `test/`.

## 3. Stack obligatorio

| Capa | Tecnología |
|---|---|
| Runtime | Kotlin 2.3+ K2 / AGP 9.4 / Gradle 9.8 / JDK 21 |
| UI | Compose BOM 2026.08 + Material3 + Navigation type-safe |
| DI | Koin 4.x + anotaciones/KSP, migrable Hilt/Metro |
| Tests | JUnit5 + MockK + Turbine + coroutines-test + Compose-Test |
| Validación | Pura en UseCase, sin framework en `domain/` |
| Logs | `util/Logger` sanitizado, sin PII/token |
| SAST | CodeQL java-kotlin + Detekt + Android Lint + Ktlint |
| CI | GitHub Actions solo `ci.yml`, sin deploy |

No nuevas librerías sin evaluar licencia/mantenimiento/seguridad. Regla de excepción en FT-00.

## 4. Arquitectura

Rutas solo `@Serializable` en `ui/navigation/`, `NavHost` centralizado, graphs anidados. Sin string `{id}` manual.
JSON/modelo convención Kotlin: `data class` inmutables con defaults, sin anotaciones de serialización en `domain/`.
Sin caché entidades en UI. Cero singletons de datos en `ui/`.
Pipeline fijo por feature: Model → Repository interface → UseCase → ViewModel(State+Effect) → Screen → Route → DI → Tests.
Health build: `lint detekt ktlintCheck` + `testDebugUnitTest koverVerify` verdes. Sin metrics full — diferido a observabilidad.
Previews + `collectAsStateWithLifecycle()` obligatorios. Solo `DEBUG` expone tooling.

## 5. Código y API interna

Inglés en código. Tablas/columnas locales con prefijo + `RowVersion` como `String` opaca + decimales con precisión fija.
`IOptions`-equivalente lazy en DI si hay overrides de test. Nada de `new` para servicios clave.
Complejidad ≤10 método / ≤15 clase. `.editorconfig` `max_line_length=160` + Detekt como error.
Sin `try/catch` ad-hoc en Screen. `NotFound→UI vacía`, `Forbidden→login`, `Conflicto→409`, `Validación→mensaje`, `Error→snackbar`.
Nombres sin `_` en features. `S1135`-equivalente `ForbiddenComment` caza `TODO/FIXME/STOPSHIP`.

## 6. Seguridad DevSecOps

Build: `isMinifyEnabled+isShrinkResources`, `debuggable=false`, `allowBackup=false` + `backupRules`/`dataExtractionRules`, `cleartextTraffic=false`, timeout centralizado en DI.
Store: solo cifrado para sensible, nunca en `Bundle`, logs o `BuildConfig` debug.
Secrets: key ≥256b solo env, `keys.properties` gitignored + `keys.properties.example`, `SIGNING_*` solo CI.
Pipeline CI fijo: `validate-wrapper → static-analysis → unit-test → instrumentation-compile → security-scan → build(main)`.
Docs `docs/endpoints-internos.md + rate-matrix.md + asvs-l2-checklist.md` vivos solo si aplican, specs enlazan no duplican.

## 7. Testing y QA

Suites: Unit, Security, Database local, Contract captura serialización. Sin Mutation/Performance/Chaos obligatorios — Stryker/Kover mutation ≥80% y property-tests van nightly/opcional, no bloquean release.
Contract/Fixtures captura real con flag `CAPTURE=1`, `IsConventional`, serie en CI.
Cobertura ≥45% `koverVerify`. Gate real futuro es mutation, hoy no bloquea.
Puerta incremental advisory (no bloquea): delta-coverage `--min 80 --advisory` sobre delta `merge-base main`, higiene `Migrations/*, App/*, Dtos/*` adaptado a `data/*, di/*`.
Provider-states/test-tags gateados `EnableProviderStates+no-prod` equivalente `buildConfigField DEBUG`.

## 8. CI y release manual

CI-PR orden: `restore → build → unit → security → database → contract → critic → endpoints-internos → sdd → coverage-merge → coverage → delta-check → mutation → semgrep-codeql`, `--no-daemon Release`.
Sin CD. Release manual: `./gradlew assembleRelease bundleRelease --no-daemon` + set env `KEYSTORE_*` + verificar `lint/test/verify` + tag `vX.Y.Z-ftN`.
`CHECKLIST_PR.md` obligatorio. 1 revisor. `main` protegida.
TOML/YAML validados. Tras mutación siempre `build` antes de `--no-build`.

## 9. Observabilidad mínima

Logger sanitizado + `CorrelationId` por operación. Sin OTel/Prometheus/Grafana por ahora.
Audit local sin PII en logs. Hash-chain full diferido, no verde falso.

## 10. Memoria

`Memoria.md` por fase/slice: estado, decisiones con porqué, errores a evitar. Máx 10000 líneas. Sin datos sensibles.

## 11. Gobernanza SDD

Specs en `/specs/FT-0X` con plantilla 7 secciones + Aprobación. Sin cambio de contrato interno sin spec. `AGENTS.md` dice cómo, constitución dice qué no se negocia.
