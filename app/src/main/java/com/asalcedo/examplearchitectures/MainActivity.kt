package com.asalcedo.examplearchitectures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.asalcedo.examplearchitectures.data.MoviesRepository
import com.asalcedo.examplearchitectures.data.local.LocalDataSource
import com.asalcedo.examplearchitectures.data.local.MoviesDataBase
import com.asalcedo.examplearchitectures.data.remote.MoviesService
import com.asalcedo.examplearchitectures.data.remote.RemoteDataSource
import com.asalcedo.examplearchitectures.ui.screens.home.Home
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //Esto solo hasta implementar inyección de dependencias
    //Se cambia porque aún el applicationContext, aún no se lo tiene y da el error
    //Attempt to invoke virtual method 'android.content.Context android.content.Context.getApplicationContext()' on a null object reference
    /*val db = Room.databaseBuilder(
        applicationContext,
        MoviesDataBase::class.java, "movies-db"
    ).build()*/
    //private lateinit var db: MoviesDataBase

    @Inject
    lateinit var db: MoviesDataBase

    @Inject
    lateinit var moviesService: MoviesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*db = Room.databaseBuilder(
            applicationContext,
            MoviesDataBase::class.java, "movies-db"
        ).build()*/

        val repository =
            MoviesRepository(
                localDataSource = LocalDataSource(db.moviesDao()),
                remoteDataSource = RemoteDataSource(moviesService)
            )

        setContent {
            Home(repository)
        }
    }
}