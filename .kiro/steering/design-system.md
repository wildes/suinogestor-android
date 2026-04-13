# Design System — SuinoGestor

Guia prescritivo de UI/UX para todas as features do SuinoGestor. Todo código de interface **deve** seguir estas diretrizes. Não é necessário redefinir esses padrões em specs individuais — apenas referencie este documento.

---

## Perfil do Usuário

Produtor rural brasileiro com baixa familiaridade tecnológica. Contexto de uso:
- Campo aberto com variação de luminosidade
- Frequentemente com uma mão
- Interações rápidas e diretas
- Necessidade de alto contraste visual

---

## Navegação

### Estrutura Principal
- **Tela Compacta** (< 600dp): `BottomNavigationBar` com 4 destinos fixos
- **Tela Média/Expandida** (≥ 600dp): `NavigationRail` posicionada à esquerda
- Destinos: Reprodução · Engorda · Financeiro · Indicadores
- Rótulos de texto sempre visíveis abaixo dos ícones
- Altura mínima da barra: **80dp** · Área de toque por item: **48dp**

### Tokens de Navegação
- Destino ativo — fundo: `md.sys.color.secondaryContainer`
- Destino ativo — ícone/rótulo: `md.sys.color.onSecondaryContainer`
- Badge de alertas: exibir contagem numérica no ícone do módulo afetado

### Comportamento
- Preservar estado de rolagem e filtros ao trocar de módulo
- Botão físico Voltar na tela raiz → diálogo de confirmação de saída

---

## Paleta de Cores

| Papel | Valor | Uso |
|---|---|---|
| Primary | `#5D7A3E` (verde campo) | Ações primárias, botões de salvar/confirmar |
| Secondary | `#8B6914` (âmbar terroso) | Destaques secundários, navegação ativa |
| Tertiary | `#C0522A` (terracota) | Alertas de atenção, avisos não críticos |
| Error | `md.sys.color.error` | Ações destrutivas, erros de validação |

- **Android 12+**: usar `dynamicLightColorScheme` / `dynamicDarkColorScheme` quando disponível
- **Dark theme**: esquema escuro gerado pelo Material Theme Builder, aplicado automaticamente
- **Contraste mínimo**: 4,5:1 para texto sobre fundo (WCAG 2.1 AA)

### Cores Semânticas de Status (chips/badges)
| Status | Token |
|---|---|
| Gestante | `md.sys.color.primaryContainer` |
| Lactante | `md.sys.color.secondaryContainer` |
| Vazia | `md.sys.color.tertiaryContainer` |
| Descartada | `md.sys.color.errorContainer` |
| Offline banner | `md.sys.color.tertiaryContainer` |

---

## Tipografia

- **Família**: Nunito (Regular 400 · SemiBold 600 · Bold 700)
- Escolha justificada: legibilidade em telas pequenas + tom acolhedor

| Escala M3 | Uso |
|---|---|
| `headlineSmall` | Saudação/título de tela |
| `titleMedium` | Títulos de cards e seções |
| `bodyLarge` | Mensagens de estado vazio, descrições |
| `bodyMedium` | Conteúdo de lista, campos de formulário |
| `displaySmall` | KPIs em destaque (módulo Indicadores) |
| `labelMedium` | Rótulos de chips, badges |

---

## Shapes

| Token | Raio | Aplicação |
|---|---|---|
| `ExtraLarge` | 28dp | Cards de destaque, módulos no Dashboard |
| `Large` | 16dp | Cards de lista, Bottom Sheets |
| `Medium` | 12dp | Chips, botões secundários, campos |
| `Full` | circular | FABs, avatares, badges |

---

## Componentes Padrão

### Botões e Seleção
- **Ação primária**: `Button` com `md.sys.color.primary` — sempre na metade inferior da tela (Zona de Alcance)
- **Seleção binária/ternária**: `ConnectedButtonGroup` (M3 Expressive) com shape morph na seleção
- **Área de toque mínima**: 48dp × 48dp em todos os elementos interativos

### Contadores Numéricos
- Usar botões de incremento/decremento (+/−) com toque mínimo de 48dp
- Evitar teclado numérico para entradas simples (ex.: contagem de leitões)

### Campos de Data
- Sempre usar `DatePicker` do Material 3 em modo modal com calendário visual
- Pré-preencher com a data atual quando aplicável

