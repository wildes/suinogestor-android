package br.com.suinogestor.domain.model

import java.time.LocalDate

data class Femea(
    val id: Long = 0,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val categoria: CategoriaFemea,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: OrigemAnimal?,
    val fotoUri: String?,
    val status: StatusFemea,
    val paridade: Int = 0,
    val dataEntrada: LocalDate,
    val ativo: Boolean = true
)
