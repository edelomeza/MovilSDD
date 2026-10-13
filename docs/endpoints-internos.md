# endpoints-internos.md — vivo

> Canónico (§6). Specs enlazan, no duplican. Gate: `endpoints-validator` → OK.

Sin endpoints internos en la arquitectura base: no hay red, no hay backend, no hay IPC.
El primer contrato interno nace con la primera entidad (spec obligatoria).

| Endpoint interno | Origen → Destino | Auth | Rate | Estado |
|---|---|---|---|---|
| N/A (base sin red) | — | — | timeout 60s (`di/CoreModule.AppConfig`) | OK 2026-10-12 |
