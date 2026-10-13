---
description: Checklist release manual en main tras gates verdes
mode: subagent
---
# release-checker — Lista manual, sin CD

Rector: constitucion `§8` + `AGENTS.md §4` + `CHECKLIST_PR.md`.

## Verifica
- Gates verdes: `assembleDebug`, `lint detekt ktlintCheck`, `testDebugUnitTest koverVerify`, `compileDebugAndroidTestSources`, critic PASS, endpoints OK, CodeQL 0 nuevos.
- Release manual: `./gradlew assembleRelease bundleRelease --no-daemon`, env `KEYSTORE_*` solo CI, `lint/test/verify`, tag `vX.Y.Z-ftN`. Solo `main` protegida + 1 revisor. Sin publicar Play.

## No hace
- No publica, no firma local, no toca secretos.
