# Design — Identidade Visual do SuinoGestor

## Visão Geral da Arquitetura

A identidade visual do SuinoGestor é inteiramente composta por assets de **Presentation
Layer** — nenhuma camada de Domain ou Data é tocada.

```
app/
├── src/main/
│   ├── res/
│   │   ├── drawable/          ← VectorDrawables ic_sg_* (UI icons + splash)
│   │   ├── mipmap-anydpi-v26/ ← Adaptive Icon (launcher)
│   │   ├── mipmap-*/          ← Ícones rasterizados legados
│   │   └── values/            ← Temas e cor de background do launcher
│   └── java/.../ui/theme/
│       ├── SuinoGestorIcons.kt        ← Catálogo tipado (@DrawableRes)
│       └── SuinoGestorIconsPreview.kt ← @Preview composables
└── src/androidTest/
    ├── IconInflateTest.kt    ← Valida inflate de todos ic_sg_*
    └── SplashScreenTest.kt   ← Valida instalação e transição da splash
```

---

## Análise de Componentes

### Componentes Existentes — Status

| Arquivo | Status | Observação |
|---|---|---|
| `res/drawable/ic_sg_matriz.xml` | ✅ Completo | strokeWidth tail = 1.5 |
| `res/drawable/ic_sg_reprodutor.xml` | ✅ Completo | sem cores hardcoded |
| `res/drawable/ic_sg_leitao.xml` | ⚠️ Ajuste | strokeWidth tail = 1.2 → normalizar para 1.5 |
| `res/drawable/ic_sg_cobertura.xml` | ✅ Completo | |
| `res/drawable/ic_sg_gestacao.xml` | ✅ Completo | |
| `res/drawable/ic_sg_parto.xml` | ✅ Completo | |
| `res/drawable/ic_sg_lactacao.xml` | ✅ Completo | |
| `res/drawable/ic_sg_desmame.xml` | ✅ Completo | |
| `res/drawable/ic_sg_reproducao_outlined.xml` | ✅ Completo | |
| `res/drawable/ic_sg_reproducao_filled.xml` | ✅ Completo | |
| `res/drawable/ic_sg_engorda_outlined.xml` | ✅ Completo | |
| `res/drawable/ic_sg_engorda_filled.xml` | ✅ Completo | |
| `res/drawable/ic_sg_financeiro_outlined.xml` | ✅ Completo | espiga + R$ |
| `res/drawable/ic_sg_financeiro_filled.xml` | ✅ Completo | |
| `res/drawable/ic_sg_indicadores_outlined.xml` | ⚠️ Ajuste | strokeWidth = 1.2 → normalizar para 1.5 |
| `res/drawable/ic_sg_indicadores_filled.xml` | ✅ Completo | |
| `res/drawable/ic_sg_splash_static.xml` | ✅ Correto | `#FFFFFF` hardcoded — exceção documentada |
| `res/drawable/ic_sg_splash_animated.xml` | ✅ Completo | scale+alpha, 600ms+400ms |
| `res/drawable/ic_launcher_foreground.xml` | ✅ Correto | `#FFFFFF` hardcoded — exceção documentada |
| `res/mipmap-anydpi-v26/ic_launcher.xml` | ✅ Completo | inclui monochrome (API 33+) |
| `res/mipmap-anydpi-v26/ic_launcher_round.xml` | ✅ Completo | |
| `res/values/ic_launcher_background.xml` | ✅ Completo | `#5D7A3E` |
| `res/values/themes.xml` | ✅ Completo | Theme.SuinoGestor.Starting |
| `res/values-night/themes.xml` | ✅ Completo | fundo `#2D3E1E` dark |
| `ui/theme/SuinoGestorIcons.kt` | ✅ Completo | Animais / FasesReprodutivas / Modulos |
| `ui/theme/SuinoGestorIconsPreview.kt` | ✅ Completo | previews por categoria, claro+escuro |
| `androidTest/IconInflateTest.kt` | ✅ Completo | 16 drawables validados |
| `androidTest/SplashScreenTest.kt` | ✅ Completo | transição < 5s |

