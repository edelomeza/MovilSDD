# Tasks Template — FT-0X

> 1 PR = 1 slice. Orden: Model → Repository → UseCase → ViewModel → Screen → Route → DI → Tests → Gates.

- [ ] Model + Repository interface (`domain/`)
- [ ] Implementación real (`data/`), sin fake en prod
- [ ] UseCase + validación pura
- [ ] ViewModel (`UiState` + Effect, `runTest` + Turbine)
- [ ] Screen + Preview + `collectAsStateWithLifecycle()`
- [ ] Route `@Serializable` + `NavHost`
- [ ] DI Koin + `Reset()` en estáticos si aplica
- [ ] Tests Unit/Security/Contract + `koverVerify` ≥45%
- [ ] `CHECKLIST_PR.md` + `Memoria.md` + tag `vX.Y.Z-ftN`
