# Documento de Requisitos — Preparação e Controle de Marrãs para Primeira Cobertura

## Introdução

Este módulo acompanha o desenvolvimento das marrãs de reposição no SuinoGestor, emitindo alertas quando elas atingem as condições ideais para a primeira cobertura. O objetivo é evitar coberturas em fêmeas imaturas, que comprometem o desempenho do 2º parto e a longevidade produtiva do plantel.

---

## Glossário

- **Marrã**: Fêmea suína virgem de reposição, ainda não coberta ou que ainda não pariu; também chamada de leitoa de reposição.
- **Paridade**: Número de partos já realizados por uma fêmea (0 = marrã).
- **Cio / Estro**: Período de receptividade sexual da fêmea, detectado por comportamento e reflexo de tolerância ao macho.
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN).
- **Flushing**: Aumento do aporte energético (ração à vontade) nos 10–14 dias pré-cobertura para elevar a taxa ovulatória.
- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 a 5.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Calculadora_Reprodutiva**: Componente do Sistema responsável por calcular datas e indicadores derivados de eventos reprodutivos.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 4: Preparação e Controle de Marrãs para Primeira Cobertura

**User Story:** Como produtor, quero acompanhar o desenvolvimento das marrãs de reposição e receber alertas quando elas atingirem as condições ideais para a primeira cobertura, para que eu não cubra fêmeas imaturas e comprometa o desempenho do 2º parto.

#### Critérios de Aceitação

1. THE Sistema SHALL rastrear para cada marrã: idade em dias, peso atual, ECC atual e número de cios detectados.
2. WHEN uma marrã atingir 145 dias de idade, THE Motor_de_Alertas SHALL gerar o alerta "Iniciar exposição ao rufião — estimulação da puberdade recomendada a partir de 145–155 dias".
3. WHEN uma marrã atingir simultaneamente idade ≥ 210 dias, peso ≥ 140 kg e ao menos o 2º cio detectado, THE Motor_de_Alertas SHALL gerar o alerta "Marrã apta para primeira cobertura".
4. IF o Produtor registrar uma cobertura em uma marrã com idade inferior a 210 dias, THEN THE Motor_de_Alertas SHALL exibir o aviso "Cobertura precoce — risco de redução de até 2,3 leitões no 2º parto e alta taxa de retorno ao cio".
5. IF o Produtor registrar uma cobertura em uma marrã com peso inferior a 140 kg, THEN THE Motor_de_Alertas SHALL exibir o aviso "Peso abaixo do mínimo recomendado (140 kg) para primeira cobertura".
6. WHEN o Produtor registrar o início do flushing de uma marrã, THE Calculadora_Reprodutiva SHALL calcular e exibir a data prevista de cobertura como 10 a 14 dias após o início do flushing.
7. WHERE o Produtor optar por registrar o número de cios da marrã, THE Sistema SHALL recomendar que a cobertura ocorra preferencialmente no 2º ou 3º cio detectado.
