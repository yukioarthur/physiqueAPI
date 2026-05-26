# CORS — Physique API

Esta versão adiciona configuração centralizada de CORS no backend Spring Boot, sem alterar Android, banco, SQL ou migrations.

## Estratégia usada

O projeto não possui Spring Security configurado; ele usa filtros customizados para API Key, rate limit e idempotência. Por isso, a solução usa:

- `CorsConfig` centralizada no pacote `senac.tsi.physique.infrastructure`.
- `WebMvcConfigurer` para configuração global MVC.
- `CorsFilter` registrado com maior precedência para aplicar headers CORS antes dos filtros customizados.

Essa escolha evita que respostas geradas diretamente pelos filtros, como 401, 403, 409, 422 ou 429, cheguem ao navegador sem os headers CORS.

## Origens permitidas

- `http://localhost:3000`
- `http://localhost:5173`
- `http://localhost:4200`
- `http://127.0.0.1:3000`
- `http://127.0.0.1:5173`
- `http://127.0.0.1:4200`

## Métodos permitidos

- `GET`
- `POST`
- `PUT`
- `PATCH`
- `DELETE`
- `OPTIONS`

## Headers permitidos

- `Content-Type`
- `Accept`
- `Authorization`
- `X-API-Key`
- `Idempotency-Key`
- `X-Idempotency-Key`
- `Origin`
- `Cache-Control`

## Headers expostos ao frontend

- `X-Rate-Limit-Plan`
- `X-Rate-Limit-Remaining`
- `X-Rate-Limit-Retry-After-Seconds`
- `RateLimit`
- `RateLimit-Policy`
- `Location`

## Credentials

Foi usado `allowCredentials(false)`, porque o projeto autentica por `X-API-Key` e não depende de cookies ou sessão de navegador.

## Convivência com filtros

- `OPTIONS` não exige `X-API-Key`.
- `OPTIONS` não exige `Idempotency-Key`.
- `OPTIONS` não consome rate limit.
- `ApiKeyAuthenticationFilter` ignora explicitamente `OPTIONS`.
- `IdempotencyFilter` ignora explicitamente `OPTIONS`.
- `CorsFilter` roda antes dos filtros customizados.

## Teste manual

```bash
curl -i -X OPTIONS http://localhost:8080/treinos \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,X-API-Key,Idempotency-Key"
```

Resultado esperado:

- `HTTP 200` ou `HTTP 204`.
- `Access-Control-Allow-Origin: http://localhost:5173`.
- `Access-Control-Allow-Methods` contendo `POST`.
- `Access-Control-Allow-Headers` contendo `X-API-Key` e `Idempotency-Key`.
- Sem exigência de `X-API-Key`.
- Sem exigência de `Idempotency-Key`.
