package com.example.hikigai.ui.gallery

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hikigai.PhotoAlbumApplication
import com.example.hikigai.R
import com.example.hikigai.databinding.FragmentGalleryBinding
import com.example.hikigai.ui.adapters.PhotoAdapter
import com.example.hikigai.ui.adapters.PhotoAdapterItem
import com.example.hikigai.ui.adapters.PhotoLoadStateAdapter
import com.example.hikigai.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels {
        GalleryViewModelFactory((requireActivity().application as PhotoAlbumApplication).repository)
    }

    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeToRefresh()
        observePhotos()
        setupNetworkErrorHandling()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter { photo ->
            viewModel.toggleFavorite(photo)
            Snackbar.make(
                binding.root,
                if (photo.isFavorite) R.string.removed_from_favorites else R.string.added_to_favorites,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = photoAdapter.withLoadStateHeaderAndFooter(
                header = PhotoLoadStateAdapter { photoAdapter.retry() },
                footer = PhotoLoadStateAdapter { photoAdapter.retry() }
            )
        }

        photoAdapter.addLoadStateListener { loadState ->
            // Show loading spinner during initial load or refresh
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading

            // Show the retry button if initial load or refresh fails
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Empty view
            val isListEmpty = loadState.refresh is LoadState.NotLoading && photoAdapter.itemCount == 0
            binding.emptyView.isVisible = isListEmpty
            binding.recyclerView.isVisible = !isListEmpty

            // Handle errors
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
                ?: loadState.refresh as? LoadState.Error

            errorState?.let {
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        R.string.no_internet_connection,
                        Snackbar.LENGTH_LONG
                    ).setAction(R.string.retry) {
                        photoAdapter.retry()
                    }.show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "\uD83D\uDE28 ${it.error.localizedMessage}",
                        Snackbar.LENGTH_LONG
                    ).setAction(R.string.retry) {
                        photoAdapter.retry()
                    }.show()
                }
            }
        }

        binding.retryButton.setOnClickListener {
            photoAdapter.retry()
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            photoAdapter.refresh()
        }

        photoAdapter.addLoadStateListener { loadState ->
            binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
        }
    }

    private fun observePhotos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photosFlow
                .map { pagingData ->
                    pagingData.map { photo -> PhotoAdapterItem.PhotoItem(photo) as PhotoAdapterItem }
                }
                .collectLatest { pagingData ->
                    photoAdapter.submitData(pagingData)
                }
        }
    }


    private fun setupNetworkErrorHandling() {
        if (!NetworkUtils.isNetworkAvailable(requireContext()) && photoAdapter.itemCount == 0) {
            Snackbar.make(
                binding.root,
                R.string.offline_mode,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    search(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Reset to original list if search query is cleared
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.resetSearch()
                        observePhotos()
                    }
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean = true

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.resetSearch()
                    observePhotos()
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun search(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchPhotos(query)
                .map { pagingData ->
                    pagingData.map { photo -> PhotoAdapterItem.PhotoItem(photo) as PhotoAdapterItem }
                }
                .collectLatest { pagingData ->
                    photoAdapter.submitData(pagingData)
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
