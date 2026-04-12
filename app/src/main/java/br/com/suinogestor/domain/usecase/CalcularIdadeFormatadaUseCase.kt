package br.com.suinogestor.domain.usecase

import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class CalcularIdadeFormatadaUseCase @Inject constructor() {
    operator fun invoke(dataNascimento: LocalDate, dataReferencia: LocalDate = LocalDate.now()): String {
        val periodo = Period.between(dataNascimento, dataReferencia)
        return "${periodo.years}a${periodo.months}m${periodo.days}d"
    }
}
