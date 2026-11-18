package com.jetpack.moviecataloguejetpack.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.databinding.FragmentMovieDetailBinding
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.presentation.common.LoadingStateAdapter
import com.jetpack.moviecataloguejetpack.utils.Constants
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import com.jetpack.moviecataloguejetpack.utils.loadImage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

open class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModel()
    private lateinit var reviewAdapter: ReviewPagedAdapter

    private var movieId: Int = 0
    private var movieTitle: String = ""
    private var currentMovieDetail: MovieDetail? = null
    private var isFavorite = false
    private var trailerVideoKey: String? = null
    private var isPlayerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("movieId") ?: 0
        movieTitle = arguments?.getString("movieTitle").orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(binding.youtubePlayerView)

        setupToolbar()
        setupReviewsRecyclerView()
        observeDetail()
        observeTrailers()
        observeReviews()
        observeFavoriteStatus()

        viewModel.loadMovie(movieId)

        binding.btnRetry.setOnClickListener {
            viewModel.loadMovie(movieId)
            reviewAdapter.retry()
        }

        binding.btnFavorite.setOnClickListener {
            val detail = currentMovieDetail
            if (detail != null) {
                viewModel.toggleFavorite(detail, isFavorite)
                val msg = if (isFavorite) "Dihapus dari Favorit" else "Ditambahkan ke Favorit"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = if (movieTitle.isNotEmpty()) movieTitle else "Detail Film"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupReviewsRecyclerView() {
        reviewAdapter = ReviewPagedAdapter()
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { reviewAdapter.retry() }
            )
            isNestedScrollingEnabled = false
        }

        reviewAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && reviewAdapter.itemCount == 0
            binding.tvNoReviews.isVisible = isListEmpty
        }
    }

    private fun observeDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.shimmerView.isVisible = true
                            binding.shimmerView.startShimmer()
                            binding.contentScroll.isVisible = false
                            binding.layoutError.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.shimmerView.stopShimmer()
                            binding.shimmerView.isVisible = false
                            binding.contentScroll.isVisible = true
                            binding.layoutError.isVisible = false
                            populateDetail(resource.data)
                        }
                        is Resource.Error -> {
                            binding.shimmerView.stopShimmer()
                            binding.shimmerView.isVisible = false
                            binding.contentScroll.isVisible = false
                            binding.layoutError.isVisible = true
                            binding.tvError.text = resource.message
                        }
                    }
                }
            }
        }
    }

    private fun populateDetail(detail: MovieDetail) {
        currentMovieDetail = detail
        binding.toolbar.title = detail.title
        binding.tvTitle.text = detail.title
        binding.tvTagline.text = detail.tagline.orEmpty()
        binding.tvTagline.isVisible = !detail.tagline.isNullOrEmpty()
        binding.tvRating.text = String.format(Locale.getDefault(), "★ %.1f", detail.voteAverage)
        binding.tvReleaseDate.text = DateFormatter.formatDate(detail.releaseDate)
        binding.tvRuntime.text = DateFormatter.formatRuntime(detail.runtime)
        binding.tvGenres.text = detail.genres.joinToString(", ") { it.name }
        binding.tvOverview.text = detail.overview.ifEmpty { "Sinopsis belum tersedia." }

        binding.ivBackdrop.loadImage(Constants.getBackdropUrl(detail.backdropPath))
        binding.ivPoster.loadImage(Constants.getPosterUrl(detail.posterPath))
    }

    private fun observeTrailers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trailersState.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val firstTrailer = resource.data.firstOrNull()
                            if (firstTrailer != null) {
                                trailerVideoKey = firstTrailer.key
                                binding.youtubePlayerView.isVisible = true
                                binding.tvNoTrailer.isVisible = false
                                initializeYouTubePlayer(firstTrailer.key)
                            } else {
                                binding.youtubePlayerView.isVisible = false
                                binding.tvNoTrailer.isVisible = true
                            }
                        }
                        is Resource.Error -> {
                            binding.youtubePlayerView.isVisible = false
                            binding.tvNoTrailer.isVisible = true
                        }
                        is Resource.Loading -> {
                            // Handled by main loading shimmer
                        }
                    }
                }
            }
        }
    }

    private fun initializeYouTubePlayer(videoKey: String) {
        if (!isPlayerInitialized) {
            isPlayerInitialized = true
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoKey, 0f)
                }
            })
        }
    }

    private fun observeReviews() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getReviews(movieId).collectLatest { pagingData ->
                    reviewAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite(movieId).collect { isFav ->
                    isFavorite = isFav
                    updateFavoriteIcon(isFav)
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFav: Boolean) {
        binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_favorite_black_24dp
            else R.drawable.ic_favorite_border_black_24dp
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