### Ajustes a Realizar (sem criar novos arquivos)

| Arquivo | Ajuste | Requisito |
|---|---|---|
| `res/drawable/ic_sg_leitao.xml` | Normalizar `strokeWidth` do rabo de 1.2 para 1.5 | Req 1.2, 1.6 |
| `res/drawable/ic_sg_indicadores_outlined.xml` | Normalizar `strokeWidth` de 1.2 para 1.5 | Req 1.2, 1.6 |

---

## Especificação Visual do Icon_System

### Regras de Linguagem Visual

Todos os ícones `ic_sg_*` obedecem às seguintes regras derivadas do logo
`suinogestor-logo.png` e do design system:

```
Estilo:       Silhueta vetorial preenchida (filled) ou contorno (outlined)
Referência:   Material Symbols Rounded – peso 400, grade 0, tamanho óptico 24
strokeWidth:  1.5dp (padrão canônico) em todos os strokes
Caps/Joins:   strokeLineCap="round" e strokeLineJoin="round" em todos os strokes
Gradientes:   PROIBIDO
Sombras:      PROIBIDO
Cores hard:   PROIBIDO nos ic_sg_* de UI
              EXCEÇÕES documentadas:
                · ic_sg_splash_static.xml  → fillColor="#FFFFFF" (splash sobre fundo fixo #5D7A3E)
                · ic_launcher_foreground.xml → fillColor="#FFFFFF" (contraste launcher + monochrome)
              Transparência em stroke-only paths: fillColor="#00000000" (permitido)
Viewport UI:  android:viewportWidth="24" android:viewportHeight="24"
Viewport App: android:viewportWidth="108" android:viewportHeight="108" (launcher apenas)
tint:         Toda colorização via tint no Compose — nunca via fillColor/strokeColor literal
```

### Catálogo de Ícones com Especificação de Elementos

#### Grupo 1 — Animais (Entidades)

**`ic_sg_matriz.xml`** — Suína adulta fêmea de perfil (direita)

| Elemento | Forma | Detalhes |
|---|---|---|
| Corpo | Elipse horizontal | Largura > altura; maior que reprodutor |
| Cabeça | Círculo | Lado direito do corpo, sobreposto |
| Orelha | Curva caída para frente | **Diferenciador feminino** vs orelha ereta do macho |
| Focinho | Oval pequeno | Ponta da cabeça |
| Linha de tetas | 3 círculos pequenos | Barriga inferior — **diferenciador materno principal** |
| Pernas | 4 retângulos arredondados | Abaixo do corpo |
| Rabo | Espiral curva | `stroke-only`, `strokeWidth=1.5`, `round cap` |

**`ic_sg_reprodutor.xml`** — Suíno adulto macho de perfil (direita)

| Elemento | Forma | Detalhes |
|---|---|---|
| Corpo | Elipse retangular/robusta | Mais quadrado que a matriz — **diferenciador de porte** |
| Cabeça | Forma angular/quadrada | Contrasta com cabeça arredondada da matriz |
| Orelha | **Ereta** | **Diferenciador masculino principal** |
| Focinho | Quadrado | Mais angular que o da matriz |
| Tetas | Ausente | SEM linha de tetas |
| Pernas | 4 retângulos mais grossos | Ligeiramente mais robustos que os da matriz |
| Rabo | Espiral curva | `stroke-only`, `strokeWidth=1.5`, `round cap` |

**`ic_sg_leitao.xml`** — Filhote suíno

| Elemento | Forma | Detalhes |
|---|---|---|
| Corpo | Oval pequeno e arredondado | Proporcionalmente menor que adultos |
| Cabeça | Circulo proporcionalmente maior | **Proporção bebê** — cabeça grande vs corpo |
| Orelha | Pequena e ereta | Menor que ambos os adultos |
| Focinho | Protuberante e grande | Proporcionalmente maior que nos adultos |
| Pernas | 4 traços curtos | Encurtados vs adultos |
| Rabo | Espiral pequena | `stroke-only`, `strokeWidth=1.5` (**ajuste de 1.2→1.5**), `round cap` |

