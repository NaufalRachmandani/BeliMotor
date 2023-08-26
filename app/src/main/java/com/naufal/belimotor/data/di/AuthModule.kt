package com.naufal.belimotor.data.di

import android.content.SharedPreferences
import com.naufal.belimotor.data.auth.AuthPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthPrefs(sharedPreferences: SharedPreferences): AuthPrefs {
        return AuthPrefs(sharedPreferences)
    }
}