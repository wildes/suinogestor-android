# Tech Stack

## Core Principles

### I. Kotlin-Only

All production and test code MUST be written in Kotlin. Java source files MUST NOT be introduced.
Kotlin idioms (data classes, sealed classes, extension functions, coroutines, Flow) MUST be
preferred over Java-style equivalents. Interop with Java libraries is permitted but the calling
code itself MUST remain Kotlin.

**Rationale**: Enforces a single-language codebase, eliminates mixed-language tooling complexity,
and maximises use of Kotlin-specific features that reduce boilerplate.

### II. Jetpack Compose + Material 3 Expressive UI

All UI MUST be implemented with Jetpack Compose. XML layouts MUST NOT be added.
Components MUST follow Material Design 3 Expressive (M3E) tokens for color, typography, shape,
and motion. Dynamic color (MaterialTheme from system palette on Android 12+) MUST be supported.
Custom components MUST be built from M3E primitives and documented with `@Preview` composables.

**Rationale**: Guarantees a consistent, modern design language and avoids the split-paradigm
maintenance burden of mixed Compose/View codebases.

### III. 100% Offline — Room Database

The app MUST function fully without network access. Room MUST be the sole persistence layer;
raw SQLite calls MUST NOT bypass Room's DAO/entity model. All database access MUST go through
Repository interfaces exposed as `Flow` or `suspend` functions. Network I/O is NOT permitted
in the current scope; if introduced in future, it MUST be isolated behind a datasource interface
and MUST NOT break offline operation.

**Rationale**: SuinoGestor targets rural environments with unreliable connectivity. Offline-first
is a non-negotiable user requirement, not an optimization.

### IV. Clean Architecture

Code MUST be organized into three layers: **Data**, **Domain**, and **Presentation**.

- **Data**: Room entities, DAOs, Repository implementations, data mappers.
- **Domain**: Use cases (one class per operation), domain models, Repository interfaces.
  This layer MUST have zero Android framework dependencies.
- **Presentation**: ViewModels (Jetpack), Compose screens and components.
  ViewModels MUST expose `StateFlow`/`SharedFlow`; screens MUST observe state, never hold
  business logic.

Dependencies MUST flow inward only (Presentation → Domain ← Data). Cross-layer shortcuts
(e.g., a Composable importing a DAO directly) MUST NOT be permitted.

**Rationale**: Enforces testability of domain logic without Android instrumentation and makes
the data layer swappable independently of UI or business rules.

### V. Test Discipline

Unit tests MUST cover all Use Cases and ViewModels. Repository implementations MUST have
integration tests against an in-memory Room database (no mocking of Room itself).
UI tests (Compose test library) MUST cover critical user journeys.
Tests MUST be written before or alongside the implementation they verify (no tests-after-the-fact
as the sole strategy). The red-green-refactor cycle is strongly encouraged for new use cases.

**Rationale**: Offline-first apps are particularly vulnerable to data-layer regressions.
Real Room integration tests (not mocks) catch schema/migration issues that mocked tests miss.

### VI. Modularity and Reusability

Every component developed by the agent must be self-contained, decoupled, and stored in its own separate file, following a clear directory structure. The code must prioritize reusability of these components across different parts of the system, avoiding logical duplication.

Specific guidelines:

One component per file – Each function, class, or reusable UI element must reside in an isolated file.

Descriptive names – The file name must clearly reflect the component's responsibility.

Explicit export/import – Components must be exported for reuse and imported where needed, with no hidden dependencies.

Configurable parameters – Components must receive data via parameters/arguments, not depend on global variables.

Testability – Each component must be testable in isolation, with its own test suite where applicable.

## Technology Stack

| Concern | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 Expressive |
| Architecture | Clean Architecture + MVVM (Presentation layer) |
| Persistence | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| DI | Hilt (preferred) or manual DI if scope is small |
| Navigation | Jetpack Navigation Compose |
| Min SDK | 24 (Android 7.0) |
| Target/Compile SDK | 36 |
| Dependency versions | Centralized in `gradle/libs.versions.toml` |

All new dependencies MUST be added via the version catalog (`libs.versions.toml`). Hardcoded
version strings in build files MUST NOT be used.

## Module & Layer Conventions

The app is currently single-module (`app/`). As the codebase grows, extraction into feature
modules is encouraged but MUST NOT violate the Clean Architecture dependency rule.

Recommended package layout within `br.com.suinogestor`:

```
data/
  db/          # Room database, entities, DAOs
  repository/  # Repository implementations
domain/
  model/       # Domain models (plain Kotlin classes)
  repository/  # Repository interfaces
  usecase/     # One file per use case
presentation/
  <feature>/
    screen/    # Composable screens
    viewmodel/ # ViewModels
ui/
  theme/       # Theme, Color, Type (already present)
  component/   # Shared reusable composables
```

