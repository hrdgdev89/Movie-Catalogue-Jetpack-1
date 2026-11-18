package com.jetpack.moviecataloguejetpack.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jetpack.moviecataloguejetpack.data.mapper.DataMapper
import com.jetpack.moviecataloguejetpack.data.source.remote.api.MovieApiService
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.utils.Constants
import java.io.IOException
import retrofit2.HttpException

class MovieByGenrePagingSource(
    private val apiService: MovieApiService,
    private val genreId: Int
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: 1
        return try {
            val response = apiService.getMoviesByGenre(
                genreId = genreId,
                page = position,
                apiKey = Constants.API_KEY
            )
            val movies = DataMapper.mapMovieResponsesToDomain(response.results)
            val totalPages = response.totalPages ?: 1

            LoadResult.Page(
                data = movies,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position >= totalPages || movies.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}
