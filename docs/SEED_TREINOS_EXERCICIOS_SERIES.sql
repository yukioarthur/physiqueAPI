-- PHYSIQUE - CARGA RICA DE TREINOS, EXERCICIOS E SERIES PRESCRITAS
-- Uso: colar e executar no Beekeeper Studio no banco MySQL da faculdade.
-- Objetivo: transformar exercicio em catalogo, treino_exercicio em composicao e treino_serie em prescricao.

SET FOREIGN_KEY_CHECKS = 0;

-- 1) Tabela de series prescritas do treino.
-- A tabela estava vazia no seu banco. Aqui ela passa a ter utilidade real:
-- cada linha representa uma serie planejada de um exercicio dentro de um treino.
DROP TABLE IF EXISTS treino_serie;
CREATE TABLE treino_serie (
    id BIGINT NOT NULL AUTO_INCREMENT,

    -- Campos legados mantidos para compatibilidade com /series-calculadas
    treino VARCHAR(255) NULL,
    peso DOUBLE NULL,
    reps INT NULL,
    uma_rep_max DOUBLE NULL,
    prox_serie_max DOUBLE NULL,
    prox_serie_rep INT NULL,

    -- Campos novos de prescricao
    treino_id BIGINT NULL,
    exercicio_id BIGINT NULL,
    ordem_exercicio INT NULL,
    numero_serie INT NULL,
    repeticoes_min INT NULL,
    repeticoes_max INT NULL,
    carga_sugerida DOUBLE NULL,
    rir INT NULL,
    descanso_segundos INT NULL,
    tempo_execucao VARCHAR(40) NULL,
    observacao VARCHAR(500) NULL,

    PRIMARY KEY (id),
    INDEX idx_treino_serie_treino (treino_id),
    INDEX idx_treino_serie_exercicio (exercicio_id),
    CONSTRAINT fk_treino_serie_treino FOREIGN KEY (treino_id) REFERENCES treino(id),
    CONSTRAINT fk_treino_serie_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- 2) Grupos musculares
INSERT INTO grupo_muscular (id, nome) VALUES
(1, 'Peito'),
(2, 'Pernas'),
(3, 'Costas'),
(4, 'Ombros'),
(5, 'Bracos'),
(6, 'Core'),
(7, 'Gluteos')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- 3) Musculos
INSERT INTO musculo (id, nome, grupo_muscular_id) VALUES
(1, 'Peitoral maior', 1),
(2, 'Quadriceps', 2),
(3, 'Latissimo do dorso', 3),
(4, 'Deltoide anterior', 4),
(5, 'Deltoide lateral', 4),
(6, 'Biceps braquial', 5),
(7, 'Triceps braquial', 5),
(8, 'Isquiotibiais', 2),
(9, 'Gluteo maximo', 7),
(10, 'Panturrilha', 2),
(11, 'Reto abdominal', 6),
(12, 'Romboides e trapezio medio', 3),
(13, 'Peitoral superior', 1),
(14, 'Eretores da espinha', 3)
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    grupo_muscular_id = VALUES(grupo_muscular_id);

