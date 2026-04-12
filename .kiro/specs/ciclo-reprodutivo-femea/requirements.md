# Documento de Requisitos — Registro e Controle do Ciclo Reprodutivo da Fêmea

## Introdução

Este módulo implementa o registro de todos os eventos do ciclo reprodutivo de cada fêmea no SuinoGestor: detecção de cio, cobertura, diagnóstico de gestação, parto e desmame. O sistema calcula automaticamente datas críticas e alertas para que o produtor não perca nenhuma janela de manejo.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez.
- **Marrã**: Fêmea suína virgem de reposição, ainda não coberta ou que ainda não pariu.
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN) registrado com data, reprodutor e tipo de serviço.
- **Cio / Estro**: Período de receptividade sexual da fêmea, detectado por comportamento e reflexo de tolerância ao macho.
- **Gestação**: Período de aproximadamente 114 dias entre a cobertura e o parto.
- **Desmame**: Separação dos leitões da mãe; ocorre tipicamente entre 21 e 28 dias de vida.
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura; KPI reprodutivo primário.
- **Natimorto**: Leitão nascido morto; taxa alvo < 4% dos nascidos totais.
- **Mumificado**: Feto que morreu durante a gestação e foi reabsorvido parcialmente.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Calculadora_Reprodutiva**: Componente do Sistema responsável por calcular datas e indicadores derivados de eventos reprodutivos.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 3: Registro e Controle do Ciclo Reprodutivo da Fêmea

**User Story:** Como produtor, quero registrar todos os eventos do ciclo reprodutivo de cada fêmea (cio, cobertura, diagnóstico de gestação, parto, desmame), para que o sistema calcule automaticamente as datas críticas e eu não perca nenhuma janela de manejo.

#### Critérios de Aceitação

1. THE Sistema SHALL registrar os seguintes eventos reprodutivos na Ficha_da_Matriz: detecção de cio, cobertura (IA ou MN), diagnóstico de gestação (positivo/negativo), parto e desmame.
2. WHEN o Produtor registrar uma cobertura, THE Sistema SHALL exigir: data da cobertura, identificação do reprodutor utilizado, tipo de serviço (IA ou MN) e número de ordem da cobertura no cio (1ª, 2ª ou 3ª).
3. WHEN o Produtor registrar a primeira cobertura de um cio, THE Calculadora_Reprodutiva SHALL calcular e exibir automaticamente: data provável do parto (data da cobertura + 114 dias), data de retorno ao cio regular esperado (data da cobertura + 18 a 24 dias) e data de diagnóstico de gestação recomendado (data da cobertura + 30 dias).
4. WHEN o Produtor registrar o desmame de uma leitegada, THE Calculadora_Reprodutiva SHALL calcular e exibir o IDE (número de dias entre o desmame e a próxima cobertura registrada) assim que a nova cobertura for lançada.
5. WHEN o IDE calculado for superior a 7 dias, THE Motor_de_Alertas SHALL classificar a fêmea como "IDE longo — possível subfertilidade" e registrar o evento no histórico.
6. WHEN o Produtor registrar um parto, THE Sistema SHALL exigir o registro de: número de nascidos vivos, natimortos, mumificados e peso médio da leitegada ao nascer.
7. WHEN o número de natimortos registrado em um parto for superior a 4% do total de nascidos, THE Motor_de_Alertas SHALL gerar o alerta "Taxa de natimortos acima do alvo (4%) — verificar manejo de maternidade".
8. THE Sistema SHALL permitir registrar múltiplas coberturas por cio (até 3), associando um reprodutor diferente a cada serviço.
9. WHEN o Produtor registrar o desmame com menos de 20 dias de lactação, THE Motor_de_Alertas SHALL gerar o alerta "Desmame precoce (< 20 dias) — alto risco de repetição de cio e falha na próxima concepção".
