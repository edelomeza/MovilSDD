# Plan — FT-01 núcleo

> Pipeline §4 (recortado sin entidad). Agentes: `slice-scaffolder` + `test-writer`.

## 1. Diseño

- `common/SealedResult.kt`: `Success(data) | Failure(error: DomainError)`; `DomainError`: `NotFound | Forbidden | Conflict | Validation(msg) | Unexpected`.
- `common/Page.kt`: `data class Page<T>(items, page, pageSize, total)`.
- `common/CorrelationId.kt`: `value class` + generador UUID.
- `util/Logger.kt`: `d/i/w/e` con filtro de secretos (regex token/key/password) + `correlationId` por llamada; detalle solo `BuildConfig.DEBUG`.
- `test/shared/TestDataFactory`: strings/ids genéricos sin PII.
- `domain/` dummy único permitido.

## 2. UI y navegación

- `ui/navigation/Routes.kt`: `@Serializable object Home`.
- `ui/navigation/NavGraph.kt`: `NavHost` centralizado + `composable<Home>`.
- `MainActivity`: `MovilSDDTheme + NavGraph` (quitar `Greeting`).

## 3. DI

- `di/CoreModule.kt` (`Logger`, dispatchers/timeout 60s lazy), `di/DataModule.kt` (vacío, puntos de extensión), `di/FeatureModule.kt` (vacío), `di/AppModule.kt` (agrega).
- `MovilSddApp : Application` con `startKoin`; manifest `android:name`.

## 4. Gates

`assembleDebug` → unit JUnit5 → `SanitizationTest` → Preview compila.

## Tareas

Ver `tasks.md`.
