# Design: Cadastro e Identificação Individual de Fêmeas

## Visão Geral

O módulo **Cadastro e Identificação Individual de Fêmeas** é o ponto de entrada de cada animal no SuinoGestor. Ele digitaliza o cabeçalho da Ficha da Matriz — identificação, origem, raça, categoria e dados biométricos iniciais — e dispara o primeiro alerta de manejo quando uma marrã é registrada abaixo da idade mínima de preparação reprodutiva (160 dias).

Este design cobre exclusivamente o **Requisito 1** do documento de requisitos. Os demais eventos reprodutivos (cobertura, parto, desmame etc.) são tratados em specs separadas que dependem deste cadastro como pré-requisito.

A arquitetura segue Clean Architecture + MVVM com três camadas bem definidas: **Data** (Room + repositórios), **Domain** (modelos, interfaces, casos de uso) e **Presentation** (ViewModel + Compose). O fluxo de dependências é estritamente unidirecional: Presentation → Domain ← Data.

```
┌──────────────────────────────────────────────────────────┐
│                     PRESENTATION                         │
│  CadastroFemeaScreen  ListaFemeasScreen                  │
│  FemeaViewModel                                          │
└───────────────────────────┬──────────────────────────────┘
                            │ observa StateFlow / chama UseCases
┌───────────────────────────▼──────────────────────────────┐
│                       DOMAIN                             │
│  Femea  CategoriaFemea  OrigemAnimal  StatusFemea        │
│  Alerta  TipoAlerta  PrioridadeAlerta                    │
│  CadastrarFemeaUseCase  ObterFemeaUseCase                │
│  ListarFemeasUseCase  CalcularIdadeFormatadaUseCase      │
│  FemeaRepository (interface)  AlertaRepository (iface)   │
└───────────────────────────┬──────────────────────────────┘
                            │ implementado por
┌───────────────────────────▼──────────────────────────────┐
│                        DATA                              │
│  FemeaEntity  AlertaEntity                               │
│  FemeaDao  AlertaDao                                     │
│  FemeaRepositoryImpl  AlertaRepositoryImpl               │
│  SuinoGestorDatabase (Room)                              │
└──────────────────────────────────────────────────────────┘
```

---

## Arquitetura

### Camadas e Responsabilidades

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Data | `br.com.suinogestor.data.db` | Entidades Room, DAOs, contribuição ao database |
| Data | `br.com.suinogestor.data.repository` | Implementações de repositório e mapeadores entity↔domain |
| Domain | `br.com.suinogestor.domain.model` | Modelos de domínio (Kotlin puro, zero dependências Android) |
| Domain | `br.com.suinogestor.domain.repository` | Interfaces de repositório |
| Domain | `br.com.suinogestor.domain.usecase` | Um arquivo por caso de uso |
| Presentation | `br.com.suinogestor.presentation.femea.screen` | Composable screens |
| Presentation | `br.com.suinogestor.presentation.femea.viewmodel` | ViewModel |
| UI | `br.com.suinogestor.ui.component` | Composables reutilizáveis compartilhados |

### Fluxo de Dados

```
Room (Flow<FemeaEntity>) → FemeaRepositoryImpl (mapeia para Femea)
  → ListarFemeasUseCase / ObterFemeaUseCase
    → FemeaViewModel (StateFlow<FemeaUiState>)
      → ListaFemeasScreen / CadastroFemeaScreen (observa e renderiza)

Escrita:
CadastroFemeaScreen → FemeaViewModel.cadastrar(comando)
  → CadastrarFemeaUseCase (valida, persiste, dispara alerta)
    → FemeaRepositoryImpl.salvar(femea)  +  AlertaRepositoryImpl.salvar(alerta)
```

### Injeção de Dependência (Hilt)

- `SuinoGestorDatabase` provido como `@Singleton` via `@Module`
- `FemeaDao` e `AlertaDao` providos a partir do database
- `FemeaRepositoryImpl` e `AlertaRepositoryImpl` vinculados via `@Binds`
- Use cases com `@Inject constructor` (sem escopo — stateless)
- `FemeaViewModel` anotado com `@HiltViewModel`

---

## Camada de Dados

### Esquema do Banco de Dados

