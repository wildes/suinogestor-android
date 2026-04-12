package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.OrigemAnimal
import java.time.LocalDate

data class CadastrarFemeaComando(
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val categoria: CategoriaFemea,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: OrigemAnimal?,
    val fotoUri: String?
)
