-- PHYSIQUE - PLANOS DO ONBOARDING SEM CORTE POR FREQUÊNCIA
-- Ideia corrigida:
-- 1) A frequência semanal NÃO limita a quantidade de treinos do plano.
-- 2) Quem define a quantidade de treinos é o vínculo plano_treino_item.
-- 3) O app deve mostrar os treinos do plano ativo do aluno, não o catálogo inteiro.
-- 4) Se um professor/plano novo for ativado, os treinos antigos deixam de aparecer.

CREATE TABLE IF NOT EXISTS plano_treino (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  objetivo VARCHAR(120) NOT NULL,
  resumo VARCHAR(600),
  nivel VARCHAR(80),
  frequencia_semanal VARCHAR(80),
  foco VARCHAR(160),
  tags VARCHAR(500),
  metodologias VARCHAR(500),
  ordem INT NOT NULL DEFAULT 0,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS plano_treino_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  plano_treino_id BIGINT NOT NULL,
  treino_id BIGINT NOT NULL,
  ordem INT NOT NULL DEFAULT 1,
  nome_exibicao VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_plano_treino_item_plano (plano_treino_id),
  KEY idx_plano_treino_item_treino (treino_id),
  CONSTRAINT fk_plano_treino_item_plano FOREIGN KEY (plano_treino_id) REFERENCES plano_treino(id),
  CONSTRAINT fk_plano_treino_item_treino FOREIGN KEY (treino_id) REFERENCES treino(id)
);

CREATE TABLE IF NOT EXISTS usuario_plano_treino (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  plano_treino_id BIGINT NOT NULL,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_usuario_plano_treino_usuario (usuario_id),
  KEY idx_usuario_plano_treino_plano (plano_treino_id),
  CONSTRAINT fk_usuario_plano_treino_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
  CONSTRAINT fk_usuario_plano_treino_plano FOREIGN KEY (plano_treino_id) REFERENCES plano_treino(id)
);

-- Desativa planos antigos do onboarding para evitar que um cadastro novo pegue plano antigo poluído.
UPDATE plano_treino SET ativo = 0;

-- Recria os planos oficiais do onboarding com vínculos enxutos e explícitos.
DELETE FROM plano_treino_item WHERE plano_treino_id BETWEEN 201 AND 212;
DELETE FROM plano_treino WHERE id BETWEEN 201 AND 212;

INSERT INTO plano_treino (id, nome, objetivo, resumo, nivel, frequencia_semanal, foco, tags, metodologias, ordem, ativo) VALUES
(201, 'Base completa segura', 'Começar com segurança', 'Um começo organizado para aprender os movimentos principais sem excesso de variações.', 'iniciante', '3x por semana', 'base técnica, confiança e adaptação', 'Iniciante|Técnica guiada|Base segura', 'Séries tradicionais|Descanso guiado|Progressão simples', 1, 1),
(202, 'Máquinas e técnica', 'Começar com segurança', 'Plano direto para ganhar confiança usando máquinas, postura estável e execução controlada.', 'iniciante', '3x por semana', 'máquinas, postura e controle', 'Máquinas|Controle|Baixa complexidade', 'Execução guiada|Movimentos estáveis|Progressão simples', 2, 1),
(203, 'Corpo inteiro leve', 'Começar com segurança', 'Rotina simples para criar constância e preparar o corpo para evoluir sem pressa.', 'iniciante', '3x por semana', 'corpo inteiro e constância', 'Corpo inteiro|Constância|Adaptação', 'Movimentos simples|Volume leve|Descanso inteligente', 3, 1),

(204, 'Base de hipertrofia', 'Ganhar massa muscular', 'Base sólida para ganhar massa com exercícios essenciais, técnica consistente e progressão clara.', 'iniciante', '3x por semana', 'ganho de massa com técnica e constância', 'Hipertrofia|Progressão de carga|Base', 'Séries tradicionais|Progressão simples|Descanso guiado', 1, 1),
(205, 'Hipertrofia com volume', 'Ganhar massa muscular', 'Mais estímulo para quem já treina e quer evoluir com volume melhor distribuído.', 'intermediário', '4x por semana', 'mais volume para superiores e pernas', 'Hipertrofia|Volume moderado|Progressão', 'Progressão de carga|Bi-set pontual|Séries tradicionais', 2, 1),
(206, 'Massa e força base', 'Ganhar massa muscular', 'Plano para ganhar massa sem abandonar força, com movimentos principais e evolução gradual.', 'intermediário', '3x por semana', 'força nos básicos com hipertrofia complementar', 'Massa muscular|Força base|Progressão', 'Foco nos básicos|Progressão gradual|Descanso inteligente', 3, 1),

(207, 'Definição início', 'Perder gordura e melhorar definição', 'Sessões objetivas para aumentar gasto, manter técnica e criar ritmo de treino.', 'iniciante', '3x por semana', 'ritmo, controle e gasto calórico', 'Definição|Ritmo|Controle', 'Densidade controlada|Descanso curto|Execução segura', 1, 1),
(208, 'Definição organizada', 'Perder gordura e melhorar definição', 'Estrutura para manter intensidade sem perder qualidade nos exercícios.', 'intermediário', '3x por semana', 'intensidade moderada e organização', 'Definição|Volume controlado|Organização', 'Full body|Descanso controlado|Progressão de densidade', 2, 1),
(209, 'Metabólico completo', 'Perder gordura e melhorar definição', 'Sessões mais densas para quem já treina, com controle de ritmo e execução.', 'avançado', '3x por semana', 'alta densidade com segurança', 'Metabólico|Alta densidade|Avançado', 'Circuitos controlados|Pausas curtas|Técnica preservada', 3, 1),

