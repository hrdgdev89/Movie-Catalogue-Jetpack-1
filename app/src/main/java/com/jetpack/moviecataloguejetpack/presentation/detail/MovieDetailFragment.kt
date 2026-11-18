package com.jetpack.moviecataloguejetpack.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    private var activeYouTubePlayer: YouTubePlayer? = null
    private var isPlayerInitialized = false
    private var shouldPlayOnReady = false

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

        setupButtons()
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

        binding.btnWatchTrailer.setOnClickListener {
            val key = trailerVideoKey
            if (!key.isNullOrEmpty()) {
                binding.contentScroll.post {
                    val targetY = binding.tvTrailerHeading.top - 20
                    binding.contentScroll.smoothScrollTo(0, if (targetY > 0) targetY else 0)
                }
                playTrailer(key)
            } else {
                Toast.makeText(requireContext(), "Trailer tidak tersedia untuk film ini", Toast.LENGTH_SHORT).show()
            }
        }

        binding.layoutTrailerPreview.setOnClickListener {
            val key = trailerVideoKey
            if (!key.isNullOrEmpty()) {
                playTrailer(key)
            }
        }

        binding.btnOpenYoutube.setOnClickListener {
            val key = trailerVideoKey
            if (!key.isNullOrEmpty()) {
                openInYouTube(key)
            }
        }
    }

    private fun playTrailer(videoKey: String) {
        binding.tvTrailerNotice.isVisible = false
        val player = activeYouTubePlayer
        if (player != null) {
            binding.layoutTrailerPreview.isVisible = false
            binding.pbTrailerLoading.isVisible = false
            player.loadVideo(videoKey, 0f)
        } else {
            shouldPlayOnReady = true
            binding.pbTrailerLoading.isVisible = true
            if (!isPlayerInitialized) {
                setupYouTubePlayer(videoKey)
            }
        }
    }

    private fun setupYouTubePlayer(videoKey: String) {
        if (isPlayerInitialized) return
        isPlayerInitialized = true

        configureWebViewSettings(binding.youtubePlayerView)

        val options = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(0)
            .rel(0)
            .ivLoadPolicy(3)
            .ccLoadPolicy(0)
            .origin("https://com.jetpack.moviecataloguejetpack")
            .build()

        binding.youtubePlayerView.initialize(
            youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    activeYouTubePlayer = youTubePlayer
                    configureWebViewSettings(binding.youtubePlayerView)
                    if (shouldPlayOnReady) {
                        shouldPlayOnReady = false
                        binding.layoutTrailerPreview.isVisible = false
                        binding.pbTrailerLoading.isVisible = false
                        youTubePlayer.loadVideo(videoKey, 0f)
                    } else {
                        youTubePlayer.cueVideo(videoKey, 0f)
                    }
                }

                override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                    when (state) {
                        PlayerConstants.PlayerState.PLAYING -> {
                            binding.layoutTrailerPreview.isVisible = false
                            binding.pbTrailerLoading.isVisible = false
                        }
                        PlayerConstants.PlayerState.BUFFERING -> {
                            binding.pbTrailerLoading.isVisible = true
                        }
                        PlayerConstants.PlayerState.PAUSED,
                        PlayerConstants.PlayerState.ENDED -> {
                            binding.pbTrailerLoading.isVisible = false
                        }
                        else -> Unit
                    }
                }

                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    binding.pbTrailerLoading.isVisible = false
                    binding.layoutTrailerPreview.isVisible = true
                    val message = when (error) {
                        PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER ->
                            "Pemutaran dibatasi di luar YouTube oleh pemilik video. Silakan tonton melalui aplikasi YouTube."
                        else ->
                            "Trailer tidak dapat diputar langsung di pemutar tertanam. Silakan gunakan tombol di bawah."
                    }
                    binding.tvTrailerNotice.text = message
                    binding.tvTrailerNotice.isVisible = true
                }
            },
            handleNetworkEvents = false,
            playerOptions = options
        )
    }

    private fun configureWebViewSettings(view: View) {
        if (view is WebView) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            view.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36"
            }
            return
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configureWebViewSettings(view.getChildAt(i))
            }
        }
    }

    private fun openInYouTube(videoKey: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoKey"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
        try {
            startActivity(appIntent)
        } catch (e: Exception) {
            try {
                startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Tidak dapat membuka YouTube", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
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
        binding.tvTitle.text = detail.title
        binding.tvTagline.text = detail.tagline.orEmpty()
        binding.tvTagline.isVisible = !detail.tagline.isNullOrEmpty()

        val year = if (!detail.releaseDate.isNullOrEmpty() && detail.releaseDate.length >= 4) {
            detail.releaseDate.substring(0, 4)
        } else {
            "-"
        }
        val genresStr = if (detail.genres.isNotEmpty()) detail.genres.joinToString(", ") { it.name } else "Movie"
        val runtimeStr = DateFormatter.formatRuntime(detail.runtime)
        binding.tvMetaSubtitle.text = "$year | $genresStr | $runtimeStr"

        val stars = (detail.voteAverage / 2.0).coerceIn(1.0, 5.0)
        binding.star1.setImageResource(if (stars >= 1.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
        binding.star2.setImageResource(if (stars >= 2.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
        binding.star3.setImageResource(if (stars >= 3.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
        binding.star4.setImageResource(if (stars >= 4.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
        binding.star5.setImageResource(if (stars >= 4.7) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)

        binding.tvOverview.text = detail.overview.ifEmpty { "Sinopsis belum tersedia." }

        val backdropUrl = Constants.getBackdropUrl(detail.backdropPath)
            ?: Constants.getPosterUrl(detail.posterPath)
        binding.ivBackdrop.loadImage(backdropUrl)
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
                                binding.cardYoutubePlayer.isVisible = true
                                binding.tvTrailerHeading.isVisible = true
                                binding.tvNoTrailer.isVisible = false

                                val thumbnailUrl = "https://img.youtube.com/vi/${firstTrailer.key}/hqdefault.jpg"
                                binding.ivTrailerPoster.loadImage(thumbnailUrl)
                                binding.layoutTrailerPreview.isVisible = true

                                setupYouTubePlayer(firstTrailer.key)
                            } else {
                                trailerVideoKey = null
                                binding.cardYoutubePlayer.isVisible = false
                                binding.tvTrailerHeading.isVisible = false
                                binding.tvNoTrailer.isVisible = true
                            }
                        }
                        is Resource.Error -> {
                            trailerVideoKey = null
                            binding.cardYoutubePlayer.isVisible = false
                            binding.tvTrailerHeading.isVisible = false
                            binding.tvNoTrailer.isVisible = true
                        }
                        is Resource.Loading -> {
                            // Handled by main shimmer
                        }
                    }
                }
            }
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
        binding.btnFavorite.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                if (isFav) R.color.colorPrimary else android.R.color.white
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.youtubePlayerView.release()
        activeYouTubePlayer = null
        isPlayerInitialized = false
        shouldPlayOnReady = false
        _binding = null
    }
}
