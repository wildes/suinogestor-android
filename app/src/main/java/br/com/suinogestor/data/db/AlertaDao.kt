package br.com.suinogestor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {
    @Query("SELECT * FROM alertas WHERE lido = 0 ORDER BY prioridade DESC, dataGeracao DESC")
    fun observarNaoLidos(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas WHERE femeaId = :femeaId ORDER BY dataGeracao DESC")
    fun observarPorFemea(femeaId: Long): Flow<List<AlertaEntity>>

    @Insert
    suspend fun inserir(alerta: AlertaEntity): Long

    @Query("UPDATE alertas SET lido = 1, dataLeitura = :dataLeitura WHERE id = :id")
    suspend fun marcarComoLido(id: Long, dataLeitura: String)
}
