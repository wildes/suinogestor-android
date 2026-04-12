package br.com.suinogestor.presentation.femea.viewmodel

import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.StatusFemea

data class FemeaUiState(
    val carregando: Boolean = false,
    val femeas: List<FemeaResumo> = emptyList(),
    val erro: String? = null,
    val termoBusca: String = "",
    val femeasFiltradas: List<FemeaResumo> = emptyList()
)

data class FemeaResumo(
    val id: Long,
    val identificacao: String,
    val idadeFormatada: String,
    val categoria: CategoriaFemea,
    val racaLinhagem: String,
    val status: StatusFemea
)
