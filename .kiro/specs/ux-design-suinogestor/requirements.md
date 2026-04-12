# Documento de Requisitos: UI/UX Design — SuinoGestor

## Introdução

Este documento especifica os requisitos de design de interface e experiência do usuário para o aplicativo **SuinoGestor**, um sistema de gestão de suinocultura para Android nativo. O foco prioritário é o fluxo de **Controle de Reprodução**, com cobertura de navegação global e padrões visuais aplicáveis a todos os módulos.

O perfil central do usuário é o **produtor rural brasileiro** com baixa familiaridade tecnológica, que opera o aplicativo em campo — sob variação de luminosidade, frequentemente com uma mão, em situações que exigem interações rápidas e diretas. O app deve funcionar **100% offline**, com armazenamento local via Room.

A stack técnica é: Kotlin + Jetpack Compose + Material 3 Expressive, Clean Architecture + MVVM, Room, Min SDK 24 (Android 7.0), todo conteúdo em Português Brasileiro.

---

## Glossário

- **SuinoGestor**: o aplicativo Android de gestão de suinocultura.
- **Sistema_de_Navegação**: o componente de navegação principal do app (bottom navigation bar).
- **Dashboard**: tela inicial do app com resumo dos módulos e alertas.
- **Módulo_Reprodução**: conjunto de telas que cobrem matrizes, coberturas, partos, leitões e desmame.
- **Módulo_Engorda**: conjunto de telas para controle de lotes de engorda.
- **Módulo_Financeiro**: conjunto de telas para controle de custos e receitas.
- **Módulo_Indicadores**: conjunto de telas para visualização de KPIs zootécnicos.
- **Matriz**: fêmea suína reprodutora cadastrada individualmente no sistema.
- **Marrã**: fêmea suína jovem ainda em preparação reprodutiva.
- **Cobertura**: evento reprodutivo de inseminação ou monta natural registrado para uma matriz.
- **Parto**: evento de nascimento de leitões registrado para uma matriz gestante.
- **Leitão**: animal nascido de uma matriz, rastreado individualmente ou por lote.
- **Desmame**: evento de separação dos leitões da matriz lactante.
- **IDE**: Intervalo Desmame-Cobertura — KPI reprodutivo chave.
- **ECC**: Escore de Condição Corporal — avaliação de 1 a 5 da condição física da matriz.
- **Card_de_Resumo**: componente visual que exibe métricas ou alertas de forma compacta no Dashboard.
- **Tema_Visual**: conjunto de tokens de cor, tipografia, forma e movimento do Material 3 Expressive.
- **Zona_de_Alcance**: área da tela acessível com o polegar em uso com uma mão (zona inferior e central).
- **Tela_Compacta**: dispositivo com largura de janela < 600dp (WindowWidthSizeClass.Compact).
- **Tela_Media**: dispositivo com largura de janela entre 600dp e 840dp (WindowWidthSizeClass.Medium).
- **Tela_Expandida**: dispositivo com largura de janela > 840dp (WindowWidthSizeClass.Expanded).
- **Token_M3E**: token semântico do Material 3 Expressive (ex.: `md.sys.color.primary`).
- **Microinteração**: animação ou feedback visual sutil que confirma uma ação do usuário.
- **Estado_Offline**: condição em que o dispositivo não possui conexão com a internet.

---

## Requisitos

### Requisito 1: Arquitetura de Navegação Global

**User Story:** Como produtor rural com baixa familiaridade tecnológica, quero uma navegação simples e previsível, para que eu encontre qualquer funcionalidade sem me perder no aplicativo.

#### Critérios de Aceitação

