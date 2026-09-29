# Auditoría final

## Funcional
- [x] Aplicación inicia
- [x] Endpoints principales responden (salud, puede-cancelar, crear, buscar por id)

## Diseño
- [x] Controller delgado: solo recibe, valida la forma de los datos y delega.
- [x] Service claro: concentra la regla de cancelación y las operaciones de reserva.
- [x] Dominio coherente: `Reserva` valida su id y controla su estado.

## Pruebas
- [x] mvn clean test exitoso (27 pruebas en verde)
- [x] JaCoCo revisado (100 % ramas, 96,4 % líneas)

## Git
- [x] Rama final: `main` con etiqueta `v1.0.1` (integra `feature/integracion-api`, `release/candidato-final`, `feature/entrega-final` y `fix/creacion-atomica`)
- [x] Commits descriptivos
- [x] Pull Request documentado

## Limitaciones
1. Los datos se guardan en memoria y se pierden al reiniciar la aplicación.
2. El campo `tipo` acepta cualquier texto y no hay autenticación.

Detalle completo en `05_AUDITORIA_CALIDAD_TRAZABILIDAD.md`.
