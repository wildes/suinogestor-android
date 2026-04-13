package br.com.suinogestor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FemeaEntity::class, AlertaEntity::class, ReprodutorEntity::class],
    version = 2,
    exportSchema = true
)
abstract class SuinoGestorDatabase : RoomDatabase() {
    abstract fun femeaDao(): FemeaDao
    abstract fun alertaDao(): AlertaDao
    abstract fun reprodutorDao(): ReprodutorDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS reprodutores (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                identificacao TEXT NOT NULL,
                dataNascimento TEXT NOT NULL,
                racaLinhagem TEXT NOT NULL,
                tipoUso TEXT NOT NULL,
                pesoAtualKg REAL,
                eccAtual INTEGER CHECK(eccAtual BETWEEN 1 AND 5),
                ativo INTEGER NOT NULL DEFAULT 1,
                dataCadastro TEXT NOT NULL
            )
        """)
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_reprodutores_identificacao 
            ON reprodutores(identificacao)
        """)
    }
}
