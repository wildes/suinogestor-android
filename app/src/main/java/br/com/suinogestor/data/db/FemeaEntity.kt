package br.com.suinogestor.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "femeas",
    indices = [Index(value = ["identificacao"], unique = true)]
)
data class FemeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identificacao: String,
    val dataNascimento: String,
    val racaLinhagem: String,
    val categoria: String,
    val pesoEntradaKg: Double?,
    val eccAtual: Int?,
    val origem: String?,
    val fotoUri: String?,
    val status: String,
    val paridade: Int = 0,
    val dataEntrada: String,
    val ativo: Int = 1
)
