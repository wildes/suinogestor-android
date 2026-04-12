package br.com.suinogestor.presentation.femea.screen

object Rotas {
    const val LISTA_FEMEAS = "femeas"
    const val CADASTRO_FEMEA = "cadastro-femea"
    const val DETALHE_FEMEA = "femeas/{femeaId}"

    fun detalheFemea(femeaId: Long) = "femeas/$femeaId"
}
