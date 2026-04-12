
# Refatoração UI Cadastro Fêmeas — Design Técnico

## Visão Geral

Este documento detalha **como** corrigir os 15 gaps de conformidade identificados no `bugfix.md`. O escopo é exclusivamente a camada de apresentação (`ui/theme/`, `ui/component/`, `presentation/femea/`). Zero alterações em `domain/` e `data/`.

A estratégia de correção é organizada em 6 grupos, seguindo a ordem de dependência: o tema visual deve ser corrigido primeiro, pois todos os componentes dependem dos tokens corretos.

```
Ordem de implementação:
  1. ui/theme/Color.kt          → paleta SuinoGestor
  2. ui/theme/Type.kt           → fonte Nunito
  3. ui/theme/Theme.kt          → shapes customizados
  4. ui/component/AlertaBanner.kt
  5. ui/component/CategoriaSelector.kt
  6. presentation/femea/viewmodel/FemeaUiState.kt + FemeaViewModel.kt
  7. presentation/femea/screen/CadastroFemeaScreen.kt
  8. presentation/femea/screen/ListaFemeasScreen.kt
     └── novos composables: EstadoVazioFemeas.kt, StatusFemeaChip.kt
```

---

## Glossário

- **Bug_Condition (C)**: Componente de apresentação que usa tokens, componentes ou comportamentos divergentes do design system em `.kiro/steering/design-system.md`
- **Property (P)**: Conformidade total com o design system após a correção
- **Preservation**: Lógica de negócio, fluxo de navegação e contratos de ViewModel inalterados
- **SuinoGestorShapes**: Objeto `Shapes` do M3 com raios customizados (28dp / 16dp / 12dp)
- **NunitoFontFamily**: `FontFamily` composta por pesos 400, 600 e 700 da fonte Nunito
- **ConnectedButtonGroup**: Componente M3 Expressive que substitui `SegmentedButton` — disponível a partir de `material3:1.4.0`
- **BOM atual**: `2024.09.00` → `material3:1.3.x` — ConnectedButtonGroup **não disponível** nesta versão
- **termoBusca**: Campo de `FemeaUiState` que armazena o texto digitado no campo de busca
- **femeasFiltradas**: Lista derivada de `femeas` filtrada por `termoBusca` no ViewModel

---

## Grupo 1 — Tema Visual (`ui/theme/`)

### 1.1 Color.kt — Paleta SuinoGestor

**Gap corrigido**: 1.1, 1.3 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/ui/theme/Color.kt`

**Problema atual**: O arquivo define apenas as 6 cores do template Android Studio (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`). O `Theme.kt` usa essas cores nos esquemas claro e escuro estáticos.

**Solução**: Substituir completamente as cores do template pelas seeds SuinoGestor e gerar esquemas claro/escuro completos usando `lightColorScheme`/`darkColorScheme` do M3.

**Pseudocódigo da correção**:
```
// Seeds primárias (usadas como referência para derivar o esquema completo)
SuinoGreen  = Color(0xFF5D7A3E)   // primary seed
SuinoAmber  = Color(0xFF8B6914)   // secondary seed
SuinoTerra  = Color(0xFFC0522A)   // tertiary seed

// Esquema claro — derivado manualmente das seeds via Material Theme Builder
LightColorScheme = lightColorScheme(
  primary             = Color(0xFF5D7A3E),
  onPrimary           = Color(0xFFFFFFFF),
  primaryContainer    = Color(0xFFDEF0BB),
  onPrimaryContainer  = Color(0xFF1A2E00),
  secondary           = Color(0xFF8B6914),
  onSecondary         = Color(0xFFFFFFFF),
  secondaryContainer  = Color(0xFFFFDEA0),
  onSecondaryContainer= Color(0xFF2B1D00),
  tertiary            = Color(0xFFC0522A),
  onTertiary          = Color(0xFFFFFFFF),
  tertiaryContainer   = Color(0xFFFFDBCF),
  onTertiaryContainer = Color(0xFF3E0E00),
  error               = Color(0xFFBA1A1A),
  onError             = Color(0xFFFFFFFF),
  errorContainer      = Color(0xFFFFDAD6),
  onErrorContainer    = Color(0xFF410002),
  background          = Color(0xFFFCFCF4),
  onBackground        = Color(0xFF1B1C17),
  surface             = Color(0xFFFCFCF4),
  onSurface           = Color(0xFF1B1C17),
  surfaceVariant      = Color(0xFFE2E4D5),
  onSurfaceVariant    = Color(0xFF45483C)
)

// Esquema escuro — derivado das mesmas seeds
DarkColorScheme = darkColorScheme(
  primary             = Color(0xFFC2D89E),
  onPrimary           = Color(0xFF2D4A0E),
  primaryContainer    = Color(0xFF446122),
  onPrimaryContainer  = Color(0xFFDEF0BB),
  secondary           = Color(0xFFEFBE4A),
  onSecondary         = Color(0xFF472D00),
  secondaryContainer  = Color(0xFF664400),
  onSecondaryContainer= Color(0xFFFFDEA0),
  tertiary            = Color(0xFFFFB59A),
  onTertiary          = Color(0xFF612200),
  tertiaryContainer   = Color(0xFF8A3914),
  onTertiaryContainer = Color(0xFFFFDBCF),
  error               = Color(0xFFFFB4AB),
  onError             = Color(0xFF690005),
  errorContainer      = Color(0xFF93000A),
  onErrorContainer    = Color(0xFFFFDAD6),
  background          = Color(0xFF13140F),
  onBackground        = Color(0xFFE3E4D8),
  surface             = Color(0xFF13140F),
  onSurface           = Color(0xFFE3E4D8),
  surfaceVariant      = Color(0xFF45483C),
  onSurfaceVariant    = Color(0xFFC5C8B9)
)
```

