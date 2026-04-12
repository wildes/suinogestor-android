# Documento de Requisitos — Cadastro e Identificação Individual de Reprodutores

## Introdução

Este módulo implementa o cadastro e identificação individual de cada varrão (reprodutor) no SuinoGestor. Permite gerenciar a utilização dos machos, controlar frequência de uso e evitar super ou subutilização, preservando a qualidade seminal e a eficiência reprodutiva do plantel.

---

## Glossário

- **Varrão / Reprodutor**: Macho suíno utilizado em monta natural (MN) ou coleta de sêmen para inseminação artificial (IA).
- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 (muito magro) a 5 (muito gordo).
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN) registrado com data, reprodutor e tipo de serviço.
- **Plantel**: Conjunto total de animais reprodutivos da granja (matrizes + marrãs + varrões).
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_do_Reprodutor**: Registro digital individual de um varrão, contendo histórico de uso e avaliações.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 2: Cadastro e Identificação Individual de Reprodutores

**User Story:** Como produtor, quero cadastrar cada varrão do plantel com seus dados de identificação e controle de uso, para que eu possa gerenciar a utilização dos machos e evitar super ou subutilização.

#### Critérios de Aceitação

1. THE Sistema SHALL exigir os seguintes campos obrigatórios no cadastro de um reprodutor: número de identificação, data de nascimento, raça/linhagem genética e tipo de uso (monta natural, coleta para IA ou ambos).
2. THE Sistema SHALL registrar para cada reprodutor: peso atual, ECC atual (escala 1–5).
3. WHEN o Produtor registrar o peso de um reprodutor, THE Sistema SHALL calcular e exibir a quantidade de ração diária recomendada como 1% do peso vivo em kg.
4. WHEN um reprodutor ficar mais de 15 dias sem nenhuma cobertura registrada, THE Motor_de_Alertas SHALL gerar o alerta "Reprodutor [identificação] há [N] dias sem uso — risco de queda na motilidade espermática".
5. WHEN a soma de coberturas registradas de um reprodutor adulto (acima de 300 dias) ultrapassar 6 montas na mesma semana, THE Motor_de_Alertas SHALL gerar o alerta "Limite semanal de uso atingido para o reprodutor [identificação] — risco de superutilização".
6. WHEN um reprodutor em treinamento (entre 210 e 300 dias de idade) tiver mais de 1 cobertura registrada na mesma semana, THE Motor_de_Alertas SHALL gerar o alerta "Reprodutor em treinamento — máximo de 1 monta por semana recomendado".
7. IF o Produtor tentar cadastrar um reprodutor com número de identificação já existente no plantel, THEN THE Sistema SHALL rejeitar o cadastro e exibir a mensagem "Identificação já cadastrada no plantel".
