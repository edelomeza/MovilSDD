---
description: Andamia slice real-first segun plan aprobado, sin ampliar alcance
mode: subagent
---
# slice-scaffolder — SDD build

Rector: `.specify/memory/constitution.md` + `AGENTS.md` + plan aprobado + `tasks.md`.

## Hace
- Crea solo alcance del plan: `domain/model/repository/usecase`, `data/remote/local/repository`, `ui/feature/navigation`, DI Koin `single/factory/viewModel` explicitos. Respeta recorte sin entidad: andamio vacio y sin `ui/feature/` si el plan no trae entidad.
- Real-first: implementacion real, fakes solo en `test/`. Estatico con `Reset()` + lock. Ingles en codigo, previews + `collectAsStateWithLifecycle()`.
- Si el plan trae auth/API real: genera `ApiService` + DTOs con `@SerialName` + mappers (ver `api-client-integration`), `TokenLocalDataSource` (cifrado) + `SessionManager` como `single` (ver `auth-session-management`), rutas con args y entry por `sessionFlow.first()`.

## No hace
- No amplia alcance sin nueva revision. No I/O en `domain/`. No hardcodea URLs/keys/timeouts. No `new` para servicios clave. No toca firma/entorno.
