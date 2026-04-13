package br.com.suinogestor.domain.usecase

import javax.inject.Inject

/**
 * Use case para calcular a quantidade diária de ração recomendada para um reprodutor.
 *
 * A fórmula utilizada é: ração diária (kg) = peso (kg) × 0.01 (1% do peso vivo)
 *
 * @throws IllegalArgumentException se o peso for menor ou igual a zero
 */
class CalcularRacaoDiariaUseCase @Inject constructor() {
    operator fun invoke(pesoKg: Double): Double {
        require(pesoKg > 0) { "Peso deve ser maior que zero" }
        return pesoKg * 0.01
    }
}
