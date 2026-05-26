# Integração final — Physique API + Android + MySQL Faculdade + Beekeeper

## 1. Arquitetura

```text
Android Studio ↔ Spring Boot Physique API ↔ MySQL da faculdade ↔ Beekeeper Studio
```

O Beekeeper Studio é apenas o cliente visual para inspecionar o MySQL. O banco real é o MySQL fornecido pela faculdade.

## 2. Diagnóstico do backend

- Projeto: `physique`.
- Pacote base: `senac.tsi.physique`.
- Java: 21.
- Spring Boot: 3.5.13.
- Dependências principais: Spring Web, Spring Data JPA, Bean Validation, HATEOAS, springdoc-openapi, H2, MySQL Connector/J e Bucket4j.
- Recursos já integrados: idempotência, autenticação por `X-API-Key`, rate limit, CORS, versionamento por `X-API-Version`, Bean Validation, tratamento global de erros e Swagger/OpenAPI.

## 3. Controllers principais

- `AuthController`: login público em `POST /auth/login`.
- `UsuarioController`: CRUD de usuários.
- `GrupoMuscularController`: CRUD de grupos musculares.
- `MusculoController`: CRUD de músculos.
- `ExercicioController`: CRUD de exercícios.
- `TreinoController`: CRUD de treinos e endpoint versionado `GET /treinos/{id}`.
- `TreinoAppController`: endpoints do app Android: treino atual, finalizar treino e desafios.
- `DashboardController`: dashboard da Home do app.
- `ResultadoTreinoController`: resultados de treino.
- `TreinoSerieController`: séries calculadas.
- `ApiKeyController`: bootstrap, criação, listagem e revogação de API keys.

## 4. Profiles Spring

### `application.properties`

Usa profile por variável de ambiente:

```properties
spring.profiles.active=${SPRING_PROFILES_ACTIVE:local}
server.port=${PORT:8080}
```

### `application-local.properties`

Usa H2 em memória para testes locais rápidos.

### `application-faculdade.properties`

Usa MySQL da faculdade via variáveis de ambiente:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

URL MySQL esperada:

```text
jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
```

### `application-render.properties`

Também usa MySQL via variáveis de ambiente. Não usa PostgreSQL.

## 5. Variáveis de ambiente necessárias

Para rodar com MySQL da faculdade:

```bash
SPRING_PROFILES_ACTIVE=faculdade
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=SENHA
```

No Render:

```bash
SPRING_PROFILES_ACTIVE=render
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=SENHA
PORT=8080
```

Nunca coloque senha real em arquivos versionados.

## 6. Como rodar a API localmente

Com H2:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Com MySQL da faculdade:

```bash
export SPRING_PROFILES_ACTIVE=faculdade
export SPRING_DATASOURCE_URL='jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo'
export SPRING_DATASOURCE_USERNAME='USUARIO'
export SPRING_DATASOURCE_PASSWORD='SENHA'
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="faculdade"
$env:SPRING_DATASOURCE_URL="jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
$env:SPRING_DATASOURCE_USERNAME="USUARIO"
$env:SPRING_DATASOURCE_PASSWORD="SENHA"
./mvnw spring-boot:run
```

## 7. Swagger/OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Caminho configurado também: `http://localhost:8080/api-docs`

## 8. SQL de inspeção no Beekeeper

Use apenas para consultar, não apaga dados.

```sql
SELECT NOW();
SHOW TABLES;

SELECT * FROM usuario LIMIT 10;
SELECT * FROM treino LIMIT 10;
SELECT * FROM exercicio LIMIT 10;
SELECT * FROM resultado_treino ORDER BY id DESC LIMIT 10;
SELECT * FROM serie_executada ORDER BY id DESC LIMIT 10;
SELECT * FROM usuario_treino LIMIT 10;
SELECT * FROM desafio LIMIT 10;
SELECT * FROM usuario_desafio LIMIT 10;

SELECT * FROM idempotency_record ORDER BY id DESC LIMIT 10;

SELECT id, name, key_prefix, access_plan, status, created_at, last_used_at, revoked_at
FROM api_key
ORDER BY id DESC
LIMIT 10;
```

## 9. Testes manuais da API

### 9.1 Login público

```bash
curl -i -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"jorge@email.com","senha":"123456"}'
```

### 9.2 Bootstrap da primeira chave ADMIN

```bash
curl -i -X POST "http://localhost:8080/api-keys/bootstrap-admin" \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"name":"Chave ADMIN inicial","accessPlan":"ADMIN","expiresAt":"2026-12-31T23:59:59"}'
```

Copie o campo `apiKey`. A chave completa só aparece nessa resposta.

### 9.3 Criar chave ALUNO

```bash
curl -i -X POST "http://localhost:8080/api-keys" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: SUA_CHAVE_ADMIN" \
  -d '{"usuarioId":1,"name":"Chave Android aluno","accessPlan":"ALUNO","expiresAt":"2026-12-31T23:59:59"}'
```

### 9.4 Dashboard protegido

```bash
curl -i -X GET "http://localhost:8080/dashboard/1" \
  -H "X-API-Key: SUA_CHAVE_ALUNO" \
  -H "X-API-Version: 1"
```

Sem `X-API-Key`, deve retornar `401`.

### 9.5 Versionamento V1/V2

```bash
curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Key: SUA_CHAVE_PROFESSOR_OU_ADMIN" \
  -H "X-API-Version: 1"

curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Key: SUA_CHAVE_PROFESSOR_OU_ADMIN" \
  -H "X-API-Version: 2"
```

