# Design Document — Cadastro e Identificação Individual de Reprodutores

## Overview

Este módulo implementa o cadastro e gerenciamento individual de varrões (reprodutores machos) no SuinoGestor, permitindo controle preciso da utilização reprodutiva, monitoramento de condição corporal e geração automática de alertas para otimizar a eficiência reprodutiva do plantel.

### Objetivos

- Registrar dados individuais de cada reprodutor (identificação, genética, idade, peso, condição corporal)
- Calcular automaticamente a ração diária recomendada baseada no peso vivo
- Gerar alertas automáticos para prevenir subutilização (queda de motilidade espermática) e superutilização (exaustão reprodutiva)
- Validar regras de negócio específicas para reprodutores em treinamento vs. adultos
- Integrar com o sistema de alertas existente seguindo os padrões estabelecidos no módulo de fêmeas

### Escopo

**Incluído:**
- Cadastro de reprodutores com validação de campos obrigatórios
- Registro de peso e ECC (Escore de Condição Corporal)
- Cálculo automático de ração diária (1% do peso vivo)
- Motor de alertas para: inatividade prolongada (15+ dias), superutilização adultos (6+ montas/semana), uso excessivo em treinamento (>1 monta/semana)
- Validação de duplicidade de identificação

**Excluído desta fase:**
- Registro de coberturas/montas (será implementado em módulo separado)
- Histórico detalhado de uso reprodutivo
- Análise de qualidade seminal
- Integração com sistema de lotes

### Dependências

- **Room Database**: persistência offline
- **Sistema de Alertas**: reutilização da infraestrutura existente (`AlertaEntity`, `AlertaRepository`)
- **Clean Architecture**: seguir padrões estabelecidos no módulo de fêmeas
- **Material 3 Expressive**: componentes de UI conforme design system

---

## Architecture

### Camadas (Clean Architecture)

```
Presentation Layer (UI + ViewModel)
    ↓
Domain Layer (Use Cases + Models + Repository Interfaces)
    ↓
Data Layer (Room Entities + DAOs + Repository Implementations)
```

### Fluxo de Dados

1. **Cadastro de Reprodutor**:
   - `CadastroReprodutorScreen` → `ReprodutorViewModel` → `CadastrarReprodutorUseCase` → `ReprodutorRepository` → `ReprodutorDao` → Room Database

2. **Geração de Alertas**:
   - Background Worker (WorkManager) → `GerarAlertasReprodutoresUseCase` → `ReprodutorRepository` + `AlertaRepository` → Room Database

3. **Listagem**:
   - `ListaReprodutoresScreen` → `ReprodutorViewModel` → `ListarReprodutoresUseCase` → `ReprodutorRepository` (Flow) → UI reativa

### Padrões Arquiteturais

- **Repository Pattern**: abstração da camada de dados
- **Use Case Pattern**: uma classe por operação de negócio
- **MVVM**: ViewModels expõem `StateFlow`, Composables observam estado
- **Reactive Streams**: `Flow` para observação de mudanças no banco
- **Command Pattern**: comandos de entrada para use cases (ex: `CadastrarReprodutorComando`)

---

## Components and Interfaces

### Domain Layer

#### Models

**Reprodutor** (domain/model/Reprodutor.kt)
```kotlin
data class Reprodutor(
    val id: Long = 0,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val tipoUso: TipoUsoReprodutor,
    val pesoAtualKg: Double?,
    val eccAtual: Int?, // 1-5
    val ativo: Boolean = true,
    val dataCadastro: LocalDate = LocalDate.now()
)
```

**TipoUsoReprodutor** (domain/model/TipoUsoReprodutor.kt)
```kotlin
enum class TipoUsoReprodutor {
    MONTA_NATURAL,
    COLETA_IA,
    AMBOS
}
```

**CategoriaIdadeReprodutor** (domain/model/CategoriaIdadeReprodutor.kt)
```kotlin
enum class CategoriaIdadeReprodutor {
    TREINAMENTO, // 210-300 dias
    ADULTO,      // 300+ dias
    IMATURO      // < 210 dias
}
```

#### Repository Interface

