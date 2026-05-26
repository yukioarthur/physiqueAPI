# API Key + Rate Limit — Physique API

## Endpoints protegidos

### ALUNO
- `GET /dashboard/{usuarioId}`
- `GET /usuarios/{usuarioId}/treino-atual`
- `GET /usuarios/{usuarioId}/desafios`
- `POST /treinos/finalizar`
- `POST /resultados-treino`

### PROFESSOR
- `POST /treinos`
- `PUT /treinos/{id}`
- `DELETE /treinos/{id}`
- `POST /exercicios`
- `PUT /exercicios/{id}`
- `DELETE /exercicios/{id}`
- `POST /grupos-musculares`
- `PUT /grupos-musculares/{id}`
- `DELETE /grupos-musculares/{id}`
- `POST /musculos`
- `PUT /musculos/{id}`
- `DELETE /musculos/{id}`
- `POST /series-calculadas`
- `PUT /series-calculadas/{id}`
- `DELETE /series-calculadas/{id}`

### ADMIN
- `POST /api-keys`
- `GET /api-keys/usuarios/{usuarioId}`
- `DELETE /api-keys/{id}`

### Bootstrap acadêmico
- `POST /api-keys/bootstrap-admin` fica público somente enquanto não existir nenhuma chave ADMIN ativa.

## Planos

- `ALUNO`: 60 requisições por minuto.
- `PROFESSOR`: 300 requisições por minuto.
- `ADMIN`: 1000 requisições por minuto.

Hierarquia: `ALUNO < PROFESSOR < ADMIN`.

## Headers

Enviar em endpoints protegidos:

```http
X-API-Key: phy_xxxxxxxxxxxxxxxxx
```

Respostas protegidas retornam:

```http
X-Rate-Limit-Plan
X-Rate-Limit-Remaining
RateLimit-Policy
RateLimit
```

Quando o limite estoura, também retorna:

```http
X-Rate-Limit-Retry-After-Seconds
Retry-After
```

## Respostas de erro

Sem chave:

```json
{ "error": "Missing X-API-Key header" }
```

Chave inválida:

```json
{ "error": "Invalid API key" }
```

Chave revogada ou expirada:

```json
{ "error": "Inactive API key" }
```

Plano insuficiente:

```json
{ "error": "Insufficient API access plan" }
```

Rate limit excedido:

```json
{
  "error": "Too Many Requests",
  "message": "API rate limit exceeded"
}
```

## Convivência com idempotência

`ApiKeyAuthenticationFilter` roda antes de `IdempotencyFilter`. Assim, em POSTs protegidos e idempotentes, primeiro ocorre autenticação/rate limit e depois a verificação de `Idempotency-Key`.

O `IdempotencyFilter` agora usa o usuário da API key como parte do escopo da idempotência quando existe contexto autenticado.
