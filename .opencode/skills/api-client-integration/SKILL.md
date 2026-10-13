---
name: api-client-integration
description: Integrar APIs REST OpenAPI reales en Clean 3 capas con DTOs SerialName, mappers y Retrofit. Usar al consumir endpoints backend, mapear respuestas o configurar cliente HTTP.
---

## Overview

`data/remote/` es la única capa con I/O de red. DTOs con `@Serializable` + `@SerialName` mapean nombres del API (ES/hungarian) a dominio en inglés sin anotaciones.

## DTOs y mappers

- DTOs en `data/remote/dto/`: `@Serializable data class` + `@SerialName("strNombreUsuario")` por campo API.
- Dominio en `domain/model/`: `data class` inmutables con defaults, sin `kotlinx.serialization`, sin `android.*`.
- Mappers en `data/remote/*Mappers.kt`: funciones puras `toDomain()` / `toDto()`, sin I/O, testeables con Contract `CAPTURE=1` + `IsConventional`.
- Fechas ISO-8601 (`dteExpira`): parseo en mapper a `expiresAtMillis: Long` (epoch-ms). Fallo de parseo = `SealedResult.Failure(DomainError.Unexpected)`, nunca persistir sesión inválida.
- Ejemplo FT-04: `LoginRequestDto{strNombreUsuario,strPWD}` <-> `LoginRequest{username,password}`; `LoginResponseDto{strToken,dteExpira}` <-> `LoginResponse{token,username,expiresAtMillis}`.

## Cliente HTTP

- `AuthApiService` (Retrofit interface): rutas relativas reales (`api/v1/Auth/login`, `api/v1/Auth/logout`), `Content-Type: application/json`.
- Base URL inyectada desde DI (`BuildConfig`/config, dev `https://localhost:7229/` solo mock local). Nunca hardcodeada en `*.kt`.
- Timeout centralizado 60s desde `CoreModule` (`docs/rate-matrix.md`). Headers dinámicos (`Authorization: Bearer <token>`) por parámetro, no interceptor con estado oculto.
- `RemoteDataSource` mapea `HttpException`/timeout/`IOException` a `SealedResult.Failure`: red -> mensaje "No logramos conectarnos...", 401/403 -> `Forbidden`, 404 -> `NotFound`, 409 -> `Conflict`, resto -> `Unexpected`.

## Reglas MovilSDD

- `ui -> domain <- data`. Sin I/O en `domain/`. Inglés en código. Complejidad <=10/<=15, `max_line_length=160`.
- `single` cliente/fuentes, `factory` operaciones. Config lazy para overrides de test.

## Cuando usar

- Nuevo endpoint backend, nuevo DTO, mapper de fechas, error HTTP, revisión de cliente API.

## Deny-list

Nunca `keys.properties`, `local.properties`, `*.jks`, `SIGNING_*`, `KEYSTORE_*`, ni URLs/keys/timeouts literales, ni token/password en logs.