**ReprodutorRepository** (domain/repository/ReprodutorRepository.kt)
```kotlin
interface ReprodutorRepository {
    suspend fun cadastrar(reprodutor: Reprodutor): ResultadoOperacao<Long>
    suspend fun atualizar(reprodutor: Reprodutor): ResultadoOperacao<Unit>
    suspend fun buscarPorId(id: Long): Reprodutor?
    suspend fun buscarPorIdentificacao(identificacao: String): Reprodutor?
    fun observarTodos(): Flow<List<Reprodutor>>
    suspend fun contarCoberturasNaSemana(reprodutorId: Long, dataReferencia: LocalDate): Int
    suspend fun obterDataUltimaCobertura(reprodutorId: Long): LocalDate?
}
```

#### Use Cases

**CadastrarReprodutorUseCase** (domain/usecase/CadastrarReprodutorUseCase.kt)
- Valida campos obrigatórios
- Verifica duplicidade de identificação
- Valida ECC (1-5)
- Persiste no repositório

**CalcularRacaoDiariaUseCase** (domain/usecase/CalcularRacaoDiariaUseCase.kt)
- Entrada: peso em kg
- Saída: quantidade de ração (1% do peso vivo)
- Lógica: `racaoDiariaKg = pesoKg * 0.01`

**CalcularCategoriaIdadeReprodutorUseCase** (domain/usecase/CalcularCategoriaIdadeReprodutorUseCase.kt)
- Entrada: data de nascimento
- Saída: `CategoriaIdadeReprodutor`
- Lógica:
  - < 210 dias: IMATURO
  - 210-300 dias: TREINAMENTO
  - 300+ dias: ADULTO

**GerarAlertasReprodutoresUseCase** (domain/usecase/GerarAlertasReprodutoresUseCase.kt)
- Executa periodicamente (WorkManager, diariamente)
- Para cada reprodutor ativo:
  1. Verifica inatividade (15+ dias sem cobertura)
  2. Verifica superutilização adultos (6+ montas/semana)
  3. Verifica uso excessivo em treinamento (>1 monta/semana)
- Gera alertas via `AlertaRepository`

**ListarReprodutoresUseCase** (domain/usecase/ListarReprodutoresUseCase.kt)
- Retorna `Flow<List<Reprodutor>>`
- Permite filtros (ativo/inativo, tipo de uso)

**ObterReprodutorUseCase** (domain/usecase/ObterReprodutorUseCase.kt)
- Busca por ID
- Retorna detalhes completos + dados calculados (idade, ração diária)

### Data Layer

#### Entity

**ReprodutorEntity** (data/db/ReprodutorEntity.kt)
```kotlin
@Entity(
    tableName = "reprodutores",
    indices = [Index(value = ["identificacao"], unique = true)]
)
data class ReprodutorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String, // ISO-8601
    val racaLinhagem: String,
    val tipoUso: String, // enum serializado
    val pesoAtualKg: Double?,
    val eccAtual: Int?,
    val ativo: Int = 1, // SQLite boolean
    val dataCadastro: String // ISO-8601
)
```

#### DAO

**ReprodutorDao** (data/db/ReprodutorDao.kt)
```kotlin
@Dao
interface ReprodutorDao {
    @Insert
    suspend fun inserir(reprodutor: ReprodutorEntity): Long
    
    @Update
    suspend fun atualizar(reprodutor: ReprodutorEntity)
    
    @Query("SELECT * FROM reprodutores WHERE id = :id")
    suspend fun buscarPorId(id: Long): ReprodutorEntity?
    
    @Query("SELECT * FROM reprodutores WHERE identificacao = :identificacao")
    suspend fun buscarPorIdentificacao(identificacao: String): ReprodutorEntity?
    
    @Query("SELECT * FROM reprodutores WHERE ativo = 1 ORDER BY identificacao ASC")
    fun observarTodosAtivos(): Flow<List<ReprodutorEntity>>
}
```

**Nota**: Contagem de coberturas será implementada quando o módulo de coberturas for criado. Por ora, `ReprodutorRepository` retornará valores mock (0) para essas consultas.

#### Repository Implementation

**ReprodutorRepositoryImpl** (data/repository/ReprodutorRepositoryImpl.kt)
- Implementa `ReprodutorRepository`
- Mapeia entre `ReprodutorEntity` ↔ `Reprodutor`
- Trata exceções de banco (ex: constraint violation para duplicidade)

### Presentation Layer

#### ViewModel

