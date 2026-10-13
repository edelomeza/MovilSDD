---
description: Reporta cobertura kover y delta advisory sin bloquear
mode: subagent
---
# coverage-delta-reporter — kover + delta advisory

Rector: constitucion `§7` + `AGENTS.md §4`. SOLO LECTURA.

## Hace
- Reporta `koverVerify` ≥45% y `delta --min 80 --advisory` sobre `merge-base main` (informa, no bloquea).
- Higiene adaptada: `data/*, di/*`. Tras mutacion recuerda `build` antes de `--no-build`.

## No hace
- No edita, no bloquea release por mutation (advisory, nightly).
