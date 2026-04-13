package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.repository.ReprodutorRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Detalhes completos de um reprodutor incluindo dados calculados.
 *
 * @property reprodutor Dados base do reprodutor
 * @property idadeDias Idade em dias calculada a partir da data de nascimento
 * @property categoria Categoria de idade (IMATURO, TREINAMENTO ou ADULTO)
 * @property racaoDiariaKg Quantidade de ração diária recomendada em kg (null se peso não disponível)
 */
data class ReprodutorDetalhes(
    val reprodutor: Reprodutor,
    val idadeDias: Long,
    val categoria: CategoriaIdadeReprodutor,
    val racaoDiariaKg: Double?
)

/**
 * Use case para obter detalhes completos de um reprodutor por ID.
 *
 * Busca o reprodutor no repositório e calcula dados derivados:
 * - Idade em dias
 * - Categoria de idade (IMATURO, TREINAMENTO ou ADULTO)
 * - Ração diária recomendada (se peso disponível)
 *
 * @param repository Repositório de reprodutores
 * @param calcularRacaoDiariaUseCase Use case para calcular ração diária
 * @param calcularCategoriaIdadeUseCase Use case para calcular categoria de idade
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 */
class ObterReprodutorUseCase @Inject constructor(
    private val repository: ReprodutorRepository,
    private val calcularRacaoDiariaUseCase: CalcularRacaoDiariaUseCase,
    private val calcularCategoriaIdadeUseCase: CalcularCategoriaIdadeReprodutorUseCase
) {
    /**
     * Obtém detalhes completos de um reprodutor.
     *
     * @param id ID do reprodutor
     * @return ReprodutorDetalhes com dados calculados, ou null se reprodutor não encontrado
     */
    suspend operator fun invoke(id: Long): ReprodutorDetalhes? {
        val reprodutor = repository.buscarPorId(id) ?: return null
        
        val idadeDias = ChronoUnit.DAYS.between(reprodutor.dataNascimento, LocalDate.now())
        val categoria = calcularCategoriaIdadeUseCase(reprodutor.dataNascimento)
        val racaoDiaria = reprodutor.pesoAtualKg?.let { calcularRacaoDiariaUseCase(it) }
        
        return ReprodutorDetalhes(
            reprodutor = reprodutor,
            idadeDias = idadeDias,
            categoria = categoria,
            racaoDiariaKg = racaoDiaria
        )
    }
}
