# Documento de Requisitos — Identidade Visual do SuinoGestor

## Visão Geral

Define a identidade visual completa do app SuinoGestor: linguagem visual coesa derivada
do logotipo existente (`suinogestor-logo.png`) e dos mockups de referência
(`suiogestor-mockup.png`), sistema de ícones conceituais para cada entidade e fase do
negócio de suinocultura, ícone do app (launcher adaptativo), splash screen animada e
infraestrutura Compose tipada.

**Público-alvo:** Produtor rural brasileiro com baixa familiaridade tecnológica, uso em
campo aberto com variação de luminosidade, frequentemente com uma mão.

**Referências visuais obrigatórias:**
- Logo: `suinogestor-logo.png` — silhueta outline de suíno com gráfico de barras + linha
  de tendência ascendente no interior. Estilo minimalista, sem gradientes no traço,
  verde escuro.
- Mockup: `suiogestor-mockup.png` — splash com logo centralizado + tipografia Nunito,
  dashboard com ilustrações de suínos em estilo vetorial suave (cartoon discreto),
  navegação inferior com 4 destinos.

---

## Glossário

- **Logo SuinoGestor**: símbolo formado pela silhueta outline de suíno com gráfico de
  barras ascendentes + linha de tendência no interior, conforme `suinogestor-logo.png`.
- **Linguagem Visual**: conjunto de regras de estilo (traço, proporção, forma, cor) que
  garante coesão entre todos os ícones do sistema.
- **Icon_System**: conjunto completo de ícones customizados `ic_sg_*`.
- **Adaptive Icon**: formato Android API 26+ com camadas foreground + background.
- **Safe Zone**: área central garantida como visível em qualquer forma de launcher
  (66dp × 66dp dentro do viewport de 108dp).
- **AVD**: Animated VectorDrawable — formato XML Android para animações vetoriais.
- **SuinoGestorIcons**: objeto Kotlin tipado que expõe todos os ícones do Icon_System.
- **tint**: mecanismo Compose/Android para colorizar ícones via token M3 sem hardcode.
- **Viewport 24×24**: dimensão canônica dos ícones do sistema, compatível com o padrão
  Material Symbols.

---

## Requisito 1 — Linguagem Visual Coesa (Design Language)

**User Story:** Como designer e desenvolvedor do SuinoGestor, quero uma linguagem visual
única e documentada para todos os ícones, para que o app tenha aparência profissional e
reconhecível consistente com o logotipo.

### Critérios de Aceitação

1. WHEN qualquer ícone do Icon_System é exibido, THE Icon_System SHALL usar estilo de
   silhueta vetorial arredondada (sem serifa, sem gradientes, sem sombras), derivado do
   estilo do logo `suinogestor-logo.png`.
2. THE Icon_System SHALL manter peso de traço equivalente ao Material Symbols Rounded
   peso 400 (strokeWidth 1.5dp para outlined, preenchimento sólido para filled),
   garantindo coesão com os ícones do sistema M3 usados no mesmo app.
3. WHEN um ícone do Icon_System é renderizado em qualquer cor via tint, THE ícone SHALL
   ser reconhecível e sem artefatos visuais — proibido hardcode de `fillColor` ou
   `strokeColor` com valores hexadecimais literais nos XMLs `ic_sg_*`.
4. THE Icon_System SHALL usar viewport uniforme de 24dp × 24dp para todos os ícones de
   uso em UI (animais, fases reprodutivas, módulos de navegação).
5. THE linguagem visual SHALL ser compatível com renderização via `tint` nos tokens M3:
   `onSurfaceVariant`, `onSecondaryContainer`, `primary`, `onPrimary`,
   `onPrimaryContainer`.
6. WHEN comparados lado a lado, os ícones de diferentes categorias (animais, fases
   reprodutivas, módulos) SHALL ter proporções e pesos visuais consistentes, sem que
   nenhum ícone pareça visivelmente maior ou mais pesado que os demais.

---

## Requisito 2 — Logo e Ícone do App (Launcher Icon)