(210, 'Força base', 'Ficar mais forte', 'Plano para construir força nos movimentos principais com boa técnica e descanso adequado.', 'iniciante', '3x por semana', 'força nos básicos', 'Força|Básicos|Progressão', 'Séries tradicionais|Descanso longo|Progressão gradual', 1, 1),
(211, 'Força intermediária', 'Ficar mais forte', 'Divisão organizada para evoluir carga entre empurrar, puxar e pernas.', 'intermediário', '3x por semana', 'progressão de carga organizada', 'Força|Progressão|Intermediário', 'Push pull legs|Descanso longo|Controle de volume', 2, 1),
(212, 'Força avançada', 'Ficar mais forte', 'Plano exigente para quem já domina os básicos e quer progressão mais forte.', 'avançado', '3x por semana', 'cargas desafiadoras com controle', 'Força avançada|Periodização|Recuperação', 'Periodização simples|Movimentos principais|Recuperação planejada', 3, 1);

INSERT INTO plano_treino_item (plano_treino_id, treino_id, ordem, nome_exibicao) VALUES
(201, 122, 1, 'Treino A — Adaptação segura'),
(201, 123, 2, 'Treino B — Corpo inteiro leve'),
(201, 124, 3, 'Treino C — Máquinas e técnica'),

(202, 124, 1, 'Treino A — Máquinas e técnica'),
(202, 125, 2, 'Treino B — Corpo inteiro leve'),
(202, 126, 3, 'Treino C — Postura e base'),

(203, 125, 1, 'Treino A — Corpo inteiro leve'),
(203, 126, 2, 'Treino B — Postura e base'),
(203, 128, 3, 'Treino C — Superiores base segura'),

(204, 101, 1, 'Treino A — Peito, ombros e tríceps'),
(204, 102, 2, 'Treino B — Costas e bíceps'),
(204, 103, 3, 'Treino C — Pernas completas'),

(205, 104, 1, 'Treino A — Peito, ombros e tríceps'),
(205, 105, 2, 'Treino B — Costas e bíceps'),
(205, 106, 3, 'Treino C — Pernas completas'),
(205, 107, 4, 'Treino D — Corpo inteiro para massa'),

-- Exemplo proposital: plano 3x por semana com 4 treinos possíveis.
-- O treinador pode ter 4 sessões no plano e o aluno executa 3 na semana conforme agenda/orientação.
(206, 115, 1, 'Treino A — Corpo inteiro força base'),
(206, 116, 2, 'Treino B — Supino e puxadas'),
(206, 117, 3, 'Treino C — Pernas força base'),
(206, 118, 4, 'Treino D — Peito e ombros força'),

(207, 108, 1, 'Treino A — Circuito corpo inteiro'),
(207, 109, 2, 'Treino B — Pernas e core'),
(207, 110, 3, 'Treino C — Superiores em ritmo'),

(208, 111, 1, 'Treino A — Corpo inteiro definição'),
(208, 112, 2, 'Treino B — Pernas e core definição'),
(208, 113, 3, 'Treino C — Superiores definição'),

(209, 111, 1, 'Treino A — Corpo inteiro definição'),
(209, 112, 2, 'Treino B — Pernas e core definição'),
(209, 114, 3, 'Treino C — Metabólico completo'),

(210, 115, 1, 'Treino A — Corpo inteiro força base'),
(210, 116, 2, 'Treino B — Supino e puxadas'),
(210, 117, 3, 'Treino C — Pernas força base'),

(211, 118, 1, 'Treino A — Peito e ombros força'),
(211, 119, 2, 'Treino B — Costas e bíceps força'),
(211, 120, 3, 'Treino C — Pernas força'),

(212, 121, 1, 'Treino A — Força avançada corpo inteiro'),
(212, 115, 2, 'Treino B — Base técnica pesada'),
(212, 117, 3, 'Treino C — Pernas força base');

-- Limpa vínculos antigos de usuários que já foram cadastrados com plano poluído.
-- Isso remove da lista do aluno qualquer treino que não pertença ao plano ativo atual dele.
DELETE ut
FROM usuario_treino ut
JOIN usuario_plano_treino upt ON upt.usuario_id = ut.usuario_id AND upt.ativo = 1
JOIN plano_treino p ON p.id = upt.plano_treino_id AND p.ativo = 1
LEFT JOIN plano_treino_item pti ON pti.plano_treino_id = p.id AND pti.treino_id = ut.treino_id
WHERE pti.id IS NULL;

-- Garante que o treino ativo do aluno seja um treino do plano ativo.
UPDATE usuario_treino ut
JOIN usuario_plano_treino upt ON upt.usuario_id = ut.usuario_id AND upt.ativo = 1
JOIN plano_treino p ON p.id = upt.plano_treino_id AND p.ativo = 1
LEFT JOIN plano_treino_item pti ON pti.plano_treino_id = p.id AND pti.treino_id = ut.treino_id
SET ut.ativo = 0
WHERE ut.ativo = 1 AND pti.id IS NULL;

-- Conferência final: cada plano ativo deve retornar apenas os treinos vinculados explicitamente abaixo.
SELECT p.id, p.objetivo, p.nome, p.frequencia_semanal, COUNT(pi.id) AS treinos_vinculados
FROM plano_treino p
LEFT JOIN plano_treino_item pi ON pi.plano_treino_id = p.id
WHERE p.ativo = 1
GROUP BY p.id, p.objetivo, p.nome, p.frequencia_semanal
ORDER BY p.objetivo, p.ordem;
