# PixelVault — Prototipo funcional (Alternativa 1: Arquitectura Web Desacoplada)

Este paquete contiene el código fuente para la demo en vivo del APF1:

```
PixelVault-Demo/
├── backend/     -> API REST en Java 17 + Spring Boot + PostgreSQL (JWT)
└── frontend/    -> index.html + styles.css + script.js (HTML5/CSS3/JS)
```

Arquitectura: **Frontend (HTML/CSS/JS)** ⇄ REST API ⇄ **Backend Java/Spring Boot** ⇄ **PostgreSQL**.
Corresponde a la Alternativa 1 seleccionada en el informe APF1.

---

## 0. Arranque rápido con Docker (base + backend + frontend)

```bash
cp .env.example .env      # en Windows: copy .env.example .env  (y edita las claves)
docker-compose up --build -d
```

- Frontend: http://localhost (Nginx) · API: http://localhost:8080/api/juegos
- La base espera a estar `healthy` (pg_isready) antes de arrancar el backend.
- Sin archivo `.env` se usan los valores por defecto (`admin` y el secreto JWT de desarrollo).

## Pruebas e integración continua

```bash
cd backend
mvn test        # 18 pruebas JUnit 5 + Mockito (compras, cupones, usuarios y validación de DTO)
```

`.github/workflows/ci.yml` (GitHub Actions) ejecuta `mvn verify` en cada push y Pull Request a `main`,
y construye la imagen Docker del backend; solo desde `main` la publica en GitHub Container Registry.

## 1. Requisitos previos

- **Java 17** o superior (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **PostgreSQL** instalado y corriendo (local o en la nube)
- Un navegador para abrir el frontend

## 2. Preparar la base de datos

Crea la base de datos vacía (las tablas las genera Spring Boot / Hibernate automáticamente
gracias a `spring.jpa.hibernate.ddl-auto=update`):

```sql
CREATE DATABASE pixelvault_db;
```

Edita `backend/src/main/resources/application.properties` si tu usuario/clave/host de
PostgreSQL son distintos a los valores por defecto (`postgres` / `admin` / `localhost:5432`, BD `pixelvault_db`).

> Alternativa sin instalar PostgreSQL: `docker-compose up -d db` levanta solo la base de datos con esos mismos valores.

## 3. Levantar el backend (Java / Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Al iniciar veras en consola: `PixelVault backend corriendo en http://localhost:8080/api`.
En el primer arranque, Hibernate crea las tablas (`usuarios`, `juegos`, `cupones`, `compras`,
`detalle_compra`) y `data.sql` inserta automáticamente:
- 10 videojuegos del catálogo (los mismos que ya tenías en tu `script.js` original)
- 3 cupones: `PIXEL10` (10%), `GAMER20` (20%), `VAULT15` (15%)

### Probar rápidamente que el backend responde
```bash
curl http://localhost:8080/api/juegos
```
Debe devolver el listado de juegos en JSON.

## 4. Abrir el frontend

No necesita servidor propio: abre `Frontend/index.html` directamente en el navegador
(doble clic, o clic derecho → "Abrir con..."), o sírvelo con cualquier servidor estático,
por ejemplo:

```bash
cd Frontend
npx serve .
```

El `script.js` ya apunta a `http://localhost:8080/api` (el backend Java). Al cargar la
página, sincroniza el catálogo real desde PostgreSQL automáticamente.

> **Importante para la demo:** si por algún motivo el backend no llega a responder en vivo
> (problemas de red, PostgreSQL no disponible, etc.), el frontend cae automáticamente al
> catálogo local de respaldo y sigue funcionando — así la presentación nunca se detiene.
> Verás un aviso en la consola del navegador (F12) indicando cuál fuente de datos se usó.

## 5. Flujo sugerido para la sustentación (5 pantallas)

1. **Portada / Hero** — pantalla de inicio con métricas (`#inicio`).
2. **Catálogo filtrable** — sección `#catalogo`, filtra por plataforma (PC, PlayStation,
   Xbox, Nintendo) y busca por nombre. Los datos vienen de `GET /api/juegos` (Java + PostgreSQL).
3. **Modal de detalles** — clic en una tarjeta de juego abre el modal con género,
   desarrollador, precio y descripción.
4. **Modal de carrito y cupones** — agrega productos, aplica un cupón (`PIXEL10`,
   `GAMER20` o `VAULT15`) validado contra `POST /api/cupones/validar`, y finaliza la compra
   (`POST /api/compras`, requiere haber iniciado sesión).
5. **Modal de autenticación y perfil** — registro (`POST /api/registro`) e inicio de sesión
   (`POST /api/login`) con JWT; el panel de perfil muestra el historial local de compras.

## 6. Endpoints principales (para mostrar en la sustentación)

| Método | Endpoint                  | Descripción                                   | Auth |
|--------|----------------------------|------------------------------------------------|------|
| POST   | `/api/registro`            | RF-01: crea un usuario (password con BCrypt)    | No   |
| POST   | `/api/login`                | RF-01: valida credenciales, devuelve JWT        | No   |
| GET    | `/api/juegos?plataforma=pc` | RF-02: catálogo con filtro dinámico             | No   |
| GET    | `/api/juegos/{id}`          | RF-03: detalle de un juego                      | No   |
| POST   | `/api/cupones/validar`      | RF-05: valida un código de cupón                | No   |
| POST   | `/api/compras`              | RF-04: crea Compra + DetalleCompra              | Sí (Bearer token) |
| GET    | `/api/compras/historial`    | Historial de compras del usuario autenticado    | Sí (Bearer token) |

## 7. Estructura del backend (arquitectura por capas, ver Diagrama de Clases)

```
com.pixelvault
├── controller/   UsuarioController, JuegoController, CuponController, CompraController
├── service/      interfaces (UsuarioService, JuegoService, CompraService)
├── service/impl/ implementaciones (UsuarioServiceImpl, JuegoServiceImpl, CompraServiceImpl)
├── repository/   UsuarioRepository, JuegoRepository, CuponRepository, CompraRepository
├── model/        Usuario, Juego, Cupon, Compra, DetalleCompra  (entidades JPA -> Diagrama ER)
├── dto/          AuthRequest, CompraRequest, CuponRequest
├── security/     JwtUtil, JwtAuthFilter
└── config/       CorsConfig, GlobalExceptionHandler
```

## 8. Notas técnicas

- `spring.jpa.hibernate.ddl-auto=update` crea/actualiza las tablas automáticamente a partir
  de las entidades — cómodo para el avance del curso. Para producción se recomendaría usar
  migraciones (Flyway/Liquibase) en vez de `update`.
- `data.sql` se ejecuta en **cada arranque** y reinicia todo el contenido transaccional
  (borra `detalle_compra`, `compras`, `juegos` y `cupones`, y vuelve a insertar el catálogo
  base). Esto es intencional para que cada ensayo de la sustentación arranque con datos
  limpios; si prefieres conservar las compras entre reinicios, comenta las líneas `DELETE`
  en `data.sql` o cambia `spring.sql.init.mode=always` a `never` después del primer arranque.
- El JWT expira en 2 horas (`pixelvault.jwt.expiration-ms=7200000`), igual que en el
  servidor Node.js original que ya tenías.
