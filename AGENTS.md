# AGENTS.md — MovilSDD

> Rector: `.specify/memory/constitution.md` manda ante conflicto. Espejo: `./constitution.md`.
> Este archivo dice cómo trabajar. La constitución dice qué no se negocia.

## 1. Forma de trabajo

> Stack: ver constitución §3. Nada fuera sin excepción documentada.
Setup: `keys.properties.example → keys.properties`; `local.properties` con SDK (target 36, compile 37 — BOM 2026.08 lo exige); `./gradlew assembleDebug`; Docker solo para mock local opcional.
Comandos: `./gradlew assembleDebug`, `./gradlew lint detekt ktlintCheck --no-daemon`, `./gradlew testDebugUnitTest koverVerify --no-daemon`, `./gradlew compileDebugAndroidTestSources --no-daemon`, `./gradlew connectedDebugAndroidTest --no-configuration-cache`, `./gradlew assembleRelease bundleRelease --no-daemon`.
CI `ci.yml`: `validate-wrapper → static-analysis → unit-test → instrumentation-compile → security-scan → build(main)`. Sin deploy.
PR = 1 slice feature; `CHECKLIST_PR.md` + 1 revisor + `Memoria.md` al día.
Naming: inglés código; `feature/*Screen+*ViewModel` (desde primera entidad), rutas `@Serializable` tipadas, `UiState/UiEvent`.
Sin secretos, sin TODO sin issue, sin imports muertos, sin código muerto.

## 2. Memoria

Plantilla canónica `.specify/templates/spec-template.md` (alias `specs/_template.md`), DoD por feature en `FT-00…FT-03` (base) y siguientes.
`AGENTS.md`: manual operativo vivo.
`Memoria.md`: estado, decisiones, lecciones por fase y diagnóstico. Actualizar al cerrar cada fase.
Al empezar, lee `Memoria.md`. Al terminar tarea, actualízalo: estado, decisiones con porqué y errores a evitar.
Al cerrar fase, concilia `spec.md/plan.md` con lo implementado y resuelve Aprobación: Borrador con evidencia, o firmado solo si el usuario indicó revisor.
Aprobación firmada (`✅ Aprobada`): exige revisor humano + fecha de aceptación expresa + `CHECKLIST_PR.md` OK + "Sin conflicto con constitución" como evidencia. Sin estos 4 campos, queda en Borrador.
Máx ~10000 líneas. Si se vuelve regla, mover a `AGENTS.md`. Nunca sensibles.

## 3. Límites — no hacer

No I/O concreto en `domain/`; solo 1 test dummy si se necesita.
No singleton estático sin `Reset()` para tests.
No hardcodear URLs/keys/timeouts.
No DI débil; `single/factory/viewModel` con tipos explícitos.
No tocar `NavHost` sin orden §4 constitución.
No relajar gates en prod; sin flags `PERF_*` en este MVP.
No config eager en DI con overrides de test — lazy.
No duplicar tablas vivas — canónico en `docs/`.
No estados saga — no existe aquí.
No `adb` sin selector de dispositivo en scripts multi-device.

## 4. Verificación por feature

`assembleDebug` verde antes de test.
Unit/Security/Contract `--no-daemon` 100% verdes.
`critic-guardrails` → PASS, `check_endpoints-internos` → OK, `koverVerify` ≥45%, delta `--min 80 --advisory` informa no bloquea, CodeQL/Semgrep 0 nuevos.
Mutación PR advisory (`since:main`, `break:0`, `continue-on-error`) + solo nightly; tras él siempre build antes de `--no-build`.
Captura `CAPTURE=1` para regenerar, sin flag solo valida.

## 5. Convenciones técnicas

Rutas `@Serializable`; UseCases + validación pura; `rowVersion` opaco en Update/Delete.
Health build + previews solo Debug. Sin metrics full.
Keys memoria solo con TTL documentado. Sensible nunca en caché plano.
DI: `common/Page<T>` + validadores auto-registrados por reflexión sin paquete extra (desde primera entidad).

