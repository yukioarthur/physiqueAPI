# Idempotência em POSTs críticos — backend Spring Boot

## Endpoints críticos encontrados e marcados com `@RequireIdempotency`

- `POST /treinos` — criação de treino.
- `POST /treinos/finalizar` — confirmação/finalização de treino pelo app.
- `POST /resultados-treino` — registro de resultado/sessão de treino.
- `POST /series-calculadas` — criação de registro calculado de série.

Não foram marcados: `GET`, `PUT`, `DELETE`, login, cadastro de usuário, grupos musculares, músculos e exercícios.

## Abordagem técnica usada

A solução usa:

- anotação `@RequireIdempotency` nos métodos dos controllers;
- `IdempotencyFilter extends OncePerRequestFilter` para interceptar somente endpoints anotados;
- wrapper próprio `CachedBodyHttpServletRequest` para ler o body sem impedir o controller de ler o JSON depois;
- `ContentCachingResponseWrapper` para capturar status/body da resposta original;
- entidade JPA `IdempotencyRecord`;
- repository com busca por escopo e lock pessimista;
- service transacional `IdempotencyService`;
- hash SHA-256 do método HTTP + path + usuário + body;
- chave principal `Idempotency-Key` e fallback `X-Idempotency-Key`.

Como o projeto ainda não usa autenticação real no fluxo protegido, o escopo do usuário fica como `anonymous`.

## Comportamentos implementados

### Sem header

Retorna `400`:

```json
{
  "error": "Missing Idempotency-Key header"
}
```

### Primeira requisição

Salva `PROCESSING`, executa o controller, captura a resposta e marca `COMPLETED`.

### Retry igual

Não executa o controller novamente; retorna status/body salvos.

### Mesma chave com payload diferente

Retorna `422`:

```json
{
  "error": "Idempotency-Key reused with different request payload"
}
```

### Mesma chave ainda em processamento

Retorna `409`:

```json
{
  "error": "Request with this Idempotency-Key is still processing"
}
```

### Falha durante a execução

Se a exceção sair do controller/service, o registro é marcado como `FAILED` e a exceção real continua subindo. Isso evita esconder erro da aplicação. Um retry posterior com a mesma chave não executa automaticamente de novo; ele retorna conflito pedindo verificação do estado da operação.

## Exemplos Postman

### 1. Sem header

`POST http://localhost:8080/series-calculadas`

Body:

```json
{
  "treino": "Supino reto",
  "peso": 60.0,
  "reps": 8
}
```

Esperado: `400`.

### 2. Primeira chamada

Header:

```http
Idempotency-Key: serie-001
```

Body:

```json
{
  "treino": "Supino reto",
  "peso": 60.0,
  "reps": 8
}
```

Esperado: `201`.

### 3. Retry igual

Repita a mesma chamada com a mesma chave e o mesmo body.

Esperado: mesmo status/body da primeira chamada, sem criar novo registro de negócio.

### 4. Payload diferente

Use a mesma chave, mas altere o peso:

```json
{
  "treino": "Supino reto",
  "peso": 65.0,
  "reps": 8
}
```

Esperado: `422`.

### 5. Header legado

Também funciona:

```http
X-Idempotency-Key: serie-002
```

## Testes criados

Arquivo:

`src/test/java/senac/tsi/physique/idempotency/IdempotencyFilterIntegrationTest.java`

Cobre:

1. POST crítico sem header retorna 400.
2. Primeira chamada com chave processa normalmente.
3. Retry igual retorna resposta salva.
4. Retry igual não executa novamente a criação de `TreinoSerie`.
5. Payload diferente retorna 422.
6. Registro em `PROCESSING` retorna 409.
7. Endpoint sem `@RequireIdempotency` continua funcionando sem header.
8. GET sem header continua funcionando.
9. Header legado `X-Idempotency-Key` funciona.

## Observação sobre concorrência

O código já possui:

- escopo por chave + método + path + usuário;
- lock local em memória para evitar duplicidade dentro da mesma instância da API;
- busca com lock pessimista no repository;
- `uniqueConstraints` na entidade JPA para preparar o banco para uma constraint única futura.

Em ambiente com mais de uma instância da API, a garantia forte depende da constraint única física no banco. Não foi gerado SQL nem migration nesta entrega.