**ReprodutorViewModel** (presentation/reprodutor/viewmodel/ReprodutorViewModel.kt)
```kotlin
class ReprodutorViewModel(
    private val cadastrarReprodutorUseCase: CadastrarReprodutorUseCase,
    private val listarReprodutoresUseCase: ListarReprodutoresUseCase,
    private val calcularRacaoDiariaUseCase: CalcularRacaoDiariaUseCase
) : ViewModel() {
    
    val reprodutores: StateFlow<List<Reprodutor>>
    val uiState: StateFlow<ReprodutorUiState>
    
    fun cadastrar(comando: CadastrarReprodutorComando)
    fun calcularRacaoDiaria(pesoKg: Double): Double
}
```

#### Screens

**ListaReprodutoresScreen** (presentation/reprodutor/screen/ListaReprodutoresScreen.kt)
- LazyColumn com cards de reprodutores
- FAB para adicionar novo
- Campo de busca no topo
- Estados: vazio, carregando, lista

**CadastroReprodutorScreen** (presentation/reprodutor/screen/CadastroReprodutorScreen.kt)
- Formulário com campos obrigatórios
- Seletores para tipo de uso, ECC
- DatePicker para data de nascimento
- Cálculo automático de ração ao inserir peso
- Validação inline

**DetalhesReprodutorScreen** (presentation/reprodutor/screen/DetalhesReprodutorScreen.kt)
- Exibe dados completos
- Mostra idade calculada
- Exibe categoria (Treinamento/Adulto)
- Botão para editar

### UI Components

Reutilizar componentes existentes onde possível:
- `EccSelector`: já existe para fêmeas, reutilizar
- `IdadeChip`: reutilizar
- `AlertaBanner`: reutilizar

Novos componentes:
- `TipoUsoSelector`: ConnectedButtonGroup para seleção de tipo de uso (MN/IA/Ambos)
- `CategoriaIdadeChip`: chip visual para Treinamento/Adulto
- `RacaoDiariaDisplay`: componente read-only mostrando cálculo de ração

---

## Data Models

### Database Schema

**Tabela: reprodutores**

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID interno |
| identificacao | TEXT | NOT NULL, UNIQUE | Número de identificação do reprodutor |
| dataNascimento | TEXT | NOT NULL | Data de nascimento (ISO-8601) |
| racaLinhagem | TEXT | NOT NULL | Raça ou linhagem genética |
| tipoUso | TEXT | NOT NULL | MONTA_NATURAL, COLETA_IA, AMBOS |
| pesoAtualKg | REAL | NULL | Peso atual em kg |
| eccAtual | INTEGER | NULL, CHECK(eccAtual BETWEEN 1 AND 5) | Escore de condição corporal |
| ativo | INTEGER | NOT NULL DEFAULT 1 | 1 = ativo, 0 = inativo |
| dataCadastro | TEXT | NOT NULL | Data de cadastro (ISO-8601) |

**Índices:**
- UNIQUE INDEX em `identificacao`

### Migration Strategy

