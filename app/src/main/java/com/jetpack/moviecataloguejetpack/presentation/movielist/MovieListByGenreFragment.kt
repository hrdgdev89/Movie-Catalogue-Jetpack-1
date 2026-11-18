package com.jetpack.moviecataloguejetpack.presentation.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.FragmentMovieListByGenreBinding
import com.jetpack.moviecataloguejetpack.presentation.common.LoadingStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListByGenreFragment : Fragment() {

    private var _binding: FragmentMovieListByGenreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieListViewModel by viewModel()
    private lateinit var movieAdapter: MoviePagedAdapter

    private var genreId: Int = 0
    private var genreName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genreId = arguments?.getInt("genreId") ?: 0
        genreName = arguments?.getString("genreName").orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListByGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeMovies()

        binding.btnRetry.setOnClickListener {
            movieAdapter.retry()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = if (genreName.isNotEmpty()) genreName else "Discover Movies"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MoviePagedAdapter { movie ->
            val bundle = bundleOf(
                "movieId" to movie.id,
                "movieTitle" to movie.title
            )
            findNavController().navigate(
                R.id.action_movieListByGenreFragment_to_movieDetailFragment,
                bundle
            )
        }

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter { movieAdapter.retry() },
                footer = LoadingStateAdapter { movieAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        movieAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && movieAdapter.itemCount == 0
            val isInitialLoading = loadState.refresh is LoadState.Loading
            val isError = loadState.refresh is LoadState.Error

            binding.progressBar.isVisible = isInitialLoading
            binding.rvMovies.isVisible = !isInitialLoading && !isError && !isListEmpty
            binding.layoutError.isVisible = isError || isListEmpty

            if (isError) {
                val errorState = loadState.refresh as LoadState.Error
                binding.tvError.text = errorState.error.localizedMessage ?: "Terjadi kesalahan saat memuat film"
                binding.btnRetry.isVisible = true
            } else if (isListEmpty) {
                binding.tvError.text = "Tidak ada film yang ditemukan untuk genre ini"
                binding.btnRetry.isVisible = false
            }
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMoviesByGenre(genreId).collectLatest { pagingData ->
                    movieAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
