# Documento de Requisitos — Controle de Repetição de Cio

## Introdução

Este módulo implementa a identificação e classificação automática de repetições de cio no SuinoGestor. Permite ao produtor diagnosticar a causa provável de falhas de concepção e tomar a decisão correta entre recobertura ou descarte, além de monitorar a taxa de repetição do plantel como indicador de saúde reprodutiva coletiva.

---

## Glossário

- **Cio / Estro**: Período de receptividade sexual da fêmea.
- **Repetição de Cio**: Retorno ao estro após cobertura, indicando falha na concepção; classificada como regular (17–24 dias) ou irregular (25–60 dias).
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN).
- **Taxa de Fertilidade**: Percentual de coberturas que resultam em parto confirmado; meta ≥ 85%.
- **Plantel**: Conjunto total de animais reprodutivos da granja.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea.
- **Calculadora_Reprodutiva**: Componente do Sistema responsável por calcular datas e indicadores derivados de eventos reprodutivos.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 7: Controle de Repetição de Cio

**User Story:** Como produtor, quero que o sistema identifique e classifique automaticamente as repetições de cio, para que eu possa diagnosticar a causa provável e tomar a decisão correta de recobertura ou descarte.

#### Critérios de Aceitação

1. WHEN o Produtor registrar um novo cio em uma fêmea que já foi coberta, THE Calculadora_Reprodutiva SHALL calcular o intervalo em dias desde a última cobertura e classificar automaticamente como: "Regular (17–24 dias) — possível causa: estresse, nutrição ou manejo" ou "Irregular (25–60 dias) — possível causa: infecção, doença reprodutiva ou micotoxina".
2. WHEN uma fêmea acumular 3 repetições de cio com reprodutores diferentes, THE Motor_de_Alertas SHALL gerar o alerta "Fêmea [identificação] com 3 repetições de cio — considerar descarte por infertilidade".
3. WHEN a taxa de repetição de cio do plantel calculada no fechamento semanal ultrapassar 10%, THE Motor_de_Alertas SHALL gerar o alerta "Taxa de repetição de cio acima do alvo (10%) — investigar causas sistêmicas".
4. WHEN a taxa de repetição de cio do plantel ultrapassar 25%, THE Motor_de_Alertas SHALL gerar o alerta crítico "Taxa de repetição de cio crítica (> 25%) — falha grave no manejo reprodutivo; verificar desmame precoce, estresse calórico e qualidade dos reprodutores".
5. THE Sistema SHALL exibir no histórico de cada fêmea o número total de repetições de cio e a classificação de cada episódio (regular ou irregular).
6. WHEN uma repetição de cio irregular for registrada no intervalo de 35 a 60 dias, THE Motor_de_Alertas SHALL sugerir "Intervalo compatível com Brucelose — consultar médico veterinário".
7. WHEN uma repetição de cio irregular for registrada no intervalo de 30 a 38 dias, THE Motor_de_Alertas SHALL sugerir "Intervalo compatível com Leptospirose — consultar médico veterinário".
