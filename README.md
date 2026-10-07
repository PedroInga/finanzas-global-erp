# FINANZAS GLOBAL S.A.C. - ERP financiero (propuesta arquitectónica)

Curso: 4698 Desarrollo de Servicios Web II - Cibertec - Segunda Evaluación T1

Contenido del repositorio:

| Ruta | Descripción |
|------|-------------|
| `operaciones-cambio-service/` | Microservicio de ejemplo (compra/venta de divisas) con **arquitectura hexagonal**, Spring Security, JPA, pruebas JUnit/Spring Boot Test y JaCoCo |
| `.github/workflows/ci.yml` | Pipeline de **Integración Continua** con GitHub Actions |
| `docs/` | Diagramas (PNG y fuente Graphviz `.dot`) usados en el informe |

## Ejecutar localmente

Requisitos: JDK 21 y Maven 3.9+.

```bash
cd operaciones-cambio-service
mvn clean verify          # compila, ejecuta pruebas y genera cobertura JaCoCo
mvn spring-boot:run       # levanta el servicio en http://localhost:8083
```

El informe de cobertura queda en `operaciones-cambio-service/target/site/jacoco/index.html`.

## Usuarios de demostración (solo desarrollo)

| Usuario | Clave | Rol | Permisos |
|---------|-------|-----|----------|
| cajero | cajero123 | CAJERO | registrar y consultar operaciones |
| supervisor | super123 | SUPERVISOR | registrar, consultar y anular |
| auditor | audit123 | AUDITOR | solo consultar |

## Probar la API

```bash
# Registrar una compra de 1000 USD (201 Created)
curl -u cajero:cajero123 -H "Content-Type: application/json" -H "X-Office: MIRAFLORES" \
  -d '{"clienteId":1,"tipo":"COMPRA","moneda":"USD","monto":1000}' \
  http://localhost:8083/api/v1/operaciones

# El auditor intenta registrar (403 Forbidden)
curl -i -u auditor:audit123 -H "Content-Type: application/json" \
  -d '{"clienteId":1,"tipo":"COMPRA","moneda":"USD","monto":10}' \
  http://localhost:8083/api/v1/operaciones
```

Reglas de la demo: cliente `404` no existe (404), cliente `999` está inactivo (422), límite por operación 10,000 (422).

## Docker

```bash
docker build -t finanzas-global/operaciones-cambio-service operaciones-cambio-service
docker run -p 8083:8083 finanzas-global/operaciones-cambio-service
```
