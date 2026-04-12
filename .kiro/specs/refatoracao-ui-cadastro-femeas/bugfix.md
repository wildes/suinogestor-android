# Bugfix Requirements Document

## Introduction

A camada de apresentação do módulo `cadastro-femeas` foi implementada com o template padrão do Android Studio (paleta roxo/rosa, fonte padrão, componentes M3 genéricos) em vez de seguir o design system do SuinoGestor definido em `.kiro/steering/design-system.md`. O impacto é visual e funcional: o app não transmite a identidade agrícola esperada, componentes errados comunicam semântica incorreta (ex.: alerta de atenção exibido como erro crítico), e comportamentos essenciais de UX estão ausentes (perda de dados na rotação, ausência de feedback ao salvar, sem busca na lista). Esta spec cobre exclusivamente a camada de apresentação — domínio e dados permanecem intocados.

---

## Bug Analysis

### Current Behavior (Defect)

**Tema Visual**

1.1 QUANDO o app é iniciado em dispositivos sem dynamic color (Android < 12 ou dynamic color desativado) ENTÃO o sistema exibe a paleta padrão do template Android (primary `#6650A4` roxo, secondary `#625B71` cinza-roxo, tertiary `#7D5260` rosa) em vez da paleta SuinoGestor

1.2 QUANDO qualquer texto é renderizado na UI ENTÃO o sistema usa `FontFamily.Default` (Roboto) em vez da fonte Nunito (Regular 400, SemiBold 600, Bold 700)

