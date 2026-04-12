# Documento de Requisitos — Diagnóstico e Controle de Gestação

## Introdução

Este módulo implementa o registro do diagnóstico de gestação e o acompanhamento das fêmeas gestantes por fase no SuinoGestor. Permite identificar fêmeas vazias precocemente e gerenciar a nutrição por terço gestacional, maximizando a eficiência reprodutiva do plantel.

---

## Glossário

- **Gestação**: Período de aproximadamente 114 dias entre a cobertura e o parto.
- **Diagnóstico de Gestação**: Confirmação de prenhez por ultrassom, observação de retorno ao cio ou outro método.
- **Fêmea Vazia**: Fêmea não gestante e não lactante; indica ineficiência reprodutiva.
- **Terço Gestacional**: Divisão da gestação em três fases: 1º terço (0–38 dias), 2º terço (39–75 dias), 3º terço (76–114 dias).
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN).
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Painel_do_Plantel**: Tela principal do módulo, exibindo visão consolidada do rebanho reprodutivo.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 6: Diagnóstico e Controle de Gestação

**User Story:** Como produtor, quero registrar o diagnóstico de gestação e acompanhar as fêmeas gestantes por fase, para que eu possa identificar fêmeas vazias precocemente e gerenciar a nutrição por terço gestacional.

#### Critérios de Aceitação

1. WHEN o Produtor registrar o diagnóstico de gestação de uma fêmea, THE Sistema SHALL registrar: data do diagnóstico, método utilizado (ultrassom, observação de retorno ao cio ou outro) e resultado (positivo ou negativo).
2. WHEN o diagnóstico de gestação for negativo, THE Sistema SHALL alterar o status da fêmea para "Vazia" e THE Motor_de_Alertas SHALL gerar o alerta "Fêmea [identificação] vazia — programar nova cobertura ou avaliar descarte".
3. WHILE uma fêmea estiver com status "Gestante", THE Sistema SHALL exibir na Ficha_da_Matriz: dias de gestação decorridos, fase gestacional atual (1º terço: 0–38 dias; 2º terço: 39–75 dias; 3º terço: 76–114 dias) e data prevista do parto.
4. WHEN uma fêmea gestante atingir 70 dias de gestação, THE Motor_de_Alertas SHALL gerar o alerta "Iniciar aumento de ração — fêmea no terço final da gestação (aporte de 2,6 a 3,2 kg/dia recomendado)".
5. WHEN uma fêmea gestante atingir 110 dias de gestação, THE Motor_de_Alertas SHALL gerar o alerta "Pré-parto — reduzir ração gradativamente até 0,5–1 kg no dia do parto; transferir para maternidade".
6. WHEN uma fêmea gestante atingir 30 dias após a cobertura sem diagnóstico de gestação registrado, THE Motor_de_Alertas SHALL gerar o alerta "Diagnóstico de gestação pendente para [identificação]".
7. THE Sistema SHALL manter o registro de todas as fêmeas com status "Vazia" (não gestante e não lactante) e exibir o total no Painel_do_Plantel como indicador de eficiência.
