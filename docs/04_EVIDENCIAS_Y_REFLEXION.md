# Evidencias y reflexión

## Endpoints disponibles

| Método | Ruta | Qué hace | Respuesta |
|---|---|---|---|
| GET | `/api/reservas/salud` | Verifica que la API esté activa. | `200` – "API activa" |
| GET | `/api/reservas/puede-cancelar?horas=N` | Indica si se puede cancelar con N horas de anticipación (mínimo 2). | `200` – `true`/`false`; `400` si N es negativo |
| POST | `/api/reservas` | Crea una reserva a partir de `id` y `tipo`. | `201` – reserva creada; `400` si falta un dato |
| GET | `/api/reservas/{id}` | Consulta una reserva por su id. | `200` – reserva; `500` si no existe (ver reto) |

## 1. Aplicación iniciada

```text
Tomcat started on port 8080 (http) with context path '/'
Started ReservasApplication in 1.244 seconds
```

## 2. GET funcional

```text
$ curl "http://localhost:8080/api/reservas/puede-cancelar?horas=2"
HTTP/1.1 200
true

$ curl "http://localhost:8080/api/reservas/puede-cancelar?horas=1"
HTTP/1.1 200
false

$ curl "http://localhost:8080/api/reservas/puede-cancelar?horas=-3"
HTTP/1.1 400
{"status":400,"error":"Bad Request","path":"/api/reservas/puede-cancelar"}
```

## 3. POST funcional

```text
$ curl -X POST http://localhost:8080/api/reservas -H "Content-Type: application/json" -d '{"id":"R-001","tipo":"NORMAL"}'
HTTP/1.1 201
{"id":"R-001","tipo":"NORMAL","estado":"PENDIENTE"}

$ curl -X POST http://localhost:8080/api/reservas -H "Content-Type: application/json" -d '{"id":"","tipo":"NORMAL"}'
HTTP/1.1 400
{"status":400,"error":"Bad Request","path":"/api/reservas"}
```

## 4. Pruebas en verde

```text
$ mvn clean test
Tests run: 8, Failures: 0, Errors: 0 -- in edu.uees.reservas.api.ReservaControllerTest
Tests run: 5, Failures: 0, Errors: 0 -- in edu.uees.reservas.service.ReservaServiceTest
BUILD SUCCESS
```

- **ReservaServiceTest (5):** regla de cancelación (2 h sí, 1 h no), creación, búsqueda y búsqueda fallida.
- **ReservaControllerTest (8):** salud, GET con 2 y 1 hora, horas negativas, POST correcto, POST inválido, consulta por id y id inexistente.
- **Cobertura JaCoCo:** 83 % de instrucciones y 78 % de líneas. Lo no cubierto es el método `main` y `confirmar()`, que aún no se expone por la API.

## 5. Rama y commits

```text
$ git log --oneline
docs: diagnostico, evidencias y reflexion del laboratorio
test: cubrir regla de cancelacion y endpoints de reservas
feat: exponer api de reservas con validacion de horas
chore: proyecto base semana 8
```

Rama de trabajo: `feature/integracion-api`, integrada a `main` mediante Pull Request.

## 6. Reto adicional: GET `/api/reservas/{id}`

**Con una reserva existente:**

```text
$ curl http://localhost:8080/api/reservas/R-001
HTTP/1.1 200
{"id":"R-001","tipo":"NORMAL","estado":"PENDIENTE"}
```

**Con un id que no existe (comportamiento actual):**

```text
$ curl http://localhost:8080/api/reservas/NO-EXISTE
HTTP/1.1 500
{"status":500,"error":"Internal Server Error","path":"/api/reservas/NO-EXISTE"}
```

El Service lanza `IllegalArgumentException("Reserva no encontrada")`, pero nadie convierte ese error en una respuesta HTTP adecuada. Por eso el cliente recibe un **500**, que sugiere una falla del servidor cuando en realidad el problema es que el recurso no existe.

**Mejora propuesta:**

1. Crear una excepción propia del negocio, por ejemplo `ReservaNoEncontradaException`, para no mezclarla con otros errores de argumentos.
2. Agregar un manejador global (`@RestControllerAdvice`) que la traduzca a **404 Not Found** con un mensaje claro.
3. Usar el mismo manejador para dar mensajes más descriptivos en los errores `400` de validación.
4. Actualizar la prueba `buscarReservaInexistentePropagaExcepcion` para que espere un `404`.

Así el Controller sigue siendo delgado y el manejo de errores queda en un solo lugar.

## 7. Reflexión sobre la separación de responsabilidades

Separar el sistema en Controller, Service y Domain hizo que cada parte tenga un solo motivo para cambiar:

- El **Controller** solo se ocupa de HTTP: recibir datos, validarlos en su forma y devolver el código de respuesta correcto. No sabe cuántas horas se necesitan para cancelar.
- El **Service** concentra las reglas del negocio, como la de las 2 horas. Si la regla cambia, se modifica en un único lugar y la API no se entera.
- El **Domain** (`Reserva`) cuida su propia consistencia: no permite reservas sin id y controla su estado.

Esta división también facilitó las pruebas: la regla de negocio se prueba sin levantar un servidor, y los endpoints se prueban por separado. Además, al depender de una interfaz de repositorio, el almacenamiento en memoria podría reemplazarse por una base de datos sin tocar el Service ni el Controller.

El reto mostró el punto débil actual: los errores del negocio todavía no tienen una traducción HTTP clara. Resolverlo con un manejador global respeta la misma separación, en lugar de llenar el Controller de condicionales.

## Limitaciones

1. Los datos se guardan en memoria y se pierden al reiniciar.
2. Un id inexistente responde `500` en lugar de `404`.
3. El campo `tipo` acepta cualquier texto; no hay una lista de tipos válidos.
4. Confirmar o cancelar una reserva todavía no está disponible por la API.