**Impacto em outros arquivos**: `Theme.kt` deve referenciar `LightColorScheme` e `DarkColorScheme` em vez das cores do template.

---

### 1.2 Type.kt — Fonte Nunito

**Gap corrigido**: 1.2 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/ui/theme/Type.kt`

**Problema atual**: `Typography` define apenas `bodyLarge` com `FontFamily.Default` (Roboto). Todas as demais escalas tipográficas do M3 ficam com Roboto por herança.

**Dependência nova**: `androidx.compose.ui:ui-text-google-fonts` — deve ser adicionada ao `libs.versions.toml` e ao `app/build.gradle.kts`.

**Adições ao `libs.versions.toml`**:
```toml
[versions]
# adicionar:
uiTextGoogleFonts = "1.7.8"   # alinhar com a versão do Compose BOM em uso

[libraries]
# adicionar:
androidx-compose-ui-text-google-fonts = { group = "androidx.compose.ui", name = "ui-text-google-fonts", version.ref = "uiTextGoogleFonts" }
```

**Adição ao `app/build.gradle.kts`**:
```kotlin
implementation(libs.androidx.compose.ui.text.google.fonts)
```

**Pseudocódigo da correção em Type.kt**:
```
val GoogleFontProvider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage   = "com.google.android.gms",
  certificates      = R.array.com_google_android_gms_fonts_certs
)

val NunitoFont = GoogleFont("Nunito")

val NunitoFontFamily = FontFamily(
  Font(googleFont = NunitoFont, fontProvider = GoogleFontProvider, weight = FontWeight.Normal),
  Font(googleFont = NunitoFont, fontProvider = GoogleFontProvider, weight = FontWeight.SemiBold),
  Font(googleFont = NunitoFont, fontProvider = GoogleFontProvider, weight = FontWeight.Bold)
)

// Aplicar NunitoFontFamily a todas as escalas do M3
val Typography = Typography(
  displayLarge   = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,    fontSize = 57.sp, lineHeight = 64.sp),
  displayMedium  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,    fontSize = 45.sp, lineHeight = 52.sp),
  displaySmall   = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,    fontSize = 36.sp, lineHeight = 44.sp),
  headlineLarge  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 32.sp, lineHeight = 40.sp),
  headlineMedium = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 28.sp, lineHeight = 36.sp),
  headlineSmall  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 24.sp, lineHeight = 32.sp),
  titleLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 22.sp, lineHeight = 28.sp),
  titleMedium    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
  titleSmall     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
  bodyLarge      = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,  fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
  bodyMedium     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,  fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
  bodySmall      = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,  fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
  labelLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
  labelMedium    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
  labelSmall     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)
```

**Fallback offline**: `GoogleFont` carrega a fonte via GMS. Para garantir funcionamento offline, adicionar os arquivos `.ttf` da Nunito em `app/src/main/res/font/` e usar `Font(resId = R.font.nunito_regular)` como fallback no `FontFamily`. A abordagem recomendada é usar assets locais como fonte primária e remover a dependência de GMS, dado que o app é 100% offline.

**Decisão de implementação**: Usar **assets locais** (`res/font/`) em vez de `GoogleFont` para garantir conformidade com o requisito offline-first (Princípio III do tech.md). Os arquivos `nunito_regular.ttf`, `nunito_semibold.ttf` e `nunito_bold.ttf` devem ser adicionados ao projeto. A dependência `ui-text-google-fonts` **não é necessária** nesse caso.

---

### 1.3 Theme.kt — Shapes Customizados

**Gap corrigido**: 1.4 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/ui/theme/Theme.kt`

**Problema atual**: `MaterialTheme` é chamado sem o parâmetro `shapes`, usando os defaults do M3 (`ExtraLarge=28dp`, `Large=16dp`, `Medium=12dp` já são os defaults do M3 — mas não estão explicitamente definidos, e o `Medium` padrão do M3 é `12dp` enquanto o design system especifica `12dp`). O problema real é que o tema não passa `shapes` explicitamente, deixando o controle implícito.

**Solução**: Definir `SuinoGestorShapes` explicitamente e passá-lo ao `MaterialTheme`.