### FAB / Extended FAB
- FAB simples: ícone de adição para ações de criação em listas
- Extended FAB: rótulo descritivo (ex.: "Nova Matriz") fixo no canto inferior direito
- Posicionamento sempre dentro da Zona de Alcance do polegar

### Listas
- Usar `LazyColumn` para listas com mais de 10 itens
- Swipe-to-action: deslize para a direita para ação rápida contextual
- Campo de busca no topo com debounce de 300ms

### Bottom Sheet
- Usar para menus de ação contextual em itens de lista (ex.: opções de leitão)
- Máximo 3–4 opções por sheet

---

## Feedback e Microinterações

### Confirmações de Sucesso
- `Snackbar` na parte inferior, duração 4 segundos
- Mensagem afetiva e contextual (ex.: "Cobertura registrada! Parto previsto para [data]")
- Animação de celebração sutil (1,5s) para eventos reprodutivos importantes

### Erros de Validação
- Mensagem inline abaixo do campo específico
- Ícone de erro + cor `md.sys.color.error`
- **Nunca** usar diálogo modal para erros de campo

### Estados de Carregamento
- `CircularProgressIndicator` Small dentro do botão durante escrita no banco
- Botão de ação primária desabilitado durante operação em andamento
- Tela inicial: conteúdo em menos de 500ms; `LoadingIndicator` M3E durante espera

### Transições de Tela
- Navigation Compose: fade + slide, duração máxima 300ms
- Shared element transition ao abrir detalhes de item de lista
- Respeitar `animatorDurationScale = 0`: substituir animações por transições instantâneas

### Formulários
- Preservar estado com `rememberSaveable` (rotação de tela, interrupções)
- Saída com dados não salvos → diálogo de confirmação: "Descartar" / "Continuar editando"

---

## Estados de Tela

Toda tela de lista deve tratar os seguintes estados:

| Estado | Tratamento |
|---|---|
| **Vazio** | Ilustração temática + mensagem contextual + botão de ação primária |
| **Sem resultado de busca** | Ilustração + mensagem "Nenhum [item] encontrado com esse número" |
| **Carregando** | `LoadingIndicator` centralizado |
| **Offline** | Banner discreto no topo: "Modo offline — dados salvos localmente" |
| **Erro** | Mensagem clara + ação corretiva simples |

---

## Linguagem e Tom

- Usar linguagem próxima ao produtor rural: "sua matriz", "sua granja", "registrar cobertura"
- **Evitar** termos técnicos de TI: "sincronizar", "cache", "banco de dados", "servidor"
- Mensagens afetivas em confirmações: "Matriz pronta para nova cobertura em breve"
- Alertas com contexto zootécnico: "Essa marrã ainda não atingiu a idade mínima de preparação (160 dias)"

---

## Ícones e Ilustrações

- **Ícones de navegação e ação**: Material Symbols Rounded
- **Ilustrações**: vetoriais temáticas de suinocultura (silhuetas de suínos, celeiros, campos)
- Usar ilustrações nos estados vazios e de boas-vindas para criar conexão com o contexto

---

## Ícones Customizados

O SuinoGestor possui um sistema de ícones próprio (`Icon_System`) para entidades e módulos específicos da suinocultura, complementando os Material Symbols Rounded.

### Convenção de Nomenclatura

Todos os VectorDrawable customizados seguem o padrão:

```
ic_sg_<nome>.xml
```

onde `<nome>` é composto apenas por letras minúsculas e underscores (ex.: `ic_sg_matriz.xml`, `ic_sg_cobertura.xml`).

Localização: `app/src/main/res/drawable/`

### Catálogo de Ícones

#### Animais

| Drawable | Descrição |
|---|---|
| `ic_sg_matriz.xml` | Suína adulta de perfil, com linha de tetas |
| `ic_sg_reprodutor.xml` | Suíno adulto de perfil, corpo robusto, orelhas eretas |
| `ic_sg_leitao.xml` | Filhote suíno, corpo arredondado, cabeça proporcionalmente maior |

#### Fases Reprodutivas

| Drawable | Descrição |
|---|---|
| `ic_sg_cobertura.xml` | Dois suínos frente a frente com coração entre eles |
| `ic_sg_gestacao.xml` | Matriz com abdômen arredondado indicando prenhez |
| `ic_sg_parto.xml` | Matriz deitada com leitão emergindo |
| `ic_sg_lactacao.xml` | Matriz deitada com leitões ao longo da barriga |
| `ic_sg_desmame.xml` | Matriz e leitão com seta de separação entre eles |

