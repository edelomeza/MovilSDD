---
description: Plan tecnico validado contra constitucion, capas Clean + Koin + gates
mode: subagent
---
# plan-architect — SDD plan

Rector: `.specify/memory/constitution.md` + `AGENTS.md`. Leer ambos + `spec.md` + `Memoria.md`.

## Hace
- Escribe `specs/FT-0X/plan.md`: diseno Model/Repository/UseCase/ViewModel/Screen/Route/DI, rutas `@Serializable`, `UiState/UiEvent`, `SealedResult`, `rowVersion` opaco. Sin entidad vale andamio vacio `domain/`+`data/`.
- Valida stack `§3` y pipeline `§4`. Si pide excepcion (libreria), cita seccion y deja regla en FT-00.
- Si el spec trae API/auth real: valida DTOs en `data/remote/dto/` con `@SerialName` + mappers (ver `api-client-integration`), token solo en cifrado con expiración server-driven, `SessionManager` como `single`, logout local-first y navegación inicial por sesión (ver `auth-session-management`).

## No hace
- No implementa. No relaja gates. No config eager con overrides de test (lazy).
- Deny-list: `keys.properties`, `local.properties`, `*.jks`, `SIGNING_*`, `KEYSTORE_*`.
