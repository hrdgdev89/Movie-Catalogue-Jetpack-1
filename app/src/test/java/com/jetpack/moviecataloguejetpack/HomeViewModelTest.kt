package com.jetpack.moviecataloguejetpack

import androidx.paging.PagingData
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieGenresUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMoviesByGenreUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetTopRatedMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.SearchMoviesUseCase
import com.jetpack.moviecataloguejetpack.presentation.home.HomeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase = mockk()
    private val getMovieGenresUseCase: GetMovieGenresUseCase = mockk()
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase = mockk()
    private val searchMoviesUseCase: SearchMoviesUseCase = mockk()

    private val dummyMovies = listOf(
        Movie(1, "Movie 1", "Overview 1", null, null, "2025-01-01", 9.0),
        Movie(2, "Movie 2", "Overview 2", null, null, "2025-01-02", 8.8),
        Movie(3, "Movie 3", "Overview 3", null, null, "2025-01-03", 8.7),
        Movie(4, "Movie 4", "Overview 4", null, null, "2025-01-04", 8.6),
        Movie(5, "Movie 5", "Overview 5", null, null, "2025-01-05", 8.5),
        Movie(6, "Movie 6", "Overview 6", null, null, "2025-01-06", 8.4)
    )

    private val dummyGenres = listOf(
        Genre(28, "Action"),
        Genre(27, "Horror"),
        Genre(35, "Comedy")
    )

    @Test
    fun `loadCarouselMovies positive case - limits to exactly 5 movies with rating 5 stars`() {
        every { getTopRatedMoviesUseCase() } returns flowOf(Resource.Success(dummyMovies))
        every { getMovieGenresUseCase() } returns flowOf(Resource.Success(dummyGenres))

        val viewModel = HomeViewModel(
            getTopRatedMoviesUseCase,
            getMovieGenresUseCase,
            getMoviesByGenreUseCase,
            searchMoviesUseCase
        )

        val carouselState = viewModel.carouselMoviesState.value
        assertTrue(carouselState is Resource.Success)
        val movies = (carouselState as Resource.Success).data
        assertEquals(5, movies.size)
        assertEquals("Movie 1", movies[0].title)
        assertEquals("Movie 5", movies[4].title)
    }

    @Test
    fun `loadGenres positive case - auto selects first genre`() {
        every { getTopRatedMoviesUseCase() } returns flowOf(Resource.Success(dummyMovies))
        every { getMovieGenresUseCase() } returns flowOf(Resource.Success(dummyGenres))

        val viewModel = HomeViewModel(
            getTopRatedMoviesUseCase,
            getMovieGenresUseCase,
            getMoviesByGenreUseCase,
            searchMoviesUseCase
        )

        val genresState = viewModel.genresState.value
        assertTrue(genresState is Resource.Success)
        val genres = (genresState as Resource.Success).data
        assertEquals(3, genres.size)

        val selectedGenre = viewModel.selectedGenre.value
        assertNotNull(selectedGenre)
        assertEquals("Action", selectedGenre?.name)
    }

    @Test
    fun `selectGenre updates selected genre correctly`() {
        every { getTopRatedMoviesUseCase() } returns flowOf(Resource.Success(dummyMovies))
        every { getMovieGenresUseCase() } returns flowOf(Resource.Success(dummyGenres))

        val viewModel = HomeViewModel(
            getTopRatedMoviesUseCase,
            getMovieGenresUseCase,
            getMoviesByGenreUseCase,
            searchMoviesUseCase
        )

        val newGenre = Genre(27, "Horror")
        viewModel.selectGenre(newGenre)

        assertEquals("Horror", viewModel.selectedGenre.value?.name)
        assertEquals(27, viewModel.selectedGenre.value?.id)
    }

    @Test
    fun `getMoviesByGenre invokes getMoviesByGenreUseCase`() {
        every { getTopRatedMoviesUseCase() } returns flowOf(Resource.Success(dummyMovies))
        every { getMovieGenresUseCase() } returns flowOf(Resource.Success(dummyGenres))
        every { getMoviesByGenreUseCase(28) } returns flowOf(PagingData.empty())

        val viewModel = HomeViewModel(
            getTopRatedMoviesUseCase,
            getMovieGenresUseCase,
            getMoviesByGenreUseCase,
            searchMoviesUseCase
        )

        viewModel.getMoviesByGenre(28)
        verify { getMoviesByGenreUseCase(28) }
    }
}
