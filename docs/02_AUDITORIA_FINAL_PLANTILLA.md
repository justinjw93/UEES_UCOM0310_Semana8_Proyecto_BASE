# Auditoría final

## Funcional
- [x] Aplicación inicia
- [x] Endpoints principales responden (salud, puede-cancelar, crear, buscar por id)

## Diseño
- [x] Controller delgado: solo recibe, valida la forma de los datos y delega.
- [x] Service claro: concentra la regla de cancelación y las operaciones de reserva.
- [x] Dominio coherente: `Reserva` valida su id y controla su estado.

## Pruebas
- [x] mvn clean test exitoso (22 pruebas en verde)
- [x] JaCoCo revisado (100 % ramas, 95,9 % líneas)

## Git
- [x] Rama final: `release/candidato-final`
- [x] Commits descriptivos
- [x] Pull Request documentado

## Limitaciones
1. Crear una reserva con un id repetido reemplaza la anterior sin avisar.
2. Los datos se guardan en memoria y se pierden al reiniciar la aplicación.

Detalle completo en `05_AUDITORIA_CALIDAD_TRAZABILIDAD.md`.
