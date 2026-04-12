package br.com.suinogestor.presentation.femea.viewmodel

import br.com.suinogestor.domain.model.Alerta
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Femea
import br.com.suinogestor.domain.model.OrigemAnimal
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.model.StatusFemea
import br.com.suinogestor.domain.repository.AlertaRepository
import br.com.suinogestor.domain.repository.FemeaRepository
import br.com.suinogestor.domain.usecase.CadastrarFemeaComando
import br.com.suinogestor.domain.usecase.CadastrarFemeaUseCase
import br.com.suinogestor.domain.usecase.CalcularIdadeFormatadaUseCase
import br.com.suinogestor.domain.usecase.ListarFemeasUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// --- Fake repositories used to wire real use cases ---

private class FakeFemeaRepositoryForViewModel(
    private val femeasIniciais: List<Femea> = emptyList(),
    private val resultadoBusca: Femea? = null
) : FemeaRepository {
    private val _flow = MutableStateFlow(femeasIniciais)
    private var nextId = 1L

    override fun observarTodasAtivas(): Flow<List<Femea>> = _flow
    override fun observarPorId(id: Long): Flow<Femea?> = flowOf(femeasIniciais.find { it.id == id })
    override suspend fun buscarPorIdentificacao(identificacao: String): Femea? = resultadoBusca
    override suspend fun salvar(femea: Femea): Long = nextId++
    override suspend fun atualizar(femea: Femea) {}
}

private class FakeAlertaRepositoryForViewModel : AlertaRepository {
    override fun observarNaoLidos(): Flow<List<Alerta>> = flowOf(emptyList())
    override fun observarPorFemea(femeaId: Long): Flow<List<Alerta>> = flowOf(emptyList())
    override suspend fun salvar(alerta: Alerta): Long = 0L
    override suspend fun marcarComoLido(id: Long) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class FemeaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun femea(id: Long, identificacao: String) = Femea(
        id = id,
        identificacao = identificacao,
        dataNascimento = LocalDate.now().minusDays(200),
        racaLinhagem = "Landrace",
        categoria = CategoriaFemea.MARRA,
        pesoEntradaKg = null,
        eccAtual = null,
        origem = OrigemAnimal.PROPRIA,
        fotoUri = null,
        status = StatusFemea.MARRA_PREPARACAO,
        dataEntrada = LocalDate.now()
    )

    private fun comando() = CadastrarFemeaComando(
        identificacao = "F-001",
        dataNascimento = LocalDate.now().minusDays(200),
        racaLinhagem = "Landrace",
        categoria = CategoriaFemea.MARRA,
        pesoEntradaKg = null,
        eccAtual = null,
        origem = null,
        fotoUri = null
    )

    private fun buildViewModel(
        femeasIniciais: List<Femea> = emptyList(),
        // null = no duplicate; non-null = duplicate found (triggers IdentificacaoDuplicada)
        duplicata: Femea? = null
    ): FemeaViewModel {
        val femeaRepo = FakeFemeaRepositoryForViewModel(femeasIniciais, duplicata)
        val alertaRepo = FakeAlertaRepositoryForViewModel()
        val calcularIdade = CalcularIdadeFormatadaUseCase()
        return FemeaViewModel(
            cadastrarFemea = CadastrarFemeaUseCase(femeaRepo, alertaRepo, calcularIdade),
            listarFemeas = ListarFemeasUseCase(femeaRepo),
            calcularIdade = calcularIdade
        )
    }

    @Test
    fun listaState_inicial_estaCarregando() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.listaState.value.carregando)
    }

    @Test
    fun listaState_expoeStateFlow_comFemeasCarregadas() = runTest {
        val femeasEsperadas = listOf(femea(1L, "F-001"), femea(2L, "F-002"))
        val viewModel = buildViewModel(femeasIniciais = femeasEsperadas)
        advanceUntilIdle()

        assertEquals(2, viewModel.listaState.value.femeas.size)
        assertEquals("F-001", viewModel.listaState.value.femeas[0].identificacao)
        assertEquals("F-002", viewModel.listaState.value.femeas[1].identificacao)
    }

    @Test
    fun cadastroState_aposSuccesso_sucesso_eTrue() = runTest {
        val viewModel = buildViewModel()

        viewModel.cadastrar(comando())
        advanceUntilIdle()

        assertTrue(viewModel.cadastroState.value.sucesso)
    }

    @Test
    fun cadastroState_aposIdentificacaoDuplicada_erroIdentificacao_naoEhNulo() = runTest {
        val duplicata = femea(1L, "F-001")
        val viewModel = buildViewModel(duplicata = duplicata)

        viewModel.cadastrar(comando())
        advanceUntilIdle()

        assertNotNull(viewModel.cadastroState.value.erroIdentificacao)
    }
}
