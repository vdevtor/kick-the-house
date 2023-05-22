package com.vitorthemyth.kickthehouse.di

import com.vitorthemyth.kickthehouse.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object MainViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel()
    }

}