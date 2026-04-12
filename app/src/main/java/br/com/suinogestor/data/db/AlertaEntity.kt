package br.com.suinogestor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alertas",
    foreignKeys = [
        ForeignKey(
            entity = FemeaEntity::class,
            parentColumns = ["id"],
            childColumns = ["femeaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("femeaId"), Index("lido"), Index("dataGeracao")]
)
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val femeaId: Long?,
    val tipo: String,
    val mensagem: String,
    val prioridade: String,
    val dataGeracao: String,
    val dataLeitura: String?,
    val lido: Int = 0
)
