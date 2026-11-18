package com.jetpack.moviecataloguejetpack.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.databinding.FragmentHomeBinding
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.presentation.common.LoadingStateAdapter
import com.jetpack.moviecataloguejetpack.presentation.movielist.MoviePagedAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var carouselAdapter: HomeCarouselAdapter
    private lateinit var genreAdapter: HomeGenreAdapter
    private lateinit var genreMoviesAdapter: MoviePagedAdapter
    private lateinit var searchAdapter: HomeMovieAdapter

    private var genreMoviesJob: Job? = null
    private var currentCarouselPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()
        setupGenresRecyclerView()
        setupGenreMoviesRecyclerView()
        setupSearch()

        observeCarousel()
        observeGenres()
        observeSelectedGenre()
        observeSearchState()

        binding.btnRetry.setOnClickListener {
            viewModel.loadInitialData()
        }
    }

    private fun setupCarousel() {
        carouselAdapter = HomeCarouselAdapter { movie ->
            navigateToDetail(movie)
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCarousel.apply {
            this.layoutManager = layoutManager
            adapter = carouselAdapter
            setHasFixedSize(true)
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCarousel)

        binding.rvCarousel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val snapView = snapHelper.findSnapView(layoutManager) ?: return
                val position = layoutManager.getPosition(snapView)
                if (position != RecyclerView.NO_POSITION) {
                    updateCarouselDots(position)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(layoutManager) ?: return
                val position = layoutManager.getPosition(snapView)
                if (position != RecyclerView.NO_POSITION) {
                    updateCarouselDots(position)
                }
            }
        })
    }

    private fun setupGenresRecyclerView() {
        genreAdapter = HomeGenreAdapter { genre ->
            viewModel.selectGenre(genre)
        }

        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupGenreMoviesRecyclerView() {
        genreMoviesAdapter = MoviePagedAdapter { movie ->
            navigateToDetail(movie)
        }

        binding.rvGenreMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = genreMoviesAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { genreMoviesAdapter.retry() }
            )
            isNestedScrollingEnabled = false
        }

        genreMoviesAdapter.addLoadStateListener { loadState ->
            val isInitialLoading = loadState.refresh is LoadState.Loading
            val isEmpty = loadState.refresh is LoadState.NotLoading && genreMoviesAdapter.itemCount == 0

            binding.progressGenreMovies.isVisible = isInitialLoading
            binding.rvGenreMovies.isVisible = !isInitialLoading
            binding.tvEmptyGenreMovies.isVisible = isEmpty
        }
    }

    private fun setupSearch() {
        searchAdapter = HomeMovieAdapter { movie ->
            navigateToDetail(movie)
        }

        binding.rvSearchMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            isNestedScrollingEnabled = false
        }

        binding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()
            binding.btnClearSearch.isVisible = query.isNotEmpty()
            viewModel.setSearchQuery(query)

            if (query.isEmpty()) {
                binding.layoutMainContent.isVisible = true
                binding.layoutSearchResults.isVisible = false
            } else {
                binding.layoutMainContent.isVisible = false
                binding.layoutSearchResults.isVisible = true
            }
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun observeCarousel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.carouselMoviesState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressCarousel.isVisible = true
                            binding.layoutCarouselIndicator.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.progressCarousel.isVisible = false
                            carouselAdapter.submitList(resource.data)
                            setupCarouselIndicators(resource.data.size)
                        }
                        is Resource.Error -> {
                            binding.progressCarousel.isVisible = false
                            binding.layoutCarouselIndicator.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun observeGenres() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genresState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.rvGenres.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.rvGenres.isVisible = true
                            genreAdapter.submitList(resource.data)
                            carouselAdapter.setGenres(resource.data)
                            searchAdapter.setGenres(resource.data)
                        }
                        is Resource.Error -> {
                            binding.rvGenres.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun observeSelectedGenre() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedGenre.collect { genre ->
                    if (genre != null) {
                        genreAdapter.setSelectedGenreId(genre.id)
                        loadMoviesForGenre(genre)
                    }
                }
            }
        }
    }

    private fun loadMoviesForGenre(genre: Genre) {
        genreMoviesJob?.cancel()
        genreMoviesJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMoviesByGenre(genre.id).collectLatest { pagingData ->
                    genreMoviesAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeSearchState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { resource ->
                    if (resource == null) {
                        binding.progressSearch.isVisible = false
                        binding.tvSearchEmpty.isVisible = false
                        searchAdapter.submitList(emptyList())
                        return@collect
                    }
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressSearch.isVisible = true
                            binding.tvSearchEmpty.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.progressSearch.isVisible = false
                            val isEmpty = resource.data.isEmpty()
                            binding.tvSearchEmpty.isVisible = isEmpty
                            binding.rvSearchMovies.isVisible = !isEmpty
                            searchAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.progressSearch.isVisible = false
                            binding.tvSearchEmpty.isVisible = true
                            binding.tvSearchEmpty.text = resource.message
                            searchAdapter.submitList(emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetail(movie: Movie) {
        val bundle = bundleOf(
            "movieId" to movie.id,
            "movieTitle" to movie.title
        )
        findNavController().navigate(
            R.id.action_homeFragment_to_movieDetailFragment,
            bundle
        )
    }

    private fun setupCarouselIndicators(count: Int) {
        binding.layoutCarouselIndicator.removeAllViews()
        if (count <= 1) {
            binding.layoutCarouselIndicator.isVisible = false
            return
        }
        binding.layoutCarouselIndicator.isVisible = true

        val dotHeight = dpToPx(6)
        val activeWidth = dpToPx(20)
        val inactiveWidth = dpToPx(6)
        val marginHorizontal = dpToPx(3)

        for (i in 0 until count) {
            val dot = View(requireContext()).apply {
                val isSelected = (i == currentCarouselPosition)
                setBackgroundResource(
                    if (isSelected) R.drawable.bg_indicator_dot_active
                    else R.drawable.bg_indicator_dot_inactive
                )
                val width = if (isSelected) activeWidth else inactiveWidth
                val params = LinearLayout.LayoutParams(width, dotHeight).apply {
                    setMargins(marginHorizontal, 0, marginHorizontal, 0)
                }
                layoutParams = params
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    binding.rvCarousel.smoothScrollToPosition(i)
                    updateCarouselDots(i)
                }
            }
            binding.layoutCarouselIndicator.addView(dot)
        }
    }

    private fun updateCarouselDots(position: Int) {
        val count = binding.layoutCarouselIndicator.childCount
        if (position < 0 || position >= count) return
        if (position == currentCarouselPosition &&
            binding.layoutCarouselIndicator.getChildAt(position)?.layoutParams?.width == dpToPx(20)
        ) {
            return
        }
        currentCarouselPosition = position

        val activeWidth = dpToPx(20)
        val inactiveWidth = dpToPx(6)

        for (i in 0 until count) {
            val dot = binding.layoutCarouselIndicator.getChildAt(i) ?: continue
            val isSelected = (i == position)
            dot.setBackgroundResource(
                if (isSelected) R.drawable.bg_indicator_dot_active
                else R.drawable.bg_indicator_dot_inactive
            )
            val targetWidth = if (isSelected) activeWidth else inactiveWidth
            val lp = dot.layoutParams
            if (lp.width != targetWidth) {
                lp.width = targetWidth
                dot.layoutParams = lp
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        genreMoviesJob?.cancel()
        currentCarouselPosition = 0
        _binding = null
    }
}
