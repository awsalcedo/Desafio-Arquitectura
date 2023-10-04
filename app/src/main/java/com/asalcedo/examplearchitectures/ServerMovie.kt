package com.asalcedo.examplearchitectures

data class ServerMovie(
    val id: Int,
    val adult: Boolean,
    val title: String,
    val poster_path: String,
    val favorite: Boolean = false
)
