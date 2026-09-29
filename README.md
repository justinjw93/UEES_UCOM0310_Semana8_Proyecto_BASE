# UEES UCOM0310 — Semana 8 — Laboratorio de integración Spring Boot

API REST de reservas que expone reglas del proyecto respetando la separación **Controller → Service → Domain**.

## Requisitos
- Java 21
- Maven
- Git

## Ejecutar pruebas
```bash
mvn clean test
```
Reporte de cobertura: `target/site/jacoco/index.html`

## Ejecutar aplicación
```bash
mvn spring-boot:run
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas/salud` | Estado de la API |
| GET | `/api/reservas/puede-cancelar?horas=2` | Indica si se puede cancelar (mínimo 2 horas) |
| POST | `/api/reservas` | Crea una reserva: `{"id":"R-001","tipo":"NORMAL"}` |
| GET | `/api/reservas/{id}` | Consulta una reserva por id |

## Ejemplos
```bash
curl "http://localhost:8080/api/reservas/puede-cancelar?horas=2"
curl -X POST http://localhost:8080/api/reservas -H "Content-Type: application/json" -d '{"id":"R-001","tipo":"NORMAL"}'
curl http://localhost:8080/api/reservas/R-001
```

## Estructura
```
api/         Controller y DTO (CrearReservaRequest)
service/     Reglas de negocio (ReservaService)
domain/      Reserva y EstadoReserva
repository/  Interfaz e implementación en memoria
```

## Documentación
- [Diagnóstico y plan de integración](docs/01_DIAGNOSTICO_PLANTILLA.md)
- [Auditoría final](docs/02_AUDITORIA_FINAL_PLANTILLA.md)
- [Guía de defensa](docs/03_DEFENSA_GUIA.md)
- [Evidencias y reflexión](docs/04_EVIDENCIAS_Y_REFLEXION.md)
