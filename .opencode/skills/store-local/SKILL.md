---
name: store-local
description: Store local cifrado con migraciones seed idempotente y backup fuera del repo. Usar al tocar persistencia o backup.
---

## Overview
Persistente solo en store cifrado/local. Sensible nunca en `Bundle`, logs o `BuildConfig` (`§6`).

## Reglas
- Migraciones + seed idempotente. Tablas con prefijo + `RowVersion: String` opaca + decimales precision fija.
- Backup documentado fuera del repo. Publish listo, no publicado.
- Efimero solo memoria con TTL documentado en `Memoria.md`; se pierde al reciclar.

## Cuando usar
- Nueva entidad, migracion, seed, backup, revision de cifrado.
