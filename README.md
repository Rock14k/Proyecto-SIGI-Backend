# S.I.G.I — Sistema Inteligente de Gestión de Incendios

Backend en **Java 17** con **Spring Boot 3.5**, **Spring Cloud** (Eureka, Gateway, OpenFeign), **MySQL 8**, **JWT (jjwt)**, **Resilience4j**, **Swagger (springdoc)**, pruebas con **JUnit 5** y **Mockito**.  
Proyecto académico estilo **3er año Ingeniería Informática (Duoc UC)** — integrantes de referencia: **Hawk Durant**, **Emilio Jaramillo**, **Rodrigo Candia**.

## Arquitectura

- **eureka-server** (8761): registro de servicios  
- **api-gateway** (8080): entrada única, valida JWT y envía cabeceras `X-User-Id`, `X-User-Name`, `X-User-Role`  
- **servicio-usuario** (8081): registro / login / JWT (claim `userId` + `role`)  
- **servicio-reporte** (8082): reportes, Feign a ubicación (**circuit breaker**) y a emergencias  
- **servicio-ubicacion** (8083): geocodificación **OpenCage** + caché en MySQL  
- **servicio-emergencia** (8084): ciclo de vida de emergencias, Feign a recursos y notificaciones  
- **servicio-recurso** (8085): asignación de camiones/brigadas según prioridad del informe  
- **servicio-notificacion** (8086): registro de alertas por usuario  
- **servicio-empleo** (8087): avisos laborales y postulaciones ciudadanas  
- **servicio-media** (8088): subida y descarga de imágenes (reportes y perfil)  

Cada servicio tiene **su propia base de datos** lógica (`database per service`); en local usamos **un solo contenedor MySQL** con varias bases creadas por script (`db_empleo`, `db_media`, etc.).

## Requisitos

