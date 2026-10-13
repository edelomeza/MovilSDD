# Plan — FT-00 base-build

> Derivado de `spec.md`. Sin código de app.

## 1. Diseño

- Aplicar en `app/`: Kover + Ktlint + Detekt (versiones de catálogo; si Detekt falla con Kotlin 2.3.20, diferir con issue).
- `tasks.withType<Test> { useJUnitPlatform() }` en `app/build.gradle.kts`.
- `.github/workflows/ci.yml`: jobs `validate-wrapper → static-analysis (lint) → unit-test (testDebugUnitTest) → instrumentation-compile → security-scan (CodeQL init mínimo) → build(main, assembleDebug)`.

## 2. UI y navegación

No aplica.

## 3. DI

No aplica.

## 4. Gates

`assembleDebug` → `lint detekt ktlintCheck` → `testDebugUnitTest` → `compileDebugAndroidTestSources`. TOML/YAML validados.

## Tareas

Ver `tasks.md`.
