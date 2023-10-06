package com.asalcedo.examplearchitectures.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asalcedo.examplearchitectures.data.Movie
import com.asalcedo.examplearchitectures.data.local.MoviesDao
import com.asalcedo.examplearchitectures.data.local.toLocalMovie
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
                _state.value = UiState(loading = true)
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

            // Al hacer la base de datos reactiva se debe recolectar esos cambios y actualizar
            // la UI

            /*_state.value = UiState(
                loading = false,
                movies = dao.getMovies().map { it.toMovie() }
            )*/

            dao.getMovies().collect { movies ->
                _state.value = UiState(
                    loading = false,
                    movies = movies.map { it.toMovie() }
                )
            }
        }
    }

    fun onMovieClick(movie: Movie) {
        //Con esto solo actualizamos el valor de favorite en memoria
        /*val movies = _state.value.movies.toMutableList()
        movies.replaceAll { if (it.id == movie.id) movie.copy(favorite = !movie.favorite) else it }
        // copiamos el rorignal y le añadimos las películas,
        _state.value = _state.value.copy(movies = movies)*/

        // Con esto solo actualizamos la base de datos pero no en memoria para ello se debe hacer que la base de datos sea reactiva
        // es decir que cuando cambie algo en la base de datos, un flow nos informe del cambio
        // se lo hace en el DAO
        viewModelScope.launch {
            dao.updateMovie(
                // Con la funcion copy le decimos que haga una copia de la película pero sólo
                // que cambie el valor de favorito y principalmente porque al ser un data class,
                // la data class por defecto es un objeto inmutable
                movie.copy(favorite = !movie.favorite).toLocalMovie()
            )
        }

    }

    // Usar una dataclass que representa a la pantalla
    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie> = emptyList()
    )
}