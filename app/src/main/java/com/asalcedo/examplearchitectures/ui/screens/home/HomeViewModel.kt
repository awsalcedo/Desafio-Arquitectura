package com.asalcedo.examplearchitectures.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asalcedo.examplearchitectures.data.Movie
import com.asalcedo.examplearchitectures.data.local.MoviesDao
import com.asalcedo.examplearchitectures.data.local.toMovie
import com.asalcedo.examplearchitectures.data.remote.MoviesService
import com.asalcedo.examplearchitectures.data.remote.toLocalMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(private val dao: MoviesDao) : ViewModel() {

    // tener un estado que va hacer observado por Compose
    // mutableStateOf es específico para compose
    /*var state by mutableStateOf(UiState())
        private set*/

    // uso de LiveData
    /*private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state*/

    // uso de stateFlow
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    // Para la reactividad en UI se tiene dos componentes: Live Data y StateFlow (dentro del mundo de las corroutinas)

    init {
        viewModelScope.launch {

            // verifico si hay movies en local
            val isDbEmpty = dao.count() == 0

            if (isDbEmpty) {
                dao.insertAll(
                    Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(MoviesService::class.java)
                        .getMovies()
                        .results
                        .map { it.toLocalMovie() }
                )
            }
            _state.value = UiState(loading = true)
            //cargar las películas
            _state.value = UiState(
                loading = false,
                movies = dao.getMovies().map { it.toMovie() }
            )
        }
    }

    fun onMovieClick(movie: Movie) {
        val movies = _state.value.movies.toMutableList()
        movies.replaceAll { if (it.id == movie.id) movie.copy(favorite = !movie.favorite) else it }
        // copiamos el rorignal y le añadimos las películas,
        _state.value = _state.value.copy(movies = movies)
    }

    // Usar una dataclass que representa a la pantalla
    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie> = emptyList()
    )
}