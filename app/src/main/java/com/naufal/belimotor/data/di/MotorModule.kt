package com.naufal.belimotor.data.di

import android.content.Context
import com.naufal.belimotor.data.motor.MotorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MotorModule {
    @Provides
    fun provideMotorRepository(@ApplicationContext appContext: Context): MotorRepository {
        return MotorRepository(appContext)
    }
}