## 6. Seguridad DevSecOps

Store cifrado real siempre, sin flag legacy.
Headers móviles: `cleartext=false` + pinning + `allowBackup=false`.
Rate/límites centrales + error uniforme antes de UI.
Integridad + timeout 60s + logger filtro secretos.
SAST CodeQL bloqueante; Detekt bloqueante local (`maxIssues: 0` + `detekt.yml`); Ktlint en gates; Dependabot activo. Trivy/ZAP diferidos documentados, no exigidos.

## 7. Testing quirks

JUnit5+MockK+Turbine; `runTest` para `StateFlow/SharedFlow`.
SecurityTest reuse `test/shared/TestDataFactory`.
Emulador puerto fijo en CI. Si usas screenshots separa `reports/`.
Contract `IsConventional`, no nombres impuros.

## 8. Observabilidad mínima

Logger + consola sanitizada, sin secretos. `CorrelationId` por request.
Sin OTel/Prometheus/Grafana por ahora.

## 9. Operación manual — sin hosting aún

DB/store local: migraciones + seed idempotente + backup documentado fuera de repo.
Publish listo, no publicado. Cuando haya Play se añade runbook con env + `KeysPath` + verificación.
Sin Redis: single-instance documentado. Sin AWS.

## 10. Lecciones

Fuente: `Memoria.md`. Si se vuelve regla, mover a `AGENTS.md`.

## 11. Sub-agentes

Convención: `general` ejecuta, `explore` solo lectura. Todos leen constitución + `AGENTS.md` + `Memoria.md` antes de actuar. Ninguno guarda secretos ni toca `keys.properties`, `local.properties`, `*.jks`, `SIGNING_*`, `KEYSTORE_*`.
Definiciones: `.opencode/agents/*.md` (`mode: subagent`).

Core SDD: `spec-drafter` (spec Borrador, no código) → `plan-architect` (plan validado, no implementa) → `task-decomposer` (tareas atómicas) → `slice-scaffolder` real-first sin fake, solo alcance plan.
Calidad/gate sin editar: `layer-guardian` (capas/complejidad/estilo, incluye `critic-guardrails`+`sdd-linter`) + `security-reviewer` (DevSecOps+SAST, incluye `semgrep-codeql`) + `endpoints-validator` (`check_endpoints-internos→OK`) + `coverage-delta-reporter` (`koverVerify`+delta advisory) reportan PASS/FAIL con evidencia.
Build/tests: `test-writer` (JUnit5+MockK+Turbine, `TestDataFactory`).
Cierre: `traceability-clerk` reporta drift no edita; `release-checker` checklist manual `main` sin CD; `docs-keeper` edita solo `**/*.md` (+ alias real `readme-keeper`).
En CI solo critic ligero bloqueantes 1-4.

## 12. Skills

Definiciones: `.opencode/skills/*/SKILL.md` (`name` = directorio, kebab-case).
Base: `android-clean-base` (capas), `compose-mvvm-state` (`UiState/UiEvent`), `koin-di-generic` (DI), `android-testing-generic` (JUnit5+MockK+Turbine, `CAPTURE=1`), `devsecops-gates-generic` (gates sin deploy).
Soporte: `contracts-docs` (`docs/` vivos), `store-local` (cifrado+migraciones+seed). Ante conflicto, manda constitución.

## Solution structure

| Project | Path | Type |
|---|---|---|
| app | `app/` | Application Compose `MainActivity` |
| domain | `app/src/main/.../domain/` | Puro `model/repository/usecase` |
| data | `app/src/main/.../data/` | `remote/local/repository` |
| ui | `app/src/main/.../ui/` | `feature/navigation/theme` |
| UnitTest | `app/src/test/` | JUnit5 |
| androidTest | `app/src/androidTest/` | Compose-Test |
| scripts | `scripts/` | critic, coverage, delta |
| docs | `docs/` | endpoints-internos, matrices, asvs |