**Pseudocódigo da correção**:
```
val SuinoGestorShapes = Shapes(
  extraSmall = RoundedCornerShape(4.dp),
  small      = RoundedCornerShape(8.dp),
  medium     = RoundedCornerShape(12.dp),   // chips, campos, botões secundários
  large      = RoundedCornerShape(16.dp),   // cards de lista, bottom sheets
  extraLarge = RoundedCornerShape(28.dp)    // cards de destaque, módulos dashboard
)

// Em SuinoGestorTheme:
MaterialTheme(
  colorScheme = colorScheme,   // já existente
  typography  = Typography,    // já existente
  shapes      = SuinoGestorShapes,  // NOVO
  content     = content
)
```

**Impacto**: Todos os componentes que usam `MaterialTheme.shapes.medium` (ex.: `AlertaBanner`, `FemeaCard`) passarão a usar `12dp` explicitamente definido.

---

## Grupo 2 — AlertaBanner (`ui/component/AlertaBanner.kt`)

**Gap corrigido**: 1.14 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/ui/component/AlertaBanner.kt`

**Problema atual**: O `Card` usa `containerColor = MaterialTheme.colorScheme.errorContainer` e o ícone/texto usam `MaterialTheme.colorScheme.onErrorContainer`. Isso comunica erro crítico (vermelho) para um alerta de atenção zootécnica.

**Solução**: Substituir os tokens de cor por `tertiaryContainer` / `onTertiaryContainer` (terracota — cor semântica de alertas de atenção no design system).

**Diff conceitual**:
```
// ANTES
containerColor = MaterialTheme.colorScheme.errorContainer
tint            = MaterialTheme.colorScheme.onErrorContainer
color           = MaterialTheme.colorScheme.onErrorContainer

// DEPOIS
containerColor = MaterialTheme.colorScheme.tertiaryContainer
tint            = MaterialTheme.colorScheme.onTertiaryContainer
color           = MaterialTheme.colorScheme.onTertiaryContainer
```

**Estrutura preservada**: `Row + Icon + Spacer + Text` permanece idêntica. Apenas os 3 tokens de cor mudam.

**Raciocínio de token**: Conforme o design system, `tertiaryContainer` é o token para "Offline banner" e alertas de atenção não críticos. O alerta de idade mínima (160 dias) é um aviso zootécnico informativo, não um erro de operação — portanto `tertiaryContainer` é semanticamente correto.

---

## Grupo 3 — CategoriaSelector (`ui/component/CategoriaSelector.kt`)

**Gap corrigido**: 1.15 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/ui/component/CategoriaSelector.kt`

**Problema atual**: Usa `SingleChoiceSegmentedButtonRow` + `SegmentedButton`, que é o componente M3 padrão (não expressive). O design system especifica `ConnectedButtonGroup` do M3 Expressive com shape morph na seleção.

### Análise de Disponibilidade da API

**`ConnectedButtonGroup` no Compose**:
- Disponível a partir de `material3:1.4.0` (M3 Expressive)
- O projeto usa BOM `2024.09.00` → `material3:1.3.x`
- **Conclusão**: API não disponível na versão atual do projeto

### Decisão de Implementação

**Opção A — Atualizar BOM para versão com M3 Expressive** (recomendada):
- Atualizar `composeBom` em `libs.versions.toml` para `2025.05.00` ou superior (que inclui `material3:1.4.x`)
- Usar `ConnectedButtonGroup` + `ToggleButton` nativos
- Risco: possíveis breaking changes em outros componentes

**Opção B — Fallback melhorado com `SegmentedButton`** (conservadora):
- Manter `SingleChoiceSegmentedButtonRow` / `SegmentedButton`
- Adicionar animação de shape morph manual via `animateFloatAsState`
- Marcar com `TODO` para migração quando BOM for atualizado

**Decisão**: Implementar **Opção A** — atualizar o BOM. O design system é explícito sobre `ConnectedButtonGroup`. A atualização do BOM é o caminho correto; qualquer breaking change deve ser tratado como parte desta refatoração.

### Pseudocódigo com ConnectedButtonGroup (Opção A)

```
// Requer material3:1.4.0+ (BOM 2025.05.00+)
// Adicionar ao libs.versions.toml:
//   composeBom = "2025.05.00"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoriaSelector(
  categoriaSelecionada: CategoriaFemea?,
  onCategoriaSelecionada: (CategoriaFemea) -> Unit,
  modifier: Modifier = Modifier
) {
  val opcoes = listOf(CategoriaFemea.MARRA to "Marrã", CategoriaFemea.MATRIZ to "Matriz")

  ConnectedButtonGroup(modifier = modifier.fillMaxWidth()) {
    opcoes.forEach { (categoria, label) ->
      ToggleButton(
        checked   = categoriaSelecionada == categoria,
        onCheckedChange = { if (it) onCategoriaSelecionada(categoria) },
        modifier  = Modifier.weight(1f)
      ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}
```

**Tokens M3E aplicados**:
- `md.comp.button-group.connected.medium.between-space`: 2dp
- `md.comp.button-group.connected.medium.container.height`: 56dp
- `md.comp.button-group.connected.medium.selected.inner-corner.corner-size`: 50% (shape morph circular na seleção)
- Shape morph animado via `md.sys.motion.spring.fast.spatial` (stiffness: 1400, damping: 0.9)

### Pseudocódigo Fallback (Opção B — caso BOM não seja atualizado)

