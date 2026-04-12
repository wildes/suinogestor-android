# Design: Gestão do Plantel de Matrizes e Reprodutores

## Visão Geral

O módulo **Gestão do Plantel de Matrizes e Reprodutores** é o núcleo do SuinoGestor. Ele digitaliza a escrituração zootécnica individual de cada fêmea (matriz/marrã) e de cada varrão, calculando automaticamente datas críticas, alertas de manejo e indicadores de desempenho reprodutivo — tudo offline, em um dispositivo Android.

A arquitetura segue Clean Architecture + MVVM, com três camadas bem definidas: **Data** (Room + repositórios), **Domain** (modelos, interfaces, casos de uso) e **Presentation** (ViewModels + Compose). O fluxo de dependências é estritamente unidirecional: Presentation → Domain ← Data.

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                         │
│  PlantelScreen  FichaFemeaScreen  CalendarioScreen ...  │
│  PlantelViewModel  FichaFemeaViewModel  ...             │
└────────────────────────┬────────────────────────────────┘
                         │ observa StateFlow / chama UseCases
┌────────────────────────▼────────────────────────────────┐
│                      DOMAIN                             │
│  Femea  Reprodutor  EventoReprodutivo  Alerta  ...      │
│  CalculadoraReprodutiva  MotorDeAlertas                 │
│  CadastrarFemeaUseCase  RegistrarCoberturaUseCase  ...  │
│  FemeaRepository (interface)  ReprodutorRepository ...  │
└────────────────────────┬────────────────────────────────┘
                         │ implementado por
┌────────────────────────▼────────────────────────────────┐
│                       DATA                              │
│  FemeaEntity  ReprodutorEntity  EventoEntity  ...       │
│  FemeaDao  ReprodutorDao  EventoDao  AlertaDao  ...     │
│  FemeaRepositoryImpl  ReprodutorRepositoryImpl  ...     │
│  SuinoGestorDatabase (Room)                             │
└─────────────────────────────────────────────────────────┘
```

---

## Arquitetura

### Camadas e Responsabilidades

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Data | `br.com.suinogestor.data.db` | Entidades Room, DAOs, migrations |
| Data | `br.com.suinogestor.data.repository` | Implementações de repositório, mapeadores entity↔domain |
| Domain | `br.com.suinogestor.domain.model` | Modelos de domínio (Kotlin puro, sem Android) |
| Domain | `br.com.suinogestor.domain.repository` | Interfaces de repositório |
| Domain | `br.com.suinogestor.domain.usecase` | Um arquivo por caso de uso |
| Presentation | `br.com.suinogestor.presentation.plantel` | Painel do plantel |
| Presentation | `br.com.suinogestor.presentation.femea` | Ficha da fêmea, cadastro, eventos |
| Presentation | `br.com.suinogestor.presentation.reprodutor` | Ficha do reprodutor |
| Presentation | `br.com.suinogestor.presentation.calendario` | Calendário de manejo |
| UI | `br.com.suinogestor.ui.component` | Composables reutilizáveis |

### Fluxo de Dados

```
Room (Flow<Entity>) → RepositoryImpl (mapeia para Domain Model)
  → UseCase (lógica de negócio pura)
    → ViewModel (StateFlow<UiState>)
      → Composable (observa e renderiza)
```

Toda escrita segue o caminho inverso via `suspend fun` nos repositórios.

### Injeção de Dependência

Hilt gerencia o grafo de dependências:
- `@Database` → `SuinoGestorDatabase` provido em `@Singleton`
- DAOs providos a partir do database
- Repositórios providos como `@Singleton` via `@Binds` em módulos Hilt
- Use cases instanciados por `@Inject constructor` (sem escopo — stateless)
- ViewModels anotados com `@HiltViewModel`

---

## Camada de Dados

### Esquema do Banco de Dados

```
femeas
  id                  INTEGER PK AUTOINCREMENT
  identificacao       TEXT NOT NULL UNIQUE
  data_nascimento     TEXT NOT NULL          -- ISO-8601 (yyyy-MM-dd)
  raca_linhagem       TEXT NOT NULL
  categoria           TEXT NOT NULL          -- MARRA | MATRIZ
  peso_entrada_kg     REAL
  ecc_atual           INTEGER                -- 1..5
  origem              TEXT                   -- PROPRIA | FORNECEDOR
  foto_uri            TEXT
  status              TEXT NOT NULL          -- MARRA_PREPARACAO | AGUARDANDO_COBERTURA |
                                             --   GESTANTE | LACTANTE | VAZIA | DESCARTADA
  paridade            INTEGER NOT NULL DEFAULT 0
  data_entrada        TEXT NOT NULL
  data_descarte       TEXT
  motivo_descarte     TEXT
  ativo               INTEGER NOT NULL DEFAULT 1

reprodutores
  id                  INTEGER PK AUTOINCREMENT
  identificacao       TEXT NOT NULL UNIQUE
  data_nascimento     TEXT NOT NULL
  raca_linhagem       TEXT NOT NULL
  tipo_uso            TEXT NOT NULL          -- MONTA_NATURAL | COLETA_IA | AMBOS
  peso_kg             REAL
  ecc_atual           INTEGER                -- 1..5
  ativo               INTEGER NOT NULL DEFAULT 1

eventos_reprodutivos
  id                  INTEGER PK AUTOINCREMENT
  femea_id            INTEGER NOT NULL FK(femeas.id)
  tipo                TEXT NOT NULL          -- CIO | COBERTURA | DIAGNOSTICO_GESTACAO |
                                             --   PARTO | DESMAME | REPETICAO_CIO |
                                             --   DESCARTE | FLUSHING | AVALIACAO_ECC
  data_evento         TEXT NOT NULL
  reprodutor_id       INTEGER FK(reprodutores.id)  -- para COBERTURA
  tipo_servico        TEXT                   -- IA | MN
  ordem_cobertura     INTEGER                -- 1, 2 ou 3
  resultado           TEXT                   -- POSITIVO | NEGATIVO (para DIAGNOSTICO)
  metodo_diagnostico  TEXT
  nascidos_vivos      INTEGER
  natimortos          INTEGER
  mumificados         INTEGER
  peso_medio_nasc_kg  REAL
  peso_desmame_kg     REAL
  num_leitoes_desmame INTEGER
  ecc_valor           INTEGER                -- para AVALIACAO_ECC
  peso_femea_kg       REAL                   -- peso da fêmea no evento
  observacoes         TEXT
  ciclo_numero        INTEGER                -- número do ciclo reprodutivo