---

#### Grupo 2 — Fases Reprodutivas (Processos do Ciclo)

Os 5 ícones formam uma **narrativa visual sequencial** do ciclo reprodutivo. Quando
exibidos em ordem, o usuário deve perceber a progressão sem texto.

**`ic_sg_cobertura.xml`** — Encontro / União

| Elemento | Detalhes |
|---|---|
| Suíno 1 | Silhueta de perfil, virado para direita |
| Suíno 2 | Silhueta de perfil, virado para esquerda (frente a frente) |
| Coração | Elemento pequeno centralizado entre os dois suínos; stroke-only ou filled pequeno |

**`ic_sg_gestacao.xml`** — Prenhez

| Elemento | Detalhes |
|---|---|
| Silhueta | Matriz de perfil (mesmos elementos de ic_sg_matriz) |
| Abdômen | Visivelmente protuberante/arredondado — **notavelmente maior** que ic_sg_matriz |

**`ic_sg_parto.xml`** — Nascimento

| Elemento | Detalhes |
|---|---|
| Matriz | Deitada horizontalmente (rotação 90° vs postura normal) |
| Leitão | Emergindo abaixo do corpo da matriz |

**`ic_sg_lactacao.xml`** — Amamentação

| Elemento | Detalhes |
|---|---|
| Matriz | Deitada de lado |
| Leitões | 2–3 filhotes em fila ao longo da barriga inferior |

**`ic_sg_desmame.xml`** — Separação

| Elemento | Detalhes |
|---|---|
| Matriz | Silhueta de perfil, lado esquerdo |
| Leitão | Silhueta de perfil, lado direito |
| Seta curva | Entre eles, apontando direções opostas — **separação visual clara** |

---

#### Grupo 3 — Módulos de Navegação (outlined + filled)

Cada módulo possui dois arquivos. A variante outlined usa apenas strokes (sem fill no
corpo principal); a filled usa paths preenchidos (herdam tint) para os elementos
sólidos, mantendo strokes apenas para detalhes lineares.

**`ic_sg_reproducao_outlined/filled`** — Módulo Reprodução

| Elemento | Outlined | Filled |
|---|---|---|
| Corpo da matriz | Contorno stroke-only, 1.5 | Preenchido (herda tint) |
| Seta de renovação | Arco circular ao redor, stroke 1.5 | Mesmo arco stroke |

Conceito: ciclo contínuo da reprodução, referenciando ic_sg_matriz.

**`ic_sg_engorda_outlined/filled`** — Módulo Engorda

| Elemento | Outlined | Filled |
|---|---|---|
| Corpo de suíno | Contorno stroke-only, 1.5 | Preenchido (herda tint) |
| Seta ascendente | Diagonal saindo do corpo, stroke 1.5 | Mesmo stroke |

Conceito: crescimento / ganho de peso — derivado do logo `suinogestor-logo.png`.

**`ic_sg_financeiro_outlined/filled`** — Módulo Financeiro

| Elemento | Outlined | Filled |
|---|---|---|
| Espiga de milho | Contorno stroke-only, 1.5 | Preenchida (herda tint) |
| Símbolo R$ | Integrado sobre a espiga, stroke 1.5 | Mesmo stroke |

Conceito: gestão financeira **rural** — referência agrícola, não bancária genérica.

**`ic_sg_indicadores_outlined/filled`** — Módulo Indicadores

| Elemento | Outlined | Filled |
|---|---|---|
| 3 barras de gráfico | Stroke-only, 1.5 (**ajuste de 1.2→1.5**) | Preenchidas (herda tint) |
| Silhueta de suíno | No topo da barra mais alta, stroke 1.5 | Preenchida |

Conceito: análise de desempenho — referência direta ao gráfico de barras do logo
`suinogestor-logo.png` (barra chart + linha de tendência).

---

## Infraestrutura Compose

