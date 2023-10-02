package com.asalcedo.examplearchitectures

import retrofit2.http.GET

interface MoviesService {
    @GET("/discover/movie?api_key=5455465464556456")
    suspend fun getMovies() : MovieResult
}