**User Story:** Como produtor rural, quero reconhecer o SuinoGestor imediatamente na
tela do meu celular pela forma e cor do ícone do app, sem precisar ler o nome.

### Critérios de Aceitação

1. THE Launcher Icon SHALL ser um Adaptive Icon (API 26+) com foreground e background
   separados.
2. THE foreground layer SHALL derivar o símbolo principal do logotipo
   `suinogestor-logo.png`: silhueta de suíno com elemento gráfico interno (gráfico de
   barras ou símbolo de gestão) posicionado inteiramente dentro da Safe Zone
   (coordenadas 21–87 no viewport de 108dp).
3. THE background layer SHALL usar cor sólida Primary `#5D7A3E`.
4. THE foreground layer SHALL usar apenas branco (`#FFFFFF`) para garantir contraste
   mínimo de 4,5:1 sobre o fundo verde Primary.
5. WHEN o launcher aplica qualquer forma de corte (círculo, squircle, teardrop), THE
   símbolo principal SHALL permanecer visível sem cortes.
6. WHERE o dispositivo suportar API 33+, THE Launcher Icon SHALL incluir variante
   monocromática (`ic_launcher_monochrome`) derivada do foreground, para temas de ícones
   do sistema Android 13.
7. THE Launcher Icon SHALL incluir ícones legados rasterizados (`ic_launcher.png`,
   `ic_launcher_round.png`) para API < 26, nas densidades mdpi, hdpi, xhdpi, xxhdpi,
   xxxhdpi.
8. WHEN exibido ao lado de outros apps na tela inicial, THE Launcher Icon SHALL ser
   imediatamente distinguível pela silhueta de suíno + cor verde, sem depender do
   texto do nome do app.

---

## Requisito 3 — Splash Screen Animada

**User Story:** Como produtor rural, quero ver uma tela de abertura agradável ao iniciar
o app, que comunique identidade visual do SuinoGestor antes da tela principal carregar.

### Critérios de Aceitação

1. THE Splash Screen SHALL ser implementada com a API AndroidX SplashScreen
   (compatível com API 12+), sem activity customizada bloqueando a thread principal.
2. THE Splash Screen SHALL exibir o símbolo do SuinoGestor (derivado do logo) centrado
   sobre fundo Primary `#5D7A3E` (tema claro) ou variante escura equivalente
   (tema escuro).
3. WHEN o app é iniciado, THE Splash Screen SHALL executar animação de entrada do símbolo
   com duração máxima de 1.000ms total, usando Animated VectorDrawable.
4. THE animação SHALL combinar scale (0.6 → 1.0, 600ms, FastOutSlowIn) e alpha
   (0 → 1, 400ms), transmitindo sensação de "surgimento" do símbolo.
5. WHEN o sistema tiver `animatorDurationScale = 0`, THE Splash Screen SHALL exibir o
   símbolo estático e transitar imediatamente para a tela principal sem animação.
6. WHEN a animação conclui, THE app SHALL transitar para a tela principal com fade de
   no máximo 300ms.
7. THE Splash Screen SHALL exibir o nome "SuinoGestor" em tipografia Nunito Bold abaixo
   do símbolo, em escala `headlineSmall` do M3 ou superior.
8. WHEN o app demora mais de 1.000ms para carregar, THE Splash Screen SHALL permanecer
   visível sem exibir tela em branco intermediária.

---

## Requisito 4 — Ícones de Animais (Conceitos de Entidade)

**User Story:** Como produtor rural, quero identificar visualmente os tipos de animal
(matriz, reprodutor, leitão) pelos ícones em listas e cards, para navegar sem precisar
ler os rótulos.

### Critérios de Aceitação

1. THE Icon_System SHALL fornecer ícones conceituais distintos para Matriz (fêmea
   adulta), Reprodutor (macho adulto) e Leitão (filhote).
2. WHEN exibidos juntos, os três ícones de animais SHALL ser imediatamente distinguíveis
   entre si por diferenças visuais de porte, forma corporal e elemento diferenciador,
   sem depender de cor.