```
femeas
  id                  INTEGER PK AUTOINCREMENT
  identificacao       TEXT NOT NULL UNIQUE          -- brinco ou tatuagem
  data_nascimento     TEXT NOT NULL                 -- ISO-8601 (yyyy-MM-dd)
  raca_linhagem       TEXT NOT NULL
  categoria           TEXT NOT NULL                 -- MARRA | MATRIZ
  peso_entrada_kg     REAL                          -- opcional
  ecc_atual           INTEGER                       -- 1..5, opcional
  origem              TEXT                          -- PROPRIA | FORNECEDOR, opcional
  foto_uri            TEXT                          -- URI local, opcional
  status              TEXT NOT NULL                 -- StatusFemea inicial
  paridade            INTEGER NOT NULL DEFAULT 0
  data_entrada        TEXT NOT NULL                 -- data do cadastro
  ativo               INTEGER NOT NULL DEFAULT 1

alertas
  id                  INTEGER PK AUTOINCREMENT
  femea_id            INTEGER FK(femeas.id) ON DELETE CASCADE
  tipo                TEXT NOT NULL                 -- TipoAlerta
  mensagem            TEXT NOT NULL
  prioridade          TEXT NOT NULL                 -- PrioridadeAlerta
  data_geracao        TEXT NOT NULL                 -- ISO-8601
  data_leitura        TEXT
  lido                INTEGER NOT NULL DEFAULT 0
```

### Entidade Room — FemeaEntity

```kotlin
// data/db/FemeaEntity.kt
@Entity(
    tableName = "femeas",
    indices = [Index(value = ["identificacao"], unique = true)]
)
data class FemeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String,       // yyyy-MM-dd
    val racaLinhagem: String,
    val categoria: String,            // CategoriaFemea.name
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: String?,              // OrigemAnimal.name ou null
    val fotoUri: String?,
    val status: String,               // StatusFemea.name
    val paridade: Int = 0,
    val dataEntrada: String,          // yyyy-MM-dd
    val ativo: Int = 1
)
```

### Entidade Room — AlertaEntity

```kotlin
// data/db/AlertaEntity.kt
@Entity(
    tableName = "alertas",
    foreignKeys = [
        ForeignKey(
            entity = FemeaEntity::class,
            parentColumns = ["id"],
            childColumns = ["femeaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("femeaId"), Index("lido"), Index("dataGeracao")]
)
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val femeaId: Long?,
    val tipo: String,
    val mensagem: String,
    val prioridade: String,
    val dataGeracao: String,
    val dataLeitura: String?,
    val lido: Int = 0
)
```

### DAOs

```kotlin
// data/db/FemeaDao.kt
@Dao
interface FemeaDao {
    @Query("SELECT * FROM femeas WHERE ativo = 1 ORDER BY identificacao")
    fun observarTodasAtivas(): Flow<List<FemeaEntity>>

    @Query("SELECT * FROM femeas WHERE id = :id")
    fun observarPorId(id: Long): Flow<FemeaEntity?>

    @Query("SELECT * FROM femeas WHERE identificacao = :identificacao LIMIT 1")
    suspend fun buscarPorIdentificacao(identificacao: String): FemeaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(femea: FemeaEntity): Long

    @Update
    suspend fun atualizar(femea: FemeaEntity)
}

// data/db/AlertaDao.kt
@Dao
interface AlertaDao {
    @Query("SELECT * FROM alertas WHERE lido = 0 ORDER BY prioridade DESC, dataGeracao DESC")
    fun observarNaoLidos(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas WHERE femeaId = :femeaId ORDER BY dataGeracao DESC")
    fun observarPorFemea(femeaId: Long): Flow<List<AlertaEntity>>

    @Insert
    suspend fun inserir(alerta: AlertaEntity): Long

    @Query("UPDATE alertas SET lido = 1, dataLeitura = :dataLeitura WHERE id = :id")
    suspend fun marcarComoLido(id: Long, dataLeitura: String)
}
```

### Contribuição ao SuinoGestorDatabase

```kotlin
// data/db/SuinoGestorDatabase.kt  (trecho relevante)
@Database(
    entities = [FemeaEntity::class, AlertaEntity::class /* + futuras */],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SuinoGestorDatabase : RoomDatabase() {
    abstract fun femeaDao(): FemeaDao
    abstract fun alertaDao(): AlertaDao
}
```

