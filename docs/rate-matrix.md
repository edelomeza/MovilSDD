# rate-matrix.md — vivo

> Límites centrales (§6, `AGENTS.md` §6). Empírico antes que teórico: medir antes de fijar.

| Operación | Timeout | Reintentos | Página | Evidencia |
|---|---|---|---|---|
| Default (`AppConfig.networkTimeoutSeconds`) | 60s | 0 | — | Fijado por constitución, sin medición aún |
| `POST /api/v1/Auth/login` + `POST /api/v1/Auth/logout` | 60s (centralizado DI) | 0 (logout local-first ante fallo) | — | FT-04, 88 tests verdes 2026-10-13 |
| `Page.defaultPageSize` | — | — | 20 | Valor inicial, medir con primera entidad |

Deuda: medir timeout real y tamaño de página con la primera entidad (issue). Kover 0.9.3 no instrumenta `testDebugUnitTest` (AGP 9.4): `verify` exit 0 vacuo, cobertura global no medible; pendiente cableado de variante (issue FT-04).
