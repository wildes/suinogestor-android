# Documento de Requisitos

## Introdução

O módulo **Gestão do Plantel de Matrizes e Reprodutores** é o núcleo do SuinoGestor. Ele centraliza o rastreamento individualizado de cada fêmea (matriz ou marrã) e de cada macho reprodutor (varrão/cachaço) ao longo de todo o ciclo de vida produtivo, desde a entrada na granja até o descarte.

O módulo transforma a escrituração zootécnica manual — hoje feita em fichas de papel — em registros digitais estruturados, calculando automaticamente datas críticas, alertas de manejo e indicadores de desempenho reprodutivo. O objetivo central é maximizar o número de leitões desmamados por fêmea ao ano (meta de excelência: ≥ 30 leitões/fêmea/ano) por meio de decisões baseadas em dados precisos e atualizados.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez; identificada individualmente por brinco ou tatuagem.
- **Marrã**: Fêmea suína virgem de reposição, ainda não coberta ou que ainda não pariu; também chamada de leitoa de reposição.
- **Varrão / Reprodutor**: Macho suíno utilizado em monta natural (MN) ou coleta de sêmen para inseminação artificial (IA).
- **Paridade**: Número de partos já realizados por uma fêmea (0 = marrã, 1 = primípara, 2+ = multípara).
- **IDE (Intervalo Desmama-Cobertura)**: Número de dias entre o desmame e a próxima cobertura; KPI reprodutivo primário.
- **ECC (Escore de Condição Corporal)**: Avaliação visual/tátil da reserva energética do animal em escala de 1 (muito magro) a 5 (muito gordo).
- **Cobertura**: Ato de inseminação artificial (IA) ou monta natural (MN) registrado com data, reprodutor e tipo de serviço.
- **Cio / Estro**: Período de receptividade sexual da fêmea, detectado por comportamento e reflexo de tolerância ao macho.
- **Repetição de Cio**: Retorno ao estro após cobertura, indicando falha na concepção; classificada como regular (17–24 dias) ou irregular (25–60 dias).
- **Gestação**: Período de aproximadamente 114 dias entre a cobertura e o parto.
- **Desmame**: Separação dos leitões da mãe; ocorre tipicamente entre 21 e 28 dias de vida.
- **Descarte**: Retirada definitiva de um animal do plantel reprodutivo, por venda ou abate.
- **Flushing**: Aumento do aporte energético (ração à vontade) nos 10–14 dias pré-cobertura para elevar a taxa ovulatória.
- **Anestro**: Ausência de cio após o desmame; considerado prolongado quando superior a 7 dias.
- **MMA (Mamite-Metrite-Agalaxia)**: Síndrome pós-parto que compromete a produção de leite e a saúde da matriz.
- **Natimorto**: Leitão nascido morto; taxa alvo < 4% dos nascidos totais.
- **Mumificado**: Feto que morreu durante a gestação e foi reabsorvido parcialmente; indica falha reprodutiva.
- **Plantel**: Conjunto total de animais reprodutivos da granja (matrizes + marrãs + varrões).
- **Taxa de Fertilidade**: Percentual de coberturas que resultam em parto confirmado; meta ≥ 85%.
- **Taxa de Reposição**: Percentual do plantel de fêmeas renovado anualmente; meta entre 35% e 45%.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Ficha_do_Reprodutor**: Registro digital individual de um varrão, contendo histórico de uso e avaliações.
- **Calculadora_Reprodutiva**: Componente do Sistema responsável por calcular datas e indicadores derivados de eventos reprodutivos.
- **Motor_de_Alertas**: Componente do Sistema responsável por gerar notificações e alertas de manejo.
- **Painel_do_Plantel**: Tela principal do módulo, exibindo visão consolidada do rebanho reprodutivo.


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

---

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


---

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

---

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


---

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

---

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


---

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

---

### Requisito 8: Gestão de Descarte e Reposição do Plantel

**User Story:** Como produtor, quero registrar descartes com justificativa e acompanhar a taxa de reposição do plantel, para que eu mantenha a distribuição ideal de paridades e o fluxo contínuo de produção.

#### Critérios de Aceitação

1. WHEN o Produtor registrar o descarte de uma fêmea, THE Sistema SHALL exigir a seleção de ao menos uma justificativa dentre: problema reprodutivo (repetição de cio, anestro, aborto), baixa produção (leitegadas pequenas em 3 partos consecutivos), problema locomotor (casco/aprumos), problema sanitário crônico, idade avançada (paridade ≥ 8) ou outro (campo livre).
2. THE Sistema SHALL registrar a data de descarte e manter o histórico completo da fêmea descartada para consulta futura.
3. WHEN o Produtor registrar o descarte de uma fêmea, THE Sistema SHALL atualizar automaticamente o Painel_do_Plantel com o novo total de fêmeas ativas.
4. THE Sistema SHALL calcular e exibir mensalmente a taxa de reposição anualizada do plantel, alertando quando estiver abaixo de 35% ou acima de 45%.
5. THE Sistema SHALL exibir no Painel_do_Plantel a distribuição atual do plantel por paridade, comparando com a distribuição ideal: ~15% marrãs, ~18% 1º parto, ~49% 2º ao 5º parto, ~18% 6º parto em diante.
6. WHEN a proporção de fêmeas entre o 2º e o 5º parto for inferior a 50% do plantel ativo, THE Motor_de_Alertas SHALL gerar o alerta "Distribuição de paridades fora do ideal — menos de 50% do plantel no pico produtivo (2º–5º parto)".
7. IF o Produtor tentar registrar o descarte de uma fêmea com status "Gestante" ou "Lactante", THEN THE Sistema SHALL exibir o aviso "Fêmea em fase ativa — confirmar descarte?" antes de prosseguir.
8. THE Sistema SHALL calcular e exibir a meta semanal de reposição com base no tamanho do plantel e na taxa de reposição configurada pelo Produtor (padrão: 40% ao ano).


