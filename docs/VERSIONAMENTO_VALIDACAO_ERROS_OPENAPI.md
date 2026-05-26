# Versionamento, validações, tratamento global de erros e OpenAPI

## Escopo

Implementação backend-only da etapa final do projeto, sem alterar Android, SQL, migrations, CORS, API Key, rate limit ou idempotência.

## Versionamento por header

O endpoint escolhido para demonstrar versionamento foi:

```http
GET /treinos/{id}
```

Versões suportadas:

```http
X-API-Version: 1
X-API-Version: 2
```

Se o header estiver ausente, a API mantém compatibilidade e responde como V1. Se o valor for diferente de `1` ou `2`, a API retorna `400 Bad Request`.

Diferenças:

- V1 mantém o comportamento compatível com a resposta original em HATEOAS.
- V2 retorna uma resposta evoluída com `apiVersion`, `quantidadeExercicios`, resumo dos exercícios e links simples.

## Bean Validation

Os DTOs de entrada receberam validações com mensagens em português, incluindo:

- `@NotBlank`
- `@NotNull`
- `@NotEmpty`
- `@Size`
- `@Min`
- `@Positive`
- `@DecimalMin`
- `@Email`
- `@PastOrPresent`
- `@FutureOrPresent`
- `@Valid` em listas aninhadas

Controllers foram anotados com `@Validated`, e IDs de path variables relevantes receberam `@Positive`.

## Tratamento global de erros

Foi criado um padrão único:

```json
{
  "timestamp": "2026-05-25T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "message": "Existem campos inválidos na requisição",
  "path": "/treinos",
  "fieldErrors": [
    {
      "field": "nome",
      "message": "O nome do treino é obrigatório"
    }
  ]
}
```

O `GlobalExceptionHandler` trata validação de body, path/query params, JSON malformado, tipo inválido, parâmetro/header ausente, recurso inexistente, conflito de integridade, argumentos inválidos e erros inesperados sem expor stack trace.

Os filtros de API Key, rate limit e idempotência também foram ajustados para escrever respostas no mesmo padrão usando `ApiErrorWriter`.

## OpenAPI / Swagger

A documentação global foi atualizada para mencionar:

- `X-API-Key`
- `X-API-Version`
- `Idempotency-Key`
- `X-Idempotency-Key`
- planos e rate limit
- CORS
- padrão de erros
- versionamento por header

O `OperationCustomizer` documenta automaticamente endpoints protegidos por API Key, endpoints idempotentes e endpoints versionados de treino.

## Testes manuais

### V1 explícita

```bash
curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Version: 1"
```

### V2 explícita

```bash
curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Version: 2"
```

### Fallback V1 sem header

```bash
curl -i -X GET "http://localhost:8080/treinos/1"
```

### Versão inválida

```bash
curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Version: 99"
```

Resposta esperada: `400 Bad Request` com mensagem `Supported versions are: 1, 2`.

### Validação de POST /treinos

```bash
curl -i -X POST "http://localhost:8080/treinos" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: SUA_CHAVE_PROFESSOR" \
  -H "Idempotency-Key: teste-validacao-001" \
  -d '{}'
```

Resposta esperada: `400 Bad Request` com `fieldErrors`.

### Swagger

```http
GET http://localhost:8080/swagger-ui/index.html
GET http://localhost:8080/v3/api-docs
```

## Testes automatizados criados

Arquivo:

```text
src/test/java/senac/tsi/physique/finalfeatures/VersionValidationErrorOpenApiIntegrationTest.java
```

Cobre:

- V1 por header
- V2 por header
- fallback V1 sem header
- versão inválida
- request inválido com fieldErrors
- número negativo inválido
- path variable inválido
- recurso inexistente
- JSON malformado
- OpenAPI documentando headers relevantes

## Observações

- Não foi criado SQL nem migration.
- Não houve alteração no Android.
- O versionamento foi feito por cabeçalho, não por URI.
- Idempotência, API Key, rate limit e CORS foram preservados.