1. THE Sistema_de_Navegação SHALL utilizar Bottom Navigation Bar com 4 destinos fixos: Reprodução, Engorda, Financeiro e Indicadores.
2. WHEN o usuário toca em um item da Bottom Navigation Bar, THE Sistema_de_Navegação SHALL navegar para o destino correspondente em menos de 300ms, preservando o estado da tela anterior.
3. THE Sistema_de_Navegação SHALL exibir rótulos de texto visíveis abaixo de cada ícone da Bottom Navigation Bar em todos os tamanhos de tela.
4. WHILE o usuário está em Tela_Compacta, THE Sistema_de_Navegação SHALL posicionar a Bottom Navigation Bar na parte inferior da tela, dentro da Zona_de_Alcance do polegar.
5. WHILE o usuário está em Tela_Media ou Tela_Expandida, THE Sistema_de_Navegação SHALL substituir a Bottom Navigation Bar por uma Navigation Rail posicionada à esquerda da tela.
6. THE Sistema_de_Navegação SHALL destacar visualmente o destino ativo com o token `md.sys.color.secondaryContainer` como cor de fundo do indicador e `md.sys.color.onSecondaryContainer` para o ícone e rótulo ativos.
7. IF o usuário pressionar o botão físico de voltar na tela raiz de qualquer módulo, THEN THE Sistema_de_Navegação SHALL exibir um diálogo de confirmação de saída do aplicativo antes de encerrar.
8. THE Sistema_de_Navegação SHALL reservar altura mínima de 80dp para a Bottom Navigation Bar, garantindo área de toque de 48dp por item conforme diretrizes de acessibilidade do Material 3.
9. WHERE o módulo Reprodução estiver ativo, THE Sistema_de_Navegação SHALL exibir um badge numérico no ícone de Reprodução indicando a quantidade de alertas pendentes não lidos.

---

### Requisito 2: Dashboard Inicial

**User Story:** Como produtor rural, quero ver um resumo do estado da minha granja ao abrir o aplicativo, para que eu identifique rapidamente o que precisa de atenção hoje.

#### Critérios de Aceitação

1. THE Dashboard SHALL exibir uma saudação personalizada com o nome da granja e a data atual no topo da tela, usando `md.sys.typescale.headlineSmall`.
2. THE Dashboard SHALL exibir uma seção de "Alertas do Dia" com Cards_de_Resumo para eventos críticos: matrizes próximas ao parto, coberturas programadas e desmames pendentes.
3. WHEN não houver alertas pendentes, THE Dashboard SHALL exibir uma ilustração temática de suinocultura com a mensagem "Tudo em dia na sua granja!" em `md.sys.typescale.bodyLarge`.
4. THE Dashboard SHALL exibir 4 Cards_de_Resumo de acesso rápido aos módulos principais (Reprodução, Engorda, Financeiro, Indicadores), cada um com ícone temático, título e um indicador numérico chave.
5. WHEN o usuário toca em um Card_de_Resumo de módulo, THE Dashboard SHALL navegar para a tela raiz do módulo correspondente.
6. THE Dashboard SHALL ser rolável verticalmente (LazyColumn) para acomodar todos os cards sem truncamento em Tela_Compacta.
7. WHILE o dispositivo está em Estado_Offline, THE Dashboard SHALL exibir um banner discreto no topo com a mensagem "Modo offline — dados salvos localmente" usando `md.sys.color.tertiaryContainer`.
8. THE Dashboard SHALL carregar e renderizar o conteúdo inicial em menos de 500ms após a abertura do aplicativo, exibindo um Loading_Indicator do M3 Expressive durante o carregamento.
9. WHEN o usuário puxa a tela para baixo (pull-to-refresh), THE Dashboard SHALL atualizar os Cards_de_Resumo com os dados mais recentes do banco local, exibindo o Loading_Indicator durante a atualização.

---

### Requisito 3: Lista de Matrizes