---

## Camada de Domínio

### Modelos de Domínio

```kotlin
// domain/model/Femea.kt
data class Femea(
    val id: Long = 0,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val categoria: CategoriaFemea,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: OrigemAnimal?,
    val fotoUri: String?,
    val status: StatusFemea,
    val paridade: Int = 0,
    val dataEntrada: LocalDate,
    val ativo: Boolean = true
)

// domain/model/CategoriaFemea.kt
enum class CategoriaFemea { MARRA, MATRIZ }

// domain/model/OrigemAnimal.kt
enum class OrigemAnimal { PROPRIA, FORNECEDOR }

// domain/model/StatusFemea.kt
enum class StatusFemea {
    MARRA_PREPARACAO,
    AGUARDANDO_COBERTURA,
    GESTANTE,
    LACTANTE,
    VAZIA,
    DESCARTADA
}

// domain/model/Alerta.kt
data class Alerta(
    val id: Long = 0,
    val femeaId: Long?,
    val tipo: TipoAlerta,
    val mensagem: String,
    val prioridade: PrioridadeAlerta,
    val dataGeracao: LocalDate,
    val dataLeitura: LocalDate?,
    val lido: Boolean = false
)

// domain/model/TipoAlerta.kt
enum class TipoAlerta {
    IDADE_MINIMA_PREPARACAO,
    // outros tipos serão adicionados em specs futuras
}

// domain/model/PrioridadeAlerta.kt
enum class PrioridadeAlerta { CRITICO, ALTO, MEDIO, INFO }
```

### Interfaces de Repositório

```kotlin
// domain/repository/FemeaRepository.kt
interface FemeaRepository {
    fun observarTodasAtivas(): Flow<List<Femea>>
    fun observarPorId(id: Long): Flow<Femea?>
    suspend fun buscarPorIdentificacao(identificacao: String): Femea?
    suspend fun salvar(femea: Femea): Long
    suspend fun atualizar(femea: Femea)
}

// domain/repository/AlertaRepository.kt
interface AlertaRepository {
    fun observarNaoLidos(): Flow<List<Alerta>>
    fun observarPorFemea(femeaId: Long): Flow<List<Alerta>>
    suspend fun salvar(alerta: Alerta): Long
    suspend fun marcarComoLido(id: Long)
}
```

### Casos de Uso

#### CalcularIdadeFormatadaUseCase

Calcula a idade de um animal a partir da data de nascimento e retorna a string no formato `"XaYmZd"`.

```kotlin
// domain/usecase/CalcularIdadeFormatadaUseCase.kt
class CalcularIdadeFormatadaUseCase @Inject constructor() {
    operator fun invoke(dataNascimento: LocalDate, dataReferencia: LocalDate = LocalDate.now()): String {
        val periodo = Period.between(dataNascimento, dataReferencia)
        return "${periodo.years}a${periodo.months}m${periodo.days}d"
    }
}
```

#### CadastrarFemeaUseCase

Orquestra a validação, persistência e disparo de alertas.

