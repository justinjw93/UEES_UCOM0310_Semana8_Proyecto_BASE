# Diagnóstico inicial y plan de integración

## Objetivo

Publicar una parte del sistema de reservas como un servicio web (API REST), manteniendo cada responsabilidad en su lugar:

- **Controller:** recibe la petición HTTP y devuelve la respuesta.
- **Service:** aplica las reglas del negocio.
- **Domain:** representa la reserva y su estado.

La idea central es que el Controller solo "traduzca" entre HTTP y el sistema, sin tomar decisiones de negocio.

## Diagnóstico

| Elemento | Estado | Evidencia | Riesgo | Acción |
|---|---|---|---|---|
| Diseño OO | Bueno | `Reserva` protege su estado y valida que el id no esté vacío. | Bajo | Mantener la lógica dentro del dominio. |
| Patrones | Adecuado | Repositorio definido como interfaz (`ReservaRepository`) con una versión en memoria; inyección de dependencias por constructor. | Bajo | Permite cambiar a base de datos sin tocar el Service. |
| Refactorización | Aceptable | El Controller es delgado y delega todo al Service. | Bajo | No mover reglas al Controller. |
| Pruebas | Insuficiente al inicio | Solo 2 pruebas sobre la regla de cancelación. | Medio | Agregar pruebas del Service y de los endpoints. |
| Cobertura | Sin medir al inicio | JaCoCo configurado en `pom.xml`. | Medio | Revisar el reporte tras `mvn clean test`. |
| Git / PR | Sin repositorio | El proyecto venía sin historial. | Alto | Crear repositorio, rama de trabajo, commits pequeños y Pull Request. |
| Documentación | Plantillas vacías | Carpeta `docs/` sin completar. | Medio | Registrar diagnóstico, evidencias y reflexión. |

### Hallazgos puntuales

1. El parámetro `horas` aceptaba valores negativos sin ningún control.
2. Consultar una reserva que no existe produce un **error 500** (error interno), cuando lo correcto sería indicar que no se encontró.
3. Los datos se guardan en memoria: se pierden al reiniciar la aplicación.

## Plan de integración

| Paso | Actividad | Resultado esperado |
|---|---|---|
| 1 | Ejecutar la línea base (`mvn clean test` y `mvn spring-boot:run`). | Pruebas en verde y `/salud` respondiendo. |
| 2 | Crear la rama `feature/integracion-api`. | Trabajo aislado de `main`. |
| 3 | Exponer el GET `/puede-cancelar`. | Consulta de la regla de cancelación por HTTP. |
| 4 | Exponer el POST para crear reservas usando el DTO `CrearReservaRequest`. | Respuesta `201 Created` con la reserva. |
| 5 | Validar la entrada (id y tipo obligatorios; horas no negativas). | Respuesta `400` ante datos inválidos. |
| 6 | Probar manualmente con `curl`. | Evidencia de cada respuesta. |
| 7 | Agregar pruebas automáticas (JUnit + MockMvc). | Pruebas en verde y cobertura revisada. |
| 8 | Reto: GET `/{id}` y análisis del caso "no encontrado". | Comportamiento documentado y mejora propuesta. |
| 9 | Commits incrementales y Pull Request. | Historial claro y trazable. |
