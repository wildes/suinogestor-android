package br.com.suinogestor.domain.model

import java.time.LocalDate

data class Alerta(
    val id: Long = 0,
    val femeaId: Long?,
    val tipo: TipoAlerta,
    val mensagem: String,
    val prioridade: PrioridadeAlerta,
    val dataGeracao: LocalDate,
    val dataLeitura: LocalDate?,
    val lido: Boolean = false
)
