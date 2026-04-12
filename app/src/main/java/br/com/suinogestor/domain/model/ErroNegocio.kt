package br.com.suinogestor.domain.model

sealed class ErroNegocio {
    data class CampoObrigatorio(val campo: String) : ErroNegocio()
    data class IdentificacaoDuplicada(val mensagem: String) : ErroNegocio()
    object EccForaDoIntervalo : ErroNegocio()
}