**User Story:** Como produtor rural, quero visualizar todas as minhas matrizes de forma clara e rápida, para que eu encontre um animal específico sem precisar lembrar de números.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir uma lista de matrizes como tela raiz, com cada item mostrando: identificação (brinco/tatuagem), status atual, paridade e próximo evento previsto.
2. THE Módulo_Reprodução SHALL exibir um chip de status colorido em cada item da lista, usando cores semânticas: gestante (`md.sys.color.primaryContainer`), lactante (`md.sys.color.secondaryContainer`), vazia (`md.sys.color.tertiaryContainer`), descartada (`md.sys.color.errorContainer`).
3. WHEN a lista de matrizes contiver mais de 10 itens, THE Módulo_Reprodução SHALL renderizar a lista com LazyColumn para garantir performance de rolagem fluida.
4. THE Módulo_Reprodução SHALL exibir um campo de busca no topo da lista que filtra matrizes por identificação em tempo real, com debounce de 300ms.
5. WHEN o campo de busca não retornar resultados, THE Módulo_Reprodução SHALL exibir uma ilustração de "nenhum resultado" com a mensagem "Nenhuma matriz encontrada com esse número".
6. THE Módulo_Reprodução SHALL exibir um Extended FAB com o rótulo "Nova Matriz" fixo na parte inferior direita da tela, dentro da Zona_de_Alcance.
7. WHEN o usuário toca em um item da lista, THE Módulo_Reprodução SHALL navegar para a tela de detalhes da matriz com uma transição de entrada compartilhada (shared element transition) no card.
8. THE Módulo_Reprodução SHALL exibir um indicador visual de swipe-to-action em cada item, permitindo acesso rápido ao registro de cobertura via deslize para a direita.
9. IF a lista de matrizes estiver vazia, THEN THE Módulo_Reprodução SHALL exibir uma tela de estado vazio com ilustração temática e o botão "Cadastrar primeira matriz".

---

### Requisito 4: Cadastro e Edição de Matriz

**User Story:** Como produtor rural, quero cadastrar uma nova matriz de forma rápida e sem erros, para que o registro fique correto desde o início.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir um formulário de cadastro de matriz com os campos obrigatórios: identificação, data de nascimento, raça/linhagem e categoria (Marrã/Matriz).
2. THE Módulo_Reprodução SHALL exibir os campos opcionais: peso de entrada (kg), ECC (1–5), origem (Granja Própria/Fornecedor) e foto, agrupados em uma seção expansível "Informações adicionais".
3. WHEN o usuário toca no campo de data de nascimento, THE Módulo_Reprodução SHALL exibir um DatePicker do Material 3 em modo modal, com seleção por calendário visual.
4. THE Módulo_Reprodução SHALL calcular e exibir a idade da matriz em tempo real (formato "Xa Ym Zd") abaixo do campo de data de nascimento, atualizando a cada seleção de data.
5. THE Módulo_Reprodução SHALL usar um Connected Button Group (M3 Expressive) para a seleção de categoria (Marrã / Matriz), com shape morph na seleção.
6. WHEN o usuário toca em "Salvar" com campos obrigatórios em branco, THE Módulo_Reprodução SHALL exibir mensagens de erro inline abaixo de cada campo inválido, sem fechar o formulário.
7. IF a identificação informada já existir no plantel, THEN THE Módulo_Reprodução SHALL exibir um erro inline no campo de identificação com a mensagem "Esse número já está cadastrado no plantel".
8. WHEN o cadastro for salvo com sucesso, THE Módulo_Reprodução SHALL exibir um Snackbar com a mensagem "Matriz [identificação] cadastrada com sucesso!" e retornar à lista de matrizes.
9. IF a marrã cadastrada tiver menos de 160 dias de vida, THEN THE Módulo_Reprodução SHALL exibir um banner de alerta amarelo com o ícone de aviso e a mensagem "Essa marrã ainda não atingiu a idade mínima de preparação (160 dias)".
10. THE Módulo_Reprodução SHALL manter todos os dados do formulário preenchidos em caso de rotação de tela ou interrupção do sistema, usando `rememberSaveable`.

---

### Requisito 5: Registro de Cobertura

**User Story:** Como produtor rural, quero registrar uma cobertura rapidamente no campo, para que eu não perca o momento e o dado fique correto.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir o formulário de registro de cobertura com os campos: data da cobertura, tipo (Inseminação Artificial / Monta Natural), identificação do reprodutor/sêmen e observações.
2. THE Módulo_Reprodução SHALL pré-preencher a data da cobertura com a data atual, permitindo edição via DatePicker.
3. THE Módulo_Reprodução SHALL usar um Connected Button Group para seleção do tipo de cobertura (IA / Monta Natural).
4. WHEN o registro de cobertura for salvo com sucesso, THE Módulo_Reprodução SHALL atualizar o status da matriz para "Gestante" e calcular automaticamente a data prevista de parto (data da cobertura + 114 dias).
5. WHEN o registro de cobertura for salvo com sucesso, THE Módulo_Reprodução SHALL exibir um Snackbar com a mensagem "Cobertura registrada! Parto previsto para [data]".
6. THE Módulo_Reprodução SHALL exibir a data prevista de parto calculada em tempo real abaixo do campo de data da cobertura, antes mesmo de salvar.
7. IF a matriz selecionada não estiver no status "Aguardando Cobertura" ou "Vazia", THEN THE Módulo_Reprodução SHALL exibir um diálogo de confirmação alertando sobre o status atual antes de prosseguir.
8. THE Módulo_Reprodução SHALL permitir o registro de cobertura em Estado_Offline, persistindo os dados localmente via Room.

