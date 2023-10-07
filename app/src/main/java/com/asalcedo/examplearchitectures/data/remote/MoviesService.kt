package com.asalcedo.examplearchitectures.data.remote

import retrofit2.http.GET

interface MoviesService {
    @GET("discover/movie?api_key=368cc7d298097b7d0ae2b2165330d469")
    suspend fun getMovies() : MovieResult
}