# asvs-l2-checklist.md — vivo

> Baseline aplicable a base sin entidad (constitución §6). SAST: CodeQL bloqueante, Detekt informativo.

- [x] V2 Autenticación — FT-04: `POST /api/v1/Auth/login` (validación pura en UseCase, 401/403→`Forbidden`→"Credenciales inválidas", sin enumeración), `POST /api/v1/Auth/logout` con Bearer
- [x] V3 Sesión — FT-04: expiración server-driven (`dteExpira`→`expiresAtMillis`), token solo en `EncryptedSharedPreferences`, `sessionFlow` reactiva, arranque decide ruta, `clearSession` en expiración/logout
- [x] V4 Control de acceso — denegar por defecto (`SealedResult.Failure`, sin ramas abiertas)
- [x] V6 Criptografía — deps `security-crypto` + `datastore-preferences` verificadas; `allowBackup=false`; key ≥256b solo env (`keys.properties.example`)
- [x] V7 Errores/Logs — `SealedResult`, `Logger` sanitizado (FT-04: redacta `Bearer <token>`, `SanitizationTest`+`AuthSecurityTest`), detalle solo `DEBUG`; red/timeout→"No logramos conectarnos..." sin crash
- [x] V10 Configuración — `debuggable=false`, `allowBackup=false`, `usesCleartextTraffic=false`, minify+shrink en release
