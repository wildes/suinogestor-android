package br.com.suinogestor.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import br.com.suinogestor.data.db.ReprodutorDao
import br.com.suinogestor.data.db.ReprodutorEntity
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.model.TipoUsoReprodutor
import br.com.suinogestor.domain.repository.ReprodutorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementação concreta do repositório de reprodutores.
 *
 * Responsável por:
 * - Mapear entre entidades Room e modelos de domínio
 * - Tratar exceções de banco de dados e convertê-las em erros de negócio
 * - Fornecer observação reativa via Flow
 *
 * Métodos relacionados a coberturas retornam valores mock até que o módulo
 * de coberturas seja implementado.
 */
class ReprodutorRepositoryImpl @Inject constructor(
    private val dao: ReprodutorDao
) : ReprodutorRepository {

    override suspend fun cadastrar(reprodutor: Reprodutor): ResultadoOperacao<Long> {
        return try {
            val entity = reprodutor.toEntity()
            val id = dao.inserir(entity)
            ResultadoOperacao.Sucesso(id)
        } catch (e: SQLiteConstraintException) {
            ResultadoOperacao.Erro(
                ErroNegocio.IdentificacaoDuplicada(
                    "Identificação já cadastrada no plantel"
                )
            )
        } catch (e: SQLiteException) {
            ResultadoOperacao.Erro(
                ErroNegocio.ErroInterno(
                    "Erro ao cadastrar reprodutor: ${e.message}"
                )
            )
        }
    }

    override suspend fun atualizar(reprodutor: Reprodutor): ResultadoOperacao<Unit> {
        return try {
            val entity = reprodutor.toEntity()
            dao.atualizar(entity)
            ResultadoOperacao.Sucesso(Unit)
        } catch (e: SQLiteConstraintException) {
            ResultadoOperacao.Erro(
                ErroNegocio.IdentificacaoDuplicada(
                    "Identificação já cadastrada no plantel"
                )
            )
        } catch (e: SQLiteException) {
            ResultadoOperacao.Erro(
                ErroNegocio.ErroInterno(
                    "Erro ao atualizar reprodutor: ${e.message}"
                )
            )
        }
    }

    override suspend fun buscarPorId(id: Long): Reprodutor? {
        return dao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarPorIdentificacao(identificacao: String): Reprodutor? {
        return dao.buscarPorIdentificacao(identificacao)?.toDomain()
    }

    override fun observarTodos(): Flow<List<Reprodutor>> {
        return dao.observarTodosAtivos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Conta coberturas na semana (mock).
     * Retorna sempre 0 até que o módulo de coberturas seja implementado.
     */
    override suspend fun contarCoberturasNaSemana(
        reprodutorId: Long,
        dataReferencia: LocalDate
    ): Int {
        // TODO: Implementar quando módulo de coberturas estiver pronto
        return 0
    }

    /**
     * Obtém data da última cobertura (mock).
     * Retorna sempre null até que o módulo de coberturas seja implementado.
     */
    override suspend fun obterDataUltimaCobertura(reprodutorId: Long): LocalDate? {
        // TODO: Implementar quando módulo de coberturas estiver pronto
        return null
    }
}

/**
 * Converte ReprodutorEntity (camada de dados) para Reprodutor (domínio).
 */
fun ReprodutorEntity.toDomain(): Reprodutor = Reprodutor(
    id = id,
    identificacao = identificacao,
    dataNascimento = LocalDate.parse(dataNascimento),
    racaLinhagem = racaLinhagem,
    tipoUso = TipoUsoReprodutor.valueOf(tipoUso),
    pesoAtualKg = pesoAtualKg,
    eccAtual = eccAtual,
    ativo = ativo == 1,
    dataCadastro = LocalDate.parse(dataCadastro)
)

/**
 * Converte Reprodutor (domínio) para ReprodutorEntity (camada de dados).
 */
fun Reprodutor.toEntity(): ReprodutorEntity = ReprodutorEntity(
    id = id,
    identificacao = identificacao,
    dataNascimento = dataNascimento.toString(),
    racaLinhagem = racaLinhagem,
    tipoUso = tipoUso.name,
    pesoAtualKg = pesoAtualKg,
    eccAtual = eccAtual,
    ativo = if (ativo) 1 else 0,
    dataCadastro = dataCadastro.toString()
)
