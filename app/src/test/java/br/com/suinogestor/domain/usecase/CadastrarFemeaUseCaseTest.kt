package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.Alerta
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Femea
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.model.TipoAlerta
import br.com.suinogestor.domain.repository.AlertaRepository
import br.com.suinogestor.domain.repository.FemeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// --- Fakes ---

class FakeFemeaRepository : FemeaRepository {
    val femeas = mutableListOf<Femea>()
    private var nextId = 1L

    override fun observarTodasAtivas(): Flow<List<Femea>> = flowOf(femeas.filter { it.ativo })
    override fun observarPorId(id: Long): Flow<Femea?> = flowOf(femeas.find { it.id == id })
    override suspend fun buscarPorIdentificacao(identificacao: String): Femea? =
        femeas.find { it.identificacao == identificacao }
    override suspend fun salvar(femea: Femea): Long {
        val id = nextId++
        femeas.add(femea.copy(id = id))
        return id
    }
    override suspend fun atualizar(femea: Femea) {
        val idx = femeas.indexOfFirst { it.id == femea.id }
        if (idx >= 0) femeas[idx] = femea
    }
}

class FakeAlertaRepository : AlertaRepository {
    val alertas = mutableListOf<Alerta>()
    private var nextId = 1L

    override fun observarNaoLidos(): Flow<List<Alerta>> = flowOf(alertas.filter { !it.lido })
    override fun observarPorFemea(femeaId: Long): Flow<List<Alerta>> =
        flowOf(alertas.filter { it.femeaId == femeaId })
    override suspend fun salvar(alerta: Alerta): Long {
        val id = nextId++
        alertas.add(alerta.copy(id = id))
        return id
    }
    override suspend fun marcarComoLido(id: Long) {
        val idx = alertas.indexOfFirst { it.id == id }
        if (idx >= 0) alertas[idx] = alertas[idx].copy(lido = true)
    }
}

// --- Tests ---

class CadastrarFemeaUseCaseTest {

    private lateinit var femeaRepo: FakeFemeaRepository
    private lateinit var alertaRepo: FakeAlertaRepository
    private lateinit var useCase: CadastrarFemeaUseCase

    @Before
    fun setUp() {
        femeaRepo = FakeFemeaRepository()
        alertaRepo = FakeAlertaRepository()
        useCase = CadastrarFemeaUseCase(femeaRepo, alertaRepo, CalcularIdadeFormatadaUseCase())
    }

    @Test
    fun cadastrar_comDadosValidos_persisteFemea() = runTest {
        val comando = CadastrarFemeaComando(
            identificacao = "F-001",
            dataNascimento = LocalDate.now().minusDays(200),
            racaLinhagem = "Landrace",
            categoria = CategoriaFemea.MARRA,
            pesoEntradaKg = 90.0,
            eccAtual = 3,
            origem = null,
            fotoUri = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Sucesso)
        assertEquals(1, femeaRepo.femeas.size)
        assertEquals("F-001", femeaRepo.femeas.first().identificacao)
    }

    @Test
    fun cadastrar_comIdentificacaoDuplicada_retornaErroIdentificacaoDuplicada() = runTest {
        val comando = CadastrarFemeaComando(
            identificacao = "F-001",
            dataNascimento = LocalDate.now().minusDays(200),
            racaLinhagem = "Landrace",
            categoria = CategoriaFemea.MARRA,
            pesoEntradaKg = null,
            eccAtual = null,
            origem = null,
            fotoUri = null
        )
        // Persist once to create duplicate
        useCase(comando)

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertTrue((resultado as ResultadoOperacao.Erro).erro is ErroNegocio.IdentificacaoDuplicada)
    }

    @Test
    fun cadastrar_marraComMenos160Dias_geraAlertaIdadeMinima() = runTest {
        val hoje = LocalDate.now()
        val comando = CadastrarFemeaComando(
            identificacao = "F-002",
            dataNascimento = hoje.minusDays(100),
            racaLinhagem = "Duroc",
            categoria = CategoriaFemea.MARRA,
            pesoEntradaKg = null,
            eccAtual = null,
            origem = null,
            fotoUri = null
        )

        useCase(comando, hoje)

        val alertaGerado = alertaRepo.alertas.find { it.tipo == TipoAlerta.IDADE_MINIMA_PREPARACAO }
        assertNotNull("Alerta IDADE_MINIMA_PREPARACAO deve ser gerado", alertaGerado)
    }

    @Test
    fun cadastrar_comEccForaDoIntervalo_retornaErroEcc() = runTest {
        val comando = CadastrarFemeaComando(
            identificacao = "F-003",
            dataNascimento = LocalDate.now().minusDays(200),
            racaLinhagem = "Landrace",
            categoria = CategoriaFemea.MARRA,
            pesoEntradaKg = null,
            eccAtual = 6,
            origem = null,
            fotoUri = null
        )

        val resultado = useCase(comando)

        assertTrue(resultado is ResultadoOperacao.Erro)
        assertEquals(ErroNegocio.EccForaDoIntervalo, (resultado as ResultadoOperacao.Erro).erro)
    }
}
