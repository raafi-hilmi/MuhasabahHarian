package com.raafi.muhasabahharian.di

import android.content.Context
import androidx.room.Room
import com.raafi.muhasabahharian.data.local.ReflectionDatabase
import com.raafi.muhasabahharian.data.local.dao.MuhasabahDao
import com.raafi.muhasabahharian.data.remote.RemoteMuhasabahDataSource
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import com.raafi.muhasabahharian.data.repository.MuhasabahRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ReflectionDatabase {
        return Room.databaseBuilder(
            context,
            ReflectionDatabase::class.java,
            "reflection_db"
        ).build()
    }

    @Provides
    fun provideDao(db: ReflectionDatabase): MuhasabahDao = db.muhasabahDao()

    @Provides
    @Singleton
    fun provideRepository(dao: MuhasabahDao, remoteMuhasabahDataSource: RemoteMuhasabahDataSource): MuhasabahRepository =
        MuhasabahRepositoryImpl(dao, remoteMuhasabahDataSource)
}