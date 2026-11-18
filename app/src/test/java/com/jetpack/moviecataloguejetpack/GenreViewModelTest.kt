package com.jetpack.moviecataloguejetpack

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieGenresUseCase
import com.jetpack.moviecataloguejetpack.presentation.genre.GenreViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GenreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMovieGenresUseCase: GetMovieGenresUseCase = mockk()

    @Test
    fun `loadGenres positive case - emits Resource Success with genres`() {
        val dummyGenres = listOf(
            Genre(28, "Action"),
            Genre(12, "Adventure"),
            Genre(16, "Animation")
        )
        every { getMovieGenresUseCase() } returns flowOf(Resource.Success(dummyGenres))

        val viewModel = GenreViewModel(getMovieGenresUseCase)

        val state = viewModel.genresState.value
        assertTrue(state is Resource.Success)
        assertEquals(dummyGenres, (state as Resource.Success).data)
        assertEquals(3, state.data.size)
        verify(exactly = 1) { getMovieGenresUseCase() }
    }

    @Test
    fun `loadGenres negative case - emits Resource Error with message`() {
        val errorMessage = "Koneksi internet bermasalah"
        every { getMovieGenresUseCase() } returns flowOf(Resource.Error(errorMessage))

        val viewModel = GenreViewModel(getMovieGenresUseCase)

        val state = viewModel.genresState.value
        assertTrue(state is Resource.Error)
        assertEquals(errorMessage, (state as Resource.Error).message)
        verify(exactly = 1) { getMovieGenresUseCase() }
    }
}