```kotlin
// domain/usecase/CadastrarFemeaUseCase.kt
class CadastrarFemeaUseCase @Inject constructor(
    private val femeaRepository: FemeaRepository,
    private val alertaRepository: AlertaRepository,
    private val calcularIdade: CalcularIdadeFormatadaUseCase
) {
    suspend operator fun invoke(comando: CadastrarFemeaComando): ResultadoOperacao<Long> {
        // 1. Validar campos obrigatórios
        val erros = validarCamposObrigatorios(comando)
        if (erros.isNotEmpty()) return ResultadoOperacao.Erro(erros.first())

        // 2. Verificar unicidade da identificação
        val existente = femeaRepository.buscarPorIdentificacao(comando.identificacao)
        if (existente != null) {
            return ResultadoOperacao.Erro(
                ErroNegocio.IdentificacaoDuplicada(
                    "Identificação já cadastrada no plantel"
                )
            )
        }

        // 3. Determinar status inicial
        val statusInicial = when (comando.categoria) {
            CategoriaFemea.MARRA -> StatusFemea.MARRA_PREPARACAO
            CategoriaFemea.MATRIZ -> StatusFemea.AGUARDANDO_COBERTURA
        }

        // 4. Persistir fêmea
        val femea = Femea(
            identificacao = comando.identificacao.trim(),
            dataNascimento = comando.dataNascimento,
            racaLinhagem = comando.racaLinhagem.trim(),
            categoria = comando.categoria,
            pesoEntradaKg = comando.pesoEntradaKg,
            eccAtual = comando.eccAtual,
            origem = comando.origem,
            fotoUri = comando.fotoUri,
            status = statusInicial,
            dataEntrada = LocalDate.now()
        )
        val id = femeaRepository.salvar(femea)

        // 5. Verificar alerta de idade mínima (Req 1.6)
        if (comando.categoria == CategoriaFemea.MARRA) {
            val diasDeVida = ChronoUnit.DAYS.between(comando.dataNascimento, LocalDate.now())
            if (diasDeVida < IDADE_MINIMA_PREPARACAO_DIAS) {
                alertaRepository.salvar(
                    Alerta(
                        femeaId = id,
                        tipo = TipoAlerta.IDADE_MINIMA_PREPARACAO,
                        mensagem = "Fêmea abaixo da idade mínima de preparação reprodutiva (160 dias)",
                        prioridade = PrioridadeAlerta.MEDIO,
                        dataGeracao = LocalDate.now()
                    )
                )
            }
        }

        return ResultadoOperacao.Sucesso(id)
    }

    private fun validarCamposObrigatorios(cmd: CadastrarFemeaComando): List<ErroNegocio> {
        val erros = mutableListOf<ErroNegocio>()
        if (cmd.identificacao.isBlank()) erros += ErroNegocio.CampoObrigatorio("identificacao")
        if (cmd.racaLinhagem.isBlank()) erros += ErroNegocio.CampoObrigatorio("racaLinhagem")
        if (cmd.eccAtual != null && cmd.eccAtual !in 1..5) erros += ErroNegocio.EccForaDoIntervalo
        return erros
    }

    companion object {
        const val IDADE_MINIMA_PREPARACAO_DIAS = 160L
    }
}
```

#### ObterFemeaUseCase

```kotlin
// domain/usecase/ObterFemeaUseCase.kt
class ObterFemeaUseCase @Inject constructor(
    private val femeaRepository: FemeaRepository
) {
    operator fun invoke(id: Long): Flow<Femea?> = femeaRepository.observarPorId(id)
}
```

#### ListarFemeasUseCase

```kotlin
// domain/usecase/ListarFemeasUseCase.kt
class ListarFemeasUseCase @Inject constructor(
    private val femeaRepository: FemeaRepository
) {
    operator fun invoke(): Flow<List<Femea>> = femeaRepository.observarTodasAtivas()
}
```

### Comando e Resultado

```kotlin
// domain/usecase/CadastrarFemeaComando.kt
data class CadastrarFemeaComando(
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val categoria: CategoriaFemea,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: OrigemAnimal?,
    val fotoUri: String?
)

// domain/model/ResultadoOperacao.kt
sealed class ResultadoOperacao<out T> {
    data class Sucesso<T>(val dados: T) : ResultadoOperacao<T>()
    data class Erro(val erro: ErroNegocio) : ResultadoOperacao<Nothing>()
}

// domain/model/ErroNegocio.kt
sealed class ErroNegocio {
    data class CampoObrigatorio(val campo: String) : ErroNegocio()
    data class IdentificacaoDuplicada(val mensagem: String) : ErroNegocio()
    object EccForaDoIntervalo : ErroNegocio()
}
```

---

## Camada de Apresentação

### UI State

```kotlin
// presentation/femea/viewmodel/FemeaUiState.kt
data class FemeaUiState(
    val carregando: Boolean = false,
    val femeas: List<FemeaResumo> = emptyList(),
    val erro: String? = null
)

data class FemeaResumo(
    val id: Long,
    val identificacao: String,
    val idadeFormatada: String,   // ex: "0a5m12d"
    val categoria: CategoriaFemea,
    val racaLinhagem: String,
    val status: StatusFemea
)

// presentation/femea/viewmodel/CadastroFemeaUiState.kt
data class CadastroFemeaUiState(
    val salvando: Boolean = false,
    val sucesso: Boolean = false,
    val idadeFormatada: String = "",
    val erroIdentificacao: String? = null,
    val erroRaca: String? = null,
    val erroEcc: String? = null,
    val erroGeral: String? = null,
    val alertaIdadeMinima: Boolean = false
)
```