```
// TODO: Migrar para ConnectedButtonGroup quando BOM >= 2025.05.00
// Fallback: SegmentedButton com animação de shape manual
@Composable
fun CategoriaSelector(...) {
  val opcoes = listOf(CategoriaFemea.MARRA to "Marrã", CategoriaFemea.MATRIZ to "Matriz")
  SingleChoiceSegmentedButtonRow(modifier = modifier) {
    opcoes.forEachIndexed { index, (categoria, label) ->
      SegmentedButton(
        selected = categoriaSelecionada == categoria,
        onClick  = { onCategoriaSelecionada(categoria) },
        shape    = SegmentedButtonDefaults.itemShape(index = index, count = opcoes.size),
        label    = { Text(text = label, style = MaterialTheme.typography.labelLarge) }
      )
    }
  }
}
```

---

## Grupo 4 — FemeaUiState + FemeaViewModel (suporte à busca)

**Gap corrigido**: 1.13 do bugfix.md (suporte de ViewModel para o campo de busca da lista)

### 4.1 FemeaUiState.kt

**Arquivo**: `app/src/main/java/br/com/suinogestor/presentation/femea/viewmodel/FemeaUiState.kt`

**Problema atual**: `FemeaUiState` não tem campo para o termo de busca nem lista filtrada.

**Pseudocódigo da correção**:
```
data class FemeaUiState(
  val carregando: Boolean = false,
  val femeas: List<FemeaResumo> = emptyList(),
  val erro: String? = null,
  val termoBusca: String = "",                    // NOVO — texto digitado no campo de busca
  val femeasFiltradas: List<FemeaResumo> = emptyList()  // NOVO — lista derivada filtrada
)
```

**Invariante**: `femeasFiltradas` é sempre um subconjunto de `femeas`. Quando `termoBusca` está vazio, `femeasFiltradas == femeas`.

### 4.2 FemeaViewModel.kt

**Arquivo**: `app/src/main/java/br/com/suinogestor/presentation/femea/viewmodel/FemeaViewModel.kt`

**Problema atual**: Não há função de busca nem debounce.

**Pseudocódigo da correção**:
```
// Adicionar ao ViewModel:

private val _termoBusca = MutableStateFlow("")

init {
  carregarFemeas()
  observarBusca()   // NOVO
}

// NOVO — debounce de 300ms via Flow
private fun observarBusca() {
  viewModelScope.launch {
    _termoBusca
      .debounce(300)
      .collect { termo ->
        _listaState.update { state ->
          state.copy(
            termoBusca = termo,
            femeasFiltradas = if (termo.isBlank()) {
              state.femeas
            } else {
              state.femeas.filter {
                it.identificacao.contains(termo, ignoreCase = true)
              }
            }
          )
        }
      }
  }
}

// NOVO — chamado pela tela ao digitar no campo de busca
fun atualizarBusca(termo: String) {
  _termoBusca.value = termo
}

// MODIFICAR carregarFemeas para também atualizar femeasFiltradas:
private fun carregarFemeas() {
  viewModelScope.launch {
    listarFemeas()
      .map { lista -> lista.map { femea -> FemeaResumo(...) } }
      .catch { e -> _listaState.update { it.copy(erro = e.message) } }
      .collect { resumos ->
        _listaState.update { state ->
          state.copy(
            femeas = resumos,
            carregando = false,
            femeasFiltradas = if (state.termoBusca.isBlank()) resumos
                              else resumos.filter {
                                it.identificacao.contains(state.termoBusca, ignoreCase = true)
                              }
          )
        }
      }
  }
}
```

**Preservação**: Os `StateFlow`s `listaState` e `cadastroState` mantêm a mesma semântica. A função `cadastrar` e `atualizarIdadeExibida` permanecem inalteradas. Nenhuma alteração em `domain/` ou `data/`.

---

## Grupo 5 — CadastroFemeaScreen

**Gaps corrigidos**: 1.5, 1.6, 1.7, 1.8, 1.9 do bugfix.md

**Arquivo**: `app/src/main/java/br/com/suinogestor/presentation/femea/screen/CadastroFemeaScreen.kt`

### 5.1 DatePicker Modal (gap 1.5)

**Problema atual**: Campo `OutlinedTextField` com digitação manual `aaaa-mm-dd`.

**Solução**: Substituir por botão de abertura + `DatePickerDialog` do M3.

**Pseudocódigo**:
```
// Estado do diálogo
var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }
val datePickerState = rememberDatePickerState(
  initialSelectedDateMillis = System.currentTimeMillis()
)

// Botão que abre o diálogo (substitui o OutlinedTextField de data)
OutlinedTextField(
  value = dataNascimentoTexto,
  onValueChange = {},
  readOnly = true,
  label = { Text("Data de Nascimento *") },
  trailingIcon = {
    Row {
      if (state.idadeFormatada.isNotEmpty()) IdadeChip(idadeFormatada = state.idadeFormatada)
      IconButton(onClick = { mostrarDatePicker = true }) {
        Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar data")
      }
    }
  },
  modifier = Modifier.fillMaxWidth()
)

// Diálogo
if (mostrarDatePicker) {
  DatePickerDialog(
    onDismissRequest = { mostrarDatePicker = false },
    confirmButton = {
      TextButton(onClick = {
        datePickerState.selectedDateMillis?.let { millis ->
          val data = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
          dataNascimentoTexto = data.toString()
          viewModel.atualizarIdadeExibida(data)
        }
        mostrarDatePicker = false
      }) { Text("OK") }
    },
    dismissButton = {
      TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}
```

