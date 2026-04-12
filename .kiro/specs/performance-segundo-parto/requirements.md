# Documento de Requisitos — Controle de Performance do 2º Parto

## Introdução

Este módulo identifica fêmeas primíparas em risco de queda de desempenho no 2º parto no SuinoGestor. Sugere intervenções de manejo baseadas em critérios zootécnicos (IDE, perda de peso, idade na primeira cobertura) para proteger a longevidade produtiva dessas fêmeas.

---

## Glossário

- **Primípara**: Fêmea que pariu pela primeira vez (paridade = 1).
- **Paridade**: Número de partos já realizados por uma fêmea.
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura; KPI reprodutivo primário.
- **Desmame**: Separação dos leitões da mãe.
- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 a 5.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 11: Controle de Performance do 2º Parto

**User Story:** Como produtor, quero que o sistema identifique fêmeas primíparas em risco de queda de desempenho no 2º parto e sugira intervenções de manejo, para que eu proteja a longevidade produtiva dessas fêmeas.

#### Critérios de Aceitação

1. WHEN uma fêmea primípara for desmamada, THE Sistema SHALL calcular e registrar: peso no desmame, perda de peso na lactação (%) e IDE até a próxima cobertura.
2. WHEN o IDE de uma fêmea primípara for inferior a 11 dias, THE Motor_de_Alertas SHALL gerar o alerta "Considerar atraso na cobertura da primípara — coberturas após 11–12 dias do desmame aumentam em 2,2 a 3,4 leitões nascidos vivos no 2º parto".
3. WHEN uma fêmea primípara perder mais de 9% do peso corporal durante a primeira lactação, THE Motor_de_Alertas SHALL gerar o alerta "Desgaste excessivo na 1ª lactação — risco de queda no 2º parto; revisar manejo nutricional".
4. THE Sistema SHALL exibir na Ficha_da_Matriz de fêmeas de 1º e 2º parto o indicador "Risco de queda no 2º parto" com base nos critérios: cobertura antes dos 210 dias de idade, perda de peso > 9% na 1ª lactação ou IDE < 11 dias pós-1º desmame.
5. WHEN o número de nascidos vivos no 2º parto for inferior ao número de nascidos vivos no 1º parto em mais de 2 leitões, THE Sistema SHALL registrar o evento como "Queda de desempenho no 2º parto" no histórico da fêmea.
