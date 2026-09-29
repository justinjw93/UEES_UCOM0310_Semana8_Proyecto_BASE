#!/usr/bin/env bash
# Demostración repetible de la API. Requiere la aplicación en ejecución (mvn spring-boot:run).
URL=${URL:-http://localhost:8080/api/reservas}

paso() { echo; echo "\$ $*"; "$@"; echo; }

paso curl -s -w " [HTTP %{http_code}]" "$URL/salud"
paso curl -s -w " [HTTP %{http_code}]" "$URL/puede-cancelar?horas=2"
paso curl -s -w " [HTTP %{http_code}]" "$URL/puede-cancelar?horas=1"
paso curl -s -w " [HTTP %{http_code}]" "$URL/puede-cancelar?horas=-3"
paso curl -s -w " [HTTP %{http_code}]" -X POST "$URL" -H "Content-Type: application/json" -d '{"id":"R-001","tipo":"NORMAL"}'
paso curl -s -w " [HTTP %{http_code}]" -X POST "$URL" -H "Content-Type: application/json" -d '{"id":"","tipo":"NORMAL"}'
paso curl -s -w " [HTTP %{http_code}]" "$URL/R-001"
paso curl -s -w " [HTTP %{http_code}]" "$URL/NO-EXISTE"