### 5.2 rememberSaveable (gap 1.6)

**Problema atual**: Todos os campos usam `remember`, perdendo estado na rotação.

**Solução**: Substituir todos os `remember { mutableStateOf(...) }` por `rememberSaveable { mutableStateOf(...) }`.

**Campos afetados**:
```
// ANTES → DEPOIS
var identificacao      by remember      { mutableStateOf("") }
var identificacao      by rememberSaveable { mutableStateOf("") }

var dataNascimentoTexto by remember     { mutableStateOf("") }
var dataNascimentoTexto by rememberSaveable { mutableStateOf("") }

var racaLinhagem       by remember      { mutableStateOf("") }
var racaLinhagem       by rememberSaveable { mutableStateOf("") }

var categoria          by remember      { mutableStateOf<CategoriaFemea?>(null) }
var categoria          by rememberSaveable { mutableStateOf<CategoriaFemea?>(null) }

var pesoEntrada        by remember      { mutableStateOf("") }
var pesoEntrada        by rememberSaveable { mutableStateOf("") }

var eccSelecionado     by remember      { mutableStateOf<Int?>(null) }
var eccSelecionado     by rememberSaveable { mutableStateOf<Int?>(null) }

var origemSelecionada  by remember      { mutableStateOf<OrigemAnimal?>(null) }
var origemSelecionada  by rememberSaveable { mutableStateOf<OrigemAnimal?>(null) }

var mostrarDatePicker  by rememberSaveable { mutableStateOf(false) }  // NOVO
```

**Nota**: `CategoriaFemea` e `OrigemAnimal` são enums — `rememberSaveable` os serializa automaticamente via `Bundle`. Nenhum `Saver` customizado é necessário.

### 5.3 Snackbar de Confirmação (gap 1.7)

**Problema atual**: Ao salvar com sucesso, `LaunchedEffect` chama `onCadastroSalvo()` diretamente sem feedback.

**Solução**: Adicionar `SnackbarHostState` ao `Scaffold` e exibir Snackbar antes de navegar.

**Pseudocódigo**:
```
val snackbarHostState = remember { SnackbarHostState() }

Scaffold(
  topBar = { ... },
  snackbarHost = { SnackbarHost(hostState = snackbarHostState) }  // NOVO
) { ... }

// Modificar LaunchedEffect:
LaunchedEffect(state.sucesso) {
  if (state.sucesso) {
    snackbarHostState.showSnackbar(
      message  = "Fêmea cadastrada com sucesso!",
      duration = SnackbarDuration.Short   // ~4 segundos
    )
    onCadastroSalvo()
  }
}
```

### 5.4 BackHandler com AlertDialog (gap 1.8)

**Problema atual**: Botão Voltar descarta dados silenciosamente.

**Solução**: Adicionar `BackHandler` que intercepta a navegação quando há dados preenchidos e exibe `AlertDialog` de confirmação.

**Pseudocódigo**:
```
// Estado do diálogo de descarte
var mostrarDialogoDescarte by rememberSaveable { mutableStateOf(false) }

// Função auxiliar — verifica se há dados preenchidos
val temDadosPreenchidos = identificacao.isNotBlank()
  || dataNascimentoTexto.isNotBlank()
  || racaLinhagem.isNotBlank()
  || categoria != null
  || pesoEntrada.isNotBlank()
  || eccSelecionado != null
  || origemSelecionada != null

// BackHandler — intercepta apenas quando há dados
BackHandler(enabled = temDadosPreenchidos) {
  mostrarDialogoDescarte = true
}

// Diálogo de confirmação
if (mostrarDialogoDescarte) {
  AlertDialog(
    onDismissRequest = { mostrarDialogoDescarte = false },
    title   = { Text("Descartar alterações?") },
    text    = { Text("Os dados preenchidos serão perdidos.") },
    confirmButton = {
      TextButton(onClick = {
        mostrarDialogoDescarte = false
        onVoltar()
      }) { Text("Descartar") }
    },
    dismissButton = {
      TextButton(onClick = { mostrarDialogoDescarte = false }) {
        Text("Continuar editando")
      }
    }
  )
}

// Botão Voltar na TopAppBar também deve usar o mesmo fluxo:
navigationIcon = {
  IconButton(onClick = {
    if (temDadosPreenchidos) mostrarDialogoDescarte = true
    else onVoltar()
  }) {
    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
  }
}
```

### 5.5 CircularProgressIndicator Small (gap 1.9)

**Problema atual**: `CircularProgressIndicator()` sem tamanho definido ocupa espaço desproporcional dentro do botão.

**Solução**: Adicionar `modifier = Modifier.size(24.dp)` para o tamanho Small conforme o design system.

