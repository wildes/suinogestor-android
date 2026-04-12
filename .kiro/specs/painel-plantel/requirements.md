# Documento de Requisitos — Painel do Plantel e Indicadores Reprodutivos

## Introdução

Este módulo implementa o painel consolidado do plantel no SuinoGestor. Exibe os principais indicadores reprodutivos calculados automaticamente e a situação atual de cada fêmea, permitindo ao produtor tomar decisões de manejo com base em dados atualizados.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez.
- **Marrã**: Fêmea suína virgem de reposição.
- **Paridade**: Número de partos já realizados por uma fêmea.
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura; KPI reprodutivo primário.
- **Taxa de Fertilidade**: Percentual de coberturas que resultam em parto confirmado; meta ≥ 85%.
- **Taxa de Reposição**: Percentual do plantel de fêmeas renovado anualmente; meta entre 35% e 45%.
- **Plantel**: Conjunto total de animais reprodutivos da granja.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Painel_do_Plantel**: Tela principal do módulo, exibindo visão consolidada do rebanho reprodutivo.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 9: Painel do Plantel e Indicadores Reprodutivos

**User Story:** Como produtor, quero visualizar em uma tela consolidada os principais indicadores reprodutivos do plantel e a situação atual de cada fêmea, para que eu possa tomar decisões de manejo com base em dados atualizados.

#### Critérios de Aceitação

1. THE Painel_do_Plantel SHALL exibir os seguintes indicadores calculados com base nos registros dos últimos 12 meses: número de partos por fêmea ao ano, taxa de fertilidade (%), taxa de repetição de cio (%), IDE médio (dias), média de nascidos vivos por parto e total de leitões desmamados por fêmea ao ano.
2. THE Painel_do_Plantel SHALL exibir o total de fêmeas por status atual: Marrã em preparação, Aguardando cobertura, Gestante (com subdivisão por terço), Lactante, Vazia e Descartada no período.
3. THE Sistema SHALL permitir ao Produtor filtrar a lista de fêmeas por status, paridade, data de cobertura prevista e data de parto prevista.
4. WHEN o indicador "Partos por fêmea ao ano" calculado for inferior a 2,29, THE Motor_de_Alertas SHALL exibir o aviso "Índice de partos abaixo do alvo mínimo (2,29 partos/fêmea/ano)".
5. WHEN o indicador "Leitões desmamados por fêmea ao ano" calculado for inferior a 22, THE Motor_de_Alertas SHALL exibir o aviso "Produtividade abaixo do alvo (22 leitões desmamados/fêmea/ano)".
6. THE Sistema SHALL exibir a meta semanal de coberturas calculada pela fórmula: (tamanho do plantel × partos/fêmea/ano) ÷ 52 semanas ÷ taxa de fertilidade.
7. WHEN o número de coberturas registradas na semana atual for inferior à meta semanal calculada, THE Motor_de_Alertas SHALL gerar o alerta "Meta de coberturas da semana não atingida — [N] coberturas realizadas de [meta] programadas".
8. THE Sistema SHALL exibir a lista de fêmeas com alertas pendentes em ordem de prioridade, com destaque visual para alertas críticos.
