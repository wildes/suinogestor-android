package br.com.suinogestor.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalcularIdadeFormatadaUseCaseTest {

    private val useCase = CalcularIdadeFormatadaUseCase()

    @Test
    fun calcularIdade_retornaFormatoCorreto() {
        val dataNascimento = LocalDate.of(2023, 1, 15)
        val dataReferencia = LocalDate.of(2024, 4, 12)

        val resultado = useCase(dataNascimento, dataReferencia)

        assertEquals("1a2m28d", resultado)
    }

    @Test
    fun calcularIdade_mesmoDia_retornaZeros() {
        val data = LocalDate.of(2024, 6, 10)

        val resultado = useCase(data, data)

        assertEquals("0a0m0d", resultado)
    }
}