### ViewModel

```kotlin
// presentation/femea/viewmodel/FemeaViewModel.kt
@HiltViewModel
class FemeaViewModel @Inject constructor(
    private val cadastrarFemea: CadastrarFemeaUseCase,
    private val listarFemeas: ListarFemeasUseCase,
    private val calcularIdade: CalcularIdadeFormatadaUseCase
) : ViewModel() {

    private val _listaState = MutableStateFlow(FemeaUiState())
    val listaState: StateFlow<FemeaUiState> = _listaState.asStateFlow()

    private val _cadastroState = MutableStateFlow(CadastroFemeaUiState())
    val cadastroState: StateFlow<CadastroFemeaUiState> = _cadastroState.asStateFlow()

    init {
        carregarFemeas()
    }

    private fun carregarFemeas() {
        viewModelScope.launch {
            listarFemeas()
                .map { lista ->
                    lista.map { femea ->
                        FemeaResumo(
                            id = femea.id,
                            identificacao = femea.identificacao,
                            idadeFormatada = calcularIdade(femea.dataNascimento),
                            categoria = femea.categoria,
                            racaLinhagem = femea.racaLinhagem,
                            status = femea.status
                        )
                    }
                }
                .catch { e -> _listaState.update { it.copy(erro = e.message) } }
                .collect { resumos ->
                    _listaState.update { it.copy(femeas = resumos, carregando = false) }
                }
        }
    }

    fun atualizarIdadeExibida(dataNascimento: LocalDate) {
        val idade = calcularIdade(dataNascimento)
        _cadastroState.update { it.copy(idadeFormatada = idade) }
    }

    fun cadastrar(comando: CadastrarFemeaComando) {
        viewModelScope.launch {
            _cadastroState.update { it.copy(salvando = true, erroGeral = null) }
            when (val resultado = cadastrarFemea(comando)) {
                is ResultadoOperacao.Sucesso -> {
                    _cadastroState.update {
                        it.copy(salvando = false, sucesso = true)
                    }
                }
                is ResultadoOperacao.Erro -> {
                    val state = when (val erro = resultado.erro) {
                        is ErroNegocio.IdentificacaoDuplicada ->
                            _cadastroState.value.copy(
                                salvando = false,
                                erroIdentificacao = erro.mensagem
                            )
                        is ErroNegocio.CampoObrigatorio ->
                            _cadastroState.value.copy(
                                salvando = false,
                                erroGeral = "Campo obrigatório: ${erro.campo}"
                            )
                        ErroNegocio.EccForaDoIntervalo ->
                            _cadastroState.value.copy(
                                salvando = false,
                                erroEcc = "ECC deve estar entre 1 e 5"
                            )
                    }
                    _cadastroState.value = state
                }
            }
        }
    }
}
```

### Telas Compose

#### ListaFemeasScreen

Exibe o plantel de fêmeas ativas com identificação, idade calculada, categoria e status. Possui FAB para navegar ao cadastro.

```kotlin
// presentation/femea/screen/ListaFemeasScreen.kt
@Composable
fun ListaFemeasScreen(
    onNovaCadastro: () -> Unit,
    onFemeaSelecionada: (Long) -> Unit,
    viewModel: FemeaViewModel = hiltViewModel()
) {
    val state by viewModel.listaState.collectAsStateWithLifecycle()
    // LazyColumn com FemeaCard + FloatingActionButton
}
```

#### CadastroFemeaScreen

Formulário de cadastro com campos obrigatórios e opcionais. Exibe a idade calculada em tempo real ao selecionar a data de nascimento. Mostra erros inline nos campos e um banner de alerta quando a marrã tem menos de 160 dias.

