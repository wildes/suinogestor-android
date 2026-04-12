package br.com.suinogestor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FemeaDao {
    @Query("SELECT * FROM femeas WHERE ativo = 1 ORDER BY identificacao")
    fun observarTodasAtivas(): Flow<List<FemeaEntity>>

    @Query("SELECT * FROM femeas WHERE id = :id")
    fun observarPorId(id: Long): Flow<FemeaEntity?>

    @Query("SELECT * FROM femeas WHERE identificacao = :identificacao LIMIT 1")
    suspend fun buscarPorIdentificacao(identificacao: String): FemeaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(femea: FemeaEntity): Long

    @Update
    suspend fun atualizar(femea: FemeaEntity)
}
