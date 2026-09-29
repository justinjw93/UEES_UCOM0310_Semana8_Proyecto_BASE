# Auditoría técnica de calidad y trazabilidad

**Objetivo:** comprobar que el proyecto no solo funciona, sino que también puede revisarse y reproducirse, y aplicar al menos una mejora respaldada por evidencia.

**Rama auditada:** `release/candidato-final` (creada a partir de `feature/integracion-api`).

---

## 1. Auditoría funcional

| Criterio | Resultado | Evidencia |
|---|---|---|
| La aplicación inicia | ✅ | `Started ReservasApplication` en el puerto 8080. |
| Los endpoints principales responden | ✅ | salud, puede-cancelar, crear y buscar por id (ver `evidencias/demo-salida.txt`). |
| Los errores conocidos están documentados | ✅ | Sección "Limitaciones" de este documento y de `04_EVIDENCIAS_Y_REFLEXION.md`. |
| La demostración es repetible | ✅ | Script `scripts/demo.sh`: ejecuta siempre las mismas 8 llamadas y muestra el código HTTP de cada una. |

## 2. Auditoría de pruebas

Comando: `mvn clean test` → **22 pruebas, 0 fallos, BUILD SUCCESS**.

| Criterio | Resultado | Ejemplos |
|---|---|---|
| Casos normales | ✅ | Crear reserva, buscar reserva existente, cancelar con 2 horas. |
| Límites | ✅ | 0, 1, 2 y 3 horas; horas negativas rechazadas con `400`. |
| Excepciones | ✅ | Id nulo o vacío, reserva inexistente (`404`), POST con datos incompletos (`400`). |
| Pruebas de Service y Controller | ✅ | `ReservaServiceTest` (8), `ReservaControllerTest` (8) y además `ReservaTest` del dominio (6). |

## 3. Auditoría JaCoCo

Reporte: `target/site/jacoco/index.html` (copias en CSV en `docs/evidencias/`).

| Elemento | Hallazgo |
|---|---|
| **Clase/método** | `Reserva` → constructor; también `Reserva.confirmar()`, `Reserva.cancelar()` y `ReservaService.confirmar()`. |
| **Rama no cubierta** | 3 de las 6 decisiones del constructor nunca se ejecutaban: id nulo, id en blanco y tipo nulo. |
| **Riesgo** | Son justamente las reglas que protegen la integridad de una reserva. Si alguien las modificara por error, ninguna prueba lo detectaría. |
| **Mejora** | Pruebas unitarias del dominio (`ReservaTest`) y de `confirmar()` en el Service, además de los límites 0 y 3 horas. |

## 4. Mejoras aplicadas (antes / después)

### Mejora 1: cubrir el hueco detectado por JaCoCo

| Métrica | Antes | Después |
|---|---|---|
| Pruebas | 13 | 22 |
| Ramas cubiertas | 5 / 8 (62,5 %) | 8 / 8 (100 %) |
| Líneas cubiertas | 35 / 45 (77,8 %) | 47 / 49 (95,9 %) |
| Instrucciones cubiertas | 83,3 % | 97,5 % |

Lo único que queda sin cubrir es el método `main` que arranca la aplicación; eso es habitual y no representa riesgo.

Fuente: `docs/evidencias/jacoco-antes.csv` y `docs/evidencias/jacoco-despues.csv`.

### Mejora 2: corregir el problema real "reserva no encontrada"

Este problema se detectó en la actividad anterior y quedó documentado como pendiente.

**Antes:** el Service lanzaba un error genérico que nadie traducía, y la API respondía como si el servidor hubiera fallado.

```text
$ curl http://localhost:8080/api/reservas/NO-EXISTE
{"status":500,"error":"Internal Server Error", ...}   [HTTP 500]
```

**Después:** el cliente recibe un mensaje claro que indica que el recurso no existe.

```text
$ curl http://localhost:8080/api/reservas/NO-EXISTE
{"title":"Not Found","status":404,"detail":"Reserva no encontrada: NO-EXISTE", ...}   [HTTP 404]
```

**Cómo se resolvió, sin cargar el Controller:**

- `ReservaNoEncontradaException`: un error propio del negocio que reemplaza al genérico `IllegalArgumentException`.
- `ManejadorErrores` (`@RestControllerAdvice`): un único lugar que convierte ese error en `404`.
- Se actualizaron las pruebas del Service y del Controller para verificar el nuevo comportamiento.

## 5. Auditoría Git

Comandos revisados: `git status`, `git log --oneline --decorate -15` y `git branch`.

| Criterio | Resultado | Evidencia |
|---|---|---|
| No hay `target/` | ✅ | Está en `.gitignore`; `git ls-files` no muestra archivos compilados. |
| No hay secretos | ✅ | No hay contraseñas, llaves, tokens ni archivos `.env`. El proyecto no requiere credenciales. |
| Commits descriptivos | ✅ | Prefijos `feat`, `test`, `fix`, `docs` y `release`, cada uno con un solo propósito. |
| README actualizado | ✅ | Incluye endpoints, errores, demostración y documentación. |
| Rama final identificable | ✅ | `release/candidato-final`. |
| Archivos accidentales | ✅ | Solo código, pruebas, documentación y el script de demostración. |

**Historial:**

```text
release: preparar candidato final
fix: responder 404 cuando la reserva no existe
test: cubrir ramas del dominio y limites de la regla de cancelacion
docs: diagnostico, evidencias y reflexion del laboratorio
test: cubrir regla de cancelacion y endpoints de reservas
feat: exponer api de reservas con validacion de horas
chore: proyecto base semana 8
```

**Ramas:**

```text
main                       base del proyecto
feature/integracion-api    Actividad 2: integración de la API (Pull Request #1)
release/candidato-final    Actividad 3: auditoría y versión candidata
```

**Trazabilidad:** cada hallazgo puede seguirse hasta su commit. Por ejemplo, el hueco de JaCoCo se cubre en el commit `test: cubrir ramas del dominio…` y el error 500 se corrige en `fix: responder 404…`.

## 6. Conclusión de calidad

El proyecto está listo como versión candidata:

- La aplicación inicia y responde correctamente.
- Las 22 pruebas pasan y cubren el 100 % de las decisiones del código.
- El historial de Git permite reconstruir qué se hizo y por qué.

La auditoría fue útil porque los números de JaCoCo señalaron reglas del dominio que nadie verificaba. Además, un error que el usuario veía como "falla del servidor" ahora comunica claramente lo que ocurre. Ambas mejoras se hicieron sin mover lógica al Controller, así que la separación Controller → Service → Domain se mantiene.

## 7. Limitaciones

1. **Reservas duplicadas:** crear una reserva con un id que ya existe reemplaza la anterior sin avisar. Mejora sugerida: responder `409 Conflict`.
2. **Datos en memoria:** se pierden al reiniciar la aplicación.
3. **Tipo libre:** el campo `tipo` acepta cualquier texto; no existe una lista de tipos válidos.
4. **Operaciones incompletas en la API:** confirmar y cancelar existen en el sistema, pero aún no se ofrecen como endpoints.
5. **Sin control de acceso:** cualquier cliente puede crear o consultar reservas. Es aceptable en un laboratorio, pero no en producción.