**Diff conceitual**:
```
// ANTES
if (state.salvando) {
  CircularProgressIndicator()
}

// DEPOIS
if (state.salvando) {
  CircularProgressIndicator(
    modifier = Modifier.size(24.dp),
    strokeWidth = 2.dp
  )
}
```

---

## Grupo 6 — ListaFemeasScreen + Novos Composables

**Gaps corrigidos**: 1.10, 1.11, 1.12, 1.13 do bugfix.md

**Arquivo principal**: `app/src/main/java/br/com/suinogestor/presentation/femea/screen/ListaFemeasScreen.kt`

**Novos arquivos**:
- `app/src/main/java/br/com/suinogestor/ui/component/EstadoVazioFemeas.kt`
- `app/src/main/java/br/com/suinogestor/ui/component/StatusFemeaChip.kt`

### 6.1 ExtendedFloatingActionButton (gap 1.10)

**Problema atual**: `FloatingActionButton` com apenas ícone de adição.

**Solução**: Substituir por `ExtendedFloatingActionButton` com ícone e rótulo "Nova Matriz".

**Pseudocódigo**:
```
// ANTES
floatingActionButton = {
  FloatingActionButton(onClick = onNovoCadastro) {
    Icon(imageVector = Icons.Default.Add, contentDescription = "Novo cadastro")
  }
}

// DEPOIS
floatingActionButton = {
  ExtendedFloatingActionButton(
    onClick = onNovoCadastro,
    icon    = { Icon(Icons.Default.Add, contentDescription = null) },
    text    = { Text("Nova Matriz") }
  )
}
```

**Token M3E**: `md.comp.extended-fab.small.container.height = 56dp`, shape `16dp corner radius`.

### 6.2 EstadoVazioFemeas (gap 1.11)

**Novo arquivo**: `app/src/main/java/br/com/suinogestor/ui/component/EstadoVazioFemeas.kt`

**Problema atual**: Estado vazio exibe apenas `Text("Nenhuma fêmea cadastrada")` centralizado.

**Solução**: Criar composable dedicado com ilustração vetorial, mensagem contextual e botão de ação primária.

**Pseudocódigo**:
```
@Composable
fun EstadoVazioFemeas(
  onCadastrarPrimeiraFemea: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Ilustração vetorial temática (silhueta de suíno)
    // Usar Icon com painter de recurso vetorial ou Image com painterResource
    Icon(
      painter = painterResource(R.drawable.ic_femea_vazia),  // vetor a criar
      contentDescription = null,
      modifier = Modifier.size(120.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Text(
      text  = "Nenhuma fêmea cadastrada ainda",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Text(
      text  = "Cadastre sua primeira matriz para começar a acompanhar o plantel",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Button(onClick = onCadastrarPrimeiraFemea) {
      Text("Cadastrar primeira fêmea")
    }
  }
}
```

**Recurso vetorial**: Criar `res/drawable/ic_femea_vazia.xml` — silhueta simples de suíno em estilo Material Symbols Rounded. Pode ser um `VectorDrawable` simplificado ou importado do Material Symbols.

**Uso em ListaFemeasScreen**:
```
// ANTES
state.femeas.isEmpty() -> Text(
  text = "Nenhuma fêmea cadastrada",
  ...
)

// DEPOIS
state.femeas.isEmpty() -> EstadoVazioFemeas(
  onCadastrarPrimeiraFemea = onNovoCadastro,
  modifier = Modifier.align(Alignment.Center)
)
```

### 6.3 StatusFemeaChip (gap 1.12)

**Novo arquivo**: `app/src/main/java/br/com/suinogestor/ui/component/StatusFemeaChip.kt`

**Problema atual**: Status exibido como texto bruto `femea.status.name.replace("_", " ")` com cor `primary` uniforme.

**Solução**: Criar `StatusFemeaChip` com cores semânticas por status conforme o design system.

**Mapeamento de tokens**:
```
StatusFemea.GESTANTE          → containerColor = primaryContainer,   contentColor = onPrimaryContainer
StatusFemea.LACTANTE          → containerColor = secondaryContainer, contentColor = onSecondaryContainer
StatusFemea.VAZIA             → containerColor = tertiaryContainer,  contentColor = onTertiaryContainer
StatusFemea.DESCARTADA        → containerColor = errorContainer,     contentColor = onErrorContainer
StatusFemea.MARRA_PREPARACAO  → containerColor = surfaceVariant,     contentColor = onSurfaceVariant
StatusFemea.AGUARDANDO_COBERTURA → containerColor = surfaceVariant,  contentColor = onSurfaceVariant
```

