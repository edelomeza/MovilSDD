# FT-04-login — Inicio de sesión y flujo de autenticación

> Rector: constitución §1–§6 + `AGENTS.md` §1–§5 §7. Primera entidad de aplicación.
> Conciliado con OpenAPI `APISDD v1` (`POST /api/v1/Auth/login`, `POST /api/v1/Auth/logout`).

## 1. Contexto y objetivo

Problema: la app arranca sin sesión y necesita autenticar contra `APISDD v1`, persistir la sesión de forma cifrada y navegar a la pantalla principal sin parpadeos.

Alcance (esta feature):

- Pantalla Login en Compose + Material3 (`LoginScreen` + `LoginViewModel` con `UiState`/`UiEvent`).
- `LoginUseCase` con validación pura en `domain/` + llamada a `AuthRepository`.
- `AuthRepository` (interface en `domain/`) + `AuthRepositoryImpl` en `data/` (remote + local).
- DTOs reales del OpenAPI en `data/remote/dto/` (`LoginRequestDto{strNombreUsuario,strPWD}`, `LoginResponseDto{strToken,dteExpira}`).
- `SessionManager` como `single` Koin (interface + impl, sin singleton estático): `saveSession`, `clearSession`, `isSessionValid`, `sessionFlow: Flow<UserSession?>`.
- Expiración server-driven desde `dteExpira` (ISO-8601); sustituye el supuesto previo de 24h fijas.
- Navegación type-safe `@Serializable`: `LoginRoute` -> `HomeRoute(username)`. Arranque decide ruta según `SessionManager`.
- Logout: `POST /api/v1/Auth/logout` con `Bearer <strToken>` + limpieza local + `finishAffinity()` solo desde UI.
- Tests: Unit (UseCase, ViewModel con Turbine, Repository con MockK), Security (sin token/password en logs), Contract (`CAPTURE=1`, `IsConventional`), Database local (roundtrip cifrado + expiración).

Fuera de alcance (deuda con issue):

- Registro, recuperación de contraseña, biometría, refresh token automático, SSO, multi-device.
- `HomeScreen` completa (solo placeholder para recibir `username`; pantalla real en FT-05).
- CRUD `/api/v1/SDDUsuarios` (usa `rowVersion` opaco; llega en FT-05+).
- Endpoints `/api/v1/Ping`, `/api/v1/Probe/*` (solo soporte de diagnóstico, no parte del slice).
- Publicación Play Store, FCM, OTel/Prometheus (diferidos por constitución §1).

Filosofía liberable: `assembleRelease` listo, gates verdes, tag `v0.1.0-ft4`, sin publicar.

## 2. Requisitos funcionales

### 2.1 Pantalla Login (`ui/feature/login/`)

- Título `Text("Inicio de sesión")` (string resource, español en UI; inglés en código).
- Color primario `PrimaryGreen = Color(0xFF88E788)` definido en `ui/theme/Color.kt` y expuesto vía `MaterialTheme.colorScheme`; prohibido hardcodear en Screen.
- Campo usuario: `OutlinedTextField`, label "Usuario", `leadingIcon = Icons.Default.AccountCircle`, valor ligado a `UiState.username`.
- Campo contraseña: `OutlinedTextField`, label "Contraseña", `leadingIcon = Icons.Default.Lock`, `trailingIcon = IconButton(Visibility/VisibilityOff)` para alternar, `PasswordVisualTransformation()` por defecto, valor ligado a `UiState.password`.
- Botón "Iniciar sesión": `enabled = !uiState.isLoading`; durante carga muestra `CircularProgressIndicator` inline y bloquea doble clic.
- `LoginUiState`: `username`, `password`, `isPasswordVisible`, `isLoading`, `errorMessage: String?`.
- `LoginUiEvent`: `OnUsernameChanged`, `OnPasswordChanged`, `OnTogglePasswordVisibility`, `OnLoginClick`, `OnLoginSuccess(username)`, `ShowSnackbar(message)`.
- Screen stateless: sin estado fuera de `UiState`; usa `collectAsStateWithLifecycle()`; previews solo Debug; sin `try/catch` ad-hoc (constitución §5).

### 2.2 Validación cliente (pura en UseCase, previa a red)

El OpenAPI solo exige `strNombreUsuario` y `strPWD` como `string` requeridos, sin longitud/patrón. Línea base cliente (ajustable cuando el backend confirme reglas):

| Campo | Regla base | Mensaje (Snackbar, español sencillo) |
|---|---|---|
| username | Requerido, 3–50 chars, letras/números/`._-` | "Usuario: usa 3–50 caracteres (letras, números, punto, guion)". |
| password | Requerido, 8–128 chars | "Contraseña: usa 8–128 caracteres". |

Si falla validación: `SealedResult.Failure(DomainError.Validation(msg))`, sin llamada de red.

### 2.3 Login

