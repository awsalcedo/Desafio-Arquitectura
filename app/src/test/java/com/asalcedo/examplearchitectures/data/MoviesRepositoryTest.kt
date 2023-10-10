package com.asalcedo.examplearchitectures.data

import com.asalcedo.examplearchitectures.data.local.LocalDataSource
import com.asalcedo.examplearchitectures.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking

class MoviesRepositoryTest {
    @Test
    fun `When Db is empty, server is called`() {
        // GIVEN
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 0
        }
        val remoteDataSource = mock<RemoteDataSource>()
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        // WHEN
        runBlocking { repository.requestMovies() }

        // THEN
        verifyBlocking(remoteDataSource, times(1)) { getMovies() }
    }

    @Test
    fun `When DB is empty, movies are saved into DB`() {
        val expectedMovies = listOf(Movie(1, "Title", "Overview", "PosterPath", true))
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 0
        }
        val remoteDataSource = mock<RemoteDataSource>() {
            onBlocking { getMovies() } doReturn expectedMovies
        }
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        runBlocking { repository.requestMovies() }

        //verifyBlocking(localDataSource) { insertAll(any()) } // cualquiera de las dos funciona esta es general
        verifyBlocking(localDataSource) { insertAll(expectedMovies) } // y esta es más específica
    }

    @Test
    fun `When DB is not empty, doesn't call movies server`() {
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 1
        }
        val remoteDataSource = mock<RemoteDataSource>()
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        runBlocking { repository.requestMovies() }

        verifyBlocking(remoteDataSource, times(0)) { getMovies() }
    }

    @Test
    fun `When DB is not empty, movies are recovered from DB`() {
        val localMovies = listOf(Movie(1, "Title", "Overview", "PosterPath", true))
        val remoteMovies = listOf(Movie(2, "Title2", "Overview2", "PosterPath2", false))
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 1
            onBlocking { movies } doReturn flowOf(localMovies)
        }
        val remoteDataSource = mock<RemoteDataSource>() {
            onBlocking { getMovies() } doReturn remoteMovies
        }
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        val result = runBlocking {
            repository.requestMovies()
            repository.movies.first()
        }

        assertEquals(localMovies, result)
    }
}