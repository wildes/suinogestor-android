package br.com.suinogestor.domain.repository

import br.com.suinogestor.domain.model.Femea
import kotlinx.coroutines.flow.Flow

interface FemeaRepository {
    fun observarTodasAtivas(): Flow<List<Femea>>
    fun observarPorId(id: Long): Flow<Femea?>
    suspend fun buscarPorIdentificacao(identificacao: String): Femea?
    suspend fun salvar(femea: Femea): Long
    suspend fun atualizar(femea: Femea)
}
