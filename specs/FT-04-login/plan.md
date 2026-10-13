# Plan — FT-04-login

> Validado contra constitución §3–§6 + `AGENTS.md` §1–§7 + `spec.md`. Sin implementación.

## 1. Diseño (paquetes y archivos)

```
app/src/main/java/com/example/movilsdd/
├── domain/
│   ├── model/
│   │   └── AuthModels.kt            # LoginRequest, LoginResponse, UserSession (sin @Serializable)
│   ├── repository/
│   │   └── AuthRepository.kt        # interface login/logout
│   └── usecase/
│       ├── LoginUseCase.kt          # validación pura + repo + saveSession
│       └── LogoutUseCase.kt         # logout + clearSession (local-first)
├── data/
│   ├── remote/
│   │   ├── AuthApiService.kt        # POST api/v1/Auth/login, POST api/v1/Auth/logout
│   │   ├── dto/
│   │   │   └── AuthDto.kt           # LoginRequestDto, LoginResponseDto (@Serializable + @SerialName)
│   │   ├── AuthRemoteDataSource.kt  # interface + impl (mapeo, HttpException -> SealedResult)
│   │   └── AuthMappers.kt           # dto <-> domain, parseo dteExpira ISO-8601 -> epoch-ms
│   ├── local/
│   │   ├── TokenLocalDataSource.kt  # EncryptedSharedPreferences: token/username/expiresAtMillis
│   │   ├── PreferencesLocalDataSource.kt # DataStore: flags no sensibles
│   │   └── SessionManagerImpl.kt    # single Koin, sessionFlow, save/clear/isValid/getCurrent
│   └── repository/
│       └── AuthRepositoryImpl.kt    # delega remote + session
├── ui/
│   ├── feature/
│   │   └── login/
│   │       ├── LoginUiState.kt
│   │       ├── LoginUiEvent.kt
│   │       ├── LoginViewModel.kt    # StateFlow + SharedFlow, viewModelScope, runTest/Turbine
│   │       └── LoginScreen.kt       # Material3, collectAsStateWithLifecycle, Preview Debug
│   ├── feature/
│   │   └── home/
│   │       └── HomePlaceholderScreen.kt # encabezado + etiqueta + logout (FT-05 la completa)
│   ├── navigation/
│   │   ├── Routes.kt                # + LoginRoute, HomeRoute @Serializable
│   │   └── NavGraph.kt              # entry decide por sessionFlow.first()
│   └── theme/
│       └── Color.kt                 # + PrimaryGreen 0xFF88E788
├── di/
│   └── LoginModule.kt               # authApi, remote/local, SessionManager, repo, usecases, viewModel
└── util/
    └── ValidationRules.kt           # validateUsername/validatePassword puras
```

## 2. Contratos de red (OpenAPI real)

- Base URL inyectada (DI lazy, desde `BuildConfig`/config; dev `https://localhost:7229/` solo mock local). Sin hardcodeo en Service/Repository.
- `POST api/v1/Auth/login`: req `LoginRequestDto{strNombreUsuario, strPWD}`, resp `LoginResponseDto{strToken, dteExpira}`.
- `POST api/v1/Auth/logout`: sin body, `Authorization: Bearer <strToken>` dinámico, resp `200` sin body.
- JSON `application/json` con `kotlinx.serialization`; timeouts/retrofit desde `CoreModule` (60s centralizado).

## 3. UI y navegación

- `Routes.kt`: `@Serializable data class LoginRoute(val error: String? = null)`, `@Serializable data class HomeRoute(val username: String)` (convive con `Home` existente hasta FT-05).
- `NavGraph`: `NavHost` centralizado; entry point lee `sessionManager.sessionFlow.first()` -> `HomeRoute` o `LoginRoute`.
- `LoginScreen`: `OutlinedTextField` usuario/contraseña (§2.1 spec), `PasswordVisualTransformation`, `Visibility/VisibilityOff`, `CircularProgressIndicator` en botón con `enabled=false`.
- `finishAffinity()` solo en Screen ante evento logout; sin `try/catch` ad-hoc; errores -> Snackbar vía `UiEvent`.

## 4. DI (Koin, `LoginModule.kt`)

- `single<AuthApiService>` (retrofit con baseUrl inyectada + timeout 60s).
- `single<AuthRemoteDataSource>`, `single<TokenLocalDataSource>` (EncryptedSharedPreferences), `single<PreferencesLocalDataSource>` (DataStore), `single<SessionManager>`.
- `single<AuthRepository> { AuthRepositoryImpl(...) }`.
- `factory { LoginUseCase(...) }`, `factory { LogoutUseCase(...) }`, `viewModel { LoginViewModel(...) }`.
- Registro en `featureModule`/`AppModule`; config lazy para overrides de test; tipos explícitos.

## 5. Gates

`assembleDebug` -> `./gradlew lint detekt ktlintCheck --no-daemon` (0 issues) -> `./gradlew testDebugUnitTest koverVerify --no-daemon` (>= 45% global, >= 80% nuevo) -> Contract `CAPTURE=1` + `IsConventional` -> Security (`SanitizationTest`, sin token en logs) -> `./gradlew compileDebugAndroidTestSources --no-daemon` -> `./gradlew assembleRelease bundleRelease --no-daemon` -> `critic-guardrails` PASS + `check_endpoints-internos` OK.

Sin librerías nuevas: `security-crypto`, `datastore-preferences`, `serialization-json`, Koin, Turbine/MockK ya declarados (FT-00/FT-02).

## 6. Docs vivos a actualizar al cerrar (no duplicar tablas)

- `docs/endpoints-internos.md`: `POST /api/v1/Auth/login` + `POST /api/v1/Auth/logout` (req/resp reales, auth Bearer, códigos).
- `docs/rate-matrix.md`: timeout 60s + límite login.
- `docs/asvs-l2-checklist.md`: V2 (auth), V3 (sesión), V7 (errores/logging).
- `Memoria.md`: entrada FT-04 (server-driven `dteExpira`, DTO ES -> dominio EN, logout local-first).
- `CHECKLIST_PR.md`: checks del slice.

## Tareas

Ver `tasks.md`.
