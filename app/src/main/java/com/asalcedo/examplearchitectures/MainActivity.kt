package com.asalcedo.examplearchitectures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.asalcedo.examplearchitectures.data.local.MoviesDataBase
import com.asalcedo.examplearchitectures.ui.screens.home.Home

class MainActivity : ComponentActivity() {

    //Esto solo hasta implementar inyección de dependencias
    //Se cambia porque aún el applicationContext aún no se lo tiene y da el error
    //Attempt to invoke virtual method 'android.content.Context android.content.Context.getApplicationContext()' on a null object reference
    /*val db = Room.databaseBuilder(
        applicationContext,
        MoviesDataBase::class.java, "movies-db"
    ).build()*/
    private lateinit var db: MoviesDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(
            applicationContext,
            MoviesDataBase::class.java, "movies-db"
        ).build()

        setContent {
            Home(db.moviesDao())
        }
    }
}