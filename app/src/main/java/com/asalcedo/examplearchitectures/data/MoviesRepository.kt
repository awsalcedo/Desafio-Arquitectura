package com.asalcedo.examplearchitectures.data

import com.asalcedo.examplearchitectures.data.local.LocalDataSource
import com.asalcedo.examplearchitectures.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/*
El repositorio es el único que se comunica con el el ViewModel
El repositorio puede tener varias fuentes de datos: locales, remotos, varias fuentes remotas,
varias fuentes locales, etc.
 */

class MoviesRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    // Exponemos las peliculas
    val movies: Flow<List<Movie>> = localDataSource.movies

    suspend fun updateMovie(movie: Movie) {
        localDataSource.updateMovie(movie)
    }

    suspend fun requestMovies() {
        val isDbEmpty = localDataSource.count() == 0
        if (isDbEmpty) {
            localDataSource.insertAll(remoteDataSource.getMovies())
        }
    }
}




