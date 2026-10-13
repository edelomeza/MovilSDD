---
name: devsecops-gates-generic
description: Gates DevSecOps con lint SAST secretos y release manual. Usar ante CI rojo, release o auditoria sin logica de negocio.
---

## Overview
Todo PR verde antes de merge. Seguridad no postergable. Solo `ci.yml`, sin deploy.

## Gates
```bash
./gradlew lint detekt ktlintCheck --no-daemon
./gradlew testDebugUnitTest koverVerify --no-daemon
./gradlew compileDebugAndroidTestSources --no-daemon
./gradlew assembleRelease bundleRelease --no-daemon
```

## Reglas
- `libs.*` + BOMs, cero versiones hardcodeadas. Actions por SHA, Dependabot activo. CodeQL bloqueante, Detekt bloqueante local (`dev.detekt:2.0.0-alpha.3`, `maxIssues: 0`), Ktlint-gradle 14.2.0, Semgrep 0 nuevos.
- Secrets solo env / `keys.properties` gitignored + `.example` / `SIGNING_*` solo CI. `.gitignore` endurecido (keystores, env, `signing/`). Gitleaks + push-protection recomendados.
- `permissions: contents:read`, timeouts centrales, `validate-wrapper` primero, `build` solo `main`.
- Build: R8 (`minify+shrink`), `debuggable=false`, `allowBackup=false` + `backupRules`, `cleartext=false` + pinning.
- Diferidos (no exigidos): Trivy/ZAP, `dependencyCheckAnalyze`, SBOM, benchmarks/chaos. Documentar si aplica.
- Tras mutacion siempre `build` antes de `--no-build`. `TOML/YAML` validados.

## Cuando usar
- CI rojo, release `assembleRelease bundleRelease`, auditoria, nuevo `docs/`.
