package com.asalcedo.examplearchitectures.data.remote

import com.asalcedo.examplearchitectures.data.Movie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource() {
    /*
    Lo ideal es que el RemoteDataSource haga todas las conversiones que necesite y devuelva
    las películas (Movie) que vamos a usar en la capa de Dominio
     */
    suspend fun getMovies(): List<Movie> {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesService::class.java)
            .getMovies()
            .results
            .map { it.toMovie() }

    }
}