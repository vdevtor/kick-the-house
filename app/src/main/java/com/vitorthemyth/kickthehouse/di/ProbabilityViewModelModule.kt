package com.vitorthemyth.kickthehouse.di

import com.vitorthemyth.kickthehouse.presentation.probabilities.ProbabilityViewModel
import com.vitorthemyth.use_cases.TeamUseCases
import com.vitorthemyth.use_cases.interfaces.CalculateEV
import com.vitorthemyth.use_cases.interfaces.CalculateProbabilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ProbabilityViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideAddTeamViewModel(
        getTeamsUseCase: TeamUseCases,
        calculateEV: CalculateEV,
        calculateProbabilities: CalculateProbabilities
    ): ProbabilityViewModel {
        return ProbabilityViewModel(getTeamsUseCase,calculateEV, calculateProbabilities)
    }

}