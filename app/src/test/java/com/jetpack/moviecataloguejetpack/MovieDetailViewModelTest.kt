package com.jetpack.moviecataloguejetpack

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.model.Video
import com.jetpack.moviecataloguejetpack.domain.usecase.AddFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieDetailUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieReviewsUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieTrailersUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.IsFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.RemoveFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.presentation.detail.MovieDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMovieDetailUseCase: GetMovieDetailUseCase = mockk()
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase = mockk()
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase = mockk()
    private val isFavoriteMovieUseCase: IsFavoriteMovieUseCase = mockk()
    private val addFavoriteMovieUseCase: AddFavoriteMovieUseCase = mockk(relaxed = true)
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MovieDetailViewModel

    private val dummyDetail = MovieDetail(
        id = 299534,
        title = "Avengers: Endgame",
        overview = "After the devastating events...",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2019-04-24",
        voteAverage = 8.3,
        runtime = 181,
        genres = listOf(Genre(28, "Action"), Genre(12, "Adventure")),
        tagline = "Part of the journey is the end."
    )

    private val dummyTrailers = listOf(
        Video("1", "key123", "Official Trailer", "YouTube", "Trailer")
    )

    @Before
    fun setUp() {
        viewModel = MovieDetailViewModel(
            getMovieDetailUseCase,
            getMovieTrailersUseCase,
            getMovieReviewsUseCase,
            isFavoriteMovieUseCase,
            addFavoriteMovieUseCase,
            removeFavoriteMovieUseCase
        )
    }

    @Test
    fun `loadMovie positive case - emits Resource Success with movie details and trailers`() {
        val movieId = 299534
        every { getMovieDetailUseCase(movieId) } returns flowOf(Resource.Success(dummyDetail))
        every { getMovieTrailersUseCase(movieId) } returns flowOf(Resource.Success(dummyTrailers))

        viewModel.loadMovie(movieId)

        val detailState = viewModel.detailState.value
        assertTrue(detailState is Resource.Success)
        assertEquals(dummyDetail, (detailState as Resource.Success).data)

        val trailerState = viewModel.trailersState.value
        assertTrue(trailerState is Resource.Success)
        assertEquals(dummyTrailers, (trailerState as Resource.Success).data)
    }

    @Test
    fun `loadMovie negative case - emits Resource Error when detail fetch fails`() {
        val movieId = 299534
        val errorMessage = "Movie not found (404)"
        every { getMovieDetailUseCase(movieId) } returns flowOf(Resource.Error(errorMessage))
        every { getMovieTrailersUseCase(movieId) } returns flowOf(Resource.Success(emptyList()))

        viewModel.loadMovie(movieId)

        val detailState = viewModel.detailState.value
        assertTrue(detailState is Resource.Error)
        assertEquals(errorMessage, (detailState as Resource.Error).message)
    }

    @Test
    fun `toggleFavorite - adds movie to favorite when not currently favorite`() = runTest {
        viewModel.toggleFavorite(dummyDetail, isCurrentlyFavorite = false)

        coVerify(exactly = 1) { addFavoriteMovieUseCase(dummyDetail) }
        coVerify(exactly = 0) { removeFavoriteMovieUseCase(any()) }
    }

    @Test
    fun `toggleFavorite - removes movie from favorite when currently favorite`() = runTest {
        viewModel.toggleFavorite(dummyDetail, isCurrentlyFavorite = true)

        coVerify(exactly = 1) { removeFavoriteMovieUseCase(dummyDetail.id) }
        coVerify(exactly = 0) { addFavoriteMovieUseCase(any()) }
    }
}