- Request dominio: `LoginRequest(username, password)` (sin anotaciones de serialización, constitución §4).
- DTO red: `LoginRequestDto(strNombreUsuario, strPWD)` con `@SerialName`; mapper `domain <-> dto` en `data/`.
- Endpoint real: `POST /api/v1/Auth/login`, `Content-Type: application/json`.
- Response DTO: `LoginResponseDto(strToken, dteExpira: String ISO-8601)`; dominio: `LoginResponse(token, username, expiresAtMillis: Long)` (epoch-ms parseado del ISO; si el parseo falla se trata como `Unexpected`, nunca se guarda sesión inválida).
- `LoginUseCase`: valida -> `authRepository.login()` -> en `Success` llama `sessionManager.saveSession()` -> retorna resultado.
- Mapeo de errores a mensaje amigable (Snackbar): red/timeout/sin conexión -> "No logramos conectarnos. Por favor, verifica tu conexión a internet o inténtalo más tarde."; 401/403 -> "Credenciales inválidas"; 429 -> "Demasiados intentos. Intenta en unos minutos"; 5xx -> "Error del servidor. Intenta más tarde"; validación -> mensaje de §2.2.

### 2.4 Sesión (`SessionManager`, Koin `single`)

- Interface en `domain/` o `data/local/` según plan (contrato) + `SessionManagerImpl` en `data/local/`. Sin singleton estático (constitución §2: estático exigiría `Reset()` + lock).
- Token + username + `expiresAtMillis` en `EncryptedSharedPreferences` (Security-Crypto, FT-02). Nunca en `Bundle`, logs, `BuildConfig` ni DataStore plano.
- Flags no sensibles en DataStore Preferences.
- API: `sessionFlow: Flow<UserSession?>`, `saveSession(response)`, `clearSession()`, `isSessionValid(): Boolean` (`now < expiresAtMillis`), `getCurrentUser(): UserSession?`.
- Arranque: `sessionFlow.first()` decide ruta inicial; sesión expirada -> limpieza + `LoginRoute` sin parpadeo.

### 2.5 Navegación

- Rutas `@Serializable` en `ui/navigation/` (sin strings `{id}` manuales):
  `LoginRoute(error: String? = null)`, `HomeRoute(username: String)`.
- Éxito de login: `navigate(HomeRoute(username))`.
- `HomeRoute` muestra encabezado fijo `Bienvenido [username]!!` (arriba) y etiqueta `Usuario: [username]` (abajo-izquierda) como placeholder; pantalla completa en FT-05.

### 2.6 Logout

- `LogoutUseCase`: `authRepository.logout()` (`POST /api/v1/Auth/logout`, header `Authorization: Bearer <strToken>`) y después `sessionManager.clearSession()`.
- Política local-first: aunque la API falle (red/timeout), se limpia local y se cierra igual; se registra con `Logger` sanitizado.
- `finishAffinity()` solo en capa UI: ViewModel emite evento, Screen ejecuta `(context as? Activity)?.finishAffinity()`. Prohibido I/O Android en `domain/`.
- Botón "Cerrar Sesión" con `Icons.AutoMirrored.Filled.Logout` (en placeholder `HomeScreen` de este slice).

## 3. Requisitos no funcionales

Seguridad: token nunca en logs (`Logger.sanitize`, `SanitizationTest` lo cubre); `cleartextTraffic=false`; `allowBackup=false` + reglas FT-02; Bearer solo por HTTPS; base URL por configuración inyectada (DI lazy), nunca hardcodeada (AGENTS.md §3).

Timeouts/límites: timeout centralizado 60s desde DI (`docs/rate-matrix.md`); sin valores hardcodeados en UseCase/Repository.

Performance (a medir en device real y registrar en `Memoria.md`): arranque frío < 2s, login -> home < 500ms en local, 60fps en formulario.

Accesibilidad: `contentDescription` en iconos, labels en campos, contraste AA, target táctil >= 48dp.

Código: inglés en código; complejidad <= 10 método / <= 15 clase; `max_line_length=160`; `single/factory/viewModel` con tipos explícitos; config DI lazy con overrides de test.

## 4. Arquitectura y contratos internos

Pipeline fijo por feature: Model -> Repository interface -> UseCase -> ViewModel (State+Event) -> Screen -> Route -> DI -> Tests. `ui -> domain <- data`; sin I/O concreto en `domain/` (AGENTS.md §3).

Modelos dominio (sin anotaciones de serialización):

- `LoginRequest(username: String, password: String)`
- `LoginResponse(token: String, username: String, expiresAtMillis: Long)`
- `UserSession(username: String, token: String, expiresAtMillis: Long)`

DTOs red (`data/remote/dto/`, `@Serializable` + `@SerialName`):

- `LoginRequestDto(strNombreUsuario, strPWD)`
- `LoginResponseDto(strToken, dteExpira)`

Contratos:

- `AuthRepository { suspend fun login(request: LoginRequest): SealedResult<LoginResponse>; suspend fun logout(): SealedResult<Unit> }`
- `SessionManager { val sessionFlow: Flow<UserSession?>; suspend fun saveSession(response: LoginResponse); suspend fun clearSession(); suspend fun isSessionValid(): Boolean; suspend fun getCurrentUser(): UserSession? }`
- `LoginUseCase { suspend operator fun invoke(request: LoginRequest): SealedResult<LoginResponse> }`
- `LogoutUseCase { suspend operator fun invoke(): SealedResult<Unit> }`
- `AuthApiService`: `POST("api/v1/Auth/login")` + `POST("api/v1/Auth/logout")` con header `Authorization` dinámico.

