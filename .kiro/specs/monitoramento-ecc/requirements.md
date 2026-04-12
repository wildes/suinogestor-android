# Documento de Requisitos — Monitoramento do Escore de Condição Corporal (ECC)

## Introdução

Este módulo implementa o registro e acompanhamento do ECC (Escore de Condição Corporal) de fêmeas e reprodutores no SuinoGestor. O ECC é um indicador nutricional crítico: desvios nos momentos-chave do ciclo reprodutivo (parto, desmame) causam perdas diretas em fertilidade, produção de leite e longevidade produtiva.

---

## Glossário

- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 (muito magro) a 5 (muito gordo).
- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez.
- **Marrã**: Fêmea suína virgem de reposição.
- **Varrão / Reprodutor**: Macho suíno utilizado em monta natural ou inseminação artificial.
- **Paridade**: Número de partos já realizados por uma fêmea (1 = primípara, 2+ = multípara).
- **Desmame**: Separação dos leitões da mãe.
- **Anestro**: Ausência de cio após o desmame; considerado prolongado quando superior a 7 dias.
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 5: Monitoramento do Escore de Condição Corporal (ECC)

**User Story:** Como produtor, quero registrar e acompanhar o ECC de cada fêmea e reprodutor ao longo do ciclo, para que eu possa ajustar a nutrição e evitar perdas reprodutivas por sub ou sobre-condicionamento.

#### Critérios de Aceitação

1. THE Sistema SHALL aceitar o registro de ECC em escala inteira de 1 a 5 para qualquer fêmea ou reprodutor, vinculado a uma data de avaliação.
2. THE Sistema SHALL exibir na Ficha_da_Matriz o histórico de ECC com data de cada avaliação.
3. WHEN o ECC registrado de uma fêmea no momento do desmame for inferior a 3, THE Motor_de_Alertas SHALL gerar o alerta "ECC abaixo do ideal no desmame — risco de anestro prolongado e IDE longo".
4. WHEN o ECC registrado de uma fêmea no momento do parto for inferior a 3 ou superior a 4, THE Motor_de_Alertas SHALL gerar o alerta "ECC fora do intervalo ideal no parto (3–4) — risco de complicações no parto ou baixa produção de leite".
5. WHEN o ECC registrado de um reprodutor for superior a 4, THE Motor_de_Alertas SHALL gerar o alerta "Reprodutor sobre-condicionado (ECC > 4) — risco de perda de libido e problemas de aprumos".
6. WHEN uma fêmea primípara (1º parto) perder mais de 9% do peso corporal entre o parto e o desmame, THE Motor_de_Alertas SHALL gerar o alerta "Perda de peso excessiva em primípara — comprometimento do próximo ciclo reprodutivo".
7. WHEN uma fêmea multípara perder mais de 15% do peso corporal entre o parto e o desmame, THE Motor_de_Alertas SHALL gerar o alerta "Perda de peso excessiva — risco de anestro e redução da leitegada seguinte".
