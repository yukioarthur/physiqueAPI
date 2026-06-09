# Atualização: iniciar treino ativo

## Novo endpoint

POST /usuarios/{usuarioId}/treinos/{treinoId}/iniciar

Headers:
- X-API-Key: chave ALUNO/PROFESSOR/ADMIN válida
- X-API-Version: 1

O endpoint:
1. Busca o usuário.
2. Busca o treino com exercícios, músculos e grupo muscular carregados.
3. Desativa treinos ativos anteriores do usuário.
4. Cria ou reativa o vínculo em usuario_treino.
5. Retorna o mesmo DTO usado por GET /usuarios/{usuarioId}/treino-atual.

## Banco

Não foi criada tabela nova. A atualização usa a tabela existente usuario_treino.

Para teste manual equivalente:

UPDATE usuario_treino SET ativo = 0 WHERE usuario_id = 1 AND ativo = 1;
INSERT INTO usuario_treino (usuario_id, treino_id, ativo, data_inicio) VALUES (1, 1, 1, CURDATE());

## Correção adicional

GET /usuarios/{usuarioId}/treino-atual agora usa @Transactional(readOnly = true) e EntityGraph no repository para reduzir risco de LazyInitializationException/500 no Render.
