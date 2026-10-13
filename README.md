# MovilSDD — Android base app (Kotlin + Compose + Clean + Koin)

> Project built with Spec Driven-Development and DevSecOps best practices.
> Source of truth: `.specify/memory/constitution.md` v1.0.0 (mirror `./constitution.md`); working guide: `AGENTS.md`.
> Status: base architecture **FT-00…FT-03 ✅ Approved** (2026-10-13). First entity **FT-04-login in progress** (spec/plan/tasks creados, skills/agents actualizados).

## Stack

| Layer | Technology |
|---|---|
| Runtime | Kotlin 2.3.20 / AGP 9.4.1 / Gradle 9.8.0 / JDK 21 (target 36, compile 37) |
| UI | Compose BOM 2026.08.00 + Material3 + type-safe Navigation (`@Serializable`) |
| DI | Koin BOM 4.2.2 (`single`/`factory`/`viewModel`, lazy config) |
| Tests | JUnit5 + MockK + Turbine + coroutines-test + Compose-Test + Kover ≥45% |
| SAST | Blocking CodeQL + blocking local Detekt + Android Lint + Ktlint |

## Setup

```bash
cp keys.properties.example keys.properties   # never commit secrets
# local.properties with sdk.dir (target 36, compile 37)
./gradlew assembleDebug --no-daemon
```

## Commands

```bash
./gradlew assembleDebug --no-daemon
./gradlew lint detekt ktlintCheck --no-daemon
./gradlew testDebugUnitTest koverVerify --no-daemon
./gradlew compileDebugAndroidTestSources --no-daemon
./gradlew assembleRelease bundleRelease --no-daemon   # ready, not published
```

CI (`ci.yml`, no deploy): `validate-wrapper → static-analysis → unit-test → instrumentation-compile → security-scan → build(main)`.

## Structure

```text
app/src/main/.../common/   SealedResult, Page<T>, CorrelationId
app/src/main/.../util/     Sanitized Logger
app/src/main/.../di/       coreModule, dataModule, featureModule
app/src/main/.../ui/       navigation (Home, NavGraph) + theme
app/src/main/.../domain/ + data/   FT-04: AuthModels, AuthRepository, UseCases, DTOs, Remote/Local DataSources, SessionManager
app/src/test/.../          shared/TestDataFactory, security/, common/
specs/FT-0X/               spec.md + plan.md + tasks.md + checklists/
docs/                      endpoints-internos, rate-matrix, asvs-l2 (live)
```

## SDD components

| Component | Path | Purpose |
|---|---|---|
| Constitution (source of truth) | `.specify/memory/constitution.md` | Non-negotiable principles; wins on conflict |
| Constitution (mirror) | `./constitution.md` | Bit-for-bit copy of the source |
| Agent guide | `AGENTS.md` | How to work (constitution says what is non-negotiable) |
| Feature specs | `specs/FT-0X/{spec.md,plan.md,tasks.md,checklists/}` | 7 sections + Approval per feature; 1 PR = 1 slice |
| Templates | `.specify/templates/` + `specs/_template.md` (physical copy) | Canonical spec/plan/tasks/checklist templates |
| PR gate | `CHECKLIST_PR.md` | Mandatory checklist + 1 reviewer + protected `main` |
| Memory | `Memoria.md` | Status, decisions with rationale, errors to avoid (root only) |
| Live docs | `docs/` | Specs link, never duplicate live tables |
| Quality scripts | `scripts/` | critic, coverage, delta (stubs) |

## Subagents & skills

Convention: `general` executes, `explore` reads only. All read constitution + `AGENTS.md` + `Memoria.md` first. None stores secrets or touches signing/env files. Definitions in `.opencode/agents/*.md` (`mode: subagent`).

SDD core: `spec-drafter` (Draft spec, no code) → `plan-architect` (validated plan) → `task-decomposer` (atomic tasks) → `slice-scaffolder` (real-first build).
Quality gates (read-only PASS/FAIL): `layer-guardian`, `security-reviewer`, `endpoints-validator`, `coverage-delta-reporter`.
Build/tests: `test-writer` (JUnit5+MockK+Turbine, `TestDataFactory`).
Closeout: `traceability-clerk` (drift report), `release-checker` (manual checklist, no CD), `docs-keeper` (markdown only) + `readme-keeper` alias.

Skills in `.opencode/skills/*/SKILL.md`: `android-clean-base` (layers), `compose-mvvm-state` (`UiState/UiEvent`), `koin-di-generic` (DI), `android-testing-generic` (JUnit5+MockK+Turbine), `devsecops-gates-generic` (gates, no deploy), `contracts-docs` (live `docs/`), `store-local` (encrypted store), `mobile-app-ui-design` (UI/UX), `api-client-integration` (OpenAPI/DTOs), `auth-session-management` (auth/session). On conflict, constitution wins.

## SDD flow

1 PR = 1 slice + `CHECKLIST_PR.md` + 1 reviewer + `Memoria.md` up to date. Signed approval (`✅ Aprobada`): human reviewer + express date + `CHECKLIST_PR.md` OK + "No conflict with constitution". No secrets in code/logs/errors.

## Roadmap

First entity **FT-04-login** (Model→Repo→UseCase→VM→Screen→Route→DI→Tests→Gates), tag `v0.1.0-ft4`, mutation/CodeQL in CI.
