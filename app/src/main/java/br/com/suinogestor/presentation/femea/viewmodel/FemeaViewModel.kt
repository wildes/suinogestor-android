package br.com.suinogestor.presentation.femea.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.usecase.CadastrarFemeaComando
import br.com.suinogestor.domain.usecase.CadastrarFemeaUseCase
import br.com.suinogestor.domain.usecase.CalcularIdadeFormatadaUseCase
import br.com.suinogestor.domain.usecase.ListarFemeasUseCase
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
import java.time.LocalDate
import javax.inject.Inject

@OptIn(FlowPreview::class)
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

    private val _termoBusca = MutableStateFlow("")

    init {
        carregarFemeas()
        observarBusca()
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
                    _listaState.update { state ->
                        state.copy(
                            femeas = resumos,
                            carregando = false,
                            femeasFiltradas = if (state.termoBusca.isBlank()) resumos
                            else resumos.filter {
                                it.identificacao.contains(state.termoBusca, ignoreCase = true)
                            }
                        )
                    }
                }
        }
    }

    private fun observarBusca() {
        viewModelScope.launch {
            _termoBusca
                .debounce(300)
                .collect { termo ->
                    _listaState.update { state ->
                        state.copy(
                            termoBusca = termo,
                            femeasFiltradas = if (termo.isBlank()) state.femeas
                            else state.femeas.filter {
                                it.identificacao.contains(termo, ignoreCase = true)
                            }
                        )
                    }
                }
        }
    }

    fun atualizarBusca(termo: String) {
        _termoBusca.value = termo
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
                    _cadastroState.update { it.copy(salvando = false, sucesso = true) }
                }
                is ResultadoOperacao.Erro -> {
                    val state = when (val erro = resultado.erro) {
                        is ErroNegocio.IdentificacaoDuplicada ->
                            _cadastroState.value.copy(salvando = false, erroIdentificacao = erro.mensagem)
                        is ErroNegocio.CampoObrigatorio ->
                            _cadastroState.value.copy(salvando = false, erroGeral = "Campo obrigatório: ${erro.campo}")
                        ErroNegocio.EccForaDoIntervalo ->
                            _cadastroState.value.copy(salvando = false, erroEcc = "ECC deve estar entre 1 e 5")
                    }
                    _cadastroState.value = state
                }
            }
        }
    }
}
