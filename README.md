# Prices Microservice

Servicio de consulta de precios implementado en **Spring Boot** siguiendo una
**arquitectura hexagonal (Ports & Adapters)** pragmática.

Dada una marca, un producto y un instante, el servicio devuelve la tarifa
aplicable. Cuando varias tarifas se solapan en el tiempo, se aplica la de
**mayor prioridad**.

---

## Objetivo

Prueba técnica de Backend Engineer. Se implementa el clásico caso de la tabla
`PRICES`: seleccionar el precio final de venta de un producto para una fecha de
aplicación concreta.

---

## Arquitectura hexagonal

El dominio es el núcleo y no depende de Spring ni de JPA. La infraestructura
implementa los puertos definidos por el dominio.

```
HTTP Request
   ↓
PriceRestAdapter                     (infrastructure/rest)             ← adaptador de entrada
   ↓
GetApplicablePriceUseCase            (application/usecase)             ← puerto de entrada
   ↓
DefaultGetApplicablePriceUseCase     (application/usecase)             ← implementación del caso de uso
   ↓
PriceRepository                      (domain/ports)                    ← puerto de salida
   ↓
JpaPriceRepository                   (infrastructure/persistence)      ← adaptador
   ↓
SpringDataPriceRepository            (infrastructure/persistence)
   ↓
H2 Database
```

Reglas aplicadas:

- El **dominio** (`Price`, `PriceRepository`, `PriceNotFoundException`) no
  conoce Spring ni JPA.
- El **modelo de dominio** (`Price`) y la **entidad JPA** (`PriceEntity`) son
  clases distintas, conectadas por un `PriceEntityMapper`.
- El caso de uso se expone mediante el **puerto de entrada**
  `GetApplicablePriceUseCase` (`@FunctionalInterface`), que devuelve el
  **modelo de dominio `Price`** (nunca un DTO HTTP). Toda la lógica de
  negocio vive en su implementación `DefaultGetApplicablePriceUseCase`
  (`application/usecase`), que no depende de Spring.
- La infraestructura (`ApplicationConfig`) es responsable de instanciar y
  registrar el bean del caso de uso.
- `PriceRepository` es el **puerto de salida (output port)** del dominio; su
  implementación (`JpaPriceRepository`) vive en infraestructura y traduce las
  operaciones de persistencia mediante Spring Data JPA.
- El adaptador REST (`PriceRestAdapter`) orquesta la llamada al puerto de
  entrada y convierte `Price -> PriceResponse` mediante
  `PriceResponse.from(price)`. El DTO `PriceResponse` vive en
  `infrastructure/rest/dto`, por lo que la aplicación y el dominio no conocen
  ningún contrato HTTP ni JPA.

---

## Estructura del repositorio

```
com.classora.apps.microservice.prices.prices_micro
├── domain
│   ├── model        → Price
│   ├── ports        → PriceRepository
│   └── exception    → PriceNotFoundException
├── application
│   └── usecase      → GetApplicablePriceUseCase (puerto de entrada, devuelve Price)
│                       DefaultGetApplicablePriceUseCase (implementación)
└── infrastructure
    ├── rest         → PriceRestAdapter
    │   └── dto      → PriceResponse (Price -> PriceResponse)
    ├── persistence
    │   ├── entity   → PriceEntity
    │   ├── mapper   → PriceEntityMapper
    │   └── repository → SpringDataPriceRepository, JpaPriceRepository
    ├── exception    → GlobalExceptionHandler, ApiError
    └── config       → OpenApiConfig, ApplicationConfig
```

---

## Modelo de datos

El esquema (`schema.sql`) refleja que **`BRAND_ID` es una foreign key** hacia
la tabla `BRANDS` (la cadena/marca del grupo, p. ej. ZARA):

```sql
CREATE TABLE BRANDS (
    BRAND_ID BIGINT PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL
);

CREATE TABLE PRICES (
    ...
    BRAND_ID BIGINT NOT NULL,
    ...
    CONSTRAINT FK_PRICES_BRAND FOREIGN KEY (BRAND_ID) REFERENCES BRANDS (BRAND_ID)
);
```

