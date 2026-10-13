---
name: compose-mvvm-state
description: Patron Compose MVVM con UiState StateFlow y UiEvent SharedFlow para pantallas stateless. Usar al crear pantalla o refactorizar estado.
---

## Overview
Estado unidireccional testeable, sin logica en Composables. Canonico: `UiState/UiEvent` (`AGENTS.md`).

## Patron ViewModel
```kotlin
data class FeatureUiState(val items: List<Any> = emptyList(), val isLoading: Boolean = false, val error: String? = null)
sealed interface FeatureUiEvent { data object NavigateBack : FeatureUiEvent; data class Message(val text: String) : FeatureUiEvent }
val uiState: StateFlow<FeatureUiState>
val events: SharedFlow<FeatureUiEvent>
```
Nota: `UiEffect` es alias historico de `UiEvent`; usar `UiEvent`.

## Patron Screen
- `*Screen(onNavigateBack, onError, viewModel)`: `collectAsStateWithLifecycle()`, `LaunchedEffect` solo para events.
- Previews obligatorias. Solo `DEBUG` expone tooling. Sin `try/catch` ad-hoc, sin llamada directa a datos.
- Mapeo `§5`: `NotFound->UI vacia`, `Forbidden->login`, `Conflicto->409`, `Validacion->mensaje`, `Error->snackbar`.
- `rowVersion` opaco en Update/Delete. Sin cache entidades en UI.

## Navegacion
- Solo rutas `@Serializable` en `ui/navigation/`, `NavHost` centralizado, graphs anidados. Args complejos por `SavedStateHandle`.
- Auth: rutas con args (`LoginRoute(error?)`, `HomeRoute(username)`); entry del `NavHost` lee `sessionFlow.first()` para ruta inicial sin parpadeo (ver `auth-session-management`). Éxito de login navega con username; logout emite evento y solo la Screen ejecuta `finishAffinity()`.

## Cuando usar
- Nueva pantalla, refactor de estado, revision UI, navegación con sesión/auth.
