# CHECKLIST_PR.md — Gate obligatorio (1 revisor, `main` protegida)

> Constitución §8. PR = 1 slice feature. Sin verde no hay merge.

## Pre-vuelo

- [ ] `Memoria.md` actualizado (estado, decisiones con porqué, errores a evitar)
- [ ] Spec en `specs/FT-0X/` conciliada con lo implementado (Aprobación resuelta)
- [ ] Sin secretos en código/logs/errores. `keys.properties` gitignored

## Build y análisis

- [ ] `./gradlew assembleDebug` verde antes de test
- [ ] `./gradlew lint detekt ktlintCheck --no-daemon` verde
- [ ] `./gradlew testDebugUnitTest koverVerify --no-daemon` verde (cobertura ≥45%)
- [ ] `./gradlew compileDebugAndroidTestSources --no-daemon` verde

## Testing

- [ ] Unit/Security/Contract 100% verdes (`--no-daemon`)
- [ ] Contract con `CAPTURE=1` regenerado si aplica, sin flag solo valida
- [ ] Delta-coverage `--min 80 --advisory` informado (no bloquea)

## Seguridad / SDD

- [ ] `critic-guardrails` → PASS, `check_endpoints-internos` → OK
- [ ] CodeQL/Semgrep 0 nuevos
- [ ] TOML/YAML validados
