package com.vitorthemyth.kickthehouse.di

import com.vitorthemyth.domain.repository.ProbabilitiesRepository
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.use_cases.*
import com.vitorthemyth.use_cases.interfaces.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetTeamListUseCase(teamRepository: TeamRepository): GetTeamListUseCase {
        return GetTeamListUseCaseImpl(teamRepository)
    }

    @Provides
    fun provideSaveTeamUseCase(teamRepository: TeamRepository): SaveTeamUseCase {
        return SaveTeamUseCaseImp(teamRepository)
    }

    @Provides
    fun provideDeleteTeamUseCase(teamRepository: TeamRepository): DeleteTeamUseCase {
        return com.vitorthemyth.use_cases.DeleteTeamUseCaseImp(teamRepository)
    }

    @Provides
    fun provideUpdateTeamUseCase(teamRepository: TeamRepository): UpdateTeam {
        return UpdateTeamUseCaseImp(teamRepository)
    }

    @Provides
    fun provideCalcProbabilities(probabilitiesRepository: ProbabilitiesRepository): CalculateProbabilities {
        return CalculateProbabilitiesUseCaseImp(probabilitiesRepository)
    }

    @Provides
    fun provideCalcEv(probabilitiesRepository: ProbabilitiesRepository): CalculateEV {
        return CalculateEvUseCaseImp(probabilitiesRepository)
    }


    @Provides
    fun provideTeamUseCases(
        getTeamListUseCase: GetTeamListUseCase,
        saveTeamUseCase: SaveTeamUseCase,
        deleteTeamUseCase: DeleteTeamUseCase,
        updateTeamUseCase: UpdateTeam
    ): TeamUseCases {
        return TeamUseCases(
            getTeamListUseCase,
            saveTeamUseCase,
            deleteTeamUseCase,
            updateTeamUseCase
        )
    }
}