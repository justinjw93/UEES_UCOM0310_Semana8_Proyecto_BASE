# Auditoría final

## Funcional
- [x] Aplicación inicia
- [x] Endpoints principales responden (salud, puede-cancelar, crear, buscar por id)

## Diseño
- [x] Controller delgado: solo recibe, valida la forma de los datos y delega.
- [x] Service claro: concentra la regla de cancelación y las operaciones de reserva.
- [x] Dominio coherente: `Reserva` valida su id y controla su estado.

## Pruebas
- [x] mvn clean test exitoso (13 pruebas en verde)
- [x] JaCoCo revisado (83 % instrucciones, 78 % líneas)

## Git
- [x] Rama final: `feature/integracion-api`
- [x] Commits descriptivos
- [x] Pull Request documentado

## Limitaciones
1. Un id inexistente responde `500` en lugar de `404`; la mejora está descrita en `04_EVIDENCIAS_Y_REFLEXION.md`.
2. Los datos se guardan en memoria y se pierden al reiniciar la aplicación.
