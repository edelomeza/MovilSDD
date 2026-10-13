# Tasks — FT-04-login

> 1 PR = 1 slice. Comandos con `--no-daemon`. Orden topológico. DoD = Definition of Done.

## Fase 1: Dominio + validación

- [ ] T04.1 `domain/model/AuthModels.kt` (`LoginRequest`, `LoginResponse{token,username,expiresAtMillis}`, `UserSession`, sin `@Serializable`) + `domain/repository/AuthRepository.kt` — verif: compila, sin I/O en `domain/`.
- [ ] T04.2 `util/ValidationRules.kt` (`validateUsername`, `validatePassword` puras, tabla §2.2 spec) + unit JUnit5 — verif: `testDebugUnitTest`.
- [ ] T04.3 `domain/usecase/LoginUseCase.kt` (valida -> `login()` -> en `Success` `saveSession`) + unit MockK (válido, username inválido, password inválido, repo éxito/error red/401/5xx, sin `saveSession` en fallo) — verif: `testDebugUnitTest`.
- [ ] T04.4 `domain/usecase/LogoutUseCase.kt` (`logout()` + `clearSession` local-first aun con fallo API) + unit MockK — verif: `testDebugUnitTest`.

## Fase 2: Data remote (OpenAPI real)

- [ ] T04.5 `data/remote/dto/AuthDto.kt` (`LoginRequestDto{strNombreUsuario,strPWD}`, `LoginResponseDto{strToken,dteExpira}` con `@Serializable`/`@SerialName`) + `data/remote/AuthMappers.kt` (dto<->domain, `dteExpira` ISO-8601 -> epoch-ms; parseo inválido -> `Unexpected`) + Contract `CAPTURE=1` + `IsConventional` — verif: test contract verde.
- [ ] T04.6 `data/remote/AuthApiService.kt` (`POST api/v1/Auth/login`, `POST api/v1/Auth/logout` con `Authorization` dinámico, base URL inyectada, sin hardcodeo) + `data/remote/AuthRemoteDataSource.kt` (mapeo `HttpException`/timeout -> `SealedResult`, mensaje "No logramos conectarnos..." en red) + unit MockK — verif: `testDebugUnitTest`.

## Fase 3: Data local + sesión

- [ ] T04.7 `data/local/TokenLocalDataSource.kt` (`EncryptedSharedPreferences`: save/get/clear token/username/`expiresAtMillis`) + Database test roundtrip cifrado — verif: `testDebugUnitTest`.
- [ ] T04.8 `data/local/PreferencesLocalDataSource.kt` (DataStore solo flags no sensibles, `Flow`) + unit — verif: `testDebugUnitTest`.
- [ ] T04.9 `SessionManager` (interface) + `SessionManagerImpl` (`sessionFlow`, `saveSession`, `clearSession`, `isSessionValid = now < expiresAtMillis`, `getCurrentUser`) + unit (save/clear/valid/expirado por `dteExpira` real) — verif: `testDebugUnitTest`.
- [ ] T04.10 `data/repository/AuthRepositoryImpl.kt` (delega remote + session; `dteExpira` inválido no persiste) + unit MockK — verif: `testDebugUnitTest`.

## Fase 4: UI + navegación + DI

- [ ] T04.11 `ui/feature/login/` (`LoginUiState`, `LoginUiEvent`, `LoginViewModel` con `StateFlow`/`SharedFlow`, `onEvent`, `login()` con guard `isLoading`, mapeo a Snackbar/navegación) + tests Turbine + `runTest` — verif: `testDebugUnitTest`.
- [ ] T04.12 `ui/feature/login/LoginScreen.kt` (Material3, `OutlinedTextField` + `AccountCircle`/`Lock`/`Visibility`, `PasswordVisualTransformation`, `CircularProgressIndicator`, `enabled=!isLoading`, `collectAsStateWithLifecycle`, `PrimaryGreen` desde theme, Preview Debug) + `ui/feature/home/HomePlaceholderScreen.kt` (Bienvenido/etiqueta/logout) — verif: `assembleDebug` + `compileDebugAndroidTestSources`.
- [ ] T04.13 `ui/navigation/Routes.kt` (`LoginRoute`, `HomeRoute @Serializable`) + `NavGraph` (entry por `sessionFlow.first()`, `navigate(HomeRoute(username))`, expirada -> `LoginRoute`) + `di/LoginModule.kt` (bindings §4 plan, registro en `featureModule`/`AppModule`, lazy) + instrumented smoke arranque con/sin/expirada — verif: `assembleDebug`.

## Fase 5: Gates + docs

- [ ] T04.14 Security (`SanitizationTest` token/password, token solo cifrado, Bearer solo HTTPS) + Contract + Database tests finales — verif: `testDebugUnitTest koverVerify` (>= 45% global, >= 80% nuevo).
- [ ] T04.15 Gates (`lint detekt ktlintCheck`, `testDebugUnitTest koverVerify`, `assembleRelease bundleRelease`, `critic-guardrails` PASS, `check_endpoints-internos` OK) + docs vivos (`endpoints-internos`, `rate-matrix`, `asvs-l2`) + `Memoria.md` + `CHECKLIST_PR.md` — verif: todo verde, Aprobación conciliada.
