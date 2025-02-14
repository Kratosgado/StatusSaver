package com.kratosgado.statusaver.data

import android.content.Context
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
  fun provideStatusRepository(@ApplicationContext context: Context): StatusRepository {
    return StatusRepository(context)
  }

  @Provides
  @Singleton
  fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
    return SettingsManager(context)
  }
}