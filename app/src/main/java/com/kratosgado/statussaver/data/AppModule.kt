package com.kratosgado.statussaver.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

  @Provides
  @ViewModelScoped
  fun provideStatusRepository(@ApplicationContext context: Context): StatusRepository {
    return StatusRepository(context)
  }
}