**Versão 2 do banco** (atual é versão 1 com tabela `femeas`)

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS reprodutores (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                identificacao TEXT NOT NULL,
                dataNascimento TEXT NOT NULL,
                racaLinhagem TEXT NOT NULL,
                tipoUso TEXT NOT NULL,
                pesoAtualKg REAL,
                eccAtual INTEGER CHECK(eccAtual BETWEEN 1 AND 5),
                ativo INTEGER NOT NULL DEFAULT 1,
                dataCadastro TEXT NOT NULL
            )
        """)
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_reprodutores_identificacao 
            ON reprodutores(identificacao)
        """)
    }
}
```

### Domain Model Mapping

**ReprodutorEntity → Reprodutor**
```kotlin
fun ReprodutorEntity.toDomain(): Reprodutor = Reprodutor(
    id = id,
    identificacao = identificacao,
    dataNascimento = LocalDate.parse(dataNascimento),
    racaLinhagem = racaLinhagem,
    tipoUso = TipoUsoReprodutor.valueOf(tipoUso),
    pesoAtualKg = pesoAtualKg,
    eccAtual = eccAtual,
    ativo = ativo == 1,
    dataCadastro = LocalDate.parse(dataCadastro)
)
```

**Reprodutor → ReprodutorEntity**
```kotlin
fun Reprodutor.toEntity(): ReprodutorEntity = ReprodutorEntity(
    id = id,
    identificacao = identificacao,
    dataNascimento = dataNascimento.toString(),
    racaLinhagem = racaLinhagem,
    tipoUso = tipoUso.name,
    pesoAtualKg = pesoAtualKg,
    eccAtual = eccAtual,
    ativo = if (ativo) 1 else 0,
    dataCadastro = dataCadastro.toString()
)
```

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property 1: Required Fields Validation

*For any* registration command, the system SHALL accept the registration if and only if all required fields (identificacao, dataNascimento, racaLinhagem, tipoUso) are present and non-empty.

**Validates: Requirements 2.1**

### Property 2: Data Persistence Round-Trip

*For any* valid reprodutor with peso and ECC values, saving to the repository and then retrieving SHALL return a reprodutor with identical peso and ECC values.

**Validates: Requirements 2.2**

### Property 3: Feed Calculation Accuracy

*For any* valid peso value in kg, the calculated daily feed amount SHALL equal exactly 1% of the peso value (racaoDiariaKg = pesoKg × 0.01).

**Validates: Requirements 2.3**

### Property 4: Inactivity Alert Generation

*For any* active reprodutor, an inactivity alert SHALL be generated if and only if the number of days since the last coverage exceeds 15 days.

**Validates: Requirements 2.4**

### Property 5: Adult Overuse Alert Generation

*For any* reprodutor with age greater than 300 days, an overuse alert SHALL be generated if and only if the number of coverages in the current week exceeds 6.

**Validates: Requirements 2.5**

### Property 6: Training Overuse Alert Generation

*For any* reprodutor with age between 210 and 300 days (inclusive), an overuse alert SHALL be generated if and only if the number of coverages in the current week exceeds 1.

**Validates: Requirements 2.6**

### Property 7: Duplicate Identification Rejection

*For any* identificacao value that already exists in the active reprodutor registry, attempting to register a new reprodutor with that identificacao SHALL be rejected with an appropriate error.

**Validates: Requirements 2.7**

---

## Error Handling

### Validation Errors

**Domain-Level Validation** (Use Cases)

| Erro | Condição | Mensagem |
|---|---|---|
| `ErroNegocio.CampoObrigatorio` | Campo obrigatório ausente | "Campo obrigatório: [nome do campo]" |
| `ErroNegocio.IdentificacaoDuplicada` | Identificação já existe | "Identificação já cadastrada no plantel" |
| `ErroNegocio.EccInvalido` | ECC fora do range 1-5 | "ECC deve estar entre 1 e 5" |
| `ErroNegocio.PesoInvalido` | Peso ≤ 0 | "Peso deve ser maior que zero" |
| `ErroNegocio.DataNascimentoFutura` | Data de nascimento no futuro | "Data de nascimento não pode ser futura" |

**Repository-Level Errors**

| Erro | Condição | Tratamento |
|---|---|---|
| `SQLiteConstraintException` | Violação de constraint (ex: UNIQUE) | Mapear para `ErroNegocio.IdentificacaoDuplicada` |
| `SQLiteException` | Erro genérico de banco | Mapear para `ErroNegocio.ErroInterno` |

### Error Propagation

```kotlin
sealed class ResultadoOperacao<out T> {
    data class Sucesso<T>(val valor: T) : ResultadoOperacao<T>()
    data class Erro(val erro: ErroNegocio) : ResultadoOperacao<Nothing>()
}
```

Use cases retornam `ResultadoOperacao<T>`, ViewModels mapeiam para UI state, Composables exibem mensagens inline ou Snackbar.

### UI Error Display

- **Erros de validação**: mensagem inline abaixo do campo específico, cor `error`
- **Erros de operação**: Snackbar na parte inferior, duração 4s
- **Erro de duplicidade**: mensagem inline no campo identificação + destaque visual

---

## Testing Strategy

### Unit Tests

**Domain Layer (Use Cases)**

Cada use case terá testes unitários cobrindo:
- Casos de sucesso com dados válidos
- Casos de erro com dados inválidos
- Validações de regras de negócio

Exemplos:
- `CadastrarReprodutorUseCaseTest`: campos obrigatórios, ECC válido, duplicidade
- `CalcularRacaoDiariaUseCaseTest`: cálculo correto para diversos pesos
- `CalcularCategoriaIdadeReprodutorUseCaseTest`: categorização correta por idade
- `GerarAlertasReprodutoresUseCaseTest`: geração de alertas conforme regras

**Repository Layer**

Testes de integração com Room in-memory database:
- `ReprodutorRepositoryImplTest`: CRUD operations, Flow observation, constraint violations

### Property-Based Tests

Biblioteca: **Kotest Property Testing** (já utilizada em projetos Kotlin)

Configuração: mínimo 100 iterações por teste

**Generators Customizados**

```kotlin
// Arbitrary para Reprodutor válido
fun Arb.Companion.reprodutor(): Arb<Reprodutor> = arbitrary {
    Reprodutor(
        identificacao = Arb.string(1..20).bind(),
        dataNascimento = Arb.localDate(
            minDate = LocalDate.now().minusYears(5),
            maxDate = LocalDate.now().minusDays(180)
        ).bind(),
        racaLinhagem = Arb.stringPattern("[A-Z]{2,10}").bind(),
        tipoUso = Arb.enum<TipoUsoReprodutor>().bind(),
        pesoAtualKg = Arb.double(80.0, 400.0).orNull(0.3).bind(),
        eccAtual = Arb.int(1..5).orNull(0.3).bind()
    )
}