-- 4) Catalogo de exercicios
-- repeticoes e quantidade_series continuam preenchidos para compatibilidade,
-- mas a prescricao real agora fica em treino_serie.
INSERT INTO exercicio (id, nome, repeticoes, quantidade_series, grupo_muscular_id, musculo_id, descricao, video) VALUES
(1, 'Supino reto', 8, 4, 1, 1, 'Descer a barra com controle e empurrar ate a extensao dos bracos.', NULL),
(2, 'Agachamento livre', 6, 4, 2, 2, 'Agachar mantendo coluna neutra e subir com controle.', NULL),
(3, 'Remada curvada', 10, 3, 3, 3, 'Puxar a barra em direcao ao abdomen mantendo controle do tronco.', NULL),
(4, 'Supino inclinado com halteres', 10, 3, 1, 13, 'Empurrar os halteres em banco inclinado, controlando a descida.', NULL),
(5, 'Crucifixo com halteres', 12, 3, 1, 1, 'Abrir os bracos com leve flexao dos cotovelos e aproximar os halteres no alto.', NULL),
(6, 'Flexao de bracos', 12, 3, 1, 1, 'Manter corpo alinhado, descer ate proximo do solo e subir com controle.', NULL),
(7, 'Desenvolvimento com halteres', 10, 3, 4, 4, 'Empurrar os halteres acima da cabeca sem perder estabilidade do tronco.', NULL),
(8, 'Elevacao lateral', 12, 3, 4, 5, 'Elevar os bracos lateralmente ate a linha dos ombros com controle.', NULL),
(9, 'Triceps pulley', 12, 3, 5, 7, 'Estender os cotovelos mantendo-os proximos ao tronco.', NULL),
(10, 'Triceps testa', 10, 3, 5, 7, 'Flexionar e estender cotovelos controlando a barra ou halteres.', NULL),
(11, 'Puxada frente', 10, 4, 3, 3, 'Puxar a barra em direcao ao peitoral mantendo escapulas controladas.', NULL),
(12, 'Remada baixa', 10, 3, 3, 12, 'Puxar o cabo em direcao ao abdomen mantendo coluna neutra.', NULL),
(13, 'Face pull', 12, 3, 4, 5, 'Puxar a corda em direcao ao rosto, priorizando parte posterior do ombro.', NULL),
(14, 'Rosca direta', 10, 3, 5, 6, 'Flexionar cotovelos mantendo tronco estavel.', NULL),
(15, 'Rosca martelo', 12, 3, 5, 6, 'Flexionar cotovelos com pegada neutra.', NULL),
(16, 'Leg press', 10, 4, 2, 2, 'Empurrar a plataforma com controle mantendo joelhos alinhados.', NULL),
(17, 'Cadeira extensora', 12, 3, 2, 2, 'Estender os joelhos controlando a subida e a descida.', NULL),
(18, 'Mesa flexora', 12, 3, 2, 8, 'Flexionar os joelhos com controle, priorizando posterior de coxa.', NULL),
(19, 'Levantamento terra romeno', 10, 3, 2, 8, 'Flexionar o quadril mantendo coluna neutra e sentir alongamento posterior.', NULL),
(20, 'Hip thrust', 10, 4, 7, 9, 'Estender o quadril com pausa no topo e controle da pelve.', NULL),
(21, 'Panturrilha em pe', 15, 4, 2, 10, 'Elevar os calcanhares com amplitude completa.', NULL),
(22, 'Prancha', 30, 3, 6, 11, 'Manter o tronco estavel sem perder alinhamento.', NULL),
(23, 'Abdominal crunch', 15, 3, 6, 11, 'Flexionar o tronco com controle, sem puxar o pescoco.', NULL),
(24, 'Barra fixa assistida', 8, 3, 3, 3, 'Puxar o corpo ate aproximar o queixo da barra usando assistencia se necessario.', NULL)
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    repeticoes = VALUES(repeticoes),
    quantidade_series = VALUES(quantidade_series),
    grupo_muscular_id = VALUES(grupo_muscular_id),
    musculo_id = VALUES(musculo_id),
    descricao = VALUES(descricao),
    video = VALUES(video);

-- 5) Treinos coerentes por metodologia
INSERT INTO treino (id, nome, objetivo, metodologia, criador_nome) VALUES
(1, 'Full Body Iniciante A', 'Base geral, aprendizado tecnico e hipertrofia inicial', 'Full Body 3x/semana - progressao linear simples', 'Professor Physique'),
(2, 'Push Hipertrofia', 'Peito, ombros e triceps com foco em hipertrofia', 'Push/Pull/Legs - volume moderado', 'Professor Physique'),
(3, 'Pull Hipertrofia', 'Costas e biceps com foco em hipertrofia e postura', 'Push/Pull/Legs - puxadas e remadas', 'Professor Physique'),
(4, 'Legs Hipertrofia', 'Quadriceps, posterior, gluteos e panturrilha', 'Push/Pull/Legs - membros inferiores', 'Professor Physique')
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    objetivo = VALUES(objetivo),
    metodologia = VALUES(metodologia),
    criador_nome = VALUES(criador_nome);

-- 6) Composicao dos treinos: quais exercicios pertencem a cada treino
DELETE FROM treino_exercicio WHERE treino_id IN (1,2,3,4);
INSERT INTO treino_exercicio (treino_id, exercicio_id) VALUES
-- Full Body
(1, 2), (1, 1), (1, 3), (1, 7), (1, 19), (1, 22),
-- Push
(2, 1), (2, 4), (2, 7), (2, 8), (2, 9), (2, 10),
-- Pull
(3, 11), (3, 3), (3, 12), (3, 13), (3, 14), (3, 15),
-- Legs
(4, 2), (4, 16), (4, 19), (4, 18), (4, 20), (4, 21), (4, 23);

-- 7) Series prescritas por treino/exercicio
DELETE FROM treino_serie WHERE treino_id IN (1,2,3,4);