Errores: se reutiliza `SealedResult`/`DomainError` FT-01 (`NotFound`, `Forbidden`, `Conflict`, `Validation`, `Unexpected`); `NotFound -> UI vacía`, `Forbidden -> login`, `Validación -> mensaje`, `Error -> snackbar` (constitución §5).

## 5. Criterios de aceptación

- Given app fría sin sesión When arranca Then navega a `LoginRoute`.
- Given app fría con sesión vigente (`now < expiresAtMillis`) When arranca Then navega directo a `HomeRoute(username)` sin parpadeo.
- Given sesión expirada When arranca Then limpia datos y navega a `LoginRoute`.
- Given campos vacíos When "Iniciar sesión" Then Snackbar de validación, sin llamada de red.
- Given `username`/`password` inválidos (tabla §2.2) When "Iniciar sesión" Then Snackbar específico por campo.
- Given credenciales válidas y API 200 When "Iniciar sesión" Then loading visible + botón disabled, luego navega a `HomeRoute(username)` con token cifrado guardado y `expiresAtMillis` igual al `dteExpira` del servidor.
- Given API 401/403 When login Then Snackbar "Credenciales inválidas".
- Given sin red/timeout When login Then Snackbar "No logramos conectarnos. Por favor, verifica tu conexión a internet o inténtalo más tarde.", sin crash.
- Given `isLoading=true` When doble clic Then una sola llamada (botón disabled).
- Given logout con API 200 When confirma Then limpia local + `finishAffinity()`.
- Given logout con fallo de red When confirma Then limpia local + `finishAffinity()` igual (local-first) y log sanitizado.

## 6. Plan de pruebas

Unit JUnit5 + MockK + Turbine (`runTest` para `StateFlow`/`SharedFlow`): `LoginUseCase` (validación, éxito con `saveSession`, red, 401, 5xx), `LogoutUseCase` (éxito y fallo con limpieza local), `LoginViewModel` (estado inicial, cambios de input, toggle password, loading, éxito emite navegación, error emite Snackbar), `AuthRepositoryImpl` (mapeo DTO<->dominio, `dteExpira` válido/inválido), `SessionManagerImpl` (save/clear/valid/expiry con `expiresAtMillis` server-driven). Reutiliza `test/shared/TestDataFactory`. Objetivo >= 80% en código nuevo; global `koverVerify >= 45%`.

Security: `SanitizationTest` (token/password nunca en logs), token solo en `EncryptedSharedPreferences` (nunca DataStore plano/`Bundle`/`BuildConfig`), header Bearer solo por HTTPS.

Contract con `CAPTURE=1` + `IsConventional`: serialización `LoginRequestDto` (`strNombreUsuario`, `strPWD`), `LoginResponseDto` (`strToken`, `dteExpira`), rutas `LoginRoute`/`HomeRoute`.

Database local: roundtrip cifrado token/username/`expiresAtMillis`, expiración por `dteExpira` real, `clearSession` borra todo.

Instrumented (Compose-Test): render Login (título, campos, iconos, botón), input + toggle password, loading deshabilita botón, accesibilidad básica.

## 7. Riesgos, límites y deuda

- OpenAPI sin reglas de validación (solo `string` requerido): la tabla §2.2 es línea base cliente; confirmar con backend y ajustar (issue).
- `dteExpira` como `string date-time`: el parseo ISO-8601 vive en el mapper de `data/`; fallo de parseo = `Unexpected` + sin persistencia (nunca sesión a ciegas).
- `POST /api/v1/Auth/logout` responde `200` sin body: el contrato cliente no asume payload; si el backend añade códigos (401/403) se mapean según §2.3.
- Base URL de desarrollo `https://localhost:7229/`: solo para mock local/emulador; prod por configuración inyectada (sin hardcodeo). Certificado/pinning se endurece fuera de este slice.
- Logout local-first ante fallo de red: decisión intencional (seguridad del dispositivo > consistencia servidor); revisar en FT-05.
- `HomeScreen` de este slice es placeholder (encabezado + etiqueta + logout); pantalla real en FT-05.
- Sin refresh token: al expirar `dteExpira` el usuario re-autentica.
- Actualizar al cerrar: `docs/endpoints-internos.md` (auth login/logout reales), `docs/rate-matrix.md` (timeout 60s + límite login), `docs/asvs-l2-checklist.md` (V2/V3/V7), `Memoria.md` (decisiones y deuda).

## Aprobación

| Estado | Revisor | Fecha | Evidencia |
|---|---|---|---|
| ✅ Aprobada | Usuario (aceptación expresa) | 2026-10-13 | PR #1 mergeado a `main`; CHECKLIST_PR.md OK; gates verdes (`assembleDebug`, `lint detekt ktlintCheck`, `testDebugUnitTest`, `koverVerify`, `assembleRelease`); sin conflicto con constitución |