// Arbitrary para comando de cadastro com campos faltando
fun Arb.Companion.cadastroComandoIncompleto(): Arb<CadastrarReprodutorComando> = arbitrary {
    val allFields = mapOf(
        "identificacao" to Arb.string(1..20).orNull(0.3).bind(),
        "dataNascimento" to Arb.localDate().orNull(0.3).bind(),
        "racaLinhagem" to Arb.string(1..20).orNull(0.3).bind(),
        "tipoUso" to Arb.enum<TipoUsoReprodutor>().orNull(0.3).bind()
    )
    CadastrarReprodutorComando(/* campos com nulls */)
}
```

**Property Tests**

```kotlin
class ReprodutorPropertiesTest : StringSpec({
    
    "Property 1: Required fields validation" {
        checkAll(100, Arb.cadastroComandoIncompleto()) { comando ->
            val resultado = cadastrarReprodutorUseCase(comando)
            val temTodosCampos = comando.identificacao != null && 
                                 comando.dataNascimento != null &&
                                 comando.racaLinhagem != null &&
                                 comando.tipoUso != null
            
            if (temTodosCampos) {
                resultado shouldBe instanceOf<ResultadoOperacao.Sucesso>()
            } else {
                resultado shouldBe instanceOf<ResultadoOperacao.Erro>()
            }
        }
    }
    
    "Property 2: Data persistence round-trip" {
        checkAll(100, Arb.reprodutor()) { reprodutor ->
            val id = repository.cadastrar(reprodutor).getOrThrow()
            val retrieved = repository.buscarPorId(id)
            
            retrieved?.pesoAtualKg shouldBe reprodutor.pesoAtualKg
            retrieved?.eccAtual shouldBe reprodutor.eccAtual
        }
    }
    
    "Property 3: Feed calculation accuracy" {
        checkAll(100, Arb.double(80.0, 400.0)) { peso ->
            val racao = calcularRacaoDiariaUseCase(peso)
            racao shouldBe (peso * 0.01).plusOrMinus(0.001)
        }
    }
    
    "Property 4: Inactivity alert generation" {
        checkAll(100, Arb.reprodutor(), Arb.int(0..30)) { reprodutor, diasSemUso ->
            val dataUltimaCobertura = LocalDate.now().minusDays(diasSemUso.toLong())
            // Setup: mock repository to return dataUltimaCobertura
            
            val alertas = gerarAlertasReprodutoresUseCase()
            val temAlertaInatividade = alertas.any { 
                it.tipo == TipoAlerta.REPRODUTOR_INATIVO && it.reprodutorId == reprodutor.id 
            }
            
            temAlertaInatividade shouldBe (diasSemUso > 15)
        }
    }
    
    "Property 5: Adult overuse alert generation" {
        checkAll(100, Arb.int(250..400), Arb.int(0..10)) { idadeDias, coberturasSemana ->
            val reprodutor = Arb.reprodutor().bind().copy(
                dataNascimento = LocalDate.now().minusDays(idadeDias.toLong())
            )
            // Setup: mock repository to return coberturasSemana
            
            val alertas = gerarAlertasReprodutoresUseCase()
            val temAlertaSuperuso = alertas.any { 
                it.tipo == TipoAlerta.REPRODUTOR_SUPERUTILIZADO 
            }
            
            val ehAdulto = idadeDias > 300
            val superutilizado = coberturasSemana > 6
            temAlertaSuperuso shouldBe (ehAdulto && superutilizado)
        }
    }
    
    "Property 6: Training overuse alert generation" {
        checkAll(100, Arb.int(180..320), Arb.int(0..5)) { idadeDias, coberturasSemana ->
            val reprodutor = Arb.reprodutor().bind().copy(
                dataNascimento = LocalDate.now().minusDays(idadeDias.toLong())
            )
            // Setup: mock repository to return coberturasSemana
            
            val alertas = gerarAlertasReprodutoresUseCase()
            val temAlertaTreinamento = alertas.any { 
                it.tipo == TipoAlerta.REPRODUTOR_TREINAMENTO_EXCESSO 
            }
            
            val ehTreinamento = idadeDias in 210..300
            val excedeUso = coberturasSemana > 1
            temAlertaTreinamento shouldBe (ehTreinamento && excedeUso)
        }
    }
    
    "Property 7: Duplicate identification rejection" {
        checkAll(100, Arb.reprodutor()) { reprodutor ->
            repository.cadastrar(reprodutor).getOrThrow()
            
            val duplicado = reprodutor.copy(id = 0, racaLinhagem = "OUTRA")
            val resultado = repository.cadastrar(duplicado)
            
            resultado shouldBe instanceOf<ResultadoOperacao.Erro>()
            (resultado as ResultadoOperacao.Erro).erro shouldBe 
                ErroNegocio.IdentificacaoDuplicada
        }
    }
})
```

**Tags para rastreabilidade:**
```kotlin
// Feature: cadastro-reprodutores, Property 1: Required fields validation
// Feature: cadastro-reprodutores, Property 2: Data persistence round-trip
// ... etc
```

### UI Tests (Compose)

Testes de jornadas críticas:
- Cadastro completo de reprodutor com sucesso
- Validação de campos obrigatórios
- Exibição de erro de duplicidade
- Cálculo automático de ração ao inserir peso
- Navegação entre telas

### Integration Tests

- `ReprodutorEndToEndTest`: fluxo completo de cadastro → listagem → detalhes
- `AlertaIntegrationTest`: geração de alertas com dados reais no banco in-memory

---

## UI/UX Design

### Navegação

**Fluxo Principal:**
```
BottomNavigationBar (Reprodução) 
  → ListaReprodutoresScreen
    → CadastroReprodutorScreen (via FAB)
    → DetalhesReprodutorScreen (via item click)
