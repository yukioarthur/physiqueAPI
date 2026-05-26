# Integração MVP Physique

Este projeto foi adaptado para o fluxo Android + Spring Boot + MySQL do MVP acadêmico.

## Endpoints principais

- POST /auth/login
- GET /dashboard/{usuarioId}
- GET /usuarios/{usuarioId}/treino-atual
- POST /treinos/finalizar
- GET /usuarios/{usuarioId}/desafios

## Banco MySQL

O arquivo `src/main/resources/application-faculdade.properties` foi criado como modelo.
Preencha HOST, PORTA, NOME_DO_BANCO, USUARIO e SENHA.

Para usar MySQL, altere `src/main/resources/application.properties`:

```properties
spring.profiles.active=faculdade
```

## Login de teste

Se você rodou o seed enviado anteriormente no Beekeeper:

- email: jorge@email.com
- senha: 123456

## Observação sobre segurança

O login usa senha em texto simples apenas para o MVP acadêmico. Em produção, use Spring Security + BCrypt + token/JWT.