Versão inválida:

```bash
curl -i -X GET "http://localhost:8080/treinos/1" \
  -H "X-API-Key: SUA_CHAVE_PROFESSOR_OU_ADMIN" \
  -H "X-API-Version: 99"
```

### 9.6 POST crítico com idempotência

```bash
curl -i -X POST "http://localhost:8080/treinos/finalizar" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: SUA_CHAVE_ALUNO" \
  -H "X-API-Version: 1" \
  -H "Idempotency-Key: finalizar-treino-001" \
  -d '{
    "usuarioId": 1,
    "treinoId": 1,
    "series": [
      {"exercicioId": 1, "numeroSerie": 1, "repeticoes": 10, "peso": 40.0}
    ]
  }'
```

Repita exatamente a chamada com a mesma `Idempotency-Key`; o resultado não deve duplicar no banco.

### 9.7 CORS preflight

```bash
curl -i -X OPTIONS http://localhost:8080/treinos \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,X-API-Key,Idempotency-Key"
```

Esperado: `Access-Control-Allow-Origin` e headers CORS, sem exigir `X-API-Key`.

## 10. Configuração do Android

O Android usa Retrofit/OkHttp.

Base URL para emulador:

```kotlin
http://10.0.2.2:8080/
```

Celular físico na mesma rede:

```kotlin
http://IP_DA_MAQUINA:8080/
```

Render:

```kotlin
https://NOME-DO-SERVICO.onrender.com/
```

Arquivo principal de configuração no Android:

```text
ApiConfig.kt
```

Para testar, cole uma chave ALUNO válida em:

```kotlin
const val DEFAULT_API_KEY = "COLE_A_CHAVE_ALUNO_AQUI"
```

O `ApiKeyInterceptor` envia automaticamente:

```http
X-API-Key: chave_configurada
X-API-Version: 1
```

Em `POST /treinos/finalizar`, o app gera uma `Idempotency-Key` única por operação.

## 11. Render

Dockerfile atual usa Java 21 e Maven.

Build command, quando não usar Docker:

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

Start command:

```bash
java -jar target/*.jar
```

Variáveis no Render:

```bash
SPRING_PROFILES_ACTIVE=render
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:3306/NOME_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=SENHA
```

Se o Render não conectar no MySQL da faculdade, valide firewall, porta 3306, usuário, senha, banco, permissão de acesso externo, VPN e allowlist de IP. Se o servidor da faculdade não permitir conexão externa, não é erro do código.

## 12. Troubleshooting

### Communications link failure

A API não conseguiu chegar ao MySQL. Verifique host, porta, VPN, firewall e se o MySQL aceita conexão externa.

### Access denied for user

Usuário ou senha incorretos, ou usuário sem permissão para acessar de fora do servidor.

### Unknown database

Nome do banco está errado ou o banco não existe.

### Public Key Retrieval is not allowed

Adicione na URL:

```text
allowPublicKeyRetrieval=true
```

### Timezone error

Adicione na URL:

```text
serverTimezone=America/Sao_Paulo
```

### CORS error

Confirme se a origem está em `CorsConfig`. Para Vite use `http://localhost:5173`.

### Missing X-API-Key

Envie `X-API-Key` em endpoints protegidos ou configure `ApiConfig.DEFAULT_API_KEY` no Android.

### Missing Idempotency-Key

Envie `Idempotency-Key` nos POSTs críticos.

### Too Many Requests

A chave ultrapassou o limite do plano. Aguarde a janela de refill.

### Android cleartext not permitted

Para teste local HTTP, use `network_security_config`. Para produção, prefira HTTPS.

### Failed to connect to /10.0.2.2

A API não está rodando no PC, a porta está diferente ou o emulador não consegue alcançar o host.

### Render ./mvnw permission denied

Use `chmod +x mvnw` no build ou Dockerfile.

### Render não conecta no MySQL da faculdade

Pode ser bloqueio de rede ou permissão do servidor da faculdade. Verifique com a instituição.

## 13. Checklist final

- [ ] API sobe localmente.
- [ ] API conecta no MySQL da faculdade.
- [ ] Swagger abre.
- [ ] Bootstrap cria chave ADMIN.
- [ ] ADMIN cria chave ALUNO.
- [ ] `GET /dashboard/{usuarioId}` funciona com `X-API-Key`.
- [ ] `GET /treinos/{id}` funciona com `X-API-Version: 1`.
- [ ] `GET /treinos/{id}` funciona com `X-API-Version: 2`.
- [ ] `POST /treinos/finalizar` exige `Idempotency-Key`.
- [ ] Retry com a mesma `Idempotency-Key` não duplica dados.
- [ ] Dados aparecem no Beekeeper.
- [ ] Android está com `INTERNET` no Manifest.
- [ ] Android aponta para a URL correta.
- [ ] Android envia `X-API-Key`.
- [ ] Android envia `Idempotency-Key` em finalizar treino.

## 14. Riscos e pendências

- Senha simples no login é aceitável apenas para MVP acadêmico. Em produção, usar BCrypt e Spring Security.
- API Key colocada diretamente no Android é prática para teste acadêmico, mas não é ideal para produção.
- Se o Render não alcançar o MySQL da faculdade, a correção depende da rede/servidor da faculdade.
- `ddl-auto=update` é prático no MVP, mas migrations com Flyway/Liquibase são mais adequadas em produção.
