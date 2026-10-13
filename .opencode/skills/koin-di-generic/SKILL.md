---
name: koin-di-generic
description: DI Koin modular con single factory viewModel sin acoplar domain ni ui. Usar al registrar feature o auditar dependencias.
---

## Overview
Composicion por responsabilidad, migrable a Hilt/Metro sin tocar `domain/` ni `ui/`.

## Reglas
- `single` estado compartido, `factory` operaciones/fuentes, `viewModel` presentacion. Tipos explicitos.
- Modulos por capa: `coreModule`, `dataModule`, `featureModule`. Lazy si hay overrides de test; nada de config eager.
- Config (timeouts 60s, tamanos pagina, flags) en DI/`BuildConfig`, nunca literal en `*.kt`. Nada de `new` para servicios clave.
- API real: `single<AuthApiService>` con base URL inyectada (nunca literal); `single<SessionManager>` + fuentes local/remote; header `Authorization` dinámico (ver `api-client-integration`, `auth-session-management`).
- `common/Page<T>` + validadores auto-registrados por reflexion (desde primera entidad).
- Single-instance documentado (sin Redis/AWS en esta etapa).

## Cuando usar
- Registrar feature nueva, dividir `AppModule`, auditar dependencias, registrar API/SessionManager.

## Deny-list
Sin secretos ni firma/entorno en codigo.
