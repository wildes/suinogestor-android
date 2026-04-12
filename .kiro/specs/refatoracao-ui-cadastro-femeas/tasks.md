# Implementation Plan

- [x] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - Conformidade Visual com o Design System
  - **CRITICAL**: Este teste DEVE FALHAR no código não corrigido — a falha confirma que os gaps existem
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: Este teste codifica o comportamento esperado — ele validará a correção quando passar após a implementação
  - **GOAL**: Evidenciar os counterexamples que demonstram os gaps de conformidade
  - **Scoped PBT Approach**: Para bugs determinísticos de UI, escopar a propriedade aos casos concretos de falha: tema com `dynamicColor=false` e `darkTheme=false` (força o esquema estático)
  - Escrever testes Compose que verificam:
    - Cor do botão primário usa `#5D7A3E` (não `#6650A4` roxo do template)
    - FAB da lista exibe o rótulo "Nova Matriz" (não apenas ícone)
    - Estado vazio exibe o botão "Cadastrar primeira fêmea"
    - Campo de busca está presente na `ListaFemeasScreen`
  - Executar no código não corrigido — esperar FALHA (confirma que os bugs existem)
  - Documentar counterexamples encontrados (ex.: "FAB exibe apenas ícone, sem rótulo 'Nova Matriz'")
  - Marcar tarefa como completa quando o teste estiver escrito, executado e a falha documentada
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.10, 1.11, 1.12, 1.13_

- [x] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Lógica de Negócio e Contratos de ViewModel Inalterados
  - **IMPORTANT**: Seguir a metodologia observation-first
  - Observar no código não corrigido: `CadastrarFemeaUseCase` persiste fêmea corretamente
  - Observar: validação de identificação duplicada exibe erro inline
  - Observar: alerta `IDADE_MINIMA_PREPARACAO` é gerado para marrãs < 160 dias
  - Observar: `StateFlow<FemeaUiState>` e `StateFlow<CadastroFemeaUiState>` expõem os dados corretamente
  - Escrever testes unitários de ViewModel e testes de integração Room que capturam esses comportamentos
  - Executar no código não corrigido — esperar PASS (confirma o baseline a preservar)
  - Marcar tarefa como completa quando os testes estiverem escritos, executados e passando no código não corrigido
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.10_

- [x] 3. Atualizar BOM e adicionar assets de fonte Nunito

  - [x] 3.1 Atualizar composeBom em libs.versions.toml
    - Alterar `composeBom` de `"2024.09.00"` para `"2025.05.00"` (ou superior) em `gradle/libs.versions.toml`
    - Verificar se há breaking changes em outros componentes após a atualização
    - Sincronizar o projeto e confirmar que o build compila sem erros
    - _Bug_Condition: isBugCondition(CategoriaSelector.kt) — usa SegmentedButton em vez de ConnectedButtonGroup_
    - _Expected_Behavior: BOM >= 2025.05.00 disponibiliza material3:1.4.x com ConnectedButtonGroup_
    - _Preservation: Nenhuma alteração em domain/ ou data/_
    - _Requirements: 2.15_

  - [x] 3.2 Adicionar arquivos .ttf da fonte Nunito em res/font/
    - Criar diretório `app/src/main/res/font/` se não existir
    - Adicionar `nunito_regular.ttf` (peso 400)
    - Adicionar `nunito_semibold.ttf` (peso 600)
    - Adicionar `nunito_bold.ttf` (peso 700)
    - Confirmar que os arquivos são reconhecidos pelo Android Studio como recursos de fonte
    - _Bug_Condition: isBugCondition(Type.kt) — usa FontFamily.Default (Roboto) em vez de Nunito_
    - _Expected_Behavior: Assets locais garantem funcionamento offline-first (Princípio III do tech.md)_
    - _Preservation: Nenhuma alteração em domain/ ou data/_
    - _Requirements: 2.2_

