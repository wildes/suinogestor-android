# Documento de Requisitos — Cadastro e Identificação Individual de Fêmeas

## Introdução

Este módulo implementa o cadastro e identificação individual de cada fêmea (matriz ou marrã) no SuinoGestor. É a base de dados que sustenta todo o rastreamento reprodutivo individual, transformando a escrituração manual em fichas de papel em registros digitais estruturados.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez; identificada individualmente por brinco ou tatuagem.
- **Marrã**: Fêmea suína virgem de reposição, ainda não coberta ou que ainda não pariu; também chamada de leitoa de reposição.
- **Paridade**: Número de partos já realizados por uma fêmea (0 = marrã, 1 = primípara, 2+ = multípara).
- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 (muito magro) a 5 (muito gordo).
- **Plantel**: Conjunto total de animais reprodutivos da granja (matrizes + marrãs + varrões).
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Calculadora_Reprodutiva**: Componente do Sistema responsável por calcular datas e indicadores derivados de eventos reprodutivos.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.

---

## Requisitos

### Requisito 1: Cadastro e Identificação Individual de Fêmeas

**User Story:** Como produtor, quero cadastrar cada fêmea do plantel com seus dados de identificação e origem, para que eu possa rastrear individualmente o histórico reprodutivo de cada animal.

#### Critérios de Aceitação

1. THE Sistema SHALL exigir os seguintes campos obrigatórios no cadastro de uma fêmea: número de identificação (brinco ou tatuagem), data de nascimento, raça/linhagem genética e categoria inicial (marrã ou matriz).
2. THE Sistema SHALL aceitar campos opcionais no cadastro: peso de entrada, ECC atual (escala 1–5), origem (granja própria ou fornecedor externo).
3. WHEN o Produtor informar a data de nascimento de uma marrã, THE Calculadora_Reprodutiva SHALL calcular e exibir automaticamente a idade no formato ex: 1a3m15d para 1 ano 3 meses e 15 dias.
4. IF o Produtor tentar cadastrar uma fêmea com número de identificação já existente no plantel, THEN THE Sistema SHALL rejeitar o cadastro e exibir a mensagem "Identificação já cadastrada no plantel".
5. THE Sistema SHALL permitir o registro de foto do animal vinculada à Ficha_da_Matriz.
6. WHEN uma fêmea for cadastrada como marrã com idade inferior a 160 dias, THE Motor_de_Alertas SHALL exibir o aviso "Fêmea abaixo da idade mínima de preparação reprodutiva (160 dias)".