avaliacoes_ecc
  id                  INTEGER PK AUTOINCREMENT
  animal_id           INTEGER NOT NULL
  tipo_animal         TEXT NOT NULL          -- FEMEA | REPRODUTOR
  data_avaliacao      TEXT NOT NULL
  ecc_valor           INTEGER NOT NULL       -- 1..5
  observacoes         TEXT

alertas
  id                  INTEGER PK AUTOINCREMENT
  femea_id            INTEGER FK(femeas.id)
  reprodutor_id       INTEGER FK(reprodutores.id)
  tipo                TEXT NOT NULL
  mensagem            TEXT NOT NULL
  prioridade          TEXT NOT NULL          -- CRITICO | ALTO | MEDIO | INFO
  data_geracao        TEXT NOT NULL
  data_leitura        TEXT
  lido                INTEGER NOT NULL DEFAULT 0
  acao_pendente       TEXT                   -- tipo de ação associada
  data_acao_prevista  TEXT
```

### Entidades Room

```kotlin
// data/db/FemeaEntity.kt
@Entity(tableName = "femeas",
    indices = [Index(value = ["identificacao"], unique = true)])
data class FemeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String,
    val racaLinhagem: String,
    val categoria: String,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: String?,
    val fotoUri: String?,
    val status: String,
    val paridade: Int,
    val dataEntrada: String,
    val dataDescarte: String?,
    val motivoDescarte: String?,
    val ativo: Int
)

// data/db/ReprodutorEntity.kt
@Entity(tableName = "reprodutores",
    indices = [Index(value = ["identificacao"], unique = true)])
data class ReprodutorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String,
    val racaLinhagem: String,
    val tipoUso: String,
    val pesoKg: Double?,
    val eccAtual: Int?,
    val ativo: Int
)

