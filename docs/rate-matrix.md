# rate-matrix.md — vivo

> Límites centrales (§6, `AGENTS.md` §6). Empírico antes que teórico: medir antes de fijar.

| Operación | Timeout | Reintentos | Página | Evidencia |
|---|---|---|---|---|
| Default (`AppConfig.networkTimeoutSeconds`) | 60s | 0 | — | Fijado por constitución, sin medición aún |
| `Page.defaultPageSize` | — | — | 20 | Valor inicial, medir con primera entidad |

Deuda: medir timeout real y tamaño de página con la primera entidad (issue).
