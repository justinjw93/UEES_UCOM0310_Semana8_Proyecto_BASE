# Guía de defensa del proyecto final (Ae7)

## 1. Problema y objetivo (1–2 min)

El sistema de reservas tenía sus reglas de negocio dentro del código, pero nadie podía usarlas desde afuera. El objetivo fue **publicarlas como una API REST** sin mezclar responsabilidades, y demostrar que el resultado es confiable (pruebas), medible (cobertura) y trazable (Git).

## 2. Arquitectura (2–3 min)

```
Cliente HTTP
   │
   ▼
ReservaController      → recibe la petición, valida su forma y responde con el código HTTP
   │                     ManejadorErrores traduce errores del negocio a 404 / 409
   ▼
ReservaService         → reglas de negocio (cancelación con 2 h mínimas, no duplicar reservas)
   │
   ├──► Reserva (Domain) → protege su propio estado (id obligatorio, PENDIENTE → CONFIRMADA / CANCELADA)
   ▼
ReservaRepository      → interfaz; hoy implementada en memoria
```

Cada capa depende solo de la siguiente. El Controller no conoce las reglas y el Service no conoce HTTP.

## 3. Patrones y refactorización (2 min)

### Patrones utilizados

| Patrón | Dónde | Problema que resuelve |
|---|---|---|
| Arquitectura en capas | `api` / `service` / `domain` / `repository` | Cada parte cambia por un solo motivo. |
| Repository | `ReservaRepository` + `ReservaRepositoryMemoria` | El Service no sabe dónde se guardan los datos; se puede cambiar a una base de datos sin tocarlo. |
| Inyección de dependencias | Constructores del Controller y del Service | Bajo acoplamiento; permite pasar un *mock* en las pruebas. |
| DTO | `CrearReservaRequest` | Separa lo que llega por HTTP de la entidad del dominio y concentra la validación de entrada. |
| Manejador global de errores | `ManejadorErrores` (`@RestControllerAdvice`) | Un solo lugar traduce errores del negocio a códigos HTTP, sin llenar el Controller de condicionales. |

**Patrón evitado:** *Factory* o *Strategy* para los tipos de reserva. Hoy el tipo es solo un texto sin comportamiento distinto, así que agregar jerarquías sería complejidad sin beneficio. Se justificaría si cada tipo tuviera reglas propias.

### Refactorizaciones relevantes

| Antes | Después | Beneficio |
|---|---|---|
| `IllegalArgumentException` genérica → respuesta `500`. | `ReservaNoEncontradaException` + manejador → `404`. | El error expresa el negocio y el cliente recibe una respuesta correcta. |
| Número fijo `2` dentro de `puedeCancelar`. | Constante `HORAS_MINIMAS_CANCELACION`. | La regla tiene nombre y se cambia en un solo lugar; las pruebas la reutilizan. |
| Un POST con id repetido sobrescribía la reserva. | `ReservaDuplicadaException` → `409 Conflict`. | Se evita la pérdida silenciosa de datos. |
| "Buscar y luego guardar" en dos pasos: dos POST simultáneos podían crear la misma reserva. | `guardarSiNoExiste()` en una sola operación atómica (`putIfAbsent`). | El control de duplicados funciona también con peticiones concurrentes. |

## 4. Demostración funcional (3–4 min)

```bash
mvn spring-boot:run
bash scripts/demo.sh
```

| Llamada | Resultado esperado |
|---|---|
| `GET /salud` | 200 – "API activa" |
| `GET /puede-cancelar?horas=2` / `horas=1` | 200 – `true` / `false` |
| `GET /puede-cancelar?horas=-3` | 400 |
| `POST /api/reservas` con `R-001` | 201 |
| `POST` con id vacío | 400 |
| `POST` repetido con `R-001` | 409 |
| `GET /api/reservas/R-001` | 200 |
| `GET /api/reservas/NO-EXISTE` | 404 |

## 5. Pruebas y cobertura (2 min)

- `mvn clean test` → **27 pruebas en verde**, en 4 niveles: dominio (6), repositorio (3, incluida una prueba de concurrencia), Service (9) y Controller con MockMvc (9).
- JaCoCo: **100 % de ramas**, 96 % de líneas y 98 % de instrucciones. Lo único sin cubrir es el método `main` que arranca la aplicación.
- **Interpretación:** el 100 % de ramas significa que se probaron todas las decisiones del código (sí/no), no que el sistema esté libre de errores. La cobertura mide qué se ejecutó, no si la regla es la correcta.

## 6. Git y trazabilidad (1 min)

```bash
git log --oneline --graph --decorate
```

- Ramas: `feature/integracion-api` → `release/candidato-final` → `feature/entrega-final` → `fix/creacion-atomica`, cada una integrada a `main` mediante un Pull Request (#1 a #4).
- Commits con prefijos (`feat`, `test`, `fix`, `refactor`, `docs`, `release`) y un solo propósito cada uno.
- Versión final etiquetada como `v1.0.1`.

## 7. Limitaciones y cierre (1–2 min)

1. Los datos viven en memoria y se pierden al reiniciar.
2. El campo `tipo` acepta cualquier texto.
3. Confirmar y cancelar no están expuestos como endpoints.
4. No hay autenticación ni control de acceso.

**Siguiente iteración:** persistencia con base de datos (JPA), un enum de tipos válidos, endpoints para confirmar y cancelar, y seguridad básica.

---

## Preguntas posibles y respuestas de apoyo

| Pregunta | Respuesta breve |
|---|---|
| ¿Qué decisión de diseño fue más importante? | Mantener el Controller sin reglas de negocio. Gracias a eso, la regla de cancelación se prueba sin levantar el servidor y puede cambiar sin tocar la API. |
| ¿Qué patrón resolvió un problema concreto? | El manejador global de errores: convirtió un `500` engañoso en un `404` claro sin modificar el Controller. |
| ¿Qué patrón evitaron y por qué? | Strategy/Factory para los tipos de reserva, porque hoy no tienen comportamiento distinto. |
| ¿Qué refactorización redujo deuda técnica? | Reemplazar la excepción genérica por excepciones del negocio, y el número fijo por una constante con nombre. |
| ¿Qué caso de prueba protege el mayor riesgo? | `creacionesSimultaneasConMismoIdSoloGuardanUna`: 20 hilos intentan crear la misma reserva a la vez y solo una se guarda; evita la pérdida silenciosa de datos. |
| ¿Qué significa la cobertura obtenida? | Que todas las decisiones del código se ejecutaron en alguna prueba. No garantiza ausencia de errores; por eso también probamos límites y excepciones. |
| ¿Qué evidencia Git demuestra la evolución? | El grafo de ramas y los PR #1–#4, donde cada hallazgo de la auditoría tiene su propio commit. |
| ¿Qué mejorarían en una siguiente iteración? | Persistencia real, tipos válidos, endpoints de confirmar/cancelar y autenticación. |