Esta relación existe únicamente a nivel de base de datos. El modelo de
aplicación permanece desacoplado a propósito:

- `PriceEntity` sigue teniendo `private Long brandId` (sin `@ManyToOne`).
- No existe `BrandEntity` ni `BrandRepository`.
- El dominio (`Price`) y el caso de uso solo necesitan el identificador de la
  marca para resolver la tarifa aplicable; el agregado `Brand` no participa en
  el comportamiento del sistema, por lo que modelarlo en la capa de aplicación
  añadiría complejidad innecesaria.

---

## Tecnología

- Java 21
- Spring Boot 4.1
- Spring Data JPA
- Base de datos en memoria H2
- springdoc-openapi (Swagger UI)

---

## Cómo ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

Los datos de ejemplo se cargan automáticamente en H2 al iniciar (`data.sql`).

---

## Cómo ejecutar los tests

```bash
./mvnw test
```

La estrategia combina **tests unitarios** e **integración**:

- **Unitarios** (sin Spring): caso de uso (`DefaultGetApplicablePriceUseCase`
  con el puerto de salida mockeado), mapper de persistencia
  (`PriceEntityMapper`) y DTO del adaptador REST (`PriceResponse.from`).
- **Integración**: adaptador JPA con `@DataJpaTest` sobre H2, y el endpoint
  REST con `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvc`, cubriendo
  los cinco casos del enunciado más escenarios de error (400 y 404).

---

## Cobertura de código (JaCoCo)

La cobertura se mide con **JaCoCo**, integrado en el ciclo de build. El informe
se genera al ejecutar:

```bash
./mvnw verify
```

Informe HTML:

```
target/site/jacoco/index.html
```

El build aplica un **quality gate**: `mvn verify` falla si la cobertura baja de
los umbrales definidos (**80% de instrucciones** y **70% de ramas**). Se
excluyen del cómputo la clase principal `PricesMicroApplication` y las clases de
wiring de `infrastructure/config` (`ApplicationConfig`, `OpenApiConfig`), sin
valor funcional testeable; los casos de uso, adaptadores, mappers, DTOs y
repositorios sí se miden.

---

## Docker

El proyecto incluye un `Dockerfile` **multistage**: una etapa de build compila y
empaqueta el JAR con el Maven Wrapper sobre Temurin JDK 21, y una etapa de
runtime ligera basada en Temurin JRE 21 ejecuta únicamente el JAR.

Construir la imagen:

```bash
docker build -t classora-prices-micro .
```

Ejecutar el contenedor:

```bash
docker run -p 8080:8080 classora-prices-micro
```

La aplicación queda disponible en `http://localhost:8080` (Swagger UI en
`http://localhost:8080/swagger-ui.html`).

---

## Integración continua (GitHub Actions)

La pipeline [`.github/workflows/ci.yml`](.github/workflows/ci.yml) se ejecuta en
cada `push` y `pull_request`, y:

- configura Java 21 (Temurin) con caché de dependencias Maven,
- ejecuta `./mvnw verify`, que engloba los **tests unitarios y de integración**,
  la generación del informe **JaCoCo** y su **quality gate** (el build falla si
  la cobertura baja de los umbrales),
- publica el informe de cobertura como **artifact** (`jacoco-report`),
- y construye la **imagen Docker** para validar que el empaquetado funciona.

---

## API

### Endpoint

```
GET /prices?applicationDate={ISO-8601}&productId={id}&brandId={id}
```

| Parámetro         | Tipo          | Ejemplo               |
|-------------------|---------------|-----------------------|
| `applicationDate` | ISO-8601 date | `2020-06-14T10:00:00` |
| `productId`       | Long          | `35455`               |
| `brandId`         | Long          | `1`                   |

### Diseño del endpoint REST

El endpoint se ha implementado como:

```
GET /prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1
```

Aunque una alternativa más orientada a recursos podría ser:

```
GET /brands/{brandId}/products/{productId}/price?applicationDate=2020-06-14T16:00:00
```