```kotlin
// presentation/femea/screen/CadastroFemeaScreen.kt
@Composable
fun CadastroFemeaScreen(
    onCadastroSalvo: () -> Unit,
    onVoltar: () -> Unit,
    viewModel: FemeaViewModel = hiltViewModel()
) {
    val state by viewModel.cadastroState.collectAsStateWithLifecycle()
    // Campos: identificacao, dataNascimento (DatePicker), racaLinhagem,
    //         categoria (SegmentedButton: Marrã / Matriz),
    //         pesoEntrada (opcional), ecc (Slider 1-5, opcional),
    //         origem (DropdownMenu, opcional), foto (opcional)
    // Exibe: idadeFormatada calculada em tempo real
    // Exibe: banner de alerta IDADE_MINIMA_PREPARACAO quando aplicável
    // Botão: "Cadastrar" → viewModel.cadastrar(comando)
}
```

#### Componentes Reutilizáveis (ui/component/)

| Arquivo | Descrição |
|---|---|
| `IdadeChip.kt` | Chip que exibe a idade no formato `"XaYmZd"` |
| `EccSelector.kt` | Seletor de ECC com 5 botões (1–5) |
| `AlertaBanner.kt` | Banner de aviso com ícone e mensagem de alerta |
| `CategoriaSelector.kt` | SegmentedButton para Marrã / Matriz |
| `OrigemDropdown.kt` | DropdownMenu para Granja Própria / Fornecedor |

---

## Navegação

```kotlin
// Rotas definidas em um sealed class ou object de rotas
object Rotas {
    const val LISTA_FEMEAS = "femeas"
    const val CADASTRO_FEMEA = "femeas/cadastro"
    const val DETALHE_FEMEA = "femeas/{femeaId}"
}

// NavGraph (trecho)
composable(Rotas.LISTA_FEMEAS) {
    ListaFemeasScreen(
        onNovaCadastro = { navController.navigate(Rotas.CADASTRO_FEMEA) },
        onFemeaSelecionada = { id -> navController.navigate("femeas/$id") }
    )
}
composable(Rotas.CADASTRO_FEMEA) {
    CadastroFemeaScreen(
        onCadastroSalvo = { navController.popBackStack() },
        onVoltar = { navController.popBackStack() }
    )
}
```

---

## Algoritmos Principais

### Formatação de Idade — `CalcularIdadeFormatadaUseCase`

```
Entrada: dataNascimento: LocalDate, dataReferencia: LocalDate
Saída:   String no formato "XaYmZd"

Algoritmo:
  periodo = Period.between(dataNascimento, dataReferencia)
  retorna "${periodo.years}a${periodo.months}m${periodo.days}d"

Exemplos:
  dataNascimento = 2024-01-15, dataReferencia = 2025-04-30
  → Period(years=1, months=3, days=15) → "1a3m15d"

  dataNascimento = 2025-01-01, dataReferencia = 2025-06-10
  → Period(years=0, months=5, days=9) → "0a5m9d"
```

`Period.between` usa a aritmética de calendário do `java.time`, garantindo que meses de comprimentos diferentes sejam tratados corretamente (ex.: fevereiro).

### Verificação de Idade Mínima

```
Entrada: dataNascimento: LocalDate, categoria: CategoriaFemea
Saída:   Boolean (deve gerar alerta)

Algoritmo:
  SE categoria == MARRA:
    diasDeVida = ChronoUnit.DAYS.between(dataNascimento, LocalDate.now())
    retorna diasDeVida < 160
  SENÃO:
    retorna false
```

### Validação de Unicidade de Identificação

```
Entrada: identificacao: String
Saída:   Boolean (identificação já existe)

Algoritmo:
  existente = femeaDao.buscarPorIdentificacao(identificacao.trim())
  retorna existente != null
```

A unicidade é garantida em dois níveis:
1. **Domínio**: `CadastrarFemeaUseCase` consulta o repositório antes de persistir.
2. **Banco de dados**: índice `UNIQUE` na coluna `identificacao` da tabela `femeas` — garante integridade mesmo em cenários de concorrência.

---

## Tratamento de Erros

### ResultadoOperacao

```kotlin
// domain/model/ResultadoOperacao.kt
sealed class ResultadoOperacao<out T> {
    data class Sucesso<T>(val dados: T) : ResultadoOperacao<T>()
    data class Erro(val erro: ErroNegocio) : ResultadoOperacao<Nothing>()
}
```

### ErroNegocio

