# FT-00 base-build — Línea base de build (retro-cierre)

> Rector: `.specify/memory/constitution.md` v1.0.0 + `AGENTS.md`. Sin "bootstrap" (término no móvil): FT-00 = cierre de línea base.

## 1. Contexto y objetivo

Fijar lo ya verde: Gradle 9.8.0, Kotlin 2.3.20, AGP 9.4.1, BOM 2026.08.00, Koin BOM 4.2.2, compileSdk 37/target 36, Java 21, `allowBackup=false` + `usesCleartextTraffic=false`. Cerrar huecos: plugins calidad sin aplicar, JUnit5 sin ejecutar, sin `ci.yml`.

## 2. Requisitos funcionales

- Detekt/Ktlint/Kover aplicados y compatibles con Kotlin 2.3.20 (o diferir con issue).
- `tasks.withType<Test> { useJUnitPlatform() }` para que Jupiter ejecute.
- `ci.yml` mínimo: `validate-wrapper → static-analysis → unit-test → instrumentation-compile → security-scan → build(main)`, sin deploy.
- `compileDebugAndroidTestSources` verde.

## 3. Requisitos no funcionales

Sin secretos; `keys.properties` gitignored + `.example`. TOML/YAML validados. Complejidad no aplica (solo build).

## 4. Arquitectura y contratos internos

Solo `gradle/`, `app/build.gradle.kts`, `.github/workflows/ci.yml`, `.editorconfig`. Sin código de app.

## 5. Criterios de aceptación

- Given sincronización When `lint detekt ktlintCheck` Then verde (o issue de diferimiento).
- Given `testDebugUnitTest` When hay test Jupiter Then ejecuta (no solo JUnit4).
- Given PR When pipeline Then `ci.yml` corre las 5 etapas.

## 6. Plan de pruebas

`assembleDebug` + `testDebugUnitTest` + `compileDebugAndroidTestSources` verdes. Sin suites nuevas.

## 7. Riesgos, límites y deuda

Detekt 1.23.8 puede no soportar Kotlin 2.3.20 → medir y diferir con issue si falla. `connectedDebugAndroidTest` requiere emulador (solo compila en CI).

## Aprobación

| Estado | Revisor | Fecha | Evidencia |
|---|---|---|---|
| ✅ Aprobada | Usuario (aceptación expresa en sesión) | 2026-10-13 | CHECKLIST_PR.md OK. Sin conflicto con constitución. Gates revalidados: assembleDebug, lint detekt ktlintCheck, testDebugUnitTest koverVerify, compileDebugAndroidTestSources, assembleRelease |
