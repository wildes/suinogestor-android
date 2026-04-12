package br.com.suinogestor.domain.repository

import br.com.suinogestor.domain.model.Alerta
import kotlinx.coroutines.flow.Flow

interface AlertaRepository {
    fun observarNaoLidos(): Flow<List<Alerta>>
    fun observarPorFemea(femeaId: Long): Flow<List<Alerta>>
    suspend fun salvar(alerta: Alerta): Long
    suspend fun marcarComoLido(id: Long)
}