3. THE ícone Matriz SHALL comunicar feminilidade/maternidade através de elemento visual
   integrado ao design (ex.: linha de tetas, postura maternal ou símbolo feminino
   discreto).
4. THE ícone Reprodutor SHALL comunicar masculinidade/robustez através de elemento
   visual integrado (ex.: porte corporal maior, musculatura, postura ereta ou orelhas
   eretas).
5. THE ícone Leitão SHALL comunicar filhote/jovem através de proporções menores,
   cabeça proporcionalmente maior e traços mais arredondados que os adultos.
6. WHEN exibido em tamanho 24dp, EACH ícone de animal SHALL ser legível como silhueta
   de suíno reconhecível.
7. WHEN exibido em tamanho 48dp ou maior (ex.: estados vazios de tela), EACH ícone
   SHALL manter proporções sem distorção ou borrão.
8. THE ícones de animais SHALL aceitar tint sem cores residuais hardcoded.

---

## Requisito 5 — Ícones de Fases Reprodutivas (Conceitos de Processo)

**User Story:** Como produtor rural, quero identificar cada fase do ciclo reprodutivo
(cobertura, gestação, parto, lactação, desmame) por ícones, para registrar eventos sem
ambiguidade.

### Critérios de Aceitação

1. THE Icon_System SHALL fornecer ícones conceituais distintos para as cinco fases:
   Cobertura, Gestação, Parto, Lactação e Desmame.
2. WHEN exibidos em sequência no app, os cinco ícones SHALL comunicar visualmente uma
   progressão temporal de ciclo reprodutivo — o usuário SHALL perceber a narrativa do
   ciclo sem texto.
3. THE ícone Cobertura SHALL representar o encontro/união: dois suínos próximos ou
   símbolo de coração integrado a silhueta suína.
4. THE ícone Gestação SHALL representar a prenhez: fêmea com abdômen visivelmente
   arredondado/protuberante.
5. THE ícone Parto SHALL representar o nascimento: fêmea com leitão emergindo ou
   símbolo de nascimento contextualizado ao universo suíno.
6. THE ícone Lactação SHALL representar a amamentação: fêmea deitada com leitões ao
   longo da barriga.
7. THE ícone Desmame SHALL representar a separação: matriz e leitão com seta ou espaço
   visual de separação entre eles.
8. WHEN usados em chips de status (16dp), THE ícones de fases SHALL permanecer legíveis
   como símbolo da fase, sem detalhe excessivo que se perca em tamanho pequeno.
9. THE ícones de fases SHALL compartilhar o mesmo vocabulário visual dos ícones de
   animais (Requisito 4), garantindo coesão do Icon_System.
10. THE ícones de fases SHALL aceitar tint sem cores hardcoded.

---

## Requisito 6 — Ícones de Módulos de Navegação

**User Story:** Como produtor rural, quero identificar cada módulo do app (Reprodução,
Engorda, Financeiro, Indicadores) pelos ícones na barra de navegação, para trocar de
seção rapidamente.

### Critérios de Aceitação

1. THE Icon_System SHALL fornecer ícones conceituais distintos para os quatro módulos:
   Reprodução, Engorda, Financeiro e Indicadores.
2. EACH ícone de módulo SHALL ter variante **outlined** (inativo) e **filled** (ativo),
   seguindo a convenção Material Symbols para estados de navegação.
3. THE ícone Reprodução SHALL representar o ciclo reprodutivo suíno — derivado
   conceitualmente do ícone de matriz (Requisito 4), acrescido de símbolo de ciclo
   ou renovação.
4. THE ícone Engorda SHALL representar crescimento/ganho de peso — silhueta de suíno
   acrescida de seta ascendente ou símbolo de progresso.
5. THE ícone Financeiro SHALL representar gestão financeira rural — símbolo de valor
   (moeda, cifrão R$) integrado a elemento agrícola (espiga, folha), evitando ícones
   genéricos de banco sem contexto rural.
