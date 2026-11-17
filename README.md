# Operación Alquimia — Protocolo Savitar

Proyecto para el examen final de Construcción de Software 2. Implementa el backend que recibe la información de los 3 dispositivos de vigilancia (STAR Labs, CCPD y S.T.A.R. Satellite), calcula por trilateración la ubicación del laboratorio de Alchemy y reconstruye el mensaje interceptado por la Piedra Filosofal.

## Arquitectura

El proyecto sigue **DDD + Arquitectura Hexagonal (Puertos y Adaptadores)**, igual que NexusMarket:

```
Application/
├── domain/                     <- nucleo, sin dependencias de Spring ni HTTP
│   ├── model/                  <- Position, DeviceId, DeviceInfo, DeviceReport, TrackingResult
│   ├── exceptions/              <- UnknownDeviceException, InvalidDeviceDataException,
│   │                               InsufficientDataException, DeviceInfoNotFoundException
│   ├── ports/
│   │   ├── in/                 <- AlchemyTrackingUseCase (los 6 casos de uso)
│   │   └── out/                <- DeviceInfoRepositoryPort (contrato de persistencia)
│   └── services/                <- TrilaterationService, MessageReconstructionService,
│                                    AlchemyTrackingService (orquestador)
├── adapters/
│   ├── persistence/             <- InMemoryDeviceInfoRepositoryAdapter (Map en memoria, sin BD)
│   └── rest/                    <- AlchemyTrackingController + DTOs + GlobalExceptionHandler
├── config/                      <- DomainServicesConfig (conecta el dominio como beans de Spring)
└── OperacionAlquimiaApplication.java
```

El dominio (`domain/*`) son clases Java planas: no tienen `@RestController`, `@Component` ni anotaciones de Jackson. Solo en la capa de `adapters`/`config` se conectan con Spring. Esto cumple el requisito del enunciado: *"El dominio no debe depender de detalles de infraestructura ni del protocolo HTTP"*.

No hay base de datos: toda la información vive en un `ConcurrentHashMap` en memoria mientras el servidor esté corriendo (tal como lo exige el enunciado).

## Los 3 dispositivos y sus posiciones fijas

| Dispositivo | Identificador para la URL | Posición |
|---|---|---|
| STAR Labs | `STAR_LABS` | (-500, -200) |
| CCPD | `CCPD` | (100, -100) |
| S.T.A.R. Satellite | `STAR_SATELLITE` | (500, 100) |

Estas posiciones están fijas en el código (`DeviceId.java`) porque el enunciado dice que el cliente nunca las envía, solo la distancia medida y el fragmento de mensaje interceptado.

## Endpoints

| Requerimiento | Método | Ruta | Body |
|---|---|---|---|
| 2.1 Procesar los 3 a la vez | `POST` | `/api/devices/process` | `{"reports":[{"deviceId":"STAR_LABS","distance":...,"messageFragment":"..."}, ... x3]}` |
| 2.2 Registrar un dispositivo | `POST` | `/api/devices/{deviceId}` | `{"distance":...,"messageFragment":"..."}` |
| 2.3 Resultado acumulado | `GET` | `/api/devices/result` | — |
| 2.3 (complementario) Consultar info cruda registrada | `GET` | `/api/devices` | — |
| 2.4 Reemplazar por completo | `PUT` | `/api/devices/{deviceId}` | `{"distance":...,"messageFragment":"..."}` |
| 2.5 Actualizar parcialmente | `PATCH` | `/api/devices/{deviceId}` | `{"distance":...}` y/o `{"messageFragment":"..."}` |
| 2.6 Eliminar | `DELETE` | `/api/devices/{deviceId}` | — |

`{deviceId}` puede ser `STAR_LABS`, `CCPD` o `STAR_SATELLITE`.

### Códigos de error

| Excepción de dominio | HTTP |
|---|---|
| `UnknownDeviceException` (dispositivo no es ninguno de los 3) | 404 |
| `DeviceInfoNotFoundException` (reemplazar/actualizar/eliminar algo que no existe) | 404 |
| `InvalidDeviceDataException` (datos invalidos: distancia negativa, faltan dispositivos, etc.) | 400 |
| `InsufficientDataException` (todavia no estan los 3 dispositivos registrados) | 409 |

## Cómo correr el proyecto

```bash
./mvnw spring-boot:run
```

El servidor queda escuchando en `http://localhost:8080`.

> **Importante:** este proyecto no incluye el Maven Wrapper (`mvnw`, `mvnw.cmd`, carpeta `.mvn`). Copia esos 3 elementos desde la raíz de tu proyecto NexusM hacia la raíz de este proyecto antes de compilar — son genéricos, no dependen del proyecto.

## Fórmula de trilateración usada

Dadas 3 posiciones fijas conocidas `(x1,y1)`, `(x2,y2)`, `(x3,y3)` y las 3 distancias medidas `d1,d2,d3`, se resta la ecuación del círculo 1 a las de los círculos 2 y 3 para llegar a un sistema lineal:

```
A = 2*(x2-x1)      B = 2*(y2-y1)      C = d1²-d2²+x2²-x1²+y2²-y1²
D = 2*(x3-x1)      E = 2*(y3-y1)      F = d1²-d3²+x3²-x1²+y3²-y1²

x = (C*E - F*B) / (A*E - B*D)
y = (A*F - D*C) / (A*E - B*D)
```

Implementado en `domain/services/TrilaterationService.java`.

## Reconstrucción del mensaje

Cada dispositivo intercepta una copia del mismo mensaje, pero la interferencia de la Piedra Filosofal borra palabras distintas en cada copia (quedan como `""`). Se separa cada fragmento por espacios y, para cada posición, se toma la primera palabra no vacía entre los 3 dispositivos. Implementado en `domain/services/MessageReconstructionService.java`.