-- Treino 1: Full Body Iniciante A
INSERT INTO treino_serie (treino, peso, reps, treino_id, exercicio_id, ordem_exercicio, numero_serie, repeticoes_min, repeticoes_max, carga_sugerida, rir, descanso_segundos, tempo_execucao, observacao) VALUES
('Full Body Iniciante A - Agachamento livre', 20, 10, 1, 2, 1, 1, 8, 10, 20, 3, 120, '3-1-2', 'Priorizar tecnica e amplitude segura.'),
('Full Body Iniciante A - Agachamento livre', 20, 10, 1, 2, 1, 2, 8, 10, 20, 2, 120, '3-1-2', 'Manter joelhos alinhados.'),
('Full Body Iniciante A - Agachamento livre', 20, 10, 1, 2, 1, 3, 8, 10, 20, 2, 120, '3-1-2', 'Finalizar com boa tecnica.'),
('Full Body Iniciante A - Supino reto', 30, 10, 1, 1, 2, 1, 8, 10, 30, 3, 90, '2-1-2', 'Escapulas firmes no banco.'),
('Full Body Iniciante A - Supino reto', 30, 10, 1, 1, 2, 2, 8, 10, 30, 2, 90, '2-1-2', 'Controlar a descida.'),
('Full Body Iniciante A - Supino reto', 30, 10, 1, 1, 2, 3, 8, 10, 30, 2, 90, '2-1-2', 'Nao perder amplitude.'),
('Full Body Iniciante A - Remada curvada', 25, 10, 1, 3, 3, 1, 10, 12, 25, 3, 90, '2-1-2', 'Tronco estavel.'),
('Full Body Iniciante A - Remada curvada', 25, 10, 1, 3, 3, 2, 10, 12, 25, 2, 90, '2-1-2', 'Puxar com cotovelos.'),
('Full Body Iniciante A - Remada curvada', 25, 10, 1, 3, 3, 3, 10, 12, 25, 2, 90, '2-1-2', 'Evitar impulso.'),
('Full Body Iniciante A - Desenvolvimento', 12, 10, 1, 7, 4, 1, 8, 10, 12, 3, 90, '2-1-2', 'Manter abdomen ativo.'),
('Full Body Iniciante A - Desenvolvimento', 12, 10, 1, 7, 4, 2, 8, 10, 12, 2, 90, '2-1-2', 'Nao hiperestender lombar.'),
('Full Body Iniciante A - Terra romeno', 25, 10, 1, 19, 5, 1, 8, 10, 25, 3, 120, '3-1-2', 'Sentir posterior de coxa.'),
('Full Body Iniciante A - Terra romeno', 25, 10, 1, 19, 5, 2, 8, 10, 25, 2, 120, '3-1-2', 'Coluna neutra.'),
('Full Body Iniciante A - Prancha', 0, 30, 1, 22, 6, 1, 30, 40, 0, 2, 60, 'isometria', 'Segurar em segundos.'),
('Full Body Iniciante A - Prancha', 0, 30, 1, 22, 6, 2, 30, 40, 0, 2, 60, 'isometria', 'Respirar durante a execucao.'),
('Full Body Iniciante A - Prancha', 0, 30, 1, 22, 6, 3, 30, 40, 0, 2, 60, 'isometria', 'Manter alinhamento.');