```kotlin
// domain/model/ErroNegocio.kt
sealed class ErroNegocio {
    data class CampoObrigatorio(val campo: String) : ErroNegocio()
    data class IdentificacaoDuplicada(val mensagem: String) : ErroNegocio()
    object EccForaDoIntervalo : ErroNegocio()
}
```

### Mapeamento de Erros para UI

| ErroNegocio | Campo afetado na UI | Mensagem exibida |
|---|---|---|
| `CampoObrigatorio("identificacao")` | Campo identificação | "Identificação é obrigatória" |
| `CampoObrigatorio("racaLinhagem")` | Campo raça/linhagem | "Raça/linhagem é obrigatória" |
| `IdentificacaoDuplicada` | Campo identificação | "Identificação já cadastrada no plantel" |
| `EccForaDoIntervalo` | Campo ECC | "ECC deve estar entre 1 e 5" |

Erros de banco de dados inesperados (ex.: `SQLiteException`) são capturados no ViewModel via `try/catch` e mapeados para `erroGeral` no `CadastroFemeaUiState`.

---

## Propriedades de Correção

*Uma propriedade é uma característica ou comportamento que deve ser verdadeiro em todas as execuções válidas do sistema — essencialmente, uma declaração formal sobre o que o sistema deve fazer. As propriedades servem como ponte entre especificações legíveis por humanos e garantias de correção verificáveis por máquina.*

As propriedades abaixo foram derivadas dos critérios de aceitação do Requisito 1. Para cada critério, foi avaliado se ele é testável como propriedade universal, exemplo específico ou caso de borda.

**Prework resumido:**
- 1.1 + 1.2 → combinados em Property 3 (validação de campos obrigatórios + opcionais)
- 1.3 → Property 1 (formato de idade)
- 1.4 → Property 2 (rejeição de duplicata)
- 1.5 → teste de exemplo (não é propriedade universal)
- 1.6 → Property 4 (alerta de idade mínima)

**Reflexão de redundância:** As propriedades 1 a 4 são logicamente independentes — cada uma testa um aspecto distinto do sistema. Não há redundância a eliminar.

---

### Property 1: Formato de idade é sempre matematicamente consistente

*Para qualquer* data de nascimento e data de referência válidas (onde dataNascimento ≤ dataReferencia), a saída de `CalcularIdadeFormatadaUseCase` deve: (a) corresponder ao padrão `"\\d+a\\d+m\\d+d"`, e (b) ter os valores de anos, meses e dias exatamente iguais aos de `Period.between(dataNascimento, dataReferencia)`.

**Validates: Requirements 1.3**

---

### Property 2: Identificação duplicada é sempre rejeitada

*Para qualquer* identificação já cadastrada no plantel, qualquer tentativa de cadastrar uma nova fêmea com essa mesma identificação — independentemente dos demais campos — deve retornar `ResultadoOperacao.Erro(IdentificacaoDuplicada)` com a mensagem `"Identificação já cadastrada no plantel"`.

**Validates: Requirements 1.4**

---

### Property 3: Validação de campos obrigatórios e opcionais

*Para qualquer* `CadastrarFemeaComando`, o cadastro deve ser rejeitado com `ErroNegocio.CampoObrigatorio` se e somente se pelo menos um campo obrigatório (identificacao, dataNascimento, racaLinhagem, categoria) estiver ausente ou em branco. Quando todos os campos obrigatórios estão presentes e válidos, a ausência de qualquer subconjunto dos campos opcionais (pesoEntradaKg, eccAtual, origem, fotoUri) não deve impedir o cadastro.

**Validates: Requirements 1.1, 1.2**

---

### Property 4: Alerta de idade mínima gerado se e somente se marrã < 160 dias

*Para qualquer* cadastro de fêmea, um alerta do tipo `IDADE_MINIMA_PREPARACAO` deve ser gerado se e somente se a categoria for `MARRA` e a idade em dias no momento do cadastro for inferior a 160. Para categoria `MATRIZ` ou para marrãs com 160 dias ou mais, nenhum alerta desse tipo deve ser gerado.

**Validates: Requirements 1.6**

---

## Estratégia de Testes

### Abordagem Dual