// data/db/EventoReprodutivo Entity.kt
@Entity(tableName = "eventos_reprodutivos",
    foreignKeys = [
        ForeignKey(entity = FemeaEntity::class,
            parentColumns = ["id"], childColumns = ["femeaId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ReprodutorEntity::class,
            parentColumns = ["id"], childColumns = ["reprodutorId"],
            onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("femeaId"), Index("reprodutorId"), Index("dataEvento")])
data class EventoReprodutivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val femeaId: Long,
    val tipo: String,
    val dataEvento: String,
    val reprodutorId: Long?,
    val tipoServico: String?,
    val ordemCobertura: Int?,
    val resultado: String?,
    val metodoDiagnostico: String?,
    val nascidosVivos: Int?,
    val natimortos: Int?,
    val mumificados: Int?,
    val pesoMedioNascKg: Double?,
    val pesoDesmameKg: Double?,
    val numLeitoesDesmame: Int?,
    val eccValor: Int?,
    val pesoFemeaKg: Double?,
    val observacoes: String?,
    val cicloNumero: Int?
)

// data/db/AlertaEntity.kt
@Entity(tableName = "alertas",
    foreignKeys = [
        ForeignKey(entity = FemeaEntity::class,
            parentColumns = ["id"], childColumns = ["femeaId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("femeaId"), Index("lido"), Index("dataGeracao")])
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val femeaId: Long?,
    val reprodutorId: Long?,
    val tipo: String,
    val mensagem: String,
    val prioridade: String,
    val dataGeracao: String,
    val dataLeitura: String?,
    val lido: Int,
    val acaoPendente: String?,
    val dataAcaoPrevista: String?
)
```

### DAOs

```kotlin
// data/db/FemeaDao.kt
@Dao
interface FemeaDao {
    @Query("SELECT * FROM femeas WHERE ativo = 1 ORDER BY identificacao")
    fun observarTodasAtivas(): Flow<List<FemeaEntity>>

    @Query("SELECT * FROM femeas WHERE status = :status AND ativo = 1")
    fun observarPorStatus(status: String): Flow<List<FemeaEntity>>

    @Query("SELECT * FROM femeas WHERE id = :id")
    fun observarPorId(id: Long): Flow<FemeaEntity?>

    @Query("SELECT * FROM femeas WHERE identificacao = :identificacao LIMIT 1")
    suspend fun buscarPorIdentificacao(identificacao: String): FemeaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(femea: FemeaEntity): Long

    @Update
    suspend fun atualizar(femea: FemeaEntity)

    @Query("UPDATE femeas SET status = :status WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: String)

    @Query("UPDATE femeas SET paridade = paridade + 1 WHERE id = :id")
    suspend fun incrementarParidade(id: Long)

    @Query("SELECT COUNT(*) FROM femeas WHERE ativo = 1")
    fun observarTotalAtivas(): Flow<Int>

    @Query("SELECT paridade, COUNT(*) as total FROM femeas WHERE ativo = 1 GROUP BY paridade")
    fun observarDistribuicaoParidade(): Flow<List<ParidadeCount>>
}

// data/db/ReprodutorDao.kt
@Dao
interface ReprodutorDao {
    @Query("SELECT * FROM reprodutores WHERE ativo = 1 ORDER BY identificacao")
    fun observarTodosAtivos(): Flow<List<ReprodutorEntity>>

    @Query("SELECT * FROM reprodutores WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReprodutorEntity?>

    @Query("SELECT * FROM reprodutores WHERE identificacao = :identificacao LIMIT 1")
    suspend fun buscarPorIdentificacao(identificacao: String): ReprodutorEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(reprodutor: ReprodutorEntity): Long

    @Update
    suspend fun atualizar(reprodutor: ReprodutorEntity)
}

// data/db/EventoReprodutivoDao.kt
@Dao
interface EventoReprodutivoDao {
    @Query("SELECT * FROM eventos_reprodutivos WHERE femeaId = :femeaId ORDER BY dataEvento DESC")
    fun observarPorFemea(femeaId: Long): Flow<List<EventoReprodutivoEntity>>

    @Query("""SELECT * FROM eventos_reprodutivos
              WHERE tipo = :tipo AND dataEvento BETWEEN :inicio AND :fim
              ORDER BY dataEvento""")
    fun observarPorTipoEPeriodo(tipo: String, inicio: String, fim: String): Flow<List<EventoReprodutivoEntity>>

    @Query("""SELECT * FROM eventos_reprodutivos
              WHERE reprodutorId = :reprodutorId AND tipo = 'COBERTURA'
              AND dataEvento >= :dataInicio ORDER BY dataEvento DESC""")
    suspend fun coberturasReprodutorDesde(reprodutorId: Long, dataInicio: String): List<EventoReprodutivoEntity>

    @Insert
    suspend fun inserir(evento: EventoReprodutivoEntity): Long

    @Query("SELECT * FROM eventos_reprodutivos WHERE femeaId = :femeaId AND tipo = 'COBERTURA' ORDER BY dataEvento DESC LIMIT 1")
    suspend fun ultimaCobertura(femeaId: Long): EventoReprodutivoEntity?

    @Query("SELECT * FROM eventos_reprodutivos WHERE femeaId = :femeaId AND tipo = 'PARTO' ORDER BY dataEvento DESC LIMIT 1")
    suspend fun ultimoParto(femeaId: Long): EventoReprodutivoEntity?

    @Query("SELECT * FROM eventos_reprodutivos WHERE femeaId = :femeaId AND tipo = 'DESMAME' ORDER BY dataEvento DESC LIMIT 1")
    suspend fun ultimoDesmame(femeaId: Long): EventoReprodutivoEntity?

    @Query("SELECT COUNT(*) FROM eventos_reprodutivos WHERE femeaId = :femeaId AND tipo = 'REPETICAO_CIO'")
    suspend fun totalRepeticoesCio(femeaId: Long): Int
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

    @Query("DELETE FROM alertas WHERE femeaId = :femeaId AND tipo = :tipo AND lido = 0")
    suspend fun removerAlertasPendentes(femeaId: Long, tipo: String)
}
```

---

## Camada de Domínio

### Modelos de Domínio

```kotlin
// domain/model/Femea.kt
data class Femea(
    val id: Long,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val categoria: CategoriaFemea,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: OrigemAnimal?,
    val fotoUri: String?,
    val status: StatusFemea,
    val paridade: Int,
    val dataEntrada: LocalDate,
    val dataDescarte: LocalDate?,
    val motivoDescarte: MotivoDescarte?,
    val ativo: Boolean
)

enum class CategoriaFemea { MARRA, MATRIZ }
enum class OrigemAnimal { PROPRIA, FORNECEDOR }
enum class StatusFemea {
    MARRA_PREPARACAO, AGUARDANDO_COBERTURA,
    GESTANTE, LACTANTE, VAZIA, DESCARTADA
}
enum class MotivoDescarte {
    PROBLEMA_REPRODUTIVO, BAIXA_PRODUCAO,
    PROBLEMA_LOCOMOTOR, PROBLEMA_SANITARIO,
    IDADE_AVANCADA, OUTRO
}

// domain/model/Reprodutor.kt
data class Reprodutor(
    val id: Long,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val tipoUso: TipoUsoReprodutorEnum,
    val pesoKg: Double?,
    val eccAtual: Int?,
    val ativo: Boolean
)
enum class TipoUsoReprodutorEnum { MONTA_NATURAL, COLETA_IA, AMBOS }

// domain/model/EventoReprodutivo.kt
data class EventoReprodutivo(
    val id: Long,
    val femeaId: Long,
    val tipo: TipoEvento,
    val dataEvento: LocalDate,
    val reprodutorId: Long?,
    val tipoServico: TipoServico?,
    val ordemCobertura: Int?,
    val resultado: ResultadoDiagnostico?,
    val metodoDiagnostico: String?,
    val nascidosVivos: Int?,
    val natimortos: Int?,
    val mumificados: Int?,
    val pesoMedioNascKg: Double?,
    val pesoDesmameKg: Double?,
    val numLeitoesDesmame: Int?,
    val eccValor: Int?,
    val pesoFemeaKg: Double?,
    val observacoes: String?,
    val cicloNumero: Int?
)

enum class TipoEvento {
    CIO, COBERTURA, DIAGNOSTICO_GESTACAO, PARTO,
    DESMAME, REPETICAO_CIO, DESCARTE, FLUSHING, AVALIACAO_ECC
}
enum class TipoServico { IA, MN }
enum class ResultadoDiagnostico { POSITIVO, NEGATIVO }

// domain/model/Alerta.kt
data class Alerta(
    val id: Long,
    val femeaId: Long?,
    val reprodutorId: Long?,
    val tipo: TipoAlerta,
    val mensagem: String,
    val prioridade: PrioridadeAlerta,
    val dataGeracao: LocalDate,
    val dataLeitura: LocalDate?,
    val lido: Boolean,
    val acaoPendente: TipoEvento?,
    val dataAcaoPrevista: LocalDate?
)

enum class PrioridadeAlerta { CRITICO, ALTO, MEDIO, INFO }
enum class TipoAlerta {
    IDADE_MINIMA_PREPARACAO, MARRA_APTA_COBERTURA, COBERTURA_PRECOCE_IDADE,
    COBERTURA_PRECOCE_PESO, IDE_LONGO, DESMAME_PRECOCE, NATIMORTOS_ACIMA_ALVO,
    DIAGNOSTICO_PENDENTE, FEMEA_VAZIA, REPETICAO_CIO_TRIPLA,
    TAXA_REPETICAO_ALTA, TAXA_REPETICAO_CRITICA, REPETICAO_IRREGULAR_BRUCELOSE,
    REPETICAO_IRREGULAR_LEPTOSPIROSE, ECC_BAIXO_DESMAME, ECC_FORA_IDEAL_PARTO,
    REPRODUTOR_SOBRE_CONDICIONADO, PERDA_PESO_PRIMIPARA, PERDA_PESO_MULTIPARA,
    REPRODUTOR_SEM_USO, REPRODUTOR_SUPERUTILIZADO, REPRODUTOR_TREINAMENTO_LIMITE,
    ACAO_PENDENTE_ATRASADA, META_COBERTURAS_NAO_ATINGIDA, DISTRIBUICAO_PARIDADE,
    TAXA_REPOSICAO_BAIXA, TAXA_REPOSICAO_ALTA, INDICE_PARTOS_BAIXO,
    PRODUTIVIDADE_BAIXA, AUMETO_RACAO_GESTACAO, PRE_PARTO,
    IDE_PRIMIPARA_CURTO, DESGASTE_PRIMIPARA, QUEDA_DESEMPENHO_2_PARTO,
    CONCENTRACAO_PARTOS_MATERNIDADE
}

// domain/model/DatasCalculadas.kt
data class DatasCalculadas(
    val dataPartoPrevisto: LocalDate,
    val dataRetornoCioRegularInicio: LocalDate,
    val dataRetornoCioRegularFim: LocalDate,
    val dataDiagnosticoRecomendado: LocalDate
)

// domain/model/IndicadoresPlantel.kt
data class IndicadoresPlantel(
    val totalFemeasAtivas: Int,
    val partosPorFemeaAno: Double,
    val taxaFertilidade: Double,
    val taxaRepeticaoCio: Double,
    val ideMedio: Double,
    val mediaNascidosVivosPorParto: Double,
    val leitoesDesmamadosPorFemeaAno: Double,
    val metaCoberturasSemanais: Double,
    val coberturasNaSemanaAtual: Int,
    val distribuicaoParidade: Map<Int, Int>,
    val totalPorStatus: Map<StatusFemea, Int>
)

// domain/model/EventoCalendario.kt
data class EventoCalendario(
    val data: LocalDate,
    val tipo: TipoEvento,
    val femeaId: Long,
    val femeaIdentificacao: String,
    val descricao: String,
    val atrasado: Boolean
)
```

### Interfaces de Repositório

```kotlin
// domain/repository/FemeaRepository.kt
interface FemeaRepository {
    fun observarTodasAtivas(): Flow<List<Femea>>
    fun observarPorStatus(status: StatusFemea): Flow<List<Femea>>
    fun observarPorId(id: Long): Flow<Femea?>
    suspend fun buscarPorIdentificacao(identificacao: String): Femea?
    suspend fun salvar(femea: Femea): Long
    suspend fun atualizar(femea: Femea)
    suspend fun atualizarStatus(id: Long, status: StatusFemea)
    suspend fun incrementarParidade(id: Long)
    fun observarDistribuicaoParidade(): Flow<Map<Int, Int>>
}

// domain/repository/ReprodutorRepository.kt
interface ReprodutorRepository {
    fun observarTodosAtivos(): Flow<List<Reprodutor>>
    fun observarPorId(id: Long): Flow<Reprodutor?>
    suspend fun buscarPorIdentificacao(identificacao: String): Reprodutor?
    suspend fun salvar(reprodutor: Reprodutor): Long
    suspend fun atualizar(reprodutor: Reprodutor)
}

// domain/repository/EventoReprodutivoRepository.kt
interface EventoReprodutivoRepository {
    fun observarPorFemea(femeaId: Long): Flow<List<EventoReprodutivo>>
    fun observarPorTipoEPeriodo(tipo: TipoEvento, inicio: LocalDate, fim: LocalDate): Flow<List<EventoReprodutivo>>
    suspend fun salvar(evento: EventoReprodutivo): Long
    suspend fun ultimaCobertura(femeaId: Long): EventoReprodutivo?
    suspend fun ultimoParto(femeaId: Long): EventoReprodutivo?
    suspend fun ultimoDesmame(femeaId: Long): EventoReprodutivo?
    suspend fun totalRepeticoesCio(femeaId: Long): Int
    suspend fun coberturasReprodutorNaSemana(reprodutorId: Long, inicioSemana: LocalDate): Int
}

// domain/repository/AlertaRepository.kt
interface AlertaRepository {
    fun observarNaoLidos(): Flow<List<Alerta>>
    fun observarPorFemea(femeaId: Long): Flow<List<Alerta>>
    suspend fun salvar(alerta: Alerta): Long
    suspend fun marcarComoLido(id: Long)
    suspend fun removerAlertasPendentes(femeaId: Long, tipo: TipoAlerta)
}
```

### Casos de Uso

Cada caso de uso reside em seu próprio arquivo em `domain/usecase/`.

| Arquivo | Responsabilidade |
|---|---|
| `CadastrarFemeaUseCase` | Valida unicidade de identificação, cria Femea, dispara alertas de idade |
| `CadastrarReprodutorUseCase` | Valida unicidade, cria Reprodutor |
| `RegistrarCioUseCase` | Registra detecção de cio, atualiza status da fêmea |
| `RegistrarCoberturaUseCase` | Valida reprodutor, registra cobertura, calcula DatasCalculadas, verifica alertas de marrã |
| `RegistrarDiagnosticoGestacaoUseCase` | Registra diagnóstico, atualiza status (Gestante/Vazia), dispara alertas |
| `RegistrarPartoUseCase` | Registra parto, incrementa paridade, calcula taxa natimortos, dispara alertas |
| `RegistrarDesmameUseCase` | Registra desmame, calcula IDE quando nova cobertura for lançada, dispara alertas de desmame precoce |
| `RegistrarEccUseCase` | Registra avaliação de ECC, dispara alertas de condição corporal |
| `RegistrarDescarteUseCase` | Registra descarte com justificativa, atualiza status |
| `RegistrarFlushingUseCase` | Registra início do flushing, calcula data prevista de cobertura |
| `ObterFichaFemeaUseCase` | Agrega todos os eventos e alertas de uma fêmea |
| `ObterFichaReprodutorUseCase` | Agrega histórico de uso do reprodutor |
| `ObterIndicadoresPlantelUseCase` | Calcula todos os KPIs do painel |
| `ObterCalendarioManejoUseCase` | Projeta eventos futuros para o calendário |
| `ObterDistribuicaoParidadeUseCase` | Retorna distribuição atual vs. ideal |
| `CalcularMetaCoberturasSemanaisUseCase` | Fórmula: (plantel × partos/fêmea/ano) ÷ 52 ÷ taxa fertilidade |
| `VerificarRepeticaoCioUseCase` | Classifica repetição (regular/irregular), acumula contador, dispara alertas |
| `GerarRelatorioFemeaUseCase` | Monta estrutura de dados para PDF da ficha individual |
| `GerarRelatorioReprodutorUseCase` | Monta estrutura de dados para PDF do reprodutor |
| `GerarRelatorioResumoPlanteiUseCase` | Monta estrutura de dados para PDF do resumo do plantel |
| `AtualizarPesoReprodutorUseCase` | Atualiza peso, calcula ração diária recomendada (1% PV) |
| `VerificarAlertasReprodutorUseCase` | Verifica sub/superutilização do reprodutor |
| `ConfigurarParametrosGranjaUseCase` | Salva intervalo de desmame padrão e capacidade de maternidade |

---

## Algoritmos Principais

### Calculadora Reprodutiva

A `CalculadoraReprodutiva` é um objeto Kotlin puro (sem dependências Android) que encapsula todas as fórmulas do domínio.

```kotlin
// domain/usecase/CalculadoraReprodutiva.kt
object CalculadoraReprodutiva {

    const val DIAS_GESTACAO = 114
    const val DIAS_RETORNO_CIO_INICIO = 18
    const val DIAS_RETORNO_CIO_FIM = 24
    const val DIAS_DIAGNOSTICO_GESTACAO = 30
    const val DIAS_DESMAME_PADRAO = 21
    const val DIAS_IDE_LONGO = 7
    const val DIAS_IDE_PRIMIPARA_CURTO = 11
    const val IDADE_MINIMA_PREPARACAO_DIAS = 160
    const val IDADE_MINIMA_COBERTURA_DIAS = 210
    const val PESO_MINIMO_COBERTURA_KG = 140.0
    const val PERCENTUAL_NATIMORTOS_ALVO = 0.04
    const val PERCENTUAL_PERDA_PESO_PRIMIPARA = 0.09
    const val PERCENTUAL_PERDA_PESO_MULTIPARA = 0.15
    const val DIAS_REPETICAO_REGULAR_INICIO = 17
    const val DIAS_REPETICAO_REGULAR_FIM = 24
    const val DIAS_REPETICAO_IRREGULAR_INICIO = 25
    const val DIAS_REPETICAO_IRREGULAR_FIM = 60
    const val DIAS_REPETICAO_BRUCELOSE_INICIO = 35
    const val DIAS_REPETICAO_BRUCELOSE_FIM = 60
    const val DIAS_REPETICAO_LEPTOSPIROSE_INICIO = 30
    const val DIAS_REPETICAO_LEPTOSPIROSE_FIM = 38

    /** Req 3.3 — data parto = data cobertura + 114 dias */
    fun calcularDataPartoPrevisto(dataCobertura: LocalDate): LocalDate =
        dataCobertura.plusDays(DIAS_GESTACAO.toLong())

    /** Req 3.3 — janela de retorno ao cio regular */
    fun calcularJanelaRetornoCio(dataCobertura: LocalDate): ClosedRange<LocalDate> =
        dataCobertura.plusDays(DIAS_RETORNO_CIO_INICIO.toLong())..
        dataCobertura.plusDays(DIAS_RETORNO_CIO_FIM.toLong())

    /** Req 3.3 — data recomendada para diagnóstico de gestação */
    fun calcularDataDiagnostico(dataCobertura: LocalDate): LocalDate =
        dataCobertura.plusDays(DIAS_DIAGNOSTICO_GESTACAO.toLong())

    /** Req 3.4 — IDE = dias entre desmame e próxima cobertura */
    fun calcularIde(dataDesmame: LocalDate, dataProximaCobertura: LocalDate): Int =
        ChronoUnit.DAYS.between(dataDesmame, dataProximaCobertura).toInt()

    /** Req 7.1 — classifica repetição de cio */
    fun classificarRepeticaoCio(dataCobertura: LocalDate, dataRetorno: LocalDate): ClassificacaoRepeticao {
        val intervalo = ChronoUnit.DAYS.between(dataCobertura, dataRetorno).toInt()
        return when {
            intervalo in DIAS_REPETICAO_REGULAR_INICIO..DIAS_REPETICAO_REGULAR_FIM ->
                ClassificacaoRepeticao.REGULAR
            intervalo in DIAS_REPETICAO_IRREGULAR_INICIO..DIAS_REPETICAO_IRREGULAR_FIM ->
                ClassificacaoRepeticao.IRREGULAR
            else -> ClassificacaoRepeticao.FORA_DO_PADRAO
        }
    }

    /** Req 9.6 — meta semanal de coberturas */
    fun calcularMetaCoberturasSemanais(
        totalFemeas: Int,
        partosPorFemeaAno: Double,
        taxaFertilidade: Double
    ): Double {
        require(taxaFertilidade > 0.0) { "Taxa de fertilidade deve ser > 0" }
        return (totalFemeas * partosPorFemeaAno) / 52.0 / taxaFertilidade
    }

    /** Req 9.1 — partos por fêmea ao ano */
    fun calcularPartosPorFemeaAno(totalPartos: Int, diasNoPlantel: Int): Double {
        require(diasNoPlantel > 0) { "Dias no plantel deve ser > 0" }
        return totalPartos / (diasNoPlantel / 365.0)
    }

    /** Req 8.4 — taxa de reposição anualizada */
    fun calcularTaxaReposicaoAnualizada(
        totalDescartesNoPeriodo: Int,
        totalFemeasAtivas: Int,
        diasNoPeriodo: Int
    ): Double {
        require(totalFemeasAtivas > 0) { "Total de fêmeas deve ser > 0" }
        require(diasNoPeriodo > 0) { "Dias no período deve ser > 0" }
        return (totalDescartesNoPeriodo.toDouble() / totalFemeasAtivas) * (365.0 / diasNoPeriodo)
    }

    /** Req 9.1 — taxa de fertilidade */
    fun calcularTaxaFertilidade(totalCoberturas: Int, totalPartos: Int): Double {
        require(totalCoberturas > 0) { "Total de coberturas deve ser > 0" }
        return totalPartos.toDouble() / totalCoberturas
    }

    /** Req 9.1 — taxa de repetição de cio */
    fun calcularTaxaRepeticaoCio(totalRepeticoes: Int, totalCoberturas: Int): Double {
        require(totalCoberturas > 0) { "Total de coberturas deve ser > 0" }
        return totalRepeticoes.toDouble() / totalCoberturas
    }

    /** Req 3.7 — taxa de natimortos */
    fun calcularTaxaNatimortos(natimortos: Int, totalNascidos: Int): Double {
        require(totalNascidos > 0) { "Total de nascidos deve ser > 0" }
        return natimortos.toDouble() / totalNascidos
    }

    /** Req 5.6/5.7 — percentual de perda de peso */
    fun calcularPercentualPerdaPeso(pesoParto: Double, pesoDesmame: Double): Double {
        require(pesoParto > 0.0) { "Peso no parto deve ser > 0" }
        return (pesoParto - pesoDesmame) / pesoParto
    }

    /** Req 2.3 — ração diária recomendada para reprodutor (1% PV) */
    fun calcularRacaoDiariaReprodutorKg(pesoKg: Double): Double = pesoKg * 0.01

    /** Req 4.6 — data prevista de cobertura após flushing */
    fun calcularDataCoberturaPosFlushingRange(dataInicioFlushing: LocalDate): ClosedRange<LocalDate> =
        dataInicioFlushing.plusDays(10)..dataInicioFlushing.plusDays(14)

    /** Req 1.3 — formata idade no padrão "1a3m15d" */
    fun formatarIdade(dataNascimento: LocalDate, dataReferencia: LocalDate): String {
        val periodo = Period.between(dataNascimento, dataReferencia)
        return "${periodo.years}a${periodo.months}m${periodo.days}d"
    }

    /** Req 6.3 — fase gestacional */
    fun calcularFaseGestacional(dataCobertura: LocalDate, dataReferencia: LocalDate): FaseGestacional {
        val dias = ChronoUnit.DAYS.between(dataCobertura, dataReferencia).toInt()
        return when {
            dias <= 38 -> FaseGestacional.PRIMEIRO_TERCO
            dias <= 75 -> FaseGestacional.SEGUNDO_TERCO
            else -> FaseGestacional.TERCEIRO_TERCO
        }
    }
}

enum class ClassificacaoRepeticao { REGULAR, IRREGULAR, FORA_DO_PADRAO }
enum class FaseGestacional { PRIMEIRO_TERCO, SEGUNDO_TERCO, TERCEIRO_TERCO }
```

### Motor de Alertas

O `MotorDeAlertas` é invocado pelos casos de uso após cada evento registrado. Ele avalia as condições e persiste alertas via `AlertaRepository`.

```kotlin
// domain/usecase/MotorDeAlertas.kt
class MotorDeAlertas @Inject constructor(
    private val alertaRepository: AlertaRepository
) {
    // Cada método avalia uma condição e persiste o alerta se necessário.
    // Retorna o Alerta criado ou null se a condição não foi atingida.

    suspend fun verificarIdadeMinimaPreparacao(femea: Femea, hoje: LocalDate): Alerta?
    suspend fun verificarMarraAptaCobertura(femea: Femea, hoje: LocalDate): Alerta?
    suspend fun verificarCoberturaPrecoce(femea: Femea, dataCobertura: LocalDate): List<Alerta>
    suspend fun verificarDesmamePrecoce(femea: Femea, diasLactacao: Int): Alerta?
    suspend fun verificarNatimortos(femea: Femea, natimortos: Int, totalNascidos: Int): Alerta?
    suspend fun verificarIdeLongo(femea: Femea, ide: Int): Alerta?
    suspend fun verificarIdePrimipara(femea: Femea, ide: Int): Alerta?
    suspend fun verificarEccDesmame(femea: Femea, ecc: Int): Alerta?
    suspend fun verificarEccParto(femea: Femea, ecc: Int): Alerta?
    suspend fun verificarEccReprodutorSobreCondicionado(reprodutor: Reprodutor, ecc: Int): Alerta?
    suspend fun verificarPerdaPesoPrimipara(femea: Femea, percentualPerda: Double): Alerta?
    suspend fun verificarPerdaPesoMultipara(femea: Femea, percentualPerda: Double): Alerta?
    suspend fun verificarReprodutorSemUso(reprodutor: Reprodutor, diasSemUso: Int): Alerta?
    suspend fun verificarReprodutorSuperutilizado(reprodutor: Reprodutor, coberturasNaSemana: Int): Alerta?
    suspend fun verificarReprodutorTreinamento(reprodutor: Reprodutor, coberturasNaSemana: Int): Alerta?
    suspend fun verificarDiagnosticoPendente(femea: Femea, diasSemDiagnostico: Int): Alerta?
    suspend fun verificarFemeaVazia(femea: Femea): Alerta?
    suspend fun verificarRepeticaoCioTripla(femea: Femea, totalRepeticoes: Int): Alerta?
    suspend fun verificarTaxaRepeticaoPlantel(taxa: Double): Alerta?
    suspend fun verificarRepeticaoIrregularDoenca(femea: Femea, intervalo: Int): List<Alerta>
    suspend fun verificarDistribuicaoParidade(distribuicao: Map<Int, Int>, totalAtivas: Int): Alerta?
    suspend fun verificarMetaCoberturasSemanal(realizadas: Int, meta: Double): Alerta?
    suspend fun verificarAcaoPendente(femea: Femea, tipo: TipoEvento, diasAtraso: Int): Alerta?
    suspend fun verificarConcentracaoPartos(datasParto: List<LocalDate>, capacidadeMaternidade: Int): List<Alerta>
    suspend fun verificarDesgastePrimipara(femea: Femea, percentualPerda: Double): Alerta?
    suspend fun verificarQuedaDesempenho2Parto(femea: Femea, nascidosVivos1Parto: Int, nascidosVivos2Parto: Int): Alerta?
}
```

### Distribuição Ideal de Paridades

```kotlin
// domain/model/DistribuicaoParidade.kt
object DistribuicaoIdealParidade {
    // Req 8.5 — distribuição alvo
    val ALVO = mapOf(
        0 to 0.15,   // marrãs (paridade 0)
        1 to 0.18,   // 1º parto
        2 to 0.1225, // 2º parto  ─┐
        3 to 0.1225, // 3º parto   │ ~49% entre 2º e 5º
        4 to 0.1225, // 4º parto   │
        5 to 0.1225, // 5º parto  ─┘
        6 to 0.18    // 6º parto em diante
    )

    fun calcularDesvio(distribuicaoAtual: Map<Int, Int>, totalAtivas: Int): Map<Int, Double> {
        if (totalAtivas == 0) return emptyMap()
        return ALVO.mapValues { (paridade, alvo) ->
            val atual = (distribuicaoAtual[paridade] ?: 0).toDouble() / totalAtivas
            atual - alvo
        }
    }

    fun percentualNoPicoReprodutivo(distribuicaoAtual: Map<Int, Int>, totalAtivas: Int): Double {
        if (totalAtivas == 0) return 0.0
        val noPico = (2..5).sumOf { distribuicaoAtual[it] ?: 0 }
        return noPico.toDouble() / totalAtivas
    }
}
```

---

## Camada de Apresentação

### Telas e ViewModels

```
presentation/
  plantel/
    screen/   PlantelScreen.kt
    viewmodel/ PlantelViewModel.kt
  femea/
    screen/   CadastroFemeaScreen.kt
              FichaFemeaScreen.kt
              RegistrarEventoScreen.kt
    viewmodel/ CadastroFemeaViewModel.kt
              FichaFemeaViewModel.kt
              RegistrarEventoViewModel.kt
  reprodutor/
    screen/   CadastroReprodutorScreen.kt
              FichaReprodutorScreen.kt
    viewmodel/ CadastroReprodutorViewModel.kt
              FichaReprodutorViewModel.kt
  calendario/
    screen/   CalendarioScreen.kt
    viewmodel/ CalendarioViewModel.kt
  relatorio/
    screen/   RelatorioScreen.kt
    viewmodel/ RelatorioViewModel.kt
```

### Estados de UI

```kotlin
// presentation/plantel/viewmodel/PlantelViewModel.kt
data class PlantelUiState(
    val indicadores: IndicadoresPlantel? = null,
    val distribuicaoParidade: Map<Int, Int> = emptyMap(),
    val alertasPendentes: List<Alerta> = emptyList(),
    val femeasPorStatus: Map<StatusFemea, List<Femea>> = emptyMap(),
    val isLoading: Boolean = false,
    val erro: String? = null
)

// presentation/femea/viewmodel/FichaFemeaViewModel.kt
data class FichaFemeaUiState(
    val femea: Femea? = null,
    val eventos: List<EventoReprodutivo> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val datasCalculadas: DatasCalculadas? = null,
    val idadeFormatada: String = "",
    val faseGestacional: FaseGestacional? = null,
    val diasGestacao: Int? = null,
    val indicadorRisco2Parto: Boolean = false,
    val isLoading: Boolean = false,
    val erro: String? = null
)

// presentation/femea/viewmodel/RegistrarEventoViewModel.kt
data class RegistrarEventoUiState(
    val tipoEvento: TipoEvento? = null,
    val datasCalculadas: DatasCalculadas? = null,
    val alertasGerados: List<Alerta> = emptyList(),
    val sucesso: Boolean = false,
    val isLoading: Boolean = false,
    val erro: String? = null
)

// presentation/calendario/viewmodel/CalendarioViewModel.kt
data class CalendarioUiState(
    val eventosPorData: Map<LocalDate, List<EventoCalendario>> = emptyMap(),
    val dataSelecionada: LocalDate = LocalDate.now(),
    val modoVisualizacao: ModoVisualizacaoCalendario = ModoVisualizacaoCalendario.SEMANA,
    val isLoading: Boolean = false
)
enum class ModoVisualizacaoCalendario { DIA, SEMANA, MES }
```

### Componentes UI Reutilizáveis

```
ui/component/
  AlertaCard.kt          — card de alerta com cor por prioridade
  FemeaListItem.kt       — item de lista com status badge
  EccSelector.kt         — seletor visual de ECC 1–5
  StatusBadge.kt         — chip colorido por StatusFemea
  DistribuicaoParidadeChart.kt — gráfico de barras paridade atual vs. ideal
  IndicadorKpiCard.kt    — card de KPI com valor e meta
  EventoTimelineItem.kt  — item de linha do tempo de eventos
  DataPickerField.kt     — campo de data com DatePicker M3
  CalendarioEventoDot.kt — marcador de evento no calendário
```

---

## Navegação

```kotlin
// Rotas de navegação
sealed class Rota(val caminho: String) {
    object Plantel : Rota("plantel")
    object CadastroFemea : Rota("femea/cadastro")
    data class FichaFemea(val femeaId: Long) : Rota("femea/{femeaId}") {
        companion object { const val TEMPLATE = "femea/{femeaId}" }
    }
    data class RegistrarEvento(val femeaId: Long, val tipoEvento: String) :
        Rota("femea/{femeaId}/evento/{tipoEvento}") {
        companion object { const val TEMPLATE = "femea/{femeaId}/evento/{tipoEvento}" }
    }
    object CadastroReprodutorRota : Rota("reprodutor/cadastro")
    data class FichaReprodutorRota(val reprodutorId: Long) : Rota("reprodutor/{reprodutorId}") {
        companion object { const val TEMPLATE = "reprodutor/{reprodutorId}" }
    }
    object Calendario : Rota("calendario")
    object Relatorio : Rota("relatorio")
}
```

```
Painel do Plantel (início)
  ├── Lista de Fêmeas → Ficha da Fêmea
  │     ├── Registrar Evento (CIO, COBERTURA, DIAGNÓSTICO, PARTO, DESMAME, ECC, DESCARTE)
  │     └── Histórico de Alertas
  ├── Cadastro de Fêmea (novo)
  ├── Lista de Reprodutores → Ficha do Reprodutor
  │     └── Cadastro de Reprodutor (novo)
  ├── Calendário de Manejo
  └── Relatórios
```

---

## Tratamento de Erros

| Cenário | Estratégia |
|---|---|
| Identificação duplicada (fêmea ou reprodutor) | `Result.Failure` com mensagem localizada; ViewModel expõe `erro: String?` no UiState |
| Cobertura em fêmea descartada | Validação no UseCase; retorna erro antes de persistir |
| Descarte de fêmea gestante/lactante | UseCase retorna `ConfirmacaoNecessaria`; ViewModel exibe diálogo de confirmação |
| Evento com data no futuro | Validação no UseCase; data não pode ser posterior a `LocalDate.now()` |
| Banco de dados corrompido | Room lança exceção; ViewModel captura e expõe mensagem genérica de erro |
| Cálculo com divisão por zero | `require()` nos métodos da `CalculadoraReprodutiva`; UseCase trata `IllegalArgumentException` |
| Geração de PDF > 10s | Timeout com `withTimeout`; exibe mensagem de erro ao usuário |

Todos os casos de uso retornam `Result<T>` (sealed class) para separar sucesso de falha sem exceções não tratadas:

```kotlin
sealed class ResultadoOperacao<out T> {
    data class Sucesso<T>(val dados: T) : ResultadoOperacao<T>()
    data class Erro(val mensagem: String, val causa: Throwable? = null) : ResultadoOperacao<Nothing>()
    object ConfirmacaoNecessaria : ResultadoOperacao<Nothing>()
}
```

---

## Estratégia de Testes

### Testes Unitários (Use Cases e CalculadoraReprodutiva)
- Cobrem toda a lógica de negócio pura com JUnit 5 + Kotlin Test
- `CalculadoraReprodutiva` é testada exaustivamente por ser stateless e pura
- Use cases são testados com repositórios fake (implementações in-memory)
- `MotorDeAlertas` é testado verificando quais alertas são gerados para cada combinação de entrada

### Testes de Integração (Repositórios)
- Room in-memory database (`Room.inMemoryDatabaseBuilder`)
- Verificam mapeamento entity↔domain, queries complexas e constraints de FK
- Executados como testes instrumentados no Android

### Testes de Propriedade (Property-Based Testing)
- Biblioteca: **Kotest** com módulo `kotest-property` (100+ iterações por propriedade)
- Geradores customizados para `LocalDate`, `Femea`, `EventoReprodutivo`
- Cada teste referencia a propriedade do design com tag: `// Feature: gestao-plantel-matrizes-reprodutores, Property N: <texto>`

### Testes de UI (Compose)
- Cobrem jornadas críticas: cadastro de fêmea, registro de cobertura, visualização do painel
- Usam `ComposeTestRule` com fakes injetados via Hilt test


---

## Propriedades de Correção

*Uma propriedade é uma característica ou comportamento que deve ser verdadeiro em todas as execuções válidas de um sistema — essencialmente, uma declaração formal sobre o que o sistema deve fazer. As propriedades servem como ponte entre especificações legíveis por humanos e garantias de correção verificáveis por máquina.*

### Propriedade 1: Validação de campos obrigatórios no cadastro

*Para qualquer* conjunto de dados de entrada para cadastro de fêmea ou reprodutor, a operação de cadastro deve ser rejeitada se e somente se ao menos um campo obrigatório (identificação, data de nascimento, raça/linhagem, categoria/tipo de uso) estiver ausente ou inválido. Inversamente, qualquer entrada com todos os campos obrigatórios preenchidos e válidos deve ser aceita independentemente dos valores dos campos opcionais.

**Valida: Requisitos 1.1, 1.2, 2.1**

---

### Propriedade 2: Unicidade de identificação no plantel

*Para qualquer* string de identificação, tentar cadastrar dois animais (fêmeas ou reprodutores) com a mesma identificação deve sempre resultar na rejeição do segundo cadastro, independentemente dos demais campos fornecidos.

**Valida: Requisitos 1.4, 2.7**

---

### Propriedade 3: Formatação de idade no padrão "XaYmZd"

*Para qualquer* par de datas (dataNascimento, dataReferencia) onde dataNascimento ≤ dataReferencia, a função `formatarIdade` deve produzir uma string no formato `\d+a\d+m\d+d` cujos valores de anos, meses e dias correspondem exatamente ao período entre as duas datas, e a soma dos componentes deve ser matematicamente consistente com a diferença total de dias.

**Valida: Requisito 1.3**

---

### Propriedade 4: Cálculo de datas reprodutivas a partir da cobertura

*Para qualquer* data de cobertura válida, as quatro datas calculadas devem satisfazer invariavelmente: dataParto = dataCobertura + 114 dias, retornoCioInicio = dataCobertura + 18 dias, retornoCioFim = dataCobertura + 24 dias, dataDiagnostico = dataCobertura + 30 dias. Nenhuma variação de entrada deve alterar esses offsets fixos.

**Valida: Requisito 3.3**

---

### Propriedade 5: Cálculo do IDE (Intervalo Desmame-Cobertura)

*Para qualquer* par (dataDesmame, dataProximaCobertura) onde dataProximaCobertura ≥ dataDesmame, o IDE calculado deve ser exatamente igual ao número de dias entre as duas datas, sem arredondamento ou truncamento.

**Valida: Requisito 3.4**

---

### Propriedade 6: Alertas de limiar do IDE

*Para qualquer* valor de IDE calculado, o alerta de "IDE longo" deve ser gerado se e somente se IDE > 7 dias. O alerta de "IDE curto em primípara" deve ser gerado se e somente se a fêmea tem paridade 1 e IDE < 11 dias. Nenhum alerta deve ser gerado para valores dentro dos limites aceitáveis.

**Valida: Requisitos 3.5, 11.2**

---

### Propriedade 7: Classificação de repetição de cio

*Para qualquer* par (dataCobertura, dataRetorno), o intervalo em dias deve ser classificado como: REGULAR se e somente se o intervalo está em [17, 24]; IRREGULAR se e somente se o intervalo está em [25, 60]; FORA_DO_PADRAO caso contrário. A classificação deve ser determinística e cobrir todos os inteiros positivos sem sobreposição ou lacuna entre as categorias.

**Valida: Requisito 7.1**

---

### Propriedade 8: Alertas de limiar de natimortos

*Para qualquer* par (natimortos, totalNascidos) com totalNascidos > 0, o alerta de natimortos acima do alvo deve ser gerado se e somente se natimortos / totalNascidos > 0,04. Para qualquer razão ≤ 0,04, nenhum alerta deve ser gerado.

**Valida: Requisito 3.8**

---

### Propriedade 9: Alertas de limiar de ECC

*Para qualquer* valor de ECC registrado em um evento de desmame ou parto, os alertas devem obedecer: no desmame, alerta gerado iff ECC < 3; no parto, alerta gerado iff ECC ∉ {3, 4}. Para reprodutores, alerta gerado iff ECC > 4. Os alertas devem ser mutuamente exclusivos com suas condições negadas.

**Valida: Requisitos 5.3, 5.4, 5.5**

---

### Propriedade 10: Alertas de perda de peso por categoria de fêmea

*Para qualquer* par (pesoParto, pesoDesmame) com pesoParto > 0, o percentual de perda é (pesoParto − pesoDesmame) / pesoParto. O alerta deve ser gerado se e somente se: para primíparas (paridade = 1), percentual > 0,09; para multíparas (paridade ≥ 2), percentual > 0,15. O limiar correto deve ser aplicado com base exclusivamente na paridade da fêmea.

**Valida: Requisitos 5.6, 5.7**

---

### Propriedade 11: Ração diária do reprodutor (1% do peso vivo)

*Para qualquer* peso positivo de reprodutor em kg, a ração diária recomendada calculada deve ser exatamente igual a pesoKg × 0,01, sem arredondamento. A relação deve ser linear e preservada para qualquer valor positivo de entrada.

**Valida: Requisito 2.3**

---

### Propriedade 12: Alertas de utilização do reprodutor

*Para qualquer* reprodutor e contagem de coberturas na semana: se o reprodutor tem idade > 300 dias e coberturasNaSemana > 6, o alerta de superutilização deve ser gerado; se o reprodutor tem idade entre 210 e 300 dias e coberturasNaSemana > 1, o alerta de treinamento deve ser gerado; se diasSemUso > 15, o alerta de subutilização deve ser gerado. Nenhum alerta deve ser gerado quando os valores estão dentro dos limites aceitáveis.

**Valida: Requisitos 2.4, 2.5, 2.6**

---

### Propriedade 13: Condição de aptidão da marrã para cobertura

*Para qualquer* combinação de (idadeDias, pesoKg, numCiosDetectados), o alerta "Marrã apta para primeira cobertura" deve ser gerado se e somente se as três condições são satisfeitas simultaneamente: idadeDias ≥ 210 E pesoKg ≥ 140 E numCiosDetectados ≥ 2. Para qualquer combinação em que ao menos uma condição não seja satisfeita, o alerta não deve ser gerado.

**Valida: Requisito 4.3**

---

### Propriedade 14: Fórmula da meta semanal de coberturas

*Para qualquer* tripla (totalFemeas > 0, partosPorFemeaAno > 0, taxaFertilidade ∈ (0, 1]), a meta semanal de coberturas calculada deve ser exatamente igual a (totalFemeas × partosPorFemeaAno) / 52 / taxaFertilidade. A fórmula deve ser monotonicamente crescente em totalFemeas e partosPorFemeaAno, e monotonicamente decrescente em taxaFertilidade.

**Valida: Requisito 9.6**

---

### Propriedade 15: Fórmula da taxa de reposição anualizada

*Para qualquer* tripla (totalDescartes ≥ 0, totalFemeasAtivas > 0, diasNoPeriodo > 0), a taxa de reposição anualizada calculada deve ser exatamente igual a (totalDescartes / totalFemeasAtivas) × (365 / diasNoPeriodo). A taxa deve ser zero quando não há descartes e deve escalar linearmente com o número de descartes.

**Valida: Requisito 8.4**

---

### Propriedade 16: Consistência da distribuição de paridades

*Para qualquer* conjunto de fêmeas ativas com paridades registradas, a soma dos percentuais de todas as faixas de paridade deve ser exatamente 1,0 (100%), e o percentual de cada faixa deve ser igual ao número de fêmeas naquela faixa dividido pelo total de fêmeas ativas. A distribuição deve ser invariante à ordem de inserção das fêmeas.

**Valida: Requisito 8.5**