- [x] 4. Corrigir ui/theme/ (Color, Type, Theme)

  - [x] 4.1 Substituir paleta do template pela paleta SuinoGestor em Color.kt
    - Remover as 6 cores do template (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`)
    - Definir seeds: `SuinoGreen = Color(0xFF5D7A3E)`, `SuinoAmber = Color(0xFF8B6914)`, `SuinoTerra = Color(0xFFC0522A)`
    - Definir `LightColorScheme` completo com `lightColorScheme(...)` conforme pseudocódigo do design.md §1.1
    - Definir `DarkColorScheme` completo com `darkColorScheme(...)` conforme pseudocódigo do design.md §1.1
    - _Bug_Condition: isBugCondition(Color.kt) — define apenas cores do template Android Studio_
    - _Expected_Behavior: paleta SuinoGestor com primary=#5D7A3E, secondary=#8B6914, tertiary=#C0522A_
    - _Preservation: dynamic color (Android 12+) continua usando dynamicLightColorScheme/dynamicDarkColorScheme_
    - _Requirements: 2.1, 2.3, 3.1_

  - [x] 4.2 Configurar NunitoFontFamily com assets locais em Type.kt
    - Definir `NunitoFontFamily` usando `Font(resId = R.font.nunito_regular)`, `R.font.nunito_semibold`, `R.font.nunito_bold`
    - Aplicar `NunitoFontFamily` a todas as 15 escalas tipográficas do M3 conforme pseudocódigo do design.md §1.2
    - Remover qualquer referência a `FontFamily.Default` ou `GoogleFont`
    - _Bug_Condition: isBugCondition(Type.kt) — usa FontFamily.Default (Roboto) em todas as escalas_
    - _Expected_Behavior: Nunito Regular/SemiBold/Bold aplicado a displayLarge..labelSmall_
    - _Preservation: Escalas tipográficas do M3 mantêm os mesmos tamanhos e line heights_
    - _Requirements: 2.2_

  - [x] 4.3 Definir SuinoGestorShapes e passar ao MaterialTheme em Theme.kt
    - Definir `SuinoGestorShapes = Shapes(extraSmall=4dp, small=8dp, medium=12dp, large=16dp, extraLarge=28dp)`
    - Adicionar parâmetro `shapes = SuinoGestorShapes` na chamada de `MaterialTheme(...)`
    - Atualizar referências a `LightColorScheme` e `DarkColorScheme` (substituindo as cores do template)
    - _Bug_Condition: isBugCondition(Theme.kt) — não define shapes customizados nem usa paleta SuinoGestor_
    - _Expected_Behavior: shapes explícitos (ExtraLarge=28dp, Large=16dp, Medium=12dp) e paleta correta_
    - _Preservation: dynamic color e dark theme automático continuam funcionando_
    - _Requirements: 2.1, 2.3, 2.4, 3.1_

- [x] 5. Corrigir ui/component/AlertaBanner.kt

  - [x] 5.1 Substituir tokens errorContainer por tertiaryContainer no AlertaBanner
    - Alterar `containerColor` de `MaterialTheme.colorScheme.errorContainer` para `MaterialTheme.colorScheme.tertiaryContainer`
    - Alterar `tint` do ícone de `onErrorContainer` para `onTertiaryContainer`
    - Alterar `color` do texto de `onErrorContainer` para `onTertiaryContainer`
    - Estrutura `Row + Icon + Spacer + Text` permanece idêntica — apenas os 3 tokens de cor mudam
    - _Bug_Condition: isBugCondition(AlertaBanner.kt) — usa errorContainer (vermelho) para alerta de atenção_
    - _Expected_Behavior: tertiaryContainer (terracota) comunica alerta de atenção, não erro crítico_
    - _Preservation: alerta IDADE_MINIMA_PREPARACAO continua sendo gerado e exibido pelo use case_
    - _Requirements: 2.14, 3.3_

- [x] 6. Corrigir ui/component/CategoriaSelector.kt

  - [x] 6.1 Substituir SegmentedButton por ConnectedButtonGroup (M3 Expressive)
    - Adicionar `@OptIn(ExperimentalMaterial3ExpressiveApi::class)` ao composable
    - Substituir `SingleChoiceSegmentedButtonRow` + `SegmentedButton` por `ConnectedButtonGroup` + `ToggleButton`
    - Implementar conforme pseudocódigo do design.md §3 (Opção A)
    - Verificar que shape morph animado funciona na seleção entre Marrã/Matriz
    - _Bug_Condition: isBugCondition(CategoriaSelector.kt) — usa SegmentedButton padrão M3 sem shape morph_
    - _Expected_Behavior: ConnectedButtonGroup M3E com shape morph animado na seleção_
    - _Preservation: callback onCategoriaSelecionada continua funcionando; CategoriaFemea enum inalterado_
    - _Requirements: 2.15_

- [x] 7. Atualizar FemeaUiState e FemeaViewModel (suporte à busca)

  - [x] 7.1 Adicionar termoBusca e femeasFiltradas ao FemeaUiState
    - Adicionar campo `val termoBusca: String = ""` ao data class `FemeaUiState`
    - Adicionar campo `val femeasFiltradas: List<FemeaResumo> = emptyList()` ao data class `FemeaUiState`
    - Garantir invariante: quando `termoBusca` está vazio, `femeasFiltradas == femeas`
    - _Bug_Condition: isBugCondition(FemeaUiState.kt) — não tem suporte a busca_
    - _Expected_Behavior: estado expõe termoBusca e femeasFiltradas para a tela_
    - _Preservation: campos existentes (carregando, femeas, erro) inalterados; StateFlow<FemeaUiState> mantém semântica_
    - _Requirements: 2.13, 3.10_

  - [x] 7.2 Implementar atualizarBusca com debounce de 300ms no FemeaViewModel
    - Adicionar `private val _termoBusca = MutableStateFlow("")`
    - Implementar `fun atualizarBusca(termo: String)` que atualiza `_termoBusca.value`
    - Implementar `observarBusca()` com `debounce(300)` que filtra `femeas` por `identificacao.contains(termo, ignoreCase = true)`
    - Chamar `observarBusca()` no `init` block
    - Atualizar `carregarFemeas()` para também popular `femeasFiltradas` respeitando o `termoBusca` atual
    - _Bug_Condition: isBugCondition(FemeaViewModel.kt) — não tem função de busca nem debounce_
    - _Expected_Behavior: busca com debounce 300ms filtrando por identificação_
    - _Preservation: funções cadastrar, atualizarIdadeExibida e StateFlow cadastroState inalterados_
    - _Requirements: 2.13, 3.10_

- [x] 8. Corrigir CadastroFemeaScreen

  - [x] 8.1 Substituir OutlinedTextField de data por DatePickerDialog modal
    - Adicionar `var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }`
    - Adicionar `val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())`
    - Substituir o `OutlinedTextField` de digitação manual por campo `readOnly` com `IconButton` de calendário
    - Implementar `DatePickerDialog` com botões "OK" e "Cancelar" conforme pseudocódigo do design.md §5.1
    - Converter `selectedDateMillis` para `LocalDate` via `Instant.ofEpochMilli(...).atZone(ZoneId.systemDefault()).toLocalDate()`
    - _Bug_Condition: isBugCondition(CadastroFemeaScreen.kt) — OutlinedTextField com digitação manual aaaa-mm-dd_
    - _Expected_Behavior: DatePickerDialog modal com calendário visual, pré-preenchido com data atual_
    - _Preservation: dataNascimento continua sendo passado ao ViewModel como LocalDate_
    - _Requirements: 2.5_

  - [x] 8.2 Migrar todos os campos de remember para rememberSaveable
    - Substituir `remember { mutableStateOf(...) }` por `rememberSaveable { mutableStateOf(...) }` em todos os campos:
      `identificacao`, `dataNascimentoTexto`, `racaLinhagem`, `categoria`, `pesoEntrada`, `eccSelecionado`, `origemSelecionada`, `mostrarDatePicker`, `mostrarDialogoDescarte`
    - Confirmar que `CategoriaFemea` e `OrigemAnimal` (enums) são serializados automaticamente via Bundle
    - _Bug_Condition: isBugCondition(CadastroFemeaScreen.kt) — usa remember, perde estado na rotação_
    - _Expected_Behavior: todos os campos preservados após rotação de tela_
    - _Preservation: lógica de validação e submissão do formulário inalterada_
    - _Requirements: 2.6_

  - [x] 8.3 Adicionar SnackbarHost ao Scaffold e exibir Snackbar ao salvar
    - Adicionar `val snackbarHostState = remember { SnackbarHostState() }`
    - Adicionar `snackbarHost = { SnackbarHost(hostState = snackbarHostState) }` ao `Scaffold`
    - Modificar `LaunchedEffect(state.sucesso)` para chamar `snackbarHostState.showSnackbar("Fêmea cadastrada com sucesso!", duration = SnackbarDuration.Short)` antes de `onCadastroSalvo()`
    - _Bug_Condition: isBugCondition(CadastroFemeaScreen.kt) — navega diretamente sem feedback ao salvar_
    - _Expected_Behavior: Snackbar com mensagem afetiva por ~4 segundos antes de navegar_
    - _Preservation: onCadastroSalvo() continua sendo chamado após o Snackbar_
    - _Requirements: 2.7_

  - [x] 8.4 Adicionar BackHandler com AlertDialog de confirmação de descarte
    - Adicionar `var mostrarDialogoDescarte by rememberSaveable { mutableStateOf(false) }`
    - Definir `val temDadosPreenchidos` verificando se qualquer campo está preenchido
    - Adicionar `BackHandler(enabled = temDadosPreenchidos) { mostrarDialogoDescarte = true }`
    - Implementar `AlertDialog` com título "Descartar alterações?", botões "Descartar" e "Continuar editando"
    - Atualizar `navigationIcon` da TopAppBar para usar o mesmo fluxo de verificação
    - _Bug_Condition: isBugCondition(CadastroFemeaScreen.kt) — Voltar descarta dados silenciosamente_
    - _Expected_Behavior: diálogo de confirmação "Descartar / Continuar editando" quando há dados preenchidos_
    - _Preservation: quando não há dados preenchidos, Voltar navega diretamente sem diálogo_
    - _Requirements: 2.8_

  - [x] 8.5 Corrigir CircularProgressIndicator para tamanho Small
    - Adicionar `modifier = Modifier.size(24.dp)` e `strokeWidth = 2.dp` ao `CircularProgressIndicator` dentro do botão
    - _Bug_Condition: isBugCondition(CadastroFemeaScreen.kt) — CircularProgressIndicator sem tamanho definido_
    - _Expected_Behavior: indicador compacto (24dp) proporcional ao botão_
    - _Preservation: indicador continua aparecendo apenas quando state.salvando == true_
    - _Requirements: 2.9_

- [x] 9. Corrigir ListaFemeasScreen e criar novos composables

  - [x] 9.1 Criar StatusFemeaChip.kt com mapeamento semântico de cores por status
    - Criar `app/src/main/java/br/com/suinogestor/ui/component/StatusFemeaChip.kt`
    - Implementar mapeamento: `GESTANTE→primaryContainer`, `LACTANTE→secondaryContainer`, `VAZIA→tertiaryContainer`, `DESCARTADA→errorContainer`, demais→`surfaceVariant`
    - Usar `Surface` com `shape = MaterialTheme.shapes.medium` e `Text` com `typography.labelMedium`
    - Adicionar `@Preview` composable
    - _Bug_Condition: isBugCondition(ListaFemeasScreen.kt) — texto bruto com cor primary uniforme_
    - _Expected_Behavior: chip colorido com tokens semânticos por status_
    - _Preservation: StatusFemea enum inalterado; nenhuma alteração em domain/_
    - _Requirements: 2.12_

  - [x] 9.2 Criar EstadoVazioFemeas.kt com ilustração e botão de ação primária
    - Criar `app/src/main/java/br/com/suinogestor/ui/component/EstadoVazioFemeas.kt`
    - Criar `app/src/main/res/drawable/ic_femea_vazia.xml` — VectorDrawable com silhueta de suíno
    - Implementar `Column` com `Icon(painterResource(R.drawable.ic_femea_vazia), size=120dp)`, mensagem contextual e `Button("Cadastrar primeira fêmea")`
    - Adicionar `@Preview` composable
    - _Bug_Condition: isBugCondition(ListaFemeasScreen.kt) — estado vazio exibe apenas texto centralizado_
    - _Expected_Behavior: ilustração temática + mensagem contextual + botão de ação primária_
    - _Preservation: callback onNovoCadastro continua sendo chamado ao clicar no botão_
    - _Requirements: 2.11_

  - [x] 9.3 Substituir FloatingActionButton por ExtendedFloatingActionButton
    - Substituir `FloatingActionButton` por `ExtendedFloatingActionButton(icon = { Icon(Add) }, text = { Text("Nova Matriz") })`
    - _Bug_Condition: isBugCondition(ListaFemeasScreen.kt) — FAB simples sem rótulo_
    - _Expected_Behavior: ExtendedFAB com ícone de adição e rótulo "Nova Matriz"_
    - _Preservation: callback onNovoCadastro inalterado_
    - _Requirements: 2.10_

  - [x] 9.4 Adicionar campo de busca e integrar com ViewModel
    - Adicionar `OutlinedTextField` de busca no topo da lista com `leadingIcon = Search`, `trailingIcon = Clear` (quando não vazio)
    - Conectar `value = state.termoBusca` e `onValueChange = { viewModel.atualizarBusca(it) }`
    - Substituir `state.femeas` por `state.femeasFiltradas` no `LazyColumn`
    - Adicionar estado "sem resultado de busca": `Text("Nenhuma fêmea encontrada com \"${state.termoBusca}\"")`
    - Substituir estado vazio de texto simples por `EstadoVazioFemeas(...)`
    - Substituir texto bruto de status por `StatusFemeaChip(status = femea.status)`
    - _Bug_Condition: isBugCondition(ListaFemeasScreen.kt) — lista não é filtrável_
    - _Expected_Behavior: campo de busca com debounce 300ms filtrando por identificação_
    - _Preservation: navegação para detalhe ao selecionar fêmea inalterada; LazyColumn continua usando key = { it.id }_
    - _Requirements: 2.10, 2.11, 2.12, 2.13, 3.6, 3.7_

- [x] 10. Fix verification — Verificar que o teste de bug condition agora passa

  - [x] 10.1 Re-executar Property 1: Bug Condition
    - **Property 1: Expected Behavior** - Conformidade Visual com o Design System
    - **IMPORTANT**: Re-executar o MESMO teste da tarefa 1 — NÃO escrever um novo teste
    - O teste da tarefa 1 codifica o comportamento esperado
    - Quando este teste passar, confirma que os gaps foram corrigidos
    - Executar o teste de bug condition da etapa 1
    - **EXPECTED OUTCOME**: Teste PASSA (confirma que os bugs foram corrigidos)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.7, 2.8, 2.9, 2.10, 2.11, 2.12, 2.13, 2.14, 2.15_

  - [x] 10.2 Re-executar Property 2: Preservation
    - **Property 2: Preservation** - Lógica de Negócio e Contratos de ViewModel Inalterados
    - **IMPORTANT**: Re-executar os MESMOS testes da tarefa 2 — NÃO escrever novos testes
    - Executar os testes de preservation da etapa 2
    - **EXPECTED OUTCOME**: Testes PASSAM (confirma que não há regressões)
    - Confirmar que todos os testes de domínio existentes continuam passando
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.10_

- [x] 11. Checkpoint — Garantir que todos os testes passam
  - Executar a suite completa de testes (unitários, integração, UI)
  - Confirmar que nenhum teste de domínio ou dados foi quebrado
  - Confirmar que o build compila sem warnings de API experimental não anotados
  - Verificar visualmente no emulador: tema verde, fonte Nunito, FAB com rótulo, busca funcional, DatePicker modal, Snackbar ao salvar, diálogo ao voltar com dados
  - Perguntar ao usuário se houver dúvidas antes de fechar a spec
