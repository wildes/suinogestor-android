package br.com.suinogestor.presentation.reprodutor.viewmodel

/**
 * Estado da UI para a tela de listagem de reprodutores.
 *
 * Gerencia estados de carregamento, erro, busca e filtragem
 * da lista de reprodutores.
 *
 * @property reprodutores Lista completa de reprodutores
 * @property reprodutoresFiltrados Lista filtrada pela busca
 * @property termoBusca Termo atual de busca
 * @property carregando Indica se está carregando dados
 * @property erro Mensagem de erro (null se não houver erro)
 */
data class ReprodutorListaUiState(
    val reprodutores: List<ReprodutorResumo> = emptyList(),
    val reprodutoresFiltrados: List<ReprodutorResumo> = emptyList(),
    val termoBusca: String = "",
    val carregando: Boolean = true,
    val erro: String? = null
)
