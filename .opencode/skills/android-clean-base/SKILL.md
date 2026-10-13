---
name: android-clean-base
description: Molde Clean 3 capas para feature Android Compose sin entidad concreta. Usar al crear feature desde specs/FT-0X o revisar drift de capas.
---

## Overview
Molde para cualquier feature. `domain/` puro, `data/` I/O, `ui/` presentacion, `di/` composicion.

## Arquitectura
`domain/model|repository|usecase/` -> puro, sin `android.*`, sin HTTP, sin JSON.
`data/remote|local|repository/` -> unica con I/O, mapea externo -> Model interno.
`ui/feature/Screen+ViewModel|navigation/|theme/` -> solo render + delegacion.
`di/` -> `single` estado, `factory` operaciones, `viewModel` UI.
`util/` -> helpers puros sanitizados (`Logger` sin PII/token + `CorrelationId`).
Regla: `ui -> domain <- data`.

## Reglas MovilSDD
- `model/`: `data class` inmutables con defaults, sin anotaciones de serializacion. Tablas/columnas con prefijo + `RowVersion: String` opaca + decimales precision fija.
- `repository/`: interfaces `suspend fun`, retorna Model o `Page<T>` generico.
- `usecase/`: 1 accion, `operator fun invoke(): SealedResult`, validacion pura. Fail-fast, sin tragar excepciones.
- ViewModel solo depende de usecases/interfaces, nunca de fuente concreta.
- Sin estado oculto: todo en DI; estatico expone `Reset()` + lock. Efimero solo memoria con TTL en `Memoria.md`.
- Complejidad <=10 metodo / <=15 clase, `max_line_length=160`.
- Ingles en codigo. Rutas `@Serializable`, `NavHost` centralizado.

## Cuando usar
- Nueva feature desde `specs/FT-0X/spec.md`.
- Revisar drift de capas.

## Deny-list
Nunca `keys.properties`, `local.properties`, `*.jks`, `SIGNING_*`, `KEYSTORE_*`, ni secretos en codigo/logs.
