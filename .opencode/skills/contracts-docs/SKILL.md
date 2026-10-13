---
name: contracts-docs
description: Tablas vivas de contratos internos rate-matrix y ASVS-L2. Usar al cambiar contrato interno o auditar docs vivos.
---

## Overview
Canonica en `docs/`. Specs enlazan, no duplican (`§6`).

## Archivos
- `docs/endpoints-internos.md`: interno origen -> destino, auth, rate.
- `docs/rate-matrix.md`: timeout 60s centralizado, reintentos, pagina. Medir antes de fijar; evidencia en `Memoria.md`.
- `docs/asvs-l2-checklist.md`: store cifrado, `allowBackup=false`, `cleartext=false`, `SealedResult`, logger sin PII.

## Reglas
- Sin cambio de contrato interno sin spec `FT-0X`. Gate `check_endpoints-internos -> OK`.
- Nunca sensibles en docs.

## Cuando usar
- Nuevo endpoint interno, cambio de limite, checklist release.
