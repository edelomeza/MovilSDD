# Memoria.md — MovilSDD (única raíz)

> Vivo. Leer al empezar, actualizar al terminar tarea y al cerrar fase.
> Máx ~10000 líneas. Sin datos sensibles. Si una lección se vuelve regla, mover a `AGENTS.md`.
> Rector: `.specify/memory/constitution.md`.

## Estado

- [2026-10-12] Inicializado andamio SDD: constitución v1.0.0 + AGENTS.md + templates + stubs `docs/`/`scripts/`/`specs/`.
- [2026-10-12] Verificado: `constitution.md` espejo bit-a-bit (SHA256 igual), `specs/_template.md` copia física (SHA256 igual). 15 archivos creados.
- [2026-10-12] Integrados 12 subagentes en `.opencode/agents/` (`mode: subagent`): spec-drafter, plan-architect, task-decomposer, slice-scaffolder, layer-guardian, test-writer, security-reviewer, traceability-clerk, release-checker, docs-keeper (reemplaza readme-keeper), endpoints-validator, coverage-delta-reporter. `AGENTS.md:11` actualizado.
- [2026-10-12] FT-00…FT-03 ejecutados (plan `003-arquitectura-base` v2, sin entidad, sin "bootstrap"): `specs/FT-{00-base-build,01-nucleo,02-store-skeleton,03-gates-cierre}/` con spec/plan/tasks/checklists; código `common/util/di/ui/navigation` + andamio `domain/data`; `assembleDebug`, `lint detekt ktlintCheck`, `testDebugUnitTest koverVerify`, `compileDebugAndroidTestSources`, `assembleRelease` verdes.
- [2026-10-13] FT-00…FT-03 ✅ Aprobadas por Usuario (aceptación expresa en sesión, fecha 2026-10-13 — corrige 2026-10-11 propuesto, anterior a los specs): gates re-ejecutados exit 0, trazabilidad código↔spec OK; regla de firma en `AGENTS.md:23`.
- [2026-10-13] FT-04-login implementado en rama `LoginImplementacion` (spec/plan/tasks Borrador conciliados con OpenAPI `APISDD v1`): dominio (`AuthModels`, `AuthRepository`, `SessionManager` interface, `Login/LogoutUseCase`), data (`AuthDto`+mappers, `AuthApiService`, `AuthRemoteDataSource`, `TokenLocalDataSource` cifrado, `PreferencesLocalDataSource`, `SessionManagerImpl`, `AuthRepositoryImpl`), UI (`LoginScreen`+`LoginViewModel`, `HomePlaceholderScreen`, `LoginRoute`/`HomeRoute`), `di/LoginModule`; 88 tests verdes; gates `assembleDebug`, `lint detekt ktlintCheck`, `testDebugUnitTest koverVerify`, `compileDebugAndroidTestSources`, `assembleRelease bundleRelease` verdes; docs vivos actualizados.

## Decisiones