se ha optado por utilizar **query parameters** para `brandId`, `productId` y
`applicationDate` por los siguientes motivos:

- El enunciado de la prueba define explícitamente los tres valores como
  parámetros de entrada del servicio.
- La operación representa una **consulta** del precio aplicable para un
  conjunto de criterios, más cercana a una operación de búsqueda que a la
  recuperación de un recurso persistente identificado por una única URI.
- Esta elección mantiene el endpoint simple y facilita la extensión futura
  con nuevos criterios de consulta sin modificar la estructura de la ruta.

La alternativa basada en **path parameters** es una opción REST perfectamente
válida y, probablemente, sería la elección preferida en una API pública
orientada a recursos. En este proyecto se ha priorizado la fidelidad al
enunciado y la simplicidad de la implementación.

### Ejemplo de llamada

```bash
curl "http://localhost:8080/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Respuesta:

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

### Errores

Si no existe tarifa aplicable, el servicio responde **HTTP 404** con un cuerpo
de error estándar. El caso se modela mediante la excepción de dominio
`PriceNotFoundException`, traducida a HTTP en `GlobalExceptionHandler`
(`@RestControllerAdvice`).

---

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Definición OpenAPI: `http://localhost:8080/v3/api-docs`

---

## Colección Postman

En [`postman/`](postman) se incluye una colección lista para importar en
Postman: [`Classora-Prices.postman_collection.json`](postman/Classora-Prices.postman_collection.json).

Contiene los 5 casos del enunciado más el caso de error 404, usando la
variable `{{baseUrl}}` (por defecto `http://localhost:8080`):

1. `GET /prices` — 2020-06-14 10:00 → rate 1 / 35.50 EUR
2. `GET /prices` — 2020-06-14 16:00 → rate 2 / 25.45 EUR
3. `GET /prices` — 2020-06-14 21:00 → rate 1 / 35.50 EUR
4. `GET /prices` — 2020-06-15 10:00 → rate 3 / 30.50 EUR
5. `GET /prices` — 2020-06-16 21:00 → rate 4 / 38.95 EUR
6. `GET /prices` — sin tarifa aplicable → **404**

Para usarla: abre Postman → *Import* → selecciona el archivo `.json` → ajusta
la variable `baseUrl` si la aplicación no corre en `localhost:8080`.

---

## Regla de prioridad

Cuando varias tarifas coinciden para la misma marca, producto e instante, se
aplica la de **mayor valor numérico de `priority`**.

Por eso `priority` se modela como `Integer` (no como boolean): aunque los datos
iniciales solo contienen `0` y `1`, el modelo admite prioridades futuras
(`2`, `3`, …).

La selección se resuelve en una única consulta derivada de Spring Data que
filtra por marca, producto y rango de fechas, ordena por `priority DESC` y
devuelve el primer resultado:

```
findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc
```

---

## Datos de ejemplo

`BRANDS`:

| BRAND_ID | NAME |
|----------|------|
| 1        | ZARA |

`PRICES`:

| BRAND_ID | START_DATE          | END_DATE            | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
|----------|---------------------|---------------------|------------|------------|----------|-------|------|
| 1        | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1          | 35455      | 0        | 35.50 | EUR  |
| 1        | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2          | 35455      | 1        | 25.45 | EUR  |
| 1        | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3          | 35455      | 1        | 30.50 | EUR  |
| 1        | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4          | 35455      | 1        | 38.95 | EUR  |

### Casos de prueba del enunciado

| # | applicationDate     | Resultado esperado (priceList / price) |
|---|---------------------|----------------------------------------|
| 1 | 2020-06-14T10:00:00 | 1 / 35.50                              |
| 2 | 2020-06-14T16:00:00 | 2 / 25.45                              |
| 3 | 2020-06-14T21:00:00 | 1 / 35.50                              |
| 4 | 2020-06-15T10:00:00 | 3 / 30.50                              |
| 5 | 2020-06-16T21:00:00 | 4 / 38.95                              |