6. THE ícone Indicadores SHALL representar análise de desempenho — gráfico de barras
   ou linha de tendência, referenciando visualmente o gráfico interno do logo
   `suinogestor-logo.png`.
7. WHEN o destino de navegação está ativo, THE app SHALL usar a variante filled com
   tint `onSecondaryContainer`.
8. WHEN o destino de navegação está inativo, THE app SHALL usar a variante outlined com
   tint `onSurfaceVariant`.
9. WHEN exibidos na barra de navegação em 24dp, os quatro ícones SHALL ser
   distinguíveis entre si sem rótulo de texto.
10. THE ícones de módulos SHALL ser legíveis tanto no tema claro quanto no escuro.

---

## Requisito 7 — Infraestrutura Compose (SuinoGestorIcons)

**User Story:** Como desenvolvedor do SuinoGestor, quero referenciar todos os ícones
customizados via objeto Kotlin tipado, para evitar erros de ID de drawable e facilitar
descoberta dos ícones disponíveis.

### Critérios de Aceitação

1. THE Asset_Pipeline SHALL expor todos os ícones do Icon_System via
   `SuinoGestorIcons.kt` em `app/src/main/java/br/com/suinogestor/ui/theme/`.
2. THE SuinoGestorIcons SHALL agrupar ícones em objetos aninhados:
   `Animais` (Matriz, Reprodutor, Leitao), `FasesReprodutivas` (Cobertura, Gestacao,
   Parto, Lactacao, Desmame), `Modulos` (Reproducao, Engorda, Financeiro, Indicadores
   — cada um com Outlined e Filled).
3. EACH entrada no SuinoGestorIcons SHALL ser `@DrawableRes val <Nome>: Int`, permitindo
   uso via `painterResource()` no Compose.
4. THE Asset_Pipeline SHALL organizar todos os VectorDrawable customizados com prefixo
   `ic_sg_` em `app/src/main/res/drawable/`.
5. THE Asset_Pipeline SHALL fornecer `SuinoGestorIconsPreview.kt` com `@Preview`
   composables para cada ícone, exibindo tamanhos 24dp e 48dp nos temas claro e escuro,
   organizados por categoria.
6. WHEN um desenvolvedor abre o arquivo `SuinoGestorIconsPreview.kt` no Android Studio,
   THE previews SHALL renderizar todos os ícones do Icon_System sem erros de inflate.

---

## Requisito 8 — Acessibilidade e Suporte a Temas

**User Story:** Como produtor rural que usa o celular em campo com sol forte ou à noite,
quero que todos os ícones e o ícone do app sejam sempre visíveis, legíveis e acessíveis.

### Critérios de Aceitação

1. THE Icon_System SHALL garantir contraste mínimo de 4,5:1 entre o tint aplicado e o
   fundo do componente, em ambos os temas (claro e escuro), conforme WCAG 2.1 AA.
2. WHEN o usuário altera o tema do sistema, THE app SHALL atualizar todos os ícones
   automaticamente via tokens M3 sem reinicialização.
3. THE Icon_System SHALL funcionar em modo de alto contraste do Android, sem depender
   de gradientes ou diferenças sutis de cor para transmitir significado.
4. WHEN um ícone customizado é elemento interativo (botão, item de lista), THE app
   SHALL fornecer `contentDescription` não nulo em português descrevendo a entidade
   ou ação.
5. WHEN um ícone customizado é puramente decorativo (acompanhado de rótulo de texto),
   THE app SHALL definir `contentDescription = null` para evitar redundância com
   leitores de tela.
6. WHILE o usuário navega com TalkBack ativado, THE app SHALL anunciar cada ícone
   interativo em português de forma contextual (ex.: "Matriz 023", "Registrar cobertura").

---

## Não está no escopo

- Design de telas e fluxos de navegação (coberto por specs de features individuais)
- Ilustrações decorativas de estados vazios (escopo de spec futura)
- Animações de transição entre telas
- Assets para web ou outras plataformas além de Android
- Geração automatizada de ícones rasterizados (feito manualmente via Asset Studio)
