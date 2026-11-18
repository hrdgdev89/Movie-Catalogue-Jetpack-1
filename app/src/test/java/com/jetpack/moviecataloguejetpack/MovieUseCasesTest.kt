package com.jetpack.moviecataloguejetpack

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import com.jetpack.moviecataloguejetpack.domain.usecase.GetTopRatedMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.SearchMoviesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieUseCasesTest {

    private val repository: IMovieRepository = mockk()

    @Test
    fun `GetTopRatedMoviesUseCase emits movies from repository`() = runTest {
        val dummyMovies = listOf(
            Movie(1, "Top Movie", "Overview", null, null, "2025-01-01", 9.0)
        )
        every { repository.getTopRatedMovies() } returns flowOf(Resource.Success(dummyMovies))

        val useCase = GetTopRatedMoviesUseCase(repository)
        val result = useCase().first()

        assertTrue(result is Resource.Success)
        assertEquals(dummyMovies, (result as Resource.Success).data)
        verify(exactly = 1) { repository.getTopRatedMovies() }
    }

    @Test
    fun `SearchMoviesUseCase emits search results from repository`() = runTest {
        val query = "Batman"
        val dummyMovies = listOf(
            Movie(1, "Batman Begins", "Overview", null, null, "2005-06-15", 8.2)
        )
        every { repository.searchMovies(query) } returns flowOf(Resource.Success(dummyMovies))

        val useCase = SearchMoviesUseCase(repository)
        val result = useCase(query).first()

        assertTrue(result is Resource.Success)
        assertEquals(dummyMovies, (result as Resource.Success).data)
        verify(exactly = 1) { repository.searchMovies(query) }
    }
}
