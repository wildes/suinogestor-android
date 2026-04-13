package br.com.suinogestor.presentation.reprodutor.screen

/**
 * Definição de rotas de navegação para o módulo de reprodutores.
 *
 * Centraliza as strings de rota para evitar duplicação e facilitar
 * manutenção da navegação.
 */
object Rotas {
    const val LISTA_REPRODUTORES = "reprodutores"
    const val CADASTRO_REPRODUTOR = "cadastro-reprodutor"
    const val DETALHE_REPRODUTOR = "reprodutores/{reprodutorId}"

    fun detalheReprodutor(reprodutorId: Long) = "reprodutores/$reprodutorId"
}
