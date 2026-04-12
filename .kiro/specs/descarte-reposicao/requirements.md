# Documento de Requisitos — Gestão de Descarte e Reposição do Plantel

## Introdução

Este módulo implementa o registro de descartes com justificativa e o acompanhamento da taxa de reposição do plantel no SuinoGestor. O objetivo é manter a distribuição ideal de paridades e o fluxo contínuo de produção, garantindo que o plantel esteja sempre no pico produtivo.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez.
- **Marrã**: Fêmea suína virgem de reposição.
- **Paridade**: Número de partos já realizados por uma fêmea (0 = marrã, 1 = primípara, 2+ = multípara).
- **Descarte**: Retirada definitiva de um animal do plantel reprodutivo, por venda ou abate.
- **Taxa de Reposição**: Percentual do plantel de fêmeas renovado anualmente; meta entre 35% e 45%.
- **Plantel**: Conjunto total de animais reprodutivos da granja (matrizes + marrãs + varrões).
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Painel_do_Plantel**: Tela principal do módulo, exibindo visão consolidada do rebanho reprodutivo.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 8: Gestão de Descarte e Reposição do Plantel

**User Story:** Como produtor, quero registrar descartes com justificativa e acompanhar a taxa de reposição do plantel, para que eu mantenha a distribuição ideal de paridades e o fluxo contínuo de produção.

#### Critérios de Aceitação

1. WHEN o Produtor registrar o descarte de uma fêmea, THE Sistema SHALL exigir a seleção de ao menos uma justificativa dentre: problema reprodutivo (repetição de cio, anestro, aborto), baixa produção (leitegadas pequenas em 3 partos consecutivos), problema locomotor (casco/aprumos), problema sanitário crônico, idade avançada (paridade ≥ 8) ou outro (campo livre).
2. THE Sistema SHALL registrar a data de descarte e manter o histórico completo da fêmea descartada para consulta futura.
3. WHEN o Produtor registrar o descarte de uma fêmea, THE Sistema SHALL atualizar automaticamente o Painel_do_Plantel com o novo total de fêmeas ativas.
4. THE Sistema SHALL calcular e exibir mensalmente a taxa de reposição anualizada do plantel, alertando quando estiver abaixo de 35% ou acima de 45%.
5. THE Sistema SHALL exibir no Painel_do_Plantel a distribuição atual do plantel por paridade, comparando com a distribuição ideal: ~15% marrãs, ~18% 1º parto, ~49% 2º ao 5º parto, ~18% 6º parto em diante.
6. WHEN a proporção de fêmeas entre o 2º e o 5º parto for inferior a 50% do plantel ativo, THE Motor_de_Alertas SHALL gerar o alerta "Distribuição de paridades fora do ideal — menos de 50% do plantel no pico produtivo (2º–5º parto)".
7. IF o Produtor tentar registrar o descarte de uma fêmea com status "Gestante" ou "Lactante", THEN THE Sistema SHALL exibir o aviso "Fêmea em fase ativa — confirmar descarte?" antes de prosseguir.
8. THE Sistema SHALL calcular e exibir a meta semanal de reposição com base no tamanho do plantel e na taxa de reposição configurada pelo Produtor (padrão: 40% ao ano).