**Pseudocódigo**:
```
@Composable
fun StatusFemeaChip(
  status: StatusFemea,
  modifier: Modifier = Modifier
) {
  val (containerColor, contentColor) = when (status) {
    StatusFemea.GESTANTE           -> MaterialTheme.colorScheme.primaryContainer   to MaterialTheme.colorScheme.onPrimaryContainer
    StatusFemea.LACTANTE           -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    StatusFemea.VAZIA              -> MaterialTheme.colorScheme.tertiaryContainer  to MaterialTheme.colorScheme.onTertiaryContainer
    StatusFemea.DESCARTADA         -> MaterialTheme.colorScheme.errorContainer     to MaterialTheme.colorScheme.onErrorContainer
    StatusFemea.MARRA_PREPARACAO,
    StatusFemea.AGUARDANDO_COBERTURA -> MaterialTheme.colorScheme.surfaceVariant   to MaterialTheme.colorScheme.onSurfaceVariant
  }

  val label = when (status) {
    StatusFemea.GESTANTE             -> "Gestante"
    StatusFemea.LACTANTE             -> "Lactante"
    StatusFemea.VAZIA                -> "Vazia"
    StatusFemea.DESCARTADA           -> "Descartada"
    StatusFemea.MARRA_PREPARACAO     -> "Em preparação"
    StatusFemea.AGUARDANDO_COBERTURA -> "Aguardando cobertura"
  }

  Surface(
    modifier = modifier,
    shape    = MaterialTheme.shapes.medium,   // 12dp
    color    = containerColor
  ) {
    Text(
      text     = label,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      style    = MaterialTheme.typography.labelMedium,
      color    = contentColor
    )
  }
}
```

**Uso em FemeaCard** (dentro de `ListaFemeasScreen.kt`):
```
// ANTES
Text(
  text  = femea.status.name.replace("_", " "),
  style = MaterialTheme.typography.labelSmall,
  color = MaterialTheme.colorScheme.primary
)

// DEPOIS
StatusFemeaChip(status = femea.status)
```

### 6.4 SearchBar / Campo de Busca (gap 1.13)

**Problema atual**: Não há campo de busca na lista.

**Solução**: Adicionar `OutlinedTextField` de busca no topo da lista. O debounce de 300ms é tratado no ViewModel (Grupo 4).

**Pseudocódigo em ListaFemeasScreen**:
```
// Adicionar ao Box principal, acima do LazyColumn:
Column(modifier = Modifier.fillMaxSize()) {
  OutlinedTextField(
    value = state.termoBusca,
    onValueChange = { viewModel.atualizarBusca(it) },
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    placeholder = { Text("Buscar por identificação...") },
    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    trailingIcon = {
      if (state.termoBusca.isNotEmpty()) {
        IconButton(onClick = { viewModel.atualizarBusca("") }) {
          Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
        }
      }
    },
    singleLine = true,
    shape = MaterialTheme.shapes.medium
  )

  // Lista usa femeasFiltradas em vez de femeas
  when {
    state.carregando -> CircularProgressIndicator(...)
    state.erro != null -> Text(...)
    state.femeasFiltradas.isEmpty() && state.termoBusca.isNotEmpty() ->
      // Estado "sem resultado de busca"
      Text(
        text = "Nenhuma fêmea encontrada com \"${state.termoBusca}\"",
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
      )
    state.femeasFiltradas.isEmpty() ->
      EstadoVazioFemeas(onCadastrarPrimeiraFemea = onNovoCadastro, ...)
    else ->
      LazyColumn(...) {
        items(state.femeasFiltradas, key = { it.id }) { femea -> FemeaCard(...) }
      }
  }
}
```

---

---

## Correctness Properties

Property 1: Bug Condition — Conformidade Visual com o Design System

_Para qualquer_ componente de apresentação do módulo `cadastro-femeas` onde `isBugCondition(X)` retorna verdadeiro (usa tokens, componentes ou comportamentos divergentes do design system), a versão corrigida SHALL renderizar usando a paleta SuinoGestor (`primary=#5D7A3E`, `secondary=#8B6914`, `tertiary=#C0522A`), fonte Nunito, shapes customizados (`extraLarge=28dp`, `large=16dp`, `medium=12dp`), e os componentes M3/M3E corretos (`ConnectedButtonGroup`, `ExtendedFloatingActionButton`, `DatePickerDialog`, `SnackbarHost`, `BackHandler`+`AlertDialog`).

**Validates: Requirements 2.1, 2.2, 2.3, 2.4, 2.5, 2.7, 2.8, 2.9, 2.10, 2.11, 2.12, 2.13, 2.14, 2.15**

Property 2: Preservation — Lógica de Negócio e Contratos de ViewModel Inalterados

_Para qualquer_ entrada onde `isBugCondition(X)` retorna falso (comportamentos de domínio, persistência, validação e navegação), a versão corrigida SHALL produzir exatamente o mesmo resultado que a versão original: persistência via `CadastrarFemeaUseCase`, validações inline, alertas de idade mínima, erros de identificação duplicada, navegação para detalhe, e os `StateFlow`s `listaState`/`cadastroState` com a mesma semântica (exceto pelos campos `termoBusca` e `femeasFiltradas` adicionados).

**Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.10**

---

## Dependências e Mudanças de Build

### libs.versions.toml

| Mudança | Tipo | Justificativa |
|---|---|---|
| `composeBom = "2025.05.00"` (ou superior) | Atualização | Necessário para `ConnectedButtonGroup` (material3 1.4+) |
| Remover `uiTextGoogleFonts` | N/A | Não necessário — usar assets locais (offline-first) |

### app/build.gradle.kts

Nenhuma dependência nova além da atualização do BOM.

### Recursos a criar

