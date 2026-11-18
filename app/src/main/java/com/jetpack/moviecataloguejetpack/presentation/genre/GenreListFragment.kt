package com.jetpack.moviecataloguejetpack.presentation.genre

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.databinding.FragmentGenreListBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenreListFragment : Fragment() {

    private var _binding: FragmentGenreListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GenreViewModel by viewModel()
    private lateinit var genreAdapter: GenreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeGenres()

        binding.btnRetry.setOnClickListener {
            viewModel.loadGenres()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favorite -> {
                    findNavController().navigate(R.id.action_genreListFragment_to_favoriteListFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        genreAdapter = GenreAdapter { genre ->
            val bundle = bundleOf(
                "genreId" to genre.id,
                "genreName" to genre.name
            )
            findNavController().navigate(
                R.id.action_genreListFragment_to_movieListByGenreFragment,
                bundle
            )
        }
        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = genreAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeGenres() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genresState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.shimmerView.isVisible = true
                            binding.shimmerView.startShimmer()
                            binding.rvGenres.isVisible = false
                            binding.layoutError.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.shimmerView.stopShimmer()
                            binding.shimmerView.isVisible = false
                            binding.rvGenres.isVisible = true
                            binding.layoutError.isVisible = false
                            genreAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.shimmerView.stopShimmer()
                            binding.shimmerView.isVisible = false
                            binding.rvGenres.isVisible = false
                            binding.layoutError.isVisible = true
                            binding.tvError.text = resource.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
