package com.asalcedo.examplearchitectures.di

import android.content.Context
import androidx.room.Room
import com.asalcedo.examplearchitectures.data.local.MoviesDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val MOVIES_DATABASE_NAME = "movies-db"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MoviesDataBase::class.java, MOVIES_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideMoviesDao(dataBase: MoviesDataBase) = dataBase.moviesDao()

}