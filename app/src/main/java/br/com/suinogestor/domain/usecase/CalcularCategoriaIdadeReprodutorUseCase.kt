package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Use case para calcular a categoria de idade de um reprodutor baseada em dias de vida.
 *
 * Categorias:
 * - IMATURO: < 210 dias - não deve ser utilizado
 * - TREINAMENTO: 210-300 dias - máximo 1 cobertura por semana
 * - ADULTO: 300+ dias - máximo 6 coberturas por semana
 *
 * @param dataNascimento Data de nascimento do reprodutor
 * @param dataReferencia Data de referência para o cálculo (padrão: data atual)
 * @return Categoria de idade do reprodutor
 */
class CalcularCategoriaIdadeReprodutorUseCase @Inject constructor() {
    operator fun invoke(
        dataNascimento: LocalDate,
        dataReferencia: LocalDate = LocalDate.now()
    ): CategoriaIdadeReprodutor {
        val idadeDias = ChronoUnit.DAYS.between(dataNascimento, dataReferencia)
        
        return when {
            idadeDias < 210 -> CategoriaIdadeReprodutor.IMATURO
            idadeDias < 300 -> CategoriaIdadeReprodutor.TREINAMENTO
            else -> CategoriaIdadeReprodutor.ADULTO
        }
    }
}
