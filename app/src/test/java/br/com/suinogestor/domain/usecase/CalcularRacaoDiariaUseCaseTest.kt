package br.com.suinogestor.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularRacaoDiariaUseCaseTest {

    private val useCase = CalcularRacaoDiariaUseCase()

    @Test
    fun calcularRacao_pesoValido_retornaUmPorCento() {
        val peso = 280.0

        val resultado = useCase(peso)

        assertEquals(2.8, resultado, 0.001)
    }

    @Test
    fun calcularRacao_pesoMinimo_retornaValorCorreto() {
        val peso = 80.0

        val resultado = useCase(peso)

        assertEquals(0.8, resultado, 0.001)
    }

    @Test
    fun calcularRacao_pesoMaximo_retornaValorCorreto() {
        val peso = 400.0

        val resultado = useCase(peso)

        assertEquals(4.0, resultado, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calcularRacao_pesoZero_lancaExcecao() {
        useCase(0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calcularRacao_pesoNegativo_lancaExcecao() {
        useCase(-10.0)
    }
}