### SuinoGestorIcons.kt

**Localização:** `app/src/main/java/br/com/suinogestor/ui/theme/SuinoGestorIcons.kt`

```kotlin
object SuinoGestorIcons {
    object Animais {
        @DrawableRes val Matriz: Int = R.drawable.ic_sg_matriz
        @DrawableRes val Reprodutor: Int = R.drawable.ic_sg_reprodutor
        @DrawableRes val Leitao: Int = R.drawable.ic_sg_leitao
    }
    object FasesReprodutivas {
        @DrawableRes val Cobertura: Int = R.drawable.ic_sg_cobertura
        @DrawableRes val Gestacao: Int = R.drawable.ic_sg_gestacao
        @DrawableRes val Parto: Int = R.drawable.ic_sg_parto
        @DrawableRes val Lactacao: Int = R.drawable.ic_sg_lactacao
        @DrawableRes val Desmame: Int = R.drawable.ic_sg_desmame
    }
    object Modulos {
        object Reproducao { @DrawableRes val Outlined: Int = ...; @DrawableRes val Filled: Int = ... }
        object Engorda    { @DrawableRes val Outlined: Int = ...; @DrawableRes val Filled: Int = ... }
        object Financeiro { @DrawableRes val Outlined: Int = ...; @DrawableRes val Filled: Int = ... }
        object Indicadores{ @DrawableRes val Outlined: Int = ...; @DrawableRes val Filled: Int = ... }
    }
}
```

### Padrão de Uso em Compose

```kotlin
// Ícone interativo (ex.: item de lista de matrizes)
Icon(
    painter = painterResource(SuinoGestorIcons.Animais.Matriz),
    contentDescription = "Matriz",             // não-nulo: elemento interativo
    tint = MaterialTheme.colorScheme.onSurfaceVariant
)

// Módulo ativo na BottomNavigationBar
Icon(
    painter = painterResource(SuinoGestorIcons.Modulos.Reproducao.Filled),
    contentDescription = "Reprodução",
    tint = MaterialTheme.colorScheme.onSecondaryContainer
)

// Módulo inativo na BottomNavigationBar
Icon(
    painter = painterResource(SuinoGestorIcons.Modulos.Reproducao.Outlined),
    contentDescription = "Reprodução",
    tint = MaterialTheme.colorScheme.onSurfaceVariant
)

// Fase em chip (decorativo — rótulo de texto já descreve)
Icon(
    painter = painterResource(SuinoGestorIcons.FasesReprodutivas.Gestacao),
    contentDescription = null,                 // nulo: decorativo
    tint = MaterialTheme.colorScheme.primary
)
```

### SuinoGestorIconsPreview.kt

**Localização:** `app/src/main/java/br/com/suinogestor/ui/theme/SuinoGestorIconsPreview.kt`

```kotlin
@Preview(name = "Animais — Claro", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Animais — Escuro", uiMode = UI_MODE_NIGHT_YES)
@Composable fun AnimaisIconsPreview() {
    // Row com Matriz, Reprodutor, Leitao em 24dp e 48dp
}

@Preview(name = "Fases Reprodutivas — Claro", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Fases Reprodutivas — Escuro", uiMode = UI_MODE_NIGHT_YES)
@Composable fun FasesReprodutivasIconsPreview() {
    // Row com Cobertura, Gestacao, Parto, Lactacao, Desmame em 24dp e 48dp
}

@Preview(name = "Módulos — Claro", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Módulos — Escuro", uiMode = UI_MODE_NIGHT_YES)
@Composable fun ModulosIconsPreview() {
    // Grid com pares Outlined/Filled de cada módulo em 24dp
}
```

---

## Launcher Icon — Estrutura de Arquivos

