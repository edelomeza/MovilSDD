---
name: auth-session-management
description: Gestionar autenticación completa con SessionManager Koin, token cifrado y navegación por sesión. Usar al implementar login, logout, persistencia de sesión o arranque con sesión vigente.
---

## Overview

Sesión como `single` Koin (interface + impl), nunca singleton estático. Persistente solo en store cifrado; reactiva vía `Flow`.

## SessionManager

```kotlin
interface SessionManager {
    val sessionFlow: Flow<UserSession?>
    suspend fun saveSession(response: LoginResponse)
    suspend fun clearSession()
    suspend fun isSessionValid(): Boolean
    suspend fun getCurrentUser(): UserSession?
}
data class UserSession(val username: String, val token: String, val expiresAtMillis: Long)
```

- `saveSession`: guarda token + username + `expiresAtMillis` (server-driven desde `dteExpira`, nunca 24h hardcodeado).
- `isSessionValid`: `System.currentTimeMillis() < expiresAtMillis`.
- `sessionFlow`: emite `UserSession?` actual; arranque lo lee con `first()` para decidir ruta sin parpadeo.

## Almacenamiento

- Sensible (token, username, `expiresAtMillis`) -> `EncryptedSharedPreferences` (`TokenLocalDataSource`). Nunca DataStore plano, `Bundle`, logs ni `BuildConfig`.
- Flags no sensibles -> DataStore Preferences (`PreferencesLocalDataSource`).
- `clearSession()` borra todo; se llama en expiración y logout.

## Flujos

- Login: `LoginUseCase` valida (puro) -> `AuthRepository.login()` -> en `Success` `saveSession()` -> ViewModel emite `OnLoginSuccess(username)` -> `navigate(HomeRoute(username))`.
- Logout local-first: `LogoutUseCase` llama `POST logout` con `Bearer` y después `clearSession()` aunque la API falle; Screen ejecuta `(context as? Activity)?.finishAffinity()` ante el evento. I/O Android solo en UI, nunca en `domain/`.
- Arranque: `sessionFlow.first()` -> vigente a `HomeRoute(username)`, ausente/expirada a `LoginRoute` (expirada limpia antes).
- Sin refresh token en MVP: al expirar se re-autentica. Multi-device fuera de alcance (issue).

## Reglas MovilSDD

- Koin `single<SessionManager>` con tipos explícitos, config lazy. Validación pura en UseCase. Errores vía `SealedResult`; red -> "No logramos conectarnos...". `Logger` sanitizado, sin PII/token.
- Inglés en código; UI strings en resources. `UiState`/`UiEvent`, `collectAsStateWithLifecycle()`, botón `enabled=!isLoading` + `CircularProgressIndicator`.

## Cuando usar

- Login/logout, expiración de sesión, arranque con sesión, auditoría de token cifrado.

## Deny-list

Sin singleton estático (exigiría `Reset()` + lock), sin token en DataStore plano/logs, sin `finishAffinity()` en `domain/`, sin secretos en código.
