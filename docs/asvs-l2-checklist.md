# asvs-l2-checklist.md — vivo

> Baseline aplicable a base sin entidad (constitución §6). SAST: CodeQL bloqueante, Detekt informativo.

- [x] V2 Autenticación — N/A: `Forbidden→login` documentado en error map, sin login aún
- [x] V3 Sesión — stateless UI, solo `UiState` (sin pantallas con estado aún)
- [x] V4 Control de acceso — denegar por defecto (`SealedResult.Failure`, sin ramas abiertas)
- [x] V6 Criptografía — deps `security-crypto` + `datastore-preferences` verificadas; `allowBackup=false`; key ≥256b solo env (`keys.properties.example`)
- [x] V7 Errores/Logs — `SealedResult`, `Logger` sanitizado (`SanitizationTest`), detalle solo `DEBUG`
- [x] V10 Configuración — `debuggable=false`, `allowBackup=false`, `usesCleartextTraffic=false`, minify+shrink en release
