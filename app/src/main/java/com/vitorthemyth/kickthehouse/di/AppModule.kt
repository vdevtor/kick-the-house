package com.vitorthemyth.kickthehouse.di

import android.content.Context
import androidx.room.Room
import com.vitorthemyth.domain.repository.ProbabilitiesRepository
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.persistence.AppDatabase
import com.vitorthemyth.persistence.repository.ProbabilityRepositoryImp
import com.vitorthemyth.persistence.repository.TeamRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideTeamRepository(database: AppDatabase): TeamRepository {
        return TeamRepositoryImpl(database.teamDao())
    }

    @Provides
    fun provideProbabilityRepository(): ProbabilitiesRepository {
        return ProbabilityRepositoryImp()
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "my-app-database"
        ).build()
    }
}