```
res/
├── drawable/
│   ├── ic_launcher_foreground.xml     108×108, #FFFFFF hardcoded (exceção documentada)
│   ├── ic_sg_splash_static.xml        24×24,  #FFFFFF hardcoded (exceção documentada)
│   └── ic_sg_splash_animated.xml      AVD: scale 0.6→1.0 (600ms) + alpha 0→1 (400ms)
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml                Adaptive Icon + monochrome (API 33+)
│   └── ic_launcher_round.xml          Variante round
├── mipmap-mdpi/        ic_launcher.png + ic_launcher_round.png   48×48 px
├── mipmap-hdpi/        ic_launcher.png + ic_launcher_round.png   72×72 px
├── mipmap-xhdpi/       ic_launcher.png + ic_launcher_round.png   96×96 px
├── mipmap-xxhdpi/      ic_launcher.png + ic_launcher_round.png   144×144 px
├── mipmap-xxxhdpi/     ic_launcher.png + ic_launcher_round.png   192×192 px
└── values/
    ├── ic_launcher_background.xml     <color name="ic_launcher_background">#5D7A3E</color>
    ├── themes.xml                     Theme.SuinoGestor + Theme.SuinoGestor.Starting
    └── (values-night/)themes.xml      fundo #2D3E1E no tema escuro
```

---

## Splash Screen — Fluxo de Inicialização

```
MainActivity.onCreate()
  └─ installSplashScreen()
       ├─ setKeepOnScreenCondition { !viewModel.isReady }
       └─ setOnExitAnimationListener { splashScreenView ->
              val scale = splashScreenView.iconAnimationDurationMillis
              if (animatorDurationScale == 0f) {
                  splashScreenView.remove()          // sem animação
              } else {
                  // fade alpha 0→1, 300ms, AccelerateDecelerateInterpolator
                  ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                      .apply { duration = 300; doOnEnd { splashScreenView.remove() } }
                      .start()
              }
          }
```

---

## Estratégia de Testes

### Testes Existentes (instrumentados)

| Arquivo | Cobertura |
|---|---|
| `androidTest/IconInflateTest.kt` | 16 drawables `ic_sg_*` inflatam sem exceção via `VectorDrawableCompat` |
| `androidTest/SplashScreenTest.kt` | `installSplashScreen()` chamado; transição para MainActivity < 5s |

### Testes Opcionais a Adicionar

Estes testes não bloqueiam o MVP mas aumentam a confiança em manutenção futura:

| Teste | Propriedade | Requisito |
|---|---|---|
| Parse XML: verificar `viewportWidth=24` e `viewportHeight=24` em todos `ic_sg_*` | Viewport canônico | Req 1.4 |
| Parse XML: ausência de padrão `#[0-9A-Fa-f]{6}` em `fillColor`/`strokeColor` nos ic_sg_* de UI | Sem cores hardcoded | Req 1.3 |
| Existência dos pares `ic_sg_<modulo>_outlined.xml` e `ic_sg_<modulo>_filled.xml` para cada módulo | Variantes nav | Req 6.2 |
| Contraste WCAG ≥ 4,5:1 nos pares de cor: `onSecondaryContainer`/`secondaryContainer`, `onSurfaceVariant`/`surface`, `#FFFFFF`/`#5D7A3E` | Acessibilidade | Req 8.1 |

---

## Decisões de Design Documentadas

| Decisão | Justificativa |
|---|---|
| `#FFFFFF` hardcoded em `ic_launcher_foreground` e `ic_sg_splash_static` | Launcher sempre sobre `#5D7A3E`; splash idem. Branco fixo garante contraste 4,5:1 e funcionalidade do monochrome API 33+. Não é bug — é requisito de contraste. |
| `@DrawableRes Int` em vez de `ImageVector` | VectorDrawables Android com múltiplos `<path>` e `<group>` são mais eficientes via `painterResource()`. `ImageVector` manual seria código verboso sem ganho. |
| strokeWidth 1.5 como padrão canônico | Equivale ao Material Symbols Rounded peso 400 em 24dp. Dois ícones existentes com 1.2 (leitão, indicadores) serão ajustados para uniformidade. |
| `contentDescription = null` em ícones decorativos | Ícones acompanhados de rótulo de texto não devem duplicar informação para TalkBack — padrão M3 e WCAG. |