```

### ListaReprodutoresScreen

**Layout:**
- `TopAppBar` com título "Reprodutores" + campo de busca
- `LazyColumn` com cards de reprodutores
- `ExtendedFAB` fixo no canto inferior direito: "Novo Reprodutor"

**Card de Reprodutor:**
```
┌─────────────────────────────────────┐
│ 🐗 R-042                            │
│ Duroc | Adulto (450 dias)           │
│ Peso: 280kg | ECC: 3                │
│ Ração diária: 2.8kg                 │
│ ⚠️ 18 dias sem uso                  │ (se houver alerta)
└─────────────────────────────────────┘
```

**Estados:**
- **Vazio**: ilustração de varrão + "Nenhum reprodutor cadastrado" + botão "Cadastrar Primeiro Reprodutor"
- **Sem resultado**: "Nenhum reprodutor encontrado com esse número"
- **Carregando**: `CircularProgressIndicator` centralizado

### CadastroReprodutorScreen

**Layout (formulário vertical):**

1. **Identificação** (TextField obrigatório)
   - Label: "Número de identificação"
   - Placeholder: "Ex: R-001"
   - Validação inline: duplicidade

2. **Data de Nascimento** (DatePicker obrigatório)
   - Label: "Data de nascimento"
   - Ícone de calendário
   - Exibe idade calculada abaixo: "Idade: 320 dias (Adulto)"

3. **Raça/Linhagem** (TextField obrigatório)
   - Label: "Raça ou linhagem genética"
   - Placeholder: "Ex: Duroc, Landrace"

4. **Tipo de Uso** (ConnectedButtonGroup obrigatório)
   - Opções: "Monta Natural" | "Coleta IA" | "Ambos"
   - Shape morph na seleção (M3 Expressive)

5. **Peso Atual** (TextField numérico opcional)
   - Label: "Peso atual (kg)"
   - Sufixo: "kg"
   - Ao preencher: exibe abaixo "Ração diária recomendada: X.XX kg"

6. **ECC Atual** (EccSelector opcional)
   - Reutilizar componente existente
   - Escala visual 1-5

**Botão de Ação:**
- `Button` primário na parte inferior: "Cadastrar Reprodutor"
- Desabilitado se campos obrigatórios vazios
- Loading indicator durante salvamento

**Feedback:**
- Sucesso: Snackbar "Reprodutor cadastrado com sucesso!" + navegação para lista
- Erro: mensagem inline no campo específico

### DetalhesReprodutorScreen

**Layout:**

**Seção: Identificação**
- Número de identificação (destaque)
- Raça/linhagem
- Tipo de uso (chip)

**Seção: Dados Físicos**
- Idade: "450 dias (Adulto)" com chip de categoria
- Peso: "280 kg"
- ECC: escala visual 1-5 com valor destacado
- Ração diária: "2.8 kg/dia"

**Seção: Alertas Ativos** (se houver)
- Lista de alertas com ícone de prioridade
- Mensagem do alerta
- Data de geração

**Ações:**
- `IconButton` no TopAppBar: editar
- `Button` secundário: "Ver Histórico de Uso" (placeholder para módulo futuro)

### Componentes Customizados

**TipoUsoSelector.kt**
```kotlin
@Composable
fun TipoUsoSelector(
    tipoSelecionado: TipoUsoReprodutor?,
    onTipoSelecionado: (TipoUsoReprodutor) -> Unit,
    modifier: Modifier = Modifier
) {
    ConnectedButtonGroup(
        options = listOf("Monta Natural", "Coleta IA", "Ambos"),
        selectedIndex = tipoSelecionado?.ordinal,
        onSelectionChange = { index -> 
            onTipoSelecionado(TipoUsoReprodutor.values()[index])
        },
        modifier = modifier
    )
}
```

**CategoriaIdadeChip.kt**
```kotlin
@Composable
fun CategoriaIdadeChip(
    categoria: CategoriaIdadeReprodutor,
    modifier: Modifier = Modifier
) {
    val (label, containerColor) = when (categoria) {
        CategoriaIdadeReprodutor.IMATURO -> 
            "Imaturo" to MaterialTheme.colorScheme.tertiaryContainer
        CategoriaIdadeReprodutor.TREINAMENTO -> 
            "Treinamento" to MaterialTheme.colorScheme.secondaryContainer
        CategoriaIdadeReprodutor.ADULTO -> 
            "Adulto" to MaterialTheme.colorScheme.primaryContainer
    }
    
    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor
        ),
        modifier = modifier
    )
}
```

**RacaoDiariaDisplay.kt**
```kotlin
@Composable
fun RacaoDiariaDisplay(
    racaoKg: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ração diária recomendada",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "%.2f kg".format(racaoKg),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
```

### Acessibilidade

- Todos os campos com labels descritivos
- Área de toque mínima 48dp
- Contraste 4.5:1 em todos os textos
- `contentDescription` em ícones interativos
- Suporte a TalkBack

### Linguagem

- Tom acolhedor: "Cadastre seu primeiro reprodutor"
- Mensagens contextuais: "Reprodutor R-042 cadastrado! Lembre-se de registrar as coberturas."
- Alertas com contexto zootécnico: "Reprodutor há 18 dias sem uso — risco de queda na motilidade espermática"

---

## Performance Considerations

### Database Queries

- Índice UNIQUE em `identificacao` para busca rápida
- `Flow` para observação reativa evita polling
- Queries limitadas a campos necessários (não usar `SELECT *` em produção)

### UI Performance

- `LazyColumn` para listas longas (virtualização)
- `rememberSaveable` para preservar estado em rotações
- Debounce de 300ms no campo de busca
- Cálculo de ração em background (coroutine)

### Background Work

- `WorkManager` para geração periódica de alertas (1x por dia, 6h da manhã)
- Constraints: device idle + battery not low
- Retry policy: exponential backoff

---

## Security Considerations

### Data Validation

- Sanitização de inputs (trim, validação de formato)
- Validação de ranges (ECC 1-5, peso > 0)
- Proteção contra SQL injection (Room parametriza queries automaticamente)

### Data Integrity

- Constraints de banco (UNIQUE, NOT NULL, CHECK)
- Transações atômicas para operações compostas
- Validação em múltiplas camadas (UI, Domain, Data)

---

## Future Enhancements

### Fase 2 (Módulo de Coberturas)

- Implementar tabela `coberturas` com FK para `reprodutores`
- Implementar queries reais para contagem de coberturas
- Histórico detalhado de uso reprodutivo
- Gráficos de utilização ao longo do tempo

### Fase 3 (Análise Avançada)

- Registro de qualidade seminal (motilidade, concentração)
- Correlação entre ECC e desempenho reprodutivo
- Recomendações de descarte baseadas em idade + desempenho
- Integração com sistema de lotes

### Fase 4 (Sincronização)

- Backup em nuvem (quando conectividade disponível)
- Sincronização entre dispositivos
- Exportação de relatórios (PDF, Excel)

---

## Dependencies

### Gradle Dependencies

```kotlin
// Room
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)

