package com.asalcedo.examplearchitectures.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.asalcedo.examplearchitectures.data.remote.ServerMovie
import com.asalcedo.examplearchitectures.ui.theme.ExampleArchitecturesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    ExampleArchitecturesTheme {
        //Recuperar el viewModel
        val viewModel: HomeViewModel = viewModel()
        // Para usar LiveData en compose se debe hacer una conversion en los estados para eso se debe agregar una dependencia runtime-livedata
        // val state by viewModel.state.observeAsState(MainViewModel.UiState())

        // Para usar StateFlow en compose
        val state by viewModel.state.collectAsState()
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(topBar = { TopAppBar(title = { Text(text = "Movies") }) })
            { padding ->
                if (state.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }

                if (state.movies.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        modifier = Modifier.padding(padding),
                        horizontalArrangement = Arrangement.spacedBy(4.dp), //darle espacio horizontalmente
                        verticalArrangement = Arrangement.spacedBy(4.dp), //darle espacio verticlamente
                        contentPadding = PaddingValues(4.dp) // darle espacio alrededor
                    ) {
                        items(state.movies) { movie ->
                            // Le informo al viewModel de que se ha hecho un click
                            MovieItem(
                                movie = movie,
                                onClick = { viewModel.onMovieClick(movie) })

                        }
                    }
                }

            }

        }
    }
}

@Composable
private fun MovieItem(movie: ServerMovie, onClick: () -> Unit) {
    // esto hago clickeable a un composable y le informo a la parte superior de que se ha hecho click (onClick: () -> Unit)
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w185${movie.poster_path}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2 / 3f)
            )
            if (movie.favorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    tint = Color.White
                )
            }

        }
        Text(
            text = movie.title,
            modifier = Modifier
                .padding(16.dp)
                .height(48.dp),
            maxLines = 1
        )

    }
}