| Fecha | Decisión | Porqué |
|---|---|---|
| 2026-10-12 | Canónico `.specify/memory/constitution.md`, espejo `./constitution.md` | Estándar Spec-Kit, evita drift |
| 2026-10-12 | `Memoria.md` solo raíz | Decisión usuario, evita fragmentación |
| 2026-10-12 | `specs/_template.md` copia física | Git en Windows no maneja bien symlinks |
| 2026-10-12 | Stubs mínimos en `docs/`/`scripts/`/`specs/` | Evitar carpetas vacías no trackeadas por Git |
| 2026-10-12 | 7 skills en `.opencode/skills/` + `AGENTS.md:12` | 5 base ajustadas a constitución + 2 soporte (`contracts-docs`, `store-local`); `UiEffect`→`UiEvent`, recorte Espresso/SBOM a nightly |
| 2026-10-12 | FT-00: compileSdk 37/target 36 (BOM 2026.08 exige 37), `kotlin{compilerOptions{jvmTarget=21}}` (AGP9 sin `kotlinOptions`), `usesCleartextTraffic` (no `cleartextTraffic`), wrapper 9.8.0 sha `bafd5ce9…` | Empírico antes que teórico; `AGENTS.md:9` SDK 36 queda como target, compile 37 por evidencia build |
| 2026-10-12 | FT-00…FT-03: JUnit Platform 1.11.4 + vintage (Jupiter no ejecutaba sin launcher), Detekt `dev.detekt:2.0.0-alpha.3` (1.23.8 no soporta JDK 25), Ktlint-gradle 14.2.0 (12.6.1 inexistente; naming-Composables exento en `.editorconfig`, lo cubre Detekt), `Routes.kt→Home.kt`, tests camelCase, `docs/` vivos | Medir antes de fijar; clase ≤15 sin regla detekt 2.x → gate `layer-guardian`; tag pendiente (sin repo git) |
| 2026-10-13 | Hardening DevSecOps + README: `.gitignore` (keystores, env, signing/, hprof/log; duplicado `local.properties` consolidado), `AGENTS.md` (SDK target/compile, rutas tipadas, DoD FT-00…FT-03, sin `PaginationQuery` demo, Detekt bloqueante), `README.md` creado | Constitución v1.0.0 intacta (redacción genérica, sin re-ratificación) |
| 2026-10-13 | Agentes/skills al día con lo medido: 8 agentes + 3 skills actualizados, alias real `readme-keeper` creado, `README.md` en sync de `docs-keeper` | Cierra stales (Detekt, `PaginationQuery`, Aprobación 4 campos, JUnit Platform) |
| 2026-10-13 | `README.md` en inglés: descripción SDD+DevSecOps + secciones SDD components y Subagents & skills; tag pendiente corregido (repo existe desde `19b93f9`) | Docs-keeper: README en sync |
| 2026-10-13 | Tag `v0.1.0-ft3` creado y pusheado a `origin` (gates revalidados exit 0, FT-00…FT-03 ✅) | Cierra FT-03 gates-cierre; `main` aún sin protección en GitHub (activar en Settings) |
| 2026-10-13 | Repo git inicializado (`main`, root-commit `19b93f9`, 120 archivos, identidad global existente): `local.properties`/`build/` fuera, sin secretos commiteados | Tag `v0.1.0-ft3` pendiente de primera entidad con cambios |
| 2026-10-13 | FT-04: expiración server-driven (`dteExpira`→`expiresAtMillis`), DTOs ES→dominio EN con `@SerialName`, `SessionManager` interface en `domain/` (evita drift `domain→data`), logout local-first, `finishAffinity()` solo UI | OpenAPI real manda; constitución §4 (capas) y §6 (cifrado) |
| 2026-10-13 | FT-04 deps: Retrofit 2.11.0 + OkHttp 4.12.0 + converter-kotlinx 1.0.0 (Apache 2.0, Square), security-crypto 1.0.0 estable (1.1.0 depreca API sin reemplazo), `material-icons-extended` (BOM 2026.08 recorta `icons-core`), desugaring por minSdk 24 (`java.time`) | Excepción documentada a plan §5 "sin libs nuevas"; medida por resolución Maven 200 |
| 2026-10-13 | FT-04: `Logger.sanitize` ahora redacta `Bearer <token>` (regex solo cubría 1ª palabra); skills nuevos `api-client-integration` + `auth-session-management`; `critic-guardrails`/`check_endpoints-internos` siguen stubs en `scripts/` (gates cubiertos por agents) | Hallazgo `AuthSecurityTest`; deuda Kover 0.9.3 sin instrumentación (verify vacuo, issue) |
| 2026-10-13 | Run local en físico: `AUTH_BASE_URL` por buildType (debug→`https://192.168.1.76:7229/`, release→placeholder), `network_security_config.xml` con `debug-overrides` (system+user, release intacto); causa del "No logramos conectarnos": `localhost` en el teléfono + cert dev no confiable | `login network unavailable` = `IOException` sin salida a red; falta lado backend (Kestrel en LAN) + CA en el teléfono |

## Errores a evitar

- Duplicar tablas vivas fuera de `docs/`.
- TODO/FIXME sin issue (caza `ForbiddenComment`).
- Hardcodear URLs/keys/timeouts.
- Fake en `prod`; fakes solo en `test/`.
- `const val` en camelCase (ktlint exige SCREAMING_SNAKE_CASE); `return try` (ktlint exige expression body); DTOs multilínea que quepan en 160 (ktlint las colapsa).
- `SessionManager` interface en `data/` (crea dependencia `domain→data`); va en `domain/repository/`.