---

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

---

### Requisito 10: Calendário de Manejo Reprodutivo

**User Story:** Como produtor, quero visualizar um calendário com todas as ações de manejo previstas para os próximos dias (coberturas, diagnósticos, partos, desmames), para que eu possa organizar a rotina da granja com antecedência.

#### Critérios de Aceitação

1. THE Sistema SHALL exibir um calendário com as seguintes ações previstas calculadas automaticamente: coberturas programadas (baseadas em IDE esperado pós-desmame), diagnósticos de gestação pendentes (30 dias pós-cobertura), retornos ao cio esperados (18–24 dias pós-cobertura), partos previstos (114 dias pós-cobertura) e desmames previstos (21 dias pós-parto por padrão, configurável pelo Produtor).
2. THE Sistema SHALL permitir ao Produtor configurar o intervalo padrão de desmame da granja entre 18 e 28 dias.
3. WHEN uma ação de manejo prevista não for registrada até 2 dias após a data calculada, THE Motor_de_Alertas SHALL gerar o alerta "Ação pendente: [tipo de ação] para [identificação da fêmea] — [N] dias em atraso".
4. THE Sistema SHALL permitir visualizar o calendário nas visões: dia, semana e mês.
5. WHEN o Produtor selecionar um evento no calendário, THE Sistema SHALL exibir o resumo da Ficha_da_Matriz da fêmea correspondente e o botão de registro da ação.
6. THE Sistema SHALL destacar no calendário os dias com concentração de partos previstos superior a 20% da capacidade de maternidade configurada pelo Produtor.


---

### Requisito 11: Controle de Performance do 2º Parto

**User Story:** Como produtor, quero que o sistema identifique fêmeas primíparas em risco de queda de desempenho no 2º parto e sugira intervenções de manejo, para que eu proteja a longevidade produtiva dessas fêmeas.

#### Critérios de Aceitação

1. WHEN uma fêmea primípara for desmamada, THE Sistema SHALL calcular e registrar: peso no desmame, perda de peso na lactação (%) e IDE até a próxima cobertura.
2. WHEN o IDE de uma fêmea primípara for inferior a 11 dias, THE Motor_de_Alertas SHALL gerar o alerta "Considerar atraso na cobertura da primípara — coberturas após 11–12 dias do desmame aumentam em 2,2 a 3,4 leitões nascidos vivos no 2º parto".
3. WHEN uma fêmea primípara perder mais de 9% do peso corporal durante a primeira lactação, THE Motor_de_Alertas SHALL gerar o alerta "Desgaste excessivo na 1ª lactação — risco de queda no 2º parto; revisar manejo nutricional".
4. THE Sistema SHALL exibir na Ficha_da_Matriz de fêmeas de 1º e 2º parto o indicador "Risco de queda no 2º parto" com base nos critérios: cobertura antes dos 210 dias de idade, perda de peso > 9% na 1ª lactação ou IDE < 11 dias pós-1º desmame.
5. WHEN o número de nascidos vivos no 2º parto for inferior ao número de nascidos vivos no 1º parto em mais de 2 leitões, THE Sistema SHALL registrar o evento como "Queda de desempenho no 2º parto" no histórico da fêmea.

---

### Requisito 12: Exportação e Relatórios da Ficha Individual

**User Story:** Como produtor, quero exportar a ficha individual de uma fêmea ou reprodutor e gerar relatórios do plantel, para que eu possa compartilhar dados com o veterinário, o integrador ou o banco.

#### Critérios de Aceitação

1. THE Sistema SHALL gerar a Ficha_da_Matriz em formato PDF contendo: dados de identificação, histórico completo de eventos reprodutivos (coberturas, gestações, partos, desmames), histórico de ECC, alertas gerados e indicadores individuais calculados.
2. THE Sistema SHALL gerar a Ficha_do_Reprodutor em formato PDF contendo: dados de identificação, histórico de uso (coberturas por semana/mês), ECC e alertas gerados.
3. THE Sistema SHALL gerar o relatório "Resumo do Plantel" em formato PDF contendo: todos os indicadores do Painel_do_Plantel, distribuição por paridade, taxa de reposição e lista de fêmeas por status.
4. WHEN o Produtor solicitar a exportação de um relatório, THE Sistema SHALL gerar o arquivo em até 10 segundos para plantéis de até 500 fêmeas.
5. THE Sistema SHALL permitir o compartilhamento do PDF gerado via aplicativos de mensagem instalados no dispositivo móvel do Produtor.

