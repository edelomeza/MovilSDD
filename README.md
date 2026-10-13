# MovilSDD — App base Android (Kotlin + Compose + Clean + Koin)

> Metodología SDD. Rector: `.specify/memory/constitution.md` v1.0.0 (espejo `./constitution.md`); cómo trabajar: `AGENTS.md`.
> Estado: arquitectura base **FT-00…FT-03 ✅ Aprobadas** (2026-10-13). Sin entidad de aplicación aún.

## Stack

| Capa | Tecnología |
|---|---|
| Runtime | Kotlin 2.3.20 / AGP 9.4.1 / Gradle 9.8.0 / JDK 21 (target 36, compile 37) |
| UI | Compose BOM 2026.08.00 + Material3 + Navigation type-safe (`@Serializable`) |
| DI | Koin BOM 4.2.2 (`single`/`factory`/`viewModel`, config lazy) |
| Tests | JUnit5 + MockK + Turbine + coroutines-test + Compose-Test + Kover ≥45% |
| SAST | CodeQL bloqueante + Detekt bloqueante local + Android Lint + Ktlint |

## Setup

```bash
cp keys.properties.example keys.properties   # nunca subir secretos
# local.properties con sdk.dir (target 36, compile 37)
./gradlew assembleDebug --no-daemon
```

## Comandos

```bash
./gradlew assembleDebug --no-daemon
./gradlew lint detekt ktlintCheck --no-daemon
./gradlew testDebugUnitTest koverVerify --no-daemon
./gradlew compileDebugAndroidTestSources --no-daemon
./gradlew assembleRelease bundleRelease --no-daemon   # listo, sin publicar
```

CI (`ci.yml`, sin deploy): `validate-wrapper → static-analysis → unit-test → instrumentation-compile → security-scan → build(main)`.

## Estructura

```text
app/src/main/.../common/   SealedResult, Page<T>, CorrelationId
app/src/main/.../util/     Logger sanitizado
app/src/main/.../di/       coreModule, dataModule, featureModule
app/src/main/.../ui/       navigation (Home, NavGraph) + theme
app/src/main/.../domain/ + data/   andamio vacío (primera entidad)
app/src/test/.../          shared/TestDataFactory, security/, common/
specs/FT-0X/               spec.md + plan.md + tasks.md + checklists/
docs/                      endpoints-internos, rate-matrix, asvs-l2 (vivos)
```

## Flujo SDD

1 PR = 1 slice + `CHECKLIST_PR.md` + 1 revisor + `Memoria.md` al día. Aprobación firmada (`✅ Aprobada`): revisor humano + fecha expresa + `CHECKLIST_PR.md` OK + "Sin conflicto con constitución". Sin secretos en código/logs/errores.

## Pendiente

Primera entidad (Model→…→DI→Tests, Database/Contract), tag `v0.1.0-ft3` (sin repo git), mutation/CodeQL en CI.
