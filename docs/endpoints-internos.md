# endpoints-internos.md — vivo

> Canónico (§6). Specs enlazan, no duplican. Gate: `endpoints-validator` → OK.

| Endpoint interno | Origen → Destino | Auth | Rate | Estado |
|---|---|---|---|---|
| `POST /api/v1/Auth/login` (`LoginRequestDto{strNombreUsuario,strPWD}` → `LoginResponseDto{strToken,dteExpira}`) | `AuthRepositoryImpl` → `AuthRemoteDataSource` → `AuthApiService` (base URL inyectada, HTTPS) | No (emite Bearer) | timeout 60s (`di/CoreModule.AppConfig`), sin reintentos | OK FT-04 |
| `POST /api/v1/Auth/logout` (sin body, `Authorization: Bearer <strToken>` → `200` sin body) | `LogoutUseCase` → `AuthRepositoryImpl` → `AuthApiService` | Sí (Bearer dinámico) | timeout 60s, limpieza local-first aun con fallo | OK FT-04 |
| Sesión local (`sessionFlow`, `saveSession`/`clearSession`/`isSessionValid`) | `LoginUseCase`/`LogoutUseCase` ↔ `SessionManager` → `TokenLocalDataSource` (cifrado) | Token en `EncryptedSharedPreferences`, nunca en logs/Bundle | N/A local | OK FT-04 |