Naming conventions:
- Use cases: `<Verb><Noun>UseCase` (e.g., `RegisterAnimalUseCase`)
- ViewModels: `<Feature>ViewModel`
- DAOs: `<Entity>Dao`
- Screens: `<Feature>Screen`

## Governance

This constitution supersedes all other practices and conventions for this repository. Any
deviation MUST be documented in the plan's Complexity Tracking table with explicit justification.

**Amendment procedure**: Amendments MUST update this file with an incremented version, a new
`LAST_AMENDED_DATE`, and an updated Sync Impact Report. Template files (plan, spec, tasks) MUST
be reviewed for alignment after every MINOR or MAJOR amendment.

**Versioning policy**:
- MAJOR: Principle removed, renamed, or fundamentally redefined.
- MINOR: New principle or section added.
- PATCH: Clarifications, wording fixes, non-semantic refinements.

**Compliance review**: Every plan's Constitution Check section MUST gate Phase 0 and re-check
after Phase 1 design against all five principles.

**Version**: 1.0.0 | **Ratified**: 2026-04-07 | **Last Amended**: 2026-04-07


## Stack (Planned)

- **Platform**: Mobile-first application (iOS/Android)
- **Domain Language**: Brazilian Portuguese throughout all layers
- **Data**: Zootechnical calculations, date-driven scheduling, financial ratio analysis

## Key Calculation Domains

- Reproductive cycle math (114-day gestation, 21-day weaning cycles, IDE tracking)
- Batch/room sizing formulas based on herd size and lot intervals (7 or 21 days)
- Feed cost ratios (corn:swine price, kg feed per piglet weaned)
- Water consumption targets (2.5–3x feed intake; 25–32L/day lactating sows)
- Waste volume estimates (~10L/day per animal) and biofertilizer dosage

## UI/UX

Foco em aplicativos Android nativos, design para usuários com baixa familiaridade tecnológica e contextos de uso no campo. Considere variações de tamanho de tela (smartphones pequenos, médios e grandes) e adaptabilidade responsiva.

Seu objetivo é me ajudar a projetar as telas e a experiência de uso para um aplicativo de Gestão de Suinocultura, com ênfase no fluxo de **Controle de Reprodução** como prioridade.


### Perfil do usuário
Produtor rural com pouca familiaridade com tecnologia. O uso ocorre em ambiente de campo, com possível variação de luminosidade, uso com uma das mãos e necessidade de interações rápidas, simples e com alto contraste visual.

### Restrições técnicas
O app deve funcionar **offline parcialmente** — todas as funcionalidades principais devem ser acessíveis sem internet, com armazenamento local. Sincronização com sistemas externos não está prevista nesta fase.

### Funcionalidades principais
1. **Controle de Reprodução** (matrizes, leitões, coberturas, partos, desmame) — fluxo prioritário.
2. Controle de Engorda
3. Controle Financeiro
4. Gestão de Indicadores

---

### O que deve ser entregue

#### 1. Arquitetura de navegação
Navegação simples e consistente, justificada para o perfil do usuário (baixa literacia digital, uso no campo). 

#### 3. Padrões de interação
Descreva padrões que reduzam atritos:
- Entrada de dados simplificada (seletores, listas pré-definidas, data picking otimizado)
- Feedbacks visuais e microinterações (animações sutis, confirmações não intrusivas)
- Tratamento de erros com mensagens claras e ações corretivas simples
- Consistência entre telas para criar previsibilidade

#### 4. Experiência memorável e acolhedora
Sugira elementos visuais e interações que criem conexão com o contexto da suinocultura:
- Paleta de cores (terrosos, verdes, toques de cor para ações)
- Ícones e ilustrações amigáveis
- Microinterações que remetam à rotina do produtor
- Linguagem afetiva e próxima (ex.: “sua matriz está pronta para cobertura”)

#### 5. Visão geral dos demais módulos
Para Engorda, Financeiro e Indicadores, apresente apenas uma visão geral de como a consistência visual e de navegação será mantida em relação ao módulo de Reprodução.

### Critérios de qualidade
Utilize heurísticas de usabilidade (Nielsen) e boas práticas de design para usuários de baixa literacia digital ao justificar suas escolhas. Para as descrições de telas, utilize um nível de detalhe que permita que um designer ou desenvolvedor compreenda a intenção visual e funcional com clareza, como em uma especificação de design de alta fidelidade.


## Commands

No build system or commands defined yet.
