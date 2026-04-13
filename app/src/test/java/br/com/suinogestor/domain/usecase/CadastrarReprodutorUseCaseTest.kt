package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.model.TipoUsoReprodutor
import br.com.suinogestor.domain.repository.ReprodutorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// --- Fake Repository ---

class FakeReprodutorRepository : ReprodutorRepository {
    val reprodutores = mutableListOf<Reprodutor>()
    private var nextId = 1L

    override suspend fun cadastrar(reprodutor: Reprodutor): ResultadoOperacao<Long> {
        val id = nextId++
        reprodutores.add(reprodutor.copy(id = id))
        return ResultadoOperacao.Sucesso(id)
    }

    override suspend fun atualizar(reprodutor: Reprodutor): ResultadoOperacao<Unit> {
        val idx = reprodutores.indexOfFirst { it.id == reprodutor.id }
        if (idx >= 0) {
            reprodutores[idx] = reprodutor
            return ResultadoOperacao.Sucesso(Unit)
        }
        return ResultadoOperacao.Erro(ErroNegocio.ErroInterno("Reprodutor não encontrado"))
    }

    override suspend fun buscarPorId(id: Long): Reprodutor? =
        reprodutores.find { it.id == id }

    override suspend fun buscarPorIdentificacao(identificacao: String): Reprodutor? =
        reprodutores.find { it.identificacao == identificacao }

    override fun observarTodos(): Flow<List<Reprodutor>> =
        flowOf(reprodutores.filter { it.ativo })

    override suspend fun contarCoberturasNaSemana(reprodutorId: Long, dataReferencia: LocalDate): Int = 0

    override suspend fun obterDataUltimaCobertura(reprodutorId: Long): LocalDate? = null
}

// --- Tests ---

class CadastrarReprodutorUseCaseTest {

    private lateinit var repository: FakeReprodutorRepository
    private lateinit var useCase: CadastrarReprodutorUseCase

    @Before
    fun setUp() {
        repository = FakeReprodutorRepository()
        useCase = CadastrarReprodutorUseCase(repository)
    }

    @Test
    fun cadastrar_comDadosValidos_persisteReprodutor() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = 280.0,
            eccAtual = 3
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Sucesso)
        assertEquals(1, repository.reprodutores.size)
        assertEquals("R-001", repository.reprodutores.first().identificacao)
        assertEquals("Duroc", repository.reprodutores.first().racaLinhagem)
        assertEquals(TipoUsoReprodutor.MONTA_NATURAL, repository.reprodutores.first().tipoUso)
    }

    @Test
    fun cadastrar_comIdentificacaoDuplicada_retornaErroIdentificacaoDuplicada() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = null,
            eccAtual = null
        )
        // Cadastrar uma vez para criar duplicata
        useCase(comando)

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertTrue((resultado as ResultadoOperacao.Erro).erro is ErroNegocio.IdentificacaoDuplicada)
    }

    @Test
    fun cadastrar_comIdentificacaoVazia_retornaErroCampoObrigatorio() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = null,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        val erro = (resultado as ResultadoOperacao.Erro).erro
        assertTrue(erro is ErroNegocio.CampoObrigatorio)
        assertEquals("identificacao", (erro as ErroNegocio.CampoObrigatorio).campo)
    }

    @Test
    fun cadastrar_comRacaLinhagemVazia_retornaErroCampoObrigatorio() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = null,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        val erro = (resultado as ResultadoOperacao.Erro).erro
        assertTrue(erro is ErroNegocio.CampoObrigatorio)
        assertEquals("racaLinhagem", (erro as ErroNegocio.CampoObrigatorio).campo)
    }

    @Test
    fun cadastrar_comDataNascimentoFutura_retornaErroInterno() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().plusDays(10),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = null,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertTrue((resultado as ResultadoOperacao.Erro).erro is ErroNegocio.ErroInterno)
    }

    @Test
    fun cadastrar_comEccForaDoIntervalo_retornaErroEcc() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = null,
            eccAtual = 6
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertEquals(ErroNegocio.EccForaDoIntervalo, (resultado as ResultadoOperacao.Erro).erro)
    }

    @Test
    fun cadastrar_comPesoNegativo_retornaErroInterno() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = -10.0,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertTrue((resultado as ResultadoOperacao.Erro).erro is ErroNegocio.ErroInterno)
    }

    @Test
    fun cadastrar_comPesoZero_retornaErroInterno() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-001",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Duroc",
            tipoUso = TipoUsoReprodutor.MONTA_NATURAL,
            pesoAtualKg = 0.0,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertTrue((resultado as ResultadoOperacao.Erro).erro is ErroNegocio.ErroInterno)
    }

    @Test
    fun cadastrar_comCamposOpcionaisNulos_persisteReprodutor() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "R-002",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "Landrace",
            tipoUso = TipoUsoReprodutor.COLETA_IA,
            pesoAtualKg = null,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Sucesso)
        assertEquals(1, repository.reprodutores.size)
        val reprodutor = repository.reprodutores.first()
        assertEquals("R-002", reprodutor.identificacao)
        assertEquals(null, reprodutor.pesoAtualKg)
        assertEquals(null, reprodutor.eccAtual)
    }

    @Test
    fun cadastrar_trimIdentificacaoEracaLinhagem() = runTest {
        val comando = CadastrarReprodutorComando(
            identificacao = "  R-003  ",
            dataNascimento = LocalDate.now().minusDays(400),
            racaLinhagem = "  Duroc  ",
            tipoUso = TipoUsoReprodutor.AMBOS,
            pesoAtualKg = null,
            eccAtual = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Sucesso)
        val reprodutor = repository.reprodutores.first()
        assertEquals("R-003", reprodutor.identificacao)
        assertEquals("Duroc", reprodutor.racaLinhagem)
    }
}
