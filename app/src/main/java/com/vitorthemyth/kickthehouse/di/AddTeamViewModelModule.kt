package com.vitorthemyth.kickthehouse.di

import com.vitorthemyth.kickthehouse.presentation.add_team.AddTeamViewModel
import com.vitorthemyth.use_cases.TeamUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AddTeamViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideAddTeamViewModel(
        getTeamsUseCase: TeamUseCases
    ): AddTeamViewModel {
        return AddTeamViewModel(getTeamsUseCase)
    }

}