| Tipo | Foco | Ferramentas |
|---|---|---|
| Testes unitários | Exemplos específicos, casos de borda, integrações entre componentes | JUnit 5 + MockK |
| Testes de propriedade | Propriedades universais (Properties 1–4) | Kotest Property Testing |
| Testes de integração | Repositórios contra banco Room em memória | Room in-memory + JUnit 5 |
| Testes de UI | Jornadas críticas (cadastro, listagem, exibição de alerta) | Compose Test Library |

### Testes de Propriedade (Kotest)

Biblioteca: **Kotest Property Testing** (`io.kotest:kotest-property`).
Cada teste de propriedade deve executar no mínimo **100 iterações**.
Cada teste deve referenciar a propriedade do design com o tag:
`Feature: cadastro-femeas, Property N: <texto da propriedade>`

#### Property 1 — Formato de idade

```kotlin
// Gerador: Arb.localDate() para dataNascimento e dataReferencia
// Restrição: dataNascimento <= dataReferencia
// Verificação:
//   (a) resultado.matches(Regex("\\d+a\\d+m\\d+d"))
//   (b) Period.between(dataNascimento, dataReferencia) == parsed(resultado)
```

#### Property 2 — Duplicata sempre rejeitada

```kotlin
// Gerador: Arb.string() para identificacao + Arb para demais campos válidos
// Setup: inserir primeira fêmea com sucesso
// Verificação: segunda tentativa com mesma identificacao retorna
//   ResultadoOperacao.Erro(IdentificacaoDuplicada) com mensagem exata
// Usar FakeAlertaRepository e FakeFemeaRepository in-memory
```

#### Property 3 — Validação de campos obrigatórios

```kotlin
// Gerador: Arb para CadastrarFemeaComando com campos obrigatórios
//          aleatoriamente nulos/em branco
// Verificação:
//   - SE qualquer campo obrigatório está em branco → Erro(CampoObrigatorio)
//   - SE todos obrigatórios presentes → Sucesso (independente dos opcionais)
// Usar FakeFemeaRepository que nunca retorna duplicata
```

#### Property 4 — Alerta de idade mínima

```kotlin
// Gerador: Arb.localDate() para dataNascimento, Arb para categoria
// Verificação:
//   - SE categoria == MARRA E diasDeVida < 160 → alerta IDADE_MINIMA_PREPARACAO salvo
//   - SE categoria == MARRA E diasDeVida >= 160 → nenhum alerta salvo
//   - SE categoria == MATRIZ → nenhum alerta salvo (independente da idade)
// Usar FakeAlertaRepository para capturar alertas salvos
// Injetar LocalDate.now() via parâmetro para controle determinístico
```

### Testes Unitários (exemplos específicos)

- `CadastrarFemeaUseCase`: cadastro bem-sucedido com todos os campos
- `CadastrarFemeaUseCase`: cadastro bem-sucedido sem campos opcionais
- `CadastrarFemeaUseCase`: rejeição com identificação duplicada (mensagem exata)
- `CadastrarFemeaUseCase`: rejeição com identificação em branco
- `CadastrarFemeaUseCase`: rejeição com ECC = 0 e ECC = 6
- `CadastrarFemeaUseCase`: foto vinculada é persistida (Req 1.5)
- `CalcularIdadeFormatadaUseCase`: exemplos concretos (0a0m0d, 1a3m15d, 0a5m9d)
- `FemeaViewModel`: estado de erro mapeado corretamente para cada ErroNegocio

### Testes de Integração (Room in-memory)

- `FemeaRepositoryImpl`: inserção e recuperação por id
- `FemeaRepositoryImpl`: `buscarPorIdentificacao` retorna null para identificação inexistente
- `FemeaRepositoryImpl`: índice UNIQUE lança exceção ao inserir duplicata via DAO
- `AlertaRepositoryImpl`: alerta salvo e recuperado por femeaId

### Testes de UI (Compose Test Library)

- `CadastroFemeaScreen`: campos obrigatórios exibem erro inline ao tentar salvar vazio
- `CadastroFemeaScreen`: banner de alerta aparece quando marrã < 160 dias
- `CadastroFemeaScreen`: idade formatada atualiza em tempo real ao selecionar data
- `ListaFemeasScreen`: lista exibe identificação e idade de cada fêmea cadastrada
