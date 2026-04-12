package br.com.suinogestor.domain.model

sealed class ResultadoOperacao<out T> {
    data class Sucesso<T>(val dados: T) : ResultadoOperacao<T>()
    data class Erro(val erro: ErroNegocio) : ResultadoOperacao<Nothing>()
}