#### Módulos de Navegação

| Drawable | Variante | Uso |
|---|---|---|
| `ic_sg_reproducao_outlined.xml` | Outlined | Módulo Reprodução — inativo |
| `ic_sg_reproducao_filled.xml` | Filled | Módulo Reprodução — ativo |
| `ic_sg_engorda_outlined.xml` | Outlined | Módulo Engorda — inativo |
| `ic_sg_engorda_filled.xml` | Filled | Módulo Engorda — ativo |
| `ic_sg_financeiro_outlined.xml` | Outlined | Módulo Financeiro — inativo |
| `ic_sg_financeiro_filled.xml` | Filled | Módulo Financeiro — ativo |
| `ic_sg_indicadores_outlined.xml` | Outlined | Módulo Indicadores — inativo |
| `ic_sg_indicadores_filled.xml` | Filled | Módulo Indicadores — ativo |

### Regras de Uso de `tint`

- **Nunca** definir `fillColor` ou `strokeColor` com valores hexadecimais literais nos XMLs `ic_sg_*`
- A cor é **sempre** aplicada via `tint` pelo Compose:

```kotlin
// Ícone interativo (com contentDescription)
Icon(
    painter = painterResource(SuinoGestorIcons.Animais.Matriz),
    contentDescription = "Matriz",
    tint = MaterialTheme.colorScheme.onSurfaceVariant
)

// Módulo ativo na barra de navegação
Icon(
    painter = painterResource(SuinoGestorIcons.Modulos.Reproducao.Filled),
    contentDescription = "Reprodução",
    tint = MaterialTheme.colorScheme.onSecondaryContainer
)

// Ícone decorativo (acompanhado de rótulo de texto)
Icon(
    painter = painterResource(SuinoGestorIcons.FasesReprodutivas.Gestacao),
    contentDescription = null, // decorativo — rótulo de texto já descreve
    tint = MaterialTheme.colorScheme.primary
)
```

### Referência Tipada — `SuinoGestorIcons.kt`

Use sempre o objeto `SuinoGestorIcons` para referenciar ícones em Compose, evitando IDs de drawable espalhados pelo código:

```kotlin
// Estrutura do objeto
SuinoGestorIcons.Animais.Matriz          // @DrawableRes Int
SuinoGestorIcons.Animais.Reprodutor
SuinoGestorIcons.Animais.Leitao
SuinoGestorIcons.FasesReprodutivas.Cobertura
SuinoGestorIcons.FasesReprodutivas.Gestacao
SuinoGestorIcons.FasesReprodutivas.Parto
SuinoGestorIcons.FasesReprodutivas.Lactacao
SuinoGestorIcons.FasesReprodutivas.Desmame
SuinoGestorIcons.Modulos.Reproducao.Outlined
SuinoGestorIcons.Modulos.Reproducao.Filled
// ... (idem para Engorda, Financeiro, Indicadores)
```

Localização: `app/src/main/java/br/com/suinogestor/ui/theme/SuinoGestorIcons.kt`

### Acessibilidade

- Ícones **interativos**: fornecer `contentDescription` não nulo em português descrevendo a ação ou entidade
- Ícones **decorativos** (acompanhados de rótulo de texto): definir `contentDescription = null`
- Contraste mínimo 4,5:1 entre `tint` e fundo do componente em ambos os temas

---

## Acessibilidade

- Contraste mínimo 4,5:1 (WCAG 2.1 AA) em todos os pares texto/fundo
- Área de toque mínima 48dp × 48dp
- Suporte a dark theme automático
- Suporte a escala de animação do sistema (`animatorDurationScale`)
- Rótulos de texto sempre visíveis (não depender apenas de ícones)

---

## Responsividade

| Classe | Largura | Navegação | Layout |
|---|---|---|---|
| Compacta | < 600dp | BottomNavigationBar | Coluna única |
| Média | 600–840dp | NavigationRail | 2 colunas onde aplicável |
| Expandida | > 840dp | NavigationRail | Layout adaptado com painel lateral |

---

## Referência

Este guideline é derivado de `.kiro/specs/ux-design-suinogestor/requirements.md`.  
Para decisões de design não cobertas aqui, consultar o documento de requisitos completo.