---

### Requisito 6: Registro de Parto

**User Story:** Como produtor rural, quero registrar um parto de forma completa mas rápida, para que os dados dos leitões fiquem corretos desde o nascimento.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir o formulário de registro de parto com os campos: data do parto, total de nascidos vivos, total de nascidos mortos, total de mumificados e observações.
2. THE Módulo_Reprodução SHALL usar campos numéricos com botões de incremento/decremento (+/-) de toque amplo (mínimo 48dp) para os contadores de leitões, evitando teclado numérico para entradas simples.
3. WHEN o registro de parto for salvo com sucesso, THE Módulo_Reprodução SHALL atualizar o status da matriz para "Lactante" e incrementar a paridade em 1.
4. WHEN o registro de parto for salvo com sucesso, THE Módulo_Reprodução SHALL calcular e exibir automaticamente a data prevista de desmame (data do parto + 21 dias).
5. WHEN o registro de parto for salvo com sucesso, THE Módulo_Reprodução SHALL exibir um Snackbar com a mensagem "Parto registrado! [N] leitões nascidos vivos. Desmame previsto para [data]".
6. IF o total de nascidos vivos for zero, THEN THE Módulo_Reprodução SHALL exibir um diálogo de confirmação com a mensagem "Nenhum leitão nascido vivo. Confirmar registro?" antes de salvar.
7. THE Módulo_Reprodução SHALL exibir a data prevista de desmame calculada em tempo real abaixo do campo de data do parto.
8. THE Módulo_Reprodução SHALL permitir o registro de parto em Estado_Offline, persistindo os dados localmente via Room.

---

### Requisito 7: Lista de Leitões por Matriz

**User Story:** Como produtor rural, quero ver os leitões de uma matriz de forma organizada, para que eu acompanhe o desenvolvimento da leitegada.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir a lista de leitões de uma matriz com: identificação individual (se houver), sexo, peso ao nascer e status atual.
2. THE Módulo_Reprodução SHALL exibir um resumo no topo da lista com: total de leitões vivos, total de mortos e peso médio da leitegada.
3. WHEN a lista de leitões estiver vazia, THE Módulo_Reprodução SHALL exibir o estado vazio com a mensagem "Nenhum leitão registrado para esta matriz".
4. THE Módulo_Reprodução SHALL exibir um FAB com ícone de adição para registrar um novo leitão individualmente.
5. WHEN o usuário toca em um leitão da lista, THE Módulo_Reprodução SHALL exibir um Bottom Sheet com as opções: "Registrar morte", "Editar dados" e "Registrar transferência".

---

### Requisito 8: Registro de Desmame

**User Story:** Como produtor rural, quero registrar o desmame de forma simples, para que o ciclo reprodutivo da matriz seja atualizado corretamente.

#### Critérios de Aceitação

1. THE Módulo_Reprodução SHALL exibir o formulário de desmame com os campos: data do desmame, quantidade de leitões desmamados e peso médio ao desmame.
2. THE Módulo_Reprodução SHALL pré-preencher a quantidade de leitões desmamados com o total de leitões vivos registrados no parto correspondente.
3. WHEN o desmame for salvo com sucesso, THE Módulo_Reprodução SHALL atualizar o status da matriz para "Vazia" e registrar o IDE (Intervalo Desmame-Cobertura) como pendente.
4. WHEN o desmame for salvo com sucesso, THE Módulo_Reprodução SHALL exibir um Snackbar com a mensagem "Desmame registrado! [N] leitões desmamados. Matriz pronta para nova cobertura em breve."
5. THE Módulo_Reprodução SHALL usar campos numéricos com botões de incremento/decremento para a quantidade de leitões desmamados.
6. THE Módulo_Reprodução SHALL permitir o registro de desmame em Estado_Offline, persistindo os dados localmente via Room.

