package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.repository.ReprodutorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case para listar todos os reprodutores do plantel de forma reativa.
 *
 * Retorna um Flow que emite a lista atualizada de reprodutores sempre que houver
 * mudanças no banco de dados, permitindo UI reativa.
 *
 * **Validates: Requirements 2.1**
 */
class ListarReprodutoresUseCase @Inject constructor(
    private val repository: ReprodutorRepository
) {
    /**
     * Observa todos os reprodutores do plantel.
     *
     * @return Flow que emite lista de reprodutores sempre que houver mudanças
     */
    operator fun invoke(): Flow<List<Reprodutor>> {
        return repository.observarTodos()
    }
}
