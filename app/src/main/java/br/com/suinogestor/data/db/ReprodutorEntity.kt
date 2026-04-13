package br.com.suinogestor.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reprodutores",
    indices = [Index(value = ["identificacao"], unique = true)]
)
data class ReprodutorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String, // ISO-8601
    val racaLinhagem: String,
    val tipoUso: String, // enum serializado: MONTA_NATURAL, COLETA_IA, AMBOS
    val pesoAtualKg: Double?,
    val eccAtual: Int?, // CHECK constraint 1-5 será aplicado na migration
    val ativo: Int = 1, // SQLite boolean: 1 = ativo, 0 = inativo
    val dataCadastro: String // ISO-8601
)