---

### Requisito 9: Tema Visual e Identidade do SuinoGestor

**User Story:** Como produtor rural, quero que o aplicativo tenha uma aparência agradável e familiar ao meu contexto, para que eu me sinta confortável usando-o no dia a dia.

#### Critérios de Aceitação

1. THE Tema_Visual SHALL definir uma paleta de cores primária baseada em tons terrosos e verdes, com cor primária `#5D7A3E` (verde campo), cor secundária `#8B6914` (âmbar terroso) e cor terciária `#C0522A` (terracota).
2. THE Tema_Visual SHALL suportar Dynamic Color no Android 12+ (API 31+), usando `dynamicLightColorScheme` e `dynamicDarkColorScheme` quando disponível.
3. THE Tema_Visual SHALL garantir contraste mínimo de 4,5:1 para texto sobre fundos em todos os pares de cores primárias e de superfície, conforme WCAG 2.1 AA.
4. THE Tema_Visual SHALL usar a família tipográfica **Nunito** como fonte principal, com pesos Regular (400), SemiBold (600) e Bold (700), por sua legibilidade em telas pequenas e tom acolhedor.
5. THE Tema_Visual SHALL aplicar shapes arredondadas: `ExtraLarge` (28dp) para cards de destaque, `Large` (16dp) para cards de lista, `Medium` (12dp) para chips e botões secundários, `Full` (circular) para FABs e avatares.
6. THE Tema_Visual SHALL usar ícones do conjunto **Material Symbols Rounded** para todos os elementos de navegação e ações, complementados por ilustrações vetoriais temáticas de suinocultura (silhuetas de suínos, celeiros, campos).
7. THE Tema_Visual SHALL aplicar o token `md.sys.color.primary` para ações primárias (botões de salvar, confirmar), `md.sys.color.error` para ações destrutivas e `md.sys.color.tertiary` para ações de alerta/atenção.
8. WHILE o dispositivo estiver em modo escuro (dark theme), THE Tema_Visual SHALL aplicar automaticamente o esquema de cores escuro gerado pelo Material Theme Builder, mantendo contraste e legibilidade.
9. THE Tema_Visual SHALL usar linguagem textual próxima ao produtor rural: "sua matriz", "sua granja", "registrar cobertura" — evitando termos técnicos de TI como "sincronizar", "cache" ou "banco de dados".

---

### Requisito 10: Padrões de Interação e Microinterações

**User Story:** Como produtor rural usando o app com uma mão no campo, quero que as interações sejam simples e com feedback claro, para que eu saiba que minha ação foi registrada sem precisar olhar duas vezes.

#### Critérios de Aceitação

1. THE SuinoGestor SHALL garantir área de toque mínima de 48dp × 48dp para todos os elementos interativos, conforme diretrizes de acessibilidade do Material 3.
2. THE SuinoGestor SHALL posicionar ações primárias (botões de salvar, confirmar, registrar) na metade inferior da tela, dentro da Zona_de_Alcance do polegar em Tela_Compacta.
3. WHEN o usuário salva um registro com sucesso, THE SuinoGestor SHALL exibir um Snackbar na parte inferior da tela com mensagem de confirmação e duração de 4 segundos.
4. WHEN o usuário comete um erro de validação, THE SuinoGestor SHALL exibir a mensagem de erro inline abaixo do campo específico, com ícone de erro e cor `md.sys.color.error`, sem usar diálogos modais para erros de campo.
5. THE SuinoGestor SHALL aplicar animações de entrada e saída de tela usando as transições padrão do Navigation Compose (fade + slide), com duração máxima de 300ms.
6. WHEN o usuário pressiona e segura um botão de ação primária, THE SuinoGestor SHALL aplicar o efeito de ripple do Material 3 com cor `md.sys.color.onPrimary` para feedback tátil visual.
7. THE SuinoGestor SHALL usar seletores visuais (Connected Button Groups, Dropdowns, DatePickers) em vez de campos de texto livre sempre que o conjunto de valores for finito e conhecido.
8. WHEN uma operação de escrita no banco de dados estiver em andamento, THE SuinoGestor SHALL desabilitar o botão de ação primária e exibir um CircularProgressIndicator de tamanho Small dentro do botão.
9. THE SuinoGestor SHALL honrar a configuração de escala de animação do sistema Android: quando `animatorDurationScale = 0`, todas as animações SHALL ser substituídas por transições instantâneas.
10. IF o usuário tentar sair de um formulário com dados não salvos, THEN THE SuinoGestor SHALL exibir um diálogo de confirmação com as opções "Descartar" e "Continuar editando".

