package br.com.suinogestor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReprodutorDao {
    @Insert
    suspend fun inserir(reprodutor: ReprodutorEntity): Long
    
    @Update
    suspend fun atualizar(reprodutor: ReprodutorEntity)
    
    @Query("SELECT * FROM reprodutores WHERE id = :id")
    suspend fun buscarPorId(id: Long): ReprodutorEntity?
    
    @Query("SELECT * FROM reprodutores WHERE identificacao = :identificacao")
    suspend fun buscarPorIdentificacao(identificacao: String): ReprodutorEntity?
    
    @Query("SELECT * FROM reprodutores WHERE ativo = 1 ORDER BY identificacao ASC")
    fun observarTodosAtivos(): Flow<List<ReprodutorEntity>>
}
