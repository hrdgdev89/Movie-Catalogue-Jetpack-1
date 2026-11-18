package com.jetpack.moviecataloguejetpack

import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.usecase.GetFavoriteMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.RemoveFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.presentation.favorite.FavoriteViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase = mockk()
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase = mockk(relaxed = true)

    @Test
    fun `favoriteMovies positive case - emits list of favorite movies`() = runTest {
        val dummyMovies = listOf(
            Movie(1, "Movie 1", "Overview 1", null, null, "2026-01-01", 8.0),
            Movie(2, "Movie 2", "Overview 2", null, null, "2026-02-02", 7.5)
        )
        every { getFavoriteMoviesUseCase() } returns flowOf(dummyMovies)

        val viewModel = FavoriteViewModel(getFavoriteMoviesUseCase, removeFavoriteMovieUseCase)

        val result = viewModel.favoriteMovies.first { it.isNotEmpty() }
        assertEquals(2, result.size)
        assertEquals(dummyMovies, result)
    }

    @Test
    fun `favoriteMovies negative empty case - emits empty list when no favorites`() = runTest {
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())

        val viewModel = FavoriteViewModel(getFavoriteMoviesUseCase, removeFavoriteMovieUseCase)

        val result = viewModel.favoriteMovies.value
        assertTrue(result.isEmpty())
    }

    @Test
    fun `removeFavorite - calls removeFavoriteMovieUseCase with correct movieId`() = runTest {
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        val viewModel = FavoriteViewModel(getFavoriteMoviesUseCase, removeFavoriteMovieUseCase)

        viewModel.removeFavorite(123)

        coVerify(exactly = 1) { removeFavoriteMovieUseCase(123) }
    }
}
