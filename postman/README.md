# Colección Postman — Classora Prices API

Colección lista para importar en Postman que cubre los 5 casos de prueba del
enunciado más un caso adicional de error 404, contra el endpoint:

```
GET /prices?applicationDate={ISO-8601}&productId={id}&brandId={id}
```

## Cómo importarla

1. Abre Postman.
2. **Import** → **File** → selecciona `Classora-Prices.postman_collection.json`.
3. La colección usa la variable `baseUrl` (por defecto `http://localhost:8080`).
   Si la aplicación corre en otro host/puerto, edítala en la pestaña
   **Variables** de la colección.
4. Arranca la aplicación (`./mvnw spring-boot:run`) y ejecuta las peticiones.

## Peticiones incluidas

| # | Nombre                              | applicationDate     | Resultado esperado      |
|---|--------------------------------------|----------------------|--------------------------|
| 1 | Test 1 - 2020-06-14 10:00            | 2020-06-14T10:00:00  | priceList 1 / 35.50 EUR  |
| 2 | Test 2 - 2020-06-14 16:00            | 2020-06-14T16:00:00  | priceList 2 / 25.45 EUR  |
| 3 | Test 3 - 2020-06-14 21:00            | 2020-06-14T21:00:00  | priceList 1 / 35.50 EUR  |
| 4 | Test 4 - 2020-06-15 10:00            | 2020-06-15T10:00:00  | priceList 3 / 30.50 EUR  |
| 5 | Test 5 - 2020-06-16 21:00            | 2020-06-16T21:00:00  | priceList 4 / 38.95 EUR  |
| 6 | Test 6 - Precio no encontrado (404)  | 2020-01-01T10:00:00  | HTTP 404                 |

Todas las peticiones usan `productId=35455` y `brandId=1` (ZARA), salvo el
caso de error, que usa un `productId` inexistente para forzar el 404.