// Coroutines
implementation(libs.kotlinx.coroutines.android)

// Hilt (DI)
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)

// Compose
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui.tooling.preview)

// WorkManager
implementation(libs.androidx.work.runtime.ktx)

// Testing
testImplementation(libs.kotest.runner.junit5)
testImplementation(libs.kotest.assertions.core)
testImplementation(libs.kotest.property)
testImplementation(libs.androidx.room.testing)
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
```

### Version Catalog Updates

Adicionar ao `gradle/libs.versions.toml`:
```toml
[versions]
kotest = "5.8.0"

[libraries]
kotest-runner-junit5 = { module = "io.kotest:kotest-runner-junit5", version.ref = "kotest" }
kotest-assertions-core = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
kotest-property = { module = "io.kotest:kotest-property", version.ref = "kotest" }
```

---

## Implementation Phases

### Phase 1: Data Layer (Prioridade Alta)
- [ ] Criar `ReprodutorEntity`, `ReprodutorDao`
- [ ] Implementar migration 1→2
- [ ] Criar `ReprodutorRepositoryImpl`
- [ ] Testes de integração do repositório

### Phase 2: Domain Layer (Prioridade Alta)
- [ ] Criar models (`Reprodutor`, `TipoUsoReprodutor`, `CategoriaIdadeReprodutor`)
- [ ] Implementar use cases (Cadastrar, Listar, Obter, CalcularRacao, CalcularCategoria)
- [ ] Testes unitários de use cases
- [ ] Property-based tests

### Phase 3: Presentation Layer (Prioridade Média)
- [ ] Criar `ReprodutorViewModel`
- [ ] Implementar `ListaReprodutoresScreen`
- [ ] Implementar `CadastroReprodutorScreen`
- [ ] Implementar `DetalhesReprodutorScreen`
- [ ] Testes de UI

### Phase 4: Componentes UI (Prioridade Média)
- [ ] `TipoUsoSelector`
- [ ] `CategoriaIdadeChip`
- [ ] `RacaoDiariaDisplay`
- [ ] Previews e testes de componentes

### Phase 5: Sistema de Alertas (Prioridade Baixa - depende de módulo de coberturas)
- [ ] Implementar `GerarAlertasReprodutoresUseCase`
- [ ] Configurar WorkManager
- [ ] Adicionar novos tipos de alerta em `TipoAlerta`
- [ ] Testes de geração de alertas

### Phase 6: Integração e Polimento (Prioridade Baixa)
- [ ] Integração com navegação principal
- [ ] Testes end-to-end
- [ ] Ajustes de acessibilidade
- [ ] Documentação de código

---

## Open Questions

1. **Foto do reprodutor**: Incluir campo `fotoUri` como nas fêmeas? (Decisão: SIM, adicionar ao modelo)
2. **Histórico de peso**: Manter apenas peso atual ou criar tabela de histórico? (Decisão: apenas atual nesta fase, histórico em Fase 2)
3. **Integração com módulo de coberturas**: Quando implementar? (Decisão: após conclusão deste módulo, em spec separado)
4. **Alertas em tempo real**: Gerar alertas apenas em background ou também em tempo real ao registrar cobertura? (Decisão: apenas background nesta fase)

---

## References

- Requirements: `.kiro/specs/cadastro-reprodutores/requirements.md`
- Tech Stack: `tech.md`
- Design System: `design-system.md`
- Existing Femea Module: `app/src/main/java/br/com/suinogestor/domain/model/Femea.kt`
- Material 3 Expressive Guidelines: `.agents/skills/material-3-expressive/`
