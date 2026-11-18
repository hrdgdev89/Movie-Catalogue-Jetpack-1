package com.jetpack.moviecataloguejetpack

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.data.repository.MovieRepositoryImpl
import com.jetpack.moviecataloguejetpack.data.source.remote.api.MovieApiService
import com.jetpack.moviecataloguejetpack.data.source.remote.response.GenreListResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.GenreResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieDetailResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieVideosResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.VideoItemResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val apiService: MovieApiService = mockk()
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(apiService)
    }

    @Test
    fun `getGenres positive case - returns Resource Success with mapped genres`() = runTest {
        val dummyResponse = GenreListResponse(
            genres = listOf(
                GenreResponse(28, "Action"),
                GenreResponse(12, "Adventure")
            )
        )
        coEvery { apiService.getMovieGenres(any()) } returns dummyResponse

        val results = repository.getGenres().toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        val successData = (results[1] as Resource.Success).data
        assertEquals(2, successData.size)
        assertEquals("Action", successData[0].name)
    }

    @Test
    fun `getGenres negative case - returns Resource Error on network failure`() = runTest {
        coEvery { apiService.getMovieGenres(any()) } throws IOException("No internet connection")

        val results = repository.getGenres().toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("No internet connection", (results[1] as Resource.Error).message)
    }

    @Test
    fun `getMovieTrailers positive case - filters only YouTube trailer and teaser videos`() = runTest {
        val dummyVideosResponse = MovieVideosResponse(
            id = 100,
            results = listOf(
                VideoItemResponse("1", "key1", "Official Trailer", "YouTube", "Trailer", true),
                VideoItemResponse("2", "key2", "Behind The Scenes", "YouTube", "Featurette", true),
                VideoItemResponse("3", "key3", "Other Platform", "Vimeo", "Trailer", true)
            )
        )
        coEvery { apiService.getMovieVideos(100, any()) } returns dummyVideosResponse

        val results = repository.getMovieTrailers(100).toList()

        assertEquals(2, results.size)
        assertTrue(results[1] is Resource.Success)
        val trailers = (results[1] as Resource.Success).data
        assertEquals(1, trailers.size)
        assertEquals("key1", trailers[0].key)
    }

    @Test
    fun `getMovieDetail negative case - returns Resource Error when api throws exception`() = runTest {
        coEvery { apiService.getMovieDetail(any(), any()) } throws RuntimeException("Server error 500")

        val results = repository.getMovieDetail(999).toList()

        assertEquals(2, results.size)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Server error 500", (results[1] as Resource.Error).message)
    }
}