- JDK 17, Maven 3.9+  
- Docker (para `docker compose`)  
- Opcional: cuenta y API key de [OpenCage](https://opencagedata.com/) (`OPENCAGE_API_KEY`)

### Secretos compartidos

- **JWT_SECRET**: misma clave en **api-gateway** y **servicio-usuario** (variable de entorno `JWT_SECRET`).

## Ejecución local sin Docker

1. Crear en MySQL las bases `db_usuario`, `db_reporte`, `db_ubicacion`, `db_emergencia`, `db_recurso`, `db_notificacion` (o usar el script `docker/mysql-init/01-databases.sql`).  
2. Arrancar **Eureka**, luego **Gateway** y los microservicios (puertos 8081–8086).  
3. Configurar `MYSQL_HOST`, `MYSQL_PORT`, `EUREKA_HOST` si no usas valores por defecto (`localhost`, `8761`).

Pruebas unitarias (sin Docker):

```bash
cd servicio-usuario && mvn test
cd ../servicio-reporte && mvn test
# …idem otros módulos
```

## Docker Compose

```bash
# Opcional si usas geocodificación OpenCage:
# export OPENCAGE_API_KEY=tu_clave
docker compose up --build
```

- Eureka: http://localhost:8761  
- API Gateway: http://localhost:8080  
- MySQL desde tu Mac (DBeaver, etc.): **localhost:13306** (usuario `root`, contraseña por defecto `root`). El puerto **3306** del host no se usa para no chocar con un MySQL que ya tengas instalado; los contenedores siguen usando `mysql-sigi:3306` en la red interna de Docker.

- Swagger por servicio: `http://localhost:808X/swagger-ui.html` (X según puerto del servicio si lo expones; detrás del gateway solo rutas mapeadas).

### Eureka: enlaces que no cargan en el navegador

En el panel de Eureka aparecen **IP internas de Docker** (por ejemplo `172.x.x.x`) y puertos de cada microservicio. Esas URLs están pensadas para **comunicación entre contenedores** en la red de Docker. Desde el navegador de tu Mac muchas veces **no abren nada** o quedan cargando, porque esa IP no es la forma en que debes consumir la API desde fuera.

**Forma correcta de probar:** usa siempre el **API Gateway** en `http://localhost:8080` (rutas `/auth/...`, `/api/...`). Los microservicios en 8081–8086 no están publicados en el `docker-compose` actual salvo por la red interna; el único punto público pensado para el cliente es el Gateway.

### Si el contenedor `api-gateway` se cae al instante

1. Revisa el log: `docker logs api-gateway`  
2. Si dice **port already in use** para 8080, en tu Mac algo usa ese puerto: `lsof -i :8080` y cierra ese proceso o cambia el mapeo en `docker-compose.yml` (por ejemplo `"8081:8080"` en el servicio api-gateway).  
3. Si el error era **Unable to find GatewayFilterFactory with name JwtAuth**, ya está corregido en el código: el filtro debe llamarse `JwtAuthGatewayFilterFactory` para que en el YAML funcione `- JwtAuth`. Vuelve a construir: `docker compose build --no-cache api-gateway && docker compose up`.

## Flujo de prueba (curl vía Gateway)

Sustituye el token de login en `TOKEN=...`.

### 1) Registro ciudadano (Hawk Durant)

```bash
curl -s -X POST http://localhost:8080/auth/registro -H "Content-Type: application/json" -d '{
  "nombre":"Hawk","apellido":"Durant","rut":"12.345.678-9",
  "email":"hawk.durant@test.com","password":"secreta123",
  "rol":"CIUDADANO","telefono":"+56911112222"
}'
```

### 2) Login ciudadano

```bash
curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{
  "email":"hawk.durant@test.com","password":"secreta123"
}'
```

### 3) Reporte de incendio

```bash
TOKEN="...jwt..."
curl -s -X POST http://localhost:8080/api/reportes -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "descripcion":"Columna de humo detrás del colegio","direccion":"Avenida Valle del Sol 450",
  "prioridad":"ALTA"
}'
```

### 4) Registro + login operador (Emilio Jaramillo)

```bash
curl -s -X POST http://localhost:8080/auth/registro -H "Content-Type: application/json" -d '{
  "nombre":"Emilio","apellido":"Jaramillo","rut":"11.222.333-4",
  "email":"emilio.jaramillo@municipalidad.cl","password":"operador123",
  "rol":"OPERADOR_MUNICIPAL","telefono":"+56933334444"
}'

TOKEN_OP="...jwt operador..."
```

### 5) Validar reporte (crea emergencia, asigna recursos, notifica)

Primero lista pendientes: `GET http://localhost:8080/api/reportes/pendientes` con Bearer del operador.  
Luego (id de ejemplo 1):

```bash
curl -s -X PUT "http://localhost:8080/api/reportes/1/validar" \
  -H "Authorization: Bearer $TOKEN_OP" -H "Content-Type: application/json" \
  -d '{"aprobado":true,"notasOperador":"Confirmado por central — Emilio Jaramillo"}'
```

### 6) Empleos y postulaciones

```bash
# Listar avisos (ciudadano)
curl -s http://localhost:8080/api/empleos -H "Authorization: Bearer $TOKEN"

# Postular (Hawk)
curl -s -X POST http://localhost:8080/api/empleos/1/postular -H "Authorization: Bearer $TOKEN"

# Crear aviso (Rodrigo admin)
curl -s -X POST http://localhost:8080/api/empleos -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Auxiliar municipal","departamento":"Obras","plazas":2,"descripcion":"Apoyo en faenas menores","fechaCierre":"2026-12-01"}'
```

### 7) Subir imagen (reporte o perfil)

```bash
curl -s -X POST http://localhost:8080/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/ruta/foto.jpg" -F "tipo=REPORTE"

# Crear reporte con fotoMediaId devuelto
curl -s -X POST http://localhost:8080/api/reportes -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"descripcion":"Incendio con foto","direccion":"Valle del Sol 100","prioridad":"ALTA","fotoMediaId":1}'
```

## Kubernetes

Manifiestos de ejemplo en `k8s/` (1 réplica, entorno demo). Ajustar imágenes y secretos en tu cluster.

## Notas de aprendizaje

- Los **beans** `@Component` / `@Service` en Spring son en la práctica **singleton**: una sola instancia por contexto (lo menciona el informe).  
- El **Circuit Breaker** envuelve la llamada Feign a **servicio-ubicacion**; si falla, el reporte se guarda **sin coordenadas** con mensaje `"No disponible"`.