-- Treino 2: Push Hipertrofia
INSERT INTO treino_serie (treino, peso, reps, treino_id, exercicio_id, ordem_exercicio, numero_serie, repeticoes_min, repeticoes_max, carga_sugerida, rir, descanso_segundos, tempo_execucao, observacao) VALUES
('Push - Supino reto', 40, 8, 2, 1, 1, 1, 6, 8, 40, 2, 150, '2-1-2', 'Exercicio principal do dia.'),
('Push - Supino reto', 40, 8, 2, 1, 1, 2, 6, 8, 40, 2, 150, '2-1-2', 'Manter controle.'),
('Push - Supino reto', 40, 8, 2, 1, 1, 3, 6, 8, 40, 1, 150, '2-1-2', 'Chegar perto da falha com seguranca.'),
('Push - Supino reto', 35, 10, 2, 1, 1, 4, 8, 10, 35, 1, 120, '2-1-2', 'Back-off set.'),
('Push - Supino inclinado', 18, 10, 2, 4, 2, 1, 8, 10, 18, 2, 120, '2-1-2', 'Foco no peitoral superior.'),
('Push - Supino inclinado', 18, 10, 2, 4, 2, 2, 8, 10, 18, 2, 120, '2-1-2', 'Controlar halteres.'),
('Push - Supino inclinado', 18, 10, 2, 4, 2, 3, 8, 10, 18, 1, 120, '2-1-2', 'Manter estabilidade.'),
('Push - Desenvolvimento', 14, 10, 2, 7, 3, 1, 8, 10, 14, 2, 120, '2-1-2', 'Ombros e triceps.'),
('Push - Desenvolvimento', 14, 10, 2, 7, 3, 2, 8, 10, 14, 2, 120, '2-1-2', 'Evitar impulso.'),
('Push - Desenvolvimento', 14, 10, 2, 7, 3, 3, 8, 10, 14, 1, 120, '2-1-2', 'Ultima serie forte.'),
('Push - Elevacao lateral', 6, 12, 2, 8, 4, 1, 12, 15, 6, 2, 60, '2-1-2', 'Controle total.'),
('Push - Elevacao lateral', 6, 12, 2, 8, 4, 2, 12, 15, 6, 1, 60, '2-1-2', 'Sem balancar.'),
('Push - Elevacao lateral', 6, 12, 2, 8, 4, 3, 12, 15, 6, 1, 60, '2-1-2', 'Queimar deltoide lateral.'),
('Push - Triceps pulley', 20, 12, 2, 9, 5, 1, 10, 12, 20, 2, 75, '2-1-2', 'Cotovelos fixos.'),
('Push - Triceps pulley', 20, 12, 2, 9, 5, 2, 10, 12, 20, 1, 75, '2-1-2', 'Amplitude completa.'),
('Push - Triceps testa', 12, 10, 2, 10, 6, 1, 8, 10, 12, 2, 90, '2-1-2', 'Cuidado com cotovelos.'),
('Push - Triceps testa', 12, 10, 2, 10, 6, 2, 8, 10, 12, 1, 90, '2-1-2', 'Executar sem dor.');

-- Treino 3: Pull Hipertrofia
INSERT INTO treino_serie (treino, peso, reps, treino_id, exercicio_id, ordem_exercicio, numero_serie, repeticoes_min, repeticoes_max, carga_sugerida, rir, descanso_segundos, tempo_execucao, observacao) VALUES
('Pull - Puxada frente', 45, 10, 3, 11, 1, 1, 8, 10, 45, 2, 120, '2-1-2', 'Puxar com dorsais.'),
('Pull - Puxada frente', 45, 10, 3, 11, 1, 2, 8, 10, 45, 2, 120, '2-1-2', 'Escapulas controladas.'),
('Pull - Puxada frente', 45, 10, 3, 11, 1, 3, 8, 10, 45, 1, 120, '2-1-2', 'Ultima serie pesada.'),
('Pull - Remada curvada', 35, 8, 3, 3, 2, 1, 8, 10, 35, 2, 120, '2-1-2', 'Tronco estavel.'),
('Pull - Remada curvada', 35, 8, 3, 3, 2, 2, 8, 10, 35, 2, 120, '2-1-2', 'Sem roubar.'),
('Pull - Remada curvada', 35, 8, 3, 3, 2, 3, 8, 10, 35, 1, 120, '2-1-2', 'Puxar ate abdomen.'),
('Pull - Remada baixa', 40, 10, 3, 12, 3, 1, 10, 12, 40, 2, 90, '2-1-2', 'Apertar escapulas.'),
('Pull - Remada baixa', 40, 10, 3, 12, 3, 2, 10, 12, 40, 1, 90, '2-1-2', 'Controle na volta.'),
('Pull - Remada baixa', 40, 10, 3, 12, 3, 3, 10, 12, 40, 1, 90, '2-1-2', 'Boa postura.'),
('Pull - Face pull', 15, 12, 3, 13, 4, 1, 12, 15, 15, 2, 60, '2-1-2', 'Posterior de ombro.'),
('Pull - Face pull', 15, 12, 3, 13, 4, 2, 12, 15, 15, 1, 60, '2-1-2', 'Cotovelos altos.'),
('Pull - Rosca direta', 18, 10, 3, 14, 5, 1, 8, 10, 18, 2, 75, '2-1-2', 'Sem balancar tronco.'),
('Pull - Rosca direta', 18, 10, 3, 14, 5, 2, 8, 10, 18, 1, 75, '2-1-2', 'Controle na descida.'),
('Pull - Rosca martelo', 12, 12, 3, 15, 6, 1, 10, 12, 12, 2, 75, '2-1-2', 'Foco braquial.'),
('Pull - Rosca martelo', 12, 12, 3, 15, 6, 2, 10, 12, 12, 1, 75, '2-1-2', 'Pegada neutra.');

