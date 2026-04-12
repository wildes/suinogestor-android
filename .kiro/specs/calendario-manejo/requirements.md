# Documento de Requisitos — Calendário de Manejo Reprodutivo

## Introdução

Este módulo implementa o calendário de manejo reprodutivo no SuinoGestor. Exibe todas as ações previstas para os próximos dias (coberturas, diagnósticos, partos, desmames) calculadas automaticamente a partir dos eventos registrados, permitindo ao produtor organizar a rotina da granja com antecedência.

---

## Glossário

- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN).
- **Gestação**: Período de aproximadamente 114 dias entre a cobertura e o parto.
- **Desmame**: Separação dos leitões da mãe; ocorre tipicamente entre 21 e 28 dias de vida.
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura.
- **Diagnóstico de Gestação**: Confirmação de prenhez; recomendado 30 dias após a cobertura.
- **Vazio Sanitário**: Período de descanso e desinfecção entre lotes.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 10: Calendário de Manejo Reprodutivo

**User Story:** Como produtor, quero visualizar um calendário com todas as ações de manejo previstas para os próximos dias (coberturas, diagnósticos, partos, desmames), para que eu possa organizar a rotina da granja com antecedência.

#### Critérios de Aceitação

1. THE Sistema SHALL exibir um calendário com as seguintes ações previstas calculadas automaticamente: coberturas programadas (baseadas em IDE esperado pós-desmame), diagnósticos de gestação pendentes (30 dias pós-cobertura), retornos ao cio esperados (18–24 dias pós-cobertura), partos previstos (114 dias pós-cobertura) e desmames previstos (21 dias pós-parto por padrão, configurável pelo Produtor).
2. THE Sistema SHALL permitir ao Produtor configurar o intervalo padrão de desmame da granja entre 18 e 28 dias.
3. WHEN uma ação de manejo prevista não for registrada até 2 dias após a data calculada, THE Motor_de_Alertas SHALL gerar o alerta "Ação pendente: [tipo de ação] para [identificação da fêmea] — [N] dias em atraso".
4. THE Sistema SHALL permitir visualizar o calendário nas visões: dia, semana e mês.
5. WHEN o Produtor selecionar um evento no calendário, THE Sistema SHALL exibir o resumo da Ficha_da_Matriz da fêmea correspondente e o botão de registro da ação.
6. THE Sistema SHALL destacar no calendário os dias com concentração de partos previstos superior a 20% da capacidade de maternidade configurada pelo Produtor.