1.3 QUANDO o tema estático de fallback é aplicado (sem dynamic color) ENTÃO o sistema usa as cores do template (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`) em vez das cores derivadas da paleta SuinoGestor

1.4 QUANDO o tema é configurado ENTÃO o sistema não define shapes customizados (`ExtraLarge` 28dp, `Large` 16dp, `Medium` 12dp) — usa os defaults do M3

**CadastroFemeaScreen**

1.5 QUANDO o usuário precisa informar a data de nascimento ENTÃO o sistema exibe um `OutlinedTextField` com digitação manual no formato `aaaa-mm-dd` em vez de um `DatePicker` modal com calendário visual

1.6 QUANDO o usuário preenche o formulário e rotaciona a tela ENTÃO o sistema perde todos os dados digitados porque o estado usa `remember` em vez de `rememberSaveable`

1.7 QUANDO o cadastro é salvo com sucesso ENTÃO o sistema navega diretamente para a tela anterior sem exibir nenhum feedback de confirmação ao usuário

1.8 QUANDO o usuário pressiona Voltar com campos preenchidos ENTÃO o sistema descarta os dados silenciosamente sem exibir diálogo de confirmação "Descartar / Continuar editando"

1.9 QUANDO o cadastro está sendo salvo ENTÃO o sistema exibe um `CircularProgressIndicator` sem tamanho `Small` definido, ocupando espaço desproporcional dentro do botão

**ListaFemeasScreen**

1.10 QUANDO a tela de lista é exibida ENTÃO o sistema usa `FloatingActionButton` simples com apenas ícone de adição em vez de `ExtendedFloatingActionButton` com rótulo "Nova Matriz"

1.11 QUANDO não há fêmeas cadastradas ENTÃO o sistema exibe apenas o texto "Nenhuma fêmea cadastrada" centralizado, sem ilustração temática e sem botão de ação primária

1.12 QUANDO o status de uma fêmea é exibido no card ENTÃO o sistema renderiza o texto bruto `femea.status.name.replace("_", " ")` com cor `primary` uniforme, sem chip colorido com cores semânticas por status

1.13 QUANDO o usuário precisa localizar uma fêmea específica ENTÃO o sistema não oferece campo de busca — a lista não é filtrável

**AlertaBanner**

1.14 QUANDO um alerta de idade mínima de preparação é exibido ENTÃO o sistema usa `errorContainer` (vermelho) como cor de fundo, comunicando erro crítico em vez de alerta de atenção

**CategoriaSelector**

1.15 QUANDO o usuário seleciona a categoria da fêmea ENTÃO o sistema usa `SingleChoiceSegmentedButtonRow` / `SegmentedButton` padrão M3 em vez de `ConnectedButtonGroup` do M3 Expressive com shape morph na seleção

---

### Expected Behavior (Correct)

**Tema Visual**

2.1 QUANDO o app é iniciado em dispositivos sem dynamic color ENTÃO o sistema SHALL exibir a paleta SuinoGestor: primary `#5D7A3E` (verde campo), secondary `#8B6914` (âmbar terroso), tertiary `#C0522A` (terracota), com esquemas claro e escuro completos gerados a partir dessas seeds

2.2 QUANDO qualquer texto é renderizado na UI ENTÃO o sistema SHALL usar a fonte Nunito com os pesos Regular (400), SemiBold (600) e Bold (700) aplicados às escalas tipográficas do M3

2.3 QUANDO o tema estático de fallback é aplicado ENTÃO o sistema SHALL usar as cores derivadas da paleta SuinoGestor em `lightColorScheme` e `darkColorScheme`, substituindo completamente as cores do template

2.4 QUANDO o tema é configurado ENTÃO o sistema SHALL definir shapes customizados: `ExtraLarge` 28dp, `Large` 16dp, `Medium` 12dp, `Full` circular

**CadastroFemeaScreen**

2.5 QUANDO o usuário precisa informar a data de nascimento ENTÃO o sistema SHALL exibir um `DatePicker` modal do Material 3 com calendário visual, pré-preenchido com a data atual

2.6 QUANDO o usuário preenche o formulário e rotaciona a tela ENTÃO o sistema SHALL preservar todos os dados digitados usando `rememberSaveable` para cada campo do formulário

2.7 QUANDO o cadastro é salvo com sucesso ENTÃO o sistema SHALL exibir um `Snackbar` com mensagem afetiva e contextual (ex.: "Fêmea cadastrada com sucesso!") por 4 segundos antes de navegar

2.8 QUANDO o usuário pressiona Voltar com campos preenchidos ENTÃO o sistema SHALL exibir um diálogo de confirmação com as opções "Descartar" e "Continuar editando"

2.9 QUANDO o cadastro está sendo salvo ENTÃO o sistema SHALL exibir `CircularProgressIndicator` com tamanho `Small` (indicador compacto) dentro do botão

**ListaFemeasScreen**

2.10 QUANDO a tela de lista é exibida ENTÃO o sistema SHALL usar `ExtendedFloatingActionButton` com ícone de adição e rótulo "Nova Matriz"

2.11 QUANDO não há fêmeas cadastradas ENTÃO o sistema SHALL exibir ilustração temática de suinocultura, mensagem contextual e botão de ação primária "Cadastrar primeira fêmea"

2.12 QUANDO o status de uma fêmea é exibido no card ENTÃO o sistema SHALL renderizar um chip colorido usando os tokens semânticos do design system: `primaryContainer` (Gestante), `secondaryContainer` (Lactante), `tertiaryContainer` (Vazia), `errorContainer` (Descartada), cor neutra para demais status

2.13 QUANDO o usuário precisa localizar uma fêmea específica ENTÃO o sistema SHALL oferecer campo de busca no topo da lista com debounce de 300ms, filtrando por identificação

**AlertaBanner**

2.14 QUANDO um alerta de idade mínima de preparação é exibido ENTÃO o sistema SHALL usar `tertiaryContainer` (terracota) como cor de fundo e `onTertiaryContainer` para ícone e texto, comunicando alerta de atenção

**CategoriaSelector**

2.15 QUANDO o usuário seleciona a categoria da fêmea ENTÃO o sistema SHALL usar `ConnectedButtonGroup` do M3 Expressive com shape morph animado na seleção, conforme especificado no design system

---

### Unchanged Behavior (Regression Prevention)

3.1 QUANDO o app é executado em Android 12+ com dynamic color habilitado ENTÃO o sistema SHALL CONTINUE TO usar `dynamicLightColorScheme` / `dynamicDarkColorScheme` derivados da paleta do sistema operacional

3.2 QUANDO o usuário preenche todos os campos obrigatórios e confirma o cadastro ENTÃO o sistema SHALL CONTINUE TO persistir a fêmea no banco Room via `CadastrarFemeaUseCase` sem alteração na lógica de negócio

3.3 QUANDO uma marrã com menos de 160 dias é cadastrada ENTÃO o sistema SHALL CONTINUE TO gerar o alerta `IDADE_MINIMA_PREPARACAO` via `CadastrarFemeaUseCase` e exibi-lo no `AlertaBanner`

3.4 QUANDO uma identificação duplicada é submetida ENTÃO o sistema SHALL CONTINUE TO exibir o erro inline no campo de identificação com a mensagem "Identificação já cadastrada no plantel"

3.5 QUANDO campos obrigatórios estão em branco e o formulário é submetido ENTÃO o sistema SHALL CONTINUE TO exibir erros de validação inline nos campos correspondentes

3.6 QUANDO a lista de fêmeas é carregada ENTÃO o sistema SHALL CONTINUE TO exibir identificação, raça/linhagem e idade formatada (`XaYmZd`) de cada fêmea via `ListarFemeasUseCase`

3.7 QUANDO o usuário seleciona uma fêmea na lista ENTÃO o sistema SHALL CONTINUE TO navegar para a tela de detalhe com o `id` correto

3.8 QUANDO o ECC é selecionado fora do intervalo 1–5 ENTÃO o sistema SHALL CONTINUE TO exibir o erro "ECC deve estar entre 1 e 5" via validação do use case

3.9 QUANDO a camada de domínio (use cases, modelos, repositórios) é executada ENTÃO o sistema SHALL CONTINUE TO operar sem qualquer modificação — zero alterações em `domain/` e `data/`

3.10 QUANDO o `FemeaViewModel` processa eventos de cadastro e listagem ENTÃO o sistema SHALL CONTINUE TO expor `StateFlow<CadastroFemeaUiState>` e `StateFlow<FemeaUiState>` com a mesma semântica atual, exceto pelos campos adicionados para suporte à busca

---

## Bug Condition

```pascal
FUNCTION isBugCondition(X)
  INPUT: X de tipo ComponenteUI (arquivo de apresentação do módulo cadastro-femeas)
  OUTPUT: boolean

  RETURN X usa tokens de cor, tipografia, shape ou componentes
         que divergem do design system em .kiro/steering/design-system.md
END FUNCTION
```

```pascal
// Property: Fix Checking — Conformidade com Design System
FOR ALL X WHERE isBugCondition(X) DO
  resultado ← renderizar'(X)
  ASSERT resultado usa paleta SuinoGestor
     AND resultado usa fonte Nunito
     AND resultado usa shapes customizados
     AND resultado usa componentes M3E corretos (ConnectedButtonGroup, ExtendedFAB, DatePicker)
     AND resultado preserva estado com rememberSaveable
     AND resultado exibe Snackbar de confirmação ao salvar
     AND resultado exibe diálogo ao descartar com dados preenchidos
END FOR

// Property: Preservation Checking — Lógica de Negócio Intacta
FOR ALL X WHERE NOT isBugCondition(X) DO
  ASSERT F(X) = F'(X)  // comportamento de domínio e dados inalterado
END FOR
```