-- Treino 4: Legs Hipertrofia
INSERT INTO treino_serie (treino, peso, reps, treino_id, exercicio_id, ordem_exercicio, numero_serie, repeticoes_min, repeticoes_max, carga_sugerida, rir, descanso_segundos, tempo_execucao, observacao) VALUES
('Legs - Agachamento livre', 40, 8, 4, 2, 1, 1, 6, 8, 40, 2, 180, '3-1-2', 'Principal do dia.'),
('Legs - Agachamento livre', 40, 8, 4, 2, 1, 2, 6, 8, 40, 2, 180, '3-1-2', 'Manter tecnica.'),
('Legs - Agachamento livre', 40, 8, 4, 2, 1, 3, 6, 8, 40, 1, 180, '3-1-2', 'Serie forte.'),
('Legs - Leg press', 100, 10, 4, 16, 2, 1, 10, 12, 100, 2, 150, '2-1-2', 'Amplitude segura.'),
('Legs - Leg press', 100, 10, 4, 16, 2, 2, 10, 12, 100, 2, 150, '2-1-2', 'Joelhos alinhados.'),
('Legs - Leg press', 100, 10, 4, 16, 2, 3, 10, 12, 100, 1, 150, '2-1-2', 'Sem travar joelhos.'),
('Legs - Terra romeno', 45, 10, 4, 19, 3, 1, 8, 10, 45, 2, 150, '3-1-2', 'Posterior e gluteos.'),
('Legs - Terra romeno', 45, 10, 4, 19, 3, 2, 8, 10, 45, 2, 150, '3-1-2', 'Coluna neutra.'),
('Legs - Terra romeno', 45, 10, 4, 19, 3, 3, 8, 10, 45, 1, 150, '3-1-2', 'Sem perder controle.'),
('Legs - Mesa flexora', 30, 12, 4, 18, 4, 1, 10, 12, 30, 2, 90, '2-1-2', 'Posterior de coxa.'),
('Legs - Mesa flexora', 30, 12, 4, 18, 4, 2, 10, 12, 30, 1, 90, '2-1-2', 'Controlar volta.'),
('Legs - Hip thrust', 60, 10, 4, 20, 5, 1, 8, 10, 60, 2, 120, '2-1-2', 'Pausa no topo.'),
('Legs - Hip thrust', 60, 10, 4, 20, 5, 2, 8, 10, 60, 2, 120, '2-1-2', 'Contrair gluteos.'),
('Legs - Hip thrust', 60, 10, 4, 20, 5, 3, 8, 10, 60, 1, 120, '2-1-2', 'Sem hiperextender lombar.'),
('Legs - Panturrilha em pe', 40, 15, 4, 21, 6, 1, 12, 15, 40, 2, 60, '2-1-2', 'Amplitude completa.'),
('Legs - Panturrilha em pe', 40, 15, 4, 21, 6, 2, 12, 15, 40, 1, 60, '2-1-2', 'Pausa no topo.'),
('Legs - Panturrilha em pe', 40, 15, 4, 21, 6, 3, 12, 15, 40, 1, 60, '2-1-2', 'Controle total.'),
('Legs - Crunch', 0, 15, 4, 23, 7, 1, 12, 15, 0, 2, 60, '2-1-2', 'Core ao final.'),
('Legs - Crunch', 0, 15, 4, 23, 7, 2, 12, 15, 0, 2, 60, '2-1-2', 'Sem puxar pescoco.');

-- 8) Opcional para apresentacao: deixar o usuario 1 com treino ativo padrao.
-- Se voce quiser obrigar o usuario a clicar no app em "Iniciar treino", comente este bloco.
UPDATE usuario_treino SET ativo = 0 WHERE usuario_id = 1;
INSERT INTO usuario_treino (usuario_id, treino_id, ativo, data_inicio)
VALUES (1, 1, 1, CURDATE());

-- 9) Conferencias uteis
SELECT 'exercicios' AS tabela, COUNT(*) AS total FROM exercicio
UNION ALL SELECT 'treinos', COUNT(*) FROM treino
UNION ALL SELECT 'treino_exercicio', COUNT(*) FROM treino_exercicio
UNION ALL SELECT 'treino_serie', COUNT(*) FROM treino_serie
UNION ALL SELECT 'usuario_treino_ativo_usuario_1', COUNT(*) FROM usuario_treino WHERE usuario_id = 1 AND ativo = 1;
