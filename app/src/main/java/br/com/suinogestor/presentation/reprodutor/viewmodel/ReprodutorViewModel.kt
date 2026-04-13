package br.com.suinogestor.presentation.reprodutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.usecase.CadastrarReprodutorComando
import br.com.suinogestor.domain.usecase.CadastrarReprodutorUseCase
import br.com.suinogestor.domain.usecase.CalcularCategoriaIdadeReprodutorUseCase
import br.com.suinogestor.domain.usecase.CalcularRacaoDiariaUseCase
import br.com.suinogestor.domain.usecase.ListarReprodutoresUseCase
import br.com.suinogestor.domain.usecase.ObterReprodutorUseCase
import br.com.suinogestor.domain.usecase.ReprodutorDetalhes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel para gerenciar o estado e operações relacionadas a reprodutores.
 *
 * Responsabilidades:
 * - Expor lista reativa de reprodutores via StateFlow
 * - Gerenciar estado da UI (Loading, Success, Error)
 * - Executar cadastro de novos reprodutores
 * - Calcular ração diária recomendada
 * - Filtrar lista com busca debounced
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 *
 * @property cadastrarReprodutorUseCase Use case para cadastrar novo reprodutor
 * @property listarReprodutoresUseCase Use case para listar reprodutores
 * @property calcularRacaoDiariaUseCase Use case para calcular ração diária
 * @property calcularCategoriaIdadeUseCase Use case para calcular categoria de idade
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class ReprodutorViewModel @Inject constructor(
    private val cadastrarReprodutorUseCase: CadastrarReprodutorUseCase,
    private val listarReprodutoresUseCase: ListarReprodutoresUseCase,
    private val calcularRacaoDiariaUseCase: CalcularRacaoDiariaUseCase,
    private val calcularCategoriaIdadeUseCase: CalcularCategoriaIdadeReprodutorUseCase,
    private val obterReprodutorUseCase: ObterReprodutorUseCase
) : ViewModel() {

    private val _listaState = MutableStateFlow(ReprodutorListaUiState())
    /**
     * Estado da lista de reprodutores com suporte a busca e filtros.
     */
    val listaState: StateFlow<ReprodutorListaUiState> = _listaState.asStateFlow()

    private val _uiState = MutableStateFlow<ReprodutorUiState>(ReprodutorUiState.Idle)
    /**
     * Estado atual da UI para operações de cadastro.
     * Gerencia estados de loading, sucesso e erro.
     */
    val uiState: StateFlow<ReprodutorUiState> = _uiState.asStateFlow()

    private val _termoBusca = MutableStateFlow("")

    init {
        carregarReprodutores()
        observarBusca()
    }

    /**
     * Carrega a lista de reprodutores do repositório de forma reativa.
     * Atualiza o StateFlow sempre que houver mudanças no banco.
     */
    private fun carregarReprodutores() {
        viewModelScope.launch {
            listarReprodutoresUseCase()
                .map { lista ->
                    lista.map { reprodutor ->
                        val idadeDias = ChronoUnit.DAYS.between(
                            reprodutor.dataNascimento,
                            java.time.LocalDate.now()
                        )
                        val categoria = calcularCategoriaIdadeUseCase(reprodutor.dataNascimento)
                        val racaoDiaria = reprodutor.pesoAtualKg?.let { 
                            calcularRacaoDiariaUseCase(it) 
                        }
                        
                        ReprodutorResumo(
                            id = reprodutor.id,
                            identificacao = reprodutor.identificacao,
                            racaLinhagem = reprodutor.racaLinhagem,
                            categoria = categoria,
                            idadeDias = idadeDias.toInt(),
                            pesoAtualKg = reprodutor.pesoAtualKg,
                            eccAtual = reprodutor.eccAtual,
                            racaoDiariaKg = racaoDiaria,
                            diasSemUso = null // TODO: implementar quando módulo de coberturas estiver pronto
                        )
                    }
                }
                .catch { e -> 
                    _listaState.update { it.copy(erro = e.message, carregando = false) } 
                }
                .collect { resumos ->
                    _listaState.update { state ->
                        state.copy(
                            reprodutores = resumos,
                            carregando = false,
                            reprodutoresFiltrados = if (state.termoBusca.isBlank()) resumos
                            else resumos.filter {
                                it.identificacao.contains(state.termoBusca, ignoreCase = true)
                            }
                        )
                    }
                }
        }
    }

    /**
     * Observa mudanças no termo de busca com debounce de 300ms.
     */
    private fun observarBusca() {
        viewModelScope.launch {
            _termoBusca
                .debounce(300)
                .collect { termo ->
                    _listaState.update { state ->
                        state.copy(
                            termoBusca = termo,
                            reprodutoresFiltrados = if (termo.isBlank()) state.reprodutores
                            else state.reprodutores.filter {
                                it.identificacao.contains(termo, ignoreCase = true)
                            }
                        )
                    }
                }
        }
    }

    /**
     * Atualiza o termo de busca.
     * A filtragem é aplicada automaticamente com debounce de 300ms.
     */
    fun atualizarBusca(termo: String) {
        _termoBusca.value = termo
    }

    /**
     * Cadastra um novo reprodutor no plantel.
     *
     * Executa validações de negócio via use case e atualiza o estado da UI
     * conforme o resultado da operação (sucesso ou erro).
     *
     * @param comando Dados do reprodutor a ser cadastrado
     */
    fun cadastrar(comando: CadastrarReprodutorComando) {
        viewModelScope.launch {
            _uiState.value = ReprodutorUiState.Loading

            when (val resultado = cadastrarReprodutorUseCase(comando)) {
                is ResultadoOperacao.Sucesso -> {
                    _uiState.value = ReprodutorUiState.Success(
                        "Reprodutor ${comando.identificacao} cadastrado com sucesso!"
                    )
                }
                is ResultadoOperacao.Erro -> {
                    val mensagemErro = when (val erro = resultado.erro) {
                        is ErroNegocio.IdentificacaoDuplicada -> erro.mensagem
                        is ErroNegocio.CampoObrigatorio -> "Campo obrigatório: ${erro.campo}"
                        ErroNegocio.EccForaDoIntervalo -> "ECC deve estar entre 1 e 5"
                        is ErroNegocio.ErroInterno -> erro.mensagem
                    }
                    _uiState.value = ReprodutorUiState.Error(mensagemErro)
                }
            }
        }
    }

    /**
     * Calcula a quantidade diária de ração recomendada para um reprodutor.
     *
     * Utiliza a fórmula: ração diária (kg) = peso (kg) × 0.01 (1% do peso vivo)
     *
     * @param pesoKg Peso do reprodutor em quilogramas
     * @return Quantidade de ração diária em kg, ou 0.0 se o peso for inválido
     */
    fun calcularRacaoDiaria(pesoKg: Double): Double {
        return try {
            calcularRacaoDiariaUseCase(pesoKg)
        } catch (e: IllegalArgumentException) {
            0.0
        }
    }

    /**
     * Reseta o estado da UI para Idle.
     * Útil após exibir mensagens de sucesso ou erro.
     */
    fun resetarEstado() {
        _uiState.value = ReprodutorUiState.Idle
    }

    /**
     * Obtém os detalhes completos de um reprodutor por ID.
     *
     * Retorna um StateFlow que emite o estado de carregamento dos detalhes,
     * incluindo dados calculados como idade, categoria e ração diária.
     *
     * @param reprodutorId ID do reprodutor
     * @return StateFlow com o estado dos detalhes
     */
    fun obterDetalhes(reprodutorId: Long): StateFlow<ReprodutorDetalhesUiState> {
        val detalhesState = MutableStateFlow<ReprodutorDetalhesUiState>(
            ReprodutorDetalhesUiState.Loading
        )
        
        viewModelScope.launch {
            try {
                val detalhes = obterReprodutorUseCase(reprodutorId)
                if (detalhes != null) {
                    detalhesState.value = ReprodutorDetalhesUiState.Success(detalhes)
                } else {
                    detalhesState.value = ReprodutorDetalhesUiState.Error(
                        "Reprodutor não encontrado"
                    )
                }
            } catch (e: Exception) {
                detalhesState.value = ReprodutorDetalhesUiState.Error(
                    e.message ?: "Erro ao carregar detalhes"
                )
            }
        }
        
        return detalhesState.asStateFlow()
    }
}
