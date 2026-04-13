package br.com.suinogestor.domain.repository

import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.ResultadoOperacao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface do repositório de reprodutores.
 *
 * Define as operações de persistência e consulta para o domínio de reprodutores,
 * seguindo o padrão Repository do Clean Architecture.
 *
 * Métodos relacionados a coberturas (contarCoberturasNaSemana, obterDataUltimaCobertura)
 * retornam valores mock até que o módulo de coberturas seja implementado.
 */
interface ReprodutorRepository {
    
    /**
     * Cadastra um novo reprodutor no sistema.
     *
     * @param reprodutor Dados do reprodutor a ser cadastrado
     * @return ResultadoOperacao contendo o ID gerado em caso de sucesso,
     *         ou ErroNegocio em caso de falha (ex: identificação duplicada)
     */
    suspend fun cadastrar(reprodutor: Reprodutor): ResultadoOperacao<Long>
    
    /**
     * Atualiza os dados de um reprodutor existente.
     *
     * @param reprodutor Dados atualizados do reprodutor
     * @return ResultadoOperacao indicando sucesso ou erro
     */
    suspend fun atualizar(reprodutor: Reprodutor): ResultadoOperacao<Unit>
    
    /**
     * Busca um reprodutor por seu ID interno.
     *
     * @param id ID do reprodutor
     * @return Reprodutor encontrado ou null se não existir
     */
    suspend fun buscarPorId(id: Long): Reprodutor?
    
    /**
     * Busca um reprodutor por seu número de identificação.
     *
     * @param identificacao Número de identificação do reprodutor
     * @return Reprodutor encontrado ou null se não existir
     */
    suspend fun buscarPorIdentificacao(identificacao: String): Reprodutor?
    
    /**
     * Observa todos os reprodutores do plantel de forma reativa.
     *
     * @return Flow que emite a lista atualizada de reprodutores sempre que houver mudanças
     */
    fun observarTodos(): Flow<List<Reprodutor>>
    
    /**
     * Conta o número de coberturas realizadas por um reprodutor em uma semana específica.
     *
     * **NOTA**: Implementação mock até que o módulo de coberturas seja criado.
     * Atualmente retorna sempre 0.
     *
     * @param reprodutorId ID do reprodutor
     * @param dataReferencia Data de referência para calcular a semana
     * @return Número de coberturas na semana (mock: sempre 0)
     */
    suspend fun contarCoberturasNaSemana(reprodutorId: Long, dataReferencia: LocalDate): Int
    
    /**
     * Obtém a data da última cobertura realizada por um reprodutor.
     *
     * **NOTA**: Implementação mock até que o módulo de coberturas seja criado.
     * Atualmente retorna sempre null.
     *
     * @param reprodutorId ID do reprodutor
     * @return Data da última cobertura ou null se não houver registros (mock: sempre null)
     */
    suspend fun obterDataUltimaCobertura(reprodutorId: Long): LocalDate?
}
