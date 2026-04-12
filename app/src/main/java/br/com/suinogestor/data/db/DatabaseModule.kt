package br.com.suinogestor.data.db

import android.content.Context
import androidx.room.Room
import br.com.suinogestor.data.repository.AlertaRepositoryImpl
import br.com.suinogestor.data.repository.FemeaRepositoryImpl
import br.com.suinogestor.domain.repository.AlertaRepository
import br.com.suinogestor.domain.repository.FemeaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    abstract fun bindFemeaRepository(impl: FemeaRepositoryImpl): FemeaRepository

    @Binds
    abstract fun bindAlertaRepository(impl: AlertaRepositoryImpl): AlertaRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): SuinoGestorDatabase =
            Room.databaseBuilder(context, SuinoGestorDatabase::class.java, "suinogestor.db").build()

        @Provides
        fun provideFemeaDao(db: SuinoGestorDatabase): FemeaDao = db.femeaDao()

        @Provides
        fun provideAlertaDao(db: SuinoGestorDatabase): AlertaDao = db.alertaDao()
    }
}
