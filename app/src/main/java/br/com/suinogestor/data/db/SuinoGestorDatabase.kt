package br.com.suinogestor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FemeaEntity::class, AlertaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SuinoGestorDatabase : RoomDatabase() {
    abstract fun femeaDao(): FemeaDao
    abstract fun alertaDao(): AlertaDao
}
