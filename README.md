# UEES UCOM0310 — Semana 8 — Proyecto final: API de reservas

API REST de reservas que expone reglas del proyecto respetando la separación **Controller → Service → Domain**.

## Requisitos
- Java 21
- Maven
- Git

## Inicio rápido (desde cero)
```bash
git clone https://github.com/justinjw93/UEES_UCOM0310_Semana8_Proyecto_BASE.git
cd UEES_UCOM0310_Semana8_Proyecto_BASE
mvn clean test          # 27 pruebas en verde
mvn spring-boot:run     # API en http://localhost:8080
bash scripts/demo.sh    # en otra terminal: recorrido completo de la API
```

## Ejecutar pruebas
```bash
mvn clean test
```
Reporte de cobertura: `target/site/jacoco/index.html` (100 % de ramas; captura en `docs/evidencias/jacoco-reporte.png`)

## Ejecutar aplicación
```bash
mvn spring-boot:run
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas/salud` | Estado de la API |
| GET | `/api/reservas/puede-cancelar?horas=2` | Indica si se puede cancelar (mínimo 2 horas) |
| POST | `/api/reservas` | Crea una reserva: `{"id":"R-001","tipo":"NORMAL"}` (`409` si el id ya existe) |
| GET | `/api/reservas/{id}` | Consulta una reserva por id (`404` si no existe) |

## Ejemplos
```bash
curl "http://localhost:8080/api/reservas/puede-cancelar?horas=2"
curl -X POST http://localhost:8080/api/reservas -H "Content-Type: application/json" -d '{"id":"R-001","tipo":"NORMAL"}'
curl http://localhost:8080/api/reservas/R-001
```

## Demostración repetible
Con la aplicación en ejecución:
```bash
bash scripts/demo.sh
```
Ejecuta las llamadas principales y muestra el código HTTP de cada una. Salida de referencia: `docs/evidencias/demo-salida.txt`.

## Estructura
```
api/         Controller, DTO (CrearReservaRequest) y manejo de errores
service/     Reglas de negocio (ReservaService) y excepciones
domain/      Reserva y EstadoReserva
repository/  Interfaz e implementación en memoria
```

## Documentación
- [Diagnóstico y plan de integración](docs/01_DIAGNOSTICO_PLANTILLA.md)
- [Auditoría final](docs/02_AUDITORIA_FINAL_PLANTILLA.md)
- [Guía de defensa](docs/03_DEFENSA_GUIA.md)
- [Evidencias y reflexión](docs/04_EVIDENCIAS_Y_REFLEXION.md)
- [Auditoría de calidad y trazabilidad](docs/05_AUDITORIA_CALIDAD_TRAZABILIDAD.md)

## Ramas
- `main`: proyecto base.
- `feature/integracion-api`: integración de la API REST.
- `release/candidato-final`: versión candidata auditada.
- `feature/entrega-final`: ajustes para la entrega final (Ae7).
- `fix/creacion-atomica`: evita duplicados ante peticiones simultáneas.

Versión final: etiqueta `v1.0.1`.

## Limitaciones conocidas
- Los datos se guardan en memoria y se pierden al reiniciar.
- El campo `tipo` acepta cualquier texto.
- Confirmar y cancelar no están expuestos como endpoints.
- No hay autenticación.
