-- SQL apenas para inspeção no Beekeeper. Não apaga nem altera dados.
SELECT NOW();
SHOW TABLES;

SELECT * FROM usuario LIMIT 10;
SELECT * FROM treino LIMIT 10;
SELECT * FROM exercicio LIMIT 10;
SELECT * FROM grupo_muscular LIMIT 10;
SELECT * FROM musculo LIMIT 10;
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
