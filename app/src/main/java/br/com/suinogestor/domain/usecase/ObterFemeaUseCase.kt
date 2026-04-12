package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.Femea
import br.com.suinogestor.domain.repository.FemeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObterFemeaUseCase @Inject constructor(
    private val femeaRepository: FemeaRepository
) {
    operator fun invoke(id: Long): Flow<Femea?> = femeaRepository.observarPorId(id)
}
