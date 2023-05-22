package com.vitorthemyth.kickthehouse.di

import com.vitorthemyth.kickthehouse.presentation.team_list.TeamListViewModel
import com.vitorthemyth.use_cases.TeamUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object TeamListViewModel {

    @Provides
    @ViewModelScoped
    fun provideTeamListViewModel(
        getTeamsUseCase: TeamUseCases
    ): TeamListViewModel {
        return TeamListViewModel(getTeamsUseCase)
    }
}