---

### Requisito 11: Jornada do Usuário — Registro de Cobertura

**User Story:** Como produtor rural, quero completar o registro de uma cobertura em menos de 60 segundos a partir da abertura do app, para que eu não perca tempo no meio da rotina da granja.

#### Critérios de Aceitação

1. THE SuinoGestor SHALL permitir que o usuário complete o fluxo completo de registro de cobertura (abertura do app → Dashboard → Lista de Matrizes → Seleção da Matriz → Registro de Cobertura → Confirmação) em no máximo 7 toques.
2. THE SuinoGestor SHALL exibir o atalho "Registrar Cobertura" diretamente no Card_de_Resumo de Reprodução no Dashboard, reduzindo o fluxo para 5 toques.
3. WHEN o usuário acessa o registro de cobertura via swipe na lista de matrizes, THE SuinoGestor SHALL abrir o formulário de cobertura com a matriz pré-selecionada, reduzindo o fluxo para 4 toques.
4. THE SuinoGestor SHALL pré-preencher a data da cobertura com a data atual e o tipo com "Inseminação Artificial" como padrão, minimizando campos a preencher.
5. WHEN o registro de cobertura for confirmado, THE SuinoGestor SHALL exibir uma animação de celebração sutil (confetti ou ícone animado de suíno) por 1,5 segundos antes do Snackbar de confirmação.
6. THE SuinoGestor SHALL registrar os pontos de prazer da jornada: saudação personalizada no Dashboard, cálculo automático da data de parto, e mensagem afetiva de confirmação.
7. THE SuinoGestor SHALL registrar os pontos de atenção da jornada: campo de identificação do reprodutor/sêmen (pode ser desconhecido pelo produtor) e seleção de data (deve ser intuitiva para usuários com baixa literacia digital).

---

### Requisito 12: Visão Geral dos Módulos Secundários

**User Story:** Como produtor rural, quero que todos os módulos do app tenham a mesma aparência e funcionamento, para que eu não precise reaprender a usar cada parte do aplicativo.

#### Critérios de Aceitação

1. THE Módulo_Engorda SHALL seguir os mesmos padrões visuais do Módulo_Reprodução: mesma tipografia, paleta de cores, shapes e componentes M3 Expressive.
2. THE Módulo_Engorda SHALL exibir como tela raiz uma lista de lotes de engorda com: identificação do lote, quantidade de animais, fase atual e data prevista de abate.
3. THE Módulo_Financeiro SHALL seguir os mesmos padrões visuais do Módulo_Reprodução e exibir como tela raiz um resumo financeiro com: receitas, despesas e saldo do período atual.
4. THE Módulo_Financeiro SHALL usar gráficos de barras simples (sem interatividade complexa) para visualização de custos por categoria, priorizando legibilidade sobre sofisticação visual.
5. THE Módulo_Indicadores SHALL exibir KPIs zootécnicos em Cards_de_Resumo com valor numérico em destaque (`md.sys.typescale.displaySmall`), rótulo descritivo e indicador de tendência (seta para cima/baixo com cor semântica).
6. THE Módulo_Indicadores SHALL incluir os KPIs: IDE médio, taxa de parição, leitões desmamados por matriz por ano, taxa de mortalidade pré-desmame e conversão alimentar.
7. WHILE o usuário navega entre módulos, THE Sistema_de_Navegação SHALL manter o estado de rolagem e filtros de cada módulo, evitando que o usuário perca o contexto ao retornar.