| Arquivo | Tipo | Descrição |
|---|---|---|
| `res/font/nunito_regular.ttf` | Asset | Fonte Nunito Regular 400 |
| `res/font/nunito_semibold.ttf` | Asset | Fonte Nunito SemiBold 600 |
| `res/font/nunito_bold.ttf` | Asset | Fonte Nunito Bold 700 |
| `res/drawable/ic_femea_vazia.xml` | VectorDrawable | Ilustração estado vazio |

---

## Estratégia de Testes

### Abordagem

Seguindo a metodologia bug condition do `bugfix.md`:

1. **Exploratory (antes do fix)**: Confirmar que os gaps existem — screenshots/testes de UI com o tema atual mostram cores roxas, Roboto, FAB simples, etc.
2. **Fix Checking**: Verificar que cada gap foi corrigido após a implementação.
3. **Preservation Checking**: Verificar que nenhum comportamento de domínio foi alterado.

### Testes de UI (Compose Test Library)

**Fix Checking — Tema**:
```
// Verificar que o tema usa as cores corretas
// Testar com darkTheme=false e dynamicColor=false para forçar o esquema estático
onNodeWithText("Cadastrar").assertIsDisplayed()
// Verificar cor do botão via semantics ou screenshot test
```

**Fix Checking — rememberSaveable**:
```
// Preencher campo de identificação
onNodeWithText("Identificação (brinco/tatuagem) *").performTextInput("F001")
// Simular rotação (recriar Activity)
activityRule.scenario.recreate()
// Verificar que o campo mantém o valor
onNodeWithText("F001").assertIsDisplayed()
```

**Fix Checking — BackHandler**:
```
// Preencher um campo
onNodeWithText("Identificação (brinco/tatuagem) *").performTextInput("F001")
// Pressionar Voltar
Espresso.pressBack()
// Verificar que o diálogo aparece
onNodeWithText("Descartar alterações?").assertIsDisplayed()
// Confirmar descarte
onNodeWithText("Descartar").performClick()
// Verificar navegação
```

**Fix Checking — Snackbar**:
```
// Preencher formulário válido e submeter
// Verificar que Snackbar aparece antes de navegar
onNodeWithText("Fêmea cadastrada com sucesso!").assertIsDisplayed()
```

**Fix Checking — StatusFemeaChip**:
```
// Verificar que o chip de status Gestante usa primaryContainer
// Verificar que o chip de status Descartada usa errorContainer
```

**Fix Checking — ExtendedFAB**:
```
onNodeWithText("Nova Matriz").assertIsDisplayed()
```

**Fix Checking — EstadoVazioFemeas**:
```
// Com lista vazia
onNodeWithText("Cadastrar primeira fêmea").assertIsDisplayed()
```

**Fix Checking — Busca com debounce**:
```
// Digitar no campo de busca
onNodeWithText("Buscar por identificação...").performTextInput("F001")
// Aguardar debounce (300ms)
// Verificar que a lista foi filtrada
```

### Testes de Propriedade (Preservation Checking)

**Property 2 — Preservation**:
```kotlin
// Para qualquer sequência de inputs não relacionados à UI (cadastro, listagem):
// Verificar que CadastrarFemeaUseCase continua funcionando identicamente
// Usar os testes existentes da spec cadastro-femeas como baseline
// Nenhum teste de domínio deve quebrar após esta refatoração
```

### Testes Unitários

- `FemeaViewModel.atualizarBusca`: verificar que `femeasFiltradas` é atualizado corretamente após debounce
- `FemeaViewModel.atualizarBusca("")`: verificar que `femeasFiltradas == femeas` quando busca está vazia
- `StatusFemeaChip`: verificar mapeamento correto de cada `StatusFemea` para os tokens de cor

### Testes de Integração

- Verificar que `FemeaUiState` com `termoBusca` e `femeasFiltradas` é serializado/desserializado corretamente pelo `SavedStateHandle` (se aplicável)
- Verificar que a atualização do BOM não quebra nenhum teste existente

---

## Restrições e Decisões de Design

| Decisão | Justificativa |
|---|---|
| Assets locais para Nunito (não GoogleFont) | Princípio III do tech.md: 100% offline. GoogleFont requer conectividade para download inicial. |
| Atualizar BOM para 2025.05.00+ | ConnectedButtonGroup é requisito explícito do design system. Fallback com SegmentedButton seria não-conformidade documentada. |
| `rememberSaveable` sem Saver customizado para enums | Enums são `Parcelable`-compatíveis via `Bundle` — serialização automática. |
| `OutlinedTextField` readOnly para data (não `SearchBar` do M3) | `SearchBar` do M3 tem comportamento de expansão inadequado para campo de data em formulário. `OutlinedTextField` readOnly com `DatePickerDialog` é o padrão do design system. |
| `OutlinedTextField` para busca (não `SearchBar` do M3) | `SearchBar` do M3 Expressive ocupa a tela inteira em modo expandido — inadequado para o perfil de usuário (baixa familiaridade tecnológica). `OutlinedTextField` com ícone de busca é mais previsível. |
| Zero alterações em `domain/` e `data/` | Restrição explícita do escopo. Toda lógica de negócio permanece intacta. |
