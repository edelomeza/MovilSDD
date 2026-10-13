---
name: android-testing-generic
description: Testing Android con JUnit5 MockK Turbine y Compose-Test con Kover. Usar al crear UseCase o ViewModel, ante gate rojo o revision de cobertura.
---

## Overview
1 test por UseCase/VM. Fakes en `test/shared/`, fixtures sin sensibles. `IsConventional` en Contract.

## Unit `src/test/`
- JUnit5 + MockK + `runTest` + Turbine para `StateFlow/SharedFlow`. Exige `useJUnitPlatform()` + launcher + vintage.
- Casos: exito, validacion, error, sesion expirada generica.
- Reutiliza `test/shared/TestDataFactory`, no duplica helpers. Max 1 test dummy en `domain/` si se necesita.

## UI `src/androidTest/`
- Compose-Test (canonico `§3`). Espresso/Roborazzi solo opcional/nightly o con excepcion FT-00.
- Store cifrado y pinning como categoria `security/`. Emulador puerto fijo en CI; screenshots en `reports/`.

## Cobertura
- `testDebugUnitTest koverVerify` umbral >=45%. `CAPTURE=1` regenera Contract, sin flag solo valida.
- Higiene: `data/*, di/*`. Delta `--min 80 --advisory` informa, no bloquea. Mutation nightly advisory.

## Cuando usar
- Nuevo UseCase/VM, gate rojo, revision de cobertura.
