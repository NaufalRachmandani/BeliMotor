package com.naufal.belimotor.data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext appContext: Context,
    ): SharedPreferences {
        return try {
            appContext.getSharedPreferences("belimotor.preferences", Context.MODE_PRIVATE)
        } catch (e: NullPointerException) {
            throw Exception("Data module initialization at Main Application is required.")
        }
    }
}